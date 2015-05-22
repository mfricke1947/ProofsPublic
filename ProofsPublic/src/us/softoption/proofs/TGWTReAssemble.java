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

//scanned through 5/22/2015

/*This is a Feb2013 Google GWT friendly version of 2012 TReAssemble */

/*To understand the algorithms here, you might want to check
 * 
 * Frické, M [2012] 'Best-path theorem proving: compiling derivations', 
 * Chapter in Rationis Defensor: Essays in Honour of Colin Cheyne. Springer, 
 * 2012, ISBN-10: 9400739826, pp. 255- 275.
 * 
 */

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;

import java.util.ArrayList;
import java.util.Iterator;

import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TPreferencesData;
import us.softoption.infrastructure.TUtilities;
import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*This is for reassembling a proof from closed TestNode*/

/* DON'T USE  new TProofline(fParser); in this file use supplyProofline from Factory */


/*look at EIConc break dummy */


class TGWTReAssemble{
  TParser fParser;
  TTestNode fTestNode;
  public ArrayList fHead = new ArrayList();
  int fLastAssIndex = 0;


  String fAssJustification=TProofController.fAssJustification;
  String fAndEJustification=TProofController.fAndEJustification;
  String fAndIJustification=TProofController.fAndIJustification;
  String fOrIJustification=TProofController.fOrIJustification;
  String fImplicEJustification=TProofController.fImplicEJustification;
  String fImplicIJustification=TProofController.fImplicIJustification;

  String fEIJustification=TProofController.fEIJustification;

  String fNegIJustification=TProofController.fNegIJustification;
  String fNegEJustification=TProofController.negEJustification;

  String fEquivEJustification=TProofController.equivEJustification;




  TGWTReAssemble(){    // don't use this

  }

  TGWTReAssemble(TParser aParser, TTestNode aTestNode, ArrayList aHead,
              int aLastAssIndex) {
    fParser = aParser;
    fTestNode = aTestNode;
    fHead = aHead;
    fLastAssIndex = aLastAssIndex;

  }



  /****************
  * for debgging
  *
  */

private  int maxSubProofLevel(ArrayList head){
int upper=0;
int lower=0;
TProofline aProofLine=null;

 if (head.size() > 0) {
   Iterator iter = head.iterator();

   while (iter.hasNext()) {
     aProofLine = (TProofline) iter.next();

     if (aProofLine.fSubprooflevel>upper)
       upper=aProofLine.fSubprooflevel;
     }

     lower=aProofLine.fHeadlevel;   // can be -1 or 0 might be setting it twice
   }



return
   upper-lower; }


 public String proofToString(ArrayList head){  //no html wrapper
String outputStr="";
 TProofline aProofLine;

/*To do a whole proof we have to look through it and find the highest subprooflevel. This
 is because the eventual html table may have different columns in different rows*/


int maxSubProoflevel=maxSubProofLevel(head);



if (head.size() > 0) {
  Iterator iter = head.iterator();

  while (iter.hasNext()) {
    aProofLine = (TProofline) iter.next();

    outputStr=outputStr+aProofLine.toTableRow(maxSubProoflevel);
    }

  }



return
    "<br>"+TProofline.addTableWrapper(outputStr) + "<br>";
}


/****************
* for debgging
*
*/




  /************* Factory ************/

  /* we want this to subclass for other types of proof eg Copi */

  public TProofline supplyProofline(){
     return
         new TProofline(fParser);
  }


public TGWTReAssemble supplyTReAssemble(TTestNode aTestNode, ArrayList aHead,
              int aLastAssIndex){
return
   new TGWTReAssemble(fParser, aTestNode, aHead, aLastAssIndex);
}


  /************* End of Proofline factory ************/

  void prependToHead(TFormula newformula) {  // used to be called addtoHead
    if ( (fHead.size() > 0) && ( (TProofline) fHead.get(0)).fBlankline) {
      TProofListModel.increaseSubProofLevels(fHead, 1);
      fHead.remove(0); //blankstart
    }

    TProofline newline = supplyProofline();
    newline.fLineno = 1000;
    newline.fFormula = newformula.copyFormula();
    newline.fJustification = fAssJustification;

    fHead.add(0, newline);
    fLastAssIndex += 1;

  }


  /*
   procedure AddtoHead (newformula: TFormula; var Head: TList; var lastAssumption: integer);

     var
      newline: TProofline;

    begin
     if (Head.fSize <> 0) then
      if TProofline(Head.First).fBlankline then
       begin
        IncreaseSubProofLevels(Head, +1);
        lastAssumption := 0;  {changed from 1 on  12/11/90}
        newline := TProofline(Head.First); {removing blankstart}
        Head.Delete(Head.First);
        newline.DismantleProofline;
       end;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 1000;
       fFormula := newformula.CopyFormula; {to here}
       fJustification := 'Ass';
      end;

     Head.InsertFirst(newline);
     lastAssumption := lastAssumption + 1;
     newline := nil;

    end;


   */



/****************** AtomicS (Node type 3) *************************************/

  class AtomicS{
    TFormula conclusionFormula;
    boolean shortcut=false;
    boolean absurdity=false;

  void addDummy(){
    TProofline newline;

    if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/){
      newline = supplyProofline();   // Absurd

      newline.fLineno = 3;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fFirstjustno=1;
      newline.fSecondjustno=2;
      newline.fJustification = TProofController.absIJustification;
      newline.fSubprooflevel =0;

      fHead.add(newline);
    }
    else{
      newline = supplyProofline();   // A^~A

      newline.fLineno = 3;
      newline.fFormula = new TFormula(TFormula.binary,
                             String.valueOf(chAnd),
                             ((TProofline)fHead.get(0)).getFormula().copyFormula(),
                             ((TProofline)fHead.get(1)).getFormula().copyFormula());

      newline.fFirstjustno=1;
      newline.fSecondjustno=2;
      newline.fJustification = fAndIJustification;
      newline.fSubprooflevel =0;

      fHead.add(newline);
    }
  }

  /*
     {$IFC useAbsurd}
      procedure AddDummy;

      begin
       SupplyProofline(newline);
       with newline do
        begin
         fLineno := 3;
         fFormula := gAbsurdFormula.CopyFormula;
         fFirstjustno := 1;
         fSecondjustno := 2;
         fJustification := concat(chBlank, 'AbsI');
        end;

       localHead.InsertLast(newline);
       newformulanode := nil;
       newline := nil;
      end;

     {$ELSEC}

      procedure AddDummy;

      begin
       SupplyFormula(newformulanode);
       with newformulanode do
        begin
         fKind := Binary;
         fInfo := chAnd;
         fLlink := TProofline(localHead.At(1)).fFormula.CopyFormula;
         fRlink := TProofline(localHead.At(2)).fFormula.CopyFormula;
        end;

       SupplyProofline(newline);
       with newline do
        begin
         fLineno := 3;
         fFormula := newformulanode;
         fFirstjustno := 1;
         fSecondjustno := 2;
         fJustification := concat(chBlank, chAnd, 'I');
        end;

       localHead.InsertLast(newline);
       newformulanode := nil;
       newline := nil;
      end;
  {$ENDC}

  */

  void doAbsurd(int first, int second){
  TProofline newline=supplyProofline();

   newline.fLineno = 3;
   newline.fSubprooflevel=0;
   newline.fFormula = conclusionFormula.copyFormula();
   newline.fFirstjustno=first;
   newline.fSecondjustno=second;
   newline.fJustification = TProofController.absIJustification;

   fHead.add(newline);

   absurdity=true;
  }

  /*
   procedure DoAbsurd (First, second: integer);

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 3;
       fFormula := newFormula;
       fFirstjustno := First;
       fSecondjustno := second;
       fJustification := concat(chBlank, 'AbsI');
      end;

     localHead.InsertLast(newline);
     newline := nil;
   end;

  */


  void doShortcut(int first, int second){
  TProofline newline=supplyProofline();

   newline.fLineno = 3;
   newline.fSubprooflevel=0;
   newline.fFormula = conclusionFormula.copyFormula();
   newline.fFirstjustno=first;
   newline.fSecondjustno=second;
   newline.fJustification = fAndIJustification;

   fHead.add(newline);

   shortcut=true;
  }
    /*
   procedure DoShortcut (First, second: integer);

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 3;
       fFormula := newFormula;
       fFirstjustno := First;
       fSecondjustno := second;
       fJustification := concat(chBlank, chAnd, 'I');
      end;

     localHead.InsertLast(newline);
     newline := nil;
   end;

  */


  void doAndSpecialCase(){
    TProofline newLine;
    TFormula firstFormula=((TProofline)fHead.get(0)).getFormula();
    TFormula secondFormula=((TProofline)fHead.get(1)).getFormula();

    if (TFormula.formulasContradict(firstFormula,secondFormula)){ //{this might be A, not A therefore A^notA}

       if (TFormula.equalFormulas(firstFormula,conclusionFormula.fLLink)&&
           TFormula.equalFormulas(secondFormula,conclusionFormula.fRLink))
              doShortcut(1,2);
       else if
           (TFormula.equalFormulas(firstFormula,conclusionFormula.fRLink)&&
           TFormula.equalFormulas(secondFormula,conclusionFormula.fLLink))
              doShortcut(2,1);

      // shortcut=true; doShortcut sets this

    }

  }

  /*
   begin
           firstformula := TProofline(localHead.At(1)).fFormula;
           secondformula := TProofline(localHead.At(2)).fFormula;

           if (EqualFormulas(newFormula.fLlink, firstformula) and EqualFormulas(newFormula.fRlink, secondformula)) then
            begin
             DoShortcut(1, 2); {this is A, not A therefore A^notA}
             shortcut := true;
            end;
           if not shortcut then
            if (EqualFormulas(newFormula.fRlink, firstformula) and EqualFormulas(newFormula.fLlink, secondformula)) then
             begin
             DoShortcut(2, 1);
             shortcut := true;
             end;
         end;

  */




  void doAtomicS(){
    /*

    we are trying to get from A,~A to C, usually this will be


     |A
     |~A
     |__
     ||_~C
     ||Absurd
     |~~C
     |C


    however, if there can be some shorter cases  NOT PROGRAMMED YET APRIL 24 2006


     A,~A, C to C is

     |C
     |__
     |C

     A,~A, C to CvD is

     |C
     |__
     |CvD

     A,~A, C, D to C^D is

     |C
     |D
     |__
     |C^D




     */


  TFormula firstFormula;

   if (fHead==null)
      fHead=new ArrayList();

    fHead=new ArrayList();  //should start again Oct 08

    if (TConstants.DEBUG){
      System.out.println(strCR +"Entering doAtomicS <br>");

      System.out.println(proofToString(fHead)+ "<br>");
   }



   twoPremiseStart();  // say A, ~A
   fLastAssIndex=1;

  /*
       TwoPremiseStart(thisnode, localHead);
       lastAssumption := 2;
       shortcut := false;
       absurdity := false;

   if (thisnode.fsucceedent.fSize = 0) then {this puts in a dummy contradiction to be picked}
  {                                                    up by a reductio later}
    AddDummy
   else
    begin

   */


  if(fTestNode.fSuccedent.size()<1){
     addDummy();
  }
  else{
     conclusionFormula=((TFormula)fTestNode.fSuccedent.get(0)).copyFormula();

     if (conclusionFormula.fKind==TFormula.quantifier)
       ;//REMOvE instantiating, I think not needed as we now do not store on formula;
     if (conclusionFormula.fInfo.equals(String.valueOf(chAnd))){
       doAndSpecialCase();

  /*
     newFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;
        if newFormula.fKind = quantifier then
         RemoveInstantiatingInfo(newFormula);
                        { newFormula.fInfo := COPY(newFormula.fInfo, 1, 2); remove instantiating info}

        if newFormula.fInfo = chAnd then
         DoAndSpecialCase

  */
     }

  if (TFormula.equalFormulas(conclusionFormula, TFormula.fAbsurd)){
        doAbsurd(1,2);
       }
       /*
            if EqualFormulas(newFormula, gAbsurdFormula) then
             begin

               DoAbsurd(1, 2); {this is A, not A therefore Absurd}
              absurdity := true;
             end;
       */

  if (!shortcut&&!absurdity){

    doAtomicSNoOptimize(conclusionFormula);

 /*       firstFormula=conclusionFormula;

        conclusionFormula=new TFormula(TFormula.unary,String.valueOf(chNeg),null,firstFormula);

        TProofline newline = supplyProofline();   // not C

   newline.fLineno = 3;
   newline.fFormula = conclusionFormula;
   newline.fJustification = fAssJustification;
   newline.fSubprooflevel =1;
   newline.fLastassumption = true;

   fHead.add(newline);



   if (TPreferencesData.fUseAbsurd){

     newline = supplyProofline(); // not not C

     newline.fLineno = 4;
     newline.fFormula = TFormula.fAbsurd.copyFormula();
     newline.fFirstjustno = 1;
     newline.fSecondjustno = 2;
     newline.fJustification = TProofController.absIJustification;
     newline.fSubprooflevel = 1;

     fHead.add(newline);
   }

    endSubProof();  //to here

    firstFormula=conclusionFormula.copyFormula();

    conclusionFormula=new TFormula(TFormula.unary,String.valueOf(chNeg),null,firstFormula);  //not not C

    newline = supplyProofline();   // not not C

    newline.fLineno = TPreferencesData.fUseAbsurd?5:4;
    newline.fFormula = conclusionFormula;
    newline.fFirstjustno=TPreferencesData.fUseAbsurd?4:1;
    newline.fSecondjustno=TPreferencesData.fUseAbsurd?0:2;
    newline.fJustification = TProofController.fNegIJustification;


    fHead.add(newline);

    newline = supplyProofline();   //  C

    newline.fLineno = TPreferencesData.fUseAbsurd?6:5;
    newline.fFormula = conclusionFormula.fRLink.fRLink.copyFormula();
    newline.fFirstjustno=TPreferencesData.fUseAbsurd?5:4;
    newline.fJustification = TProofController.negEJustification;


    fHead.add(newline);

 */ }

   /*

    {$IFC useAbsurd}

         localHead.InsertLast(newline);

         SupplyProofline(newline); {not notC}
         with newline do
          begin
           fLineno := 4;
           fFormula := gAbsurdFormula.CopyFormula;
           fFirstjustno := 1;
           fSecondjustno := 2;
           fJustification := ' AbsI';
           fSubprooflevel := 1;
          end;

         localHead.InsertLast(newline);

         EndSubProof(localHead);

         firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

         SupplyFormula(newFormula);

         with newFormula do
          begin
           fKind := Unary;
           fInfo := chNeg;
           fRlink := firstformula;
          end;

         SupplyProofline(newline); {not notC}
         with newline do
          begin
           fLineno := 5;
           fFormula := newFormula;
           fFirstjustno := 4;
           fJustification := ' ~I';
          end;

         localHead.InsertLast(newline);

         SupplyProofline(newline); {C}
         with newline do
          begin
           fLineno := 6;
           fFormula := newFormula.fRlink.fRlink.CopyFormula;
           fFirstjustno := 5;
           fJustification := ' ~E';
          end;

         localHead.InsertLast(newline);

    {$ELSEC}

       localHead.InsertLast(newline);

       EndSubProof(localHead);

       firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

       SupplyFormula(newFormula);

       with newFormula do
        begin
         fKind := Unary;
         fInfo := chNeg;
         fRlink := firstformula;
        end;

       SupplyProofline(newline); {not notC}
       with newline do
        begin
         fLineno := 4;
         fFormula := newFormula;
         fFirstjustno := 1;
         fSecondjustno := 2;
         fJustification := ' ~I';
        end;

       localHead.InsertLast(newline);

       SupplyProofline(newline); {C}
       with newline do
        begin
         fLineno := 5;
         fFormula := newFormula.fRlink.fRlink.CopyFormula;
         fFirstjustno := 4;
         fJustification := ' ~E';
        end;

       localHead.InsertLast(newline);
   */


     }

     if (TConstants.DEBUG){
  System.out.println(strCR +"Leaving doAtomicS <br>");

  System.out.println(proofToString(fHead)+ "<br>");
}

  }

   void twoPremiseStart(){
        /*
               {This is the case when two antecedents contradict} {I used to say that they are at the Head of the list,
         but they do not have to be}
           {In which case all that is needed is the two premise start justified by Ass}

        */


    TFormula firstone= new TFormula();
    TFormula secondone= new TFormula();

    if (TFormula.twoInListContradict(fTestNode.fAntecedents,firstone, secondone)){

      TProofline newline=supplyProofline();
    newline.fLineno = 1;
    newline.fFormula = firstone.copyFormula();
    newline.fJustification = fAssJustification;

    fHead.add(newline);

    newline=supplyProofline();
    newline.fLineno = 2;
    newline.fFormula = secondone.copyFormula();
    newline.fJustification = fAssJustification;

    fHead.add(newline);



    }

   }

      /*
        procedure TwoPremiseStart (thisnode: TTestnode; var localHead: TList);

        var
         firstone, secondone: TFormula;
         newline: TProofline;

                 {This is the case when two antecedents contradict}
                 {They are at the Head of the list}
                 {In which case all that is needed is the two premise start justified by Ass}

       begin

        if thisnode.TwoContradict(firstone, secondone) then
                 {This identifies the contradictory formulas}

         begin

          SupplyProofline(newline);
          with newline do
           begin
            fLineno := 1;
            fFormula := firstone.CopyFormula;
            fJustification := 'Ass';
           end;

          localHead.InsertLast(newline);
          newline := nil;

          SupplyProofline(newline);
          with newline do
           begin
            fLineno := 2;
            fFormula := secondone.CopyFormula;
            fJustification := 'Ass';
           end;

          localHead.InsertLast(newline);
          newline := nil;
         end;
       end;



   */




  }    /// end of class AtomicS


/* We pull the next method out of the class so the subclasses can override */

 void doAtomicSNoOptimize(TFormula conclusionFormula){

/*   we are trying to get from A,~A to C, usually this will be


   |A
   |~A
   |__
   ||_~C
   ||Absurd
   |~~C
   |C

*/




TFormula C=conclusionFormula;

TFormula notC=new TFormula(TFormula.unary,
                           String.valueOf(chNeg),
                           null,
                           C);
TFormula notnotC=new TFormula(TFormula.unary,
                           String.valueOf(chNeg),
                           null,
                           notC);


TProofline newline = supplyProofline();   // not C

newline.fLineno = 3;
newline.fFormula = notC.copyFormula();
newline.fJustification = fAssJustification;
newline.fSubprooflevel =1;
newline.fLastassumption = true;

fHead.add(newline);


if (TPreferencesData.fUseAbsurd){

newline = supplyProofline();

newline.fLineno = 4;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fFirstjustno = 1;
newline.fSecondjustno = 2;
newline.fJustification = TProofController.absIJustification;
newline.fSubprooflevel = 1;

fHead.add(newline);
}

endSubProof();


newline = supplyProofline();   // not not C

newline.fLineno = TPreferencesData.fUseAbsurd?5:4;
newline.fFormula = notnotC.copyFormula();
newline.fFirstjustno=TPreferencesData.fUseAbsurd?4:1;
newline.fSecondjustno=TPreferencesData.fUseAbsurd?0:2;
newline.fJustification = TProofController.fNegIJustification;


fHead.add(newline);

newline = supplyProofline();   //  C

newline.fLineno = TPreferencesData.fUseAbsurd?6:5;
newline.fFormula = C.copyFormula();
newline.fFirstjustno=TPreferencesData.fUseAbsurd?5:4;
newline.fJustification = TProofController.negEJustification;


fHead.add(newline);



/*

{$IFC useAbsurd}

    localHead.InsertLast(newline);

    SupplyProofline(newline); {not notC}
    with newline do
     begin
      fLineno := 4;
      fFormula := gAbsurdFormula.CopyFormula;
      fFirstjustno := 1;
      fSecondjustno := 2;
      fJustification := ' AbsI';
      fSubprooflevel := 1;
     end;

    localHead.InsertLast(newline);

    EndSubProof(localHead);

    firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

    SupplyFormula(newFormula);

    with newFormula do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := firstformula;
     end;

    SupplyProofline(newline); {not notC}
    with newline do
     begin
      fLineno := 5;
      fFormula := newFormula;
      fFirstjustno := 4;
      fJustification := ' ~I';
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline); {C}
    with newline do
     begin
      fLineno := 6;
      fFormula := newFormula.fRlink.fRlink.CopyFormula;
      fFirstjustno := 5;
      fJustification := ' ~E';
     end;

    localHead.InsertLast(newline);

{$ELSEC}

  localHead.InsertLast(newline);

  EndSubProof(localHead);

  firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

  SupplyFormula(newFormula);

  with newFormula do
   begin
    fKind := Unary;
    fInfo := chNeg;
    fRlink := firstformula;
   end;

  SupplyProofline(newline); {not notC}
  with newline do
   begin
    fLineno := 4;
    fFormula := newFormula;
    fFirstjustno := 1;
    fSecondjustno := 2;
    fJustification := ' ~I';
   end;

  localHead.InsertLast(newline);

  SupplyProofline(newline); {C}
  with newline do
   begin
    fLineno := 5;
    fFormula := newFormula.fRlink.fRlink.CopyFormula;
    fFirstjustno := 4;
    fJustification := ' ~E';
   end;

  localHead.InsertLast(newline);
*/

  }


  /*
  procedure DoAtomicS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);
  {Two of the antecedents contradict.  If there is no succeedent a dummy is inserted.}
  {This happens if a reductio comes down from higher}

   var
    newline: TProofline;
    newformulanode, newFormula, firstformula, secondformula: TFormula;
    shortcut, absurdity: boolean;

  {$IFC useAbsurd}
   procedure AddDummy;

   begin
    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 3;
      fFormula := gAbsurdFormula.CopyFormula;
      fFirstjustno := 1;
      fSecondjustno := 2;
      fJustification := concat(chBlank, 'AbsI');
     end;

    localHead.InsertLast(newline);
    newformulanode := nil;
    newline := nil;
   end;

  {$ELSEC}

   procedure AddDummy;

   begin
    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Binary;
      fInfo := chAnd;
      fLlink := TProofline(localHead.At(1)).fFormula.CopyFormula;
      fRlink := TProofline(localHead.At(2)).fFormula.CopyFormula;
     end;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 3;
      fFormula := newformulanode;
      fFirstjustno := 1;
      fSecondjustno := 2;
      fJustification := concat(chBlank, chAnd, 'I');
     end;

    localHead.InsertLast(newline);
    newformulanode := nil;
    newline := nil;
   end;
  {$ENDC}

   procedure DoShortcut (First, second: integer);

   begin
    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 3;
      fFormula := newFormula;
      fFirstjustno := First;
      fSecondjustno := second;
      fJustification := concat(chBlank, chAnd, 'I');
     end;

    localHead.InsertLast(newline);
    newline := nil;
   end;

   procedure DoAbsurd (First, second: integer);

   begin
    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 3;
      fFormula := newFormula;
      fFirstjustno := First;
      fSecondjustno := second;
      fJustification := concat(chBlank, 'AbsI');
     end;

    localHead.InsertLast(newline);
    newline := nil;
   end;

  begin       ********************** STARTS HERE*********************************************************
   localHead := nil;
   localHead := newlist;

   TwoPremiseStart(thisnode, localHead);
   lastAssumption := 2;
   shortcut := false;
   absurdity := false;

   if (thisnode.fsucceedent.fSize = 0) then {this puts in a dummy contradiction to be picked}
  {                                                    up by a reductio later}
    AddDummy
   else
    begin
     newFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;
     if newFormula.fKind = quantifier then
      RemoveInstantiatingInfo(newFormula);
                     { newFormula.fInfo := COPY(newFormula.fInfo, 1, 2); remove instantiating info}

     if newFormula.fInfo = chAnd then
      begin
       firstformula := TProofline(localHead.At(1)).fFormula;
       secondformula := TProofline(localHead.At(2)).fFormula;

       if (EqualFormulas(newFormula.fLlink, firstformula) and EqualFormulas(newFormula.fRlink, secondformula)) then
        begin
         DoShortcut(1, 2); {this is A, not A therefore A^notA}
         shortcut := true;
        end;
       if not shortcut then
        if (EqualFormulas(newFormula.fRlink, firstformula) and EqualFormulas(newFormula.fLlink, secondformula)) then
         begin
         DoShortcut(2, 1);
         shortcut := true;
         end;
      end;

     if EqualFormulas(newFormula, gAbsurdFormula) then
      begin
       DoAbsurd(1, 2); {this is A, not A therefore Absurd}
       absurdity := true;
      end;


     if (not shortcut and not absurdity) then
      begin
       firstformula := newFormula; {thisnode.fsucceedent.ffrmla}

       newFormula := nil;

       SupplyFormula(newFormula);

       with newFormula do
        begin
         fKind := Unary;
         fInfo := chNeg;
         fRlink := firstformula;
        end;

       SupplyProofline(newline); {notC}
       with newline do
        begin
         fLineno := 3;
         fFormula := newFormula;
         fJustification := 'Ass';
         fSubprooflevel := 1;
         fLastAssumption := true;
        end;

  {$IFC useAbsurd}

       localHead.InsertLast(newline);

       SupplyProofline(newline); {not notC}
       with newline do
        begin
         fLineno := 4;
         fFormula := gAbsurdFormula.CopyFormula;
         fFirstjustno := 1;
         fSecondjustno := 2;
         fJustification := ' AbsI';
         fSubprooflevel := 1;
        end;

       localHead.InsertLast(newline);

       EndSubProof(localHead);

       firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

       SupplyFormula(newFormula);

       with newFormula do
        begin
         fKind := Unary;
         fInfo := chNeg;
         fRlink := firstformula;
        end;

       SupplyProofline(newline); {not notC}
       with newline do
        begin
         fLineno := 5;
         fFormula := newFormula;
         fFirstjustno := 4;
         fJustification := ' ~I';
        end;

       localHead.InsertLast(newline);

       SupplyProofline(newline); {C}
       with newline do
        begin
         fLineno := 6;
         fFormula := newFormula.fRlink.fRlink.CopyFormula;
         fFirstjustno := 5;
         fJustification := ' ~E';
        end;

       localHead.InsertLast(newline);

  {$ELSEC}

       localHead.InsertLast(newline);

       EndSubProof(localHead);

       firstformula := newFormula.CopyFormula; {thisnode.fsucceedent.ffrmla}

       SupplyFormula(newFormula);

       with newFormula do
        begin
         fKind := Unary;
         fInfo := chNeg;
         fRlink := firstformula;
        end;

       SupplyProofline(newline); {not notC}
       with newline do
        begin
         fLineno := 4;
         fFormula := newFormula;
         fFirstjustno := 1;
         fSecondjustno := 2;
         fJustification := ' ~I';
        end;

       localHead.InsertLast(newline);

       SupplyProofline(newline); {C}
       with newline do
        begin
         fLineno := 5;
         fFormula := newFormula.fRlink.fRlink.CopyFormula;
         fFirstjustno := 4;
         fJustification := ' ~E';
        end;

       localHead.InsertLast(newline);



  {$ENDC}



      end;




    end;
  end;


*/


/****************** End of AtomicS (Node type 3) ******************************/

/****************** Implic (Node type 16) *************************************/

/* A->B branches to ~A and B and from there to proofs either of C or the same or
 different dummies or contradictions. In Hausman we have modus ponens, modus
 tollens or a fancy CD and taut step   */


class Implic {
   TFormula notA;          //not A
   TFormula B;             //B
   TFormula arrowFormula; //A->B

   TProofline firstConc;
   TProofline secondConc;

   int dummy1;
   int dummy2;

   TTestNode proveATest;
   TTestNode proveNotBTest;


  Implic(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss) {

   notA = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(0)); //not A
   B = (TFormula) (fTestNode.getRightChild().fAntecedents.get(0)); //B
   arrowFormula = new TFormula(TFormula.binary,
                               String.valueOf(chImplic),
                               notA.fRLink.copyFormula(),
                               B.copyFormula());  //A->B





   firstConc = (TProofline) leftReAss.fHead.get(leftReAss.fHead.size() - 1);
   secondConc = (TProofline) rightReAss.fHead.get(rightReAss.fHead.size() - 1);

   dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                      leftReAss.fLastAssIndex, notA);
   dummy2 = TGWTMergeData.inPremises(fTestNode, rightReAss.fHead,
                                     rightReAss.fLastAssIndex, B);
  }


  boolean canProveA(){

    /*
       {This is a test of whether we can prove A from the rest of the premises}
         {without (A>B) If we can we use the right branch only ie Modus Ponens}

    }*/

    proveATest=fTestNode.getLeftChild().copyNodeInFullWithInstInfo();
    //{this is node with not-A aantecedent haead}

    proveATest.fStepType=TTestNode.unknown;
    proveATest.fDead=false; // reset for new test
    proveATest.fClosed=false;
   // we are going to put this into a new Tree Model so what used to be the l and r links are null

    TFormula aFormula=((TFormula)(proveATest.fAntecedents.get(0))).fRLink.copyFormula();

    // the first is ~A so this is A

    proveATest.fSuccedent.clear();

    proveATest.fSuccedent.add(aFormula);  //A is now consequent
    proveATest.fAntecedents.remove(0);    // drop ~A from antecedents

    TTreeModel aTreeModel = new TTreeModel(proveATest.fSwingTreeNode);

    return
        (proveATest.treeValid(aTreeModel, TTestNode.kMaxTreeDepth) ==
          TTestNode.valid);

  }


  /*

   procedure AssembleFirstTest; {same as other}
         {This is a test of whether we can prove A from the rest of the premises}
         {without (A>B) If we can we use the right branch only}

     begin
      temptest := nil;
      temptest := thisnode.fLlink.CopyNodeinFullWithInstInfo; {this is node with not-A as}
    {                                                                        antecedent haead}

      with temptest do
       begin
        fSteptype := unknown;
        fDead := false; {must reset it for a new test}
        fClosed := false;
        fLlink := nil;
        frlink := nil;
       end;

      if (temptest.fsucceedent.fSize <> 0) then
       begin
        aFormula := TFormula(temptest.fsucceedent.First);
        temptest.fsucceedent.Delete(temptest.fsucceedent.First);
        aFormula.DismantleFormula; {drops existing}
       end;

      aFormula := leftFormula.frlink.CopyFormula;
      temptest.fsucceedent.InsertFirst(aFormula); {makes A consequent}

      aFormula := TFormula(temptest.fantecedents.First);
      temptest.fantecedents.Delete(temptest.fantecedents.First);
      aFormula.DismantleFormula; {drops not-A}

     end;



*/

  boolean canProveNotB(){

    /*
       {This is a test of whether we can prove notB from the rest of the premises}
         {without (A>B) If we can we use the left branch only ie modus tollens}

    }*/

    proveNotBTest=fTestNode.getRightChild().copyNodeInFullWithInstInfo();
    //{this is node with B aantecedent haead}

    proveNotBTest.fStepType=TTestNode.unknown;
    proveNotBTest.fDead=false; // reset for new test
    proveNotBTest.fClosed=false;
   // we are going to put this into a new Tree Model so what used to be the l and r links are null

    TFormula aFormula=((TFormula)(proveNotBTest.fAntecedents.get(0))).copyFormula();

    aFormula= new TFormula (TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 aFormula
                                );


    // the first is B so this is ~B

    proveNotBTest.fSuccedent.clear();

    proveNotBTest.fSuccedent.add(aFormula);  //~B is now consequent
    proveNotBTest.fAntecedents.remove(0);    // drop B from antecedents

    TTreeModel aTreeModel = new TTreeModel(proveNotBTest.fSwingTreeNode);

    return
        (proveNotBTest.treeValid(aTreeModel, TTestNode.kMaxTreeDepth) ==
          TTestNode.valid); /*probably want less depth*/

  }



  void doImplic(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss) {

    if ( (dummy1 != -1) && (dummy2 != -1)) { //normal case ~A B


      if (canProveA()) { // seeing if there is a proof of A
        optimizeImplicPonens(proveATest, arrowFormula, rightReAss);
      }
      else {
        if (canProveNotB()) { // seeing if there is a proof of notB
          optimizeImplicTollens(proveNotBTest, arrowFormula, leftReAss);
        }
        else {

          noOptimizeImplic(leftReAss, rightReAss,
                        notA, B,arrowFormula,
                        firstConc,secondConc);

      /* separated out into its own method Jan 07



          leftReAss.prependToHead(arrowFormula);
          rightReAss.prependToHead(arrowFormula);

          if (leftReAss.transfer(TTestNode.atomic, notA)) // moves ~A
            ;
          if (rightReAss.transfer(TTestNode.atomic, B)) // moves B
            ;

          leftReAss.numberLines();
          rightReAss.numberLines();

          leftReAss.convertToSubProof();
          rightReAss.convertToSubProof();

          TMergeData mergeData = new TMergeData(leftReAss, rightReAss);

          mergeData.merge();

          fHead = mergeData.firstLocalHead;
          fLastAssIndex = mergeData.firstLastAssIndex;

          createLemma4(fLastAssIndex + 1, TPreferencesData.fUseAbsurd); //this adds ten or eight lines ending in `AvB
          numberLines();

          int firstTailIndex = fHead.indexOf(firstConc);
          int secondTailIndex = fHead.indexOf(secondConc);

          if (TPreferencesData.fUseAbsurd) {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 10);
          }
          else {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 8);
          }

          /*     if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
                 begin
                  AssembleFirstTest;

                              {InitStringStore;}

                  if temptest.TreeValid(kMaxTreesize) = valid then
                   Optimize1
                  else
                   begin
                    DismantleTestTree(temptest);
                    AddCond(localHead, lastAssumption);
                    AddCond(rightHead, rightlastAss);

           if Transfer(atomic, leftFormula, localHead, lastAssumption) then
                     ; {moves notA}
               {                         into body of proof}
                    NumberLines(localHead);
           if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
                     ; {moves B into}
               {                         body of proof}

                    NumberLines(rightHead);

                    ConvertToSubProof(localHead, lastAssumption);
                    ConvertToSubProof(rightHead, rightlastAss);

           Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

               {$IFC useAbsurd}

           CreateLemma4(localHead, lastAssumption);  {changed from +1 12/11/90}

           {This adds 10lines ending in the derived line (notA V B)}

                    firstno := firstno + 10;
                    secondno := secondno + 10;

                    NumberLines(localHead);

                    firstTail := localHead.GetSameItemNo(firstconc);
                    secondTail := localHead.GetSameItemNo(secondconc);

                    AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 10);

               {$ELSEC}

           CreateLemma4(localHead, lastAssumption); {changed from +1 12/11/90}

           {This adds 8lines ending in the derived line (notA V B)}

                    firstno := firstno + 8;
                    secondno := secondno + 8;

                    NumberLines(localHead);

                    firstTail := localHead.GetSameItemNo(firstconc);
                    secondTail := localHead.GetSameItemNo(secondconc);

                    AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 8);


               {$ENDC}

                   end;
                 end  */



        }
      }
    }
   else
      {
        //more to come

        /*     if not InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
         SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss); */


        /*Next case means implic redundant*/

        if (dummy2 == -1) { // I used to have != here but I think that was a mistake June05
          fHead = rightReAss.fHead;
          fLastAssIndex = rightReAss.fLastAssIndex; //{we'll go with the right leg}
        }
        else {
          fHead = leftReAss.fHead;
          fLastAssIndex = leftReAss.fLastAssIndex; //{proof of B from not A}
        }
      }
  }



  void optimizeImplicPonens(TTestNode proveATest,TFormula arrowFormula,TGWTReAssemble rightReAss){  //I think this is done
      /*
       The branches split with ~A on left and B on right, and we can derive A

       */

      TGWTReAssemble proveA = supplyTReAssemble( proveATest, null, 0);

      proveA.reAssembleProof(); //proof of A}

      fHead = proveA.fHead; //transfer it to this, our main proof
      fLastAssIndex = proveA.fLastAssIndex;

      prependToHead(arrowFormula);

      TProofline templateline = (TProofline) (fHead.get(fHead.size() - 1));

      TProofline newline = supplyProofline();
      newline.fLineno = templateline.fLineno + 1;
      newline.fFormula = arrowFormula.fRLink.copyFormula(); // B
      newline.fSubprooflevel = templateline.fSubprooflevel;
      newline.fFirstjustno = templateline.fLineno;
      newline.fSecondjustno = 1000;
      newline.fJustification = fImplicEJustification;

      fHead.add(newline);
      numberLines();

      rightReAss.prependToHead(arrowFormula);

      if (rightReAss.transfer(TTestNode.atomic, arrowFormula.fRLink)){  //B

         TGWTMergeData mergeData = new TGWTMergeData(TGWTReAssemble.this, rightReAss);

         mergeData.merge();

         fHead = mergeData.firstLocalHead;
         fLastAssIndex = mergeData.firstLastAssIndex;

         numberLines();

         int secondBIndex=fHead.indexOf(newline) +1;

         TProofListModel.reNumSingleLine (fHead, secondBIndex, mergeData.firstLineNum);

         fHead.remove(secondBIndex);

         numberLines();

         /*
              secondBindex := localHead.GetSameItemNo(newline) + 1;

                RenumSingleLine(localHead, secondBindex, firstno);

                newline := TProofline(localHead.At(secondBindex)); {omit second B}
                localHead.Delete(newline);
                newline.DismantleProofline;

                NumberLines(localHead);


      }*/


         }
    }


  /*

    procedure Optimize1;  //FROM ARROW

      var
       templateline: TProofline;
       secondBindex: integer;

     begin
      DismantleProofList(localHead);

      localHead := nil;
      lastAssumption := 0;

      ReAssembleProof(temptest, localHead, lastAssumption);

      DismantleTestTree(temptest); {new}

      AddCond(localHead, lastAssumption);

      templateline := TProofline(localHead.Last);

      SupplyProofline(newline); {newline points to new proofline}

      with newline do
       begin
        fLineno := templateline.fLineno + 1;
        fSubprooflevel := templateline.fSubprooflevel; {checkthis}
        fFormula := TProofline(localHead.First).fFormula.frlink.CopyFormula;
        fFirstjustno := templateline.fLineno;
        fSecondjustno := 1000;
        fJustification := ' �E';
       end;

      localHead.InsertLast(newline);

      NumberLines(localHead);

      AddCond(rightHead, rightlastAss);
      if Transfer(atomic, rightFormula, rightHead, rightlastAss) then {B}
       begin
        Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

        secondBindex := localHead.GetSameItemNo(newline) + 1;

        RenumSingleLine(localHead, secondBindex, firstno);

        newline := TProofline(localHead.At(secondBindex)); {omit second B}
        localHead.Delete(newline);
        newline.DismantleProofline;

        NumberLines(localHead);
       end;
     end;

 */


}  //end of DoImplic class


  void optimizeImplicTollens(TTestNode proveNotBTest,TFormula arrowFormula,TGWTReAssemble leftReAss){  //I think this is done
         /*

      We take this out of the Implic Class so that subclasses that really have Modus
Tollens can override it.


                   The branches split with ~A on left and B on right, and we can derive ~B

          */

         TGWTReAssemble proveNotB = supplyTReAssemble( proveNotBTest, null, 0);

         proveNotB.reAssembleProof(); //proof of notB, notB is last line}

         fHead = proveNotB.fHead; //transfer it to this, our main proof
         fLastAssIndex = proveNotB.fLastAssIndex;

         prependToHead(arrowFormula);

         numberLines();

         TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

         TFormula A=arrowFormula.fLLink;
         TFormula B=arrowFormula.fRLink;
         TFormula notA= new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                    null,
                                    A);



         TProofline newline = supplyProofline();
       newline.fLineno = templateLine.fLineno+1;
       newline.fFormula = A.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
      newline.fJustification = fAssJustification;
      newline.fLastassumption = true;

      fHead.add(newline);


      newline = supplyProofline();
      newline.fLineno = templateLine.fLineno+2;
      newline.fFormula = B;
      newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
      newline.fJustification = fImplicEJustification;
      newline.fFirstjustno = templateLine.fLineno+1;
      newline.fSecondjustno=1;

      fHead.add(newline);

  if (TPreferencesData.fUseAbsurd){

    newline = supplyProofline();
    newline.fLineno = templateLine.fLineno + 3;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fJustification = TProofController.absIJustification;
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fFirstjustno = templateLine.fLineno + 2;
    newline.fSecondjustno = templateLine.fLineno;

    fHead.add(newline);
  }

      endSubProof();

      newline = supplyProofline();
        newline.fLineno = TPreferencesData.fUseAbsurd?templateLine.fLineno+4:templateLine.fLineno+4;
        newline.fFormula = notA.copyFormula();  //~A

        newline.fJustification = TProofController.fNegIJustification;
      newline.fSubprooflevel = templateLine.fSubprooflevel;
        newline.fFirstjustno = TPreferencesData.fUseAbsurd?templateLine.fLineno+3:templateLine.fLineno+2;
        newline.fSecondjustno = TPreferencesData.fUseAbsurd?0:templateLine.fLineno;

        fHead.add(newline);

        TProofline notALine=newline;


      //////////////  the first ends in notA the second starts with notA



         leftReAss.prependToHead(arrowFormula);

         if (leftReAss.transfer(TTestNode.atomic, notA)){

            TGWTMergeData mergeData = new TGWTMergeData(TGWTReAssemble.this, leftReAss);

            mergeData.merge();

            fHead = mergeData.firstLocalHead;
            fLastAssIndex = mergeData.firstLastAssIndex;

            numberLines();

            int secondNotAIndex=fHead.indexOf(notALine) +1;

            TProofListModel.reNumSingleLine (fHead, secondNotAIndex, mergeData.firstLineNum);

            fHead.remove(secondNotAIndex);

            numberLines();

            /*
                 secondBindex := localHead.GetSameItemNo(newline) + 1;

                   RenumSingleLine(localHead, secondBindex, firstno);

                   newline := TProofline(localHead.At(secondBindex)); {omit second B}
                   localHead.Delete(newline);
                   newline.DismantleProofline;

                   NumberLines(localHead);


         }*/


            }
      }






  /*

   procedure DoArrow (thisnode: Ttestnode; var localHead: TList; var lastAssumption: integer; var rightHead: TList; var rightlastAss: integer);

    var
     firstno, secondno, firstTail, secondTail, dummy: integer;
     firstconc, secondconc, newline: TProofline;
     temptest: Ttestnode;
     leftFormula, rightFormula, newformulanode, aFormula: TFormula;

    procedure AssembleFirstTest; {same as other}
        {This is a test of whether we can prove A from the rest of the premises}
        {without (A>B) If we can we use the right branch only}

    begin
     temptest := nil;
     temptest := thisnode.fLlink.CopyNodeinFullWithInstInfo; {this is node with not-A as}
   {                                                                        antecedent haead}

     with temptest do
      begin
       fSteptype := unknown;
       fDead := false; {must reset it for a new test}
       fClosed := false;
       fLlink := nil;
       frlink := nil;
      end;

     if (temptest.fsucceedent.fSize <> 0) then
      begin
       aFormula := TFormula(temptest.fsucceedent.First);
       temptest.fsucceedent.Delete(temptest.fsucceedent.First);
       aFormula.DismantleFormula; {drops existing}
      end;

     aFormula := leftFormula.frlink.CopyFormula;
     temptest.fsucceedent.InsertFirst(aFormula); {makes A consequent}

     aFormula := TFormula(temptest.fantecedents.First);
     temptest.fantecedents.Delete(temptest.fantecedents.First);
     aFormula.DismantleFormula; {drops not-A}

    end;

    procedure AddCond (var localHead: TList; var lastAssumption: integer);
        {Adds conditional at beginning}

    begin
     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Binary;
       fInfo := chImplic;
       fLlink := leftFormula.frlink;
       frlink := rightFormula;
      end;

     AddtoHead(newformulanode, localHead, lastAssumption); {implic}

     newformulanode.fRlink := nil;
     newformulanode.fLlink := nil;
     newformulanode.DismantleFormula;   (*add makes a copy*)

     newformulanode := nil;

    end;

    procedure Optimize1;

     var
      templateline: TProofline;
      secondBindex: integer;

    begin
     DismantleProofList(localHead);

     localHead := nil;
     lastAssumption := 0;

     ReAssembleProof(temptest, localHead, lastAssumption);

     DismantleTestTree(temptest); {new}

     AddCond(localHead, lastAssumption);

     templateline := TProofline(localHead.Last);

     SupplyProofline(newline); {newline points to new proofline}

     with newline do
      begin
       fLineno := templateline.fLineno + 1;
       fSubprooflevel := templateline.fSubprooflevel; {checkthis}
       fFormula := TProofline(localHead.First).fFormula.frlink.CopyFormula;
       fFirstjustno := templateline.fLineno;
       fSecondjustno := 1000;
       fJustification := ' �E';
      end;

     localHead.InsertLast(newline);

     NumberLines(localHead);

     AddCond(rightHead, rightlastAss);
     if Transfer(atomic, rightFormula, rightHead, rightlastAss) then {B}
      begin
       Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

       secondBindex := localHead.GetSameItemNo(newline) + 1;

       RenumSingleLine(localHead, secondBindex, firstno);

       newline := TProofline(localHead.At(secondBindex)); {omit second B}
       localHead.Delete(newline);
       newline.DismantleProofline;

       NumberLines(localHead);
      end;
    end;

   begin {arrow}

    ReAssembleProof(thisnode.fLlink, localHead, lastAssumption);
    ReAssembleProof(thisnode.frlink, rightHead, rightlastAss);

    leftFormula := TFormula(thisnode.fLlink.fantecedents.First); {notA}
    rightFormula := TFormula(thisnode.frlink.fantecedents.First); {B}

    firstconc := TProofline(localHead.Last);
    secondconc := TProofline(rightHead.Last); {helps with the orconc merge later}

    if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then

     begin
      AssembleFirstTest;

                  {InitStringStore;}

      if temptest.TreeValid(kMaxTreesize) = valid then
       Optimize1
      else
       begin
        DismantleTestTree(temptest);
        AddCond(localHead, lastAssumption);
        AddCond(rightHead, rightlastAss);

        if Transfer(atomic, leftFormula, localHead, lastAssumption) then
         ; {moves notA}
   {                         into body of proof}
        NumberLines(localHead);
        if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
         ; {moves B into}
   {                         body of proof}

        NumberLines(rightHead);

        ConvertToSubProof(localHead, lastAssumption);
        ConvertToSubProof(rightHead, rightlastAss);

        Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

   {$IFC useAbsurd}

        CreateLemma4(localHead, lastAssumption);  {changed from +1 12/11/90}

                       {This adds 10lines ending in the derived line (notA V B)}

        firstno := firstno + 10;
        secondno := secondno + 10;

        NumberLines(localHead);

        firstTail := localHead.GetSameItemNo(firstconc);
        secondTail := localHead.GetSameItemNo(secondconc);

        AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 10);

   {$ELSEC}

        CreateLemma4(localHead, lastAssumption); {changed from +1 12/11/90}

                       {This adds 8lines ending in the derived line (notA V B)}

        firstno := firstno + 8;
        secondno := secondno + 8;

        NumberLines(localHead);

        firstTail := localHead.GetSameItemNo(firstconc);
        secondTail := localHead.GetSameItemNo(secondconc);

        AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 8);


   {$ENDC}

       end;
     end
    else
     begin

      if not InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
       SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss);
      DismantleProofList(rightHead); {step redundant}
     end;
   end; {arrow}


*/


  void noOptimizeImplic(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss,
                        TFormula notA, TFormula B,TFormula arrowFormula,
                        TProofline firstConc,TProofline secondConc){

          leftReAss.prependToHead(arrowFormula);
          rightReAss.prependToHead(arrowFormula);

          if (leftReAss.transfer(TTestNode.atomic, notA)) // moves ~A
            ;
          if (rightReAss.transfer(TTestNode.atomic, B)) // moves B
            ;

          leftReAss.numberLines();
          rightReAss.numberLines();

          leftReAss.convertToSubProof();
          rightReAss.convertToSubProof();

          TGWTMergeData mergeData = new TGWTMergeData(leftReAss, rightReAss);

          mergeData.merge();

          fHead = mergeData.firstLocalHead;
          fLastAssIndex = mergeData.firstLastAssIndex;

          createLemma4(fLastAssIndex + 1, TPreferencesData.fUseAbsurd); //this adds ten or eight lines ending in `AvB
          numberLines();

          int firstTailIndex = fHead.indexOf(firstConc);
          int secondTailIndex = fHead.indexOf(secondConc);

          if (TPreferencesData.fUseAbsurd) {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 10);
          }
          else {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 8);
          }

          /*     if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
                 begin
                  AssembleFirstTest;

                              {InitStringStore;}

                  if temptest.TreeValid(kMaxTreesize) = valid then
                   Optimize1
                  else
                   begin
                    DismantleTestTree(temptest);
                    AddCond(localHead, lastAssumption);
                    AddCond(rightHead, rightlastAss);

           if Transfer(atomic, leftFormula, localHead, lastAssumption) then
                     ; {moves notA}
               {                         into body of proof}
                    NumberLines(localHead);
           if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
                     ; {moves B into}
               {                         body of proof}

                    NumberLines(rightHead);

                    ConvertToSubProof(localHead, lastAssumption);
                    ConvertToSubProof(rightHead, rightlastAss);

           Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

               {$IFC useAbsurd}

           CreateLemma4(localHead, lastAssumption);  {changed from +1 12/11/90}

           {This adds 10lines ending in the derived line (notA V B)}

                    firstno := firstno + 10;
                    secondno := secondno + 10;

                    NumberLines(localHead);

                    firstTail := localHead.GetSameItemNo(firstconc);
                    secondTail := localHead.GetSameItemNo(secondconc);

                    AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 10);

               {$ELSEC}

           CreateLemma4(localHead, lastAssumption); {changed from +1 12/11/90}

           {This adds 8lines ending in the derived line (notA V B)}

                    firstno := firstno + 8;
                    secondno := secondno + 8;

                    NumberLines(localHead);

                    firstTail := localHead.GetSameItemNo(firstconc);
                    secondTail := localHead.GetSameItemNo(secondconc);

                    AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 8);


               {$ENDC}

                   end;
                 end
           */


        }



        /********************* End of Implic *******************************************/


/****************** Equivv (Node type 20) *************************************/

        void doEquivv(TGWTReAssemble leftReAss) {
           fHead = leftReAss.fHead;
           fLastAssIndex = leftReAss.fLastAssIndex;

           TFormula AarrowB = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
           TFormula BarrowA = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

           int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                              leftReAss.fLastAssIndex, AarrowB); //not sure
           int dummy2 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                              leftReAss.fLastAssIndex, BarrowA);

           if ( (dummy1 != -1) || (dummy2 != -1)) {
             TFormula formulanode = new TFormula();

             formulanode.fKind = TFormula.binary;
             formulanode.fInfo = String.valueOf(chEquiv);
             formulanode.fLLink = AarrowB.fLLink;
             formulanode.fRLink = AarrowB.fRLink;

             prependToHead(formulanode);

             if (transfer(fTestNode.fStepType, AarrowB)) // {Moves the left imlic it justifies into body of proof}
               ;

             if (transfer(fTestNode.fStepType, BarrowA))
               ;

             numberLines();

           }

         }

         /*
          procedure DoEquivv (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

           var
            newformulanode, AarrowB, BarrowA: TFormula;
            dummy: integer;

          begin
           AarrowB := TFormula(thisnode.fLlink.fantecedents.First);
           BarrowA := TFormula(thisnode.fLlink.fantecedents.At(2));

           if InPremises(thisnode, localHead, lastAssumption, AarrowB, dummy) or InPremises(thisnode, localHead, lastAssumption, BarrowA, dummy) then
            begin

             SupplyFormula(newformulanode);
             with newformulanode do
              begin
               fKind := Binary;
               fInfo := chEquiv;
               fLlink := AarrowB.fLlink;
               fRlink := AarrowB.fRlink;
              end;
             AddtoHead(newformulanode, localHead, lastAssumption); {equivv}
             newformulanode.fRlink := nil;
             newformulanode.fLlink := nil;
             newformulanode.DismantleFormula;   (*add makes a copy*)
             newformulanode := nil;

             if Transfer(thisnode.fSteptype, AarrowB, localHead, lastAssumption) then
              ; {Moves the}
          {                    left conjunct it justifies into body of proof}

             if Transfer(thisnode.fSteptype, BarrowA, localHead, lastAssumption) then
              ; {Moves the}
          {                    right conjunct it justifies into body of proof}

             NumberLines(localHead)

            end;
          end;


   */




 /****************** End of Equivv (Node type 20) *************************************/



  /********************* Utility test **********************/


 TTestNode proofFromRest(TTestNode child,TFormula target){

 TTestNode proofNode=null;

  /*
When we branch, for example with 'implic' or 'or', we are often interested whether
we can prove a particular formula. For example, with A->B which branches to ~A and B, if
we can prove A we can use ImplicE and go down the right branch. When we branch, the formula
we don't use in the proof eg ~A or B is always first, so we will drop that one.

*/
  proofNode=child.copyNodeInFullWithInstInfo();
  proofNode.fStepType=TTestNode.unknown;
  proofNode.fDead=false; // reset for new test
  proofNode.fClosed=false;

  proofNode.fSuccedent.clear();
  proofNode.fSuccedent.add(target.copyFormula());
  proofNode.fAntecedents.remove(0);    // drop ~A, or B or whatever from antecedents

  TTreeModel aTreeModel = new TTreeModel(proofNode.fSwingTreeNode);

  if (proofNode.treeValid(aTreeModel, TTestNode.kMaxTreeDepth) ==TTestNode.valid)
    return
        proofNode;
  else
    return
        null;
}





  /*********************************************************/

  void doAand(TGWTReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TFormula leftFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
    TFormula rightFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

    int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, leftFormula); //not sure
    int dummy2 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, rightFormula);

    if ( (dummy1 != -1) || (dummy2 != -1)) {
      TFormula formulanode = new TFormula();

      formulanode.fKind = TFormula.binary;
      formulanode.fInfo = String.valueOf(chAnd);
      formulanode.fLLink = leftFormula;
      formulanode.fRLink = rightFormula;

      prependToHead(formulanode);

      if (transfer(fTestNode.fStepType, leftFormula)) // {Moves the left conjunct it justifies into body of proof}
        ;

      if (transfer(fTestNode.fStepType, rightFormula))
        ;

      numberLines();

    }

  }

  /*
     procedure DoAand (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

      var
       leftFormula, rightFormula, newformulanode: TFormula;
       dummy: integer;

     begin
      leftFormula := TFormula(thisnode.fLlink.fantecedents.First);
      rightFormula := TFormula(thisnode.fLlink.fantecedents.At(2));

      if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) or InPremises(thisnode, localHead, lastAssumption, rightFormula, dummy) then (*or*)


       begin
        SupplyFormula(newformulanode);
        with newformulanode do
         begin
          fKind := Binary;
          fInfo := chAnd;
          fLlink := leftFormula;
          fRlink := rightFormula;
         end;

   AddtoHead(newformulanode, localHead, lastAssumption); {and, makes a copy}

        newformulanode.fLlink := nil;
        newformulanode.fRlink := nil;
        newformulanode.DismantleFormula;

   if Transfer(thisnode.fSteptype, leftFormula, localHead, lastAssumption) then
         ;
                    {Moves the left conjunct it justifies into body of proof}

   if Transfer(thisnode.fSteptype, rightFormula, localHead, lastAssumption) then
         ;
                    {Moves the right conjunct it justifies into body of proof}

        NumberLines(localHead);

       end;
     end;


   */

  void doDoubleNeg(TGWTReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TFormula theFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));

    int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, theFormula); //not sure

    if (dummy1 != -1) {
      TFormula formula = new TFormula();

      formula.fKind = TFormula.unary;
      formula.fInfo = String.valueOf(chNeg);
      formula.fRLink = theFormula;

      TFormula formulanode = new TFormula();

      formulanode.fKind = TFormula.unary;
      formulanode.fInfo = String.valueOf(chNeg);
      formulanode.fRLink = formula;

      prependToHead(formulanode);

      if (transfer(fTestNode.fStepType, theFormula)) // {Moves the formula it justifies into body of proof}
        numberLines();
    }

  }

  /*
   procedure DoDoubleNeg (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

    var
     dummy: integer;
     theFormula: TFormula;

    procedure AddDoubleNeg;

     var
      temp, newformulanode: TFormula;

    begin
     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Unary;
       fInfo := chNeg;
       fRlink := theFormula;
      end;
     SupplyFormula(temp);
     with temp do
      begin
       fKind := Unary;
       fInfo := chNeg;
       fRlink := newformulanode;
      end;
     AddtoHead(temp, localHead, lastAssumption); {doublenegation, makes a copy}

     newformulanode.fRlink := nil;
     temp.fRlink := nil;
     newformulanode.DismantleFormula;
     temp.DismantleFormula;

    end;

   begin
   theFormula := TFormula(thisnode.fLlink.fantecedents.First);

   if InPremises(thisnode, localHead, lastAssumption, theFormula, dummy) then
    begin
     AddDoubleNeg;
                 { lastAssumption :=  lastAssumption+1; addtohead does this}
   if Transfer(thisnode.fSteptype, theFormula, localHead, lastAssumption) then
      NumberLines(localHead);
    end;
   end;



   */


  void doDoubleNegS(TGWTReAssemble leftReAss) {

    // from |-A to |-~~A

    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TProofline ALine = (TProofline) (fHead.get(fHead.size() - 1));
    TFormula A=ALine.fFormula;
   TFormula notA= new TFormula(TFormula.unary,
                           String.valueOf(chNeg),
                           null,
                           A);
TFormula notNotA= new TFormula(TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 notA);



TProofline newline = supplyProofline();                       //~A
newline.fLineno = ALine.fLineno+1;
newline.fFormula = notA.copyFormula();
newline.fSubprooflevel = ALine.fSubprooflevel + 1;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(newline);


    if (TPreferencesData.fUseAbsurd){

      newline = supplyProofline();
      newline.fLineno = ALine.fLineno + 2;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fJustification = TProofController.absIJustification;
      newline.fSubprooflevel = ALine.fSubprooflevel + 1;
      newline.fFirstjustno = ALine.fLineno;
      newline.fSecondjustno = ALine.fLineno + 1;

      fHead.add(newline);
    }

       endSubProof();

       newline = supplyProofline();
         newline.fLineno = TPreferencesData.fUseAbsurd?ALine.fLineno+3:ALine.fLineno+2;
         newline.fFormula = notNotA.copyFormula();  //~~A

         newline.fJustification = TProofController.fNegIJustification;
       newline.fSubprooflevel = ALine.fSubprooflevel;
         newline.fFirstjustno = TPreferencesData.fUseAbsurd?ALine.fLineno+2:ALine.fLineno;
         newline.fSecondjustno =TPreferencesData.fUseAbsurd?0:ALine.fLineno+1;

         fHead.add(newline);

    }




  /*

     procedure DoDoubleNegS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

    var
     newline, formerlastline: TProofline;
     newformulanode, secondnewformulanode: TFormula;


   {$IFC useAbsurd}

    procedure AddAssumption;

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := formerlastline.fLineno + 1;
       fFormula := newformulanode;
       fSubprooflevel := formerlastline.fSubprooflevel + 1;
       fJustification := 'Ass';
       fLastAssumption := true;
      end;

     localHead.InsertLast(newline);

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := formerlastline.fLineno + 2;
       fFormula := gAbsurdFormula.CopyFormula;
       fSubprooflevel := formerlastline.fSubprooflevel + 1;
       fJustification := ' AbsI';
       fFirstjustno := formerlastline.fLineno;
       fSecondjustno := formerlastline.fLineno + 1;
      end;

     localHead.InsertLast(newline);
    end;

    procedure AddNegAssumption;

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := formerlastline.fLineno + 3;
       fFormula := secondnewformulanode;
       fSubprooflevel := formerlastline.fSubprooflevel;
       fFirstjustno := formerlastline.fLineno + 2;
       fJustification := ' ~I';
      end;

     localHead.InsertLast(newline);
    end;

   {$ELSEC}

    procedure AddAssumption;

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := formerlastline.fLineno + 1;
       fFormula := newformulanode;
       fSubprooflevel := formerlastline.fSubprooflevel + 1;
       fJustification := 'Ass';
       fLastAssumption := true;
      end;

     localHead.InsertLast(newline);

    end;

    procedure AddNegAssumption;

    begin
     SupplyProofline(newline);
     with newline do
      begin
       fLineno := formerlastline.fLineno + 2;
       fFormula := secondnewformulanode;
       fSubprooflevel := formerlastline.fSubprooflevel;
       fFirstjustno := formerlastline.fLineno;
       fSecondjustno := formerlastline.fLineno + 1;
       fJustification := ' ~I';
      end;

     localHead.InsertLast(newline);
    end;

   {$ENDC}

   begin
    formerlastline := TProofline(localHead.Last);

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := formerlastline.fFormula.CopyFormula;
     end;

    AddAssumption;

    SupplyFormula(secondnewformulanode);
    with secondnewformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := newformulanode.CopyFormula;
     end;

    EndSubProof(localHead);
    AddNegAssumption;
   end;


  */




  void doEquivvS(TGWTReAssemble leftReAss,TGWTReAssemble rightReAss) {
    TProofline firstcon=(TProofline)leftReAss.fHead.get(leftReAss.fHead.size()-1);
    TProofline secondcon=(TProofline)rightReAss.fHead.get(rightReAss.fHead.size()-1);

    TFormula AequivB = (TFormula) (fTestNode.fSuccedent.get(0));
    TFormula A=AequivB.fLLink;
    TFormula B=AequivB.fRLink;

    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    if (!transfer(TTestNode.atomic, A))
     createAssLine(A, fLastAssIndex+1);

    numberLines();

    convertToSubProof();

    if (!rightReAss.transfer(TTestNode.atomic, B))
     rightReAss.createAssLine(B, rightReAss.fLastAssIndex+1);

    rightReAss.numberLines();

    rightReAss.convertToSubProof();

    TGWTMergeData mergeData = new TGWTMergeData(this, rightReAss);

       mergeData.merge();

       fHead = mergeData.firstLocalHead;
       fLastAssIndex = mergeData.firstLastAssIndex;

       numberLines();

       TProofline templateLine=(TProofline)leftReAss.fHead.get(leftReAss.fHead.size()-1);

       TProofline newline=supplyProofline();
    newline.fLineno = templateLine.fLineno+1;
    newline.fSubprooflevel= templateLine.fSubprooflevel;
    newline.fFormula = AequivB.copyFormula();
    newline.fJustification = TProofController.equivIJustification;
    newline.fFirstjustno=firstcon.fLineno;
    newline.fSecondjustno=secondcon.fLineno;

    fHead.add(newline);



  }


  /*
     procedure DoEquivvS (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer; var rightHead: Tlist; var rightlastAss: integer);

    var
     firstcon, secondcon, newline, templateline: TProofline;
     AequivB: TFormula;
     dummy: integer;

   begin
    firstcon := TProofline(localHead.Last);
    secondcon := TProofline(rightHead.Last);

    AequivB := TFormula(thisnode.fsucceedent.First);

    if not Transfer(atomic, AequivB.fLlink, localHead, lastAssumption) then
     CreateAssLine(AequivB.fLlink, localHead, lastAssumption);

    NumberLines(localHead);

    if not Transfer(atomic, AequivB.fRlink, rightHead, rightlastAss) then
     CreateAssLine(AequivB.fRlink, rightHead, rightlastAss);

    ConvertToSubProof(localHead, lastAssumption);

    NumberLines(rightHead);

    ConvertToSubProof(rightHead, rightlastAss);

    Merge(localHead, lastAssumption, rightHead, rightlastAss, dummy, dummy);

    NumberLines(localHead);

    templateline := TProofline(localHead.Last);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := templateline.fLineno + 1;
      fSubprooflevel := templateline.fSubprooflevel;
      fFormula := AequivB.CopyFormula;
      fJustification := ' �I';
      fFirstjustno := firstcon.fLineno;
      fSecondjustno := secondcon.fLineno;
     end;

    localHead.InsertLast(newline);
   end;


  */

  void convertToSubProof() {
    int i = fLastAssIndex + 1;

    ( (TProofline) (fHead.get(i))).fLastassumption = true; // off by one error on index?

    while (i < fHead.size()) {
      ( (TProofline) (fHead.get(i))).fSubprooflevel += 1;
      i++;
    }
    endSubProof();
  }

  /*
   procedure ConvertToSubProof (var localHead: TList; var lastAssumption: integer);
   {makes the entire of a proof into a subproof with first line as ass}

    var
     i: integer;

   begin
    i := lastAssumption + 1;
    TProofline(localHead.At(i)).fLastAssumption := true;

    while i <= localHead.fSize do
     begin
      TProofline(localHead.At(i)).fSubprooflevel := TProofline(localHead.At(i)).fSubprooflevel + 1;
      i := i + 1;
     end;

    EndSubProof(localHead);
   end;


   */

  void endSubProof() {
    TProofline newline = supplyProofline();
    TProofline lastline = (TProofline) (fHead.get(fHead.size() - 1));

    newline.fLineno = lastline.fLineno;
    newline.fSubprooflevel = lastline.fSubprooflevel - 1;
    newline.fBlankline = true;
    newline.fJustification = "";
    newline.fSelectable = false;

    fHead.add(newline);
  }

  /*
   procedure EndSubProof (var localHead: TList);
   {does this at end of proof}

     var
      newline, lastline: TProofline;

    begin
     lastline := TProofline(localHead.Last);

     SupplyProofline(newline); {newline points to new proofline}

     with newline do
      begin
       fLineno := lastline.fLineno;
       fSubprooflevel := lastline.fSubprooflevel - 1; {checkthis}
       fBlankline := true;
       fJustification := '';
       fSelectable := false;
      end;

     localHead.InsertLast(newline);
     newline := nil;
    end;


   */

  /******************* Steps ******************************/

/*
  void createLemma1(int insertIndex){
    if (TPreferencesData.fUseAbsurd)
      createLemma1WithAbsurd(insertIndex);
  }

*/

  void createLemma1(int insertIndex){

    /*{this creates a proof of  ~B from ~(A ^ B) as new line 1000 and
   an existing proof of A as last line}

    */
    TProofline newline,templateLine,firstLine;

    TFormula  formulaB,formulaAandB, notAandB;

    firstLine=(TProofline)fHead.get(0);               // the ~(A^B)
    templateLine=(TProofline)fHead.get(fHead.size()-1);

    notAandB=firstLine.fFormula;
    formulaAandB=notAandB.fRLink;
   // formulaA=formulaAandB.fLLink;
    formulaB=formulaAandB.fRLink;

   newline = supplyProofline();  //assume B
   newline.fLineno = 2001;
   newline.fFormula = formulaB.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();  // get A^B
  newline.fLineno = 2002;
  newline.fFormula = formulaAandB.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
  newline.fJustification = fAndIJustification;
  newline.fFirstjustno = templateLine.fLineno;
  newline.fSecondjustno=2001;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  if (TPreferencesData.fUseAbsurd){  //add Absurd, if needed

    newline = supplyProofline();
    newline.fLineno = 2003;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fJustification = TProofController.absIJustification;
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fFirstjustno = firstLine.fLineno;
    newline.fSecondjustno = 2002;

    fHead.add(insertIndex, newline);
    insertIndex += 1;
  }

  endSubProof();
  insertIndex += 1; // BUG discovered Dec 06 endSubProof does not advance index by itself


  newline = supplyProofline();
    newline.fLineno = TPreferencesData.fUseAbsurd?2004:2003;
    newline.fFormula = new TFormula (TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 formulaB.copyFormula()
                                );  //~B

    newline.fJustification = TProofController.fNegIJustification;
  newline.fSubprooflevel = templateLine.fSubprooflevel;
    newline.fFirstjustno = TPreferencesData.fUseAbsurd?2003:firstLine.fLineno;  //~(A^B)
    newline.fSecondjustno = TPreferencesData.fUseAbsurd?0:2002;    //A^B

    fHead.add(insertIndex, newline);
  insertIndex += 1;

}


  /*
   {$IFC useAbsurd}

   procedure CreateLemma1 (var localHead: Tlist);

   {this creates a proof of  ~B from ~(A ^ B) as new line 1000 and an existing proof of A as last line}

    var
     newline, templateline: TProofline;
     newformulanode: TFormula;

   begin
    templateline := TProofline(localHead.Last); {A}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2001;
      fFormula := TProofline(localHead.First).fFormula.fRlink.fRlink.CopyFormula; {B}
      fJustification := 'Ass';
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fLastAssumption := true;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2002;
      fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula; {A^B}
      fJustification := ' ^I';
      fFirstjustno := templateline.fLineno;
      fSecondjustno := 2001;
      fSubprooflevel := templateline.fSubprooflevel + 1;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2003;
      fFormula := gAbsurdFormula.CopyFormula;
      fJustification := ' AbsI';
      fFirstjustno := TProofline(localHead.First).fLineno;
      fSecondjustno := 2002;
      fSubprooflevel := templateline.fSubprooflevel + 1;
     end;

    localHead.InsertLast(newline);

    EndSubProof(localHead);

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := TProofline(localHead.First).fFormula.fRlink.fRlink.CopyFormula; {B}
     end;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2004;
      fFormula := newformulanode;
      fJustification := ' ~I';
      fFirstjustno := 2003;
      fSubprooflevel := templateline.fSubprooflevel;
     end;

    localHead.InsertLast(newline);
end;

   */

/*  void createLemma2(int insertIndex){
   if (TPreferencesData.fUseAbsurd)
     createLemma2WithAbsurd(insertIndex);
 }
*/


 void createLemma2(int insertIndex){

     /*   {this creates a proof of  ~A from ~(A ^ B) as new line 1000 and
      an existing proof of B as last line}


   */
   TProofline newline,templateLine,firstLine;

   TFormula  formulaA, formulaB,formulaAandB, notAandB;

   firstLine=(TProofline)fHead.get(0);               // the ~(A^B)
   templateLine=(TProofline)fHead.get(fHead.size()-1);

   notAandB=firstLine.fFormula;
   formulaAandB=notAandB.fRLink;
   formulaA=formulaAandB.fLLink;
   formulaB=formulaAandB.fRLink;

  newline = supplyProofline();
  newline.fLineno = 2001;
  newline.fFormula = formulaA.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
 newline.fJustification = fAssJustification;
 newline.fLastassumption = true;

 fHead.add(insertIndex, newline);
 insertIndex += 1;

 newline = supplyProofline();
 newline.fLineno = 2002;
 newline.fFormula = formulaAandB.copyFormula();
 newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
 newline.fJustification = fAndIJustification;
 newline.fFirstjustno = templateLine.fLineno;
 newline.fSecondjustno=2001;

 fHead.add(insertIndex, newline);
 insertIndex += 1;

 if (TPreferencesData.fUseAbsurd){  //add Absurd, if needed
   newline = supplyProofline();
   newline.fLineno = 2003;
   newline.fFormula = TFormula.fAbsurd.copyFormula();
   newline.fJustification = TProofController.absIJustification;
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fFirstjustno = firstLine.fLineno;
   newline.fSecondjustno = 2002;

   fHead.add(insertIndex, newline);
   insertIndex += 1;
 }

 endSubProof();
 insertIndex += 1; // BUG discovered Dec 06 endSubProof does not advance index by itself

 newline = supplyProofline();
   newline.fLineno = TPreferencesData.fUseAbsurd?2004:2003;
   newline.fFormula = new TFormula (TFormula.unary,
                                String.valueOf(chNeg),
                                null,
                                formulaA.copyFormula()
                               );  //~A

   newline.fJustification = TProofController.fNegIJustification;
 newline.fSubprooflevel = templateLine.fSubprooflevel;
   newline.fFirstjustno = TPreferencesData.fUseAbsurd?2003:2002;
    newline.fSecondjustno = TPreferencesData.fUseAbsurd?0:firstLine.fLineno;  //the ~(A^B)

   fHead.add(insertIndex, newline);
 insertIndex += 1;

}

  /*
   procedure CreateLemma2 (var localHead: Tlist);

   {this creates a proof of  ~A from ~(A ^ B) as new line 1000 and an existing proof of B as last line}

    var
     newline, templateline: TProofline;
     newformulanode: TFormula;

   begin
    templateline := TProofline(localHead.Last); {B}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2001;
      fFormula := TProofline(localHead.First).fFormula.fRlink.fLlink.CopyFormula; {A}
      fJustification := 'Ass';
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fLastAssumption := true;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2002;
      fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula; {A^B}
      fJustification := ' ^I';
      fFirstjustno := templateline.fLineno;
      fSecondjustno := 2001;
      fSubprooflevel := templateline.fSubprooflevel + 1;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2003;
      fFormula := gAbsurdFormula.CopyFormula;
      fJustification := ' AbsI';
      fFirstjustno := TProofline(localHead.First).fLineno;
      fSecondjustno := 2002;
      fSubprooflevel := templateline.fSubprooflevel + 1;
     end;

    localHead.InsertLast(newline);

    EndSubProof(localHead);

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := TProofline(localHead.First).fFormula.fRlink.fLlink.CopyFormula; {A}
     end;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2004;
      fFormula := newformulanode;
      fJustification := ' ~I';
      fFirstjustno := 2003;
      fSubprooflevel := templateline.fSubprooflevel;
     end;

    localHead.InsertLast(newline);
   end;

   {$ELSEC}

   procedure CreateLemma2 (var localHead: Tlist);

   {this creates a proof of  ~A from ~(A ^ B) as new line 1000 and an existing proof of B as last line}

    var
     newline, templateline: TProofline;
     newformulanode: TFormula;

   begin
    templateline := TProofline(localHead.Last); {B}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2001;
      fFormula := TProofline(localHead.First).fFormula.fRlink.fLlink.CopyFormula; {A}
      fJustification := 'Ass';
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fLastAssumption := true;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2002;
      fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula; {A^B}
      fJustification := ' ^I';
      fFirstjustno := templateline.fLineno;
      fSecondjustno := 2001;
      fSubprooflevel := templateline.fSubprooflevel + 1;
     end;

    localHead.InsertLast(newline);

    EndSubProof(localHead);

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := TProofline(localHead.First).fFormula.fRlink.fLlink.CopyFormula; {A}
     end;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2003;
      fFormula := newformulanode;
      fJustification := ' ~I';
      fFirstjustno := TProofline(localHead.First).fLineno;
      fSecondjustno := 2002;
      fSubprooflevel := templateline.fSubprooflevel;
     end;

    localHead.InsertLast(newline);
   end;


{$ENDC}

*/



  void createLemma1A(TFormula orFormula) {      // this can be overridden eg by Copi
    /*
       {This is quite complicated.  We have a proof of B from premises including ~A, and we}
     {are trying to convert this into a proof of AVB.  We have pushed ~A as first genuine line}
     {of proof}

     |Premises
     |~A
     |Perhaps more premises
     |_______
     |~A
     |<other lines>
     |B

     and to start with we are going to do the insertions before the 'pushed'~A pushing everything else down

     |Premises
     |~A
     |_______
     |<we insert in here>
     |~A
     |<other lines>
     |B

     and what we are heading for is

     |Premises
     |~A
     |__
     ||_~(AvB)
     |||_A
     ||| A v B
     |||Absurd

     |~A
     |<other lines>
     |B

     at this point we'll take the formerly pushed ~A, increase it and all
     the remaining lines subprooflevels and step over it

     |Premises
     |~A
     |__
     ||_~(AvB)
     |||_A
     ||| A v B
     |||Absurd
     ||~A
     ||<other lines>
     ||B

     and start adding at the very end to get

     |Premises
     |~A
     |__
     ||_~(AvB)
     |||_A
     ||| A v B
     |||Absurd
     ||~A
     ||<other lines>
     ||B
     ||A v B
     ||Absurd
     |~~(AvB)
     |A v B



     */
    int index, i;
    TProofline templateline, newline;
    TFormula notAorB,notnotAorB;//, tempFormula;

    index = fLastAssIndex;
    templateline = (TProofline) (fHead.get(index));

    notAorB = new TFormula(TFormula.unary, String.valueOf(chNeg), null,
                                  orFormula.copyFormula()); //{not(A or B) }

    notnotAorB = new TFormula(TFormula.unary, String.valueOf(chNeg), null,
                                     notAorB.copyFormula()); //not {not(A or B) }

if (TPreferencesData.fUseAbsurd){
      newline = supplyProofline();
      newline.fLineno = 2000;
      newline.fFormula = notAorB;
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fJustification = fAssJustification;
      newline.fLastassumption = true;

      index += 1; // our add adds at index not before next index so increment to start
      fHead.add(index, newline);
      index += 1;

      /*
        index := lastAssumption;
        templateline := TProofline(localHead.At(index));

        SNIP
        begin
          fLineno := 2000;
          fFormula := newformulanode;
          fSubprooflevel := templateline.fSubprooflevel + 1;
          fJustification := 'Ass';
          fLastAssumption := true;
         end;


        }*/

      newline = supplyProofline();
      newline.fLineno = 2001;
      newline.fFormula = orFormula.fLLink.copyFormula(); //A
      newline.fSubprooflevel = templateline.fSubprooflevel + 2;
      newline.fJustification = fAssJustification;
      newline.fLastassumption = true;

      fHead.add(index, newline);
      index += 1;

      /*
        SupplyProofline(newline);
        with newline do
         begin
          fLineno := 2001;
          fFormula := orFormula.fLlink.CopyFormula; {A}
          fSubprooflevel := templateline.fSubprooflevel + 2;
          fJustification := 'Ass';
          fLastAssumption := true;
         end;

        localHead.InsertBefore(index + 1, newline);
        index := index + 1;


       }
       */
    // newformulanode= new TFormula(TFormula.unary,String.valueOf(chNeg),null,orFormula.copyFormula()); //{not(A or B) }

      newline = supplyProofline();
      newline.fLineno = 2002;
      newline.fFormula = orFormula.copyFormula(); //A v B
      newline.fSubprooflevel = templateline.fSubprooflevel + 2;
      newline.fJustification = fOrIJustification;
      newline.fFirstjustno = 2001;

      fHead.add(index, newline);
      index += 1;

      /*
          SupplyProofline(newline);
         with newline do
          begin
           fLineno := 2002;
           fFormula := orFormula.CopyFormula; {AorB}
           fSubprooflevel := templateline.fSubprooflevel + 2;
           fJustification := ' �I';
           fFirstjustno := 2001;
          end;

         localHead.InsertBefore(index + 1, newline);
         index := index + 1;


        }*/

      newline = supplyProofline();
      newline.fLineno = 2003;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel + 2;
      newline.fJustification = TProofController.absIJustification;
      newline.fFirstjustno = 2002; // used to be 2000
      newline.fSecondjustno = 2000; // used to be 2002

      fHead.add(index, newline);
      index += 1;

      /*
          SupplyProofline(newline);
           with newline do
            begin
             fLineno := 2003;
             fFormula := gAbsurdFormula.CopyFormula; {AorB}
             fSubprooflevel := templateline.fSubprooflevel + 2;
             fJustification := ' AbsI';
             fFirstjustno := 2000;
             fSecondjustno := 2002;
            end;

       */

      newline = supplyProofline();
      newline.fLineno = 2003;
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fBlankline = true;
      newline.fJustification = "";
      newline.fSelectable = false;

      fHead.add(index, newline);
      index += 1; //now at blankline
      /*
        SupplyProofline(newline); {newline points to new proofline}

        with newline do
         begin
          fLineno := 2003;
          fSubprooflevel := templateline.fSubprooflevel + 1; {checkthis}
          fBlankline := true;
          fJustification := '';
          fSelectable := false;
         end;

       */

      // **** index+=1;                 // exisitng notA

      ( (TProofline) (fHead.get(index))).fFirstjustno = 2003;
      ( (TProofline) (fHead.get(index))).fJustification = fNegIJustification;

      for (i = index; i <= (fHead.size() - 1); i++)
        ( (TProofline) (fHead.get(i))).fSubprooflevel += 1;

      newline = supplyProofline();
      newline.fLineno = 2004;
      newline.fFormula = orFormula.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fJustification = fOrIJustification;
      newline.fFirstjustno = ( (TProofline) (fHead.get(fHead.size() - 1))).
          fLineno;

      fHead.add( /*index,*/newline); // now we start adding at the end, so can forget the index
      //index+=1;


      /*
        localHead.InsertBefore(index + 1, newline);
        index := index + 1;  (*now pointing at blankline*)

        index := index + 1;

                 {existing notA}

        TProofline(localHead.At(index)).fFirstjustno := 2003;
        TProofline(localHead.At(index)).fJustification := ' ~I';

        for i := index to localHead.fSize do
         TProofline(localHead.At(i)).fSubprooflevel := TProofline(localHead.At(i)).fSubprooflevel + 1;

                 {fix the subprooflevels}

                 {B}

        SupplyProofline(newline);
        with newline do
         begin
          fLineno := 2004;
          fFormula := orFormula.CopyFormula;
          fSubprooflevel := templateline.fSubprooflevel + 1;
          fJustification := ' �I';
          fFirstjustno := TProofline(localHead.Last).fLineno;
         end;

       */

      newline = supplyProofline();
      newline.fLineno = 2005;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fJustification = TProofController.absIJustification;
      newline.fFirstjustno = 2000;
      newline.fSecondjustno = 2004;

      fHead.add( /*index,*/newline);
      // index+=1;

      newline = supplyProofline();
      newline.fLineno = 2005;
      newline.fSubprooflevel = templateline.fSubprooflevel;
      newline.fBlankline = true;
      newline.fJustification = "";
      newline.fSelectable = false;

      fHead.add( /*index,*/newline);
      //index+=1;                 //now at blankline


      /*
       SupplyProofline(newline);
        with newline do
         begin
          fLineno := 2005;
          fFormula := gAbsurdFormula.CopyFormula; {AorB}
          fSubprooflevel := templateline.fSubprooflevel + 1;
          fJustification := ' AbsI';
          fFirstjustno := 2000;
          fSecondjustno := 2004;
         end;

        localHead.InsertLast(newline);

        SupplyProofline(newline); {newline points to new proofline}

        with newline do
         begin
          fLineno := 2005;
          fSubprooflevel := templateline.fSubprooflevel; {checkthis}
          fBlankline := true;
          fJustification := '';
          fSelectable := false;
         end;

       */

      newline = supplyProofline();
      newline.fLineno = 2006;
      newline.fFormula = notnotAorB;
      newline.fSubprooflevel = templateline.fSubprooflevel;
      newline.fJustification = TProofController.fNegIJustification;
      newline.fFirstjustno = 2005;

      fHead.add( /*index,*/newline);
      //index+=1;

      newline = supplyProofline();
      newline.fLineno = 2007;
      newline.fFormula = orFormula.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel;
      newline.fJustification = TProofController.negEJustification;
      newline.fFirstjustno = 2006;

      fHead.add(newline);
    }
else{
  newline = supplyProofline();
 newline.fLineno = 2000;
 newline.fFormula = notAorB ;
 newline.fSubprooflevel = templateline.fSubprooflevel + 1;
 newline.fJustification = fAssJustification;
 newline.fLastassumption = true;

 index += 1; // our add adds at index not before next index so increment to start
 fHead.add(index, newline);
 index += 1;

 newline = supplyProofline();
 newline.fLineno = 2001;
 newline.fFormula = orFormula.fLLink.copyFormula(); //A
 newline.fSubprooflevel = templateline.fSubprooflevel + 2;
 newline.fJustification = fAssJustification;
 newline.fLastassumption = true;

 fHead.add(index, newline);
 index += 1;

 newline = supplyProofline();
 newline.fLineno = 2002;
 newline.fFormula = orFormula.copyFormula(); //A v B
 newline.fSubprooflevel = templateline.fSubprooflevel + 2;
 newline.fJustification = fOrIJustification;
 newline.fFirstjustno = 2001;

 fHead.add(index, newline);
 index += 1;

/*
 newline = supplyProofline();
 newline.fLineno = 2003;
 newline.fFormula = TFormula.fAbsurd.copyFormula();
 newline.fSubprooflevel = templateline.fSubprooflevel + 2;
 newline.fJustification = TProofController.absIJustification;
 newline.fFirstjustno = 2002; // used to be 2000
 newline.fSecondjustno = 2000; // used to be 2002

 fHead.add(index, newline);
 index += 1;
*/

 newline = supplyProofline();
 newline.fLineno = 2002;
 newline.fSubprooflevel = templateline.fSubprooflevel + 1;
 newline.fBlankline = true;
 newline.fJustification = "";
 newline.fSelectable = false;

 fHead.add(index, newline);
 index += 1; //now at blankline

//This line is A
 ( (TProofline) (fHead.get(index))).fFirstjustno = 2002;//2003; AvB
 ( (TProofline) (fHead.get(index))).fSecondjustno = 2000;   //~(avB)
 ( (TProofline) (fHead.get(index))).fJustification = fNegIJustification;

 for (i = index; i <= (fHead.size() - 1); i++)
   ( (TProofline) (fHead.get(i))).fSubprooflevel += 1;

 newline = supplyProofline();
 newline.fLineno = 2003;
 newline.fFormula = orFormula.copyFormula();      //AvB
 newline.fSubprooflevel = templateline.fSubprooflevel + 1;
 newline.fJustification = fOrIJustification;
 newline.fFirstjustno = ( (TProofline) (fHead.get(fHead.size() - 1))).
     fLineno;

 fHead.add(newline); // now we start adding at the end, so can forget the index

/*
 newline = supplyProofline();
 newline.fLineno = 2005;
 newline.fFormula = TFormula.fAbsurd.copyFormula();
 newline.fSubprooflevel = templateline.fSubprooflevel + 1;
 newline.fJustification = TProofController.absIJustification;
 newline.fFirstjustno = 2000;
 newline.fSecondjustno = 2004;

 fHead.add( newline);
 // index+=1;  */

 newline = supplyProofline();
 newline.fLineno = 2003;
 newline.fSubprooflevel = templateline.fSubprooflevel;
 newline.fBlankline = true;
 newline.fJustification = "";
 newline.fSelectable = false;

 fHead.add(newline);


 newline = supplyProofline();
 newline.fLineno = 2004;
 newline.fFormula = notnotAorB;
 newline.fSubprooflevel = templateline.fSubprooflevel;
 newline.fJustification = TProofController.fNegIJustification;
 newline.fFirstjustno = 2000;
 newline.fSecondjustno = 2003;  //was 2

 fHead.add(newline);

 newline = supplyProofline();
 newline.fLineno = 2005;
 newline.fFormula = orFormula.copyFormula();
 newline.fSubprooflevel = templateline.fSubprooflevel;
 newline.fJustification = TProofController.negEJustification;
 newline.fFirstjustno = 2004;

 fHead.add(newline);

}


    /*
     SupplyFormula(tempformula);
      with tempformula do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := orFormula.CopyFormula;
       end; {not(A or B) }

      SupplyFormula(newformulanode);
      with newformulanode do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := tempFormula;
       end;

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2006;
        fFormula := newformulanode; {not not(A or B) }
        fSubprooflevel := templateline.fSubprooflevel;
        fJustification := ' ~I';
        fFirstjustno := 2005;
       end;

      localHead.InsertLast(newline);

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2007;
        fSubprooflevel := templateline.fSubprooflevel;
        fFormula := orFormula.CopyFormula;
        fJustification := ' ~E';
        fFirstjustno := 2006;
       end;

      localHead.InsertLast(newline);
     end;

     */
  }


  /*
   procedure CreateLemma1A (orFormula: TFormula; var localHead: Tlist; lastAssumption: integer);

   {This is quite complicated.  We have a proof of B from premises including ~A, and we}
   {are trying to convert this into a proof of AVB.  We have pushed ~A as first genuine line}
   {of proof}

   var
    index, i: integer;
    templateline, newline: TProofline;
    newformulanode, tempFormula: TFormula;

   begin
   index := lastAssumption;
   templateline := TProofline(localHead.At(index));

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := orFormula.CopyFormula;
    end; {not(A or B) }

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2000;
     fFormula := newformulanode;
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fJustification := 'Ass';
     fLastAssumption := true;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2001;
     fFormula := orFormula.fLlink.CopyFormula; {A}
     fSubprooflevel := templateline.fSubprooflevel + 2;
     fJustification := 'Ass';
     fLastAssumption := true;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := orFormula.CopyFormula; {AorB}
     fSubprooflevel := templateline.fSubprooflevel + 2;
     fJustification := ' �I';
     fFirstjustno := 2001;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := gAbsurdFormula.CopyFormula; {AorB}
     fSubprooflevel := templateline.fSubprooflevel + 2;
     fJustification := ' AbsI';
     fFirstjustno := 2000;
     fSecondjustno := 2002;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2003;
     fSubprooflevel := templateline.fSubprooflevel + 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;  (*now pointing at blankline*)

   index := index + 1;

            {existing notA}

   TProofline(localHead.At(index)).fFirstjustno := 2003;
   TProofline(localHead.At(index)).fJustification := ' ~I';

   for i := index to localHead.fSize do
    TProofline(localHead.At(i)).fSubprooflevel := TProofline(localHead.At(i)).fSubprooflevel + 1;

            {fix the subprooflevels}

            {B}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := orFormula.CopyFormula;
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fJustification := ' �I';
     fFirstjustno := TProofline(localHead.Last).fLineno;
    end;

   localHead.InsertLast(newline);

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := gAbsurdFormula.CopyFormula; {AorB}
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fJustification := ' AbsI';
     fFirstjustno := 2000;
     fSecondjustno := 2004;
    end;

   localHead.InsertLast(newline);

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2005;
     fSubprooflevel := templateline.fSubprooflevel; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertLast(newline);

   SupplyFormula(tempformula);
   with tempformula do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := orFormula.CopyFormula;
    end; {not(A or B) }

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula;
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := newformulanode; {not not(A or B) }
     fSubprooflevel := templateline.fSubprooflevel;
     fJustification := ' ~I';
     fFirstjustno := 2005;
    end;

   localHead.InsertLast(newline);

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fSubprooflevel := templateline.fSubprooflevel;
     fFormula := orFormula.CopyFormula;
     fJustification := ' ~E';
     fFirstjustno := 2006;
    end;

   localHead.InsertLast(newline);
   end;


   */


/*  without Absurd

   procedure CreateLemma1A (orFormula: TFormula; var localHead: Tlist; lastAssumption: integer);

   {This is quite complicated.  We have a proof of B from premises including ~A, and we}
   {are trying to convert this into a proof of AVB.  We have pushed ~A as first genuine line}
   {of proof}

    var
     index, i: integer;
     templateline, newline: TProofline;
     newformulanode, tempFormula: TFormula;

   begin
    index := lastAssumption;
    templateline := TProofline(localHead.At(index));

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := orFormula.CopyFormula;
     end; {not(A or B) }

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2000;
      fFormula := newformulanode;
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fJustification := 'Ass';
      fLastAssumption := true;
     end;

    localHead.InsertBefore(index + 1, newline);
    index := index + 1;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2001;
      fFormula := orFormula.fLlink.CopyFormula; {A}
      fSubprooflevel := templateline.fSubprooflevel + 2;
      fJustification := 'Ass';
      fLastAssumption := true;
     end;

    localHead.InsertBefore(index + 1, newline);
    index := index + 1;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2002;
      fFormula := orFormula.CopyFormula; {AorB}
      fSubprooflevel := templateline.fSubprooflevel + 2;
      fJustification := ' �I';
      fFirstjustno := 2001;
     end;

    localHead.InsertBefore(index + 1, newline);
    index := index + 1;

    SupplyProofline(newline); {newline points to new proofline}

    with newline do
     begin
      fLineno := 2002;
      fSubprooflevel := templateline.fSubprooflevel + 1; {checkthis}
      fBlankline := true;
      fJustification := '';
      fSelectable := false;
     end;

    localHead.InsertBefore(index + 1, newline);
    index := index + 1;  (*now pointing at blankline*)

    index := index + 1;

             {existing notA}

    TProofline(localHead.At(index)).fFirstjustno := 2002;
    TProofline(localHead.At(index)).fSecondjustno := 2000;
    TProofline(localHead.At(index)).fJustification := ' ~I';

    for i := index to localHead.fSize do
     TProofline(localHead.At(i)).fSubprooflevel := TProofline(localHead.At(i)).fSubprooflevel + 1;

             {fix the subprooflevels}

             {B}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2003;
      fFormula := orFormula.CopyFormula;
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fJustification := ' �I';
      fFirstjustno := TProofline(localHead.Last).fLineno;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline); {newline points to new proofline}

    with newline do
     begin
      fLineno := 2003;
      fSubprooflevel := templateline.fSubprooflevel; {checkthis}
      fBlankline := true;
      fJustification := '';
      fSelectable := false;
     end;

    localHead.InsertLast(newline);

    SupplyFormula(tempformula);
    with tempformula do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := orFormula.CopyFormula;
     end; {not(A or B) }

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := tempFormula;
     end;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2004;
      fFormula := newformulanode; {not not(A or B) }
      fSubprooflevel := templateline.fSubprooflevel;
      fJustification := ' ~I';
      fFirstjustno := 2003;
      fSecondjustno := 2000;
     end;

    localHead.InsertLast(newline);

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2005;
      fSubprooflevel := templateline.fSubprooflevel;
      fFormula := orFormula.CopyFormula;
      fJustification := ' ~E';
      fFirstjustno := 2004;
     end;

    localHead.InsertLast(newline);
end;


  */

  void createLemma3(int insertIndex, TFormula negAndFormula){
     if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/)
       createLemma3WithAbsurd(insertIndex,negAndFormula);
     else
       createLemma3WithOutAbsurd(insertIndex,negAndFormula);
   }



   void createLemma3WithAbsurd(int insertIndex,TFormula negAndFormula){

  /*   {this creates a thirteen line proof of (~A v ~B) from ~(A ^ B) as line 1}*/

   TFormula  A, B,AandB, notAornotB,
       notA, notB,notnotAornotB,notnotnotAornotB;


     AandB=negAndFormula.fRLink;
     A=AandB.fLLink;
     B=AandB.fRLink;

     notA=new TFormula (TFormula.unary,
                        String.valueOf(chNeg),
                        null,
                        A);

     notB=new TFormula (TFormula.unary,
                        String.valueOf(chNeg),
                        null,
                        B);

     notAornotB=new TFormula (TFormula.binary,
                        String.valueOf(chOr),
                        notA,
                        notB);

     notnotAornotB=new TFormula (TFormula.unary,
                          String.valueOf(chNeg),
                          null,
                          notAornotB);
  notnotnotAornotB=new TFormula (TFormula.unary,
                          String.valueOf(chNeg),
                          null,
                          notnotAornotB);



     TProofline newline,templateLine,firstLine;



     firstLine=(TProofline)fHead.get(0);
     templateLine=(TProofline)fHead.get(fHead.size()-1);



    newline = supplyProofline();
    newline.fLineno = 2001;
    newline.fFormula = notnotAornotB.copyFormula();
    newline.fSubprooflevel = 1;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

   /*

   SupplyProofline(newline); {line 1 puts in ~(~A V ~B)}
      with newline do
       begin
        fLineno := 2001;
        fFormula := notnotAornotB;
        fJustification := 'Ass';
        fSubprooflevel := 1;
        fLastAssumption := true;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;


     */

    newline = supplyProofline();
    newline.fLineno = 2002;
    newline.fFormula = A.copyFormula();
    newline.fSubprooflevel = 2;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;


  /*
   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;  */

    newline = supplyProofline();
    newline.fLineno = 2003;
    newline.fFormula = B.copyFormula();
    newline.fSubprooflevel = 3;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;



  /**

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := formulaB.CopyFormula; {B}
     fJustification := 'Ass';
     fSubprooflevel := 3;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   */
    newline = supplyProofline();
    newline.fLineno = 2004;
    newline.fFormula = AandB.copyFormula();
    newline.fSubprooflevel = 3;
    newline.fJustification = fAssJustification;
    newline.fFirstjustno=2002;
    newline.fSecondjustno=2003;

    fHead.add(insertIndex, newline);
    insertIndex += 1;


  /*

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := newformulanode; {A^B}
     fJustification := ' ^I';
     fFirstjustno := 2002;
     fSecondjustno := 2003;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   */
  newline = supplyProofline();
 newline.fLineno = 2005;
 newline.fFormula = TFormula.fAbsurd.copyFormula();
 newline.fSubprooflevel = 3;
 newline.fJustification = TProofController.absIJustification;
 newline.fFirstjustno=1;
 newline.fSecondjustno=2004;

 fHead.add(insertIndex, newline);
 insertIndex += 1;


  /*

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := gAbsurdFormula.CopyFormula;
     fJustification := ' AbsI';
     fFirstjustno := 1;
     fSecondjustno := 2004;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;


   */

    newline = supplyProofline();


    newline.fLineno = 2005;
    newline.fSubprooflevel = 2;
    newline.fBlankline = true;
    newline.fJustification = "";
    newline.fSelectable = false;

    fHead.add(insertIndex, newline);
 insertIndex += 1;


  /*
    SupplyProofline(newline); {newline points to new proofline}

      with newline do
       begin
        fLineno := 2005;
        fSubprooflevel := 2; {checkthis}
        fBlankline := true;
        fJustification := '';
        fSelectable := false;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;


   */

  newline = supplyProofline();
newline.fLineno = 2006;
newline.fFormula = notB.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=2005;

fHead.add(insertIndex, newline);
insertIndex += 1;



  /*

      SupplyFormula(newformulanode);
      with newformulanode do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := formulaB.CopyFormula; {B}
       end;

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2006;
        fFormula := newformulanode; {not-B}
        fJustification := ' ~I';
        fFirstjustno := 2005;
        fSubprooflevel := 2;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;

 */

newline = supplyProofline();
newline.fLineno = 2007;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = fOrIJustification;
newline.fFirstjustno=2006;

fHead.add(insertIndex, newline);
 insertIndex += 1;


  /*
    SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2007;
        fFormula := notnotAornotB.fRlink.CopyFormula; {notA ornotB}
        fJustification := ' �I';
        fFirstjustno := 2006;
        fSubprooflevel := 2;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;  */

  newline = supplyProofline();
newline.fLineno = 2008;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2007;

fHead.add(insertIndex, newline);
insertIndex += 1;




  /*

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2008;
        fFormula := gAbsurdFormula.CopyFormula; {notA ornotB}
        fJustification := ' AbsI';
        fFirstjustno := 2001;
        fSecondjustno := 2007;
        fSubprooflevel := 2;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

*/

  newline = supplyProofline();


newline.fLineno = 2008;
newline.fSubprooflevel = 1;
newline.fBlankline = true;
newline.fJustification = "";
newline.fSelectable = false;

fHead.add(insertIndex, newline);
insertIndex += 1;


  /*
   SupplyProofline(newline); {newline points to new proofline}

      with newline do
       begin
        fLineno := 2008;
        fSubprooflevel := 1; {checkthis}
        fBlankline := true;
        fJustification := '';
        fSelectable := false;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;
   */

  newline = supplyProofline();
newline.fLineno = 2009;
newline.fFormula = notA.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=2008;

fHead.add(insertIndex, newline);
insertIndex += 1;
/*
      SupplyFormula(newformulanode);
      with newformulanode do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := formulaA.CopyFormula; {A}
       end;

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2009;
        fFormula := newformulanode; {notA}
        fJustification := ' ~I';
        fFirstjustno := 2008;
        fSubprooflevel := +1;
    end;

*/

newline = supplyProofline();
newline.fLineno = 2010;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = fOrIJustification;
newline.fFirstjustno=2009;

fHead.add(insertIndex, newline);
 insertIndex += 1;

/*
 SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2010;
      fFormula := notnotAornotB.fRlink.CopyFormula; {notA or notB}
      fJustification := ' �I';
      fFirstjustno := 2009;
      fSubprooflevel := +1;
     end;

    localHead.InsertBefore(insertHere + 1, newline);
    insertHere := insertHere + 1;  */

newline = supplyProofline();
newline.fLineno = 2011;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2010;

fHead.add(insertIndex, newline);
insertIndex += 1;

/*

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2011;
      fFormula := gAbsurdFormula.CopyFormula; {notA or notB}
      fJustification := ' AbsI';
      fFirstjustno := 2001;
      fSecondjustno := 2010;
      fSubprooflevel := +1;
     end;

    localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

*/
newline = supplyProofline();


newline.fLineno = 2011;
newline.fSubprooflevel = 0;
newline.fBlankline = true;
newline.fJustification = "";
newline.fSelectable = false;

fHead.add(insertIndex, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2012;
newline.fFormula = notnotnotAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=2011;

fHead.add(insertIndex, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2013;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TProofController.negEJustification;
newline.fFirstjustno=2012;

fHead.add(insertIndex, newline);
insertIndex += 1;

/*

 SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2012;
      fFormula := newformulanode; {notnot Aor ntoB}
      fJustification := ' ~I';
      fFirstjustno := 2011;
     end;

    localHead.InsertBefore(insertHere + 1, newline);
    insertHere := insertHere + 1;

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 2013;
      fFormula := notnotAornotB.fRlink.CopyFormula; {notAor notB}
      fJustification := ' ~E';
      fFirstjustno := 2012;
     end;

    localHead.InsertBefore(insertHere + 1, newline);
    insertHere := insertHere + 1;


*/

}

/*
    procedure CreateLemma3 (var localHead: Tlist; insertHere: integer); {no longer a var param}
  {this creates a ten line proof of (~A v ~B) from ~(A ^ B) as line 1}

   var
    newformulanode, tempFormula, formulaA, formulaB, notnotAornotB: TFormula;
    newline: TProofline;

  begin

   formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
   formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chOr;
     fLlink := tempFormula;
     fRlink := formulaB.CopyFormula; {B}
    end;

            {so far we have not-A or B}

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula.fRlink; {B}
    end;

   tempFormula.fRlink := newformulanode;

            {so far we have not-A or not-B}

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula;
    end;

            {so far we have not (not-A or not-B)}

   notnotAornotB := newformulanode;

   SupplyProofline(newline); {line 1 puts in ~(~A V ~B)}
   with newline do
    begin
     fLineno := 2001;
     fFormula := notnotAornotB;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := formulaB.CopyFormula; {B}
     fJustification := 'Ass';
     fSubprooflevel := 3;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chAnd;
     fLlink := formulaA.CopyFormula; {A}
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := newformulanode; {A^B}
     fJustification := ' ^I';
     fFirstjustno := 2002;
     fSecondjustno := 2003;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := gAbsurdFormula.CopyFormula;
     fJustification := ' AbsI';
     fFirstjustno := 1;
     fSecondjustno := 2004;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2005;
     fSubprooflevel := 2; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := newformulanode; {not-B}
     fJustification := ' ~I';
     fFirstjustno := 2005;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notA ornotB}
     fJustification := ' �I';
     fFirstjustno := 2006;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2008;
     fFormula := gAbsurdFormula.CopyFormula; {notA ornotB}
     fJustification := ' AbsI';
     fFirstjustno := 2001;
     fSecondjustno := 2007;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2008;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2009;
     fFormula := newformulanode; {notA}
     fJustification := ' ~I';
     fFirstjustno := 2008;
     fSubprooflevel := +1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2010;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notA or notB}
     fJustification := ' �I';
     fFirstjustno := 2009;
     fSubprooflevel := +1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2011;
     fFormula := gAbsurdFormula.CopyFormula; {notA or notB}
     fJustification := ' AbsI';
     fFirstjustno := 2001;
     fSecondjustno := 2010;
     fSubprooflevel := +1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2011;
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := notnotAornotB.CopyFormula; {notnot Aor ntoB}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2012;
     fFormula := newformulanode; {notnot Aor ntoB}
     fJustification := ' ~I';
     fFirstjustno := 2011;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2013;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notAor notB}
     fJustification := ' ~E';
     fFirstjustno := 2012;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

  end;

  {$ELSEC}

  procedure CreateLemma3 (var localHead: Tlist; insertHere: integer); {no longer a var param}
  {this creates a ten line proof of (~A v ~B) from ~(A ^ B) as line 1}

   var
    newformulanode, tempFormula, formulaA, formulaB, notnotAornotB: TFormula;
    newline: TProofline;

  begin

   formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
   formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chOr;
     fLlink := tempFormula;
     fRlink := formulaB.CopyFormula; {B}
    end;

            {so far we have not-A or B}

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula.fRlink; {B}
    end;

   tempFormula.fRlink := newformulanode;

            {so far we have not-A or not-B}

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula;
    end;

            {so far we have not (not-A or not-B)}

   notnotAornotB := newformulanode;

   SupplyProofline(newline); {line 1 puts in ~(~A V ~B)}
   with newline do
    begin
     fLineno := 2001;
     fFormula := notnotAornotB;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := formulaB.CopyFormula; {B}
     fJustification := 'Ass';
     fSubprooflevel := 3;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chAnd;
     fLlink := formulaA.CopyFormula; {A}
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := newformulanode; {A^B}
     fJustification := ' ^I';
     fFirstjustno := 2002;
     fSecondjustno := 2003;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2004;
     fSubprooflevel := 2; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := newformulanode; {not-B}
     fJustification := ' ~I';
     fFirstjustno := 1;
     fSecondjustno := 2004;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notA ornotB}
     fJustification := ' �I';
     fFirstjustno := 2005;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2006;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := newformulanode; {notA}
     fJustification := ' ~I';
     fFirstjustno := 2001;
     fSecondjustno := 2006;

     fSubprooflevel := +1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2008;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notA or notB}
     fJustification := ' �I';
     fFirstjustno := 2007;

     fSubprooflevel := +1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2008;
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := notnotAornotB.CopyFormula; {notnot Aor ntoB}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2009;
     fFormula := newformulanode; {notnot Aor ntoB}
     fJustification := ' ~I';
     fFirstjustno := 2001;
     fSecondjustno := 2008;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2010;
     fFormula := notnotAornotB.fRlink.CopyFormula; {notAor notB}
     fJustification := ' ~E';
     fFirstjustno := 2009;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

  end;


  {$ENDC}


  */

 void createLemma3WithOutAbsurd(int insertIndex,TFormula negAndFormula){

 /*   {this creates a thirteen line proof of (~A v ~B) from ~(A ^ B) as line 1}*/

  TFormula  A, B,AandB, notAornotB,
      notA, notB,notnotAornotB,notnotnotAornotB;


    AandB=negAndFormula.fRLink;
    A=AandB.fLLink;
    B=AandB.fRLink;

    notA=new TFormula (TFormula.unary,
                       String.valueOf(chNeg),
                       null,
                       A);

    notB=new TFormula (TFormula.unary,
                       String.valueOf(chNeg),
                       null,
                       B);

    notAornotB=new TFormula (TFormula.binary,
                       String.valueOf(chOr),
                       notA,
                       notB);

    notnotAornotB=new TFormula (TFormula.unary,
                         String.valueOf(chNeg),
                         null,
                         notAornotB);
 notnotnotAornotB=new TFormula (TFormula.unary,
                         String.valueOf(chNeg),
                         null,
                         notnotAornotB);



    TProofline newline,templateLine,firstLine;



    firstLine=(TProofline)fHead.get(0);
    templateLine=(TProofline)fHead.get(fHead.size()-1);



   newline = supplyProofline();
   newline.fLineno = 2001;
   newline.fFormula = notnotAornotB.copyFormula();
   newline.fSubprooflevel = 1;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;


   newline = supplyProofline();
   newline.fLineno = 2002;
   newline.fFormula = A.copyFormula();
   newline.fSubprooflevel = 2;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
   newline.fLineno = 2003;
   newline.fFormula = B.copyFormula();
   newline.fSubprooflevel = 3;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;


   newline = supplyProofline();
   newline.fLineno = 2004;
   newline.fFormula = AandB.copyFormula();
   newline.fSubprooflevel = 3;
   newline.fJustification = fAssJustification;
   newline.fFirstjustno=2002;
   newline.fSecondjustno=2003;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

/*
 newline = supplyProofline();
newline.fLineno = 2005;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 3;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno=1;
newline.fSecondjustno=2004;

fHead.add(insertIndex, newline);
insertIndex += 1;


 /*

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2005;
    fFormula := gAbsurdFormula.CopyFormula;
    fJustification := ' AbsI';
    fFirstjustno := 1;
    fSecondjustno := 2004;
    fSubprooflevel := 3;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;


  */

   newline = supplyProofline();


   newline.fLineno = 2004;
   newline.fSubprooflevel = 2;
   newline.fBlankline = true;
   newline.fJustification = "";
   newline.fSelectable = false;

   fHead.add(insertIndex, newline);
insertIndex += 1;


 newline = supplyProofline();
newline.fLineno = 2005;
newline.fFormula = notB.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=1;
 newline.fSecondjustno=2004;

fHead.add(insertIndex, newline);
insertIndex += 1;


newline = supplyProofline();
newline.fLineno = 2006;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = fOrIJustification;
newline.fFirstjustno=2005;

fHead.add(insertIndex, newline);
insertIndex += 1;

/*
 newline = supplyProofline();
newline.fLineno = 2008;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 2;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2007;

fHead.add(insertIndex, newline);
insertIndex += 1;




 /*

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := 2008;
       fFormula := gAbsurdFormula.CopyFormula; {notA ornotB}
       fJustification := ' AbsI';
       fFirstjustno := 2001;
       fSecondjustno := 2007;
       fSubprooflevel := 2;
      end;

     localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

*/

 newline = supplyProofline();


newline.fLineno = 2006;
newline.fSubprooflevel = 1;
newline.fBlankline = true;
newline.fJustification = "";
newline.fSelectable = false;

fHead.add(insertIndex, newline);
insertIndex += 1;


 newline = supplyProofline();
newline.fLineno = 2007;
newline.fFormula = notA.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2006;

fHead.add(insertIndex, newline);
insertIndex += 1;


newline = supplyProofline();
newline.fLineno = 2008;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = fOrIJustification;
newline.fFirstjustno=2007;

fHead.add(insertIndex, newline);
insertIndex += 1;
/*
newline = supplyProofline();
newline.fLineno = 2009;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2008;

fHead.add(insertIndex, newline);
insertIndex += 1;
  */

newline = supplyProofline();


newline.fLineno = 2008;
newline.fSubprooflevel = 0;
newline.fBlankline = true;
newline.fJustification = "";
newline.fSelectable = false;

fHead.add(insertIndex, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2009;
newline.fFormula = notnotnotAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2008;

fHead.add(insertIndex, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2010;
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TProofController.negEJustification;
newline.fFirstjustno=2009;

fHead.add(insertIndex, newline);
insertIndex += 1;
}


void createLemma4(int insertIndex,boolean useAbsurd){

  //{this creates a proof of (~A v B) from (A > B) as line 1}

TFormula /* newformulanode,*/ tempFormula, formulaA, formulaB, notA,notAorB,notnotAorB,notnotnotAorB;
TProofline newline;

formulaA=((TProofline)fHead.get(0)).fFormula.fLLink;
formulaB=((TProofline)fHead.get(0)).fFormula.fRLink;

notA=new TFormula(TFormula.unary,
                              String.valueOf(chNeg),
                              null,formulaA.copyFormula()); //~A
notAorB=new TFormula(TFormula.binary,
                       String.valueOf(chOr),
                       new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                    null,
                                    formulaA.copyFormula()),
                       formulaB.copyFormula()); //~A v B
notnotAorB=new TFormula(TFormula.unary,            // ~(~AvB)
                          String.valueOf(chNeg),
                          null,
                          notAorB);

notnotnotAorB=new TFormula(TFormula.unary,
                             String.valueOf(chNeg),
                             null,
                             notnotAorB.copyFormula()); //~~(~AvB)



if (useAbsurd){

  newline = supplyProofline();
  newline.fLineno = 2001;
  newline.fFormula = notnotAorB;
  newline.fSubprooflevel = 1;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fFormula = formulaA.copyFormula();
  newline.fSubprooflevel = 2;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2003;
  newline.fFormula = formulaB.copyFormula();
  newline.fJustification = fImplicEJustification;
  newline.fFirstjustno = 2002;
  newline.fSecondjustno = 1000;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2004;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~(AvB)
  newline.fJustification = fOrIJustification;
  newline.fFirstjustno = 2003;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2005;
  newline.fFormula = TFormula.fAbsurd.copyFormula();
  newline.fJustification = TProofController.absIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2004;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2005;
  newline.fBlankline = true;
  newline.fJustification = "";
  newline.fSelectable = false;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2006;
  newline.fFormula = notnotAorB.fRLink.fLLink.copyFormula();  //~A
  newline.fJustification = TProofController.fNegIJustification;
  newline.fFirstjustno = 2005;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2007;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
  newline.fJustification = fOrIJustification;
  newline.fFirstjustno = 2006;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2008;
  newline.fFormula = TFormula.fAbsurd.copyFormula();  //~A
  newline.fJustification = TProofController.absIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2007;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2008;
  newline.fBlankline = true;
  newline.fJustification = "";
  newline.fSelectable = false;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;


  newline = supplyProofline();
  newline.fLineno = 2009;
  newline.fFormula = notnotnotAorB;
  newline.fJustification = TProofController.fNegIJustification;
  newline.fFirstjustno = 2008;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2010;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
  newline.fJustification = TProofController.negEJustification;
  newline.fFirstjustno = 2009;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;


//end

}
else{
  newline = supplyProofline();
  newline.fLineno = 2001;
  newline.fFormula = notnotAorB;
  newline.fSubprooflevel = 1;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fFormula = formulaA.copyFormula();
  newline.fSubprooflevel = 2;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2003;
  newline.fFormula = formulaB.copyFormula();
  newline.fJustification = fImplicEJustification;
  newline.fFirstjustno = 2002;
  newline.fSecondjustno = 1000;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2004;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~(AvB)
  newline.fJustification = fOrIJustification;
  newline.fFirstjustno = 2003;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

/*  newline = supplyProofline();
  newline.fLineno = 2005;
  newline.fFormula = TFormula.fAbsurd.copyFormula();
  newline.fJustification = TProofController.absIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2004;
  newline.fSubprooflevel = 2;

  fHead.add(insertIndex, newline);
  insertIndex += 1; */

  newline = supplyProofline();
  newline.fLineno = 2004;
  newline.fBlankline = true;
  newline.fJustification = "";
  newline.fSelectable = false;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2005;
  newline.fFormula = notnotAorB.fRLink.fLLink.copyFormula();  //~A
  newline.fJustification = TProofController.fNegIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2004;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2006;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
  newline.fJustification = fOrIJustification;
  newline.fFirstjustno = 2005;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

 /* newline = supplyProofline();
  newline.fLineno = 2008;
  newline.fFormula = TFormula.fAbsurd.copyFormula();  //~A
  newline.fJustification = TProofController.absIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2007;
  newline.fSubprooflevel = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1; */

  newline = supplyProofline();
  newline.fLineno = 2006;
  newline.fBlankline = true;
  newline.fJustification = "";
  newline.fSelectable = false;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;


  newline = supplyProofline();
  newline.fLineno = 2007;
  newline.fFormula = notnotnotAorB;
  newline.fJustification = TProofController.fNegIJustification;
  newline.fFirstjustno = 2001;
  newline.fSecondjustno = 2006;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2008;
  newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
  newline.fJustification = TProofController.negEJustification;
  newline.fFirstjustno = 2007;
  newline.fSubprooflevel = 0;

  fHead.add(insertIndex, newline);
  insertIndex += 1;
   }


}



 /*
  {$IFC useAbsurd}

  procedure CreateLemma4 (var localHead: Tlist; insertHere: integer);

  {this creates a proof of (~A v B) from (A > B) as line 1}

   var
    newformulanode, tempFormula, formulaA, formulaB, notnotAorB: TFormula;
    newline: TProofline;

  begin

   formulaA := TProofline(localHead.First).fFormula.fLlink;
   formulaB := TProofline(localHead.First).fFormula.fRlink;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chOr;
     fLlink := tempFormula;
     fRlink := formulaB.CopyFormula; {B}
    end;

            {so far we have not-A or B}

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula; {B}
    end;

   notnotAorB := newformulanode;

   SupplyProofline(newline); {line 1 puts in ~(~A VB)}
   with newline do
    begin
     fLineno := 2001;
     fFormula := notnotAorB;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := formulaB.CopyFormula; {B}
     fJustification := ' �E';
     fFirstjustno := 2002;
     fSecondjustno := 1000;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := notnotAorB.fRlink.CopyFormula; {~AVB}
     fJustification := ' �I';
     fFirstjustno := 2003;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := gAbsurdFormula.CopyFormula; {~AVB}
     fJustification := ' AbsI';
     fFirstjustno := 2001;
     fSecondjustno := 2004;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2005;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := notnotAorB.fRlink.fLlink.CopyFormula; {~A}
     fJustification := ' ~I';
     fFirstjustno := 2005;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := notnotAorB.fRlink.CopyFormula; {~AVB}
     fJustification := ' �I';
     fFirstjustno := 2005;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2008;
     fFormula := gAbsurdFormula.CopyFormula; {~AVB}
     fJustification := ' AbsI';
     fFirstjustno := 2001;
     fSecondjustno := 2007;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2008;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := notnotAorB.CopyFormula;
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2009;
     fFormula := newformulanode; {doublenot}
     fJustification := ' ~I';
     fFirstjustno := 2008;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2010;
     fFormula := notnotAorB.fRlink.CopyFormula; {notA or B}
     fJustification := ' ~E';
     fFirstjustno := 2009;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

  end;


*/

 void createLemma5(TFormula formula,int insertIndex){
    if (TPreferencesData.fUseAbsurd)
      createLemma5WithAbsurd(formula,insertIndex);
    else
      createLemma5WithoutAbsurd(formula,insertIndex);
  }

  void createLemma5WithAbsurd(TFormula formula, int insertIndex){
/*
       {this meshes in lines in a complicated fashion. The proof has ~(AvB) as}
  {its first line and ~A and ~B as itsHead^.next the lines after last assumption}

 */

   TProofline newline,templateLine,firstLine;

   firstLine=(TProofline)fHead.get(0);               // the ~(AvB)
   templateLine=(TProofline)fHead.get(insertIndex-1);

   newline = supplyProofline();
   newline.fLineno = 2001;
   newline.fFormula = formula.fRLink.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fFormula = firstLine.fFormula.fRLink.copyFormula();  // A v B
  newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
  newline.fJustification = fOrIJustification;
  newline.fFirstjustno = 2001;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
    newline.fLineno = 2003;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fJustification = TProofController.absIJustification;
    newline.fFirstjustno = 1;
    newline.fSecondjustno = 2002;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
    newline.fLineno = 2003;
    newline.fSubprooflevel = templateLine.fSubprooflevel;
    newline.fBlankline = true;
    newline.fJustification = "";
    newline.fSelectable = false;

    fHead.add(insertIndex, newline);
    insertIndex += 1; //now at line after blankline

    TProofline nextLine=(TProofline)fHead.get(insertIndex);

    nextLine.fFirstjustno=2003;
    nextLine.fJustification=TProofController.fNegIJustification;
    nextLine.fSubprooflevel=templateLine.fSubprooflevel;

    /*
         localIndex := localIndex + 1; {pointing at blankline}

        localIndex := localIndex + 1;

        TProofline(localHead.At(localIndex)).fFirstjustno := 2003;
        TProofline(localHead.At(localIndex)).fJustification := ' ~I';
        TProofline(localHead.At(localIndex)).fSubprooflevel := templateline.fSubprooflevel;


    */

  }


 /*
  {$IFC useAbsurd}

  procedure CreateLemma5 (var localHead: Tlist; formula: TFormula; index: integer);
  {this meshes in lines in a complicated fashion. The proof has ~(AvB) as}
  {its first line and ~A and ~B as itsHead^.next the lines after last assumption}

   var
    templateline, newline: TProofline;
    localIndex: integer;

  begin
   localIndex := index;

   templateline := TProofline(localHead.At(localIndex));

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2001;
     fFormula := formula.fRlink.CopyFormula;
     fJustification := 'Ass';
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula;
     fJustification := ' �I';
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fFirstjustno := 2001;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := gAbsurdFormula.CopyFormula;
     fJustification := ' AbsI';
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fFirstjustno := 1;
     fSecondjustno := 2002;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2003;
     fSubprooflevel := templateline.fSubprooflevel; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1; {pointing at blankline}

   localIndex := localIndex + 1;

   TProofline(localHead.At(localIndex)).fFirstjustno := 2003;
   TProofline(localHead.At(localIndex)).fJustification := ' ~I';
   TProofline(localHead.At(localIndex)).fSubprooflevel := templateline.fSubprooflevel;

  end;

  {$ELSEC}

  procedure CreateLemma5 (var localHead: Tlist; formula: TFormula; index: integer);
  {this meshes in lines in a complicated fashion. The proof has ~(AvB) as}
  {its first line and ~A and ~B as itsHead^.next the lines after last assumption}

   var
    templateline, newline: TProofline;
    localIndex: integer;

  begin
   localIndex := index;

   templateline := TProofline(localHead.At(localIndex));

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2001;
     fFormula := formula.fRlink.CopyFormula;
     fJustification := 'Ass';
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula;
     fJustification := ' �I';
     fSubprooflevel := templateline.fSubprooflevel + 1;
     fFirstjustno := 2001;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2002;
     fSubprooflevel := templateline.fSubprooflevel; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(localIndex + 1, newline);
   localIndex := localIndex + 1; {pointing at blankline}

   localIndex := localIndex + 1;

   TProofline(localHead.At(localIndex)).fFirstjustno := 1;
   TProofline(localHead.At(localIndex)).fSecondjustno := 2002;
   TProofline(localHead.At(localIndex)).fJustification := ' ~I';
   TProofline(localHead.At(localIndex)).fSubprooflevel := templateline.fSubprooflevel;

  end;


{$ENDC}

*/

 void createLemma5WithoutAbsurd(TFormula formula, int insertIndex){
 /*
        {this meshes in lines in a complicated fashion. The proof has ~(AvB) as}
   {its first line and ~A and ~B as itsHead^.next the lines after last assumption}

  */

    TProofline newline,templateLine,firstLine;

    firstLine=(TProofline)fHead.get(0);               // the ~(AvB)
    templateLine=(TProofline)fHead.get(insertIndex-1);

    newline = supplyProofline();
    newline.fLineno = 2001;
    newline.fFormula = formula.fRLink.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
   newline.fLineno = 2002;
   newline.fFormula = firstLine.fFormula.fRLink.copyFormula();  // A v B
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = fOrIJustification;
   newline.fFirstjustno = 2001;

   fHead.add(insertIndex, newline);
   insertIndex += 1;
 /*
   newline = supplyProofline();
     newline.fLineno = 2003;
     newline.fFormula = TFormula.fAbsurd.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
     newline.fJustification = TProofController.absIJustification;
     newline.fFirstjustno = 1;
     newline.fSecondjustno = 2002;

     fHead.add(insertIndex, newline);
     insertIndex += 1;  */

     newline = supplyProofline();
     newline.fLineno = 2002;
     newline.fSubprooflevel = templateLine.fSubprooflevel;
     newline.fBlankline = true;
     newline.fJustification = "";
     newline.fSelectable = false;

     fHead.add(insertIndex, newline);
     insertIndex += 1; //now at line after blankline

     TProofline nextLine=(TProofline)fHead.get(insertIndex);

     nextLine.fFirstjustno=1;
     nextLine.fFirstjustno=2002;
     nextLine.fJustification=TProofController.fNegIJustification;
     nextLine.fSubprooflevel=templateLine.fSubprooflevel;

  }

 void createLemma6(int insertIndex){
    if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/)
      createLemma6WithAbsurd(insertIndex);
    else
      createLemma6WithoutAbsurd(insertIndex);
  }

  void createLemma6WithAbsurd( int insertIndex){
/*
        { ~(A>B) as}
       {its first line and ~B as itsHead^.next the line after last assumption}
       {it proves the second from the first}


 */

   TProofline newline,templateLine,firstLine;

   firstLine=(TProofline)fHead.get(0);
   templateLine=(TProofline)fHead.get(insertIndex-1);

   TFormula AarrowB = firstLine.fFormula.fRLink;

   TFormula A = AarrowB.fLLink;
   TFormula B = AarrowB.fRLink;


   newline = supplyProofline();
   newline.fLineno = 2001;
   newline.fFormula = B.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
   newline.fLineno = 2002;
   newline.fFormula = A.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
   newline.fJustification = fAssJustification;
   newline.fLastassumption = true;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
    newline.fLineno = 2002;
    newline.fSubprooflevel = templateLine.fSubprooflevel+1;
    newline.fBlankline = true;
    newline.fJustification = "";
    newline.fSelectable = false;

    fHead.add(insertIndex, newline);
    insertIndex += 1; //now at line after blankline



   newline = supplyProofline();
  newline.fLineno = 2003;
  newline.fFormula = AarrowB.copyFormula();  // A -> B
  newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
  newline.fJustification = fImplicIJustification;
  newline.fFirstjustno = 2001;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
    newline.fLineno = 2004;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fJustification = TProofController.absIJustification;
    newline.fFirstjustno = 1;
    newline.fSecondjustno = 2003;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
    newline.fLineno = 2004;
    newline.fSubprooflevel = templateLine.fSubprooflevel;
    newline.fBlankline = true;
    newline.fJustification = "";
    newline.fSelectable = false;

    fHead.add(insertIndex, newline);
    insertIndex += 1; //now at line after blankline

    TProofline nextLine=(TProofline)fHead.get(insertIndex);

    nextLine.fFirstjustno=2004;
    nextLine.fJustification=TProofController.fNegIJustification;
    nextLine.fSubprooflevel=templateLine.fSubprooflevel;

    /*
         TProofline(localHead.At(insertHere)).fJustification := ' ~I';
       TProofline(localHead.At(insertHere)).fSubprooflevel := templateline.fSubprooflevel;
       TProofline(localHead.At(insertHere)).fFirstjustno := 2004;


    */

  }

  void createLemma6WithoutAbsurd( int insertIndex){
 /*
         { ~(A>B) as}
        {its first line and ~B as itsHead^.next the line after last assumption}
        {it proves the second from the first}


  */

    TProofline newline,templateLine,firstLine;

    firstLine=(TProofline)fHead.get(0);
    templateLine=(TProofline)fHead.get(insertIndex-1);

    TFormula AarrowB = firstLine.fFormula.fRLink;

    TFormula A = AarrowB.fLLink;
    TFormula B = AarrowB.fRLink;


    newline = supplyProofline();
    newline.fLineno = 2001;
    newline.fFormula = B.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
    newline.fLineno = 2002;
    newline.fFormula = A.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
    newline.fJustification = fAssJustification;
    newline.fLastassumption = true;

    fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
     newline.fLineno = 2002;
     newline.fSubprooflevel = templateLine.fSubprooflevel+1;
     newline.fBlankline = true;
     newline.fJustification = "";
     newline.fSelectable = false;

     fHead.add(insertIndex, newline);
     insertIndex += 1; //now at line after blankline



    newline = supplyProofline();
   newline.fLineno = 2003;
   newline.fFormula = AarrowB.copyFormula();  // A -> B
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = fImplicIJustification;
   newline.fFirstjustno = 2001;

   fHead.add(insertIndex, newline);
   insertIndex += 1;
/*
   newline = supplyProofline();
     newline.fLineno = 2004;
     newline.fFormula = TFormula.fAbsurd.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
     newline.fJustification = TProofController.absIJustification;
     newline.fFirstjustno = 1;
     newline.fSecondjustno = 2003;

     fHead.add(insertIndex, newline);
     insertIndex += 1;  */

     newline = supplyProofline();
     newline.fLineno = 2003;
     newline.fSubprooflevel = templateLine.fSubprooflevel;
     newline.fBlankline = true;
     newline.fJustification = "";
     newline.fSelectable = false;

     fHead.add(insertIndex, newline);
     insertIndex += 1; //now at line after blankline

     TProofline nextLine=(TProofline)fHead.get(insertIndex);

     nextLine.fFirstjustno=1;
     nextLine.fSecondjustno=2003;
     nextLine.fJustification=TProofController.fNegIJustification;
     nextLine.fSubprooflevel=templateLine.fSubprooflevel;
  }

/*

   {$IFC useAbsurd}

 procedure CreateLemma6 (var localHead: Tlist; insertHere: integer);

 { ~(A>B) as}
 {its first line and ~B as itsHead^.next the line after last assumption}
 {it proves the second from the first}

  var
   newformulanode, tempFormula, formulaA, formulaB, notnotAorB: TFormula;
   newline, templateline: TProofline;

 begin

  formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
  formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;

  templateline := TProofline(localHead.At(insertHere));

  SupplyProofline(newline); {line 1 puts in B)}
  with newline do
   begin
    fLineno := 2001;
    fFormula := formulaB.CopyFormula;
    fJustification := 'Ass';
    fSubprooflevel := templateline.fSubprooflevel + 1;
    fLastAssumption := true;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2002;
    fFormula := formulaA.CopyFormula; {A}
    fJustification := 'Ass';
    fSubprooflevel := templateline.fSubprooflevel + 2;
    fLastAssumption := true;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fLineno := 2002;
    fSubprooflevel := templateline.fSubprooflevel + 1; {checkthis}
    fBlankline := true;
    fJustification := '';
    fSelectable := false;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2003;
    fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula; {A�B}
    fJustification := ' �I';
    fFirstjustno := 2001;
    fSubprooflevel := templateline.fSubprooflevel + 1;
    ;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2004;
    fFormula := gAbsurdFormula.CopyFormula; {A�B}
    fJustification := ' AbsI';
    fFirstjustno := 1;
    fSecondjustno := 2003;
    fSubprooflevel := templateline.fSubprooflevel + 1;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fLineno := 2004;
    fSubprooflevel := templateline.fSubprooflevel; {checkthis}
    fBlankline := true;
    fJustification := '';
    fSelectable := false;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  insertHere := insertHere + 1;

  TProofline(localHead.At(insertHere)).fJustification := ' ~I';
  TProofline(localHead.At(insertHere)).fSubprooflevel := templateline.fSubprooflevel;
  TProofline(localHead.At(insertHere)).fFirstjustno := 2004;
 end;

 {$ELSEC}

 procedure CreateLemma6 (var localHead: Tlist; insertHere: integer);

 { ~(A>B) as}
 {its first line and ~B as itsHead^.next the line after last assumption}
 {it proves the second from the first}

  var
   newformulanode, tempFormula, formulaA, formulaB, notnotAorB: TFormula;
   newline, templateline: TProofline;

 begin

  formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
  formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;

  templateline := TProofline(localHead.At(insertHere));

  SupplyProofline(newline); {line 1 puts in B)}
  with newline do
   begin
    fLineno := 2001;
    fFormula := formulaB.CopyFormula;
    fJustification := 'Ass';
    fSubprooflevel := templateline.fSubprooflevel + 1;
    fLastAssumption := true;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2002;
    fFormula := formulaA.CopyFormula; {A}
    fJustification := 'Ass';
    fSubprooflevel := templateline.fSubprooflevel + 2;
    fLastAssumption := true;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fLineno := 2002;
    fSubprooflevel := templateline.fSubprooflevel + 1; {checkthis}
    fBlankline := true;
    fJustification := '';
    fSelectable := false;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := 2003;
    fFormula := TProofline(localHead.First).fFormula.fRlink.CopyFormula; {A�B}
    fJustification := ' �I';
    fFirstjustno := 2001;
    fSubprooflevel := templateline.fSubprooflevel + 1;
    ;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  SupplyProofline(newline); {newline points to new proofline}

  with newline do
   begin
    fLineno := 2003;
    fSubprooflevel := templateline.fSubprooflevel; {checkthis}
    fBlankline := true;
    fJustification := '';
    fSelectable := false;
   end;

  localHead.InsertBefore(insertHere + 1, newline);
  insertHere := insertHere + 1;

  insertHere := insertHere + 1;

  TProofline(localHead.At(insertHere)).fJustification := ' ~I';
  TProofline(localHead.At(insertHere)).fSubprooflevel := templateline.fSubprooflevel;
  TProofline(localHead.At(insertHere)).fFirstjustno := 1;
  TProofline(localHead.At(insertHere)).fSecondjustno := 2003;
 end;


     */

    void createLemma7(int insertIndex){
        if (TPreferencesData.fUseAbsurd)
          createLemma7WithAbsurd(insertIndex);
        else
          createLemma7WithoutAbsurd(insertIndex);
      }

      void createLemma7WithAbsurd( int insertIndex){
    /*
            { ~(A>B) as}
           {its first line and ~B as itsHead^.next the line after last assumption}
           {it proves the second from the first}


     */

       TProofline newline,templateLine,firstLine;

       firstLine=(TProofline)fHead.get(0);
       templateLine=(TProofline)fHead.get(insertIndex-1);

       TFormula notAarrowB = firstLine.fFormula;
       TFormula AarrowB = notAarrowB.fRLink;

       TFormula A = AarrowB.fLLink;
       TFormula B = AarrowB.fRLink;

       TFormula notA = new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                   null,
                                   A);
      TFormula notB = new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                   null,
                                   B);
      TFormula notnotA = new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                   null,
                                   notA);

   TFormula notnotB = new TFormula(TFormula.unary,
                                    String.valueOf(chNeg),
                                   null,
                                   notB);




       newline = supplyProofline();
       newline.fLineno = 2001;
       newline.fFormula = notA.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
       newline.fJustification = fAssJustification;
       newline.fLastassumption = true;

       fHead.add(insertIndex, newline);
       insertIndex += 1;

       newline = supplyProofline();
       newline.fLineno = 2002;
       newline.fFormula = A.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
       newline.fJustification = fAssJustification;
       newline.fLastassumption = true;

       fHead.add(insertIndex, newline);
       insertIndex += 1;

       newline = supplyProofline();
       newline.fLineno = 2003;
       newline.fFormula = notB.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel + 3;
       newline.fJustification = fAssJustification;
       newline.fLastassumption = true;

       fHead.add(insertIndex, newline);
       insertIndex += 1;

       newline = supplyProofline();
           newline.fLineno = 2004;
           newline.fFormula = TFormula.fAbsurd.copyFormula();
           newline.fSubprooflevel = templateLine.fSubprooflevel + 3;
           newline.fJustification = TProofController.absIJustification;
           newline.fFirstjustno = 2001;
           newline.fSecondjustno = 2002;

           fHead.add(insertIndex, newline);
           insertIndex += 1;


       newline = supplyProofline();
        newline.fLineno = 2004;
        newline.fSubprooflevel = templateLine.fSubprooflevel+2;
        newline.fBlankline = true;
        newline.fJustification = "";
        newline.fSelectable = false;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
           newline.fLineno = 2005;
           newline.fFormula = notnotB.copyFormula();
           newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
           newline.fJustification = TProofController.fNegIJustification;
           newline.fFirstjustno = 2004;

           fHead.add(insertIndex, newline);
           insertIndex += 1;

           newline = supplyProofline();
                     newline.fLineno = 2006;
                     newline.fFormula = B.copyFormula();
                     newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
                     newline.fJustification = TProofController.negEJustification;
                     newline.fFirstjustno = 2005;

                     fHead.add(insertIndex, newline);
           insertIndex += 1;

           newline = supplyProofline();
               newline.fLineno = 2006;
               newline.fSubprooflevel = templateLine.fSubprooflevel+1;
               newline.fBlankline = true;
               newline.fJustification = "";
               newline.fSelectable = false;

               fHead.add(insertIndex, newline);
               insertIndex += 1; //now at line after blankline

       newline = supplyProofline();
      newline.fLineno = 2007;
      newline.fFormula = AarrowB.copyFormula();  // A -> B
      newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
      newline.fJustification = fImplicIJustification;
      newline.fFirstjustno = 2006;

      fHead.add(insertIndex, newline);
      insertIndex += 1;



      newline = supplyProofline();
        newline.fLineno = 2008;
        newline.fFormula = TFormula.fAbsurd.copyFormula();
        newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
        newline.fJustification = TProofController.absIJustification;
        newline.fFirstjustno = 2007;
        newline.fSecondjustno = 1;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
        newline.fLineno = 2008;
        newline.fSubprooflevel = templateLine.fSubprooflevel;
        newline.fBlankline = true;
        newline.fJustification = "";
        newline.fSelectable = false;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
       newline.fLineno = 2009;
       newline.fFormula = notnotA.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel;
       newline.fJustification = TProofController.fNegIJustification;
       newline.fFirstjustno = 2008;

       fHead.add(insertIndex, newline);
       insertIndex += 1;


        TProofline nextLine=(TProofline)fHead.get(insertIndex);

        nextLine.fFirstjustno=2009;
        nextLine.fJustification=fNegEJustification;
        nextLine.fSubprooflevel=templateLine.fSubprooflevel;

        /*
           TProofline(localHead.At(insertHere)).fFirstjustno := 2009;
   TProofline(localHead.At(insertHere)).fJustification := ' ~E';


        */

  }

  void createLemma7WithoutAbsurd( int insertIndex){
/*
       { ~(A>B) as}
      {its first line and ~B as itsHead^.next the line after last assumption}
      {it proves the second from the first}


*/

  TProofline newline,templateLine,firstLine;

  firstLine=(TProofline)fHead.get(0);
  templateLine=(TProofline)fHead.get(insertIndex-1);

  TFormula notAarrowB = firstLine.fFormula;
  TFormula AarrowB = notAarrowB.fRLink;

  TFormula A = AarrowB.fLLink;
  TFormula B = AarrowB.fRLink;

  TFormula notA = new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                              null,
                              A);
 TFormula notB = new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                              null,
                              B);
 TFormula notnotA = new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                              null,
                              notA);

TFormula notnotB = new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                              null,
                              notB);




  newline = supplyProofline();
  newline.fLineno = 2001;
  newline.fFormula = notA.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fFormula = A.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2003;
  newline.fFormula = notB.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel + 3;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex, newline);
  insertIndex += 1;
/*
  newline = supplyProofline();
      newline.fLineno = 2004;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fSubprooflevel = templateLine.fSubprooflevel + 3;
      newline.fJustification = TProofController.absIJustification;
      newline.fFirstjustno = 2001;
      newline.fSecondjustno = 2002;

      fHead.add(insertIndex, newline);
      insertIndex += 1;  */


  newline = supplyProofline();
   newline.fLineno = 2003;
   newline.fSubprooflevel = templateLine.fSubprooflevel+2;
   newline.fBlankline = true;
   newline.fJustification = "";
   newline.fSelectable = false;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
      newline.fLineno = 2004;
      newline.fFormula = notnotB.copyFormula();
      newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
      newline.fJustification = TProofController.fNegIJustification;
      newline.fFirstjustno = 2001;
      newline.fSecondjustno = 2002;

      fHead.add(insertIndex, newline);
      insertIndex += 1;

      newline = supplyProofline();
                newline.fLineno = 2005;
                newline.fFormula = B.copyFormula();
                newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
                newline.fJustification = TProofController.negEJustification;
                newline.fFirstjustno = 2004;

                fHead.add(insertIndex, newline);
      insertIndex += 1;

      newline = supplyProofline();
          newline.fLineno = 2005;
          newline.fSubprooflevel = templateLine.fSubprooflevel+1;
          newline.fBlankline = true;
          newline.fJustification = "";
          newline.fSelectable = false;

          fHead.add(insertIndex, newline);
          insertIndex += 1; //now at line after blankline

  newline = supplyProofline();
 newline.fLineno = 2006;
 newline.fFormula = AarrowB.copyFormula();  // A -> B
 newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
 newline.fJustification = fImplicIJustification;
 newline.fFirstjustno = 2005;

 fHead.add(insertIndex, newline);
 insertIndex += 1;

/*

 newline = supplyProofline();
   newline.fLineno = 2008;
   newline.fFormula = TFormula.fAbsurd.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = TProofController.absIJustification;
   newline.fFirstjustno = 2007;
   newline.fSecondjustno = 1;

   fHead.add(insertIndex, newline);
   insertIndex += 1; */

   newline = supplyProofline();
   newline.fLineno = 2006;
   newline.fSubprooflevel = templateLine.fSubprooflevel;
   newline.fBlankline = true;
   newline.fJustification = "";
   newline.fSelectable = false;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
  newline.fLineno = 2007;
  newline.fFormula = notnotA.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel;
  newline.fJustification = TProofController.fNegIJustification;
  newline.fFirstjustno = 2006;
  newline.fSecondjustno = 1;

  fHead.add(insertIndex, newline);
  insertIndex += 1;


   TProofline nextLine=(TProofline)fHead.get(insertIndex);

   nextLine.fFirstjustno=2007;
   nextLine.fJustification=fNegEJustification;
   nextLine.fSubprooflevel=templateLine.fSubprooflevel;

   /*
      TProofline(localHead.At(insertHere)).fFirstjustno := 2009;
TProofline(localHead.At(insertHere)).fJustification := ' ~E';


   */

}


/*
    {$IFC useAbsurd}

  procedure CreateLemma7 (var localHead: Tlist; insertHere: integer);
  {The proof has ~(A>B) as}
  {its first line and A  itsHead^.next the line after last assumption}

   var
    newformulanode, tempFormula, formulaA, formulaB, notAarrowB: TFormula;
    newline, templateline: TProofline;

  begin

   formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
   formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;
   notAarrowB := TProofline(localHead.First).fFormula;

   templateline := TProofline(localHead.At(insertHere));

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   SupplyProofline(newline); {line 1 puts in ~A )}
   with newline do
    begin
     fLineno := 2001;
     fFormula := newformulanode;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := newformulanode; {not B}
     fJustification := 'Ass';
     fSubprooflevel := 3;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := gAbsurdFormula.CopyFormula; {not B}
     fJustification := ' AbsI';
     fFirstjustno := 2001;
     fSecondjustno := 2002;
     fSubprooflevel := 3;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2004;
     fSubprooflevel := 2; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula; {notB}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := newformulanode; {~~B}
     fJustification := ' ~I';
     fFirstjustno := 2004;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := formulaB.CopyFormula; {~A}
     fJustification := ' ~E';
     fFirstjustno := 2005;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2006;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := notAarrowB.fRlink.CopyFormula; {A�B}
     fJustification := ' �I';
     fFirstjustno := 2006;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2008;
     fFormula := gAbsurdFormula.CopyFormula; {A�B}
     fJustification := ' AbsI';
     fFirstjustno := 2007;
     fSecondjustno := 1;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2008;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula; {notA}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2009;
     fFormula := newformulanode; {doublenotA}
     fJustification := ' ~I';
     fFirstjustno := 2008;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   insertHere := insertHere + 1;

            {existing A}

   TProofline(localHead.At(insertHere)).fFirstjustno := 2009;
   TProofline(localHead.At(insertHere)).fJustification := ' ~E';

  end;

  {$ELSEC}

  procedure CreateLemma7 (var localHead: Tlist; insertHere: integer);
  {The proof has ~(A>B) as}
  {its first line and A  itsHead^.next the line after last assumption}

   var
    newformulanode, tempFormula, formulaA, formulaB, notAarrowB: TFormula;
    newline, templateline: TProofline;

  begin

   formulaA := TProofline(localHead.First).fFormula.fRlink.fLlink;
   formulaB := TProofline(localHead.First).fFormula.fRlink.fRlink;
   notAarrowB := TProofline(localHead.First).fFormula;

   templateline := TProofline(localHead.At(insertHere));

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   SupplyProofline(newline); {line 1 puts in ~A )}
   with newline do
    begin
     fLineno := 2001;
     fFormula := newformulanode;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := formulaA.CopyFormula; {A}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := newformulanode; {not B}
     fJustification := 'Ass';
     fSubprooflevel := 3;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2003;
     fSubprooflevel := 2; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaB.CopyFormula; {B}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not not B}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula; {notB}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := newformulanode; {~~B}
     fJustification := ' ~I';
     fFirstjustno := 2002;
     fSecondjustno := 2001;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := formulaB.CopyFormula; {~A}
     fJustification := ' ~E';
     fFirstjustno := 2004;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2005;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := notAarrowB.fRlink.CopyFormula; {A�B}
     fJustification := ' �I';
     fFirstjustno := 2005;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2006;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := formulaA.CopyFormula; {A}
    end;

   tempFormula := newformulanode;

   SupplyFormula(newformulanode);
   with newformulanode do {not not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula; {notA}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := newformulanode; {doublenotA}
     fJustification := ' ~I';
     fFirstjustno := 2006;
     fSecondjustno := 1;

     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   insertHere := insertHere + 1;

            {existing A}

   TProofline(localHead.At(insertHere)).fFirstjustno := 2007;
   TProofline(localHead.At(insertHere)).fJustification := ' ~E';

  end;

  {$ENDC}


  */

 void createLemma9(int insertIndex){
        if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/)
          createLemma9WithAbsurd(insertIndex);
        else
          createLemma9WithoutAbsurd(insertIndex);
      }

void createLemma9WithAbsurd(int insertIndex){
  /*
     {The proof has ~allxFx as}
     {its first line and Exn~Fx  itsHead^.next the line after last assumption}
   */

  TProofline newline, templateLine, firstLine;

  TProofline notAllLine = (TProofline) fHead.get(0);

  TProofline exNotLine = (TProofline) fHead.get(insertIndex + 1);


 TFormula notAllFx = notAllLine.fFormula;
  TFormula exNotFx = exNotLine.fFormula;
  TFormula notFx = exNotFx.scope();
  TFormula AllxFx = notAllFx.fRLink;
  TFormula Fx = AllxFx.fRLink;

  newline = supplyProofline();
newline.fLineno = 2001;
  newline.fFormula = new TFormula(TFormula.unary,
                                         String.valueOf(chNeg),
                                         null,
                                         exNotFx.copyFormula());

newline.fSubprooflevel = 1;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2002;
newline.fFormula = notFx.copyFormula();

newline.fSubprooflevel = 2;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

         newline = supplyProofline();
         newline.fLineno = 2003;
         newline.fFormula = exNotFx.copyFormula();

         newline.fSubprooflevel = 2;
         newline.fJustification = TProofController.EGJustification;
         newline.fFirstjustno=2002;

         fHead.add(insertIndex+1, newline);
insertIndex += 1;


         newline = supplyProofline();
             newline.fLineno = 2004;
             newline.fFormula = TFormula.fAbsurd.copyFormula();
             newline.fJustification = TProofController.absIJustification;
           newline.fSubprooflevel = 2;
             newline.fFirstjustno = 2003;
             newline.fSecondjustno=2001;

             fHead.add(insertIndex+1, newline);
           insertIndex += 1;

           newline = supplyProofline();
             newline.fLineno = 2004;
             newline.fBlankline = true;
             newline.fJustification = "";
           newline.fSubprooflevel = 1;
             newline.fSelectable = false;


             fHead.add(insertIndex+1, newline);
           insertIndex += 1;




           newline = supplyProofline();
             newline.fLineno = 2005;
             newline.fFormula = new TFormula (TFormula.unary,
                                          String.valueOf(chNeg),
                                          null,
                                          notFx.copyFormula()
                                         );  //~~Fx

             newline.fJustification = TProofController.fNegIJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2004;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
             newline.fLineno = 2006;
             newline.fFormula = Fx.copyFormula();

             newline.fJustification = TProofController.negEJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2005;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
           newline.fLineno = 2007;
           newline.fFormula = AllxFx.copyFormula();

           newline.fJustification = TProofController.UGJustification;
         newline.fSubprooflevel = 1;
           newline.fFirstjustno = 2006;

           fHead.add(insertIndex+1, newline);
insertIndex += 1;

           newline = supplyProofline();
    newline.fLineno = 2008;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fJustification = TProofController.absIJustification;
  newline.fSubprooflevel = 1;
    newline.fFirstjustno = 2007;
    newline.fSecondjustno=1;

    fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
    newline.fLineno = 2008;
    newline.fBlankline = true;
    newline.fJustification = "";
  newline.fSubprooflevel = 0;
    newline.fSelectable = false;


    fHead.add(insertIndex+1, newline);
  insertIndex += 1;




  newline = supplyProofline();
    newline.fLineno = 2009;
    newline.fFormula = new TFormula (TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 new TFormula(TFormula.unary,
                                         String.valueOf(chNeg),
                                         null,
                                         exNotFx.copyFormula())

                                );

    newline.fJustification = TProofController.fNegIJustification;
  newline.fSubprooflevel = 0;
    newline.fFirstjustno = 2008;

    fHead.add((insertIndex+1), newline);
insertIndex += 1;


    exNotLine.fFirstjustno=2009;   //existing line
    exNotLine.fJustification = TProofController.negEJustification;




}

  void createLemma9WithoutAbsurd(int insertIndex){
  /*
     {The proof has ~allxFx as}
     {its first line and Exn~Fx  itsHead^.next the line after last assumption}
   */

  TProofline newline, templateLine, firstLine;

  TProofline notAllLine = (TProofline) fHead.get(0);

  TProofline exNotLine = (TProofline) fHead.get(insertIndex + 1);


 TFormula notAllFx = notAllLine.fFormula;
  TFormula exNotFx = exNotLine.fFormula;
  TFormula notFx = exNotFx.scope();
  TFormula AllxFx = notAllFx.fRLink;
  TFormula Fx = AllxFx.fRLink;

  newline = supplyProofline();
newline.fLineno = 2001;
  newline.fFormula = new TFormula(TFormula.unary,
                                         String.valueOf(chNeg),
                                         null,
                                         exNotFx.copyFormula());

newline.fSubprooflevel = 1;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

newline = supplyProofline();
newline.fLineno = 2002;
newline.fFormula = notFx.copyFormula();

newline.fSubprooflevel = 2;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

         newline = supplyProofline();
         newline.fLineno = 2003;
         newline.fFormula = exNotFx.copyFormula();

         newline.fSubprooflevel = 2;
         newline.fJustification = TProofController.EGJustification;
         newline.fFirstjustno=2002;

         fHead.add(insertIndex+1, newline);
insertIndex += 1;

/*
         newline = supplyProofline();
             newline.fLineno = 2004;
             newline.fFormula = TFormula.fAbsurd.copyFormula();
             newline.fJustification = TProofController.absIJustification;
           newline.fSubprooflevel = 2;
             newline.fFirstjustno = 2003;
             newline.fSecondjustno=2001;

             fHead.add(insertIndex+1, newline);
           insertIndex += 1;  */

           newline = supplyProofline();
             newline.fLineno = 2003;
             newline.fBlankline = true;
             newline.fJustification = "";
           newline.fSubprooflevel = 1;
             newline.fSelectable = false;


             fHead.add(insertIndex+1, newline);
           insertIndex += 1;




           newline = supplyProofline();
             newline.fLineno = 2004;
             newline.fFormula = new TFormula (TFormula.unary,
                                          String.valueOf(chNeg),
                                          null,
                                          notFx.copyFormula()
                                         );  //~~Fx

             newline.fJustification = TProofController.fNegIJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2003;
             newline.fSecondjustno = 2001;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
             newline.fLineno = 2005;
             newline.fFormula = Fx.copyFormula();

             newline.fJustification = TProofController.negEJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2004;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
           newline.fLineno = 2006;
           newline.fFormula = AllxFx.copyFormula();

           newline.fJustification = TProofController.UGJustification;
         newline.fSubprooflevel = 1;
           newline.fFirstjustno = 2005;

           fHead.add(insertIndex+1, newline);
insertIndex += 1;
/*
           newline = supplyProofline();
    newline.fLineno = 2008;
    newline.fFormula = TFormula.fAbsurd.copyFormula();
    newline.fJustification = TProofController.absIJustification;
  newline.fSubprooflevel = 1;
    newline.fFirstjustno = 2007;
    newline.fSecondjustno=1;  */

    fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
    newline.fLineno = 2006;
    newline.fBlankline = true;
    newline.fJustification = "";
  newline.fSubprooflevel = 0;
    newline.fSelectable = false;


    fHead.add(insertIndex+1, newline);
  insertIndex += 1;




  newline = supplyProofline();
    newline.fLineno = 2007;
    newline.fFormula = new TFormula (TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 new TFormula(TFormula.unary,
                                         String.valueOf(chNeg),
                                         null,
                                         exNotFx.copyFormula())

                                );

    newline.fJustification = TProofController.fNegIJustification;
  newline.fSubprooflevel = 0;
    newline.fFirstjustno = 2006; // changed from 2007; June 2008
    newline.fSecondjustno = 1;

    fHead.add((insertIndex+1), newline);
insertIndex += 1;


    exNotLine.fFirstjustno=2007;   //existing line
    exNotLine.fJustification = TProofController.negEJustification;




}


 /*
  procedure CreateLemma9 (var localHead: Tlist; insertHere: integer);
  {The proof has ~allxFx as}
  {its first line and Exn~Fx  itsHead^.next the line after last assumption}

   var
    newformulanode, tempFormula, notAll, Exnot: TFormula;
    newline: TProofline;

  begin

   notAll := TProofline(localHead.First).fFormula;
   Exnot := TProofline(localHead.At(insertHere + 1)).fFormula;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.CopyFormula; {exnot}
    end;

   SupplyProofline(newline); {line 1 puts in notexnot}
   with newline do
    begin
     fLineno := 2001;
     fFormula := newformulanode;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := Exnot.fRlink.CopyFormula; {notFx}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := Exnot.CopyFormula; {B}
     fJustification := ' EG';
     fFirstjustno := 2002;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := gAbsurdFormula.CopyFormula; {B}
     fJustification := ' AbsI';
     fFirstjustno := 2003;
     fSecondjustno := 2001;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2004;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.fRlink.CopyFormula; {Fx}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := newformulanode; {notnotFx}
     fJustification := ' ~I';
     fFirstjustno := 2004;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := Exnot.fRlink.fRlink.CopyFormula; {Fx}
     fJustification := ' ~E';
     fFirstjustno := 2005;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := notAll.fRlink.CopyFormula; {allxF}
     fJustification := ' UG';
     fFirstjustno := 2006;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2008;
     fFormula := gAbsurdFormula.CopyFormula;
     fJustification := ' AbsI';
     fFirstjustno := 2007;
     fSecondjustno := 1;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2008;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(tempFormula);
   with tempFormula do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.CopyFormula;
    end;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula;
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2009;
     fFormula := newformulanode; {doublenotexnot}
     fJustification := ' ~I';
     fFirstjustno := 2008;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;
   insertHere := insertHere + 1;

            {existing exnot}
   TProofline(localHead.At(insertHere)).fFirstjustno := 2009;
   TProofline(localHead.At(insertHere)).fJustification := ' ~E';

  end;

  {$ELSEC}

  procedure CreateLemma9 (var localHead: Tlist; insertHere: integer);
  {The proof has ~allxFx as}
  {its first line and Exn~Fx  itsHead^.next the line after last assumption}

   var
    newformulanode, tempFormula, notAll, Exnot: TFormula;
    newline: TProofline;

  begin

   notAll := TProofline(localHead.First).fFormula;
   Exnot := TProofline(localHead.At(insertHere + 1)).fFormula;

   SupplyFormula(newformulanode);
   with newformulanode do {not A}
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.CopyFormula; {exnot}
    end;

   SupplyProofline(newline); {line 1 puts in notexnot}
   with newline do
    begin
     fLineno := 2001;
     fFormula := newformulanode;
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := Exnot.fRlink.CopyFormula; {notFx}
     fJustification := 'Ass';
     fSubprooflevel := 2;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := Exnot.CopyFormula; {B}
     fJustification := ' EG';
     fFirstjustno := 2002;
     fSubprooflevel := 2;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2003;
     fSubprooflevel := 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.fRlink.CopyFormula; {Fx}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := newformulanode; {notnotFx}
     fJustification := ' ~I';
     fFirstjustno := 2003;
     fSecondjustno := 2001;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2005;
     fFormula := Exnot.fRlink.fRlink.CopyFormula; {Fx}
     fJustification := ' ~E';
     fFirstjustno := 2004;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2006;
     fFormula := notAll.fRlink.CopyFormula; {allxF}
     fJustification := ' UG';
     fFirstjustno := 2005;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2006;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyFormula(tempFormula);
   with tempFormula do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := Exnot.CopyFormula;
    end;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := tempFormula;
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2007;
     fFormula := newformulanode; {doublenotexnot}
     fJustification := ' ~I';
     fFirstjustno := 2006;
     fSecondjustno := 1;

     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;
   insertHere := insertHere + 1;

            {existing exnot}
   TProofline(localHead.At(insertHere)).fFirstjustno := 2007;
   TProofline(localHead.At(insertHere)).fJustification := ' ~E';

end;

  */

 void createLemma10(int insertIndex){
        if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/)
          createLemma10WithAbsurd(insertIndex);
        else
          createLemma10WithoutAbsurd(insertIndex);
      }

void createLemma10WithAbsurd(int insertIndex){
  /*
     {The proof has ~ExFx as}
  {its first line and Allxn~Fx  itsHead.fnext the line after last assumption?}
  */

 TProofline newline,templateLine,firstLine;

firstLine=(TProofline)fHead.get(0);
//templateLine=(TProofline)fHead.get(insertIndex-1);
TProofline allNotLine=(TProofline)fHead.get(insertIndex+1); /*for some reason the Pascal has +1
it is called with the index of the last assumption*/

TFormula notExFx = firstLine.fFormula;
TFormula allxNotFx = allNotLine.fFormula;
TFormula notFx = allxNotFx.scope();
TFormula ExFx = notExFx.fRLink;
TFormula Fx = ExFx.fRLink;


newline = supplyProofline();
newline.fLineno = 2001;
newline.fFormula = Fx.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

 newline = supplyProofline();
newline.fLineno = 2002;
newline.fFormula = ExFx.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.EGJustification;
newline.fFirstjustno = 2001;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

 newline = supplyProofline();
newline.fLineno = 2003;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno = 2002;
newline.fSecondjustno = 1;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

 newline = supplyProofline();
newline.fLineno = 2003;
newline.fSubprooflevel = 0;
 newline.fBlankline = true;
     newline.fJustification = "";
    newline.fSelectable = false;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

    newline = supplyProofline();
   newline.fLineno = 2004;
   newline.fFormula = notFx.copyFormula();
   newline.fSubprooflevel = 0;
   newline.fJustification = TProofController.fNegIJustification;
   newline.fFirstjustno = 2003;


   fHead.add(insertIndex+1, newline);
insertIndex += 1;

   // the next line is the existing all not line that has been pushed down

   allNotLine.fSubprooflevel = 0;
   allNotLine.fJustification = TProofController.UGJustification;
   allNotLine.fFirstjustno = 2004;

   /*
    newline := TProofline(localHead.At(insertHere + 1)); {existing all x not line}

       with newline do
        begin
         fJustification := ' UG';
         fSubprooflevel := 0;
         fFirstjustno := 2004;
        end;
  end;

*/



}

  void createLemma10WithoutAbsurd(int insertIndex){
  /*
     {The proof has ~ExFx as}
  {its first line and Allxn~Fx  itsHead.fnext the line after last assumption?}
  */

 TProofline newline,templateLine,firstLine;

firstLine=(TProofline)fHead.get(0);
//templateLine=(TProofline)fHead.get(insertIndex-1);
TProofline allNotLine=(TProofline)fHead.get(insertIndex+1); /*for some reason the Pascal has +1
it is called with the index of the last assumption*/

TFormula notExFx = firstLine.fFormula;
TFormula allxNotFx = allNotLine.fFormula;
TFormula notFx = allxNotFx.scope();
TFormula ExFx = notExFx.fRLink;
TFormula Fx = ExFx.fRLink;


newline = supplyProofline();
newline.fLineno = 2001;
newline.fFormula = Fx.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = fAssJustification;
newline.fLastassumption = true;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

 newline = supplyProofline();
newline.fLineno = 2002;
newline.fFormula = ExFx.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.EGJustification;
newline.fFirstjustno = 2001;

fHead.add(insertIndex+1, newline);
insertIndex += 1;
/*
 newline = supplyProofline();
newline.fLineno = 2003;
newline.fFormula = TFormula.fAbsurd.copyFormula();
newline.fSubprooflevel = 1;
newline.fJustification = TProofController.absIJustification;
newline.fFirstjustno = 2002;
newline.fSecondjustno = 1;

fHead.add(insertIndex+1, newline);
insertIndex += 1;  */

 newline = supplyProofline();
newline.fLineno = 2002;
newline.fSubprooflevel = 0;
 newline.fBlankline = true;
     newline.fJustification = "";
    newline.fSelectable = false;

fHead.add(insertIndex+1, newline);
insertIndex += 1;

    newline = supplyProofline();
   newline.fLineno = 2003;
   newline.fFormula = notFx.copyFormula();
   newline.fSubprooflevel = 0;
   newline.fJustification = TProofController.fNegIJustification;
   newline.fFirstjustno = 2002;
   newline.fSecondjustno = 1;


   fHead.add(insertIndex+1, newline);
insertIndex += 1;

   // the next line is the existing all not line that has been pushed down

   allNotLine.fSubprooflevel = 0;
   allNotLine.fJustification = TProofController.UGJustification;
   allNotLine.fFirstjustno = 2003;

}

 /*
  {$IFC useAbsurd}

  procedure CreateLemma10 (var localHead: Tlist; insertHere: integer);
  {The proof has ~ExFx as}
  {its first line and Allxn~Fx  itsHead.fnext the line after last assumption}

   var
    notexFx, allxNot, tempFormula: TFormula;
    newline: TProofline;

  begin

   notexFx := TProofline(localHead.First).fFormula;
   allxNot := TProofline(localHead.At(insertHere + 1)).fFormula.CopyFormula; {copied}
   tempFormula := notexFx.fRlink.fRlink.CopyFormula; {fx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2001;
     fFormula := tempFormula; {fx}
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   tempFormula := notexFx.fRlink.CopyFormula; {exfx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := tempFormula; {exfx}
     fJustification := ' EG';
     fFirstjustno := 2001;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := gAbsurdFormula.CopyFormula; {exfx}
     fJustification := ' AbsI';
     fFirstjustno := 2002;
     fSecondjustno := 1;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2003;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   tempFormula := allxNot.fRlink.CopyFormula; {notfx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2004;
     fFormula := tempFormula; {notfx}
     fJustification := ' ~I';
     fFirstjustno := 2003;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   newline := TProofline(localHead.At(insertHere + 1)); {existing all x not line}

   with newline do
    begin
     fJustification := ' UG';
     fSubprooflevel := 0;
     fFirstjustno := 2004;
    end;
  end;

  {$ELSEC}

  procedure CreateLemma10 (var localHead: Tlist; insertHere: integer);
  {The proof has ~ExFx as}
  {its first line and Allxn~Fx  itsHead.fnext the line after last assumption}

   var
    notexFx, allxNot, tempFormula: TFormula;
    newline: TProofline;

  begin

   notexFx := TProofline(localHead.First).fFormula;
   allxNot := TProofline(localHead.At(insertHere + 1)).fFormula.CopyFormula; {copied}
   tempFormula := notexFx.fRlink.fRlink.CopyFormula; {fx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2001;
     fFormula := tempFormula; {fx}
     fJustification := 'Ass';
     fSubprooflevel := 1;
     fLastAssumption := true;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   tempFormula := notexFx.fRlink.CopyFormula; {exfx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2002;
     fFormula := tempFormula; {exfx}
     fJustification := ' EG';
     fFirstjustno := 2001;
     fSubprooflevel := 1;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := 2002;
     fSubprooflevel := 0; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   tempFormula := allxNot.fRlink.CopyFormula; {notfx}

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2003;
     fFormula := tempFormula; {notfx}
     fJustification := ' ~I';
     fFirstjustno := 2002;
     fSecondjustno := 1;
     fSubprooflevel := 0;
    end;

   localHead.InsertBefore(insertHere + 1, newline);
   insertHere := insertHere + 1;

   newline := TProofline(localHead.At(insertHere + 1)); {existing all x not line}

   with newline do
    begin
     fJustification := ' UG';
     fSubprooflevel := 0;
     fFirstjustno := 2003;
    end;
  end;

{$ENDC}

  */


 void createLemma11(int insertIndex){

  /*
        {The proof has ExFx as}
     {its first line and  EvFv  for a different variable as itsHead.fnext the line after last assumption}
   */



   TProofline newline,templateLine,firstLine;

  firstLine=(TProofline)fHead.get(0);

  TProofline evFvLine=(TProofline)fHead.get(insertIndex+1); /*for some reason the Pascal has +1
  it is called with the index of the last assumption*/

  TFormula exFx = firstLine.fFormula;
  TFormula evFv = evFvLine.fFormula;
  TFormula scope = exFx.fRLink;


  newline = supplyProofline();
  newline.fLineno = 2001;
  newline.fFormula = scope.copyFormula();
  newline.fSubprooflevel = 1;
  newline.fJustification = fAssJustification;
  newline.fLastassumption = true;

  fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fFormula = evFv.copyFormula();
  newline.fSubprooflevel = 1;
  newline.fJustification = TProofController.EGJustification;
  newline.fFirstjustno = 2001;

  fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  newline = supplyProofline();
  newline.fLineno = 2002;
  newline.fSubprooflevel = 0;
  newline.fBlankline=true;
  newline.fJustification = "";
  newline.fSelectable = false;

  fHead.add(insertIndex+1, newline);
  insertIndex += 1;

  //next line is evFv line that has been pushed down

  evFvLine.fJustification=TProofController.fEIJustification;
  evFvLine.fSubprooflevel=0;
  evFvLine.fFirstjustno=1;
  evFvLine.fSecondjustno=2002;

     }


/*

       procedure CreateLemma11 (var localHead: Tlist; insertHere: integer);
     {The proof has ExFx as}
     {its first line and  EvFv  for a different variable as itsHead.fnext the line after last assumption}

      var
       exFx, evFv: TFormula;
       newline: TProofline;

     begin

      exFx := TProofline(localHead.First).fFormula;
      evFv := TProofline(localHead.At(insertHere + 1)).fFormula.CopyFormula; {copied}
      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2001;
        fFormula := exFx.fRlink.CopyFormula;
        fJustification := 'Ass';
        fSubprooflevel := 1;
        fLastAssumption := true;
       end;

      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;
      SupplyProofline(newline);
      with newline do
       begin
        fLineno := 2002;
        fFormula := evFv;
        fJustification := ' EG';
        fFirstjustno := 2001;
        fSubprooflevel := 1;
       end;
      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;
      SupplyProofline(newline); {newline points to new proofline}
      with newline do
       begin
        fLineno := 2002;
        fSubprooflevel := 0; {checkthis}
        fBlankline := true;
        fJustification := '';
        fSelectable := false;
       end;
      localHead.InsertBefore(insertHere + 1, newline);
      insertHere := insertHere + 1;
      newline := TProofline(localHead.At(insertHere + 1)); {existing EvFv line}

      with newline do
       begin
        fJustification := ' EI';
        fSubprooflevel := 0;
        fFirstjustno := 1; {1000;}
        fSecondjustno := 2002;
       end;
     end;


  */



class OreS{
 TTestNode proveDisjunctTest;


void doOreS(TGWTReAssemble leftReAss){

  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;  //{proof of B from not A}

  TFormula notAFormula=(TFormula)((fTestNode.getLeftChild()).fAntecedents.get(0));   //not A
  TFormula orFormula=(TFormula)(fTestNode.fSuccedent.get(0));       //A or B
  TFormula A=orFormula.fLLink.copyFormula();
  TFormula B=orFormula.fRLink.copyFormula();


   if (canProveDisjunct(A)||canProveDisjunct(B))
      {
      optimizeOr(proveDisjunctTest,orFormula);
   }
   else{
     if (!transfer(TTestNode.atomic, notAFormula)) { //
       prependToHead(notAFormula);
       if (transfer(TTestNode.atomic, notAFormula))
         numberLines();
     } // not A

     createLemma1A(orFormula);
     numberLines();
   }
}

/*
  begin {we have not A at head and B succeedent}

        DismantleTestTree(temptest); {to here}

        if not Transfer(atomic, notAformula, localHead, lastAssumption) then
         begin
          AddtoHead(notAformula, localHead, lastAssumption);
          if Transfer(atomic, notAformula, localHead, lastAssumption) then
          NumberLines(localHead);

         end; {notA}

        CreateLemma1A(orFormula, localHead, lastAssumption);

        NumberLines(localHead);

     end;

*/

boolean canProveDisjunct(TFormula aFormula){

  //{This is a test of whether we can prove aFormula from the rest of the premises}

  proveDisjunctTest=fTestNode.copyNodeInFullWithInstInfo();

  proveDisjunctTest.fStepType=TTestNode.unknown;
  proveDisjunctTest.fDead=false; // reset for new test
  proveDisjunctTest.fClosed=false;
 // we are going to put this into a new Tree Model so what used to be the l and r links are null


  proveDisjunctTest.fSuccedent.remove(0);

  proveDisjunctTest.fSuccedent.add(0,aFormula.copyFormula());  //aFormula is now consequent

   TTreeModel aTreeModel= new TTreeModel(proveDisjunctTest.fSwingTreeNode);

   return
       (proveDisjunctTest.treeValid(aTreeModel,((TProofline)fHead.get(fHead.size()-1)).fLineno+6)==TTestNode.valid);

}

/*

 procedure AssembleFirstTest;
       {This is a test of whether we can prove A from the rest of the premises}

   begin
    temptest := nil;
    temptest := thisnode.CopyNodeinFullWithInstInfo;

    with temptest do
     begin
      fSteptype := unknown;
      fDead := false; {must reset it for a new test}
      fClosed := false;
      fLlink := nil;
      fRlink := nil;
     end;

    aFormula := TFormula(temptest.fsucceedent.First).fLlink.CopyFormula;
    TFormula(temptest.fsucceedent.First).DismantleFormula;
    temptest.fsucceedent.delete(temptest.fsucceedent.First);
    temptest.fsucceedent.InsertFirst(aFormula); {makes A consequent}

   end;


*/

}

  void optimizeOr(TTestNode tempTest,TFormula orFormula){

    TGWTReAssemble tempReAss=supplyTReAssemble(tempTest,null,0);

            tempReAss.reAssembleProof();

            fHead=tempReAss.fHead;
            fLastAssIndex=tempReAss.fLastAssIndex;  //{proof of A} or B, depending

            TProofline lastLine=(TProofline)fHead.get(fHead.size()-1);


            TProofline newline= supplyProofline();
            newline.fLineno=lastLine.fLineno+1;
            newline.fFormula=orFormula.copyFormula();  // A v B
            newline.fHeadlevel=lastLine.fHeadlevel;
            newline.fSubprooflevel=lastLine.fSubprooflevel;
            newline.fFirstjustno=lastLine.fLineno;
            newline.fJustification=fOrIJustification;


            fHead.add(newline);
}

          /*
      procedure Optimize1;

        begin

         DismantleProofList(localHead);

         localHead := nil;
         lastAssumption := 0;

         ReAssembleProof(temptest, localHead, lastAssumption);

         SupplyProofline(newline);
         with newline do
          begin
           fLineno := TProofline(localHead.Last).fLineno + 1;
           fFormula := orFormula.CopyFormula;
           fSubprooflevel := TProofline(localHead.Last).fSubprooflevel;
           fFirstjustno := TProofline(localHead.Last).fLineno;
           fJustification := ' �I';
          end;

         localHead.InsertLast(newline);

         DismantleTestTree(temptest); {new}
        end;

*/


///////////////////////

/*
 procedure DoOreS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   temptest: TTestNode;
   newline: TProofline;
   aFormula, notAformula, orFormula: TFormula;

  procedure Optimize1;

  begin

   DismantleProofList(localHead);

   localHead := nil;
   lastAssumption := 0;

   ReAssembleProof(temptest, localHead, lastAssumption);

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := TProofline(localHead.Last).fLineno + 1;
     fFormula := orFormula.CopyFormula;
     fSubprooflevel := TProofline(localHead.Last).fSubprooflevel;
     fFirstjustno := TProofline(localHead.Last).fLineno;
     fJustification := ' �I';
    end;

   localHead.InsertLast(newline);

   DismantleTestTree(temptest); {new}
  end;

  procedure AssembleFirstTest;
      {This is a test of whether we can prove A from the rest of the premises}

  begin
   temptest := nil;
   temptest := thisnode.CopyNodeinFullWithInstInfo;

   with temptest do
    begin
     fSteptype := unknown;
     fDead := false; {must reset it for a new test}
     fClosed := false;
     fLlink := nil;
     fRlink := nil;
    end;

   aFormula := TFormula(temptest.fsucceedent.First).fLlink.CopyFormula;
   TFormula(temptest.fsucceedent.First).DismantleFormula;
   temptest.fsucceedent.delete(temptest.fsucceedent.First);
   temptest.fsucceedent.InsertFirst(aFormula); {makes A consequent}

  end;

  procedure AssembleSecondTest;
      {This is a test of whether we can prove B from the rest of the premises}

  begin
   temptest := nil;
   temptest := thisnode.CopyNodeinFullWithInstInfo;

   with temptest do
    begin
     fSteptype := unknown;
     fDead := false; {must reset it for a new test}
     fClosed := false;
     fLlink := nil;
     fRlink := nil;
    end;

   aFormula := TFormula(temptest.fsucceedent.First).fRlink.CopyFormula;
   TFormula(temptest.fsucceedent.First).DismantleFormula;
   temptest.fsucceedent.delete(temptest.fsucceedent.First);
   temptest.fsucceedent.InsertFirst(aFormula); {makes B consequent}
  end;

 begin

  ReAssembleProof(thisnode.fLlink, localHead, lastAssumption);

           {proof of B from not A}

  orFormula := TFormula(thisnode.fsucceedent.First);
  notAformula := TFormula(thisnode.fLlink.fantecedents.First);

  AssembleFirstTest;

           {InitStringStore;}

  if temptest.TreeValid(TProofline(localHead.Last).fLineno + 6) = valid then
   Optimize1
  else
   begin

    DismantleTestTree(temptest);

    AssembleSecondTest;

                {InitStringStore;}

    if temptest.TreeValid(TProofline(localHead.Last).fLineno + 6) = valid then
     Optimize1
    else
     begin {we have not A at head and B succeedent}

      DismantleTestTree(temptest); {to here}

      if not Transfer(atomic, notAformula, localHead, lastAssumption) then
       begin
        AddtoHead(notAformula, localHead, lastAssumption);
        if Transfer(atomic, notAformula, localHead, lastAssumption) then
        NumberLines(localHead);

       end; {notA}

      CreateLemma1A(orFormula, localHead, lastAssumption);

      NumberLines(localHead);

     end;

   end;
 end;


*/

void doNegImplic(TGWTReAssemble leftReAss){

  TFormula A = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(
      0));
  TFormula notB = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(
      1));

  fHead = leftReAss.fHead;
  fLastAssIndex = leftReAss.fLastAssIndex;

  int dummy1 = TGWTMergeData.inPremises(fTestNode, fHead,
                                     fLastAssIndex, A); //not sure
  int dummy2 = TGWTMergeData.inPremises(fTestNode, fHead,
                                     fLastAssIndex, notB);
  if ( (dummy1 != -1) || (dummy2 != -1)) {

      TFormula formulanode=new TFormula(TFormula.unary,
                                       String.valueOf(chNeg),
                                       null,
                                       new TFormula(TFormula.binary,
                                       String.valueOf(chImplic),
                                       A,
                                       notB.fRLink));

      prependToHead(formulanode);  //nore

      if (transfer(TTestNode.atomic, notB)){
        numberLines();
        createLemma6(fLastAssIndex+1);
        numberLines();
      } //{Moves the right disjunct it justifies into body of proof}

      if (transfer(TTestNode.atomic, A)){
        numberLines();
        createLemma7(fLastAssIndex+1);
        numberLines();
      }






   }

}

/*

 procedure DoNegArrow (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   newformulanode, formulaA, formulanotB: TFormula;
   dummy: integer;

  procedure AddNegArrow (var localHead: Tlist; var lastAssumption: integer);

   var
    temp: TFormula;

  begin
   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chImplic;
     fLlink := formulaA;
     fRlink := formulanotB.fRlink;
    end;

   temp := newformulanode;
   newformulanode := nil;

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := temp;
    end;

   AddtoHead(newformulanode, localHead, lastAssumption); {nore}

   temp.fRlink := nil;
   temp.fLlink := nil;
   newformulanode.DismantleFormula;
  end;

 begin
  formulaA := TFormula(thisnode.fLlink.fantecedents.First); { A}
  formulanotB := TFormula(thisnode.fLlink.fantecedents.At(2)); {notB}

  if InPremises(thisnode, localHead, lastAssumption, formulaA, dummy) or InPremises(thisnode, localHead, lastAssumption, formulanotB, dummy) then
   begin

    AddNegArrow(localHead, lastAssumption);

    if Transfer(atomic, formulanotB, localHead, lastAssumption) then
     begin
      NumberLines(localHead);

      CreateLemma6(localHead, lastAssumption);

      NumberLines(localHead);

     end;

                {Moves the neg of then clause it justifies into body of proof}

    if Transfer(atomic, formulaA, localHead, lastAssumption) then
     begin
      NumberLines(localHead);

      CreateLemma7(localHead, lastAssumption);

      NumberLines(localHead);

     end;

                {Moves the if clause justifies into body of proof}

   end;
end;


*/

void doNore(TGWTReAssemble leftReAss){

   TFormula leftFormula = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(0));
   TFormula rightFormula = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(1));

   fHead = leftReAss.fHead;
   fLastAssIndex = leftReAss.fLastAssIndex;

   int dummy1 = TGWTMergeData.inPremises(fTestNode, fHead,
                                       fLastAssIndex, leftFormula); //not sure
   int dummy2 = TGWTMergeData.inPremises(fTestNode, fHead,
                                       fLastAssIndex, rightFormula);

   if ( (dummy1 != -1) || (dummy2 != -1)) {

      TFormula formulanode=new TFormula(TFormula.unary,
                                       String.valueOf(chNeg),
                                       null,
                                       new TFormula(TFormula.binary,
                                       String.valueOf(chOr),
                                       leftFormula.fRLink,
                                       rightFormula.fRLink));

      prependToHead(formulanode);  //nore

      if (transfer(TTestNode.atomic, rightFormula)){
        numberLines();
        createLemma5(rightFormula,fLastAssIndex+1);
        numberLines();
      } //{Moves the right disjunct it justifies into body of proof}

      if (transfer(TTestNode.atomic, leftFormula)){
        numberLines();
        createLemma5(leftFormula,fLastAssIndex+1);
        numberLines();
      }
   }
}

/*
 procedure DoNore (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

 var
  newformulanode, leftFormula, rightFormula: TFormula;
  dummy: integer;

 procedure AddNore (var localHead: Tlist; var lastAssumption: integer);

  var
   temp: TFormula;

 begin
  SupplyFormula(temp);
  with temp do
   begin
    fKind := Binary;
    fInfo := chOr;
    fLlink := leftFormula.fRlink;
    fRlink := rightFormula.fRlink;
   end;

  SupplyFormula(newformulanode);
  with newformulanode do
   begin
    fKind := Unary;
    fInfo := chNeg;
    fRlink := temp;
   end;

  AddtoHead(newformulanode, localHead, lastAssumption); {nore}

  temp.fLlink := nil;
  temp.fRlink := nil;
  newformulanode.DismantleFormula;
 end;

begin
 leftFormula := TFormula(thisnode.fLlink.fantecedents.First);
 rightFormula := TFormula(thisnode.fLlink.fantecedents.At(2));

 if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy)
 or InPremises(thisnode, localHead, lastAssumption, rightFormula, dummy) then
  begin

   AddNore(localHead, lastAssumption);

   if Transfer(atomic, rightFormula, localHead, lastAssumption) then
    begin
     NumberLines(localHead);

     CreateLemma5(localHead, rightFormula, lastAssumption);

     NumberLines(localHead);

    end;

               {Moves the right disjunct it justifies into body of proof}

   if Transfer(atomic, leftFormula, localHead, lastAssumption) then
    begin
     NumberLines(localHead);

     CreateLemma5(localHead, leftFormula, lastAssumption);

     NumberLines(localHead);

    end;

               {Moves the left disjunct it justifies into body of proof}

  end;
end;


*/


void doOre(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss){
    TFormula leftFormula, rightFormula;
    TProofline lastProofLine, firstConc, secondConc;
    int firstTailIndex,secondTailIndex;

    leftFormula = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(0)); //A
    rightFormula = (TFormula) (fTestNode.getRightChild().fAntecedents.get(0)); //B

    firstConc = (TProofline) leftReAss.fHead.get(leftReAss.fHead.size() - 1);
    secondConc = (TProofline) rightReAss.fHead.get(rightReAss.fHead.size() - 1);

    int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,            //tells if left formula used
                                       leftReAss.fLastAssIndex, leftFormula); //not sure
    int dummy2 = TGWTMergeData.inPremises(fTestNode, rightReAss.fHead,
                                       rightReAss.fLastAssIndex, rightFormula);

    if ( (dummy1 != -1) && (dummy2 != -1)) {        //both used
      TFormula formulanode = new TFormula();  //AvB

      formulanode.fKind = TFormula.binary;
      formulanode.fInfo = String.valueOf(chOr);
      formulanode.fLLink = leftFormula;
      formulanode.fRLink = rightFormula;

      leftReAss.prependToHead(formulanode);
      rightReAss.prependToHead(formulanode);

      if (leftReAss.transfer(TTestNode.atomic, leftFormula)) //
        ;
      leftReAss.numberLines();

      if (rightReAss.transfer(TTestNode.atomic, rightFormula))
        ;

      rightReAss.numberLines();

      leftReAss.convertToSubProof();
      rightReAss.convertToSubProof();

      /*
          AddOre(localHead, lastAssumption);
           AddOre(rightHead, rightlastAss);
           if Transfer(atomic, leftFormula, localHead, lastAssumption) then
            ;

           NumberLines(localHead);

           if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
            ;

           NumberLines(rightHead);

           ConvertToSubProof(localHead, lastAssumption);
           ConvertToSubProof(rightHead, rightlastAss);


   */

      TGWTMergeData mergeData = new TGWTMergeData(leftReAss, rightReAss);

      mergeData.merge();

      fHead = mergeData.firstLocalHead;
      fLastAssIndex = mergeData.firstLastAssIndex;

      numberLines();

      firstTailIndex=fHead.indexOf(firstConc);
      secondTailIndex=fHead.indexOf(secondConc);

      addOrConc(firstTailIndex,secondTailIndex,1);

      /*
          Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

           NumberLines(localHead);

           firstTail := localHead.GetSameItemNo(firstconc);
           secondTail := localHead.GetSameItemNo(secondconc);

           AddOrConc(localHead, firstTail, secondTail, 1);


   */

    }
    else{

     /*This step can be redundant for example if we come in with
      F
      F^F

      for both sides, there is no need to add (GvH)

      G
      F
      F^F

      H
      F
      F^F
      */

     /*w, */

      if (dummy1 != -1){     //changed Nov 06 from dummy2
        fHead=rightReAss.fHead;
        fLastAssIndex=rightReAss.fLastAssIndex;  //{we'll go with the right leg}
      }
      else{
        fHead=leftReAss.fHead;
        fLastAssIndex=leftReAss.fLastAssIndex;  //{proof of B from not A}
      }



    }
  }
/*
    procedure DoOre (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer; var rightHead: Tlist; var rightlastAss: integer);

  var
   firstno, secondno, dummy, firstTail, secondTail: integer;
   firstconc, secondconc: TProofline;
   newformulanode, leftFormula, rightFormula: TFormula;

  procedure AddOre (var localHead: Tlist; var lastAssumption: integer);

  begin
   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chOr;
     fLlink := leftFormula;
     fRlink := rightFormula;
    end;
   AddtoHead(newformulanode, localHead, lastAssumption); {(AVB)}
   newformulanode.fLlink := nil;
   newformulanode.fRlink := nil;
   newformulanode.DismantleFormula;
  end;

 begin
  leftFormula := TFormula(thisnode.fLlink.fantecedents.First); {A}
  rightFormula := TFormula(thisnode.fRlink.fantecedents.First); {B}

  firstconc := TProofline(localHead.Last);
  secondconc := TProofline(rightHead.Last); {helps with the orconc merge later}

  if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
   begin
    AddOre(localHead, lastAssumption);
    AddOre(rightHead, rightlastAss);
    if Transfer(atomic, leftFormula, localHead, lastAssumption) then
     ;

    NumberLines(localHead);

    if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
     ;

    NumberLines(rightHead);

    ConvertToSubProof(localHead, lastAssumption);
    ConvertToSubProof(rightHead, rightlastAss);

    Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

    NumberLines(localHead);

    firstTail := localHead.GetSameItemNo(firstconc);
    secondTail := localHead.GetSameItemNo(secondconc);

    AddOrConc(localHead, firstTail, secondTail, 1);

   end
  else
   begin
    if not InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
     SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss);

    DismantleProofList(rightHead); {step redundant}
   end;
 end;


*/

void addOrConc(int firstTailIndex,int secondTailIndex,int orJustNum){
  /*
     {thisnode : testptr;varfirstTail, secondTail : lineptr;ornum : integer)}
   {this takes the conclusions of two subproofs and prepares a joint conclusion from }
   {them both. There are three cases: normal, the case where the conclusion is a dummy
   which happens with a reductio proof}
   {and the case where there are two different dummies (reductio)}

  The tails here are followed by blanklines because they are at the end of subproofs


   */



 class PerhapsBreakItsDummy{

   int secondTailIndex;

   PerhapsBreakItsDummy(int index){
     secondTailIndex=index;

   }

   boolean doIt(){
     TProofline templateline = (TProofline) (fHead.get(secondTailIndex - 1));

     if (templateline.fBlankline){
       /*
        {this is the case where the second dummy has itself been proved by a subproof}
               {and so needs breaking}

       */

      templateline = (TProofline) (fHead.get(secondTailIndex));  // now pointing at conclusion (B^~B) say

      TProofline newline=supplyProofline();

      newline.fLineno = templateline.fLineno+1;
      newline.fSubprooflevel= templateline.fSubprooflevel;  //check?
      newline.fFormula = templateline.getFormula().getLLink().copyFormula();  // B
      newline.fJustification = TProofController.fAndEJustification;
      newline.fFirstjustno=templateline.fLineno;


      secondTailIndex+=1;
      fHead.add(secondTailIndex,newline);

      newline=supplyProofline();

      newline.fLineno = templateline.fLineno+2;
      newline.fSubprooflevel= templateline.fSubprooflevel;  //check?
      newline.fFormula = templateline.getFormula().getRLink().copyFormula();  // ~B
      newline.fJustification = TProofController.fAndEJustification;
      newline.fFirstjustno=templateline.fLineno;


      secondTailIndex+=1;
      fHead.add(secondTailIndex,newline);

      newline=supplyProofline();

      newline.fLineno = templateline.fLineno+3;
      newline.fSubprooflevel= templateline.fSubprooflevel;
      newline.fFormula = templateline.getFormula().copyFormula();  // B^~B
      newline.fJustification = TProofController.fAndIJustification;
      newline.fFirstjustno=templateline.fLineno+1;
      newline.fSecondjustno=templateline.fLineno+2;


      secondTailIndex+=1;
      fHead.add(secondTailIndex,newline);

      return
          true;

     }
     return
         false;
   }
 }



/*************
  procedure PerhapsBreakItsDummy;
     {The dummy contradiction has to be removed and its just nos used}
     {but sometime it occurs directly after a subproof in  which case it}
     {has to be split up and put together again first}

  begin
   templateline := TProofline(localHead.At(secondTail - 1));

   if templateline.fBlankline then

               {this is the case where the second dummy has itself been proved by a subproof}
               {and so needs breaking}
    begin

     templateline := TProofline(localHead.At(secondTail)); {template}

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 1;
       fFormula := secondTailFormula.fLlink.CopyFormula; {A}
       fJustification := ' ^E';
       fFirstjustno := templateline.fLineno;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 2;
       fFormula := secondTailFormula.fRlink.CopyFormula; {~A}
       fJustification := ' ^E';
       fFirstjustno := templateline.fLineno;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 3;
       fFormula := secondTailFormula.CopyFormula; {A^~A}
       fJustification := ' ^I';
       fFirstjustno := templateline.fLineno + 1;
       fSecondjustno := templateline.fLineno + 2;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

    end;
  end;
**********************/



















  TFormula firstTailFormula =  ((TProofline)(fHead.get(firstTailIndex))).fFormula;
  TFormula secondTailFormula =  ((TProofline)(fHead.get(secondTailIndex))).fFormula;


  if (!TFormula.equalFormulas(firstTailFormula, secondTailFormula)){
    //  {really funny. This is the case where there are two different dummies}
    //NEEDS WRITING

    PerhapsBreakItsDummy procedure= new PerhapsBreakItsDummy(secondTailIndex);

    if (procedure.doIt())
      secondTailIndex+=3;      // adds 3 lines

    TProofline templateline = (TProofline) (fHead.get(secondTailIndex));  /* now pointing at conclusion (B^~B) say
    eventually we will not need this line, but we do need its just nos. So we will change it for now, and zero
    its just nos later. Surgery*/

    TFormula contra=firstTailFormula.copyFormula();

    TFormula notContra=new TFormula(TFormula.unary,
                                   String.valueOf(chNeg),
                                   null,
                                   contra.copyFormula());  //~(A^~A)

    TFormula notnotContra=new TFormula(TFormula.unary,
                               String.valueOf(chNeg),
                               null,
                               notContra.copyFormula());

   int subprooflevel=templateline.fSubprooflevel;


   templateline.fSubprooflevel= subprooflevel+ 1;  //surgery on template line
   templateline.fFormula = notContra;  //~(A^~A)
   templateline.fJustification = fAssJustification;
   templateline.fLastassumption = true;

   TProofline newline=supplyProofline();

   newline.fLineno = templateline.fLineno;
   newline.fSubprooflevel= subprooflevel;
   newline.fBlankline = true;  //~(A^~A)
   newline.fSelectable = false;

   secondTailIndex+=1;
   fHead.add(secondTailIndex,newline);

   newline=supplyProofline();

   newline.fLineno = templateline.fLineno+1;
   newline.fSubprooflevel= subprooflevel;  //check?
   newline.fFormula = notnotContra;  //~~(A^~A)
   newline.fJustification = fNegIJustification;
   newline.fFirstjustno = templateline.fFirstjustno;       //using template lines justnos for B and ~B
   newline.fSecondjustno = templateline.fSecondjustno;

   secondTailIndex+=1;
   fHead.add(secondTailIndex,newline);

   newline=supplyProofline();

   newline.fLineno = templateline.fLineno+2;
   newline.fSubprooflevel= subprooflevel;  //check?
   newline.fFormula = contra;  //(A^~A)
   newline.fJustification = fNegEJustification;
   newline.fFirstjustno = templateline.fLineno+1;

   secondTailIndex+=1;
   fHead.add(secondTailIndex,newline);

   templateline.fFirstjustno=0;       //surgery has made template line an assumption, setting justnos to 0
   templateline.fSecondjustno=0;

  }

    TProofline newline=supplyProofline();

    newline.fLineno = ((TProofline)(fHead.get(secondTailIndex))).fLineno+1;
    newline.fSubprooflevel= ((TProofline)(fHead.get(secondTailIndex))).fSubprooflevel-1;  //check?
    newline.fFormula = firstTailFormula.copyFormula();
    newline.fJustification = TProofController.fOrEJustification;
    newline.fFirstjustno=orJustNum;
    newline.fSecondjustno=((TProofline)(fHead.get(firstTailIndex))).fLineno;
    newline.fThirdjustno=((TProofline)(fHead.get(secondTailIndex))).fLineno;

    secondTailIndex+=1; //now pointing at blank linw
    fHead.add(secondTailIndex+1,newline);
                           //do I need to increment secondTailIndex



        /*
         {This adds the conclusion}

           SupplyProofline(newline);

           with newline do
            begin
             fLineno := TProofline(localHead.At(secondTail)).fLineno + 1;
             fSubprooflevel := TProofline(localHead.At(secondTail)).fSubprooflevel - 1; {checkthis}

             fFormula := firstTailFormula.CopyFormula;
             fJustification := ' ~E';

             fFirstjustno := ornum;
             fSecondjustno := TProofline(localHead.At(firstTail)).fLineno;
             fthirdjustno := TProofline(localHead.At(secondTail)).fLineno;
             fJustification := ' �E';
            end;

           secondTail := secondTail + 1; {now at blank}

           localHead.InsertBefore(secondTail + 1, newline);
           secondTail := secondTail + 1;

*/

}

/*
  procedure AddOrConc (var localHead: TList; var firstTail, secondTail: integer; ornum: integer);

{thisnode : testptr;varfirstTail, secondTail : lineptr;ornum : integer)}
{this takes the conclusions of two subproofs and prepares a joint conclusion from }
{them both. There are three cases: normal, the case where the conclusion is a dummy}
{and the case where there are two different dummies}

  var
   firstTailFormula, secondTailFormula, newformulanode: TFormula;
   newline, templateline: TProofline;
*************
  procedure PerhapsBreakItsDummy;
     {The dummy contradiction has to be removed and its just nos used}
     {but sometime it occurs directly after a subproof in  which case it}
     {has to be split up and put together again first}

  begin
   templateline := TProofline(localHead.At(secondTail - 1));

   if templateline.fBlankline then

               {this is the case where the second dummy has itself been proved by a subproof}
               {and so needs breaking}
    begin

     templateline := TProofline(localHead.At(secondTail)); {template}

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 1;
       fFormula := secondTailFormula.fLlink.CopyFormula; {A}
       fJustification := ' ^E';
       fFirstjustno := templateline.fLineno;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 2;
       fFormula := secondTailFormula.fRlink.CopyFormula; {~A}
       fJustification := ' ^E';
       fFirstjustno := templateline.fLineno;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 3;
       fFormula := secondTailFormula.CopyFormula; {A^~A}
       fJustification := ' ^I';
       fFirstjustno := templateline.fLineno + 1;
       fSecondjustno := templateline.fLineno + 2;
       fSubprooflevel := templateline.fSubprooflevel;
      end;

     localHead.InsertBefore(secondTail + 1, newline);
     secondTail := secondTail + 1;

    end;
  end;
**********************
 begin
  firstTailFormula := TProofline(localHead.At(firstTail)).fFormula;
  secondTailFormula := TProofline(localHead.At(secondTail)).fFormula;

  if not EqualFormulas(firstTailFormula, secondTailFormula) then
          {really funny. This is the case where there are two different dummies}
   begin1 {add more lines to make the two contradictions the same}

    PerhapsBreakItsDummy;

   {secondTail.fnext.fnext.fthirdjustno := secondTail.fnext.fnext.fthirdjustno + 3; this adds lines}
               {changed from 3 to 2 may 25 as only two new lines added}

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := firstTailFormula.CopyFormula;
                    {change second contradiction into negation of first}
     end;

    templateline := TProofline(localHead.At(secondTail));

    with templateline do {will have to set justnos to zero later}
     begin
      fFormula := newformulanode;
      fJustification := 'Ass';
      fSubprooflevel := templateline.fSubprooflevel + 1;
      fLastAssumption := true;
     end;
    newformulanode := nil;

    SupplyProofline(newline); {newline points to new proofline}

    with newline do
     begin
      fLineno := templateline.fLineno;
      fSubprooflevel := templateline.fSubprooflevel - 1; {checkthis}
      fBlankline := true;
      fJustification := '';
      fSelectable := false;
     end;

    localHead.InsertBefore(secondTail + 1, newline);
    secondTail := secondTail + 1;

               {secondTail point at blankline }

    SupplyFormula(newformulanode); { doubleneg for next line}
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := templateline.fFormula.CopyFormula;
     end;

    SupplyProofline(newline); {doubleneg}

    with newline do
     begin
      fLineno := templateline.fLineno + 1;
      fSubprooflevel := templateline.fSubprooflevel - 1; {checkthis}

      fFormula := newformulanode;
      fJustification := ' ~I';
      fFirstjustno := templateline.fFirstjustno;
      fSecondjustno := templateline.fSecondjustno;
     end;

    localHead.InsertBefore(secondTail + 1, newline);
    secondTail := secondTail + 1;

    templateline.fFirstjustno := 0; {set Ass justs to nothing}
    templateline.fSecondjustno := 0;
    templateline.fthirdjustno := 0;

    SupplyProofline(newline);

    with newline do
     begin
      fLineno := templateline.fLineno + 2;
      fSubprooflevel := templateline.fSubprooflevel - 1; {checkthis}

      fFormula := templateline.fFormula.fRlink.CopyFormula;
      fJustification := ' ~E';
      fFirstjustno := templateline.fLineno + 1;
     end;

    localHead.InsertBefore(secondTail + 1, newline);
    secondTail := secondTail + 1;

    newline := nil;

    TProofline(localHead.At(secondTail + 1)).fLineno := TProofline(localHead.At(secondTail)).fLineno; {resetting}
{                    blank}

   end;

          {This adds the conclusion}

  SupplyProofline(newline);

  with newline do
   begin
    fLineno := TProofline(localHead.At(secondTail)).fLineno + 1;
    fSubprooflevel := TProofline(localHead.At(secondTail)).fSubprooflevel - 1; {checkthis}

    fFormula := firstTailFormula.CopyFormula;
    fJustification := ' ~E';

    fFirstjustno := ornum;
    fSecondjustno := TProofline(localHead.At(firstTail)).fLineno;
    fthirdjustno := TProofline(localHead.At(secondTail)).fLineno;
    fJustification := ' �E';
   end;

  secondTail := secondTail + 1; {now at blank}

  localHead.InsertBefore(secondTail + 1, newline);
  secondTail := secondTail + 1;

 end;


*/

void doReductio(TGWTReAssemble leftReAss){
  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  TFormula refutedFormula=((TFormula)(this.fTestNode.fSuccedent.get(0))).fRLink;

  if (!transfer(TTestNode.atomic, refutedFormula))
     createAssLine(refutedFormula, fLastAssIndex+1);

  numberLines();
  convertToSubProof();
  removeDummy(refutedFormula);

  numberLines();


}


/*
 procedure DoReductio (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);
  var
   refutedFormula: TFormula;

 begin
  refutedFormula := TFormula(thisnode.fsucceedent.First).fRlink;
  if not Transfer(atomic, refutedFormula, localHead, lastAssumption) then
   CreateAssLine(refutedFormula, localHead, lastAssumption);

  NumberLines(localHead);
  ConvertToSubProof(localHead, lastAssumption);
  RemoveDummy(localHead, refutedFormula);
  NumberLines(localHead);
end;

*/




void doUni(TGWTReAssemble leftReAss){
  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  String instantiations="";

  TFormula uniQuant=(TFormula)(leftReAss.fTestNode.fAntecedents.get(leftReAss.fTestNode.fAntecedents.size()-1));


  if (fTestNode.fTreeModel.getOldInstantiations().containsKey(uniQuant))
       instantiations=(String)(fTestNode.fTreeModel.getOldInstantiations().get(uniQuant));

     //oldInstantions give us all the might be there ie too much-- but we sort that out later


/*The semantics puts in all the instantiations in the tree (prepended
      to the antecedents). But in the
     derivation only one gets used

 So, for example, Allx(FxVGx) might put FaVGa, FbVGb etc

 It never puts more than the number of instantiations, so we need to check if any are used.

 Trouble is there may be other stuff there, for other reasons

 */

/*
  termForm=new TFormula(TFormula.functor,newInstants.substring(i,i+1),null,null);

       newInstance=uniQuant.scope().copyFormula();

       TFormula.subTermVarwInstance, termForm, variForm);


  */

   boolean redundant=true,instance=false;
   TFormula search=null,termForm,newInstance;
   int dummy=-1;

   for (int i=0;(i<instantiations.length())&&redundant;i++){
     search=(TFormula)leftReAss.fTestNode.fAntecedents.get(i);

     instance=false;

     for (int j=0;(j<instantiations.length())&&!instance;j++){ //this checks that search is an instance
       termForm = new TFormula(TFormula.functor, instantiations.substring(j, j + 1), null, null);
       newInstance = uniQuant.scope().copyFormula();
       TFormula.subTermVar(newInstance, termForm, uniQuant.quantVarForm());
       if(search.equalFormulas(search,newInstance))
         instance=true;
     }

     if (instance){

       dummy = TGWTMergeData.inPremises(fTestNode, fHead, fLastAssIndex, search);

       if (dummy != -1)
         redundant = false;
     }
     //redundant := not InPremises(thisnode, localHead, lastAssumption, search, dummy);

   }

   if (!redundant){
     prependToHead(uniQuant);
     for (int i=0;(i<instantiations.length());i++){
       search = (TFormula) leftReAss.fTestNode.fAntecedents.get(i);

       instance=false;

       for (int j=0;(j<instantiations.length())&&!instance;j++){ //this checks that search is an instance
         termForm = new TFormula(TFormula.functor, instantiations.substring(j, j + 1), null, null);
         newInstance = uniQuant.scope().copyFormula();
         TFormula.subTermVar(newInstance, termForm, uniQuant.quantVarForm());
         if(search.equalFormulas(search,newInstance))
           instance=true;
     }

if (instance){


  if (transfer(fTestNode.fStepType, search)) // {Moves every formula it justifies into body of proof}
    numberLines();
}
     }
   }
}

/*
 procedure DoUni (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   j: integer;
   redundant: boolean;
   uniform, search: TFormula;
   dummy, instantiationNo: integer;

 begin
  redundant := true;
  j := 0;

  uniform := TFormula(thisnode.fLlink.fantecedents.Last); {uniform}
  instantiationNo := NoOfInstantiations(uniform);

  j := 1;

  while redundant and (j <= instantiationNo) do
   begin
    search := TFormula(thisnode.fLlink.fantecedents.At(j)); {first instance}

    redundant := not InPremises(thisnode, localHead, lastAssumption, search, dummy);
    j := j + 1;

   end;

  if not redundant then
   begin

    AddtoHead(uniform, localHead, lastAssumption);

    uniform := TProofline(localHead.First).fFormula;

    RemoveInstantiatingInfo(uniform);

    TProofline(localHead.First).fFormula := uniform;

    j := 1;

    while (j <= instantiationNo) do
     begin
      search := TFormula(thisnode.fLlink.fantecedents.At(j)); {first instance}

      if Transfer(thisnode.fSteptype, search, localHead, lastAssumption) then
       NumberLines(localHead); {Moves the instance it justifies into body of}
 {                                                  proof}
      j := j + 1;

     end;

   end;
 end;


*/

void doTypedExi(TGWTReAssemble leftReAss) {
  fHead = leftReAss.fHead;
  fLastAssIndex = leftReAss.fLastAssIndex;

  TFormula theFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));

  int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                     leftReAss.fLastAssIndex, theFormula); //not sure

  if (dummy1 != -1) {
    TFormula formula = fParser.contractTypeExi(theFormula);

    prependToHead(formula);

    if (transfer(fTestNode.fStepType, theFormula)) // {Moves the formula it justifies into body of proof}
      numberLines();
  }

  }

  void doNegTypedExi(TGWTReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TFormula theFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));

    int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, theFormula); //not sure

    if (dummy1 != -1) {
      TFormula rLink = fParser.contractTypeExi(theFormula.fRLink);

      TFormula neg=new TFormula(TFormula.unary,
                                String.valueOf(chNeg),
                                null,
                                rLink);

      prependToHead(neg);

      if (transfer(fTestNode.fStepType, theFormula)) // {Moves the formula it justifies into body of proof}
        numberLines();
    }

  }





void doTypedUni(TGWTReAssemble leftReAss) {
  fHead = leftReAss.fHead;
  fLastAssIndex = leftReAss.fLastAssIndex;

  TFormula theFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));

  int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                     leftReAss.fLastAssIndex, theFormula); //not sure

  if (dummy1 != -1) {
    TFormula formula = fParser.contractTypeUni(theFormula);

    prependToHead(formula);

    if (transfer(fTestNode.fStepType, theFormula)) // {Moves the formula it justifies into body of proof}
      numberLines();
  }

  }

  void doNegTypedUni(TGWTReAssemble leftReAss) {
    fHead = leftReAss.fHead;
    fLastAssIndex = leftReAss.fLastAssIndex;

    TFormula theFormula = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));

    int dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                       leftReAss.fLastAssIndex, theFormula); //not sure

    if (dummy1 != -1) {
      TFormula rLink = fParser.contractTypeUni(theFormula.fRLink);

      TFormula neg=new TFormula(TFormula.unary,
                                String.valueOf(chNeg),
                                null,
                                rLink);

      prependToHead(neg);

      if (transfer(fTestNode.fStepType, theFormula)) // {Moves the formula it justifies into body of proof}
        numberLines();
    }

  }

class DoExi{
  TFormula exiFormula;

  void changeContradictions(){  //NEEDS WRITING

  }

void addEIConc(){
  TFormula concFormula= ((TProofline) (fHead.get(fHead.size() - 2))).fFormula;
  // last line is a blank

  if (concFormula.freeTest(exiFormula.quantVarForm()))
    changeContradictions();
  else{
    TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 2));

   TProofline newline = supplyProofline();
     newline.fLineno = templateLine.fLineno+1;
     newline.fFormula = concFormula.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel-1;
     newline.fFirstjustno = 1000;
     newline.fSecondjustno = templateLine.fLineno;
    newline.fJustification = TProofController.fEIJustification;

    fHead.add(newline);
  }
}

  /*
   begin
     exiFormula := TProofline(localHead.First).fFormula;
     concFormula := TProofline(localHead.At(localHead.fSize - 1)).fFormula; {last line is a}
   {               blank}

     if concFormula.FreeTest(exiFormula.QuantVarForm) then
      ChangeContradictions
     else
      begin
       templateline := TProofline(localHead.At(localHead.fSize - 1)); {old conc}

       SupplyProofline(newline);
       with newline do
        begin
         fLineno := templateline.fLineno + 1;
         fFormula := concFormula.CopyFormula;
         fFirstjustno := 1000;
         fSecondjustno := templateline.fLineno;
         fJustification := ' EI';
         fSubprooflevel := templateline.fSubprooflevel - 1;
        end;

       localHead.InsertLast(newline);

       newline := nil;
      end;
 end;

*/


/*
 procedure AddEIConc (thisnode: TTestnode; var localHead: TList);

{this takes the conclusions of an EI subproof and prepares conclusion . There are two cases: normal, }
{and the case where the conclusion is a dummy}

  var
   index: integer;
   exiFormula, concFormula, newformulanode: TFormula;
   templateline, newline: TProofline;

  procedure ChangeContradictions;

     {firsTail points at Fx and not Fx for some-- needs changing}

   procedure TakeCaution;
          {against dummy after subproof}

   begin
    if TProofline(localHead.At(localHead.fSize - 2)).fBlankline then {this means}
{                            that the dummy comes immediately after}
                    {a subproof and so needs extra lines}
     BreakDummy(localHead);
   end;

  begin

   TakeCaution;

   templateline := TProofline(localHead.At(localHead.fSize - 1)); {old contradiction}

   with templateline do
    begin
     fFormula := exiFormula.CopyFormula;
     fLastAssumption := true;
     fJustification := 'Ass';
     fSubprooflevel := templateline.fSubprooflevel + 1; {retains its justnos}
    end;

   SupplyProofline(newline); {newline points to new proofline}

   with newline do
    begin
     fLineno := templateline.fLineno;
     fSubprooflevel := templateline.fSubprooflevel - 1; {checkthis}
     fBlankline := true;
     fJustification := '';
     fSelectable := false;
    end;

   index := localHead.fSize - 1;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

               {****}

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     fRlink := templateline.fFormula.CopyFormula;
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := templateline.fLineno + 1;
     fFormula := newformulanode;
     fFirstjustno := templateline.fFirstjustno;
     fSecondjustno := templateline.fSecondjustno;
     fJustification := ' ~I';
     fSubprooflevel := templateline.fSubprooflevel - 1;
    end;

   localHead.InsertBefore(index + 1, newline);
   index := index + 1;

   templateline.fFirstjustno := 0; {TI so remove justnos}
   templateline.fSecondjustno := 0;
   templateline.fthirdjustno := 0;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := templateline.fLineno + 2;
     fFormula := TProofline(localHead.At(localHead.fSize - 1)).fFormula.CopyFormula;{neg}
{                         ex}
     fFirstjustno := 1000;
     fSecondjustno := templateline.fLineno + 1;
     fJustification := ' EI';
     fSubprooflevel := templateline.fSubprooflevel - 2;
    end;

   localHead.InsertLast(newline); {miss the blankline}
   newline := nil;

               {}

   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chAnd;
     fLlink := exiFormula.CopyFormula;
     fRlink := TProofline(localHead.Last).fFormula.CopyFormula; {neg ex}
    end;

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := templateline.fLineno + 3;
     fFormula := newformulanode;
     fFirstjustno := 1000;
     fSecondjustno := templateline.fLineno + 2;
     fJustification := ' ^I';
     fSubprooflevel := templateline.fSubprooflevel - 2;
    end;

   localHead.InsertLast(newline);
   newline := nil;

  end;

 begin
  exiFormula := TProofline(localHead.First).fFormula;
  concFormula := TProofline(localHead.At(localHead.fSize - 1)).fFormula; {last line is a}
{               blank}

  if concFormula.FreeTest(exiFormula.QuantVarForm) then
   ChangeContradictions
  else
   begin
    templateline := TProofline(localHead.At(localHead.fSize - 1)); {old conc}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := templateline.fLineno + 1;
      fFormula := concFormula.CopyFormula;
      fFirstjustno := 1000;
      fSecondjustno := templateline.fLineno;
      fJustification := ' EI';
      fSubprooflevel := templateline.fSubprooflevel - 1;
     end;

    localHead.InsertLast(newline);

    newline := nil;
   end;
 end;



*/


void doExi(TGWTReAssemble leftReAss){

  TFormula scope=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);
  TFormula variable=(TFormula)leftReAss.fTestNode.fAntecedents.get(1);

  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  int dummy = TGWTMergeData.inPremises(fTestNode, fHead,
                                       fLastAssIndex, scope);

    if (dummy != -1) {
      exiFormula = new TFormula(TFormula.quantifier,
                                          String.valueOf(chExiquant),
                                          variable,
                                          scope);

      prependToHead(exiFormula);

      if (transfer(fTestNode.fStepType, scope))
        ;

      convertToSubProof();
      addEIConc();
      numberLines();

    }
}

/*
 procedure DoExi (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   firstformula, secondformula, newformulanode: TFormula;
   dummy: integer;

 begin
  firstformula := TFormula(thisnode.fLlink.fantecedents.First);
  secondformula := TFormula(thisnode.fLlink.fantecedents.At(2));

  if InPremises(thisnode, localHead, lastAssumption, firstformula, dummy) then
   begin

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := quantifier;
                    { fBinary := true;}
                    { fInfo := concat(chExiquant, secondformula.fInfo); }

      fInfo := chExiquant;

      fLlink := secondformula;

                     {the correct variable is stored in the second node}
      fRlink := firstformula;
     end;

    AddtoHead(newformulanode, localHead, lastAssumption);

    newformulanode.fLlink := nil;
    newformulanode.fRlink := nil;
    newformulanode.DismantleFormula;   (*add makes a copy*)

    newformulanode := nil;

    if Transfer(thisnode.fSteptype, firstformula, localHead, lastAssumption) then
     ; {Moves}
 {                    the instance it justifies into body of proof}

    ConvertToSubProof(localHead, lastAssumption);

    AddEIConc(thisnode, localHead);

    NumberLines(localHead);
   end;

end;

*/
}


  class DoExiCV{
    TFormula exiFormula,oldExiFormula,scope,oldScope,variable,oldVariable;

 public  DoExiCV(TGWTReAssemble leftReAss){
   scope=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);
   variable=(TFormula)leftReAss.fTestNode.fAntecedents.get(1);
   oldVariable=(TFormula)leftReAss.fTestNode.fAntecedents.get(2);

   oldScope=scope.copyFormula();
   TFormula.subTermVar(oldScope,oldVariable,variable);
  }


    void changeContradictions(){  //NEEDS WRITING

    }

  void addEIConc(){
    TFormula concFormula= ((TProofline) (fHead.get(fHead.size() - 2))).fFormula;
    // last line is a blank

    if (concFormula.freeTest(exiFormula.quantVarForm()))
      changeContradictions();
    else{
      TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 2));

     TProofline newline = supplyProofline();
       newline.fLineno = templateLine.fLineno+1;
       newline.fFormula = concFormula.copyFormula();
       newline.fSubprooflevel = templateLine.fSubprooflevel-1;
       newline.fFirstjustno = 1000;
       newline.fSecondjustno = templateLine.fLineno;
      newline.fJustification = TProofController.fEIJustification;

      fHead.add(newline);
    }
  }

  void addOldExi(){

    oldExiFormula = new TFormula(TFormula.quantifier,
                                    String.valueOf(chExiquant),
                                    oldVariable,
                                    oldScope);
    prependToHead(oldExiFormula);

  }

  void doExiCV(TGWTReAssemble leftReAss){



    fHead=leftReAss.fHead;
    fLastAssIndex=leftReAss.fLastAssIndex;

    int dummy = TGWTMergeData.inPremises(fTestNode, fHead,
                                         fLastAssIndex, scope);

      if (dummy != -1) {
        exiFormula = new TFormula(TFormula.quantifier,
                                            String.valueOf(chExiquant),
                                            variable,
                                            scope);

        prependToHead(exiFormula);

        if (transfer(fTestNode.fStepType, scope))
          ;

        convertToSubProof();
        addEIConc();
        numberLines();

        addOldExi();

        if (transfer(TTestNode.atomic, exiFormula)){  //just guessing here what gets transferred
          numberLines();
          createLemma11(fLastAssIndex);
          numberLines();
  }



      }
  }

}

 /*

  procedure DoExiCV (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  {standard exi then with lemma on front}

   var
    newformulanode, firstformula, termForm, variForm: TFormula;
    dummy: integer;

   procedure AddOldExi;

    var
     tempFormula: TFormula;
               {  term, vari: char; }

   begin
    tempFormula := TFormula(thisnode.fLlink.fantecedents.First);
              {   term := TFormula(thisnode.fLlink.fantecedents.At(3)).fInfo[1];}
  {               vari := TFormula(thisnode.fLlink.fantecedents.At(2)).fInfo[1];}


    NewSubTermVar(tempFormula, termForm, variForm);

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := quantifier;
                      {fBinary := true;}
  {                    fInfo := concat(chExiquant, term);}
      fInfo := chExiquant;

                      {the correct variable is stored in the third node}
      fLlink := termForm;
      fRlink := tempFormula;
     end;

    AddtoHead(newformulanode, localHead, lastAssumption);

    newformulanode.fLlink := nil;
    newformulanode.fRlink := nil;
    newformulanode.DismantleFormula;   (*add makes a copy*)

    newformulanode := nil;

   end;

  begin
   firstformula := TFormula(thisnode.fLlink.fantecedents.First);
          {  secondformula := TFormula(thisnode.fLlink.fantecedents.At(2)); }

   termForm := TFormula(thisnode.fLlink.fantecedents.At(3));
   variForm := TFormula(thisnode.fLlink.fantecedents.At(2));


   if InPremises(thisnode, localHead, lastAssumption, firstformula, dummy) then
    begin
     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := quantifier;
                      {fBinary := true;}
  {					fInfo := concat(chExiquant, secondformula.fInfo);}
       fInfo := chExiquant;
       fLLink := variform;

                      {the correct variable is stored in the second node}
       fRlink := firstformula;
      end;

     AddtoHead(newformulanode, localHead, lastAssumption);
     newformulanode.fRlink := nil;
     newformulanode.fLlink := nil;
     newformulanode.DismantleFormula;   (*add makes a copy*)

     newformulanode := nil;

     if Transfer(thisnode.fSteptype, firstformula, localHead, lastAssumption) then
      ; {Moves}
  {                    the instance it justifies into body of proof}

     ConvertToSubProof(localHead, lastAssumption);

     AddEIConc(thisnode, localHead);

     NumberLines(localHead);

     AddOldExi;

     firstformula := TProofline(localHead.At(2)).fFormula;

     if Transfer(atomic, firstformula, localHead, lastAssumption) then
      begin
       NumberLines(localHead);
       CreateLemma11(localHead, lastAssumption);
       NumberLines(localHead);
      end;

    end;
end;

*/






  void doNegExi(TGWTReAssemble leftReAss){

    TFormula allNot=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);

    fHead=leftReAss.fHead;
    fLastAssIndex=leftReAss.fLastAssIndex;

    int dummy1 = TGWTMergeData.inPremises(fTestNode, fHead,
                                         fLastAssIndex, allNot);
      if (dummy1 != -1) {
        TFormula negExi = new TFormula(TFormula.unary,
                                            String.valueOf(chNeg),
                                            null,
                                            new TFormula(TFormula.quantifier,
                                                         String.valueOf(chExiquant),
                                                         allNot.quantVarForm().copyFormula(),
                                                         (allNot.fRLink.fRLink).copyFormula()));

        prependToHead(negExi);

        if (transfer(TTestNode.atomic, allNot)){
          numberLines();
          createLemma10(fLastAssIndex);
          numberLines();
        }
      }
}

 /*
  procedure DoNegExi (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

   var
    dummy: integer;
    firstformula: TFormula;

   procedure AddNegExi;

    var
     temp, newformulanode: TFormula;

   begin
    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := quantifier;
      fInfo := chExiquant;
      fLlink := firstformula.QuantVarForm;
      fRlink := firstformula.fRlink.fRlink;
     end;

    temp := newformulanode;
    newformulanode := nil;

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := temp;
     end;
    AddtoHead(newformulanode, localHead, lastAssumption); {negexi}
    temp.fRlink := nil;
    temp.fLlink := nil;
    newformulanode.DismantleFormula;   (*add makes a copy*)
    temp := nil;
    newformulanode := nil;
   end;

  begin
   firstformula := TFormula(thisnode.fLlink.fantecedents.First);
   if InPremises(thisnode, localHead, lastAssumption, firstformula, dummy) then
    begin
     AddNegExi;
     if Transfer(atomic, firstformula, localHead, lastAssumption) then
      begin
       NumberLines(localHead);
       CreateLemma10(localHead, lastAssumption);
       NumberLines(localHead);
      end;
    end;
end;

*/

 void doNegUni(TGWTReAssemble leftReAss){

  TFormula exiNot=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);

  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  int dummy1 = TGWTMergeData.inPremises(fTestNode, fHead,
                                       fLastAssIndex, exiNot);
    if (dummy1 != -1) {
      TFormula negUni = new TFormula(TFormula.unary,
                                          String.valueOf(chNeg),
                                          null,
                                          new TFormula(TFormula.quantifier,
                                                       String.valueOf(chUniquant),
                                                       exiNot.quantVarForm().copyFormula(),
                                                       (exiNot.fRLink.fRLink).copyFormula()));

      prependToHead(negUni);

      if (transfer(TTestNode.atomic, exiNot)){
        numberLines();
        createLemma9(fLastAssIndex);
        numberLines();
      }
    }
}


 /*
  procedure DoNegUni (thisnode: TTestNode; var localHead: Tlist; lastAssumption: integer);

   var
    dummy: integer;
    newformulanode: TFormula;

   procedure AddNegUni (var localHead: Tlist; var lastAssumption: integer);

    var
     temp: TFormula;

   begin
    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := quantifier;
                      {fBinary := true;}
      fInfo := chUniquant;

      fLlink := TFormula(thisnode.fLlink.fantecedents.First).QuantVarForm;

      fRlink := TFormula(thisnode.fLlink.fantecedents.First).fRlink.fRlink;
     end;

    temp := newformulanode;
    newformulanode := nil;

    SupplyFormula(newformulanode);
    with newformulanode do
     begin
      fKind := Unary;
      fInfo := chNeg;
      fRlink := temp;
     end;

    AddtoHead(newformulanode, localHead, lastAssumption); {neguni}
    temp.fRlink := nil;
    temp.fLlink := nil;
    newformulanode.DismantleFormula;   (*add makes a copy*)

    temp := nil;
    newformulanode := nil;
   end;

  begin
   if InPremises(thisnode, localHead, lastAssumption, TFormula(thisnode.fLlink.fantecedents.First), dummy) then
    begin
     AddNegUni(localHead, lastAssumption);
     if Transfer(atomic, TFormula(thisnode.fLlink.fantecedents.First), localHead, lastAssumption) then
      begin
       NumberLines(localHead);
       CreateLemma9(localHead, lastAssumption);
       NumberLines(localHead);
      end;
    end;
end;

*/


 void doExi(TGWTReAssemble leftReAss){

   DoExi doer = new DoExi();

   doer.doExi(leftReAss);
 }

void doExiS(TGWTReAssemble leftReAss){
  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

   TFormula EGFormula=(TFormula)fTestNode.fSuccedent.get(0);

   TProofline newline = supplyProofline();
     newline.fLineno = templateLine.fLineno+1;
     newline.fFormula = EGFormula.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel;
     newline.fFirstjustno = templateLine.fLineno;
    newline.fJustification = TProofController.EGJustification;

    fHead.add(newline);
}


/*

 procedure DoExiS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   newline, templateline: TProofline;
   tempFormula: TFormula;

 begin
  templateline := TProofline(localHead.Last);

  tempFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;
  RemoveInstantiatingInfo(tempFormula);
          { delete(tempFormula.fInfo, 3, 2); removing instantiating info}

  SupplyProofline(newline);
  with newline do
   begin
    fLineno := templateline.fLineno + 1;
    fFormula := tempFormula;
    fSubprooflevel := templateline.fSubprooflevel;
    fFirstjustno := templateline.fLineno;
    fJustification := ' EG';
   end;

  localHead.InsertLast(newline);
end;

*/

void doUniS(TGWTReAssemble leftReAss){
  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

   TFormula UGFormula=(TFormula)fTestNode.fSuccedent.get(0);

      TProofline newline = supplyProofline();
     newline.fLineno = templateLine.fLineno+1;
     newline.fFormula = UGFormula.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel;
     newline.fFirstjustno = templateLine.fLineno;
    newline.fJustification = TProofController.UGJustification;


    fHead.add(newline);


}

/*
 procedure DoUniS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

  var
   newline, templateline: TProofline;

 begin
  templateline := TProofline(localHead.Last);
  SupplyProofline(newline);
  with newline do
   begin
    fLineno := templateline.fLineno + 1;
    fFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;
    fSubprooflevel := templateline.fSubprooflevel;
    fFirstjustno := templateline.fLineno;
    fJustification := ' UG';
   end;

  localHead.InsertLast(newline);
end;

*/

void doTypedRewriteS(TGWTReAssemble leftReAss){
  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  TProofline templateLine = (TProofline) (fHead.get(fHead.size() - 1));

TFormula UGFormula=(TFormula)fTestNode.fSuccedent.get(0);

  TProofline newline = supplyProofline();
     newline.fLineno = templateLine.fLineno+1;
     newline.fFormula = UGFormula.copyFormula();
     newline.fSubprooflevel = templateLine.fSubprooflevel;
     newline.fFirstjustno = templateLine.fLineno;
    newline.fJustification = TProofController.typedRewriteJustification;

fHead.add(newline);


}

void numberLines(){

  TProofListModel.renumberLines(fHead,1000);  //renumber to numbers that do not occur
  TProofListModel.renumberLines(fHead,1);

}

/*
 procedure NumberLines (var localHead: TList);

  begin
   RenumberLines(localHead, 1000); {first renumber to numbers that do not occur}
   RenumberLines(localHead, 1);
  end;


*/

void createAssLine(TFormula assFormula, int insertIndex){

  TProofline newline=supplyProofline();
  newline.fLineno = 2000;
  newline.fSubprooflevel=((TProofline)fHead.get(insertIndex-1)).fSubprooflevel;
  newline.fFormula = assFormula.copyFormula();
  newline.fJustification = fAssJustification;

  fHead.add(insertIndex,newline);
//  fLastAssIndex+=1;


}


/*
 procedure CreateAssLine (newone: TFormula; var localHead: TList; var where: integer);

   var
    newline: TProofline;

  begin
   SupplyProofline(newline);
   with newline do
    begin
     fLineno := 2000;
     fSubprooflevel := TProofline(localHead.At(where)).fSubprooflevel;
     fFormula := newone.CopyFormula; {check maybe don't need copy here, yes do need it}
     fJustification := 'Ass';
    end;

   localHead.InsertBefore(where + 1, newline); {check mf}
  end;


*/







/////////////////////


// similar to Or

class NegAnd {

/*  {if both redundant then we miss}
 {This is optimised by seeing if we can drop either branch and follow other} */

   TFormula leftFormula; //not A
   TFormula rightFormula; //~B
   TFormula negAndFormula; //~(A^B)

   TProofline firstConc;
   TProofline secondConc;

   int dummy1;
   int dummy2;

   TTestNode proveAandBTest,proveATest,proveBTest;


  NegAnd(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss) {

   leftFormula = (TFormula) (fTestNode.getLeftChild().fAntecedents.get(0)); //not A
   rightFormula = (TFormula) (fTestNode.getRightChild().fAntecedents.get(0)); //not B

   negAndFormula = new TFormula (TFormula.unary,
                                 String.valueOf(chNeg),
                                 null,
                                 new TFormula(TFormula.binary,
                                    String.valueOf(chAnd),
                                    leftFormula.fRLink.copyFormula(),
                                    rightFormula.fRLink.copyFormula())
                                );  //~(A^B)

   firstConc = (TProofline) leftReAss.fHead.get(leftReAss.fHead.size() - 1);
   secondConc = (TProofline) rightReAss.fHead.get(rightReAss.fHead.size() - 1);

   dummy1 = TGWTMergeData.inPremises(fTestNode, leftReAss.fHead,
                                      leftReAss.fLastAssIndex, leftFormula);
   dummy2 = TGWTMergeData.inPremises(fTestNode, rightReAss.fHead,
                                     rightReAss.fLastAssIndex, rightFormula);
  }

  /*
     leftFormula := TFormula(thisnode.fLlink.fantecedents.First); {notA}
     rightFormula := TFormula(thisnode.frlink.fantecedents.First); {notB}

     firstconc := TProofline(localHead.Last);
     secondconc := TProofline(rightHead.Last); {helps with the orconc merge later}

     if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy)
   and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then

  */

 boolean canProveAandB(int maxSteps){

    /*
      {We are seeing whether we can prove (A^B), if so we can reductio}
      */


    proveAandBTest=fTestNode.getLeftChild().copyNodeInFullWithInstInfo();
    //{this is node with not-A antecedent head without not(A^B)}

    proveAandBTest.fAntecedents.remove(0); //{this is node without not-A antecedent

    proveAandBTest.fStepType=TTestNode.unknown;
    proveAandBTest.fDead=false; // reset for new test
    proveAandBTest.fClosed=false;
   // we are going to put this into a new Tree Model so what used to be the l and r links are null

    proveAandBTest.fSuccedent.clear();

    proveAandBTest.fSuccedent.add(negAndFormula.getRLink().copyFormula());  //A^B is now consequent

    TTreeModel aTreeModel = new TTreeModel(proveAandBTest.fSwingTreeNode);

    return
        (proveAandBTest.treeValid(aTreeModel, maxSteps) ==
          TTestNode.valid);

  }



  boolean canProveA(int maxSteps){

    /*
      {When first called this is a test of whether we can prove A from the rest of the premises}
      {without ~(A^B) If we can we use the right branch only}
      {Second call This is a test of whether we can prove B from the rest of the premises}
      {without ~(A^B) If we can we use the left branch only}  */


    proveATest=fTestNode.getLeftChild().copyNodeInFullWithInstInfo();
    //{this is node with not-A antecedent head without not(A^B)}

    proveATest.fStepType=TTestNode.unknown;
    proveATest.fDead=false; // reset for new test
    proveATest.fClosed=false;
   // we are going to put this into a new Tree Model so what used to be the l and r links are null

    TFormula aFormula=((TFormula)(proveATest.fAntecedents.get(0))).fRLink.copyFormula();

    // the first is ~A so this is A

    proveATest.fSuccedent.clear();

    proveATest.fSuccedent.add(aFormula);  //A is now consequent
    proveATest.fAntecedents.remove(0);    // ~(A from antecedents

    TTreeModel aTreeModel = new TTreeModel(proveATest.fSwingTreeNode);

    return
        (proveATest.treeValid(aTreeModel, maxSteps) ==
          TTestNode.valid);

  }

  boolean canProveB(int maxSteps){

      /*
            {When first called this is a test of whether we can prove A from the rest of the premises}
            {without ~(A^B) If we can we use the right branch only}
            {Second call This is a test of whether we can prove B from the rest of the premises}
      {without ~(A^B) If we can we use the left branch only}  */

      proveBTest=fTestNode.getRightChild().copyNodeInFullWithInstInfo();
      //{this is node with not-B antecedent head without not(A^B)}

      proveBTest.fStepType=TTestNode.unknown;
      proveBTest.fDead=false; // reset for new test
      proveBTest.fClosed=false;
     // we are going to put this into a new Tree Model so what used to be the l and r links are null

      TFormula aFormula=((TFormula)(proveBTest.fAntecedents.get(0))).fRLink.copyFormula();

      // the first is ~B so this is B

      proveBTest.fSuccedent.clear();

      proveBTest.fSuccedent.add(aFormula);  //~B is now consequent
      proveBTest.fAntecedents.remove(0);    // ~(B from antecedents

      TTreeModel aTreeModel = new TTreeModel(proveBTest.fSwingTreeNode);

      return
          (proveBTest.treeValid(aTreeModel, maxSteps) ==
            TTestNode.valid);

  }



  void doNegAnd(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss) {

    if ( (dummy1 != -1) && (dummy2 != -1)) { //normal case ~A B

      int maxSteps = leftReAss.fHead.size() + rightReAss.fHead.size() + 10;

      if (canProveAandB(maxSteps))  // often a ~(A^B) is part of a reductio, this is to take that shortcut

        // to do March 06 THERE IS AN OPTIMIZATION YOU CAN PUT IN HERE
      ;


      if (canProveA(maxSteps)) { // seeing if there is a proof of A
        optimize1(proveATest, negAndFormula, rightReAss);
      }
      else {
        if (canProveB(maxSteps)) { // seeing if there is a proof of B
          optimize2(proveBTest, negAndFormula, leftReAss);
        }
        else {
          leftReAss.prependToHead(negAndFormula);
          rightReAss.prependToHead(negAndFormula);

          if (leftReAss.transfer(TTestNode.atomic, leftFormula)) // moves ~A
            ;
          if (rightReAss.transfer(TTestNode.atomic, rightFormula)) // moves ~B
            ;

          leftReAss.numberLines();
          rightReAss.numberLines();

          leftReAss.convertToSubProof();
          rightReAss.convertToSubProof();

          TGWTMergeData mergeData = new TGWTMergeData(leftReAss, rightReAss);

          mergeData.merge();

          fHead = mergeData.firstLocalHead;
          fLastAssIndex = mergeData.firstLastAssIndex;

          createLemma3(fLastAssIndex + 1, negAndFormula);

          numberLines();

          int firstTailIndex = fHead.indexOf(firstConc);
          int secondTailIndex = fHead.indexOf(secondConc);

          /*coding style diffent here to Pascal but I think the result is the same*/

          if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/) {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 13);
          }
          else {
            addOrConc(firstTailIndex, secondTailIndex,
                      ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 10);
          }

          /*
            AddNegAnd(localHead, lastAssumption);
           AddNegAnd(rightHead, rightlastAss);

           if Transfer(atomic, leftFormula, localHead, lastAssumption) then
           ; {moves}
           {                              notA into body of proof}
           NumberLines(localHead);
           if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
           ; {moves}
           {                              notB into body of proof}
           NumberLines(rightHead);

           ConvertToSubProof(localHead, lastAssumption);
           ConvertToSubProof(rightHead, rightlastAss);

           Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

           CreateLemma3(localHead, lastAssumption);  {12/11/90 changed from +1}

           {This adds 10 or 13lines ending in}
           {                                                                       the derived line}


           {$IFC useAbsurd}
           firstno := firstno + 13;
           secondno := secondno + 13;
           NumberLines(localHead);

           firstTail := localHead.GetSameItemNo(firstconc);
           secondTail := localHead.GetSameItemNo(secondconc);

           AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 13);

           {$ELSEC}
           firstno := firstno + 10;  {mf not sure if this is right}
           secondno := secondno + 10;



           NumberLines(localHead);

           firstTail := localHead.GetSameItemNo(firstconc);
           secondTail := localHead.GetSameItemNo(secondconc);

           AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 10);
           {$ENDC}
           end;
           */

        }
      }
    }
   else
      {
        //more to come

        /*     if not InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
         SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss); */

        if (dummy2 == -1) {    // I used to make a mistake with !=
          fHead = rightReAss.fHead;
          fLastAssIndex = rightReAss.fLastAssIndex; //{we'll go with the right leg}
        }
        else {
          fHead = leftReAss.fHead;
          fLastAssIndex = leftReAss.fLastAssIndex; //{proof of B from not A}
        }
      }
  }

  void optimize1(TTestNode proveATest,TFormula negAndFormula,TGWTReAssemble rightReAss){
      //I think this is done
      /*
       The branches split with ~A on left and ~B on right, and we can derive A. And this
       has come from ~(A^B) above. So we take the proof of A couple that with ~(A^B to
       get  a proof of ~B then go down the right branch

       */

      TGWTReAssemble proveA = supplyTReAssemble( proveATest, null, 0);

      proveA.reAssembleProof(); //proof of A}

      fHead = proveA.fHead; //transfer it to this, our main proof
      fLastAssIndex = proveA.fLastAssIndex;

      prependToHead(negAndFormula);

      createLemma1(fHead.size());

      numberLines();

      rightReAss.prependToHead(negAndFormula);

      rightReAss.numberLines();

      /*
             AddNegAnd(localHead, lastAssumption);

            CreateLemma1(localHead);

            NumberLines(localHead);

            AddNegAnd(rightHead, rightlastAss);


      */

      if (rightReAss.transfer(TTestNode.atomic, rightFormula)){  //~B

         TProofline notBLine=(TProofline)(rightReAss.fHead.get(rightReAss.fLastAssIndex+1));

         TGWTMergeData mergeData = new TGWTMergeData(TGWTReAssemble.this, rightReAss);

         mergeData.merge();

         fHead = mergeData.firstLocalHead;
         fLastAssIndex = mergeData.firstLastAssIndex;

         numberLines();  //stopped here

         int secondNotBIndex=fHead.indexOf(notBLine); // Iused to have  +1; but not sure why

         TProofListModel.reNumSingleLine (fHead, secondNotBIndex, mergeData.firstLineNum);

         fHead.remove(secondNotBIndex);

         numberLines();

         /*
            if Transfer(atomic, rightFormula, rightHead, rightlastAss) then {notB}
                begin

                 localPseudoTailNext := rightHead.At(rightlastAss + 1); {~B I hope}

                 Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

                 notBindex := localHead.GetSameItemNo(localPseudoTailNext);

                 RenumSingleLine(localHead, notBindex, firstno);

                 garbageline := localHead.At(notBindex);

                 localHead.Delete(garbageline); {omit second ~B}

                 TProofline(garbageline).DismantleProofline;

                 NumberLines(localHead);
                end;



      }*/


         }
   }

/*
    procedure Optimize1;

     var
      localPseudoTailNext: TObject;
      notBindex: integer;

    begin
     DismantleProofList(localHead);

     localHead := nil;
     lastAssumption := 0;

     ReAssembleProof(temptest, localHead, lastAssumption);

     DismantleTestTree(temptest); {new}

     AddNegAnd(localHead, lastAssumption);

     CreateLemma1(localHead);

     NumberLines(localHead);

     AddNegAnd(rightHead, rightlastAss);

     if Transfer(atomic, rightFormula, rightHead, rightlastAss) then {notB}
      begin

       localPseudoTailNext := rightHead.At(rightlastAss + 1); {~B I hope}

       Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

       notBindex := localHead.GetSameItemNo(localPseudoTailNext);

       RenumSingleLine(localHead, notBindex, firstno);

       garbageline := localHead.At(notBindex);

       localHead.Delete(garbageline); {omit second ~B}

       TProofline(garbageline).DismantleProofline;

       NumberLines(localHead);
      end;
    end;


  */


void optimize2(TTestNode proveBTest,TFormula negAndFormula,TGWTReAssemble leftReAss){

     /*
      The branches split with ~A on left and ~B on right, and we can derive B. And this
      has come from ~(A^B) above. So we take the proof of B couple that with ~(A^B to
      get  a proof of ~A then go down the leftt branch

      */

     TGWTReAssemble proveB = supplyTReAssemble( proveBTest, null, 0);

     proveB.reAssembleProof(); //proof of B}

     fHead = proveB.fHead; //transfer it to this, our main proof
     fLastAssIndex = proveB.fLastAssIndex;

     prependToHead(negAndFormula);

     createLemma2(fHead.size());

     numberLines();

     leftReAss.prependToHead(negAndFormula);

     leftReAss.numberLines();

     if (leftReAss.transfer(TTestNode.atomic, rightFormula)){  //~B

        TProofline notALine=(TProofline)(leftReAss.fHead.get(leftReAss.fLastAssIndex+1));

        TGWTMergeData mergeData = new TGWTMergeData(TGWTReAssemble.this, leftReAss);

        mergeData.merge();

        fHead = mergeData.firstLocalHead;
        fLastAssIndex = mergeData.firstLastAssIndex;

        numberLines();

        int secondNotAIndex=fHead.indexOf(notALine); // Iused to have  +1; but not sure why

        TProofListModel.reNumSingleLine (fHead, secondNotAIndex, mergeData.firstLineNum);

        fHead.remove(secondNotAIndex);

        numberLines();

        }

 }

/*
   procedure Optimize2;
       {note that this is the same as 1 except for changing right and left and A and B}
       {and different lemmas}

    var
     localPseudoTailNext: TObject;
     notBindex: integer;

   begin
    SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss);

    DismantleProofList(localHead);

    localHead := nil;
    lastAssumption := 0;

    ReAssembleProof(temptest, localHead, lastAssumption);

    DismantleTestTree(temptest); {new}

    AddNegAnd(localHead, lastAssumption);
    CreateLemma2(localHead);

    NumberLines(localHead);

    AddNegAnd(rightHead, rightlastAss);

    if Transfer(atomic, leftFormula, rightHead, rightlastAss) then {notA}
     begin

      localPseudoTailNext := rightHead.At(rightlastAss + 1); {~B I hope}

      Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

      notBindex := localHead.GetSameItemNo(localPseudoTailNext);

      RenumSingleLine(localHead, notBindex, firstno);

      garbageline := localHead.At(notBindex);

      localHead.Delete(garbageline); {omit second ~B}

      TProofline(garbageline).DismantleProofline;

      NumberLines(localHead);
     end;
   end;


  */



}  //end of DoNegAnd class

//////////////////////


/*
 procedure DoNegAnd (thisnode: Ttestnode; var localHead: TList; var lastAssumption: integer; var rightHead: TList; var rightlastAss: integer);
 {if both redundant then we miss}
 {This is optimised by seeing if we can drop either branch and follow other}

  var
   firstno, secondno, dummy, firstTail, secondTail: integer;
   temptest: Ttestnode;
   firstconc, secondconc: TProofline;
   garbageline: TObject;
   newformulanode, leftFormula, rightFormula: TFormula;

  procedure AddNegAnd (var localHead: TList; var lastAssumption: integer);
      {Adds the negation of the conjunction of left,right at beginning}

   var
    tempformula: TFormula;

  begin
   SupplyFormula(tempformula);
   with tempformula do {and}
    begin
     fKind := Binary;
     fInfo := chAnd;
     fLlink := leftFormula.frlink;
     frlink := rightFormula.frlink;
    end;

   SupplyFormula(newformulanode); {negand}
   with newformulanode do
    begin
     fKind := Unary;
     fInfo := chNeg;
     frlink := tempformula;
    end;

   AddtoHead(newformulanode, localHead, lastAssumption); {makes a copy}

   tempformula.fLlink := nil;
   tempformula.frlink := nil;

   newformulanode.DismantleFormula;

  end;

  procedure Optimize1;

   var
    localPseudoTailNext: TObject;
    notBindex: integer;

  begin
   DismantleProofList(localHead);

   localHead := nil;
   lastAssumption := 0;

   ReAssembleProof(temptest, localHead, lastAssumption);

   DismantleTestTree(temptest); {new}

   AddNegAnd(localHead, lastAssumption);

   CreateLemma1(localHead);

   NumberLines(localHead);

   AddNegAnd(rightHead, rightlastAss);

   if Transfer(atomic, rightFormula, rightHead, rightlastAss) then {notB}
    begin

     localPseudoTailNext := rightHead.At(rightlastAss + 1); {~B I hope}

     Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

     notBindex := localHead.GetSameItemNo(localPseudoTailNext);

     RenumSingleLine(localHead, notBindex, firstno);

     garbageline := localHead.At(notBindex);

     localHead.Delete(garbageline); {omit second ~B}

     TProofline(garbageline).DismantleProofline;

     NumberLines(localHead);
    end;
  end;

  procedure Optimize2;
      {note that this is the same as 1 except for changing right and left and A and B}
      {and different lemmas}

   var
    localPseudoTailNext: TObject;
    notBindex: integer;

  begin
   SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss);

   DismantleProofList(localHead);

   localHead := nil;
   lastAssumption := 0;

   ReAssembleProof(temptest, localHead, lastAssumption);

   DismantleTestTree(temptest); {new}

   AddNegAnd(localHead, lastAssumption);
   CreateLemma2(localHead);

   NumberLines(localHead);

   AddNegAnd(rightHead, rightlastAss);

   if Transfer(atomic, leftFormula, rightHead, rightlastAss) then {notA}
    begin

     localPseudoTailNext := rightHead.At(rightlastAss + 1); {~B I hope}

     Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

     notBindex := localHead.GetSameItemNo(localPseudoTailNext);

     RenumSingleLine(localHead, notBindex, firstno);

     garbageline := localHead.At(notBindex);

     localHead.Delete(garbageline); {omit second ~B}

     TProofline(garbageline).DismantleProofline;

     NumberLines(localHead);
    end;
  end;

  procedure AssembleTest (var temptest: Ttestnode);
      {When first called this is a test of whether we can prove A from the rest of the premises}
      {without ~(A^B) If we can we use the right branch only}
      {Second call This is a test of whether we can prove B from the rest of the premises}
      {without ~(A^B) If we can we use the left branch only}

   var
    negAformula, aFormula: TFormula;

  begin
   with temptest do
    begin
     fSteptype := unknown;
     fDead := false; {must reset it for a new test}
     fClosed := false;
     fLlink := nil;
     frlink := nil;
    end;

   if (temptest.fsucceedent.fSize <> 0) then
    begin
     aFormula := TFormula(temptest.fsucceedent.First);
     temptest.fsucceedent.Delete(temptest.fsucceedent.First);
     aFormula.DismantleFormula; {drops existing}
    end;

   aFormula := TFormula(temptest.fantecedents.First).frlink.CopyFormula;
   temptest.fsucceedent.InsertFirst(aFormula); {makes A consequent}

   aFormula := TFormula(temptest.fantecedents.First);
   temptest.fantecedents.Delete(temptest.fantecedents.First);
   aFormula.DismantleFormula; {drops not-A}
  end;

 begin
  ReAssembleProof(thisnode.fLlink, localHead, lastAssumption);
  ReAssembleProof(thisnode.frlink, rightHead, rightlastAss);

  leftFormula := TFormula(thisnode.fLlink.fantecedents.First); {notA}
  rightFormula := TFormula(thisnode.frlink.fantecedents.First); {notB}

  firstconc := TProofline(localHead.Last);
  secondconc := TProofline(rightHead.Last); {helps with the orconc merge later}

  if InPremises(thisnode, localHead, lastAssumption, leftFormula, dummy) and InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then

   begin

    temptest := nil;
    temptest := thisnode.fLlink.CopyNodeinFullWithInstInfo; {this is node with not-A as}
 {                                                                        antecedent haead}
    AssembleTest(temptest);

                {InitStringStore; not needed if retaining instantiating ingfo}

    if (temptest.TreeValid(localHead.fSize + rightHead.fSize + 10) = valid) then
     Optimize1
    else
     begin
      DismantleTestTree(temptest);
      temptest := nil;
      temptest := thisnode.frlink.CopyNodeinFullWithInstInfo; {this is node with not-B}
 {                         as antecedent haead}
      AssembleTest(temptest);

                     {InitStringStore;}

      if (temptest.TreeValid(localHead.fSize + rightHead.fSize + 10) = valid) then
       Optimize2
      else
       begin
        DismantleTestTree(temptest);
        AddNegAnd(localHead, lastAssumption);
        AddNegAnd(rightHead, rightlastAss);

        if Transfer(atomic, leftFormula, localHead, lastAssumption) then
        ; {moves}
 {                              notA into body of proof}
        NumberLines(localHead);
        if Transfer(atomic, rightFormula, rightHead, rightlastAss) then
        ; {moves}
 {                              notB into body of proof}
        NumberLines(rightHead);

        ConvertToSubProof(localHead, lastAssumption);
        ConvertToSubProof(rightHead, rightlastAss);

        Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

        CreateLemma3(localHead, lastAssumption);  {12/11/90 changed from +1}

 {This adds 10 or 13lines ending in}
 {                                                                       the derived line}


 {$IFC useAbsurd}
        firstno := firstno + 13;
        secondno := secondno + 13;
        NumberLines(localHead);

        firstTail := localHead.GetSameItemNo(firstconc);
        secondTail := localHead.GetSameItemNo(secondconc);

        AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 13);

 {$ELSEC}
        firstno := firstno + 10;  {mf not sure if this is right}
        secondno := secondno + 10;



        NumberLines(localHead);

        firstTail := localHead.GetSameItemNo(firstconc);
        secondTail := localHead.GetSameItemNo(secondconc);

        AddOrConc(localHead, firstTail, secondTail, TProofline(localHead.At(lastAssumption)).fLineno + 10);
 {$ENDC}
       end;
     end;
   end
  else
   begin
    if not InPremises(thisnode, rightHead, rightlastAss, rightFormula, dummy) then
     SwapLeftRight(localHead, lastAssumption, rightHead, rightlastAss);
    DismantleProofList(rightHead); {step redundant}
   end;
end;

*/



/////////////////////////////////////

class OptimizeR{

 /*
  {In a reductio, this checks whether there is the negation of any formula in the}
 {antecedents, if so it checks whether the positive version of that formula can be}
 {proved from the rest if so a complex contradiction has been found and this often
shortens a proof}

  */


  TTestNode provePosHornTest;
  TTestNode proveNegHorn;
  TFormula fNegHorn;


void addAndAtEnd(int firstLineNo, int secondLineNo){
  if (TPreferencesData.fUseAbsurd)
    addAbsurd(firstLineNo,secondLineNo);
  else{
    addAnd(firstLineNo,secondLineNo);
    }
}


void addAbsurd(int firstLineNo, int secondLineNo){
  TProofline templateLine=(TProofline)fHead.get(fHead.size()-1);


  TProofline newline = supplyProofline();
  newline.fLineno = templateLine.fLineno + 1;
  newline.fFormula = TFormula.fAbsurd.copyFormula();
  newline.fFirstjustno=firstLineNo;
  newline.fSecondjustno=secondLineNo;
  newline.fJustification = TProofController.absIJustification;
  newline.fSubprooflevel =templateLine.fSubprooflevel;

  fHead.add(newline);


}

void addAnd(int firstLineNo, int secondLineNo){
  TProofline templateLine=(TProofline)fHead.get(fHead.size()-1);


  TProofline newline = supplyProofline();
  newline.fLineno = templateLine.fLineno + 1;
  newline.fFormula = new TFormula(TFormula.binary,
                                  String.valueOf(chAnd),
                                  fNegHorn.fRLink.copyFormula(),
                                  fNegHorn.copyFormula());
  newline.fFirstjustno=firstLineNo;
  newline.fSecondjustno=secondLineNo;
  newline.fJustification = fAndIJustification;
  newline.fSubprooflevel =templateLine.fSubprooflevel;

  fHead.add(newline);


}

/*
    {$IFC useAbsurd}

    procedure AddAndAtEnd;

     var
      newline, templateline: TProofline;

    begin
     templateline := TProofline(localHead.Last);

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 1;
       fSubprooflevel := templateline.fSubprooflevel;
       fFormula := gAbsurdFormula.CopyFormula;
       fFirstjustno := firstno;
       fSecondjustno := secondno;
       fJustification := ' AbsI';
      end;

     localHead.InsertLast(newline);
     newline := nil;
    end;
   {$ELSEC}

    procedure AddAndAtEnd;

     var
      newformulanode: TFormula;
      newline, templateline: TProofline;

    begin
     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Binary;
       fInfo := chAnd;
       fLlink := negHorn.frlink.CopyFormula;
       frlink := negHorn;
      end;

     templateline := TProofline(localHead.Last);

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := templateline.fLineno + 1;
       fSubprooflevel := templateline.fSubprooflevel;
       fFormula := newformulanode;
       fFirstjustno := firstno;
       fSecondjustno := secondno;
       fJustification := ' ^I';
      end;

     localHead.InsertLast(newline);
     newline := nil;
     newformulanode := nil;
    end;

   {$ENDC}


  */



  boolean canProvePosHorn(TFormula negHorn,int index){

    /*
       {This is a test of whether we can prove (complex) from the rest of the premises}
         {without ~(complex)}

    }*/

    provePosHornTest=fTestNode.copyNodeInFullWithInstInfo();

    provePosHornTest.fStepType=TTestNode.unknown;
    provePosHornTest.fDead=false; // reset for new test
    provePosHornTest.fClosed=false;
   // we are going to put this into a new Tree Model so what used to be the l and r links are null

    TFormula posHorn=negHorn.fRLink.copyFormula();

    // index is ~complex so this is complex

    provePosHornTest.fSuccedent.clear();

    provePosHornTest.fSuccedent.add(posHorn);  //complex is now consequent
    provePosHornTest.fAntecedents.remove(index);    // drop ~complex from antecedents

    TTreeModel aTreeModel = new TTreeModel(provePosHornTest.fSwingTreeNode);

    return
        (provePosHornTest.treeValid(aTreeModel, 20) == //20 is just a guess
          TTestNode.valid);

  }



/*
    procedure AssembleTest;

      var
       aFormula: TFormula;

     begin
      temptest := nil;
      temptest := thisnode.CopyNodeinFullWithInstInfo;

      with temptest do
       begin
        fSteptype := unknown;
        fDead := false; {must reset it for a new test}
        fClosed := false;
        fLlink := nil;
        frlink := nil;
       end;

      if (temptest.fsucceedent.fSize <> 0) then
       begin
        aFormula := TFormula(temptest.fsucceedent.First);
        temptest.fsucceedent.Delete(temptest.fsucceedent.First);
        aFormula.DismantleFormula; {drops existing}
       end;

      aFormula := negFormula.frlink.CopyFormula; {makes pos consequent}
      temptest.fsucceedent.InsertFirst(aFormula);

      aFormula := TFormula(temptest.fantecedents.At(index));
      temptest.fantecedents.Delete(temptest.fantecedents.At(index));
      aFormula.DismantleFormula; {drops existing negFormula}

                                           {check}
     end;


  */

void produceNegHornNode(){

    /*
       We just here  construct a one line proof

    }*/

    proveNegHorn=new TTestNode(null,null);

    proveNegHorn.fStepType=TTestNode.atomic;
    proveNegHorn.fDead=true; // reset for new test
    proveNegHorn.fClosed=true;


    proveNegHorn.fAntecedents.add(fNegHorn.copyFormula());
    proveNegHorn.fSuccedent.add(fNegHorn.copyFormula());

  }


boolean blockUniS(TFormula searchFormula){
  if (TParser.isUniquant(searchFormula)&&
     TFormula.varFree(searchFormula.quantVarForm(), fTestNode.fAntecedents))
   return
       false;

 else
   return true;
}

  /*
   function BlockUniS: boolean;
              {uniS with free variables inserts negation into antecedents}
              {creating potential for loop}

      begin
       BlockUniS := true;
       if negFormula.frlink.fKind = quantifier then
        if (negFormula.frlink.fInfo[1] = chUniquant) then
         if NewVarFree(negFormula.QuantVarForm, thisnode.fantecedents) then
          BlockUniS := false;
   end;

*/

boolean doOptimizeR(){

  int index = 0;
  TFormula search = null;  //search is going to be the negHorn eg ~(A^B)
  boolean found=false;


  if (fTestNode.fAntecedents.size() > 0) {
    Iterator iter = fTestNode.fAntecedents.iterator();

    while (iter.hasNext() && !found) {
      search = ( (TFormula) (iter.next()));

      found=negProvable(search,index);   //seeing if we can prove positive,say A^B

      if (!found)
        index++;
    }
  }

  if (found){
     TGWTReAssemble leftReAss =supplyTReAssemble(provePosHornTest,null,0);
     leftReAss.reAssembleProof();

     fNegHorn=search;

     produceNegHornNode();

     TGWTReAssemble rightReAss =supplyTReAssemble(proveNegHorn,null,0);
     rightReAss.reAssembleProof();

     TGWTMergeData mergeData=new TGWTMergeData(leftReAss,rightReAss);

     mergeData.merge();

     fHead=mergeData.firstLocalHead;
     fLastAssIndex=mergeData.firstLastAssIndex;

     addAndAtEnd(mergeData.firstLineNum,mergeData.secondLineNum);

     numberLines();

  }
  return
      found;

}

/*
 begin
   index := 1;
   search := thisnode.fantecedents.FirstThat(NegProvable);

   if (search <> nil) then
    begin
     negHorn := TFormula(search).CopyFormula; {need to do this before dismantle}

     ReAssembleProof(temptest, localHead, lastAssumption); {proof of + }

     DismantleTestTree(temptest);

     AssembleSecondNode;

     ReAssembleProof(temptest, rightHead, rightlastAss);

     DismantleTestTree(temptest);

     Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

     AddAndAtEnd;

     NumberLines(localHead);

    end;

   OptimizeR := (search <> nil);

end;

*/

boolean negProvable(TFormula searchFormula,int index){
  boolean found=false;

  if ((searchFormula.fKind==TFormula.unary)&&  //has to be a negation
     ((searchFormula.fRLink.fKind==TFormula.unary)||(searchFormula.fRLink.fKind==TFormula.binary))&&  //not atomic
     blockUniS(searchFormula)){

        found=canProvePosHorn(searchFormula,index);
 }
 return
     found;
}

/*
 begin
    found := false;
    negFormula := TFormula(item);
    if negFormula.fKind = Unary then {negation}
     begin
      if ((negFormula.frlink.fKind = Unary) or (negFormula.frlink.fKind = Binary)) and BlockUniS then {not atomic}
       begin
        AssembleTest;
                           {InitStringStore; not if retaining instantiating info}
        if temptest.TreeValid(20) = valid then {20 is just a guess}
         found := true
        else
         DismantleTestTree(temptest);
       end;
     end;
    index := index + 1;
    NegProvable := found;
  end;

*/
}

/*
 function OptimizeR (thisnode: Ttestnode; var localHead: TList; var lastAssumption: integer; var rightHead: TList; var rightlastAss: integer): boolean;

 {In a reductio, this checks whether there is the negation of any formula in the}
 {antecedents, if so it checks whether the positive version of that formula can be}
 {proved from the rest if so a complex contradiction has been found and this often shortens a proof}

  var
   search: TObject;
   temptest: Ttestnode;
   firstno, secondno, index: integer;
   negHorn: TFormula;


 {$IFC useAbsurd}

  procedure AddAndAtEnd;

   var
    newline, templateline: TProofline;

  begin
   templateline := TProofline(localHead.Last);

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := templateline.fLineno + 1;
     fSubprooflevel := templateline.fSubprooflevel;
     fFormula := gAbsurdFormula.CopyFormula;
     fFirstjustno := firstno;
     fSecondjustno := secondno;
     fJustification := ' AbsI';
    end;

   localHead.InsertLast(newline);
   newline := nil;
  end;
 {$ELSEC}

  procedure AddAndAtEnd;

   var
    newformulanode: TFormula;
    newline, templateline: TProofline;

  begin
   SupplyFormula(newformulanode);
   with newformulanode do
    begin
     fKind := Binary;
     fInfo := chAnd;
     fLlink := negHorn.frlink.CopyFormula;
     frlink := negHorn;
    end;

   templateline := TProofline(localHead.Last);

   SupplyProofline(newline);
   with newline do
    begin
     fLineno := templateline.fLineno + 1;
     fSubprooflevel := templateline.fSubprooflevel;
     fFormula := newformulanode;
     fFirstjustno := firstno;
     fSecondjustno := secondno;
     fJustification := ' ^I';
    end;

   localHead.InsertLast(newline);
   newline := nil;
   newformulanode := nil;
  end;

 {$ENDC}

  procedure AssembleSecondNode;

   var
    tempformula: TFormula;

  begin
   temptest := nil;
   New(temptest);
   FailNIL(temptest);
   temptest.ITestnode; {creates lists}

   with temptest do
    begin
     fClosed := thisnode.fClosed;
     fDead := thisnode.fDead;
     fSteptype := atomic;
    end;

   tempformula := TFormula(search).CopyFormula;
   temptest.fantecedents.InsertFirst(tempformula); {neg}

   tempformula := TFormula(search).CopyFormula;
   temptest.fsucceedent.InsertFirst(tempformula); {neg}
  end;

  function NegProvable (item: TObject): boolean;

   var
    negFormula: TFormula;
    found: boolean;

   procedure AssembleTest;

    var
     aFormula: TFormula;

   begin
    temptest := nil;
    temptest := thisnode.CopyNodeinFullWithInstInfo;

    with temptest do
     begin
      fSteptype := unknown;
      fDead := false; {must reset it for a new test}
      fClosed := false;
      fLlink := nil;
      frlink := nil;
     end;

    if (temptest.fsucceedent.fSize <> 0) then
     begin
      aFormula := TFormula(temptest.fsucceedent.First);
      temptest.fsucceedent.Delete(temptest.fsucceedent.First);
      aFormula.DismantleFormula; {drops existing}
     end;

    aFormula := negFormula.frlink.CopyFormula; {makes pos consequent}
    temptest.fsucceedent.InsertFirst(aFormula);

    aFormula := TFormula(temptest.fantecedents.At(index));
    temptest.fantecedents.Delete(temptest.fantecedents.At(index));
    aFormula.DismantleFormula; {drops existing negFormula}

                                         {check}
   end;

   function BlockUniS: boolean;
           {uniS with free variables inserts negation into antecedents}
           {creating potential for loop}

   begin
    BlockUniS := true;
    if negFormula.frlink.fKind = quantifier then
     if (negFormula.frlink.fInfo[1] = chUniquant) then
      if NewVarFree(negFormula.QuantVarForm, thisnode.fantecedents) then
       BlockUniS := false;
   end;

  begin
   found := false;
   negFormula := TFormula(item);
   if negFormula.fKind = Unary then {negation}
    begin
     if ((negFormula.frlink.fKind = Unary) or (negFormula.frlink.fKind = Binary)) and BlockUniS then {not atomic}
      begin
       AssembleTest;
                          {InitStringStore; not if retaining instantiating info}
       if temptest.TreeValid(20) = valid then {20 is just a guess}
        found := true
       else
        DismantleTestTree(temptest);
      end;
    end;
   index := index + 1;
   NegProvable := found;
  end;

 begin
  index := 1;
  search := thisnode.fantecedents.FirstThat(NegProvable);

  if (search <> nil) then
   begin
    negHorn := TFormula(search).CopyFormula; {need to do this before dismantle}

    ReAssembleProof(temptest, localHead, lastAssumption); {proof of + }

    DismantleTestTree(temptest);

    AssembleSecondNode;

    ReAssembleProof(temptest, rightHead, rightlastAss);

    DismantleTestTree(temptest);

    Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);

    AddAndAtEnd;

    NumberLines(localHead);

   end;

  OptimizeR := (search <> nil);

end;

*/

//////////////////////////////////////


void doImplicS(TGWTReAssemble leftReAss){

  /*

     {We have a proof of B from premises , and we}
   {are trying to convert this into a proof of A->B.

   |Premises
   |_______
   |<other lines>
   |B

we add A after last index

   |Premises
   |_______
   |A
   |<other lines>
   |B

convert the last bit to a subproof, and add A ->B

   |Premises
   |_______
   ||A
   || _
   ||<other lines>
   ||B
   |A->B



*/


  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  TFormula arrowFormula=(TFormula)(fTestNode.fSuccedent.get(0)); // A arrow B

  TProofline templateline = (TProofline)(fHead.get(fHead.size()-1));

  if (!transfer(TTestNode.atomic, arrowFormula.fLLink)) //
       createAssLine(arrowFormula.fLLink, fLastAssIndex+1);

  numberLines();

  int justNo=templateline.fLineno;

   convertToSubProof();

      numberLines();

      templateline = (TProofline)(fHead.get(fHead.size()-1));

      TProofline newline=supplyProofline();
        newline.fLineno = templateline.fLineno+1;
        newline.fSubprooflevel=templateline.fSubprooflevel;
        newline.fFormula = arrowFormula.copyFormula();
        newline.fFirstjustno=justNo;
        newline.fJustification = fImplicIJustification;

        fHead.add(newline);
      //  fLastAssIndex+=1;


}

    /*

     procedure DoArrowS (var thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

      var
       justno: integer;
       arrowFormula: TFormula;
       newline, templateline: TProofline;

     begin
      arrowFormula := TFormula(thisnode.fsucceedent.First); {A�B}

      templateline := TProofline(localHead.Last);

      if not Transfer(atomic, arrowFormula.fLlink, localHead, lastAssumption) then
       CreateAssLine(arrowFormula.fLlink, localHead, lastAssumption);

      NumberLines(localHead);

      justno := templateline.fLineno;

      ConvertToSubProof(localHead, lastAssumption);

      NumberLines(localHead);

      templateline := TProofline(localHead.Last);

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := templateline.fLineno + 1;
        fSubprooflevel := templateline.fSubprooflevel;
        fFormula := arrowFormula.CopyFormula;
        fJustification := ' �I';
        fFirstjustno := justno;
       end;

      localHead.InsertLast(newline);
     end;


*/




void doAandS(TGWTReAssemble leftReAss, TGWTReAssemble rightReAss){
  TFormula leftFormula, rightFormula;
  TProofline lastProofLine;

  leftFormula= (TFormula)(fTestNode.getLeftChild().fSuccedent.get(0));
  rightFormula= (TFormula)(fTestNode.getRightChild().fSuccedent.get(0));

  TGWTMergeData mergeData=new TGWTMergeData(leftReAss,rightReAss);

  mergeData.merge();

  fHead=mergeData.firstLocalHead;
  fLastAssIndex=mergeData.firstLastAssIndex;

  //localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno)

  lastProofLine=(TProofline)fHead.get(fHead.size()-1);

  TFormula formulanode=new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chAnd);
     formulanode.fLLink = leftFormula.copyFormula();
     formulanode.fRLink = rightFormula.copyFormula();


 TProofline newline = supplyProofline();

 newline.fLineno = lastProofLine.fLineno+1;
 newline.fSubprooflevel=lastProofLine.fSubprooflevel;
 newline.fFormula = formulanode;
 newline.fFirstjustno=mergeData.firstLineNum;
 newline.fSecondjustno=mergeData.secondLineNum;
 newline.fJustification = fAndIJustification;

 fHead.add(newline);
/*
   begin
        lastProofline := TProofline(localHead.Last);

        SupplyFormula(newformulanode);
        with newformulanode do
         begin
          fKind := Binary;
          fInfo := chAnd;
          fLlink := leftFormula.CopyFormula;
          fRlink := rightFormula.CopyFormula;
         end;

        SupplyProofline(newline);
        with newline do
         begin
          fLineno := lastProofline.fLineno + 1;
          fSubprooflevel := lastProofline.fSubprooflevel;
          fFormula := newformulanode;
          fFirstjustno := firstno;
          fSecondjustno := secondno;
          fJustification := ' ^I';
         end;

        localHead.InsertLast(newline);
        newline := nil;

        newformulanode := nil;

       end;

*/

}

/*

   procedure DoAandS (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer; var rightHead: Tlist; var rightlastAss: integer);

    var
     firstno, secondno: integer;
     leftFormula, rightFormula: TFormula;

    procedure AddAndAtEnd;

     var
      newformulanode: TFormula;
      lastProofline, newline: TProofline;

    begin
     lastProofline := TProofline(localHead.Last);

     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Binary;
       fInfo := chAnd;
       fLlink := leftFormula.CopyFormula;
       fRlink := rightFormula.CopyFormula;
      end;

     SupplyProofline(newline);
     with newline do
      begin
       fLineno := lastProofline.fLineno + 1;
       fSubprooflevel := lastProofline.fSubprooflevel;
       fFormula := newformulanode;
       fFirstjustno := firstno;
       fSecondjustno := secondno;
       fJustification := ' ^I';
      end;

     localHead.InsertLast(newline);
     newline := nil;

     newformulanode := nil;

    end;

   begin
    leftFormula := TFormula(thisnode.fLlink.fsucceedent.First);
    rightFormula := TFormula(thisnode.fRlink.fsucceedent.First);

    Merge(localHead, lastAssumption, rightHead, rightlastAss, firstno, secondno);
    AddAndAtEnd;
   end;


*/




void doAtomic(){
  TFormula newFormula;
  /*
  {One of the antecedents equals the succeedent}
  {In which case all that is needed is the one premise proof justified by TI}
  */

 newFormula=((TFormula)fTestNode.fSuccedent.get(0)).copyFormula();

   /*if (newFormula.fKind==TFormula.quantifier)
 remove; */

 TProofline newline = supplyProofline();

 newline.fLineno = 1;
 newline.fFormula = newFormula;
 newline.fJustification = fAssJustification;

 if (fHead==null)
   fHead=new ArrayList();

 fHead=new ArrayList();  //should start again Oct 08

 fHead.add(0,newline);
 fLastAssIndex=0;
}

/*

   procedure DoAtomic (thisnode: TTestNode; var localHead: Tlist; var lastAssumption: integer);

    var
     newline: TProofline;
     newFormula: TFormula;

             {One of the antecedents equals the succeedent}
             {In which case all that is needed is the one premise proof justified by TI}

   begin
    localHead := nil;
    localHead := newlist;

    newFormula := TFormula(thisnode.fsucceedent.First).CopyFormula;

    if newFormula.fKind = quantifier then
     RemoveInstantiatingInfo(newFormula);
                 { newFormula.fInfo := COPY(newFormula.fInfo, 1, 2); remove instantiating info}

    SupplyProofline(newline);
    with newline do
     begin
      fLineno := 1;
      fFormula := newFormula;
      fJustification := 'Ass';
     end;

    localHead.InsertFirst(newline);
    lastAssumption := 1;
   end;

*/

/************************* Direct Shortcut Methods ************************/

void buildDirectStart(TFormula first, TFormula second,TFormula conclusion,String justification){

 // some are one line starts, some two then they all end with the conclusion

   TProofline newline = supplyProofline();

 newline.fLineno = 1;
 newline.fFormula = first.copyFormula();
 newline.fJustification = fAssJustification;

 if (fHead==null)
    fHead=new ArrayList();

 fHead.add(0,newline);
 fLastAssIndex=0;

 newline = supplyProofline();

 if (second!=null){

   newline.fLineno = 2;
   newline.fFormula = second.copyFormula();
   newline.fJustification = fAssJustification;

   fHead.add(newline);
   fLastAssIndex += 1;
 }

newline = supplyProofline();

if (second!=null){
  newline.fLineno = 3;
  newline.fFirstjustno = 1;
  newline.fSecondjustno = 2;
}
else{
  newline.fLineno = 2;
  newline.fFirstjustno = 1;
}

newline.fFormula = conclusion.copyFormula();

newline.fJustification = justification;

fHead.add(newline);
}


/*

void doAndDirect(TFormula left, TFormula right,TFormula conclusion){
  TProofline newline = supplyProofline();

newline.fLineno = 1;
newline.fFormula = left.copyFormula();
newline.fJustification = fAssJustification;

if (fHead==null)
   fHead=new ArrayList();

fHead.add(0,newline);
fLastAssIndex=0;

 newline = supplyProofline();

 newline.fLineno = 2;
 newline.fFormula = right.copyFormula();
 newline.fJustification = fAssJustification;

fHead.add(newline);
fLastAssIndex+=1;

newline = supplyProofline();

newline.fLineno = 3;
newline.fFirstjustno = 1;
newline.fSecondjustno = 2;
newline.fFormula = conclusion.copyFormula();
newline.fJustification = fAndIJustification;

fHead.add(newline);

}

 */

  void doOrDirect(TFormula left, TFormula conclusion){
    TProofline newline = supplyProofline();

  newline.fLineno = 1;
  newline.fFormula = left.copyFormula();
  newline.fJustification = fAssJustification;

  if (fHead==null)
     fHead=new ArrayList();

  fHead.add(0,newline);
  fLastAssIndex=0;

  newline = supplyProofline();

  newline.fLineno = 2;
  newline.fFirstjustno = 1;
  newline.fFormula = conclusion.copyFormula();
  newline.fJustification = fOrIJustification;

  fHead.add(newline);

}


void doSimpDirect(TFormula left, TFormula conclusion){
  TProofline newline = supplyProofline();

newline.fLineno = 1;
newline.fFormula = left.copyFormula();
newline.fJustification = fAssJustification;

if (fHead==null)
   fHead=new ArrayList();

fHead.add(0,newline);
fLastAssIndex=0;

newline = supplyProofline();

newline.fLineno = 2;
newline.fFirstjustno = 1;
newline.fFormula = conclusion.copyFormula();
newline.fJustification = fAndEJustification;

fHead.add(newline);

}

  void doMPDirect(TFormula implic, TFormula conclusion){
    TProofline newline = supplyProofline();

  newline.fLineno = 1;
  newline.fFormula = implic.copyFormula();
  newline.fJustification = fAssJustification;

  if (fHead==null)
     fHead=new ArrayList();

  fHead.add(0,newline);
  fLastAssIndex=0;

  newline = supplyProofline();

  newline.fLineno = 2;
  newline.fFormula = implic.fLLink.copyFormula();
  newline.fJustification = fAssJustification;

  fHead.add(newline);
  fLastAssIndex+=1;

  newline = supplyProofline();

newline.fLineno = 3;
newline.fFirstjustno = 1;
  newline.fSecondjustno = 2;
newline.fFormula = conclusion.copyFormula();
newline.fJustification = fImplicEJustification;

fHead.add(newline);

}

/************************* End of Direct Shortcut Methods ************************/

/************************* Direct Shortcut Tests ************************/

class SimplificationTest implements FunctionalParameter{   //nextInList(ArrayList list,FunctionalParameter object,Object parameter, int index)

TFormula fConclusion;

public SimplificationTest(TFormula conclusion){
  fConclusion=conclusion;
}

public void execute (Object parameter){
  };


//the test is that the conclusion is the left conjunct of an and

public boolean testIt(Object parameter){

  if (fParser.isAnd((TFormula)parameter)&&
      fConclusion.equalFormulas(fConclusion,((TFormula)parameter).fLLink))
         return
             true;

  return
      false;
}
}

  class ImplicIfTest implements FunctionalParameter{

  TFormula fIf;
  TFormula fImplic=null;
  int index=-1;

  public ImplicIfTest(TFormula ifFormula){
    fIf=ifFormula;
  }

  public void execute (Object parameter){
    };


//the test is that the If is the left link of an implic

  public boolean testIt(Object parameter){

    if (fParser.isImplic((TFormula)parameter)&&
        fIf.equalFormulas(fIf,((TFormula)parameter).fLLink)){
        fImplic=(TFormula)parameter;

           return
               true;}

    return
        false;
  }
}


  class ImplicThenTest implements FunctionalParameter{

  TFormula fThen;
  TFormula fImplic=null;
  int index=-1;

  public ImplicThenTest(TFormula then){
    fThen=then;
  }

  public void execute (Object parameter){
    };


//the test is that the Then is the right link of an implic

  public boolean testIt(Object parameter){

    if (fParser.isImplic((TFormula)parameter)&&
        fThen.equalFormulas(fThen,((TFormula)parameter).fRLink)){
        fImplic=(TFormula)parameter;

           return
               true;}

    return
        false;
  }
}

class ThereTest implements FunctionalParameter{

TFormula fSearch;


public ThereTest(TFormula search){
  fSearch=search;
}

public void execute (Object parameter){
  };

public boolean testIt(Object parameter){

  if (fSearch.equalFormulas(fSearch,((TFormula)parameter))){
         return
             true;}

  return
      false;
}
}

/************************* End of Direct Shortcut Methods ************************/


boolean doDirectStepOptimization(){  //stub for override

  Object found=null;
  Object secondFound=null;

  ArrayList antecedents = fTestNode.fAntecedents;
  ArrayList succedent = fTestNode.fSuccedent;

  if ( (antecedents.size() > 0) && (succedent.size() > 0)) {
    TFormula conclusion = (TFormula) succedent.get(0);

    //trying Conj

    if (conclusion.isAnd(conclusion)) {
      TFormula left = conclusion.getLLink();
      TFormula right = conclusion.getRLink();

      if (left.formulaInList(antecedents) &&
          right.formulaInList(antecedents)) {

        buildDirectStart(left,right,conclusion,fAndIJustification);

       // doAndDirect(left, right, conclusion);
        return
            true;
      }
    }

    //trying Add

  if (fParser.isOr(conclusion)) {
    TFormula left = conclusion.getLLink();

    if (left.formulaInList(antecedents)) {

      doOrDirect(left, conclusion);
      return
          true;
    }
  }


   //trying Simp.

   SimplificationTest test = new SimplificationTest(conclusion);
   found=null;

   found= TUtilities.nextInList(antecedents,test,0);

   if (found!=null){
     doSimpDirect((TFormula)found, conclusion);
     return
         true;
   }

   //trying ModusPonens.   need to find both F->G and F

   ImplicThenTest mpTest = new ImplicThenTest(conclusion);
   Object implic=null;
   Object ifClause=null;

   implic= TUtilities.nextInList(antecedents,mpTest,0);

   boolean bothFound=false;

   while ((implic!=null)&&!bothFound){  //now we look for F
     ThereTest thereTest = new ThereTest(((TFormula)implic).fLLink);

     ifClause= TUtilities.nextInList(antecedents,thereTest,0);

     if (ifClause!=null)
       bothFound=true;
     else{
       int start = antecedents.indexOf(implic)+1;
       implic= TUtilities.nextInList(antecedents,mpTest,start);  // try further along the list
     }
   }

   if (bothFound){
     doMPDirect((TFormula)implic,conclusion);
     return
         true;
   }


  }

    return
        false;
}




void reAssembleProof(){
  int dummy;

  if (doDirectStepOptimization()){
           if (TConstants.DEBUG){
             System.out.print(strCR +"doDirectStepOptimization" + fTestNode.fStepType+
                              proofToString(fHead)+ "<br>");

           }

    return;}



  TGWTReAssemble leftReAss=null;
  TGWTReAssemble rightReAss=null;


  TTestNode leftChild=fTestNode.getLeftChild();
  TTestNode rightChild=fTestNode.getRightChild();

  if (leftChild!=null){

    if (TConstants.DEBUG){
    System.out.println(strCR +"ReAssemble going down left branch<br>");

 }



    leftReAss=supplyTReAssemble(leftChild,null,0);
    leftReAss.reAssembleProof();
  }
  if (rightChild!=null){

    if (TConstants.DEBUG){
System.out.println(strCR +"ReAssemble going down right branch<br>");

}

    rightReAss=supplyTReAssemble(rightChild,null,0);
    rightReAss.reAssembleProof();
  }


  if (TConstants.DEBUG){
    System.out.println(strCR +"ReAssemble, before type: " + fTestNode.fStepType+ "<br>");
    if (leftReAss!=null)
    System.out.println("ReAssemble before Left proof" + proofToString(leftReAss.fHead)+ "<br>");
  if (rightReAss!=null)
    System.out.println("ReAssemble before Right proof" + proofToString(rightReAss.fHead)+ "<br>");

 }




  if ((fTestNode.fStepType!=TTestNode.atomicS)  //can we prove a complex reductio?
      &&(fTestNode.fSuccedent.size()==0)

 //March 06 This next or clause is an attempt to make a shortcut for the same thing when the conclusion is Absurd
 // whether the repeated tests will bog anything down I don't know

      || ((fTestNode.fSuccedent.size()==1)
      && (TFormula.equalFormulas((TFormula)(fTestNode.fSuccedent.get(0)),
                                           TFormula.fAbsurd)))

      ){
         OptimizeR doer= new OptimizeR();

        if (doer.doOptimizeR())
          return;                              //bale out completely
  }


    switch (fTestNode.fStepType) {

      case (TTestNode.atomic):
         doAtomic();
         break;

      case (TTestNode.atomicS):{
         AtomicS doer = new AtomicS();
         doer.doAtomicS();
         }
         break;

      case (TTestNode.negatomicS):
      case (TTestNode.negandS):
      case (TTestNode.noreS):
      case (TTestNode.negarrowS):
      case (TTestNode.nequivS):
      case (TTestNode.neguniS):
      case (TTestNode.negexiS):
      case (TTestNode.negTypedUniS):
      case (TTestNode.negTypedExiS):
         doReductio(leftReAss);
         break;


      case (TTestNode.aand):

        doAand(leftReAss);

        break;

      case (TTestNode.aandS):

        doAandS(leftReAss,rightReAss);


        break;


      case (TTestNode.negand):

           {NegAnd doer= new NegAnd(leftReAss,rightReAss);

           doer.doNegAnd(leftReAss,rightReAss);


          break;}

        case (TTestNode.doubleneg):

        doDoubleNeg(leftReAss);

        break;


        /*
            doubleneg:
                begin
                 ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
                 DoDoubleNeg(thisnode, localHead, lastAssumption);
                end;

    */

 case (TTestNode.doublenegS):

      doDoubleNegS(leftReAss);

      break;

      case (TTestNode.implic):

        {

        Implic doer= new Implic(leftReAss,rightReAss);

        doer.doImplic(leftReAss,rightReAss);

         break;}



 case (TTestNode.arrowS):

      doImplicS(leftReAss);

      break;

    case (TTestNode.negarrow):

         doNegImplic(leftReAss);

      break;


 case (TTestNode.equivv):

    doEquivv(leftReAss);

    break;

  case (TTestNode.equivvS):

     doEquivvS(leftReAss,rightReAss);

    break;



 case (TTestNode.uni):

  doUni(leftReAss);

  break;


       case (TTestNode.uniS):

       doUniS(leftReAss);

       break;

     case (TTestNode.exiS):

     doExiS(leftReAss);

     break;

   case (TTestNode.negexi):

   doNegExi(leftReAss);

   break;

 case (TTestNode.neguni):

 doNegUni(leftReAss);

 break;



   case (TTestNode.exi):{

     doExi(leftReAss);

 /*  DoExi doer= new DoExi();

   doer.doExi(leftReAss); */

     break;
    }

    case (TTestNode.exiCV):{

    DoExiCV doer= new DoExiCV(leftReAss);

         doer.doExiCV(leftReAss);

      break;
    }



    case (TTestNode.typedExi):


   doTypedExi(leftReAss);

   break;

    case (TTestNode.typedUni):

   doTypedUni(leftReAss);

   break;


        case (TTestNode.typedUniS):

        doTypedRewriteS(leftReAss);

        break;

      case (TTestNode.negTypedUni):

   doNegTypedUni(leftReAss);

   break;



    case (TTestNode.typedExiS):

    doTypedRewriteS(leftReAss);

    break;

  case (TTestNode.negTypedExi):

doNegTypedExi(leftReAss);

break;











    case (TTestNode.ore):


              doOre(leftReAss,rightReAss);
break;

/*ore:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        ReAssembleProof(thisnode.frlink, rightHead, rightLastAss);
        DoOre(thisnode, localHead, lastAssumption, rightHead, rightLastAss);
       end;

    */
 case (TTestNode.oreS):
 {

      OreS doer= new OreS();


          doer.doOreS(leftReAss);
          break;}

/*
    oreS:
       DoOreS(thisnode, localHead, lastAssumption);
    */

   case (TTestNode.nore):


              doNore(leftReAss);
break;


   /*
        nore:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNore(thisnode, localHead, lastAssumption);
       end;

          */

  default:
    System.out.print("In Reasemble, no case called " + fTestNode.fStepType);
}



if (TConstants.DEBUG){
  System.out.println(strCR +"ReAssemble Steptype, after: " + fTestNode.fStepType+ "<br>");
  if (leftReAss!=null)
  System.out.println("ReAssemble After Left Proof"+ fTestNode.fStepType+ "<br>" + proofToString(leftReAss.fHead) + "<br>");
 }




}


/*

    procedure ReAssembleProof (thisnode: Ttestnode; var localHead: TList; var lastAssumption: integer);

  {check node a var param}

  {The steptype used to build a node is on its immediate successor}

   var
    rightHead: TList;
    rightLastAss: integer;
    dummy: integer;

  begin
   rightHead := nil;
   rightLastAss := 0;

   if (thisnode.fsteptype <> atomicS) and (thisnode.fsucceedent.fSize = 0) then
    begin
     if OptimizeR(thisnode, localHead, lastAssumption, rightHead, rightLastAss) then

  (* *)

      dummy := 0
     else
      dummy := 1;
    end
   else
    dummy := 1;

   if dummy = 1 then
    begin

     case thisnode.fsteptype of
      absurd:
       DoAbsurd(thisnode, localHead, lastAssumption);

      atomic:
       DoAtomic(thisnode, localHead, lastAssumption);

      atomicS:
       DoAtomicS(thisnode, localHead, lastAssumption);

      negatomicS, negandS, noreS, negarrowS, nequivS, negUniS, negExiS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoReductio(thisnode, localHead, lastAssumption);
       end;

      doubleneg:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoDoubleNeg(thisnode, localHead, lastAssumption);
       end;

      doublenegS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoDoubleNegS(thisnode, localHead, lastAssumption);
       end;

      aand:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoAand(thisnode, localHead, lastAssumption);
       end;

      aandS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        ReAssembleProof(thisnode.frlink, rightHead, rightLastAss);
        DoAandS(thisnode, localHead, lastAssumption, rightHead, rightLastAss);
       end;

      negand:
       DoNegAnd(thisnode, localHead, lastAssumption, rightHead, rightLastAss);

      ore:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        ReAssembleProof(thisnode.frlink, rightHead, rightLastAss);
        DoOre(thisnode, localHead, lastAssumption, rightHead, rightLastAss);
       end;

      oreS:
       DoOreS(thisnode, localHead, lastAssumption);

      nore:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNore(thisnode, localHead, lastAssumption);
       end;

      aroww:
       DoArrow(thisnode, localHead, lastAssumption, rightHead, rightLastAss);

      arrowS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoArrowS(thisnode, localHead, lastAssumption);
       end;

      negarrow:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNegArrow(thisnode, localHead, lastAssumption);
       end;

      equivv:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoEquivv(thisnode, localHead, lastAssumption);
       end;

      equivvS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        ReAssembleProof(thisnode.frlink, rightHead, rightLastAss);
        DoEquivvS(thisnode, localHead, lastAssumption, rightHead, rightLastAss);
       end;

      nequiv:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNEquiv(thisnode, localHead, lastAssumption);
       end;

      uni:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoUni(thisnode, localHead, lastAssumption);
       end;

      negUni:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNegUni(thisnode, localHead, lastAssumption);
       end;

      uniS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoUniS(thisnode, localHead, lastAssumption);
       end;

      uniSCV:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoUniSCV(thisnode, localHead, lastAssumption);
       end;

      uniSpec:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoUniSpec(thisnode, localHead, lastAssumption);
       end;

      exi:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoExi(thisnode, localHead, lastAssumption);
       end;

      exiCV:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoExiCV(thisnode, localHead, lastAssumption);
       end;

      negExi:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoNegExi(thisnode, localHead, lastAssumption);
       end;

      exiS:
       begin
        ReAssembleProof(thisnode.fllink, localHead, lastAssumption);
        DoExiS(thisnode, localHead, lastAssumption);
       end;

      otherwise
     end;

                 {$IFC myDebugging}

     if FALSE then
      begin
       writeln('leaving reassemble');
       derivlook(localHead);
       writeln('lastassumption is', lastAssumption);

       BugWriteDerivation(lastAssumption, thisnode, lastAssumption);
      end;

                 {$ENDC}

    end;
  end;



     */


void removeDummy(TFormula newFormula){
/*

 {When enters last line is a blank, due to closing subproof. Line before is dummy}

{This does one of two things depending on compiler options.  It comes with a contradiction}
{say, A^~A as its last line.  This really is only a place holder for the line numbers of the}
 {formulas which compose.  In one version that uses Absurd, the contradition is just}
{replaced by absurd; otherwise the lineonos are retrieved}

   */

TProofline newline,templateLine, searchline;

      templateLine=(TProofline)fHead.get(fHead.size()-1);//(*now pointing at blankline*)



if (TPreferencesData.fUseAbsurd/*TConstants.useAbsurd*/){
   newline = supplyProofline();
   newline.fLineno = templateLine.fLineno+1;
   newline.fFormula = new TFormula (TFormula.unary,
                             String.valueOf(chNeg),
                             null,
                             newFormula.copyFormula());

newline.fSubprooflevel = templateLine.fSubprooflevel;
newline.fJustification = TProofController.fNegIJustification;
newline.fFirstjustno=templateLine.fLineno;

fHead.add(newline);
}
      else{  //last line is blank, line before is dummy, line before that may be blank

         searchline=(TProofline)fHead.get(fHead.size()-3);

         /*     if searchline.fBlankline then {this means that the dummy comes immediately after}
              {an inner subproof and so needs extra lines}
      begin
       BreakDummy(localHead);
      end;


         */

        if (searchline.fBlankline)
          breakDummy(fHead);

        searchline=(TProofline)fHead.get(fHead.size()-2);  //points at dummy, we remove contradiction

        fHead.remove(searchline);// take it out

        ((TProofline)fHead.get(fHead.size()-1)).fLineno-=1; // decrement lineno on blankline
                                                            // not vital, but tidying

       // searchline.fLineno+=1;        NO INCOrrect                      // increment the dummy's lineno

        searchline.fFormula=new TFormula(
                                       TFormula.unary,
                                       String.valueOf(chNeg),
                                       null,
                                       newFormula);
        searchline.fSubprooflevel-= 1;

        searchline.fJustification=fNegIJustification;     // and change to newformula, leaving the just nos

        fHead.add(searchline); // put it back in (after the blankline)

      }

    }


/*

     procedure RemoveDummy (var localHead: TList; newformula: TFormula);
    {When enters last line is a blank, due to closing subproof. Line before is dummy}

    {This does one of two things depending on compiler options.  It comes with a contradiction}
    {say, A^~A as its last line.  This really is only a place holder for the line numbers of the}
    {formulas which compose.  In one version that uses Absurd, the contradition is just}
    {replaced by absured; otherwise the lineonos are retriefed}

      var
       searchline, newline: TProofline;
       newformulanode: TFormula;

    {$IFC useAbsurd}

     begin

      SupplyFormula(newformulanode);
      with newformulanode do
       begin
        fKind := Unary;
        fInfo := chNeg;
        fRlink := newformula.CopyFormula;
       end;

      searchline := TProofline(localHead.Last); (*now pointing at blankline*)

      SupplyProofline(newline);
      with newline do
       begin
        fLineno := searchline.fLineno + 1;
        fFormula := newformulanode;
        fSubprooflevel := searchline.fSubprooflevel;
        fFirstjustno := searchline.fLineno;
        fJustification := ' ~I';
       end;

      localHead.InsertLast(newline);

     end;

    {$ELSEC}

    begin
     searchline := TProofline(localHead.At(localHead.fSize - 2));

     if searchline.fBlankline then {this means that the dummy comes immediately after}
              {a subproof and so needs extra lines}
      begin
       BreakDummy(localHead);
      end;

     searchline := TProofline(localHead.At(localHead.fSize - 1)); { newline points at}
    {                                                                        dummy,removes it}

     localHead.Delete(searchline);
     searchline.fFormula.DismantleFormula;   (*remove contradiction*)

     SupplyFormula(newformulanode);
     with newformulanode do
      begin
       fKind := Unary;
       fInfo := chNeg;
       fRlink := newformula.CopyFormula;
      end;

              {with newline^ do}

     begin
      searchline.fSubprooflevel := searchline.fSubprooflevel - 1; {its jusnos stay same}
      searchline.fFormula := newformulanode;
      searchline.fJustification := ' ~I';
     end;

     localHead.InsertLast(searchline);

    end;

    {$ENDC}



 */


/*It is possible, though rare, to get duplicate assumptions. In which case we will remove one */

void removeDuplicateAss(){
  int i,j,limit,deletions;
  TProofline firstline,searchline;

  limit=fLastAssIndex;       //only assumptions
  i=0;
  deletions=0;

  while ((i+deletions)<limit){  //no need to do last, cannot be duplicate then
    firstline=(TProofline)fHead.get(i);

    if (!firstline.fBlankline){
      j=i+1;

      while ((j + deletions) <= limit){
        searchline = (TProofline) (fHead.get(j));
        if (!searchline.fBlankline &&
            TFormula.equalFormulas(firstline.fFormula, searchline.fFormula)
            ) {                      // delete searchline

                TProofListModel.reNumSingleLine(fHead, j, firstline.fLineno);
                fHead.remove(j);
                fLastAssIndex-=1;

                j-=1;
                deletions+=1;
           }
           j+=1;
        }
    }
    i+=1;
  }

}



















boolean transfer(int steptype, TFormula thisFormula){
  /*
{Finds this formula among the displayed Ass steps if it is there, justifies it and removes it to}
{body of proof. The found formula will not normally be the head. But if it is the singleton}
{Head then a blankstart is created and lastassumption removed}

  */

 boolean found=false;
 TProofline searchLine,foundLine=null;

 if ((fHead!=null)&&fHead.size()>0){


   removeDuplicateAss();   // new Dec 06-- rare case, two identical assumptions, we don't want to transfer just one

   for(int i=0;(i<=fLastAssIndex)&&(foundLine==null);i++){
     searchLine=(TProofline)fHead.get(i);
     if (!searchLine.fBlankline&&TFormula.equalFormulas(thisFormula,searchLine.fFormula))
       foundLine=searchLine;
   }

  if (foundLine!=null){
    if ((fLastAssIndex==0)&&(!((TProofline)fHead.get(0)).fBlankline)){     //single premise
      TProofListModel.increaseSubProofLevels(fHead,-1);
      createBlankStart(); // increments lastAss
    }
    fHead.remove(foundLine);
    fLastAssIndex-=1;

    foundLine.fFirstjustno=1000; // this is the number of the Head
    foundLine.fSubprooflevel=((TProofline)fHead.get(fLastAssIndex)).fSubprooflevel;

 /*
    searchIndex := 1;
      searchObject := localHead.FirstThat(Identical);

      if searchObject <> nil then
       begin
        if (lastAssumption = 1) then
         if not TProofline(localHead.First).fBlankline then {single premise}
          begin
           IncreaseSubProofLevels(localHead, -1); {decreases}
           CreateBlankStart(localHead, lastAssumption); {increments last ass}
          end;

        localHead.Delete(searchObject);
        lastAssumption := lastAssumption - 1;

        TProofline(searchObject).fFirstjustno := 1000; {this is the line number of added}
     {                                                               localHead}
        TProofline(searchObject).fSubprooflevel := TProofline(localHead.At(lastAssumption)).fSubprooflevel;

    !!!! end comes after switch
    */



    switch (steptype) {
      case (TTestNode.atomic):
        foundLine.fJustification=fAssJustification;
        foundLine.fFirstjustno=0;

        /*
    TProofline(searchObject).fJustification := 'Ass';
           TProofline(searchObject).fFirstjustno := 0; {no justno needed}

    */
        break;
      case (TTestNode.doubleneg):
        foundLine.fJustification=fNegEJustification;
        /*
    doubleneg:
          TProofline(searchObject).fJustification := ' ~E';
    */
        break;
      case (TTestNode.aand):
        foundLine.fJustification= fAndEJustification;
        /*aand:
          TProofline(searchObject).fJustification := ' ^E';
    */break;
      case (TTestNode.ore):
        foundLine.fJustification=fAssJustification;
        foundLine.fFirstjustno=0;

        /*ore:
          begin
           TProofline(searchObject).fJustification := 'Ass';
           TProofline(searchObject).fFirstjustno := 0; {no justno needed}
          end;
 */
        break;
      case (TTestNode.equivv):
        foundLine.fJustification=TProofController.equivEJustification;
        /*equivv:
          TProofline(searchObject).fJustification := ' �E';*/
        break;
      case (TTestNode.uni):
        foundLine.fJustification=TProofController.UIJustification;
        /*TProofline(searchObject).fJustification := ' UI'; */
        break;

      case (TTestNode.typedUni):
      case (TTestNode.negTypedUni):
foundLine.fJustification=TProofController.typedRewriteJustification;
/*TProofline(searchObject).fJustification := ' Typed'; */
break;

      case (TTestNode.typedExi):
      case (TTestNode.negTypedExi):
foundLine.fJustification=TProofController.typedRewriteJustification;
/*TProofline(searchObject).fJustification := ' Typed'; */
break;


      case (TTestNode.exi):
      case (TTestNode.exiCV):
        foundLine.fFirstjustno=0;
        break;
    }

    fHead.add(fLastAssIndex+1,foundLine);

  }
 }

  return
      foundLine!=null;
}

/*

     function Transfer (steptype: formulatype; thisformula: TFormula; var localHead: TList; var lastAssumption: integer): boolean;

      var
       searchObject: TObject;
       searchIndex: integer;

         {Finds this formula among the displayed Ass steps if it is there, justifies it and removes it to}
             {body of proof. The found formula will not normally be the head. But if it is the singleton}
               {Head then a blankstart is created and lastassumption removed}

      function Identical (item: TObject): boolean;

      begin
       if (searchIndex <= lastAssumption) and not TProofline(item).fBlankline then
        begin
         Identical := EqualFormulas(thisformula, TProofline(item).fFormula);
        end
       else
        Identical := false;
       searchIndex := searchIndex + 1;
      end;

     begin
      searchIndex := 1;
      searchObject := localHead.FirstThat(Identical);

      if searchObject <> nil then
       begin
        if (lastAssumption = 1) then
         if not TProofline(localHead.First).fBlankline then {single premise}
          begin
           IncreaseSubProofLevels(localHead, -1); {decreases}
           CreateBlankStart(localHead, lastAssumption); {increments last ass}
          end;

        localHead.Delete(searchObject);
        lastAssumption := lastAssumption - 1;

        TProofline(searchObject).fFirstjustno := 1000; {this is the line number of added}
     {                                                               localHead}
        TProofline(searchObject).fSubprooflevel := TProofline(localHead.At(lastAssumption)).fSubprooflevel;

        case steptype of

         atomic:
          begin
           TProofline(searchObject).fJustification := 'Ass';
           TProofline(searchObject).fFirstjustno := 0; {no justno needed}
          end;

         doubleneg:
          TProofline(searchObject).fJustification := ' ~E';

         aand:
          TProofline(searchObject).fJustification := ' ^E';

         ore:
          begin
           TProofline(searchObject).fJustification := 'Ass';
           TProofline(searchObject).fFirstjustno := 0; {no justno needed}
          end;

         equivv:
          TProofline(searchObject).fJustification := ' �E';

         uni:
          TProofline(searchObject).fJustification := ' UI';

         exi, exiCV:
          TProofline(searchObject).fFirstjustno := 0; {no justno needed}
         otherwise
        end;

        localHead.InsertBefore(lastAssumption + 1, searchObject);

       end;
      Transfer := (searchObject <> nil);
     end;

     */


void breakDummy(ArrayList localHead){
   TProofline newline,templateLine;//,firstLine;

   int insertIndex= localHead.size()-2;

   templateLine=(TProofline)localHead.get(localHead.size()-2);  //june 10 07 changed from get(localHead.size()-1);

   TFormula A = templateLine.fFormula.fLLink;
   TFormula notA = templateLine.fFormula.fRLink;


   newline = supplyProofline();
   newline.fLineno = 2001;
   newline.fFormula = A.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel;
   newline.fJustification = fAndEJustification;
   newline.fFirstjustno=templateLine.fLineno;

   insertIndex+=1;
   localHead.add(insertIndex,newline);

   //localHead.add(newline);

   newline = supplyProofline();
newline.fLineno = 2002;
newline.fFormula = notA.copyFormula();
newline.fSubprooflevel = templateLine.fSubprooflevel;
newline.fJustification = fAndEJustification;
newline.fFirstjustno=templateLine.fLineno;

   insertIndex+=1;
   localHead.add(insertIndex,newline);

//localHead.add(newline);

   newline = supplyProofline();
newline.fLineno = 2003;
newline.fFormula = templateLine.fFormula.copyFormula();
newline.fSubprooflevel = templateLine.fSubprooflevel;
newline.fJustification = fAndIJustification;
newline.fFirstjustno=2001;
   newline.fSecondjustno=2002;

   insertIndex+=1;
   localHead.add(insertIndex,newline);

//localHead.add(newline);

    }

/*

     procedure BreakDummy (var localHead: TList);

      var
       newline, templateline: TProofline;
              {The dummy contradiction has to be removed and its just nos used}
              {but sometime it occurs directly after a subproof in  which case it}
              {has to be split up and put together again first.The last line is usually a blank.}

     begin
      templateline := TProofline(localHead.At(localHead.fSize - 1));
      newline := nil;

      SupplyProofline(newline); {new A}
      with newline do
       begin
        fLineno := templateline.fLineno + 1;
        fFormula := templateline.fFormula.fLlink.CopyFormula;
        fFirstjustno := templateline.fLineno;
        fJustification := ' ^E';
        fSubprooflevel := templateline.fSubprooflevel;
       end;

      localHead.InsertBefore(localHead.fSize, newline);

      newline := nil;

      SupplyProofline(newline); {new not A}
      with newline do
       begin
        fLineno := templateline.fLineno + 2;
        fFormula := templateline.fFormula.fRlink.CopyFormula;
        fFirstjustno := templateline.fLineno;
        fJustification := ' ^E';
        fSubprooflevel := templateline.fSubprooflevel;
       end;

      localHead.InsertBefore(localHead.fSize, newline);

      newline := nil;

      SupplyProofline(newline); {new duplicate line (A ^ notA)}
      with newline do
       begin
        fLineno := templateline.fLineno + 3;
        fFormula := templateline.fFormula.CopyFormula;
        fJustification := ' ^I';
        fFirstjustno := templateline.fLineno + 1;
        fSecondjustno := templateline.fLineno + 2;
        fthirdjustno := 0;
        fSubprooflevel := templateline.fSubprooflevel;
       end;

      localHead.InsertBefore(localHead.fSize, newline);

      newline := nil;

 end;


 */

    protected void createBlankStart(){
          TProofline newline = supplyProofline();

          newline.fLineno=0;
          newline.fBlankline=true;
          newline.fFormula=null;
          newline.fSelectable=false;
          newline.fHeadlevel=-1;
          newline.fSubprooflevel=-1;
          newline.fJustification="";

          fHead.add(0,newline);

          fLastAssIndex+=1;

        }

/*

         procedure CreateBlankStart (var localHead: TList; var lastAssumption: integer);

           var
            newline: TProofline;

          begin
           SupplyProofline(newline);
           with newline do
            begin
             fLineno := 0;
             fBlankline := true;
             fSelectable := false;
             fSubprooflevel := -1;
             fJustification := strNull;
            end;

           localHead.InsertFirst(newline);
           lastAssumption := lastAssumption + 1;
          end;


      */

}
