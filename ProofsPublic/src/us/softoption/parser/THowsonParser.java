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
	  
	/*  fCCParser=new CCParser(/*new java.io.BufferedReader(sr) sr,HOWSON); */
	  
	  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(sr)*/ sr);
	  
	  fMinCellWidth=14;
  }
 
  public THowsonParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
      fMinCellWidth=12;
      }
 
  public boolean term (TFormula root, Reader aReader){    // sometimes called externally to parse term
		TFormula cCroot;
		
		  if (fCCParser==null)
			  /*
			  fCCParser=new CCParser(/*new java.io.BufferedReader(aReader) aReader,HOWSON);
			  */
			  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(aReader)*/ aReader);
		  else
			  /*
			  fCCParser.reInit(/*new java.io.BufferedReader(aReader) aReader,HOWSON);
		 */
			  fCCParserTwo.reInit(/*new java.io.BufferedReader(aReader)*/ aReader);
		try {
			/*cCroot= fCCParser.term();*/
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
	  
	  if (fCCParser==null)
		 /* fCCParser=new CCParser(/*new java.io.BufferedReader(aReader) aReader,HOWSON); */
		  fCCParserTwo=new CCParserTwo(/*new java.io.BufferedReader(aReader)*/ aReader);
	  else
		  /* fCCParser.reInit(/*new java.io.BufferedReader(aReader) aReader,HOWSON); */
		  fCCParserTwo.reInit(/*new java.io.BufferedReader(aReader)*/ aReader);

	  try {
	  		/*cCroot= fCCParser.wffCheck();*/
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
	  
	  return
	  false;
  }



 /***************  writing **************************/ 
  
  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }

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

  /******** need to revise these overrides *****/


  /*

  public boolean isNegationCh(char aChar) {

    return
        ( (aChar == chNeg) || (negationChs.indexOf(aChar) > -1)); // we allow the parser to 'softly' parse alternative negations

  }

/*  public boolean isOr(TFormula root) {

    if ( (root.fKind == binary) && (root.fInfo.charAt(0) == chOr))
      return
          true;
    else
      return
          false;
  }  */

  /***************************/











  /*public static boolean isFunctor (char ch)
          {
          return  ((gFunctors.indexOf((int)ch)!=-1));
          } */

  /* public static boolean isPredicate (char ch)
           {
           return  ((gPredicates.indexOf((int)ch)!=-1));
           } */
/*
  public static boolean isVariable(char ch) {
    return ( (gCopiVariables.indexOf( (int) ch) != -1));
  }

  /*Dec 09 no longer subclassing this
  
  boolean isPredInfix(String inf) {
    return ( inf.equals(strEquals)|| 
    		inf.equals(strLessThan));
  }
  
  */

  /*I don't think I need to subclass term, subclassing isVariable will do it
    private boolean term (TFormula root)     /*seems ok June 26th
             {
             TFormula subterm=null;

             if (isFunctor(fCurrCh))    			/* tests whether functor
                  {
                  root.fInfo=String.valueOf(fCurrCh);

                  if (isVariable(fCurrCh))   			 /* tests whether a variable
      root.fKind=variable;  /*some zero order functors are variables
                        else
                                root.fKind=functor;

                    skip(1); 				/*looking at small left-bracket or next item

                     if (fCurrCh == chSmallLeftBracket)
                         {
                         root.fKind = functor;  		/*cannot now be a variable

                          skip(1); 				/*looking at next item

                           while (fCurrCh != chSmallRightBracket)
                               {
                               subterm = new TFormula();
                               if (this.term(subterm))
                                   root.appendToFormulaList(subterm);
                               else
                                   return ILLFORMED;
                                   }

                           skip(1); 				/*the small right-bracket
                            }
                        }
                    else
                        {
           writeError("(*The character '"+fCurrCh+"' should be a functor.*)");
                        return ILLFORMED;
                        }
                    return WELLFORMED;
                  }  */






}


/************************** End of Version *************************************/

/***************** OLD ***************************/

/*
 * 
 * static final String gCopiConstants = "abcdefghijklmnopqrst";
//static final String gFunctors="abcdefghijklmnopqrstuvwxyz012";
  /*zero-order functors a..l are constants, m..z are variables
   the binary predefineds like + are treated as special cases
   and there are the numerals 0,1,2; conceptually gFunctors is a
                                            set of char

  public static final String gCopiVariables = "uvwxyz"; //{take care if you change these as some procedures use}
  //   their indices, e.g. TFormula.firstfreevar}
  //public static final String gPredicates="ABCDEFGHIJKLMNOPQRSTUVWXYZ";


  /*
          gCopiConstants: set of 'a'..'t'; {for Copi}
        gCopiFunctors: set of 'a'..'z';   {zero-order functors a..t are constants, u..z are variables}
        gCopiTerms: set of 'a'..'z';
        gCopiVariables: set of 'u'..'z'; {take care if you change these as some procedures use}
      {                                                their indices, e.g. firstfreevar}
        gCopiPredicates: set of 'A'..'Z';


   


  public static final String gCopiConnectives = "" + /*chCopiAnd+ chNeg +
      chOr + chExiquant + chImplic + chEquiv;

  public static final String gHowsonConnectives=""+chAnd+chNotSign+chOr+chExiquant+chUniquant+
  												chArrow+chHArr;
  
  /*
  public static boolean isCopiConnective(char ch) {
    return
        ( (gCopiConnectives.indexOf( (int) ch) != -1));
  }

/*  public boolean isImplic(TFormula root) {

    if ( (root.fKind == binary) && (root.fInfo.charAt(0) == chImplic))
      return
          true;
    else
      return
          false;
  }

  public boolean isNegation(TFormula root) {

    return
        ( (root.fKind == unary) && (root.fInfo.charAt(0) == chNeg));

  } 
  
  public /*static boolean isConnective (char ch){
	    return
	        ((gHowsonConnectives.indexOf((int)ch)!=-1));
	 }
  
  

/************  July 08 **************************

boolean secondary(TFormula root)  //seems ok

/*{<secondary>::= ~<secondary> | Allv<secondary>}
    {Exv<secondary>|<primary>}*/

/*added May 08 also{<secondary>::= modalpossible<secondary> | modalnecessary<secondary>*/

/*Note July 08 Howson does not put brackets around quantifier 

{
  class Negtest {
    boolean testIt(TFormula negRoot){
      TFormula newnode= new TFormula(unary,String.valueOf(chNeg),null,null);
      TFormula rLink=new TFormula();

      skip(1);   // the negation sign

      if (secondary(rLink)){
        newnode.fRLink = rLink;
        negRoot.assignFieldsToMe(newnode);
        newnode=null;
        return
        WELLFORMED;
      }
      else
        return
            ILLFORMED;
    }
  }

  class ModalPossibletest {
 boolean testIt(TFormula possRoot){
   TFormula newnode= new TFormula(unary,String.valueOf(chModalPossible),null,null);
   TFormula rLink=new TFormula();

   skip(1);   // the negation sign

   if (secondary(rLink)){
     newnode.fRLink = rLink;
     possRoot.assignFieldsToMe(newnode);
     newnode=null;
     return
     WELLFORMED;
   }
   else
     return
         ILLFORMED;
 }
}


class ModalNecessarytest {
boolean testIt(TFormula necessRoot){
TFormula newnode= new TFormula(unary,String.valueOf(chModalNecessary),null,null);
TFormula rLink=new TFormula();

skip(1);   // the negation sign

if (secondary(rLink)){
  newnode.fRLink = rLink;
  necessRoot.assignFieldsToMe(newnode);
  newnode=null;
  return
  WELLFORMED;
}
else
  return
      ILLFORMED;
}
}

  class QuantTest {  //{take care here for this might be Ex! for the unique quantifier}
    boolean testIt(TFormula quantRoot) {
     // skip(1); // the opening bracktet, now looking at quantifier NO NEED WITH HOWSON

      if ((fCurrCh == chExiquant) && (fLookAheadCh == chUnique)) // {unique case}
        skip(1); // {now looking at exclamation mark}

      TFormula newnode = new TFormula(quantifier, String.valueOf(fCurrCh), null, null);
      TFormula variableNode = new TFormula(variable,
                                           String.valueOf(fLookAheadCh), null, null);
      skip(1); //now looking at variable


      /*<variable>::= isVariable(atomic) | <variable>:<type lable> }
        <type lable> ::= <functor ie constant>

      if (!(isVariable(fCurrCh))) {
         writeError(strCR + "( *'"+fCurrCh +
            "' should be a variable. *)");
      return
        ILLFORMED;
      }

      if (fLookAheadCh == chColon){    //typed variable/quantifier
        /*typing of variables only occurs in the presence of a quantifier, so it is clearer to say that
        the quantifier is typed 

        newnode.fKind=typedQuantifier;  //change from quantifier to typedQuantifier

        skip(1);   //current looking at colon, lookahead looking at type label

        TFormula typeNode = new TFormula(functor, String.valueOf(fLookAheadCh), null, null);

        if (!type(typeNode)){    //new  June 08
           writeError(strCR + "( *'" + fCurrCh +
                  "' should be a type label. *)");
           return
               ILLFORMED;

     }

     TFormula head = new TFormula(kons,
                                "",
                                variableNode,
                                null);

     newnode.fLLink=head;

     //head.appendToFormulaList(variableNode);     //variable has its type hanging off
     newnode.fLLink.appendToFormulaList(typeNode);         //variable has its type hanging off
     //newnode.fLLink=head;

     //new to here  




      }

else


      newnode.fLLink = variableNode;                    //no type


/*      if ((fLookAheadCh != ')')) {   //new
        writeError(strCR + "( *'" + fCurrCh +
                   "' should be a ')' . *)");
        return
            ILLFORMED;
      }
else {
        skip(1); // the variable USED TO BE SKIP(2) TO GET VARIABLE AND BRACKET

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
    }
  }





  if (isNegationCh(fCurrCh)/*fCurrCh == chNeg){
    Negtest tester =new Negtest();
    if (tester.testIt(root))
      return
         WELLFORMED;
    else
      return
         ILLFORMED;
     }
  else
  if (isModalPossibleCh(fCurrCh)/*fCurrCh == chNeg){
 ModalPossibletest tester =new ModalPossibletest();
 if (tester.testIt(root))
   return
      WELLFORMED;
 else
   return
      ILLFORMED;
  }
else
  if (isModalNecessaryCh(fCurrCh)/*fCurrCh == chNeg){
 ModalNecessarytest tester =new ModalNecessarytest();
 if (tester.testIt(root))
   return
      WELLFORMED;
 else
   return
      ILLFORMED;
  }
else

  if (isUniquantCh(fCurrCh)||isExiquantCh(fCurrCh)


    /*(fCurrCh == '(')&&((fLookAheadCh == chUniquant)||fLookAheadCh == chExiquant)){
    QuantTest tester =new QuantTest();
    if (tester.testIt(root))
      return
         WELLFORMED;
    else
      return
         ILLFORMED;
  }
 else
    return primary(root);

}










boolean predicate(TFormula root) { /* predicate P(<term1>, <term2>,...) */
 /*Seems OK June25 03

  TFormula subterm;

  if (isPredicate(fCurrCh)) {
    root.fKind = predicator;
    root.fInfo = String.valueOf(fCurrCh);

    skip(1);

    /*The termlist must be surrounded by brackets) 

    if (fCurrCh == chSmallLeftBracket) {

      skip(1);

      while (isFunctor(fCurrCh) || (fCurrCh == chSmallLeftBracket))
      /*a term can start with a bracket
      {
        subterm = new TFormula();
        if (term(subterm))
          root.appendToFormulaList(subterm);
        else
          return ILLFORMED;

        /*at this point it can be looking either at a comma (meaning
           there is another term) or a right bracket (meaning we're
         done

        if (fCurrCh != chSmallRightBracket &&
            fCurrCh != chComma)
          return ILLFORMED;

        if (fCurrCh == chComma)
          skip(1); //ignore comma

      }

      //    skip(1);

      /*The termlist must be surrounded by brackets) 

      if (fCurrCh != chSmallRightBracket)
        return ILLFORMED;

      skip(1); //closing bracket

    }

    return WELLFORMED;
  }

  else {
    writeError("(*The character '" + fCurrCh +
               "' should be a ( or a Predicate.*)");
    return ILLFORMED;
  }

} */


