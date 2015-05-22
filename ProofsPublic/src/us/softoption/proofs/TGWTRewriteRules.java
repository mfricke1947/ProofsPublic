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

/*This is a Feb2013 Google GWT friendly version of 2012 TRewriteRules */

//scanned through 5/22/2015


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strEquals;
import static us.softoption.infrastructure.Symbols.strIntersection;
import static us.softoption.infrastructure.Symbols.strMemberOf;
import static us.softoption.infrastructure.Symbols.strMinus;
import static us.softoption.infrastructure.Symbols.strPowerSet;
import static us.softoption.infrastructure.Symbols.strSubsetOf;
import static us.softoption.infrastructure.Symbols.strUnion;
import static us.softoption.infrastructure.Symbols.strXProd;
import static us.softoption.parser.TFormula.unary;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import us.softoption.infrastructure.TPreferencesData;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;



/*what we are going to do here is to abstract the rewriting from the adding of a line. The rewriting can be
widespread but the line adding needs to be different for each logical system*/

/*maybe use gwt list box */

public class TGWTRewriteRules {
  String fPreSelection,fOldSelection,fSelection,fPostSelection,fSelectionRewrite;
  protected TextBox fBeforeText=new TextBox();
  protected TextBox fAfterText=new TextBox();
  protected TextBox fBeforeTextReference=new TextBox();
  protected TFormula fSelectionRoot=null;  //this is the root of the
                                           //text they have selected
  protected TFormula fNewRoot=null;
  protected String fLastRewrite="";
  TFormula fSelectedFormula;  //from proof

  private ListBox fRulesListBox=new ListBox();;
  protected ArrayList fRulesList=new ArrayList();

  TParser fParser=new TParser();





  /* This almost always wants to have its Deriver document because the undoable proof
edits set the fDirty field of the document */

/*   public TRewrite(){

     initialize();
   } */

public TGWTRewriteRules(TFormula selectedFormula,
                        TParser aParser /*TDeriverDocument itsDeriverDocument*/){
 //NEW    super(itsDeriverDocument);

 fParser=aParser;

 fPreSelection="";fSelection="";fPostSelection="";fSelectionRewrite="";
 fOldSelection="";

fBeforeText.setText(fParser.writeFormulaToString(selectedFormula));
fBeforeTextReference.setText(fParser.writeFormulaToString(selectedFormula));
fAfterText.setText("");

//fBeforeText.setReadOnly(true);
// unfortunabe;y the ipad won't let us do the correct selecting
//if the text is not editablle. So we make it editable
// but then check that it does not change
fAfterText.setReadOnly(true);


fNewRoot=new TFormula();
fSelectionRoot=new TFormula();

 fSelectedFormula=selectedFormula;

 fSelectionRewrite="";


     initialize();

}


  private void initialize(){
	      initializeRulesList();
          initializeRulesBox();

   /*to do       fBeforeText.addCaretListener(new CaretListener(){
        public void caretUpdate(CaretEvent e){
          int dot = e.getDot();
          int mark = e.getMark();
          if (dot!=mark)
            doChoice();
          ;}});
*/

 /*we need to act on selection change but don't have any
  * handy listener         
  */
          
// Create a new timer
          Timer pollTimer = new Timer () {
            public void run() {
            if(!fBeforeText.getText().equals(fBeforeTextReference.getText())){
            	fBeforeText.setText(fBeforeTextReference.getText());
            	//has to be set editable but we don't want eidting
            }
            	
              fSelection=fBeforeText.getSelectedText();
              if (!fSelection.equals(fOldSelection)){  //a change
            	  if (fSelection.equals(""))
            		  fAfterText.setText(fBeforeTextReference.getText());
            	  //nothing selected set new to old
            	  else
            		  doChoice(); //a selection that may rewrite
            	  fOldSelection=fSelection.substring(0); //need copy
              }
            }
          };       
          
          pollTimer.scheduleRepeating(1000);  // every second
          
  
  }


/*
    The TextComponentDemo program uses a caret listener to display the current position of the caret or, if text is selected, the extent of the selection.

    The caret listener class in this example is a JLabel subclass. Here's the code that creates the caret listener label and makes it a caret listener of the text pane:

        //Create the status area
        CaretListenerLabel caretListenerLabel = new CaretListenerLabel(
                                                    "Caret Status");
        ...
        textPane.addCaretListener(caretListenerLabel);

    A caret listener must implement one method, caretUpdate, which is called each time the caret moves or the selection changes. Here's the CaretListenerLabel implementation of caretUpdate:

        public void caretUpdate(CaretEvent e) {
            //Get the location in the text
            int dot = e.getDot();
            int mark = e.getMark();
            if (dot == mark) {  // no selection
                try {
                    Rectangle caretCoords = textPane.modelToView(dot);
                    //Convert it to view coordinates
                    setText("caret: text position: " + dot +
                            ", view location = [" +
                            caretCoords.x + ", " + caretCoords.y + "]" +
                            newline);
                } catch (BadLocationException ble) {
                    setText("caret: text position: " + dot + newline);
                }
             } else if (dot < mark) {
                setText("selection from: " + dot + " to " + mark + newline);
             } else {
                setText("selection from: " + mark + " to " + dot + newline);
             }
        }


As you can see, this listener updates its text label to reflect the current state of the caret or selection.

*/

 void doChoice(){
	 	 
   if(testOldFormula()){  //boolean error?
	   
	//	 Window.alert("Demo 1 click");
    
      AbstractRule rule=(AbstractRule) getSelectedRule();

      if (rule!=null&&(rule.doRule()))
           putNewFormula(); 
   }
   else{
	   fAfterText.setText("");
	   fAfterText.setText(fBeforeText.getText());
   }
 }



//getSelectedItem


/*************** some getters and setters ************/

 public String getLastRewrite(){
    return
        fLastRewrite;
  }

  public TextBox getBeforeText(){
    return
        fBeforeText;
  }

  public TextBox getAfterText(){
  return
      fAfterText;
}

public ListBox getListBox(){
  return
      fRulesListBox;
}



  public TFormula getNewRoot(){
    return
        fNewRoot;
  }

  public TFormula getSelectionRoot(){
    return
        fSelectionRoot;
  }

 public TFormula getAfterRoot(){
   // we need to find the entire after formula

   TFormula afterRoot = new TFormula();
   StringReader aReader = new StringReader(fAfterText.getText());
   ArrayList dummy = new ArrayList();

   boolean wellFormed = fParser.wffCheck(afterRoot, /*dummy,*/ aReader);

   if (wellFormed)
     return afterRoot;
   else
     return
         null;
 }


/******  end of getters *******/




boolean testOldFormula(){
  boolean wellFormed=false;
  
  String entry=fBeforeText.getText();
  int selStart=fBeforeText.getCursorPos();
  fSelection=fBeforeText.getSelectedText();
 // int selEnd=fBeforeText.getSelectionEnd();
  //int entryEnd=entry.length();

  fPreSelection=entry.substring(0, selStart);
  fPostSelection=entry.substring(selStart+fSelection.length(),
		  entry.length());
 
  fSelectionRoot = new TFormula();
  StringReader aReader = new StringReader(fSelection);
  ArrayList dummy = new ArrayList();

  wellFormed = fParser.wffCheck(fSelectionRoot, /*dummy*/aReader);


/*The subformula check is for this with AvBcd they could select AvBc which is WFF but not a subformula*/

if (!wellFormed||
	!(fSelectedFormula).subFormulaOccursInFormula(fSelectionRoot, fSelectedFormula)){
//Toolkit.getDefaultToolkit().beep(); //give error message?
//fBeforeText.setSelectionRange(0,0);            // we'll clear the selection if it is not well formed

return
		false;  //not occurs
}
  
  /* to do
  
  String entry=fBeforeText.getText();
  int selStart=fBeforeText.getSelectionStart();
  int selEnd=fBeforeText.getSelectionEnd();
  //int entryEnd=entry.length();

  try
     {fPreSelection=fBeforeText.getText(0,selStart);}  //NB getText uses offset, len
  catch (BadLocationException e)
     {fPreSelection="";
     System.out.print("Rewrite catch Pre");}
  fSelection=fBeforeText.getSelectedText();
  try
     {fPostSelection=fBeforeText.getText(selEnd,entry.length()-selEnd);}
  catch (BadLocationException ex)
     {fPostSelection="";
     System.out.print("Rewrite catch Post");
     }


            fSelectionRoot = new TFormula();
            StringReader aReader = new StringReader(fSelection);
            ArrayList dummy = new ArrayList();

            wellFormed = fParser.wffCheck(fSelectionRoot, /*dummy,aReader);


   /*The subformula check is for this with AvBcd they could select AvBc which is WFF but not a subformula

    if (!wellFormed||!(fSelectedFormula).subFormulaOccursInFormula(fSelectionRoot, fSelectedFormula)){
     Toolkit.getDefaultToolkit().beep(); //give error message?
     fBeforeText.select(0,0);            // we'll clear the selection if it is not well formed
   }
*/
  return
     wellFormed;
}


/*
    function TRewriteWindow.GetOldFormula (var preselection, selection, postselection: str255; var itsroot: TFormula): boolean;

      var
       entryView: TEditText;
       startsel, endsel: integer;
       newValuation: TList;
       entireStr: str255;

    {this next bit is a patch to prevent them selecting only Fa of a formula Fabc.}
    {This can only happen with predicates, and it can only happen at the end}
    {with the last one.}
    {We find the index of the last predicate,	 then parse it, then write it back}
    {to get its real length, then increase the selection to include it (if the selection does}
    {not already do so}

      function FindLastPredicator: integer;
       var
        index, i: integer;
      begin
       index := 0;
       for i := 1 to length(selection) do
        if selection[i] in gPredicates then
         index := i;
       FindLastPredicator := index;

      end;

      function IndexEndLastPredicator: integer;
       var
        lastPredForm: TFormula;
        lastindex: integer;
      begin
       lastindex := FindLastPredicator;
       if lastindex = 0 then
        IndexEndLastPredicator := 0
       else
        begin
         gIllformed := FALSE;
         gInputStr := concat(selection, postselection);
         GetStringInput;
         skip(1, standardfilter);  (*primes gCurrch, and gLookaheadCh*)
         skip(lastindex - 1, standardFilter);
         fProofWindow.fParser.Predicate(lastPredForm, gIllformed);
         if gIllformed then  {should never be}
          begin
           sysBeep(5); {check}
           IndexEndLastPredicator := 0
          end
         else
          begin
           gOutputStr := strNull;
           fProofWindow.fParser.WriteFormulaToString(lastPredForm, gOutputStr);
           lastPredForm.DismantleFormula;

           IndexEndLastPredicator := lastIndex + length(gOutputStr) - 1;
           gOutputStr := strNull;
          end;
        end;
      end;

      procedure AdjustEnd;
       var
        transfer: integer;
      begin
       transfer := (IndexEndLastPredicator - length(selection));
       if (transfer > 0) then
        begin
         selection := concat(selection, copy(postselection, 1, transfer));
         delete(postselection, 1, transfer);
        end;
      end;

     begin
      GetOldFormula := FALSE;

      preselection := strNull;
      selection := strNull;
      postselection := strNull;

      entryView := TEditText(SELF.FindSubView('VW03'));

      startsel := entryView.fTEView.fHTE^^.selstart;
      endsel := entryView.fTEView.fHTE^^.selend;

      entryView.GetText(entireStr);

      preselection := Copy(entireStr, 1, startsel); {check this on indices}

      selection := Copy(entireStr, startsel + 1, (endsel - startsel)); {check this on indices}


      postselection := Copy(entireStr, endsel + 1, length(entireStr) - endsel); {check}
    {               this on indices}

      AdjustEnd;  (*a patch*)

      gIllformed := FALSE;
      gInputStr := selection;
      GetStringInput;
      skip(1, standardfilter);  (*primes gCurrch, and gLookaheadCh*)
      fProofWindow.fParser.wffcheck(itsroot, newValuation, gIllformed);
      if gIllformed then
       begin
        sysBeep(5); {check}
       end
      else
       GetOldFormula := TRUE;

 end;

*/


void putNewFormula(){
  if (!(fNewRoot==null||fParser.writeFormulaToString(fNewRoot).equals("")))   // we'll only do this if there is change
  {if ((fPreSelection.length()==0)&&
      (fSelection.length()>0)&&
      (fPostSelection.length()==0)

        ){

      if ((fSelection.charAt(0)!= '('))
       fSelectionRewrite = fParser.writeFormulaToString(fNewRoot);   //we'll omit brackets
     else
       fSelectionRewrite = fParser.writeInner(fNewRoot);


        /*
         {the problem here is that the user has the choice}
         {suppressing extra brackets-- we don't want to give her more than she gives}
         {us}

         */
      }
      else
        fSelectionRewrite = fParser.writeInner(fNewRoot);


      //{June 1990 but then you get the problem of p:-pVp and then the pVp associating incorrectly!}

    fAfterText.setText(fPreSelection + fSelectionRewrite + fPostSelection);
    }
  }


/*
 procedure TRewriteWindow.PutNewFormula (preselection, selection, postselection: str255; itsroot: TFormula);

  var
   exitView: TStaticText;

 begin
  if length(selection) > 0 then
   begin
    if (length(preselection) = 0) and (length(postselection) = 0) then
     begin
      if selection[1] <> '(' then
       fProofWindow.fParser.WriteFormulatoString(itsroot, selection) {the problem here is that the user}
{                                                                 has the choice}
{    else of suppressing extra brackets-- we don't want to give her more than she gives}
{                     us}

      else

       fProofWindow.fParser.WriteInner(itsroot, selection);
     end
    else


{if selection[1] <> '(' then changed June90}
{    fProofWindow.fParser.WriteFormulatoString(itsroot, selection) the problem here is that the user}
{                                                                 has the choice}
{    else of suppressing extra brackets-- we don't want to give her more than she gives}
{                     us}



     fProofWindow.fParser.WriteInner(itsroot, selection);

{June 1990 but then you get the problem of p:-pVp and then the pVp associating incorrectly!}

    itsroot.DismantleFormula;
    exitView := TStaticText(SELF.FindSubView('VW05'));
    exitView.SetText(concat(preselection, selection, postselection), TRUE);
   end;
 end;


*/

/********************************** The Rules ************************************/

/*The rules are implemented as objects */

class AbstractRule{
 // public String fJustification="Error fJustification not defined";

  boolean doRule() {
    return
        false;
  }

  public String toString() {
    return
        "Error toString not defined";
  }
}

class DoAndAssocLR extends AbstractRule{


  boolean doRule() {
    TFormula one,two,three;
    if (fParser.isAnd(fSelectionRoot)&&fParser.isAnd(fSelectionRoot.getRLink())) {
      fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      one = fNewRoot.fLLink; //{p}           (p^(q^r))
      two  = fNewRoot.fRLink.fLLink;// {q}
      three = fNewRoot.fRLink.fRLink; //{r}

      fNewRoot.fLLink = fNewRoot.fRLink;
     fNewRoot.fRLink = three;
     fNewRoot.fLLink.fLLink = one;
     fNewRoot.fLLink.fRLink = two;


      fLastRewrite = " Assoc";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Association &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderAnd()+"(q"+fParser.renderAnd()+"r) :- (p"+fParser.renderAnd()+"q)"+fParser.renderAnd()+"r"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Association    "+

	        "p"+fParser.renderAnd()+"(q"+fParser.renderAnd()+"r) :- (p"+fParser.renderAnd()+"q)"+fParser.renderAnd()+"r"+

            "";
	  }
}


/*
 function TRewriteWindow.DoAndAssocLR (var oldformula, newformula: TFormula): boolean;

  var
   one, two, three: TFormula;

 begin
  if oldformula.finfo = chAnd then
   if oldformula.frlink.finfo = chAnd then
    begin
     newformula := oldformula;

     one := newformula.fllink; {p}
     two := newformula.frlink.fllink; {q}
     three := newformula.frlink.frlink; {r}

     newformula.fllink := newformula.frlink;
     newformula.frlink := three;
     newformula.fllink.fllink := one;
     newformula.fllink.frlink := two;

     oldformula := nil;
     fLastRewrite := ' Assoc';
     DoAndAssocLR := TRUE;
    end
   else
    begin
     DoAndAssocLR := FALSE;
     oldformula.DismantleFormula;
    end;
 end;
*/

class DoAndAssocRL extends AbstractRule{


  boolean doRule() {
    TFormula one,two,three;
    if (fParser.isAnd(fSelectionRoot)&&fParser.isAnd(fSelectionRoot.getLLink())) {
      fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      one = fNewRoot.fLLink.fLLink; //{p}           (p^q)^r)
      two  = fNewRoot.fLLink.fRLink;// {q}
      three = fNewRoot.fRLink; //{r}

      fNewRoot.fRLink = fNewRoot.fLLink;
     fNewRoot.fLLink = one;
     fNewRoot.fRLink.fLLink = two;
     fNewRoot.fRLink.fRLink = three;

     /*
           one := newformula.fllink.fllink; {p}
           two := newformula.fllink.frlink; {q}
           three := newformula.frlink; {r}

           newformula.frlink := newformula.fllink;
           newformula.fllink := one;
           newformula.frlink.fllink := two;
           newformula.frlink.frlink := three;


     */



      fLastRewrite = " Assoc";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Association &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "(p"+fParser.renderAnd()+"q)"+fParser.renderAnd()+"r :- p"+fParser.renderAnd()+"(q"+fParser.renderAnd()+"r)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Association      "+

	        "(p"+fParser.renderAnd()+"q)"+fParser.renderAnd()+"r :- p"+fParser.renderAnd()+"(q"+fParser.renderAnd()+"r)"+

	        "";
	  }
}

class DoOrAssocLR extends AbstractRule{


  boolean doRule() {
    TFormula one,two,three;
    if (fParser.isOr(fSelectionRoot)&&fParser.isOr(fSelectionRoot.getRLink())) {
      fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      one = fNewRoot.fLLink; //{p}           (p^(q^r))
      two  = fNewRoot.fRLink.fLLink;// {q}
      three = fNewRoot.fRLink.fRLink; //{r}

      fNewRoot.fLLink = fNewRoot.fRLink;
     fNewRoot.fRLink = three;
     fNewRoot.fLLink.fLLink = one;
     fNewRoot.fLLink.fRLink = two;


      fLastRewrite = " Assoc";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Association &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderOr()+"(q"+fParser.renderOr()+"r) :- (p"+fParser.renderOr()+"q)"+fParser.renderOr()+"r"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Association       "+

	        "p"+fParser.renderOr()+"(q"+fParser.renderOr()+"r) :- (p"+fParser.renderOr()+"q)"+fParser.renderOr()+"r"+

	        "";
	  }  
}

class DoOrAssocRL extends AbstractRule{


  boolean doRule() {
    TFormula one,two,three;
    if (fParser.isOr(fSelectionRoot)&&fParser.isOr(fSelectionRoot.getLLink())) {
      fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      one = fNewRoot.fLLink.fLLink; //{p}           (p^q)^r)
      two  = fNewRoot.fLLink.fRLink;// {q}
      three = fNewRoot.fRLink; //{r}

      fNewRoot.fRLink = fNewRoot.fLLink;
     fNewRoot.fLLink = one;
     fNewRoot.fRLink.fLLink = two;
     fNewRoot.fRLink.fRLink = three;

     /*
           one := newformula.fllink.fllink; {p}
           two := newformula.fllink.frlink; {q}
           three := newformula.frlink; {r}

           newformula.frlink := newformula.fllink;
           newformula.fllink := one;
           newformula.frlink.fllink := two;
           newformula.frlink.frlink := three;


     */



      fLastRewrite = " Assoc";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Association &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "(p"+fParser.renderOr()+"q)"+fParser.renderOr()+"r :- p"+fParser.renderOr()+"(q"+fParser.renderOr()+"r)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Association      "+

	        "(p"+fParser.renderOr()+"q)"+fParser.renderOr()+"r :- p"+fParser.renderOr()+"(q"+fParser.renderOr()+"r)"+

	        "";
	  }  
}

class DoCommAnd extends AbstractRule{


  boolean doRule() {
    if (fParser.isAnd(fSelectionRoot)) {
      fNewRoot = fSelectionRoot;
      fSelectionRoot = fNewRoot.fLLink;
      fNewRoot.fLLink = fNewRoot.fRLink;
      fNewRoot.fRLink = fSelectionRoot; //commuting
      fLastRewrite = " Com";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Commutation &nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderAnd()+"q :: q"+fParser.renderAnd()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Commutation "+
	        "p"+fParser.renderAnd()+"q :: q"+fParser.renderAnd()+"p"+
	        "";
	  }
}

class DoCommOr extends AbstractRule{

  boolean doRule() {
    if (fParser.isOr(fSelectionRoot)) {
      fNewRoot = fSelectionRoot;
      fSelectionRoot = fNewRoot.fLLink;
      fNewRoot.fLLink = fNewRoot.fRLink;
      fNewRoot.fRLink = fSelectionRoot; //commuting
      fLastRewrite = " Com";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Commutation &nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderOr()+"q :: q"+fParser.renderOr()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Commutation   "+

	        "p"+fParser.renderOr()+"q :: q"+fParser.renderOr()+"p"+

	        "";
	  }
}
/*
 function TRewriteWindow.DoComm (var oldformula, newformula: TFormula): boolean;

 begin
  if oldformula.finfo = chAnd then
   begin
    newformula := oldformula;
    oldformula := newformula.fllink;
    newformula.fllink := newformula.frlink; {commuting}
    newformula.frlink := oldformula;
    oldformula := nil;
    fLastRewrite := ' Com';
    DoComm := TRUE;
   end
  else
   begin
    DoComm := FALSE;
    oldformula.DismantleFormula;

   end;
 end;

*/

class DoDMAnd extends AbstractRule{


  boolean doRule() {
    TFormula p,q,formulanode;

    if (fParser.isAnd(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.getLLink())&&
        fParser.isNegation(fSelectionRoot.getRLink())) {

       fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      p = (fNewRoot.fLLink.fRLink).copyFormula(); //{p}
      q  = (fNewRoot.fRLink.fRLink).copyFormula();// {q}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chOr),p,q);
      fNewRoot=new TFormula(TFormula.unary,String.valueOf(chNeg),null,formulanode);

      fLastRewrite = " De M";
      return
          true;
    }

    if (fParser.isNegation(fSelectionRoot)&&
      fParser.isOr(fSelectionRoot.getRLink())) {

     fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

    p = (fNewRoot.fRLink.fLLink).copyFormula(); //{p}
    q  = (fNewRoot.fRLink.fRLink).copyFormula();// {q}

    formulanode=new TFormula(TFormula.binary,String.valueOf(chAnd),
                             new TFormula(TFormula.unary,String.valueOf(chNeg),null,p),
                             new TFormula(TFormula.unary,String.valueOf(chNeg),null,q));
    fNewRoot=formulanode;

    fLastRewrite = " De M";
    return
        true;
  }




    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>De Morgan &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        fParser.renderNot()+"p"+fParser.renderAnd()+fParser.renderNot()+"q :: "
        +fParser.renderNot()+"(p"+fParser.renderOr()+"q)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "De Morgan       "+

	        fParser.renderNot()+"p"+fParser.renderAnd()+fParser.renderNot()+"q :: "
	        +fParser.renderNot()+"(p"+fParser.renderOr()+"q)"+

	        "";
	  }  
}

/*
 function TRewriteWindow.DoDMAnd (var oldformula, newformula: TFormula): boolean;

   var
    p, q, formulanode: TFormula;
    tempboolean: boolean;

  begin
   tempboolean := FALSE;

   if oldformula.finfo = chAnd then
    if oldformula.fllink.finfo = chNeg then
     if oldformula.frlink.finfo = chNeg then
      begin

       p := oldformula.fllink.frlink; {p}
       q := oldformula.frlink.frlink; {q}

       SupplyFormula(formulanode);
       formulanode.fKind := binary;
       formulanode.finfo := chOr;
       formulanode.fllink := p.CopyFormula;
       formulanode.frlink := q.CopyFormula;

       newformula := formulanode;
       formulanode := nil;

       SupplyFormula(formulanode);
       formulanode.fKind := unary;
       formulanode.finfo := chNeg;
       formulanode.frlink := newformula;
       newformula := formulanode;
       formulanode := nil;

       oldformula.DismantleFormula;

       oldformula := nil;
       fLastRewrite := ' De M';
       tempboolean := TRUE;
      end;

   if not tempboolean then {second case}
    if oldformula.finfo = chNeg then
     if oldformula.frlink.finfo = chOr then
      begin

       p := oldformula.frlink.fllink; {p}
       q := oldformula.frlink.frlink; {q}

       SupplyFormula(formulanode);
       formulanode.fKind := unary;
       formulanode.finfo := chNeg;
       formulanode.frlink := p.CopyFormula;

       newformula := formulanode;
       formulanode := nil;

       SupplyFormula(formulanode);
       formulanode.fKind := binary;
       formulanode.finfo := chAnd;
       formulanode.fllink := newformula;
       newformula := formulanode;
       formulanode := nil;

       SupplyFormula(formulanode);
       formulanode.fKind := unary;
       formulanode.finfo := chNeg;
       formulanode.frlink := q.CopyFormula;

       newformula.frlink := formulanode;
       formulanode := nil;

       oldformula.DismantleFormula;

       oldformula := nil;
       fLastRewrite := ' De M';
       tempboolean := TRUE;
      end;

   if not tempboolean then
    oldformula.DismantleFormula;

   DoDMAnd := tempboolean;
 end;

*/


class DoDMOr extends AbstractRule{


  boolean doRule() {
    TFormula p,q,formulanode;

    if (fParser.isOr(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.getLLink())&&
        fParser.isNegation(fSelectionRoot.getRLink())) {

       fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

      p = (fNewRoot.fLLink.fRLink).copyFormula(); //{p}
      q  = (fNewRoot.fRLink.fRLink).copyFormula();// {q}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chAnd),p,q);
      fNewRoot=new TFormula(TFormula.unary,String.valueOf(chNeg),null,formulanode);

      fLastRewrite = " De M";
      return
          true;
    }

    if (fParser.isNegation(fSelectionRoot)&&
      fParser.isAnd(fSelectionRoot.getRLink())) {

     fNewRoot = fSelectionRoot.copyFormula(); //we do surgery and later compare if old = new so we need a copy for that test to work

    p = (fNewRoot.fRLink.fLLink).copyFormula(); //{p}
    q  = (fNewRoot.fRLink.fRLink).copyFormula();// {q}

    formulanode=new TFormula(TFormula.binary,String.valueOf(chOr),
                             new TFormula(TFormula.unary,String.valueOf(chNeg),null,p),
                             new TFormula(TFormula.unary,String.valueOf(chNeg),null,q));
    fNewRoot=formulanode;

    fLastRewrite = " De M";
    return
        true;
  }




    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>De Morgan &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        fParser.renderNot()+"p"+fParser.renderOr()+fParser.renderNot()+"q :: "
        +fParser.renderNot()+"(p"+fParser.renderAnd()+"q)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "De Morgan     "+
	        fParser.renderNot()+"p"+fParser.renderOr()+fParser.renderNot()+"q :: "
	        +fParser.renderNot()+"(p"+fParser.renderAnd()+"q)"+

	        "";
	  }
}


class DoDistribAnd extends AbstractRule{


  boolean doRule() {
    TFormula p,q,r,formulanode;
    if (fParser.isAnd(fSelectionRoot)&&fParser.isOr(fSelectionRoot.getRLink())) {

      p = fSelectionRoot.fLLink; //{p}           (p^(qvr))
      q  = fSelectionRoot.fRLink.fLLink;// {q}
      r = fSelectionRoot.fRLink.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chOr),
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            p,
                                            q),
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            p,
                                            r));


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Dist";
      return true;
    }
    // right to left
    if (fParser.isOr(fSelectionRoot)&&
        fParser.isAnd(fSelectionRoot.getLLink())&&
        fParser.isAnd(fSelectionRoot.getRLink())&&
        fSelectionRoot.getLLink().getLLink().equalFormulas(fSelectionRoot.getLLink().getLLink(),
                                                           fSelectionRoot.getRLink().getLLink())) {

      p = fSelectionRoot.fLLink.fLLink; //{p}           (p^q)v(p^r))
      q = fSelectionRoot.fLLink.fRLink;
      r = fSelectionRoot.fRLink.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chAnd),
                               p,
                               new TFormula(TFormula.binary,String.valueOf(chOr),
                                            q,
                                            r));


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Dist";
      return true;
    }






    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Distribution &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderAnd()+"(q"+fParser.renderOr()+"r) :: (p"
        +fParser.renderAnd()+"q)"+fParser.renderOr()+"(p"+fParser.renderAnd()+"r)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Distribution     "+
	        "p"+fParser.renderAnd()+"(q"+fParser.renderOr()+"r) :: (p"
	        +fParser.renderAnd()+"q)"+fParser.renderOr()+"(p"+fParser.renderAnd()+"r)"+
	        "";
	  }  
}

/*
 function TRewriteWindow.DoDistribAnd (var oldformula, newformula: TFormula): boolean;

   var
    p, q, r, formulanode: TFormula;
    tempboolean: boolean;

  begin
   tempboolean := FALSE;

   if oldformula.finfo = chAnd then
    if oldformula.frlink.finfo = chOr then
     begin

      p := oldformula.fllink; {p}
      q := oldformula.frlink.fllink; {q}
      r := oldformula.frlink.frlink; {r}

      SupplyFormula(formulanode);
      formulanode.fKind := binary;
      formulanode.finfo := chOr;
      newformula := formulanode;
      formulanode := nil;

      SupplyFormula(formulanode);
      formulanode.fKind := binary;
      formulanode.finfo := chAnd;
      formulanode.fllink := p.CopyFormula;
      formulanode.frlink := q.CopyFormula;
      newformula.fllink := formulanode;
      formulanode := nil;

      SupplyFormula(formulanode);
      formulanode.fKind := binary;
      formulanode.finfo := chAnd;
      formulanode.fllink := p.CopyFormula;
      formulanode.frlink := r.CopyFormula;
      newformula.frlink := formulanode;
      formulanode := nil;

      oldformula.DismantleFormula;

      oldformula := nil;
      fLastRewrite := ' Dist';
      tempboolean := TRUE;
     end;

   if not tempboolean then {second case}
    if oldformula.finfo = chOr then
     if oldformula.fllink.finfo = chAnd then
      if oldformula.frlink.finfo = chAnd then
       if EqualFormulas(oldformula.fllink.fllink, oldformula.frlink.fllink) then
        begin

        p := oldformula.fllink.fllink; {p}
        q := oldformula.fllink.frlink; {q}
        r := oldformula.frlink.frlink; {r}

        SupplyFormula(formulanode);
        formulanode.fKind := binary;
        formulanode.finfo := chAnd;
        newformula := formulanode;
        formulanode := nil;

        newformula.fllink := p.CopyFormula;

        SupplyFormula(formulanode);
        formulanode.fKind := binary;
        formulanode.finfo := chOr;
        formulanode.fllink := q.CopyFormula;
        formulanode.frlink := r.CopyFormula;
        newformula.frlink := formulanode;
        formulanode := nil;

        oldformula.DismantleFormula;

        oldformula := nil;
        fLastRewrite := ' Dist';
        tempboolean := TRUE;
        end;

   if not tempboolean then
    oldformula.DismantleFormula;

   DoDistribAnd := tempboolean;
 end;

*/

class DoDistribOr extends AbstractRule{


  boolean doRule() {
    TFormula p,q,r,formulanode;
    if (fParser.isOr(fSelectionRoot)&&fParser.isAnd(fSelectionRoot.getRLink())) {

      p = fSelectionRoot.fLLink; //{p}           (pv(q^r))
      q  = fSelectionRoot.fRLink.fLLink;// {q}
      r = fSelectionRoot.fRLink.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chAnd),
                               new TFormula(TFormula.binary,String.valueOf(chOr),
                                            p,
                                            q),
                               new TFormula(TFormula.binary,String.valueOf(chOr),
                                            p,
                                            r));  //(pvq)^(pvr))


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Dist";
      return true;
    }
    // right to left
    if (fParser.isAnd(fSelectionRoot)&&
        fParser.isOr(fSelectionRoot.getLLink())&&
        fParser.isOr(fSelectionRoot.getRLink())&&
        fSelectionRoot.getLLink().getLLink().equalFormulas(fSelectionRoot.getLLink().getLLink(),
                                                           fSelectionRoot.getRLink().getLLink())){

      p = fSelectionRoot.fLLink.fLLink; //{p}           (pvq)^(pvr))
      q = fSelectionRoot.fLLink.fRLink;
      r = fSelectionRoot.fRLink.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chOr),
                               p,
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            q,
                                            r));


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Dist";
      return true;
    }






    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Distribution &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderOr()+"(q"+fParser.renderAnd()+"r) :: (p"
        +fParser.renderOr()+"q)"+fParser.renderAnd()+"(p"+fParser.renderOr()+"r)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Distribution      "+

	        "p"+fParser.renderOr()+"(q"+fParser.renderAnd()+"r) :: (p"
	        +fParser.renderOr()+"q)"+fParser.renderAnd()+"(p"+fParser.renderOr()+"r)"+
	        "";
	  }
}


class DoDNLR extends AbstractRule{


  boolean doRule() {
    TFormula p;

    p=fSelectionRoot.copyFormula();

    fNewRoot=new TFormula(TFormula.unary,String.valueOf(chNeg),null,
                   new TFormula(TFormula.unary,String.valueOf(chNeg),null,p));

      fLastRewrite = " DN";
      return
          true;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Double Neg &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+"p :- "+fParser.renderNot()+fParser.renderNot()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Double Neg      "+
	        "p :- "+fParser.renderNot()+fParser.renderNot()+"p"+
	        "";
	  }
}


/*
 function TRewriteWindow.DoDNLR (var oldformula, newformula: TFormula): boolean;

  var
   p, formulanode: TFormula;

 begin
  p := oldformula; {p}

  SupplyFormula(formulanode);
  formulanode.fKind := unary;
  formulanode.finfo := chNeg;
  formulanode.frlink := p.CopyFormula;
  newformula := formulanode;
  formulanode := nil;

  SupplyFormula(formulanode);
  formulanode.fKind := unary;
  formulanode.finfo := chNeg;
  formulanode.frlink := newformula;
  newformula := formulanode;
  formulanode := nil;

  oldformula.DismantleFormula;

  oldformula := nil;
  fLastRewrite := ' DN';

  DoDNLR := TRUE;
 end;

*/

class DoExtension extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,z,zMemberp,zMemberq;
	    if (fParser.isEquality(fSelectionRoot)) {  // p=q::(Allz)(z member p iff z member q)
	      p=fSelectionRoot.firstTerm().copyFormula();
	      q=fSelectionRoot.secondTerm().copyFormula();
	      
	      //we don't want to capture anything here
	      
	      Set <String> badVar=fParser.variablesInFormula(fSelectionRoot);
	      String goodVar=fParser.nthNewVariable(1,badVar);
	      
	      if (!goodVar.equals("")){
	      
	      z= new TFormula(TFormula.variable,String.valueOf(goodVar),
       		 null,
      		 null);
	      
	      zMemberp= new TFormula(TFormula.predicator,strMemberOf,
	         		 null,
	        		 null);
	      zMemberp.appendToFormulaList(z.copyFormula());
	      zMemberp.appendToFormulaList(p.copyFormula());
	      
	      zMemberq= new TFormula(TFormula.predicator,strMemberOf,
	         		 null,
	        		 null);
	      zMemberq.appendToFormulaList(z.copyFormula());
	      zMemberq.appendToFormulaList(q.copyFormula());
	      

	      fNewRoot = new TFormula(TFormula.quantifier,String.valueOf(chUniquant),
	    		                 z,
	    		                 new TFormula(TFormula.binary,String.valueOf(chEquiv),
	    		                		 zMemberp,
	    		                		 zMemberq)
                 );


	      fLastRewrite = " Extens";
	      return
	          true;
  
	    }
	    }


	    if (fParser.isUniquant(fSelectionRoot)&&
	        fParser.isEquiv(fSelectionRoot.scope())&&
	        fParser.isMemberOf(fSelectionRoot.scope().fLLink)&&
	        fParser.isMemberOf(fSelectionRoot.scope().fRLink)) {  // // p=q::(Allz)(z member p iff z member q)
	     
	    	TFormula z1,z2,z3,scope,left,right;
	    	
	    	scope=fSelectionRoot.scope();
	    	left=scope.fLLink;
	    	right=scope.fRLink;
	    	
	    	z1= fSelectionRoot.quantVarForm();
	    	z2= left.firstTerm();
	    	z3= right.firstTerm();
	    	
	    	if (z1.equalFormulas(z1, z2)&&
	    		z2.equalFormulas(z2, z3)	
	    	){
	    	    TFormula pSubsetq;
	    		p=left.secondTerm();
	    		q=right.secondTerm();
	    		
	    		pSubsetq= new TFormula(TFormula.equality,strEquals,
		         		 null,
		        		 null);
	    		pSubsetq.appendToFormulaList(p.copyFormula());
	    		pSubsetq.appendToFormulaList(q.copyFormula());
	    

	     fNewRoot = pSubsetq;


	     fLastRewrite = " Extens";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {// psubsetq::(Allz)(z member p -> z member q)
	    return
	        "<html>"+
	        "<em>Extensionality &nbsp;</em>"+
	        "<strong>"+
	        "p"+fParser.renderEquals()+"q :: "+
	        "("+fParser.renderUniquant()+"m)((m"+fParser.renderMemberOf()+"p)"+ fParser.renderEquiv()+"(m"+fParser.renderMemberOf()+"q))"+
	        "</strong>"+
	        "</html>";
	  }
	  
	  public String toString() {// psubsetq::(Allz)(z member p -> z member q)
		    return
		        "Extensionality  "+

		        "p"+fParser.renderEquals()+"q :: "+
		        "("+fParser.renderUniquant()+"m)((m"+fParser.renderMemberOf()+"p)"+ fParser.renderEquiv()+"(m"+fParser.renderMemberOf()+"q))"+

		        "";
		  }	  
	}

class DoDNRL extends AbstractRule{


  boolean doRule() {
    TFormula p;

    if (fParser.isNegation(fSelectionRoot)&&fParser.isNegation(fSelectionRoot.getRLink())) {

      p = fSelectionRoot.fRLink.fRLink.copyFormula();

      fNewRoot = p;

      fLastRewrite = " DN";
      return
          true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Double Neg &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+fParser.renderNot()+fParser.renderNot()+"p"+" :- p"+
        "</strong>"+
        "</html>";
  }


public String toString() {
    return
        "Double Neg      "+
        fParser.renderNot()+fParser.renderNot()+"p"+" :- p"+

        "";
  }
}



class DoEquiv1 extends AbstractRule{


  boolean doRule() {
    TFormula p,q,formulanode;
    if (fParser.isEquiv(fSelectionRoot)) {

      p = fSelectionRoot.fLLink; //{p}           p<->q
      q  = fSelectionRoot.fRLink;// {q}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chAnd),
                               new TFormula(TFormula.binary,String.valueOf(chImplic),
                                            p,
                                            q),
                               new TFormula(TFormula.binary,String.valueOf(chImplic),
                                            q,
                                            p));


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Equiv";
      return
          true;
    }
    // right to left
    if (fParser.isAnd(fSelectionRoot)&&
        fParser.isImplic(fSelectionRoot.getLLink())&&
        fParser.isImplic(fSelectionRoot.getRLink())&&  //p->q)^(q->p))
        fSelectionRoot.getLLink().getLLink().equalFormulas(fSelectionRoot.getLLink().getLLink(),
                                                           fSelectionRoot.getRLink().getRLink())
                                                           &&
        fSelectionRoot.getRLink().getLLink().equalFormulas(fSelectionRoot.getRLink().getLLink(),
                                                           fSelectionRoot.getLLink().getRLink())) {

      p = fSelectionRoot.fLLink.fLLink; //{p}           (p->q)^(q->p))
      q = fSelectionRoot.fLLink.fRLink;


      formulanode=new TFormula(TFormula.binary,String.valueOf(chEquiv),
                               p,
                               q);


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Equiv";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Equivalence &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderEquiv()+"q  :: (p"
        +fParser.renderImplic()+"q)"+fParser.renderAnd()+"(q"+fParser.renderImplic()+"p)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Equivalence      "+

	        "p"+fParser.renderEquiv()+"q  :: (p"
	        +fParser.renderImplic()+"q)"+fParser.renderAnd()+"(q"+fParser.renderImplic()+"p)"+
	        "";
	  } 
}


class DoEquiv2 extends AbstractRule{


  boolean doRule() {
    TFormula p,q,formulanode;
    if (fParser.isEquiv(fSelectionRoot)) {

      p = fSelectionRoot.fLLink.copyFormula(); //{p}           p<->q
      q  = fSelectionRoot.fRLink.copyFormula();// {q}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chOr),
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            p,
                                            q),
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            new TFormula(TFormula.unary,String.valueOf(chNeg),
                                                         null,
                                                         p),
                                            new TFormula(TFormula.unary,String.valueOf(chNeg),
                                                         null,
                                                         q)));


     fNewRoot = formulanode;

      fLastRewrite = " Equiv";
      return
          true;
    }
    // right to left
    if (fParser.isOr(fSelectionRoot)&&
        fParser.isAnd(fSelectionRoot.getLLink())&&
        fParser.isAnd(fSelectionRoot.getRLink())&&  //(p^q)v(~p^~q)
        fParser.isNegation(fSelectionRoot.getRLink().getLLink())&&
        fParser.isNegation(fSelectionRoot.getRLink().getRLink())&&
        fSelectionRoot.getLLink().getLLink().equalFormulas(fSelectionRoot.getLLink().getLLink(),
                                                           fSelectionRoot.getRLink().getLLink().getRLink())  //p
                                                           &&
        fSelectionRoot.getLLink().getRLink().equalFormulas(fSelectionRoot.getLLink().getRLink(),      //q
                                                           fSelectionRoot.getRLink().getRLink().getRLink())) {

      p = fSelectionRoot.fLLink.fLLink.copyFormula(); //{p}           (p^q)v(~p^~q)
      q = fSelectionRoot.fLLink.fRLink.copyFormula();


      formulanode=new TFormula(TFormula.binary,String.valueOf(chEquiv),
                               p,
                               q);


     fNewRoot = formulanode;

      fLastRewrite = " Equiv";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Equivalence &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderEquiv()+"q  :: (p"
        +fParser.renderAnd()+"q)"+fParser.renderOr()
        +"("+fParser.renderNot()+"p"+fParser.renderAnd()+fParser.renderNot()+"q)"+
        "</strong>"+
        "</html>";
  }
  public String toString() {
	    return
	        "Equivalence      "+

	        "p"+fParser.renderEquiv()+"q  :: (p"
	        +fParser.renderAnd()+"q)"+fParser.renderOr()
	        +"("+fParser.renderNot()+"p"+fParser.renderAnd()+fParser.renderNot()+"q)"+

	        "";
	  }
}


class DoExp extends AbstractRule{


  boolean doRule() {
    TFormula p,q,r,formulanode;
    if (fParser.isImplic(fSelectionRoot)&&
        fParser.isAnd(fSelectionRoot.getLLink())) {

      p = fSelectionRoot.fLLink.fLLink; //{p}           (p^q)->r)
      q  = fSelectionRoot.fLLink.fRLink;// {q}
      r = fSelectionRoot.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chImplic),
                               p,
                               new TFormula(TFormula.binary,String.valueOf(chImplic),
                                            q,
                                            r));


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Exp";
      return
          true;
    }
    // right to left p->(q->r)
    if (fParser.isImplic(fSelectionRoot)&&
        fParser.isImplic(fSelectionRoot.getRLink())) {

      p = fSelectionRoot.fLLink; //{p}           p->(q->r)
      q = fSelectionRoot.fRLink.fLLink;
      r = fSelectionRoot.fRLink.fRLink; //{r}

      formulanode=new TFormula(TFormula.binary,String.valueOf(chImplic),
                               new TFormula(TFormula.binary,String.valueOf(chAnd),
                                            p,
                                            q),
                               r);


     fNewRoot = formulanode.copyFormula();

      fLastRewrite = " Exp";
      return true;
    }

    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Exportation &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "(p"+fParser.renderAnd()+"q)"+fParser.renderImplic()+"r :: p"
        +fParser.renderImplic()+"(q"+fParser.renderImplic()+"r)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Exportation      "+
	        "(p"+fParser.renderAnd()+"q)"+fParser.renderImplic()+"r :: p"
	        +fParser.renderImplic()+"(q"+fParser.renderImplic()+"r)"+
	        "";
	  }
}


/*
 function TRewriteWindow.DoExp (var oldformula, newformula: TFormula): boolean;

   var
    p, q, r, formulanode: TFormula;
    tempboolean: boolean;

  begin
   tempboolean := FALSE;

   if oldformula.finfo = chImplic then
    if oldformula.fllink.finfo = chAnd then
     begin

      p := oldformula.fllink.fllink; {p}
      q := oldformula.fllink.frlink; {q}
      r := oldformula.frlink; {r}

      SupplyFormula(formulanode);
      formulanode.fKind := binary;
      formulanode.finfo := chImplic;
      formulanode.fllink := p.CopyFormula;
      newformula := formulanode;
      formulanode := nil;

      SupplyFormula(formulanode);
      formulanode.fKind := binary;
      formulanode.finfo := chImplic;
      formulanode.fllink := q.CopyFormula;
      formulanode.frlink := r.CopyFormula;
      newformula.frlink := formulanode;
      formulanode := nil;

      oldformula.DismantleFormula;

      oldformula := nil;
      fLastRewrite := ' Exp';
      tempboolean := TRUE;
     end;

   if not tempboolean then {second case}
    if oldformula.finfo = chImplic then
     if oldformula.frlink.finfo = chImplic then
      begin

       p := oldformula.fllink; {p}
       q := oldformula.frlink.fllink; {q}
       r := oldformula.frlink.frlink; {r}

       SupplyFormula(formulanode);
       formulanode.fKind := binary;
       formulanode.finfo := chImplic;
       formulanode.frlink := r.CopyFormula;
       newformula := formulanode;
       formulanode := nil;

       SupplyFormula(formulanode);
       formulanode.fKind := binary;
       formulanode.finfo := chAnd;
       formulanode.fllink := p.CopyFormula;
       formulanode.frlink := q.CopyFormula;
       newformula.fllink := formulanode;
       formulanode := nil;

       oldformula.DismantleFormula;

       oldformula := nil;
       fLastRewrite := ' Exp';
       tempboolean := TRUE;

      end;

   if not tempboolean then
    oldformula.DismantleFormula;

   DoExp := tempboolean;
  end;


*/

class DoImplic extends AbstractRule{


  boolean doRule() {
    TFormula p,q;
    if (fParser.isImplic(fSelectionRoot)) {  // p->q::~pvq
      p=fSelectionRoot.fLLink.copyFormula();
      q=fSelectionRoot.fRLink.copyFormula();

      fNewRoot = new TFormula(TFormula.binary,String.valueOf(chOr),
                               new TFormula(TFormula.unary,String.valueOf(chNeg),
                                            null,
                                            p),
                               q);


      fLastRewrite = " Impl";
      return
          true;
    }


    if (fParser.isOr(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.fLLink)) {  // p->q::~pvq
     p=fSelectionRoot.fLLink.fRLink.copyFormula();
     q=fSelectionRoot.fRLink.copyFormula();

     fNewRoot = new TFormula(TFormula.binary,String.valueOf(chImplic),
                             p,
                            q);


     fLastRewrite = " Impl";
     return
         true;
   }





    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Implication &nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderImplic()+"q :: "+fParser.renderNot()+"q"+fParser.renderOr()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Implication      "+
	        "p"+fParser.renderImplic()+"q :: "+fParser.renderNot()+"q"+fParser.renderOr()+"p"+
	        "";
	  }  
}

/*

 function TRewriteWindow.DoImplic (var oldformula, newformula: TFormula): boolean;

   var
    p, q, formulanode: TFormula;
    tempboolean: boolean;

  begin
   tempboolean := FALSE;

   if oldformula.finfo = chImplic then
    begin

     p := oldformula.fllink; {p}
     q := oldformula.frlink; {q}

     SupplyFormula(formulanode);
     formulanode.fKind := binary;
     formulanode.finfo := chOr;
     formulanode.frlink := q.CopyFormula;

     newformula := formulanode;
     formulanode := nil;

     SupplyFormula(formulanode);
     formulanode.fKind := unary;
     formulanode.finfo := chNeg;
     formulanode.frlink := p.CopyFormula;

     newformula.fllink := formulanode;
     formulanode := nil;

     oldformula.DismantleFormula;

     oldformula := nil;
     fLastRewrite := ' Impl';
     tempboolean := TRUE;
    end;

   if not tempboolean then {second case}
    if oldformula.finfo = chOr then
     if oldformula.fllink.finfo = chNeg then
      begin

       p := oldformula.fllink.frlink; {p}
       q := oldformula.frlink; {q}

       SupplyFormula(formulanode);
       formulanode.fKind := binary;
       formulanode.finfo := chImplic;
       formulanode.fllink := p.CopyFormula;
       formulanode.frlink := q.CopyFormula;

       newformula := formulanode;
       formulanode := nil;

       oldformula.DismantleFormula;

       oldformula := nil;
       fLastRewrite := ' Impl';
       tempboolean := TRUE;

      end;

   if not tempboolean then
    oldformula.DismantleFormula;

   DoImplic := TRUE;
 end;

*/
/*

 class QuantTest {  //{take care here for this might be Ex! for the unique quantifier}
      boolean testIt(TFormula quantRoot) {
        skip(1); // the opening bracktet, now looking at quantifier

        if ((fCurrCh == chExiquant) && (fLookAheadCh == chUnique)) // {unique case}
          skip(1); // {now looking at exclamation mark}

        TFormula newnode = new TFormula(quantifier, String.valueOf(fCurrCh), null, null);
        TFormula variableNode = new TFormula(variable,
                                             String.valueOf(fLookAheadCh), null, null);
        skip(1); //now looking at variable

        newnode.fLLink = variableNode;

        if (!(isVariable(fCurrCh)) || (fLookAheadCh != ')')) {
          writeError(gCR + "( *Either '" + fCurrCh +
                     "' should be a variable or a ) is missing. *)");
          return
              ILLFORMED;
        }
        else {
          skip(2); // the variable and the )

          TFormula rLink = new TFormula();

          if (secondary(rLink)) {       //the scope of the quantified expression
            newnode.fRLink = rLink;
            quantRoot.assignFieldsToMe(newnode);
            newnode = null;
            return
                WELLFORMED;
          }
          else
            return
                ILLFORMED;
       }

*/



class DoQNExi extends AbstractRule{


  boolean doRule() {
    TFormula p,m;
    if (fParser.isNegation(fSelectionRoot)&&
        fParser.isExiquant(fSelectionRoot.fRLink)) {  // ~(Ex m)p)::(Allx m)~p)
           p=fSelectionRoot.fRLink.fRLink.copyFormula();
           m=fSelectionRoot.fRLink.quantVarForm().copyFormula();

      fNewRoot = new TFormula(TFormula.quantifier,String.valueOf(chUniquant),
                               m,
                               new TFormula(TFormula.unary,String.valueOf(chNeg),
                                            null,
                                             p)
                                );


      fLastRewrite = " QN";
      return
          true;
    }


    if (fParser.isUniquant(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.fRLink)) {  // ~(Ex m)p)::(Allx m)~p)
           p=fSelectionRoot.scope().fRLink.copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();

           fNewRoot = new TFormula(TFormula.unary,String.valueOf(chNeg),
                                    null,
                                    new TFormula(TFormula.quantifier,String.valueOf(chExiquant),
                                                  m,
                                                  p)
                                    );

      fLastRewrite = " QN";
      return
          true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Quant Neg &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+
        fParser.renderNot()+"("+fParser.renderExiquant()+"m)p :: "+"("+fParser.renderUniquant()+"m)"+fParser.renderNot()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Quant Neg       "+

	        fParser.renderNot()+"("+fParser.renderExiquant()+"m)p :: "+"("+fParser.renderUniquant()+"m)"+fParser.renderNot()+"p"+

	        "";
	  }
}

/*
 function TRewriteWindow.DoQNExi (var oldformula, newformula: TFormula): boolean;

   var
    p, formulanode, variForm: TFormula;
    tempboolean: boolean;

  begin
   tempboolean := FALSE;

   if oldformula.finfo = chNeg then
    if oldformula.frlink.finfo[1] = chExiquant then
     begin
      p := oldformula.frlink.frlink; {p}
      variForm := oldformula.frlink.QuantVarForm.CopyFormula;

      SupplyFormula(formulanode);
      formulanode.fKind := quantifier;
      formulanode.finfo := chUniquant;
      formulanode.fLLink := variForm;

      newformula := formulanode;
      formulanode := nil;

      SupplyFormula(formulanode);
      formulanode.fKind := unary;
      formulanode.finfo := chNeg;
      formulanode.frlink := p.CopyFormula;
      newformula.frlink := formulanode;
      formulanode := nil;

      oldformula.DismantleFormula;

      oldformula := nil;
      fLastRewrite := ' QN';
      tempboolean := TRUE;
     end;

   if not tempboolean then {second case}
    if oldformula.finfo[1] = chUniquant then
     if oldformula.frlink.finfo = chNeg then
      begin

       p := oldformula.frlink.frlink; {p}
       variForm := oldformula.QuantVarForm.CopyFormula;

       SupplyFormula(formulanode);
       formulanode.fKind := unary;
       formulanode.finfo := chNeg;

       newformula := formulanode;
       formulanode := nil;

       SupplyFormula(formulanode);
       formulanode.fKind := quantifier;
       formulanode.finfo := chExiquant;
       formulanode.fllink := variForm;
       formulanode.frlink := p.CopyFormula;
       newformula.frlink := formulanode;
       formulanode := nil;

       oldformula.DismantleFormula;

       oldformula := nil;
       fLastRewrite := ' QN';
       tempboolean := TRUE;
      end;

   if not tempboolean then
    oldformula.DismantleFormula;

   DoQNExi := tempboolean;

 end;

*/

class DoQNUni extends AbstractRule{


  boolean doRule() {
    TFormula p,m;
    if (fParser.isNegation(fSelectionRoot)&&
        fParser.isUniquant(fSelectionRoot.fRLink)) {  // ~(All x m)p)::(Ex m)~p)
           p=fSelectionRoot.fRLink.fRLink.copyFormula();
           m=fSelectionRoot.fRLink.quantVarForm().copyFormula();

      fNewRoot = new TFormula(TFormula.quantifier,String.valueOf(chExiquant),
                               m,
                               new TFormula(TFormula.unary,String.valueOf(chNeg),
                                            null,
                                             p)
                                );


      fLastRewrite = " QN";
      return
          true;
    }


    if (fParser.isExiquant(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.fRLink)) {  // ~(All x m)p)::(Ex m)~p)
           p=fSelectionRoot.scope().fRLink.copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();

           fNewRoot = new TFormula(TFormula.unary,String.valueOf(chNeg),
                                    null,
                                    new TFormula(TFormula.quantifier,String.valueOf(chUniquant),
                                                  m,
                                                  p)
                                    );

      fLastRewrite = " QN";
      return
          true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Quant Neg &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+
        fParser.renderNot()+"("+fParser.renderUniquant()+"m)p :: "+"("+fParser.renderExiquant()+"m)"+fParser.renderNot()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Quant Neg       </em>"+
	        fParser.renderNot()+"("+fParser.renderUniquant()+"m)p :: "+"("+fParser.renderExiquant()+"m)"+fParser.renderNot()+"p"+

	        "";
	  } 
}

class DoSubset extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,z,zMemberp,zMemberq;
	    if (fParser.isSubset(fSelectionRoot)) {  // psubsetq::(Allz)(z member p -> z member q)
	      p=fSelectionRoot.firstTerm().copyFormula();
	      q=fSelectionRoot.secondTerm().copyFormula();
	      
	      //we don't want to capture anything here
	      
	      Set <String> badVar=fParser.variablesInFormula(fSelectionRoot);
	      String goodVar=fParser.nthNewVariable(1,badVar);
	      
	      if (!goodVar.equals("")){
	      
	      z= new TFormula(TFormula.variable,String.valueOf(goodVar),
         		 null,
        		 null);
	      
	      zMemberp= new TFormula(TFormula.predicator,strMemberOf,
	         		 null,
	        		 null);
	      zMemberp.appendToFormulaList(z.copyFormula());
	      zMemberp.appendToFormulaList(p.copyFormula());
	      
	      zMemberq= new TFormula(TFormula.predicator,strMemberOf,
	         		 null,
	        		 null);
	      zMemberq.appendToFormulaList(z.copyFormula());
	      zMemberq.appendToFormulaList(q.copyFormula());
	      

	      fNewRoot = new TFormula(TFormula.quantifier,String.valueOf(chUniquant),
	    		                 z,
	    		                 new TFormula(TFormula.binary,String.valueOf(chImplic),
	    		                		 zMemberp,
	    		                		 zMemberq)
                   );


	      fLastRewrite = " Subset";
	      return
	          true;
    
	    }
	    }


	    if (fParser.isUniquant(fSelectionRoot)&&
	        fParser.isImplic(fSelectionRoot.scope())&&
	        fParser.isMemberOf(fSelectionRoot.scope().fLLink)&&
	        fParser.isMemberOf(fSelectionRoot.scope().fRLink)) {  // psubsetq::(Allz)(z member p -> z member q)
	     
	    	TFormula z1,z2,z3,scope,left,right;
	    	
	    	scope=fSelectionRoot.scope();
	    	left=scope.fLLink;
	    	right=scope.fRLink;
	    	
	    	z1= fSelectionRoot.quantVarForm();
	    	z2= left.firstTerm();
	    	z3= right.firstTerm();
	    	
	    	if (z1.equalFormulas(z1, z2)&&
	    		z2.equalFormulas(z2, z3)	
	    	){
	    	    TFormula pSubsetq;
	    		p=left.secondTerm();
	    		q=right.secondTerm();
	    		
	    		pSubsetq= new TFormula(TFormula.predicator,strSubsetOf,
		         		 null,
		        		 null);
	    		pSubsetq.appendToFormulaList(p.copyFormula());
	    		pSubsetq.appendToFormulaList(q.copyFormula());
	    

	     fNewRoot = pSubsetq;


	     fLastRewrite = " Subset";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {// psubsetq::(Allz)(z member p -> z member q)
	    return
	        "<html>"+
	        "<em>Subset &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "p"+fParser.renderSubset()+"q :: "+
	        "("+fParser.renderUniquant()+"m)((m"+fParser.renderMemberOf()+"p)"+ fParser.renderImplic()+"(m"+fParser.renderMemberOf()+"q))"+
	        "</strong>"+
	        "</html>";
	  }
	  
	  public String toString() {// psubsetq::(Allz)(z member p -> z member q)
		    return
		        "Subset             "+

		        "p"+fParser.renderSubset()+"q :: "+
		        "("+fParser.renderUniquant()+"m)((m"+fParser.renderMemberOf()+"p)"+ fParser.renderImplic()+"(m"+fParser.renderMemberOf()+"q))"+

		        "";
		  }
	}

class DoPowerSet extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q;
	    if (fParser.isMemberOf(fSelectionRoot)&&
	    	fParser.isPowerSet(fSelectionRoot.secondTerm())) {  // p MemberOf Power q:: p subset q
	      p=fSelectionRoot.firstTerm().copyFormula();
	      q=fSelectionRoot.secondTerm().firstTerm().copyFormula();
	      
  	    TFormula pSubsetq;
		pSubsetq= new TFormula(TFormula.predicator,strSubsetOf,
         		 null,
        		 null);
		pSubsetq.appendToFormulaList(p.copyFormula());
		pSubsetq.appendToFormulaList(q.copyFormula());

	      fNewRoot = pSubsetq;
	      fLastRewrite = " PowerSet";
	      return
	          true;
	    }



	    if (fParser.isSubset(fSelectionRoot)) { // p MemberOf Power q:: p subset q
	          p=fSelectionRoot.firstTerm().copyFormula();
		      q=fSelectionRoot.secondTerm().copyFormula();
	     
	    	TFormula powerSetq,pMemberPowerq;
	    	
	    	powerSetq=new TFormula(TFormula.functor,strPowerSet,
	         		 null,
	        		 null);
	    	powerSetq.appendToFormulaList(q.copyFormula());

	    		
	    	pMemberPowerq= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
	    	pMemberPowerq.appendToFormulaList(p.copyFormula());
	    	pMemberPowerq.appendToFormulaList(powerSetq.copyFormula());
	    

	     fNewRoot = pMemberPowerq;


	     fLastRewrite = " PowerSet";
	     return
	         true;
	   }


	    return
	        false;
	  }

	  public String toHTMLString() {//p MemberOf Power q:: p subset q
	    return
	        "<html>"+
	        "<em>PowerSet &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "p"+fParser.renderMemberOf()+fParser.renderPowerSet()+"(q) :: "+
	        "p"+ fParser.renderSubset()+"q"+
	        "</strong>"+
	        "</html>";
	  }

	  public String toString() {//p MemberOf Power q:: p subset q
		    return
		        "PowerSet          "+

		        "p"+fParser.renderMemberOf()+fParser.renderPowerSet()+"(q) :: "+
		        "p"+ fParser.renderSubset()+"q"+

		        "";
		  }
	  
	}

class DoTautOrLR extends AbstractRule{  //p :- pVp


  boolean doRule() {
    TFormula p;
      p = fSelectionRoot.copyFormula(); //{p}           (

      fNewRoot = new TFormula(TFormula.binary,
                              String.valueOf(chOr),
                              p,
                              p.copyFormula()
                                     );

      fLastRewrite = " Taut";
      return true;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Tautology &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+ " :- " + "p"+ fParser.renderOr()+"p"+
        "</strong>"+
        "</html>";
  }

  public String toString() {
	    return
	        "Tautology        "+

	        "p"+ " :- " + "p"+ fParser.renderOr()+"p"+

	        "";
	  }
  
}

class DoTautOrRL extends AbstractRule{  //pVp :- p


  boolean doRule() {
    TFormula p;

    if (fParser.isOr(fSelectionRoot)&&
        fSelectionRoot.fLLink.equalFormulas(
          fSelectionRoot.fLLink,
          fSelectionRoot.fRLink)){

      p = fSelectionRoot.fLLink.copyFormula(); //{p}           (

      fNewRoot = p;

      fLastRewrite = " Taut";
      return true;
    }
    else
      return
          false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Tautology &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+ fParser.renderOr()+"p"+ " :- "+ "p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Tautology        "+
	        "p"+ fParser.renderOr()+"p"+ " :- "+ "p"+
	        "";
	  }  
}

class DoTautAndLR extends AbstractRule{  //p :- p.p


  boolean doRule() {
    TFormula p;
      p = fSelectionRoot.copyFormula(); //{p}           (

      fNewRoot = new TFormula(TFormula.binary,
                              String.valueOf(chAnd),
                              p,
                              p.copyFormula()
                                     );

      fLastRewrite = " Taut";
      return true;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Tautology &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+ " :- " + "p"+ fParser.renderAnd()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Tautology        "+

	        "p"+ " :- " + "p"+ fParser.renderAnd()+"p"+

	        "";
	  }  
}

class DoTautAndRL extends AbstractRule{  //p.p :- p


  boolean doRule() {
    TFormula p;

    if (fParser.isAnd(fSelectionRoot)&&
        fSelectionRoot.fLLink.equalFormulas(
          fSelectionRoot.fLLink,
          fSelectionRoot.fRLink)){

      p = fSelectionRoot.fLLink.copyFormula(); //{p}           (

      fNewRoot = p;

      fLastRewrite = " Taut";
      return true;
    }
    else
      return
          false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Tautology &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
        "<strong>"+
        "p"+ fParser.renderAnd()+"p"+ " :- "+ "p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Tautology        "+
	        "p"+ fParser.renderAnd()+"p"+ " :- "+ "p"+
	        "";
	  } 
}

class DoTransLR extends AbstractRule{  //p->q :: ~q->~p


  boolean doRule() {
    TFormula p,q;
    if (fParser.isImplic(fSelectionRoot)) {

      p = fSelectionRoot.fLLink.copyFormula(); //{p}           (
      q  = fSelectionRoot.fRLink.copyFormula();// {q}


      fNewRoot = new TFormula(TFormula.binary,String.valueOf(chImplic),
                              new TFormula(TFormula.unary,String.valueOf(chNeg),
                                            null,
                                            q),
                              new TFormula(TFormula.unary,String.valueOf(chNeg),
                                                   null,
                                                   p)
                                     );



      fLastRewrite = " Trans";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Transposition &nbsp; </em>"+
        "<strong>"+
        "p"+fParser.renderImplic()+"q"+" :- "+fParser.renderNot()+"q"+fParser.renderImplic()+fParser.renderNot()+"p"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Transposition   "+

	        "p"+fParser.renderImplic()+"q"+" :- "+fParser.renderNot()+"q"+fParser.renderImplic()+fParser.renderNot()+"p"+

	        "";
	  }  
}

/*

 function TRewriteWindow.DoTransLR (var oldformula, newformula: TFormula): boolean;

  var
   p, q, formulanode: TFormula;
   tempboolean: boolean;

 begin
  tempboolean := FALSE;

  if oldformula.finfo = chImplic then
   begin

    p := oldformula.fllink; {p}
    q := oldformula.frlink; {q}

    SupplyFormula(formulanode);
    formulanode.fKind := binary;
    formulanode.finfo := chImplic;

    newformula := formulanode;
    formulanode := nil;

    SupplyFormula(formulanode);
    formulanode.fKind := unary;
    formulanode.finfo := chNeg;
    formulanode.frlink := q.CopyFormula;

    newformula.fllink := formulanode;
    formulanode := nil;

    SupplyFormula(formulanode);
    formulanode.fKind := unary;
    formulanode.finfo := chNeg;
    formulanode.frlink := p.CopyFormula;

    newformula.frlink := formulanode;
    formulanode := nil;

    oldformula.DismantleFormula;

    oldformula := nil;
    fLastRewrite := ' Trans';
    tempboolean := TRUE;
   end;

  if not tempboolean then
   oldformula.DismantleFormula;

  DoTransLR := tempboolean;

 end;

*/

class DoTransRL extends AbstractRule{  // ~q->~p:: p->q


  boolean doRule() {
    TFormula p,q;
    if (fParser.isImplic(fSelectionRoot)&&
        fParser.isNegation(fSelectionRoot.fLLink)&&
        fParser.isNegation(fSelectionRoot.fRLink)) {

      q = fSelectionRoot.fLLink.fRLink.copyFormula(); //{p}           (
      p  = fSelectionRoot.fRLink.fRLink.copyFormula();// {q}


      fNewRoot = new TFormula(TFormula.binary,String.valueOf(chImplic),
                              p,
                              q);



      fLastRewrite = " Trans";
      return true;
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Transposition &nbsp; </em>"+
        "<strong>"+
        fParser.renderNot()+"q"+fParser.renderImplic()+fParser.renderNot()+"p :- p"+fParser.renderImplic()+"q"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Transposition  "+

	        fParser.renderNot()+"q"+fParser.renderImplic()+fParser.renderNot()+"p :- p"+fParser.renderImplic()+"q"+

	        "";
	  }
}

class DoTypeExi extends AbstractRule{


  boolean doRule() {
    TFormula p,m,Tm;
    char type;
    if (fParser.isExiquant(fSelectionRoot)&&
        fParser.isAnd(fSelectionRoot.scope())&&
        fParser.isMonadicPredicateWithVar(fSelectionRoot.scope().getLLink())) { // (Ex m:t)p :: (Ex m)(Tm and p)
       /*    p=fSelectionRoot.scope().fRLink.copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();
           Tm=fSelectionRoot.scope().getLLink().copyFormula();

           if (m.equalFormulas(m,Tm.firstTerm())){


             TFormula typeNode = new TFormula(TFormula.functor, Tm.fInfo.toLowerCase(), null, null);

             TFormula head = new TFormula(TFormula.kons,
                          "",
                          m,
                          null);


             head.appendToFormulaList(typeNode);



             fNewRoot = new TFormula(TParser.typedQuantifier,
                                     String.valueOf(chExiquant),
                                     head,
                                     p
                                );

             fLastRewrite = " Type";
             return
                 true;  */

             fNewRoot=fParser.contractTypeExi(fSelectionRoot);
  if (fNewRoot!=null){
    fLastRewrite = " Type";
    return
        true;
          }
    }


    if (fParser.isTypedExiquant(fSelectionRoot)) {  /// (Ex m:t)p :: (Ex m)(Tm and p)

      fNewRoot=fParser.expandTypeExi(fSelectionRoot);
      if (fNewRoot!=null){
        fLastRewrite = " Type";
        return
            true;
      }



        /*     p=fSelectionRoot.scope().copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();
           type=fSelectionRoot.quantType();

           if (type!=chBlank){

             Tm = new TFormula(TFormula.predicator,
                              String.valueOf(type).toUpperCase(),
                              null,
                              null);
             Tm.appendToFormulaList(m.copyFormula());      //Tm


             fNewRoot = new TFormula(TFormula.quantifier,
                                     String.valueOf(chExiquant),
                                     m,
                                     new TFormula(TFormula.binary,
                                                  String.valueOf(chAnd),
                                                  Tm,
                                                  p)
                                     );

             fLastRewrite = " Type";
             return
                 true;
           } */
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Type Exi &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+
        "("+fParser.renderExiquant()+"m:t)p :: "+
        "("+fParser.renderExiquant()+"m)(Tm"+fParser.renderAnd()+"p)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Type Exi         "+

	        "("+fParser.renderExiquant()+"m:t)p :: "+
	        "("+fParser.renderExiquant()+"m)(Tm"+fParser.renderAnd()+"p)"+

	        "";
	  }
}

class DoTypeUni extends AbstractRule{


  boolean doRule() {
    TFormula p,m,Tm;
    char type;

    if (fParser.isUniquant(fSelectionRoot)&&
        fParser.isImplic(fSelectionRoot.scope())&&
        fParser.isMonadicPredicateWithVar(fSelectionRoot.scope().getLLink())) { // (All m:t)p :: (All m)(Tm implic p)
/*           p=fSelectionRoot.scope().fRLink.copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();
           Tm=fSelectionRoot.scope().getLLink().copyFormula();

           if (m.equalFormulas(m,Tm.firstTerm())){


             TFormula typeNode = new TFormula(TFormula.functor, Tm.fInfo.toLowerCase(), null, null);

             TFormula head = new TFormula(TFormula.kons,
                          "",
                          m,
                          null);


             head.appendToFormulaList(typeNode);



             fNewRoot = new TFormula(TParser.typedQuantifier,
                                     String.valueOf(chUniquant),
                                     head,
                                     p
                                ); */


    fNewRoot=fParser.contractTypeUni(fSelectionRoot);
      if (fNewRoot!=null){fLastRewrite = " Type";
             return
                 true;
           }
    }


    if (fParser.isTypedUniquant(fSelectionRoot)) {  // (All m:t)p :: (All m)(Tm implic p)


      fNewRoot=fParser.expandTypeUni(fSelectionRoot);
      if (fNewRoot!=null){
        fLastRewrite = " Type";
             return
                 true;
      }


  /*    p=fSelectionRoot.scope().copyFormula();
           m=fSelectionRoot.quantVarForm().copyFormula();
           type=fSelectionRoot.quantType();

           if (type!=chBlank){

             Tm = new TFormula(TFormula.predicator,
                              String.valueOf(type).toUpperCase(),
                              null,
                              null);
             Tm.appendToFormulaList(m.copyFormula());      //Tm


             fNewRoot = new TFormula(TFormula.quantifier,
                                     String.valueOf(chUniquant),
                                     m,
                                     new TFormula(TFormula.binary,
                                                  String.valueOf(chImplic),
                                                  Tm,
                                                  p)
                                     );

             fLastRewrite = " Type";
             return
                 true;
           }  */
    }
    return
        false;
  }

  public String toHTMLString() {
    return
        "<html>"+
        "<em>Type Uni &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
        "<strong>"+
        "("+fParser.renderUniquant()+"m:t)p :: "+
        "("+fParser.renderUniquant()+"m)(Tm"+fParser.renderImplic()+"p)"+
        "</strong>"+
        "</html>";
  }
  
  public String toString() {
	    return
	        "Type Uni         "+

	        "("+fParser.renderUniquant()+"m:t)p :: "+
	        "("+fParser.renderUniquant()+"m)(Tm"+fParser.renderImplic()+"p)"+

	        "";
	  }  
}

class DoComplement extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,r,pMemberq,pMemberr,pNotMemberr;
	    
	    if (fParser.isMemberOf(fSelectionRoot)&& 
	    	fParser.isComplement(fSelectionRoot.secondTerm())
	    		) {  // p member (q  - r)::p member q and p notMember r)
		      p=fSelectionRoot.firstTerm().copyFormula();
		      q=fSelectionRoot.secondTerm().firstTerm().copyFormula();
		      r=fSelectionRoot.secondTerm().secondTerm().copyFormula();
		      
		      pMemberq= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberq.appendToFormulaList(p.copyFormula());
		      pMemberq.appendToFormulaList(q.copyFormula());
		      
		      pMemberr= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberr.appendToFormulaList(p.copyFormula());
		      pMemberr.appendToFormulaList(r.copyFormula());
		      
		      pNotMemberr=new TFormula(unary,String.valueOf(chNeg),null,pMemberr);
		      
		      fNewRoot = new TFormula(TFormula.binary,
		    		     String.valueOf(chAnd),
		    		     pMemberq,
		                 pNotMemberr);

	      fLastRewrite = " Complement";
	      return
	          true;

	    }


	    if (fParser.isAnd(fSelectionRoot)&& // p member (q  - r)::p member q and p notMember r)
	        fParser.isMemberOf(fSelectionRoot.fLLink)&&
	        fParser.isNegation(fSelectionRoot.fRLink)&&
	        fParser.isMemberOf(fSelectionRoot.fRLink.fRLink)) {
	     
	    	TFormula p1,p2;
	    	
	    	p1=fSelectionRoot.fLLink.firstTerm();
	    	p2=fSelectionRoot.fRLink.fRLink.firstTerm();
	    	
	    	if (p1.equalFormulas(p1, p2)){
	    	    TFormula qComplementr,pMemberqComplementr;
	    		q=fSelectionRoot.fLLink.secondTerm();
	    		r=fSelectionRoot.fRLink.fRLink.secondTerm();
	    		
	    		qComplementr= new TFormula(TFormula.functor,
	    				 strMinus,
		         		 null,
		        		 null);
	    		qComplementr.appendToFormulaList(q.copyFormula());
	    		qComplementr.appendToFormulaList(r.copyFormula());
	    		
	    		pMemberqComplementr= new TFormula(TFormula.predicator,
	    				 strMemberOf,
		         		 null,
		        		 null);
	    		pMemberqComplementr.appendToFormulaList(p1.copyFormula());
	    		pMemberqComplementr.appendToFormulaList(qComplementr.copyFormula());
	    

	            fNewRoot = pMemberqComplementr;


	            fLastRewrite = " Complement";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {//p member (q  - r)::p member q and p notMember r
	    return
	        "<html>"+
	        "<em>Complement&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "p"+fParser.renderMemberOf()+"(q"+fParser.renderComplement()+"r) :: "+
	        "(p"+fParser.renderMemberOf()+"q)"+ 
	        fParser.renderAnd()+"(p"+fParser.renderNotMemberOf()+"r)"+
	        "</strong>"+
	        "</html>";
	  }
	  
	  public String toString() {//p member (q  - r)::p member q and p notMember r
		    return
		        "Complement    "+

		        "p"+fParser.renderMemberOf()+"(q"+fParser.renderComplement()+"r) :: "+
		        "(p"+fParser.renderMemberOf()+"q)"+ 
		        fParser.renderAnd()+"(p"+fParser.renderNotMemberOf()+"r)"+

		        "";
		  }  
	  
	  
	}

class DoIntersection extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,r,z,zMemberp,zMemberq,pMemberq,pMemberr;
	    
	    if (fParser.isMemberOf(fSelectionRoot)&& 
	    	fParser.isIntersection(fSelectionRoot.secondTerm())
	    		) {  // p member (q  intersection r)::p member q and p member r)
		      p=fSelectionRoot.firstTerm().copyFormula();
		      q=fSelectionRoot.secondTerm().firstTerm().copyFormula();
		      r=fSelectionRoot.secondTerm().secondTerm().copyFormula();
		      
		      pMemberq= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberq.appendToFormulaList(p.copyFormula());
		      pMemberq.appendToFormulaList(q.copyFormula());
		      
		      pMemberr= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberr.appendToFormulaList(p.copyFormula());
		      pMemberr.appendToFormulaList(r.copyFormula());
		      
		      fNewRoot = new TFormula(TFormula.binary,
		    		     String.valueOf(chAnd),
		    		     pMemberq,
		                 pMemberr);

	      fLastRewrite = " Intersection";
	      return
	          true;

	    }


	    if (fParser.isAnd(fSelectionRoot)&& // p member (q  intersect r)::p member q and p member r)
	        fParser.isMemberOf(fSelectionRoot.fLLink)&&
	        fParser.isMemberOf(fSelectionRoot.fRLink)) {
	     
	    	TFormula p1,p2;
	    	
	    	p1=fSelectionRoot.fLLink.firstTerm();
	    	p2=fSelectionRoot.fRLink.firstTerm();
	    	
	    	if (p1.equalFormulas(p1, p2)){
	    	    TFormula qIntersectionr,pMemberqUnionr;
	    		q=fSelectionRoot.fLLink.secondTerm();
	    		r=fSelectionRoot.fRLink.secondTerm();
	    		
	    		qIntersectionr= new TFormula(TFormula.functor,
	    				 strIntersection,
		         		 null,
		        		 null);
	    		qIntersectionr.appendToFormulaList(q.copyFormula());
	    		qIntersectionr.appendToFormulaList(r.copyFormula());
	    		
	    		pMemberqUnionr= new TFormula(TFormula.predicator,
	    				 strMemberOf,
		         		 null,
		        		 null);
	    		pMemberqUnionr.appendToFormulaList(p1.copyFormula());
	    		pMemberqUnionr.appendToFormulaList(qIntersectionr.copyFormula());
	    

	            fNewRoot = pMemberqUnionr;


	            fLastRewrite = " Intersection";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {//p member (q  union r)::p member q and p member r)
	    return
	        "<html>"+
	        "<em>Intersection&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "p"+fParser.renderMemberOf()+"(q"+fParser.renderIntersection()+"r) :: "+
	        "(p"+fParser.renderMemberOf()+"q)"+ 
	        fParser.renderAnd()+"(p"+fParser.renderMemberOf()+"r)"+
	        "</strong>"+
	        "</html>";
	  }
	  public String toString() {//p member (q  union r)::p member q and p member r)
		    return
		        "Intersection     "+

		        "p"+fParser.renderMemberOf()+"(q"+fParser.renderIntersection()+"r) :: "+
		        "(p"+fParser.renderMemberOf()+"q)"+ 
		        fParser.renderAnd()+"(p"+fParser.renderMemberOf()+"r)"+

		        "";
		  }	  
	  
	}



class DoPair extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,r,z,zMemberp,zMemberq,pMemberq,pMemberr;
	    
	    if (fParser.isMemberOf(fSelectionRoot)&& 
	    	fParser.isComprehension(fSelectionRoot.secondTerm())
	    		) {  // p member {q,r}::p = q or p = r)
		      p=fSelectionRoot.firstTerm().copyFormula();
		      q=fSelectionRoot.secondTerm().firstTerm().copyFormula();
		      r=fSelectionRoot.secondTerm().secondTerm().copyFormula();
		      
		      pMemberq= new TFormula(TFormula.equality,strEquals,
		         		 null,
		        		 null);
		      pMemberq.appendToFormulaList(p.copyFormula());
		      pMemberq.appendToFormulaList(q.copyFormula());
		      
		      pMemberr= new TFormula(TFormula.equality,strEquals,
		         		 null,
		        		 null);
		      pMemberr.appendToFormulaList(p.copyFormula());
		      pMemberr.appendToFormulaList(r.copyFormula());
		      
		      fNewRoot = new TFormula(TFormula.binary,
		    		     String.valueOf(chOr),
		    		     pMemberq,
		                 pMemberr);

	      fLastRewrite = " Pair";
	      return
	          true;

	    }


	    if (fParser.isOr(fSelectionRoot)&& // // p member {q,r}::p = q or p = r)
	        fParser.isEquality(fSelectionRoot.fLLink)&&
	        fParser.isEquality(fSelectionRoot.fRLink)) {
	     
	    	TFormula p1,p2;
	    	
	    	p1=fSelectionRoot.fLLink.firstTerm();
	    	p2=fSelectionRoot.fRLink.firstTerm();
	    	
	    	if (p1.equalFormulas(p1, p2)){
	    	    TFormula pMemberComp,qCompr;
	    		q=fSelectionRoot.fLLink.secondTerm();
	    		r=fSelectionRoot.fRLink.secondTerm();
	    		
	    		
	    		qCompr= new TFormula(TFormula.comprehension,
	    				 "",
		         		 null,
		        		 null);
	    		qCompr.appendToFormulaList(q.copyFormula());
	    		qCompr.appendToFormulaList(r.copyFormula());
	    		
	    		pMemberComp= new TFormula(TFormula.predicator,
	    				 strMemberOf,
		         		 null,
		        		 null);
	    		pMemberComp.appendToFormulaList(p1.copyFormula());
	    		pMemberComp.appendToFormulaList(qCompr.copyFormula());
	    

	            fNewRoot = pMemberComp;


	            fLastRewrite = " Pair";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {//p member (q  union r)::p member q and p member r)
	    return
	        "<html>"+
	        "<em>Pair&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
	        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</em>"+
	        "<strong>"+
	        "p"+fParser.renderMemberOf()+"{q,r} :: "+
	        "(p=q)"+ 
	        fParser.renderOr()+"(p=r)"+
	        "</strong>"+
	        "</html>";
	  }
	  public String toString() {//p member (q  union r)::p member q and p member r)
		    return
		        "Pair        "+
		        "          "+

		        "p"+fParser.renderMemberOf()+"{q,r} :: "+
		        "(p=q)"+ 
		        fParser.renderOr()+"(p=r)"+

		        "";
		  }	  
	  
	}

class DoUnion extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,r,z,zMemberp,zMemberq,pMemberq,pMemberr;
	    
	    if (fParser.isMemberOf(fSelectionRoot)&& 
	    	fParser.isUnion(fSelectionRoot.secondTerm())
	    		) {  // p member (q  union r)::p member q or p member r)
		      p=fSelectionRoot.firstTerm().copyFormula();
		      q=fSelectionRoot.secondTerm().firstTerm().copyFormula();
		      r=fSelectionRoot.secondTerm().secondTerm().copyFormula();
		      
		      pMemberq= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberq.appendToFormulaList(p.copyFormula());
		      pMemberq.appendToFormulaList(q.copyFormula());
		      
		      pMemberr= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberr.appendToFormulaList(p.copyFormula());
		      pMemberr.appendToFormulaList(r.copyFormula());
		      
		      fNewRoot = new TFormula(TFormula.binary,
		    		     String.valueOf(chOr),
		    		     pMemberq,
		                 pMemberr);

	      fLastRewrite = " Union";
	      return
	          true;

	    }


	    if (fParser.isOr(fSelectionRoot)&& // p member (q  union r)::p member q or p member r)
	        fParser.isMemberOf(fSelectionRoot.fLLink)&&
	        fParser.isMemberOf(fSelectionRoot.fRLink)) {
	     
	    	TFormula p1,p2;
	    	
	    	p1=fSelectionRoot.fLLink.firstTerm();
	    	p2=fSelectionRoot.fRLink.firstTerm();
	    	
	    	if (p1.equalFormulas(p1, p2)){
	    	    TFormula qUnionr,pMemberqUnionr;
	    		q=fSelectionRoot.fLLink.secondTerm();
	    		r=fSelectionRoot.fRLink.secondTerm();
	    		
	    		qUnionr= new TFormula(TFormula.functor,
	    				 strUnion,
		         		 null,
		        		 null);
	    		qUnionr.appendToFormulaList(q.copyFormula());
	    		qUnionr.appendToFormulaList(r.copyFormula());
	    		
	    		pMemberqUnionr= new TFormula(TFormula.predicator,
	    				 strMemberOf,
		         		 null,
		        		 null);
	    		pMemberqUnionr.appendToFormulaList(p1.copyFormula());
	    		pMemberqUnionr.appendToFormulaList(qUnionr.copyFormula());
	    

	            fNewRoot = pMemberqUnionr;


	            fLastRewrite = " Union";
	     return
	         true;
	   }
	    }

	    return
	        false;
	  }

	  public String toHTMLString() {//p member (q  union r)::p member q or p member r)
	    return
	        "<html>"+
	        "<em>Union&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "p"+fParser.renderMemberOf()+"(q"+fParser.renderUnion()+"r) :: "+
	        "(p"+fParser.renderMemberOf()+"q)"+ 
	        fParser.renderOr()+"(p"+fParser.renderMemberOf()+"r)"+
	        "</strong>"+
	        "</html>";
	  }
	  public String toString() {//p member (q  union r)::p member q or p member r)
		    return
		        "Union              "+

		        "p"+fParser.renderMemberOf()+"(q"+fParser.renderUnion()+"r) :: "+
		        "(p"+fParser.renderMemberOf()+"q)"+ 
		        fParser.renderOr()+"(p"+fParser.renderMemberOf()+"r)"+

		        "";
		  }	  
	  
	}

class DoXProd extends AbstractRule{


	  boolean doRule() {
	    TFormula p,q,r,s,pMemberq,rMembers;
	    
	    if (fParser.isMemberOf(fSelectionRoot)&& 
	    	fParser.isXProd(fSelectionRoot.secondTerm())&&
	    	fParser.isPair(fSelectionRoot.firstTerm())
	    		) {  // <p,q> member (r  X  s)::p member q and r member s)
		      p=fSelectionRoot.firstTerm().firstTerm().copyFormula();
		      q=fSelectionRoot.firstTerm().secondTerm().copyFormula();
		      r=fSelectionRoot.secondTerm().firstTerm().copyFormula();
		      s=fSelectionRoot.secondTerm().secondTerm().copyFormula();
		      
		      pMemberq= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      pMemberq.appendToFormulaList(p.copyFormula());
		      pMemberq.appendToFormulaList(q.copyFormula());
		      
		      rMembers= new TFormula(TFormula.predicator,strMemberOf,
		         		 null,
		        		 null);
		      rMembers.appendToFormulaList(r.copyFormula());
		      rMembers.appendToFormulaList(s.copyFormula());
		      
		      fNewRoot = new TFormula(TFormula.binary,
		    		     String.valueOf(chAnd),
		    		     pMemberq,
		                 rMembers);

	      fLastRewrite = " XProd";
	      return
	          true;

	    }


	    if (fParser.isAnd(fSelectionRoot)&& // <p,q> member (r  X  s)::p member q and r member s)
	        fParser.isMemberOf(fSelectionRoot.fLLink)&&
	        fParser.isMemberOf(fSelectionRoot.fRLink)) {
	    	
	    	p=fSelectionRoot.fLLink.firstTerm();
	    	q=fSelectionRoot.fLLink.secondTerm();
	    	r=fSelectionRoot.fRLink.firstTerm();
	    	s=fSelectionRoot.fRLink.secondTerm();
	    	

	    	    TFormula rXs,pPairq,pqMemberrs;
	    		q=fSelectionRoot.fLLink.secondTerm();
	    		r=fSelectionRoot.fRLink.secondTerm();
	    		
	    		rXs= new TFormula(TFormula.functor,
	    				 strXProd,
		         		 null,
		        		 null);
	    		rXs.appendToFormulaList(r.copyFormula());
	    		rXs.appendToFormulaList(s.copyFormula());
	    		
	    		pPairq= new TFormula(TFormula.pair,
	    				 "",
		         		 null,
		        		 null);
	    		pPairq.appendToFormulaList(p.copyFormula());
	    		pPairq.appendToFormulaList(q.copyFormula());
	    		
	    		pqMemberrs= new TFormula(TFormula.predicator,
	    				 strMemberOf,
		         		 null,
		        		 null);
	    		pqMemberrs.appendToFormulaList(pPairq);
	    		pqMemberrs.appendToFormulaList(rXs);
	    

	            fNewRoot = pqMemberrs;


	            fLastRewrite = " XProd";
	     return
	         true;

	    }

	    return
	        false;
	  }

	  public String toHTMLString() {//p member (q  union r)::p member q and p member r)
	    return
	        "<html>"+
	        "<em>XProd&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </em>"+
	        "<strong>"+
	        "&lt;p,q&gt;"+fParser.renderMemberOf()+"(r"+fParser.renderXProd()+"s) :: "+
	        "(p"+fParser.renderMemberOf()+"q)"+ 
	        fParser.renderAnd()+"(r"+fParser.renderMemberOf()+"s)"+
	        "</strong>"+
	        "</html>";
	  }
	  
	  public String toString() {//p member (q  union r)::p member q and p member r)
		    return
		        "XProd           "+

		        fParser.renderMemberOf()+"(r"+fParser.renderXProd()+"s) :: "+
		        "(p"+fParser.renderMemberOf()+"q)"+ 
		        fParser.renderAnd()+"(r"+fParser.renderMemberOf()+"s)"+

		        "";
		  }	  
	}

/*************************************** End of Rules ********************************/

/* sample code
ListBox getListBox(boolean dropdown)
{
    ListBox widget = new ListBox();
    widget.addStyleName("demo-ListBox");
    widget.addItem("One");
    widget.addItem("Two");
    widget.addItem("Three");
    widget.addItem("Four");
    widget.addItem("Five");
    if(!dropdown)widget.setVisibleItemCount(3);
    return widget;
}
*/
void initializeRulesList(){
//  fRulesList=new ArrayList();
 

 fRulesList.add(new DoAndAssocLR());
 fRulesList.add(new DoAndAssocRL());
 fRulesList.add(new DoOrAssocLR());
 fRulesList.add(new DoOrAssocRL());
 fRulesList.add(new DoCommAnd());
 fRulesList.add(new DoCommOr());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoComplement());
 
 fRulesList.add(new DoDMAnd());
 fRulesList.add(new DoDMOr());
 fRulesList.add(new DoDistribAnd());
 fRulesList.add(new DoDistribOr());
 fRulesList.add(new DoDNLR());
 fRulesList.add(new DoDNRL());
 fRulesList.add(new DoEquiv1());
 fRulesList.add(new DoEquiv2());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoExtension());

 
 fRulesList.add(new DoExp());
 fRulesList.add(new DoImplic());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoIntersection());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoPair());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoPowerSet());
 
 fRulesList.add(new DoQNExi());
 fRulesList.add(new DoQNUni());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoSubset());

 fRulesList.add(new DoTautOrLR());
 fRulesList.add(new DoTautOrRL());
 fRulesList.add(new DoTautAndLR());
 fRulesList.add(new DoTautAndRL());

 fRulesList.add(new DoTransLR());
 fRulesList.add(new DoTransRL());

 fRulesList.add(new DoTypeExi());
 fRulesList.add(new DoTypeUni());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoUnion());
 
 if (TPreferencesData.fSetTheory)
	  fRulesList.add(new DoXProd());

}

AbstractRule getSelectedRule(){
	int index=fRulesListBox.getSelectedIndex();
	
	if (index>-1)
		return
				(AbstractRule)(fRulesList.get(index));
	else
		return
				null;
}

void initializeRulesBox(){
	AbstractRule rule;
	
	for (int i=0;i<fRulesList.size();i++)
	{
		rule=(AbstractRule)(fRulesList.get(i));
		fRulesListBox.addItem(rule.toString());
	}


/*  
  
 
  rules=new JComboBox();
  rules.setMaximumRowCount(26);

 

  rules.addItem(new DoAndAssocLR());
  rules.addItem(new DoAndAssocRL());
  rules.addItem(new DoOrAssocLR());
  rules.addItem(new DoOrAssocRL());
  rules.addItem(new DoCommAnd());
  rules.addItem(new DoCommOr());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoComplement());
  
  rules.addItem(new DoDMAnd());
  rules.addItem(new DoDMOr());
  rules.addItem(new DoDistribAnd());
  rules.addItem(new DoDistribOr());
  rules.addItem(new DoDNLR());
  rules.addItem(new DoDNRL());
  rules.addItem(new DoEquiv1());
  rules.addItem(new DoEquiv2());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoExtension());

  
  rules.addItem(new DoExp());
  rules.addItem(new DoImplic());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoIntersection());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoPair());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoPowerSet());
  
  rules.addItem(new DoQNExi());
  rules.addItem(new DoQNUni());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoSubset());

  rules.addItem(new DoTautOrLR());
  rules.addItem(new DoTautOrRL());
  rules.addItem(new DoTautAndLR());
  rules.addItem(new DoTautAndRL());

  rules.addItem(new DoTransLR());
  rules.addItem(new DoTransRL());

  rules.addItem(new DoTypeExi());
  rules.addItem(new DoTypeUni());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoUnion());
  
  if (TPreferencesData.fSetTheory)
	  rules.addItem(new DoXProd());

*/


}


/*

  if (firstLine!=null){
        TProofline newline = new TProofline(fParser);

        newline.fFormula=firstLine.fFormula.copyFormula();
        newline.fFirstjustno=firstLine.fLineno;
        newline.fJustification= repeatJustification;
        newline.fSubprooflevel= fModel.getHeadLastLine().fSubprooflevel;


        TUndoableProofEdit  newEdit = new TUndoableProofEdit();

        newEdit.fNewLines.add(newline);

         newEdit.doEdit();


    }

*/


/********* needs to go up ***********/

/*
 public class RewriteAction extends AbstractAction{
 // TProofline fFirstLine;
  RewriteAction(String label){
    putValue(NAME, label);
  //  fFirstLine=firstLine;
  }

  public void actionPerformed(ActionEvent ae){
    if ((fNewRoot!=null)&&
        (fSelectionRoot!=null)&&
        !fNewRoot.equalFormulas(fNewRoot,fSelectionRoot)
    )
        {

     // we need to find the entire after formula

     TFormula afterRoot = new TFormula();
            StringReader aReader = new StringReader(fAfterText.getText());
            ArrayList dummy = new ArrayList();

            boolean wellFormed = fParser.wffCheck(afterRoot, dummy, aReader);

          if (wellFormed){ // should alwyas be

            TProofline newline = new TProofline(fParser);

            newline.fFormula = afterRoot.copyFormula();
            newline.fFirstjustno = fSelectedLine.fLineno;
            newline.fJustification = fLastRewrite;
            newline.fSubprooflevel = fModel.getHeadLastLine().fSubprooflevel;

            TUndoableProofEdit newEdit = new TUndoableProofEdit();

            newEdit.fNewLines.add(newline);

            newEdit.doEdit();
            removeInputPane();
          }

   }


  }

}  */

/* UP

   public void doRewrite(){
   JButton defaultButton;

   TProofInputPanel inputPane;

   fSelectionRewrite="";

   fSelectedLine=fProofListView.oneSelected();

   if (fSelectedLine!=null){

     String originalFormulaStr=fParser.writeFormulaToString(fSelectedLine.fFormula);

     fBeforeText.setText(originalFormulaStr);
     fAfterText.setText("");
     fNewRoot=new TFormula();
     fSelectionRoot=new TFormula();

      defaultButton = new JButton(new RewriteAction("Go"));


      JComponent[]components = {fComboBox,  new JButton(new CancelAction()), defaultButton };  // put cancel on left

      inputPane = new TProofInputPanel("Choose rule, select (sub)formula to rewrite, click Go...",
                                       fBeforeText,
                                       "After rewrite, the formula will look like this:",
                                       fAfterText,
                                       components);


           addInputPane(inputPane);

           inputPane.getRootPane().setDefaultButton(defaultButton);
           fInputPane.setVisible(true); // need this
           fBeforeText.requestFocus();         // so selected text shows

    }

}

*/

/*

void rewriteMenuItem_actionPerformed(ActionEvent e) {

doRewrite();

  } */


}
