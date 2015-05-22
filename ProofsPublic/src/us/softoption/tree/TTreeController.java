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

package us.softoption.tree;



/*
 This is to provide 'backroom' support for Trees in javascript


 */





import static us.softoption.infrastructure.Symbols.chKappa;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chRho;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;
import static us.softoption.parser.TFormula.binary;
import static us.softoption.parser.TFormula.modalKappa;
import static us.softoption.parser.TFormula.modalRho;
import static us.softoption.parser.TFormula.quantifier;
import static us.softoption.parser.TFormula.unary;
import static us.softoption.parser.TFormula.variable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.Symbols;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class TTreeController{
	
	  private TJournal fJournal;       //interface to receive writing  (sometimes a Browser sometimes an applet)
      // can be null in some cases, so check for it.
	
	 TReset   fTreesJSClient;         // this is in part its view or its html and css host
	  
	  VerticalPanel fInputPanel = null;
	  
	  protected TParser fParser;
	  public String fParserName="";        //default, gets shown in title bar of browser, subclasses change
	  
	  private String fStartStr="";
	  String fTreeStr="";
	  TGWTTestNode fRoot;
	  TGWTTree fGWTTree = null;  // the root of the tree, but as a soft option TestNode, 
	                                       // not as the 
	  
	  TTreeDisplayCellTable fDisplayCellTable= null;
	  TTreeDisplayTableModel fDisplayTableModel= null;
	  
	  
	  final static String setMember = "SM";

	  // these following do not want to be static because there can be several panels open at once

	  // in some html settings we want non-breaking spaces here asciichr160
	  // so, for example, it does not make a table column narrow
	  // intend to use s = s.replaceAll(" ","\u00a0");  when needed
	  
	  String andDJustification = " " + Symbols.chAnd + "D";
	  String negDJustification = " ~~D";
	  String implicDJustification = " " + Symbols.chImplic + "D";
	  String equivDJustification = " " + Symbols.chEquiv + "D";
	  String exiDJustification = " " + Symbols.chExiquant + "D";
	  String uniqueDJustification = " " + Symbols.chUnique + "D";
	  String negUniqueDJustification = " ~" + Symbols.chUnique + "D";
	  String negAndDJustification = " ~" + Symbols.chAnd + "D";
	  String negArrowDJustification = " ~" + Symbols.chImplic + "D";
	  String negEquivDJustification = " ~" + Symbols.chEquiv + "D";
	  String negExiDJustification = " ~" + Symbols.chExiquant + "D";
	  String negUniDJustification = " ~" + Symbols.chUniquant + "D";
	  String noreDJustification = " ~" + Symbols.chOr + "D";
	  String orDJustification = " " + Symbols.chOr + "D";
	  String UDJustification = " "+Symbols.chUniquant+ "D";
	  String identityDJustification = " =D";
	  String identityIJustification = " =I";

	  String notPossibleJustification =" MN";
	  String s5PossJustification =" "+chModalPossible+"S5";
	  String s5NecessJustification =" "+chModalNecessary+"S5";
	  String tNecessJustification =" "+chModalNecessary+"T";
	  String rPossJustification =" "+chModalPossible+"R";
	  String rNecessJustification =" "+chModalNecessary+"R";
	  String rNecessNecessJustification =" "+chModalNecessary+chModalNecessary+"R";
	  String symNecessNecessJustification =" "+chModalNecessary+chModalNecessary+"SymR";
	  String accessRefJustification= " Refl";
	  String accessSymJustification= " Sym";
	  String accessTransJustification= " Trans";
	  
	  String kPNJustification =" KPN";
	  String pRJustification =" PR";
	  String kRJustification =" KR";
	  String kKRJustification =" KKR";
	  String kTRJustification =" KTR";
	  String trKRJustification =" TrKR";
	  
	  String typeEJustification =" =Type";
	  
	 

	  String chNeg=""+Symbols.chNeg;
	  String chAnd=""+Symbols.chAnd;
	  String chOr=""+Symbols.chOr;
	  String chImplic=""+Symbols.chImplic;
	  String chEquiv=""+Symbols.chEquiv;
	  String chUniquant=""+Symbols.chUniquant;
	  String chExiquant=""+Symbols.chExiquant;
	  String chIdentity="=";  
	  
	  boolean s4Switch=false;
	  boolean s5Switch=true;
	  boolean kSwitch=false;
	  boolean tSwitch=false;
	  boolean s5AltSwitch=false;
	  
	  boolean s4Rules=false;
	  boolean kRules=false;
	  boolean tRules=false;
	  boolean s5AltRules=false;
	  boolean s5Rules=true;
	  
	    RadioButton fS5=new RadioButton("group", "S5");
	    RadioButton fK=new RadioButton("group", "K");
	    RadioButton fT=new RadioButton("group", "T");
	    RadioButton fS4=new RadioButton("group", "S4");
	    RadioButton fS5Alt=new RadioButton("group", "S5Alt");
	    
	    Button fReflex= new Button("Ref.",new ReflexHandler());	
	    Button fSymm= new Button("Symm.",new SymmHandler());
	    Button fTrans= new Button("Trans.",new TransHandler());

	    static final boolean SELECT=true;
	  
	  
	  
	  public TTreeController(){
		    
	//	  initializeParser();
	//	  initializePalettes();

		  fDisplayCellTable = new TTreeDisplayCellTable(this);
		  fDisplayTableModel = fDisplayCellTable.getModel();
		  fDisplayTableModel.setDisplay(fDisplayCellTable);
		  
		  localizeJustStrings();
	  }
	  
	  public TTreeController(TParser aParser){
		  fParser=aParser;
		    
	//	  initializeParser();
	//	  initializePalettes();

		  fDisplayCellTable = new TTreeDisplayCellTable(this);
		  fDisplayTableModel = fDisplayCellTable.getModel();
		  fDisplayTableModel.setDisplay(fDisplayCellTable);
		  
		  localizeJustStrings();

	  }
	
	
	
	 public TTreeController(TJournal itsJournal){   // when it is running without a Browser, eg in an applet

		 this();

		    //   fApplication=itsApplication;

		  /*at the moment the JournalPane is just text (not subclassed) so, for
		  the present it is part of the Browser. The main edit menu (and other menus)
		  in the Browser, refer to the journalPane. So it is natural to leave it there,*/

		//fBrowser= null;
		fJournal=itsJournal;
		
		  fDisplayCellTable = new TTreeDisplayCellTable(this);
		  fDisplayTableModel = fDisplayCellTable.getModel();
		  fDisplayTableModel.setDisplay(fDisplayCellTable);
		  
		  localizeJustStrings();
		}
	 
	 public TTreeController(TParser aParser, TReset aClient,TJournal itsJournal, VerticalPanel inputPanel,
			 TGWTTree itsTree,TTreeDisplayCellTable itsDisplay){
		  fParser=aParser;
		  fTreesJSClient=aClient;
		  fJournal=itsJournal;
		  fInputPanel = inputPanel;
		  fGWTTree=itsTree;
		  
		  fDisplayCellTable = itsDisplay;
		  fDisplayCellTable.setController(this);
		  fDisplayTableModel = fDisplayCellTable.getModel();
		  fDisplayTableModel.setDisplay(fDisplayCellTable);
		  
		  localizeJustStrings();
	 }
		    
	 
	/********** More initialization ************
	 * 
	 */

	 void localizeJustStrings(){
			
			String localNeg=fParser.translateConnective(chNeg);
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
			
			
			
			
		}	 
	 
	 
	 /************************** Starting Trees **********************/
	 
	 public void startTree(String inputStr){


		//TO DO	   // dismantleProof(); //{previous one}

		    removeInputPanel();

		    initTree();

		   if (load(inputStr))
			   ;

		  setStartStr(inputStr);  //for restart

		  }
	 
	 void initializeTreeModel(){
		  fRoot = new TGWTTestNode(fParser,null);            //does not initialize TreeModel
		  fDisplayTableModel.setHostRoot(fRoot);             // Table Model knows its root
		  
		  fGWTTree.removeItems();                            //get rid of earlier
		  fGWTTree.addItem(fRoot);                           //display knows its root
		  
		  
	//	  fGWTTree= new TGWTTree(fRoot);
		  fRoot.fGWTTree=fGWTTree;                          //root knows its Tree
		}
	 
	 
	 void initTree(){
//			TO DO   jScrollPane1.getViewport().remove(fTreeTableView);

		   initializeTreeModel();

//				TO DO     fTreeTableModel = new TTreeTableModel(/*fGWTTree,*/ /*fRoot); // this is where the data is

//				TO DO  	   fTreeTableView = new TTreeTableView(fTreeTableModel);

//				TO DO  	   jScrollPane1.getViewport().add(fTreeTableView, null); 
			 }



		/*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
		compound terms eg Pf(a,b),Hc.

		Also in some systems a relation Lxy is written L(x,y) ie also with commas


		We want to separate the premises but not the terms. So we will change the
		premise comma separators to another character. For the moment '!'*/


		private static char chSpecialSeparator='#';  //was ! ie unique!

		private String changeListSeparator(String input){

		int nested=0;
		char currCh;

		StringBuffer output= new StringBuffer(input);
		for (int i=0;i<input.length();i++){
		currCh=output.charAt(i);

		if (currCh==chSmallLeftBracket)
		nested++;
		if (currCh==chSmallRightBracket)
		nested--;

		if ((nested<1)&&(currCh==Symbols.chComma))    //commas separating the list of premises are not nested
		output.setCharAt(i,chSpecialSeparator);
		}

		return
		output.toString();
		}


String [] splitPremisesAndConclusion(String inputStr){
	String [] output= {strNull,strNull};
	
	int firstTherefore=inputStr.indexOf(Symbols.chTherefore);
	
	if (firstTherefore<0)
		output[0]=inputStr;
	else{
		output[0]=inputStr.substring(0, firstTherefore);  //omit the therefore
		output[1]=inputStr.substring(firstTherefore+1);
	}
	return
			output;
	
}
		
		
		
		boolean load(String inputStr){                       //similar routine in TMyProofPanel

			   TGWTTestNode currentNode=null;
			   int lineNo=1;

			  fParser.initializeErrorString();

			   ArrayList dummy=new ArrayList();
			   boolean wellformed = true;

			   fTreeStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there

		//	   Window.alert("InputStr " +inputStr);
			   
			   
			        if ((inputStr==null)||inputStr.equals(strNull)){
			          return
			              false;                // cannot be empty for a tree
			        }
/*older code
			 String[]premisesAndConclusion = inputStr.split(String.valueOf(Symbols.chTherefore),2);  /* they may input two
			         therefore symbols, in which case we'll split at the first and let the parser report the second 
*/
			        
			 String[]premisesAndConclusion=splitPremisesAndConclusion(inputStr);
			    
		//	 Window.alert("premisesAndConclusion[0] " +premisesAndConclusion[0]);
		//	 Window.alert("premisesAndConclusion[1] " +premisesAndConclusion[1]);
			 
			 if (premisesAndConclusion[0]!=null&&
			     !premisesAndConclusion[0].equals(strNull)){  // there are premises


			      premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);  //kludge


			      StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSpecialSeparator));

			   while ((premises.hasMoreTokens())&&wellformed){
			      inputStr=premises.nextToken();

			      if (!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
			             TFormula root = new TFormula();
			             StringReader aReader = new StringReader(inputStr);


			             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

			             if (!wellformed){

			                bugAlert ("Error on reading.",fParser.fCurrCh + TConstants.fErrors12 + 
			            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""));
			            	 
			            	// writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
			            	//	   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
			             }
			             else
			                 {//addPremise(root);

			                  if (currentNode==null)          //first one is a special case
			                  {fRoot.addToAntecedents(root);
			                      fRoot.fJustification=setMember;
			                   if (fParser.containsModalOperator(root))
			                     fRoot.fWorld=fParser.startWorld();
			                   fRoot.fLineno=lineNo;
			                   
			                   fRoot.setHTML(inputStr);  //debug only

			                   lineNo++;

			                   	fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,fRoot);              //need a listener for this, need it no Insert
			                    currentNode=fRoot;
			                  }
			                  else{
			                	TGWTTestNode newNode = (fRoot.supplyTGWTTestNode(fParser,fGWTTree));   //using one node for each formula
			                    newNode.addToAntecedents(root);
			                    newNode.fJustification=setMember;
			                    if (fParser.containsModalOperator(root))
			                     newNode.fWorld=fParser.startWorld();
			                    newNode.fLineno=lineNo;
			                    
			                    newNode.setHTML(inputStr);  //debug only
			                    
			                  //  currentNode.straightInsert(currentNode,newNode,null,1);  // change
			                    currentNode.straightInsert(newNode,null);  // change
			                    
			                    fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,newNode);              //need a listener for this
			                    currentNode=newNode;

			                    lineNo++;
			                  }



			                 if (fTreeStr.length()==0)
			                   fTreeStr=inputStr;
			                 else
			                   fTreeStr+=Symbols.chComma+inputStr;
			                 }
			             }
			   }
			      }          // done with premises

			                //on to conclusion
			      if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
			        inputStr = premisesAndConclusion[1];

			        if (!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
			             TFormula root = new TFormula();
			             StringReader aReader = new StringReader(inputStr);

			             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

			             if (!wellformed){
			             //  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");
			              /* fTreeDocument.*/
			            	 bugAlert ("Error on reading.",fParser.fCurrCh + TConstants.fErrors12 + 
				            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "")); 
			            	 //writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
			            	//	   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
			             }else
			                 {//addConclusion(root);
			            	 
			//            	 Window.alert("4Conclusion Command Fired");

			                   TFormula newFormula = new TFormula();

			                   newFormula.fKind = TFormula.unary;
			                   newFormula.fInfo = String.valueOf(Symbols.chNeg);
			                   newFormula.fRLink = root;

			                   root=newFormula;
			                   // add its negation
			                   if (currentNode==null)          //first one is a special case
			                     {fRoot.addToAntecedents(root);
			                       fRoot.fJustification=setMember;
			                       if (fParser.containsModalOperator(root))
			                          fRoot.fWorld=fParser.startWorld();
			                       fRoot.fLineno=lineNo;

			                       lineNo++;   // really no need, there are no more
			                       
			   //                    Window.alert("5Conclusion Command Fired");

			                       fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,fRoot);              //need a listener for this, need it no Insert
			                       currentNode=fRoot;
			                     }
			                     else{

			                       TGWTTestNode newNode = (fRoot.supplyTGWTTestNode(fParser,fGWTTree)); //to do (TGWTTestNode) (fRoot.supplyTTestNode(fParser,fGWTTree)); //using one node for each formula
			                       newNode.addToAntecedents(root);
			                       newNode.fJustification = setMember;
			                       if (fParser.containsModalOperator(root))
			                          newNode.fWorld=fParser.startWorld();
			                       newNode.fLineno = lineNo;
			                       
			                       newNode.setHTML(chNeg + inputStr);  //debug only
			                       
			                       //currentNode.straightInsert(currentNode,newNode, null,1);
			                       currentNode.straightInsert(newNode,null);  // change
			                       
			                       fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newNode); //need a listener for this
			                       currentNode = newNode;
			                     }

			                 fTreeStr+=Symbols.chTherefore+inputStr;
			                 
		//	                 Window.alert(fTreeStr);
			                 }    //else actual conclusion
			             }      //null conclusion

			    };   //end off conclusion

			        return
			            wellformed;

			     }




		/************/	
		
	
		/*************************************************************************/	
		
		
	/*********************** Extending Trees ***********************************/
		
/*		from TJournal
 * 
 
void extendMenuItem_actionPerformed(ActionEvent e) {

    final TGWTTestNode [] selectedNodes= fTreeTableView.selectedDataNodes();
    TFormula root;


    if ((selectedNodes!=null)&&(selectedNodes.length==1))
    {
      TGWTTestNode selected= selectedNodes[0];

      TFormula theFormula=null;

      if (!selected.fDead&&               //can select dead for closing formula
          selected.fAntecedents!=null&&
          selected.fAntecedents.size()==1){
        theFormula=(TFormula)(selected.fAntecedents.get(0));
      }
      else
        return
          ;

    switch (selected.typeOfFormula(theFormula)){

      case TTestNode.aand:
        doAnd(selected,theFormula);
        break;

      case TTestNode.implic:
        doImplic(selected,theFormula);
        break;

      case TTestNode.doubleneg:
        doDoubleNeg(selected,theFormula);
        break;

/*** Modal ***
      case TTestNode.notPossible:
  doNegPossible(selected,theFormula);
  break;

case TTestNode.notNecessary:
doNegNecessary(selected,theFormula);
break;

case TTestNode.modalPossible:
  if (s5Rules)
     doPossible(selected,theFormula);
  else
  if (kRules)
     doPossibleR(selected,theFormula);
break;

case TTestNode.modalNecessary:
   if (s5Rules)
      doNecessary(selected,theFormula);
   else
   if (tRules)
	  doNecessaryT(selected,theFormula);  // order is important here, two right ways (S% and T) and one wrong way
   else
   if (kRules)
	   bugAlert("Trying "+chModalNecessary+ "R","With R necess, you need also to select a second line with an 'Access' relation.");


break;

/***** End of Modal ********

/*** Epistemic ***

case TTestNode.notModalKappa:
	doNotKnow(selected,theFormula);
	break;

case TTestNode.notModalRho:
	doNotKnowNot(selected,theFormula);
	break;
	
case TTestNode.modalRho:
	doPossibleKnow(selected,theFormula);
	break;
	
case TTestNode.modalKappa:
	doKnow(selected,theFormula);
	break;	

case TTestNode.modalDoubleKappa:
	doKnow(selected,theFormula);
	doTRKR(selected,theFormula);
	break;	
	
	
/*** End Epistemic ***


      case TTestNode.equivv:
        doEquivv(selected,theFormula);
        break;

      case TTestNode.exi:
       doExi(selected,theFormula);
       break;
       
      case TTestNode.unique:
          doUnique(selected,theFormula);
          break;

     case TTestNode.negand:
       doNegAnd(selected,theFormula);
       break;

     case TTestNode.negarrow:
       doNegArrow(selected,theFormula);
       break;

     case TTestNode.negexi:
       doNegExi(selected,theFormula);
       break;

     case TTestNode.neguni:
       doNegUni(selected,theFormula);
       break;

     case TTestNode.nequiv:
       doNegEquiv(selected,theFormula);
       break;


     case TTestNode.nore:
       doNore(selected,theFormula);
       break;
       
     case TTestNode.negunique:
         doNegUnique(selected,theFormula);
         break;

      case TTestNode.ore:
        doOr(selected,theFormula);
        break;

      case TTestNode.uni:
        doUni(selected,theFormula);
        break;

      case TTestNode.typedUni:
        root=fParser.expandTypeUni(theFormula);
        if (root!=null)
           doUni(selected,root);
        break;
      case TTestNode.negTypedUni:    //FIX

        root=fParser.expandTypeUni(theFormula.fRLink);

        root = new TFormula(TFormula.unary,
                             String.valueOf(chNeg),
                             null,
                             root);

        doNegUni(selected,root);
        break;
      case TTestNode.typedExi:
        root=fParser.expandTypeExi(theFormula);
        if (root!=null)
          doExi(selected,root);
        break;

      case TTestNode.negTypedExi:    //FIX
        root=fParser.expandTypeExi(theFormula.fRLink);

        root = new TFormula(TFormula.unary,
                     String.valueOf(chNeg),
                     null,
                     root);

        doNegExi(selected,root);

        break;

    }

  }

  if ((selectedNodes!=null)&&(selectedNodes.length==2)){  //might be IE or necessaryR or necessarynecessaryR or necnecSymR

     if (isKRPossible(selectedNodes)){
    	 doKR(selectedNodes);
    	 return;
     }
	  
	  if (isNecessaryRPossible(selectedNodes)&& kRules){
	  
	if (s4Rules/*&&isNecessaryNecessaryRPossible(selectedNodes)*){  //might be double necessary, could do either

	  	  
	  getTheChoice(new AbstractAction(chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryR(selectedNodes);
      }},           //the calling actions must remove the input pane

    new AbstractAction(""+chModalNecessary+chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryNecessaryR(selectedNodes);
      }},
    "You have a choice between the rules Necessary R and Necessary Necessary R.",
    "Please choose");}
	else
		doNecessaryR(selectedNodes);
    
	return ;

  }

  //else

    TGWTTestNode firstSelected = selectedNodes[0];
    TGWTTestNode secondSelected = selectedNodes[1];

    TFormula firstFormula = null;
    TFormula secondFormula = null;

    if ((firstSelected.fAntecedents != null &&
        firstSelected.fAntecedents.size() == 1)&&
       (secondSelected.fAntecedents != null &&
        secondSelected.fAntecedents.size() == 1)&&
       firstSelected.fWorld.equals(secondSelected.fWorld)

       )
   {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
      if (typeEPossible(firstSelected,secondSelected,firstFormula,secondFormula))
    	  doTypeE(firstSelected,secondSelected,firstFormula,secondFormula);
      else   
    	  doIE(firstSelected,secondSelected,firstFormula,secondFormula);
    }

  }

}
		
*/
		
		
ArrayList <TGWTTestNode> selectedNodes(TGWTTestNode root){
	ArrayList <TGWTTestNode> result= new ArrayList();
	
	if (root!=null){
		
		if (root.fSelected)
			result.add(root);
		
		int numChildren = root.getChildCount();
		
		ArrayList selectedChildren;
		
		for (int i=0;i<numChildren;i++){
			selectedChildren=selectedNodes((TGWTTestNode)(root.getChild(i)));
			result.addAll(selectedChildren);
			
		}
	
	}
	
	return
			result;
}

/*
public void bugAlert(String label,String message){
	   CancelAction ok = new CancelAction();

	   ok.putValue(AbstractAction.NAME, "OK");

	   JButton okButton = new JButton(ok);

	   JButton [] buttons = {okButton};

	   JTextField text = new JTextField(message);

	   text.selectAll();

	   TProofInputPanel inputPane = new TProofInputPanel(label, text
	     ,buttons);

	   addInputPane(inputPane);

	   inputPane.getRootPane().setDefaultButton(okButton);

	   fInputPane.setVisible(true);

	   text.requestFocus();

	  // fInputPane.setVisible(true);   // do I need two of these?


	  }


	  void removeBugAlert(){
	  removeInputPane();
	}

*/


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


public void addInputPane(TGWTTreeInputPanel inputPane, boolean selectText){
	
	fInputPanel.clear();
	
 //   if (fInputPane!=null)
 //      removeInputPane();
	
	 fInputPanel.add(inputPane);   // we want the focus on the text
	 
	 if (selectText)
		 inputPane.selectAllInTextBox();

	/*
     fInputPane=inputPane;
     fInputPane.setAlignmentX((float) 0.0);
     fInputPane.setAlignmentY((float) 0.0);

     this.add(fInputPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
             ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

     fInputPane.setVisible(false);  // experiment, but need it */

  /*   fRulesMenu.setEnabled(false);   //we're modal and don't want anything else done
     fAdvancedRulesMenu.setEnabled(false);
     fEditMenu.setEnabled(false);
     fWizardMenu.setEnabled(false);*/

  disableMenus();

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


void enableMenus(){

	fTreesJSClient.enableMenus();



	}

void disableMenus(){
	fTreesJSClient.disableMenus();
	}



/****************************** Menu Commands ************************************/

/*
void closeMenuItem_actionPerformed(ActionEvent e) {
	  TGWTTestNode [] selectedNodes= fTreeTableView.selectedDataNodes();


	if ((selectedNodes!=null)&&(selectedNodes.length==1))
	     closeFromNegationOfIdentity();
	else{
	     deSelectAll();

	if ((selectedNodes==null)||(selectedNodes.length!=2))
	   bugAlert("Trying to Close Branch. Warning.", "You need to select two formulas.");
	 else{

	  TGWTTestNode selected= selectedNodes[0];
	  TGWTTestNode secondSelected= selectedNodes[1];


	  if (!selected.fWorld.equals(TGWTTestNode.nullWorld)&&      //if the world is null we understand the formula as being universal ie without modal operators
		  !secondSelected.fWorld.equals(TGWTTestNode.nullWorld)&&
		  !selected.fWorld.equals(secondSelected.fWorld)){  //they need to be in the same world, for modal
	    bugAlert("Trying to Close Branch. Warning.", "The selected two formulas need to be in the same world.");
	    return
	        ;
	  }
	  
	  
	  TFormula firstFormula=null;

	 if (selected.fAntecedents!=null&&
	    selected.fAntecedents.size()==1){
	      firstFormula=(TFormula)(selected.fAntecedents.get(0));
	 }
	 else
	  return
	    ;

	  TFormula secondFormula=null;

	 if (secondSelected.fAntecedents!=null&&
	    secondSelected.fAntecedents.size()==1){
	      secondFormula=(TFormula)(secondSelected.fAntecedents.get(0));
	 }
	 else
	  return
	    ;

	 if (!TFormula.formulasContradict(firstFormula,secondFormula))
	   return
	       ;


	 //They need to be in the same branch

	 TreeNode[] firstbranch = selected.fSwingTreeNode.getPath();
	 TreeNode[] secondbranch = secondSelected.fSwingTreeNode.getPath();

	 boolean firstHigher= false;
	 for (int i=0;(i<secondbranch.length)&&!firstHigher;i++)
	   firstHigher=(secondbranch[i]==selected.fSwingTreeNode);

	 boolean secondHigher= false;
	 for (int i=0;(i<firstbranch.length)&&!secondHigher;i++)
	   secondHigher=(firstbranch[i]==secondSelected.fSwingTreeNode);

	 if (!firstHigher&&
	     !secondHigher){
	   bugAlert("Trying to Close Branch. Warning.", "The two formulas need to be in the same branch.");
	   return
	       ;
	 }

	 else{   //we are in business

	   TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);

	   newDataNode.fClosed = true;
	   // newDataNode.fDead=true;  // don't make it dead else renderer will tick it

	   // we need to add it after the lower selection
	   if (firstHigher) {
	     secondSelected.fSwingTreeNode.removeAllChildren(); //immediate close
	     secondSelected.straightInsert(newDataNode, null);
	   }
	   else {
	     selected.fSwingTreeNode.removeAllChildren(); //immediate close
	     selected.straightInsert(newDataNode, null);
	   }
	   fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newDataNode); //need a listener for this

	   //  deSelectAll();
	 }
	}
	}
	}

*/



void closeFromNegationOfIdentity(){
	//  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();
		TGWTTestNode selected=null;
		TGWTTestNode secondSelected=null;
	    
	    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);

	     fRoot.deSelectAll();

	  if ((selectedNodes!=null)&&(selectedNodes.size()==1)){
		  selected= selectedNodes.get(0);
	    TFormula firstFormula=null;

	    if (selected.fAntecedents!=null&&
	       selected.fAntecedents.size()==1){
	       firstFormula=(TFormula)(selected.fAntecedents.get(0));

	      if (!firstFormula.isNegIdentity(firstFormula))
	        bugAlert("Trying to Close Branch. Warning.", "Select two formulas that contradict, or a single negation of identity.");
	      else{

	    	  TGWTTestNode newDataNode = TGWTTestNode.supplyClosed();

	 //       newDataNode.fClosed = true;
	// newDataNode.fDead=true;  // don't make it dead else renderer will tick it

	    	selected.removeItems();; //immediate close //immediate close
	        selected.straightInsert(newDataNode, null);

	        TGWTTestNode dummy=null;
			   selected.deSelectAll();
			  // fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newDataNode); //need a listener for this
			   fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);


	      }


	}
	else
	 return
	   ;

	  }
	    ;

	}








public void closeBranch(){
	TGWTTestNode selected=null;
	TGWTTestNode secondSelected=null;
    
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);
    
    if (selectedNodes==null)
    	return;
    
    int numSelected=selectedNodes.size();
    
    switch (numSelected){
    case 0:
    	bugAlert("Trying to Close Branch. Warning.", "You need to select some formulas.");
    	break;
    case 1:
    	closeFromNegationOfIdentity();	
    	break;
    case 2:
    
      selected= selectedNodes.get(0);
      secondSelected= selectedNodes.get(1);
         
      // need to be in same world
	  if (!selected.fWorld.equals(TGWTTestNode.nullWorld)&&      //if the world is null we understand the formula as being universal ie without modal operators
			  !secondSelected.fWorld.equals(TGWTTestNode.nullWorld)&&
			  !selected.fWorld.equals(secondSelected.fWorld)){  //they need to be in the same world, for modal
		    bugAlert("Trying to Close Branch. Warning.", "The selected two formulas need to be in the same world.");
		    return
		        ;
		  }
		  
	  // they need to contradict
	  
	  TFormula firstFormula=null;

	 if (selected.fAntecedents!=null&&
	    selected.fAntecedents.size()==1){
	      firstFormula=(TFormula)(selected.fAntecedents.get(0));
	 }
	 else
	  return
	    ;

	  TFormula secondFormula=null;

	 if (secondSelected.fAntecedents!=null&&
	    secondSelected.fAntecedents.size()==1){
	      secondFormula=(TFormula)(secondSelected.fAntecedents.get(0));
	 }
	 else
	  return
	    ;

	 if (!TFormula.formulasContradict(firstFormula,secondFormula)){
		 bugAlert("Trying to Close Branch. Warning.", "The two formulas need to contradict each other.");
	   return
	       ;
	 }
	  
	  
	  //path is root to item
    
	//They need to be in the same branch
	  
	  ArrayList<TGWTTestNode> firstbranch = selected.getPath();
	  ArrayList<TGWTTestNode> secondbranch = secondSelected.getPath();
	  
	  boolean firstHigher= false;
		 for (int i=0;(i<secondbranch.size())&&!firstHigher;i++)
		   firstHigher=(secondbranch.get(i)==selected);
	  
		 boolean secondHigher= false;
		 for (int i=0;(i<firstbranch.size())&&!secondHigher;i++)
		   secondHigher=(firstbranch.get(i)==secondSelected);
		 
		 if (!firstHigher&&
			     !secondHigher){
			   bugAlert("Trying to Close Branch. Warning.", "The two formulas need to be in the same branch.");
			   return
			       ;
 		 }
		 else{ //we are in business
			
			 TGWTTestNode newDataNode = TGWTTestNode.supplyClosed(); //new TGWTTestNode(fParser,fGWTTree);
			 
			 newDataNode.fClosed = true;
			 
			 // we need to add it after the lower selection
			   if (firstHigher) {
			     secondSelected.removeItems(); //immediate close
			     secondSelected.straightInsert(newDataNode, null);
			   }
			   else {
			     selected.removeItems();; //immediate close
			     selected.straightInsert(newDataNode, null);
			   }
			   TGWTTestNode dummy=null;
			   selected.deSelectAll();
			  // fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newDataNode); //need a listener for this
			   fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
			 
		 }
    
		 
/*
 * 
 * else{   //we are in business

	   TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);

	   newDataNode.fClosed = true;
	   // newDataNode.fDead=true;  // don't make it dead else renderer will tick it

	   // we need to add it after the lower selection
	   if (firstHigher) {
	     secondSelected.fSwingTreeNode.removeAllChildren(); //immediate close
	     secondSelected.straightInsert(newDataNode, null);
	   }
	   else {
	     selected.fSwingTreeNode.removeAllChildren(); //immediate close
	     selected.straightInsert(newDataNode, null);
	   }
	   fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newDataNode); //need a listener for this

	   //  deSelectAll();
	 }
 * 
 * 
 * 
 * 		 
 */
		 
		 
    
    break;
    default:
    	 bugAlert("Trying to Close Branch. Warning.", "You need to select one or two formulas.");
    
    }
	
//	bugAlert("","");
	
}


public void executeIsClosed(){
if (isTreeClosed())
    bugAlert("Tree closed?","Yes, it is. All branches are closed");
else
    bugAlert("Tree closed?","No, there is an open branch.");
}

public void executeIsOpenAndComplete(){
if (isABranchOpenAndComplete())
    bugAlert("Is there a complete open branch?","Yes, there is one.");
else
    bugAlert("Is there a complete open branch?","No, every open branch is incomplete.") ;

}

public void executeII(){
    TGWTTestNode selected=null;
    
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);

    if ((selectedNodes!=null)&&(selectedNodes.size()==1))
    	{
      	selected= selectedNodes.get(0);
      	int rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	int selectedDepth= fRoot.getNodeDepth(selected);
    	int depthIncrement= rootDepth - selectedDepth;
    	
    	if (selected!=null)
    		selected.deSelectAll(); 
      	    	
      	doII(selected,depthIncrement);
    	}
    
	{TGWTTestNode dummy =fRoot;
	
	//TO DO it may be better to do this somewhere else
	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
	}
}

public void executeAnaCon(){
	  //stub
}


public boolean isTreeClosed(){
	   return
	       (fRoot!=null?fRoot.isTreeClosed():true);
	 }

public boolean isABranchOpenAndComplete(){
	  return
	    (fRoot!=null?fRoot.isABranchOpenAndComplete():true);
	}


/*
	public boolean isABranchOpenAndComplete(){
	  return
	    (fTreeDataRoot!=null?fTreeDataRoot.isABranchOpenAndComplete():true);
	}

	  public boolean isABranchOpenAndClosable(){
	    return
	      (fTreeDataRoot!=null?fTreeDataRoot.isABranchOpenAndClosable():false);
	  }   

*/  







		
		
		public void extendTree(){
	
//we'll assume that the first line is selected
	
/*the Tree can be thought of as a list of lines. So the last line is the depth of the 
 * tree. When we extend a selected node in the middle of the tree, we want to extend
 * all open branches below it. So the new line must be at (depth +1) and this is where
 * the new nodes are to go. There may have to be some padding to get there.
 * 	
 */
		

    TFormula root;
    TGWTTestNode selected=null;
    
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);
    int rootDepth=0;
	int selectedDepth=0;
	int depthIncrement=0;
    


    if ((selectedNodes!=null)&&(selectedNodes.size()==1))
    	{
      	selected= selectedNodes.get(0);
    
		
	
 	//TGWTTestNode selected =fRoot;
	TFormula theFormula=null;
	
	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
	selectedDepth= fRoot.getNodeDepth(selected);
	depthIncrement= rootDepth - selectedDepth;
			
		/* The insertion should always come at the (max) depth of the tree
		 * 
		 */
			
			
			
			
	
	if (selected.fDead||               //can select dead for closing formula
	    selected.fAntecedents==null||
	    selected.fAntecedents.size()!=1)
	return;
	else{
	        theFormula=(TFormula)(selected.fAntecedents.get(0));
	      }
	      
	int switcher=selected.typeOfFormula(theFormula);
	
	if (selected!=null)
		selected.deSelectAll();   //maybe later?
	
	switch (switcher){
    
		case TGWTTestNode.aand:
			doAnd(selected,theFormula,depthIncrement);
			break;

	    case TGWTTestNode.implic:
	        doImplic(selected,theFormula,depthIncrement);
	        break;

	    case TGWTTestNode.doubleneg:
	        doDoubleNeg(selected,theFormula,depthIncrement);
	        break;
	    
	    case TGWTTestNode.equivv:
	        doEquivv(selected,theFormula,depthIncrement);
	        break;

	    case TGWTTestNode.exi:
	       doExi(selected,theFormula,depthIncrement);
	       break;

	     case TGWTTestNode.negand:
	       doNegAnd(selected,theFormula,depthIncrement);
	       break;

	     case TGWTTestNode.negarrow:
	       doNegArrow(selected,theFormula,depthIncrement);
	       break;

	     case TGWTTestNode.negexi:
	       doNegExi(selected,theFormula,depthIncrement);
	       break;

	     case TGWTTestNode.neguni:
	       doNegUni(selected,theFormula,depthIncrement);
	       break;
	       
	     case TGWTTestNode.nequiv:
		       doNegEquiv(selected,theFormula,depthIncrement);
		       break;

		 case TGWTTestNode.nore:
		       doNore(selected,theFormula,depthIncrement);
		       break;
		       
		 case TGWTTestNode.negunique:
		         doNegUnique(selected,theFormula,depthIncrement);
		         break;
		         
	     case TGWTTestNode.ore:
		        doOr(selected,theFormula,depthIncrement);
		        break;	        

		  case TGWTTestNode.uni:
		        doUni(selected,theFormula,depthIncrement);
		        break;
	     
		 case TGWTTestNode.unique:
		   doUnique(selected,theFormula,depthIncrement);
		   break;

	/*** Modal ***/
		 case TGWTTestNode.notPossible:
			  doNegPossible(selected,theFormula,depthIncrement);
			  break;

	     case TGWTTestNode.notNecessary:
			doNegNecessary(selected,theFormula,depthIncrement);
			break;	
	     case TGWTTestNode.modalPossible:
			  if (s5Rules)
			     doPossible(selected,theFormula,depthIncrement);
			  else
			  if (kRules)
			     doPossibleR(selected,theFormula,depthIncrement);
			break;
	     case TGWTTestNode.modalNecessary:
	    	   if (s5Rules)
	    	      doNecessary(selected,theFormula,depthIncrement);
	    	   else
	    	   if (tRules)
	    		  doNecessaryT(selected,theFormula,depthIncrement);  // order is important here, two right ways (S% and T) and one wrong way
	    	   else
	    	   if (kRules)
	    		   bugAlert("Trying "+chModalNecessary+ "R","With R necess, you need also to select a second line with an 'Access' relation.");


	    	break;

	    	/***** End of Modal ********/
	    	
	    	/*** Epistemic ***/
			
	     case TGWTTestNode.notModalKappa:
	    		doNotKnow(selected,theFormula,depthIncrement);
	    		break;

	    	case TGWTTestNode.notModalRho:
	    		doNotKnowNot(selected,theFormula,depthIncrement);
	    		break;
	    		
	    	case TGWTTestNode.modalRho:
	    		doPossibleKnow(selected,theFormula,depthIncrement);
	    		break;
	    		
	    	case TGWTTestNode.modalKappa:
	    		doKnow(selected,theFormula,depthIncrement);
	    		break;	

	    	case TGWTTestNode.modalDoubleKappa:
	    		doKnow(selected,theFormula,depthIncrement);
	    		doTRKR(selected,theFormula,depthIncrement);
	    		break;	
	    		
	    		
	    	/*** End Epistemic ***/
	    		
	    	/******************Typed ***/
	    		
	    	case TGWTTestNode.typedUni:
	            root=fParser.expandTypeUni(theFormula);
	            if (root!=null)
	               doUni(selected,root,depthIncrement);
	            break;
	          case TGWTTestNode.negTypedUni:    //FIX

	            root=fParser.expandTypeUni(theFormula.fRLink);

	            root = new TFormula(TFormula.unary,
	                                 String.valueOf(chNeg),
	                                 null,
	                                 root);

	            doNegUni(selected,root,depthIncrement);
	            break;
	          case TGWTTestNode.typedExi:
	            root=fParser.expandTypeExi(theFormula);
	            if (root!=null)
	              doExi(selected,root,depthIncrement);
	            break;

	          case TGWTTestNode.negTypedExi:    //FIX
	            root=fParser.expandTypeExi(theFormula.fRLink);

	            root = new TFormula(TFormula.unary,
	                         String.valueOf(chNeg),
	                         null,
	                         root);

	            doNegExi(selected,root,depthIncrement);

	            break;		
	    		
	    		/*** End Typed ***/
			
	}
	
//	if (selected!=null)
//		selected.deSelectAll();
	
	
	{TGWTTestNode dummy =fRoot;
	
	//TO DO it may be better to do this somewhere else
	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
	}
    	}  // end of selected nodes = 1
    
    if ((selectedNodes!=null)&&(selectedNodes.size()==2)){  //might be IE or necessaryR or necessarynecessaryR or necnecSymR
   
    	 if (isKRPossible(selectedNodes)){
        	 doKR(selectedNodes, depthIncrement);
        	 return;
         }
    	
    	 
    	 if (isNecessaryRPossible(selectedNodes)&& kRules){
    		  
    			if (s4Rules/*&&isNecessaryNecessaryRPossible(selectedNodes)*/){  //might be double necessary, could do either

    			  	Button leftButton= new Button(chModalNecessary+ "R");  
    			  	leftButton.addClickHandler(new NecessaryRHandler(selectedNodes,depthIncrement));

    			  	Button rightButton= new Button(""+chModalNecessary+chModalNecessary+ "R");  
    			  	rightButton.addClickHandler(new NecessaryNecessaryRHandler(selectedNodes,depthIncrement));
    				
    				getTheChoice(leftButton,           //the calling actions must remove the input pane

    		    rightButton,
    		    "You have a choice between the rules Necessary R and Necessary Necessary R.",
    		    "Please choose");}
    			else
    				doNecessaryR(selectedNodes,depthIncrement);
    		    
    			return ;

    		  }
    	
    	//else
    	TGWTTestNode firstSelected = selectedNodes.get(0);
    	TGWTTestNode secondSelected = selectedNodes.get(1);
    	
    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	selectedDepth= fRoot.getNodeDepth(secondSelected); // not sure about this when there are two
    	depthIncrement= rootDepth - selectedDepth;
    	
    	

        TFormula firstFormula = null;
        TFormula secondFormula = null;

        if ((firstSelected.fAntecedents != null &&
            firstSelected.fAntecedents.size() == 1)&&
           (secondSelected.fAntecedents != null &&
            secondSelected.fAntecedents.size() == 1)&&
           firstSelected.fWorld.equals(secondSelected.fWorld)

           )
       {
          firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
          secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
          if (typeEPossible(firstSelected,secondSelected,firstFormula,secondFormula))
        	  doTypeE(firstSelected,secondSelected,firstFormula,secondFormula,depthIncrement);
          else   
        	  doIE(firstSelected,secondSelected,firstFormula,secondFormula);
        }
    
    }
    
    /*
     * 
     * 
     * if ((selectedNodes!=null)&&(selectedNodes.length==2)){  //might be IE or necessaryR or necessarynecessaryR or necnecSymR

     if (isKRPossible(selectedNodes)){
    	 doKR(selectedNodes);
    	 return;
     }
	  
	  if (isNecessaryRPossible(selectedNodes)&& kRules){
	  
	if (s4Rules/*&&isNecessaryNecessaryRPossible(selectedNodes)){  //might be double necessary, could do either

	  	  
	  getTheChoice(new AbstractAction(chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryR(selectedNodes);
      }},           //the calling actions must remove the input pane

    new AbstractAction(""+chModalNecessary+chModalNecessary+ "R")
      {public void actionPerformed(ActionEvent ae){
        removeInputPane();
        doNecessaryNecessaryR(selectedNodes);
      }},
    "You have a choice between the rules Necessary R and Necessary Necessary R.",
    "Please choose");}
	else
		doNecessaryR(selectedNodes);
    
	return ;

  }

  //else

    TTreeDataNode firstSelected = selectedNodes[0];
    TTreeDataNode secondSelected = selectedNodes[1];

    TFormula firstFormula = null;
    TFormula secondFormula = null;

    if ((firstSelected.fAntecedents != null &&
        firstSelected.fAntecedents.size() == 1)&&
       (secondSelected.fAntecedents != null &&
        secondSelected.fAntecedents.size() == 1)&&
       firstSelected.fWorld.equals(secondSelected.fWorld)

       )
   {
      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
      if (typeEPossible(firstSelected,secondSelected,firstFormula,secondFormula))
    	  doTypeE(firstSelected,secondSelected,firstFormula,secondFormula);
      else   
    	  doIE(firstSelected,secondSelected,firstFormula,secondFormula);
    }

  }
     * 
     */
    
    refreshDisplay();
    
}
		
/*********************** Extension Rules ***************************************/		

 
		
		
		
	
	/*	
		
		void extendMenuItem_actionPerformed(ActionEvent e) {

		    final TGWTTestNode [] selectedNodes= fTreeTableView.selectedDataNodes();
		    TFormula root;


		    if ((selectedNodes!=null)&&(selectedNodes.length==1))
		    {
		      TGWTTestNode selected= selectedNodes[0];

		      TFormula theFormula=null;

		      if (!selected.fDead&&               //can select dead for closing formula
		          selected.fAntecedents!=null&&
		          selected.fAntecedents.size()==1){
		        theFormula=(TFormula)(selected.fAntecedents.get(0));
		      }
		      else
		        return
		          ;

		    switch (selected.typeOfFormula(theFormula)){

		      

		/*** Modal ***
		      

		

		case TTestNode.modalNecessary:
		   if (s5Rules)
		      doNecessary(selected,theFormula);
		   else
		   if (tRules)
			  doNecessaryT(selected,theFormula);  // order is important here, two right ways (S% and T) and one wrong way
		   else
		   if (kRules)
			   bugAlert("Trying "+chModalNecessary+ "R","With R necess, you need also to select a second line with an 'Access' relation.");


		break;

		/***** End of Modal ********

		/*** Epistemic ***

		case TTestNode.notModalKappa:
			doNotKnow(selected,theFormula);
			break;

		case TTestNode.notModalRho:
			doNotKnowNot(selected,theFormula);
			break;
			
		case TTestNode.modalRho:
			doPossibleKnow(selected,theFormula);
			break;
			
		case TTestNode.modalKappa:
			doKnow(selected,theFormula);
			break;	

		case TTestNode.modalDoubleKappa:
			doKnow(selected,theFormula);
			doTRKR(selected,theFormula);
			break;	
			
			
		/*** End Epistemic ***

	      case TTestNode.typedUni:
		        root=fParser.expandTypeUni(theFormula);
		        if (root!=null)
		           doUni(selected,root);
		        break;
		      case TTestNode.negTypedUni:    //FIX

		        root=fParser.expandTypeUni(theFormula.fRLink);

		        root = new TFormula(TFormula.unary,
		                             String.valueOf(chNeg),
		                             null,
		                             root);

		        doNegUni(selected,root);
		        break;
		      case TTestNode.typedExi:
		        root=fParser.expandTypeExi(theFormula);
		        if (root!=null)
		          doExi(selected,root);
		        break;

		      case TTestNode.negTypedExi:    //FIX
		        root=fParser.expandTypeExi(theFormula.fRLink);

		        root = new TFormula(TFormula.unary,
		                     String.valueOf(chNeg),
		                     null,
		                     root);

		        doNegExi(selected,root);

		        break;

		    }

		  }

		  if ((selectedNodes!=null)&&(selectedNodes.length==2)){  //might be IE or necessaryR or necessarynecessaryR or necnecSymR

		     if (isKRPossible(selectedNodes)){
		    	 doKR(selectedNodes);
		    	 return;
		     }
			  
			  if (isNecessaryRPossible(selectedNodes)&& kRules){
			  
			if (s4Rules/*&&isNecessaryNecessaryRPossible(selectedNodes)*){  //might be double necessary, could do either

			  	  
			  getTheChoice(new AbstractAction(chModalNecessary+ "R")
		      {public void actionPerformed(ActionEvent ae){
		        removeInputPane();
		        doNecessaryR(selectedNodes);
		      }},           //the calling actions must remove the input pane

		    new AbstractAction(""+chModalNecessary+chModalNecessary+ "R")
		      {public void actionPerformed(ActionEvent ae){
		        removeInputPane();
		        doNecessaryNecessaryR(selectedNodes);
		      }},
		    "You have a choice between the rules Necessary R and Necessary Necessary R.",
		    "Please choose");}
			else
				doNecessaryR(selectedNodes);
		    
			return ;

		  }

		  //else

		    TGWTTestNode firstSelected = selectedNodes[0];
		    TGWTTestNode secondSelected = selectedNodes[1];

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ((firstSelected.fAntecedents != null &&
		        firstSelected.fAntecedents.size() == 1)&&
		       (secondSelected.fAntecedents != null &&
		        secondSelected.fAntecedents.size() == 1)&&
		       firstSelected.fWorld.equals(secondSelected.fWorld)

		       )
		   {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
		      if (typeEPossible(firstSelected,secondSelected,firstFormula,secondFormula))
		    	  doTypeE(firstSelected,secondSelected,firstFormula,secondFormula);
		      else   
		    	  doIE(firstSelected,secondSelected,firstFormula,secondFormula);
		    }

		  }

		}
		
	*/	
  
  void refreshDisplay(){
		TGWTTestNode dummy =fRoot;
		
		//TO DO it may be better to do this somewhere else
		 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
		}		
		

  
  
		void doAnd(TGWTTestNode selected,TFormula theFormula, int depthIncrement){
		    TFormula leftFormula=theFormula.fLLink.copyFormula();

		    TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
		    leftDataNode.fAntecedents.add(0,leftFormula);
		    leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
		    leftDataNode.fJustification=andDJustification;

		    TFormula rightFormula=theFormula.fRLink.copyFormula();

		    TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
		    rightDataNode.fAntecedents.add(0,rightFormula);
		    rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
		    rightDataNode.fJustification=andDJustification;


		    selected.fDead=true;
		    selected.fSelected=false;

		    selected.straightInsert(selected,leftDataNode,rightDataNode,depthIncrement);
		  };
		  
		  void doOr(TGWTTestNode selected,TFormula theFormula,int depthIncrement){


			   TFormula leftFormula=theFormula.fLLink.copyFormula();

			 TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
			 leftDataNode.fAntecedents.add(0,leftFormula);
			 leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
			 leftDataNode.fJustification=orDJustification;

			   TFormula rightFormula=theFormula.fRLink.copyFormula();

			 TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
			 rightDataNode.fAntecedents.add(0,rightFormula);
			 rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
			 rightDataNode.fJustification=orDJustification;

			 selected.fDead=true;
			 selected.fSelected=false;

			 selected.splitInsert(selected,leftDataNode,rightDataNode,depthIncrement,fRoot);
			  } 
		  
			 void doOrOriginal(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

				 // int [][] selectedIndices= fTreeTableView.selectedIndices();   //we know there is only one selected

				 



				  TFormula leftFormula=theFormula.fLLink.copyFormula();

				TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
				leftDataNode.fAntecedents.add(0,leftFormula);
				leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
				leftDataNode.fJustification=orDJustification;

				  TFormula rightFormula=theFormula.fRLink.copyFormula();

				TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
				rightDataNode.fAntecedents.add(0,rightFormula);
				rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
				rightDataNode.fJustification=orDJustification;

				selected.fDead=true;

				selected.splitInsert(selected,leftDataNode,rightDataNode,depthIncrement,fRoot);
				 // selected.straightInsert(leftDataNode,rightDataNode);

				//fTreeTableView.addColumn(new TableColumn());fTreeTableView.addColumn(new TableColumn());
				//fTreeTableModel.treeChanged(TTreeTableModel.COLCHANGE,leftDataNode);              //need a listener for this

				//deSelectAll();

				 }
		  
  /**************   Sun May 20        *************/
  
  
  
  void doDoubleNeg(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	    TFormula newFormula=theFormula.fRLink.fRLink.copyFormula();

	    TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	    newDataNode.fAntecedents.add(0,newFormula);
	    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	    newDataNode.fJustification=negDJustification;

	    selected.fDead=true;

	    selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  };


	/**************** Modal Rules *************************/

	  void doNegPossible(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	    TFormula newFormula=theFormula.copyFormula();

	    newFormula.fInfo= String.valueOf(chModalNecessary);  //permuting operators
	    newFormula.fRLink.fInfo= String.valueOf(Symbols.chNeg);

	    TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	    newDataNode.fAntecedents.add(0,newFormula);
	    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	    newDataNode.fJustification=notPossibleJustification;
	    newDataNode.fWorld=selected.fWorld;

	    selected.fDead=true;

	    selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  };

	  void doNegNecessary(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	    TFormula newFormula=theFormula.copyFormula();

	    newFormula.fInfo= String.valueOf(chModalPossible);  //permuting operators
	    newFormula.fRLink.fInfo= String.valueOf(Symbols.chNeg);

	    TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	    newDataNode.fAntecedents.add(0,newFormula);
	    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	    newDataNode.fJustification=notPossibleJustification;
	    newDataNode.fWorld=selected.fWorld;

	    selected.fDead=true;

	    selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  };


	  void doPossible(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	  String newWorld = selected.newWorldForBranches(selected);

	  if (newWorld.equals(""))
	    return;

	 TFormula scope=theFormula.fRLink.copyFormula();

	 TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	 newDataNode.fAntecedents.add(0,scope);
	 newDataNode.fFirstjustno=selected.fLineno;
	 newDataNode.fWorld=newWorld;
	 newDataNode.fJustification=s5PossJustification;


	 selected.fDead=true;

	 selected.straightInsert(selected,newDataNode,null,depthIncrement);
	};





	void doPossibleR(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //restricted on accessibility

	  String newWorld = selected.newWorldForBranches(selected);

	  if (newWorld.equals(""))
	    return;

	 String oldWorld=selected.fWorld;

	TFormula access=fParser.makeAnAccessRelation(oldWorld,newWorld);

	 TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	 newDataNode.fAntecedents.add(0,access);
	 newDataNode.fFirstjustno=selected.fLineno;
	 newDataNode.fJustification=rPossJustification;

	 selected.fDead=true;

	 selected.straightInsert(selected,newDataNode,null,depthIncrement);

	TFormula scope=theFormula.fRLink.copyFormula();

	newDataNode = new TGWTTestNode(fParser,fGWTTree);
	newDataNode.fAntecedents.add(0,scope);
	newDataNode.fFirstjustno=selected.fLineno;
	newDataNode.fJustification=rPossJustification;
	newDataNode.fWorld=newWorld;

	selected.fDead=true;

	selected.straightInsert(selected,newDataNode,null,depthIncrement);

	};


	/*************************NecessaryR Handlers **************/
	  public class NecessaryRHandler implements ClickHandler{
		     ArrayList<TGWTTestNode> fSelectedNodes;
		     int fDepth;

		     public NecessaryRHandler(ArrayList <TGWTTestNode> selected,
					 int depthIncrement){
		     //  putValue(NAME, label);

		       fSelectedNodes=selected;
		   fDepth=depthIncrement;	
		     }

		   	 public void onClick(ClickEvent ae){
	    		        removeInputPanel();
	    		        doNecessaryR(fSelectedNodes,fDepth);
	    		      }	    	 
		    	 
	  }  	 
	
	  public class NecessaryNecessaryRHandler implements ClickHandler{
		     ArrayList<TGWTTestNode> fSelectedNodes;
		     int fDepth;

		     public NecessaryNecessaryRHandler(ArrayList <TGWTTestNode> selected,
					 int depthIncrement){
		     //  putValue(NAME, label);

		       fSelectedNodes=selected;
		   fDepth=depthIncrement;	
		     }

		   	 public void onClick(ClickEvent ae){
	    		        removeInputPanel();
	    		        doNecessaryNecessaryR(fSelectedNodes,fDepth);
	    		      }	    	 
		    	 
	  }
		    	 
	
	/************************ Necessary Action****************/
	

	  public class NecessaryAction implements ClickHandler{
		 TextBox fText;
	     TGWTTestNode fSelected=null;
	     TFormula fFormula=null;
	     int fDepth;

	     public NecessaryAction(TextBox text, String label, TGWTTestNode selected, TFormula formula,
				 int depthIncrement){
	     //  putValue(NAME, label);

	       fText=text;
	       fSelected=selected;
	       fFormula=formula;
		   fDepth=depthIncrement;	
	     }

	     public void  onClick(ClickEvent event){

	    String aString=TUtilities.defaultFilter(fText.getText());
	       //String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

	       boolean wellformed=false;

	       if (fParser.isPossibleWorld(aString))//(aString.length()==1&& (fWorlds.indexOf(aString))>-1)
	         wellformed=true;

	       if (!wellformed) {
	         String message = "You need to enter a single lower case letter or single numeral."; //filter out returns

	                          //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

	                          fText.setText(message);
	                          fText.selectAll();
	      //                    fText.requestFocus();
	                        }

	                        else {   // we're good

	                        	removeInputPanel();
	                        	
	                        	TFormula scope = fFormula.fRLink.copyFormula();

	                            TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	                            newDataNode.fAntecedents.add(0,scope);
	                            newDataNode.fFirstjustno=fSelected.fLineno;
	                            newDataNode.fJustification= s5NecessJustification;
	                            newDataNode.fWorld= aString;


	                           // selected.fDead=true;    don't make it dead


	                            fSelected.straightInsert(fSelected,newDataNode,null,fDepth);

	                            
	                            
	                            {TGWTTestNode dummy =fRoot;
	                        	
	                        	//TO DO it may be better to do this somewhere else
	                        	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
	                        	}

	                        }

	                    }

	                  }
	  



	/************************ End of Necessary Action *********/




	  void doNecessary(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
        Button defaultButton;
	    TGWTTreeInputPanel inputPane;

	    TextBox text = new TextBox();
	    text.setText("Enter the index for the possible world (a single lower case letter or numeral)?");
	    text.selectAll();

	    defaultButton = new Button("Go");
	    defaultButton.addClickHandler(new NecessaryAction(text,"Go", selected, theFormula,depthIncrement));
	    		
	    		
	    		

	    Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	    inputPane = new TGWTTreeInputPanel("Doing "+chModalNecessary+ "S5", text, buttons);


	    addInputPane(inputPane,SELECT);
//	    inputPane.getRootPane().setDefaultButton(defaultButton);
//	    fInputPane.setVisible(true); // need this
//	    text.requestFocus();         // so selected text shows
	};


	void doNecessaryT(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
		

	   TFormula scope = theFormula.fRLink.copyFormula();

		                            TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		                            newDataNode.fAntecedents.add(0,scope);
		                            newDataNode.fFirstjustno=selected.fLineno;
		                            newDataNode.fJustification= tNecessJustification;
		                            newDataNode.fWorld= selected.fWorld;


		                            selected.fDead=true;   


		                            selected.straightInsert(selected,newDataNode,null,depthIncrement);
		                        }

		



	boolean isNecessaryRPossible(ArrayList <TGWTTestNode> selectedNodes){   //check for two selected access relation and necess
	// preliminary check
		
		if ( (selectedNodes != null) && (selectedNodes.size() == 2)) {
		    TGWTTestNode firstSelected = selectedNodes.get(0);
		    TGWTTestNode secondSelected = selectedNodes.get(1);

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      if (!TParser.isModalNecessary(firstFormula)&&
		    	  !TParser.isModalNecessary(secondFormula))
		    	  return
		    	  	false;
		    }}
		else
			return
				false;
		
		
		
		
		
		return
		   necessaryRExtension(selectedNodes)!=null;
 

	}

	

	boolean isNecessaryNecessarySymRPossible(TGWTTestNode [] selectedNodes){   //check for two selected access relation and necess

	    if ((selectedNodes!=null)&&(selectedNodes.length==2)){
	      TGWTTestNode firstSelected = selectedNodes[0];
	      TGWTTestNode secondSelected = selectedNodes[1];

	      TFormula firstFormula = null;
	      TFormula secondFormula = null;

	    if ((firstSelected.fAntecedents != null &&
	        firstSelected.fAntecedents.size() == 1)&&
	       (secondSelected.fAntecedents != null &&
	        secondSelected.fAntecedents.size() == 1)) {
	      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	      if (fParser.getAccessRelation(firstFormula).equals("")
	          &&
	          fParser.getAccessRelation(secondFormula).equals(""))

	        return
	            false;

	      if (firstSelected.typeOfFormula(firstFormula)==TGWTTestNode.modalNecessary
	          ||
	          secondSelected.typeOfFormula(secondFormula)==TGWTTestNode.modalNecessary)

	        return
	            true;


	    }

	  }
	  return
	      false;

	}


	TGWTTestNode necessaryRExtension(ArrayList <TGWTTestNode> selectedNodes){
/*
		  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
		    TGWTTestNode firstSelected = selectedNodes[0];
		    TGWTTestNode secondSelected = selectedNodes[1];

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      //They need to be in the same branch

		      TreeNode[] firstbranch = firstSelected.fSwingTreeNode.getPath();
		      TreeNode[] secondbranch = secondSelected.fSwingTreeNode.getPath();

		      boolean firstHigher = false;
		      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
		        firstHigher = (secondbranch[i] == firstSelected.fSwingTreeNode);

		      boolean secondHigher = false;
		      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
		        secondHigher = (firstbranch[i] == secondSelected.fSwingTreeNode);

		      if (!firstHigher &&
		          !secondHigher) {
		        bugAlert("Trying to do Necessary R. Warning.",
		                 "The two formulas need to be in the same branch.");
		        return
		           null;
		            
		      }

		      else { //we are in business

		        boolean firstIsAccess = true;
		        String worlds = fParser.getAccessRelation(firstFormula);

		        TGWTTestNode accessSelected = firstSelected;
		        TGWTTestNode necessarySelected = secondSelected;
		        TFormula access = firstFormula;
		        TFormula necessary = secondFormula;

		        if (worlds.equals("")) {
		          accessSelected = secondSelected;
		          necessarySelected = firstSelected;
		          access = secondFormula;
		          necessary = firstFormula;
		          worlds = fParser.getAccessRelation(access);
		        }
		        
		        if (worlds.equals("")) {    // no worlds at all, this could happen with non-modal formulas
		        	bugAlert("Trying to do Necessary R. Warning.",
	                "There is no Access relation.");
		        	
		        	return
			          null;
			        }
		       
		        
		        if (!fParser.isModalNecessary(necessary)) {    // no worlds at all, this could happen with non-modal formulas
		        	bugAlert("Trying to do Necessary R. Warning.",
	                "There is no Necessary formula.");
		        	
		        	return
			          null;
			        }
		        
		        

		        //we need to check that the access from and the world of the necess are the same

		        if (!necessarySelected.fWorld.equals(worlds.substring(0, 1))) {
		          bugAlert("Trying to do Necessary R. Warning.",
		                   "The necessary formula does not have Access.");
		          return
		             null;

		        }

		        TFormula scope = necessary.fRLink.copyFormula();

		        TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
		        newDataNode.fAntecedents.add(0, scope);
		        newDataNode.fFirstjustno = necessarySelected.fLineno;
		        newDataNode.fSecondjustno = accessSelected.fLineno;
		        newDataNode.fJustification = rNecessJustification;
		        newDataNode.fWorld = worlds.substring(1, 2);

		        return
		           newDataNode;
		      }

		    }
		  }
	*/	  return
		     null; 
		}


	void doNecessaryR(ArrayList <TGWTTestNode> selectedNodes, int depthIncrement){
	
		
		TGWTTestNode newDataNode=necessaryRExtension(selectedNodes);
		
		if (newDataNode!=null){
			TGWTTestNode necessarySelected = fParser.isModalNecessary((TFormula) (selectedNodes.get(0).fAntecedents.get(0)))?
					                          selectedNodes.get(0):
						                      selectedNodes.get(1);
					                          necessarySelected.straightInsert(necessarySelected, newDataNode, null,depthIncrement);		
		}
	}

	void doNecessaryNecessaryR(ArrayList <TGWTTestNode> selectedNodes, int depthIncrement){

	/*	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
		    TGWTTestNode firstSelected = selectedNodes[0];
		    TGWTTestNode secondSelected = selectedNodes[1];

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      //They need to be in the same branch

		      TreeNode[] firstbranch = firstSelected.fSwingTreeNode.getPath();
		      TreeNode[] secondbranch = secondSelected.fSwingTreeNode.getPath();

		      boolean firstHigher = false;
		      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
		        firstHigher = (secondbranch[i] == firstSelected.fSwingTreeNode);

		      boolean secondHigher = false;
		      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
		        secondHigher = (firstbranch[i] == secondSelected.fSwingTreeNode);

		      if (!firstHigher &&
		          !secondHigher) {
		        bugAlert("Trying to do Necessary Necessary R. Warning.",
		                 "The two formulas need to be in the same branch.");
		        return
		            ;
		      }

		      else { //we are in business

		        boolean firstIsAccess = true;
		        String worlds = fParser.getAccessRelation(firstFormula);

		        TGWTTestNode accessSelected = firstSelected;
		        TGWTTestNode necessarySelected = secondSelected;
		        TFormula access = firstFormula;
		        TFormula necessary = secondFormula;

		        if (worlds.equals("")) {
		          accessSelected = secondSelected;
		          necessarySelected = firstSelected;
		          access = secondFormula;
		          necessary = firstFormula;
		          worlds = fParser.getAccessRelation(access);
		        }

		        //we need to check that the access from and the world of the necess are the same

		        if (!necessarySelected.fWorld.equals(worlds.substring(0, 1))) {
		          bugAlert("Trying to do Necessary Necessary R. Warning.",
		                   "The necessary formula does not have Access.");
		          return;

		        }

		        TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
		        newDataNode.fAntecedents.add(0, necessary.copyFormula());
		        newDataNode.fFirstjustno = necessarySelected.fLineno;
		        newDataNode.fSecondjustno = accessSelected.fLineno;
		        newDataNode.fJustification = rNecessNecessJustification;
		        newDataNode.fWorld = worlds.substring(1, 2);

		        straightInsert(necessarySelected, newDataNode, null);
		      }

		    }
		  } */
		}

	void doNecessaryNecessarySymR(TGWTTestNode [] selectedNodes){

	/*	  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
		    TGWTTestNode firstSelected = selectedNodes[0];
		    TGWTTestNode secondSelected = selectedNodes[1];

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      //They need to be in the same branch

		      TreeNode[] firstbranch = firstSelected.fSwingTreeNode.getPath();
		      TreeNode[] secondbranch = secondSelected.fSwingTreeNode.getPath();

		      boolean firstHigher = false;
		      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
		        firstHigher = (secondbranch[i] == firstSelected.fSwingTreeNode);

		      boolean secondHigher = false;
		      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
		        secondHigher = (firstbranch[i] == secondSelected.fSwingTreeNode);

		      if (!firstHigher &&
		          !secondHigher) {
		        bugAlert("Trying to do Necessary Necessary SymR. Warning.",
		                 "The two formulas need to be in the same branch.");
		        return
		            ;
		      }

		      else { //we are in business

		        boolean firstIsAccess = true;
		        String worlds = fParser.getAccessRelation(firstFormula);

		        TGWTTestNode accessSelected = firstSelected;
		        TGWTTestNode necessarySelected = secondSelected;
		        TFormula access = firstFormula;
		        TFormula necessary = secondFormula;

		        if (worlds.equals("")) {
		          accessSelected = secondSelected;
		          necessarySelected = firstSelected;
		          access = secondFormula;
		          necessary = firstFormula;
		          worlds = fParser.getAccessRelation(access);
		        }

		        //we need to check that the access to and the world of the necess are the same
		        
		        String fromWorld=worlds.substring(1, 2);
		        String toWorld=worlds.substring(0, 1);

		        if (!necessarySelected.fWorld.equals(fromWorld)) {
		          bugAlert("Trying to do Necessary Necessary SymR. Warning.",
		                   "The necessary formula does not have 'inverse' Access.");
		          return;

		        }

		        TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
		        newDataNode.fAntecedents.add(0, necessary.copyFormula());
		        newDataNode.fFirstjustno = necessarySelected.fLineno;
		        newDataNode.fSecondjustno = accessSelected.fLineno;
		        newDataNode.fJustification = rNecessNecessJustification;
		        newDataNode.fWorld = toWorld;

		        straightInsert(necessarySelected, newDataNode, null);
		      }

		    }
		  } */
		}


	/***************************  Type Elimination ********/

	//want a type identity and a typed formula

	public boolean typeEPossible (TGWTTestNode firstSelected,TGWTTestNode secondSelected,
	        TFormula firstFormula,TFormula secondFormula){
		TGWTTestNode identitySelected = null;
	    TGWTTestNode typedSelected = null;
	    TFormula identity = null;
	    TFormula typed = null;
	    
		int firstType=firstSelected.typeOfFormula(firstFormula);
		int secondType=secondSelected.typeOfFormula(secondFormula);
			
		if ((firstType==TGWTTestNode.atomic && TFormula.isEquality(firstFormula))&&
			((secondType==TGWTTestNode.typedExi)||
			 (secondType==TGWTTestNode.typedUni)||	
			(secondType==TGWTTestNode.negTypedExi)||
			(secondType==TGWTTestNode.negTypedUni))
			){
			identitySelected = firstSelected;
		    typedSelected = secondSelected;
		    identity = firstFormula;
		    typed = secondFormula;		
		}
		else
			if ((secondType==TGWTTestNode.atomic && TFormula.isEquality(secondFormula))&&
					((firstType==TGWTTestNode.typedExi)||
					 (firstType==TGWTTestNode.typedUni)||	
					(firstType==TGWTTestNode.negTypedExi)||
					(firstType==TGWTTestNode.negTypedUni))
					){
					identitySelected = secondSelected;
				    typedSelected = firstSelected;
				    identity = secondFormula;
				    typed = firstFormula;
				    secondType=firstType;
				}
		
		// we know the first type is equality, the second type is what it is
			
		if (identitySelected==null||typedSelected==null)
			return
			   false;
		
		//we're good to go further.
		
		TFormula firstTerm=identity.firstTerm();
		TFormula secondTerm=identity.secondTerm();

	   if (!((firstTerm.isClosedTerm() &&
			secondTerm.isClosedTerm())))
		   return
		   false;
	 
	   //we're good to go further.
	   
	   TFormula typeFormula=null;
	   
	   if (secondType==TGWTTestNode.typedExi||
	       secondType==TGWTTestNode.typedUni)
		   typeFormula=typed.quantTypeForm();
	   else
		   if (secondType==TGWTTestNode.negTypedExi||
			       secondType==TGWTTestNode.negTypedUni)
				   typeFormula=typed.fRLink.quantTypeForm();
	   
	   TFormula replacement=null;
	   
	   if (firstTerm.equalFormulas(firstTerm, typeFormula))
		   replacement=secondTerm.copyFormula();

	   if (secondTerm.equalFormulas(secondTerm, typeFormula))
		   replacement=firstTerm.copyFormula();;
	   
	   if (replacement==null)
		   return
		   false;
	   
	   return
	   true;


	}


	// want a type identity and a typed formula

	public void doTypeE (TGWTTestNode firstSelected,TGWTTestNode secondSelected,
	        TFormula firstFormula,TFormula secondFormula, int depthIncrement){
		TGWTTestNode identitySelected = null;
	    TGWTTestNode typedSelected = null;
	    TFormula identity = null;
	    TFormula typed = null;
	    
		int firstType=firstSelected.typeOfFormula(firstFormula);
		int secondType=secondSelected.typeOfFormula(secondFormula);
			
		if ((firstType==TGWTTestNode.atomic && TFormula.isEquality(firstFormula))&&
			((secondType==TGWTTestNode.typedExi)||
			 (secondType==TGWTTestNode.typedUni)||	
			(secondType==TGWTTestNode.negTypedExi)||
			(secondType==TGWTTestNode.negTypedUni))
			){
			identitySelected = firstSelected;
		    typedSelected = secondSelected;
		    identity = firstFormula;
		    typed = secondFormula;		
		}
		else
			if ((secondType==TGWTTestNode.atomic && TFormula.isEquality(secondFormula))&&
					((firstType==TGWTTestNode.typedExi)||
					 (firstType==TGWTTestNode.typedUni)||	
					(firstType==TGWTTestNode.negTypedExi)||
					(firstType==TGWTTestNode.negTypedUni))
					){
					identitySelected = secondSelected;
				    typedSelected = firstSelected;
				    identity = secondFormula;
				    typed = firstFormula;
				    secondType=firstType;
				}
		
		// we know the first type is equality, the second type is what it is
			
		if (identitySelected==null||typedSelected==null)
			return;
		
		//we're good to go further.
		
		TFormula firstTerm=identity.firstTerm();
		TFormula secondTerm=identity.secondTerm();

	   if (!((firstTerm.isClosedTerm() &&
			secondTerm.isClosedTerm())))
	      return;
	 
	   //we're good to go further.
	   
	   TFormula typeFormula=null;
	   
	   if (secondType==TGWTTestNode.typedExi||
	       secondType==TGWTTestNode.typedUni)
		   typeFormula=typed.quantTypeForm();
	   else
		   if (secondType==TGWTTestNode.negTypedExi||
			       secondType==TGWTTestNode.negTypedUni)
				   typeFormula=typed.fRLink.quantTypeForm();
	   
	   TFormula replacement=null;
	   
	   if (firstTerm.equalFormulas(firstTerm, typeFormula))
		   replacement=secondTerm.copyFormula();

	   if (secondTerm.equalFormulas(secondTerm, typeFormula))
		   replacement=firstTerm.copyFormula();;
	   
	   if (replacement==null)
		   return;
	   
	   TFormula newFormula=typed.copyFormula();
	   
	   if (secondType==TGWTTestNode.typedExi||
		       secondType==TGWTTestNode.typedUni)
		   newFormula.setQuantType(replacement);
		   else
			   if (secondType==TGWTTestNode.negTypedExi||
				       secondType==TGWTTestNode.negTypedUni)
				   newFormula.fRLink.setQuantType(replacement);
	 
	 
	   TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	   newDataNode.fAntecedents.add(0,newFormula);
	   newDataNode.fFirstjustno=firstSelected.fLineno;
	   newDataNode.fSecondjustno=secondSelected.fLineno;
	   newDataNode.fJustification= typeEJustification;
	  // newDataNode.fWorld= selected.fWorld;  
	   
	   firstSelected.straightInsert(firstSelected,newDataNode,null, depthIncrement);


	}





	/**************** end of Type Elinination ******/





	/************* Access **************/


	void doRefI(TGWTTestNode selected, TFormula theFormula, int depthIncrement){

	  //we require selected here to pick out the desired world branch

	       TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	       newDataNode.fAntecedents.add(0,fParser.makeAnAccessRelation(selected.fWorld,selected.fWorld));
	       newDataNode.fFirstjustno=selected.fLineno;
	       newDataNode.fJustification= accessRefJustification;
	      // newDataNode.fWorld= selected.fWorld;  
	       
	       selected.straightInsert(selected,newDataNode,null,depthIncrement);
	       refreshDisplay();
	  }

	void doSymI(TGWTTestNode selected, TFormula theFormula, int depthIncrement){

		  //selected needs to be an access relation
		
	    String worlds = fParser.getAccessRelation(theFormula);

	    if (worlds.equals("")) {
	    	bugAlert("Trying to do Access Symmetry. Warning.", "Select an Access relation.");
	    	return;
	    		
	    }
		
		TFormula access=fParser.makeAnAccessRelation(worlds.substring(1, 2),worlds.substring(0, 1));

		       TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		       newDataNode.fAntecedents.add(0,access);
		       newDataNode.fFirstjustno=selected.fLineno;
		       newDataNode.fJustification= accessSymJustification;
		      // newDataNode.fWorld= selected.fWorld;  
		       
		       selected.straightInsert(selected,newDataNode,null,depthIncrement);
		       refreshDisplay();
		  }

	void doTransI(TGWTTestNode selected1,TGWTTestNode selected2, TFormula theFormula1,
			TFormula theFormula2, int depthIncrement){

		  //selected needs to be an access relation
		
	  String worlds1 = fParser.getAccessRelation(theFormula1);
	  String worlds2 = fParser.getAccessRelation(theFormula2);

	  if (worlds1.equals("")||
		  worlds2.equals("")) {
	  	bugAlert("Trying to do Access Trans. Warning.", "Select two Access relations.");
	  	return;
	  		
	  }
		
	 boolean reverse=false;
	 
	 if (worlds1.charAt(1)!=worlds2.charAt(0)){
		 reverse=true;
		 if (worlds2.charAt(1)!=worlds1.charAt(0)){
			 bugAlert("Trying to do Access Trans. Warning.", "The second world of one Access must be the first of the other.");
			  	return;
		 }
	 }
	  
	 String w1 = reverse?worlds2.substring(0, 1):worlds1.substring(0, 1); 
	 String w2 = reverse?worlds1.substring(1, 2):worlds2.substring(1, 2); 
	  
	  TFormula access=fParser.makeAnAccessRelation(w1,w2);

		       TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		       newDataNode.fAntecedents.add(0,access);
		       newDataNode.fFirstjustno=reverse?selected2.fLineno:selected1.fLineno;
		       newDataNode.fSecondjustno=reverse?selected1.fLineno:selected2.fLineno;
		       newDataNode.fJustification= accessTransJustification;
		      // newDataNode.fWorld= selected.fWorld;  
		       
		       selected1.straightInsert(reverse?selected2:selected1,newDataNode,null,depthIncrement);
		       refreshDisplay();
		  }

	/*************** End of Modal Rules ***********************/


	/**************** Epistemic Rules *************************/

	TGWTTestNode kRExtension(ArrayList <TGWTTestNode> selectedNodes){
	/*	
		// one is kappa(agent, prop), the other Access(agent,from,to)

		  if ( (selectedNodes != null) && (selectedNodes.length == 2)) {
		    TGWTTestNode firstSelected = selectedNodes[0];
		    TGWTTestNode secondSelected = selectedNodes[1];

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      //They need to be in the same branch

		      TreeNode[] firstbranch = firstSelected.fSwingTreeNode.getPath();
		      TreeNode[] secondbranch = secondSelected.fSwingTreeNode.getPath();

		      boolean firstHigher = false;
		      for (int i = 0; (i < secondbranch.length) && !firstHigher; i++)
		        firstHigher = (secondbranch[i] == firstSelected.fSwingTreeNode);

		      boolean secondHigher = false;
		      for (int i = 0; (i < firstbranch.length) && !secondHigher; i++)
		        secondHigher = (firstbranch[i] == secondSelected.fSwingTreeNode);

		      if (!firstHigher &&
		          !secondHigher) {
		        bugAlert("Trying to do K R. Warning.",
		                 "The two formulas need to be in the same branch.");
		        return
		           null;
		            
		      }

		      else { //we are in business

		        boolean firstIsAccess = true;
		        String worlds = fParser.getEAccessRelation(firstFormula);

		        TGWTTestNode accessSelected = firstSelected;
		        TGWTTestNode necessarySelected = secondSelected;
		        TFormula access = firstFormula;
		        TFormula necessary = secondFormula;

		        if (worlds.equals("")) {
		          accessSelected = secondSelected;
		          necessarySelected = firstSelected;
		          access = secondFormula;
		          necessary = firstFormula;
		          worlds = fParser.getEAccessRelation(access);
		        }
		        
		        if (worlds.equals("")) {    // no worlds at all, this could happen with non-modal formulas
		        	bugAlert("Trying to do KR. Warning.",
	                "There is no EAccess relation.");
		        	return
			          null;
			        }
		        
		        if (!fParser.isModalKappa(necessary)) {    // no worlds at all, this could happen with non-modal formulas
		        	bugAlert("Trying to do KR. Warning.",
	                "There is no Knows formula.");
		        	
		        	return
			          null;
			        }

		        //we need to check that the access from and the world of the necess are the same

		        String agent=worlds.substring(0, 1);
		        String from=worlds.substring(1, 2);
		        String to=worlds.substring(2, 3);
		        
		        if (!necessary.fLLink.fInfo.equals(agent)) {
			          bugAlert("Trying to do KR. Warning.",
			                   "The agent has to be the same for the formula and EAccess.");
			          return
			             null;

			        }
		        
		        if (!necessarySelected.fWorld.equals(from)) {
		          bugAlert("Trying to do KR. Warning.",
		                   "The necessary formula does not have Access.");
		          return
		             null;

		        }

		        TFormula scope = necessary.fRLink.copyFormula();

		        TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
		        newDataNode.fAntecedents.add(0, scope);
		        newDataNode.fFirstjustno = necessarySelected.fLineno;
		        newDataNode.fSecondjustno = accessSelected.fLineno;
		        newDataNode.fJustification = kRJustification;
		        newDataNode.fWorld = to;

		        return
		           newDataNode;
		      }

		    }
		  }*/
		  return
		     null;
		}

	boolean isKRPossible(ArrayList <TGWTTestNode> selectedNodes){   //check for two selected access relation and necess
	// preliminary check
	// we'll define it to be possible of one K formula
		// then let the routine give the error messages
		
		if ( (selectedNodes != null) && (selectedNodes.size() == 2)) {
		    TGWTTestNode firstSelected = selectedNodes.get(0);
		    TGWTTestNode secondSelected = selectedNodes.get(1);

		    TFormula firstFormula = null;
		    TFormula secondFormula = null;

		    if ( (firstSelected.fAntecedents != null &&
		          firstSelected.fAntecedents.size() == 1) &&
		        (secondSelected.fAntecedents != null &&
		         secondSelected.fAntecedents.size() == 1)) {
		      firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		      secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

		      if (TParser.isModalKappa(firstFormula)||
		    	  TParser.isModalKappa(secondFormula))
		    	  return
		    	  	true;
		    }}

			return
				false;
	}

	void doKR(ArrayList <TGWTTestNode> selectedNodes, int depthIncrement){
			
			TGWTTestNode newDataNode=kRExtension(selectedNodes);
			
			if (newDataNode!=null){
				TGWTTestNode necessarySelected = fParser.isModalKappa((TFormula) (selectedNodes.get(0).fAntecedents.get(0)))?
						                          selectedNodes.get(0):
							                      selectedNodes.get(1);
				necessarySelected.straightInsert(necessarySelected, newDataNode, null, depthIncrement);
				
				//we actually want to put a second node in here, the KKR node
				
				TGWTTestNode kKRNode = new TGWTTestNode(fParser, fGWTTree);
				kKRNode.fAntecedents.add(0, ((TFormula)(necessarySelected.fAntecedents.get(0))).copyFormula());
				kKRNode.fFirstjustno = newDataNode.fFirstjustno;
				kKRNode.fSecondjustno = newDataNode.fSecondjustno;
				kKRNode.fJustification = kKRJustification;
				kKRNode.fWorld = newDataNode.fWorld;
				
				necessarySelected.straightInsert(necessarySelected, kKRNode, null, depthIncrement);
				
				
			}
		}



	void doNotKnowNot(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
		// this has neg rho(agent,prop) and we want kappa (agent, neg prop) 	
		
		  TFormula agent=theFormula.fRLink.fLLink.copyFormula();
		  TFormula prop=theFormula.fRLink.fRLink.copyFormula();
			
		  TFormula newFormula= new TFormula(modalKappa,
				       String.valueOf(chKappa),
				       agent,  
				       new TFormula(TFormula.unary,
				    		            String.valueOf(chNeg),
						       null,prop));
		  
		  TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		  newDataNode.fAntecedents.add(0,newFormula);
		  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
		  newDataNode.fJustification=kPNJustification;
		  newDataNode.fWorld=selected.fWorld;

		  selected.fDead=true;

		  selected.straightInsert(selected,newDataNode,null,depthIncrement);
		};

	void doNotKnow(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	// this has neg kappa(agent,prop) and we want rho (agent, neg prop) 	
		
	  TFormula agent=theFormula.fRLink.fLLink.copyFormula();
	  TFormula prop=theFormula.fRLink.fRLink.copyFormula();
		
	  TFormula newFormula= new TFormula(modalRho,
			       String.valueOf(chRho),
			       agent,  
			       new TFormula(TFormula.unary,
			    		            String.valueOf(chNeg),
					       null,prop));
	  
	  TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	  newDataNode.fAntecedents.add(0,newFormula);
	  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	  newDataNode.fJustification=kPNJustification;
	  newDataNode.fWorld=selected.fWorld;

	  selected.fDead=true;

	  selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  
	};

	void doPossibleKnow(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //restricted on accessibility

		// rho(agent, prop)
		
	/*	TFormula agent=theFormula.fLLink.copyFormula();
		
		  String newWorld = newWorldForBranches(selected.fSwingTreeNode);

		  if (newWorld.equals(""))
		    return;

		 String oldWorld=selected.fWorld;

		TFormula access=fParser.makeAnEAccessRelation(agent.fInfo,oldWorld,newWorld);

		 TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		 newDataNode.fAntecedents.add(0,access);
		 newDataNode.fFirstjustno=selected.fLineno;
		 newDataNode.fJustification=pRJustification;

		 selected.fDead=true;

		 selected.straightInsert(selected,leftDataNode,rightDataNode,depthIncrement);

		TFormula scope=theFormula.fRLink.copyFormula();

		newDataNode = new TGWTTestNode(fParser,fGWTTree);
		newDataNode.fAntecedents.add(0,scope);
		newDataNode.fFirstjustno=selected.fLineno;
		newDataNode.fJustification=pRJustification;
		newDataNode.fWorld=newWorld;

		selected.fDead=true;

		selected.straightInsert(selected,leftDataNode,rightDataNode,depthIncrement);
*/
		};
		
		void doKnow(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //restricted on accessibility

			// kappa(agent, prop)
			
			TFormula agent=theFormula.fLLink.copyFormula();
			

			 String oldWorld=selected.fWorld;

			TFormula scope=theFormula.fRLink.copyFormula();

			TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
			newDataNode.fAntecedents.add(0,scope);
			newDataNode.fFirstjustno=selected.fLineno;
			newDataNode.fJustification=kTRJustification;
			newDataNode.fWorld=oldWorld;

			selected.fDead=true;

			selected.straightInsert(selected,newDataNode,null,depthIncrement);

			};
			
			void doTRKR(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //restricted on accessibility

				// kappa(x, kappa(y, prop))  gives us kappa(x, prop)
				
				if (theFormula!=null&&
					TParser.isModalKappa(theFormula)&&
					TParser.isModalKappa(theFormula.fRLink)){
				
				TFormula agent=theFormula.fLLink.copyFormula();
				TFormula scope=theFormula.fRLink.fRLink.copyFormula();
		
				 String oldWorld=selected.fWorld;

				TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
				newDataNode.fAntecedents.add(0,new TFormula(
												modalKappa,
												String.valueOf(chKappa),
						                     agent,
						                     scope));
				newDataNode.fFirstjustno=selected.fLineno;
				newDataNode.fJustification=trKRJustification;
				newDataNode.fWorld=oldWorld;

				selected.fDead=true;

				selected.straightInsert(selected,newDataNode,null,depthIncrement);

				}	
			}
			

	/**************** Epistemic Rules *************************/

	void doEquivv(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	  

	TFormula leftFormula=theFormula.fLLink.copyFormula();

	 TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	 leftDataNode.fAntecedents.add(0,leftFormula);
	 leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	 leftDataNode.fJustification=equivDJustification;

	 TFormula left2Formula=theFormula.fRLink.copyFormula();

	 TGWTTestNode left2DataNode = new TGWTTestNode(fParser,fGWTTree);
	 left2DataNode.fAntecedents.add(0,left2Formula);
	 left2DataNode.fFirstjustno=selected.fLineno;left2DataNode.fWorld=selected.fWorld;
	 left2DataNode.fJustification=equivDJustification;

	TFormula rightFormula=theFormula.fLLink.copyFormula();

	 TFormula newFormula = new TFormula();

	newFormula.fKind = TFormula.unary;
	newFormula.fInfo = String.valueOf(Symbols.chNeg);
	newFormula.fRLink = rightFormula;

	rightFormula= newFormula;                       //not A


	 TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	 rightDataNode.fAntecedents.add(0,rightFormula);
	 rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	 rightDataNode.fJustification=equivDJustification;

	 TFormula right2Formula=theFormula.fRLink.copyFormula();

	newFormula = new TFormula();

	newFormula.fKind = TFormula.unary;
	newFormula.fInfo = String.valueOf(Symbols.chNeg);
	newFormula.fRLink = right2Formula;

	right2Formula= newFormula;                       //not B


	 TGWTTestNode right2DataNode = new TGWTTestNode(fParser,fGWTTree);
	 right2DataNode.fAntecedents.add(0,right2Formula);
	 right2DataNode.fFirstjustno=selected.fLineno;right2DataNode.fWorld=selected.fWorld;
	 right2DataNode.fJustification=equivDJustification;


	 selected.fDead=true;

	 selected.splitInsertTwo(selected,leftDataNode,left2DataNode,
	                 rightDataNode,right2DataNode,depthIncrement);
	 }


	



	 void doExi(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	  TFormula newConstant = selected.newConstantForBranches(selected);

	   if (newConstant==null)
	     return;

	  TFormula variForm=theFormula.quantVarForm();
	  TFormula scope=theFormula.fRLink.copyFormula();
	  TFormula.subTermVar(scope,newConstant,variForm);

	  TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	  newDataNode.fAntecedents.add(0,scope);
	  newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	  newDataNode.fJustification=exiDJustification;

	  selected.fDead=true;

	  selected.straightInsert(selected,newDataNode,null,depthIncrement);
	};

	void doUnique(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
		
		TFormula newConstant = selected.newConstantForBranches(selected);

		   if (newConstant==null)
		     return; 
	   	  

	// String firstVariables=variablesInFormula(first);
	//         String secondVariables=variablesInFormula(second);

	//         char newVar =nthNewVariable(1, firstVariables+secondVariables);	  
	
		   // Fa & allxally(Fx&Fy->x=y)

			  TFormula firstVar=theFormula.quantVarForm();
			  TFormula Fx=theFormula.fRLink.copyFormula();
			  TFormula Fa=Fx.copyFormula();
			  TFormula.subTermVar(Fa,newConstant,firstVar);
			  
			  Set <String> oldVariables =fParser.variablesInFormula(theFormula);
			  String secondVarStr= fParser.nthNewVariable(1,oldVariables);
			  
			  if(secondVarStr.equals(""))
				  return;
			  TFormula secondVar =new TFormula(
				  			variable,
				  			secondVarStr,
				  			null,
				  			null);
	  
			  TFormula Fy=Fx.copyFormula();
			  TFormula.subTermVar(Fy,secondVar,firstVar);
		  
		  TFormula uniForm = new TFormula(quantifier,
				  String.valueOf(chUniquant),
				  firstVar,
				  new TFormula(
				  			quantifier,
				  			String.valueOf(chUniquant),
				  			secondVar,
				  			new TFormula(
						  			binary,
						  			String.valueOf(chImplic),
						  			new TFormula(
								  			binary,
								  			String.valueOf(chAnd),
								  			Fx,
								  			Fy),
								  			TFormula.equateTerms(firstVar.copyFormula(),secondVar.copyFormula())
								  			)
				  			));

		  //uniForm.fRLink = TFormula.equateTerms(variablenode,variablenode.copyFormula());

		  
		   TGWTTestNode firstDataNode = new TGWTTestNode(fParser,fGWTTree);
		   firstDataNode.fAntecedents.add(0,Fa);
		   firstDataNode.fFirstjustno=selected.fLineno;firstDataNode.fWorld=selected.fWorld;
		   firstDataNode.fJustification=uniqueDJustification;

		    TGWTTestNode secondDataNode = new TGWTTestNode(fParser,fGWTTree);
		    secondDataNode.fAntecedents.add(0,uniForm);
		    secondDataNode.fFirstjustno=selected.fLineno;secondDataNode.fWorld=selected.fWorld;
		    secondDataNode.fJustification=uniqueDJustification;

		    selected.fDead=true;

		    selected.straightInsert(selected,firstDataNode,secondDataNode,depthIncrement);

		}

	void doNegUnique(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
		
		// notEx!Fx

	//	TFormula newConstant = newConstantForBranches(selected);

	//	   if (newConstant==null)
	//	     return;
		   
		   // not one at all or at least two

			  TFormula firstVar=theFormula.fRLink.quantVarForm();
			  TFormula Fx=theFormula.fRLink.fRLink.copyFormula();
			  TFormula notFx=new TFormula(
			  			unary,
			  			String.valueOf(chNeg),
			  			null,
			  			Fx);
			  TFormula allxnotFx=new TFormula(
			  			quantifier,
			  			String.valueOf(chUniquant),
			  			firstVar.copyFormula(),
			  			notFx);
			  
			  
		//	  TFormula Fa=Fx.copyFormula();
		//	  Fa.subTermVar(Fa,newConstant,firstVar);
			  
			  Set <String> oldVariables =fParser.variablesInFormula(theFormula);
			  String secondVarStr= fParser.nthNewVariable(1,oldVariables);
			  
			  if(secondVarStr.equals(""))
				  return;
			  TFormula secondVar =new TFormula(
				  			variable,
				  			secondVarStr,
				  			null,
				  			null);

			  TFormula Fy=Fx.copyFormula();
			  Fy.subTermVar(Fy,secondVar,firstVar);
		  
		  TFormula exiForm = new TFormula(quantifier,
				  String.valueOf(chExiquant),
				  firstVar,
				  new TFormula(
				  			quantifier,
				  			String.valueOf(chExiquant),
				  			secondVar,
				  			new TFormula(
						  			binary,
						  			String.valueOf(chAnd),
						  			new TFormula(
								  			binary,
								  			String.valueOf(chAnd),
								  			Fx,
								  			Fy),
								  			
								  			
								  			new TFormula(
										  			unary,
										  			String.valueOf(chNeg),
										  			null,
										  			TFormula.equateTerms(firstVar.copyFormula(),secondVar.copyFormula()))						  			
								  			)
				  			));

	  
		   TGWTTestNode firstDataNode = new TGWTTestNode(fParser,fGWTTree);
		   firstDataNode.fAntecedents.add(0,allxnotFx);
		   firstDataNode.fFirstjustno=selected.fLineno;firstDataNode.fWorld=selected.fWorld;
		   firstDataNode.fJustification=negUniqueDJustification;

		    TGWTTestNode secondDataNode = new TGWTTestNode(fParser,fGWTTree);
		    secondDataNode.fAntecedents.add(0,exiForm);
		    secondDataNode.fFirstjustno=selected.fLineno;secondDataNode.fWorld=selected.fWorld;
		    secondDataNode.fJustification=negUniqueDJustification;


		    selected.fDead=true;

		    selected.splitInsert(selected,firstDataNode,secondDataNode,depthIncrement,fRoot);

		};


	/************* Identity **************/


	void doII(TGWTTestNode selected, int depthIncrement){

	  //we require selected here only to pick out the desired branch

	  TFormula variablenode= new TFormula();

	  variablenode.fKind = variable;
	  variablenode.fInfo = "x";

	  TFormula uniForm = new TFormula();

	  uniForm.fKind = quantifier;
	  uniForm.fInfo = String.valueOf(chUniquant);
	  uniForm.fLLink = variablenode;
	  uniForm.fRLink = TFormula.equateTerms(variablenode,variablenode.copyFormula());

	// (Allx)(x=x)

	  TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	  newDataNode.fAntecedents.add(0,uniForm);
	  newDataNode.fJustification=identityIJustification;
	  newDataNode.fWorld = selected.fWorld;

	  selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  }

	//Dec09
	String capturePossible(TFormula alpha, TFormula gamma, TFormula firstLineFormula){
		  String capturable="";
		  
	/*	  
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

		  } */
		  return
		      capturable;
		}

	/*
	private void getTheChoice(Action leftAction,    //the calling actions must remove the input pane
	                         Action rightAction,
	                         String heading,String prompt){

	  JButton leftButton=new JButton(leftAction);
	  JButton rightButton=new JButton(rightAction);

	  TProofInputPanel inputPane;

	  JTextField text = new JTextField(
	              prompt);
	  text.selectAll();

	  JButton[] buttons = {
	     new JButton(new CancelAction()), leftButton,rightButton};

	  inputPane = new TProofInputPanel(heading,text, buttons);

	  addInputPane(inputPane);

	  fInputPane.setVisible(true); // need this
	  text.requestFocus(); // so selected text shows

	}

*/
	
	/*
	public class IEYesNoAction extends AbstractAction{


	   IEAction fParent;
	   boolean fYes;
	   TFormula fSubstitution;

	   public IEYesNoAction(IEAction parent,boolean yes,TFormula substitution){

	     if (yes)
	       putValue(NAME, "Yes");
	     else
	       putValue(NAME, "No");

	     fParent=parent;
	     fYes=yes;
	     fSubstitution=substitution;

	   }


	   public void actionPerformed(ActionEvent ae){

	      TFormula surgeryTerm;

	     if (fParent.fNumTreated<fParent.fNumToTreat){

	        surgeryTerm= fParent.fTermsToTreat[fParent.fNumTreated];

	        surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


	        if (fYes){

	         // The surgery term might be a, f(a), f(g(a,b)) etc, and so too might be the term that is to
	         // be substituted, fSubstitution. We just copy everything across


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

	       fParent.fTermsToTreat[fParent.fNumTreated].fInfo= Symbols.chInsertMarker+
	                                                    fParent.fTermsToTreat[fParent.fNumTreated].fInfo;


	         String message= fParser.writeFormulaToString(fParent.fCopy);


	         fParent.fText.setText(message);

	         fParent.fText.requestFocus();

	     }
	     else{                                        //  last one, return to parent

	      JButton defaultButton = new JButton(fParent);

	      JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
	      TProofInputPanel inputPane = new TProofInputPanel("Doing =D-- Stage3,"+
	            " displaying result. " +
	            "If suitable, press Go.", fParent.fText, buttons);


	      addInputPane(inputPane);

	      String message= fParser.writeFormulaToString(fParent.fCopy);

	       fParent.fText.setEditable(true);
	       fParent.fText.setText(message);
	       fParent.fText.selectAll();

	      inputPane.getRootPane().setDefaultButton(defaultButton);
	      fInputPane.setVisible(true); // need this
	      fParent.fText.requestFocus();         // so selected text shows

	   fParent.fStage+=1;  // 3 I think

	   if (fParent.fStage==3)
	     fParent.askAboutGamma();  //I think
	   else
	     fParent.displayResult();


	     }

	   }

	 }


	public class IEAction extends AbstractAction{


	JTextField fText;


	TGWTTestNode fFirstline=null;
	TGWTTestNode fSecondline=null;
	TFormula fFirstFormula;
	TFormula fSecondFormula;
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

	//We only have to run through the occurrences seeing which ones they want to subs in 


	public IEAction(JTextField text, String label,TGWTTestNode firstline,TGWTTestNode secondline,
	                    TFormula firstFormula, TFormula secondFormula){
	   putValue(NAME, label);

	   fText = text;
	   fFirstline = firstline;
	   fSecondline = secondline;
	   fFirstFormula=firstFormula;
	   fSecondFormula=secondFormula;

	   fAlpha = fSecondFormula.firstTerm(); // alpha=gamma
	   fGamma = fSecondFormula.secondTerm();

	   fCopy = fFirstFormula.copyFormula(); //??

	   fNumAlpha = fFirstFormula.numOfFreeOccurrences(fAlpha);

	   if (fNumAlpha > 0) {
	     fAlphas = new TFormula[fNumAlpha];    // create an array of the actual terms in the copy that we will do surgery on

	     for (int i = 0; i < fNumAlpha; i++) { // initialize
	       fAlphas[i] = fCopy.depthFirstNthOccurence(fAlpha, i + 1); // one uses zero based index, other 1 based
	     }
	   }

	 fNumGamma = fFirstFormula.numOfFreeOccurrences(fGamma);

	   if (fNumGamma > 0) {
	     fGammas = new TFormula[fNumGamma];    // create an array of the actual terms in the copy that we will do surgery on

	     for (int i = 0; i < fNumGamma; i++) { // initialize
	       fGammas[i] = fCopy.depthFirstNthOccurence(fGamma, i + 1); // one uses zero based index, other 1 based
	     }
	   }
	 }


	public void start(){
	fStage=1;
	actionPerformed(null);
	}


	  public void actionPerformed(ActionEvent ae){


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

	void subFormCheck(){

	boolean flag =(fAlpha.numInPredOrTerm(fGamma)!=0)
	         ||(fGamma.numInPredOrTerm(fAlpha)!=0);

	if (flag){
	String outputStr="Do you wish to substitute for "
	    +fParser.writeFormulaToString(fAlpha) +"?";


	getTheChoice(new AbstractAction("No")
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
	             outputStr);

	}
	else{
	fStage = 2;
	actionPerformed(null);
	}
	}


	void alphaByGamma(){

	  int occurences =fFirstFormula.numOfFreeOccurrences(fAlpha);

	}

	private void displayResult(){

	String message= fParser.writeFormulaToString(fCopy);

	fText.setEditable(false);  // we don't want them changing it
	fText.setText(message);
	fText.selectAll();
	fText.requestFocus();

	fStage=5;

	}


	private void readResult(){


	if (fScope==null){
	boolean useFilter = true;
	ArrayList dummy = new ArrayList();

	String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

	TFormula root = new TFormula();
	StringReader aReader = new StringReader(aString);
	boolean wellformed;

//	wellformed= fParser.wffCheck(root, /*dummy, aReader);  // it can never be illformed since we put a well formed one there

	if (!wellformed) {
	  String message = "The string is illformed." +
	      (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

	  fText.setText(message);
	  fText.selectAll();
	  fText.requestFocus();
	}
	else {
	  fScope = root;

	  goodFinish();
	}
	}
	}


	private void goodFinish(){

	    if (fCopy.formulaInList(fFirstline.getInstantiations())){
	    	bugAlert("Identity Elimination, Observation.","You have created the result before.");
	    }
	    else{
		
		
		if (!fCopy.equalFormulas(fCopy, fFirstFormula)) {
	    TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
	    newDataNode.fAntecedents.add(0, fCopy);
	    newDataNode.fFirstjustno = fFirstline.fLineno;
	    newDataNode.fSecondjustno = fSecondline.fLineno;
	    newDataNode.fJustification = identityDJustification;
	    newDataNode.fWorld=fFirstline.fWorld; //the worlds are the same for both lines

	    // selected.fDead=true;    don't make it dead
	    
	    fFirstline.addToInstantiations(fCopy.copyFormula());  // don't let them do the same twice

	    straightInsert(fFirstline, newDataNode, null);

	    removeInputPane();
	  }
	  else{
	    removeInputPane();
	    bugAlert("=D", "You need to substitute for at least one occurrence.");
	    deSelectAll();
	  }
	    }
	}

	private void askAboutAlpha(){
	String aString;
	String message;

	if (fGammaOnly||fNumAlpha == 0) { // we just go on to gamma
	  fStage = 3;
	  askAboutGamma();
	}

	else {
	  if (fNumAlpha > 0) {

	    fAlphas[0].fInfo = Symbols.chInsertMarker + fAlphas[0].fInfo;
	    fTermsToTreat = fAlphas;
	    fNumTreated = 0;
	    fNumToTreat = fNumAlpha;

	    //********* going to yes/no subroutine *****

	    boolean yes = true;

	    JButton yesButton = new JButton(new IEYesNoAction(this, yes, fGamma));
	    JButton noButton = new JButton(new IEYesNoAction(this, !yes, fGamma));

	    message = fParser.writeFormulaToString(fCopy);

	    //JTextField text = new JTextField(message);

	    fText.setText(message);

	    JButton[] buttons = {
	        noButton, yesButton}; // put cancel on left
	    TProofInputPanel inputPane = new TProofInputPanel(
	        "Doing =D-- Stage1, substitute for this occurrence of left term?",
	        fText, buttons);

	    addInputPane(inputPane);

	    fInputPane.setVisible(true); // need this
	    fText.setEditable(false);
	    fText.requestFocus(); // so selected text shows

	    message = fParser.writeFormulaToString(fCopy);

	    fText.setText(message);
	    fText.selectAll();
	    fText.requestFocus();

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


	     fGammas[0].fInfo= Symbols.chInsertMarker+ fGammas[0].fInfo;
	     fTermsToTreat=fGammas;
	     fNumTreated=0;
	     fNumToTreat=fNumGamma;


	      //********* going to yes/no subroutine *****

	      boolean yes=true;

	   JButton yesButton = new JButton(new IEYesNoAction(this,yes,fAlpha));
	   JButton noButton = new JButton(new IEYesNoAction(this,!yes,fAlpha));


	   message= fParser.writeFormulaToString(fCopy);

	  //JTextField text = new JTextField(message);

	  fText.setText(message);

	  JButton[]buttons = {noButton, yesButton };  // put cancel on left
	  TProofInputPanel inputPane = new TProofInputPanel("Doing =D-- Stage2, substitute for this occurrence of right term?", fText, buttons);


	  addInputPane(inputPane);

	        fInputPane.setVisible(true); // need this
	        fText.setEditable(false);
	       fText.requestFocus();         // so selected text shows

	 message= fParser.writeFormulaToString(fCopy);

	fText.setText(message);
	fText.selectAll();
	fText.requestFocus();

	fStage=4;  // 3 probably, or 2

	   }
	 }

	}


	}

	void launchIEAction(TGWTTestNode firstline,TGWTTestNode secondline,
	                    TFormula firstFormula, TFormula secondFormula){

	TFormula alpha=secondFormula.firstTerm();
	TFormula gamma=secondFormula.secondTerm();

	String captured=capturePossible(alpha,   // alpha=gamma
	                                gamma,
	                                firstFormula);


	if (!captured.equals("")){

	  bugAlert("Problems with free and bound variables (remedy: rewrite bound variable)",
	      "The variable "+ captured + " occurs in the identity and is bound in "
	      + fParser.writeFormulaToString(firstFormula));

	}
	else{


	     // now we want to move into the substiuting bit



	     JTextField text = new JTextField("Starting =D"); ////// HERE
	     text.selectAll();

	     IEAction launchAction =new IEAction(text, "Go", firstline,
	         secondline,firstFormula,secondFormula);

	     JButton defaultButton = new JButton(launchAction);

//	     JButton defaultButton = new JButton(new IEAction(text, "Go", firstline,
//	         secondline));

	     JButton[] buttons = {
	         new JButton(new CancelAction()), defaultButton}; // put cancel on left
	     TProofInputPanel inputPane = new TProofInputPanel(
	         "Doing Identity Substitution", text, buttons);

	     addInputPane(inputPane);

	     inputPane.getRootPane().setDefaultButton(defaultButton);
	     fInputPane.setVisible(true); // need this
	     text.requestFocus(); // so selected text shows  

	     launchAction.start();
	   }
	}

	class FirstSecondAction extends AbstractAction{
	  boolean fFirst=true;
	  TGWTTestNode fFirstline;
	  TGWTTestNode fSecondline;
	  TFormula fFirstFormula;
	  TFormula fSecondFormula;

	  FirstSecondAction(boolean isFirst,TGWTTestNode firstline, TGWTTestNode secondline,
	                    TFormula firstFormula,TFormula secondFormula){
	    if (isFirst)
	       putValue(NAME, "First");
	     else
	       putValue(NAME, "Second");



	     fFirst=isFirst;
	     fFirstline=firstline;
	     fSecondline=secondline;
	     fFirstFormula=firstFormula;
	     fSecondFormula=secondFormula;
	  }

	  public void actionPerformed(ActionEvent ae){

	    if (!fFirst){       // if they want to subs in first, fine; otherwise we have to swap
	      TGWTTestNode temp = fFirstline;
	      fFirstline = fSecondline;
	      fSecondline = temp; // now the secondline is the identity

	      {TFormula tempFormula=fFirstFormula;
	        fFirstFormula=fSecondFormula;
	        fSecondFormula=tempFormula;
	      }

	    }


	    removeInputPane();

	    launchIEAction(fFirstline,fSecondline,fFirstFormula,fSecondFormula);

	  }

	}


	private void orderForSwap(TGWTTestNode firstSelection, TGWTTestNode secondSelection,
	                          TFormula firstFormula, TFormula secondFormula){
	/*{this determines which we are going to subs in-- they could both be identities}
	// this launches or puts up a prelim dialog which itself launches
	//we want the identity as the second line and the formula it is substituted in as the first line 

	   int dispatcher=0;
	   int inFirst=0;
	   int inSecond=0;

	   if (fParser.isEquality(firstFormula))
	     inSecond = (secondFormula).numOfFreeOccurrences(firstFormula.firstTerm()) +
	           (secondFormula).numOfFreeOccurrences(firstFormula.secondTerm());

	   if (fParser.isEquality(secondFormula))
	     inFirst = (firstFormula).numOfFreeOccurrences(secondFormula.firstTerm()) +
	           (firstFormula).numOfFreeOccurrences(secondFormula.secondTerm());

	   if ((inFirst+inSecond)==0)
	     return;                  //if neither appears in the other no substitution is possible


	   if (fParser.isEquality(firstFormula)){
	     if (!fParser.isEquality(secondFormula))
	       dispatcher=2;
	     else
	       dispatcher=3;    // both
	   }
	   else
	     dispatcher=1;     //first not, second is

	   switch (dispatcher){
	     case 0: break;   // neither an identity cannot happen because orderForSwap called only if at least one is
	     case 1:          // what we want first not identity second is
	       launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
	       break;
	     case 2: {        // wrong way round so we swap
	       {TGWTTestNode temp=firstSelection;
	         firstSelection = secondSelection;
	         secondSelection = temp; // now the secondSelection is the identity
	       }
	       {TFormula temp=firstFormula;
	       firstFormula=secondFormula;
	       secondFormula=temp;
	       }

	       launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
	       break;
	     }
	     case 3: {               // both identities

	       //{now, if neither of the second terms appear in the first, we want to subs in the second}
	        //  {if neeither of the first terms appear in the second, we want to subs in the first}
	        // {otherwise we have to ask} Don't fully understand the logic of this Jan06
	        // oh, I suppose it is this a=b and f(a)=c, can only subs in second etc.

	       if (inFirst == 0) {
	         {TGWTTestNode temp = firstSelection;
	           firstSelection = secondSelection;
	           secondSelection = temp; // now the secondSelection is the identity
	         }
	         {TFormula temp=firstFormula;
	           firstFormula = secondFormula;
	           secondFormula = temp;
	         }
	         launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
	       }
	       else {
	         if (inSecond == 0) { // leave them as they are, both identities some in first none in second
	           launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
	         }
	         else { // we ask

	           TProofInputPanel inputPane;
	           JTextField text = new JTextField(
	               "Do you wish to substitute in the first or in the second?");

	           text.setDragEnabled(true);
	           text.selectAll();

	           boolean isFirst = true;

	           JButton firstButton = new JButton(new FirstSecondAction(isFirst,
	               firstSelection, secondSelection,firstFormula,secondFormula));
	           JButton secondButton = new JButton(new FirstSecondAction(!isFirst,
	               firstSelection, secondSelection,firstFormula,secondFormula));

	           JButton[] buttons = {
	               new JButton(new CancelAction()), firstButton, secondButton}; // put cancel on left
	           inputPane = new TProofInputPanel("Doing Identity Substitution", text,
	                                            buttons);

	           addInputPane(inputPane);

	           //inputPane.getRootPane().setDefaultButton(firstButton);    //I don't think we want a default
	           fInputPane.setVisible(true); // need this
	           text.requestFocus(); // so selected text shows

	         }
	       }
	     break;}
	   }
	 }

*/
	
	
/*
 * 
 * public class UIHandler/*UIAction  implements ClickHandler/*AbstractAction {
		TextBox fText;
		TGWTTestNode fSelected=null;
		TFormula fFormula=null;
		int fDepth;
		
		 public UIHandler(TextBox text, String label, TGWTTestNode selected, TFormula formula,
				 int depthIncrement){
		    fText=text;
		    fSelected=selected;
		    fFormula=formula;
		    fDepth=depthIncrement;		 
		 }		
		
		public void onClick(ClickEvent event) {
		      // handle the click event
			String filteredStr=TUtilities.defaultFilter(fText.getText());
			
			TFormula term = new TFormula();
		     StringReader aReader = new StringReader(filteredStr);
		     boolean wellformed=false;

		     wellformed=fParser.term(term,aReader);
		     
		     if ((!wellformed)||(!term.isClosedTerm()/*fParser.isAtomicConstant(term) )) {
		  	       String message = "The string is neither a constant nor a closed term." +
		  	                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

		  	                        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

		  	                        fText.setText(message);
		  	                        fText.selectAll();
	//	  	                        fText.requestFocus();
		  	                      }
		     else {   // we're good
           	  
                 TFormula scope = fFormula.fRLink.copyFormula();
                 scope.subTermVar(scope,term,fFormula.quantVarForm());
                 
                 if (term.formulaInList(fSelected.getInstantiations())){
                 	bugAlert("Universal Decomposition, Observation.","You have already made this instantiation.");
                 }
                 else{
                	 
                    removeInputPanel();

                   TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
                   newDataNode.fAntecedents.add(0,scope);
                   newDataNode.fFirstjustno=fSelected.fLineno;
                   newDataNode.fJustification= UDJustification;
                   newDataNode.fWorld= fSelected.fWorld;

                  // selected.fDead=true;    don't make it dead

                   fSelected.addToInstantiations(term.copyFormula());

                   fSelected.straightInsert(fSelected,newDataNode,null,fDepth);

               	{TGWTTestNode dummy =fRoot;
            	
            	//TO DO it may be better to do this somewhere else
            	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
            	}
                   
                   
                 }

               }

           }
	}
 * 
 * 	
 */
	
	
	
public class IEHandler/*IEAction*/  implements ClickHandler/*AbstractAction*/ {
	TextBox fText;


	TGWTTestNode fFirstline=null;
	TGWTTestNode fSecondline=null;
	TFormula fFirstFormula;
	TFormula fSecondFormula;
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

	public IEHandler(TextBox text, String label,TGWTTestNode firstline,TGWTTestNode secondline,
            TFormula firstFormula, TFormula secondFormula){
// TO DO ? putValue(NAME, label);

fText = text;
fFirstline = firstline;
fSecondline = secondline;
fFirstFormula=firstFormula;
fSecondFormula=secondFormula;

fAlpha = fSecondFormula.firstTerm(); // alpha=gamma
fGamma = fSecondFormula.secondTerm();

fCopy = fFirstFormula.copyFormula(); //??

fNumAlpha = fFirstFormula.numOfFreeOccurrences(fAlpha);

if (fNumAlpha > 0) {
fAlphas = new TFormula[fNumAlpha];    // create an array of the actual terms in the copy that we will do surgery on

for (int i = 0; i < fNumAlpha; i++) { // initialize
fAlphas[i] = fCopy.depthFirstNthOccurence(fAlpha, i + 1); // one uses zero based index, other 1 based
}
}

fNumGamma = fFirstFormula.numOfFreeOccurrences(fGamma);

if (fNumGamma > 0) {
fGammas = new TFormula[fNumGamma];    // create an array of the actual terms in the copy that we will do surgery on

for (int i = 0; i < fNumGamma; i++) { // initialize
fGammas[i] = fCopy.depthFirstNthOccurence(fGamma, i + 1); // one uses zero based index, other 1 based
}
}
}
	
	
	//We only have to run through the occurrences seeing which ones they want to subs in 

	public void onClick(ClickEvent event) {

		actionPerformed();    //depends on state
	    }


	public void start(){
		fStage=1;
		actionPerformed();
		}


		  public void actionPerformed(){


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

	void subFormCheck(){

				boolean flag =(fAlpha.numInPredOrTerm(fGamma)!=0)
				         ||(fGamma.numInPredOrTerm(fAlpha)!=0);

				if (flag){
				String outputStr="Do you wish to substitute for "
				    +fParser.writeFormulaToString(fAlpha) +"?";

				Button leftButton=new Button("No");
				leftButton.addClickHandler(new ClickHandler()
				               {public void onClick(ClickEvent e){
				                 fGammaOnly=true;
				                 removeInputPanel();
				                 fStage = 3;  // go straight to askGamma and miss alpha
				                 askAboutGamma();
				               }});
				
				Button rightButton=new Button("Yes");
				rightButton.addClickHandler(new ClickHandler()
				               {public void onClick(ClickEvent e){
				            	   fAlphaOnly=true;
				                 removeInputPanel();
				                 fStage = 2;  // go straight to askGamma and miss alpha
				                 askAboutAlpha();
				               }});

				getTheChoice(leftButton,           //the calling actions must remove the input pane

				             rightButton,
				             "One term is a subterm of the other, just treat one at a time",
				             outputStr);

				}
				else{
				fStage = 2;
				actionPerformed();
				}
				}
		  
		  
	private void askAboutAlpha(){
		String aString;
		String message;

		if (fGammaOnly||fNumAlpha == 0) { // we just go on to gamma
		  fStage = 3;
		  askAboutGamma();
		}

		else {
		  if (fNumAlpha > 0) {

		    fAlphas[0].fInfo = Symbols.chInsertMarker + fAlphas[0].fInfo;
		    fTermsToTreat = fAlphas;
		    fNumTreated = 0;
		    fNumToTreat = fNumAlpha;

		    //********* going to yes/no subroutine *****

		    boolean yes = true;

		    Button yesButton = new Button("Yes");
		    yesButton.addClickHandler(new IEYesNoAction(this, yes, fGamma));
		    Button noButton = new Button("No");
		    noButton.addClickHandler(new IEYesNoAction(this, !yes, fGamma));

		    message = fParser.writeFormulaToString(fCopy);

		    //JTextField text = new JTextField(message);

		    fText.setText(message);
		    
		    fText.setReadOnly(true);

		    Button[] buttons = {
		        noButton, yesButton}; // put cancel on left
		    TGWTTreeInputPanel inputPane = new TGWTTreeInputPanel(
		        "Doing =D-- Stage1, substitute for this occurrence of left term?",
		        fText, buttons);

		    addInputPane(inputPane,!SELECT);

	//TO Do	    fInputPane.setVisible(true); // need this
		  //TO Do		    fText.setEditable(false);
		  //TO Do		    fText.requestFocus(); // so selected text shows

		    message = fParser.writeFormulaToString(fCopy);

		    fText.setText(message);
		    //fText.selectAll();
		    fText.setReadOnly(true);
		    
		  //TO Do		    fText.requestFocus();

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


		     fGammas[0].fInfo= Symbols.chInsertMarker+ fGammas[0].fInfo;
		     fTermsToTreat=fGammas;
		     fNumTreated=0;
		     fNumToTreat=fNumGamma;


		      //********* going to yes/no subroutine *****

		      boolean yes=true;

		/*   JButton yesButton = new JButton(new IEYesNoAction(this,yes,fAlpha));
		   JButton noButton = new JButton(new IEYesNoAction(this,!yes,fAlpha)); */

		   Button yesButton = new Button("Yes");
		    yesButton.addClickHandler(new IEYesNoAction(this, yes, fAlpha));
		    Button noButton = new Button("No");
		    noButton.addClickHandler(new IEYesNoAction(this, !yes, fAlpha));
		   
		   message= fParser.writeFormulaToString(fCopy);

		  //JTextField text = new JTextField(message);

		  fText.setText(message);
		  
		  fText.setReadOnly(true);

		  Button[]buttons = {noButton, yesButton };  // put cancel on left
		  TGWTTreeInputPanel inputPane = new TGWTTreeInputPanel("Doing =D-- Stage2, substitute for this occurrence of right term?", fText, buttons);


		  addInputPane(inputPane,!SELECT);

	/*TO DO	        fInputPane.setVisible(true); // need this
		        fText.setEditable(false);
		       fText.requestFocus();         // so selected text shows  */

		 message= fParser.writeFormulaToString(fCopy);

		fText.setText(message);
		//fText.selectAll();
		
		fText.setReadOnly(true);
//to do		fText.requestFocus();

		fStage=4;  // 3 probably, or 2

		   }
		 }

		}	
	
	
	private void displayResult(){

	String message= fParser.writeFormulaToString(fCopy);

//TO DO	fText.setEditable(false);  // we don't want them changing it
	fText.setText(message);
	//fText.selectAll();
//TO DO	fText.requestFocus();
	
	fText.setReadOnly(true);

	fStage=5;

	}
	
	
	private void readResult(){


		if (fScope==null){
		boolean useFilter = true;
		ArrayList dummy = new ArrayList();

		String aString = fText.getText();
				
		aString=TUtilities.defaultFilter(aString);
		
		//To DO clean up
		aString=TUtilities.logicFilter(aString);
		
		aString=TUtilities.htmlEscToUnicodeFilter(aString);
		// some might be &amp; etc.
		
		

		TFormula root = new TFormula();
		StringReader aReader = new StringReader(aString);
		boolean wellformed;

		wellformed= fParser.wffCheck(root, /*dummy,*/ aReader);  // it can never be illformed since we put a well formed one there

		if (!wellformed) {
		  String message = "The string is illformed." +
		      (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");

		  fText.setText(message);
	//	  fText.selectAll();
	//	  fText.requestFocus();
		}
		else {
		  fScope = root;

		  goodFinish();
		}
		}
		}	
	
	
	
	
	private void goodFinish(){

	    if (fCopy.formulaInList(fFirstline.getInstantiations())){
	    	bugAlert("Identity Elimination, Observation.","You have created the result before.");
	    }
	    else{
		
		
		if (!fCopy.equalFormulas(fCopy, fFirstFormula)) {
	    TGWTTestNode newDataNode = new TGWTTestNode(fParser, fGWTTree);
	    newDataNode.fAntecedents.add(0, fCopy);
	    newDataNode.fFirstjustno = fFirstline.fLineno;
	    newDataNode.fSecondjustno = fSecondline.fLineno;
	    newDataNode.fJustification = identityDJustification;
	    newDataNode.fWorld=fFirstline.fWorld; //the worlds are the same for both lines

	    // selected.fDead=true;    don't make it dead
	    
	    fFirstline.addToInstantiations(fCopy.copyFormula());  // don't let them do the same twice

	    
		int rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
		int selectedDepth= fRoot.getNodeDepth(fFirstline);
		int depthIncrement= rootDepth - selectedDepth;
				
	    //TO do check whether first or second
	    
		fFirstline.straightInsert(fFirstline, newDataNode, null,depthIncrement);

	    removeInputPanel();

       	{TGWTTestNode dummy =fRoot;
       	
       	fRoot.deSelectAll();
    	
    	//TO DO it may be better to do this somewhere else
    	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
    	}
	  }
	  else{
	    removeInputPanel();
	    bugAlert("=D", "You need to substitute for at least one occurrence.");
	    fRoot.deSelectAll();
	  }
	    }
	}	
	
	
	
	
	
	
	
		  

}


private void getTheChoice(Button leftButton,    //the calling actions must remove the input pane
		Button rightButton,
        String heading,String prompt){

	/*
Button leftButton=new Button();
leftButton.addClickHandler(leftAction);
Button rightButton=new Button();
rightButton.addClickHandler(rightAction);
*/
	
TGWTTreeInputPanel inputPane;

TextBox text = new TextBox();
text.setText(prompt);
text.selectAll();

Button[] buttons = {
cancelButton(), leftButton,rightButton};

inputPane = new TGWTTreeInputPanel(heading,text, buttons);

addInputPane(inputPane,!SELECT);

//TO DO 
/*fInputPane.setVisible(true); // need this
text.requestFocus(); // so selected text shows */

}





	
	
void launchIEAction(TGWTTestNode firstline,TGWTTestNode secondline,
            TFormula firstFormula, TFormula secondFormula){

	TFormula alpha=secondFormula.firstTerm();
	TFormula gamma=secondFormula.secondTerm();

	String captured=capturePossible(alpha,   // alpha=gamma
                        gamma,
                        firstFormula);


if (!captured.equals("")){

bugAlert("Problems with free and bound variables (remedy: rewrite bound variable)",
"The variable "+ captured + " occurs in the identity and is bound in "
+ fParser.writeFormulaToString(firstFormula));

}
else{


// now we want to move into the substiuting bit

	 Button defaultButton;
	 Button cancelButton=cancelButton();
	 TGWTTreeInputPanel inputPanel;


TextBox text = new TextBox(); //new TextBox("Starting =D"); ////// HERE
text.setText("Starting =D");

text.selectAll();

IEHandler/*IEAction*/ launchAction =new IEHandler(text, "Go", firstline,
 secondline,firstFormula,secondFormula);

//JButton defaultButton = new JButton(launchAction);
defaultButton = new Button("Go");
defaultButton.addClickHandler(new IEHandler(text,"Go", firstline,
		 secondline,firstFormula,secondFormula));


//JButton defaultButton = new JButton(new IEAction(text, "Go", firstline,
// secondline));

Button[] buttons = {cancelButton, defaultButton}; // put cancel on left
 inputPanel = new TGWTTreeInputPanel(
 "Doing Identity Substitution", text, buttons);

addInputPane(inputPanel,!SELECT);

/*TO DO
inputPane.getRootPane().setDefaultButton(defaultButton);
fInputPane.setVisible(true); // need this
text.requestFocus(); // so selected text shows  
*/
launchAction.start();


}
}	
	
	
class FirstSecondAction implements ClickHandler /*AbstractAction*/{
	  boolean fFirst=true;
	  TGWTTestNode fFirstline;
	  TGWTTestNode fSecondline;
	  TFormula fFirstFormula;
	  TFormula fSecondFormula;

	  FirstSecondAction(boolean isFirst,TGWTTestNode firstline, TGWTTestNode secondline,
	                    TFormula firstFormula,TFormula secondFormula){
	    if (isFirst)
	;//TO DO//       putValue(NAME, "First");
	     else
	    	 ;//TO DO     putValue(NAME, "Second");



	     fFirst=isFirst;
	     fFirstline=firstline;
	     fSecondline=secondline;
	     fFirstFormula=firstFormula;
	     fSecondFormula=secondFormula;
	  }

	  public void onClick(ClickEvent ae){

	    if (!fFirst){       // if they want to subs in first, fine; otherwise we have to swap
	      TGWTTestNode temp = fFirstline;
	      fFirstline = fSecondline;
	      fSecondline = temp; // now the secondline is the identity

	      {TFormula tempFormula=fFirstFormula;
	        fFirstFormula=fSecondFormula;
	        fSecondFormula=tempFormula;
	      }

	    }


	    removeInputPanel();

	    launchIEAction(fFirstline,fSecondline,fFirstFormula,fSecondFormula);

	  }

	}


public class IEYesNoAction implements ClickHandler /* AbstractAction*/{


	   IEHandler fParent;
	   boolean fYes;
	   TFormula fSubstitution;

	   public IEYesNoAction(IEHandler /*IEAction*/ parent,boolean yes,TFormula substitution){

	     if (yes)
	   ;//TO DO    putValue(NAME, "Yes");
	     else
	   ;//TO DO    putValue(NAME, "No");

	     fParent=parent;
	     fYes=yes;
	     fSubstitution=substitution;

	   }


	   public void onClick(ClickEvent ae){

	      TFormula surgeryTerm;

	     if (fParent.fNumTreated<fParent.fNumToTreat){

	        surgeryTerm= fParent.fTermsToTreat[fParent.fNumTreated];

	        surgeryTerm.fInfo=surgeryTerm.fInfo.substring(1);  // surgically omits the marker which is leading


	        if (fYes){

	         // The surgery term might be a, f(a), f(g(a,b)) etc, and so too might be the term that is to
	         // be substituted, fSubstitution. We just copy everything across


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

	       fParent.fTermsToTreat[fParent.fNumTreated].fInfo= Symbols.chInsertMarker+
	                                                    fParent.fTermsToTreat[fParent.fNumTreated].fInfo;


	         String message= fParser.writeFormulaToString(fParent.fCopy);


	         fParent.fText.setText(message);

//to do	         fParent.fText.requestFocus();

	     }
	     else{                                        //  last one, return to parent

	    //  JButton defaultButton = new JButton(fParent);
	    	 
	    	 Button defaultButton = new Button("Go");
	    	 defaultButton.addClickHandler(fParent);

	      Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
	      TGWTTreeInputPanel inputPane = new TGWTTreeInputPanel("Doing =D-- Stage3,"+
	            " displaying result. " +
	            "If suitable, press Go.", fParent.fText, buttons);


	      addInputPane(inputPane,!SELECT);

	      String message= fParser.writeFormulaToString(fParent.fCopy);

	//TO Do       fParent.fText.setEditable(true);
	       fParent.fText.setText(message);
	    //   fParent.fText.selectAll();  not sure

	     //TO Do     inputPane.getRootPane().setDefaultButton(defaultButton);
	     //TO Do      fInputPane.setVisible(true); // need this
	     //TO Do      fParent.fText.requestFocus();         // so selected text shows

	   fParent.fStage+=1;  // 3 I think

	   if (fParent.fStage==3)
		   fParent.askAboutGamma();  //I think
	   else
		   fParent.displayResult();


	     }

	   }

	 }



	
private void orderForSwap(TGWTTestNode firstSelection, TGWTTestNode secondSelection,
            TFormula firstFormula, TFormula secondFormula){
/*{this determines which we are going to subs in-- they could both be identities}
// this launches or puts up a prelim dialog which itself launches
//we want the identity as the second line and the formula it is substituted in as the first line 
*/
int dispatcher=0;
int inFirst=0;
int inSecond=0;

if (fParser.isEquality(firstFormula))
	inSecond = (secondFormula).numOfFreeOccurrences(firstFormula.firstTerm()) +
	(secondFormula).numOfFreeOccurrences(firstFormula.secondTerm());

if (fParser.isEquality(secondFormula))
	inFirst = (firstFormula).numOfFreeOccurrences(secondFormula.firstTerm()) +
	(firstFormula).numOfFreeOccurrences(secondFormula.secondTerm());

if ((inFirst+inSecond)==0)
	return;                  //if neither appears in the other no substitution is possible


if (fParser.isEquality(firstFormula)){
	if (!fParser.isEquality(secondFormula))
		dispatcher=2;
	else
		dispatcher=3;    // both
	}
else
dispatcher=1;     //first not, second is

switch (dispatcher){
case 0: break;   // neither an identity cannot happen because orderForSwap called only if at least one is
case 1:          // what we want first not identity second is
	launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
break;
case 2: {        // wrong way round so we swap
{TGWTTestNode temp=firstSelection;
firstSelection = secondSelection;
secondSelection = temp; // now the secondSelection is the identity
}
{TFormula temp=firstFormula;
firstFormula=secondFormula;
secondFormula=temp;
}

	launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
break;
}
case 3: {               // both identities

//{now, if neither of the second terms appear in the first, we want to subs in the second}
//  {if neeither of the first terms appear in the second, we want to subs in the first}
// {otherwise we have to ask} Don't fully understand the logic of this Jan06
// oh, I suppose it is this a=b and f(a)=c, can only subs in second etc.

if (inFirst == 0) {
{TGWTTestNode temp = firstSelection;
firstSelection = secondSelection;
secondSelection = temp; // now the secondSelection is the identity
}
{TFormula temp=firstFormula;
firstFormula = secondFormula;
secondFormula = temp;
}
	launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
}
else {
if (inSecond == 0) { // leave them as they are, both identities some in first none in second
	launchIEAction(firstSelection,secondSelection,firstFormula,secondFormula);
}
else { // we ask

	
TGWTTreeInputPanel inputPane;
TextBox text = new TextBox();
text.setText(
		 "Do you wish to substitute in the first or in the second?");

//TO DO text.setDragEnabled(true);
//text.selectAll();

text.setReadOnly(true);

boolean isFirst = true;

Button firstButton = new Button("First");
firstButton.addClickHandler((new FirstSecondAction(isFirst,
 firstSelection, secondSelection,firstFormula,secondFormula)));

Button secondButton = new Button("Second");
secondButton.addClickHandler((new FirstSecondAction(!isFirst,
		firstSelection, secondSelection,firstFormula,secondFormula)));


/*JButton secondButton = new JButton(new FirstSecondAction(!isFirst,
 firstSelection, secondSelection,firstFormula,secondFormula)); */

Button[] buttons = {
 cancelButton(), firstButton, secondButton}; // put cancel on left
inputPane = new TGWTTreeInputPanel("Doing Identity Substitution", text,
                              buttons);

addInputPane(inputPane,!SELECT);

//inputPane.getRootPane().setDefaultButton(firstButton);    //I don't think we want a default
//TO DO fInputPane.setVisible(true); // need this
//TO DO text.requestFocus(); // so selected text shows

}
}
break;}
}
}
	
	
	
	
	
	
	
	
	boolean isIEPossible(TGWTTestNode [] selectedNodes){
	  if ((selectedNodes!=null)&&(selectedNodes.length==2)){
	    TGWTTestNode firstSelected = selectedNodes[0];
	    TGWTTestNode secondSelected = selectedNodes[1];

	    if(!firstSelected.fWorld.equals(secondSelected.fWorld))
	      return
	          false;


	    TFormula firstFormula = null;
	    TFormula secondFormula = null;

	  if ((firstSelected.fAntecedents != null &&
	      firstSelected.fAntecedents.size() == 1)&&
	     (secondSelected.fAntecedents != null &&
	      secondSelected.fAntecedents.size() == 1)) {
	    firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
	    secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));

	    if ((fParser.isEquality(firstFormula) &&
	        firstFormula.firstTerm().isClosedTerm() &&
	        firstFormula.secondTerm().isClosedTerm())

	        ||
	        (fParser.isEquality(secondFormula)) &&
	        secondFormula.firstTerm().isClosedTerm() &&
	        secondFormula.secondTerm().isClosedTerm())

	      return
	          true;
	  }

	}
	return
	    false;

	}


	public void doIE (TGWTTestNode firstSelected,TGWTTestNode secondSelected,
	             TFormula firstFormula,TFormula secondFormula){


	  if ((TParser.isEquality(firstFormula) &&
	      firstFormula.firstTerm().isClosedTerm() &&
	      firstFormula.secondTerm().isClosedTerm())

	      ||
	      (TParser.isEquality(secondFormula)) &&
	      secondFormula.firstTerm().isClosedTerm() &&
	      secondFormula.secondTerm().isClosedTerm())
	    	{
		  	orderForSwap(firstSelected, secondSelected,firstFormula,secondFormula); // this launches or puts up a prelim dialog which launches
	        //{we allow substitution is any formula provided no variable in the equality is bound in the formula}
	    	}
	  }



	void doImplic(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	 TFormula leftFormula=theFormula.fLLink.copyFormula();

	 TFormula newFormula = new TFormula();

	newFormula.fKind = TFormula.unary;
	newFormula.fInfo = String.valueOf(Symbols.chNeg);
	newFormula.fRLink = leftFormula;

	 leftFormula= newFormula;                       //not A


	TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	leftDataNode.fAntecedents.add(0,leftFormula);
	leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	leftDataNode.fJustification=implicDJustification;

	 TFormula rightFormula=theFormula.fRLink.copyFormula();

	TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	rightDataNode.fAntecedents.add(0,rightFormula);
	rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	rightDataNode.fJustification=implicDJustification;

	selected.fDead=true;

	selected.splitInsert(selected,leftDataNode,rightDataNode,depthIncrement,fRoot);

	}


	void doNegAnd(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	   TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

	   TFormula newFormula = new TFormula();

	  newFormula.fKind = TFormula.unary;
	  newFormula.fInfo = String.valueOf(Symbols.chNeg);
	  newFormula.fRLink = leftFormula;

	   leftFormula= newFormula;                       //not A


	  TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	  leftDataNode.fAntecedents.add(0,leftFormula);
	  leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	  leftDataNode.fJustification=negAndDJustification;

	   newFormula = new TFormula();
	   TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

	   newFormula.fKind = TFormula.unary;
	newFormula.fInfo = String.valueOf(Symbols.chNeg);
	newFormula.fRLink = rightFormula;

	 rightFormula= newFormula;                       // not B


	  TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	  rightDataNode.fAntecedents.add(0,rightFormula);
	  rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	  rightDataNode.fJustification=negAndDJustification;

	  selected.fDead=true;

	  selected.splitInsert(selected,leftDataNode,rightDataNode,depthIncrement,fRoot);

	}

	void doNegArrow(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	  TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

	  TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	  leftDataNode.fAntecedents.add(0,leftFormula);
	  leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	  leftDataNode.fJustification=negArrowDJustification;

	  TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

	  TFormula newFormula = new TFormula();

	newFormula.fKind = TFormula.unary;
	newFormula.fInfo = String.valueOf(Symbols.chNeg);
	newFormula.fRLink = rightFormula;

	rightFormula= newFormula;                       //not B


	  TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	  rightDataNode.fAntecedents.add(0,rightFormula);
	  rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	  rightDataNode.fJustification=negArrowDJustification;


	  selected.fDead=true;

	  selected.straightInsert(selected,leftDataNode,rightDataNode, depthIncrement);
	  };


	void doNegEquiv(TGWTTestNode selected,TFormula theFormula,int depthIncrement){

	  TFormula A=theFormula.fRLink.fLLink.copyFormula(); //A
	  TFormula B=theFormula.fRLink.fRLink.copyFormula(); //B

	  TFormula notA = new TFormula(TFormula.unary,
	                         String.valueOf(chNeg), null, A.copyFormula());
	  TFormula notB = new TFormula(TFormula.unary,
	                         String.valueOf(chNeg), null, B.copyFormula());

	   TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	   leftDataNode.fAntecedents.add(0,A);
	   leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	   leftDataNode.fJustification=negEquivDJustification;


	   TGWTTestNode left2DataNode = new TGWTTestNode(fParser,fGWTTree);
	   left2DataNode.fAntecedents.add(0,notB);
	   left2DataNode.fFirstjustno=selected.fLineno;left2DataNode.fWorld=selected.fWorld;
	   left2DataNode.fJustification=negEquivDJustification;


	   TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	   rightDataNode.fAntecedents.add(0,notA);
	   rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	   rightDataNode.fJustification=negEquivDJustification;


	   TGWTTestNode right2DataNode = new TGWTTestNode(fParser,fGWTTree);
	   right2DataNode.fAntecedents.add(0,B);
	   right2DataNode.fFirstjustno=selected.fLineno;right2DataNode.fWorld=selected.fWorld;
	   right2DataNode.fJustification=negEquivDJustification;


	   selected.fDead=true;

	   selected.splitInsertTwo(selected,leftDataNode,left2DataNode,
	                   rightDataNode,right2DataNode, depthIncrement);
	 }

	  void doNegExi(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //not is to all not
	    TFormula uniFormula =theFormula.fRLink.copyFormula(); // the un-negated Exi
	    uniFormula.fInfo=String.valueOf(chUniquant); //changed exiquant to uniquant
	    uniFormula.fRLink=new TFormula(TFormula.unary,
	                               String.valueOf(Symbols.chNeg),
	                               null,
	                               uniFormula.fRLink);   //negated scope

	    TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	    newDataNode.fAntecedents.add(0,uniFormula);
	    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	    newDataNode.fJustification=negExiDJustification;

	    selected.fDead=true;

	    selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  };

	  void doNegUni(TGWTTestNode selected,TFormula theFormula,int depthIncrement){  //not all to is not
	    TFormula exiFormula =theFormula.fRLink.copyFormula(); // the un-negated Uni
	    exiFormula.fInfo=String.valueOf(chExiquant); //changed Uniquant to exiquant
	    exiFormula.fRLink=new TFormula(TFormula.unary,
	                               String.valueOf(Symbols.chNeg),
	                               null,
	                               exiFormula.fRLink);   //negated scope

	    TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	    newDataNode.fAntecedents.add(0,exiFormula);
	    newDataNode.fFirstjustno=selected.fLineno;newDataNode.fWorld=selected.fWorld;
	    newDataNode.fJustification=negUniDJustification;

	    selected.fDead=true;

	    selected.straightInsert(selected,newDataNode,null,depthIncrement);
	  };

	  void doNore(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
	    TFormula leftFormula=theFormula.fRLink.fLLink.copyFormula();

	    TFormula newFormula = new TFormula();

	    newFormula.fKind = TFormula.unary;
	    newFormula.fInfo = String.valueOf(Symbols.chNeg);
	    newFormula.fRLink = leftFormula;

	    leftFormula=newFormula;

	    TGWTTestNode leftDataNode = new TGWTTestNode(fParser,fGWTTree);
	    leftDataNode.fAntecedents.add(0,leftFormula);
	    leftDataNode.fFirstjustno=selected.fLineno;leftDataNode.fWorld=selected.fWorld;
	    leftDataNode.fJustification=noreDJustification;

	    TFormula rightFormula=theFormula.fRLink.fRLink.copyFormula();

	    newFormula = new TFormula();

	    newFormula.fKind = TFormula.unary;
	    newFormula.fInfo = String.valueOf(Symbols.chNeg);
	    newFormula.fRLink = rightFormula;

	    rightFormula=newFormula;

	    TGWTTestNode rightDataNode = new TGWTTestNode(fParser,fGWTTree);
	    rightDataNode.fAntecedents.add(0,rightFormula);
	    rightDataNode.fFirstjustno=selected.fLineno;rightDataNode.fWorld=selected.fWorld;
	    rightDataNode.fJustification=noreDJustification;


	    selected.fDead=true;

	    selected.straightInsert(selected,leftDataNode,rightDataNode,depthIncrement);
	  };

	 


	/************************ UI Click Handler (was a Swing Action)****************/
	 
	 /*Don't want to instantiate to the same formula twice*/

	public class UIHandler/*UIAction*/ implements ClickHandler/*AbstractAction*/{
		TextBox fText;
		TGWTTestNode fSelected=null;
		TFormula fFormula=null;
		int fDepth;
		
		 public UIHandler(TextBox text, String label, TGWTTestNode selected, TFormula formula,
				 int depthIncrement){
		    fText=text;
		    fSelected=selected;
		    fFormula=formula;
		    fDepth=depthIncrement;		 
		 }		
		
		public void onClick(ClickEvent event) {
		      // handle the click event
			String filteredStr=TUtilities.defaultFilter(fText.getText());
			
			TFormula term = new TFormula();
		     StringReader aReader = new StringReader(filteredStr);
		     boolean wellformed=false;

		     wellformed=fParser.term(term,aReader);
		     
		     if ((!wellformed)||(!term.isClosedTerm()/*fParser.isAtomicConstant(term)*/ )) {
		  	       String message = "The string is neither a constant nor a closed term." +
		  	                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

		  	                        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

		  	                        fText.setText(message);
		  	                        fText.selectAll();
	//	  	                        fText.requestFocus();
		  	                      }
		     else {   // we're good
           	  
                 TFormula scope = fFormula.fRLink.copyFormula();
                 scope.subTermVar(scope,term,fFormula.quantVarForm());
                 
                 if (term.formulaInList(fSelected.getInstantiations())){
                 	bugAlert("Universal Decomposition, Observation.","You have already made this instantiation.");
                 }
                 else{
                	 
                    removeInputPanel();

                   TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
                   newDataNode.fAntecedents.add(0,scope);
                   newDataNode.fFirstjustno=fSelected.fLineno;
                   newDataNode.fJustification= UDJustification;
                   newDataNode.fWorld= fSelected.fWorld;

                  // selected.fDead=true;    don't make it dead

                   fSelected.addToInstantiations(term.copyFormula());

                   fSelected.straightInsert(fSelected,newDataNode,null,fDepth);

               	{TGWTTestNode dummy =fRoot;
            	
            	//TO DO it may be better to do this somewhere else
            	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
            	}
                   
                   
                 }

               }

           }
	}
		
	/*   JTextComponent fText;
	   TGWTTestNode fSelected=null;
	   TFormula fFormula=null;

	   public UIAction(JTextComponent text, String label, TGWTTestNode selected, TFormula formula){
	     putValue(NAME, label);

	     fText=text;
	     fSelected=selected;
	     fFormula=formula;
	   }

	   public void actionPerformed(ActionEvent ae){

	     //boolean useFilter = true;
	     //ArrayList dummy = new ArrayList();

	     String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

	     TFormula term = new TFormula();
	     StringReader aReader = new StringReader(aString);
	     boolean wellformed=false;

	     wellformed=fParser.term(term,aReader);

	     if ((!wellformed)||(!term.isClosedTerm()/*fParser.isAtomicConstant(term)*/ /*)) {
	       String message = "The string is neither a constant nor a closed term." +
	                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

	                        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

	                        fText.setText(message);
	                        fText.selectAll();
	                        fText.requestFocus();
	                      }

	                      else {   // we're good
	                    	  
	                    	 
	                    	  

	                        TFormula scope = fFormula.fRLink.copyFormula();
	                        scope.subTermVar(scope,term,fFormula.quantVarForm());
	                        
	                        if (term.formulaInList(fSelected.getInstantiations())){
	                        	bugAlert("Universal Decomposition, Observation.","You have already made this instantiation.");
	                        }
	                        else{

	                          TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
	                          newDataNode.fAntecedents.add(0,scope);
	                          newDataNode.fFirstjustno=fSelected.fLineno;
	                          newDataNode.fJustification= UDJustification;
	                          newDataNode.fWorld= fSelected.fWorld;

	                         // selected.fDead=true;    don't make it dead

	                          fSelected.addToInstantiations(term.copyFormula());

	                          straightInsert(fSelected,newDataNode,null);

	                          removeInputPane();
	                        }

	                      }

	                  }
*/



	              /************************ End of UI Action *********/

	 public void doUni(TGWTTestNode selected,TFormula theFormula,int depthIncrement){
		 Button defaultButton;
		 Button cancelButton=cancelButton();
		 TGWTTreeInputPanel inputPanel;
		 
		 TextBox textBox = new TextBox();
		 textBox.setText("Constant, or closed term, to instantiate with?");
		 textBox.selectAll();
		
		 
		 defaultButton = new Button("Go");
		 defaultButton.addClickHandler(new UIHandler(textBox,"Go", selected, theFormula,depthIncrement));
		 
		 Button[]buttons={cancelButton, defaultButton };
		 
		 inputPanel = new TGWTTreeInputPanel("Doing "+chUniquant+ "D", textBox, buttons);
		 
		 addInputPane(inputPanel,SELECT);
		 
		 /*   JButton defaultButton;
	   TProofInputPanel inputPane;

	   JTextField text = new JTextField("Constant, or closed term, to instantiate with?");
	   text.selectAll();

	   defaultButton = new JButton(new UIAction(text,"Go", selected, theFormula));

	   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
	   inputPane = new TProofInputPanel("Doing "+chUniquant+ "D", text, buttons);


	   addInputPane(inputPane);
	   inputPane.getRootPane().setDefaultButton(defaultButton);
	   fInputPane.setVisible(true); // need this
	   text.requestFocus();         // so selected text shows
*/	};

	
  
 
  
  
  
  
  
  
  /**************   End of Sun May 20 ***************/
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
		
		  public void writeToJournal(String message, boolean highlight,boolean toMarker){
			    if (fJournal==null)
			      System.out.println("writeToJournal() called with null Journal. With applets might not be an error.");
			    else
			      fJournal.writeToJournal(message, highlight, toMarker);
			  }

		public String getStartStr() {
			return fStartStr;
		}

		public void setStartStr(String fStartStr) {
			this.fStartStr = fStartStr;
		}		


/************************* Modal *********************************/
	
		
	public Widget[] supplyModalRadioButtons(){
		 Widget [] buttons =  {fS5,fK,fT,fS4,fS5Alt,fReflex, fSymm,fTrans};
		 
		 fS4.addClickHandler(new ClickHandler(){@Override public void onClick(ClickEvent event){
				fS4.setValue(true);
				ruleSetMenuItem_actionPerformed();}});	 
		 fS5.addClickHandler(new ClickHandler(){@Override public void onClick(ClickEvent event){
				fS5.setValue(true);
				ruleSetMenuItem_actionPerformed();}});
		 fK.addClickHandler(new ClickHandler(){@Override public void onClick(ClickEvent event){
				fK.setValue(true);
				ruleSetMenuItem_actionPerformed();}});
		 fT.addClickHandler(new ClickHandler(){@Override public void onClick(ClickEvent event){
				fT.setValue(true);
				ruleSetMenuItem_actionPerformed();}});
		 fS5Alt.addClickHandler(new ClickHandler(){@Override public void onClick(ClickEvent event){
				fS5Alt.setValue(true);
				ruleSetMenuItem_actionPerformed();}});
		 
			fS5.setValue(true);  //default
		 
		 return
				 buttons;
	}
		
		
		void ruleSetMenuItem_actionPerformed() {

			if (fS4.getValue())
			    s4Switch=true;
			else
			    s4Switch=false;

			if (fS5.getValue())
			    s5Switch=true;
			else
			    s5Switch=false;

			if (fK.getValue())
			      kSwitch=true;
			  else
			    kSwitch=false;

			if (fT.getValue())
			    tSwitch=true;
			else
			    tSwitch=false;

			if (fS5Alt.getValue())
				s5AltSwitch=true;
			else
				s5AltSwitch=false;

			// now, the rules include each other

			if (s5Switch){
			   s5Rules=true;
			   kRules=false;
			   tRules=false;
			   s4Rules=false;
			   s5AltRules=false;
			}

			if (kSwitch){
				   s5Rules=false;
				   kRules=true;
				   tRules=false;
				   s4Rules=false;
				   s5AltRules=false;
				}

			if (tSwitch){
				   s5Rules=false;
				   kRules=true;
				   tRules=true;
				   s4Rules=false;
				   s5AltRules=false;
				}

			if (s4Switch){
				   s5Rules=false;
				   kRules=true;
				   tRules=true;
				   s4Rules=true;
				   s5AltRules=false;
				}

			if (s5AltSwitch){
				   s5Rules=false;
				   kRules=true;
				   tRules=true;
				   s4Rules=true;
				   s5AltRules=true;
				}
			}		
		  
		  
class ReflexHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
	    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);
	    int rootDepth=0;
		int selectedDepth=0;
		int depthIncrement=0;
	    
	    if ((selectedNodes!=null)&&(selectedNodes.size()==1))
	    	{
	    	TGWTTestNode selected= selectedNodes.get(0);
	    	
	    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
	    	selectedDepth= fRoot.getNodeDepth(selected);
	    	depthIncrement= rootDepth - selectedDepth;
	    
		       TFormula theFormula=null;

		      if (//!selected.fDead&&               //can select dead for closing formula
		          !selected.fWorld.equals(TGWTTestNode.nullWorld)&&
		    	  selected.fAntecedents!=null&&
		          selected.fAntecedents.size()==1){
		        theFormula=(TFormula)(selected.fAntecedents.get(0));
		      }
		      else
		        return
		          ;
		      
		    doRefI(selected, theFormula, depthIncrement);
	    	}
		}
}

class SymmHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);
    int rootDepth=0;
	int selectedDepth=0;
	int depthIncrement=0;
    
    if ((selectedNodes!=null)&&(selectedNodes.size()==1))
    	{
    	TGWTTestNode selected= selectedNodes.get(0);
    	
    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	selectedDepth= fRoot.getNodeDepth(selected);
    	depthIncrement= rootDepth - selectedDepth;
    
	      TFormula theFormula=null;

	      if (//!selected.fDead&&               //can select dead for closing formula
	         // !selected.fWorld.equals(TGWTTestNode.nullWorld)&&
	    	  selected.fAntecedents!=null&&
	          selected.fAntecedents.size()==1){
	        theFormula=(TFormula)(selected.fAntecedents.get(0));
	      }
	      else
	        return
	          ;
	      
	    doSymI(selected, theFormula, depthIncrement);
    	}
	}
}




class TransHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);
    int rootDepth=0;
	int selectedDepth=0;
	int depthIncrement=0;
    
    if ((selectedNodes!=null)&&(selectedNodes.size()==2))
    	{
    	
    	
    	
    	
    	TGWTTestNode selected1= selectedNodes.get(0);
    	TGWTTestNode selected2= selectedNodes.get(1);
    	
    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	selectedDepth= fRoot.getNodeDepth(selected2);
    	depthIncrement= rootDepth - selectedDepth;
    
	      TFormula theFormula=null;

	      TFormula theFormula1,theFormula2=null;

	      if (//!selected.fDead&&               //can select dead for closing formula
	         // !selected.fWorld.equals(TTreeDataNode.nullWorld)&&
	    	  selected1.fAntecedents!=null&&
	          selected1.fAntecedents.size()==1&&
	          selected2.fAntecedents!=null&&
	          selected2.fAntecedents.size()==1){
	        theFormula1=(TFormula)(selected1.fAntecedents.get(0));
	        theFormula2=(TFormula)(selected2.fAntecedents.get(0));
	      }
	      else
	        return
	          ;
	      
	      doTransI(selected1,selected2, theFormula1,theFormula2,depthIncrement);
    	}
	}
}
		
		
		  
}
