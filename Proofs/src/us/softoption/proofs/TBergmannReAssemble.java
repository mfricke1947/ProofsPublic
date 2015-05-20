package us.softoption.proofs;

/*This is a Jan2014 Google GWT friendly version of 2012 TBergmannReAssemble */

import us.softoption.parser.TParser;
import us.softoption.interpretation.*;
import java.util.ArrayList;
import us.softoption.parser.TFormula;
import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;
import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;
import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;
import us.softoption.editor.*;
import us.softoption.parser.TBergmannParser;


/*Bergmann<-Gentzen*/


/* Note double negE used ie ~E in

Lemma 7 with or without absurd
doOr in PerhapsBreakItsDummy
doubleNeg transfer

 */

class TBergmannReAssemble extends TReAssemble{

  String fQNJustification = " QN";



  TBergmannReAssemble(TParser aParser, TTestNode aTestNode, ArrayList aHead,
                     int aLastAssIndex) {
    super(aParser, aTestNode, aHead, aLastAssIndex);

    fAndEJustification = " "+ TBergmannParser.chBergmannAnd + "E";
    fAndIJustification = " "+ TBergmannParser.chBergmannAnd + "I";




  }

  /************* Proofline factory ************/

  /* we want this to subclass for other types of proof eg Copi */

  public TProofline supplyProofline() {
    return
        new TProofline(fParser);
  }

  public TReAssemble supplyTReAssemble(TTestNode aTestNode, ArrayList aHead,
                                       int aLastAssIndex) {
    return
        new TBergmannReAssemble(fParser, aTestNode, aHead, aLastAssIndex);
  }

  /************* End of Proofline factory ************/





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
    newline.fJustification = TMyBergmannProofController.fEIJustification;

    fHead.add(newline);
  }
}




void doExi(TReAssemble leftReAss){

  TFormula scope=(TFormula)leftReAss.fTestNode.fAntecedents.get(0);
  TFormula variable=(TFormula)leftReAss.fTestNode.fAntecedents.get(1);
  TFormula constant=(TFormula)leftReAss.fTestNode.fAntecedents.get(2);

  fHead=leftReAss.fHead;
  fLastAssIndex=leftReAss.fLastAssIndex;

  int dummy = TMergeData.inPremises(fTestNode, fHead,
                                       fLastAssIndex, scope);

    if (dummy != -1) {

      TFormula oldScope=scope.copyFormula();

      TFormula.subTermVar(oldScope,variable,constant);  //put it back as it was


      exiFormula = new TFormula(TFormula.quantifier,
                                          String.valueOf(chExiquant),
                                          variable,
                                          oldScope);

      prependToHead(exiFormula);

      if (transfer(fTestNode.fStepType, scope))  // this is the instantiated scope
        ;

      convertToSubProof();
      addEIConc();
      numberLines();

    }
}


}




/********************  Lemmas *************************/

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



      newline = supplyProofline();
      newline.fLineno = 2003;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel + 2;
      newline.fJustification = TMyBergmannProofController.absIJustification;
      newline.fFirstjustno = 2002; // used to be 2000
      newline.fSecondjustno = 2000; // used to be 2002

      fHead.add(index, newline);
      index += 1;


      newline = supplyProofline();
      newline.fLineno = 2003;
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fBlankline = true;
      newline.fJustification = "";
      newline.fSelectable = false;

      fHead.add(index, newline);
      index += 1; //now at blankline


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

      fHead.add( /*index,*/newline);

      newline = supplyProofline();
      newline.fLineno = 2005;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel + 1;
      newline.fJustification = TMyBergmannProofController.absIJustification;
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


      newline = supplyProofline();
      newline.fLineno = 2006;
      newline.fFormula = orFormula.copyFormula();
      newline.fSubprooflevel = templateline.fSubprooflevel;
      newline.fJustification = TMyBergmannProofController.fNegEJustification;
      newline.fFirstjustno = 2005;  //absurd

      fHead.add( /*index,*/newline);

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



 newline = supplyProofline();
 newline.fLineno = 2003;
 newline.fSubprooflevel = templateline.fSubprooflevel;
 newline.fBlankline = true;
 newline.fJustification = "";
 newline.fSelectable = false;

 fHead.add(newline);


 newline = supplyProofline();
 newline.fLineno = 2004;
 newline.fFormula = orFormula.copyFormula();
 newline.fSubprooflevel = templateline.fSubprooflevel;
 newline.fJustification = TMyBergmannProofController.fNegEJustification;
 newline.fFirstjustno = 2000;
 newline.fSecondjustno = 2003;  //was 2

 fHead.add(newline);

}



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
newline.fJustification = TMyBergmannProofController.absIJustification;
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
newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
newline.fJustification = TMyBergmannProofController.absIJustification;
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
newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
newline.fJustification = TMyBergmannProofController.absIJustification;
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
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TMyBergmannProofController.fNegEJustification;
newline.fFirstjustno=2011; //absurd

fHead.add(insertIndex, newline);
insertIndex += 1;

}

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
newline.fJustification = TProofPanel.absIJustification;
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
newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
newline.fJustification = TProofPanel.absIJustification;
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
newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
newline.fJustification = TProofPanel.absIJustification;
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
newline.fFormula = notAornotB.copyFormula();
newline.fSubprooflevel = 0;
newline.fJustification = TMyBergmannProofController.fNegEJustification;
newline.fFirstjustno=2001;
newline.fSecondjustno=2008;

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
    newline.fJustification = TMyBergmannProofController.absIJustification;
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
    newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
    newline.fJustification = TMyBergmannProofController.absIJustification;
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
    newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
    newline.fJustification = TMyBergmannProofController.fNegEJustification;
    newline.fFirstjustno = 2008;
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
    newline.fJustification = TMyBergmannProofController.fNegIJustification;
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
    newline.fFormula = notnotAorB.fRLink.copyFormula();  //~AvB
    newline.fJustification = TMyBergmannProofController.fNegEJustification;
    newline.fFirstjustno = 2001;
    newline.fSecondjustno = 2006;
    newline.fSubprooflevel = 0;

    fHead.add(insertIndex, newline);
    insertIndex += 1;
     }


}


  void createLemma7WithAbsurd( int insertIndex){
/*
       { ~(A>B) as}
      {its first line and ~B as itsHead^.next the line after last assumption}
      {it proves the second from the first}

Bergmann does not need doubleneg elim
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
      newline.fJustification = TMyBergmannProofController.absIJustification;
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
      newline.fFormula = B.copyFormula();// notnotB.copyFormula();
      newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
      newline.fJustification = TMyBergmannProofController.fNegEJustification;
      newline.fFirstjustno = 2004;

      fHead.add(insertIndex, newline);
      insertIndex += 1;

   /*   newline = supplyProofline();
                newline.fLineno = 2006;
                newline.fFormula = B.copyFormula();
                newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
                newline.fJustification = TProofPanel.negEJustification;
                newline.fFirstjustno = 2005;

                fHead.add(insertIndex, newline);
      insertIndex += 1; */

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



 newline = supplyProofline();
   newline.fLineno = 2007;
   newline.fFormula = TFormula.fAbsurd.copyFormula();
   newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
   newline.fJustification = TMyBergmannProofController.absIJustification;
   newline.fFirstjustno = 2006;
   newline.fSecondjustno = 1;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
   newline.fLineno = 2007;
   newline.fSubprooflevel = templateLine.fSubprooflevel;
   newline.fBlankline = true;
   newline.fJustification = "";
   newline.fSelectable = false;

   fHead.add(insertIndex, newline);
   insertIndex += 1;

   newline = supplyProofline();
newline.fLineno = 2008;
newline.fFormula = A.copyFormula();
newline.fSubprooflevel = templateLine.fSubprooflevel;
newline.fJustification = TMyBergmannProofController.fNegEJustification;
newline.fFirstjustno = 2007;

fHead.add(insertIndex, newline);
insertIndex += 1;


/*
   newline = supplyProofline();
  newline.fLineno = 2009;
  newline.fFormula = notnotA.copyFormula();
  newline.fSubprooflevel = templateLine.fSubprooflevel;
  newline.fJustification = TProofPanel.fNegIJustification;
  newline.fFirstjustno = 2008;

  fHead.add(insertIndex, newline);
  insertIndex += 1;


   TProofline nextLine=(TProofline)fHead.get(insertIndex);

   nextLine.fFirstjustno=2009;
   nextLine.fJustification=fNegEJustification;
   nextLine.fSubprooflevel=templateLine.fSubprooflevel; */



}

  void createLemma9(int insertIndex){  // we'll use a rewrite rule
    /*
       {The proof has ~allxFx as}
       {its first line and Exn~Fx  itsHead^.next the line after last assumption}
     */


    TProofline exNotLine = (TProofline) fHead.get(insertIndex + 1);

    exNotLine.fJustification=fQNJustification;
    exNotLine.fFirstjustno=1;

  }

  void createLemma10(int insertIndex){
      /*
         {The proof has ~ExlxFx as}
         {its first line and Allxn~Fx  itsHead^.next the line after last assumption}
       */



      TProofline allNotLine = (TProofline) fHead.get(insertIndex + 1);

      allNotLine.fJustification=fQNJustification;
      allNotLine.fFirstjustno=1;
     }


/*
  void createLemma9WithAbsurd(int insertIndex){

       {The proof has ~allxFx as}
       {its first line and Exn~Fx  itsHead^.next the line after last assumption}


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
           newline.fJustification = TProofPanel.EGJustification;
           newline.fFirstjustno=2002;

           fHead.add(insertIndex+1, newline);
  insertIndex += 1;


           newline = supplyProofline();
               newline.fLineno = 2004;
               newline.fFormula = TFormula.fAbsurd.copyFormula();
               newline.fJustification = TProofPanel.absIJustification;
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
               newline.fFormula = Fx.copyFormula();

               newline.fJustification = TProofPanel.fNegEJustification;
             newline.fSubprooflevel = 1;
               newline.fFirstjustno = 2004;

               fHead.add(insertIndex+1, newline);
    insertIndex += 1;



    newline = supplyProofline();
             newline.fLineno = 2006;
             newline.fFormula = AllxFx.copyFormula();

             newline.fJustification = TProofPanel.UGJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2005;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;

             newline = supplyProofline();
      newline.fLineno = 2007;
      newline.fFormula = TFormula.fAbsurd.copyFormula();
      newline.fJustification = TProofPanel.absIJustification;
    newline.fSubprooflevel = 1;
      newline.fFirstjustno = 2006;
      newline.fSecondjustno=1;

      fHead.add(insertIndex+1, newline);
    insertIndex += 1;

    newline = supplyProofline();
      newline.fLineno = 2007;
      newline.fBlankline = true;
      newline.fJustification = "";
    newline.fSubprooflevel = 0;
      newline.fSelectable = false;


      fHead.add(insertIndex+1, newline);
    insertIndex += 1;





      exNotLine.fFirstjustno=2007;   //existing line
      exNotLine.fJustification = TProofPanel.negEJustification;




  }

    void createLemma9WithoutAbsurd(int insertIndex){

       {The proof has ~allxFx as}
       {its first line and Exn~Fx  itsHead^.next the line after last assumption}


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
           newline.fJustification = TProofPanel.EGJustification;
           newline.fFirstjustno=2002;

           fHead.add(insertIndex+1, newline);
  insertIndex += 1;


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
               newline.fFormula = Fx.copyFormula();

               newline.fJustification = TProofPanel.negEJustification;
             newline.fSubprooflevel = 1;
               newline.fFirstjustno = 2003;
               newline.fSecondjustno = 2001;

               fHead.add(insertIndex+1, newline);
    insertIndex += 1;

    newline = supplyProofline();
             newline.fLineno = 2005;
             newline.fFormula = AllxFx.copyFormula();

             newline.fJustification = TProofPanel.UGJustification;
           newline.fSubprooflevel = 1;
             newline.fFirstjustno = 2004;

             fHead.add(insertIndex+1, newline);
  insertIndex += 1;


      fHead.add(insertIndex+1, newline);
    insertIndex += 1;

    newline = supplyProofline();
      newline.fLineno = 2005;
      newline.fBlankline = true;
      newline.fJustification = "";
    newline.fSubprooflevel = 0;
      newline.fSelectable = false;


      fHead.add(insertIndex+1, newline);
    insertIndex += 1;





      exNotLine.fFirstjustno=2005;   //existing line
      newline.fSecondjustno = 1;
      exNotLine.fJustification = TProofPanel.negEJustification;




}

*/

/******************** End of Lemmas ******************/

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
          newline.fJustification = TMyBergmannProofController.fAndEJustification;
          newline.fFirstjustno=templateline.fLineno;


          secondTailIndex+=1;
          fHead.add(secondTailIndex,newline);

          newline=supplyProofline();

          newline.fLineno = templateline.fLineno+2;
          newline.fSubprooflevel= templateline.fSubprooflevel;  //check?
          newline.fFormula = templateline.getFormula().getRLink().copyFormula();  // ~B
          newline.fJustification = TMyBergmannProofController.fAndEJustification;
          newline.fFirstjustno=templateline.fLineno;


          secondTailIndex+=1;
          fHead.add(secondTailIndex,newline);

          newline=supplyProofline();

          newline.fLineno = templateline.fLineno+3;
          newline.fSubprooflevel= templateline.fSubprooflevel;
          newline.fFormula = templateline.getFormula().copyFormula();  // B^~B
          newline.fJustification = TMyBergmannProofController.fAndIJustification;
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
       newline.fBlankline = true;
       newline.fSelectable = false;

       secondTailIndex+=1;
       fHead.add(secondTailIndex,newline);

       /*
       newline=supplyProofline();                             //super does this

       newline.fLineno = templateline.fLineno+1;
       newline.fSubprooflevel= subprooflevel;  //check?
       newline.fFormula = notnotContra;  //~~(A^~A)
       newline.fJustification = fNegIJustification;
       newline.fFirstjustno = templateline.fFirstjustno;       //using template lines justnos for B and ~B
       newline.fSecondjustno = templateline.fSecondjustno;

       secondTailIndex+=1;
       fHead.add(secondTailIndex,newline);  */

       newline=supplyProofline();

       newline.fLineno = templateline.fLineno+1;
       newline.fSubprooflevel= subprooflevel;  //check?
       newline.fFormula = contra;  //(A^~A)
       newline.fJustification = fNegEJustification;
       newline.fFirstjustno = templateline.fFirstjustno;
       newline.fSecondjustno = templateline.fSecondjustno;

       secondTailIndex+=1;
       fHead.add(secondTailIndex,newline);

       templateline.fFirstjustno=0;       //surgery has made template line an assumption, setting justnos to 0
       templateline.fSecondjustno=0;

      }

        TProofline newline=supplyProofline();

        newline.fLineno = ((TProofline)(fHead.get(secondTailIndex))).fLineno+1;
        newline.fSubprooflevel= ((TProofline)(fHead.get(secondTailIndex))).fSubprooflevel-1;  //check?
        newline.fFormula = firstTailFormula.copyFormula();
        newline.fJustification = TMyBergmannProofController.fOrEJustification;
        newline.fFirstjustno=orJustNum;
        newline.fSecondjustno=((TProofline)(fHead.get(firstTailIndex))).fLineno;
        newline.fThirdjustno=((TProofline)(fHead.get(secondTailIndex))).fLineno;

        secondTailIndex+=1; //now pointing at blank linw
        fHead.add(secondTailIndex+1,newline);
                               //do I need to increment secondTailIndex



}







  void doAtomicSNoOptimize(TFormula conclusionFormula){

    /* the caller has set fLastAssIndex=1  */

/*   we are trying to get from A,~A to C, usually this will be


   |A
   |~A
   |__
   ||_~C
   ||Absurd
   |~~C         In Bergmann don't need this
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
newline.fJustification = TMyBergmannProofController.absIJustification;
newline.fSubprooflevel = 1;

fHead.add(newline);
}

endSubProof();

  newline = supplyProofline();   // C

  newline.fLineno = TPreferencesData.fUseAbsurd?5:4;
  newline.fFormula = C.copyFormula();
  newline.fFirstjustno=TPreferencesData.fUseAbsurd?4:1;
  newline.fSecondjustno=TPreferencesData.fUseAbsurd?0:2;
  newline.fJustification = TMyBergmannProofController.fNegEJustification;


fHead.add(newline);

/*
newline = supplyProofline();   // not not C

newline.fLineno = TPreferencesData.fUseAbsurd?5:4;
newline.fFormula = notnotC.copyFormula();
newline.fFirstjustno=TPreferencesData.fUseAbsurd?4:1;
newline.fSecondjustno=TPreferencesData.fUseAbsurd?0:2;
newline.fJustification = TProofPanel.fNegIJustification;


fHead.add(newline);

newline = supplyProofline();   //  C

newline.fLineno = TPreferencesData.fUseAbsurd?6:5;
newline.fFormula = C.copyFormula();
newline.fFirstjustno=TPreferencesData.fUseAbsurd?5:4;
newline.fJustification = TProofPanel.negEJustification;


fHead.add(newline);  */


if (TConstants.DEBUG){
    System.out.println(strCR +"Leaving Bergmann doAtomicSNoOptimize <br>");

    System.out.println(proofToString(fHead)+ "<br>");
 }


  }


void doExi(TReAssemble leftReAss){

    DoExi doer = new DoExi();

    doer.doExi(leftReAss);
 }


 void noOptimizeImplic(TReAssemble leftReAss, TReAssemble rightReAss,
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

           TMergeData mergeData = new TMergeData(leftReAss, rightReAss);

           mergeData.merge();

           fHead = mergeData.firstLocalHead;
           fLastAssIndex = mergeData.firstLastAssIndex;

           createLemma4(fLastAssIndex + 1, TPreferencesData.fUseAbsurd); //this adds nine (was ten) or seven (was eight) lines ending in `AvB
           numberLines();

           int firstTailIndex = fHead.indexOf(firstConc);
           int secondTailIndex = fHead.indexOf(secondConc);

           if (TPreferencesData.fUseAbsurd) {
             addOrConc(firstTailIndex, secondTailIndex,
                       ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 9);
           }
           else {
             addOrConc(firstTailIndex, secondTailIndex,
                       ( (TProofline) (fHead.get(fLastAssIndex))).fLineno + 7);
           }


        }




class EquivThenTest implements FunctionalParameter{

TFormula fThen;
TFormula fEquiv=null;
boolean fRight=true;
int index=-1;

public EquivThenTest(TFormula then, boolean right){
  fThen=then;
  fRight=right;
}

public void execute (Object parameter){
  };


//the test is that the Then is the right link of an equiv

public boolean testIt(Object parameter){

  if (fParser.isEquiv((TFormula)parameter)){
    if (fRight&&fThen.equalFormulas(fThen, ( (TFormula) parameter).fRLink)) {
      fEquiv = (TFormula) parameter;
      return
          true;
    }

    if (!fRight&&fThen.equalFormulas(fThen, ( (TFormula) parameter).fLLink)) {
      fEquiv = (TFormula) parameter;
      return
          true;
    }

  }

 return
      false;
}

}




  boolean doDirectStepOptimization(){  //stub for override


  if (!super.doDirectStepOptimization()){  // let super try first

    Object found = null;
    Object secondFound = null;

    ArrayList antecedents = fTestNode.fAntecedents;
    ArrayList succedent = fTestNode.fSuccedent;

    if ( (antecedents.size() > 0) && (succedent.size() > 0)) {
      TFormula conclusion = (TFormula) succedent.get(0);

      /*might be able to optimize on Equiv */

      //trying Equiv.   need to find both F<->G and F  (on left or right)

      boolean onRight=true;       //trying F and F<->G

      EquivThenTest mpTest = new EquivThenTest(conclusion,onRight);
      Object implic = null;
      Object ifClause = null;

      implic = TUtilities.nextInList(antecedents, mpTest, 0);

      boolean bothFound = false;

      while ( (implic != null) && !bothFound) { //now we look for F
        ThereTest thereTest = new ThereTest( ( (TFormula) implic).fLLink);

        ifClause = TUtilities.nextInList(antecedents, thereTest, 0);

        if (ifClause != null)
          bothFound = true;
        else {
          int start = antecedents.indexOf(implic) + 1;
          implic = TUtilities.nextInList(antecedents, mpTest, start); // try further along the list
        }
      }

      if (bothFound) {
        doEquivDirect( (TFormula) implic,((TFormula) implic).fLLink, conclusion);
        return
            true;
      }

      //onRight=false;       //trying F and G<->F

      mpTest = new EquivThenTest(conclusion,!onRight);
      implic = null;
      ifClause = null;

      implic = TUtilities.nextInList(antecedents, mpTest, 0);

      bothFound = false;

      while ( (implic != null) && !bothFound) { //now we look for F
        ThereTest thereTest = new ThereTest( ( (TFormula) implic).fRLink);

        ifClause = TUtilities.nextInList(antecedents, thereTest, 0);

        if (ifClause != null)
          bothFound = true;
        else {
          int start = antecedents.indexOf(implic) + 1;
          implic = TUtilities.nextInList(antecedents, mpTest, start); // try further along the list
        }
      }

      if (bothFound) {
        doEquivDirect( (TFormula) implic,((TFormula) implic).fRLink, conclusion);
        return
            true;
      }

    }



  }

  return
      false;

  }


/*************  doubleNeg, Bergmann does not have this Rule ********/

  /* we go ~~A
            ~A   Ass
            A  1,2 NegE  */

  void doDoubleNeg(TReAssemble leftReAss) {
   fHead = leftReAss.fHead;
   fLastAssIndex = leftReAss.fLastAssIndex;

   TFormula A = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));  //A

   int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                      leftReAss.fLastAssIndex, A); //not sure

   if (dummy1 != -1) {
     TFormula notA = new TFormula();

     notA.fKind = TFormula.unary;
     notA.fInfo = String.valueOf(chNeg);
     notA.fRLink = A;

     TFormula notnotA = new TFormula();

     notnotA.fKind = TFormula.unary;
     notnotA.fInfo = String.valueOf(chNeg);
     notnotA.fRLink = notA;

     prependToHead(notnotA);

     if (transfer(fTestNode.fStepType, A)){ // {Moves the formula it justifies into body of proof}
       numberLines();

       {
         TProofline newline,templateLine,firstLine,implicLine;

         int insertIndex=leftReAss.fLastAssIndex+1;

        firstLine=(TProofline)fHead.get(0);
        templateLine=(TProofline)fHead.get(insertIndex-1);
        implicLine=(TProofline)fHead.get(insertIndex);


        newline = supplyProofline();
        newline.fLineno = 2001;
        newline.fFormula = notA.copyFormula();
        newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
        newline.fJustification = fAssJustification;
        newline.fLastassumption = true;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
         newline.fLineno = 2001;
         newline.fSubprooflevel = templateLine.fSubprooflevel;
         newline.fBlankline = true;
         newline.fJustification = "";
         newline.fSelectable = false;

         fHead.add(insertIndex, newline);
         insertIndex += 1; //now at existing line after blankline


         TProofline nextLine=(TProofline)fHead.get(insertIndex);

         nextLine.fFirstjustno=1;
         nextLine.fSecondjustno=2001;
         nextLine.fJustification=fNegEJustification;
         nextLine.fSubprooflevel=templateLine.fSubprooflevel;

       }




     }
   }

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

newline = supplyProofline();
 newline.fLineno = 2003;
 newline.fSubprooflevel = templateLine.fSubprooflevel+2;
 newline.fBlankline = true;
 newline.fJustification = "";
 newline.fSelectable = false;

 fHead.add(insertIndex, newline);
 insertIndex += 1;
/*
 newline = supplyProofline();
    newline.fLineno = 2004;
    newline.fFormula = notnotB.copyFormula();
    newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
    newline.fJustification = TProofPanel.fNegIJustification;
    newline.fFirstjustno = 2001;
    newline.fSecondjustno = 2002;

    fHead.add(insertIndex, newline);
    insertIndex += 1;  */

    newline = supplyProofline();
              newline.fLineno = 2004;
              newline.fFormula = B.copyFormula();
              newline.fSubprooflevel = templateLine.fSubprooflevel + 2;
              newline.fJustification = TMyBergmannProofController.negEJustification;
              newline.fFirstjustno = 2001;
    newline.fSecondjustno = 2002;

              fHead.add(insertIndex, newline);
    insertIndex += 1;

    newline = supplyProofline();
        newline.fLineno = 2004;
        newline.fSubprooflevel = templateLine.fSubprooflevel+1;
        newline.fBlankline = true;
        newline.fJustification = "";
        newline.fSelectable = false;

        fHead.add(insertIndex, newline);
        insertIndex += 1; //now at line after blankline

newline = supplyProofline();
newline.fLineno = 2005;
newline.fFormula = AarrowB.copyFormula();  // A -> B
newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
newline.fJustification = fImplicIJustification;
newline.fFirstjustno = 2004;

fHead.add(insertIndex, newline);
insertIndex += 1;

/*

newline = supplyProofline();
 newline.fLineno = 2008;
 newline.fFormula = TFormula.fAbsurd.copyFormula();
 newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
 newline.fJustification = TProofPanel.absIJustification;
 newline.fFirstjustno = 2007;
 newline.fSecondjustno = 1;

 fHead.add(insertIndex, newline);
 insertIndex += 1; */

 newline = supplyProofline();
 newline.fLineno = 2005;
 newline.fSubprooflevel = templateLine.fSubprooflevel;
 newline.fBlankline = true;
 newline.fJustification = "";
 newline.fSelectable = false;

 fHead.add(insertIndex, newline);
 insertIndex += 1;

 TProofline nextLine=(TProofline)fHead.get(insertIndex);

 nextLine.fJustification=fNegEJustification;
 nextLine.fSubprooflevel=templateLine.fSubprooflevel;
 nextLine.fFirstjustno = 2005;
 nextLine.fSecondjustno = 1;


 /*
    TProofline(localHead.At(insertHere)).fFirstjustno := 2009;
TProofline(localHead.At(insertHere)).fJustification := ' ~E';


 */

}



 /******************* endOfDoubleNeg ********************************************************/



 void createLemmaBergmann1(int insertIndex){
     /*
             { (A<->B) as}
            {its first line and A->B (or B->A) as itsHead^.next the line after last assumption}
            {it proves the second from the first}


      */

        TProofline newline,templateLine,firstLine,implicLine;

        firstLine=(TProofline)fHead.get(0);
        templateLine=(TProofline)fHead.get(insertIndex-1);
        implicLine=(TProofline)fHead.get(insertIndex);

        TFormula antecedent = implicLine.fFormula.fLLink;
        TFormula consequent = implicLine.fFormula.fRLink;


        newline = supplyProofline();
        newline.fLineno = 2001;
        newline.fFormula = antecedent.copyFormula();
        newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
        newline.fJustification = fAssJustification;
        newline.fLastassumption = true;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
        newline.fLineno = 2002;
        newline.fFormula = consequent.copyFormula();
        newline.fSubprooflevel = templateLine.fSubprooflevel + 1;
        newline.fJustification = fEquivEJustification;
        newline.fFirstjustno = 1;
        newline.fSecondjustno = 2001;

        fHead.add(insertIndex, newline);
        insertIndex += 1;

        newline = supplyProofline();
         newline.fLineno = 2002;
         newline.fSubprooflevel = templateLine.fSubprooflevel;
         newline.fBlankline = true;
         newline.fJustification = "";
         newline.fSelectable = false;

         fHead.add(insertIndex, newline);
         insertIndex += 1; //now at existing line after blankline


         TProofline nextLine=(TProofline)fHead.get(insertIndex);

         nextLine.fFirstjustno=2002;
         nextLine.fJustification=fImplicIJustification;
         nextLine.fSubprooflevel=templateLine.fSubprooflevel;


 }


  void doEquivv(TReAssemble leftReAss) {
   fHead = leftReAss.fHead;
   fLastAssIndex = leftReAss.fLastAssIndex;

   TFormula AarrowB = (TFormula) (leftReAss.fTestNode.fAntecedents.get(0));
   TFormula BarrowA = (TFormula) (leftReAss.fTestNode.fAntecedents.get(1));

   int dummy1 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                      leftReAss.fLastAssIndex, AarrowB); //not sure
   int dummy2 = TMergeData.inPremises(fTestNode, leftReAss.fHead,
                                      leftReAss.fLastAssIndex, BarrowA);

   if ( (dummy1 != -1) || (dummy2 != -1)) {
     TFormula formulanode = new TFormula();

     formulanode.fKind = TFormula.binary;
     formulanode.fInfo = String.valueOf(chEquiv);
     formulanode.fLLink = AarrowB.fLLink;
     formulanode.fRLink = AarrowB.fRLink;

     prependToHead(formulanode);

     if (transfer(fTestNode.fStepType, AarrowB)){ // {Moves the left implic A->B it justifies into body of proof}
       numberLines();
       createLemmaBergmann1(fLastAssIndex+1);  // can alter fLastAssIndex
       numberLines();
     }
       ;

       if (transfer(fTestNode.fStepType, BarrowA)){ // {Moves the left implic B->A it justifies into body of proof}
         numberLines();
         createLemmaBergmann1(fLastAssIndex+1);
         numberLines();
       }

       ;

     numberLines();

   }

 }






  void doEquivDirect(TFormula equiv, TFormula ifFormula, TFormula conclusion){
    TProofline newline = supplyProofline();

  newline.fLineno = 1;
  newline.fFormula = equiv.copyFormula();
  newline.fJustification = fAssJustification;

  if (fHead==null)
     fHead=new ArrayList();

  fHead.add(0,newline);
  fLastAssIndex=0;

  newline = supplyProofline();

  newline.fLineno = 2;
  newline.fFormula = ifFormula.copyFormula();
  newline.fJustification = fAssJustification;

  fHead.add(newline);
  fLastAssIndex+=1;

  newline = supplyProofline();

newline.fLineno = 3;
newline.fFirstjustno = 1;
  newline.fSecondjustno = 2;
newline.fFormula = conclusion.copyFormula();
newline.fJustification = fEquivEJustification;

fHead.add(newline);

}






}


