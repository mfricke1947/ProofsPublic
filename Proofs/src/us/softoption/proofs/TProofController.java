

/*
 The deriver document is both data and an intermediary between the three data panes (the journal,
 the proof, and the drawing).


 The deriver document has all the data (conceptually a file). There is a browser that
 looks at the data. One browser can change data from one document to another document, or you can
 have several browsers each with their own data


 Feb 06 Adding a fourth data panel, for Trees


 */


package us.softoption.proofs;


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strCR;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.TPreferencesData;

import us.softoption.infrastructure.TUtilities;
import us.softoption.infrastructure.undo.UndoManager;
import us.softoption.infrastructure.undo.UndoableEdit;
import us.softoption.infrastructure.undo.UndoableEditEvent;
import us.softoption.infrastructure.undo.UndoableEditListener;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


import us.softoption.tree.TGWTTestNode;
import us.softoption.tree.TGWTTree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class TProofController{
	
	TJournal fJournal;
	VerticalPanel fInputPanel = null;
	TReset fProofClient = null;
	TParser fParser = null;   // note the document has one too, maybe only need one (although each proofline needs a reference to one

public String fProofStr=""; /* this is to hold the string version of the premises and conclusion, so that we
	  can confirm which proof was proved. Filled in load()*/

MenuBar fMenuBar = new MenuBar();  //true makes it vertical
MenuBar fRules = new MenuBar(true);  //true means vertical
MenuBar fAdvancedRules = new MenuBar(true);  //true means vertical
MenuBar fEdit = new MenuBar(true);  //true means vertical
MenuBar fWizard = new MenuBar(true);  //true means vertical

MenuItem fUndoRedo= new MenuItem("Undo Redo", undoRedoCommand());
MenuItem fCutProofline= new MenuItem("Cut Proofline", cutProoflineCommand());
MenuItem fPrune= new MenuItem("Prune", pruneCommand());
MenuItem fStartAgain= new MenuItem("Start Again", startAgainCommand());
MenuItem fNewGoal= new MenuItem("New Subgoal", newGoalCommand());  //to do

MenuItem fTactics= new MenuItem("Tactics ?", tacticsCommand());
MenuItem fNextLine= new MenuItem("Next Line", nextLineCommand());
MenuItem fDeriveIt= new MenuItem("Derive It", deriveItCommand());


MenuItem fClose;
MenuItem fIsClosed;
MenuItem fOpenBranch;
MenuItem fStartOver;

//undo helpers
//protected UndoAction fUndoAction = new UndoAction();
//protected TUndoRedoButton fUndoRedoButton = new TUndoRedoButton();
//protected MenuItem fUndoRedoMenuItem = new MenuItem("Undo", undoRedoCommand());



//protected RedoButton fRedoButton = new RedoButton();
protected UndoManager fUndoManager = new UndoManager();
//private UndoableEditListener fListener;     // only allows one, more typical to have list
private ArrayList <UndoableEditListener>fListeners = new ArrayList<UndoableEditListener>();  // july 06 implementatation of list of listeners

static TUndoableProofEdit fLastEdit=null; // this is so we can restrict the undos to 1






	
static final char chBlank = ' ';
static final char chComma = ',';
static final char chLSqBracket = '[';
static final char chRSqBracket = ']';
public static final char chTherefore='\u2234';


	
	final static int noPremNoConc=1;
	  final static int noPremConc=2;
	  final static int premNoConc=3;
	  final static int premConc=4;
	  final static int pfFinished=5;



	  final static int kMaxNesting = 10; //no of subprooflevels}


	  final static String negEJustification=" "+ chNeg + "E";


	  //final static String orIJustification=" "+ chOr + "I";
	  //final static String arrowIJustification=" "+ chImplic + "I";

	 // final static String andEJustification=" "+ chAnd + "E";
	  final static String equivIJustification=" "+ chEquiv + "I";
	  final static String equivEJustification=" "+ chEquiv + "E";
	  final static String absIJustification=" AbsI";

	  static String EGJustification=" EG";
	  final static String UIJustification=" UI";
	  static String UGJustification=" UG";

	  final static String typedRewriteJustification=" Type";
	 // final static String typedUniJustification=" Type";

	  final static String IEJustification=" =E";
	  final static String IIJustification=" =I";

	  final static String uniqueIJustification=" !I";
	  final static String uniqueEJustification=" !E";

	  final static String inductionJustification="Induction";  // no space needed, no line numbers


	  final static String questionJustification="?";
	  final static String repeatJustification=" R";



	  static String fAndEJustification=" "+ chAnd + "E";   // subclass can alter this
	  static String fAndIJustification=" "+ chAnd + "I";   // subclass can alter this
	  final static String fAssJustification="Ass";  // subclass CANNOT alter this. we use it to identify assumptions

	  //if it needs to display differently, use the TProofline drawing routine
	  // in particular String transformJustification(String inStr)

	  static String fOrIJustification=" "+ chOr + "I";   // subclass can alter this
	  static String fCommJustification=" Com";   // subclass can alter this

	  static String fImplicEJustification=" "+ chImplic + "E";

	  static String fImplicIJustification=" "+ chImplic + "I";     //used in traceBack()
	  static String fNegIJustification=" "+ chNeg + "I";
	  static String fNegEJustification=" "+ chNeg + "E";
	  static String fOrEJustification=" "+ chOr + "E";
	  static String fEquivIJustification=" "+ chEquiv + "I";
	  static String fEIJustification=" EI";

	  static String fTIInput = "Doing TI";


	  boolean fTemplate=false;
	  int fProofType;
	 // int fRightMargin=200;  //for prooflines Leave this with Model then Beans will save it

	  ScrollPanel jScrollPane1 = new ScrollPanel();  // to contain the prooflist

	  TProofDisplayCellTable fDisplayCellTable= null;
	  public TProofListModel fModel= null;  // this is where the data is

//	  TProofTableView fProofListView = new TProofTableView(this,fModel);//to display the data



//	  TDeriverDocument fDeriverDocument;
	  //TJournal fDeriverDocument;

	  //undo helpers
//	  protected UndoAction fUndoAction = new UndoAction();
////	  protected RedoAction fRedoAction = new RedoAction();
//	  protected UndoManager fUndoManager = new UndoManager();
//	  private UndoableEditListener fListener;     // only allows one, more typical to have list

	 

//	  static TUndoableProofEdit fLastEdit=null; // this is so we can restrict the undos to 1

	  VerticalPanel fInputPane = null; // this takes input in a modeless dialog fashion, visually above the proof
	  TGWTProofInputPanel fGWTInputPanel=null;
	  
	  /*Feb 2013, I'm not sure why and this may need sorting later,
	   * but for some reason there is a vertical panel which is used
	   * as an exterior host for both bugalert and tgwtinput
	   */

	  /*String fInputPalette=strNeg+strAnd+strOr+chImplic+chEquiv+chUniquant+chExiquant+chLambda+chMemberOf
	  +chNotMemberOf+chUnion+chIntersection+chPowerSet+chSubset+chEmptySet; */
	  
	  String fInputPalette="";   // call parser to initialize
	  

	  
	  
	 /* Button /*JMenuItem tIMenuItem = new Button("TI",new ClickHandler(){@Override 
			public void onClick(ClickEvent event) {
			doTI();}}); */
	  
	  MenuItem tIMenuItem = new MenuItem("Assumption",new Command(){
			public void execute() {
			doTI();}});  
	  MenuItem negIMenuItem = new MenuItem("~I",new Command(){
			public void execute() {
			doNegI();}});
	  MenuItem negEMenuItem = new MenuItem("~E",new Command(){
			public void execute() {
			doNegE();}});
	  
	  MenuItem andIMenuItem = new MenuItem(chAnd +"I",new Command(){
			public void execute() {
			doAndI();}});
	  
	  MenuItem andEMenuItem = new MenuItem(chAnd +"E",new Command(){
			public void execute() {
			doAndE();}});
	  
	  MenuItem equivIMenuItem = new MenuItem(chEquiv +"I",new Command(){
			public void execute() {
			doEquivI();}}); 
	  
	  MenuItem equivEMenuItem = new MenuItem(chEquiv +"E",new Command(){
			public void execute() {
			doEquivE();}});
	  
	  MenuItem orIMenuItem = new MenuItem(chOr+"I",new Command(){
			public void execute() {
			boolean rightOnly=true;
			dovI(!rightOnly);}});
	  
	  MenuItem orEMenuItem = new MenuItem(chOr+"E",new Command(){
			public void execute() {
			boolean rightOnly=true;
			dovE();}});
	  
	  MenuItem implicIMenuItem = new MenuItem(chImplic +"I",new Command(){
			public void execute() {
			doImplicI();}});
	  
	  MenuItem implicEMenuItem = new MenuItem(chImplic +"E",new Command(){
			public void execute() {
			doImplicE();}});
	  
	  MenuItem uGMenuItem = new MenuItem("UG",new Command(){
			public void execute() {
			doUG();}});
	  
	  MenuItem uIMenuItem = new MenuItem("UI",new Command(){
			public void execute() {
			doUI();}});
	  
	  MenuItem eGMenuItem = new MenuItem("EG",new Command(){
			public void execute() {
			doEG();}});
	  
	  MenuItem eIMenuItem = new MenuItem("EI",new Command(){
			public void execute() {
			doEI();}});
	  
	  MenuItem theoremMenuItem = new MenuItem("Theorem",new Command(){
			public void execute() {
			doTheorem();}});
	  
	  MenuItem absurdIMenuItem = new MenuItem("Absurd I",new Command(){
			public void execute() {
			doAbsI();}});
	  
	  
	  MenuItem iIMenuItem = new MenuItem("=I",new Command(){
			public void execute() {
			doII();}});	  
	  
	  MenuItem iEMenuItem = new MenuItem("=E",new Command(){
			public void execute() {
			doIE();}});	  
	  
	  MenuItem inductionMenuItem = new MenuItem("Induction",new Command(){
			public void execute() {
			doInduction();}});	  
	  
	  MenuItem rAMenuItem = new MenuItem("Repeat",new Command(){
			public void execute() {
			doRA();}});	  

	  

	  MenuItem uniqueIMenuItem = new MenuItem("!I",new Command(){
			public void execute() {
			doUniqueI();}});	  
	  
	  MenuItem uniqueEMenuItem = new MenuItem("!E",new Command(){
			public void execute() {
			doUniqueE();}});

	  MenuItem absIMenuItem = new MenuItem("Absurd I",new Command(){
			public void execute() {
			doAbsI();}});
	  
	  MenuItem rewriteMenuItem = new MenuItem("Rewrite Rules",new Command(){
			public void execute() {
			doRewrite();}});



	 // GridBagLayout gridBagLayout1 = new GridBagLayout();

//	  Button /*JMenuItem*/ newGoalMenuItem = new Button /*JMenuItem*/();

	  Button /*JMenuItem*/ cutLineMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ pruneMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ startAgainMenuItem = new Button /*JMenuItem*/();

//	  JMenu fWizardMenu = new JMenu();
//	  CheckBox tacticsMenuItem = new CheckBox();
	  Button /*JMenuItem*/ absurdMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ writeProofMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ writeConfirmationMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ marginMenuItem = new Button /*JMenuItem*/();
	  Button /*JMenuItem*/ nextLineMenuItem = new Button /*JMenuItem*/();
//	  Button /*JMenuItem*/ deriveItMenuItem = new Button /*JMenuItem*/();




	 boolean fUseIdentity=false;    // for getting more menu items independently of Preferences

	 private boolean fLambda=false;   // Lambda proofs have different menus
	                                       // the same proof panel is used for both
	                                       // so, on Load, the set Lambda (t/f)
	                                       // should be called to get the menus right
	
	
	
	
	 static final boolean SELECT=true;
	
	
	
	/*************** End of fields **********************/
	
	
	
	public TProofController(){
				
		  fDisplayCellTable = new TProofDisplayCellTable(this);
		  fModel = fDisplayCellTable.getModel();
		  fModel.setDisplay(fDisplayCellTable);
		  
		  localizeJustStrings();
		  
		  componentInitialization();
		
		
		
		
		

	}		
	
public TProofController(TJournal itsJournal){
	fJournal=itsJournal;
	
	  fDisplayCellTable = new TProofDisplayCellTable(this);
	  fModel = fDisplayCellTable.getModel();
	  fModel.setDisplay(fDisplayCellTable);
	  
	  localizeJustStrings();
	  componentInitialization();
}

public TProofController(TParser aParser, TReset aClient,TJournal itsJournal, VerticalPanel inputPanel,
		 TProofDisplayCellTable itsDisplay){
	
	  fParser=aParser;
	  fProofClient=aClient;
	  fJournal=itsJournal;
	  fInputPanel = inputPanel;
	
	  fDisplayCellTable = itsDisplay;//new TProofDisplayCellTable(this);
	  fModel = fDisplayCellTable.getModel();
	  fModel.setDisplay(fDisplayCellTable);
	  
	  localizeJustStrings();
	  componentInitialization();
	
}



void initializeParser(){
	  fParser=new TDefaultParser();
	};


	/********** More initialization ************
	 * 
	 */

	 void localizeJustStrings(){
		 
/* TO DO, not sure if need this for proofs		 
		 
			
			String localNeg=fParser.translateConnective(strNeg);
			String localAnd=fParser.translateConnective(chAnd);
			String localOr=fParser.translateConnective(chOr);
			String localImplic=fParser.translateConnective(chImplic);
			String localEquiv=fParser.translateConnective(chEquiv);
			String localUniquant= !fParser.translateConnective(chUniquant).equals("")?  //some systems do not use uniquant
			               fParser.translateConnective(chUniquant):
			               "U";
			String localExiquant=fParser.translateConnective(chExiquant);
			               String localIdentity=fParser.translateConnective(chIdentity);


			   andDJustification = " " + localAnd + "D";	   
			   negDJustification = " "+localNeg+localNeg+"D";	   
			   implicDJustification = " " + localImplic + "D";	   
			   equivDJustification = " " + localEquiv + "D";	   
			   exiDJustification = " " + localExiquant + "D";	   
			   negAndDJustification = " "+localNeg + localAnd + "D";	   
			   negArrowDJustification = " "+localNeg + localImplic + "D";	   
			   negEquivDJustification = " "+localNeg + localEquiv + "D";	   
			   negExiDJustification = " "+localNeg + localExiquant + "D";	   
			   negUniDJustification = " "+localNeg + localUniquant + "D";   
			   noreDJustification = " "+localNeg + localOr + "D";	   
			   orDJustification = " " + localOr + "D";
			   UDJustification = " "+localUniquant+ "D";
			   identityDJustification = " "+localIdentity+"D";	
			
			
		*/	
			
		}	
	 

void componentInitialization(){
	
    UndoableEditListener aListener= new UndoableEditListener(){
        public void undoableEditHappened(UndoableEditEvent e)
            {
              //Remember the edit and update the menus.
            fUndoManager.addEdit(e.getEdit());
      //      fUndoRedoButton.updateUndoState();
            updateUndoState(fUndoRedo);
 //TO DO           fRedoButton.updateRedoState();

//            fDeriverDocument.setDirty(true);
             }
         };

addUndoableEditListener(aListener);
	
	
}
	 
	 
	 
/*	 
	 //Component initialization
	  private void jbInit() throws Exception  {
	    this.setSize(new Dimension(300, 400));
	    this.setLayout(gridBagLayout1);

	    fRulesMenu.setDoubleBuffered(true);
	    fRulesMenu.setText("Rules");
	    fRulesMenu.addMenuListener(new TProofPanel_fRulesMenu_menuAdapter(this));
	    fRulesMenu.addMouseListener(new TProofPanel_fRulesMenu_mouseAdapter(this));

	    //if (TPreferences.fIdentity) {

	    fAdvancedRules  = new JMenu();
	    fAdvancedRules.setText("Advanced");
	    fAdvancedRules.addMenuListener(new TProofPanel_fRulesMenu_menuAdapter(this));
	    //the adapter just enables or disables all the rules, so we can use the same one as for the plain rules
	    //}


	    fEditMenu.setText("Edit+");
	    fEditMenu.addMouseListener(new TProofPanel_fEditMenu_mouseAdapter(this));
	    rAMenuItem.setText("Repeat");
	    rAMenuItem.addActionListener(new TProofPanel_rAMenuItem_actionAdapter(this));

	    tIMenuItem.addActionListener(new TProofPanel_tIMenuItem_actionAdapter(this));
	    tIMenuItem.setText("Assumption");
	    negIMenuItem.setText("~I");
	    negIMenuItem.addActionListener(new TProofPanel_negIMenuItem_actionAdapter(this));
	    negEMenuItem.setText("~E");
	    negEMenuItem.addActionListener(new TProofPanel_negEMenuItem_actionAdapter(this));
	    andIMenuItem.setText("^I");
	    andIMenuItem.addActionListener(new TProofPanel_andIMenuItem_actionAdapter(this));
	    andEMenuItem.setText("^E");
	    andEMenuItem.addActionListener(new TProofPanel_andEMenuItem_actionAdapter(this));
	    theoremMenuItem.setText("Theorem");
	    theoremMenuItem.addActionListener(new TProofPanel_theoremMenuItem_actionAdapter(this));

	    iIMenuItem.setText("=I");
	    iIMenuItem.addActionListener(new TProofPanel_iIMenuItem_actionAdapter(this));
	    iEMenuItem.setText("=E");
	    iEMenuItem.addActionListener(new TProofPanel_iEMenuItem_actionAdapter(this));

	    uniqueIMenuItem.setText("!I");
	    uniqueIMenuItem.addActionListener(new TProofPanel_uniqueIMenuItem_actionAdapter(this));
	    uniqueEMenuItem.setText("!E");
	    uniqueEMenuItem.addActionListener(new TProofPanel_uniqueEMenuItem_actionAdapter(this));

	    inductionMenuItem.setText("Induction");
	    inductionMenuItem.addActionListener(new TProofPanel_inductionMenuItem_actionAdapter(this));

	    rewriteMenuItem.setText("Rewrite Rules");
	    rewriteMenuItem.addActionListener(new TProofPanel_rewriteMenuItem_actionAdapter(this));


	    orIMenuItem.setText("vI");
	    orIMenuItem.addActionListener(new TProofPanel_orIMenuItem_actionAdapter(this));
	    orEMenuItem.setText("vE");
	    orEMenuItem.addActionListener(new TProofPanel_orEMenuItem_actionAdapter(this));
	    implicIMenuItem.setText(chImplic + "I");
	    implicIMenuItem.addActionListener(new TProofPanel_implicIMenuItem_actionAdapter(this));
	    implicEMenuItem.setText(chImplic +"E");
	    implicEMenuItem.addActionListener(new TProofPanel_implicEMenuItem_actionAdapter(this));
	    equivIMenuItem.setText(chEquiv +"I");
	    equivIMenuItem.addActionListener(new TProofPanel_equivIMenuItem_actionAdapter(this));
	    equivEMenuItem.setText(chEquiv +"E");
	    equivEMenuItem.addActionListener(new TProofPanel_equivEMenuItem_actionAdapter(this));
	    uGMenuItem.setText("UG");
	    uGMenuItem.addActionListener(new TProofPanel_uGMenuItem_actionAdapter(this));
	    uIMenuItem.setText("UI");
	    uIMenuItem.addActionListener(new TProofPanel_uIMenuItem_actionAdapter(this));
	    eGMenuItem.setText("EG");
	    eGMenuItem.addActionListener(new TProofPanel_eGMenuItem_actionAdapter(this));
	    eIMenuItem.setText("EI");
	    eIMenuItem.addActionListener(new TProofPanel_eIMenuItem_actionAdapter(this));
	    cutLineMenuItem.setText("Cut Proofline");
	    cutLineMenuItem.addActionListener(new TProofPanel_cutLineMenuItem_actionAdapter(this));
	    newGoalMenuItem.addActionListener(new TProofPanel_newGoalMenuItem_actionAdapter(this));
	    newGoalMenuItem.setText("New Subgoal");
	    fWizardMenu.setText("Wizard");
	    fWizardMenu.addMenuListener(new TProofPanel_fWizardMenu_menuAdapter(this));
	    //fWizardMenu.addMouseListener(new TProofPanel_fWizardMenu_mouseAdapter(this));
	    tacticsMenuItem.setText("Tactics");
	    tacticsMenuItem.addActionListener(new TProofPanel_tacticsMenuItem_actionAdapter(this));
	    absurdMenuItem.setText("Absurd I");
	    absurdMenuItem.addActionListener(new TProofPanel_absurdMenuItem_actionAdapter(this));
	    writeProofMenuItem.setText("Write To Journal");
	    writeProofMenuItem.addActionListener(new TProofPanel_writeProofMenuItem_actionAdapter(this));
	    writeConfirmationMenuItem.setText("Write Confirmation Code");
	    writeConfirmationMenuItem.addActionListener(new
	        TProofPanel_writeConfirmationMenuItem_actionAdapter(this));
	    marginMenuItem.setText("Set Margin");
	    marginMenuItem.addActionListener(new
	                                     TProofPanel_marginMenuItem_actionAdapter(this));
	    nextLineMenuItem.setText("Next Line");
	    nextLineMenuItem.addActionListener(new TProofPanel_nextLineMenuItem_actionAdapter(this));
	    deriveItMenuItem.setText("Derive It");
	    deriveItMenuItem.addActionListener(new TProofPanel_deriveItMenuItem_actionAdapter(this));
	    pruneMenuItem.setText("Prune");
	    pruneMenuItem.addActionListener(new TProofPanel_pruneMenuItem_actionAdapter(this));

	    startAgainMenuItem.setText("Start Again");
	    startAgainMenuItem.addActionListener(new TProofPanel_startAgainMenuItem_actionAdapter(this));

	    fMenuBar.add(fRulesMenu);
	    if ((TPreferences.fIdentity)||
	       fUseIdentity||
	       TPreferences.fRewriteRules||
	       TPreferences.fFirstOrder||
	       TPreferences.fSetTheory) {

	    fMenuBar.add(fAdvancedRules);
	    }

	    fMenuBar.add(fEditMenu);
	    fMenuBar.add(fWizardMenu);

	    this.add(jScrollPane1,     new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
	            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),0,0 ));

	    fMenuBar.setMinimumSize(new Dimension(120,20));  // we don't want the menubar squeezed away

	    this.add(fMenuBar,         new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
	            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,  new Insets(0, 0, 0, 0), 0,0));

	    jScrollPane1.getViewport().add(fProofListView, null);
	    fRulesMenu.add(tIMenuItem);
	    fRulesMenu.add(negIMenuItem);
	    fRulesMenu.add(negEMenuItem);
	    fRulesMenu.add(andIMenuItem);
	    fRulesMenu.add(andEMenuItem);
	    fRulesMenu.add(orIMenuItem);
	    fRulesMenu.add(orEMenuItem);
	    fRulesMenu.add(implicIMenuItem);
	    fRulesMenu.add(implicEMenuItem);
	    fRulesMenu.add(equivIMenuItem);
	    fRulesMenu.add(equivEMenuItem);
	    fRulesMenu.add(uGMenuItem);
	    fRulesMenu.add(uIMenuItem);
	    fRulesMenu.add(eGMenuItem);
	    fRulesMenu.add(eIMenuItem);
	    fRulesMenu.add(rAMenuItem);
	    if (TPreferences.fUseAbsurd)
	      fRulesMenu.add(absurdMenuItem);


	    assembleAdvancedMenu();

	    fEditMenu.add(fUndoAction);

	    fEditMenu.add(fRedoAction);

	    fEditMenu.addSeparator();

	    fEditMenu.add(cutLineMenuItem);
	    fEditMenu.add(pruneMenuItem);
	    fEditMenu.add(startAgainMenuItem);
	    fEditMenu.addSeparator();
	    fEditMenu.add(newGoalMenuItem);
	    fEditMenu.addSeparator();
	    fEditMenu.add(writeProofMenuItem);
	    fEditMenu.add(writeConfirmationMenuItem);
	    fEditMenu.addSeparator();
	    fEditMenu.add(marginMenuItem);

	    fWizardMenu.add(tacticsMenuItem);
	    if ((TPreferences.fDerive)&&
	    	!TPreferences.fSetTheory&&
	    	!TPreferences.fIdentity&&
	    	!TPreferences.fFirstOrder){
	      fWizardMenu.add(nextLineMenuItem);
	      fWizardMenu.add(deriveItMenuItem);
	    }

	    UndoableEditListener aListener= new UndoableEditListener(){
	                           public void undoableEditHappened(UndoableEditEvent e)
	                               {
	                                 //Remember the edit and update the menus.
	                               fUndoManager.addEdit(e.getEdit());
	                               fUndoAction.updateUndoState();
	                               fRedoAction.updateRedoState();

	                               fDeriverDocument.setDirty(true);
	                                }
	                            };

	    addUndoableEditListener(aListener);

	  } */
	 
	 
	 
	private void initializeButtons(){
		
		
		
		
		
	}
	
	
	
	
	
	
	  /************* Proofline factory ************/

	/* we want this to subclass for other types of proof eg Copi */

	public TProofline supplyProofline(){
	   return
	       new TProofline(fParser);
	}

	/************* End of Proofline factory ************/

	 /*******************  Factory *************************/

	 TGWTTestNode supplyTGWTTestNode (TParser aParser,TGWTTree aTreeModel){         // so we can subclass
	   return
	       new TGWTTestNode (aParser,aTreeModel);
	 }

	 /*******************  Factory *************************/

	 TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
	   return
	       new TTestNode (aParser,aTreeModel);
	 }


	/******************************************************/
	/******************************************************/	
	
	
	
public Widget[] getButtons(){
	Widget[] buttons={/*tIMenuItem,*/ /*theoremMenuItem,*//*negEMenuItem*/
			
	};
	return
			buttons;
}

public Widget[] getEditButtons(){
	Widget[] buttons={/*fUndoRedoButton*/};
	return
			buttons;
}








/*
 * 
 * 
 * class TUndoRedoButton extends Button {
public TUndoRedoButton() {
super("Undo");
setEnabled(false);

this.addClickHandler(new ClickHandler(){@Override 
	public void onClick(ClickEvent event) {
	try {
		fUndoManager.undo();
		} catch (Exception ex) {
		System.out.println("Unable to undo: " + ex);
		ex.printStackTrace();
		}
		//updateUndoState();
	toggle();
//TO DO		fRedoButton.updateRedoState();
		}});
}
 * 
 * 
 * 
 */









public MenuBar createMenuBar(){

/*
	// A command for general use
		Command command = new Command()
		{
		    public void execute()
		    {
		  ;//      Window.alert("Command Fired");
		    }
		};
		
/*		Command undoRedoCommand = new Command()
		{
		    public void execute()
		    {
		    	extendTree();
		    }
		}; 
		
		Command closeCommand = new Command()
		{
		    public void execute()
		    {
		    	closeBranch();
		    }
		};
		
		Command closedCommand = new Command()
		{
		    public void execute()
		    {
		    	executeIsClosed();
		    }
		};
		
		Command openBranchCommand = new Command()
		{
		    public void execute()
		    {
		    	executeIsOpenAndComplete();
		    }
		};
		
		Command startOverCommand = new Command()
		{
		    public void execute()
		    {
		    	startProof(getStartStr()); 
		    }
		};
	
	
	*/
	
	
	
	// Top-level menu
	
	fMenuBar.addStyleName("menu");
	
	fMenuBar.addItem("Rules",fRules); // Creates item and adds menutwo 
	
	
	
    if (    (TPreferencesData.fAdvancedMenu)&&
    		((TPreferencesData.fIdentity)||
    	       fUseIdentity||
    	       TPreferencesData.fRewriteRules||
    	       TPreferencesData.fFirstOrder||
    	       TPreferencesData.fSetTheory)) {	
    	
    	   fMenuBar.addItem("Advanced",fAdvancedRules);
    	    }
    
    //fAdvancedMenu defaults true, so if it is set false we don't want it
	
	fMenuBar.addItem("Edit+",fEdit); // Creates item and adds menutwo
	fMenuBar.addItem("Wizard",fWizard); // Creates item and adds menutwo
	
	
	
	
	
	fRules.addItem(tIMenuItem);
	fRules.addItem(negIMenuItem);
	fRules.addItem(negEMenuItem);
	fRules.addItem(andIMenuItem);
	fRules.addItem(andEMenuItem);
	fRules.addItem(orIMenuItem);
	fRules.addItem(orEMenuItem);
	fRules.addItem(implicIMenuItem);
	fRules.addItem(implicEMenuItem);
	fRules.addItem(equivIMenuItem);
	fRules.addItem(equivEMenuItem);
	fRules.addItem(uGMenuItem);
	fRules.addItem(uIMenuItem);
	fRules.addItem(eGMenuItem);
	fRules.addItem(eIMenuItem);
//	fRules.addItem(theoremMenuIte
//	fRules.addItem(theoremMenuItem);
	fRules.addItem(rAMenuItem);
	fRules.addItem(absurdIMenuItem);
	
	assembleAdvancedMenu();
	
//	fUndoRedo = new MenuItem("Undo", undoRedoCommand);	
	fEdit.addItem(fUndoRedo);
	fEdit.addSeparator();
	fEdit.addItem(fCutProofline);
	fEdit.addItem(fPrune);
	fEdit.addItem(fStartAgain);
	fEdit.addSeparator();
	fEdit.addItem(fNewGoal);
	
	fWizard.addItem(fTactics);
//	fWizard.addItem(fNextLine);
//	fWizard.addItem(fDeriveIt);
	
    if ((TPreferencesData.fDerive)&&
    	!TPreferencesData.fSetTheory&&
    	!TPreferencesData.fIdentity&&
    	!TPreferencesData.fFirstOrder){
    	fWizard.addItem(fNextLine);
    	fWizard.addItem(fDeriveIt);
    }
	
	
	/*
    fClose = new MenuItem("Close", closeCommand);	
	fRules.addItem(fClose);
	
	fRules.addSeparator();
	
    fIsClosed = new MenuItem("Closed?", closedCommand);	
	fRules.addItem(fIsClosed);
	
    fOpenBranch = new MenuItem("Complete Open Branch?", openBranchCommand);	
	fRules.addItem(fOpenBranch);
	
	fRules.addSeparator();
	
    fStartOver = new MenuItem("Start Over", startOverCommand);	
	fRules.addItem(fStartOver); */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	return
			fMenuBar;
}				
				

void assembleAdvancedMenu(){

if (TPreferencesData.fIdentity||
      fUseIdentity||
      TPreferencesData.fSetTheory||
      TPreferencesData.fFirstOrder){

	fAdvancedRules.addItem(iIMenuItem);
  fAdvancedRules.addItem(iEMenuItem);
  fAdvancedRules.addSeparator();
  fAdvancedRules.addItem(uniqueIMenuItem);
  fAdvancedRules.addItem(uniqueEMenuItem);
  fAdvancedRules.addSeparator();
}

if (TPreferencesData.fFirstOrder){
  fAdvancedRules.addItem(inductionMenuItem);
  fAdvancedRules.addSeparator();
}
if (TPreferencesData.fRewriteRules)
  fAdvancedRules.addItem(rewriteMenuItem);

fAdvancedRules.addItem(theoremMenuItem);

if (TPreferencesData.fSetTheory){
	 TSetTheory setSupport= new TSetTheory(this,fParser);
	 setSupport.augmentAdvancedMenu(fAdvancedRules);
}

}	
	
	
	

public void extendTree(){};	
public void closeBranch(){};
public void executeIsClosed(){};;
public void executeIsOpenAndComplete(){};
public void executeII(){};


public boolean usingInputPane(){
	  return
	      fInputPane!=null;
	}


/************************** Starting Proofs **********************/




boolean load(String inputStr){return false;}



public void startProof(String inputStr){    // a stub that needs to be overridden by subclass

}

void initProof(){
	fProofType = noPremNoConc;
	initializeProofModel();
	
}


void initializeProofModel(){
	
	
/*	  fRoot = new TGWTTestNode(fParser,null);            //does not initialize TreeModel
	  fDisplayTableModel.setHostRoot(fRoot);             // Table Model knows its root
	  
	  fGWTTree.removeItems();                            //get rid of earlier
	  fGWTTree.addItem(fRoot);                           //display knows its root
	  
	  
//	  fGWTTree= new TGWTTree(fRoot);
	  fRoot.fGWTTree=fGWTTree;                          //root knows its Tree */
	}


public void createBlankStart(){
    TProofline newline = supplyProofline();

    newline.fBlankline=true;
    newline.fFormula=null;
    newline.fSelectable=false;
    newline.fHeadlevel=-1;
    newline.fSubprooflevel=-1;

    fModel.addToHead(0,newline);

    fProofType=noPremNoConc;  //new Nov 03

  }




public void dismantleProof(){
	

    fModel.clear();	
    
    fDisplayCellTable.clearSelections();
	
	
	//TO DO		    

    //System.out.print("dismantle needs to commit undo command");

   if (fLastEdit!=null){ // kill the previous one, we don't want to undo a proof that is gone
      fLastEdit.die();
     // fUndoRedoButton.updateUndoState();
        updateUndoState(fUndoRedo);
// TO DO      fRedoButton.updateRedoState();
    }


    removeInputPanel();

  }


void doSetUpEditMenu(){

/* TO DO
    boolean cutOK = false;

    TProofline firstline = fDisplayCellTable.oneSelected();

    if ( (firstline != null) && fModel.lineCutable(firstline, null)) {
      cutOK = true;
    }

  cutLineMenuItem.setEnabled(cutOK);


  if (newGoalPossible())
    newGoalMenuItem.setEnabled(true);
  else
    newGoalMenuItem.setEnabled(false);


  int size= fModel.getProofSize();

  if ((size>1)||((size==1)&&(!fModel.getHeadLastLine().fBlankline))){

    writeProofMenuItem.setEnabled(true);

    pruneMenuItem.setEnabled(true);
   startAgainMenuItem.setEnabled(true);


  }
  else{
    writeProofMenuItem.setEnabled(false); // don't write singleton blank line

    pruneMenuItem.setEnabled(false);
    startAgainMenuItem.setEnabled(false);
  }

  if (fModel.getHeadSize()!=0&&fModel.getTailSize()!=0)
       newGoalMenuItem.setEnabled(true);
  else
    newGoalMenuItem.setEnabled(false);

if (fModel.finishedAndNoAutomation())
    writeConfirmationMenuItem.setEnabled(true);
else
    writeConfirmationMenuItem.setEnabled(false);  // don't write singleton blank line


*/

}






void removeBugAlert(){
	   removeInputPanel();
	}

	void removeInputPanel(){
	   /* if (fInputPane!=null){
	      fInputPane.setVisible(false);
	       this.remove(fInputPane);

	        fInputPane=null;*/

	   fInputPanel.clear(); //remove its contents, not it
	   enableMenus();
	  //  }
		
	  }










	void display(String aString){

		  if (fInputPane!=null)
		; //TO DO    ((TProofInputPanel)fInputPane).setText1(aString);
		  


		}













public String getStartStr(){return "";};

public Button cancelButton(){
	Button cancelButton=new Button("Cancel"); 
	cancelButton.addClickHandler(new ClickHandler(){
	    public void onClick(ClickEvent event)
	    {
	        fInputPanel.clear();
	        enableMenus();
	    }
	}
	);
	return
			cancelButton;
	
}



void enableMenus(){

	fProofClient.enableMenus();



	}

void disableMenus(){
	fProofClient.disableMenus();
	}


public void bugAlert(String label,String message){
	
	
	//TextBox aTextBox=new TextBox();
	Button okButton=new Button("OK"); 
	okButton.addClickHandler(new ClickHandler(){
	    public void onClick(ClickEvent event)
	    {
	        fInputPanel.clear();
	        enableMenus();
	    }
	}
	);		
			
	//aTextBox.setSize("400px", "18px");
	
	//aTextBox.setText(message);
	
	fInputPanel.clear();
	
	fInputPanel.setSpacing(10);
	
	fInputPanel.add(new Label(label));
	
	//fInputPanel.add(aTextBox);
	
	fInputPanel.add(new Label(message));
	
	//May 2012, using label instead of Text Box
	
	fInputPanel.add(okButton);
	
//	aTextBox.setSelectionRange(0, message.length());  //not working?
//	aTextBox.setFocus(true);
	
	disableMenus();
	
}




public Widget[] supplyModalRadioButtons(){
	
	Widget[] buttons={new Button("Hello")};
	return buttons;
}



/****************************** Rules ******************************************/

public void addInputPane(TGWTProofInputPanel inputPane, boolean selectText){
	
	fInputPanel.clear();

	 fInputPanel.add(inputPane);   // we want the focus on the text
	 
	 fGWTInputPanel=inputPane; //Feb 2013 remove
	 
	 if (selectText)
		 inputPane.selectAllInTextBox();


  disableMenus();

 }

TProofline endSubProof (int lastlevel){
	   TProofline newline = supplyProofline();

	  newline.fBlankline=true;
	  newline.fJustification= "";
	  newline.fSubprooflevel= lastlevel-1;
	  newline.fSelectable=false;

	return
	      newline;
	}

void toNewPseudoTail(){   //TUESDAY. WORKING IN THIS  Hey the model should do this?
	   int indexOfNextQuestionMark = fModel.nextQuestionMark();

	   if (indexOfNextQuestionMark>-1){
	     fModel.resetSplitBetweenLists(indexOfNextQuestionMark
	                                   /*-1 alterned July 04*/);
	   }
	   else{   // there isn't another question mark and the proof is finished
	     fTemplate=false;
	     fProofType=pfFinished;

	     fModel.resetSplitBetweenLists(fModel.getSize());  // put all lines in Head list

	   }
	 }




/************************ Rule of TI **********************************/



public class TIKeepLastHandler /*Action*/ implements ClickHandler /*extends AbstractAction*/{
  TextBox fText;

   public TIKeepLastHandler(TextBox text){
   //  putValue(NAME, label);

     fText=text;
   }

   public void onClick(ClickEvent event) {
   boolean useFilter =true;
   ArrayList dummy = new ArrayList();

   String filteredStr=TUtilities.defaultFilter(fText.getText());

   TFormula root = new TFormula();
   StringReader aReader = new StringReader(filteredStr);
   boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

   if (!wellformed){
       String message = "The string is illformed."+
                         (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

     //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

       fText.setText(message);
       fText.selectAll();
 //      fText.requestFocus();

        }
  else {
   TProofline newline = supplyProofline();

   newline.fFormula=root;
   newline.fJustification= fAssJustification;
   newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel+1;
   newline.fLastassumption=true;

   TUndoableProofEdit  newEdit = new TUndoableProofEdit();
   newEdit.fNewLines.add(newline);
   newEdit.doEdit();

   removeInputPanel();}
  }

}

public class TIDropLastHandler implements ClickHandler /*extends AbstractAction*/{
	TextBox fText;
  int fLastLevel;

   public TIDropLastHandler(TextBox text, int level){
 //    putValue(NAME, "Drop Last");

     fText=text;
     fLastLevel=level;
   }

   public void onClick(ClickEvent event) {
   boolean useFilter =true;
   ArrayList dummy = new ArrayList();

   String aString= TUtilities.defaultFilter(fText.getText());

   TFormula root = new TFormula();
   StringReader aReader = new StringReader(aString);
   boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

   if (!wellformed){
       String message = "The string is illformed."+
                         (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

     //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

       fText.setText(message);
       fText.selectAll();
 //TO DO      fText.requestFocus();

        }
  else {
   TProofline newline = supplyProofline();

   newline.fFormula=root;
   newline.fJustification= fAssJustification;
   newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;
   newline.fLastassumption=true;

   TUndoableProofEdit  newEdit = new TUndoableProofEdit();

   newEdit.fNewLines.add(endSubProof(fLastLevel));
   newEdit.fNewLines.add(newline);
   newEdit.doEdit();

   removeInputPanel();};
  }

}

public void doTI(){
    Button defaultButton;
    Button dropLastButton;
    Button cancelButton=cancelButton();
    TGWTProofInputPanel inputPanel;

     if (fModel.getHeadLastLine().fSubprooflevel > kMaxNesting) {
       bugAlert("Doing"+fAssJustification+". Warning.", "Phew... no more assumptions, please.");
     }
     else {

       if (fTemplate) {
         bugAlert("Doing"+fAssJustification+". Warning.", "Cancel! Ass. is not usual with Tactics on-- you will never be able to drop a new assumption.");
       }
       else {

       /*  JTextField text = new JTextField("New antecedent?");
         text.setDragEnabled(true);
         text.selectAll();  */
         
         TextBox textBox = new TextBox();
		 textBox.setText("New antecedent?");
		 textBox.selectAll();

       TProofline lastLine = fModel.getHeadLastLine();

       if (lastLine.fSubprooflevel>lastLine.fHeadlevel){

        // defaultButton = new JButton(new TIKeepLastAction(text,"Keep Last"));
    	   defaultButton = new Button("Keep Last");
    	   defaultButton.addClickHandler(new TIKeepLastHandler(textBox));
    	   
    	   dropLastButton = new Button("Drop  Last");
    	   dropLastButton.addClickHandler(new TIDropLastHandler(textBox,lastLine.fSubprooflevel));
   		
        // dropLastButton = new JButton(new TIDropLastAction(text,lastLine.fSubprooflevel));

         Button[]buttons = {
             cancelButton, dropLastButton,
             defaultButton };  // put cancel on left
         inputPanel = new TGWTProofInputPanel(fTIInput,
            textBox, buttons,fInputPalette);
       }
      else{
       // defaultButton = new JButton(new TIKeepLastAction(text,"Go"));
        defaultButton = new Button("Go");
 	   defaultButton.addClickHandler(new TIKeepLastHandler(textBox));

       Button[]buttons = {
        cancelButton,

         defaultButton };  // put cancel on left
       inputPanel = new TGWTProofInputPanel(fTIInput,
          textBox, buttons,fInputPalette);

      }

         addInputPane(inputPanel,SELECT);

//TO DO         fInputPane.setVisible(true); // need this
 //TO DO        text.requestFocus();         // so selected text shows
       }
     }
   }


/************************ Strategy of New Goal **********************************/



public class GoalActionHandler implements ClickHandler /*extends AbstractAction*/{
	TextBox fText;
 // int fLastLevel;
  boolean fAfterLast;

   public GoalActionHandler(TextBox text, String label, boolean afterLast){
 //    putValue(NAME, "Drop Last");

     fText=text;
     fAfterLast=afterLast;

   //  putValue(NAME, label);
   }

   public void onClick(ClickEvent event) {
   boolean useFilter =true;
   ArrayList dummy = new ArrayList();

   String aString= TUtilities.defaultFilter(fText.getText());

   TFormula root = new TFormula();
   StringReader aReader = new StringReader(aString);
   boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

   if (!wellformed){
       String message = "The string is illformed."+
                         (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

     //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

       fText.setText(message);
       fText.selectAll();
 //TO DO      fText.requestFocus();

        }
  else {
	  
	/********** new **************/  
	  
	  int level= fModel.getHeadLastLine().fSubprooflevel;

	   if (!fAfterLast)
	     level= fModel.getTailLine(0).fSubprooflevel;   // before the tail line (after subproof)

	   TFormula formulanode= new TFormula();

	   formulanode.fInfo = "?";
	   formulanode.fKind = TFormula.predicator;


	   TProofline newline = supplyProofline();

	   newline.fFormula=formulanode;
	   newline.fJustification= questionJustification;
	   newline.fSubprooflevel= level;
	   newline.fSelectable= false;

	   TUndoableProofEdit  newEdit = new TUndoableProofEdit();
	   newEdit.fNewLines.add(newline);

	   newline = supplyProofline();

	   newline.fFormula=root;
	   newline.fJustification= questionJustification;
	   newline.fSubprooflevel= level;

	   newEdit.fNewLines.add(newline);

	   newEdit.doEdit();

	   removeInputPanel();
	   };  

}

}



boolean newGoalPossible(){
if (fModel.getHead()!=null&&
   fModel.getHead().size()>0&&
   fModel.getTail()!=null&&
   fModel.getTail().size()>0)
  return
      true;
else
 return
     false;
}


void doNewGoal(){

/*This is similar to TI */

if (newGoalPossible()){


Button defaultButton;
Button dropLastButton;
Button cancelButton=cancelButton();
TGWTProofInputPanel inputPane;
boolean afterLast = true;

/*JTextField text = new JTextField(
   "New Goal? Then click on question mark to work on a different sub-problem.");
text.setDragEnabled(true);
text.selectAll();*/

TextBox textBox = new TextBox();
textBox.setText("New Goal? Then click on question mark to work on a different sub-problem.");
textBox.selectAll();

TProofline lastLine = fModel.getHeadLastLine();
TProofline tailFirstLine = fModel.getTailLine(0);

if ( (tailFirstLine != null) &&
   lastLine.fSubprooflevel > tailFirstLine.fSubprooflevel) {

 /*If the insertion point is at the end of a subproof, the new goal can either go in
         continuing the subproof, or continuing outside it.*/

 //defaultButton = new JButton(new NewGoalAction(text, "After Last", afterLast));
defaultButton = new Button ("After Last");
defaultButton.addClickHandler(new GoalActionHandler(textBox, "After Last",
	     afterLast));
dropLastButton = new Button ("After Last");
dropLastButton.addClickHandler(new GoalActionHandler(textBox, "Before Next",
	     !afterLast));



 Button[] buttons = {
    cancelButton, dropLastButton,
     defaultButton}; // put cancel on left
 inputPane = new TGWTProofInputPanel("New Goal",
                                  textBox, buttons,fInputPalette);
}
else {
 //defaultButton = new JButton(new NewGoalAction(text, "Go", afterLast));
 defaultButton = new Button ("Go");
 defaultButton.addClickHandler(new GoalActionHandler(textBox, "Go",
 	     afterLast));

 Button[] buttons = {
     cancelButton,

     defaultButton}; // put cancel on left
 inputPane = new TGWTProofInputPanel("New Goal",
                                  textBox, buttons,fInputPalette);

}

addInputPane(inputPane,SELECT);

//fInputPane.setVisible(true); // need this
//text.requestFocus(); // so selected text shows
}

}




/************************ Rule of Theorem **********************************/

public class TheoremHandler /*Action*/ implements ClickHandler /*extends AbstractAction*/{
	  TextBox fText;
	  TFormula fRoot=null;


	   public TheoremHandler(TextBox text){
	   //  putValue(NAME, label);

	     fText=text;
	   }

	   public void onClick(ClickEvent event) {
		   if (fRoot==null){
	   boolean useFilter =true;
	   ArrayList dummy = new ArrayList();

	   String filteredStr=TUtilities.defaultFilter(fText.getText());

	   TFormula root = new TFormula();
	   StringReader aReader = new StringReader(filteredStr);
	   boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

	   if (!wellformed){
	       String message = "The string is illformed."+
	                         (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns

	     //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

	       fText.setText(message);
	       fText.selectAll();
	 //      fText.requestFocus();

	        }
	   else {
           fRoot=root;      // found root move to second stage

           String message = "Brief annotation? eg. Theorem 1";
           fText.setText(message);
           fText.selectAll();
       //    fText.requestFocus();
         }
       }
	  else {
		  String justification=fText.getText();

          if (justification.equals("Brief annotation? eg. Theorem 1"))
            justification="Theorem";   // correcting thoughtless input
	   
          TProofline newline = supplyProofline();

	   newline.fFormula=fRoot;
	   newline.fJustification= justification;
	   newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;

	   TUndoableProofEdit  newEdit = new TUndoableProofEdit();
	   newEdit.fNewLines.add(newline);
	   newEdit.doEdit();

	   removeInputPanel();}
	  }

	}

public void doTheorem(){
	Button defaultButton;
	Button cancelButton=cancelButton();
	TGWTProofInputPanel inputPanel;


//JTextField text = new JTextField("Theorem?");

TextBox text = new TextBox();
text.setText("Theorem?");
text.selectAll();

 //  text.setDragEnabled(true);
 //  text.selectAll();

  // defaultButton = new JButton(new TheoremAction(text,"Go"));
defaultButton = new Button("Go");
defaultButton.addClickHandler(new TheoremHandler(text));

   Button[]buttons = {cancelButton, defaultButton };  // put cancel on left
   inputPanel = new TGWTProofInputPanel("Doing Theorem", text, buttons);


        addInputPane(inputPanel,SELECT);

 //       inputPane.getRootPane().setDefaultButton(defaultButton);
 //       fInputPane.setVisible(true); // need this
 //       text.requestFocus();         // so selected text shows
}

/************************ Rule of doInduction **********************************/



public class InductionHandler implements ClickHandler{
  TextBox fText;
  TFormula fRoot=null;

  TFormula nFormula = new TFormula(TFormula.variable,"x",null,null);

 /*We have here to get  the root of new formula, whicch must contain free x  */


   public InductionHandler(TextBox text){
//     putValue(NAME, label);

     fText=text;
   }

    public void onClick(ClickEvent event){


    if (fRoot==null){
      boolean useFilter = true;
      ArrayList dummy = new ArrayList();

      String aString = TUtilities.defaultFilter(fText.getText());

      TFormula root = new TFormula();
      StringReader aReader = new StringReader(aString);
      boolean wellformed;

      wellformed = fParser.wffCheck(root, /*dummy,*/ aReader);

      if (!wellformed) {
        String message = "The string is illformed." +
            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

        fText.setText(message);
        fText.selectAll();
   //     fText.requestFocus();
      }
      else {
             // found root move to, checking that it contains n

        if (root.numOfFreeOccurrences(nFormula)==0){
          String message = "The inductive formula must contain x free.";
          fText.setText(message);
          fText.selectAll();
//          fText.requestFocus();
        }
        else{            // good to go
          fRoot=root;


          TProofline newline = supplyProofline();

           newline.fFormula = makeInductionFormula(fRoot,nFormula);
           newline.fJustification = inductionJustification;
            newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();
            newEdit.fNewLines.add(newline);
            newEdit.doEdit();

        removeInputPanel();

        }
      }
    }

    else {               //we have a root, getting justification

      String justification=fText.getText();

      if (justification.equals("Brief annotation? eg. Theorem 1"))
        justification="Theorem";   // correcting thoughtless input


      }

  }

  /// NEED TO USE CONSTANTS HERE

  TFormula makeInductionFormula(TFormula uniForm, TFormula inductVar){
    TFormula baseForm=uniForm.copyFormula();                   // uniform is P(n)
    TFormula zeroForm=new TFormula(TFormula.functor,"0",null,null);

    baseForm.subTermVar(baseForm,zeroForm,inductVar);       // P(0)


    TFormula succTerm=new TFormula(TFormula.functor,"'",null,null);

    succTerm.appendToFormulaList(inductVar.copyFormula());     //n'

    TFormula succForm=uniForm.copyFormula();

    succForm.subTermVar(succForm,succTerm,inductVar);       // P(n')

    TFormula hookForm = new TFormula(TFormula.binary,          // P(n)->P(n')
                                     String.valueOf(chImplic),
                                     uniForm.copyFormula(),
                                     succForm);

    TFormula quantForm = new TFormula(TFormula.quantifier,     // Alln(P(n)->P(n'))
                                     String.valueOf(chUniquant),
                                     inductVar.copyFormula(),
                                     hookForm);

    TFormula anotherQuantForm = new TFormula(TFormula.quantifier,     // Alln(P(n))
                                    String.valueOf(chUniquant),
                                    inductVar.copyFormula(),
                                    uniForm);

    TFormula andForm = new TFormula(TFormula.binary,          // P(0)^Alln(P(n)->P(n'))
                           String.valueOf(chAnd),
                           baseForm.copyFormula(),
                           quantForm);



    return
        new TFormula(TFormula.binary,          // {P(0)^Alln(P(n)->P(n'))] -> Alln(P(n))
                                   String.valueOf(chImplic),
                                   andForm,
                                   anotherQuantForm);
  }

}


public void doInduction(){
	 Button defaultButton;
	 TGWTProofInputPanel inputPane;


	// JTextField text = new JTextField("Enter inductive formula A(x) containing the term x.");

	//    text.setDragEnabled(true);
	//    text.selectAll();
	    
        TextBox text = new TextBox();
        text.setText("Enter inductive formula A(x) containing the term x.");
        text.selectAll();

	    //defaultButton = new JButton(new InductionAction(text,"Go"));
	    
	    defaultButton = new Button("Go");
        defaultButton.addClickHandler(new InductionHandler(text));

	    Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	    inputPane = new TGWTProofInputPanel("Induction", text, buttons,fInputPalette);


	    addInputPane(inputPane,SELECT);

	  //       inputPane.getRootPane().setDefaultButton(defaultButton);
	  //       fInputPane.setVisible(true); // need this
	  //       text.requestFocus();         // so selected text shows
	}





public void addUndoableEditListener(UndoableEditListener listener){
    //  fListener =listener; June 06

      fListeners.add(listener);
    }


  public void tellListeners(UndoableEditEvent e){

 //   for (int i=0;i<fListeners.size();i++)
//      ((UndoableEditListener)fListeners.get(i)).undoableEditHappened(e);

    Iterator iter = fListeners.iterator();

        while (iter.hasNext()){
          ((UndoableEditListener)iter.next()).undoableEditHappened(e);

        }


  }
  
  /********************** Rule of NegI ****************************/

  void doHintNegI(){

    if (!TPreferencesData.fUseAbsurd)
      doHintNegINoAbs();
    else{


      /* Two cases:- if the target is ~C we add C; if the target is C we add ~C.
           Gentzen does not permit the second case. But we will disable the menu
       in with Genzten rules. Then the subclasses can use it */




 // TFormula newFormula=null;
      TProofline newline = supplyProofline();
      TFormula conclusion = findNextConclusion();

      TFormula negroot;

      if (fParser.isNegation(conclusion))
        negroot = conclusion.fRLink; //~C to C
      else
        negroot = new TFormula(TFormula.unary, //C to ~C
                               String.valueOf(chNeg),
                               null,
                               conclusion);

      //negroot = conclusion.fRLink;

      TUndoableProofEdit newEdit = new TUndoableProofEdit();

      TProofline headLastLine = fModel.getHeadLastLine();

      int oldeHeadLineno = headLastLine.fLineno;
      int level = headLastLine.fSubprooflevel;

      newline.fFormula = negroot.copyFormula();
      newline.fJustification = fAssJustification;
      newline.fSubprooflevel = level + 1;
      newline.fLastassumption = true;

      newEdit.fNewLines.add(newline);

      newline = supplyProofline();

      int absurdLineno = addIfNotThere(TFormula.fAbsurd, level + 1,
                                       newEdit.fNewLines);

      if (absurdLineno == -1) // not there

        absurdLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it

      newEdit.fNewLines.add(endSubProof(level + 1));
      newEdit.fNewLines.add(addNegAssumption(negroot, level, absurdLineno, 0));

      newEdit.doEdit();
    }


 }


 void doHintNegINoAbs(){

 Button defaultButton;
 Button dropLastButton;
 TGWTProofInputPanel inputPane;

    TFormula conclusion = findNextConclusion();

  TFormula negroot;
  String justification;

  if (fParser.isNegation(conclusion)) {
    negroot = conclusion.fRLink; // C to ~C
    justification = fNegIJustification;
  }
  else {
    negroot = new TFormula(TFormula.unary, //~C to C
                           String.valueOf(chNeg),
                           null,
                           conclusion);
    justification = fNegEJustification; // need to fix this
  }





// JTextField text = new JTextField("Positive Horn? Hint, one of: "+posForksAsString());

 //   text.setDragEnabled(true);
 //   text.selectAll();
    
    TextBox text = new TextBox();
    text.setText("Positive Horn? Hint, one of: "+posForksAsString());
    text.selectAll();

 //   defaultButton = new JButton(new HintReductioNoAbs(text,"Go",negroot,conclusion,justification));
    
    defaultButton = new Button("Go");
    defaultButton.addClickHandler(new HintReductioNoAbsHandler(text,negroot,conclusion,justification));


    Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
    inputPane = new TGWTProofInputPanel("Doing Reductio", text, buttons);


         addInputPane(inputPane,SELECT);

       //TO DO        inputPane.getRootPane().setDefaultButton(defaultButton);
       //TO DO         fInputPane.setVisible(true); // need this
       //TO DO       text.requestFocus();         // so selected text shows


  }


  public boolean negIPossible(TProofline lastAssumption,
                       boolean oneSelected,
                       boolean twoSelected,
                       TFormula selectedFormula,
                       TFormula secondSelectedFormula,
                       int totalSelected){
    if ((lastAssumption!=null)&&
    (lastAssumption.fFormula!=null)   // not needed, blankstart
    &&((oneSelected
        &&totalSelected==1
        &&fParser.isContradiction(selectedFormula))
      ||(twoSelected
         &&totalSelected==2
         &&
         (TFormula.formulasContradict(selectedFormula,secondSelectedFormula)) ) ))
   return
       true;
 else
   return
       false;
  }

  void doNegI(){
    if (fTemplate){
      if (TPreferencesData.fUseAbsurd)
        doHintNegI();
      else{
        doHintNegINoAbs();
      }


    }
     else{
       TProofline lastAssumption=fModel.findLastAssumption();

       if (lastAssumption!=null){                           // if we haven't got a last assumption we cannot drop it
          TProofline firstLine=fDisplayCellTable.oneSelected();

          if (firstLine!=null)
            introduceFromContradiction(lastAssumption,firstLine);
          else{
             TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

             if (selections != null)
                introduceFromContradictoryLines(lastAssumption,selections);
       }

       }
     }
  }

 void introduceFromContradictoryLines(TProofline lastAssumption,TProofline[] selections){
    if (TFormula.formulasContradict(selections[0].fFormula,selections[1].fFormula)){
       int level=fModel.getHeadLastLine().fSubprooflevel;
       TUndoableProofEdit  newEdit = new TUndoableProofEdit();

       newEdit.fNewLines.add(endSubProof(level));
       newEdit.fNewLines.add(addNegAssumption(lastAssumption.fFormula, level-1, selections[0].fLineno, selections[1].fLineno));
       newEdit.doEdit();
       }
    }


  void introduceFromContradiction(TProofline lastAssumption, TProofline firstLine){

    if ((lastAssumption!=null)&&
        fParser.isContradiction(firstLine.fFormula)) {
     int level=fModel.getHeadLastLine().fSubprooflevel;
     TUndoableProofEdit  newEdit = new TUndoableProofEdit();

     newEdit.fNewLines.add(endSubProof(level));
     newEdit.fNewLines.add(addNegAssumption(lastAssumption.fFormula, level-1, firstLine.fLineno, 0));
     newEdit.doEdit();
     }
  }


  TProofline addNegAssumption(TFormula whichone, int level, int posHorn, int negHorn){
    TProofline newline=supplyProofline();
    TFormula formulanode = new TFormula();

    formulanode.fKind = TFormula.unary;
    formulanode.fInfo = String.valueOf(chNeg);
    formulanode.fRLink = whichone.copyFormula();

    newline.fSubprooflevel = level;
    newline.fFormula = formulanode;
    newline.fFirstjustno = posHorn;
    newline.fSecondjustno = negHorn;
    newline.fJustification = fNegIJustification;

    return
        newline;
  }

  public class HintReductioNoAbsHandler implements ClickHandler{
	  TextBox fText;
      TFormula fAssumption;
      TFormula fTarget;
      String fJustification;




       public HintReductioNoAbsHandler(TextBox text,
                          //  String label,
                            TFormula assumption,
                            TFormula target,
                            String justification){
//         putValue(NAME, label);

         fText=text;
         fAssumption=assumption;
         fTarget=target;
         fJustification=justification;
       }

        public void onClick(ClickEvent event){



          boolean useFilter = true;
          ArrayList dummy = new ArrayList();

          String aString= TUtilities.defaultFilter(fText.getText());

          TFormula root = new TFormula();
          StringReader aReader = new StringReader(aString);
          boolean wellformed;

          wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

          if (!wellformed) {
            String message = "The string is illformed." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

            fText.setText(message);
            fText.selectAll();
 //           fText.requestFocus();
          }
          else {  // we have now got a Positive Horn

            /* Two cases:- if the target is ~C we add C; if the target is C we add ~C.
             Gentzen does not permit the second case. But we will disable the menu
             in with Genzten rules. Then the subclasses can use it */


            TProofline newline = supplyProofline();
   /*         TFormula conclusion = findNextConclusion();

            TFormula negroot;
            String justification;

            if (fParser.isNegation(conclusion)) {
              negroot = conclusion.fRLink; // C to ~C
              justification = fNegIJustification;
            }
            else {
              negroot = new TFormula(TFormula.unary, //~C to C
                                     String.valueOf(chNeg),
                                     null,
                                     conclusion);
              justification = fNegEJustification; // need to fix this
            }  */

            TUndoableProofEdit newEdit = new TUndoableProofEdit();

            TProofline headLastLine = fModel.getHeadLastLine();
            int currentHeadLineno = headLastLine.fLineno;
            int level = headLastLine.fSubprooflevel;

            newline.fLineno = currentHeadLineno;
            newline.fFormula = fAssumption.copyFormula(); //start subproof
            newline.fJustification = fAssJustification;
            newline.fSubprooflevel = level + 1;
            newline.fLastassumption = true;

            newEdit.fNewLines.add(newline);
            currentHeadLineno += 1;

            int posHornLineno = addIfNotThere(root, level+1,
                newEdit.fNewLines);

            if (posHornLineno == -1) {
              posHornLineno = currentHeadLineno + 2;
              currentHeadLineno += 2;
            }

            TFormula negHorn = new TFormula(TFormula.unary,
                String.valueOf(chNeg), null, root);

            int negHornLineno = addIfNotThere(negHorn, level+1,
                newEdit.fNewLines);

            if (negHornLineno == -1) {
              negHornLineno = currentHeadLineno + 2;
              currentHeadLineno += 2;
            }

            newEdit.fNewLines.add(endSubProof(level + 1));

            newline = supplyProofline();

            newline.fFormula = fTarget.copyFormula();
            newline.fFirstjustno = posHornLineno;
            newline.fSecondjustno = negHornLineno;
            newline.fJustification = fJustification;
            newline.fSubprooflevel = level;

            newEdit.fNewLines.add(newline);

            newEdit.doEdit();

            removeInputPanel();
          }



      }

}
  
  

/************************* Rule of NegE *********************************/
  
  
  void doNegE(){
	  TProofline firstLine = fDisplayCellTable.oneSelected();

	  if ( (firstLine != null) && fParser.isDoubleNegation(firstLine.fFormula)) {
	    TProofline newline = supplyProofline();

	    newline.fFormula = (firstLine.fFormula.fRLink.fRLink).copyFormula();
	    newline.fFirstjustno = firstLine.fLineno;
	    newline.fJustification = negEJustification;
	    newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

	    TUndoableProofEdit newEdit = new TUndoableProofEdit();
	    newEdit.fNewLines.add(newline);
	    newEdit.doEdit();
	    return;
	  }
	  if (fTemplate) {

	    TFormula conclusion = findNextConclusion();

	    boolean noneSelected = (fDisplayCellTable.exactlyNLinesSelected(0)) != null;
	    if ( (conclusion != null) && (noneSelected)) // we are going to allow ~E as a tactic
	     ;// doHintNegE();   TODO
	  }
	}  
  
  /************************* Rule of AndI  AndE *********************************/
  
  void doAndI(){
	   if (fTemplate)
	     doHintAndI();
	   else {

	     TProofline firstLine = fDisplayCellTable.oneSelected();

	     if (firstLine != null) {
	       oneSelectionAnd(firstLine);
	       return;
	     }

	     //Trying two selection And

	     TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

	     if (selections != null)
	        {
	          Button leftButton;
	          Button rightButton;
	          TGWTProofInputPanel inputPane;

	          TextBox text = new TextBox();
	          text.setText("Choose where you wish the first selected line to appear.");
	          text.selectAll();

	          TProofline lastLine = fModel.getHeadLastLine();

	          /*  if (lastLine.fSubprooflevel>lastLine.fHeadlevel)*/
	          {

	/*            leftButton = new Button(new AndIOnLeftHandler(text, "On Left",
	                selections));*/
	        	  leftButton = new Button("On Left");
	        	  leftButton.addClickHandler(new AndIOnLeftHandler(text,selections));
	        	  rightButton = new Button("On Right");
	        	  rightButton.addClickHandler(new AndIOnRightHandler(text,selections));
	      /*      rightButton = new Button(new AndIOnRightAction(text, "On Right",
	                selections)); */

	            Button[] buttons = {
	                cancelButton(), leftButton,
	                rightButton}; // put cancel on left
	            inputPane = new TGWTProofInputPanel("Doing"+ fAndIJustification,
	                                             text, buttons);
	          }

	          addInputPane(inputPane,SELECT);

	        //  inputPane.getRootPane().setDefaultButton(defaultButton);
	        //TO DO	          fInputPane.setVisible(true); // need this
	        //TO DO	          text.requestFocus(); // so selected text shows
	        }
	   }
	 }
  
  void oneSelectionAnd(TProofline firstLine){

	     TProofline newline = supplyProofline();

	     TFormula formulanode = new TFormula();

	     formulanode.fKind = TFormula.binary;
	     formulanode.fInfo = String.valueOf(chAnd);
	     formulanode.fLLink = firstLine.fFormula.copyFormula();
	     formulanode.fRLink = firstLine.fFormula.copyFormula();


	     newline.fFormula = formulanode;
	     newline.fFirstjustno = firstLine.fLineno;
	     newline.fSecondjustno = firstLine.fLineno;
	     newline.fJustification = fAndIJustification;//" ^I";
	     newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

	     TUndoableProofEdit newEdit = new TUndoableProofEdit();
	     newEdit.fNewLines.add(newline);
	     newEdit.doEdit();

	}
  
  void doHintAndI(){
	  TFormula newFormula=null;
	  TProofline newline=null;
	  TFormula conclusion =findNextConclusion();

	  if ((conclusion==null)||!fParser.isAnd(conclusion))
	    bugAlert("Doing"+fAndIJustification+". Warning.",
	             "With the tactic for "+chAnd+ "the conclusion must be a conjunction.");
	  // do not need this as menu is disabled if conditions not satisfied
	  else{

	    TFormula leftconj = conclusion.fLLink;
	    TFormula rightconj = conclusion.fRLink;

	    TUndoableProofEdit newEdit = new TUndoableProofEdit();

	    TProofline headLastLine=fModel.getHeadLastLine();

	    int level = headLastLine.fSubprooflevel;
	    int lastlineno = headLastLine.fLineno;

	    int leftconjlineno=addIfNotThere(leftconj, level, newEdit.fNewLines);

	    if (leftconjlineno==-1){   // not there
	      leftconjlineno = lastlineno+2;
	      lastlineno += 2;
	    }

	    int rightconjlineno=addIfNotThere(rightconj, level, newEdit.fNewLines);

	    if (rightconjlineno==-1){   // not there
	      rightconjlineno = lastlineno+2;
	      lastlineno += 2;
	    }


	            newline = supplyProofline();

	            newline.fFormula = conclusion.copyFormula();
	            newline.fFirstjustno=leftconjlineno;
	            newline.fSecondjustno=rightconjlineno;

	            newline.fJustification = fAndIJustification;
	            newline.fSubprooflevel = level;

	            newEdit.fNewLines.add(newline);

	          newEdit.doEdit();

	  }

	}
  
  public class AndIOnLeftHandler implements ClickHandler{
      TextBox fText;
      TProofline[] fSelections;

       public AndIOnLeftHandler(TextBox text, TProofline[] selections){
 //        putValue(NAME, label);

         fText=text;

         fSelections=selections;
       }

        public void onClick(ClickEvent event){

          TProofline newline = supplyProofline();

  TFormula formulanode = new TFormula();

  formulanode.fKind = TFormula.binary;
  formulanode.fInfo = String.valueOf(chAnd);
  formulanode.fLLink = fSelections[0].fFormula.copyFormula();
  formulanode.fRLink = fSelections[1].fFormula.copyFormula();


  newline.fFormula = formulanode;
  newline.fFirstjustno = fSelections[0].fLineno;
  newline.fSecondjustno = fSelections[1].fLineno;
  newline.fJustification = fAndIJustification;
  newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

  TUndoableProofEdit newEdit = new TUndoableProofEdit();
  newEdit.fNewLines.add(newline);
  newEdit.doEdit();

removeInputPanel();

      }

    }

public class AndIOnRightHandler implements ClickHandler{
          TextBox fText;
          TProofline[] fSelections;

           public AndIOnRightHandler(TextBox text,TProofline[] selections){
           //  putValue(NAME, label);

             fText=text;

             fSelections=selections;
           }

            public void onClick(ClickEvent event){

              TProofline newline = supplyProofline();

      TFormula formulanode = new TFormula();

      formulanode.fKind = TFormula.binary;
      formulanode.fInfo = String.valueOf(chAnd);
      formulanode.fLLink = fSelections[1].fFormula.copyFormula();
      formulanode.fRLink = fSelections[0].fFormula.copyFormula();


      newline.fFormula = formulanode;
      newline.fFirstjustno = fSelections[1].fLineno;
      newline.fSecondjustno = fSelections[0].fLineno;
      newline.fJustification = fAndIJustification;
      newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

      TUndoableProofEdit newEdit = new TUndoableProofEdit();
      newEdit.fNewLines.add(newline);
      newEdit.doEdit();

   removeInputPanel();

            }
}
public class AndEHandler implements ClickHandler{
  TextBox fText;
  boolean fLeft;
  TProofline fSelection;

   public AndEHandler(TextBox text, TProofline proofline, boolean onLeft){
//     putValue(NAME, label);

     fText=text;

     fLeft=onLeft;
     fSelection=proofline;

   }

public void onClick(ClickEvent event){

  TProofline newline = supplyProofline();


  if (fLeft)
    newline.fFormula = fSelection.fFormula.fLLink.copyFormula();
   else
   newline.fFormula = fSelection.fFormula.fRLink.copyFormula();

  newline.fFirstjustno = fSelection.fLineno;

newline.fJustification = fAndEJustification;
newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

TUndoableProofEdit newEdit = new TUndoableProofEdit();
newEdit.fNewLines.add(newline);
newEdit.doEdit();

removeInputPanel();


  }

}


void doAndE(){
	   TProofline firstLine=fDisplayCellTable.oneSelected();

	   if ((firstLine!=null)&&fParser.isAnd(firstLine.fFormula)){

	     {
	          Button leftButton;
	          Button rightButton;
	          TGWTProofInputPanel inputPane;

	          TextBox text = new TextBox();
	          text.setText("Choose which conjunct you would like.");
	          text.selectAll();


	          {

	            boolean left=true;
	            
	            leftButton = new Button("Left");
	        	leftButton.addClickHandler(new AndEHandler(text,firstLine,left));
	        	 rightButton = new Button("Right");
		        	rightButton.addClickHandler(new AndEHandler(text,firstLine,!left));  



	            Button[] buttons = {
	                cancelButton(), leftButton,
	                rightButton}; // put cancel on left
	            inputPane = new TGWTProofInputPanel("Doing " + fAndEJustification,
	                                             text, buttons);
	          }

	          addInputPane(inputPane, SELECT);

	       //   inputPane.getRootPane().setDefaultButton(defaultButton);
	          //TO DO	          fInputPane.setVisible(true); // need this
	          //TO DO	          text.requestFocus(); // so selected text shows
	        }



	   }


	 }


/******************************* Rule of AbsI **********************************/

/***************************************/

void doHintAbsI(){
Button defaultButton;
Button dropLastButton;
TGWTProofInputPanel inputPane;


//JTextField text = new JTextField("Positive Conjunct? Hint, one of: "+posForksAsString());

//   text.setDragEnabled(true);
//   text.selectAll();
   
     TextBox text = new TextBox();
	 text.setText("Positive Conjunct? Hint, one of: "+posForksAsString());
	 text.selectAll();

//   defaultButton = new JButton(new DoHintAbsI(text,"Go"));
   
   defaultButton = new Button("Go");
   defaultButton.addClickHandler(new DoHintAbsIHandler(text));
   

   Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
   inputPane = new TGWTProofInputPanel("Doing Absurd I", text, buttons);


        addInputPane(inputPane,SELECT);

      //TO DO        inputPane.getRootPane().setDefaultButton(defaultButton);
      //TO DO       fInputPane.setVisible(true); // need this
      //TO DO       text.requestFocus();         // so selected text shows
}

/************************ Rule of DoHintAbsI **********************************/



public class DoHintAbsIHandler implements ClickHandler{
	TextBox fText;




   public DoHintAbsIHandler(TextBox text){
//     putValue(NAME, label);

     fText=text;
   }

   public void onClick(ClickEvent event){



      boolean useFilter = true;
      ArrayList dummy = new ArrayList();

      String aString = TUtilities.defaultFilter(fText.getText());

      TFormula root = new TFormula();
      StringReader aReader = new StringReader(aString);
      boolean wellformed;

      wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

      if (!wellformed) {
        String message = "The string is illformed." +
            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

        fText.setText(message);
        fText.selectAll();
      //To DO        fText.requestFocus();
      }
      else {

         TUndoableProofEdit newEdit = new TUndoableProofEdit();

         TProofline headLastLine=fModel.getHeadLastLine();
         int currentHeadLineno = headLastLine.fLineno;
         int level = headLastLine.fSubprooflevel;

        // TProofline newline = supplyProofline();

         int posHornLineno=addIfNotThere(root, level, newEdit.fNewLines);

         if (posHornLineno==-1){
           posHornLineno = currentHeadLineno + 2;
           currentHeadLineno += 2;
         }

         TFormula negroot= new TFormula();

               negroot.fKind = TFormula.unary;
               negroot.fInfo = String.valueOf(chNeg);
               negroot.fRLink = root;

               int negHornLineno=addIfNotThere(negroot, level, newEdit.fNewLines);

               if (negHornLineno==-1){
                 negHornLineno = currentHeadLineno + 2;
                 currentHeadLineno += 2;
               }

               TProofline newline = supplyProofline();

             newline.fFormula = TFormula.fAbsurd.copyFormula();
             newline.fFirstjustno = posHornLineno;
             newline.fSecondjustno = negHornLineno;
             newline.fJustification = absIJustification;
             newline.fSubprooflevel = level;


        newEdit.fNewLines.add(newline);
        newEdit.doEdit();

        removeInputPanel();
      }


  }

}



void doAbsI(){

    if (fTemplate)
      doHintAbsI();
    else {

      TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

      if (selections != null) {

        TProofline firstline = selections[0];
        TProofline secondline = selections[1];

        if (TFormula.formulasContradict(firstline.fFormula,secondline.fFormula)) {

            TProofline newline = supplyProofline();

            newline.fFormula = TFormula.fAbsurd.copyFormula();
            newline.fFirstjustno = firstline.fLineno;
            newline.fSecondjustno = secondline.fLineno;
            newline.fJustification = absIJustification;
            newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();

            newEdit.fNewLines.add(newline);

            newEdit.doEdit();

          }

        }

    }
  }


String posForksAsString(){

	   ArrayList allNegations=fModel.listNegationSubFormulasInProof();

	   String outStr="";

	   Iterator iter = allNegations.iterator();


	   while (iter.hasNext()) {
	     TFormula aFormula = (TFormula) iter.next();

	     outStr= outStr+fParser.writeFormulaToString(aFormula.fRLink)+ chBlank;  //removing the negation
	   }
	return
	       outStr;
	 }

/***************************************/



/*************************** Rule of ImplicI **********************************/



void doHintImplicI(){
 TFormula newFormula=null;
 TProofline newline= supplyProofline();
 TFormula conclusion =findNextConclusion();


   TFormula anteroot = conclusion.fLLink;
   TFormula conseroot = conclusion.fRLink;

   TUndoableProofEdit newEdit = new TUndoableProofEdit();

   TProofline headLastLine=fModel.getHeadLastLine();

  // int anteLineno = headLastLine.fLineno + 1;
   int oldeHeadLineno = headLastLine.fLineno;
   int level = headLastLine.fSubprooflevel;

   newline.fFormula=anteroot.copyFormula();
   newline.fJustification= fAssJustification;
   newline.fSubprooflevel= level+1;
   newline.fLastassumption=true;

   newEdit.fNewLines.add(newline);

   newline = supplyProofline();

   int conseLineno=addIfNotThere(conseroot, level+1, newEdit.fNewLines);

   if (conseLineno==-1)
     conseLineno=oldeHeadLineno +3;   // the assumption, the ?, and then it


  

   newEdit.fNewLines.add(endSubProof(level+1));
   newEdit.fNewLines.add(addImplication(anteroot, conseroot,level, conseLineno));

         newEdit.doEdit();
}

TProofline addImplication (TFormula anteroot, TFormula conseroot, int level, int consequent){
	  TProofline newline=supplyProofline();
	     TFormula formulanode = new TFormula();

	     formulanode.fKind = TFormula.binary;
	     formulanode.fInfo = String.valueOf(chImplic);
	     formulanode.fLLink = anteroot.copyFormula();
	     formulanode.fRLink = conseroot.copyFormula();


	     newline.fSubprooflevel = level;
	     newline.fFormula = formulanode;
	     newline.fFirstjustno = consequent;
	     newline.fJustification = fImplicIJustification;

	     return
	         newline;
	}



	 void doImplicI(){
	   TProofline subhead,subtail;
	   int level;

	   if (fTemplate)
	     doHintImplicI();
	   else{
	     subtail=fDisplayCellTable.oneSelected();

	     if (subtail!=null){
	       subhead=fModel.findLastAssumption();

	       if (subhead!=null){

	         level=fModel.getHeadLastLine().fSubprooflevel;

	         TUndoableProofEdit  newEdit = new TUndoableProofEdit();

	         newEdit.fNewLines.add(endSubProof(level));
	         newEdit.fNewLines.add(addImplication(subhead.fFormula, subtail.fFormula,level-1, subtail.fLineno));
	         newEdit.doEdit();

	       }

	     }

	   }

	 }


/************************ Rule of EquivE ****************************/

public class EquivEHandler implements ClickHandler{
	TextBox fText;
  boolean fLeft;
  TProofline fSelection;

 public EquivEHandler(TextBox text, TProofline proofline, boolean onLeft){
 //    putValue(NAME, label);

     fText=text;

     fLeft=onLeft;
     fSelection=proofline;
   }

public void onClick(ClickEvent event){

  TProofline newline = supplyProofline();

  int level=fModel.getHeadLastLine().fSubprooflevel;

  TFormula formulanode = new TFormula();

  formulanode.fKind = TFormula.binary;
  formulanode.fInfo = String.valueOf(chImplic);



  if (fLeft){
    formulanode.fLLink = fSelection.fFormula.fLLink.copyFormula();
    formulanode.fRLink = fSelection.fFormula.fRLink.copyFormula();
  }
  else {
  formulanode.fLLink = fSelection.fFormula.fRLink.copyFormula();
  formulanode.fRLink = fSelection.fFormula.fLLink.copyFormula();
  }

  newline.fFormula = formulanode;

  newline.fFirstjustno = fSelection.fLineno;

  newline.fJustification = equivEJustification;
  newline.fSubprooflevel = level;

  TUndoableProofEdit newEdit = new TUndoableProofEdit();
  newEdit.fNewLines.add(newline);
  newEdit.doEdit();

  removeInputPanel();
  }

}









void doEquivE(){
  TProofline firstLine=fDisplayCellTable.oneSelected();

   if ((firstLine!=null)&&fParser.isEquiv(firstLine.fFormula)){

     {
          Button leftButton;
          Button rightButton;
          TGWTProofInputPanel inputPane;

          TextBox text = new TextBox();
          text.setText("Choose on buttons.");
          text.selectAll();

          {

            boolean left=true;

 /*           leftButton = new JButton(new EquivEAction(text, "L" + chImplic+ "R", firstLine,
                left));
            rightButton = new JButton(new EquivEAction(text, "R" + chImplic+ "L", firstLine,
                !left));  */
            
          leftButton = new Button("L" + chImplic+ "R");
      	  leftButton.addClickHandler(new EquivEHandler(text,firstLine,left));
      	  rightButton = new Button("R" + chImplic+ "L");
      	  rightButton.addClickHandler(new EquivEHandler(text,firstLine,!left));
  

            Button[] buttons = {
                cancelButton(), leftButton,
                rightButton}; // put cancel on left
            inputPane = new TGWTProofInputPanel("Doing " + chEquiv + "E",
                                             text, buttons);
          }

          addInputPane(inputPane,SELECT);


        //TO DO	 fInputPane.setVisible(true); // need this
        //TO DO	  text.requestFocus(); // so selected text shows
        }

   }

}

/******************  Equiv I ****************************************/




/************************* vI vE *********************************/  


void dovI(boolean rightOnly){ // some versions allow introduction only on right to A v ?
	   Button onRightButton;
	    Button onLeftButton;
	    TGWTProofInputPanel inputPane;
	     Button[] buttons;



	   if (fTemplate)
	     doHintvI(rightOnly);  //new on right only
	   else{
	     TProofline firstLine=fDisplayCellTable.oneSelected();

	     if (firstLine!=null){

	       TextBox text = new TextBox();
	       text.setText("New formula?");
	       text.selectAll();


	        boolean left=true;



	        if (rightOnly){
	          //onRightButton = new JButton(new OrIAction(text,"Go",firstLine, !left));
	        	
	        	onRightButton = new Button("Go");
	        	onRightButton.addClickHandler(new OrIHandler(text,firstLine,!left));
	        	

	          Button[] rightButtons = {
	              cancelButton(),
	              onRightButton}; // put cancel on left
	          buttons=rightButtons;

	        }
	        else{
	        //  onRightButton = new JButton(new OrIAction(text,"On Right",firstLine, !left));
	          
	          onRightButton = new Button("On Right");
	          onRightButton.addClickHandler(new OrIHandler(text,firstLine,!left));
	          
	          
//	          onLeftButton = new JButton(new OrIAction(text, "On Left", firstLine, left));
	          
	          onLeftButton = new Button("On Left");
	          onLeftButton.addClickHandler(new OrIHandler(text,firstLine,left));

	          Button[] bothButtons = {
	              cancelButton(), onLeftButton,
	              onRightButton}; // put cancel on left
	          buttons=bothButtons;

	        }


	          inputPane = new TGWTProofInputPanel("Doing"+fOrIJustification,
	             text, buttons,fInputPalette);



	          addInputPane(inputPane,SELECT);

	         // inputPane.getRootPane().setDefaultButton(defaultButton);
	        //TO DO		          fInputPane.setVisible(true); // need this
	        //TO DO		          text.requestFocus();         // so selected text shows
	        }

	     }

	   }

void doHintvI(boolean rightOnly){

	   // menu enabled only if good

	     TFormula newFormula=null;
	     boolean proofover=false;
	     TProofline newline=null;
	     int leftdisjlineno=0,rightdisjlineno=0;
	     TFormula conclusion =findNextConclusion();

	     if ((conclusion!=null)||!fParser.isOr(conclusion)){


	       /*we have four cases here a) trivial b) left, c) right, d) both*/

	       TFormula leftdisj = conclusion.fLLink;
	       TFormula rightdisj = conclusion.fRLink;

	       TUndoableProofEdit leftEdit = new TUndoableProofEdit();

	       TProofline headLastLine=fModel.getHeadLastLine();

	       int level = headLastLine.fSubprooflevel;
	       int lastlineno = headLastLine.fLineno;

	       leftdisjlineno=addIfNotThere(leftdisj, level, leftEdit.fNewLines);

	       newline = supplyProofline();

	         newline.fFormula = conclusion.copyFormula();
	         if (leftdisjlineno!=-1)
	           newline.fFirstjustno = leftdisjlineno;
	         else
	           newline.fFirstjustno = lastlineno+2;

	         newline.fJustification = fOrIJustification;
	         newline.fSubprooflevel = level;

	        leftEdit.fNewLines.add(newline);


	       if (leftdisjlineno!=-1){   // trivial proof finished
	        leftEdit.doEdit();

	        return;                   // finished, so leave
	      }

	      TUndoableProofEdit rightEdit = new TUndoableProofEdit();

	      rightdisjlineno=addIfNotThere(rightdisj, level, rightEdit.fNewLines);

	      newline = supplyProofline();

	         newline.fFormula = conclusion.copyFormula();
	         if (rightdisjlineno!=-1)
	           newline.fFirstjustno = rightdisjlineno;
	         else
	           newline.fFirstjustno = lastlineno+2;

	         newline.fJustification = fOrIJustification;
	         newline.fSubprooflevel = level;

	        rightEdit.fNewLines.add(newline);


	        /* if our rule is right only, we have to do this

	        Right
	        Right v ?   vI
	        ? v Right comm   */

	       if (rightOnly){
	         TFormula temp, commuted;

	        temp=newline.fFormula.getLLink();
	        newline.fFormula.setLLink(newline.fFormula.getRLink());
	        newline.fFormula.setRLink(temp);             // commuted previous line

	        newline = supplyProofline();

	        newline.fFormula = conclusion.copyFormula();
	        if (rightdisjlineno!=-1){
	          newline.fLineno = lastlineno + 2; // the previous newline is either newline 1 or newline 3
	          newline.fFirstjustno = lastlineno + 1;
	        }
	        else{
	          newline.fLineno = lastlineno + 4;
	          newline.fFirstjustno = lastlineno + 3;
	        }
	        newline.fJustification = fCommJustification;
	        newline.fSubprooflevel = level;

	         rightEdit.fNewLines.add(newline);
	       }



	       if (rightdisjlineno!=-1){   // trivial proof finished
	        rightEdit.doEdit();

	        return;                   // finished, so leave
	      }


	   //not trivial

	   TUndoableProofEdit bothEdit = new TUndoableProofEdit();
	   leftdisjlineno=addIfNotThere(leftdisj, level, bothEdit.fNewLines);
	   rightdisjlineno=addIfNotThere(rightdisj, level, bothEdit.fNewLines);


	   // now we have 3 edit actions, we'll let the User decide


	   TProofline firstLine=fDisplayCellTable.oneSelected();  //starting And


	      {
	           Button leftButton;
	           Button rightButton;
	           Button bothButton;
	           TGWTProofInputPanel inputPane;

	           TextBox text = new  TextBox();
	            text.setText("If you are unsure, choose 'Both' then later edit out the unused one (tricky!).");
	           text.selectAll();


	           {

	       //      leftButton = new Button(new HintVIAction(text, "Left", firstLine,leftEdit));
	             
	        	   leftButton = new Button("Left");
		           leftButton.addClickHandler(new HintVIHandler(text, firstLine,leftEdit));
		           
		           bothButton = new Button("Both");
		           bothButton.addClickHandler(new HintVIHandler(text, firstLine,bothEdit));
		           
		           rightButton = new Button("Right");
		           rightButton.addClickHandler(new HintVIHandler(text, firstLine,rightEdit));
		        	    
	             
	             
	        //     bothButton = new JButton(new HintVIAction(text, "Both", firstLine,bothEdit));
	    //         rightButton = new JButton(new HintVIAction(text, "Right", firstLine,rightEdit));

	             Button[] buttons = {
	                 cancelButton(), leftButton,bothButton,
	                 rightButton}; // put cancel on left
	             inputPane = new TGWTProofInputPanel("Doing"+fOrIJustification+" with Tactics." + "Choose disjunct to aim for.",
	                                              text, buttons);
	           }

	           addInputPane(inputPane,SELECT);

	        //   inputPane.getRootPane().setDefaultButton(defaultButton);
	         //TO DO	          fInputPane.setVisible(true); // need this
	         //TO DO	          text.requestFocus(); // so selected text shows
	         }


	     }

	   }


public class OrIHandler implements ClickHandler{
    TextBox fText;
    TProofline fSelection;
    boolean fLeft;

     public OrIHandler(TextBox text, TProofline selection, boolean left){
 //      putValue(NAME, label);

       fText=text;

       fSelection=selection;

       fLeft=left;
     }

      public void onClick(ClickEvent event){
     boolean useFilter =true;
     ArrayList dummy = new ArrayList();

     String aString= TUtilities.defaultFilter(fText.getText());

     TFormula root = new TFormula();
     StringReader aReader= new StringReader(aString);
    
     boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);
     
     if (!wellformed){
         String message = "The string is illformed."+
                           (fParser.fParserErrorMessage.toString()).replaceAll(strCR,"");  //filter out returns



         fText.setText(message);
         fText.selectAll();
 //To DO        fText.requestFocus();

          }
    else {
     TProofline newline = supplyProofline();


     TFormula formulanode = new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chOr);
     if (fLeft){
        formulanode.fLLink = root;
        formulanode.fRLink = fSelection.fFormula.copyFormula();
      }
      else{
         formulanode.fLLink = fSelection.fFormula.copyFormula();
         formulanode.fRLink = root;
       }


     newline.fFormula=formulanode;
     newline.fFirstjustno = fSelection.fLineno;
     newline.fJustification= fOrIJustification;
     newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;

     TUndoableProofEdit  newEdit = new TUndoableProofEdit();
     newEdit.fNewLines.add(newline);
     newEdit.doEdit();

     removeInputPanel();};


    }

  }





  public class HintVIHandler implements ClickHandler{
	  TextBox fText;
     TProofline fSelection;
     TUndoableProofEdit fEdit;

      public HintVIHandler(TextBox text, TProofline proofline,
                          TUndoableProofEdit edit){
  //      putValue(NAME, label);

        fText=text;
        fSelection=proofline;
        fEdit=edit;

      }

  public void onClick(ClickEvent event){

     fEdit.doEdit();

  removeInputPanel();


     }

 }
  
  /************************ Rule of OrE ****************************/



  void doHintvE(){

      TProofline orline=fDisplayCellTable.oneSelected();

      if ((orline != null)&&fParser.isOr(orline.fFormula)) {  //menu enabled only it this is true


        TFormula conclusion =findNextConclusion();

        if (conclusion!=null) { //menu enabled only it this is true

          TFormula leftDisjunct = orline.fFormula.fLLink;
          TFormula rightDisjunct = orline.fFormula.fRLink;

          TUndoableProofEdit newEdit = new TUndoableProofEdit();

          TProofline headLastLine=fModel.getHeadLastLine();


       int oldeHeadLineno = headLastLine.fLineno;
       int level = headLastLine.fSubprooflevel;

       TProofline newline = supplyProofline();

       newline.fFormula=leftDisjunct.copyFormula();
       newline.fJustification= fAssJustification;
       newline.fSubprooflevel= level+1;
       newline.fLastassumption=true;

       newEdit.fNewLines.add(newline);

       int firstLineno=addIfNotThere(conclusion, level+1, newEdit.fNewLines);

       if (firstLineno==-1){
         firstLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
         oldeHeadLineno += 3;
       }

       newEdit.fNewLines.add(endSubProof(level+1));

       newline = supplyProofline();

       newline.fFormula=rightDisjunct.copyFormula();
       newline.fJustification= fAssJustification;
       newline.fSubprooflevel= level+1;
       newline.fLastassumption=true;

       newEdit.fNewLines.add(newline);

       int secondLineno=addIfNotThere(conclusion, level+1, newEdit.fNewLines);

       if (secondLineno==-1){
         secondLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
         oldeHeadLineno += 3;
       }

       newEdit.fNewLines.add(endSubProof(level+1));
     //  newEdit.fNewLines.add(addImplication(anteroot, conseroot,level, conseLineno));


  newline = supplyProofline();

     newline.fFormula = conclusion.copyFormula();
     newline.fFirstjustno = orline.fLineno;
     newline.fSecondjustno = firstLineno;
     newline.fThirdjustno = secondLineno;
     newline.fJustification = fOrEJustification;
     newline.fSubprooflevel = level;

     newEdit.fNewLines.add(newline);

     newEdit.doEdit();



        }
         }


  }


  


  boolean canDoVE(FiveLines lines){
        /*
        {In the normal way they choose three lines, but one is a selectable line and the other}
        {the last line of a subproof, so total selected = 3 but Three Selected is false. However
        if the proof }
        {is from the same assumption Three selected will be true}


       */
        TProofline [] selected=  fDisplayCellTable.exactlyNLinesSelected(3);
        boolean strangecase;

        strangecase=(selected!=null);

        if (strangecase){
          lines.fFirstline=(TProofline)selected[0];
          lines.fSubtail1=(TProofline)selected[1];
          lines.fSubtail2=(TProofline)selected[2];
        }


        if (fDisplayCellTable.totalSelected()==3){       // we have to have this

          if (!strangecase){
             selected=  fDisplayCellTable.exactlyNLinesSelected(2);

             if (selected==null)
               return
                   false;
             else{

               lines.fFirstline = (TProofline) selected[0];
               lines.fSubtail2 = (TProofline) selected[1];

               TProofline [][] subProofs = fDisplayCellTable.nSubProofsSelected(1);

               if (subProofs==null)
                  return
                      false;
               else{
                  lines.fSubhead1=subProofs[0][0];
                  lines.fSubtail1=subProofs[0][1];

               }

             }





             /*{This peculiar condition}
       {                                 covers the case of the same antecedent twice}
       {and the jump is to avoid a side effect in the selection routines}
    */


          }

          // at this point we have the v Line and both tails set, we lines.fSubhead1 set
          // in the normal case, but not in the strange one

          // The firstline has to be an 'Or'

          if (!fParser.isOr(lines.fFirstline.fFormula))
            return
                false;      // bale out


          lines.fSubhead2=fDisplayCellTable.getProofListModel().findLastAssumption();

          if (lines.fSubhead2==null)
                    return
                        false;
          else{
             if (strangecase)
               lines.fSubhead1=lines.fSubhead2;
          }

         // now all is set


         if (((TFormula.equalFormulas(lines.fSubhead1.fFormula, lines.fFirstline.fFormula.fLLink) && TFormula.equalFormulas(lines.fSubhead2.fFormula, lines.fFirstline.fFormula.fRLink)) ||
             (TFormula.equalFormulas(lines.fSubhead2.fFormula, lines.fFirstline.fFormula.fLLink) && TFormula.equalFormulas(lines.fSubhead1.fFormula, lines.fFirstline.fFormula.fRLink)) &&
             TFormula.equalFormulas(lines.fSubtail1.fFormula, lines.fSubtail2.fFormula)))
            return
                true;
        }

  return
     false;

    }



    void dovE(){
      TProofline firstline=null,subtail1=null, subtail2=null;


      if (fTemplate)
        doHintvE();
      else{
          FiveLines lines=new FiveLines();

        if (canDoVE(lines)){

          firstline=lines.fFirstline;
          subtail1=lines.fSubtail1;
          subtail2=lines.fSubtail2;

          TProofline newline = supplyProofline();
          int level = fModel.getHeadLastLine().fSubprooflevel;

          newline.fFormula=subtail1.fFormula.copyFormula();
          newline.fFirstjustno=firstline.fLineno;
          newline.fSecondjustno=subtail1.fLineno;
          newline.fThirdjustno=subtail2.fLineno;
          newline.fJustification= fOrEJustification;
          newline.fSubprooflevel= level-1;


          TUndoableProofEdit  newEdit = new TUndoableProofEdit();

          newEdit.fNewLines.add(endSubProof(level));

          newEdit.fNewLines.add(newline);

          newEdit.doEdit();

        }


      }

      }

    /************************ Rule of EquivI ****************************/

    void doHintEquivI(){    //menu not enabled if not possible


      TFormula conclusion =findNextConclusion();

      if (conclusion!=null) { //menu enabled only it this is true


         TFormula anteroot = conclusion.fLLink;
         TFormula conseroot = conclusion.fRLink;


              TUndoableProofEdit newEdit = new TUndoableProofEdit();

              TProofline headLastLine=fModel.getHeadLastLine();


           int oldeHeadLineno = headLastLine.fLineno;
           int level = headLastLine.fSubprooflevel;

           TProofline newline = supplyProofline();

           newline.fFormula=anteroot.copyFormula();
           newline.fJustification= fAssJustification;
           newline.fSubprooflevel= level+1;
           newline.fLastassumption=true;

           newEdit.fNewLines.add(newline);

           int firstLineno=addIfNotThere(conseroot, level+1, newEdit.fNewLines);

           if (firstLineno==-1){
             firstLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
             oldeHeadLineno += 3;
           }

           newEdit.fNewLines.add(endSubProof(level+1));

           newline = supplyProofline();

           newline.fFormula=conseroot.copyFormula();
           newline.fJustification= fAssJustification;
           newline.fSubprooflevel= level+1;
           newline.fLastassumption=true;

           newEdit.fNewLines.add(newline);

           int secondLineno=addIfNotThere(anteroot, level+1, newEdit.fNewLines);

           if (secondLineno==-1){
             secondLineno = oldeHeadLineno + 3; // the assumption, the ?, and then it
             oldeHeadLineno += 3;
           }

           newEdit.fNewLines.add(endSubProof(level+1));


      newline = supplyProofline();

         newline.fFormula = conclusion.copyFormula();
         newline.fFirstjustno = firstLineno;
         newline.fSecondjustno = secondLineno;
         newline.fJustification = equivIJustification;
         newline.fSubprooflevel = level;

         newEdit.fNewLines.add(newline);

         newEdit.doEdit();



            }
    }




    /*

     function TProofWindow.DoHintEquivI: TCommand;

       var
        Anteroot, Conseroot, conclusion: TFormula;
        Antecedent, Consequent, level: integer;
        cancel, consequentThere, error: Boolean;
        aLineCommand: TLineCommand;
        firstnewline, secondnewline: TProofline;

      begin
       DoHintEquivI := gNoChanges;

       error := false;
       conclusion := SELF.FindTailFormula;
       if (conclusion = nil) then
        error := true
       else if (conclusion.fInfo <> chEquiv) then
        error := true;



       if error then
        BugAlert('With the tactic for �I, the conclusion must be an equivalence.')
       else
        begin

         anteroot := conclusion.fLlink;
         conseroot := conclusion.fRlink;


         Antecedent := 0;
         Consequent := 0;

         cancel := false;
         consequentThere := true;

         New(aLineCommand);
         FailNIL(aLineCommand);
         aLineCommand.ILineCommand(cAddLine, SELF);

         level := TProofline(SELF.fHead.Last).fSubProofLevel;

         aLineCommand.fNewlines.InsertLast(Addassumption(Anteroot, false));

         AddIfNotThere(Conseroot, level + 1, Consequent, firstnewline, secondnewline);

         if Consequent = 0 then {not already there}
          begin
           consequentThere := false;
           aLineCommand.fNewlines.InsertLast(firstnewline);
           aLineCommand.fNewlines.InsertLast(secondnewline);
           Consequent := TProofline(fHead.Last).flineno + 3; {the lines are only in the}
     {                                                                       command at this stage not}
     {                                                                       the document}
          end;

         aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

         aLineCommand.fNewlines.InsertLast(Addassumption(Conseroot, false));

         AddIfNotThere(Anteroot, level + 1, Antecedent, firstnewline, secondnewline);

         if Antecedent = 0 then {not already there}
          begin
           aLineCommand.fNewlines.InsertLast(firstnewline);
           aLineCommand.fNewlines.InsertLast(secondnewline);
           if consequentThere then
            Antecedent := TProofline(fHead.Last).flineno + 3 {the lines are only in the}
     {                                                                           command at this stage}
     {                                                                           not the document}
           else
            Antecedent := TProofline(fHead.Last).flineno + 6 {the lines are only in the}
     {                                                                           command at this stage}
     {                                                                           not the document}

          end;

         aLineCommand.fNewlines.InsertLast(EndSubProof(level + 1));

         aLineCommand.fNewlines.InsertLast(AddEquiv(Anteroot, Conseroot, level, Antecedent, Consequent));

         DoHintEquivI := aLineCommand;

        end;
      end;


    */

    class FiveLines {
      TProofline fFirstline=null;
      TProofline fSubhead1=null;
      TProofline fSubhead2=null;
      TProofline fSubtail1=null;
      TProofline fSubtail2=null;
    }

    boolean canDoEquivI(FiveLines lines){
      TProofline[] selected;
      if (fDisplayCellTable.totalSelected() == 2) { //must have this

        TProofline[][] subProofs = fDisplayCellTable.nSubProofsSelected(1);

        if (subProofs != null) {
          lines.fSubhead1 = subProofs[0][0];
          lines.fSubtail1 = subProofs[0][1];

          lines.fSubtail2 = fDisplayCellTable.oneSelected();

          if (lines.fSubtail2 != null) {
            // lines.fSubtail2 = (TProofline) selected[0];
            lines.fSubhead2 = fDisplayCellTable.getProofListModel().findLastAssumption();
          }

        }

        /*This is the normal case and all heads and tails should be set. However..... */

        if ( (lines.fSubhead1 == null) || (lines.fSubhead2 == null) || (lines.fSubtail1 == null) ||
            (lines.fSubtail2 == null)) {

          /*  {In the normal way they choose two lines, but one is a selectable line and the other}
                  {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
                  {is of F=F Two selected will be true} */

          lines.fSubhead1 = null;
          lines.fSubhead2 = null;
          lines.fSubtail1 = null;
          lines.fSubtail2 = null;

          selected = fDisplayCellTable.exactlyNLinesSelected(2);

          if (selected != null) {
            lines.fSubtail1 = (TProofline) selected[0];
            lines.fSubtail2 = (TProofline) selected[1];

            if (lines.fSubtail1.fFormula.equalFormulas(lines.fSubtail1.fFormula,
                                                lines.fSubtail2.fFormula))
              lines.fSubhead1 = lines.fSubtail1;
            lines.fSubhead2 = fDisplayCellTable.getProofListModel().findLastAssumption();
          }

        }

        /*We continue if everything is set*/

        if ( (lines.fSubhead1 != null) && (lines.fSubhead2 != null) && (lines.fSubtail1 != null) &&
            (lines.fSubtail2 != null)) {
          if (lines.fSubhead1.fFormula.equalFormulas(lines.fSubhead1.fFormula,
                                              lines.fSubtail2.fFormula) &&
              lines.fSubhead2.fFormula.equalFormulas(lines.fSubhead2.fFormula,
                                              lines.fSubtail1.fFormula)) {
            return true;
          }
        }
      }
      return
          false;
    }

     void doEquivI(){
       TProofline subtail1 = null, subtail2 = null;
      // TProofline[] selected;

       if (fTemplate)
         doHintEquivI();
       else {

       FiveLines lines= new FiveLines();

       if (canDoEquivI(lines)){

           /******** OLD VERSION
         if (fProofListView.totalSelected() == 2) { //must have this

           TProofline[][] subProofs = fProofListView.nSubProofsSelected(1);

           if (subProofs != null) {
             subhead1 = subProofs[0][0];
             subtail1 = subProofs[0][1];

             subtail2 = fProofListView.oneSelected();

             if (subtail2 != null) {
               // subtail2 = (TProofline) selected[0];
               subhead2 = fProofListView.fModel.findLastAssumption();
             }

           }

           /*This is the normal case and all heads and tails should be set. However.....

           if ( (subhead1 == null) || (subhead2 == null) || (subtail1 == null) ||
               (subtail2 == null)) {

             /*  {In the normal way they choose two lines, but one is a selectable line and the other}
                     {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
                     {is of F=F Two selected will be true}

             subhead1 = null;
             subhead2 = null;
             subtail1 = null;
             subtail2 = null;

             selected = fProofListView.exactlyNLinesSelected(2);

             if (selected != null) {
               subtail1 = (TProofline) selected[0];
               subtail2 = (TProofline) selected[1];

               if (subtail1.fFormula.equalFormulas(subtail1.fFormula,
                                                   subtail2.fFormula))
                 subhead1 = subtail1;
               subhead2 = fProofListView.fModel.findLastAssumption();
             }

           }

           /*We continue if everything is set

           if ( (subhead1 != null) && (subhead2 != null) && (subtail1 != null) &&
               (subtail2 != null)) {
             if (subhead1.fFormula.equalFormulas(subhead1.fFormula,
                                                 subtail2.fFormula) &&
                 subhead2.fFormula.equalFormulas(subhead2.fFormula,
                                                 subtail1.fFormula))*/

               subtail1=lines.fSubtail1;
               subtail2=lines.fSubtail2;


               int level = fModel.getHeadLastLine().fSubprooflevel;

               TUndoableProofEdit newEdit = new TUndoableProofEdit();

               newEdit.fNewLines.add(endSubProof(level));
               newEdit.fNewLines.add(addEquiv(subtail2.fFormula, subtail1.fFormula,
                                              level - 1,subtail2.fLineno,subtail1.fLineno));
               newEdit.doEdit();


       }
           }
         }

     TProofline addEquiv (TFormula anteroot, TFormula conseroot, int level, int antecedent, int consequent){
         TProofline newline=supplyProofline();
         TFormula formulanode = new TFormula();

         formulanode.fKind = TFormula.binary;
         formulanode.fInfo = String.valueOf(chEquiv);
         formulanode.fLLink = anteroot.copyFormula();
         formulanode.fRLink = conseroot.copyFormula();


         newline.fSubprooflevel = level;
         newline.fFormula = formulanode;
         newline.fFirstjustno = consequent;
         newline.fSecondjustno = antecedent;
         newline.fJustification = equivIJustification;

         return
             newline;



    }

    /*

      function TProofWindow.DoEquivI: TCommand;

       label
        99, 98;

       var
        strangecase: boolean;
        firstline, subhead1, subhead2, subtail1, subtail2: TProofline;

        aLineCommand: TLineCommand;
        formula1: TFormula;
        level: integer;

      begin

       DoEquivI := gNoChanges;

       if fTemplate then {gTemplate}
        DoEquivI := DoHintEquivI
       else
        begin

     {In the normal way they choose two lines, but one is a selectable line and the other}
     {the last line of a subproof, so total selected = 2 but TwoSelected is false. However if they proof }
     {is of F=F Two selected will be true}

         strangecase := fTextList.TwoSelected(subtail1, subtail2); {trying for F equiv F }

         if strangecase then
          begin
           if Equalformulas(subtail1.fFormula, subtail2.fFormula) then
            begin
            subhead1 := subtail1;
            goto 99;
            end
           else
            goto 98;
          end;

         if (fTextList.TotalSelected = 2) then
          if fTextList.OneSubProofSelected(subhead1, subtail1) then
           if fTextList.OneSelected(subtail2) then
     99:
            if FindLastAssumption(subhead2) then
            begin
            if (Equalformulas(subhead1.fFormula, subtail2.fFormula) and Equalformulas(subhead2.fFormula, subtail1.fFormula)) then
            begin

            level := TProofline(fHead.Last).fSubprooflevel;

            New(aLineCommand);
            FailNil(aLineCommand);
            aLineCommand.ILineCommand(cAddLine, SELF);

            aLineCommand.fNewlines.InsertLast(EndSubProof(level));

            aLineCommand.fNewlines.InsertLast(AddEquiv(subtail2.fFormula, subtail1.fFormula, level - 1, subtail2.fLineno, subtail1.fLineno));

            DoEquivI := aLineCommand;

            end;
            end;
        end;
     98:
      end;



    */




/************************* Utilities *********************************/  

int addIfNotThere(TFormula theFormula, int level, ArrayList lineList){
	 /* {returns lineno if the formula is in proof and selectable, else supplies a question mark
	  line and a formula ine and adds them to the line list}
	 {and returns -1 as its line no } */

	    int returnLineno=fModel.lineNoOfLastSelectableEqualFormula(theFormula);

	     if (returnLineno==-1){   // not there

	       TFormula newFormula= new TFormula();

	       newFormula.fInfo = "?";
	       newFormula.fKind = TFormula.predicator;

	       TProofline newline = supplyProofline();

	       newline.fFormula = newFormula;
	       newline.fSelectable = false;
	       newline.fJustification = "?";
	       newline.fSubprooflevel = level;

	       lineList.add(newline);

	       newline = supplyProofline();

	       newline.fFormula = theFormula.copyFormula();
	       newline.fJustification = "?";
	       newline.fSubprooflevel = level;

	       lineList.add(newline);

	     }

	return
	         returnLineno;

	}

  
  /************************* Rule of ImplicE *********************************/  
  
  void doImplicE(){

	   TProofline newline, firstline, secondline;
	   TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

	     if (selections != null){

	       firstline = selections[0];
	       secondline = selections[1];

	       if ( (fParser.isImplic(secondline.fFormula)) &&
	           (firstline.fFormula.equalFormulas(secondline.fFormula.fLLink,
	                                             firstline.fFormula))) {
	         newline = firstline;
	         firstline = secondline;
	         secondline = newline; //{to make line 1 contain the arrow}
	         newline = null;
	       }

	       if ( (fParser.isImplic(firstline.fFormula)) &&
	           (firstline.fFormula.equalFormulas(firstline.fFormula.fLLink,
	                                             secondline.fFormula))) {

	          newline = supplyProofline();
	          int level=fModel.getHeadLastLine().fSubprooflevel;


	          newline.fFormula = (firstline.fFormula.fRLink).copyFormula();
	          newline.fFirstjustno = firstline.fLineno;
	          newline.fSecondjustno = secondline.fLineno;
	          newline.fJustification = fImplicEJustification;
	          newline.fSubprooflevel = level;

	          TUndoableProofEdit newEdit = new TUndoableProofEdit();
	          newEdit.fNewLines.add(newline);
	          newEdit.doEdit();
	       }
	     }
	 }
  
  /********************** Rule of UG ****************************/

  void doHintUG(){   // enabled only if appropriate

    TFormula conclusion =findNextConclusion();

    TFormula variForm = conclusion.quantVarForm();
    TFormula scope = conclusion.scope();

    TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);

    if (freeFormula!=null){
      String message = "Not permitted "
          + fParser.writeFormulaToString(variForm)
          + " is free in "
          + fParser.writeFormulaToString(freeFormula);
      bugAlert("DoingUG. Warning.",message);
    }
    else{
       TUndoableProofEdit newEdit = new TUndoableProofEdit();

       TProofline headLastLine=fModel.getHeadLastLine();

       int level = headLastLine.fSubprooflevel;
       int lastlineno = headLastLine.fLineno;

       int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

       if (scopelineno==-1){   // not there
          scopelineno = lastlineno+2;
          lastlineno += 2;
       }

            TProofline newline = supplyProofline();

            newline.fFormula = conclusion.copyFormula();
            newline.fFirstjustno=scopelineno;

            newline.fJustification = UGJustification;
            newline.fSubprooflevel = level;

            newEdit.fNewLines.add(newline);

          newEdit.doEdit();


    }


  }



  public class UGHandler implements ClickHandler{
	   TextBox fText;
       TProofline fFirstline=null;




        public UGHandler(TextBox text, TProofline firstline){
  //        putValue(NAME, label);

          fText=text;
          fFirstline=firstline;
        }

         public void onClick(ClickEvent event){

           boolean useFilter=true;


           String aString = TUtilities.defaultFilter(fText.getText());

           if ((aString==null)||
               (aString.length()!=1)||
               !fParser.isVariable(aString.charAt(0))){

             String message = aString + " is not a variable.";

             fText.setText(message);
             fText.selectAll();
//             fText.requestFocus();
           }
           else {

             TFormula variablenode= new TFormula();

             variablenode.fKind = TFormula.variable;
             variablenode.fInfo = aString;

             // test for free

             TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variablenode);

             if (freeFormula!=null){
               String message = aString + " is free in " + fParser.writeFormulaToString(freeFormula);

                fText.setText(message);
                fText.selectAll();
 //               fText.requestFocus();
             }
             else{

               TFormula formulanode = new TFormula();

               formulanode.fKind = TFormula.quantifier;
               formulanode.fInfo = String.valueOf(chUniquant);
               formulanode.fLLink = variablenode;
               formulanode.fRLink = fFirstline.fFormula.copyFormula();

               TProofline newline = supplyProofline();

               int level = fModel.getHeadLastLine().fSubprooflevel;

               newline.fFormula = formulanode;
               newline.fJustification = UGJustification;
               newline.fFirstjustno = fFirstline.fLineno;
               newline.fSubprooflevel = level;

               TUndoableProofEdit newEdit = new TUndoableProofEdit();
               newEdit.fNewLines.add(newline);
               newEdit.doEdit();

               removeInputPanel();
             }
           }

       }

     }






  void doUG(){
    TProofline firstline;
    Button defaultButton;
//   JButton dropLastButton;
    TGWTProofInputPanel inputPane;





    if (fTemplate)
      doHintUG();
    else{

      firstline = fDisplayCellTable.oneSelected();

      if (firstline != null) {


       // JTextField text = new JTextField("Variable of quantification?");
      //  text.selectAll();
        TextBox text = new TextBox();
        text.setText("Variable of quantification?");
        text.selectAll();

    //    defaultButton = new JButton(new UGAction(text,"Go", firstline));
        defaultButton = new Button("Go");
        defaultButton.addClickHandler(new UGHandler(text,firstline));


        Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
        inputPane = new TGWTProofInputPanel("Doing UG", text, buttons);


        addInputPane(inputPane,SELECT);

  //      inputPane.getRootPane().setDefaultButton(defaultButton);
 //       fInputPane.setVisible(true); // need this
 //       text.requestFocus();         // so selected text shows

      }



    }

  }


 private boolean getTheTerm(TextBox inOutText,TFormula term){
    boolean useFilter = true;
    ArrayList dummy = new ArrayList();
    String aString = TUtilities.defaultFilter(inOutText.getText());
    StringReader aReader = new StringReader(aString);
    boolean wellformed=false;

    wellformed=fParser.term(term,aReader);

    if (!wellformed) {
             String message = "The string is not a term." +
                 (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

             //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

             inOutText.setText(message);
             inOutText.selectAll();
 //            inOutText.requestFocus();
           }
 return
    wellformed;
 } 
  
 /*************************** Rule of UI *************************/


 public class UIHandler implements ClickHandler{
	 TextBox fText;
       TProofline fFirstline=null;




        public UIHandler(TextBox text, TProofline firstline){
//          putValue(NAME, label);

          fText=text;
          fFirstline=firstline;
        }

         public void onClick(ClickEvent event){


           /*********************/


           boolean useFilter = true;
           ArrayList dummy = new ArrayList();

           String aString = TUtilities.defaultFilter(fText.getText());

           TFormula term = new TFormula();
           StringReader aReader = new StringReader(aString);
           boolean wellformed=false;

           wellformed=fParser.term(term,aReader);

           if (!wellformed) {
             String message = "The string is not a term." +
                 (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

             //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

             fText.setText(message);
             fText.selectAll();
     //        fText.requestFocus();
           }

           else {

             TFormula scope = fFirstline.fFormula.fRLink.copyFormula();

             if(!scope.freeForTest(term, fFirstline.fFormula.quantVarForm())){


               String message = aString + " for " +
                                fFirstline.fFormula.quantVar()+
                                " in " +
                                fParser.writeFormulaToString(scope) +
                                " leads to capture. " +
                                "Use another term or Cancel";


                fText.setText(message);
                fText.selectAll();
 //               fText.requestFocus();
             }
             else{

             	scope.subTermVar(scope,term,fFirstline.fFormula.quantVarForm());
    



               TProofline newline = supplyProofline();

               int level = fModel.getHeadLastLine().fSubprooflevel;

               newline.fFormula = scope;
               newline.fJustification = UIJustification;
               newline.fFirstjustno = fFirstline.fLineno;
               newline.fSubprooflevel = level;

               TUndoableProofEdit newEdit = new TUndoableProofEdit();
               newEdit.fNewLines.add(newline);
               newEdit.doEdit();

               removeInputPanel();
             }
           }

       }

     } 
  
  /**********************************UI**********************************/ 
  
 void doUI(){
	  TProofline firstline;
	  Button defaultButton;
//	   JButton dropLastButton;
	   TGWTProofInputPanel inputPane;


	  firstline=fDisplayCellTable.oneSelected();

	  if ((firstline != null)&&fParser.isUniquant(firstline.fFormula)) {

	 //   JTextField text = new JTextField("Term to instantiate with?");
	//       text.selectAll();
	       
	          TextBox text = new TextBox();
	          text.setText("Term to instantiate with?");
	          text.selectAll();

	//       defaultButton = new JButton(new UIAction(text,"Go", firstline));

	       defaultButton = new Button("Go");
	       defaultButton.addClickHandler(new UIHandler(text,firstline));

	       
	       
	       Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	       inputPane = new TGWTProofInputPanel("Doing UI", text, buttons,fInputPalette);


	       addInputPane(inputPane,SELECT);

	    //   inputPane.getRootPane().setDefaultButton(defaultButton);
	 //      fInputPane.setVisible(true); // need this
	 //      text.requestFocus();         // so selected text shows


	     }


	}
 
 /**********************************EG**********************************/ 

 public class HintEGHandler implements ClickHandler{
	 TextBox fText;
     TFormula fVariForm,fScope,fConclusion;


      public HintEGHandler(TextBox text, TFormula variForm, TFormula scope, TFormula conclusion){
 //       putValue(NAME, label);

        fText=text;
        fVariForm=variForm;
        fScope=scope;
        fConclusion=conclusion;
      }

       public void onClick(ClickEvent event){
         String message="";

         String aString = TUtilities.defaultFilter(fText.getText());

         TFormula term = new TFormula();
         StringReader aReader = new StringReader(aString);
         boolean wellformed=false;

         wellformed=fParser.term(term,aReader);

         if (!wellformed) {
           message = "The string is not a term." +
               (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

           fText.setText(message);
           fText.selectAll();
 //          fText.requestFocus();
         }


         else {

          if(!fScope.freeForTest(term, fVariForm)){

             message = aString + " for " +
                              fParser.writeFormulaToString(fVariForm)+
                              " in " +
                              fParser.writeFormulaToString(fScope) +
                              " leads to capture. " +
                              "Use another term or Cancel";


              fText.setText(message);
              fText.selectAll();
 //             fText.requestFocus();
           }
           else{

             boolean proofover = false;

             fScope.subTermVar(fScope, term, fVariForm);
             


             TUndoableProofEdit newEdit = new TUndoableProofEdit();

             TProofline headLastLine = fModel.getHeadLastLine();

             int level = headLastLine.fSubprooflevel;
             int lastlineno = headLastLine.fLineno;

             int scopelineno = addIfNotThere(fScope, level, newEdit.fNewLines);

             if (scopelineno == -1) { // not there
               scopelineno = lastlineno + 2;
               lastlineno += 2;
             }
  
               TProofline newline = supplyProofline();

               newline.fFormula = fConclusion.copyFormula();
               newline.fJustification = EGJustification;
               newline.fFirstjustno = scopelineno;
               newline.fSubprooflevel = level;

               newEdit.fNewLines.add(newline);

           //  }

           newEdit.doEdit();

            removeInputPanel();


           }
         }

     }

   }
 
 
 public void doHintEG(){  // only enabled if viable
	   Button defaultButton;
	   TGWTProofInputPanel inputPane;

	   TFormula conclusion = findNextConclusion();
	   TFormula variForm = conclusion.quantVarForm();
	   TFormula scope = conclusion.scope().copyFormula();


//	   JTextField text = new JTextField("Term that was generalized on?");
//
	   //text.selectAll();
	       
	       TextBox text = new TextBox();
	       text.setText("Term that was generalized on?");
	       text.selectAll();
	       
	       defaultButton = new Button("Go");
	       defaultButton.addClickHandler(new HintEGHandler(text, variForm,scope,conclusion));

//	       defaultButton = new JButton(new HintEGAction(text,"Go", variForm,scope,conclusion));

	       Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	       inputPane = new TGWTProofInputPanel("Doing"+EGJustification+" with Tactics.", text, buttons);


	       addInputPane(inputPane,SELECT);

//	       inputPane.getRootPane().setDefaultButton(defaultButton);
//	       fInputPane.setVisible(true); // need this
//	       text.requestFocus();         // so selected text shows


	 }
 
 public class EGYesNoHandler implements ClickHandler{


	   EGHandler fParent;
	   boolean fYes;

	   public EGYesNoHandler(EGHandler parent,boolean yes){

/*	     if (yes)
	       putValue(NAME, "Yes");
	     else
	       putValue(NAME, "No"); */

	     fParent=parent;
	     fYes=yes;

	   }

	   public void onClick(ClickEvent event){

	      TFormula surgeryTerm;

	     if (fParent.fNumTreated<fParent.fNumOccurrences){

	        surgeryTerm= fParent.fTerms[fParent.fNumTreated];

	        surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


	        if (fYes){
	           surgeryTerm.fKind = TFormula.variable;
	           surgeryTerm.fInfo = fParent.fVariable.fInfo; // (*surgery*)
	           surgeryTerm.fRLink = null;  // important becuase there might be the rest of a term there
	        }

	       // if they have pressed the No button, fYes is false and we do nothing

	       fParent.fNumTreated+=1;

	   }

	     if (fParent.fNumTreated<fParent.fNumOccurrences){
	                   // put the marker in the next one

	       fParent.fTerms[fParent.fNumTreated].fInfo= chInsertMarker+
	                                                    fParent.fTerms[fParent.fNumTreated].fInfo;


	         String message= fParser.writeFormulaToString(fParent.fCopy);


	         fParent.fText.setText(message);

//	         fParent.fText.requestFocus();

	     }
	     else{                                        //  last one, return to parent

	   //   JButton defaultButton = new JButton(fParent);
	    	 
	      Button defaultButton=new Button("Go");
	/*      if (fYes)
	    	  defaultButton = new Button("Yes");
	      else
	    	  defaultButton = new Button("No"); */
	      defaultButton.addClickHandler(fParent);

	      Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	      TGWTProofInputPanel inputPane = new TGWTProofInputPanel("Doing EG-- Stage4,"+
	            " displaying scope. " +
	            "If suitable, press Go.", fParent.fText, buttons);


	      addInputPane(inputPane,SELECT);

	      String message= fParser.writeFormulaToString(fParent.fCopy);

//	       fParent.fText.setEditable(true);
	       fParent.fText.setText(message);
	       fParent.fText.selectAll();




//	      inputPane.getRootPane().setDefaultButton(defaultButton);
//	      fInputPane.setVisible(true); // need this
//	      fParent.fText.requestFocus();         // so selected text shows



	/********/





	   fParent.fStage=4;


	      /************/






	     }


	   }

	 }
 
 public class EGHandler implements ClickHandler{
	  //    JTextComponent fText;

	  TextBox fText;


	      TProofline fFirstline=null;
	      TFormula fTerm=null, fVariable=null, fScope=null, fCopy=null,
	          fCurrentNode=null,fCurrentCopyNode=null;
	      int fNumOccurrences=0; //of term
	      int fNumTreated=0;
	      int fStage=1;
	      TFormula.MarkerData markerData;

	      TFormula [] fTerms; // the occurrences of the (same) term in the intended scope


	      boolean useFilter=true;

	     /*We have here to get three things out of the User:- the term to generalize on,
	      the variable to generalize with, and the occurrences. Since we might enter
	      this several times, we initialize fTerm to null. Then, when we get the
	      term it is set to a value, and so on. And we do only one of these things per pass through */


	       public EGHandler(TextBox text, TProofline firstline){
//	         putValue(NAME, label);


	         fText=text;
	         fFirstline=firstline;

	         fCopy = fFirstline.fFormula.copyFormula();



	       }

	        public void onClick(ClickEvent event){
	          // typically this will be called 3 times for the 3 stages

	         // boolean useFilter = true;
	         // String message=null;
	         // String aString=null;

	         /* if (fTerm==null)   // First stage, trying to find the term
	             find
	          ();
	          else{
	            if (fVariable == null)  // Second stage, we have the term trying for variable
	                  findVariable();
	            else{

	              fNumOccurrences = (fFirstline.fFormula).numOfFreeOccurrences(fTerm);
	              if (fNumOccurrences < 2) {
	                fLastStage = true;
	                //  doLastStage

	                doOccurrences();
	              }
	            }
	          }  */

	          switch (fStage){

	            case 1:
	              findTerm();
	              break;

	            case 2:
	              findVariable();
	              break;

	            case 3:
	              displayScope();
	              break;

	            case 4:
	              readScope();
	              break;




	            default: ;
	          }
	          }


	private void displayScope(){

	   String message= fParser.writeFormulaToString(fCopy);

	   fText.setText(message);
	   fText.selectAll();
//	   fText.requestFocus();

	   fStage=4;

	}


	private void readScope(){


	    if (fScope==null){
	      boolean useFilter = true;
	      ArrayList dummy = new ArrayList();

	      String aString = TUtilities.defaultFilter(fText.getText());

	      TFormula root = new TFormula();
	      StringReader aReader = new StringReader(aString);
	     
	      boolean wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);
	      
	      if (!wellformed) {
	        String message = "The string is illformed." +
	            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

	        fText.setText(message);
	        fText.selectAll();
//	        fText.requestFocus();
	      }
	      else {
	        fScope = root;

	        testScope();
	      }
	    }


	}

	private void testScope(){

		  fScope.subTermVar(fScope,fTerm,fVariable);

	  if (! fScope.equalFormulas(fScope,fFirstline.fFormula)){

	    fTerm=null;
	    fVariable=null;
	    fScope=null;


	    String message = "That cannot be the scope of your generalization-- please start again. " +
	                     "Term to generalize on? " +
	                     "Sub term var does not give original";
	    fText.setText(message);
	    fText.selectAll();
//	    fText.requestFocus();

	    fStage=1;
	  }
	  else{

	    TFormula temp = fScope.copyFormula();

	    if (!temp.freeForTest(fTerm, fVariable)){

	      String message = fParser.writeFormulaToString(fTerm)+
	                      " for " +
	                       fParser.writeFormulaToString(fVariable)+
	                       " in " +
	                       fParser.writeFormulaToString(temp)+


	                      "leads to capture-- please start again. " +
	                      "Term to generalize on? " ;
	       fText.setText(message);
	       fText.selectAll();
//	       fText.requestFocus();

	       fStage=1;

	    }
	    else
	      goodFinish();


	  }


	}


	private void goodFinish(){

	  TFormula formulanode = new TFormula();

	  formulanode.fKind = TFormula.quantifier;
	  formulanode.fInfo = String.valueOf(chExiquant);
	  formulanode.fLLink = fVariable;
	  formulanode.fRLink = fCopy;


	                        TProofline newline = supplyProofline();

	                        int level = fModel.getHeadLastLine().fSubprooflevel;

	                        newline.fFormula = formulanode;
	                        newline.fJustification = EGJustification;
	                        newline.fFirstjustno = fFirstline.fLineno;
	                        newline.fSubprooflevel = level;

	                        TUndoableProofEdit newEdit = new TUndoableProofEdit();
	                        newEdit.fNewLines.add(newline);
	                        newEdit.doEdit();

	                        removeInputPanel();



	          }


	private void alterCopy (TFormula termPart, TFormula variable){

	  termPart.fKind=TFormula.variable;     /*surgery*/
	  termPart.fInfo=variable.fInfo;
	  termPart.fRLink=null;       // need this to get rid of any subterms

	}

	private void removeMarker(boolean alterCopy){
	  /* {removes marker and alters copy if needed}                 */

	   fCurrentNode.fInfo=fCurrentNode.fInfo.substring(1);  // omits the marker which is leading

	   if (alterCopy){
	     fCurrentCopyNode.fKind = TFormula.variable;
	     fCurrentCopyNode.fInfo = fVariable.fInfo; // (*surgery*)

	     fCurrentCopyNode.fRLink = null;  // important becuase there might be the rest of a term there


	   }



	}


	          private void findTerm(){
	    String message;


	    String aString = TUtilities.defaultFilter(fText.getText());

	            TFormula term = new TFormula();
	            StringReader aReader = new StringReader(aString);
	            boolean wellformed = false;

	            wellformed = fParser.term(term, aReader);

	            if (!wellformed) {
	              message = "The string is not a term." +
	                  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

	              //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

	              fText.setText(message);
	              fText.selectAll();
//	              fText.requestFocus();
	            }

	            else {
	              fTerm = term; // term found, end of first stage

	              message = "Variable to quantify with?";
	              fText.setText(message);
	              fText.selectAll();
//	              fText.requestFocus();

	              fStage=2;

	//              ((TGWTProofInputPanel)fInputPanel).setLabel1("Doing EG-- Stage2, identifying variable");
	              fGWTInputPanel.setLabel1("Doing EG-- Stage2, identifying variable");
	              
	              
	            }
	  }

	 private void findVariable(){
	   String aString;
	   String message;

	   aString = TUtilities.defaultFilter(fText.getText());

	   if ((aString==null)||
	     (aString.length()!=1)||
	     !fParser.isVariable(aString.charAt(0))){

	     message = aString +
	         " is not a variable. " +
	         "Variable to quantify with?";

	     fText.setText(message);
	     fText.selectAll();
//	     fText.requestFocus();
	   }
	   else { // variable found, end of second stage

	     fVariable = new TFormula();

	     fVariable.fKind = TFormula.variable;
	     fVariable.fInfo = aString;

	     fNumOccurrences = (fFirstline.fFormula).numOfFreeOccurrences(fTerm);



	   /*  if ((fNumOccurrences ==0)||
	        (fNumOccurrences ==1) ){  */

	      if (fNumOccurrences ==0){

	    /*    if (fNumOccurrences ==1) {
	          TFormula surgeryTerm = fCopy.nthFreeOccurence(fTerm, 1);

	          if (surgeryTerm != null)
	            alterCopy(surgeryTerm, fVariable);
	           }  */


	//        ((TProofInputPanel)fInputPane).setLabel1("Doing EG-- Stage4,"+
	    	  fGWTInputPanel.setLabel1("Doing EG-- Stage4,"+
	            " displaying scope. " +
	            "If suitable, press Go.");


	     message= fParser.writeFormulaToString(fCopy);

	   fText.setText(message);
	   fText.selectAll();
//	   fText.requestFocus();

	   fStage=4;
	     }
	     else{
	       if (fNumOccurrences >0) {  //used to be 1


//	         ((TProofInputPanel)fInputPane).setLabel1("Doing EG-- Stage3,"+
	    	   fGWTInputPanel.setLabel1("Doing EG-- Stage3,"+
	            " Occurrences. " +
	            "Generalize on this one?");

	         fTerms = new TFormula[fNumOccurrences];

	         for (int i=0;i<fNumOccurrences;i++){             // initialize

	           fTerms[i] = fCopy.nthFreeOccurence(fTerm, i + 1);   // one uses zero based index, other 1 based
	         }



	         fTerms[0].fInfo= chInsertMarker+ fTerms[0].fInfo;


	          /********* going to yes/no subroutine *****/

	          boolean yes=true;

	//       JButton yesButton = new JButton(new EGYesNoAction(this,yes/*text,"Go", firstline*/));
	//       JButton noButton = new JButton(new EGYesNoAction(this,!yes/*text,"Go", firstline*/));

           Button yesButton = new Button("Yes");
           yesButton.addClickHandler(new EGYesNoHandler(this,yes));
           Button noButton = new Button("No");
          noButton.addClickHandler(new EGYesNoHandler(this,!yes));		   
        		   
           
	          

	       message= fParser.writeFormulaToString(fCopy);

	      //JTextField text = new JTextField(message);

	      fText.setText(message);

	      Button[]buttons = {noButton, yesButton };  // put cancel on left
	      TGWTProofInputPanel inputPane = new TGWTProofInputPanel("Doing EG-- Stage3, generalize on this occurrence?", fText, buttons);


	      addInputPane(inputPane,SELECT);



	//fText.setText(message);
	//fText.selectAll();
	//fText.requestFocus();





	 //     inputPane.getRootPane().setDefaultButton(defaultButton);
//	            fInputPane.setVisible(true); // need this
//	            fText.setEditable(false);
//	           fText.requestFocus();         // so selected text shows
//







	     //


	         message= fParser.writeFormulaToString(fCopy);

	fText.setText(message);
	fText.selectAll();
//	fText.requestFocus();

	fStage=4;


	       }

	     }

	  }

	 }



	      }


	
 
 
 public void doEG(){

	 /*

	  {This is quite complicated because generalization is done on individual occurrences of a }
	              {term.  I take a copy.  Then I insert and remove markers in the}
	              {original and display it and alter the copy if the user indicates.}

	 */

	   TProofline firstline;
	   Button defaultButton;
	  // JButton dropLastButton;
	   TGWTProofInputPanel inputPane;

	   if (fTemplate)
	     doHintEG();
	   else{
	      firstline=fDisplayCellTable.oneSelected();

	      if (firstline != null) {

	      //   JTextField text = new JTextField("Term to generalize on?");
	     //    text.selectAll();
	         
	         TextBox text = new TextBox();
	         text.setText("Term to generalize on?");
	         text.selectAll();

	   //   defaultButton = new JButton(new EGAction(text,"Go", firstline));

	      defaultButton = new Button("Go");
	      defaultButton.addClickHandler(new EGHandler(text,firstline));	      
	      
	      Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	      inputPane = new TGWTProofInputPanel("Doing EG-- Stage1, identifying term", text, buttons);


	      addInputPane(inputPane,SELECT);




//	      inputPane.getRootPane().setDefaultButton(defaultButton);
//	      fInputPane.setVisible(true); // need this
//	      text.requestFocus();         // so selected text shows


	    }

	   }



	 }




	 /*

	   function TProofWindow.DoEG: TCommand;

	    var

	     firstline, newline: TProofline;
	     aLineCommand: TLineCommand;
	     firstformula, formulanode, root, copyf, currentNode, currentCopyNode, termForm, variForm: TFormula;
	     outputStr, prompt: str255;
	     cancel, found, done: boolean;
	     occurences, metSoFar, i: integer;

	            {This is quite complicated because generalization is done on individual occurrences of a }
	            {term.  I take a copy.  Then I insert and remove markers in the}
	            {original and display it and alter the copy if the user indicates.}

	                  (*dont need current index*)

	    procedure RemoveMarker (yes: boolean);
	       {removes marker and alters copy if needed}

	    begin
	     delete(currentNode.fInfo, 1, 1); {removes marker}
	     if yes then
	      begin
	       currentCopyNode.fKind := variable;
	       currentCopyNode.fInfo := variForm.fInfo;  (*surgery*)
	       if (currentCopyNode.fRlink <> nil) then
	        currentCopyNode.fRlink.DismantleFormula;
	       currentCopyNode.fRlink := nil;
	      end;

	    end;

	   begin

	    DoEG := gNoChanges;
	    cancel := FALSE;
	    found := FALSE;
	    prompt := strNull;
	    outputStr := strNull;

	    if fTemplate then {gTemplate}
	     DoEG := DoHintEG
	    else
	     begin

	      if fTextList.OneSelected(firstline) then
	       begin

	        GetIndString(prompt, kStringRSRCID, 19); { Term }

	        GetTheTerm(strNull, strNull, prompt, termForm, cancel);

	        outputStr := strNull;
	        prompt := strNull;
	        found := FALSE;

	        if not cancel then
	         repeat
	         begin

	         GetIndString(prompt, kStringRSRCID, 20); { Variable}

	         prompt := concat(outputStr, prompt);

	         if not GetTheChoice(strNull, strNull, prompt) then
	         cancel := TRUE
	         else if length(prompt) = 1 then
	         begin
	         if prompt[1] in gVariables then
	         begin
	         found := TRUE;

	         SupplyFormula(variForm);
	         with variForm do
	         begin
	         fKind := variable;
	         fInfo := prompt[1];
	         end;

	         end
	         else
	         outputStr := concat(prompt, ' is not a variable.');
	         end;

	         end;

	         until found or cancel;

	        outputStr := strNull;
	        prompt := strNull;

	        if not cancel then
	         repeat
	         begin
	         found := FALSE;

	         occurences := firstline.fFormula.NumofFreeOccurences(termForm);

	         copyf := firstline.fFormula.CopyFormula;

	         firstformula := firstline.fFormula;

	         if occurences = 0 then

	         begin

	         prompt := 'Press Go to display the scope. Then Press Go again, (or Cancel or edit it).';

	         if not GetTheChoice(strNull, strNull, prompt) then
	         cancel := TRUE;

	         end;

	         if occurences = 1 then {this is a silly way of finding the one occurence}
	  {						                                      to alter the copy.}
	         begin
	         currentNode := nil;
	         currentCopyNode := nil;
	         metSoFar := 0;
	         done := FALSE;

	         NewInsertMarker(termForm, 1, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);
	         RemoveMarker(TRUE);

	         prompt := 'Press Go to display the scope. Then Press Go again, (or Cancel or edit it).';

	         if not GetTheChoice(strNull, strNull, prompt) then
	         cancel := TRUE;

	         end;


	         if occurences > 1 then
	         begin
	         i := 1;

	         while (i <= occurences) and not cancel do
	         begin
	         currentNode := nil;
	         currentCopyNode := nil;

	         metSoFar := 0;

	         done := FALSE;

	         NewInsertMarker(termForm, i, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);
	         firstline.fFormula := firstformula;
	         fTextList.InvalidateItem(fTextList.FirstSelectedItem);

	         prompt := 'Do you wish to generalize on the arrowed occurrence?';

	         if i = occurences then
	         prompt := 'After this choice, which is the last, the scope will be displayed. Press Go again (or edit it).';

	         if not GetTheChoice('Yes?', 'No?', prompt) then
	         cancel := TRUE;
	         RemoveMarker(fRadio);
	         i := i + 1;

	         end;

	         end;

	         firstline.fFormula := firstformula;

	         fTextList.InvalidateItem(fTextList.FirstSelectedItem);

	         if cancel then
	         copyf.DismantleFormula
	         else
	         begin

	                                     {****}

	                                     {  GetIndString(prompt, kStringRSRCID, 21);  }

	         fParser.WriteFormulaToString(copyf, outPutStr);


	         copyf.DismantleFormula;

	         prompt := outputStr;

	         GetTheRoot(strNull, strNull, prompt, root, cancel);

	         if not cancel then
	         begin
	         copyf := root.CopyFormula;
	         NewSubTermVar(root, termForm, variForm);

	         if not Equalformulas(root, firstline.fFormula) then
	         begin
	         cancel := true;
	         BugAlert('This cannot be the scope of your formula-- please start again.')
	         end
	         else
	         begin
	         root.DismantleFormula;
	         root := copyf.CopyFormula;
	         if not root.FreeForTest(termForm, variForm) then {}
	         begin

	         fParser.WriteFormulaToString(root, outPutStr);

	         outputStr := concat(' in ', outputStr, '.');

	         fParser.WriteTermToString(termForm, prompt);

	         BugAlert(concat(prompt, ' for ', variForm.fInfo, outputStr, ' leads to capture.'));

	         root.DismantleFormula; {check}
	         copyf.DismantleFormula;
	         variForm.DismantleFormula;
	         end
	         else
	         found := TRUE;
	         end;
	         end;
	         end;
	         end;

	         until found or cancel;

	        if not cancel then
	         begin

	         SupplyFormula(formulanode);
	         with formulanode do
	         begin
	         fKind := quantifier;
	         fInfo := chExiquant;
	         fLlink := variForm;
	         fRlink := copyf;
	         end;

	         New(aLineCommand);
	         FailNil(aLineCommand);
	         aLineCommand.ILineCommand(cAddLine, SELF);

	         SupplyProofline(newline);
	         with newline do
	         begin
	         fFormula := formulanode;
	         ffirstjustno := firstline.fLineno;
	         fJustification := ' EG';
	         fSubprooflevel := TProofline(SELF.fHead.Last).fSubprooflevel;
	         end;

	         aLineCommand.fNewlines.InsertLast(newline);
	         newline := nil;
	         DoEG := aLineCommand;

	         end;
	       end;
	     end;
	   end;


	 */
 
 /************************ Rule of EI ****************************/


 void doHintEI(){   //menu enabled only if existential selected and conclusion exists



   TProofline selection = fDisplayCellTable.oneSelected();
   TFormula selectedFormula=selection.fFormula;

   TFormula conclusion =findNextConclusion();

    TFormula variForm = selectedFormula.quantVarForm();
    TFormula scope = selectedFormula.scope();

    TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);

    if (freeFormula!=null){                              //free in premises
      String message = "Not permitted "
          + fParser.writeFormulaToString(variForm)
          + " is free in "
          + fParser.writeFormulaToString(freeFormula);
      bugAlert("Doing"+fEIJustification+" with Tactics." +   " Warning.",message);
    }
    else{

      if (conclusion.freeTest(variForm)) { //free in B}

        String outPutStr = fParser.writeFormulaToString(conclusion);

        //  BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

        bugAlert("Doing"+fEIJustification+" with Tactics." +   " Warning.",
                 fParser.writeFormulaToString(variForm) +
                 " is free in " +
                 outPutStr +
                 ".");

      }
      else {                     // everything ok

        TUndoableProofEdit newEdit = new TUndoableProofEdit();

        TProofline headLastLine = fModel.getHeadLastLine();



        int level = headLastLine.fSubprooflevel;
        int lastlineno = headLastLine.fLineno;

        TProofline newline = supplyProofline();


        newline.fFormula=scope.copyFormula();
    newline.fJustification= fAssJustification;
    newline.fSubprooflevel= level+1;
    newline.fLastassumption=true;

    newEdit.fNewLines.add(newline);

    newline = supplyProofline();

        int conclusionlineno = addIfNotThere(conclusion, level+1, newEdit.fNewLines);

        if (conclusionlineno == -1) { // not there
          conclusionlineno = lastlineno + 3;   // the assumption and the ?
          lastlineno += 3;
        }

        newEdit.fNewLines.add(endSubProof(level+1));

        newEdit.fNewLines.add(addExTarget(conclusion, level, selection.fLineno,conclusionlineno));

        newEdit.doEdit();

      }
    }


 }

 TProofline addExTarget (TFormula targetroot, int level, int one,int two){
	  TProofline newline=supplyProofline();

	     newline.fSubprooflevel = level;
	     newline.fFormula = targetroot.copyFormula();
	     newline.fFirstjustno = one;
	     newline.fSecondjustno = two;
	     newline.fJustification = fEIJustification;

	     return
	         newline;

	} 

 void doEI(){
    TFormula variForm, scope;

    TProofline subhead,subtail;

   if (fTemplate)
     doHintEI();
   else{

     TProofline firstline;
     TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

      if (selections != null){

        firstline = selections[0];
        subtail = selections[1];

        if (fParser.isExiquant(firstline.fFormula)) {

          variForm = firstline.fFormula.quantVarForm();
          scope = firstline.fFormula.scope();

          subhead = fModel.findLastAssumption();

          if (subhead != null) {

            if (scope.equalFormulas(scope, subhead.fFormula)) {
              if (subtail.fFormula.freeTest(variForm)) { //free in B}

                String outPutStr = fParser.writeFormulaToString(subtail.fFormula);

                //  BugAlert(concat(firstline.fFormula.QuantVarForm.fInfo, ' is free in ', outputStr, '.'));

                bugAlert("DoingEI. Warning.", firstline.fFormula.quantVar() +
                         " is free in " +
                         outPutStr +
                         ".");

              }
              else {                                         // looking for free in premises

                // test for free

             TFormula freeFormula=fModel.firstAssumptionWithVariableFree(variForm);  //check this, pasted from UG

             /* now, it is allowed to be free in the assumption of the instantion, so .... */

             if ((freeFormula!=null)&&freeFormula.equalFormulas(freeFormula,subhead.fFormula))
               freeFormula=null;   // this one does not count

             if (freeFormula!=null){
               bugAlert("DoingEI. Warning.", firstline.fFormula.quantVar() +
                        " is free in " +
                        fParser.writeFormulaToString(freeFormula) +
                        ".");
             }
             else {

                int level=fModel.getHeadLastLine().fSubprooflevel;

                TUndoableProofEdit  newEdit = new TUndoableProofEdit();

                newEdit.fNewLines.add(endSubProof(level));
                newEdit.fNewLines.add(addExTarget(subtail.fFormula, level-1, firstline.fLineno,subtail.fLineno));
                newEdit.doEdit();

             }




              }
            }

          }
        }
      }






   }

 }

 /************************ Rule of DoII **********************************/



 public class IIClickHandler implements ClickHandler{
	 TextBox fText;
   TFormula fRoot=null;

  /*We just need to get the term  */


    public IIClickHandler(TextBox text){
//      putValue(NAME, label);

      fText=text;
    }

     public void onClick(ClickEvent event){

       boolean wellformed;

       TFormula term = new TFormula();

      wellformed=getTheTerm(fText,term);

      if (wellformed){
        fRoot=term;

       TProofline newline = supplyProofline();

        newline.fFormula = TFormula.equateTerms(fRoot,fRoot.copyFormula());
        newline.fJustification = IIJustification;
         newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

         TUndoableProofEdit newEdit = new TUndoableProofEdit();
         newEdit.fNewLines.add(newline);
         newEdit.doEdit();

         removeInputPanel();
       }

   }

 }

 public void doII(){
	 Button defaultButton;
//	 JButton dropLastButton;
	 TGWTProofInputPanel inputPane;


	 //JTextField text = new JTextField("Term?");

	   // text.setDragEnabled(true);
	 //   text.selectAll();
	    
	    TextBox text = new TextBox();
		 text.setText("Term?");
		 text.selectAll();

	   // defaultButton = new JButton(new IIAction(text,"Go"));
	    defaultButton = new Button("Go");
 	    defaultButton.addClickHandler(new IIClickHandler(text));

	    Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	    inputPane = new TGWTProofInputPanel("Doing Identity Introduction", text, buttons,fInputPalette);


	    addInputPane(inputPane,SELECT);

	//         inputPane.getRootPane().setDefaultButton(defaultButton);
	//         fInputPane.setVisible(true); // need this
	//         text.requestFocus();         // so selected text shows
	 }
 
 
 /************************ Rule of DoIE **********************************/


 private void getTheChoice(ClickHandler leftHandler,    //the calling actions must remove the input pane
         String leftText,
         ClickHandler rightHandler,
         String rightText,
         String heading,String prompt){

Button leftButton=new Button(leftText);
leftButton.addClickHandler(leftHandler);	
Button rightButton=new Button(rightText);
rightButton.addClickHandler(rightHandler);

TGWTProofInputPanel inputPane;

TextBox text = new TextBox();
text.setText(prompt);
text.selectAll();

/*JTextField text = new JTextField(
prompt);
text.selectAll(); */

Button[] buttons = {
cancelButton(), leftButton,rightButton};

inputPane = new TGWTProofInputPanel(heading,text, buttons);

addInputPane(inputPane,SELECT);

//fInputPane.setVisible(true); // need this
//text.requestFocus(); // so selected text shows

}


public class IEYesNoHandler implements ClickHandler{

  IEHandler fParent;
  boolean fYes;
  TFormula fSubstitution;

  public IEYesNoHandler(IEHandler parent,boolean yes,TFormula substitution){

/*    if (yes)
      putValue(NAME, "Yes");
    else
      putValue(NAME, "No"); */

    fParent=parent;
    fYes=yes;
    fSubstitution=substitution; 

  }

  public void onClick(ClickEvent event){

     TFormula surgeryTerm;

    if (fParent.fNumTreated<fParent.fNumToTreat){

       surgeryTerm= fParent.fTermsToTreat[fParent.fNumTreated];

       surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


       if (fYes){

         /* The surgery term might be a, f(a), f(g(a,b)) etc, and so too might be the term that is to
         be substituted, fSubstitution. We just copy everything across*/


          surgeryTerm.fKind = fSubstitution.getKind();
          surgeryTerm.fInfo = fSubstitution.getInfo(); // (*surgery*)
          if (fSubstitution.getLLink() == null)
            surgeryTerm.fLLink=null;
          else
            surgeryTerm.fLLink=fSubstitution.getLLink().copyFormula();;  // should be no left link
          if (fSubstitution.getRLink() == null)
            surgeryTerm.fRLink=null;
          else
            surgeryTerm.fRLink=fSubstitution.getRLink().copyFormula();;  // important becuase there might be the rest of a term there
       }

      // if they have pressed the No button, fYes is false and we do nothing

      fParent.fNumTreated+=1;

  }

    if (fParent.fNumTreated<fParent.fNumToTreat){
                  // put the marker in the next one

      fParent.fTermsToTreat[fParent.fNumTreated].fInfo= chInsertMarker+
                                                   fParent.fTermsToTreat[fParent.fNumTreated].fInfo;


        String message= fParser.writeFormulaToString(fParent.fCopy);


        fParent.fText.setText(message);

//        fParent.fText.requestFocus();

    }
    else{                                        //  last one, return to parent

//     JButton defaultButton = new JButton(fParent);
    	
	      Button defaultButton=new Button("Go");
	/*      if (fYes)
	    	  defaultButton = new Button("Yes");
	      else
	    	  defaultButton = new Button("No"); */
	      defaultButton.addClickHandler(fParent);

	      Button[] buttons = {cancelButton(), defaultButton };  // put cancel on left
	      TGWTProofInputPanel inputPane = new TGWTProofInputPanel("Doing IE-- Stage3,"+
           " displaying result. " +
           "If suitable, press Go.", fParent.fText, buttons);

//     JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
//     TProofInputPanel inputPane = new TProofInputPanel("Doing IE-- Stage3,"+
//           " displaying result. " +
//           "If suitable, press Go.", fParent.fText, buttons);


     addInputPane(inputPane,SELECT);

     String message= fParser.writeFormulaToString(fParent.fCopy);

//      fParent.fText.setEditable(true);
      fParent.fText.setText(message);
      fParent.fText.selectAll();

 //    inputPane.getRootPane().setDefaultButton(defaultButton);
 //    fInputPane.setVisible(true); // need this
 //    fParent.fText.requestFocus();         // so selected text shows

  fParent.fStage+=1;  // 3 I think

  if (fParent.fStage==3)
    fParent.askAboutGamma();  //I think
  else
    fParent.displayResult();


    }

  }

}

  public class IEHandler implements ClickHandler{


//JTextField fText;
TextBox fText;


 TProofline fFirstline=null;
 TProofline fSecondline=null;
 TFormula fAlpha=null, fGamma=null, fScope=null, fCopy=null,
     fCurrentNode=null,fCurrentCopyNode=null;
 int fNumAlpha=0; //of term alpha
 int fNumGamma=0; //of term gamma
// int fNumAlphaTreated=0;
 int fStage=1;
 TFormula.MarkerData markerData;

 private TFormula [] fAlphas; // the occurrences of alpha in the (copy of) original formula
 TFormula [] fGammas; // the occurrences of gamma in the (copy of) original formula

 TFormula [] fTermsToTreat;
 int fNumTreated=0;
 int fNumToTreat=0;

 boolean useFilter=true;

 boolean fAlphaOnly=false;
 boolean fGammaOnly=false;

/*We only have to run through the occurrences seeing which ones they want to subs in */


  public IEHandler(TextBox text, TProofline firstline,TProofline secondline){
//    putValue(NAME, label);

    fText = text;
    fFirstline = firstline;
    fSecondline = secondline;

    fAlpha = fSecondline.getFormula().firstTerm(); // alpha=gamma
    fGamma = fSecondline.getFormula().secondTerm();

    fCopy = fFirstline.fFormula.copyFormula(); //??

/*    fNumAlpha = (fFirstline.fFormula).numInPredOrTerm(fAlpha);

Aug 06 There is a change here that needs explaining. In the original Deriver one could substitute into
compound formulas eg a=b Fa^Ga to get Fb^Gb. But I must have been nervous when first coding this. And restricted
the substitution to atomic formulas only (you could do compounds by taking them apart and putting them
back together).

Colin has asked me to revert to compound substitution. Hence the change


*/
    fNumAlpha = (fFirstline.fFormula).numOfFreeOccurrences(fAlpha);

    if (fNumAlpha > 0) {
      fAlphas = new TFormula[fNumAlpha];    // create an array of the actual terms in the copy that we will do surgery on

      for (int i = 0; i < fNumAlpha; i++) { // initialize
        fAlphas[i] = fCopy.depthFirstNthOccurence(fAlpha, i + 1); // one uses zero based index, other 1 based
      }
    }

  //  fNumGamma = (fFirstline.fFormula).numInPredOrTerm(fGamma);

  fNumGamma = (fFirstline.fFormula).numOfFreeOccurrences(fGamma);

    if (fNumGamma > 0) {
      fGammas = new TFormula[fNumGamma];    // create an array of the actual terms in the copy that we will do surgery on

      for (int i = 0; i < fNumGamma; i++) { // initialize
        fGammas[i] = fCopy.depthFirstNthOccurence(fGamma, i + 1); // one uses zero based index, other 1 based
      }
    }
  }


public void start(){
 fStage=1;
// actionPerformed(null);
 onClick(null);
}


   public void onClick(ClickEvent event){


     switch (fStage){


       case 1:
         subFormCheck();
         break;

       case 2:
         askAboutAlpha();
         break;

       case 3:
         askAboutGamma();
         break;

       case 4:
         displayResult();
         break;

       case 5:
         readResult();
         break;

       default: ;
     }
     }



     /*
            function SubFormCheck: boolean;
              var
               temp: boolean;
             begin
           {There is a potential problem here that we must look out for. If one term in the identity}
           {is a subterm of the other, eg y=f(y) and both occur in the original formula eg Gf(y)}
           {the we should ask whether substitution for y or f(y) is required and not permit both}

              temp := ((gammaForm.SubFormulaOccursInFormula(gammaForm, alphaForm) | gammaForm.SubFormulaOccursInFormula(alphaForm, gammaForm)) & (firstline.fFormula.NumofFreeOccurences(alphaForm) <> 0) & (firstline.fFormula.NumofFreeOccurences(gammaForm) <> 0));

              if temp then
               begin
                outPutStr := strNull;
                fParser.WriteFormulaToString(alphaForm, outPutStr);
                prompt := concat('Do you wish to substitute for ', outputStr, ' ?');

                if not GetTheChoice('Yes?', 'Other term?', prompt) then
                 cancel := TRUE;
           {This sets fRadio}
                useFirstTerm := fRadio;
               end;{3}

              SubFormCheck := temp;
             end;

     */

void subFormCheck(){    // YOU NEED TO WRITE THIS JAN 06

     /* In the Pascal I worried

           {There is a potential problem here that we must look out for. If one term in the identity}
               {is a subterm of the other, eg y=f(y) and both occur in the original formula eg Gf(y)}
               {the we should ask whether substitution for y or f(y) is required and not permit both}

     The terms 'overlap' so we cannot ask about one then the other. The User must choose

       */

boolean flag =(fAlpha.numInPredOrTerm(fGamma)!=0)
          ||(fGamma.numInPredOrTerm(fAlpha)!=0);

if (flag){
 String outputStr="Do you wish to substitute for "
     +fParser.writeFormulaToString(fAlpha) +"?";


ClickHandler noHandler= new ClickHandler(){
	public void onClick(ClickEvent event){
		fGammaOnly=true;
        removeInputPanel();
        fStage = 3;  // go straight to askGamma and miss alpha
        askAboutGamma();		
}};

ClickHandler yesHandler= new ClickHandler(){
	public void onClick(ClickEvent event){
		fAlphaOnly=true;
        removeInputPanel();
        fStage = 2;
        askAboutAlpha();	
}};

getTheChoice(noHandler, "No",           //the calling actions must remove the input pane
             yesHandler, "Yes",
"One term is a subterm of the other, just treat one at a time",
outputStr); 
 
 /*getTheChoice(new AbstractAction("No")
                {public void actionPerformed(ActionEvent ae){
                  fGammaOnly=true;
                  removeInputPane();
                  fStage = 3;  // go straight to askGamma and miss alpha
                  askAboutGamma();
                }},           //the calling actions must remove the input pane

              new AbstractAction("Yes")
                {public void actionPerformed(ActionEvent ae){
                  fAlphaOnly=true;
                  removeInputPane();
                  fStage = 2;
                  askAboutAlpha();
                }},
              "One term is a subterm of the other, just treat one at a time",
              outputStr);  */

}
else{
 fStage = 2;
// actionPerformed(null);
 onClick(null);
}
}


 void alphaByGamma(){

   int occurences =fFirstline.getFormula().numOfFreeOccurrences(fAlpha);

 }


/*

     replacementForm := gammaForm; {replacing alpha by gamma}

            occurences := firstline.fFormula.NumofFreeOccurences(alphaForm);

                                             {***}

            copyf := firstline.fFormula.CopyFormula;

            firstformula := firstline.fFormula;

            if (occurences > 0) & (not subformulaRewrite | useFirstTerm) then
            begin {7}
            i := 1;

            while (i <= occurences) and not cancel do
            begin {8}
            currentNode := nil;
            currentCopyNode := nil;

            metSoFar := 0;

            done := FALSE;

            NewInsertMarker(alphaForm, i, metSoFar, firstformula, copyf, done, currentNode, currentCopyNode);

            firstline.fFormula := firstformula;
            if subsinfirst then
            fTextList.InvalidateItem(fTextList.FirstSelectedItem)
            else
            fTextList.InvalidateItem(fTextList.LastSelectedItem);

            prompt := 'Do you wish to replace the arrowed occurrence?';


            if not GetTheChoice('Yes?', 'No?', prompt) then
            cancel := TRUE;
            RemoveMarker(fRadio);
            i := i + 1;

            end; {8}

            end; {7}


            firstline.fFormula := firstformula;

            if subsinfirst then
            fTextList.InvalidateItem(fTextList.FirstSelectedItem)
            else
            fTextList.InvalidateItem(fTextList.LastSelectedItem);



 */



private void displayResult(){

String message= fParser.writeFormulaToString(fCopy);

//to do fText.setEditable(false);  // we don't want them changing it
fText.setText(message);
fText.selectAll();
//fText.requestFocus();

fStage=5;

}


private void readResult(){


if (fScope==null){
 boolean useFilter = true;
 ArrayList dummy = new ArrayList();

 String aString = TUtilities.defaultFilter(fText.getText());

 TFormula root = new TFormula();
 StringReader aReader = new StringReader(aString);
 boolean wellformed;

 wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);  // it can never be illformed since we put a well formed one there

 if (!wellformed) {
   String message = "The string is illformed." +
       (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

   fText.setText(message);
   fText.selectAll();
//   fText.requestFocus();
 }
 else {
   fScope = root;

   goodFinish();
 }
}
}


private void goodFinish(){



                   TProofline newline = supplyProofline();

                   int level = fModel.getHeadLastLine().fSubprooflevel;

                   newline.fFormula = fCopy;
                   newline.fJustification = IEJustification;
                   newline.fFirstjustno = fFirstline.fLineno;
                   newline.fSecondjustno = fSecondline.fLineno;
                   newline.fSubprooflevel = level;

                   TUndoableProofEdit newEdit = new TUndoableProofEdit();
                   newEdit.fNewLines.add(newline);
                   newEdit.doEdit();

                   removeInputPanel();
     }



/*private void askAboutGamma(){
fStage=3;
displayResult();             //TEMP
}*/

private void askAboutAlpha(){
 String aString;
 String message;

 if (fGammaOnly||fNumAlpha == 0) { // we just go on to gamma
   fStage = 3;
   askAboutGamma();
 }

 else {
   if (fNumAlpha > 0) {

/*     ( (TProofInputPanel) fInputPane).fLabel.setText("Doing IE-- Stage1," + //they never see this
         " Occurrences of first term. " +
         "Substitute for this one?");  */

     fAlphas[0].fInfo = chInsertMarker + fAlphas[0].fInfo;
     fTermsToTreat = fAlphas;
     fNumTreated = 0;
     fNumToTreat = fNumAlpha;

     /********* going to yes/no subroutine *****/

     boolean yes = true;

   //  JButton yesButton = new JButton(new IEYesNoAction(this, yes, fGamma));
   //  JButton noButton = new JButton(new IEYesNoAction(this, !yes, fGamma));

     Button yesButton = new Button("Yes");
     yesButton.addClickHandler(new IEYesNoHandler(this,yes,fGamma));
     Button noButton = new Button("No");
     noButton.addClickHandler(new IEYesNoHandler(this,!yes,fGamma));	
     
     message = fParser.writeFormulaToString(fCopy);

     //JTextField text = new JTextField(message);

     fText.setText(message);

     Button[] buttons = {
         noButton, yesButton}; // put cancel on left
     TGWTProofInputPanel inputPane = new TGWTProofInputPanel(
         "Doing =E-- Stage1, substitute for this occurrence of left term?",
         fText, buttons);

     addInputPane(inputPane,SELECT);

  //   fInputPane.setVisible(true); // need this
 //    fText.setEditable(false);
 //    fText.requestFocus(); // so selected text shows

     message = fParser.writeFormulaToString(fCopy);

     fText.setText(message);
     fText.selectAll();
//     fText.requestFocus();

   //  fStage = 3; // 3 probably, or 2  // the yes/no sets this

   }
 }
}

private void askAboutGamma(){
 String aString;
 String message;


  if (fAlphaOnly||fNumGamma ==0){       // we just go on to display
    fStage=4;
    displayResult();
  }

  else{
    if (fNumGamma >0) {


 /*     ((TProofInputPanel)fInputPane).fLabel.setText("Doing IE-- Stage2,"+    //they never see this
         " Occurrences of second term. " +
         "Substitute for this one?");  */

      fGammas[0].fInfo= chInsertMarker+ fGammas[0].fInfo;
      fTermsToTreat=fGammas;
      fNumTreated=0;
      fNumToTreat=fNumGamma;


       /********* going to yes/no subroutine *****/

       boolean yes=true;

 //   JButton yesButton = new JButton(new IEYesNoAction(this,yes,fAlpha));
//    JButton noButton = new JButton(new IEYesNoAction(this,!yes,fAlpha));

    Button yesButton = new Button("Yes");
    yesButton.addClickHandler(new IEYesNoHandler(this,yes,fAlpha));
    Button noButton = new Button("No");
   noButton.addClickHandler(new IEYesNoHandler(this,!yes,fAlpha));	

    message= fParser.writeFormulaToString(fCopy);

   //JTextField text = new JTextField(message);

   fText.setText(message);

   Button[]buttons = {noButton, yesButton };  // put cancel on left
   TGWTProofInputPanel inputPane = new TGWTProofInputPanel("Doing =E-- Stage2, substitute for this occurrence of right term?", fText, buttons);


   addInputPane(inputPane,SELECT);

  //       fInputPane.setVisible(true); // need this
 //        fText.setEditable(false);
//        fText.requestFocus();         // so selected text shows

  message= fParser.writeFormulaToString(fCopy);

 fText.setText(message);
 fText.selectAll();
 //fText.requestFocus();

 fStage=4;  // 3 probably, or 2

    }
  }

}


}
  
  String capturePossible(TFormula alpha, TFormula gamma, TFormula firstLineFormula){
 	  String capturable="";
 	  
 	  
 	  Set <String> atomicTermsInIdentity =alpha.atomicTermsInFormula();
 	      
 	  if (atomicTermsInIdentity.addAll(gamma.atomicTermsInFormula()))
 		  ;
 	
 	  String boundVars=firstLineFormula.boundVariablesInFormula();
 	  String search;

 	  for (int i=0;i<boundVars.length();i++){
 	    search=boundVars.substring(i,i+1);

 	    if (atomicTermsInIdentity.contains(search)){
 	      capturable=search;
 	      break;
 	    }

 	  }
 	  return
 	      capturable;
 	}

  void launchIEAction(TProofline firstline,TProofline secondline){


	  /*
	        if CapturePossible then
	                   begin
	                   outPutStr := strNull;
	                   fParser.WriteFormulaToString(firstline.fFormula, outPutStr);
	                   BugAlert(concat('The variable ', chStr, ' occurs in the identity and is bound in ', outPutStr, ' . '));
	                   end
	                   else
	                   begin


	    */

	  TFormula alpha=secondline.getFormula().firstTerm();
	  TFormula gamma=secondline.getFormula().secondTerm();

	  String captured=capturePossible(alpha,   // alpha=gamma
	                                  gamma,
	                                  firstline.getFormula());


	  if (!captured.equals("")){

	    bugAlert("Problems with free and bound variables (remedy: rewrite bound variable)",
	        "The variable "+ captured + " occurs in the identity and is bound in "
	        + fParser.writeFormulaToString(firstline.getFormula()));

	  }
	  else{


	       // now we want to move into the substiuting bit



//	       JTextField text = new JTextField("Starting =E"); ////// HERE
		   TextBox text = new TextBox();
		   text.setText("Starting =E");
	       text.selectAll();

	       IEHandler launchHandler =new IEHandler(text,/* "Go",*/ firstline,
	           secondline);

//	       JButton defaultButton = new JButton(launchAction);
	       Button defaultButton = new Button("Go");
           defaultButton.addClickHandler(launchHandler);

	       Button[] buttons = {
	           cancelButton(), defaultButton}; // put cancel on left
	       TGWTProofInputPanel inputPane = new TGWTProofInputPanel(
	           "Doing Identity Elimination", text, buttons);

	       addInputPane(inputPane,SELECT);

//	       inputPane.getRootPane().setDefaultButton(defaultButton);
//	       fInputPane.setVisible(true); // need this
//	       text.requestFocus(); // so selected text shows  */

	       launchHandler.start();
	     }
	  }

  class FirstSecondHandler implements ClickHandler{
	  boolean fFirst=true;
	  TProofline fFirstline;
	  TProofline fSecondline;

	  FirstSecondHandler(boolean isFirst,TProofline firstline, TProofline secondline){
/*	    if (isFirst)
	       putValue(NAME, "First");
	     else
	       putValue(NAME, "Second"); */



	     fFirst=isFirst;
	     fFirstline=firstline;
	     fSecondline=secondline;
	  }

	  public void onClick(ClickEvent event){

	    if (!fFirst){       // if they want to subs in first, fine; otherwise we have to swap
	      TProofline temp = fFirstline;
	      fFirstline = fSecondline;
	      fSecondline = temp; // now the secondline is the identity
	    }


	    removeInputPanel();

	    launchIEAction(fFirstline,fSecondline);

	  }

	}
  
  
  private void orderForSwap(TProofline firstline, TProofline secondline){
	  /*{this determines which we are going to subs in-- they could both be identities}
	  // this launches or puts up a prelim dialog which itself launches
	  we want the identity as the second line and the formula it is substituted in as the first line */

	     int dispatcher=0;
	     int inFirst=0;
	     int inSecond=0;

	     if (fParser.isEquality(firstline.getFormula()))
	       inSecond = (secondline.getFormula()).numOfFreeOccurrences(firstline.getFormula().firstTerm()) +
	             (secondline.getFormula()).numOfFreeOccurrences(firstline.getFormula().secondTerm());

	     if (fParser.isEquality(secondline.getFormula()))
	       inFirst = (firstline.getFormula()).numOfFreeOccurrences(secondline.getFormula().firstTerm()) +
	             (firstline.getFormula()).numOfFreeOccurrences(secondline.getFormula().secondTerm());

	     if ((inFirst+inSecond)==0)
	       return;                  //if neither appears in the other no substitution is possible


	     if (fParser.isEquality(firstline.getFormula())){
	       if (!fParser.isEquality(secondline.getFormula()))
	         dispatcher=2;
	       else
	         dispatcher=3;    // both
	     }
	     else
	       dispatcher=1;     //first not, second is

	     switch (dispatcher){
	       case 0: break;   // neither an identity cannot happen because orderForSwap called only if at least one is
	       case 1:          // what we want first not identity second is
	         launchIEAction(firstline,secondline);
	         break;
	       case 2: {        // wrong way round so we swap
	         TProofline temp=firstline;
	         firstline=secondline;
	         secondline=temp;  // now the secondline is the identity
	         launchIEAction(firstline,secondline);
	         break;
	       }
	       case 3: {               // both identities

	         /*{now, if neither of the second terms appear in the first, we want to subs in the second}
	            {if neeither of the first terms appear in the second, we want to subs in the first}
	          {otherwise we have to ask} Don't fully understand the logic of this Jan06
	          oh, I suppose it is this a=b and f(a)=c, can only subs in second etc.*/

	         if (inFirst == 0) {
	           TProofline temp = firstline;
	           firstline = secondline;
	           secondline = temp; // now the secondline is the identity
	           launchIEAction(firstline,secondline);
	         }
	         else {
	           if (inSecond == 0) { // leave them as they are, both identities some in first none in second
	             launchIEAction(firstline,secondline);
	           }
	           else { // we ask

	             TGWTProofInputPanel inputPane;
	  //           JTextField text = new JTextField(
	   //              "Do you wish to substitute in the first or in the second?");

	          //   text.setDragEnabled(true);
	 //            text.selectAll();
	             
	             TextBox text = new TextBox();
	             
	             text.setText("Do you wish to substitute in the first or in the second?");

//		             text.setDragEnabled(true);
		             text.selectAll();

	             boolean isFirst = true;

	            Button firstButton = new Button();
	            firstButton.setText("First");
	            firstButton.addClickHandler(new FirstSecondHandler(isFirst,
		                 firstline, secondline));
	            		 
	            Button secondButton = new Button();
	            secondButton.setText("Second");
	            secondButton.addClickHandler(new FirstSecondHandler(!isFirst,
		                 firstline, secondline));		 
	            		 
	   /*         		 new JButton(new FirstSecondAction(isFirst,
	                 firstline, secondline));
	             JButton secondButton = new JButton(new FirstSecondAction(!isFirst,
	                 firstline, secondline)); */

	             Button[] buttons = {
	                 cancelButton(), firstButton, secondButton}; // put cancel on left
	             inputPane = new TGWTProofInputPanel("Doing Identity Elimination", text,
	                                              buttons);

	             addInputPane(inputPane,SELECT);

	             //inputPane.getRootPane().setDefaultButton(firstButton);    //I don't think we want a default
//	             fInputPane.setVisible(true); // need this
//	             text.requestFocus(); // so selected text shows

	           }
	         }
	       break;}
	     }
	   }

  public void doIE(){
	  TProofline[] selections = fDisplayCellTable.exactlyNLinesSelected(2);

	    if (selections != null) {
	     TProofline firstline =  selections[0];
	     TProofline secondline =  selections[1];

	     if (fParser.isEquality(firstline.getFormula())||
	         fParser.isEquality(secondline.getFormula())){
	            orderForSwap(firstline, secondline); // this launches or puts up a prelim dialog which launches
	         //{we allow substitution is any formula provided no variable in the equality is bound in the formula}
	     }
	   }
	 }
 
  public void doUniqueE(){

      TProofline firstLine = fDisplayCellTable.oneSelected();

      if (firstLine != null&&fParser.isUnique(firstLine.fFormula)) {


        TFormula scope = firstLine.fFormula.expandUnique();

        if (scope == null) {
          bugAlert("DoingUniqueE. Warning.",
                   "There are no variables left to use in the expansion.");
        }
        else {

          TUndoableProofEdit newEdit = new TUndoableProofEdit();

          TProofline headLastLine = fModel.getHeadLastLine();

          int level = headLastLine.fSubprooflevel;
          int lastlineno = headLastLine.fLineno;


          TProofline newline = supplyProofline();

          newline.fFormula = scope.copyFormula();
          newline.fFirstjustno = lastlineno+1;

          newline.fJustification = uniqueEJustification;
          newline.fSubprooflevel = level;

          newEdit.fNewLines.add(newline);

          newEdit.doEdit();

        }
      }
      }
  
  
  public void doHintUniqueI(){ TFormula newFormula=null;
  TProofline newline=null;
  TFormula conclusion =findNextConclusion();

  if ((conclusion==null)||!fParser.isUnique(conclusion))
    bugAlert("DoingTacticsUniqueI. Warning.",
             "With the tactic for "+chUnique+ "the conclusion must be a unique quantification.");
  // do not need this as menu is disabled if conditions not satisfied
  else{


TFormula scope=conclusion.expandUnique();

if (scope==null){
  bugAlert("DoingTacticsUniqueI. Warning.",
             "There are no variables left to use in the expansion.");

}
else{

}


    TUndoableProofEdit newEdit = new TUndoableProofEdit();

    TProofline headLastLine=fModel.getHeadLastLine();

    int level = headLastLine.fSubprooflevel;
    int lastlineno = headLastLine.fLineno;

    int scopelineno=addIfNotThere(scope, level, newEdit.fNewLines);

    if (scopelineno==-1){   // not there
      scopelineno = lastlineno+2;
      lastlineno += 2;
    }

            newline = supplyProofline();

            newline.fFormula = conclusion.copyFormula();
            newline.fFirstjustno=scopelineno;

            newline.fJustification = uniqueIJustification;
            newline.fSubprooflevel = level;

            newEdit.fNewLines.add(newline);

          newEdit.doEdit();

  }

}
  
  public void doUniqueI(){
	  TFormula formulanode;

	  if (fTemplate)
	   doHintUniqueI();
	 else {

	TProofline firstLine = fDisplayCellTable.oneSelected();

	   if (firstLine != null) {

	     formulanode=firstLine.fFormula.abbrevUnique();

	     if (formulanode==null){
	       bugAlert("DoingUnique. Warning.", "Your formula does not have the right form.");

	     }
	     else{


	       TProofline newline = supplyProofline();

	       newline.fFormula = formulanode;
	       newline.fFirstjustno = firstLine.fLineno;
	       newline.fJustification = uniqueIJustification;
	       newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

	       TUndoableProofEdit newEdit = new TUndoableProofEdit();
	       newEdit.fNewLines.add(newline);
	       newEdit.doEdit();
	     }
	   }
	 }
	  }
 
  public void doRA(){
	  TProofline firstLine=fDisplayCellTable.oneSelected();

	  if (firstLine!=null){
	        TProofline newline = supplyProofline();

	        newline.fFormula=firstLine.fFormula.copyFormula();
	        newline.fFirstjustno=firstLine.fLineno;
	        newline.fJustification= repeatJustification;
	        newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;


	        TUndoableProofEdit  newEdit = new TUndoableProofEdit();

	        newEdit.fNewLines.add(newline);

	         newEdit.doEdit();


	    //    fModel.insertAtPseudoTail(newline);  //no undo yet

	      }

	    }
  
  /************ Experiments with Rewrite ******************
  *
  *
  */




public class RewriteHandler implements ClickHandler{

  TRewriteRules fRules;
  int fLineno;
  boolean fMustChange;    //whether the rewrites have to change the formula
  TFormula fOriginalFormula;
  
  RewriteHandler(
               TRewriteRules rules,
               int lineNo,
               TFormula originalFormula,
               boolean mustChange ){
   //putValue(NAME, label);

   fRules=rules;
   fLineno=lineNo;
   fMustChange=mustChange;
   fOriginalFormula=originalFormula;

 }

 public void onClick(ClickEvent event){
   TFormula afterRoot = fRules.getAfterRoot();
   
   if(afterRoot==null)
	   return;
	 
   if (((fRules.getNewRoot()!=null)&&
       (fRules.getSelectionRoot()!=null)&&
       (!fRules.getNewRoot().equalFormulas(fRules.getNewRoot(),fRules.getSelectionRoot()))&&
       !fOriginalFormula.equalFormulas(fOriginalFormula,afterRoot)
		   )    		   
    		   ||
        !fMustChange)
       {

    // we need to find the entire after formula

  

         if (afterRoot!=null){ // should alwyas be

           TProofline newline = supplyProofline();

           newline.fFormula = afterRoot.copyFormula();
           newline.fFirstjustno = fLineno;
           newline.fJustification = fRules.getLastRewrite();
           newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

           TUndoableProofEdit newEdit = new TUndoableProofEdit();

           newEdit.fNewLines.add(newline);

           newEdit.doEdit();
           removeInputPanel();
         }

     }


 }

}


public void doRewrite(){
  Button defaultButton;

  TGWTProofInputPanel inputPane;

 // fSelectionRewrite="";

  TProofline selectedLine=fDisplayCellTable.oneSelected();

  if (selectedLine!=null){

    String originalFormulaStr=fParser.writeFormulaToString(selectedLine.fFormula);

    TRewriteRules rules= new TRewriteRules(selectedLine.fFormula,fParser);

     boolean mustChange=true;
     
     defaultButton = new Button("Go");
     defaultButton.addClickHandler(new RewriteHandler(
             						rules,
             						selectedLine.fLineno,
             						selectedLine.fFormula,
             						mustChange));

/*     defaultButton = new JButton(new RewriteAction("Go",
                                                   rules,
                                                   selectedLine.fLineno,
                                                   mustChange)); */


     Widget[]components = {rules.getListBox(),  cancelButton(), defaultButton };  // put cancel on left

     inputPane = new TGWTProofInputPanel("Choose rule, select (sub)formula to rewrite, click Go...",
                                      rules.getBeforeText(),
                                      "After rewrite, the formula will look like this:",
                                      rules.getAfterText(),
                                      components);


          addInputPane(inputPane,!SELECT);

          //TO DO	 inputPane.getRootPane().setDefaultButton(defaultButton);
        //TO DO fInputPane.setVisible(true); // need this
        //TO DO rules.getBeforeText().requestFocus();         // so selected text shows

   }

}

  
  
  
  
  
/************************ Utilities *************************************/
  
  TFormula findNextConclusion(){

	  TProofline conclusion= fModel.getNextConclusion();

	  if (conclusion!=null)
	    return
	        conclusion.fFormula;
	  else
	    return
	        null;


	}
  

public class TUndoableProofEdit extends UndoableEdit{


ArrayList fNewLines= new ArrayList();
ArrayList fGarbageLines= new ArrayList();
ArrayList fOldHead = null;
ArrayList fOldTail =null;
boolean fCutQuestionMark=false;
int fOldProofType= pfFinished;



/*

   fNewLines: TList;
    fOldHead: TList;
    fOldTail: TList;
    fGarbageLInes: TList;
    fCutQuestionMark: boolean;
    fOldProofType: proofType;
    fProofWindow: TProofWindow;


 */


public  TUndoableProofEdit(){


  fOldHead = /*fModel*/TProofListModel.shallowCopy(fModel.getHead());  // note: this is only a shallow copy, I think that's enough as we don't change formulas
  fOldTail = /*fModel*/TProofListModel.shallowCopy(fModel.getTail());
  fCutQuestionMark=false;
  fOldProofType= fProofType;
}

/*

  procedure TLineCommand.ILineCommand (itsCmdNumber: CmdNumber; itsProofWindow: TProofWindow);

procedure CopyHead (item: TObject);

 var
  aProofline: TProofline;

begin
 aProofline := TProofline(item).CloneIt;
 fOldHead.InsertLast(aProofline);
end;

procedure CopyTail (item: TObject);

 var
  aProofline, newline: TProofline;

begin
 aProofline := TProofline(item).CloneIt;
 fOldTail.InsertLast(aProofline);
end;

begin
ICommand(itsCmdNumber, itsProofWindow.fDocument, itsProofWindow, itsProofWindow.GetScroller(TRUE));
fProofWindow := itsProofWindow;

fProofWindow.fTextList.SetEmptySelection(TRUE); {check de-selects}

fNewlines := nil;
fNewlines := NewList;
fCausesChange := TRUE;
fCanUndo := TRUE;

fCutQuestionMark := FALSE;

fOldProofType := fProofWindow.fProofType;

fOldHead := nil;
fOldHead := NewList;
fOldTail := nil;
fOldTail := NewList;
fGarbageLInes := nil;
fGarbageLInes := NewList;

fProofWindow.fHead.Each(CopyHead);
fProofWindow.fTail.Each(CopyTail);

end;


 */






public String getPresentationName(){
return

 "proof change";
}

void cutQuestionMark(){
 //{This removes the question line, and the 'conclusion' line after the gPseudoTail}
int headSize=fModel.getHeadSize();
int tailSize=fModel.getTailSize();

 if (tailSize==0)
   toNewPseudoTail();// {this is to check that} a new subgoals has not been added to a finished proof}

 // what we are looking for is that the formula on the head last line, and the one on
 // the tail second line (the first is a question mark) are the same. And that they have
 // the same proof level. If so, we'll cut the first two from the tail, slightly collapsing the proof

 if ((headSize>0)&&(tailSize>1)){
   TProofline lastHeadLine = fModel.getHeadLastLine();
   TProofline secondTailLine = fModel.getTailLine(1);

   if (lastHeadLine.fSubprooflevel==secondTailLine.fSubprooflevel){
     TFormula firstFormula = lastHeadLine.fFormula;
     TFormula secondFormula = secondTailLine.fFormula;

     if (TFormula.equalFormulas(firstFormula,secondFormula)){
       fCutQuestionMark=true;  //now remove first two tail lines.

       fModel.remove(headSize+1);
       fModel.remove(headSize);

       fModel.incrementTailLineNos(-2,lastHeadLine.fLineno);

       toNewPseudoTail();

       cutQuestionMark();

     }
       //Bizarre case to go in here
   }
 }
}

/*

 procedure TLineCommand.CutQuestionMark;

{This removes the question line, and the 'conclusion' line after the gPseudoTail}

var
 templine: TProofline;
 i, headlastlineno: integer;
 firstformula, secondformula: TFormula;

procedure DecrementLineNos (item: TObject);

 var
  aProofline: TProofline;

begin
 aProofline := TProofline(item);
 aProofline.fLineno := aProofline.fLineno - 2;
 if aProofline.fFirstJustno > headlastlineno then
  aProofline.fFirstJustno := aProofline.fFirstJustno - 2;
 if aProofline.fSecondJustno > headlastlineno then
  aProofline.fSecondJustno := aProofline.fSecondJustno - 2;
 if aProofline.fThirdJustno > headlastlineno then
  aProofline.fThirdJustno := aProofline.fThirdJustno - 2;
end;

procedure IncrementLineNos (item: TObject);

 var
  aProofline: TProofline;

begin
 aProofline := TProofline(item);
 aProofline.fLineno := aProofline.fLineno + 1;
 if aProofline.fFirstJustno > headlastlineno then
  aProofline.fFirstJustno := aProofline.fFirstJustno + 1;
 if aProofline.fSecondJustno > headlastlineno then
  aProofline.fSecondJustno := aProofline.fSecondJustno + 1;
 if aProofline.fThirdJustno > headlastlineno then
  aProofline.fThirdJustno := aProofline.fThirdJustno + 1;
end;

procedure BizarreCase; {Alterations to CutQuestionMark 6/28/91}
 var
  lastlevel: integer;
  newline: TProofline;
  assFormula: TFormula;

 function FindLastAssumption: boolean; {of proof as a whole}

  var
   dummy: TObject;

  function Premise (item: TObject): boolean;
   var
    aProofLIne: TProofLine;
  begin
   Premise := false;
   aProofLIne := TProofLine(item);
   if (aProofLIne.fjustification = 'Ass') and (aProofLIne.fSubprooflevel = lastlevel + 1) then
    begin
     Premise := TRUE;
     assFormula := aProofLIne.fFormula;
    end;
  end;

 begin
  dummy := nil;
  lastlevel := TProofLine(fProofWindow.fHead.Last).fSubprooflevel;
  dummy := fProofWindow.fHead.LastThat(Premise);
  FindLastAssumption := dummy <> nil;
 end;


{This is when a proof is started with TacticsOn, then they are switched off and the last}
{assmption is dropped-- thus damaging the proof. The last assumption has to be }
{added to the tail.}
begin
 if FindLastAssumption then
  begin
   assFormula := assFormula.CopyFormula;
   SupplyProofline(newline); {newline points to new proofline}
   with newline do
    begin
     fSubprooflevel := TProofLine(fProofWindow.fHead.Last).fSubprooflevel + 1;
     fLineNo := TProofLine(fProofWindow.fHead.Last).fLineNo + 1;
     fFormula := assFormula;
     fjustification := 'Ass';
     fLastassumption := TRUE;
     fHeadlevel := TProofLine(fProofWindow.fHead.Last).fHeadLevel;
    end;
   fProofWindow.fHead.InsertLast(newline);
   fProofWindow.fTail.Each(IncrementLineNos);
   newline := nil;
  end;
end;

begin
        {IF fProofWindow.fTemplate THEN fProofWindow.ToNewPseudoTail; must work forward}

if fProofWindow.fTail.fSize = 0 then
 fProofWindow.ToNewPseudoTail; {this is to check that}
{   a new subgoals has not been added to a finished proof}

if (fProofWindow.fHead.fSize > 0) and (fProofWindow.fTail.fSize > 1) then
 begin
  headlastlineno := TProofline(fProofWindow.fHead.Last).fLineno;

  if (TProofline(fProofWindow.fHead.Last).fSubprooflevel = TProofline(fProofWindow.fTail.At(2)).fSubprooflevel) then
   begin
    firstformula := TProofline(fProofWindow.fHead.Last).fFormula;
    secondformula := TProofline(fProofWindow.fTail.At(2)).fFormula;

    if Equalformulas(firstformula, secondformula) then
     begin
     fCutQuestionMark := TRUE;
     templine := TProofline(fProofWindow.fTail.first);
     fGarbageLInes.InsertFirst(templine);
     fProofWindow.fTail.delete(fProofWindow.fTail.first);
     templine := TProofline(fProofWindow.fTail.first);
     fGarbageLInes.InsertFirst(templine);
     fProofWindow.fTail.delete(fProofWindow.fTail.first);

     fProofWindow.fTail.Each(DecrementLineNos);

                       {IF NOT fProofWindow.fTemplate THEN   check}

     fProofWindow.ToNewPseudoTail; {goes to}
{                              first for next}

     CutQuestionMark; {new}

                       {fProofWindow.fProofType := pfFinished; check this is done in ToNewPsed}
     end;
   end
  else if (TProofline(fProofWindow.fHead.Last).fSubprooflevel < TProofline(fProofWindow.fTail.At(2)).fSubprooflevel) then
   BizarreCase;

 end;
end;


*/


/*
procedure TCutLineCommand.DoIt;
 OVERRIDE;

 var
  newline: TProofline;
  textlist: TTextListView;
  badtop, i: integer;
  badCell: GridCell;

 procedure Remove (item: TObject);

 begin
  fProofWindow.fHead.delete(item);
  if not TProofline(item).fBlankline then
   begin
    fProofWindow.DecrementLineNos(fProofWindow.fHead, TProofline(item).fLineno, 1);
    fProofWindow.DecrementLineNos(fProofWindow.fTail, TProofline(item).fLineno, 1);
   end;
 end;

begin
         {enters with old and new lists same}

         {first alter newlist}

 if fGarbageLInes.fSize <> 0 then
  fGarbageLInes.Each(Remove);

        { IF (fProofWindow.fProofType = premconc) OR (fProofWindow.fProofType = NOpremconc) THEN}
{               CutQuestionMark;  check maybe this should be in}

         {the erase bad grid cells}

 badCell.h := 1;

 textlist := fProofWindow.fTextList;

 badtop := textlist.fNumofRows;

 EraseBadCells(fBadBottom, badtop);

         { ensure same no of cells as prooflines}

 badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

 if (textlist.fNumofRows - badtop) <> 0 then
  begin
   if (textlist.fNumofRows - badtop) < 0 then
    fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
   else
    fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
  end;

         {redraw}

 for i := (fBadBottom) to badtop do {patch over erase chunks problem with zero heights}
  begin
   badCell.v := i;
   textlist.InvalidateCell(badCell);
  end;

 fProofWindow.CheckCellHeights;
 fProofWindow.ResetSelectables;
end;


*/


public void doCutLinesEdit(){                            // note this is not an override, we want to callit directly


 int endIndex=fGarbageLines.size()-1;
 TProofline cutLine;
 UndoableEditEvent edit=new UndoableEditEvent(TProofController.this,this);

 fDisplayCellTable.clearSelection();

 while (endIndex>=0){
      cutLine = (TProofline)(fGarbageLines.get(endIndex));

      fModel.removeProofline(cutLine);

      endIndex-=1;                                // clearer to work from the end back
    }

 cutQuestionMark();                               // don't know whether we need this

 if (fLastEdit!=null)                             // kill the previous one so there's only one undo
      fLastEdit.die();

//  fListener.undoableEditHappened(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

tellListeners(edit);  // tell the listeners.

 fLastEdit=this;

 fModel.resetSelectables();   //NEW APRIL

}





public void doEdit(){                            // note this is not an override, we want to callit directly

int i=0;
int endIndex=fNewLines.size()-1;
TProofline aNewLine;

//fProofListView.clearSelection();
fDisplayCellTable.clearSelection();

while (i<=endIndex){
   aNewLine = (TProofline)(fNewLines.get(i));

   fModel.insertAtPseudoTail(aNewLine);

   i=i+1;
 }

cutQuestionMark();

if (fLastEdit!=null)                             // kill the previous one so there's only one undo
   fLastEdit.die();

//fListener.undoableEditHappened(new UndoableEditEvent(TProofPanel.this,this));  // tell the listener.

 tellListeners(new UndoableEditEvent(TProofController.this,this));  // tell the listener.

fLastEdit=this;

fModel.resetSelectables();   //NEW APRIL

setUndoEnabled(true);//fCanUndo=true; sse
setRedoEnabled(false);//fCanRedo=false;

}

/*
procedure TLineCommand.DoIt;
OVERRIDE;

var
 newline: TProofline;
 textlist: TTextListView;
 badbottom, badtop, i: integer;
 badCell: GridCell;

procedure AddToTail (item: TObject);

 var
  newline: TProofline;

begin
 newline := TProofline(item);
 fProofWindow.InsertAtPseudoTail(newline);
end;

begin
        {enters with old and new lists same}

        {first alter newlist}

if fNewlines.fSize <> 0 then
 fNewlines.Each(AddToTail);

{ IF (fProofWindow.fProofType = premconc) OR (fProofWindow.fProofType = NOpremconc) THEN   check not needed}
CutQuestionMark;

        {the erase bad grid cells}

badCell.h := 1;
badbottom := fOldHead.fSize + 1;
if ((fProofWindow.fHead.fSize + 1) < badbottom) then
 badbottom := fProofWindow.fHead.fSize + 1;

textlist := fProofWindow.fTextList;

badtop := textlist.fNumofRows;

EraseBadCells(badbottom, badtop);

        { ensure same no of cells as prooflines}

badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

if (textlist.fNumofRows - badtop) <> 0 then
 begin
  if (textlist.fNumofRows - badtop) < 0 then
   fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
  else
   fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
 end;

        {redraw}

for i := (badbottom) to badtop do {patch over erase chunks problem with zero heights}
 begin
  badCell.v := i;
  textlist.InvalidateCell(badCell);
 end;

fProofWindow.CheckCellHeights;
fProofWindow.ResetSelectables;
end;



*/

@Override
public void redo() /*throws CannotRedoException*/{
//TO DO  super.redo();
//  System.out.println("UndoableProofEditRedo");

  //fProofListView.clearSelection();   // they may have made one in the meantime
  fDisplayCellTable.clearSelection();

  ArrayList tempHead=fModel.getHead();
  ArrayList tempTail=fModel.getTail();



  fModel.replaceHeadAndTail(fOldHead,fOldTail);
  fOldHead=tempHead;
  fOldTail=tempTail;

  int tempProofType = fProofType;
  fProofType = fOldProofType;
  fOldProofType = tempProofType;

  fModel.resetSelectables();
  
  setUndoEnabled(true);//fCanUndo=true; sse
  setRedoEnabled(false);//fCanRedo=false;

 }

/*

     procedure TLineCommand.RedoIt;
   OVERRIDE;

   var
    textlist: TTextListView;
    badbottom, badtop, i: integer;
    badCell: GridCell;
    tempList: TLIst;
    tempProofType: proofType;

  begin

   fProofWindow.fTextList.SetEmptySelection(TRUE); {remove selections they've made in}
 {                                                           meantime}

           {change lists}

   tempList := fProofWindow.fHead;
   fProofWindow.fHead := fOldHead;
   fOldHead := tempList;
   tempList := nil;

   tempList := fProofWindow.fTail;
   fProofWindow.fTail := fOldTail;
   fOldTail := tempList;
   tempList := nil;

   tempProofType := fProofWindow.fProofType;
   fProofWindow.fProofType := fOldProofType;
   fOldProofType := tempProofType;

           {remove garbage}

   fGarbageLInes.DeleteAll; {check}

           {Erase bad}

   badCell.h := 1;
   badbottom := fOldHead.fSize + 1;
   if ((fProofWindow.fHead.fSize + 1) < badbottom) then
    badbottom := fProofWindow.fHead.fSize + 1;

   textlist := fProofWindow.fTextList;

   badtop := textlist.fNumofRows;

   EraseBadCells(badbottom, badtop);

           {to ensure same no of cells as prooflines}

   badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

   if (textlist.fNumofRows - badtop) <> 0 then
    begin
     if (textlist.fNumofRows - badtop) < 0 then
      fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
     else
      fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);
    end;

   for i := (badbottom) to badtop do
    begin
     badCell.v := i;
     textlist.InvalidateCell(badCell);
    end;

   fProofWindow.CheckCellHeights;
   fProofWindow.ResetSelectables;

  end;


  */

@Override
public void undo() /*throws CannotUndoException*/{
//To do super.undo();
//System.out.println("UndoableProofEditUndo");

//fProofListView.clearSelection();   // they may have made one in the meantime
fDisplayCellTable.clearSelection();

ArrayList tempHead=fModel.getHead();
ArrayList tempTail=fModel.getTail();
fModel.replaceHeadAndTail(fOldHead,fOldTail);
fOldHead=tempHead;
fOldTail=tempTail;

int tempProofType = fProofType;
fProofType = fOldProofType;
fOldProofType = tempProofType;

fModel.resetSelectables();

setUndoEnabled(false);//fCanUndo=false; sse
setRedoEnabled(true);//fCanRedo=true;
}

/*

procedure TLineCommand.UndoIt;
  OVERRIDE;

  var
   textlist: TTextListView;
   badbottom, badtop, i: integer;
   badCell: GridCell;
   tempList: TLIst;
   tempProofType: proofType;

  procedure AddToGarbage (item: TObject);

  begin
   fGarbageLInes.InsertLast(item);
  end;

 begin
  fProofWindow.fTextList.SetEmptySelection(TRUE); {remove selections they've made in}
{                                                           meantime}

          {change lists}

  tempList := fProofWindow.fHead;
  fProofWindow.fHead := fOldHead;
  fOldHead := tempList;
  tempList := nil;

  tempList := fProofWindow.fTail;
  fProofWindow.fTail := fOldTail;
  fOldTail := tempList;
  tempList := nil;

  tempProofType := fProofWindow.fProofType;
  fProofWindow.fProofType := fOldProofType;
  fOldProofType := tempProofType;

          {insert garbage}

  if fNewlines.fSize <> 0 then
   fNewlines.Each(AddToGarbage);

          {Erase bad}

  badCell.h := 1;
  badbottom := fOldHead.fSize + 1;
  if ((fProofWindow.fHead.fSize + 1) < badbottom) then
   badbottom := fProofWindow.fHead.fSize + 1;

  textlist := fProofWindow.fTextList;

  badtop := textlist.fNumofRows;

  EraseBadCells(badbottom, badtop);

  badtop := fProofWindow.fHead.fSize + fProofWindow.fTail.fSize;

  if (textlist.fNumofRows - badtop) <> 0 then {to ensure same no of cells as prooflines}
   begin
    if (textlist.fNumofRows - badtop) < 0 then
     fProofWindow.fTextList.InsItemLast(badtop - textlist.fNumofRows)
    else
     fProofWindow.fTextList.DelItemLast(textlist.fNumofRows - badtop);

   end;

  for i := (badbottom) to badtop do
   begin
    badCell.v := i;
    textlist.InvalidateCell(badCell);
   end;

  fProofWindow.CheckCellHeights;
  fProofWindow.ResetSelectables;

 end;



*/



}


/************************  EditPlus Menu ********************************/


void assembleLinesToCut(TProofline startLine, ArrayList garbageLines){
	  if ((startLine != null)&&fModel.lineCutable(startLine, null)){

	    prependIfNotThere(startLine, garbageLines);

	    TProofline predecessor = fModel.predecessor(startLine);

	    if (predecessor != null) {

	      /*if line is a subgoal, and the linebefore is therefore a ?, need to cut that as well */

	      if (predecessor.fJustification.equals(questionJustification))
	        prependIfNotThere(predecessor, garbageLines);

	        /*if line is the conclusion of a subproof, cut all subproof */

	      if (predecessor.fBlankline) {
	        TProofline twoBefore = fModel.predecessor(predecessor);

	        if (twoBefore != null) { // we don't cut the first line of proof

	          prependIfNotThere(predecessor, garbageLines); //the blankline

	          if (startLine.fJustification.equals(fNegIJustification) ||
	              startLine.fJustification.equals(fImplicIJustification) ||
	              startLine.fJustification.equals(fEIJustification) ||
	              startLine.fJustification.equals(fOrEJustification) ||
	              startLine.fJustification.equals(equivIJustification)) {

	            //we cut every line back to, and inclusive, to previous assumption

	            TProofline lastAssumption = fModel.
	                findLastAssumptionOfPriorSubProof(startLine,
	                startLine.fSubprooflevel + 1);

	            TProofline toCut = fModel.predecessor(predecessor);

	            while (toCut != null && toCut != lastAssumption) {
	              prependIfNotThere(toCut, garbageLines);
	              toCut = fModel.predecessor(toCut);
	            }

	            prependIfNotThere(lastAssumption, garbageLines);

	            // for those rules with two subproofs we have to cut the other one

	            if (startLine.fJustification.equals(fOrEJustification) ||
	                startLine.fJustification.equals(equivIJustification)) {

	              int endIndex = fModel.indexOfLineno(startLine.fSecondjustno) +
	                  1; //we want the blankline

	              //there is a mistake in the original Pascal here using fFirstjustno

	              TProofline endOfSecond = (TProofline) (fModel.getElementAt(
	                  endIndex));

	              lastAssumption = fModel.findLastAssumptionOfPriorSubProof(
	                  endOfSecond, endOfSecond.fSubprooflevel + 1);

	              toCut = endOfSecond;

	              while (toCut != null && toCut != lastAssumption) {
	                prependIfNotThere(toCut, garbageLines);
	                toCut = fModel.predecessor(toCut);
	              }

	              prependIfNotThere(lastAssumption, garbageLines);

	            }

	          }
	        }

	      }
	    }
	  }


	}

Command cutProoflineCommand() {
	return
		new Command(){
			public void execute()
			{
				doCutProofline();
    		
			}
	};
}

void doCutProofline(){   // this assumes that this command is enabled only if the line is actually cutable

	   TProofline firstline=fDisplayCellTable.oneSelected();

	   if ((firstline != null)&&fModel.lineCutable(firstline, null)){

	     TUndoableProofEdit newEdit = new TUndoableProofEdit();

	     assembleLinesToCut(firstline,newEdit.fGarbageLines);

	     newEdit.doCutLinesEdit();
	   }
	}


void prependIfNotThere(TProofline line, ArrayList list){
	  if (!list.contains(line))
	    list.add(0,line);

	}

Command pruneCommand() {
	return
		new Command(){
			public void execute()
			{
				doPrune();
    		
			}
	};
}

	 void doPrune(){   // this assumes that this command is enabled only if the line is actually cutable

	   int searchIndex = fModel.getHeadSize()-1;
	   TProofline lineToCut;

	   TUndoableProofEdit newEdit = new TUndoableProofEdit();

	   if (fModel.getTailSize()==0)
	     searchIndex-=1;              // don't cut the last line

	/*we'll go backward through the proof cutting the lines we can. Notice that
	when a line is cut the size of the proof changes (that does not matter in this algortihm)*/

	   while (searchIndex>0){         // don't cut the first line

	     lineToCut=fModel.getHeadLine(searchIndex);

	     if (fModel.lineCutable(lineToCut,newEdit.fGarbageLines))
	       assembleLinesToCut(lineToCut,newEdit.fGarbageLines);

	    searchIndex-=1;

	   }

	     if (newEdit.fGarbageLines.size()>0)
	      newEdit.doCutLinesEdit();


	}
	 
Command newGoalCommand() {
			return
				new Command(){
					public void execute()
					{
						doNewGoal();
		    		
					}
			};
		}	 
	 
Command startAgainCommand() {
			return
				new Command(){
					public void execute()
					{
						doStartAgain();
		    		
					}
			};
		}	 

void  doStartAgain(){
		   TUndoableProofEdit newEdit = new TUndoableProofEdit();   // this copies old lines

		   if (fProofStr!=null)          // record of this proof as a string
		     startProof(fProofStr);

		   newEdit.doEdit();            // does not do any editing but kills last edit and allows undo
		 }


/********************** Wizard Menu ********************************/


void doDerive(boolean allLines){};  //stub at present, pushed down to subclass TMyProofController and to other systems

Command deriveItCommand() {
	return
		new Command(){
			public void execute()  //toggle menu
			{
			boolean allLines=true;
			doDerive(allLines);
    		
			}
	};
}

Command nextLineCommand() {
	return
		new Command(){
			public void execute()  //toggle menu
			{
			boolean allLines=true;
			doDerive(!allLines);
    		
			}
	};
}

Command tacticsCommand() {
	return
		new Command(){
			public void execute()  //toggle menu
			{
			if (fTemplate)
				{fTemplate=false;
				fTactics.setText("Tactics ?");
				}
			else
			{fTemplate=true;
			fTactics.setText("√ Tactics");
			}
    		
			}
	};
}


/************************************************************************/




////////////////////Undo

/*We have here commands and menuitems */

Command undoRedoCommand() {return
		new Command()
{
    public void execute()
    {
    	try {
    		fUndoManager.undo();
    		} catch (Exception ex) {
    		System.out.println("Unable to undo: " + ex);
    		ex.printStackTrace();
    		}
    		//updateUndoState();
    	toggle(fUndoRedo);
    //TO DO		fRedoButton.updateRedoState();
    		
    }
};
}



protected void toggle(MenuItem undoItem) {
if (fUndoManager.canUndo()) {
	undoItem.setEnabled(true);
	undoItem.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
	
if (fUndoManager.canRedo()) {
	undoItem.setEnabled(true);
	undoItem.setTitle(fUndoManager.getRedoPresentationName());
	} else
	
		undoItem.setEnabled(false);
//putValue(Action.NAME, "Undo");
//this.setTitle("Undo");
}
}

protected void updateUndoState(MenuItem undoItem) {
if (fUndoManager.canUndo()) {
	undoItem.setEnabled(true);
	undoItem.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
	undoItem.setEnabled(false);
//putValue(Action.NAME, "Undo");
	undoItem.setTitle("Undo");
}
}






/*class TUndoRedoMenuItem extends MenuItem {
	
public TUndoRedoMenuItem() {
	super("Undo",true,null);

		
	}

public TUndoRedoMenuItem(String title, Command command) {
super(title,command);	

//super("Undo");
setEnabled(false);

this.addClickHandler(new ClickHandler(){@Override 
	public void onClick(ClickEvent event) {
	try {
		fUndoManager.undo();
		} catch (Exception ex) {
		System.out.println("Unable to undo: " + ex);
		ex.printStackTrace();
		}
		//updateUndoState();
	toggle();
//TO DO		fRedoButton.updateRedoState();
		}});
}

Command undoRedoCommand() {return
		new Command()
{
    public void execute()
    {
    	try {
    		fUndoManager.undo();
    		} catch (Exception ex) {
    		System.out.println("Unable to undo: " + ex);
    		ex.printStackTrace();
    		}
    		//updateUndoState();
    	toggle();
    //TO DO		fRedoButton.updateRedoState();
    		
    }
};
}





protected void toggle() {
if (fUndoManager.canUndo()) {
setEnabled(true);
this.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
	
if (fUndoManager.canRedo()) {
		setEnabled(true);
		this.setTitle(fUndoManager.getRedoPresentationName());
	} else
	
setEnabled(false);
//putValue(Action.NAME, "Undo");
//this.setTitle("Undo");
}
}


protected void updateUndoState() {
if (fUndoManager.canUndo()) {
setEnabled(true);
this.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
setEnabled(false);
//putValue(Action.NAME, "Undo");
this.setTitle("Undo");
}
}


*/


class TUndoRedoButton extends Button {
public TUndoRedoButton() {
super("Undo");
setEnabled(false);

this.addClickHandler(new ClickHandler(){@Override 
	public void onClick(ClickEvent event) {
	try {
		fUndoManager.undo();
		} catch (Exception ex) {
		System.out.println("Unable to undo: " + ex);
		ex.printStackTrace();
		}
		//updateUndoState();
	toggle();
//TO DO		fRedoButton.updateRedoState();
		}});
}

protected void toggle() {
if (fUndoManager.canUndo()) {
setEnabled(true);
this.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
	
if (fUndoManager.canRedo()) {
		setEnabled(true);
		this.setTitle(fUndoManager.getRedoPresentationName());
	} else
	
setEnabled(false);
//putValue(Action.NAME, "Undo");
//this.setTitle("Undo");
}
}


protected void updateUndoState() {
if (fUndoManager.canUndo()) {
setEnabled(true);
this.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
setEnabled(false);
//putValue(Action.NAME, "Undo");
this.setTitle("Undo");
}
}




}


/*
class UndoButton extends Button {
public UndoButton() {
super("Undo");
setEnabled(false);

this.addClickHandler(new ClickHandler(){@Override 
	public void onClick(ClickEvent event) {
	try {
		fUndoManager.undo();
		} catch (Exception ex) {
		System.out.println("Unable to undo: " + ex);
		ex.printStackTrace();
		}
		updateUndoState();
		fRedoButton.updateRedoState();
		}});
}




protected void updateUndoState() {
if (fUndoManager.canUndo()) {
setEnabled(true);
this.setTitle(fUndoManager.getUndoPresentationName());
//putValue(Action.NAME, fUndoManager.getUndoPresentationName());
} else {
setEnabled(false);
//putValue(Action.NAME, "Undo");
this.setTitle("Undo");
}
}
}

class RedoButton extends Button {
public RedoButton() {
super("Redo");
setEnabled(false);

this.addClickHandler(new ClickHandler(){@Override 
	public void onClick(ClickEvent event) {
	try {fUndoManager.redo();
} catch (Exception ex) {
System.out.println("Unable to redo: " + ex);
ex.printStackTrace();
}
updateRedoState();
fUndoButton.updateUndoState();
}});
}


protected void updateRedoState() {
if (fUndoManager.canRedo()) {
setEnabled(true);
//putValue(Action.NAME, fUndoManager.getRedoPresentationName());
this.setTitle(fUndoManager.getRedoPresentationName());
} else {
setEnabled(false);
//putValue(Action.NAME, "Redo");
this.setTitle("Redo");
}
}
}

*/

/***************** Deriving stuff **********************
*
*
*/

TTestNode assembleTestNode(){
TTestNode aTestRoot = supplyTTestNode(/*fDeriverDocument.getParser()*/ fParser,null);  //does not initialize TreeModel

/*
  begin
     i := 1;
     if fHead.fSize <> 0 then
      repeat
       aProofline := TProofline(fHead.At(i));
       if not aProofline.fblankline then
        if aProofline.fSelectable then
        begin
        gTestroot.fAntecedents.InsertFirst(aProofline.fFormula.CopyFormula);
 {best to have these in reverse order, because the User may have done some work}

        if (not abandon) then
        abandon := fDeriverDocument.fJournalWindow.BadCharacters(aProofline.fFormula, equals, compoundterms, higharity);

        end;
       i := i + 1;
      until (i > fHead.fSize);



*/

int size=fModel.getHeadSize();
TProofline aProofline;
int badChar;

for (int i=0;((i<size)&&(aTestRoot!=null));i++){
 aProofline=fModel.getHeadLine(i);

 if ((!aProofline.fBlankline)&&aProofline.fSelectable){
   badChar=fParser.badCharacters(aProofline.fFormula);

   if (badChar==TParser.kNone)
      aTestRoot.fAntecedents.add(0,aProofline.fFormula.copyFormula()); // {best to have these in reverse order, because the User may have done some work}
   else{
      aTestRoot=null;
      writeBadCharError(badChar);
   }
  }
 }

if (aTestRoot!=null){
   aProofline=fModel.getTailLine(1);
   badChar=fParser.badCharacters(aProofline.fFormula);

   if (badChar==TParser.kNone)
     aTestRoot.fSuccedent.add(aProofline.fFormula.copyFormula());
   else{
      aTestRoot=null;
      writeBadCharError(badChar);
   }

 }

/*

  gTestroot.fSucceedent.InsertLast(TProofline(fTail.At(2)).fFormula.CopyFormula);

       if (not abandon) then
        abandon := fDeriverDocument.fJournalWindow.BadCharacters(TProofline(fTail.Last).fFormula, equals, compoundterms, higharity);


*/

return
    aTestRoot;

}

void insertAll(ArrayList tempHead){

	  if ((tempHead!=null)&&(tempHead.size() > 0)) {
	    Iterator iter = tempHead.iterator();
	    TProofline aProofLine;
	    TProofline headLastLine = fModel.getHeadLastLine();
	    int theSubProofLevel = headLastLine.fSubprooflevel -
	        headLastLine.fHeadlevel;

	    TUndoableProofEdit newEdit = new TUndoableProofEdit();

	    while (iter.hasNext()) {
	      aProofLine = (TProofline) iter.next();
	      aProofLine.fSubprooflevel += theSubProofLevel;

	      aProofLine.fDerived=true; // to stop the students cheating

	      newEdit.fNewLines.add(aProofLine);

	    }

	    newEdit.doEdit();
	  }

	}

void insertFirstLine(ArrayList tempHead){
	  if ((tempHead!=null)&&(tempHead.size() > 0)) {

	  TProofline theProofLine=(TProofline)tempHead.get(0);
	  TProofline headLastLine = fModel.getHeadLastLine();
	  int theSubProofLevel = headLastLine.fSubprooflevel -
	      headLastLine.fHeadlevel;

	  TUndoableProofEdit newEdit = new TUndoableProofEdit();


	    theProofLine.fSubprooflevel += theSubProofLevel;
	    theProofLine.fDerived=true; // to stop the students cheating

	    newEdit.fNewLines.add(theProofLine);



	  newEdit.doEdit();
	}

	}

void writeBadCharError(int badChar){
    switch (badChar) {

      case TParser.kEquality:
        bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for = has not yet been implemented.");
        break;

      case TParser.kUnique:
        bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for "
                                    + chUnique
                                    +" has not yet been implemented.");
        break;
     case TParser.kHighArity:
       bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, relations have to be of arity 2 or less.");
        break;

        case TParser.kCompoundTerms:
          bugAlert("Exiting from Derive It. Warning.",
                 "Sorry, the semantics for compound terms has not yet been implemented.");
        break;

    }


}


/********************** Improving **********************************************/

void improve(ArrayList localHead, int lastAssIndex){   //change Oct 08 to add lastAssIndex

	  removeDuplicates(localHead);

	 // TDeriverApplication.fDebug.displayProof(localHead);

	  numberLines(localHead);

	  removeRedundant(localHead,lastAssIndex);

	  numberLines(localHead);

	}

TProofline nextBlankLineAfterSubproof(ArrayList localHead,TProofline theLine){
    if (localHead==null||localHead.size()<2)
      return
          null;

    int index=localHead.indexOf(theLine) +1;

    TProofline search;

    while (index<localHead.size()){

      search=(TProofline)localHead.get(index);

      if ((search.fSubprooflevel==(theLine.fSubprooflevel-1))&&  // a closing blank line has level one less than subproof
          search.fBlankline)
        return
           search;

     index+=1;
     }
  return
          null;
}

void numberLines(ArrayList localHead){
	  TProofListModel.renumberLines(localHead,1000);  //renumber to numbers that do not occur
	  TProofListModel.renumberLines(localHead,1);
	}

void removeDuplicates(ArrayList localHead){

	  //not assumptions

	  int i,j,limit,deletions;
	  TProofline firstline,searchline;

	  limit=localHead.size()-1;
	  i=0;
	  deletions=0;

	  while ((i+deletions)<limit){
	    firstline=(TProofline)localHead.get(i);

	    if (!firstline.fBlankline){
	      j=i+1;

	      while ((j + deletions) < limit){//not last line
	        searchline = (TProofline) (localHead.get(j));
	        if (!searchline.fBlankline &&
	            (!searchline.fJustification.equals(fAssJustification)) &&
	            TFormula.equalFormulas(firstline.fFormula, searchline.fFormula)
	            ) {  // it may be possible to delete searchline

	              TProofListModel.resetSelectablesToHere(localHead,j);

	              if (firstline.fSelectable){ //{searchline.fformula} redundant}

	                TProofListModel.reNumSingleLine(localHead, j, firstline.fLineno);
	                localHead.remove(j);

	                j-=1;
	                deletions+=1;
	              }
	           }
	           j+=1;
	        }
	    }
	    i+=1;
	  }

	}

void prune(ArrayList localHead){
	  int index=0;
	  int deletions=0;
	  int limit =localHead.size()-1;
	  TProofline aProofline,nextline;

	  while ((index+deletions)<limit){

	    aProofline=(TProofline)localHead.get(index);
	    nextline=(TProofline)localHead.get(index+1);

	    if (!nextline.fSelectable) {  //redundant

	      if (aProofline.fBlankline &&
	          (index + 2 < localHead.size()))
	        aProofline.fSubprooflevel=((TProofline)localHead.get(index+2)).fSubprooflevel;
	        //to cope with cutting entire subproofs

	      localHead.remove(index+1);
	      deletions+=1;
	    }
	    else
	      index+=1;

	  }

	}


void removeRedundant(ArrayList localHead, int lastAssIndex){
	  if (localHead!=null&&localHead.size()>0){
	    TProofline lastLine=((TProofline) localHead.get(localHead.size()-1));
	    Iterator iter = localHead.iterator();

	    while (iter.hasNext()) {
	      ( (TProofline) iter.next()).fSelectable = false; //using selectable as a flag
	    }

	    ((TProofline) localHead.get(0)).fSelectable = true; //in case of blankstart
	    lastLine.fSelectable = true;                        //setting first and last line true

	    int i=0;

	    while (i<=lastAssIndex)
	       {((TProofline) localHead.get(i)).fSelectable = true;
	       i++;}  //Oct 08 to fix decrement lastAss-- we won't prune premises


	    traceBack(localHead,lastLine.fLineno);

	    prune(localHead);

	    iter = localHead.iterator();

	    while (iter.hasNext()) {
	      ( (TProofline) iter.next()).fSelectable = false; //using selectable as a flag
	    }


	  }


	}

int selectLastAssumption(ArrayList localHead, int beforeHere){
    // needs to be an assumption at same subprooflevel

    int lastTIIndex=beforeHere-1;
    boolean found=false;
    TProofline searchLine;
    TProofline tempLast=(TProofline)localHead.get(beforeHere);
    int rightLevel=tempLast.fSubprooflevel+1;

    while ((lastTIIndex>-1)&&!found){
      searchLine=(TProofline)localHead.get(lastTIIndex);

      if ((searchLine.fSubprooflevel==rightLevel)&&
          (searchLine.fJustification.equals(TProofController.fAssJustification))&&
          searchLine.fLineno<=tempLast.fLineno){  //don't know why we need last condition
        found=true;
        searchLine.fSelectable=true;
      }
      else
        lastTIIndex-=1;
    }

    return
        lastTIIndex;  //-1 is not found
  }

void setHeadLevels(ArrayList localHead){
int headlevel;

if (((TProofline)localHead.get(0)).fBlankline)  // no premises
headlevel=-1;
else
headlevel=0;

Iterator iter = localHead.iterator();

while (iter.hasNext()) {
   ( (TProofline) iter.next()).fHeadlevel=headlevel;
    }


}

String traceAssOverride(){     //for subclasses
	  return
	      "";
	}

void traceBack(ArrayList localHead,int lineNo){
    int index=lineNo-1;    //cannot be less than lineNo-1
    TProofline traceSearch=(TProofline)localHead.get(index);
    boolean found=false;
    int lastTIIndex;

    // first we find the line with this line number

    while ((index<localHead.size())&&!found){

      if (traceSearch.fLineno==lineNo)
        found=true;
      else{
        index+=1;
        traceSearch=(TProofline)localHead.get(index);
      }
    }

    // if we find it, we mark it and things it depends on

      if (found){
        traceSearch.fSelectable=true; // using selectable as a boolena flag

        if (index<(localHead.size()-1)){   // not last line
          TProofline nextBlank =nextBlankLineAfterSubproof(localHead,traceSearch);

          if(nextBlank!=null)
            nextBlank.fSelectable=true;

        /*the line we are looking at might be in a subproof, in which case we must not cut the blankline
          that closes the subproof */

        }
              /*(TProofline)localHead.get(index+1);
          if (nextLine.fBlankline)
            nextLine.fSelectable=true;  //following blankline has same lineno as last line of subproof
        }    */                           // mark blankline also

        if (traceSearch.fJustification.equals(fNegIJustification)||
            traceSearch.fJustification.equals(fImplicIJustification)||
            traceSearch.fJustification.equals(fEIJustification)||
            traceSearch.fJustification.equals(traceAssOverride()))
               lastTIIndex=selectLastAssumption(localHead,index);

        if (traceSearch.fJustification.equals(fOrEJustification)||
            traceSearch.fJustification.equals(fEquivIJustification)){
               lastTIIndex = selectLastAssumption(localHead, index);
               lastTIIndex = selectLastAssumption(localHead, lastTIIndex-1);
               //must have a -1 here because need to get out of subproof to previous line
        }

        if (traceSearch.fFirstjustno!=0)  //its ancestors
          traceBack(localHead,traceSearch.fFirstjustno);
        if (traceSearch.fSecondjustno!=0)  //its ancestors
          traceBack(localHead,traceSearch.fSecondjustno);
        if (traceSearch.fThirdjustno!=0)  //its ancestors
          traceBack(localHead,traceSearch.fThirdjustno);

    }
  }

}



