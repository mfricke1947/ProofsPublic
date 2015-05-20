package us.softoption.tree;



import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;



/*This is to give a view of TTreeTableModel, it is the counterpart of
 
the Swing class 
 * TTreeTableView and TTreePanel
 
 *  The CellTable is the display or panel or view, and it displays the underlying data
 *  of fTreeTableModel
 */

/*  This next bit concerns GWT and the fact the its celltable is (normallly) columns
 * of plain Strings. 
 * 
 * 
 * You can represent your "rows" as List<String> instances, you have to change your 
 * parameterization from String to List<String> in your Grid, Column and data provider; 
 * and of course you have to call updateRowData with a List<List<String>>, not a 
 * List<String>.

You also need one Column instance per column, taking the value out of the List<String> by index:

class IndexedColumn extends Column<List<String>, String>
   private final int index;
   public IndexedColumn(int index) {
     super(new EditTextCell();
     this.index = index;
   }

   @Override
   public String getValue(List<String> object) {
      return object.get(this.index);
   }
}

We do this here as an array of Objects i.e.Object[]
*/

/*We deal with selection by looking at clicks on out Custom Cells
 * The Nodes of the tree have a fSelected field which is toggled on
 * and off on Click.*/

/*check also
 * http://www.gwtproject.org/doc/latest/DevGuideUiCellTable.html
 * on column width
 */

public class TTreeDisplayCellTable extends CellTable<Object[]>{
private TTreeDisplayTableModel fTreeTableModel=new TTreeDisplayTableModel();
private TTreeController fTreeController=null;

		
	
public TTreeDisplayCellTable(){
	}

public TTreeDisplayCellTable(TTreeController itsController){
	fTreeController=itsController;
}

public TTreeDisplayCellTable(TTreeDisplayTableModel aModel){
	fTreeTableModel=aModel;
} 



/*************************** Saving and Opening Files, Beans *************************/



public TTreeDisplayTableModel getModel(){
  return
      fTreeTableModel;
}

public void  setModel(TTreeDisplayTableModel aModel){

      fTreeTableModel=aModel;
}

public TTreeController getController(){
	  return
	      fTreeController;
	}

	public void  setController(TTreeController aController){

	      fTreeController=aController;
	}



/***********************************************************************/






public void synchronizeViewToData(){
	int NUMWIDTH=30;
	int JUSTWIDTH=60;
	
	// Set the width of the table and put the table in fixed width mode.
   // this.setWidth("100%", true);  //Feb2014
    
	 
	
	if (fTreeTableModel!=null){  //updating display from the model
		//empty old
		int presentCols=this.getColumnCount();
		
		for (int i=0;i<presentCols;i++)
			this.removeColumn(presentCols-i-1);
		//prep new
		int rows = fTreeTableModel.getRowCount();
		int cols = fTreeTableModel.getColumnCount();
		
		List<Object[]> rowList = Arrays.asList(fTreeTableModel.fData);
		
		//this.setVisibleRangeAndClearData(new Range(0,0),true);
		
		this.setRowCount(0, true);  //belt and braces
		
		//this.se
		// Create a value updater that will be called when the value in a cell
	    // changes.
	//    ValueUpdater<String> valueUpdater = new ValueUpdater<String>() {
	//      public void update(String newValue) {
	//        Window.alert("You typed: " + newValue);
//	      }
//	    };

	    // Add the value updater to the cellList.
	//    this.setUpdater(valueUpdater);
		
		
		
		this.setRowCount(rows, true);
		
		IndexedColumn column;
		
		for (int i=0;i<cols;i++){
			//this.addColumn(new IndexedColumn(i), Integer.toString(i));
			
			column=new IndexedColumn(i);  //Feb2014
			
		/*	if (i==0)
				this.setColumnWidth(column, NUMWIDTH, Unit.PX);  //line nos
			else
				if (i==cols-1)
					this.setColumnWidth(column, JUSTWIDTH, Unit.PX);  //justification
				else
					this.setColumnWidth(column, 100/(cols-2), Unit.PCT);  */
			
			this.addColumn(column/*, Integer.toString(i)*/
		/*	,new ResizableHeader("one", this, column)*/); //Feb2014
		}

		this.setRowData(rowList);
		
//		this.addSelectionModel();  doing our own clicks
	}

}

class IndexedColumn extends Column<Object[], /*String*/Object> implements TTreeRefreshListener {
	   private final int index;
	   public IndexedColumn(int index) {
		//   TTreeCustomCell columnCell = new TTreeCustomCell(); 
		   
		 super(new TTreeCustomCell()
				 
				 /*new EditTextCell() /*TextCell()*/);
	     this.index = index;
	     TTreeCustomCell itsCell= (TTreeCustomCell)(this.getCell());
	     itsCell.setRefreshListener(this);
	     
	    
	   }

	   @Override
	   public Object/*String*/ getValue(Object[] object) {
		   String debugStr =(object[this.index]).toString();
		   
	      return 
	    		  (object[this.index])/*.toString()*/;
	   }
	   
	public   void doRefresh(boolean redrawOnly){
		if (redrawOnly)
		   redraw();
		else
		   synchronizeViewToData();
		   
	   }
	}



/* avoid row selection on cell click 
 * 
 * Thx, I also just found that simply having my Cells implementing the 
handlesSelection method fixes it; 
@Override public boolean handlesSelection() { return true; } 
 * 
 * 
 * */




/*************************************************************************/	



/**************************** End of Extension rules *****************************/





/**************************** Some debug routines *****************************/



/*

public static void waiting (int n){
    
    long t0, t1;

    t0 =  System.currentTimeMillis();

    do{
        t1 = System.currentTimeMillis();
    }
    while ((t1 - t0) < (n * 1000));
}

public void test2(){
	
	fTreeTableModel=new TTreeDisplayTableModel();
	
	fTreeTableModel.tempTest();
	
	//fTableModel.fData [] [] should now have our data (as Objects)
	// [] []  is row then column
	
	int rows = fTreeTableModel.getRowCount();
	int cols = fTreeTableModel.getColumnCount();
			
	
	List<Object[]> rowList = Arrays.asList(fTreeTableModel.fData);
	
/*	rowList = new ArrayList<Object[]>();
	
	//rowList.clear();
	
	for (int i=0;i<rows;i++)
		rowList.add(fTreeTableModel.fData[i]);  */
	
	
	
	/*Class Column<rowtype,coltype>   */
	
/*	class IndexedColumn extends Column<Object[], String>{
	   private final int index;
	   public IndexedColumn(int index) {
		 super(new EditTextCell());
	     this.index = index;
	   }

	   @Override
	   public String getValue(Object[] object) {
	      return (object[this.index]).toString();
	   }
	}
	
	//DataGrid<Object[]> cellTable = new DataGrid<Object[]>();
	CellTable<Object[]> cellTable = null; new CellTable<Object[]>();


	// Set the total row count. This isn't strictly necessary, but it affects
	// paging calculations, so its good habit to keep the row count up to date.
	
	cellTable=this;
	
	
	cellTable.setRowCount(rows, true);
	
//	IndexedColumn column1 = new IndexedColumn(1);
	
//	cellTable.addColumn(column1,"name");
	
	for (int i=0;i<cols;i++){
		cellTable.addColumn(new IndexedColumn(i), "name");}

	cellTable.setRowData(rowList);
	// Push the data into the widget.
	//cellList.setRowData(0, DAYS);

	// Create a data provider.
    ListDataProvider<Object[]> dataProvider = new ListDataProvider<Object[]>();
    
    // Connect the list to the data provider.
	 //   dataProvider.addDataDisplay(cellTable); 
    
    //DataGrid needs resize panel
    
   // ResizeLayoutPanel panel=new  ResizeLayoutPanel();
    
  //  panel.add(cellTable);*/

	// Add it to the root panel.
	//RootPanel.get().add(cellTable);
/*	
	for (int i=0;i<cols;i++){
		addColumn(new IndexedColumn(i), "name");}
	
	setRowCount(rows, true);
	setRowData(rowList);
	
	synchronizeViewToData();  
    
  //  RootPanel.get().add(this);
	
	//cellTable.redraw();
	
	
//	waiting(10);
	
//   fTreeTableModel.changeTestData();
   
	synchronizeViewToData();
	
//	synchronizeViewAndData();
	
	//RootPanel.get().add(this);
	
	
	//RootPanel.get().clear();
	
//	RootPanel.get().add(this);
	
	//redraw();
	
} */





/*
public TTreeTableView(AbstractTableModel model) {

    super(model);

    setColumnSelectionAllowed(false);
    setRowSelectionAllowed(false);
    setCellSelectionEnabled(true);

    setShowHorizontalLines(false);
    setShowVerticalLines(false);
    {
      // Disable auto resizing
      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

// Set the first visible column, the linenum to 10 pixels wide
      int vColIndex = 0;
      TableColumn col = getColumnModel().getColumn(vColIndex);
      int width = 10;
      col.setPreferredWidth(width);

// fTreeTableView.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    }

    TTreeTableCellRenderer aRenderer = new TTreeTableCellRenderer( (
        TTreeTableModel) model);

    this.setDefaultRenderer(Object.class, aRenderer); //the first parameter tells which things it applies to

    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    //This will start with 3 columns

    // Set the first visible column, the linenum to 10 pixels wide
    TableColumn col = getColumnModel().getColumn(0);
    if (col != null)
      col.setPreferredWidth(20);

    // Set the first visible column, the linenum to 10 pixels wide
    col = getColumnModel().getColumn(1);
    if (col != null)
      col.setPreferredWidth(400);

    // Set the first visible column, the linenum to 10 pixels wide
    col = getColumnModel().getColumn(2);
    if (col != null)
      col.setPreferredWidth(80);

   // this.setPreferredSize(new Dimension(500,this.getRowHeight())); don't do this

    SelectionListener listener = new SelectionListener(this);
    getSelectionModel().addListSelectionListener(listener); //same listener for both
    getColumnModel().getSelectionModel().addListSelectionListener(listener);

    getModel().addTableModelListener(new TTreeTableModelListener(this));

  }

*/
	

/*

public void test1(){
	
	
class Proofline {
	String fColumn1;
	String fColumn2;
	List<String> fProofline;
	
	public Proofline (String column1,String column2, List<String> line ){
		fColumn1=column1;
		fColumn2=column2;
		fProofline=line;
}
}

List<Proofline> LINES= Arrays.asList(
	
	new Proofline ("SUnday", "one",null),
	new Proofline ("Monday", "two",null));

TextColumn<Proofline> column1 = new TextColumn<Proofline>(){
    @Override
    public String getValue(Proofline contact) {
      return contact.fColumn1;
    }
  };

  TextColumn<Proofline> column2 = new TextColumn<Proofline>(){
	    @Override
	    public String getValue(Proofline contact) {
	      return contact.fColumn2;
	    }
	  };

	
	
List<String> DAYS = Arrays.asList("Sunday", "Monday",
		      "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
//Create a cell to render each value in the list.
//TextCell textCell = new TextCell();

//TextColumn<TextCell> column1= new TextColumn<TextCell>();

//CellList<String> cellList1 = new CellList<String>(textCell);
//CellList<String> cellList2 = new CellList<String>(textCell);

// Create a CellTable that uses the cell.
CellTable<Proofline> cellTable = new CellTable<Proofline>();

// Set the total row count. This isn't strictly necessary, but it affects
// paging calculations, so its good habit to keep the row count up to date.
cellTable.setRowCount(LINES.size(), true);

/*
TextColumn<Proofline> nameColumn = new TextColumn<String>();{
    @Override
    public String getValue(Contact contact) {
      return contact.name;
    }
  };



cellTable.addColumn(column1, "Name");
cellTable.addColumn(column2, "Address");

cellTable.setRowData(LINES);
// Push the data into the widget.
//cellList.setRowData(0, DAYS);

// Add it to the root panel.
RootPanel.get().add(cellTable);
		
}  */



	
}
