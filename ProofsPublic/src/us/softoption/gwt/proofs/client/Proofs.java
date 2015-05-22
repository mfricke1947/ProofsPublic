/*
Copyright (C) 2015 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package us.softoption.gwt.proofs.client;
 
//scanned through 5/22/2015

import static us.softoption.infrastructure.Symbols.strNull;
import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.GWTSymbolToolbar;
import us.softoption.infrastructure.TPreferencesData;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TGentzenParser;
import us.softoption.parser.TGirleParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TParser;
import us.softoption.proofs.TMyBergmannProofController;
import us.softoption.proofs.TMyGentzenProofController;
import us.softoption.proofs.TMyProofController;
import us.softoption.proofs.TProofController;
import us.softoption.proofs.TProofDisplayCellTable;
import us.softoption.tree.TGWTTreeInputPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/* This expects html with

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Proofs implements EntryPoint, TJournal, TReset {

	
	VerticalPanel fInputPanel = new VerticalPanel(); // for BugAlert and ProofInputPane  
    
	
	TProofDisplayCellTable fDisplayTable = new TProofDisplayCellTable();//replaces ProofPanel
	ScrollPanel fScrollPanel=null; //holds the displayed table
	GWTSymbolToolbar fSymbolToolbar;  // tend to use this in preference to the JournalPane
	final TextArea fJournalPane = new TextArea();// often not visible, if using buttons to start
	
	RichTextArea fTextForJournal = new RichTextArea();
	
	final HorizontalPanel fRuleButtonsPanel = new HorizontalPanel(); // menu buttons
	final HorizontalPanel fEditButtonsPanel = new HorizontalPanel(); // menu buttons
	final HorizontalPanel fModalMenuButtonsPanel = new HorizontalPanel(); // menu buttons
	final HorizontalPanel fComponentsPanel = new HorizontalPanel(); //buttons
	
	TProofController fProofController= null;
	static TParser fParser=new TDefaultParser();
	
	MenuBar fMenuBar = new MenuBar();  //true makes it vertical
	
	static boolean fPropLevel=false;
	
	static final boolean HIGHLIGHT = true;

	Label  fLabel=new Label("Trees");  //must have come from the Trees code

	String fInputText=null;

	boolean fDebug=false;
	
	boolean fExtraDebug=false;
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		TPreferencesData.readParameters();		
		setLocalParameters();  // sets Parser, Controller, and Journal		
        createGUI();  
	}
	
	
	
	
void buildMenus(){

if (fProofController!=null){
   fMenuBar=fProofController.createMenuBar();
}

	
if 	(RootPanel.get("menu")!=null)
	RootPanel.get("menu").add(fMenuBar);  //commented back in Jan 20 2013


}
/*******************  TReset **************************/

public void enableMenus(){  //at present GWT does not have a good way of doing this.
	
/* TO DO	fRules.addItem(fExtend);	
	fRules.addItem(fClose);	
	fRules.addSeparator();	
	fRules.addItem(fIsClosed);	
	fRules.addItem(fOpenBranch);	
	fRules.addSeparator();	
	fRules.addItem(fStartOver);  */

	if (!fRuleButtonsPanel.isAttached())
	{if 	(RootPanel.get("menubuttons")!=null)
		RootPanel.get("menubuttons").add(fRuleButtonsPanel);
	}
	if (!fEditButtonsPanel.isAttached()){
		if 	(RootPanel.get("editbuttons")!=null)
		RootPanel.get("editbuttons").add(fEditButtonsPanel);
	}
}

public void disableMenus(){
// TO DO fRules.clearItems();

if (fRuleButtonsPanel.isAttached()){
	if 	(RootPanel.get("menubuttons")!=null)
	RootPanel.get("menubuttons").remove(fRuleButtonsPanel);
}
if (fEditButtonsPanel.isAttached()){
	if 	(RootPanel.get("editbuttons")!=null)
	RootPanel.get("editbuttons").remove(fEditButtonsPanel);
}

}

public void reset(){
	;
}

/******************** End of TReset **********************************/


void buildMenuButtons(){
	Widget[] menuButtons=null;//={fAndButton,fExtendButton, fCloseButton, fIsClosedButton, fOpenBranchButton,
//			fIdentityIntroButton};
	
	Widget[] editButtons=null;
	
//TO DO	
	
	if (fProofController!=null){
		menuButtons=fProofController.getButtons();
		editButtons=fProofController.getEditButtons();
	}

	int dummy=0;
	
	initializeRuleButtons(menuButtons,dummy);
	
	if 	(RootPanel.get("menubuttons")!=null)
		RootPanel.get("menubuttons").add(fRuleButtonsPanel);
	
	initializeEditButtons(editButtons,dummy);
	
	if 	(RootPanel.get("editbuttons")!=null)
			RootPanel.get("editbuttons").add(fEditButtonsPanel);
	
}	


void createGUI(){
	
if (fDebug)
	someDebugCode();

buildMenus();

buildMenuButtons();
	
 
Widget [] paramButtons =readParamProofs();

if (RootPanel.get("input")!=null)
	RootPanel.get("input").add(fInputPanel);

fScrollPanel=new ScrollPanel(fDisplayTable); //problem here Jan 2013

fScrollPanel.setSize("600px", "400px");  //need this


if (RootPanel.get("proof")!=null)
	RootPanel.get("proof").add(fScrollPanel);  

if ((paramButtons.length)>0)
	   finishNoPalette(paramButtons);
else
	   finishWithPalette();

fProofController.startProof("");   // gwt does not like no proof at all


}


void finishNoPalette(Widget [] components){
	int depth=30;    // this is the height of the buttons
	
	 initializeComponentsPanel(components,depth);
	 
	 if (RootPanel.get("buttons")!=null)	
		 RootPanel.get("buttons").add(fComponentsPanel);	
}

//end of createGUI

void initializeComponentsPanel(Widget [] components,int depth){
	
	fComponentsPanel.setStyleName("buttons");
	
	fComponentsPanel.setHeight("50px");
	
	fComponentsPanel.setSpacing(20);

	 for (int i=0;i<components.length;i++){
				      fComponentsPanel.add(components[i]);
				    }
				}

void initializeRuleButtons(Widget [] components,int depth){
	
	fRuleButtonsPanel.setStyleName("menubuttons");
	
	fRuleButtonsPanel.setSpacing(10);
	
	fRuleButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	
	fRuleButtonsPanel.setHeight("50px");

	 for (int i=0;i<components.length;i++){
				      fRuleButtonsPanel.add(components[i]);
				    }
				}

void initializeEditButtons(Widget [] components,int depth){
	
	fEditButtonsPanel.setStyleName("editbuttons");
	
	fEditButtonsPanel.setSpacing(10);
	
	fEditButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	
	fEditButtonsPanel.setHeight("50px");

	 for (int i=0;i<components.length;i++){
				      fEditButtonsPanel.add(components[i]);
				    }
				}

/*void initializeModalMenuButtons(Widget [] components,int depth){
		
	
	fModalMenuButtonsPanel.setStyleName("modalmenubuttons");
	
	fModalMenuButtonsPanel.setSpacing(10);
	
	fModalMenuButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	
	fModalMenuButtonsPanel.setHeight("50px");

	Widget[] buttons=fProofController.supplyModalRadioButtons();
	
	for (int i=0;i<buttons.length;i++)
	{
		fModalMenuButtonsPanel.add(buttons[i]);	
		
	}
}	*/




void finishWithPalette(){

boolean lambda=false,modal=false,settheory=false;

	String symbols =  fParser.getInputPalette(lambda,modal,settheory);
	
	
	
	fTextForJournal.setWidth("100%");
	fTextForJournal.setHeight("240px");
	
	//text.setText(fInputText);
	
	fTextForJournal.setHTML(fInputText);

	fSymbolToolbar = new GWTSymbolToolbar(fTextForJournal,symbols);

	if 	(RootPanel.get("journal")!=null)
		RootPanel.get("journal").add(fSymbolToolbar);
	if 	(RootPanel.get("journal")!=null)
		RootPanel.get("journal").add(fTextForJournal);
	
	
	if 	(RootPanel.get("startButton")!=null)
		RootPanel.get("startButton").add(startButton());
	

	Button aWidget= fProofController.cancelButton();
	Widget [] components ={aWidget};
	
	
	TGWTTreeInputPanel fInputPane = new TGWTTreeInputPanel("Hello",new  TextBox(),components);

}


void setLocalParameters(){
	fInputText=TPreferencesData.fInputText;  // probably don't need input text field
	
	fJournalPane.setText(TPreferencesData.fInputText);  // not using journal, use toolbar
	fPropLevel=TPreferencesData.fPropLevel;
	
	{ String parser =TPreferencesData.fParser;
	if (parser!=null) {
		if (parser.equals("bergmann")){
			   fParser =new TBergmannParser();
			   fProofController=new TMyBergmannProofController (fParser,this,this,fInputPanel,fDisplayTable);
		   }
		else if (parser.equals("copi")){
			   fParser =new TCopiParser();
			   fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
		   }
		else if (parser.equals("gentzen")){
			   fParser =new TGentzenParser();
			   fProofController=new TMyGentzenProofController (fParser,this,this,fInputPanel,fDisplayTable);
		   }
		else if (parser.equals("girle")){
			   fParser =new TGirleParser();
			   fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
		 	}
		else if (parser.equals("hausman")){
			   fParser =new THausmanParser();
			   fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
		   }
		else if (parser.equals("herrick")){
			   fParser =new THerrickParser();
			   fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
			}
		else if (parser.equals("howson")){
			   fParser =new THowsonParser();
			   fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
			}
		else{
			fParser =new TDefaultParser();
			fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
			}
		
			
	}
	else  //no parser from preferences
	{
		fParser =new TDefaultParser();
		fProofController=new TMyProofController (fParser,this,this,fInputPanel,fDisplayTable);
	}
	
	}
	
	fDisplayTable.setController(fProofController);	
	
}
	

String readParameterValue(String key){

Dictionary params;
	
try{
	params = Dictionary.getDictionary("Parameters");}
catch (Exception ex) {return "";}
	 		
if (params!=null){		
	try{String value= params.get(key);
	return
		value;}
	catch (Exception ex){return "";}
}
return
		"";
}  



Widget [] readParamProofs(){
	Widget[] components={};
   int i=0;

   String param= "proof"+i;	
	
   String value= readParameterValue(param);
	   while (value!=null&&!value.equals("")&&i<10){
		   i++;
		   param= "proof"+i;
		   value= readParameterValue(param);
	   }
	   
	   
	if (i>0){   
	int count =i;
	   components= new Widget[count];
	   i=0;	
	   param= "proof"+i;	   
	   String label="Proof";	   
	   if (count>6)
		   label="Pr";     // we only fit 6, but we will squeeze a few more
		
       value= readParameterValue(param);
		   while (value!=null&&!value.equals("")&&i<10){
			   components[i]=proofButton(label+(i+1),value);
			   i++;
			   param= "proof"+i;
			   value= readParameterValue(param);
		   }
	}
	   	   
	   return 
	   components;
}

/***********************  Buttons ***************************/



Button proofButton(String label, final String inputStr){
	Button button = new Button(label);	
	
	
	ProofHandler pHandler = new ProofHandler(inputStr);
	button.addClickHandler(pHandler);

	return
	   button;
}

Button startButton(){
	Button button = new Button("Start from selection");	    

	button.addClickHandler(new ClickHandler(){@Override 
		public void onClick(ClickEvent event) {
			String inputStr=fSymbolToolbar.getSelectionAsText();
			String filteredStr=TUtilities.logicFilter(inputStr);
			
			filteredStr=TUtilities.htmlEscToUnicodeFilter(filteredStr);
			// some might be &amp; etc.
			
				fProofController.startProof(filteredStr);
		}
			});
	

	return
	   button;
}

/***********************  End of Buttons ***************************/



/**************************** Utilities *************************************/

		static public String peculiarFilter(String inputStr){
			 String outputStr;

			 outputStr=inputStr.toLowerCase();
			 outputStr=outputStr.replaceAll("[^()a-z]"," ");   // we want just lower case, brackets, and blanks

			return
			     outputStr;
			}

		private String readSource(){
		return
		     peculiarFilter(fJournalPane.getSelectedText());
				}

		static public String defaultFilter(String inputStr){  //ie standard filter
		    String outputStr;

		    outputStr=inputStr.replaceAll("\\s",strNull); // removes ascii whitespace?
		    outputStr=outputStr.replaceAll("\u00A0",strNull); // removes html/unicode non breaking space?

		    return
		        outputStr;
		  }

		/**************************** End of Utilities *************************************/

			
			
		
	class ProofHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on Proof.
			 */
		String fFilteredInput="";
		
		public ProofHandler(String inputStr) {
			fFilteredInput=TUtilities.logicFilter(inputStr);		
		}
			
			public void onClick(ClickEvent event) {
				fProofController.startProof(fFilteredInput);

			}
		
	}	
	
	



/************************MF new experiments**********************************/
void experiment(){
FlexTable t = new FlexTable();



// Put some text at the table's extremes.  This forces the table to be
// 3 by 3.
t.setText(0, 0, "upper-left corner");
t.setText(2, 2, "bottom-right corner");

/*
// Let's put a button in the middle...
t.setWidget(1, 1, new Button("Wide Button"));

// ...and set it's column span so that it takes up the whole row.
t.getFlexCellFormatter().setColSpan(1, 0, 3);

*/

//TTreeDisplayCellTable grid = new TTreeDisplayCellTable();

t=createTable();

//RootPanel.get().add(t);



 //TTreePanel test= new TTreePanel();

testEverything(t);

//grid.test1();

//grid.test2();

//RootPanel.get().add(grid);






}

FlexTable createTable(){
	FlexTable t = new FlexTable();
	
	t.insertRow(0);
	t.insertRow(0);
	t.insertRow(0);
	
	for (int i=0;i<3;i++){
		t.addCell(i);
		t.addCell(i);
		t.addCell(i);
		
	}
		
	
	t.setBorderWidth(1);
	
	return
			t;
}


void testEverything(FlexTable t){
/*	TTreeDisplayTableModel test= new TTreeDisplayTableModel();

/*	test.fData = new Object[2][2] ; 

	test.fData[0][0]="1";
	test.fData[0][1]="2";
	test.fData[1][0]="3";
	test.fData[1][1]="4";
	
	test.tempTest();
	
	Object object1=test.getValueAt(0, 0);
	Object object2=test.getValueAt(0, 1);
	Object object3=test.getValueAt(0, 2);
	Object object4=test.getValueAt(1, 0);
	Object object5=test.getValueAt(1, 1);
	Object object6=test.getValueAt(1, 2);
	Object object7=test.getValueAt(2, 0);
	Object object8=test.getValueAt(2, 1);
	Object object9=test.getValueAt(2, 3);
	
	String value1= (object1!=null)?object1.toString():"null";
	String value2= (object2!=null)?object2.toString():"null";
	String value3= (object3!=null)?object3.toString():"null";
	String value4= (object4!=null)?object4.toString():"null";
	String value5= (object5!=null)?object5.toString():"null";
	String value6= (object6!=null)?object6.toString():"null";
	String value7= (object7!=null)?object7.toString():"null";
	String value8= (object8!=null)?object8.toString():"null";
	String value9= (object9!=null)?object9.toString():"null";
	

	t.setText(0, 0, value1);
	t.setText(0, 1, value2);
	t.setText(0, 0, value3);
	t.setText(1, 0, value4);
	t.setText(1, 1, value5);
	t.setText(1, 2, value6);
	t.setText(2, 0, value7);
	t.setText(2, 1, value8);
	t.setText(2, 2, value9);


	*/
	
}

/************ TO DO TO IMPLEMENT TJOURNAL INTERFACE *************/

public void writeHTMLToJournal(String message,boolean append){
// haven't written it yet
	if (append)
		fTextForJournal.setHTML(fTextForJournal.getHTML()+message);
	else{
		Formatter aFormatter=fTextForJournal.getFormatter();
		if (aFormatter!=null)
			aFormatter.insertHTML(message);
		
	}
	;
	
	
	
	}

public void writeOverJournalSelection(String message){  //I think this code is right
	Formatter aFormatter=fTextForJournal.getFormatter();
	if (aFormatter!=null)
		aFormatter.insertHTML(message);

/*	   if (message.length()>0)
	     fJournalPane.replaceSelection(message); */
	}


public int getSelectionEnd(RichTextArea text){
	//This is a hack to get the selection by putting a dummy marker around it then removing it	
		
	int end=0;
	
	if (text!=null){
		Formatter aFormatter=text.getFormatter();
		if (aFormatter!=null){
	
			String fakeUrl=	"H1e2l3l4o";
			String tag= "<a href=\""+fakeUrl+"\">";

			int tagLength= tag.length();
		
			aFormatter.createLink(fakeUrl);
		
			String allText=text.getHTML();
		
		
			int startSel=allText.indexOf(tag);
			int endSel=allText.indexOf("</a>", startSel);
		
			String selStr=allText.substring(startSel+tagLength, endSel);
		
			aFormatter.removeLink();
					
		
		//There is a problem, if there was no selection, the text of the link will be
		// inserted as extra text changing it.
		
		 if (selStr.equals(fakeUrl)){  // we have a problem (and we are assuming that fakeUrl
			                           // does not actually occur in the text
			 selStr="";                //We are going to return nothing
			 
			 allText=text.getHTML(); //start again with the altered text
			 
			 String beforeStr=allText.substring(0, startSel);
			 String afterStr=allText.substring(startSel+fakeUrl.length());
			 
			 if (allText.substring(startSel, startSel+fakeUrl.length()).equals(fakeUrl))
				 allText=beforeStr+afterStr; // remove insertion
			 
			 text.setHTML(allText);
			 
			 //works, but removes focus (don't worry about it)
		
		 }
		 
		 end=startSel+selStr.length(); //it's hard to get the end but this is one way
		
	//	allText=richText.getHTML();
		
	}
	}
	return
			end;
	}



	
public void writeToJournal(String message, boolean highlight,boolean toMarker){
	
	String allText=fTextForJournal.getHTML();
	int endSel=getSelectionEnd(fTextForJournal);
	
	String before=allText.substring(0,endSel);
	String after=allText.substring(endSel);
	
	fTextForJournal.setHTML(before+message+after);   //No highlighting yet

}







/*************************/ 


/*************************************************************/

void someDebugCode(){
	if (fExtraDebug){
	    fInputPanel.addStyleName("inputPanel");
	    if 	(RootPanel.get("inputPanel")!=null)
	    	RootPanel.get("inputPanel").add(fInputPanel);  
	    
		Button aWidget= fProofController.cancelButton();
		Widget [] components ={aWidget};
		
		/*  this works
		fProofController.doUni(null,null,3);
	*/	
	/*  this works
		
		TGWTTreeInputPanel testPane = new TGWTTreeInputPanel("Hello",new  TextBox(),components);
		
		fProofController.addInputPane(testPane);
		
	*/	
		/* this works
		RootPanel.get("richText").add(fInputPanel); */
		
//		fInputPanel.add(testPane); 
		
//		fProofController.bugAlert("Ullo", "lop");
		
		fJournalPane.addStyleName("journal");

		if 	(RootPanel.get("journal")!=null)
			RootPanel.get("journal").add(fJournalPane);
		
		 fJournalPane.setText("Journal");
	}
		

		
		// we'll use css to style
	   
	if (fDebug)	{
		fJournalPane.setCharacterWidth(69);
	    if ((TPreferencesData.fJournalSize!=null)&&
		   (TPreferencesData.fJournalSize.equals("large"))){
				  fJournalPane.setVisibleLines(25);
			   }
	    else
	    	fJournalPane.setVisibleLines(12);
//		fJournalPane.setSize("400px","400px");
	    fJournalPane.setText(TPreferencesData.fInputText);
	    
	    fJournalPane.addStyleName("journal");
	    
	 //   fPropLevel=TPreferencesData.fPropLevel;


	// We can add style names to widgets
//		sendButton.addStyleName("sendButton");

	// Add the nameField and sendButton to the RootPanel
	// Use RootPanel.get() to get the entire body element

	  //  fInputPanel.addStyleName("input");
	    
	    if 	(RootPanel.get("inputPanel")!=null)
	    	RootPanel.get("inputPanel").add(fInputPanel);   
	    
	    
	    fProofController.bugAlert("Ullo", "lop");
	    
	    
	fJournalPane.addStyleName("journal");

	if 	(RootPanel.get("journal")!=null)
		RootPanel.get("journal").add(fJournalPane);

	//RootPanel.get("treeContainer").add(fTreeCellTable);

	//RootPanel.get().add(fGrid);

	} //endif debug  DEBUG
	

	
}

}



