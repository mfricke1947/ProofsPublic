package us.softoption.proofs;

import us.softoption.parser.*;

import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;
import us.softoption.editor.*;

/*The drawing of this into a proof is done by the ProofListCellRenderer. All
the html and toString() here is for writing the thing out to an html document */


public class TProofline {
 // static int fRightMargin=300;
 /*A global. The lines are drawn as html table rows, we need a right
                         margin parameter. 355 is not the real width as there are some
                         fixed width columns that get added to it. See toString()

It's all a bit awkward. This should reside with the TProofPanel. But the prooflines do not
know which panel they belong to. And it is not easy for the Panel to pass the parameter the other
way because the lines are drawn by overriding toString() which does not have a parameter. It can
be done, but it is awkward with the editing saving many Panels etc. Once the prooflines have
been written to the journal, they become an html table which can be edited by an html
editor*/

  int fLineno=0, fFirstjustno=0, fSecondjustno=0, fThirdjustno=0, fSubprooflevel=0;
  int fHeadlevel=0;  //either 0 or 1;
  TFormula fFormula=null;
  String fJustification="";
  boolean fBlankline=false, fLastassumption=false, fSelectable=true, fSubProofSelectable=false, fDerived=false;

  /*we use fDerived to mark whether lines have been proved automatically (to check the students)*/

 TParser fParser= null;  /* a proofline is drawn relative to a logical system
                                   and so too is a formula, we need a parser to write the
formula, and TProofline will be subclassed for the different logical systems. */


int fRightMargin=TPreferencesData.fRightMargin;//changed from 355 Dec 20 06;

  /*The lines are drawn as html table rows, we need a right
                                                  margin parameter. 355 is not the real width as there are some
                                                  fixed with columns that get added to it. See toString()

It's all a bit awkward. This should reside with the TProofPanel. But the prooflines do not
know which panel they belong to. And it is not easy for the Panel to pass the parameter the other
way because the lines are drawn by overriding toString() which does not have a parameter. It can
be done, but it is awkward with the editing saving many Panels etc. Once the prooflines have
been written to the journal, they become an html table which can be edited by an html
editor. What I am doing at the moment is to  have a default and to set this when lines are added
to the proof (in TProofListModel*/

  static int fFourthColumnWidth = 60;

  int rgb=51;

  String grey="153,153,135";
  String blue="102,255,155";
  String red="251,51,255";
  String black="0,0,0";
  String realRed="251,0,0";
  String realBlue="0,0,255";
 String white="255,255,255";

 String lightSkyBlue2 = "164,211,238";




public TProofline(){         // prefer the other constructor, because this creates a parser for each line
                             // but need this for Beans and serialization
  fParser= new TParser();
}



public TProofline(TParser aParser){
  fParser= aParser;
}


TProofline supplyProofline(){  //overidden by subclasses
  return
     new TProofline(fParser);
}

void copyFieldsTo(TProofline copy){
  copy.fBlankline=fBlankline;
copy.fFirstjustno=fFirstjustno;
copy.fFormula = fFormula;       //not a copy!!
copy.fHeadlevel = fHeadlevel;
copy.fJustification = fJustification; //also not a copy
copy.fLastassumption = fLastassumption;
copy.fLineno = fLineno;
copy.fSecondjustno=fSecondjustno;
copy.fSelectable=fSelectable;
copy.fSubprooflevel=fSubprooflevel;
copy.fSubProofSelectable=fSubProofSelectable;
copy.fThirdjustno=fThirdjustno;
copy.fParser=fParser;
copy.fRightMargin=fRightMargin;


}


TProofline shallowCopy(){

  TProofline copy= supplyProofline();
  copyFieldsTo(copy);

/*
  TProofline copy= new TProofline(fParser);

  copy.fBlankline=fBlankline;
  copy.fFirstjustno=fFirstjustno;
  copy.fFormula = fFormula;       //not a copy!!
  copy.fHeadlevel = fHeadlevel;
  copy.fJustification = fJustification; //also not a copy
  copy.fLastassumption = fLastassumption;
  copy.fLineno = fLineno;
  copy.fSecondjustno=fSecondjustno;
  copy.fSelectable=fSelectable;
  copy.fSubprooflevel=fSubprooflevel;
  copy.fSubProofSelectable=fSubProofSelectable;
  copy.fThirdjustno=fThirdjustno;
  copy.fParser=fParser;
  copy.fRightMargin=fRightMargin;


*/

  return
      copy;
  }






public boolean getBlankline(){
  return
      fBlankline;
}

public void setBlankline(boolean isBlankline){
  fBlankline=isBlankline;
}

public boolean getDerived(){
  return
      fDerived;
}

public void setDerived(boolean isDerived){
  fDerived=isDerived;
}



  public TFormula getFormula(){
    return
        fFormula;
  }

  public String getJustification(){
     return
         fJustification;
  }

  public void setJustification(String justification){
     fJustification=justification;
  }

  public int getHeadlevel(){
    return
        fHeadlevel;
  }

  public void setHeadlevel(int level){
    fHeadlevel=level;
  }



  public int getLineNo(){
    return
      fLineno;
  }

  public TParser getParser(){
    return
        fParser;
  }

  public int getFirstjustno(){
      return
        fFirstjustno;
    }

  public int getSecondjustno(){
        return
          fSecondjustno;
      }

  public int getThirdjustno(){
          return
            fThirdjustno;
        }
public int getRightMargin(){
  return
      fRightMargin;
}

public void setRightMargin(int aMargin){
  fRightMargin = aMargin;
}

public int getSubprooflevel(){
                return
                  fSubprooflevel;
              }

public void setSubprooflevel(int subprooflevel){

   fSubprooflevel=subprooflevel;
                           }

/*
public boolean getSelectable(){    //NO NEED TO SAVE THIS TO FILE (BEANS)
  return
      fSelectable;
} */

public void setSelectable(boolean isSelectable){
      fSelectable=isSelectable;
}

/*    public boolean getSubProofSelectable(){ //NO NEED TO SAVE THIS TO FILE (BEANS)
      return
          fSubProofSelectable;
    } */

    public void setSubProofSelectable(boolean isSelectable){
          fSubProofSelectable=isSelectable;
    }



public void setLineNo(int num){
  fLineno=num;
}

public void setParser(TParser aParser){
  fParser=aParser;
  }

public void setFirstjustno(int num){
  fFirstjustno=num;
}



public void setSecondjustno(int num){
  fSecondjustno=num;
}

public void setLastAssumption(boolean lastAssumption){
  fLastassumption=lastAssumption;
}


public boolean getLastAssumption(){
  return
      fLastassumption;
}



public void setThirdjustno(int num){
  fThirdjustno=num;
}

public void setFormula(TFormula aFormula){
  fFormula=aFormula;
}

/************************** Working with HTML ********************************/

static String pngVertical="<img "
	    +"src=\"images/vertical.png\" style=\"height:120%;\">";

/* NOTE This assumes that the image is in the right place! */

/*
Lines looks like

1 | F^G                    Ass
2 || H                     Ass
3 || F                     1 ^E

now, a line on its own is fine, we can use one column for the line number, then one each for 
the vertical lines, the formula, and the justification.

Conceptually, the line numbers are col 1, the vertical lines are subcolumns of col 2, the formula
is col 3, and the justification col 4.

But, if we wanted to combine several lines into a table we have the problem that the different rows
might have a different number of columns and hence colspan= is needed.

If this method is called with a (max) numberOfColumns, then we can insert a colspan if needed. The colspan
needs to go with the formula (ie with F^G in line 1), we are looking for left justification.

Now, some proofs start with a headlevel of -1, others with a headlevel of 0 (that information is
on the line itself).

Then the parameter tells us the maximum nesting. So the number of columns in the table as a whole is
4 + maxSubProofLevel.

So, any particular line has to look at the difference between its level and the headlevel. It that is
maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 2 is needed

ie colspan = maxSubProoflevel - difference +1 (and colspan has to be greater than 1 to matter)


*/

//int colspan=maxSubProofLevel- (fSubprooflevel-fHeadlevel) +1;

/*May 05, I am now putting in two vertical lines for each subprooflevel-- a white spacer column
then the real thing

So the number of columns in the table as a whole is
4 + 2*maxSubProofLevel.

So, any particular line has to look at the difference between its level and the headlevel. It that is
maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 3 is needed

ie colspan = 2(maxSubProoflevel - difference) +1 (and colspan has to be greater than 1 to matter)*/

String colSpanStr(int maxSubProofLevel){
	
int colspan=2*(maxSubProofLevel- (fSubprooflevel-fHeadlevel)) +1;
String colStr="";

if (colspan>1)
      colStr= " colspan="+colspan+" ";
  return
     colStr;
}







/************************** Strings for GWT Table Rendering ********************************/

String vertLineOpen="<div style=\"display:inline;border-left:medium solid black;width:6px;height:30px;\">";
String vertLineClose="</div>";

String vertLineDiv="<div style=\"display:inline;border-left:medium solid black;width:6px;height:30px;\">&nbsp;</div>";
String vertLineDivRed="<div style=\"display:inline;border-left:medium solid red;width:6px;height:30px;\">&nbsp;</div>";
String vertLineDivBlue="<div style=\"display:inline;border-left:medium solid blue;width:6px;height:30px;\">&nbsp;</div>";

String vertLineDivOpen="<div style=\"display:inline;border-left:medium solid black;width:6px;height:30px;\">";
String vertLineDivRedOpen="<div style=\"display:inline;border-left:medium solid red;width:6px;height:30px;\">";
String vertLineDivBlueOpen="<div style=\"display:inline;border-left:medium solid blue;width:6px;height:30px;\">";

String vertHorizLineDivOpen="<div style=\"display:inline;border-left:medium solid black;border-bottom:medium solid black;width:6px;height:30px;\">";
String vertHorizLineDivRedOpen="<div style=\"display:inline;border-left:medium solid red;border-bottom:medium solid red;width:6px;height:30px;\">";
String vertHorizLineDivBlueOpen="<div style=\"display:inline;border-left:medium solid blue;border-bottom:medium solid blue;width:6px;height:30px;\">";


String vertLine2="<span style=\"border-left:thick solid #ff0000;width:6px;\"> &nbsp;</span> ";


public String firstColumnGWT(){

   if (fBlankline)
   return
       "";  // blanklines draw only the subprooflevels &nbsp;
      //"<td style= \"width: 15px;\">" + strNull + "</td>";
else
   return
      fLineno + "";  // the line number

}


public String secondColumnGWT(){

	  String spacer= ""; //"<br>";             //empty columns have trouble with re-sizing

	  String secondColumn="";           // we make many html columns, one for each subprooflevel

	 for (int i=-1;i<fSubprooflevel;i++){

		 secondColumn=secondColumn+ vertLine2;//vertLineOpen+vertLineClose;
	//   secondColumn=secondColumn+ chBoxVertLine;    //May05, white spacer (and you have to have some content &nbsp;

	/* seem to require <br> to avoid widening. YET when I put the <br> in the
	   Mac draws it too tall in the Journal ie it puts the break in ! */

	 }



	return
	     secondColumn;
	}

public static final char chHeavyDownLine = '\u257B';


/*If this is an ordinary line we want to put a series of vertical lines, of different
 * colors, and then the formula enclosed in the div.
 * If this is a last assumption we want also to put a line underneath it which we do with a bottom border
 */

/* old version Feb 20133

public String secondAndThirdColumnGWT(int maxSubProofLevel/*String colSpan){
	  int secondColumnWidth = numVertLines()*6;
	  
	  
	  String secondColumn=""; 
	 for (int i=-1;i<fSubprooflevel;i++){
	
		 //secondColumn=secondColumn+ vertLineOpen+vertLineClose; //vertLine2;		 
		 //secondColumn=secondColumn+chHeavyDownLine;		 
		 //secondColumn=secondColumn+pngVertical;
		 
		   int index = ((i+1)%3);

		   if (index==2){
		     secondColumn=secondColumn+vertLineDivBlue;
		   } else
		   if (index==1){
			   secondColumn=secondColumn+vertLineDivRed;
		   }  
		   else		 
		   secondColumn=secondColumn+vertLineDiv;  //black
	 } 

	  String thirdContent = "<br>";
	int thirdColumnWidth = fRightMargin-secondColumnWidth;
	int maxChars=thirdColumnWidth/5;

	if ((!fBlankline)&&(fFormula!=null))
	  thirdContent=fParser.writeFormulaToString(fFormula,maxChars);


	 return
	 /*    "<td style= \"width: "
	     + thirdColumnWidth+"px;\""+
	     colSpan + ">" + */
			 
//		secondColumn+	 
			 
//	     (fLastassumption ?"<u>":"")+
//	     thirdContent+ (fLastassumption ?"</u>":""); /* + 
//	     "</td>"; */
//	}


/* new version Feb 20133  */

public String secondAndThirdColumnGWT(int maxSubProofLevel){
//first we'll get the formula, thirdContent
	
	int secondColumnWidth = numVertLines()*6;
	  String thirdContent = "<br />";
	int thirdColumnWidth = fRightMargin-secondColumnWidth;
	int maxChars= 1000;  // change Feb2013thirdColumnWidth/5;

	if ((!fBlankline)&&(fFormula!=null))
	  thirdContent="&nbsp;"+fParser.writeFormulaToString(fFormula,maxChars);

//then we'll sort out the vertical lines and underline before it in the second column
// we do something different for the last vert line i.e. wrap formula. and
	// also that is different if it is the last assumption
	  
	  String secondColumn=""; 
	 for (int i=-1;i<(fSubprooflevel-1);i++){
			 
		   int index = ((i+1)%3);

		   if (index==2){
		     secondColumn=secondColumn+vertLineDivBlue;
		   } else
		   if (index==1){
			   secondColumn=secondColumn+vertLineDivRed;
		   }  
		   else		 
		   secondColumn=secondColumn+vertLineDiv;  //black
	 } 
// now we come to the last vertical line
	 
	 if (-1<fSubprooflevel){
		 int index = ((fSubprooflevel)%3);
		 
		 if (fLastassumption){
			 if (index==2){
			     secondColumn=secondColumn+vertHorizLineDivBlueOpen;
			   } else
			   if (index==1){
				   secondColumn=secondColumn+vertHorizLineDivRedOpen;
			   }  
			   else		 
			   secondColumn=secondColumn+vertHorizLineDivOpen;  //black
		 
	 }
		 else{
			 if (index==2){
			     secondColumn=secondColumn+vertLineDivBlueOpen;
			   } else
			   if (index==1){
				   secondColumn=secondColumn+vertLineDivRedOpen;
			   }  
			   else		 
			   secondColumn=secondColumn+vertLineDivOpen;  //black
		 }
		 
		 return
				 secondColumn+thirdContent+ "</div>";
			 
		 }
		 
// if we get to here, there are no vertical lines and (no last assumption which requires a verical line)	 
// so we just return the formula or the blanline
	 return
			 thirdContent;

}


	 
	 /*    "<td style= \"width: "
	     + thirdColumnWidth+"px;\""+
	     colSpan + ">" + */
			 
//		secondColumn+	 
			 
//	     (fLastassumption ?"<u>":"")+
//	     thirdContent+ (fLastassumption ?"</u>":""); /* + 
//	     "</td>"; */
//	}





public String thirdColumnGWT(int maxSubProofLevel/*String colSpan*/){
  int secondColumnWidth = numVertLines()*6;

  String thirdContent = "<br>";
int thirdColumnWidth = fRightMargin-secondColumnWidth;
int maxChars=thirdColumnWidth/5;

if ((!fBlankline)&&(fFormula!=null))
  thirdContent=fParser.writeFormulaToString(fFormula,maxChars);


 return
 /*    "<td style= \"width: "
     + thirdColumnWidth+"px;\""+
     colSpan + ">" + */
     (fLastassumption ?"<u>":"")+
     thirdContent+ (fLastassumption ?"</u>":""); /* + 
     "</td>"; */
}


public String fourthColumnGWT(){
  String fourthContent=""; //"<br>";
  String justification=fJustification;

if (!fBlankline){

  String temp="";

     if (fFirstjustno != 0)
       temp = temp + fFirstjustno;
     if (fSecondjustno != 0)
       temp = temp + "," + fSecondjustno;
     if (fThirdjustno != 0)
       temp = temp + "," + fThirdjustno;

/*     fourthColumn = "<td>" + temp + fJustification +
         ( (fDerived && TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") //to mark derived lines
         + "</td>";  changed Dec 07 */

    if (justification.equals(TProofListModel.kInsertionMarker))
       justification = "? &#060;&#060;";  // changing ? << to html

    fourthContent= temp + justification +
         ( (fDerived && TPreferencesData.fPrintDerived) ? "&nbsp;Auto" : "");

    if (fDerived && TPreferencesData.fBlind)
      fourthContent= ( (TPreferencesData.fPrintDerived) ? "&nbsp;Auto" : ""); // hide justification

    }

String fourthOpenTag= "<td style= \"width: " + fFourthColumnWidth+"px;\""+ ">";

return
       /* fourthOpenTag + */ fourthContent /*+  "</td>"*/;


}

/************************** End of Strings for GWT Table Rendering ********************************/



private String firstColumn(){
    /*   if (fBlankline)
        firstColumn= "<td style= \"width: 15px;\">" + strNull + "</td>";  // blanklines draw only the subprooflevels
     else
        firstColumn= "<td style= \"width: 15px;\">" + fLineno + "</td>";  // the line number
*/

   if (fBlankline)
   return
       "<td style= \"width: 15px;\"> <br> </td>";  // blanklines draw only the subprooflevels &nbsp;
      //"<td style= \"width: 15px;\">" + strNull + "</td>";
else
   return
       "<td style= \"width: 15px;\">" + fLineno + "</td>";  // the line number

}

public String secondColumn(){

	  String spacer= "<br>"; //"<br>";             //empty columns have trouble with re-sizing

	  String secondColumn="";           // we make many html columns, one for each subprooflevel

	 for (int i=-1;i<fSubprooflevel;i++){

	   secondColumn=secondColumn+
	       "<td style= \"background-color: rgb("+white +") ;width: 1px;\">"
	       + spacer/*"<font color="+colorWord+">|</font>"*/ + "</td>";    //May05, white spacer (and you have to have some content &nbsp;

	/* seem to require <br> to avoid widening. YET when I put the <br> in the
	   Mac draws it too tall in the Journal ie it puts the break in ! */


	   int index = ((i+1)%3);
	   String color=black;//grey;

	   if (index==2){
	     color = realBlue;
	   }
	   if (index==1){
	     color = realRed;
	   }


	secondColumn=secondColumn+
	       "<td style= \"background-color: rgb("+
	       color +
	       ") ;width: 2px;\">" + spacer/*"<font color="+colorWord+">|</font>"*/ + "</td>";            //May05

	   /* better for the rgb to be multiples of 17  (0 is black)   192*/

	   if (rgb<256)
	     rgb+=51;

	  // secondColumnWidth += 6;  //increment by width of two columns, should be 2 pixels
	                            //but seems to be setting to 3
	 }

	return
	     secondColumn;
	}




 /*
  int secondColumnWidth = 0; // OLD (fSubprooflevel+1)* 5;


 secondColumn="";           // we make many html columns, one for each subprooflevel



 String vertLines="";
  for (int i=-1;i<fSubprooflevel;i++){

    String colorWord="white";

    secondColumn=secondColumn+
        "<td style= \"background-color: rgb("+white +") ;width: 1px;\">"
        + "<br>"/*"<font color="+colorWord+">|</font>" + "</td>";    //May05, white spacer (and you have to have some content &nbsp;

/* seem to require <br> to avoid widening. YET when I put the <br> in the
    Mac draws it too tall in the Journal ie it puts the break in !


    int index = ((i+1)%3);
    String color=black;//grey;
    colorWord="black";

    if (index==2){
      color = realBlue;
      colorWord="blue";
    }
    if (index==1){
      color = realRed;
      colorWord = "red";
    }


secondColumn=secondColumn+
        "<td style= \"background-color: rgb("+

   /*     rgb+ "," +
        rgb+ "," +
        rgb +


        color +



   //     ") ;width: 5px;\">" + "|" + "</td>";
    ") ;width: 2px;\">" + "<br>"/*"<font color="+colorWord+">|</font>" + "</td>";            //May05

    /* better for the rgb to be multiples of 17  (0 is black)   192

    if (rgb<256)
      rgb+=51;

    secondColumnWidth += 6;  //increment by width of two columns, should be 2 pixels
                             //but seems to be setting to 3


 //   vertLines = vertLines + "|";
  }

  */

 /*  if (fBlankline)
    firstColumn= "<td style= \"width: 15px;\">" + strNull + "</td>";  // blanklines draw only the subprooflevels
 else
    firstColumn= "<td style= \"width: 15px;\">" + fLineno + "</td>";  // the line number */

/*
 for all the vertical lines we put in for subprooflevels, we must also take them off for
 the formula width

 */



/*May 05, I see all that below, but I still think something like that will be better eg
a one pixel table column*/


/* I've done a lot of experimenting here, eg one column with variable width


// '\u2502'; // // '\u20D2';   // '\uFF5C';  //;

//  "<td width=\"1\" bgcolor=\"black\"> <spacer type=\"block\" width=\"1\"></td>";

//"<img src=\"Display/blackLine.gif\" width=\"1\" height=\"1000\">";  */


/*   secondColumn= "<td style= \"background-color: rgb(192, 192, 192) ;width: "  ///*rgb(192, 192, 192)
     + secondColumnWidth+"px;\">" + vertLines + "</td>"; */




public String formulaToString(){
  int secondColumnWidth = 0;
  String thirdContent = "";
  
  

if (fSubprooflevel>-1)
  secondColumnWidth=fSubprooflevel*6;   //This is the number of vertical lines

  
int thirdColumnWidth = fRightMargin-secondColumnWidth;
int maxChars=thirdColumnWidth/6;  //was but 5 gives too many characters

//System.out.print(maxChars + " ");

if ((!fBlankline)&&(fFormula!=null))
thirdContent=fParser.writeFormulaToString(fFormula,maxChars);

if (thirdContent.length()>maxChars+2)   // if it is too long we'll shorten it
	thirdContent="...";


  return
      thirdContent;

}

public int numVertLines(){
 return
     ((fSubprooflevel>-1)?(fSubprooflevel+1):0);
}

private String thirdColumn(String colSpan){
  int secondColumnWidth = numVertLines()*6;

  String thirdContent = "<br>";
int thirdColumnWidth = fRightMargin-secondColumnWidth;
int maxChars=thirdColumnWidth/5;

if ((!fBlankline)&&(fFormula!=null))
  thirdContent=fParser.writeFormulaToString(fFormula,maxChars);


 return
     "<td style= \"width: "
     + thirdColumnWidth+"px;\""+
     colSpan + ">" +
     (fLastassumption ?"<u>":"")+
     thirdContent+ (fLastassumption ?"</u>":"") + "</td>";
}

  /*********************** Old ThirdColumn routine changed Dec 07 to give all rows same number of columns

    if (fBlankline){
      thirdColumn = strNull;
      fourthColumn = strNull;
    }
    else{
      int thirdColumnWidth = fRightMargin - secondColumnWidth;
      int maxChars = thirdColumnWidth / 5;

      if (fFormula != null) {
        thirdColumn = "<td style= \"width: "
            + thirdColumnWidth + "px;\"" +
            colStr + ">" +
            (fLastassumption ? "<u>" : "") +
            fParser.writeFormulaToString(fFormula, maxChars) +
            (fLastassumption ? "</u>" : "") + "</td>";
      }
      else
        thirdColumn = "";


      *********************** END Old ThirdColumn routine changed Dec 07 to give all rows same number of columns

      String thirdContent = "<br>";
      int thirdColumnWidth = fRightMargin-secondColumnWidth;
      int maxChars=thirdColumnWidth/5;

      if ((!fBlankline)&&(fFormula!=null))
        thirdContent=fParser.writeFormulaToString(fFormula,maxChars);


       thirdColumn= "<td style= \"width: "
       + thirdColumnWidth+"px;\""+
       colStr + ">" +
       (fLastassumption ?"<u>":"")+
       thirdContent+ (fLastassumption ?"</u>":"") + "</td>";

 /*****new Dec07 to here *****/





private String fourthColumn(){
  String fourthContent="<br>";
  String justification=fJustification;

if (!fBlankline){

  String temp="";

     if (fFirstjustno != 0)
       temp = temp + fFirstjustno;
     if (fSecondjustno != 0)
       temp = temp + "," + fSecondjustno;
     if (fThirdjustno != 0)
       temp = temp + "," + fThirdjustno;

/*     fourthColumn = "<td>" + temp + fJustification +
         ( (fDerived && TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") //to mark derived lines
         + "</td>";  changed Dec 07 */

    if (justification.equals(TProofListModel.kInsertionMarker))
       justification = "? &#060;&#060;";  // changing ? << to html

    fourthContent= temp + justification +
         ( (fDerived && TPreferencesData.fPrintDerived) ? "&nbsp;Auto" : "");

    if (fDerived && TPreferencesData.fBlind)
      fourthContent= ( (TPreferencesData.fPrintDerived) ? "&nbsp;Auto" : ""); // hide justification

    }

String fourthOpenTag= "<td style= \"width: " + fFourthColumnWidth+"px;\""+ ">";

return
        fourthOpenTag + fourthContent +  "</td>";


}

/*
 String fourthContent="<br>";

  if (!fBlankline){

    String temp=new String();

       if (fFirstjustno != 0)
         temp = temp + fFirstjustno;
       if (fSecondjustno != 0)
         temp = temp + "," + fSecondjustno;
       if (fThirdjustno != 0)
         temp = temp + "," + fThirdjustno;

  /*     fourthColumn = "<td>" + temp + fJustification +
           ( (fDerived && TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") //to mark derived lines
           + "</td>";  changed Dec 07

      fourthContent= temp + fJustification +
           ( (fDerived && TPreferences.fPrintDerived) ? "&nbsp;Auto" : "");

      if (fDerived && TPreferences.fBlind)
        fourthContent= ( (TPreferences.fPrintDerived) ? "&nbsp;Auto" : ""); // hide justification

      }

  String fourthOpenTag= "<td style= \"width: " + fFourthColumnWidth+"px;\""+ ">";

  fourthColumn = fourthOpenTag + fourthContent +  "</td>";

/*
       if (fDerived && TPreferences.fBlind)
         fourthColumn = fourthOpenTag +
                        ( (TPreferences.fPrintDerived) ? "&nbsp;Auto" : "") // hide justification
                       + "</td>";  */

   /*

    if ffirstjustno <> 0 then
   lineMessage := concat(lineMessage, StrOfNum(ffirstjustno));
  if fsecondjustno <> 0 then
   lineMessage := concat(lineMessage, ',', StrOfNum(fsecondjustno));
  if fthirdjustno <> 0 then
   lineMessage := concat(lineMessage, ',', StrOfNum(fthirdjustno));

  lineMessage := concat(lineMessage, fjustification, gCr);


        */


public String toTableRow(int maxSubProofLevel){

/*If you are interested in drawing only one line, rather than a whole proof in one table,
  call this with maxSubProofLevel of 0*/



 /*
  Lines looks like

  1 | F^G                    Ass
  2 || H                     Ass
  3 || F                     1 ^E

  now, a line on its own is fine, we can use one column for the line number, then one each for the
  vertical lines, the formula, and the justification.

  Conceptually, the line numbers are col 1, the vertical lines are subcolumns of col 2, the formula
  is col 3, and the justification col 4.

  But, if we wanted to combine several lines into a table we have the problem that the different rows
  might have a different number of columns and hence colspan= is needed.

  If this method is called with a (max) numberOfColumns, then we can insert a colspan if needed. The colspan
  needs to go with the formula (ie with F^G in line 1), we are looking for left justification.

  Now, some proofs start with a headlevel of -1, others with a headlevel of 0 (that information is
  on the line itself).

  Then the parameter tells us the maximum nesting. So the number of columns in the table as a whole is
  4 + maxSubProofLevel.

  So, any particular line has to look at the difference between its level and the headlevel. It that is
  maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 2 is needed

  ie colspan = maxSubProoflevel - difference +1 (and colspan has to be greater than 1 to matter)


  */

 //int colspan=maxSubProofLevel- (fSubprooflevel-fHeadlevel) +1;

 /*May 05, I am now putting in two vertical lines for each subprooflevel-- a white spacer column
 then the real thing

So the number of columns in the table as a whole is
  4 + 2*maxSubProofLevel.

  So, any particular line has to look at the difference between its level and the headlevel. It that is
  maxSubProoflevel no colspan is needed. If it is one less than maxSubProoflevel a colspan of 3 is needed

  ie colspan = 2(maxSubProoflevel - difference) +1 (and colspan has to be greater than 1 to matter)*/

 int colspan=2*(maxSubProofLevel- (fSubprooflevel-fHeadlevel)) +1;

 String colStr="";

// rightMarginParam=355;  set by caller

 if (colspan>1)
        colStr= " colspan="+colspan+" ";


    return
        "<tr>" +
        firstColumn()+            //firstColumn+
        secondColumn()+
        thirdColumn(colStr) +
        fourthColumn()+
        "</tr>";

  }
/* old one replace Dec 1 07, not drawing well on Windows machines and Leopard
 public static String addTableWrapper(String inputStr){
   return
        "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
        inputStr+
        "</table>";


 } */

// want to use the 'fixed' layout algorithm to get the widths correct

 public static String addTableWrapper(String inputStr){
  return
  //     "<table style=\"cellpadding:0;cellspacing:0;border:0;table-layout:fixed\">"+
  /*Dec 16 2007 CSS uses style strips out spacing padding */

      "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  >" + // width=\""+
  //   (TPreferences.fRightMargin+fFourthColumnWidth) +"\" >" +   // width=\""+ 340 +"\" table-layout=\"fixed\"

       inputStr+
       "</table>";


}


 public static String addHTMLWrapper(String inputStr){
    return
         "<html>" +
         inputStr+
         "</html>";


  }


  String transformJustification(String inStr){

// the subclasses want to draw this differently eg 'p' for 'Ass"
//  they'll override this stub


  return
      inStr;
}




/*toString is used only for exporting html-- there is a custom cell renderer for drawing onscreen */

  public String toString(){        // override the inherited method from Object so that it can draw itself

/*We are setting up to produce an html table. There are two things going on here. Each
    proofline is a row in the table and that looks like <tr> etc </tr>. Then for a
single proofline we wrap that in <table> </table>. But there are other routines
 elsewhere which print entire proofs, in which case there is only one wrapper*/

int dummy=0;
//int defaultRightMarginParam=355; //getFromGlobal

return
    addHTMLWrapper(addTableWrapper(toTableRow(dummy)));

  }

}

/*
TProofline = object(TOBJECT)
    fLineno, fFirstjustno, fSecondjustno, fThirdjustno, fSubprooflevel: INTEGER;
    fHeadlevel: INTEGER; {either0 or -1}
    fFormula: TFormula;
{$IFC NOT Defeasible}
    fJustification: string[10]; {check must match prooflinedata}
{$ELSEC}
    fJustification: string[12]; {check must match prooflinedata}
{$ENDC}
    fBlankline, fLastassumption, fSelectable, fSubProofSelectable: boolean;

                    {all new lines usually come from the procedure SupplyProofline}

    procedure TProofLine.IProofline;

    function TProofline.CloneIt: TProofline;
    procedure TProofline.DismantleProofLine;

    procedure TProofline.ReadFrom (aRefNum: INTEGER);
    procedure TProofline.WriteTo (aRefNum: INTEGER);

    function TProofline.Draw (fontSize, rightmargin: INTEGER; var wrapno: INTEGER): str255;

                                 {Debugging}
                                 {$IFC qDebug}

    procedure TProofline.Fields (procedure DoToField (fieldName: str255; fieldAddr: Ptr; fieldType: INTEGER));
    OVERRIDE;

    procedure TProofline.GetInspectorName (var inspectorName: str255);
    OVERRIDE;
                                 {$ENDC}

   end;

*/

/*



procedure SupplyProofline (var newline: TProofLine);

 begin
  New(newline);
  FailNil(newline);
  with newline do
   begin
    fLineno := 0;
    ffirstjustno := 0;
    fsecondjustno := 0;
    fthirdjustno := 0;
    fSubprooflevel := 0;
    fHeadlevel := 0;
    fFormula := nil;
    fjustification := strNull;
    fBlankline := false;
    fLastassumption := false;
    fSelectable := TRUE;
    fSubProofSelectable := false;

   end;

 end;

*/
