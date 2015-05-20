package us.softoption.parser;

//8/9/06


import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chHArr;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotSign;

import java.io.Reader;


public class TJeffreyParser extends TPriestParser/*THowsonParser*/{

  /*We want several different parsers of similar structure; we define generic procedures
   which are called in the normal way.  Most documents and windows have fParser fields to
   ensure the correct reading and writing.*/
	
	/*
	 *  Jeffrey uses no brackets for quantiers, no brackets for arguments
	 *  ie like Priesst
	 *  
	 *  but symbols like Howson
	 */


  /*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
   but Javas regular expression powers allow this to be done to the input before it ever gets to skip
   or to the parser*/

  /*constants*/
	
public static byte JEFFREY= CCParser.BRACKETFREEQUANTS;	

  public TJeffreyParser() {
	  fMinCellWidth=14;
  }

  public TJeffreyParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
      fMinCellWidth=14;
      }



  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }

  /*
  public String renderAnd() {
	    return
	        String.valueOf(chAnd2);
	  }
  */
  
  public String renderImplic() {
  return
      String.valueOf(chArrow);
}

public String renderEquiv() {
return
    String.valueOf(chHArr);
}

  public String translateConnective(String connective) {

	  
	    if (connective.equals(String.valueOf(chNeg)))
	        return
	            String.valueOf(chNotSign);
    
	  
	  if (connective.equals(String.valueOf(chImplic)))
      return
          String.valueOf(chArrow);

    if (connective.equals(String.valueOf(chEquiv)))
  return
      String.valueOf(chHArr);
/*
    if (connective.equals(String.valueOf(chAnd)))
    	  return
    	      String.valueOf(chAnd2);
*/
    return
        connective;
  }
/*
  boolean predicate(TFormula root){ /* predicate P<term1> <term2>... 
      (x<y) what about equals*/   /*Seems OK June25 03
	  
/*Howson, the superclass, has P(<term1>, <term2>,...) we don't want that   

TFormula subterm;

if (isPredicate(fCurrCh))
{
root.fKind = predicator;
root.fInfo=toInternalForm(fCurrCh);//String.valueOf(fCurrCh);

skip(1);

while (isFunctor(fCurrCh)||
(fCurrCh == chSmallLeftBracket)||
(fCurrCh == '<'))
        /*a term can start with a bracket
         * or be an ordered pair
{
subterm = new TFormula();
if (term(subterm))
root.appendToFormulaList(subterm);
else
return ILLFORMED;
}
return WELLFORMED;
}
else
{
writeError("(*The character '"+ fCurrCh+ "' should be a ( or a Predicate.*)");
return ILLFORMED;
}

}
  
  public String writePredicateToString (TFormula predicate){

	  if (isPredInfix(predicate.fInfo))
	    {
	    return
	       (//chSmallLeftBracket+   //writeInner will put brackets round inner, don't need them on outer
	       writeTermToString(predicate.firstTerm())+
	       predicate.fInfo+
	       writeTermToString(predicate.secondTerm()));//+
	      // chSmallRightBracket);

	    }

	  else
	        {
	    return
	    (
	    predicate.fInfo+
	    writeListOfTermsToString(predicate.fRLink));

	    }
	  }  
  
  String writeListOfTermsToString (TFormula head)
  {String outPutStr=strNull, tempStr=strNull;

  while ((head!=null) && (outPutStr.length() < 128))
          {
          tempStr= writeTermToString(head.fLLink);
          if (tempStr.length() > 96)
                       tempStr = "<term>";
      outPutStr= outPutStr+tempStr;
      head = head.fRLink;
          }
  if (outPutStr.length() > 127)
             outPutStr = "<terms>";
     return outPutStr;
  } */
  

}


/************************** End of Version *************************************/
