package us.softoption.proofs;

import static us.softoption.infrastructure.Symbols.chLeftCurlyBracket;
import static us.softoption.infrastructure.Symbols.chQuestionMark;
import static us.softoption.infrastructure.Symbols.chRightCurlyBracket;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.google.gwt.user.client.ui.VerticalPanel;

import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.infrastructure.TConstants;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TDefaultParser;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;



/*A bit like MyProofPanel in the old pure java code*/


public class TMyProofController extends TProofController{
	
	
	public TMyProofController(){;

	}
	
	public TMyProofController(TParser aParser, TReset aClient,TJournal itsJournal, VerticalPanel inputPanel,
			 TProofDisplayCellTable itsDisplay){
		
		super(aParser,aClient,itsJournal,inputPanel,itsDisplay);
		
		
	}	
	
	/*******************  Factory *************************/

	TReAssemble supplyTReAssemble (TTestNode root){         // so we can subclass
	  return
	      new TReAssemble(fParser, root, null, 0);
	}


	/******************************************************/

	
	
	
/************************** Starting Proofs **********************/
	
@Override	
	  public void startProof(String inputStr){


            dismantleProof(); //{previous one}

		    initProof();

		    if (load(inputStr))
		      startUp();

		  }	
	
@Override	
void initializeParser(){
	  fParser=new TDefaultParser();
	};

	
	/*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
    compound terms eg Pf(a,b),Hc.

 Also in some systems a relation Lxy is written L(x,y) ie also with commas

Also, in set theory we write {1,2,3} for a 'comprehension' of a set 

 We want to separate the premises but not the terms. So we will change the
 premise comma separators to another character. For the moment '!'*/


//private static char chSeparator='ÔøΩ';  // need to pick a character here that is not used in logic
protected static char chSeparator='\u00E7';  // need to pick a character here that is not used in logic

protected String changeListSeparator(String input){

int nested=0;
char currCh;

StringBuffer output= new StringBuffer(input);
for (int i=0;i<input.length();i++){
  currCh=output.charAt(i);

  if ((currCh==chSmallLeftBracket)||
  	(currCh==chLeftCurlyBracket))
    nested++;
  if ((currCh==chSmallRightBracket)||
  	(currCh==chRightCurlyBracket))
    nested--;

  if ((nested<1)&&(currCh==chComma))    //commas separating the list of premises are not nested
    output.setCharAt(i,chSeparator);
}

return
    output.toString();
}	
	
	
	
	


public boolean load(String inputStr){

    TParser parser=null;;

   if (fParser==null) //there may be one passed in with initialization
	   initializeParser();
  
    parser = fParser;
    
    parser.initializeErrorString();


   ArrayList dummy=new ArrayList();
   boolean wellformed = true;

   fProofStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there

//   StringSplitData data;

   if ((inputStr==null)||inputStr.equals(strNull)){

     createBlankStart();
     return
         wellformed;
   }

String[]premisesAndConclusion = inputStr.split(String.valueOf(chTherefore),2);  /* they may input two
    therefore symbols, in which case we'll split at the first and let the parser report the second*/

/*What split will do is to split on the occurrences of therefore, so if therefore is first you get
 * two strings, the nullstr and then rest. If therefore is last, I don't know what happens but I  guess
 * you end on a nullstr. If therefore does not occur you get the lot.]
 */


if (premisesAndConclusion[0]!=null&&(!premisesAndConclusion[0].equals(strNull))){  // there are premises

/*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
 compound terms eg Pf(a,b),Hc.

Also in some systems a relation Lxy is written L(x,y) ie also with commas


We want to separate the premises but not the terms. So we will change the
premise comma separators to another character
 */

premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);

 StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSeparator)/*String.valueOf(chComma)*/);

 while ((premises.hasMoreTokens())&&wellformed){
    inputStr=premises.nextToken();

 if (inputStr!=null&&!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
        TFormula root = new TFormula();
        StringReader aReader = new StringReader(inputStr);


        wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

        if (!wellformed){
       //   fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
          bugAlert ("Error on reading.",fParser.fCurrCh + TConstants.fErrors12 + 
       		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""));
        }
          else
            {addPremise(root);
            if (fProofStr.length()==0)
              fProofStr=inputStr;
            else
              fProofStr+=chComma+inputStr;
            }
        }
 }          // done with premises
}

//now to look at the conclusion
 if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
	 // and it will have length 1 and just be premises
   inputStr = premisesAndConclusion[1];

   if (inputStr!=null&&!inputStr.equals(strNull)){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
        TFormula root = new TFormula();
        StringReader aReader = new StringReader(inputStr);

        wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

        if (!wellformed){
        //  fDeriverDocument.writeToJournal(parser.fCurrCh + TConstants.fErrors12 + parser.fParserErrorMessage, TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
          bugAlert ("Error on reading.",fParser.fCurrCh + TConstants.fErrors12 + 
          		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""));
          
        }
          else
            {addConclusion(root);
            fProofStr+=chTherefore+inputStr;
            }
        }

 }


//TO DO synchronization

fDisplayCellTable.synchronizeViewToData();

   return
       wellformed;

 }


void addPremise(TFormula root){
	   TProofline newline= supplyProofline();
	   newline.fFormula=root.copyFormula();
	   newline.fJustification = fAssJustification;

	   switch (fProofType){

	     case noPremNoConc: {
	       newline.fLineno=1;
	       newline.fHeadlevel=0;

	       fModel.insertFirst(newline);

	       fProofType=premNoConc;
	       break;
	     }

	     case premNoConc:
	       fModel.insertAtPseudoTail(newline);
	        break;

	     default:  break;

	   }

	 }

void addConclusion(TFormula root){
    if ((fProofType == premNoConc) || (fProofType == noPremNoConc))  // don't add a second one}
    {

       if (fProofType == noPremNoConc)
        createBlankStart();

//         {gHint := true;                      this is a global to show that the proof is in gHint mode}
//   {  gTail := gPseudoTail;            gPseudoTail marks where the insertions will be made. See Append}

       addTailLines(root);

       if (fProofType == premNoConc)
        fProofType = premConc;
       else
        fProofType = noPremConc;
    }
}

void addTailLines(TFormula root){
	   TFormula newnode= new TFormula(TFormula.predicator,String.valueOf(chQuestionMark),null,null);
	   TProofline newline= supplyProofline();

	   newline.fFormula=newnode;
	   newline.fJustification = "?";
	   newline.fSelectable=false;

	   fModel.insertAtTailFirst(newline);

	   {TProofline secondline = supplyProofline();

	     secondline.fFormula = root.copyFormula();
	     secondline.fJustification = "?";
	     secondline.fSelectable = false;

	   fModel.insertAtTailLast(secondline);
	   }
}



void startUp(){

fModel.setLastAssumption();

collapseTrivialCase();

fModel.placeInsertionMarker();

//to do this.setVisible(true);

}

class FindConclusion implements FunctionalParameter {   //this should be more general, doesn't it just find a formula?
    boolean found=false;
    TFormula conclusion;
    int lineno=0;

    FindConclusion(TFormula theConclusion){
    conclusion=theConclusion;
    }

  public void  execute(Object parameters){
    TProofline workingLine =(TProofline)parameters;

    if (!found){
      found=conclusion.equalFormulas(conclusion, workingLine.fFormula);
      lineno=workingLine.fLineno;
    }
  }

  public boolean testIt(Object parameter){
    return
        false;
  }
}

void collapseTrivialCase(){


	  if ((fProofType == premConc) || (fProofType == noPremConc)){
	    TProofline conclusionLine = fModel.getTailLastLine();
	    FindConclusion finder = new FindConclusion(conclusionLine.fFormula);

	    fModel.doToEachInHead(finder);

	    if (finder.found){
	   //   System.out.print("found it");
	      conclusionLine.fLineno=conclusionLine.fLineno-1;
	      conclusionLine.fFirstjustno=finder.lineno;
	      conclusionLine.fJustification= " R";
	      conclusionLine.fSelectable=true;
	      conclusionLine.fSubProofSelectable=false;

	      fModel.addToHead(fModel.getHeadSize(),conclusionLine);  // last in head without resetting levels etc.

	      fModel.clearTail();

	      fProofType = pfFinished;
	    }

	  }

	  }


/*******************  deriving **************************/

/*

void deriveItMenuItem_actionPerformed(ActionEvent e) {
boolean allLines=true;
doDerive(allLines);
  }

void nextLineMenuItem_actionPerformed(ActionEvent e) {
  boolean allLines=true;
doDerive(!allLines);

    }

*/


  ArrayList createProofSegment(TTestNode root){

  TReAssemble aReAssembly= supplyTReAssemble(root); //new TReAssemble(fParser,root,null,0);

    aReAssembly.reAssembleProof();

    ArrayList tempHead=aReAssembly.fHead;
    int lastAssumption = aReAssembly.fLastAssIndex;



    improve(tempHead, lastAssumption);  // seems of  temp out Dec)6

    setHeadLevels(tempHead);

    // improve
    // set head levels
    //prepare segment for splice


  //  prepareSegmentForSplice(fModel.getHead(),tempHead);

    int dummy=0;

    TMergeData mergeData=new TMergeData(fModel.getHead(),dummy,
                                        tempHead,lastAssumption,dummy,dummy,
                                        aReAssembly.supplyProofline());

    mergeData.prepareSegmentForSplice();


    return
        tempHead;

  }

  /*

   procedure CreatProofSegment;

       var
        lastAssumption, dummy: INTEGER;


       procedure SetHeadLevels;

        var
         headLevel: INTEGER;

        procedure SetLevel (item: Tobject);

        begin
         TProofline(item).fHeadLevel := headLevel;
        end;

       begin
        if TProofline(fHead.First).fblankline then
         headLevel := -1 {no antecedents}
        else
         headLevel := 0;
        tempHead.Each(SetLevel);
       end;

      begin
       lastAssumption := 0;

    {$IFC bestpath}
       ReAssembleProof(gTestroot, tempHead, lastAssumption); { }
    {$ENDC}

       Improve(tempHead); {12 March 1991 moved on a few lines mf put back in}

       SetHeadLevels; {sadly, each proofline has to carry info on its head level}

       PrepareSegmentForSplice(fHead, tempHead, lastAssumption);

    {Improve(tempHead); new position}

    (*major changes here because you may prove something that is an assumption*)

    (*RemoveDuplicatesinNew; *)
      end;


  */





@Override 
void doDerive(boolean allLines){
  int outcome;

    if (fModel.getTailSize()==0){
      bugAlert("Exiting from Derive It. Warning.",
               "We need a target conclusion to derive to.");
      return;
    }



   if (fModel.getTailLine(0).fSubprooflevel<fModel.getHeadLastLine().fSubprooflevel){
     bugAlert("Exiting from Derive It. Warning.",
              "First please drop the extra assumptions.");
     return;
   }


   TTestNode aTestRoot=assembleTestNode();

   if (aTestRoot==null)
     return;

//   aTestRoot.startSatisfactionTree();

  // aTestRoot.fStepsToExpiry=TTestNode.kMaxTreeDepth;

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fSwingTreeNode);  //debug remove later

  // aTestRoot.fTreeModel=aTreeModel;

   aTestRoot.initializeContext(aTreeModel);  //debug Tree Model initialized now


  // int maxSteps=50;

   outcome=aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);

 /*  if (TConstants.DEBUG){

     TTestDisplayTree aTestDisplayTree = new TTestDisplayTree(aTreeModel); //debug
     aTestDisplayTree.display(); //debug
   } */


   // need to do the CV stuff

   switch (outcome){

      case TTestNode.valid:
        bugAlert("Derive It. Derivation found.",
              "Please wait while it is re-assembled.");

     boolean debug=false;

 /*    if (debug)
     {
       ArrayList debugHead= createProofSegment(aTestRoot.getLeftChild());
       insertAll(debugHead);
     } */

       ArrayList tempHead= createProofSegment(aTestRoot);
       if (allLines)
          insertAll(tempHead);
       else
          insertFirstLine(tempHead);

        removeBugAlert();
        break;
      case TTestNode.notValid:
        bugAlert("Derive It. Warning.",
              "Not derivable from these standing assumptions.");
        break;

      case TTestNode.notKnown:
        bugAlert("Derive It. Warning.",
              "Unsure whether sequent can be derived.");
        break;


   }

 /*

   if gexCVflag then
       if not (outcome = valid) then
        gexCV := TRUE;
      if gUniCVflag then
       if not (outcome = valid) then
        gUniCV := TRUE;

      if gexCV or gUniCV then
       begin
        temptest := gTestroot.CopyNodeinFull;
        temptest.fclosed := FALSE;
        temptest.fdead := FALSE;
        temptest.flLink := nil;
        temptest.fRlink := nil;

        DismantleTestTree(gTestroot);

        gTestroot := temptest;
        temptest := nil;

        outcome := gTestroot.TreeValid(kMaxtreesize); {check re-init stringstore}

       end;

      case outcome of
       valid:
        begin
        sysBeep(5); { ReDisplayProof;}
        derivationFound := true;

        BugAlert('A derivation has been found... please wait much longer.');

 {DismantleProof; previous one}

 {WriteDerivation;}

        BugAlert('!');

        end;

       notvalid:
        begin
        sysBeep(5);
        BugAlert('Not derivable from these standing assumptions.');
        end;
       notknown:
        begin
        sysBeep(5);
        BugAlert('Unsure whether the sequent can be derived.');
        end;
       otherwise
      end;

     end;


   end;


   */


/*
    This is a routine from TBrowser

    void selectionSatisfiable(){

         String inputStr;
         boolean wellFormed = true;
         int badChar=-1;
       ArrayList dummy=new ArrayList();

       ArrayList newValuation;

       TTestNode aTestRoot = new TTestNode(fDeriverDocument.fParser,null);  //does not initialize TreeModel

       DefaultTreeModel aTreeModel= new DefaultTreeModel(aTestRoot.fTreeNode);

       aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now
      // aTes


       aTestRoot.startSatisfactionTree();


      String selectionStr=TUtilities.readSelectionToString(fJournalPane,TUtilities.logicFilter);

      StringTokenizer st = new StringTokenizer(selectionStr, ",");

      while ((st.hasMoreTokens())&& wellFormed){

        inputStr = st.nextToken();

        if (inputStr != strNull) { // can be nullStr if they put two commas togethe,should just skip
          TFormula root = new TFormula();
          StringReader aReader = new StringReader(inputStr);

          newValuation = fDeriverDocument.fValuation;

          wellFormed = fDeriverDocument.fParser.wffCheck(root, newValuation,
              aReader);

          if (!wellFormed) {
            fDeriverDocument.writeToJournal(
                "(*You need to supply a list of well formed formulas"
                + " separated by commas. Next is what the parser has to"
                + " say about your errors.*)"
                + strCR
                + fDeriverDocument.fParser.fCurrCh + TConstants.fErrors12
                + fDeriverDocument.fParser.fParserErrorMessage,
                TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
          }
          else {

            badChar = badCharacters(root);

            if (badChar == kNone) {
              aTestRoot.addToAntecedents(root);
              fDeriverDocument.fValuation = newValuation;
                  /*notice here that we use only the last valuation for
               the whole thing
            }
            else {

              writeBadCharacterErrors(badChar);
              return;
            }
          }
        }
        System.out.print(inputStr + " ");
      }


    if (wellFormed){
           TFormula.interpretFreeVariables(fDeriverDocument.fValuation, aTestRoot.fAntecedents);


           // check on what I think about surgery here

           nodeSatisfiable(aTestRoot);

           TTestDisplayTree aTestDisplayTree= new TTestDisplayTree(aTreeModel);


     //not doing at present AUg04      aTestDisplayTree.display();

                }





    }
*/








}



/*

 function TMyProofWindow.DoDerive (long: boolean): TCommand;

   var
    firstline: TProofline;
    newline: TProofline;
    aLineCommand: TLineCommand;
    derivationFound: boolean;
    tempHead: TList;

   procedure DoProof;

    var
     outcome: argumenttype;
     temptest: TTestnode;
     abandon, equals, compoundterms, higharity: boolean;

    procedure AddFormulas;

     var
      aProofline: TProofline;
      i: INTEGER;

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

                {adds standing antecedents}

     gTestroot.fSucceedent.InsertLast(TProofline(fTail.At(2)).fFormula.CopyFormula);

     if (not abandon) then
      abandon := fDeriverDocument.fJournalWindow.BadCharacters(TProofline(fTail.Last).fFormula, equals, compoundterms, higharity);

    end;

   begin
    BugAlert('This may take some time. To abandon, press the command key and type a stop.');

    equals := FALSE;
    compoundterms := FALSE;
    higharity := FALSE;
    abandon := FALSE;

    StartSatisfactionTree; {initializes gTestroot}
    AddFormulas;

    if abandon then
     begin
      if equals then
       BugAlert('(*Sorry, "Derive It" for = has not yet been implemented.*)')

      else if compoundterms then
       BugAlert('(*Sorry, "Derive It" for compoundterms has not yet been implemented.*)')
      else if higharity then
       BugAlert('(*Sorry, "Derive It" for high-arity predicates has not yet been implemented.*)');

     end

    else

     begin
      outcome := gTestroot.TreeValid(kMaxtreesize);

      if gexCVflag then
       if not (outcome = valid) then
        gexCV := TRUE;
      if gUniCVflag then
       if not (outcome = valid) then
        gUniCV := TRUE;

      if gexCV or gUniCV then
       begin
        temptest := gTestroot.CopyNodeinFull;
        temptest.fclosed := FALSE;
        temptest.fdead := FALSE;
        temptest.flLink := nil;
        temptest.fRlink := nil;

        DismantleTestTree(gTestroot);

        gTestroot := temptest;
        temptest := nil;

        outcome := gTestroot.TreeValid(kMaxtreesize); {check re-init stringstore}

       end;

      case outcome of
       valid:
        begin
        sysBeep(5); { ReDisplayProof;}
        derivationFound := true;

        BugAlert('A derivation has been found... please wait much longer.');

 {DismantleProof; previous one}

 {WriteDerivation;}

        BugAlert('!');

        end;

       notvalid:
        begin
        sysBeep(5);
        BugAlert('Not derivable from these standing assumptions.');
        end;
       notknown:
        begin
        sysBeep(5);
        BugAlert('Unsure whether the sequent can be derived.');
        end;
       otherwise
      end;

     end;


   end;

   procedure CreatProofSegment;

    var
     lastAssumption, dummy: INTEGER;


    procedure SetHeadLevels;

     var
      headLevel: INTEGER;

     procedure SetLevel (item: Tobject);

     begin
      TProofline(item).fHeadLevel := headLevel;
     end;

    begin
     if TProofline(fHead.First).fblankline then
      headLevel := -1 {no antecedents}
     else
      headLevel := 0;
     tempHead.Each(SetLevel);
    end;

   begin
    lastAssumption := 0;

 {$IFC bestpath}
    ReAssembleProof(gTestroot, tempHead, lastAssumption); { }
 {$ENDC}

    Improve(tempHead); {12 March 1991 moved on a few lines mf put back in}

    SetHeadLevels; {sadly, each proofline has to carry info on its head level}

    PrepareSegmentForSplice(fHead, tempHead, lastAssumption);

 {Improve(tempHead); new position}

 (*major changes here because you may prove something that is an assumption*)

 (*RemoveDuplicatesinNew; *)
   end;

   procedure InsertAll;
    var
     theSubprooflevel: integer;
    procedure PutIn (item: TObject);
    begin
     TProofline(item).fSubProofLevel := TProofline(item).fSubProofLevel + theSubprooflevel;
     aLineCommand.fNewlines.InsertLast(TProofline(item));
    end;
   begin
    theSubprooflevel := TProofline(fHead.Last).fSubProofLevel - TProofline(fHead.Last).fHeadLevel;
    tempHead.Each(PutIn);
   end;

   procedure InsertFirstLine;
    var
     theProofline: TProofline;
     i: integer;

   begin
    theProofline := TProofline(tempHead.First);

    theProofline.fSubprooflevel := theProofline.fSubprooflevel + TProofline(fHead.Last).fSubProofLevel - TProofline(fHead.Last).fHeadLevel;

    aLineCommand.fNewlines.InsertLast(theProofline);

    for i := 2 to tempHead.fSize do
     begin
      TProofline(tempHead.At(i)).DismantleProofline;
     end;
 (*tempHead.DeleteAll;*)
   end;

   procedure RecognizePlan;  (*for future development*)
    var
     target: TFormula;
   begin
    target := TProofline(fTail.At(2)).fFormula;
   end;



  begin
   DoDerive := gNoChanges;
   if (TProofline(fTail.First).fSubProofLevel) < TProofline(fHead.Last).fSubProofLevel then
    begin
     sysBeep(5);
     BugAlert('First please drop the extra assumptions.');
    end

   else
    begin
     derivationFound := false;

     DoProof;

     if derivationFound then
      begin
       CreatProofSegment;

       New(aLineCommand);
       FailNil(aLineCommand);

       if long then
        begin
        aLineCommand.ILineCommand(cDeriveIt, SELF);
        InsertAll;
        end
       else
        begin
        aLineCommand.ILineCommand(cNextLine, SELF);
        InsertFirstLine;
        end;

       DoDerive := aLineCommand;
      end;

     DismantleTestTree(gTestroot);
    end;
  end;



*/





}