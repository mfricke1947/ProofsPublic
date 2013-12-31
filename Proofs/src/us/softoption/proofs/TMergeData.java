package us.softoption.proofs;

/*This is a Feb2013 Google GWT friendly version of 2012 TMergeData */

import javax.swing.UIManager;
import java.awt.*;
import javax.swing.*;

import java.util.*;


import us.softoption.interpretation.*;
import us.softoption.parser.*;
import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;


class TMergeData {
  ArrayList firstLocalHead;
  int firstLastAssIndex=-1;
  ArrayList secondLocalHead;
  int secondLastAssIndex=-1;
  int firstLineNum=-1;          // the line numbers of the returned conclusions
  int secondLineNum=-1;
private
  int firstTailIndex;
private
  int secondTailIndex;
  TProofline fSampleLine;      // need this 'cos varies from system to system (use only one)

final static int noPremNoConc=1;
final static int noPremConc=2;
final static int premNoConc=3;
final static int premConc=4;
final static int pfFinished=5;



  TMergeData (ArrayList firstHead,
    int firstAssumption,
    ArrayList secondHead,
    int secondAssumption,
    int first,
    int second,
    TProofline sample){

  firstLocalHead=firstHead;
  firstLastAssIndex=firstAssumption;
  secondLocalHead=secondHead;
  secondLastAssIndex=secondAssumption;
  firstLineNum=first;
  secondLineNum=second;

  fSampleLine=sample;
  }

  TMergeData (TReAssemble firstReAss,TReAssemble secondReAss){

    firstLocalHead=firstReAss.fHead;
    firstLastAssIndex=firstReAss.fLastAssIndex;
    secondLocalHead=secondReAss.fHead;
    secondLastAssIndex=secondReAss.fLastAssIndex;
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




    public static int getProofType(ArrayList head, int lastAssIndex){
        if ( ( (TProofline) head.get(0)).fBlankline)
          return
              noPremConc;
        else {
          if (lastAssIndex == (head.size() - 1)) // I think this is right
            return
                premNoConc;
          else
            return
                premConc;

        }

      }




void doNoPremConcNoPremConc(){
  /*
     begin
           garbageLIne := TProofline(secondlocalHead.First);
           secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
           garbageLIne.DismantleProofline;
           secondlastAssumption := secondlastAssumption - 1;

           AddSecondTail;

           RenumberLines(firstlocalHead, 2000);
           RenumberLines(firstlocalHead, 1);

           firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
           secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
          end;

  */


   secondLocalHead.remove(0);   /*drop second blank start */
   secondLastAssIndex-=1;

   addSecondTail();

   TProofListModel.renumberLines(firstLocalHead,2000);
   TProofListModel.renumberLines(firstLocalHead,1);

   firstLineNum= ((TProofline)firstLocalHead.get((firstTailIndex))).fLineno;
   secondLineNum= ((TProofline)firstLocalHead.get((secondTailIndex))).fLineno;

}

 void doNoPremConcPremConc(){

   TProofListModel.increaseSubProofLevels(firstLocalHead,+1);
  sift();
  addSecondTail();   // {This places firstTail at end of first proof}
  firstLocalHead.remove(0); // the blankline

  firstLastAssIndex-=1;
  firstTailIndex-=1;
  secondTailIndex-=1;

  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= ((TProofline)firstLocalHead.get(firstTailIndex)).fLineno;
  secondLineNum= ((TProofline)firstLocalHead.get(secondTailIndex)).fLineno;


 }

/*

  begin
         IncreaseSubProofLevels(firstlocalHead, +1);
         Sift;
         AddSecondTail; {This places firstTail at end of first proof}

         garbageLIne := TProofline(firstlocalHead.First);
         firstlocalHead.Delete(firstlocalHead.First);
         garbageLIne.DismantleProofline; {drop firstblankstart}

         firstlastAssumption := firstlastAssumption - 1;
         firstTail := firstTail - 1;
         secondTail := secondTail - 1;


         RenumberLines(firstlocalHead, 2000);
         RenumberLines(firstlocalHead, 1);

         firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
         secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
        end;


 */





void doNoPremConcPremNoConc(){
  TProofListModel.increaseSubProofLevels(firstLocalHead,+1);
  sift();
  firstLocalHead.remove(0); // the blankline

  firstLastAssIndex-=1;

  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= ((TProofline)firstLocalHead.get(firstLocalHead.size()-1)).fLineno;
  secondLineNum= ((TProofline)firstLocalHead.get(firstLastAssIndex)).fLineno;
}

/* begin
        IncreaseSubProofLevels(firstlocalHead, +1);
        Sift;

        garbageLIne := TProofline(firstlocalHead.First);
        firstlocalHead.Delete(firstlocalHead.First);
        garbageLIne.DismantleProofline; {drop firstblankstart}

        firstlastAssumption := firstlastAssumption - 1;

        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.Last).fLineno;
        secondline := TProofline(firstlocalHead.At(firstlastAssumption)).fLineno;
       end;
*/



void doPremNoConcNoPremConc(){

  TProofListModel.increaseSubProofLevels(firstLocalHead,+1);

  secondLocalHead.remove(0); // the blankline of second

  secondLastAssIndex+=1;  // not sure why it is incremented? should be decremented?

  addSecondTail();

  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= 1;
  secondLineNum= ((TProofline)firstLocalHead.get(secondTailIndex)).fLineno;

  /*{the}
  {                                   single line premise comes on front}*/


}

/*

 NoPremConc:
        begin
         IncreaseSubProofLevels(secondlocalHead, +1);
         garbageLIne := TProofline(secondlocalHead.First);
         secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
         garbageLIne.DismantleProofline;
         secondlastAssumption := secondlastAssumption + 1;

                                {drop secondblankstart,}
  {                                                                         loses second lastass}

         AddSecondTail;

         RenumberLines(firstlocalHead, 2000);
         RenumberLines(firstlocalHead, 1);

         firstline := 1; {check}
         secondline := TProofline(firstlocalHead.At(secondTail)).fLineno; {the}
  {                                   single line premise comes on front}
        end;


*/

void doPremConcPremNoConc(){

  TFormula secondFormula=((TProofline)secondLocalHead.get(0)).fFormula;

  secondLineNum=inPremises(null,firstLocalHead,firstLastAssIndex,secondFormula);

  if (secondLineNum==-1)
    secondLineNum=(((TProofline)firstLocalHead.get(firstLastAssIndex)).fLineno)+1;
    //{the single line premise comes last if not already there}

  sift();

  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= ((TProofline)firstLocalHead.get(firstLocalHead.size()-1)).fLineno;  //last line



}

/*

 PremNoConc:
        begin
         secondFormula := TProofline(secondlocalHead.First).fFormula;
         if not InPremises(nil, firstlocalHead, firstlastAssumption, secondFormula, secondline) then
                                {note Inpremises fixes secondline }
         secondline := TProofline(firstlocalHead.At(firstlastAssumption)).fLineno + 1; {the single line premise comes last if}
  {                                                               not already there}

         Sift;

         RenumberLines(firstlocalHead, 2000);
         RenumberLines(firstlocalHead, 1);

         firstline := TProofline(firstlocalHead.Last).fLineno;
        end;


*/

void doPremNoConcPremConc(){
  sift();

  addSecondTail();

  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= 1;
  secondLineNum= ((TProofline)firstLocalHead.get(secondTailIndex)).fLineno;


}

/*

 PremConc:
        begin
         Sift;
         AddSecondTail; {This places firstTail at end}
         RenumberLines(firstlocalHead, 2000);
         RenumberLines(firstlocalHead, 1);
         firstline := 1; {the single line premise comes on front}
         secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
        end;


*/

 void doPremNoConcPremNoConc(){
   TFormula firstFormula = ((TProofline)(firstLocalHead.get(0))).fFormula;
   TFormula secondFormula = ((TProofline)(secondLocalHead.get(0))).fFormula;

   if (TFormula.equalFormulas(firstFormula,secondFormula)){
     firstLineNum=1;
     secondLineNum=1;  //{the single line premise comes on front and two the same}
   }
   else{
     firstLineNum=1;
     secondLineNum=2;

   }
   sift();
   TProofListModel.renumberLines(firstLocalHead,2000);
   TProofListModel.renumberLines(firstLocalHead,1);


 }

 /*begin
        firstFormula := TProofline(firstlocalHead.First).fFormula;
        secondFormula := TProofline(secondlocalHead.First).fFormula;
        if EqualFormulas(firstFormula, secondFormula) then
        begin
        firstline := 1;
        secondline := 1;
        end {the single line premise comes on front and two the same}
        else
        begin
        firstline := 1;
        secondline := 2;
        end;
        Sift;
        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

       end;  */


void doPremConcNoPremConc(){

   TProofListModel.increaseSubProofLevels(secondLocalHead,+1);

   secondLocalHead.remove(0); // the blankline of second

   secondLastAssIndex-=1;

   addSecondTail();

   TProofListModel.renumberLines(firstLocalHead,2000);
   TProofListModel.renumberLines(firstLocalHead,1);

   firstLineNum= ((TProofline)firstLocalHead.get(firstTailIndex)).fLineno;
   secondLineNum= ((TProofline)firstLocalHead.get(secondTailIndex)).fLineno;


}

 /*

  NoPremConc:
         begin
          IncreaseSubProofLevels(secondlocalHead, +1);

          garbageLIne := TProofline(secondlocalHead.First);
          secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
          garbageLIne.DismantleProofline;
          secondlastAssumption := secondlastAssumption - 1;

          AddSecondTail; {This places firstTail at end of first proof}
          RenumberLines(firstlocalHead, 2000);
          RenumberLines(firstlocalHead, 1);

          firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
          secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
         end;


*/

void doPremConcPremConc(){

  sift();
  addSecondTail();
  TProofListModel.renumberLines(firstLocalHead,2000);
  TProofListModel.renumberLines(firstLocalHead,1);

  firstLineNum= ((TProofline)firstLocalHead.get((firstTailIndex))).fLineno;
  secondLineNum= ((TProofline)firstLocalHead.get((secondTailIndex))).fLineno;

/*
     begin
           Sift;
           AddSecondTail; {This places firstTail at end}
           RenumberLines(firstlocalHead, 2000);
           RenumberLines(firstlocalHead, 1);

           firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
           secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;

          end;


  */

}

void addSecondTail(){

  firstTailIndex=firstLocalHead.size()-1;

  firstLocalHead.addAll(secondLocalHead);  //I hope add all retains order? apparently adds in order of iterator

  secondTailIndex=firstLocalHead.size()-1;

  /*
     procedure AddSecondTail;

      begin
       firstTail := firstlocalHead.fSize; {index}
       AppendSecondListToFirst(firstlocalHead, secondlocalHead);
                   {secondlocalHead.Free; check, now done at end frees second list}
       secondTail := firstlocalHead.fSize; {index}
      end;


  */

}

  void merge(){

/*
     {This takes two proofs of two different conclusions from similar assumptions and merges them}
     {into one long proof of one conclusion at firstline and the second conclusion at secondline from the joint set of
     the assumptions}
     {It first makes sure the two sets of assumptions have the same line numbers}
     {Notice that in the case of one line proofs the conclusion and the antecedent are}
     {one and the same-- this affects picking up the line number of the conclusion}
     {If the second is a one line proof its antecedent becomes line number 1 of }
     {the merged proof; if the first is a one line proof its antecedent has to be found}
     {-- it is actually the last assumption because all the assumptions of the second}
     {get put before it}

The merged result occurs in firstLocalHead, firstLastAssumption, firstlineNum, secondlineNum.

 */

    int firstProofType=getProofType(firstLocalHead,firstLastAssIndex);
    int secondProofType=getProofType(secondLocalHead,secondLastAssIndex);

    firstTailIndex=firstLocalHead.size()-1;
    secondTailIndex=secondLocalHead.size()-1;


 /*   firstTailIndex=firstLocalHead.size()-1;
    secondTailIndex=secondLocalHead.size()-1;

    if (((TProofline)firstLocalHead.get(0)).fBlankline)
      firstProofType=noPremConc;
    else{
      if (firstLastAssumption==(firstLocalHead.size()+1))  // I think this is right, the line numbers start at 1 and the indices at 0
        firstProofType=premNoConc;
      else
        firstProofType=premConc;
    }

    if (((TProofline)secondLocalHead.get(0)).fBlankline)
      secondProofType=noPremConc;
    else{
      if (secondLastAssumption==(secondLocalHead.size()+1))
        secondProofType=premNoConc;
      else
        secondProofType=premConc;
    } */

    /*
      firstTail := firstlocalHead.fSize; {index}
   secondTail := secondlocalHead.fSize; { second index}

   if TProofline(firstlocalHead.First).fBlankline then
    firstprooftype := NoPremConc
   else
    begin
     if (firstlastAssumption = firstlocalHead.fSize) then
      firstprooftype := PremNoConc
     else
      firstprooftype := PremConc;
    end;



 }*/

  TProofListModel.renumberLines(secondLocalHead,1000);

    switch (firstProofType){
      case noPremConc:
        switch (secondProofType){
           case noPremConc:
             doNoPremConcNoPremConc();
           break;
           case premNoConc:
             doNoPremConcPremNoConc();
           break;
           case premConc:
             doNoPremConcPremConc();
           break;
       }

        break;
      case premNoConc:
        switch (secondProofType){
           case noPremConc:
             doPremNoConcNoPremConc();
           break;
           case premNoConc:
             doPremNoConcPremNoConc();
           break;
           case premConc:
             doPremNoConcPremConc();
           break;
       }


        break;
      case premConc:
        switch (secondProofType){
           case noPremConc:
             doPremConcNoPremConc();
           break;
           case premNoConc:
             doPremConcPremNoConc();
           break;
           case premConc:
             doPremConcPremConc();
           break;
       }

        break;
    }

  }


void prepareSegmentForSplice(){
    /*this is very similar to merge. I want to see how much common code I can use
     {This alters a second proof so that it can be fitted into the context of the first}

     It is slightly different when used in as much as the output from merge is going to
     be taken from the firstLocalHead etc., whereas for prepareSegmentForSplice the output
     will be taken from the secondLocalHead etc.


     */


   // int firstProofType = 0, secondProofType = 0;

    firstTailIndex = firstLocalHead.size() - 1;
    secondTailIndex = secondLocalHead.size() - 1;


    int firstProofType;//=getProofType(firstLocalHead,firstLastAssIndex);
    int secondProofType=getProofType(secondLocalHead,secondLastAssIndex);

    if (((TProofline)firstLocalHead.get(0)).fBlankline)
      firstProofType=noPremConc;
    else
      firstProofType=premConc;

    /*we are theorem proving here, so the firstProof is always treated as having a conclusion*/

    /*
      if TProofline(firstlocalHead.First).fBlankline then
                      firstprooftype := NoPremConc
                     else
                      begin
                       firstprooftype := PremConc;
                      end;


 */

    TProofListModel.renumberLines(secondLocalHead, 1000);

    switch (firstProofType){
      case noPremConc:
        switch (secondProofType){
           case noPremConc:
             secondLocalHead.remove(0);
             /*secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}   */
           break;
           case premNoConc:  //none
           break;
           case premConc:
              siftForSplice();
              TProofListModel.increaseSubProofLevels(secondLocalHead,-1);

             /*
              begin
                         {June 22 1991 to correct error when second proof has assumptions but}
                       {the first has no standing assumptions eg P^P hookP}
                              Sift;
                              IncreaseSubProofLevels(secondlocalHead, -1);
                             end;


        }*/
           break;
       }

        break;
      case premNoConc: //none
        switch (secondProofType){
           case noPremConc: //none
           break;
           case premNoConc: //none
           break;
           case premConc: //none
           break;
       }


        break;
      case premConc:
        switch (secondProofType){
           case noPremConc:
             TProofListModel.increaseSubProofLevels(secondLocalHead,+1);
             secondLocalHead.remove(0); //drop second bllankstart
           break;

          /* NoPremConc:
                    begin
                     IncreaseSubProofLevels(secondlocalHead, +1);
                     garbageLIne := TProofline(secondlocalHead.First);
                     secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
                     garbageLIne.DismantleProofline;
                    end; */

           case premNoConc:
             TFormula secondFormula=((TProofline)secondLocalHead.get(0)).fFormula;


             //TParser aParser = new TParser();             //Sept 06. You might need to change this if you go to different proof systems

             TProofline newline = fSampleLine; //new TProofline(aParser);

             newline.fFormula=secondFormula.copyFormula();
             newline.fFirstjustno=1;
             newline.fJustification= " R";
             newline.fSubprooflevel=0;

             secondLocalHead.add(newline);
           break;
           /*begin
                     secondFormula := TProofline(secondlocalHead.First).fFormula;

                     SupplyProofline(newline);
                     with newline do
                     begin
                     fFormula := secondFormula.CopyFormula;
                     ffirstjustno := 1;
                     fJustification := ' R';
                     fSubprooflevel := 0;
                     end;
                     secondlocalHead.InsertLast(newline);

                     Sift;
                    end;


    */
           case premConc:
             siftForSplice();
             /*PremConc:
                    Sift;*/
           break;
       }

        break;
    }



if (TConstants.DEBUG){

       System.out.println("In preparSegSplice in Merge, before remove duplicates <br>");
       System.out.println("First proof <br>"+ proofToString(firstLocalHead));
       System.out.println("Second proof <br>"+ proofToString(secondLocalHead));
 }


TProofListModel.removeDuplicatesInNew(firstLocalHead,secondLocalHead);

 if (TConstants.DEBUG){

       System.out.println("In preparSegSplice in Merge, after remove duplicates <br>");
       System.out.println("First proof <br>"+ proofToString(firstLocalHead));
       System.out.println("Second proof <br>"+ proofToString(secondLocalHead));
 }


TProofListModel.renumberLines(secondLocalHead,((TProofline)firstLocalHead.get(firstLocalHead.size()-1)).fLineno+1);


  /*
                  RemoveDuplicatesinNew(firstlocalHead, secondlocalHead);
                RenumberLines(secondlocalHead, (TProofline(firstlocalHead.Last).fLineno + 1));

  */


  }

     /*

              procedure PrepareSegmentForSplice (firstlocalHead: TList; var secondlocalHead: TList; var secondlastAssumption: integer);

              {This alters a second proof so that it can be fitted into the context of the first}


                var
                 garbageList: TList;
                 garbageLIne, newline: TProofline;
                 firstFormula, secondFormula: TFormula;

                 firstprooftype, secondprooftype: prooftype;
                 firstTail, secondTail, i: integer;

                procedure Sift;

                 var
                  searchline: TProofline;
                  itslineno, index, limit: integer;

              {This discards all the Ass steps of the second proof, renumbering to}
              {the lines in the first proof.}

                begin
                 index := 1;
                 limit := secondlastAssumption;
                 while (index <= limit) do {going to discard premises}
                  begin
                   searchline := TProofline(secondlocalHead.At(index));
                   if InProof(firstlocalHead, searchline.fFormula, itslineno) then
                    begin
                     ReNumSingleLine(secondlocalHead, index, itslineno);
                     garbageList.InsertFirst(searchLine);
                    end
                   else
                    begin
                     sysBeep(5);
              {$IFC myDebugging}
                     writeln('Merge error at index', index, ' and limit', limit);
              {$ENDC}
                    end;

                   index := index + 1;
                  end;

                 index := 1;
                 while (index <= limit) do {going to tidy up}
                  begin
                   secondlocalHead.Delete(secondlocalHead.First);
                   index := index + 1;
                  end;
                 secondlastAssumption := 0;
                end;

               begin
                garbageList := nil;
                garbageList := newlist;

                firstTail := firstlocalHead.fSize; {index}
                secondTail := secondlocalHead.fSize; { second index}

                if TProofline(firstlocalHead.First).fBlankline then
                 firstprooftype := NoPremConc
                else
                 begin
                  firstprooftype := PremConc;
                 end;

                if TProofline(secondlocalHead.First).fBlankline then
                 secondprooftype := NoPremConc
                else
                 begin
                  if (secondlastAssumption = secondlocalHead.fSize) then
                   secondprooftype := PremNoConc
                  else
                   secondprooftype := PremConc;
                 end;

                RenumberLines(secondlocalHead, 1000);

                case firstprooftype of
                 NoPremConc:
                  case secondprooftype of
                   NoPremConc:
                    begin
                     garbageLIne := TProofline(secondlocalHead.First);
                     secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
                     garbageLIne.DismantleProofline;
                    end;

                   PremConc:
                    begin
                {June 22 1991 to correct error when second proof has assumptions but}
              {the first has no standing assumptions eg P^P hookP}
                     Sift;
                     IncreaseSubProofLevels(secondlocalHead, -1);
                    end;

                   otherwise
                  end;


                 PremConc:
                  case secondprooftype of
                   NoPremConc:
                    begin
                     IncreaseSubProofLevels(secondlocalHead, +1);
                     garbageLIne := TProofline(secondlocalHead.First);
                     secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
                     garbageLIne.DismantleProofline;
                    end;

                   PremNoConc:
                    begin
                     secondFormula := TProofline(secondlocalHead.First).fFormula;

                     SupplyProofline(newline);
                     with newline do
                     begin
                     fFormula := secondFormula.CopyFormula;
                     ffirstjustno := 1;
                     fJustification := ' R';
                     fSubprooflevel := 0;
                     end;
                     secondlocalHead.InsertLast(newline);

                     Sift;
                    end;

                   PremConc:
                    Sift;

                   otherwise

                  end;
                 otherwise

                end;

                RemoveDuplicatesinNew(firstlocalHead, secondlocalHead);
                RenumberLines(secondlocalHead, (TProofline(firstlocalHead.Last).fLineno + 1));



                for i := 1 to garbageList.fSize do
                 TProofline(garbageList.At(i)).DismantleProofline;
                garbageList.DeleteAll;
                garbageList.Free;

               end;

        */



void sift(){

  /*
{This discards all the Ass steps of the second proof; if a given one appears as an}
{assumption of the first proof the renumbering is done, if not it is added at end of}
{first list of assumptions}

  */

TProofline searchLine;
int itsLineNo=-1;

for (int i=0;i<=secondLastAssIndex;i++){
  searchLine=(TProofline)secondLocalHead.get(i);

  itsLineNo=inPremises(null,firstLocalHead,firstLastAssIndex,searchLine.fFormula);


  if (itsLineNo!=-1)    // it's there
    TProofListModel.reNumSingleLine (secondLocalHead, i, itsLineNo);
  else{
    firstLocalHead.add(firstLastAssIndex+1,searchLine);  //watch for off by one error with firstLastAssumption
    firstLastAssIndex+=1;
  }
  }

  for (int i=0;i<=secondLastAssIndex;i++){
    secondLocalHead.remove(0);
  }


  secondLastAssIndex=0; // more tidying up?
}

  /*

   procedure Sift;

       var
        searchline: TProofline;
        itslineno, index, limit: integer;

                   {This discards all the Ass steps of the second proof; if a given one appears as an}
                   {assumption of the first proof the renumbering is done, if not it is added at end of}
                   {first list of assumptions}

      begin
       index := 1;
       limit := secondlastAssumption;
       while (index <= limit) do {going to discard premises}
        begin
         searchline := TProofline(secondlocalHead.At(index));
         if InPremises(nil, firstlocalHead, firstlastAssumption, searchline.fFormula, itslineno) then
          begin
           ReNumSingleLine(secondlocalHead, index, itslineno);
                            { check   searchLine.DismantleProofLine; ?cannot because others refer to it}

           garbageList.InsertFirst(searchLine);
          end
         else
          begin
           firstlocalHead.InsertBefore(firstlastAssumption + 1, searchline);
           firstlastAssumption := firstlastAssumption + 1;
          end;

         index := index + 1;
        end;

       index := 1;
       while (index <= limit) do {going to tidy up}
        begin
         secondlocalHead.Delete(secondlocalHead.First);
         index := index + 1;
        end;
       secondlastAssumption := 0;
      end;


  */

 void siftForSplice(){

   /*
     {This discards all the Ass steps of the second proof, renumbering to}
               {the lines in the first proof.}

   */

 TProofline searchLine;
 int itsLineNo=-1;


if (TConstants.DEBUG){

   System.out.println("In siftForSplice in Merge <br>");
   System.out.println("First proof <br>"+ proofToString(firstLocalHead));
   System.out.println("Second proof <br>"+ proofToString(secondLocalHead));
 }


 for (int i=0;i<=secondLastAssIndex;i++){
   searchLine=(TProofline)secondLocalHead.get(i);

   itsLineNo=inProof(firstLocalHead,searchLine.fFormula);


   if (itsLineNo!=-1)    // it's there
     TProofListModel.reNumSingleLine (secondLocalHead, i, itsLineNo);
   else{
//     Toolkit.getDefaultToolkit().beep();  to do warn user
     System.out.print("<br> Error in siftForSplice, assumption missing <br>");
   }
   }

   for (int i=0;i<=secondLastAssIndex;i++){
     secondLocalHead.remove(0);
   }


   secondLastAssIndex=0; // more tidying up?
 }


/*
  procedure Sift;  **** for splice

                  var
                   searchline: TProofline;
                   itslineno, index, limit: integer;

               {This discards all the Ass steps of the second proof, renumbering to}
               {the lines in the first proof.}

                 begin
                  index := 1;
                  limit := secondlastAssumption;
                  while (index <= limit) do {going to discard premises}
                   begin
                    searchline := TProofline(secondlocalHead.At(index));
                    if InProof(firstlocalHead, searchline.fFormula, itslineno) then
                     begin
                      ReNumSingleLine(secondlocalHead, index, itslineno);
                      garbageList.InsertFirst(searchLine);
                     end
                    else
                     begin
                      sysBeep(5);
               {$IFC myDebugging}
                      writeln('Merge error at index', index, ' and limit', limit);
               {$ENDC}
                     end;

                    index := index + 1;
                   end;

                  index := 1;
                  while (index <= limit) do {going to tidy up}
                   begin
                    secondlocalHead.Delete(secondlocalHead.First);
                    index := index + 1;
                   end;
                  secondlastAssumption := 0;
                 end;


 */



public static int inPremises(TTestNode thisNode, ArrayList head, int lastAssIndex, TFormula whichFormula){
// returns line number or -1
int lineNo=-1;
Iterator iter ;

/* {There is a problem here in that if the formula is already in the tree}
  {coming from higher up, it appears that it is needed which it isn't}
  {the second search is a patch} to fix this */

   if ((head!=null)&&(head.size()>0)){

     TProofline searchLine;
     iter = head.iterator();




     while ((iter.hasNext())&&lineNo==-1) {
       searchLine = (TProofline) iter.next();

       if (lineNo==-1                               // not found
           &&(searchLine.fLineno <= (lastAssIndex+1)) // in premises  (line nos start at 1, indices at 0
           && (!searchLine.fBlankline)              // not blank
           && TFormula.equalFormulas(whichFormula,searchLine.fFormula))
              lineNo=searchLine.fLineno;
      }

      if (thisNode!=null){                          // this is to stop finding it twice

        if (thisNode.fAntecedents.size()>0){

           TFormula searchFormula;
           iter = thisNode.fAntecedents.iterator();

          while ((iter.hasNext())&&lineNo==-1) {
             searchFormula = (TFormula) iter.next();

             if (lineNo!=-1                               //found
                &&TFormula.equalFormulas(whichFormula,searchFormula)
                )
                      lineNo=-1;                           // unfind it
           }
       }
}


     }


  /*

     function InPremises (thisnode: TTestnode; Head: TList; lastAssumption: integer; whichformula: TFormula; var itslineno: integer): boolean;
   {check var params}
   {There is a problem here in that if the formula is already in the tree}
   {coming from higher up, it appears that it is needed which it isn't}
   {the second search is a patch}

     var
      found: boolean;

     procedure FindIt (item: TObject);

      var
       searchFormula: TFormula;

     begin
      if not found then
       if (TProofline(item).fLineno <= lastAssumption) then
        begin
         if not TProofline(item).fBlankline then
          begin
          searchFormula := TProofline(item).fFormula;
          found := EqualFormulas(whichformula, searchFormula);
          if found then
          itslineno := TProofline(item).fLineno;
          end;
        end
     end;

     procedure DontFindItTwice (item: TObject);

      var
       searchFormula: TFormula;

     begin
      if found then
       begin
        searchFormula := TFormula(item);
        found := not EqualFormulas(whichformula, searchFormula);
       end;
     end;

    begin
     found := false;
     itslineno := 0;

     Head.Each(FindIt);

     if (thisnode <> nil) and found then
      thisnode.fAntecedents.Each(DontFindItTwice); {patch}

     InPremises := found;
    end;


  */

return
   lineNo;
}



  int inProof(ArrayList head, TFormula whichFormula){
// returns line number or -1
 int lineNo=-1;
 Iterator iter ;



    if (head.size()>0){

      TProofline searchLine;
      iter = head.iterator();




      while ((iter.hasNext())&&lineNo==-1) {
        searchLine = (TProofline) iter.next();

        if (lineNo==-1                               // not found
            && (!searchLine.fBlankline)              // not blank
            && (searchLine.fSelectable)              // selectable
            && TFormula.equalFormulas(whichFormula,searchLine.fFormula))
               lineNo=searchLine.fLineno;
       }
      }

 return
    lineNo;
 }




 /*
  {This discards all the Ass steps of the second proof, renumbering to}
               {the lines in the first proof.}

  function InProof (Head: TList; whichformula: TFormula; var itslineno: integer): boolean;
  {check var params}
  {There is a problem here in that if the formula is already in the tree}
  {coming from higher up, it appears that it is needed which it isn't}
  {the second search is a patch}

    var
     found: boolean;

    procedure FindIt (item: TObject);

     var
      searchFormula: TFormula;

    begin
     if not found then
      if not TProofline(item).fBlankline then
       if TProofline(item).fSelectable then
        begin
         searchFormula := TProofline(item).fFormula;
         found := EqualFormulas(whichformula, searchFormula);
         if found then
         itslineno := TProofline(item).fLineno;
        end;
    end;


   begin
    found := false;
    itslineno := 0;

    Head.Each(FindIt);

    InProof := found;
   end;

*/



}  // end of MergeDataClass


/*

 procedure Merge (var firstlocalHead: TList; var firstlastAssumption: integer; var secondlocalHead: TList;
          var secondlastAssumption, firstline, secondline: integer);

 {This takes two proofs of two different conclusions from similar assumptions and merges them}
 {into one long proof of one conclusion at firstline and the second conclusion at secondline from the joint set of
 the assumptions}
 {It first makes sure the two sets of assumptions have the same line numbers}
 {Notice that in the case of one line proofs the conclusion and the antecedent are}
 {one and the same-- this affects picking up the line number of the conclusion}
 {If the second is a one line proof its antecedent becomes line number 1 of }
 {the merged proof; if the first is a one line proof its antecedent has to be found}
 {-- it is actually the last assumption because all the assumptions of the second}
 {get put before it}

   var
    garbageList: TList;
    garbageLIne: TProofline;
    firstFormula, secondFormula: TFormula;

    firstprooftype, secondprooftype: prooftype;
    firstTail, secondTail, i: integer;

   procedure Sift;

    var
     searchline: TProofline;
     itslineno, index, limit: integer;

                {This discards all the Ass steps of the second proof; if a given one appears as an}
                {assumption of the first proof the renumbering is done, if not it is added at end of}
                {first list of assumptions}

   begin
    index := 1;
    limit := secondlastAssumption;
    while (index <= limit) do {going to discard premises}
     begin
      searchline := TProofline(secondlocalHead.At(index));
      if InPremises(nil, firstlocalHead, firstlastAssumption, searchline.fFormula, itslineno) then
       begin
        ReNumSingleLine(secondlocalHead, index, itslineno);
                         { check   searchLine.DismantleProofLine; ?cannot because others refer to it}

        garbageList.InsertFirst(searchLine);
       end
      else
       begin
        firstlocalHead.InsertBefore(firstlastAssumption + 1, searchline);
        firstlastAssumption := firstlastAssumption + 1;
       end;

      index := index + 1;
     end;

    index := 1;
    while (index <= limit) do {going to tidy up}
     begin
      secondlocalHead.Delete(secondlocalHead.First);
      index := index + 1;
     end;
    secondlastAssumption := 0;
   end;

   procedure AddSecondTail;

   begin
    firstTail := firstlocalHead.fSize; {index}
    AppendSecondListToFirst(firstlocalHead, secondlocalHead);
                {secondlocalHead.Free; check, now done at end frees second list}
    secondTail := firstlocalHead.fSize; {index}
   end;

  begin
   garbageList := nil;
   garbageList := newlist;

   firstTail := firstlocalHead.fSize; {index}
   secondTail := secondlocalHead.fSize; { second index}

   if TProofline(firstlocalHead.First).fBlankline then
    firstprooftype := NoPremConc
   else
    begin
     if (firstlastAssumption = firstlocalHead.fSize) then
      firstprooftype := PremNoConc
     else
      firstprooftype := PremConc;
    end;

   if TProofline(secondlocalHead.First).fBlankline then
    secondprooftype := NoPremConc
   else
    begin
     if (secondlastAssumption = secondlocalHead.fSize) then
      secondprooftype := PremNoConc
     else
      secondprooftype := PremConc;
    end;

   RenumberLines(secondlocalHead, 1000);

   case firstprooftype of
    NoPremConc:
     case secondprooftype of
      NoPremConc:
       begin
        garbageLIne := TProofline(secondlocalHead.First);
        secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
        garbageLIne.DismantleProofline;
        secondlastAssumption := secondlastAssumption - 1;

        AddSecondTail;

        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
       end;

      PremNoConc:
       begin
        IncreaseSubProofLevels(firstlocalHead, +1);
        Sift;

        garbageLIne := TProofline(firstlocalHead.First);
        firstlocalHead.Delete(firstlocalHead.First);
        garbageLIne.DismantleProofline; {drop firstblankstart}

        firstlastAssumption := firstlastAssumption - 1;

        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.Last).fLineno;
        secondline := TProofline(firstlocalHead.At(firstlastAssumption)).fLineno;
       end;

      PremConc:
       begin
        IncreaseSubProofLevels(firstlocalHead, +1);
        Sift;
        AddSecondTail; {This places firstTail at end of first proof}

        garbageLIne := TProofline(firstlocalHead.First);
        firstlocalHead.Delete(firstlocalHead.First);
        garbageLIne.DismantleProofline; {drop firstblankstart}

        firstlastAssumption := firstlastAssumption - 1;
        firstTail := firstTail - 1;
        secondTail := secondTail - 1;


        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
       end;
      otherwise

     end;

    PremNoConc:
     case secondprooftype of
      NoPremConc:
       begin
        IncreaseSubProofLevels(secondlocalHead, +1);
        garbageLIne := TProofline(secondlocalHead.First);
        secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
        garbageLIne.DismantleProofline;
        secondlastAssumption := secondlastAssumption + 1;

                               {drop secondblankstart,}
 {                                                                         loses second lastass}

        AddSecondTail;

        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := 1; {check}
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno; {the}
 {                                   single line premise comes on front}
       end;

      PremNoConc:
       begin
        firstFormula := TProofline(firstlocalHead.First).fFormula;
        secondFormula := TProofline(secondlocalHead.First).fFormula;
        if EqualFormulas(firstFormula, secondFormula) then
        begin
        firstline := 1;
        secondline := 1;
        end {the single line premise comes on front and two the same}
        else
        begin
        firstline := 1;
        secondline := 2;
        end;
        Sift;
        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

       end;

      PremConc:
       begin
        Sift;
        AddSecondTail; {This places firstTail at end}
        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);
        firstline := 1; {the single line premise comes on front}
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
       end;

      otherwise

     end;

    PremConc:
     case secondprooftype of
      NoPremConc:
       begin
        IncreaseSubProofLevels(secondlocalHead, +1);

        garbageLIne := TProofline(secondlocalHead.First);
        secondlocalHead.Delete(secondlocalHead.First); {drop secondblankstart}
        garbageLIne.DismantleProofline;
        secondlastAssumption := secondlastAssumption - 1;

        AddSecondTail; {This places firstTail at end of first proof}
        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;
       end;

      PremNoConc:
       begin
        secondFormula := TProofline(secondlocalHead.First).fFormula;
        if not InPremises(nil, firstlocalHead, firstlastAssumption, secondFormula, secondline) then
                               {note Inpremises fixes secondline }
        secondline := TProofline(firstlocalHead.At(firstlastAssumption)).fLineno + 1; {the single line premise comes last if}
 {                                                               not already there}

        Sift;

        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.Last).fLineno;
       end;

      PremConc:
       begin
        Sift;
        AddSecondTail; {This places firstTail at end}
        RenumberLines(firstlocalHead, 2000);
        RenumberLines(firstlocalHead, 1);

        firstline := TProofline(firstlocalHead.At(firstTail)).fLineno;
        secondline := TProofline(firstlocalHead.At(secondTail)).fLineno;

       end;

      otherwise

     end;
    otherwise

   end;

   if GetHandleSize(Handle(secondlocalHead)) > 0 then
    begin
     secondlocalHead.DeleteAll;
     secondlocalHead.Free; {frees second list}
     secondlocalHead := nil;
    end;

   for i := 1 to garbageList.fSize do
    TProofline(garbageList.At(i)).DismantleProofline;
   garbageList.DeleteAll;
   garbageList.Free;

  end;



*/
