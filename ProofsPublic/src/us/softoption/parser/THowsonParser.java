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

package us.softoption.parser;

//8/9/06

/*changed Feb 10 to use CCParserTwo */

import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chComma;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chHArr;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotSign;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strNull;
import static us.softoption.parser.TFormula.predicator;

import java.io.Reader;
import java.util.ArrayList;


public class THowsonParser extends TParser{
	
	CCParserTwo fCCParserTwo;
	
	

  /*We want several different parsers of similar structure; we define generic procedures
   which are called in the normal way.  Most documents and windows have fParser fields to
   ensure the correct reading and writing.*/
	
/* Need to override wffcheck, if different, and term, if different (eg for UI) */


  /*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
   but Javas regular expression powers allow this to be done to the input before it ever gets to skip
   or to the parser*/

  /*constants*/

	public static byte HOWSON=(byte) (CCParser.BRACKETFREEQUANTS+CCParser.BRACKETEDPREDS);


  public THowsonParser() {
	  java.io.StringReader sr = new java.io.StringReader( "" );
	  
	  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(sr)*/ sr);
	  
	  fMinCellWidth=14;
  }
 
  public THowsonParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
      fMinCellWidth=12;
      }
 
 
@Override  
  public boolean term (TFormula root, Reader aReader){    // sometimes called externally to parse term
		TFormula cCroot;
		
		  if (fCCParser==null)

			  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(aReader)*/ aReader);
		  else

			  fCCParserTwo.reInit(/*new java.io.BufferedReader(aReader)*/ aReader);
		try {

			cCroot= fCCParserTwo.term();
	    } catch (ParseException e) {
	    	cCroot=null;

	    }	
		
	if(cCroot==null)
		return
			ILLFORMED;
	else{
		root.assignFieldsToMe(cCroot);   //surgery
		return
			WELLFORMED;
	} 
  } 
  
  
@Override   
  public boolean wffCheck (TFormula root, /*ArrayList<TFormula> newValuation,*/Reader aReader){
	  TFormula cCroot;
	  
	  if (fCCParser==null)

		  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(aReader)*/ aReader);
	  else

		  fCCParserTwo.reInit(/*new java.io.BufferedReader(aReader)*/ aReader);

	  try {

		  cCroot= fCCParserTwo.wffCheck();
	      } catch (ParseException e) {
	      	cCroot=null;

	      	
	      	initializeErrorString();   	

	      	
	      	writeError("(*Selection not well-formed*)");
	      	
	      	
	      }	
	  	
	  if(cCroot==null)
	  	return
	  		ILLFORMED;
	  else{
	  	root.assignFieldsToMe(cCroot);   //surgery
	  	return
	  		WELLFORMED;
	  }
  }
  
  @Override 
  public boolean wffCheck (TFormula root, ArrayList<TFormula> newValuation,Reader aReader){
//need to write this if you need it
	  
	  return
	  false;
  }



 /***************  writing **************************/ 
  
  @Override 
  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }

  @Override 
  public String renderImplic() {
  return
      String.valueOf(chArrow);
}
@Override 
public String renderEquiv() {
return
    String.valueOf(chHArr);
}
@Override 
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



    return
        connective;
  }


@Override 
  public String writeQuantifierToString(TFormula root){

              String prefix = new String();
              String scope = new String();

              if (root.fInfo.equals(String.valueOf(chUnique)))
                prefix = ( String.valueOf(chExiquant) +
                          String.valueOf(chUnique) + root.quantVar());
              else
                prefix = ( translateConnective(root.fInfo) + root.quantVar());

              scope = writeInner(root.scope());

              if (scope.length() > fWrapBreak)
                scope = fScopeTruncate;
              return
                  (prefix + scope);


  }
@Override 
            public String writeTypedQuantifierToString(TFormula root){
                        String prefix = new String();
                        String scope = new String();
                        TFormula type = root.quantTypeForm();

                        String typeStr=type!=null?writeFormulaToString(type):"";

                        if (root.fInfo.equals(String.valueOf(chUnique)))
                          prefix = ( String.valueOf(chExiquant) +
                                    String.valueOf(chUnique) + root.quantVar() + ":"+typeStr);
                        else
                          prefix = ( translateConnective(root.fInfo) + root.quantVar() + ":"+typeStr);

                        
 /*we have to go careful here because if the Scope is a predicate
  * then with Allx:tF the predicate would be read as part of the type
  * so need to put it in brackets, thus Allx:t(F)                       
  */
                        if (root.scope()!=null)
                        	if (root.scope().fKind==predicator){
                        		scope =  "("+writeInner(root.scope())+")";
                        	}
                        	else
                        		scope =  writeInner(root.scope());

                        if (scope.length() > fWrapBreak)
                          scope = fScopeTruncate;
                        return
                            (prefix + scope);
                    }


@Override 
  String writeListOfTermsToString(TFormula head) {
    String outPutStr = strNull, tempStr = strNull;

    if (head != null) {
      outPutStr = writeTermToString(head.fLLink);
      head = head.fRLink;

    }

    while ( (head != null) && (outPutStr.length() < 128)) {
      tempStr = chComma +
          writeTermToString(head.fLLink);
      if (tempStr.length() > 96)
        tempStr = "<term>";
      outPutStr = outPutStr + tempStr;
      head = head.fRLink;
    }
    if (outPutStr.length() > 127)
      outPutStr = "<terms>";
    return outPutStr;
  }

  @Override 
  public String writePredicateToString(TFormula predicate) {

    if (isPredInfix(predicate.fInfo)) {
      return
          (//chSmallLeftBracket +
           writeTermToString(predicate.firstTerm()) +
           predicate.fInfo +
           writeTermToString(predicate.secondTerm()) //+
       //    chSmallRightBracket
           );

    }

    else {

    if (isAccessRelation(predicate)){   /*special case, we want Access(a,b) written
                                                 Access(a,b) not A(c,c,e,s,s(a,b)) */
     String worlds=getAccessRelation(predicate);


  return
          "Access" + "(" + worlds.charAt(0) + "," + worlds.charAt(1) + ")";
    }

      String terms = writeListOfTermsToString(predicate.fRLink);

      return
          (
              predicate.fInfo +

              ( (terms != strNull) ?
               (chSmallLeftBracket + terms +
                chSmallRightBracket) :
               strNull)
          );


    }
  }

 

}





