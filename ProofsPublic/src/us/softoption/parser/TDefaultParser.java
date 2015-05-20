package us.softoption.parser;


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chAnd2;
import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chComma;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strNull;
import static us.softoption.parser.TFormula.predicator;

import java.io.Reader;
import java.util.ArrayList;

import us.softoption.infrastructure.TConstants;

public class TDefaultParser extends TParser{
	
	CCParserTwo fCCParserTwo;

  public TDefaultParser() {
	  java.io.StringReader sr = new java.io.StringReader( "" );

	  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(sr)*/ sr);
	  
	  fMinCellWidth=14;
  }
 
 
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
	    //    System.out.println("Not parsed");
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
  
  
  
  public boolean wffCheck (TFormula root, /*ArrayList<TFormula> newValuation,*/Reader aReader){
	  TFormula cCroot;
	  
	  if (fCCParserTwo==null)
		  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(aReader)*/ aReader);
	  else
		  fCCParserTwo.reInit(/*new java.io.BufferedReader(aReader)*/ aReader);

	  try {

		  cCroot= fCCParserTwo.wffCheck();
	      } catch (ParseException e) {
	      	cCroot=null;
	      //    System.out.println("Not parsed");
	      	
	      	initializeErrorString();   	
	  //use for debug    	fParserErrorMessage.write(e.getMessage());
	      	
	      	writeError("(*Selection not well-formed*)");
	      	
	  //    	System.out.println(e.getMessage());
	      	
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
  
  public boolean wffCheck (TFormula root, ArrayList<TFormula> newValuation,Reader aReader){
//need to write this if you need it
	  
	  //TODO JUNE 2013, MAY BE SUSPECT
	  
	  TFormula cCroot;

	//int defaultType=0;

	if (fCCParserTwo==null)
		fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(*/aReader);
	else
		fCCParserTwo.reInit(/*new java.io.BufferedReader(*/aReader/*),CCParser.DEFAULT*/);

	//fCCParserTwo=new CCParserTwo(new java.io.BufferedReader(aReader));
		try {
			cCroot= fCCParserTwo.wffCheckWithValuation(newValuation);
	    } catch (ParseException e) {
	    	cCroot=null;
	    //    System.out.println("Not parsed");
	    	
	    	initializeErrorString();   	
	    	
	    	if (TConstants.DEBUG)
	    	   fParserErrorMessage.write(e.getMessage());
	    	else
	    		 fParserErrorMessage.write("(*Illformed*)");
	  
	    	if (TConstants.DEBUG)
	    	System.out.println(e.getMessage());
	    	
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



 /***************  writing **************************/ 
  
  public String renderAnd() {
    return
        String.valueOf(chAnd2);
  }

  public String renderImplic() {
  return
      String.valueOf(chArrow);
}
/*
public String renderEquiv() {
return
    String.valueOf(chHArr);
}*/

  public String translateConnective(String connective) {

    if (connective.equals(String.valueOf(chAnd)))
      return
          String.valueOf(chAnd2);

    if (connective.equals(String.valueOf(chImplic)))
      return
          String.valueOf(chArrow);

 /*   if (connective.equals(String.valueOf(chEquiv)))
  return
      String.valueOf(chHArr);

*/

    return
        connective;
  }



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

      //      chSmallLeftBracket+   //new
      //      writeListOfTermsToString(predicate.fRLink))+
      //      chSmallRightBracket;  //new

    }
  }
}





