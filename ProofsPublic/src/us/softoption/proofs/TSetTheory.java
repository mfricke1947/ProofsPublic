/*
Copyright (C) 2014 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

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

package us.softoption.proofs;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEmptySet;
import static us.softoption.infrastructure.Symbols.chEquals;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chMemberOf;
import static us.softoption.infrastructure.Symbols.chMinus;
import static us.softoption.infrastructure.Symbols.chNotMemberOf;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chPowerSet;
import static us.softoption.infrastructure.Symbols.chSubset;
import static us.softoption.infrastructure.Symbols.chUnion;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chUniverseSet;
import static us.softoption.infrastructure.Symbols.chXProd;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strMemberOf;


//import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextField;

import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;
import us.softoption.proofs.TProofController.InductionHandler;
import us.softoption.proofs.TProofController.TUndoableProofEdit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;




public class TSetTheory {
	
	MenuItem comprehensionMenuItem = null;//new MenuItem();
	MenuItem extensionalityMenuItem = null;//new MenuItem();
	MenuItem subsetMenuItem = null;//new MenuItem();
	MenuItem powerSetMenuItem = null;//new MenuItem();
	MenuItem emptyMenuItem = null;//new MenuItem();
	MenuItem universeMenuItem = null;//new MenuItem();
	MenuItem unionMenuItem = null;//new MenuItem();
	MenuItem intersectionMenuItem = null;//new MenuItem();
	MenuItem pairMenuItem = null;//new MenuItem();
	MenuItem xProdMenuItem = null;//new MenuItem();
	MenuItem complementMenuItem = null;//new MenuItem();
	
	TProofController fProofController= null;
	TParser fParser= new TParser();
	
	
	String comprehensionJustification= "Ax.Abstraction";
	
	String Extensionality= "("+ chUniquant + "x)(" + chUniquant + "y)((x=y)"+
    chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chEquiv+"(z"+chMemberOf +"y)))"; 
	/*String Extensionality= "(x=y)"+   //without quantifiers
    chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chEquiv+"(z"+chMemberOf +"y))"; */
	String ExtensionalityJust="Ax.Extens.";
	TFormula AxExt=null;
	
/*	String Extensionality= "("+ chUniquant + "x)(" + chUniquant + "y)((x=y)"+
    chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chEquiv+"(z"+chMemberOf +"y)))"; 

	String ExtensionalityJust="Ax.Extens.";
	TFormula AxExt=null; */

    String Subset= "("+ chUniquant + "x)(" + chUniquant + "y)((x"+chSubset+"y)"+
    chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chImplic+"(z"+chMemberOf +"y)))"; 
	/*String Subset= "(x"+chSubset+"y)"+
    chEquiv+"("+ chUniquant +"z)((z"+chMemberOf +"x)"+chImplic+"(z"+chMemberOf +"y))"; */
	String SubsetJust="Ax.Subset";
	TFormula AxSubset=null;
	

    String PowerSet= "("+ chUniquant + "x)(" + chUniquant + "y)((x"+chMemberOf+chPowerSet+"(y))"+
    chEquiv+ "(x"+chSubset+"y))"; 
	String PowerSetJust="Ax.PowerSet";
	TFormula AxPowerSet=null;
	
	String Empty= "("+ chUniquant + "x)(x"+chNotMemberOf+ chEmptySet+")";
	String EmptyJust="Ax.EmptySet";
	TFormula AxEmpty=null;
	
	String Universe= "("+ chUniquant + "x)(x"+chMemberOf+ chUniverseSet+")";
	String UniverseJust="Ax.Uni.Set";
	TFormula AxUniverse=null;
	
	String Union= "("+ chUniquant + "x)(" + chUniquant + "y)(" + chUniquant + "z)" +
			"((z"+chMemberOf +"(x"+chUnion+"y))"+
            chEquiv+"((z"+chMemberOf +"x)"+chOr+"(z"+chMemberOf +"y)))";
	String UnionJust="Ax.Union";
	TFormula AxUnion=null;
	
	String Intersection= "("+ chUniquant + "x)(" + chUniquant + "y)(" + chUniquant + "z)" +
	"((z"+chMemberOf +"(x"+chIntersection+"y))"+
    chEquiv+"((z"+chMemberOf +"x)"+chAnd+"(z"+chMemberOf +"y)))";
	String IntersectionJust="Ax.Intersect";
	TFormula AxIntersection=null;
	
	String Pair= "("+ chUniquant + "x)(" + chUniquant + "y)(" + chUniquant + "z)" +
	"((z"+chMemberOf +"{x,y})"+
    chEquiv+"((z"+chEquals +"x)"+chOr+"(z"+chEquals  +"y)))";
	String PairJust="Ax.Pair";
	TFormula AxPair=null;
	
	String XProd= "("+ chUniquant + "x)(" + chUniquant + "y)(" + chUniquant + "z)(" + chUniquant + "w)" +
	"((<z,w>"+chMemberOf +"(x"+chXProd+"y))"+
    chEquiv+"((z"+chMemberOf +"x)"+chAnd+"(w"+chMemberOf +"y)))";
	String XProdJust="Ax.XProd";
	TFormula AxXProd=null;

	String Complement= "("+ chUniquant + "x)(" + chUniquant + "y)(" + chUniquant + "z)" +
	"((z"+chMemberOf +"(x"+chMinus+"y))"+
	chEquiv+"((z"+chMemberOf +"x)"+chAnd+"(z"+chNotMemberOf +"y)))";
	String ComplementJust="Ax.Complement";
	TFormula AxComplement=null;
	
	public TSetTheory(TProofController aProofPanel, TParser aParser){
		fProofController=aProofPanel;
		fParser=aParser;
		initializeFormulas();
	}
	
	private void initializeFormulas(){
	ArrayList valuation;
	StringReader reader;
	TParser aParser = new TParser();
	
    valuation=new ArrayList();
    reader = new StringReader(Extensionality);
    AxExt = new TFormula();		
	if	(!aParser.wffCheck(AxExt,/*valuation,*/reader))
		AxExt=null;
	
	valuation=new ArrayList();
	reader = new StringReader(Subset);
	AxSubset = new TFormula();			
	if	(!aParser.wffCheck(AxSubset,/*valuation,*/reader))
		AxSubset=null;
	
	valuation=new ArrayList();
	reader = new StringReader(PowerSet);
	AxPowerSet = new TFormula();			
	if	(!aParser.wffCheck(AxPowerSet,/*valuation,*/reader))
		AxPowerSet=null;

	valuation=new ArrayList();
	reader = new StringReader(Empty);
	AxEmpty = new TFormula();			
	if	(!aParser.wffCheck(AxEmpty,/*valuation,*/reader))
		AxEmpty=null;
	
	valuation=new ArrayList();
	reader = new StringReader(Universe);
	AxUniverse = new TFormula();			
	if	(!aParser.wffCheck(AxUniverse,/*valuation,*/reader))
		AxUniverse=null;
	
	valuation=new ArrayList();
	reader = new StringReader(Union);
	AxUnion = new TFormula();			
	if	(!aParser.wffCheck(AxUnion,/*valuation,*/reader))
		AxUnion=null;

	valuation=new ArrayList();
	reader = new StringReader(Intersection);
	AxIntersection = new TFormula();			
	if	(!aParser.wffCheck(AxIntersection,/*valuation,*/reader))
		AxIntersection=null;
	
	valuation=new ArrayList();
	reader = new StringReader(Pair);
	AxPair = new TFormula();			
	if	(!aParser.wffCheck(AxPair,/*valuation,*/reader))
		AxPair=null;
	
	valuation=new ArrayList();
	reader = new StringReader(XProd);
	AxXProd = new TFormula();			
	if	(!aParser.wffCheck(AxXProd,/*valuation,*/reader))
		AxXProd=null;
	
	
	valuation=new ArrayList();
	reader = new StringReader(Complement);
	AxComplement = new TFormula();			
	if	(!aParser.wffCheck(AxComplement,/*valuation,*/reader))
		AxComplement=null;
	}
	
	public void augmentAdvancedMenu(MenuBar fAdvancedRulesMenu ){
	/*	if (TPreferencesData.fSetTheory) */{
			  fAdvancedRulesMenu.addSeparator();
			  
			  comprehensionMenuItem = new MenuItem("Ax.Abstraction",new Command(){
					public void execute() {
					doComprehension();}});	
			  fAdvancedRulesMenu.addItem(comprehensionMenuItem);
			  		  
			  complementMenuItem = new MenuItem("Ax.Complement",new Command(){
					public void execute() {
					doComplement();}});	
			  fAdvancedRulesMenu.addItem(complementMenuItem);
			  
			  emptyMenuItem = new MenuItem("Ax.Empty",new Command(){
					public void execute() {
					doEmpty();}});	
			  fAdvancedRulesMenu.addItem(emptyMenuItem);
			  
			  extensionalityMenuItem = new MenuItem("Ax.Extensionality",new Command(){
					public void execute() {
					doExtensionality();}});	
			  fAdvancedRulesMenu.addItem(extensionalityMenuItem);
		  
			  intersectionMenuItem = new MenuItem("Ax.Intersection",new Command(){
					public void execute() {
					doIntersection();}});	
			  fAdvancedRulesMenu.addItem(intersectionMenuItem);
		  
			  pairMenuItem = new MenuItem("Ax.Pair",new Command(){
					public void execute() {
					doPair();}});	
			  fAdvancedRulesMenu.addItem(pairMenuItem);
			  
			  
			  powerSetMenuItem = new MenuItem("Ax.PowerSet",new Command(){
					public void execute() {
					doPowerSet();}});	
			  fAdvancedRulesMenu.addItem(powerSetMenuItem);
			 		  
			  
			  subsetMenuItem = new MenuItem("Ax.Subset",new Command(){
					public void execute() {
					doSubset();}});	
			  fAdvancedRulesMenu.addItem(subsetMenuItem);

			  
			  
			  unionMenuItem = new MenuItem("Ax.Union",new Command(){
					public void execute() {
					doUnion();}});	
			  fAdvancedRulesMenu.addItem(unionMenuItem);
			  
			  universeMenuItem = new MenuItem("Ax.Universe",new Command(){
					public void execute() {
					doUniverse();}});	
			  fAdvancedRulesMenu.addItem(universeMenuItem);
	  
			  xProdMenuItem = new MenuItem("Ax.XProd",new Command(){
					public void execute() {
					doXProd();}});	
			  fAdvancedRulesMenu.addItem(xProdMenuItem);

			  
			  
		}

		/* to do
		if (TPreferencesData.fSetTheory){
		  fAdvancedRulesMenu.addSeparator();
		  
		  comprehensionMenuItem.setText("Ax.Abstraction");
		  comprehensionMenuItem.addActionListener(new SetTheory_comprehensionMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(comprehensionMenuItem);
		  
		  complementMenuItem.setText("Ax.Complement");
		  complementMenuItem.addActionListener(new SetTheory_complementMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(complementMenuItem);
		  
		  emptyMenuItem.setText("Ax.Empty");
		  emptyMenuItem.addActionListener(new SetTheory_emptyMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(emptyMenuItem);
		  
		  extensionalityMenuItem.setText("Ax.Extensionality");
		  extensionalityMenuItem.addActionListener(new SetTheory_extMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(extensionalityMenuItem);
		  
		  intersectionMenuItem.setText("Ax.Intersection");
		  intersectionMenuItem.addActionListener(new SetTheory_intersectionMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(intersectionMenuItem);
		  
		  pairMenuItem.setText("Ax.Pair");
		  pairMenuItem.addActionListener(new SetTheory_pairMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(pairMenuItem);
		  
		  powerSetMenuItem.setText("Ax.PowerSet");
		  powerSetMenuItem.addActionListener(new SetTheory_powerSetMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(powerSetMenuItem);
		  
		  subsetMenuItem.setText("Ax.Subset");
		  subsetMenuItem.addActionListener(new SetTheory_subsetMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(subsetMenuItem);
		  	  
		  unionMenuItem.setText("Ax.Union");
		  unionMenuItem.addActionListener(new SetTheory_unionMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(unionMenuItem);
		  
		  universeMenuItem.setText("Ax.Universe");
		  universeMenuItem.addActionListener(new SetTheory_universeMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(universeMenuItem);
		  
		  xProdMenuItem.setText("Ax.XProd");
		  xProdMenuItem.addActionListener(new SetTheory_xProdMenuItem_actionAdapter(this));		  
		  fAdvancedRulesMenu.add(xProdMenuItem);
		  

		  
		}

*/
		}	
	
	
	
	
	
	  /************************ Rule of doInduction **********************************/



    public class ComprehensionHandler implements ClickHandler{
    	// modeled on InductionACTION
      TextBox fText;
      TFormula fRoot=null;

      TFormula xVar = new TFormula(TFormula.variable,"x",null,null);

     /*We have here to get  the root of new formula, which must contain free x  */


       public ComprehensionHandler(TextBox text/*, String label*/){
 //        putValue(NAME, label);

         fText=text;
       }

        public void onClick(ClickEvent event){


        if (fRoot==null){
          boolean useFilter = true;
          ArrayList dummy = new ArrayList();

          String aString= TUtilities.defaultFilter(fText.getText());

          TFormula root = new TFormula();
          StringReader aReader = new StringReader(aString);
          boolean wellformed;

          wellformed = fParser.wffCheck(root, /*dummy,*/ aReader);

          if (!wellformed) {
            String message = "The string is illformed." +
                (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

            fText.setText(message);
            fText.selectAll();
            // to do fText.requestFocus();
          }
          else {
                 // found root move to, checking that it contains n

            if (root.numOfFreeOccurrences(xVar)==0){
              String message = "The body formula should contain x free.";
              fText.setText(message);
              fText.selectAll();
              // to do fText.requestFocus();
            }
            else{            // good to go
              fRoot=root;


              TProofline newline = fProofController.supplyProofline();

               newline.fFormula = makeComprehensionFormula(fRoot,xVar);
               newline.fJustification = comprehensionJustification;
                newline.fSubprooflevel = fProofController.fModel.getHeadLastLine().fSubprooflevel;

                TUndoableProofEdit newEdit = fProofController.new TUndoableProofEdit();
                newEdit.fNewLines.add(newline);
                newEdit.doEdit();

                fProofController.removeInputPanel();

            }
          }
        }

        else {               //we have a root, getting justification

          String justification=fText.getText();

          if (justification.equals("Brief annotation? eg. Theorem 1"))
            justification="Theorem";   // correcting thoughtless input


          }

      }


      TFormula makeComprehensionFormula(TFormula body, TFormula inductVar){
    	  
    	//(Ally)(y&epsilon;{x:&Phi;[x]}&equiv;&Phi;[y])
    	  
    	  TFormula yVar = new TFormula(TFormula.variable,"y",null,null);
    	  
    	  TFormula bodyYforX=body.copyFormula();
    	           bodyYforX.subTermVar(bodyYforX,yVar.copyFormula(),xVar);       // &Phi;[y]
    	           
    	  TFormula comprehension= new TFormula(TFormula.comprehension,":",
    			  					xVar.copyFormula(),
    			  					body.copyFormula());
    	  TFormula xMemberComp= new TFormula(TFormula.predicator,strMemberOf,
									null,
									null);
    	  			xMemberComp.appendToFormulaList(yVar.copyFormula());
    	  			xMemberComp.appendToFormulaList(comprehension);
    	  
          TFormula equivForm = new TFormula(TFormula.binary,          // P(0)^Alln(P(n)->P(n'))
                  String.valueOf(chEquiv),
                  xMemberComp,
                  bodyYforX);
	  
    	  TFormula schemaInstance =new TFormula(TFormula.quantifier,     // Alln(P(n))
                                       String.valueOf(chUniquant),
                                       yVar.copyFormula(),
                                       equivForm);

        return 
        	schemaInstance;
      }
    }

 


    public void doComprehension(){
    	Button defaultButton;
    	TGWTProofInputPanel inputPane;


    //	JTextField text = new JTextField("Body (ie 'scope')? A well formed formula with free x.");

    //	   text.setDragEnabled(true);
    //	   text.selectAll();
    	   
           TextBox text = new TextBox();
           text.setText("Body (ie 'scope')? A well formed formula with free x.");
           text.selectAll();

    	  // defaultButton = new JButton(new ComprehensionAction(text,"Go"));
           
           defaultButton = new Button("Go");
           defaultButton.addClickHandler(new ComprehensionHandler(text));

    	   Button[]buttons = {fProofController.cancelButton(), defaultButton };  // put cancel on left
    	   inputPane = new TGWTProofInputPanel("Abstraction", text, buttons,fProofController.fInputPalette);


    	        fProofController.addInputPane(inputPane,TProofController.SELECT);

 //   	        inputPane.getRootPane().setDefaultButton(defaultButton);
//    	        fProofController.fInputPane.setVisible(true); // need this
//    	        text.requestFocus();         // so selected text shows
    	}

	
	
    void comprehensionMenuItem_actionPerformed(ActionEvent e) {
        doComprehension();
      }
	
	
	
	/************************ DoAxiom (template) **********************************/	
	
	void doAxiom(TFormula axiom, String justification){

		if (axiom!=null){

		    TProofline newline = fProofController.supplyProofline();

		    newline.fFormula = axiom.copyFormula();

		    newline.fJustification = justification;
		    newline.fSubprooflevel = fProofController.fModel.getHeadLastLine().fSubprooflevel;

		    TUndoableProofEdit newEdit = fProofController.new TUndoableProofEdit();
		    newEdit.fNewLines.add(newline);
		    newEdit.doEdit();
	}

		}
	
	/************************ Axiom of Extensionality **********************************/	
	
	
	private class ActionEvent{  //throw
		
	}
	
	
	void doExtensionality(){
		doAxiom(AxExt,ExtensionalityJust);

/*
		    TProofline newline = fProofController.supplyProofline();

		    newline.fFormula = AxExt.copyFormula();

		    newline.fJustification = ExtensionalityJust;
		    newline.fSubprooflevel = fProofController.fModel.getHeadLastLine().fSubprooflevel;

		    TUndoableProofEdit newEdit = fProofController.new TUndoableProofEdit();
		    newEdit.fNewLines.add(newline);
		    newEdit.doEdit();
*/
		}
	
    void extMenuItem_actionPerformed(ActionEvent e) {
    	doExtensionality();
      }
    
	/************************ Axiom of Subset **********************************/	
	
	void doSubset(){
		doAxiom(AxSubset,SubsetJust);
		}
	
    void subsetMenuItem_actionPerformed(ActionEvent e) {
    	doSubset();
      }
    
	/************************ Axiom of PowerSet **********************************/	
	
	void doPowerSet(){
		doAxiom(AxPowerSet,PowerSetJust);
		}
	
    void powerSetMenuItem_actionPerformed(ActionEvent e) {
    	doPowerSet();
      } 
    
	/************************ Axiom of Emptyset **********************************/	
	
	void doEmpty(){
		doAxiom(AxEmpty,EmptyJust);
		}
	
    void emptyMenuItem_actionPerformed(ActionEvent e) {
    	doEmpty();
      } 
    
/************************ Axiom of Complement **********************************/	
	
	void doComplement(){
		doAxiom(AxComplement,ComplementJust);
		}
	
    void complementMenuItem_actionPerformed(ActionEvent e) {
    	doComplement();
      } 
 
/************************ Axiom of Intersection **********************************/	
	
	void doIntersection(){
		doAxiom(AxIntersection,IntersectionJust);
		}
	
    void intersectionMenuItem_actionPerformed(ActionEvent e) {
    	doIntersection();
      } 
  
/************************ Axiom of Pair **********************************/	
	
	void doPair(){
		doAxiom(AxPair,PairJust);
		}
	
    void pairMenuItem_actionPerformed(ActionEvent e) {
    	doPair();
      }     
    
/************************ Axiom of Union **********************************/	
	
	void doUnion(){
		doAxiom(AxUnion,UnionJust);
		}
	
    void unionMenuItem_actionPerformed(ActionEvent e) {
    	doUnion();
      } 
/************************ Axiom of Universeset **********************************/	
	
	void doUniverse(){
		doAxiom(AxUniverse,UniverseJust);
		}
	
    void universeMenuItem_actionPerformed(ActionEvent e) {
    	doUniverse();
      }    
	
/************************ Axiom of XProd **********************************/	
	
	void doXProd(){
		doAxiom(AxXProd,XProdJust);
		}
	
    void xProdMenuItem_actionPerformed(ActionEvent e) {
    	doXProd();
      }    
    
	/************************ Rule of DoTheorem **********************************/

//NOT USED AT PRESENT

    public class TheoremAction implements ClickHandler{
      TextBox fText;
      TFormula fRoot=null;

     /*We have here to get two things out of the User:- the root of new formula, and its
      justification. So when initialized fRoot is set to null. Then, when we get the
      root it is set to a value, and we look for the justification.  */


       public TheoremAction(TextBox text, String label){
        // putValue(NAME, label);

         fText=text;
       }

        public void onClick(ClickEvent event){


        if (fRoot==null){
          boolean useFilter = true;
          ArrayList dummy = new ArrayList();

          String aString= TUtilities.defaultFilter(fText.getText());

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
            // to do fText.requestFocus();
          }
          else {
            fRoot=root;      // found root move to second stage

            String message = "Brief annotation? eg. Theorem 1";
            fText.setText(message);
            fText.selectAll();
            // to do fText.requestFocus();
          }
        }

        else {               //we have a root, getting justification

          String justification=fText.getText();

          if (justification.equals("Brief annotation? eg. Theorem 1"))
            justification="Theorem";   // correcting thoughtless input

          TProofline newline = fProofController.supplyProofline();

           newline.fFormula = fRoot;
           newline.fJustification = justification;
            newline.fSubprooflevel = fProofController.fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = fProofController.new TUndoableProofEdit();
            newEdit.fNewLines.add(newline);
            newEdit.doEdit();

            fProofController.removeInputPanel();
          }

      }

    }
    
  
/* to do	
    public void doTheorem(){
    	JButton defaultButton;
    	TProofInputPanel inputPane;


    	JTextField text = new JTextField("Theorem?");

    	   text.setDragEnabled(true);
    	   text.selectAll();

    	   defaultButton = new JButton(new TheoremAction(text,"Go"));

    	   JButton[]buttons = {new JButton(fProofController.new CancelAction()), defaultButton };  // put cancel on left
    	   inputPane = new TProofInputPanel("Doing Theorem", text, buttons);


    	        fProofController.addInputPane(inputPane);

    	        inputPane.getRootPane().setDefaultButton(defaultButton);
    	        fProofController.fInputPane.setVisible(true); // need this
    	        text.requestFocus();         // so selected text shows
    	}
    
*/
    
/************************ End of Rule of DoTheorem **********************************/  
    
    
    
}

/* to do

class SetTheory_comprehensionMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_comprehensionMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.comprehensionMenuItem_actionPerformed(e);
	  }
	}


class SetTheory_extMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_extMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.extMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_subsetMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_subsetMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.subsetMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_powerSetMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_powerSetMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.powerSetMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_emptyMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_emptyMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.emptyMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_complementMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_complementMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.complementMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_intersectionMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_intersectionMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.intersectionMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_pairMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_pairMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.pairMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_unionMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_unionMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.unionMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_universeMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_universeMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.universeMenuItem_actionPerformed(e);
	  }
	}

class SetTheory_xProdMenuItem_actionAdapter implements java.awt.event.ActionListener {
	  SetTheory adaptee;

	  SetTheory_xProdMenuItem_actionAdapter(SetTheory adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.xProdMenuItem_actionPerformed(e);
	  }
	}
	
	*/
