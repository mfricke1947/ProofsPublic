package us.softoption.proofs;

import java.util.ArrayList;
import java.util.Iterator;

import us.softoption.infrastructure.FunctionalParameter;
import us.softoption.infrastructure.TPreferencesData;
import us.softoption.parser.TFormula;
import us.softoption.tree.TTreeDisplayCellTable;

/*This is the underlying data for list that is displayed in the ProofListView. The implementation
for this 'list' is actually two lists, a head list and a tail list. Most insertions will occur at the
end of the head (and thus before the tail*/


/*

 protected void fireContentsChanged(Object�source,
                                   int�index0,
                                   int�index1)



*/

public class TProofListModel /*extends AbstractTableModel*/{
  TProofDisplayCellTable fDisplay=null;
  private ArrayList fHead=new ArrayList();
  private ArrayList fTail=new ArrayList();
  static int fNumColumns=2;
  public static int fProofColIndex=0;
  public static int fJustColIndex=1;

  public static final String kInsertionMarker= "? <<"; //"? &#060;&#060;";

  private int fRightMargin=TPreferencesData.fRightMargin; // for setting the right margin of the prooflines
  // SHOULD BE SET BY THE BROWSWER AND BE ABLE TO BE DIFFERENT FOR EACH DOCUMENT



/*      proofType = (NOpremNOconc, NOpremconc, premNOconc, premconc, pfFinished); */


  public TProofListModel(){

  }



  int getProofType(){

    return 0;

  }

/********* Abstract Implementations ***********/
  
public int getColumnCount(){
	return
	fNumColumns;
}
  
public int getRowCount(){
	return
	 (fHead.size()+fTail.size());
} 

public Object getValueAt(int index, int colIndex){
//we want to get the proofline if there is one
	TProofline theLine;
	
	int headSize = fHead.size();
	   int tailSize=fTail.size();

	   if ((index<0)||(index>(headSize+tailSize-1)))
	    return
	        null;  //no proofline
	   else
	   if (index<headSize)
	       theLine=(TProofline)(fHead.get(index));
	   else
		   theLine=(TProofline)(fTail.get(index-headSize));
	
	// now for the columns
	
	if (colIndex==fProofColIndex||colIndex==fJustColIndex){
	     return
	       theLine;
	 }
	 else
        return
           null;
	  }

public String getColumnName(int c){

	  return
	      " ";                               // blank (not null) headers,

	}
  
/********* End Abstract Implementations ***********/  

public void setDisplay(TProofDisplayCellTable itsDisplay){
		fDisplay=itsDisplay;
}

public TProofDisplayCellTable getDisplay(){
		return
				fDisplay;
}



  public ArrayList getHead(){
    return
        fHead;
  }

  public ArrayList getTail(){
  return
      fTail;
}

public void setHead(ArrayList head){
  fHead=head;
}

public void setTail(ArrayList tail){
  fTail=tail;
}


public void addToHead(Object object){

  ((TProofline)object).fRightMargin=fRightMargin;
  fHead.add(object);
}

public void addToHead(int index,Object object){

    ((TProofline)object).fRightMargin=fRightMargin;
    fHead.add(index,object);
   fireIntervalAdded(this,index,index);
//  //  fireTableRowsDeleted(index,index);
    
    
    //to do need to sort this
    
 //   synchronizeViewToData();
    
    
    
    
    
  }



/************ Updating TO DO ******************************/

void fireIntervalAdded(Object object,int index0,int index1){
	
	if (fDisplay!=null)
		fDisplay.synchronizeViewToData();
	
}

void fireSelectablesChanged(){
	
	if (fDisplay!=null)	
		fDisplay.synchronizeViewToData();
	
}

void fireTableRowsDeleted(int index0,int index1){
	
}










  public void addToTail(Object object){

  ((TProofline)object).fRightMargin=fRightMargin;
  fTail.add(object);
}

public void addToTail(int index,Object object){

  ((TProofline)object).fRightMargin=fRightMargin;
  fTail.add(index,object);
  //fireIntervalAdded(this,index,index);
 ////  fireTableRowsDeleted(index,index);
}


/*
  public void addElementAt(int index,TProofline aProofline){   //MUST BE MORE HERE?
    aProofline.fRightMargin=fRightMargin;
    fHead.add(index,aProofline);
    fireIntervalAdded(this,index,index);      //need this and also when remove
  }

*/

public static void increaseSubProofLevels (ArrayList head,int byThis){
    if (head.size() > 0) {
       Iterator iter = head.iterator();

       while (iter.hasNext())
         ((TProofline)(iter.next())).fSubprooflevel+=byThis;
     }


}

  /*

    procedure IncreaseSubProofLevels (var Head: TList; byThis: integer);

     procedure Increase (item: TObject);

     begin
      TProofline(item).fSubprooflevel := TProofline(item).fSubprooflevel + byThis;
     end;

    begin
     Head.Each(Increase);
    end;


*/

public static void removeDuplicatesInNew(ArrayList masterProof, ArrayList newProof){

/*
     {This scans through the newProof, and if any lines of it occur in the}
       {masterProof they are omitted and the references are transferred}
       {to those in the masterProof-- except for the very last line of}
       {the newProof.  You (the calling routine) should give the newProof high line numbers, and}
       {then renumbner after omission}

*/

int masterLimit=masterProof.size();
int newLimit=newProof.size();
TProofline firstline,searchline;
int deletions=0;

for (int i=0;i<masterLimit;i++){
  firstline=(TProofline)masterProof.get(i);

  if ((!firstline.fBlankline)&&firstline.fSelectable)
    for(int j=0;(j+deletions)<(newLimit-1);j++) {       // not last line
      searchline=(TProofline)newProof.get(j);

     if (!searchline.fBlankline
         &&(!searchline.fJustification.equals(TProofController.fAssJustification)
         && (TFormula.equalFormulas(firstline.fFormula,searchline.fFormula)))){

         reNumSingleLine(newProof, j, firstline.fLineno);

         newProof.remove(j);
         j-=1;
         deletions+=1;
     }

    }

}

}

void changeGoals(TProofline newGoal){
	
int index=indexOfLine(newGoal);

if (index>-1)
	changeGoals(index);
}


void changeGoals(int index){

  // called by mousedown on ? line
  // the caller checks whether the proof panel is modal

  int bad=0;

  removeInsertionMarker();  // this is the << which shows where the tail starts

  int startHeadSize=fHead.size();

  if (index<startHeadSize){

    bad=index;


    for (int i=1;(i+index)<=startHeadSize;i++){

      fTail.add(0,fHead.get(startHeadSize-i));

    //  addToTail(0, fHead.get(startHeadSize-i));  do not call this as fires IntervalChanged which calls this routine
      fHead.remove(startHeadSize-i);

    }

  }
  else{

    bad = startHeadSize;


    for (int i=0;i<(index-startHeadSize);i++){

      fHead.add(fTail.get(0));
      fTail.remove(0);

    }

 
  }

  placeInsertionMarker();

  //fireContentsChanged(this,bad, fHead.size()+fHead.size()-1);
 ////  fireTableRowsDeleted(bad, fHead.size()+fHead.size()-1);

  resetSelectables();


}

 





  public void clear(){
 int size=(fHead.size()+fTail.size());

   if (size>0) {
     fHead.clear();
     fTail.clear();
 //    fireIntervalRemoved(this, 0, size-1);
    //  fireTableRowsDeleted(0, size-1);
   }


 }

 public void clearTail(){
 int size=fTail.size();

   if (size>0) {
     fTail.clear();
     //fireIntervalRemoved(this, fHead.size(), fHead.size()+size-1);
    //  fireTableRowsDeleted(fHead.size(), fHead.size()+size-1);
   }


 }


 public void doToEach(FunctionalParameter aFunction, ArrayList aList){
   if (aList.size() > 0) {
     Iterator iter = aList.iterator();

     while (iter.hasNext())
       aFunction.execute(iter.next());
   }

 }


 public void doToEachInHead(FunctionalParameter aFunction){
   doToEach(aFunction, fHead);

 }

int maxSubProofLevel(){
   int upper=0;
   int lower=0;
   TProofline aProofLine=null;

    if (fHead.size() > 0) {
      Iterator iter = fHead.iterator();

      while (iter.hasNext()) {
        aProofLine = (TProofline) iter.next();

        if (aProofLine.fSubprooflevel>upper)
          upper=aProofLine.fSubprooflevel;
        }

        lower=aProofLine.fHeadlevel;   // can be -1 or 0 might be setting it twice
      }

      if (fTail.size() > 0) {
        Iterator iter = fTail.iterator();

        while (iter.hasNext()) {
          aProofLine = (TProofline) iter.next();

        if (aProofLine.fSubprooflevel>upper)
          upper=aProofLine.fSubprooflevel;
        }
        lower=aProofLine.fHeadlevel;   // can be -1 or 0 might be setting it twice

          }

   return
      upper-lower; }




 public String proofToString(){  //no html wrapper
   String outputStr="";
    TProofline aProofLine;

 /*To do a whole proof we have to look through it and find the highest subprooflevel. This
    is because the eventual html table may have different columns in different rows*/


   int maxSubProoflevel=maxSubProofLevel();





   if (fHead.size() > 0) {
     Iterator iter = fHead.iterator();

     while (iter.hasNext()) {
       aProofLine = (TProofline) iter.next();

       outputStr=outputStr+aProofLine.toTableRow(maxSubProoflevel);
       }

     }

     if (fTail.size() > 0) {
       Iterator iter = fTail.iterator();

       while (iter.hasNext()) {
         aProofLine = (TProofline) iter.next();

         outputStr=outputStr+aProofLine.toTableRow(maxSubProoflevel);
         }

       }


   return
       "<br>"+TProofline.addTableWrapper(outputStr) + "<br>";
 }



 /*

   procedure TMyProofWindow.WriteProofToJournal;

     var
      aString: Str255;
      fontSize, rightMargin, dummy, i: integer;

     procedure WriteOut (item: Tobject);

     begin
      aString := TProofline(item).Draw(fontSize, rightmargin, dummy);
      fDeriverDocument.WriteToJournal(aString, FALSE, FALSE);
     end;

     procedure SpecialWriteOut (item: Tobject);  (*to indicate insertion point*)

     begin
      aString := TProofline(item).Draw(fontSize, rightmargin, dummy);
      insert(' <<', aString, length(aString));
      fDeriverDocument.WriteToJournal(aString, FALSE, FALSE);
     end;


    begin
     if fDeriverDocument.fJournalFont then
      fontSize := 12
     else
      fontSize := 9;
     rightMargin := fDeriverDocument.fRightMargin;

     TESetSelect(maxint, maxint, fDeriverDocument.fJournalTEView.fHTE);
     fDeriverDocument.WriteToJournal(gCR, FALSE, FALSE);
     fHead.Each(WriteOut);

     if fTail.fSize > 0 then
      begin

       SpecialWriteOut(fTail.At(1));
       for i := 2 to fTail.fSize do
        WriteOut(fTail.At(i));

      end;
    end;


*/


TProofline predecessor(TProofline aLine){
  if (fHead.indexOf(aLine)>0)
    return
      (TProofline)fHead.get(fHead.indexOf(aLine)-1);

  if ((fTail.indexOf(aLine)==0)&&(fHead.size()>0))
    return
      (TProofline)fHead.get(fHead.size()-1);

  if (fTail.indexOf(aLine)>0)
    return
      (TProofline)fTail.get(fTail.indexOf(aLine)-1);

   return
       null;
}



 static public ArrayList shallowCopy(ArrayList headOrTail){
   ArrayList copy = new ArrayList();
   int size=headOrTail.size();

   for (int i=0;i<size;i++){
     TProofline lineShallowCopy = ((TProofline)headOrTail.get(i)).shallowCopy();
     copy.add(lineShallowCopy);
   }

   return
       copy;
 }

 public void replaceHeadAndTail(ArrayList newHead, ArrayList newTail){

  // the prooflines have a margin, and this does NOT reset it

   int oldSize=getSize();

   fHead=newHead;
   fTail=newTail;
  // fRightMargin=newMargin;

   int newSize=getSize();

   if (oldSize<newSize)
     //fireIntervalAdded(this,oldSize-1,newSize-1);
    //  fireTableRowsDeleted(oldSize-1,newSize-1);

   if (oldSize>newSize)
	   ;
 //    fireIntervalRemoved(this,newSize-1,oldSize-1);
    //  fireTableRowsDeleted(newSize-1,oldSize-1);

 //  fireContentsChanged(this,0, newSize-1);  // they may have changed
  //  fireTableRowsDeleted(0, newSize-1);

 }






Object findNextQuestionMark(ArrayList list){
   Object value = null;
   boolean found = false;

   if (list.size() > 0) {
     Iterator iter = list.iterator();

     while ( (iter.hasNext()) && !found) {
       TProofline aProofLine = (TProofline) iter.next();

       if ((aProofLine.fFormula!=null)&&(aProofLine.fFormula.fInfo.equals("?"))) {  //blanklines, for eg, do not have formulas
          found=true;
          value=aProofLine;
       }

     }
   }
     return
         value;
   }


/*

    function TProofWindow.FindFormula (given: TFormula): integer;

     var
      searchlineno: integer;
             {returns the line number of the last found, selectable identical formula, zero otherwise}

     procedure Search (item: TObject);

      var
       aProofline: TProofline;

     begin
      aProofline := TProofline(item);
      if aProofline.fselectable then
       if Equalformulas(given, aProofline.fFormula) then
        searchlineno := aProofline.flineno;
     end;

    begin
     searchlineno := 0;
     fHead.Each(Search);
     FindFormula := searchlineno;

    end;


*/

public int lineNoOfLastSelectableEqualFormula(TFormula formula){
   int lineNo = -1;


   if (fHead.size() > 0) {
     Iterator iter = fHead.iterator();   // only lines in the Head are selectable

     while (iter.hasNext()) {
       TProofline aProofLine = (TProofline) iter.next();

       if (aProofLine.fSelectable
           &&(aProofLine.fFormula!=null)
           &&(TFormula.equalFormulas(aProofLine.fFormula,formula))) {
          lineNo=aProofLine.fLineno;
       }
     }
   }
     return
         lineNo;
   }


/* careful because there are blanklines with lineNos-- work forward */

static public TProofline lineFromLineNo(ArrayList localHead, int lineNo){

  int index = 0;
  int limit =localHead.size();

  if ((limit>0)&&(lineNo>0)){


    while (index<limit){
      TProofline traceSearch=(TProofline)localHead.get(index);

      if (traceSearch.fLineno == lineNo)
        return
            traceSearch;
      else
        index += 1;
    }
  }
 return
     null;
}




public ArrayList listNegationSubFormulasInProof(){

  TProofline searchline;
  ArrayList allNegations=new ArrayList();

   Iterator iter = fHead.iterator();


   while (iter.hasNext()) {
     searchline = (TProofline) iter.next();

     if ((searchline.fSelectable) &&
         !(searchline.fBlankline))
            allNegations.addAll((searchline.fFormula).allSubFormulasWhichAreNegations()) ;
   }

   TFormula.removeDuplicateFormulas(allNegations);


   return
       allNegations;
 }



/*

    function TProofWindow.ListPosForks: Str255;
            (*this lists all subformulas starting with a negation in headlist*)
      var
       outPutStr: str255;

      procedure ListIt (item: Tobject);
       var
        aProofline: TProofline;

       procedure AddItsSubFormulas (root: TFormula);
        var
         tempout: str255;
       begin
        tempout := strNull;
        if (root.fKind = unary) then (*NEGATION*)
         fParser.WriteFormulatoString(root.fRlink, tempout);

        tempout := concat(tempout, chBlank);
        if pos(tempout, outputStr) = 0 then
         outputStr := concat(outputStr, tempout);

        if root.fLlink <> nil then
         AddItsSubFormulas(root.fLlink);
        if root.fRlink <> nil then
         AddItsSubFormulas(root.fRlink);
       end;

      begin
       aProofline := TProofline(item);
       if aProofline.fSelectable then
        if not aProofline.fBlankline then
         AddItsSubFormulas(aProofline.fFormula);
      end;

     begin
      outputStr := strNull;
      fHead.Each(ListIt);
      ListPosForks := outputStr;
     end;


*/


int indexOfLine(TProofline line){
	   TProofline aProofLine;
	   int index=0;

	   if (fHead.size() > 0) {
	     Iterator iter = fHead.iterator();
	    

	     while (iter.hasNext()) {
	       aProofLine = (TProofline) iter.next();

	       if (aProofLine==line) {
	          return
	              index;
	       }
	       index=index+1;
	     }
	   }

	   if (fTail.size() > 0) {
	     Iterator iter = fTail.iterator();
	     

	     while (iter.hasNext()) {
	       aProofLine = (TProofline) iter.next();

	       if (aProofLine==line) {
	          return
	              index;
	       }
	       index=index+1;
	     }
	   }

	     return
	         -1;
	   }


int indexOfLineno(int lineNo){
   TProofline aProofLine;
   int index=0;

   if (fHead.size() > 0) {
     Iterator iter = fHead.iterator();
    

     while (iter.hasNext()) {
       aProofLine = (TProofline) iter.next();

       if (aProofLine.fLineno==lineNo) {
          return
              index;
       }
       index=index+1;
     }
   }

   if (fTail.size() > 0) {
     Iterator iter = fTail.iterator();
    // int index=0;   // this is a mistake seen Feb 2013, but made much earlier, don't know when

     while (iter.hasNext()) {
       aProofLine = (TProofline) iter.next();

       if (aProofLine.fLineno==lineNo) {
          return
              index;
       }
       index=index+1;
     }
   }

     return
         -1;
   }


int findIndexOfNextQuestionMark(ArrayList list){
   int value = -1;
   boolean found = false;
   TProofline aProofLine;

   if (list.size() > 0) {
     Iterator iter = list.iterator();
     int index=0;

     while ( (iter.hasNext()) && !found) {
       aProofLine = (TProofline) iter.next();

       if ((aProofLine.fFormula!=null)&&(aProofLine.fFormula.fInfo.equals("?"))) {
          found=true;
          value=index;
       }
       index=index+1;

     }
   }
     return
         value;
   }

TProofline nextLine(TProofline thisLine, ArrayList notAmongThese){

  //returns null if here isn't one

   TProofline nextLine=null;

   int lineIndex = fHead.indexOf(thisLine);
   int nextIndex;

   if (lineIndex>-1){               // line in Head

     nextIndex=lineIndex+1;         // next line

     if (nextIndex<fHead.size()){   // trial next in Head
       nextLine = (TProofline) fHead.get(nextIndex);

       if ((nextLine!=null)&&notAmongThese!=null)
         while ((notAmongThese.indexOf(nextLine)>-1)&&
              nextLine!=null){
            if (nextIndex<(fHead.size()-1)){
              nextIndex += 1;
              nextLine = (TProofline) fHead.get(nextIndex);
            }
            else
              nextLine=null;        // out of range in Head
         }
     }
   }

   if (nextLine!=null)
     return
         nextLine;                 // we've found it in Head, so leave


   // two possibilities now, the line itself is not in Head, or the nextLine is not in Head

   if (lineIndex==-1){             // not in Head
     lineIndex = fTail.indexOf(thisLine);

     if (lineIndex==-1)
       return
           null;                  // degenerate case, supplied line not there at all
     else
       nextIndex=lineIndex+1;
   }
   else
      nextIndex=0;               // line in Head, but next not, perhaps in Tail

   nextLine=null;                // belt and braces


   if (nextIndex<fTail.size()){   // trial next in Tail
       nextLine = (TProofline) fHead.get(nextIndex);

       if ((nextLine!=null)&&notAmongThese!=null)
         while ((notAmongThese.indexOf(nextLine)>-1)&&
              nextLine!=null){
            if (nextIndex<(fTail.size()-1)){
              nextIndex += 1;
              nextLine = (TProofline) fTail.get(nextIndex);
            }
            else
              nextLine=null;        // out of range in Tail
         }
     }

   return
      nextLine;
   }


/*
    nextline := nil;
           index := fTail.GetSameItemNo(thisLine);

           if index <> 0 then
            index := index + fHead.fSize
           else
            index := fHead.GetSameItemNo(thisLine);

           index := index + 1;   (*looking for nextline*)

           if index <= fHead.fSize then
            nextline := TProofLine(fHead.At(index))
           else if (index <= fHead.fSize + fTail.fSize) then
            nextline := TProofLine(fTail.At(index - fHead.fSize));


           if nextline <> nil then
            if (alreadyCut <> nil) then
             while (alreadyCut.GetSameItemNo(nextline) <> 0) and (index <= (fHead.fSize + fTail.fSize)) do (*means*)
                                                               (*nextline has been cut so should not be counted*)
              begin
              index := index + 1;   (*looking for nextline*)

              if index <= fHead.fSize then
              nextline := TProofLine(fHead.At(index))
              else if (index <= fHead.fSize + fTail.fSize) then
              nextline := TProofLine(fTail.At(index - fHead.fSize));
              end;


*/

public int nextQuestionMarkInHead(){
     return
        findIndexOfNextQuestionMark(fHead);
   }

public int nextQuestionMarkInTail(){
   return
      findIndexOfNextQuestionMark(fTail);
      }

public int nextQuestionMark(){
   if (nextQuestionMarkInHead()>-1)
     return
        nextQuestionMarkInHead();
  else{
    int index = nextQuestionMarkInTail();

    if (index==-1)
      return
          index; //error
    else
       return
         fHead.size()+index;
  }
}

 /*

  function TProofWindow.FindNextQuestionMark (var whichone: TObject; var inHead: boolean): boolean;
{finds first question mark in Tail list and returns its line before through whichone}

  function QuestionMark (item: TObject): boolean;

  begin
   QuestionMark := TProofLine(item).fFormula.fInfo = '?';
  end;

 begin
  whichone := nil;
  inHead := false;
  whichone := fHead.FirstThat(QuestionMark);
  if whichone <> nil then
   inHead := TRUE
  else
   whichone := fTail.FirstThat(QuestionMark);
  FindNextQuestionMark := (whichone <> nil);
 end;


  */


  public Object getElementAt(int index){
   int headSize = fHead.size();
   int tailSize=fTail.size();

   if ((index<0)||(index>(headSize+tailSize-1)))
    return
        null;
   else
   if (index<headSize)
     return
       fHead.get(index);
   else
     return
       fTail.get(index-headSize);


  }
  
public ArrayList <TProofline> proofAsProoflines(){
	ArrayList result=new ArrayList();
	
	result.addAll(fHead);
	result.addAll(fTail);

	     return
	       result;

	  }

class FindAssumption implements FunctionalParameter {
       boolean fFound=false;
       TProofline fLastAssumptionLine;
       int fLevel=0;

       public FindAssumption (int level){
         fLevel=level;

       }

     public void  execute(Object parameter){
       TProofline workingLine =(TProofline)parameter;

       if (//(workingLine.fJustification.equals(TProofController.assJustification))   this condition changed Dec 06
           workingLine.fLastassumption &&                                       // new condition this condition changed Dec 06
           (workingLine.fSubprooflevel ==fLevel)){
         fFound=true;
         fLastAssumptionLine=workingLine;
       }
     }

     public boolean testIt(Object parameter){
       return
           false;
     }
   }


 TProofline findLastAssumption(){

   FindAssumption finder = new FindAssumption(getHeadLastLine().fSubprooflevel);

   doToEachInHead(finder);

   if (finder.fFound)
      return
          finder.fLastAssumptionLine;
  else
    return
        null;

 }




/*
     function TProofWindow.FindLastAssumption (var subhead: TProofLine): boolean; {of proof as a whole}

    var
     dummy: TObject;

    function Premise (item: TObject): boolean;

     var
      aProofLIne: TProofLine;

    begin
     Premise := false;
     aProofLIne := TProofLine(item);
     if (aProofLIne.fjustification = 'Ass') and (aProofLIne.fSubprooflevel = TProofLine(fHead.Last).fSubprooflevel) then
      begin
       Premise := TRUE;
       subhead := aProofLIne;
      end;
    end;

   begin
    dummy := nil;
    if (TProofLine(fHead.Last).fSubprooflevel > TProofLine(fHead.Last).fHeadlevel) then
     dummy := fHead.LastThat(Premise);
    FindLastAssumption := dummy <> nil;
   end;



   */



TProofline findLastAssumptionOfPriorSubProof(TProofline before, int level){
  /*this can be called inside a subproof, or outside-- just set the subproof
  level appropriately*/
  TProofline search=before;

  /* altered Nov 06 from search=predecessor(before); because with a one line
   subproof, say F, called with F, it was not finding F as the last Assumption*/

  while (search!=null){
     if ((search.fSubprooflevel==level)&&
         (search.fLastassumption))
       return
           search;
     search=predecessor(search);
  }

  return
      null;
}


/*

 function TProofWindow.LastAssLineno (beforeHere: integer; var lastTIindex: integer): integer;

{these are indirectly referred to}

  var
   dummy: TObject;
   tempLast: TProofLine;

  function Premise (item: TObject): boolean;

   var
    aProofLIne: TProofLine;

  begin
   Premise := false;
   aProofLIne := TProofLine(item);
   if aProofLIne.fLastassumption then
    if (aProofLIne.fSubprooflevel = tempLast.fSubprooflevel + 1) then
     if aProofLIne.fLineno <= tempLast.fLineno then
      Premise := TRUE;

  end;

 begin
  LastAssLineno := 1;
  lastTIindex := 0;
  dummy := nil;

  if (beforeHere <= fHead.fSize) then
   begin
    tempLast := TProofLine(fHead.At(beforeHere));
    dummy := fHead.LastThat(Premise);
   end
  else if (beforeHere <= fHead.fSize + fTail.fSize) then
   begin
    tempLast := TProofLine(fTail.At(beforeHere - fHead.fSize));
    dummy := fTail.LastThat(Premise);
    if (dummy = nil) then
     dummy := fHead.LastThat(Premise);

   end;

                {dummy := fHead.LastThat(Premise);}

  if (dummy <> nil) then
   begin
    lastTIindex := fHead.GetSameItemNo(dummy);
    LastAssLineno := TProofLine(dummy).fLineno;
   end;
 end;

*/



  public void remove(int index){
   int headSize = fHead.size();
   int tailSize=fTail.size();

   if ((index<0)||(index>(headSize+tailSize-1)))
    return;
   else{
   if (index<headSize)
       fHead.remove(index);
   else
       fTail.remove(index-headSize);
   }

  }

public void removeProofline(TProofline theLine){

  int index=fHead.indexOf(theLine);

  if ((index>-1)&& fHead.remove(theLine)){


  //   fireIntervalRemoved(this, index, index);
    //  fireTableRowsDeleted(index, index);

     if (!theLine.fBlankline){

       decrementLineNos(fHead, theLine.fLineno, 1);
       decrementLineNos(fTail, theLine.fLineno, 1);  // this also fires interval changeed
     }

  }
  else{
    index=fTail.indexOf(theLine);

    if ((index>-1)&& fTail.remove(theLine))
    //   fireIntervalRemoved(this, fHead.size()+index, fHead.size()+index);
      //  fireTableRowsDeleted(fHead.size()+index, fHead.size()+index);

     if (!theLine.fBlankline){

       decrementLineNos(fHead, theLine.fLineno, 1);
       decrementLineNos(fTail, theLine.fLineno, 1);  // this also fires interval changeed
     }
   }
}

/*
 procedure Remove (item: TObject);

    begin
     fProofWindow.fHead.delete(item);
     if not TProofline(item).fBlankline then
      begin
       fProofWindow.DecrementLineNos(fProofWindow.fHead, TProofline(item).fLineno, 1);
       fProofWindow.DecrementLineNos(fProofWindow.fTail, TProofline(item).fLineno, 1);
      end;
    end;


*/

private static void fixJustNos(ArrayList localHead, TProofline aProofLine, int oldNo, int newStart){

  /*{you only have to deal with lines after the first one}
*/
  boolean doIt=false;


  if ((localHead!=null)&&(localHead.size() > 0)) {
       Iterator iter = localHead.iterator();
       TProofline aSecondProofLine;

       while (iter.hasNext()) {
         aSecondProofLine = (TProofline) iter.next();

         if (doIt){
           if (!aSecondProofLine.fBlankline){
             if (aSecondProofLine.fFirstjustno==oldNo)
               aSecondProofLine.fFirstjustno=newStart;
             if (aSecondProofLine.fSecondjustno==oldNo)
               aSecondProofLine.fSecondjustno=newStart;
             if (aSecondProofLine.fThirdjustno==oldNo)
               aSecondProofLine.fThirdjustno=newStart;

           }

         }
         else
           if (aSecondProofLine==aProofLine)
             doIt=true;
       }
       }
}

/*
 procedure FixJustNos (item: TObject);
            {you only have to deal with lines after the first one}

     var
      aSecondProofLine: TProofline;

    begin
     aSecondProofLine := TProofline(item);
     if doIt then
      begin
       if not aSecondProofLine.fBlankline then
        begin
         if aSecondProofLine.fFirstjustno = oldno then
         aSecondProofLine.fFirstjustno := newstart;
         if aSecondProofLine.fSecondjustno = oldno then
         aSecondProofLine.fSecondjustno := newstart;
         if aSecondProofLine.fthirdjustno = oldno then
         aSecondProofLine.fthirdjustno := newstart;

        end;
      end
     else if aSecondProofLine = aProofLine then
      doIt := true;
    end;


*/

public static void renumberLines (ArrayList localHead, int newStart){
  /* {the lines are in order and their justification numbers match the right lines}
   {but the line numbers are not in order. This fixes that. Must use first to unused numbers eg 1000}
   {then to what you want. Blank lines have the same line number as the last line number}


*/

  if ((localHead!=null)&&(localHead.size() > 0)) {
       Iterator iter = localHead.iterator();
       TProofline aProofLine;
       boolean doIt=false;
       int oldNo;

       while (iter.hasNext()) {
         aProofLine = (TProofline) iter.next();

         if (aProofLine.fBlankline)
            aProofLine.fLineno = newStart - 1;
         else{
            oldNo = aProofLine.fLineno;
            if (oldNo != newStart){
               aProofLine.fLineno = newStart;
              // doIt = false;
               fixJustNos(localHead,aProofLine,oldNo,newStart);  //actually I am not sure the callback is used
            }
            newStart += 1;
         }

         }

       }

/*
           begin
          aProofLine := TProofline(item);
          if aProofLine.fBlankline then
           aProofLine.fLineno := newstart - 1
          else
           begin
            oldno := aProofLine.fLineno;
            if oldno <> newstart then
             begin
              aProofLine.fLineno := newstart;
              doIt := false;
              localHead.Each(FixJustNos);
             end;
            newstart := newstart + 1;
           end;
         end;

    */
}

/*
 procedure RenumberLines (var localHead: TList; newstart: integer);

 {the lines are in order and their justification numbers match the right lines}
 {but the line numbers are not in order. This fixes that. Must use first to unused numbers eg 1000}
 {then to what you want. Blank lines have the same line number as the last line number}

  var
   oldno: integer;

  procedure FixNumbers (item: TObject);

   var
    aProofLine: TProofline;
    doIt: boolean;

   procedure FixJustNos (item: TObject);
           {you only have to deal with lines after the first one}

    var
     aSecondProofLine: TProofline;

   begin
    aSecondProofLine := TProofline(item);
    if doIt then
     begin
      if not aSecondProofLine.fBlankline then
       begin
        if aSecondProofLine.fFirstjustno = oldno then
        aSecondProofLine.fFirstjustno := newstart;
        if aSecondProofLine.fSecondjustno = oldno then
        aSecondProofLine.fSecondjustno := newstart;
        if aSecondProofLine.fthirdjustno = oldno then
        aSecondProofLine.fthirdjustno := newstart;

       end;
     end
    else if aSecondProofLine = aProofLine then
     doIt := true;
   end;

  begin
   aProofLine := TProofline(item);
   if aProofLine.fBlankline then
    aProofLine.fLineno := newstart - 1
   else
    begin
     oldno := aProofLine.fLineno;
     if oldno <> newstart then
      begin
       aProofLine.fLineno := newstart;
       doIt := false;
       localHead.Each(FixJustNos);
      end;
     newstart := newstart + 1;
    end;
  end;

 begin
  localHead.Each(FixNumbers);
 end;


*/

public static void reNumSingleLine (ArrayList localHead, int lineIndex, int newNo){

  /*This revises a line number and all references to it*/


  if ((localHead!=null)
      &&(localHead.size() > 0)
      && lineIndex<localHead.size()) {            // the line needs to be there

       Iterator iter = localHead.iterator();
       TProofline aProofLine;
       TProofline thisLine=(TProofline)localHead.get(lineIndex);;
       boolean start=false;

       int oldNo=thisLine.fLineno;

       if (oldNo!=newNo){                         // if the 'revision' does not change anything we leave it alone

          while (iter.hasNext()) {
             aProofLine = (TProofline) iter.next();

             if (!start){
               start=(thisLine==aProofLine);     // start toggles as the revised line is encountered
               if (start)
                 thisLine.fLineno=newNo;        // we only need revise its lineno and later justifications

             }
             else{                              // later justifications
                if (!aProofLine.fBlankline){
                   if (aProofLine.fFirstjustno==oldNo)
                      aProofLine.fFirstjustno=newNo;
                   if (aProofLine.fSecondjustno==oldNo)
                      aProofLine.fSecondjustno=newNo;
                   if (aProofLine.fThirdjustno==oldNo)
                      aProofLine.fThirdjustno=newNo;
                 }

             }

       }

         }

       }


}


/*

 procedure ReNumSingleLine (var localHead: TList; lineIndex, newno: integer);

  var
   oldno: integer;
   thisLine: TProofline;
   start: boolean;

  procedure ReviseIt (item: TObject);

   var
    revisedLine: TProofline;

  begin
   if not start then
    begin
     start := (TProofline(item) = thisLine);
     if start then
      thisLine.fLineno := newno;
    end;
   if start then
    begin
     revisedLine := TProofline(item);
     if not revisedLine.fBlankline then
      begin
       if revisedLine.fFirstjustno = oldno then
        revisedLine.fFirstjustno := newno;
       if revisedLine.fSecondjustno = oldno then
        revisedLine.fSecondjustno := newno;
       if revisedLine.fthirdjustno = oldno then
        revisedLine.fthirdjustno := newno;
      end;
    end;

  end;

 begin
  start := false;

  thisLine := TProofline(localHead.At(lineIndex));
  oldno := thisLine.fLineno;
  if oldno <> newno then
   localHead.Each(ReviseIt);
 end;


*/


  public int getSize(){
    return
        (fHead.size()+fTail.size());
  }

  public int getHeadSize(){
    return
        fHead.size();
  }

  public int getTailSize(){
    return
        fTail.size();
  }


public int getProofSize(){
  return
        fHead.size()+fTail.size();

}

public TProofline getHeadFirstLine(){
 return
    (TProofline)(fHead.get(0));
        }

public TProofline getTailFirstLine(){
	 return
	    (TProofline)(fTail.get(0));
	        }

public TProofline getHeadLine(int index){
        return
            (TProofline)(fHead.get(index));
      }



public TProofline getHeadLastLine(){
    return
        (TProofline)(getElementAt(fHead.size()-1));
  }

public TProofline getTailLastLine(){
      return
          (TProofline)(getTailLine(fTail.size()-1));
    }

public TProofline getNextConclusion(){
  if (fTail.size()>1)
    return
          (TProofline)(getTailLine(1));  //{first is "?"}
  else
    return
        null;

}

/*

     function TProofWindow.FindTailFormula: TFormula;
      begin
       FindTailFormula := nil;
       if SELF.fTail.fSize <> 0 then
        FindTailFormula := TProofline(SELF.fTail.At(2)).fFormula; {first is "?"}
      end;



 */






  public TProofline getTailLine(int index){
      return
          (TProofline)(fTail.get(index));
    }

    void decrementLineNos(ArrayList thisList, int fromThisLine, int amount){

      int listsize = thisList.size();
      if (listsize>0){


        Iterator iter = thisList.iterator();

        while (iter.hasNext()){
          TProofline aProofLine = (TProofline)iter.next();

          if (aProofLine.fLineno>fromThisLine){


             aProofLine.fLineno -= amount;

             if ( (!aProofLine.fBlankline) && (aProofLine.fJustification != "?")) {

               if (aProofLine.fFirstjustno > fromThisLine)
                 aProofLine.fFirstjustno -= amount;
               if (aProofLine.fSecondjustno > fromThisLine)
                 aProofLine.fSecondjustno -= amount;
               if (aProofLine.fThirdjustno > fromThisLine)
                 aProofLine.fThirdjustno -= amount;
            }
          }
        }
        //fireContentsChanged(this, fromThisLine-1, listsize - 1);
       //  fireTableRowsDeleted(fromThisLine-1, listsize - 1);
      }

     }



/*

      procedure TProofWindow.DecrementLineNos (thisList: TList; fromthisline, amount: integer);

    {increments all lines and justifications in thislist}

      procedure Decrement (item: TObject);

       var
        aProofLIne: TProofLine;

      begin
       aProofLIne := TProofLine(item);
       if aProofLIne.fLineno > fromthisline then
        begin
         aProofLIne.fLineno := aProofLIne.fLineno - amount;

         if (not aProofLine.fBlankline) and (aProofLine.fJustification <> '?') then
          begin
           if aProofLIne.ffirstjustno > fromthisline then
           aProofLIne.ffirstjustno := aProofLIne.ffirstjustno - amount;
           if aProofLIne.fsecondjustno > fromthisline then
           aProofLIne.fsecondjustno := aProofLIne.fsecondjustno - amount;
           if aProofLIne.fthirdjustno > fromthisline then
           aProofLIne.fthirdjustno := aProofLIne.fthirdjustno - amount;
          end;
        end;
      end;

     begin
      thisList.Each(Decrement);
     end;


 */



/*

public void incrementHeadLineNos(int amount){
   incrementLineNos(fHead,amount);
    }
*/
    public void incrementTailLineNos(int amount,int referenceBoundary){
       incrementLineNos(fTail,amount,referenceBoundary);
        }



  void incrementLineNos(ArrayList thisList, int amount, int referenceBoundary){
/*{increments all lines and justifications in thislist}

     This is typically used in the context of two lists.  We want to keep all the numbering and references intact



 There are two lists Head and Tail. And lines can be added to the end of the Head
 or removed from the beginning of the Tail.

 If lines are added to the Head. All the line numbers in the Tail change, and so do all
 the justifications that refer to Tail lines. Hence the referenceBoundary wants to be set to
 the Head last line number.

 If lines are removed from the Tail. All the line numbers in the Tail change, and so do all
 the justifications that refer to Tail lines. Hence the referenceBoundary wants to be set to
 the Head last line number.

 Where you have to go careful is that the Head last line number, and the number before the Tail first
 line number do not have to be the same.

 We might have Head 1,2,3 and Tail 4,5,6,7 and be cutting lines 4 and 5. Now this routine gets called after
 the cut so the presented Tail will be 6,7. So Head last line num is 3 and num before Tail first is 5. */

   int listsize = thisList.size();
   if (listsize>0){
     //int j = ((TProofline)thisList.get(0)).fLineno-1;

     Iterator iter = thisList.iterator();

     while (iter.hasNext()){
       TProofline aProofLine = (TProofline)iter.next();

       aProofLine.fLineno = aProofLine.fLineno + amount;

       if ((! aProofLine.fBlankline)
           && !aProofLine.fJustification.equals("?")
           && !aProofLine.fJustification.equals(kInsertionMarker)){

             if (aProofLine.fFirstjustno > referenceBoundary)
                aProofLine.fFirstjustno = aProofLine.fFirstjustno + amount;
             if (aProofLine.fSecondjustno > referenceBoundary)
                aProofLine.fSecondjustno = aProofLine.fSecondjustno + amount;
             if (aProofLine.fThirdjustno > referenceBoundary)
                aProofLine.fThirdjustno = aProofLine.fThirdjustno + amount;
       }

    //   fireContentsChanged(this,0,listsize-1);
      //  fireTableRowsDeleted(0,listsize-1);


     }
   }

  }


  /*

   procedure TProofWindow.IncrementLineNos (thisList: TList; amount: integer);

{increments all lines and justifications in thislist}

  var
   j: integer;

  procedure Increment (item: TObject);

   var
    aProofLIne: TProofLine;

  begin
   aProofLIne := TProofLine(item);
   aProofLIne.fLineno := aProofLIne.fLineno + amount;

   if (not aProofLine.fBlankline) and (aProofLine.fJustification <> '?') then
    begin
     if aProofLIne.ffirstjustno > j then
      aProofLIne.ffirstjustno := aProofLIne.ffirstjustno + amount;
     if aProofLIne.fsecondjustno > j then
      aProofLIne.fsecondjustno := aProofLIne.fsecondjustno + amount;
     if aProofLIne.fthirdjustno > j then
      aProofLIne.fthirdjustno := aProofLIne.fthirdjustno + amount;
    end;
  end;

 begin
  j := TProofLine(thisList.First).fLineno - 1;
  thisList.Each(Increment);
 end;



      */


void insertAtPseudoTail(TProofline newline){
    int listsize=fHead.size();

    if(listsize>0){
      TProofline lastline = (TProofline) fHead.get(listsize - 1);

      newline.fHeadlevel = lastline.fHeadlevel;

      if (newline.fBlankline)
        newline.fLineno = lastline.fLineno;
       else
        newline.fLineno = lastline.fLineno + 1;
    }

    addToHead(listsize,newline);

    if (!newline.fBlankline)
        incrementLineNos(fTail, 1,((TProofline)fHead.get(fHead.size()-1)).fLineno);

     }

/*

           procedure TProofWindow.InsertAtPseudoTail (thisLine: TProofLine);

     {check May 90 used to be a var param}

       var
        lastline: TProofLine;

      begin
       lastline := TProofLine(fHead.Last);

       thisLine.fHeadlevel := lastline.fHeadlevel;

       if thisLine.fBlankline then
        thisLine.fLineno := lastline.fLineno
       else
        thisLine.fLineno := lastline.fLineno + 1;

       fHead.InsertLast(thisLine);

       if not thisLine.fBlankline then
        IncrementLineNos(fTail, 1);

      end;


      */




boolean lineCutable(TProofline thisLine, ArrayList alreadyCut){



LineCut test = new LineCut(thisLine,alreadyCut);

return
    test.lineCutable();


}

class LineCut{
      TProofline fLine;       // the line in question
      ArrayList fAlreadyCut;  // a list of lines not really there
      boolean fOKToCut=true;  // once a line that is really there refers to fLine this is set to false

      /*

      {its cutable if NO line NOT in the alreadyCut lists refers to it and its not the first line}
            {i.e. some line in the proof genuinely depends on it;
           If you are not bothered about already cut}
            {then call it with already cut = null}

     */



LineCut(TProofline thisLine, ArrayList alreadyCut){
      fLine=thisLine;
      fAlreadyCut=alreadyCut;

    }

    boolean lineCutable(){
      Iterator iter;
      int lineno = fLine.fLineno;

      if (fLine==fHead.get(0)){                         // cannot cut the firstline
        fOKToCut = false;
        return
          fOKToCut;                                    // leave
      }

      if (fTail.size() > 0) {                          // does something in the tail refer to it?
        iter = fTail.iterator();

        while ( (iter.hasNext()) && fOKToCut) {
          refersToIt(lineno,(TProofline)iter.next());  // this can falsify fOKToCut
        }
      }

      if (!fOKToCut)
        return
          fOKToCut;                                   // leave if it cannot be cut

      if (fHead.size() > 0) {                          // does something in the head refer to it?
         iter = fHead.iterator();

         while ( (iter.hasNext()) && fOKToCut) {
           refersToIt(lineno,(TProofline)iter.next());  // this can falsify fOKToCut
      }
    }

      if (!fOKToCut)
         return
           fOKToCut;                                   // leave if it cannot be cut

      if (fLine.fLastassumption)
        fOKToCut=lastAssumptionCheck(fLine);

      if (!fOKToCut)
         return
           fOKToCut;                                   // leave if it cannot be cut


      return
          fOKToCut;
    }



    void refersToIt(int lineno, TProofline anotherLine){
      /*
              {no line refers to itself so we can exclude thisline from the test, this is handy}
       {	in the case of sub-proofs for no line other than thisline can refer to the last line}
       {	of such (ie the subproof)}


       */

      if ( (fOKToCut) && (anotherLine != fLine)) {/*no line refers to itself*/
          if ((fAlreadyCut==null)||(!fAlreadyCut.contains(anotherLine))){  // so we have to check

            if ((anotherLine.fLineno > lineno) &&        //referring line must be later
                ((anotherLine.fFirstjustno == lineno) ||
                 (anotherLine.fSecondjustno == lineno)|| // direct reference
                 (anotherLine.fThirdjustno == lineno)))
                     fOKToCut=false;


            if (fOKToCut){        // we have checked whether anything refers to it directly, now for indirect

 /*I think the condition is this. if anotherLine refers to the last line of a subproof, then, ipso
 facto it refers to the assumption of that subproof */


 /*
  if not localBool then
         begin
         beforeHere := fHead.GetSameItemNo(item); (*trying to find its index*)

         if beforeHere = 0 then
         beforeHere := fHead.fSize + fTail.GetSameItemNo(item);

         if TProofLine(item).fjustification = ' ~I' then
         if LastAssLineno(beforeHere, lastTIindex) = lineno then
         localBool := TRUE;

         if TProofLine(item).fjustification = ' �I' then
         if LastAssLineno(beforeHere, lastTIindex) = lineno then
         localBool := TRUE;

         if TProofLine(item).fjustification = ' EI' then
         if LastAssLineno(beforeHere, lastTIindex) = lineno then
         localBool := TRUE;

         if TProofLine(item).fjustification = ' �E' then
         begin
         if LastAssLineno(beforeHere, lastTIindex) = lineno then
         localBool := TRUE;

         if LastAssLineno(IndexOfLineno(TProofLine(item).fSecondjustno), lastTIindex) = lineno then
         localBool := TRUE;
         end;

         if TProofLine(item).fjustification = ' �I' then
         begin
         if LastAssLineno(beforeHere, lastTIindex) = lineno then
         localBool := TRUE;
         if LastAssLineno(IndexOfLineno(TProofLine(item).fFirstjustno), lastTIindex) = lineno then
         localBool := TRUE;
         end;

         end;  {localbool}

 */


/*if a line refers to a subproof, it indirectly refers to the assumption of that subroof*/
         TProofline lastLineOfSubProof;
         TProofline lastAssumption;

         if ((anotherLine.fJustification.equals(TProofController.fNegIJustification)) ||
             (anotherLine.fJustification.equals(TProofController.fImplicIJustification)) ||
             (anotherLine.fJustification.equals(TProofController.equivIJustification))){

           lastLineOfSubProof=(TProofline)getElementAt(anotherLine.fFirstjustno);
           lastAssumption=findLastAssumptionOfPriorSubProof(lastLineOfSubProof,
                                                                       lastLineOfSubProof.fSubprooflevel);
           if (lastAssumption.fLineno==lineno)
             fOKToCut=false;
         }


         if (fOKToCut&&
             (anotherLine.fJustification.equals(TProofController.fEIJustification))||
             (anotherLine.fJustification.equals(TProofController.fOrEJustification))||
            (anotherLine.fJustification.equals(TProofController.equivIJustification))){

           lastLineOfSubProof=(TProofline)getElementAt(anotherLine.fSecondjustno);
           lastAssumption=findLastAssumptionOfPriorSubProof(lastLineOfSubProof,
                                                                       lastLineOfSubProof.fSubprooflevel);
           if (lastAssumption.fLineno==lineno)
             fOKToCut=false;
         }


         if (fOKToCut&&
               (anotherLine.fJustification.equals(TProofController.fOrEJustification))){

             lastLineOfSubProof=(TProofline)getElementAt(anotherLine.fThirdjustno);
             lastAssumption=findLastAssumptionOfPriorSubProof(lastLineOfSubProof,
                                                                         lastLineOfSubProof.fSubprooflevel);
             if (lastAssumption.fLineno==lineno)
               fOKToCut=false;
           }
       }
            }

       }
    }
    }


boolean lastAssumptionCheck(TProofline thisLine){

/*In subproofs, it is possible for there to be an assumption that nothing directly
  refers to, but yet which is essential eg

 1. B
 2   |_A
 3.  | B

 3. A->B   3, ->I

 Line 2 is not cuttable.

 The only time a line like 2 is cuttable is if it is a singleton and the next line is out of the subproof

*/
boolean out=true;

if (thisLine.fLastassumption){
  TProofline nextLine=nextLine(thisLine,null);

  if (nextLine==null)                           //has to be out of subproof
     out=false;
  else{
    if (nextLine.fSubprooflevel>thisLine.fSubprooflevel)  //nested subproof
      out=false;

    if ((nextLine.fSubprooflevel==thisLine.fSubprooflevel)&&  //next subproof
        !nextLine.fLastassumption)                            // mf March05 don's understand this because there will be a blankline between two subrof
      out=false;


  }
}

return
    out;


 /*

     if thisLine.fLastassumption then
      begin
       nextline := nil;
       index := fTail.GetSameItemNo(thisLine);

       if index <> 0 then
        index := index + fHead.fSize
       else
        index := fHead.GetSameItemNo(thisLine);

       index := index + 1;   (*looking for nextline*)

       if index <= fHead.fSize then
        nextline := TProofLine(fHead.At(index))
       else if (index <= fHead.fSize + fTail.fSize) then
        nextline := TProofLine(fTail.At(index - fHead.fSize));


       if nextline <> nil then
        if (alreadyCut <> nil) then
         while (alreadyCut.GetSameItemNo(nextline) <> 0) and (index <= (fHead.fSize + fTail.fSize)) do (*means*)
                                                           (*nextline has been cut so should not be counted*)
          begin
          index := index + 1;   (*looking for nextline*)

          if index <= fHead.fSize then
          nextline := TProofLine(fHead.At(index))
          else if (index <= fHead.fSize + fTail.fSize) then
          nextline := TProofLine(fTail.At(index - fHead.fSize));
          end;



       if nextline <> nil then  (*nextline has to be out of subproof*)
        begin
         if (nextline.fSubprooflevel > thisLine.fSubprooflevel) then
          ok := false;

         if (nextline.fSubprooflevel = thisLine.fSubprooflevel) then
          if not nextline.fLastAssumption then  (*next subproof*)
          ok := false;
        end;

      end;


  */

}

 /*

    function TProofWindow.LineCutable (thisLine: TProofLine; alreadyCut: TList): boolean;
{its cutable if no line not in the alreadyCut lists refers to it and its not the first line}
{i.e. some line in the proof genuinely depends on it; if you are not bothered about already cut}
{then call it with already cut = nil}

  var
   search: TObject;
   lineno, index, beforeHere: integer;
   ok: boolean;
   nextline: TProofline;

  function Refers (item: TObject): boolean;
        {no line refers to itself so we can exclude thisline from the test, this is handy}
{	in the case of sub-proofs for no line other thatn thisline can refer to the last line}
{	of such}

   var
    localBool, doIt: boolean;
    lastTIindex: integer;

  begin
   localBool := false;
   doIt := false;
   if ok then
    if (thisline <> TProofline(item)) then
     begin
      if (alreadyCut <> nil) then
       begin
       if (alreadyCut.GetSameItemNo(item) = 0) then  (*This means that the one*)
                                                (*that does the referring is not in the already cut list*)
       doIt := TRUE;
       end
      else
       doIt := TRUE;

      if doIt then
       begin
       localBool := (TProofLine(item).fLineno > lineno) and ((TProofLine(item).ffirstjustno = lineno) or (TProofLine(item).fsecondjustno = lineno) or (TProofLine(item).fthirdjustno = lineno));

       if not localBool then
       begin
       beforeHere := fHead.GetSameItemNo(item); (*trying to find its index*)

       if beforeHere = 0 then
       beforeHere := fHead.fSize + fTail.GetSameItemNo(item);

       if TProofLine(item).fjustification = ' ~I' then
       if LastAssLineno(beforeHere, lastTIindex) = lineno then
       localBool := TRUE;

       if TProofLine(item).fjustification = ' �I' then
       if LastAssLineno(beforeHere, lastTIindex) = lineno then
       localBool := TRUE;

       if TProofLine(item).fjustification = ' EI' then
       if LastAssLineno(beforeHere, lastTIindex) = lineno then
       localBool := TRUE;

       if TProofLine(item).fjustification = ' �E' then
       begin
       if LastAssLineno(beforeHere, lastTIindex) = lineno then
       localBool := TRUE;

       if LastAssLineno(IndexOfLineno(TProofLine(item).fSecondjustno), lastTIindex) = lineno then
       localBool := TRUE;
       end;

       if TProofLine(item).fjustification = ' �I' then
       begin
       if LastAssLineno(beforeHere, lastTIindex) = lineno then
       localBool := TRUE;
       if LastAssLineno(IndexOfLineno(TProofLine(item).fFirstjustno), lastTIindex) = lineno then
       localBool := TRUE;
       end;

       end;  {localbool}
       end;  {doit}

      if localBool then
       ok := false;

     end;  {tProofline}
   Refers := localBool;
  end;   {lot}

 begin
  ok := TRUE;

  lineno := thisLine.fLineno;

  search := fTail.LastThat(Refers);

  if ok then
   search := fHead.LastThat(Refers);

  if ok then
   if thisLine = TProofLine(fHead.First) then
    ok := false;

  if ok then {next line must be out of subproof}
   if thisLine.fLastassumption then
    begin
     nextline := nil;
     index := fTail.GetSameItemNo(thisLine);

     if index <> 0 then
      index := index + fHead.fSize
     else
      index := fHead.GetSameItemNo(thisLine);

     index := index + 1;   (*looking for nextline*)

     if index <= fHead.fSize then
      nextline := TProofLine(fHead.At(index))
     else if (index <= fHead.fSize + fTail.fSize) then
      nextline := TProofLine(fTail.At(index - fHead.fSize));


     if nextline <> nil then
      if (alreadyCut <> nil) then
       while (alreadyCut.GetSameItemNo(nextline) <> 0) and (index <= (fHead.fSize + fTail.fSize)) do (*means*)
                                                        (*nextline has been cut so should not be counted*)
       begin
       index := index + 1;   (*looking for nextline*)

       if index <= fHead.fSize then
       nextline := TProofLine(fHead.At(index))
       else if (index <= fHead.fSize + fTail.fSize) then
       nextline := TProofLine(fTail.At(index - fHead.fSize));
       end;



     if nextline <> nil then  (*nextline has to be out of subproof*)
      begin
       if (nextline.fSubprooflevel > thisLine.fSubprooflevel) then
       ok := false;

       if (nextline.fSubprooflevel = thisLine.fSubprooflevel) then
       if not nextline.fLastAssumption then  (*next subproof*)
       ok := false;
      end;

    end;

  if ok then {if after subproof then no other line must refer to last line of subproof}
   if (thisline.fjustification = ' ~I') or (thisline.fjustification = ' �I') or (thisline.fjustification = ' EI') or (thisline.fjustification = ' �I') or (thisline.fjustification = ' �E') then
    begin
     lineno := lineno - 1;

     search := fTail.LastThat(Refers);

     if ok then
      search := fHead.LastThat(Refers);

     if ok then
      if (thisline.fjustification = ' �I') then
       begin
       lineno := thisline.fFirstjustno;

       search := fTail.LastThat(Refers);

       if ok then
       search := fHead.LastThat(Refers);

       end;

     if ok then
      if (thisline.fjustification = ' �E') then
       begin
       lineno := thisline.fSecondjustno;

       search := fTail.LastThat(Refers);

       if ok then
       search := fHead.LastThat(Refers);

       end;

    end;
  LineCutable := ok;
 end;


    */


void removeInsertionMarker(){  // when removed always replaced with "?"
  if (fTail.size()>0) {
    TProofline tailFirstLine = (TProofline) getTailLine(0);

    if (tailFirstLine.fJustification.equals(kInsertionMarker))
       tailFirstLine.fJustification="?";
  }

}


void placeInsertionMarker(){  // the first line in the tail ends up with ? << as its justifiction
      if (fTail.size()>0) {
        TProofline tailFirstLine=(TProofline)getTailLine(0);

        tailFirstLine.fJustification=kInsertionMarker;  // this is "? <"
      }

     }

void insertAtTailFirst(TProofline newline){
   int headSize=fHead.size();

   if ((!newline.fBlankline)&&(fTail.size()>0))
      incrementLineNos(fTail, 1,((TProofline)fTail.get(0)).fLineno-1);

   if (headSize>0){
      TProofline headlastline;

      headlastline= (TProofline)fHead.get(headSize-1);

      if (newline.fBlankline)
        newline.fLineno=headlastline.fLineno;
      else
         newline.fLineno=headlastline.fLineno+1;

      newline.fHeadlevel = headlastline.fHeadlevel;
    newline.fSubprooflevel = headlastline.fSubprooflevel;// {check}
   }
  else  //no head
  {
    if (newline.fBlankline)
     newline.fLineno = 0;
    else
     newline.fLineno = +1;
    newline.fHeadlevel = -1; //{no head}
    newline.fSubprooflevel = -1;
  }

  //fTail.add(0,newline);

  addToTail(0,newline);

       /*

        procedure TProofWindow.InsertAtTailFirst (thisLine: TProofLine);
{check May 90 used to be a var param}


  var
   headlastline: TProofLine;

 begin

  if not thisLine.fBlankline then
   IncrementLineNos(fTail, 1);

  if fHead.fSize <> 0 then
   begin
    headlastline := TProofLine(fHead.Last);
    if thisLine.fBlankline then
     thisLine.fLineno := headlastline.fLineno
    else
     thisLine.fLineno := headlastline.fLineno + 1;

    thisLine.fHeadlevel := headlastline.fHeadlevel;
    thisLine.fSubprooflevel := headlastline.fSubprooflevel; {check}

   end
  else
   begin
    if thisLine.fBlankline then
     thisLine.fLineno := 0
    else
     thisLine.fLineno := +1;
    thisLine.fHeadlevel := -1; {no head}
    thisLine.fSubprooflevel := -1;
   end;

  fTail.InsertFirst(thisLine);
 end;


        */
}
void insertAtTailLast(TProofline newline){  //HERE
TProofline taillastline, headlastline ;

   if (fTail.size() <= 0){                  //no existing tail
     if (fHead.size() > 0) {               // but there is a head
       headlastline = (TProofline)fHead.get(fHead.size()-1);
           if (newline.fBlankline)
            newline.fLineno = headlastline.fLineno;
           else
            newline.fLineno = headlastline.fLineno + 1;
           newline.fHeadlevel = headlastline.fHeadlevel;
           newline.fSubprooflevel = headlastline.fSubprooflevel; // {check}

     }
     else {                                 //no tail and no head
          if (newline.fBlankline)
            newline.fLineno = 0;
        else
            newline.fLineno = + 1;
        newline.fHeadlevel = -1;           // no head
        newline.fSubprooflevel = -1;

     }
   }                                       //tail but no head
   else{
     taillastline = (TProofline)(fTail.get(fTail.size()-1));
           if (newline.fBlankline)
            newline.fLineno = taillastline.fLineno;
           else
            newline.fLineno = taillastline.fLineno + 1;
           newline.fHeadlevel = taillastline.fHeadlevel;
           newline.fSubprooflevel = taillastline.fSubprooflevel; // {check}

   }



 //  fTail.add(newline);
   addToTail(newline);
     }

/*

      procedure TProofWindow.InsertAtTailLast (thisLine: TProofLine);
      {check May 90 used to be a var param}


        var
         taillastline, headlastline: TProofLine;

       begin
        if fTail.fSize <= 0 then
         begin
          if fHead.fSize <> 0 then
           begin
            headlastline := TProofLine(fHead.Last);
            if thisLine.fBlankline then
             thisLine.fLineno := headlastline.fLineno
            else
             thisLine.fLineno := headlastline.fLineno + 1;
            thisLine.fHeadlevel := headlastline.fHeadlevel;
            thisLine.fSubprooflevel := headlastline.fSubprooflevel; {check}

           end
          else
           begin
            if thisLine.fBlankline then
             thisLine.fLineno := 0
            else
             thisLine.fLineno := +1;
            thisLine.fHeadlevel := -1; {no head}
            thisLine.fSubprooflevel := -1;
           end;
         end
        else
         begin
          taillastline := TProofLine(fTail.Last);
          if thisLine.fBlankline then
           thisLine.fLineno := taillastline.fLineno
          else
           thisLine.fLineno := taillastline.fLineno + 1;
          thisLine.fHeadlevel := taillastline.fHeadlevel;
          thisLine.fSubprooflevel := taillastline.fSubprooflevel; {check}
         end;

        fTail.InsertLast(thisLine);
       end;


      */


void insertFirst(TProofline newline){

    newline.fLineno = 1;

    if (!newline.fBlankline){     //pushes other  lines down
       incrementLineNos(fHead, 1,-1);
       incrementLineNos(fTail, 1,-1);
     }

     addToHead(0,newline);

  }

  /*

     procedure TProofWindow.InsertFirst (thisLine: TProofLine);
   {check May 90 used to be a var param}


    begin
     if thisLine.fBlankline then
      thisLine.fLineno := 1
     else
      begin

       IncrementLineNos(fHead, 1);
       IncrementLineNos(fTail, 1);

       thisLine.fLineno := 1;

      end;

     fHead.InsertFirst(thisLine);
    end;


      */

public void resetSplitBetweenLists(int newHeadSize){
       /* our data structure is two lists, a Head and a Tail, occasionally we want more in the Head
           and less in the Tail, or vv-- no other change */

      /*Notice also that the first line of the tail, which can be only a ?, should have a << */



     int oldHeadSize=fHead.size();
     int oldTailSize =fTail.size();  //


   if ((newHeadSize>-1)
       &&(newHeadSize!=oldHeadSize)
       &&newHeadSize<=(oldHeadSize+oldTailSize)){   // no error anc change needed
     removeInsertionMarker();
     if (newHeadSize<oldHeadSize){    //transfer from head to tail
       int index = oldHeadSize-1;
       Object transferredObject;
       while (index>(newHeadSize-1)){
         transferredObject=fHead.remove(index);
        // fTail.add(0,transferredObject);
        addToTail(0,transferredObject);
         index=index-1;
       }

     }
     else{   //transfer from tail to head
       int count = newHeadSize-oldHeadSize;
       Object transferredObject;
       while (count>0){
         transferredObject=fTail.remove(0);
        // fHead.add(transferredObject);
        addToHead(transferredObject);
         count=count-1;
       }
     }
  }
  placeInsertionMarker();

  }


 void resetSelectables(){

 TProofline theLine;

   if (fTail.size() > 0) {
     Iterator iter = fTail.iterator();

     while (iter.hasNext()){
       theLine=(TProofline)iter.next();
       theLine.fSelectable=false;
       theLine.fSubProofSelectable=false;
     }
   }

   if (fHead.size()>0)
     resetToPseudoTail();
   
   fireSelectablesChanged();
 }
/*
           procedure TProofWindow.ResetSelectables;

       procedure Unselectable (item: TObject);

       begin
        TProofLine(item).fSelectable := false;
        TProofLine(item).fSubProofSelectable := false;
       end;

      begin
       fTail.Each(Unselectable); {items after insertion point are not selectable}

       if (fHead.fSize > 0) then
        ResetToPseudoTail;
      end;



         */

static void resetSelectablesToHere(ArrayList localHead, int limitIndex){  // this routine ignores subproofselection

   int index = 0;
   TProofline checkline;
   boolean doselect=false;
   ArrayList tempList;

   if ((limitIndex>=localHead.size())||limitIndex<0)
     limitIndex=localHead.size()-1;                   //bring it in range

   ArrayList tailList=listAssumptions(localHead, (TProofline)(localHead.get(limitIndex)));

   Iterator iter = localHead.iterator();


   while ((iter.hasNext())&&index<=limitIndex){
      checkline = (TProofline) iter.next();

      if ((checkline.fBlankline)||(checkline.fFormula).fInfo.equals("?"))
         doselect=false;
      else{
         tempList=listAssumptions(localHead, checkline);
         if (TFormula.subset(tempList,tailList))
            doselect=true;
         else
            doselect=false;
      }

      checkline.fSelectable=doselect;
      index+=1;

       }


   
       }


/*
             procedure TProofWindow.ResetSelectablesToHere (var localHead: TList; index: integer);

          var
           taillist, tempList: TList;
           count: integer;

          procedure CheckLine (item: TObject);

           var
            aProofLIne: TProofLine;
            doselect: boolean;

          begin
           if count <= index then
            begin

             aProofLIne := TProofLine(item);

             if aProofLIne.fBlankline then
              doselect := false
             else
              begin
               if (aProofLIne.fFormula.fInfo = '?') then
               doselect := false
               else
               begin
               tempList := NewList;
               ListAssumptions(localHead, aProofLIne, tempList);
               if Subset(tempList, taillist) then
               doselect := TRUE
               else
               doselect := false;
               tempList.DeleteAll;
               tempList.Free;
               end;
              end;

             TProofLine(item).fSelectable := doselect;
            end;
           count := count + 1;
          end;

         begin
          count := 1;

          taillist := NewList;
          ListAssumptions(localHead, TProofLine(localHead.At(index)), taillist);

          localHead.Each(CheckLine);

          taillist.DeleteAll;
          taillist.Free;

         end;


     */




 /*

  procedure TProofWindow.ResetToPseudoTail;

    var
     taillist, tempList: TList;
     top, count: integer;

    procedure CheckLine (item: TObject);

     var
      aProofLIne, theNextProofline: TProofLine;
      doselect: boolean;

    begin
     count := count + 1;
     aProofLIne := TProofLine(item);

     if aProofLIne.fBlankline then
      doselect := false
     else
      begin
       if (aProofLIne.fFormula.fInfo = '?') then
        doselect := false
       else
        begin
         tempList := NewList;
         ListAssumptions(fHead, aProofLIne, tempList);
         if Subset(tempList, taillist) then
         doselect := TRUE
         else
         doselect := false;
         tempList.DeleteAll;
         tempList.Free;
        end;
      end;

     TProofLine(item).fSelectable := doselect;

     TProofLine(item).fSubProofSelectable := false;
     if (count < top) then
      if not aProofLIne.fBlankline then
                      {IF NOT aProofLine.fSelectable THEN check}
       begin
        theNextProofline := TProofLine(fHead.At(count + 1));
        if theNextProofline.fBlankline then
         if (theNextProofline.fSubprooflevel < aProofLIne.fSubprooflevel) and TRUE then {check gTemplate}
         TProofLine(item).fSubProofSelectable := TRUE;
       end; {case of sub-proof-- note selectable followed by blankline}
    end;

   begin
    top := fHead.fSize;
    count := 0;

    taillist := NewList;
    ListAssumptions(fHead, TProofLine(fHead.Last), taillist);

    fHead.Each(CheckLine);

    taillist.DeleteAll;
    taillist.Free;

   end;



 */

void resetToPseudoTail(){
int top = fHead.size();
int count = 0;
TProofline checkline,theNextProofline;
boolean doselect=false;
ArrayList tempList;

ArrayList tailList=listAssumptions(fHead, (TProofline)(fHead.get(top-1)));

Iterator iter = fHead.iterator();


while (iter.hasNext()){
      count+=1;
      checkline = (TProofline) iter.next();

      if ((checkline.fBlankline)||
          checkline.fFormula==null||             // some old files with old file formats set this wrong, this is just a safety net
          (checkline.fFormula).fInfo.equals("?"))
         doselect=false;
      else{
         tempList=listAssumptions(fHead, checkline);
         if (TFormula.subset(tempList,tailList))
           doselect=true;
         else
           doselect=false;
      }

      checkline.fSelectable=doselect;

      /*
          begin
                   count := count + 1;
                   aProofLIne := TProofLine(item);

                   if aProofLIne.fBlankline then
                    doselect := false
                   else
                    begin
                     if (aProofLIne.fFormula.fInfo = '?') then
                      doselect := false
                     else
                      begin
                       tempList := NewList;
                       ListAssumptions(fHead, aProofLIne, tempList);
                       if Subset(tempList, taillist) then
                       doselect := TRUE
                       else
                       doselect := false;
                       tempList.DeleteAll;
                       tempList.Free;
                      end;
                    end;

                   TProofLine(item).fSelectable := doselect;


   */

      checkline.fSubProofSelectable=false;       //setting subproof selection,
                                                //looking for being followed by a blankline
                                                //of lesser subprooflevel

           // index is the index of the line of the proof we are looking at

   /*top is size of fHead so count runs to top-1, and count-1 is the index of checkline*/      //ALL THIS ALTERED DEC 05
   /* but to be followed by a blankline count needs to be less than top-1*/
      if ((count<(top-1))&&(!checkline.fBlankline)){
        theNextProofline=(TProofline)fHead.get(count);   //CHECK FOR OFF BY ONE ERROR

        if (theNextProofline.fBlankline&&((theNextProofline.fSubprooflevel)<checkline.fSubprooflevel))
          checkline.fSubProofSelectable=true;
      }

   //   index+=1;

}


      /*

            TProofLine(item).fSubProofSelectable := false;
            if (count < top) then
             if not aProofLIne.fBlankline then
                             {IF NOT aProofLine.fSelectable THEN check}
              begin
               theNextProofline := TProofLine(fHead.At(count + 1));
               if theNextProofline.fBlankline then
                if (theNextProofline.fSubprooflevel < aProofLIne.fSubprooflevel) and TRUE then {check gTemplate}
                TProofLine(item).fSubProofSelectable := TRUE;
              end; {case of sub-proof-- note selectable followed by blankline}
           end;



}*/

}

public int getRightMargin(){
  return
      fRightMargin;
}


public void setRightMargin(int margin){
  if ( (margin != fRightMargin) && (margin > 100)&&margin<10000) {
    fRightMargin = margin;

    Iterator iter = fHead.iterator();

    while (iter.hasNext()) {
      TProofline aProofLine = (TProofline) iter.next();

      aProofLine.fRightMargin = margin;
    }

    iter = fTail.iterator();

    while (iter.hasNext()) {
      TProofline aProofLine = (TProofline) iter.next();

      aProofLine.fRightMargin = margin;
    }

  //  fireContentsChanged(this,0, getSize()-1);
   //  fireTableRowsDeleted(0, getSize()-1);

  }
}


/*
 procedure TProofWindow.ListAssumptions (localHead: TList; theLine: TProofLine; var listHead: TList);

 {forms a list of new, non standing, assumption formulas of theLine using linerecords as cells}
 {listHead list should be created and destroyed elsewhere}

   var
    level, standinglevel: integer;

   function Premise (item: TObject): boolean;

    var
     aProofLIne: TProofLine;

   begin
    aProofLIne := TProofLine(item);
    Premise := (aProofLIne.fjustification = 'Ass') and (aProofLIne.fSubprooflevel = level) and (aProofLIne.fLineno <= theLine.fLineno);
   end;

  begin
   if theLine <> nil then
    begin
     level := theLine.fSubprooflevel;
     if TProofLine(localHead.First).fBlankline then
      standinglevel := -1
     else
      standinglevel := 0;

     while (level > standinglevel) do
      begin
       listHead.InsertFirst(TProofLine(localHead.LastThat(Premise)).fFormula);

       level := level - 1;
      end;
    end;

  end;



*/


static ArrayList listAssumptions(ArrayList localHead,TProofline theLine){
  /*
  {forms a list of new, non standing, assumption formulas of theLine DOES NOT use linerecords as cells}

  */

 int level, standinglevel;
 TProofline lastAss=null, testline=null;

  ArrayList returnList =new ArrayList();

  if ((theLine!=null)&&localHead!=null){
    if (localHead.size() > 0) {
       Iterator iter;

       level=theLine.fSubprooflevel;

       if (((TProofline)localHead.get(0)).fBlankline)
         standinglevel=-1;
       else
         standinglevel=0;

       while (level>standinglevel){

          lastAss=null;

          iter =localHead.iterator();       // we must run through the whole list for each level

         while (iter.hasNext()){
            testline=(TProofline)iter.next();


            if ((testline.fJustification).equals(TProofController.fAssJustification) &&
                (testline.fSubprooflevel== level) &&
                (testline.fLineno <= theLine.fLineno))
                    lastAss=testline;
          }

          if (lastAss!=null)
            returnList.add(0,lastAss.fFormula);  //change here to formula

          level-=1;
        }

      }
  }
  return
      returnList;
}


        public  void setLastAssumption(){

          getHeadLastLine().fLastassumption=true;

           /*

            procedure TProofWindow.SetLastAssumption;

        begin
         TProofLine(fHead.Last).fLastassumption := TRUE;
        end;


            */

         }


 TFormula firstAssumptionWithVariableFree(TFormula variable){

   TProofline searchline;

   TFormula returnFormula = null;

   Iterator iter = fHead.iterator();

   /*
    begin
         if not freevar then
          begin
           aProofline := TProofline(item);
           if aProofline.fselectable then
            if aProofline.fJustification = 'Ass' then
             freevar := aProofline.fFormula.Freetest(variablenode);
           if freevar then
            freeformula := aProofline.fFormula;
          end;
        end;

    s
    */

   while ( (returnFormula == null) && (iter.hasNext())) {
     searchline = (TProofline) iter.next();

     if ( (searchline.fSelectable) &&
         (searchline.fJustification.equals(TProofController.fAssJustification)) &&
         searchline.fFormula.freeTest(variable))
            returnFormula = searchline.fFormula;
   }

   return
       returnFormula;
 }

 public TProofline lineWithVariableFree(TFormula variable,
                                        TProofline end,
                                        String justification,
                                        int lowerLevel){

  TProofline searchline;

  TProofline returnLine = null;

  boolean keepSearching=true;

  Iterator iter = fHead.iterator();


  while (keepSearching&& (returnLine == null) && (iter.hasNext())) {
    searchline = (TProofline) iter.next();

    if (searchline.getLineNo()>end.getLineNo())
      keepSearching=false;
    else

    if ((!searchline.fBlankline) &&
        (searchline.fSubprooflevel>lowerLevel) &&
        (searchline.fSelectable) &&                          //assuming formula and justifcation not null
        (searchline.fJustification.equals(justification)) &&
         searchline.fFormula.freeTest(variable)){
            returnLine = searchline;
            keepSearching=false;
     }
  }

  return
      returnLine;
 }

 public TProofline variableFreeInProof(TFormula variable){

 TProofline searchline;

 TProofline returnLine = null;

 boolean keepSearching=true;

 Iterator iter = fHead.iterator();

 while (keepSearching&& (returnLine == null) && (iter.hasNext())) {
   searchline = (TProofline) iter.next();

   if ((!searchline.fBlankline) &&
        searchline.fFormula!=null&&
       searchline.fFormula.freeTest(variable)){
          returnLine = searchline;
          keepSearching=false;}
 }

if (fTail!=null)            //for this purpose need to search tail also
   iter = fTail.iterator();

 while (keepSearching&& (returnLine == null) && (iter.hasNext())) {
   searchline = (TProofline) iter.next();

   if ((!searchline.fBlankline) &&
        searchline.fFormula!=null&&
       searchline.fFormula.freeTest(variable)){
          returnLine = searchline;
          keepSearching=false;}
 }



 return
     returnLine;
}



 public boolean proofFinished(){
   if (fTail.size()==0){

     int size= fHead.size();


     if ((size>1)||((size==1)&&(!getHeadLastLine().fBlankline))) {  //we start empty proofs with a blankline
        return
            true;
     }
     return
         false;  //if head and tail are empty
   }
   return
       false;
}

 public boolean finishedAndNoAutomation(){
   boolean automation=false;
   if (fTail.size()==0){

     int size= fHead.size();


     if ((size>1)||((size==1)&&(!getHeadLastLine().fBlankline))) {  //we start empty proofs with a blankline
        Iterator iter = fHead.iterator();
        while (iter.hasNext()&& !automation)
           automation=((TProofline)(iter.next())).fDerived;
         return
         !automation;
     }
     return
         false;  //if head and tail are empty
   }
   return
       false;
}
 
 
 



}



