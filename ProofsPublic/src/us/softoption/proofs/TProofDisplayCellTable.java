package us.softoption.proofs;

/*We have a cell table of prooflines 
 * 
 * Jan 2013 Problem with selection model
 * 
 * 
 * 
 * */


import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;


public class TProofDisplayCellTable extends CellTable<TProofline>{
	
	
	private TProofListModel fProofListModel=new TProofListModel();
	private TProofController fProofController=null;
	
	private MultiSelectionModel<TProofline> fSelectionModel = new MultiSelectionModel<TProofline>();
	
	
	
	public TProofDisplayCellTable(){
		
		
		addSelectionModel();
	}

public TProofDisplayCellTable(TProofController itsController){
	fProofController=itsController;	
	addSelectionModel();
	this.setStyleName("proofDisplay");
}

public TProofDisplayCellTable(TProofListModel aModel){
	fProofListModel=aModel;	
	addSelectionModel();
	this.setStyleName("proofDisplay");
}

/************** Getters ************************/

TProofListModel getProofListModel(){
	
	return
			fProofListModel;
}

/************** Selection and Clicks or Touches ************************/


void addSelectionModel(){
	
	
	this.sinkEvents(Event.ONCLICK);
	
    //but we want click to toggle selection not require command click for multiple
    this.addCellPreviewHandler(new CellPreviewEvent.Handler<TProofline>() {
    public void onCellPreview(CellPreviewEvent<TProofline> event) {
        String type = event.getNativeEvent().getType();
        if (type.equals("click")) {
        	      	
        	TProofline value=event.getValue();
        	
        	handleClick(value);
                    
            event.setCanceled(true);
        	    	
        //    Window.alert("CLICKED " + value.fLineno);  DEBUG
        }
    }
});	
    
    this.setSelectionModel(fSelectionModel,
    		               DefaultSelectionEventManager.<TProofline> createCheckboxManager());
    
    
    /*
    final Handler<TProofline> selectionEventManager = DefaultSelectionEventManager.createCheckboxManager();
    this.setSelectionModel(fSelectionModel,selectionEventManager);  Jan 2013 */
    
/*Google sample code 
 * 
 *  // Add a selection model so we can select cells.
    final SelectionModel<ContactInfo> selectionModel = new MultiSelectionModel<ContactInfo>(
        ContactDatabase.ContactInfo.KEY_PROVIDER);
    cellTable.setSelectionModel(selectionModel,
        DefaultSelectionEventManager.<ContactInfo> createCheckboxManager());
 */
	
	
	
	 // We want multiple selection
   // final MultiSelectionModel<TProofline> selectionModel = new MultiSelectionModel<TProofline>();
  /*  this.setSelectionModel(fSelectionModel);
    fSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      public void onSelectionChange(SelectionChangeEvent event) {
    	  Set <TProofline> selected = fSelectionModel.getSelectedSet();
        if (selected != null) {
          Window.alert("You selected: " + selected.size());
        }
      }
    });	
    
*/
    
 /*   this.addCellPreviewHandler(new CellPreviewEvent.Handler<TProofline>() {
        public void onCellPreview(CellPreviewEvent<TProofline> event) {
            String type = event.getNativeEvent().getType();
            if (type.equals("click")) {
            	
            	
            	TProofline value=event.getValue();
            	
                final Boolean state = !event.getDisplay().getSelectionModel().isSelected(value);
                event.getDisplay().getSelectionModel().setSelected(value, state);
                event.setCanceled(true);
            	
            	
            	
                Window.alert("CLICKED");
            }
        }
    });
    
    */
    
    /*
    
    
    this.addCellPreviewHandler(new Handler<T>() {

        @Override
        public void onCellPreview(final CellPreviewEvent<T> event) {

            if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {

                final T value = event.getValue();
                final Boolean state = !event.getDisplay().getSelectionModel().isSelected(value);
                event.getDisplay().getSelectionModel().setSelected(value, state);
                event.setCanceled(true);
            }
        }
}); 
    
    
  */  
    
	
}



void handleClick(TProofline clickedLine){
// based on class SynchronizeSelections implements ListSelectionListener in TProofListView	
	

/*
 * 
 * //this needs to unselect unselectable lines, and synchronize lines which are selected between
//the prooflines in the data model and those in the celltable
 * 
 * 	
 */
	
	
	/* ignore null and blanklines */	
	if (clickedLine==null||clickedLine.fBlankline)  // ignore blanklines
		return;
	
	/*get its formula*/
	String formulaStr = fProofController.fParser.writeFormulaToString(clickedLine.fFormula);
	
	/* not null but using input pane */
	if(fProofController.usingInputPane()) { // are using input panel, so touch to load  
        if (!"?".equals(formulaStr))
        	fProofController.display(formulaStr);
        return;
	}
	
	/* not null, no input pane but on ? */
	
	if ("?".equals(formulaStr) 
        	//changing goals
        		&& (!fProofController.usingInputPane()) && // don't change goals in the middle of something else

                 (clickedLine != (fProofListModel.getTailFirstLine())))  //already at tqil no chqnge needed
        
        
        		{
                    fProofListModel.changeGoals(clickedLine);

                    fProofController.bugAlert("Advisory: Changing goals.",
                        "Notice that you have started to work on a different goal.");
                    
                    return;

                  } 		
     
	
	
      /* not null, normal case */
	
	//Feb152013
    if ((!clickedLine.fSelectable && // can't be this
             !clickedLine.fSubProofSelectable )) //can't be that
        		{
        	fSelectionModel.setSelected(clickedLine, false); //unselect it
        	return;
        		}
	
		final Boolean alreadySelected = fSelectionModel.isSelected(clickedLine);	

		/* change Feb 15
            if (alreadySelected &&           //apparently selected
                (!clickedLine.fSelectable && // can't be this
                 !clickedLine.fSubProofSelectable )) //can't be that
            		{
            	fSelectionModel.setSelected(clickedLine, false); //unselect it
            	return;
            		}
      */      		
            		
     
            /*all 'odd' cqses covered, just toggle it		*/          	
                fSelectionModel.setSelected(clickedLine,!alreadySelected);	
	
}

void clearSelections(){
if (fSelectionModel!=null)
	fSelectionModel.clear();
	
}


/*************************** Saving and Opening Files, Beans *************************/



public TProofListModel getModel(){
  return
      fProofListModel;
}

public void  setModel(TProofListModel aModel){

      fProofListModel=aModel;
}

public TProofController getController(){
	  return
	      fProofController;
	}

	public void  setController(TProofController aController){

	      fProofController=aController;
	}
	
	
/*	
 * 
 * http://stackoverflow.com/questions/7854147/gwt-clickabletextcell
 * 
	Column<DataType, String> myIntegerColumn 
    = new Column<DataType, String>(new ClickableTextCell());
myIntegerColumn.setFieldUpdater(new FieldUpdater<DataType, String>(){
@Override
public void update(int index, DataType object, String value){
  // execute code that reacts to a click on this hot spot
}
});

*/
	
public void synchronizeViewToData(){
	
	/*
	A proof looks like

	1 | F^G                    Ass
	2 || H                     Ass
	3 || F                     1 ^E

	now, a line (row) on its own is fine, we can use one column for the line number, then one each for 
	the vertical lines, the formula, and the justification.

	Conceptually, the line numbers are col 1, the vertical lines are subcolumns of col 2, the formula
	is col 3, and the justification col 4.

	But, if we wanted to combine several lines into a table we have the problem that the different rows
	might have a different number of columns.
	
	But we know

	If this method is called with a (max) numberOfColumns, then we can insert a colspan if needed. The colspan
	needs to go with the formula (ie with F^G in line 1), we are looking for left justification.

	Now, some proofs start with a headlevel of -1, others with a headlevel of 0 (that information is
	on the line itself).

	Then the parameter tells us the maximum nesting. So the number of columns in the table as a whole is
	4 + maxSubProofLevel.

	So, any particular line has to look at the difference between its level and the headlevel. It that is
	maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 2 is needed

	ie colspan = maxSubProoflevel - difference +1 (and colspan has to be greater than 1 to matter)


	*/	
	
	
	
	
	
		 
		
		if (fProofListModel!=null){  //updating display from the model
			//empty old
			int presentCols=this.getColumnCount();
			
			for (int i=0;i<presentCols;i++)
				this.removeColumn(presentCols-i-1);
			//prep new
			int rows = fProofListModel.getRowCount();
			int cols = fProofListModel.getColumnCount();
			
			List<TProofline> rowList = fProofListModel.proofAsProoflines();
			
			//this.setVisibleRangeAndClearData(new Range(0,0),true);
			
			this.setRowCount(0, true);  //belt and braces
			
			//this.se
			// Create a value updater that will be called when the value in a cell
		    // changes.
		//    ValueUpdater<String> valueUpdater = new ValueUpdater<String>() {
		//      public void update(String newValue) {
		//        Window.alert("You typed: " + newValue);
//		      }
//		    };

		    // Add the value updater to the cellList.
		//    this.setUpdater(valueUpdater);
			
			
			
			this.setRowCount(rows, true);
			
			
			// Add a text column to show the line number
	/*	    TextColumn<TProofline> nameColumn = new TextColumn<TProofline>() {
		      @Override
		      public String getValue(TProofline object) {
		        return (object.firstColumnGWT());
		      }
		    };
		    this.addColumn(nameColumn, "LineNo");  
			*/
			
			
	//		Column<TProofline,String> aColumn=new Column<TProofline,String>();
			
	/*		Column<TProofline, String> editableColumn = new Column<TProofline, String>(
		            new ClickableTextCell() /* (Cell)(new TProofCustomCell())*/ /*) {
		        @Override
		        public String getValue(TProofline parameter) {
		            return parameter.firstColumnGWT();
		        }
		    };
			
		    this.addColumn(editableColumn /*,"LineNo");
			
	*/
		    
		/****** experiment Jan 18 2013 *****/
/*			
			final int maxSubProofLevel = fProofListModel.maxSubProofLevel();
		    
			final SafeHtmlCell lineNoCell = new SafeHtmlCell();
			final SafeHtmlCell vertLinesCell = new SafeHtmlCell();
			final SafeHtmlCell formulaCell = new SafeHtmlCell();
			final SafeHtmlCell justificationCell = new SafeHtmlCell();
		    
		    Column<TProofline, SafeHtml> safeColumn1 = new Column<TProofline, SafeHtml>(
		             lineNoCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.firstColumnGWT());
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn1);
	
		    Column<TProofline, SafeHtml> safeColumn2 = new Column<TProofline, SafeHtml>(
		             vertLinesCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.secondColumnGWT());
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn2);
		    
		    Column<TProofline, SafeHtml> safeColumn3 = new Column<TProofline, SafeHtml>(
		             formulaCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.thirdColumnGWT(maxSubProofLevel));
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn3);
		    
		    Column<TProofline, SafeHtml> safeColumn4 = new Column<TProofline, SafeHtml>(
		             justificationCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.fourthColumnGWT());
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn4);
		    
			this.setRowData(rowList);

			
			
			//now we want to set column widths	
				
			this.setWidth("100%", true); //fixed layout
			this.setColumnWidth(safeColumn1, 36.0, Unit.PX);
			this.setColumnWidth(safeColumn2, 10.0, Unit.PCT);
			this.setColumnWidth(safeColumn3, 70.0, Unit.PCT);
			this.setColumnWidth(safeColumn4, 20.0, Unit.PCT);   
*/		    
		 /******************/


	/****** experiment Feb 2013 *****/
			
	/*we really want three columns, the line number, the vert lines and formula in one, and the justification*/		
			
			final int maxSubProofLevel = fProofListModel.maxSubProofLevel();
		    
			final SafeHtmlCell lineNoCell = new SafeHtmlCell();
			final SafeHtmlCell vertLinesAndFormulaCell = new SafeHtmlCell();
	//		final SafeHtmlCell formulaCell = new SafeHtmlCell();
			final SafeHtmlCell justificationCell = new SafeHtmlCell();
		    
		    Column<TProofline, SafeHtml> safeColumn1 = new Column<TProofline, SafeHtml>(
		             lineNoCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.firstColumnGWT());
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn1 /*,"Line No"*/);
	
		    Column<TProofline, SafeHtml> safeColumn2 = new Column<TProofline, SafeHtml>(
		             vertLinesAndFormulaCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.secondAndThirdColumnGWT(maxSubProofLevel));
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn2 /*,"Vert Lines" and formula*/);
		    
		    
		    Column<TProofline, SafeHtml> safeColumn3 = new Column<TProofline, SafeHtml>(
		             justificationCell) {
		        @Override
		        public SafeHtml getValue(TProofline parameter) {
		        	SafeHtmlBuilder sb = new SafeHtmlBuilder();
		        	sb.appendHtmlConstant(parameter.fourthColumnGWT());
			            return 
		            		sb.toSafeHtml()           		
		      ;
		        }
		    };
			
		    this.addColumn(safeColumn3 /*,"Justification"*/);
		    
			this.setRowData(rowList);

			
			
			//now we want to set column widths	
				
			this.setWidth("100%", true); //fixed layout
			this.setColumnWidth(safeColumn1, 36.0, Unit.PX);  //line no
			this.setColumnWidth(safeColumn2, 100.0, Unit.PCT);  // vert lines and formula
			this.setColumnWidth(safeColumn3, 100.0, Unit.PX);   // justification
  
		    
		 /******************/		
			
			
	
			
		}

	}
	
	
	//	http://google-web-toolkit.googlecode.com/svn/javadoc/2.1/com/google/gwt/user/cellview/client/CellTable.html

/***********************************************************************/

/***************************Selection ********************************************/

void clearSelection(){
	
	
fSelectionModel.clear();	//(TO DO) apparently done Jan 2013
	
	
}

//TO DO
//public boolean oneSelected(){return true;}

public TProofline oneSelected(){

	  TProofline []selection = exactlyNLinesSelected(1);

	  if ((selection!=null)&&(selection.length==1))
	    return
	        selection[0];
	  else
	    return
	        null;
}

public int totalSelected()
{
	Set <TProofline> selections = fSelectionModel.getSelectedSet();

  return
     selections.size();

}

public TProofline[] exactlyNLinesSelected(int n){

	// exactly n, selected and selectable, some selected lines are not, for those indicating subproofs

	     //maybe not work for 0?

	int numFound = 0;

	//int []selections = getSelectedRows(); // TO DO getSelectedRows();
	
	Set <TProofline> selections = fSelectionModel.getSelectedSet();

	int numSelections=selections.size();

	if (numSelections<n)   //at least n needed
	   return
	       null;
	else
	 {TProofline[]returnArray = new TProofline[n];
	 TProofline selectedLine;
	 
	 TProofline[] selArray = new TProofline[numSelections];
	 selArray = selections.toArray(selArray);
	  
	 for (int i = 0; i < numSelections; i++) {    
		 selectedLine = selArray[i]; //TO DO (TProofline)getValueAt(selections[i],TProofTableModel.fProofColIndex); //2 columns
		//selectedLine = (TProofline) selections[i];
					 
	    if (selectedLine.fSelectable){
	       if (numFound<n){
	         returnArray[numFound] = selectedLine;
	         numFound+=1;
	       }
	       else
	         return
	             null;

	     }
	   }

	  if (numFound==n){
		  Arrays.sort(returnArray, new prooflineComparator());
	    return
	      returnArray;
	  }
	  else
	    return
	        null;
	   }
	 }


public TProofline[][] nSubProofsSelected(int n){

	// exactly n proofs selected

	 /*{The condition is that it is selected and the nextline is a blankline indicating eo subproof}
	    However, we don't worry about the blankline because another routine resetSelectables? checks
	  it and sets the field fSubProofSelectable*/

	 /* Each subproof has to have a head and a tail, so we will return an array of two element arrays*/

	 TProofline head = null, tail = null, searchline = null;
	 int numFound = 0;

	 Set <TProofline> selections = fSelectionModel.getSelectedSet();

	 int numSelections=selections.size();

	 if (numSelections < n) //must have at least n, can have more if there are ordinary selections
	   return
	       null;
	 else {
	   TProofline[][] returnArray = new TProofline[n][2];
	   TProofline selectedLine;
	   
	   TProofline[] selArray = new TProofline[numSelections];
		 selArray = selections.toArray(selArray);

	   for (int i = 0; (i < numSelections); i++) {

	     selectedLine =selArray[i];

	     if (selectedLine.fSubProofSelectable) { // may be one

	       tail = selectedLine;
	       head = null;

	       Iterator iter = fProofListModel.getHead().iterator();

	       while (iter.hasNext()) {
	         searchline = (TProofline) iter.next();

	         if ( (searchline.fJustification.equals(TProofController.fAssJustification)) &&
	             (searchline.fSubprooflevel == tail.fSubprooflevel) &&
	             (searchline.fLineno <= tail.fLineno))

	           head = searchline; // looking for last one
	       }

	       if (head != null) { // finished while loop and we have found one
	         if (numFound >= n)
	           return
	               null; // there are too many, bale
	         else {
	           returnArray[numFound][0] = head;
	           returnArray[numFound][1] = tail;

	           numFound+=1;
	         }

	       }
	     }

	   }

	   if (numFound == n)
	     return
	         returnArray;
	   else
	     return
	         null;
	 }
	}


class prooflineComparator implements Comparator<TProofline> {

    // Comparator interface requires defining compare method.
    public int compare(TProofline lineA,TProofline lineB) {
        //... Sort directories before files,
        //    otherwise alphabetical ignoring case.
        if (lineA.fLineno>lineB.fLineno)
        	return
        			1;
        else
            if (lineA.fLineno<lineB.fLineno)
            	return
            	    			-1;	
            else
        	return
        			-1;
    }
}





	
	
}