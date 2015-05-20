package us.softoption.gwt.trees.client;
 


import static us.softoption.infrastructure.Symbols.strNull;
import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.GWTSymbolToolbar;
import us.softoption.infrastructure.TPreferencesData;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TBergmannParser;
import us.softoption.parser.TCopiParser;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TGirleParser;
import us.softoption.parser.THausmanParser;
import us.softoption.parser.THerrickParser;
import us.softoption.parser.THowsonParser;
import us.softoption.parser.TParser;
import us.softoption.tree.TBarwiseTreeController;
import us.softoption.tree.TGWTTree;
import us.softoption.tree.TGWTTreeInputPanel;
import us.softoption.tree.TTreeController;
import us.softoption.tree.TTreeDisplayCellTable;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/* This expects html with

<div id="container">

<h3>Trees</h3>

<div id="menu"> </div>

<div id="menubuttons"> </div>

<div id="input"> </div>

<div id="tree"> </div>

<div id="startButton" ></div>  

<div id="journal" ></div> 

<div id="buttons"></div>  
  
 </div>

*/

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TreesJS implements EntryPoint, TJournal, TReset {

	VerticalPanel fInputPanel = new VerticalPanel(); // for BugAlert and TreeInputPane  
    
	
	TTreeDisplayCellTable fDisplayTable = new TTreeDisplayCellTable();//replaces TreePanel
	ScrollPanel fScrollPanel=null; //holds the displayed table
	GWTSymbolToolbar fSymbolToolbar;  // tend to use this in preference to the JournalPane
	final TextArea fJournalPane = new TextArea();// often not visible, if using buttons to start
	
	RichTextArea fTextForJournal = new RichTextArea();
	
	final HorizontalPanel fMenuButtonsPanel = new HorizontalPanel(); // menu buttons
	final HorizontalPanel fModalMenuButtonsPanel = new HorizontalPanel(); // menu buttons
	final HorizontalPanel fComponentsPanel = new HorizontalPanel(); //buttons
	
	TTreeController fTreeController= null;
	static TParser fParser=new TDefaultParser();
	
	MenuBar fMenuBar = new MenuBar();  //true makes it vertical
	MenuBar fActions = new MenuBar(true);  //true means vertial	
	MenuItem fExtend;	
    MenuItem fClose;
    MenuItem fIsClosed;
    MenuItem fOpenBranch;
    MenuItem fStartOver;
    
    Button fExtendButton= new Button("Extend",new ClickHandler(){@Override 
				public void onClick(ClickEvent event) {
    			fTreeController.extendTree();}});
    Button fAnaConButton= new Button("Ana Con",new ClickHandler(){@Override 
				public void onClick(ClickEvent event) {
    			fTreeController.executeAnaCon();}});
    Button fCloseButton= new Button("Close",new ClickHandler(){@Override 
		public void onClick(ClickEvent event) {
		fTreeController.closeBranch();}});
    Button fIsClosedButton= new Button("Closed?",new ClickHandler(){@Override 
		public void onClick(ClickEvent event) {
		fTreeController.executeIsClosed();}});
    Button fOpenBranchButton= new Button("Complete Open Branch?",new ClickHandler(){@Override 
		public void onClick(ClickEvent event) {
		fTreeController.executeIsOpenAndComplete();}});
    Button fIdentityIntroButton= new Button("Identity Intro",new ClickHandler(){@Override 
		public void onClick(ClickEvent event) {
		fTreeController.executeII();}}); 
    

	
	static boolean fPropLevel=false;
	boolean fBarwise=false;          // with AnaCon, significantly different
	
	static final boolean HIGHLIGHT = true;
	
	 //Palette fPalette;
	 //SymbolToolbar fSymbolToolbar;
	 
	// HTMLEditorKit fEditorKit;
	 Label  fLabel=new Label("Trees");
	 //JPanel fComponentsPanel;    //usually buttons

	 String fInputText=null;
	 
	// Dimension fPreferredSize=new Dimension(600,400);
	 //Dimension fMinimumSize=new Dimension(/*500*/ 540,300);
	// Dimension fJournalPreferredSize=new Dimension(600,300);
	

	TGWTTree fGWTTree= new TGWTTree();
	
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
	
	
	// A command for general use
	Command command = new Command()
	{
	    public void execute()
	    {
	  ;//      Window.alert("Command Fired");
	    }
	};
	
	Command extendCommand = new Command()
	{
	    public void execute()
	    {
	    	fTreeController.extendTree();
	    }
	};
	
	Command closeCommand = new Command()
	{
	    public void execute()
	    {
	    	fTreeController.closeBranch();
	    }
	};
	
	Command closedCommand = new Command()
	{
	    public void execute()
	    {
	    	fTreeController.executeIsClosed();
	    }
	};
	
	Command openBranchCommand = new Command()
	{
	    public void execute()
	    {
	    	fTreeController.executeIsOpenAndComplete();
	    }
	};
	
	Command startOverCommand = new Command()
	{
	    public void execute()
	    {
	    	fTreeController.startTree(fTreeController.getStartStr()); 
	    }
	};
	
	
	// Top-level menu
	
	fMenuBar.addStyleName("menu");
	
	fMenuBar.addItem("Actions",fActions); // Creates item and adds menutwo
	
	fExtend = new MenuItem("Extend", extendCommand);	
	fActions.addItem(fExtend);
	
    fClose = new MenuItem("Close", closeCommand);	
	fActions.addItem(fClose);
	
	fActions.addSeparator();
	
    fIsClosed = new MenuItem("Closed?", closedCommand);	
	fActions.addItem(fIsClosed);
	
    fOpenBranch = new MenuItem("Complete Open Branch?", openBranchCommand);	
	fActions.addItem(fOpenBranch);
	
	fActions.addSeparator();
	
    fStartOver = new MenuItem("Start Over", startOverCommand);	
	fActions.addItem(fStartOver);
	
	// commented out at present 	RootPanel.get("menu").add(fMenuBar);
}


/*******************  TReset **************************/

public void enableMenus(){  //at present GWT does not have a good way of doing this.
	
	fActions.addItem(fExtend);	
	fActions.addItem(fClose);	
	fActions.addSeparator();	
	fActions.addItem(fIsClosed);	
	fActions.addItem(fOpenBranch);	
	fActions.addSeparator();	
	fActions.addItem(fStartOver);

	if (!fMenuButtonsPanel.isAttached())
		RootPanel.get("menubuttons").add(fMenuButtonsPanel);
}

public void disableMenus(){
fActions.clearItems();

if (fMenuButtonsPanel.isAttached())
	RootPanel.get("menubuttons").remove(fMenuButtonsPanel);

}

public void reset(){
	;
}

/******************** End of TReset **********************************/


void buildMenuButtons(){
	Widget[] menuButtons={fExtendButton, fCloseButton, fIsClosedButton, fOpenBranchButton,
			fIdentityIntroButton};
	
	if (fPropLevel)
       {
		Widget[] simpleButtons={fExtendButton, fCloseButton, fIsClosedButton, fOpenBranchButton};
		menuButtons=simpleButtons;
       }
	int dummy=0;
	
	initializeMenuButtons(menuButtons,dummy);
	
	if (RootPanel.get("menubuttons")!=null)
		RootPanel.get("menubuttons").add(fMenuButtonsPanel);
	
}

void buildBarwiseMenuButtons(){
	Widget[] menuButtons={fExtendButton, fAnaConButton,fCloseButton, fIsClosedButton, 
			fOpenBranchButton};
	
	int dummy=0;
	
	initializeMenuButtons(menuButtons,dummy);
	
	if (RootPanel.get("menubuttons")!=null)
		RootPanel.get("menubuttons").add(fMenuButtonsPanel);
	
}

void buildModalMenuButtons(){
	Widget[] menuButtons={fExtendButton, fCloseButton, fIsClosedButton, fOpenBranchButton,
			fIdentityIntroButton};
	
	if (fPropLevel)
       {
		Widget[] simpleButtons={fExtendButton, fCloseButton, fIsClosedButton, fOpenBranchButton};
		menuButtons=simpleButtons;
       }
	int dummy=0;
	
	initializeModalMenuButtons(menuButtons,dummy);
	
	if (RootPanel.get("modalmenubuttons")!=null)
		RootPanel.get("modalmenubuttons").add(fModalMenuButtonsPanel);
	
}

	

void someDebugCode(){
	if (fExtraDebug){
	    fInputPanel.addStyleName("inputPanel");
	    RootPanel.get("inputPanel").add(fInputPanel);  
	    
		Button aWidget= fTreeController.cancelButton();
		Widget [] components ={aWidget};
		
		/*  this works
		fTreeController.doUni(null,null,3);
	*/	
	/*  this works
		
		TGWTTreeInputPanel testPane = new TGWTTreeInputPanel("Hello",new  TextBox(),components);
		
		fTreeController.addInputPane(testPane);
		
	*/	
		/* this works
		RootPanel.get("richText").add(fInputPanel); */
		
//		fInputPanel.add(testPane); 
		
//		fTreeController.bugAlert("Ullo", "lop");
		
		fJournalPane.addStyleName("journal");

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
	    RootPanel.get("inputPanel").add(fInputPanel);   
	    
	    
	    fTreeController.bugAlert("Ullo", "lop");
	    
	    
	fJournalPane.addStyleName("journal");

	RootPanel.get("journal").add(fJournalPane);

	//RootPanel.get("treeContainer").add(fTreeCellTable);

	//RootPanel.get().add(fGrid);

	} //endif debug  DEBUG
	
	if (fDebug)
		RootPanel.get("treeContainer").add(fGWTTree);
	
	if (fDebug){
		Button extendButton=new Button("Extend");

		ExtendHandler extHandler = new ExtendHandler();
		extendButton.addClickHandler(extHandler);

		RootPanel.get("extend").add(extendButton);
		}
	
}


void createGUI(){
	
if (fDebug)
	someDebugCode();

buildMenus();

if (fBarwise)
	buildBarwiseMenuButtons();
else
	buildMenuButtons();

buildModalMenuButtons();
 
Widget [] paramButtons =readParamProofs();

RootPanel.get("input").add(fInputPanel);

fScrollPanel=new ScrollPanel(fDisplayTable);

fScrollPanel.setSize("600px", "400px");  //need this

RootPanel.get("tree").add(fScrollPanel);

if ((paramButtons.length)>0)
	   finishNoPalette(paramButtons);
else
	   finishWithPalette();		

}




void finishNoPalette(Widget [] components){
	int depth=30;    // this is the height of the buttons
	
	 initializeComponentsPanel(components,depth);
		
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

void initializeMenuButtons(Widget [] components,int depth){
	
	fMenuButtonsPanel.setStyleName("menubuttons");
	
	fMenuButtonsPanel.setSpacing(10);
	
	fMenuButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	
	fMenuButtonsPanel.setHeight("50px");

	 for (int i=0;i<components.length;i++){
				      fMenuButtonsPanel.add(components[i]);
				    }
				}

void initializeModalMenuButtons(Widget [] components,int depth){
		
	
	fModalMenuButtonsPanel.setStyleName("modalmenubuttons");
	
	fModalMenuButtonsPanel.setSpacing(10);
	
	fModalMenuButtonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	
	fModalMenuButtonsPanel.setHeight("50px");

	Widget[] buttons=fTreeController.supplyModalRadioButtons();
	
	for (int i=0;i<buttons.length;i++)
	{
		fModalMenuButtonsPanel.add(buttons[i]);	
		
	}
}	




void finishWithPalette(){

boolean lambda=false,modal=false,settheory=false;


//lambda=TPreferencesData.fLambda;



	String symbols =  fParser.getInputPalette(TPreferencesData.fLambda,TPreferencesData.fModal,
			TPreferencesData.fSetTheory);
	
	
	
	fTextForJournal.setWidth("100%");
	
	//text.setText(fInputText);
	
	fTextForJournal.setHTML(fInputText);

	fSymbolToolbar = new GWTSymbolToolbar(fTextForJournal,symbols);

	RootPanel.get("journal").add(fSymbolToolbar);
	RootPanel.get("journal").add(fTextForJournal);
	
	
	
	//RootPanel.get("richText").add(startButton());
	RootPanel.get("startButton").add(startButton());
	
	//String label, RichTextArea textField, Widget [] components
	
	//PushButton aWidget= new PushButton();
	Button aWidget= fTreeController.cancelButton();
	Widget [] components ={aWidget};
	
	
	TGWTTreeInputPanel fInputPane = new TGWTTreeInputPanel("Hello",new  TextBox(),components);
	

	/* this works
	RootPanel.get("richText").add(fInputPanel);
	
	fInputPanel.add(fInputPane); */
	
	
	
	
	
	
/*	
	if (symbols==null)
		symbols="";
	
	fPalette= new Palette(symbols,fJournalPane);
	fPalette.setSize(new Dimension(330, 32));  //was 21
	fPalette.setMaximumSize(new Dimension(330, 32));
	fPalette.setMinimumSize(new Dimension(330, 32));
	fPalette.setPreferredSize(new Dimension(330, 32));
	
	fSymbolToolbar= new SymbolToolbar(symbols,fJournalPane);
	
	
	 contentPane.add(/*fPalette fSymbolToolbar,new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));	
	
	
	int depth =fJournalPane.getPreferredSize().height;	
	// put the journal in a scroller	
	JScrollPane aScroller= new JScrollPane(fJournalPane);
	aScroller.setPreferredSize(fJournalPreferredSize);
	aScroller.setMinimumSize(new Dimension(300,200));
	aScroller.setMinimumSize(new Dimension(380,200));
	aScroller.setMinimumSize(new Dimension(360,200));
	// put scroller and button in components
	JComponent[] components= {aScroller,startButton()};
	
	initializeComponentsPanel(components,depth);
	// put compenents in content
	 contentPane.add(fComponentsPanel,new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
		       ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));	
	*/
}



class ExtendHandler implements ClickHandler {
	/**
	 * Fired when the user clicks on the sendButton.
	 */
	public void onClick(ClickEvent event) {
		
		fTreeController.extendTree();
		
	}

}	



void setLocalParameters(){
	fInputText=TPreferencesData.fInputText;  // probably don't need input text field
	
	fJournalPane.setText(TPreferencesData.fInputText);  // not using journal, use toolbar
	fPropLevel=TPreferencesData.fPropLevel;
	
	//parser initialized to default parser
	
	{ String parser =TPreferencesData.fParser;
	if (parser!=null) {
		if (parser.equals("barwise")){
			fBarwise=true;
			fParser =new THowsonParser();  //use Howson for Barwise
			}
		if (parser.equals("bergmann")){
			   fParser =new TBergmannParser();
			   //fEtoL.resetToBergmannRules();
		   }
		if (parser.equals("copi")){
			   fParser =new TCopiParser();
			   //fEtoL.resetToCopiRules();
		   }
		if (parser.equals("gentzen")){
			   fParser =new TParser();
			   //fEtoL.resetToGentzenRules();
		   }
		if (parser.equals("girle")){
			   fParser =new TGirleParser();
		 	}
		if (parser.equals("hausman")){
			   fParser =new THausmanParser();
			   //fEtoL.resetToCopiRules();
		   }
		if (parser.equals("herrick")){
			   fParser =new THerrickParser();
			   //fEtoL.resetToHerrickRules();
			}
		if (parser.equals("howson")){
			   fParser =new THowsonParser();

			}
	}
	}
	
	fDisplayTable= new TTreeDisplayCellTable();
	if (fBarwise){
		fTreeController=new TBarwiseTreeController (fParser,this,this,fInputPanel,fGWTTree,fDisplayTable);
	}else
		fTreeController=new TTreeController (fParser,this,this,fInputPanel,fGWTTree,fDisplayTable);
	
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

   String param= "tree"+i;	
	
   String value= readParameterValue(param);
	   while (value!=null&&!value.equals("")&&i<10){
		   i++;
		   param= "tree"+i;
		   value= readParameterValue(param);
	   }
	   
	   
	if (i>0){   
	int count =i;
	   components= new Widget[count];
	   i=0;	
	   param= "tree"+i;	   
	   String label="Tree";	   
	   if (count>6)
		   label="Tr";     // we only fit 6, but we will squeeze a few more
		
       value= readParameterValue(param);
		   while (value!=null&&!value.equals("")&&i<10){
			   components[i]=proofButton(label+(i+1),value);
			   i++;
			   param= "tree"+i;
			   value= readParameterValue(param);
		   }
	}
	   	   
	   return 
	   components;
}

/***********************  Buttons ***************************/




Button proofButton(String label, final String inputStr){
	Button button = new Button(label);	
	
	
	TreeHandler tHandler = new TreeHandler(inputStr);
	button.addClickHandler(tHandler);

	/*
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			String filteredStr=TUtilities.logicFilter(inputStr);
    //     filteredStr=TUtilities.htmlEscToUnicodeFilter(filteredStr);
		fTreePanel.startTree(filteredStr);
		}}); */
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
		
		
				fTreeController.startTree(filteredStr);
		}
			});
	
	
	
	
	
	/*	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			readAndStart();
		}}); */
	return
	   button;
}

/***********************  End of Buttons ***************************/





	
	
	
	
	
	

/*****************  Commands ************************/
	





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

			
			
		// Create a handler for the symbolizeButton
	class TreeHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on aTreeButton.
			 */
		String fFilteredInput="";
		
		public TreeHandler(String inputStr) {
			fFilteredInput=TUtilities.logicFilter(inputStr);		
		}
			
			public void onClick(ClickEvent event) {
				fTreeController.startTree(fFilteredInput);

			}
		
	}	
	
	
	
/*
 * JButton proofButton(String label, final String inputStr){
		JButton button = new JButton(label);	    
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String filteredStr=TUtilities.logicFilter(inputStr);
        //     filteredStr=TUtilities.htmlEscToUnicodeFilter(filteredStr);
			fTreePanel.startTree(filteredStr);
			}});
		return
		   button;
	}
 * 
 * 
 * */	

		

	




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


/*        int newCaretPosition = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one

        int messageLength = message.length();

        if (messageLength>0) {

          fJournalPane.setSelectionStart(newCaretPosition);
          fJournalPane.setCaretPosition(newCaretPosition);    //leave existing selection and do everything after

          fJournalPane.replaceSelection(message);

          if (highlight) {
            fJournalPane.setSelectionStart(newCaretPosition);
            fJournalPane.setSelectionEnd(newCaretPosition+messageLength);

          }

          fJournalPane.requestFocusInWindow(); //new Oct09
        }
     }	
	
*/
}







/*************************/ 


/*************************************************************/

}



