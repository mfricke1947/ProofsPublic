package us.softoption.parser;

//11/29/11

// (x)Fx.(y)Fy ambiguous between ((x)Fx).(y)Fy (x)F(x.y)Fy illformed


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strNull;

import java.io.Reader;


public class TCopiParser extends TParser{

/*
 * 
 * 
  no types with Copi variables
 * 
 */
	
	
	/*Dec 09, new using CCParser for this */
	
	/*We want several different parsers of similar structure; we define generic procedures
which are called in the normal way.  Most documents and windows have fParser fields to
ensure the correct reading and writing.*/


/*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
but Javas regular expression powers allow this to be done to the input before it ever gets to skip
or to the parser*/
	
/* In Copi Ab.C is conjunct and Ab.c is a statement ie the '.' can be an and or can be an infix multiply  
 * See the section on infix multiply*/	
	
/*There are two main differences with Copi. It uses '.' for and-- we don't have to worry about that as the superclass
 * TParser can parse that anyway; and when it is written back renderAnd fixes it. And it uses (x) insteand of (Allx).
 */	

/*constants*/

 public TCopiParser(){
 }

 public TCopiParser(Reader aReader,boolean firstOrder)
 {
     super(aReader,firstOrder);
     }



//static final String gCopiConstants="abcdefghijklmnopqrst012";
//static final String gFunctors="abcdefghijklmnopqrstuvwxyz012";
                                         /*zero-order functors a..l are constants, m..z are variables
                                         the binary predefineds like + are treated as special cases
                                         and there are the numerals 0,1,2; conceptually gFunctors is a
                                         set of char*/

public static final String gCopiVariables="uvwxyz";   //NEED {take care if you change these as some procedures use}
                                           //   their indices, e.g. TFormula.firstfreevar}
 //public static final String gPredicates="ABCDEFGHIJKLMNOPQRSTUVWXYZ";


/*
        gCopiConstants: set of 'a'..'t'; {for Copi}
      gCopiFunctors: set of 'a'..'z';   {zero-order functors a..t are constants, u..z are variables}
      gCopiTerms: set of 'a'..'z';
      gCopiVariables: set of 'u'..'z'; {take care if you change these as some procedures use}
    {                                                their indices, e.g. firstfreevar}
      gCopiPredicates: set of 'A'..'Z';


  */

 public static final char chCopiAnd = '.';// {for CopiLogic}

// public static final String gCopiConnectives=""+chCopiAnd+chNeg+chOr+chExiquant+chImplic+chEquiv;


public String renderAnd(){
   return
       String.valueOf(chCopiAnd);
         }

public String renderUniquant(){
	   return
	       "";
	         }


public String translateConnective(String connective){

  if (connective.equals(String.valueOf(chAnd)))
    return
        String.valueOf(chCopiAnd);
  if (connective.equals(String.valueOf(chUniquant)))
  return
      String.valueOf(strNull);
 return
     super.translateConnective(connective);
}




/******** overrides *****/

/*

//public static boolean isCopiConnective (char ch){
public /*static boolean isConnective (char ch){
           return
               ((gCopiConnectives.indexOf((int)ch)!=-1));
        }

 public static boolean isVariable (char ch)
         {
         return  ((gCopiVariables.indexOf((int)ch)!=-1));
         }
*/

/******************************** Infix multiplication versus infix And ***********************/

/* In Copi Ab.C is conjunct and Ab.c is a statement ie the '.' can be an and or can be an infix multiply  
 * When scanning this, it can only be a problem if we are reading a term, in particular infix multiply
 * So we will override the super to test whether a term follows*/	

//protected boolean infix2(TFormula root) {   
	// <termprimary> . <termsecondary>
	// <termprimary> XProd <termsecondary>   //from set theory
	
	/* what this is going to read is a . b
and we have already read a as root.  So, we will read b. Then create a x node
and make that the root and put a and b in its termlist*/
	
	/*we come in here with a well formed term, say 1. What this needs to do is to
	 * swallow any multiplication sign returning WELLFORMED if there is none
	 * So, we enter with, say, 1.2<next> and leave
	 * with fChrrCh looking at <next>
	 */	
/*	
boolean wellFormed=true;

if (infixBinFun2(fCurrCh)){
   TFormula newRoot = new TFormula(TFormula.functor,
                   String.valueOf(fCurrCh),  //probably mult symbol
                   null,
                   null);


   	if (fCurrCh=='.'){    // could be multiply or could be and
	   
   
   		TermTest isTerm = new TermTest();
   		boolean skipCurrent=true;
   
   		if (isTerm.testIt(skipCurrent)) {  //it is a term (a period b)
   		   skip(1); /*the mult

   		   TFormula rightTerm=new TFormula();

   		   wellFormed=termSecondary(rightTerm);

   		   if (wellFormed){
   		      TFormula  leftTerm = new TFormula(root.getKind(),
   		                                     root.getInfo(),
   		                                     root.getLLink(),
   		                                     root.getRLink());
   		      newRoot.appendToFormulaList(leftTerm);
   		      newRoot.appendToFormulaList(rightTerm);

   		      root.assignFieldsToMe(newRoot);   //surgery
   		      
   		      return
   		      	WELLFORMED;
	   
   		   }
   		}
   		else{                  // not period followed by term, hence well formed as is no advance of currentCh
   		 return               // something else will see if it is an and
	      	WELLFORMED;
   		}

   		} // end of currCh==period
   	
   		if (infixBinFun2(fCurrCh)){   // all the other cases of infix binary except '.'
   			                          // we have already tested for this-- just for clarity in the coee
   			newRoot = new TFormula(TFormula.functor,
   	                   String.valueOf(fCurrCh),  //probably mult symbol
   	                   null,
   	                   null);


   			skip(1); /*the mult etc 

   			TFormula rightTerm=new TFormula();

   			wellFormed=termSecondary(rightTerm);

   			if (wellFormed){
   				TFormula  leftTerm = new TFormula(root.getKind(),
   	                                     root.getInfo(),
   	                                     root.getLLink(),
   	                                     root.getRLink());
   				newRoot.appendToFormulaList(leftTerm);
   				newRoot.appendToFormulaList(rightTerm);

   				root.assignFieldsToMe(newRoot);   //surgery
   			 return
		      	WELLFORMED;
   			}
   		}	// all the other cases of infix binary except '.'
   	 	
   	
}

return
    wellFormed;   //this could be true, if nothing is triggered. But it could also be false if there
                  // is something wrong with the right hand term.
} */

/******************************** End of Infix multiplication versus infix And ***********************/



}






