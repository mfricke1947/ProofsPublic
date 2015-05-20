package us.softoption.parser;



//3/2/01


import static us.softoption.infrastructure.Symbols.andChs;
import static us.softoption.infrastructure.Symbols.chAdd;
import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chAt;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chColon;
import static us.softoption.infrastructure.Symbols.chComma;
import static us.softoption.infrastructure.Symbols.chDoubleArrow;
import static us.softoption.infrastructure.Symbols.chEmptySet;
import static us.softoption.infrastructure.Symbols.chEquals;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chIntersection;
import static us.softoption.infrastructure.Symbols.chKappa;
import static us.softoption.infrastructure.Symbols.chLSqBracket;
import static us.softoption.infrastructure.Symbols.chLambda;
import static us.softoption.infrastructure.Symbols.chMemberOf;
import static us.softoption.infrastructure.Symbols.chMinus;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chMult;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotMemberOf;
import static us.softoption.infrastructure.Symbols.chOr;
import static us.softoption.infrastructure.Symbols.chPowerSet;
import static us.softoption.infrastructure.Symbols.chRSqBracket;
import static us.softoption.infrastructure.Symbols.chRho;
import static us.softoption.infrastructure.Symbols.chSlash;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.chSubscript0;
import static us.softoption.infrastructure.Symbols.chSubscript1;
import static us.softoption.infrastructure.Symbols.chSubscript2;
import static us.softoption.infrastructure.Symbols.chSubscript3;
import static us.softoption.infrastructure.Symbols.chSubscript4;
import static us.softoption.infrastructure.Symbols.chSubscript5;
import static us.softoption.infrastructure.Symbols.chSubscript6;
import static us.softoption.infrastructure.Symbols.chSubscript7;
import static us.softoption.infrastructure.Symbols.chSubscript8;
import static us.softoption.infrastructure.Symbols.chSubscript9;
import static us.softoption.infrastructure.Symbols.chSubset;
import static us.softoption.infrastructure.Symbols.chSucc;
import static us.softoption.infrastructure.Symbols.chTherefore;
import static us.softoption.infrastructure.Symbols.chUnion;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.chUniverseSet;
import static us.softoption.infrastructure.Symbols.chVertLine;
import static us.softoption.infrastructure.Symbols.chXProd;
import static us.softoption.infrastructure.Symbols.equivChs;
import static us.softoption.infrastructure.Symbols.implicChs;
import static us.softoption.infrastructure.Symbols.intSubscript0;
import static us.softoption.infrastructure.Symbols.intSubscript1;
import static us.softoption.infrastructure.Symbols.intSubscript2;
import static us.softoption.infrastructure.Symbols.intSubscript3;
import static us.softoption.infrastructure.Symbols.intSubscript4;
import static us.softoption.infrastructure.Symbols.intSubscript5;
import static us.softoption.infrastructure.Symbols.intSubscript6;
import static us.softoption.infrastructure.Symbols.intSubscript7;
import static us.softoption.infrastructure.Symbols.intSubscript8;
import static us.softoption.infrastructure.Symbols.intSubscript9;
import static us.softoption.infrastructure.Symbols.memberOfChs;
import static us.softoption.infrastructure.Symbols.modalNecessaryChs;
import static us.softoption.infrastructure.Symbols.modalPossibleChs;
import static us.softoption.infrastructure.Symbols.negationChs;
import static us.softoption.infrastructure.Symbols.orChs;
import static us.softoption.infrastructure.Symbols.strAdd;
import static us.softoption.infrastructure.Symbols.strDownTack;
import static us.softoption.infrastructure.Symbols.strEmptySet;
import static us.softoption.infrastructure.Symbols.strEquals;
import static us.softoption.infrastructure.Symbols.strGreaterThan;
import static us.softoption.infrastructure.Symbols.strInfixPreds;
import static us.softoption.infrastructure.Symbols.strIntersection;
import static us.softoption.infrastructure.Symbols.strLessThan;
import static us.softoption.infrastructure.Symbols.strMemberOf;
import static us.softoption.infrastructure.Symbols.strMinus;
import static us.softoption.infrastructure.Symbols.strMult;
import static us.softoption.infrastructure.Symbols.strNotMemberOf;
import static us.softoption.infrastructure.Symbols.strNull;
import static us.softoption.infrastructure.Symbols.strSubsetOf;
import static us.softoption.infrastructure.Symbols.strSucc;
import static us.softoption.infrastructure.Symbols.strUnion;
import static us.softoption.infrastructure.Symbols.strUniverseSet;
import static us.softoption.infrastructure.Symbols.strUpTack;
import static us.softoption.infrastructure.Symbols.strXProd;
import static us.softoption.infrastructure.Symbols.superScripts;
import static us.softoption.parser.TFormula.application;
import static us.softoption.parser.TFormula.binary;
import static us.softoption.parser.TFormula.comprehension;
import static us.softoption.parser.TFormula.equality;
import static us.softoption.parser.TFormula.functor;
import static us.softoption.parser.TFormula.kons;
import static us.softoption.parser.TFormula.lambda;
import static us.softoption.parser.TFormula.modalKappa;
import static us.softoption.parser.TFormula.modalRho;
import static us.softoption.parser.TFormula.pair;
import static us.softoption.parser.TFormula.predicator;
import static us.softoption.parser.TFormula.quantifier;
import static us.softoption.parser.TFormula.typedQuantifier;
import static us.softoption.parser.TFormula.unary;
import static us.softoption.parser.TFormula.variable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TUtilities;

/*We want several different parsers of similar structure; we define generic procedures
which are called in the normal way.  Most documents and windows have fParser fields to
ensure the correct reading and writing.*/


/*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
but Javas regular expression powers allow this to be done to the input before it ever gets to skip
or to the parser*/

/*When reading in, we will usually accept any of the alternative versions of, say, the not symbol
but when writing back we will use the appropriate one*/

/*for the symbols we 'import static' from Symbols*/

/*SET THEORY. For set theory we treat epsilon as an infix predicate so xepsilon y is similar to x=y or x<y .
 * Then we treat union and intersection as infix functors so xuniony is similar to x+y or x.y .
 * 
 *  Note that at the moment we don't have as a matter of semantics that notMemberOf is the negation
 *  of memberOf ie they are independent infix operators
 *
 */

/*Jan 09 we will try to parse everything in one, whether it is ordinary first order, set
 * theory or what.
 * 
 * {old grammar for terms}
{<functor>::= a..z|0|1    }
{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)  }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}

{old grammar for SetTheory terms}
{<functor>::= a..z|0|1|2|3  |  } basically variables or constants
{<termprimary>::=(<term>)|<functor>|<setTheory constant eg empty set>|powerSet(term) }
{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> intersect <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> union <termtertiary>| <termsecondary>}

We will now smush these together (hopefully)

{new grammar for terms}
{<functor>::= a..z|0|1 + <setTheory constant eg empty set>  NOTE MUST INCLUDE PHI AND U AMONG CONSTANTS    }
{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)||powerSet(singleterm)  }
{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}
{<term4ary>::= <termtertiary> intersect <term4ary>| <termtertiary>}
 */

/*Note: U is a predicate, and U is also the universe set. So UU is a wff, and so too is PU etc.
 * 
 * This is not ideal, but it should not occur all that often.
 */

/*  This will also parse formulas with alternative logical symbols eg & for ^
 * 
 * 
 */

public class TParser{

/*

/******** end of  infix predicates **************

public static final String defaultFilter="[" + chBlank+"]"; // this is a regular expression for use in filtering input with replaceAll


public static final String lispFilterOut="[" + strCR+"]";   // changes a return to a blank which is a separator
public static final String lispFilterIn="[" + chBlank+"]";

*/
/* These formula types are 'import static' from TFormula

public static final short binary=1;
public static final short kons=2;
public static final short equality=3;
public static final short functor=4;
public static final short predicator=5;
public static final short quantifier=6;
public static final short unary=7;
public static final short variable=8;
public static final short typedQuantifier=9;
public static final short application=10;
public static final short lambda=11;
//public static final short bracketted=100;  // to get right association and for writing back
                                         // we add to other type  */
                                          
static final char EOF = (char)-1;
static final String CR = "\n";  // need to change this for cross platform


public static final String gConstants="abcdefghijklmnopqrstuv"+strEmptySet+strUniverseSet; //need to synch with TShapePanel

public static final String gSetTheoryConstants=strEmptySet+strUniverseSet;


public static final String gFunctors="abcdefghijklmnopqrstuvwxyz0123"+strEmptySet+strUniverseSet;
                                      /*zero-order functors a..l are constants, m..z are variables
                                      the binary predefineds like + are treated as special cases
                                      and there are the numerals 0,1,2; conceptually gFunctors is a
                                      set of char*/
public static final String gZeroFunctors=gConstants;
                                        /*zero-order functors a..l are constants, m..z are variables
                                        the binary predefineds like + are treated as special cases
                                        and there are the numerals 0,1,2; conceptually gFunctors is a
                                      set of char*/

public static final String gInfixFunctors= strMult+strXProd+strAdd+strMinus+
                                           strUnion+strIntersection;//+strCartesianProduct;

public static final String fVariables="wxyz";   //{take care if you change these as some procedures use}
                                        //   their indices, e.g. TFormula.firstfreevar}
public static final String gPredicates="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

public static final String topBottomPredicate=strDownTack+strUpTack;  // special zero ary predicates for true and false

//public static final String gConnectives=""+chAnd+chNeg+chUniquant+chOr+chExiquant+chImplic+chEquiv
//										+chModalPossible+chModalNecessary+chKappa+chRho;

public static final String gConnectives=negationChs+andChs+orChs+implicChs+equivChs+
			modalPossibleChs+modalNecessaryChs+memberOfChs+chKappa+chRho;
//changed June 2012

public static final String gLambdaNames="abcdefghijklmnopqrstuvwxyz"; // for lambda calculus
public static final String gLambdaConstant="CTF01234"+chImplic; // for lambda calculus

static String fPossibleWorlds = "klmnopqrstuvwxyzabcdefghij"+
                                    "0123456789";   // the indices for possible worlds, some parsers use numerals


static boolean ILLFORMED=false;
static boolean WELLFORMED=true;

String fErrors11= CR+ "(* is not a term.*)"+ CR;
String fErrors12= CR+ "(*Selection is illformed.*)"+ CR;
String fErrors13= CR+ "(* is not a constant.*)"+ CR;
String fErrors14= CR+ "(* is not a variable.*)"+ CR;

/*fields*/

CCParser fCCParser=null; //the grammar generated parser

public char fCurrCh=chBlank;
public char fLookAheadCh=chBlank;
public char fLookTwoAheadCh=chBlank;
private char [] fInputBuffer= {' ',' ',' '};  // three blanks

public Reader fInput;
public StringWriter fParserErrorMessage = new StringWriter();
public Writer fWrittenOutput;  // do I use this June 04?


public static String fLeftTruncate="*left*"; //"&lt;left&gt;"; for truncating wrapping formulas, unfortunately html is different
public static String fRightTruncate="*right*"; //"&lt;right&gt;"; for truncating wrapping formulas, unfortunately html is different
public static String fScopeTruncate= "*scope*";  //"&lt;scope&gt;"
public static String fTermTruncate= "*term*";
public static String fCompTruncate= "*{}*";
public static String fLongTruncate= "*long*";

private boolean fVerbose=false; // for writing out extra info

private boolean fFirstOrder=true;  //was false, changed JAN 09

                                        // types of Formula-- see also TTestNode for the succedent ones, eg atomicS 
public static final int absurd = 1; // this and following used to identify the kind of node
public static final int atomic = 2; // DO NOT ALTER THE ORDER OF THESE, WE RELY ON THE S
public static final int atomicS = 3; // CONSTANTS BEING THE SUCCESSOR OF THE ORDINARY ONES
public static final int negatomic = 4;
public static final int negatomicS = 5;
public static final int doubleneg = 6;
public static final int doublenegS = 7;
public static final int aand = 8;
public static final int aandS = 9;
public static final int negand = 10;
public static final int negandS = 11;
public static final int ore = 12;
public static final int oreS = 13;
public static final int nore = 14;
public static final int noreS = 15;
public static final int implic = 16;
public static final int arrowS = 17;
public static final int negarrow = 18;
public static final int negarrowS = 19;
public static final int equivv = 20;
public static final int equivvS = 21;
public static final int nequiv = 22;
public static final int nequivS = 23;
public static final int uni = 24;
public static final int uniS = 25;
public static final int uniSCV = 26;
public static final int uniSpec = 27;
public static final int neguni = 28;
public static final int neguniS = 29;
public static final int exi = 30;
public static final int exiS = 31;
public static final int exiCV = 32;
public static final int negexi = 33;
public static final int negexiS = 34;
public static final int unique = 35;
public static final int negunique = 36;
public static final int unknown = 37;
public static final int unknownS = 38;
public static final int startroot = 39;

public static final int modalNecessary = 40;
public static final int notNecessary = 41;
public static final int modalPossible = 42;
public static final int notPossible = 43;

public static final int typedUni = 44;
public static final int typedUniS = 45;
public static final int negTypedUni = 46;
public static final int negTypedUniS = 47;

public static final int typedExi = 48;
public static final int typedExiS = 49;
public static final int negTypedExi = 50;
public static final int negTypedExiS = 51;

public static final int epistKappa = 52;  // for epistemic logic
public static final int notKappa = 53;  // for epistemic logic

public static final int epistRho = 54;  // for epistemic logic
public static final int notRho = 55;  // for epistemic logic

public static final int modalDoubleKappa = 56;

public int fMinCellWidth=12; //sometimes the symbols are displayed in a table
                             // most symbol systems can display in 12, but
                             // some with arrows, eg Howson, Jeffrey, need 14


//public static final String fInputPalette=strNeg+strAnd+strOr+chImplic+chEquiv+chUniquant+chExiquant+chLambda+chMemberOf
//+chNotMemberOf+chUnion+chIntersection+chPowerSet+chSubset+chEmptySet; //supplies these to caller who want symbols                                             


/************************** constructors **********************************/

public TParser(){
//	fFirstOrder=TPreferences.firstOrder;   JAN09 not at the moment, always true is good?

}

public TParser(Reader aReader, boolean firstOrder)
      {
      fInput=aReader;
      fFirstOrder=firstOrder;
      }

/****************************** Undesirable characters in a formula **************************************/

public static final int kNone=-1;
public static final int kEquality=1;
public static final int kUnique=2;
public static final int kHighArity=3;
public static final int kCompoundTerms=4;


public void setVerbose (boolean verbose){
fVerbose=verbose;
}


public int badCharacters(TFormula root){

          /*{This is usually overriden to get formulas that satisfiable does not like. At present}
      { this is either equality or compound terms} */

        int toReturn=kNone;


        if (root==null)                    // none and none in subformula or subterms
          return
              kNone;
        else{

        if (isEquality(root))
          toReturn=
             kEquality;

        else{

          if (isUniqueQuant(root))
          toReturn=
             kUnique;

       else{

          if ((isPredicator(root))&&(root.arity()>2))
          toReturn=
             kHighArity;

       else{

          if ((isFunctor(root))&&(root.firstTerm()!=null))
          toReturn=
             kCompoundTerms;
       }
       }
        }

        }


        if (toReturn==kNone)
           toReturn= badCharacters(root.fLLink);

         if (toReturn==kNone)
           toReturn= badCharacters(root.fRLink);


        return
            toReturn;
        }

public boolean containsModalOperator(TFormula root){

	if (isModalPossible(root) ||
	   isModalNecessary(root) ||
	   isModalKappa(root)     ||
	   isModalRho(root))
	  return
	      true;

	switch (root.fKind) {

	  case predicator:
	  case functor:
	  case variable:
	  case equality:
	    return
	        false;
	  case unary:
	  case quantifier:
	  case typedQuantifier:
	  case lambda:
	    return
	        containsModalOperator(root.fRLink);

	  case binary:
	  case application:
	//   case application+bracketted:
	    return
	        containsModalOperator(root.fLLink) ||
	        containsModalOperator(root.fRLink);

	      default:
	          return false;
	}
	}

	public boolean containsModal(String aString){

	  if ((aString!=null)&&(aString.length()!=0)){

	      for (int i=0;i<aString.length();i++){
	        if (isModalPossibleCh(aString.charAt(i))||
	            isModalNecessaryCh(aString.charAt(i))||
	            isModalKappaCh(aString.charAt(i))	||
	            isModalRhoCh(aString.charAt(i)))



	        return
	            true;
	      }
	    }
	return
	  false;
	  }
	
	public boolean containsSubscripts(String aString){

		  if ((aString!=null)&&(aString.length()!=0)){

		      for (int i=0;i<aString.length();i++){
		        if ((aString.charAt(i)==chSubscript0)||
		        		(aString.charAt(i)==chSubscript1)||
		        		(aString.charAt(i)==chSubscript2)||
		        		(aString.charAt(i)==chSubscript3)||
		        		(aString.charAt(i)==chSubscript4)||
		        		(aString.charAt(i)==chSubscript5)||
		        		(aString.charAt(i)==chSubscript6)||
		        		(aString.charAt(i)==chSubscript7)||
		        		(aString.charAt(i)==chSubscript8)||
		        		(aString.charAt(i)==chSubscript9))
		        return
		            true;
		      }
		    }
		return
		  false;
		  }

/**************************** new Jan 09 ******************************************/
	
public String getInputPalette(boolean lambda,boolean modal,
		boolean setTheory){

	//strNeg+strAnd+strOr+chImplic+chEquiv+chUniquant+chExiquant+chLambda+chMemberOf
	//+chNotMemberOf+chUnion+chIntersection+chPowerSet+chSubset+chEmptySet;
	
	return
		renderNot()+
		renderAnd()+
		renderOr()+
		renderImplic()+
		renderEquiv()+
		renderUniquant()+
		renderExiquant()+
//		chTherefore+
		
		(lambda?       //covered by parameters passed in
			chDoubleArrow:chTherefore )+

		
		(/*TPreferences.fL*/lambda?       //covered by parameters passed in
				(renderLambda()):"" )+
		
		(/*TPreferences.fM*/modal?
				(renderModalPossible()+
				renderModalNecessary()+
				renderModalKappa()+
				renderModalRho()
				):"" )+
		
		(/*TPreferences.fS*/setTheory?
		(renderMemberOf()+
		renderNotMemberOf()+
		renderUnion()+
		renderIntersection()+
		renderPowerSet()+
		renderSubset()+
		renderEmptySet()+
		renderUniverseSet()):"" );
	
}
	
/**************************** new Dec 08 ******************************************/
	
public char firstFreeVar(TFormula theFormula){  //need to revise
		  TFormula chForm= new TFormula();
		 // String gVariables=TParser.gVariables;

		  boolean found=false;
		  int i=0;

		  chForm.fKind = variable;

		  while ((!found)&&(i<fVariables.length())){
		    chForm.fInfo=fVariables.substring(i,i+1);

		    if (theFormula.freeTest(chForm))
		      found=true;
		    else
		      i++;
		  }

		  if (found)
		    return
		      chForm.fInfo.charAt(0);
		   else

		  return
		      chBlank;
		}	
	
public static boolean freeInterpretFreeVariables(ArrayList interpretation){

    /*This will do surgery on a list of atomic formulas, say Fx, Gy and change the free varaibles
    into arbitrary constants eg Fa, Gb */


    /* What we have here is a consistent list of positive and negative atomic formulas,
     which are true, we need
       to pull out the atomic terms and make them the universe, then interpret the  predicates and relations
   suitably
  But they may have free variables. Any new constant will do here*/

  //  String universe=TFormula.atomicTermsInListOfFormulas(interpretation);
  
    Set <String> universeStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
    
    String universe="";
    
    for (Iterator i=universeStrSet.iterator();i.hasNext();)
    	universe+=i.next();    
    
    
    
    
    
    char searchCh;
    char constant;
    ArrayList valuation=new ArrayList();
    int n=1;
    TFormula valuForm;

    for (int i=0;i<universe.length();i++){

      searchCh=universe.charAt(i);

      if (TParser.isVariable(searchCh)){
       // constant=TParser.nthNewConstant(n,universe);
    	  constant=nthNewConstant(n,universe);
        n+=1;
        if (constant==' ')
          return
              false;
        else{
             valuForm=new TFormula((short)0,constant +"/" + searchCh,null,null);
             /*the info on a valuation looks like this "a/x"
                and we want to substitute the constant a for the variable
                x throughout the formula*/
             valuation.add(valuForm);
        }
      }

    }

    if (valuation.size()>0)
      TFormula.interpretFreeVariables(valuation, interpretation);  //surgery

   return
       true;
}	
	
	
/**************************** End of new Dec 09 ******************************************/	
	
	

/**************************** writing out ******************************************/

public String renderAnd(){
 return
     String.valueOf(chAnd);
       }
       public String renderEquiv(){
          return
              String.valueOf(chEquiv);
       }
       
       public String renderEmptySet(){
           return
               String.valueOf(chEmptySet);
        }      
       
       public String renderEquals(){
           return
               String.valueOf(chEquals);
        }
       public String renderImplic(){
          return
              String.valueOf(chImplic);
       }
       public String renderMemberOf(){
           return
               String.valueOf(chMemberOf);
        }
       public String renderNotMemberOf(){
           return
               String.valueOf(chNotMemberOf);
        }   
       public String renderSubset(){
           return
               String.valueOf(chSubset);
        }
       
       public String renderUniverseSet(){
           return
               String.valueOf(chUniverseSet);
        }       
       
       public String renderNot(){
 return
     String.valueOf(chNeg);
}


       public String renderOr(){
          return
              String.valueOf(chOr);
       }
 
       public String renderPowerSet(){
           return
               String.valueOf(chPowerSet);
        }
       
       public String renderExiquant(){
          return
              String.valueOf(chExiquant);
       }
 
       public String renderComplement(){
           return
               String.valueOf(chMinus);
        }      
       public String renderIntersection(){
           return
               String.valueOf(chIntersection);
        } 
   
       public String renderLambda(){
           return
               String.valueOf(chLambda);
        }       
       public String renderModalPossible(){
           return
               String.valueOf(chModalPossible);
        }       
       
       public String renderModalNecessary(){
           return
               String.valueOf(chModalNecessary);
        }       
       
       public String renderModalKappa(){
           return
               String.valueOf(chKappa);
        } 
       
       public String renderModalRho(){
           return
               String.valueOf(chRho);
        } 
       
       public String renderUnion(){
           return
               String.valueOf(chUnion);
        }       
       public String renderUniquant(){
 return
     String.valueOf(chUniquant);
}

       public String renderXProd(){
           return
               String.valueOf(chXProd);
        } 
       

public String toInternalForm(char connective){
/*we have only one internal 'canonical' form for each connective but translate
back and forth when reading or writing*/

if (isAndCh(connective))         //accepts variations
    return
     String.valueOf(chAnd);    //standard form

if (isOrCh(connective))         //accepts variations
    return
     String.valueOf(chOr);    //standard form


if (isNegationCh(connective))         //accepts variations
     return
      String.valueOf(chNeg);    //standard form

if (isImplicCh(connective))         //accepts variations
 return
     String.valueOf(chImplic);    //standard form

if (isEquivCh(connective))         //accepts variations
  return
     String.valueOf(chEquiv);    //standard form

if (isMemberOfCh(connective))         //accepts variations
	  return
	  String.valueOf(chMemberOf);    //standard form


  return
      String.valueOf(connective);
       }


/***************************** type, see also TTestNode **********************************/

public static int  typeOfFormula(TFormula aFormula){

         int  returnType=unknown;


  /*       if (aFormula==null)
           return
               unknown; */


         switch (aFormula.fKind){

           case (TFormula.unary):  //negation (or modalNecessary modalPossible
             if (isModalNecessary(aFormula))
               return
                   modalNecessary;
             if (isModalPossible(aFormula))
               return
                   modalPossible;
             if (isModalKappa(aFormula))
                 return
                     modalKappa;
             if (isModalRho(aFormula))
                 return
                     modalRho;

             if (isNegation(aFormula)) {

               if (isModalNecessary(aFormula.fRLink))
                 return
                     notNecessary;
               if (isModalPossible(aFormula.fRLink))
                 return
                     notPossible;
               if (isModalKappa(aFormula.fRLink))
                   return
                       notKappa;
               if (isModalRho(aFormula.fRLink))
                   return
                       notRho;
             }

             switch (typeOfFormula(aFormula.fRLink)) {
               case atomic:
                 return
                     negatomic;
               case negatomic:
               case doubleneg:
               case negarrow:
               case negand:
               case nore:
               case nequiv:
               case neguni:
               case negexi:
               case notNecessary:
               case notPossible:
               case notKappa:
               case notRho:
                 return
                     doubleneg;
               case implic:
                 return
                     negarrow;
               case aand:
                 return
                     negand;
               case ore:
                 return
                     nore;
               case equivv:
                 return
                     nequiv;
               case uni:
                 return
                     neguni;
               case exi:
                 return
                     negexi;
               case unique:
                 return
                     negunique;
               case typedUni:
                 return
                     negTypedUni;
               case typedExi:
                 return
                     negTypedExi;

               default:
                 return
                     unknown;

             }
             


             case TFormula.binary:  {
               if (isImplic(aFormula))
                  return
                     implic;
               else
               if (isAnd(aFormula))
                  return
                     aand;
               else
               if (isOr(aFormula))
                  return
                     ore;
               else
               if (isEquiv(aFormula))
                  return
                     equivv;
               }

             case TFormula.quantifier:{
               if (isUniquant(aFormula))
                  return
                     uni;
               else
               if (isExiquant(aFormula))
                  return
                     exi;
               else
               if (isUnique(aFormula))
                  return
                     unique;

             }

             case TFormula.typedQuantifier:{
         if (isTypedUniquant(aFormula))
            return
               typedUni;
         else
         if (isTypedExiquant(aFormula))
            return
               typedExi;


       }

         }




          return
             atomic;
}



/********************  we sometimes expand and contract sorted formulas ********************************/

public TFormula contractTypeExi(TFormula root){

 if (isExiquant(root) &&
     isAnd(root.scope()) &&
     isMonadicPredicateWithVar(root.scope().getLLink())) { // This unabbreviates (Exists m:t)p to (Exists m)(Tm and p)

   TFormula p, m, Tm, newRoot;

   p = root.scope().fRLink.copyFormula();
   m = root.quantVarForm().copyFormula();
   Tm = root.scope().getLLink().copyFormula();

   if (m.equalFormulas(m, Tm.firstTerm())) {

     TFormula typeNode = new TFormula(TFormula.functor, Tm.fInfo.toLowerCase(), null, null);

     TFormula head = new TFormula(TFormula.kons,
                                  "",
                                  m,
                                  null);

     head.appendToFormulaList(typeNode);

     newRoot = new TFormula(typedQuantifier,
                             String.valueOf(chExiquant),
                             head,
                             p
         );
     return
         newRoot;
   }
 }
 return
     null;
}


public TFormula expandTypeExi(TFormula root){

//This unabbreviates (Exists m:t)p to (Exists m)(Tm and p)

if (isTypedExiquant(root)) {

  TFormula p,m,Tm,newRoot;
  char type;

       p=root.scope().copyFormula();
       m=root.quantVarForm().copyFormula();
       
       
       
 //mfmfmfm Dec 13 09
       
       TFormula typeForm=root.quantTypeForm();
       
       //might be functor or abstraction
       
       if (typeForm.fKind==comprehension){
    	   
    	 //(Exists m:{x:&Phi;[x]})<scope> to //(Exists m)(&Phi;[m] &scope) 
    	   
    	   TFormula xVar=typeForm.fLLink;
    	   TFormula phi=typeForm.fRLink;
    	   
    	   TFormula phiMforX=phi.copyFormula();
    	   phiMforX.subTermVar(phiMforX,m.copyFormula(),xVar);
    	   
           newRoot = new TFormula(quantifier,
                   String.valueOf(chExiquant),
                   m,
                   new TFormula(binary,
                                String.valueOf(chAnd),
                                phiMforX,
                                p)
                   );
    	   
    	   
           return
              newRoot;	     	   
       }
       
       
 //mfmfmf      
       
       
       
       
       type=root.quantType();

       if (type!=chBlank){

         Tm = new TFormula(predicator,
                          String.valueOf(type).toUpperCase(),
                          null,
                          null);
         Tm.appendToFormulaList(m.copyFormula());      //Tm


         newRoot = new TFormula(quantifier,
                                 String.valueOf(chExiquant),
                                 m,
                                 new TFormula(binary,
                                              String.valueOf(chAnd),
                                              Tm,
                                              p)
                                 );

         return
             newRoot;
       }
}

return
    null;

}

public TFormula contractTypeUni(TFormula root){

if (isUniquant(root) &&
    isImplic(root.scope()) &&
    isMonadicPredicateWithVar(root.scope().getLLink())) { // (All m:t)p :: (All m)(Tm implic p)

  TFormula p, m, Tm, newRoot;

  p = root.scope().fRLink.copyFormula();
  m = root.quantVarForm().copyFormula();
  Tm = root.scope().getLLink().copyFormula();

  if (m.equalFormulas(m, Tm.firstTerm())) {

    TFormula typeNode = new TFormula(TFormula.functor, Tm.fInfo.toLowerCase(), null, null);

    TFormula head = new TFormula(TFormula.kons,
                                 "",
                                 m,
                                 null);

    head.appendToFormulaList(typeNode);

    newRoot = new TFormula(typedQuantifier,
                            String.valueOf(chUniquant),
                            head,
                            p
        );
    return
        newRoot;
  }
}
return
    null;
}
public TFormula expandTypeUni(TFormula root){

//This unabbreviates (All m:t)p to (All m)(Tm implic p)

if (isTypedUniquant(root)) {  // (All m:t)p :: (All m)(Tm implic p)

  TFormula p,m,Tm,newRoot;
  char type;

       p=root.scope().copyFormula();
       m=root.quantVarForm().copyFormula();
       
//mfmfmfm Dec 13 09
       
       TFormula typeForm=root.quantTypeForm();
       
       //might be functor or abstraction
       
       if (typeForm.fKind==comprehension){
    	   
    	 //(Exists m:{x:&Phi;[x]})<scope> to //(Exists m)(&Phi;[m] &scope) 
    	   
    	   TFormula xVar=typeForm.fLLink;
    	   TFormula phi=typeForm.fRLink;
    	   
    	   TFormula phiMforX=phi.copyFormula();
    	   phiMforX.subTermVar(phiMforX,m.copyFormula(),xVar);
    	   

           newRoot = new TFormula(quantifier,
                   String.valueOf(chUniquant),
                   m,
                   new TFormula(binary,
                                String.valueOf(chImplic),
                                phiMforX,
                                p)
                   );
    	   
           return
              newRoot;	     	   
       }
       
       
 //mfmfmf     
       
       
       
       
       
       
       type=root.quantType();

       if (type!=chBlank){

         Tm = new TFormula(predicator,
                          String.valueOf(type).toUpperCase(),
                          null,
                          null);
         Tm.appendToFormulaList(m.copyFormula());      //Tm


         newRoot = new TFormula(quantifier,
                                 String.valueOf(chUniquant),
                                 m,
                                 new TFormula(binary,
                                              String.valueOf(chImplic),
                                              Tm,
                                              p)
                                 );

         return
             newRoot;
       }
}

return
    null;

}

/********************** boolean formula identifiers ************************************************/

public static boolean isAnd(TFormula root){    //should not be static because of different parsers
                                      // No! because we use the same internal representation


        if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chAnd))
          return
              true;
        else
          return
              false;
      }

public boolean isAndCh(char aChar){

 return
    ((aChar == chAnd) || (andChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}


public boolean isApplication(TFormula root){


                if (root.fKind==application)
                  return
                      true;
                else
                  return
                      false;
      }


public static boolean isAtomic(TFormula root){    //should not be static because of different parsers
    // No! because we use the same internal representation

if ((root.fKind==equality)||(root.fKind==predicator))
   return
      true;
else
   return
      false;
}

public static boolean isAtomicOrNegAtomic(TFormula root){    //should not be static because of different parsers
    // No! because we use the same internal representation

if (isAtomic(root)||(isNegation(root)&&isAtomic(root.fRLink)))
   return
      true;
else
   return
      false;
}



public boolean isLambda(TFormula root){


                        if (root.fKind==lambda)
                          return
                              true;
                        else
                          return
                              false;
              }




public static boolean isImplic(TFormula root){


                if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chImplic))
                  return
                      true;
                else
                  return
                      false;
              }


public boolean isImplicCh(char aChar){

                 return
                    ((aChar == chImplic) || (implicChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}


public static boolean isNegation(TFormula root){

              return
                  ((root.fKind == unary) && (root.fInfo.charAt(0)==chNeg));

                      }

public boolean isNegationCh(char aChar){

  return
     ((aChar == chNeg) || (negationChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}




public static boolean isDoubleNegation(TFormula root){

              return
                 ((isNegation(root)) && (isNegation(root.fRLink)));

                      }

public static boolean isMemberOfCh(char aChar){

	   return
	 ((aChar == chMemberOf) || (memberOfChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

	}

public static boolean isMemberOfStr(String aStr){

	   return
	 (memberOfChs.indexOf(aStr)>-1);   // we allow the parser to 'softly' parse alternative negations

	}

public static boolean isModalPossibleCh(char aChar){

 return
((aChar == chModalPossible) || (modalPossibleChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}
public static boolean isModalNecessaryCh(char aChar){

   return
 ((aChar == chModalNecessary) || (modalNecessaryChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}

public static boolean isModalKappaCh(char aChar){

	   return
	 ((aChar == chKappa) );   // we allow the parser to 'softly' parse alternative negations

	}

public static boolean isModalRhoCh(char aChar){

	   return
	 ((aChar == chRho) );   // we allow the parser to 'softly' parse alternative negations

	}

public static boolean isModalPossible(TFormula root){

                return
                    ((root.fKind == unary) && (root.fInfo.charAt(0)==chModalPossible));

                      }

public static boolean isModalNecessary(TFormula root){

                return
((root.fKind == unary) && (root.fInfo.charAt(0)==chModalNecessary));

                                           }

public static boolean isModalKappa(TFormula root){

    return
((root.fKind == modalKappa) && (root.fInfo.charAt(0)==chKappa));

                               }

public static boolean isModalRho(TFormula root){

    return
((root.fKind == modalRho) && (root.fInfo.charAt(0)==chRho));

                               }

public static boolean isNotMemberOf(TFormula root){

    return
        ((root.fKind == unary) && 
         (root.fRLink.fInfo.equals(strMemberOf)));

            }




public static boolean isOr(TFormula root){


        if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chOr))
          return
              true;
        else
          return
              false;
      }

public boolean isOrCh(char aChar){

         return
            ((aChar == chOr) || (orChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

        }



public static boolean isPredicator(TFormula root){


                if (root.fKind==predicator)
                  return
                      true;
                else
                  return
                      false;
              }



public static boolean isPossibleWorld(String world){


if ((world!=null)&&
    (world.length()==1&&
    (fPossibleWorlds.indexOf(world))>-1))
  return
      true;
else
  return
      false;
}


public String firstNewWorld(String oldWorlds){

  //we will return the first world not in this list

  if (oldWorlds!=null){

    for (int i = 0; i < fPossibleWorlds.length(); i++) {
      if (oldWorlds.indexOf(fPossibleWorlds.substring(i, i + 1)) < 0) //not there
        return
            fPossibleWorlds.substring(i, i + 1);
    }
  }

  return
            fPossibleWorlds.substring(0, 0 + 1);

}



public static boolean isMonadicPredicateWithVar(TFormula root){


if (root.fKind==predicator){

TFormula var=root.nthFormula(1,root);

if (var!=null&&
    root.nthFormula(2,root)==null&&
    isVariable(var));  //not two


return
    true;
}
                                else
                                  return
                                      false;
              }




              public boolean isFunctor(TFormula root){


                                if (root.fKind==functor)
                                  return
                                      true;
                                else
                                  return
                                      false;
                              }


public static   boolean isEquality(TFormula root){


                        if (root.fKind==equality)
                          return
                              true;
                        else
                          return
                              false;
}


public static boolean isEquiv(TFormula root){


                if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chEquiv))
                  return
                      true;
                else
                  return
                      false;
              }

public boolean isEquivCh(char aChar){

                                 return
                                    ((aChar == chEquiv) || (equivChs.indexOf(aChar)>-1));   // we allow the parser to 'softly' parse alternative negations

}

public static boolean isPowerSet(TFormula root){


    if ((root.fKind==functor)&&(root.fInfo.charAt(0)==chPowerSet))
      return
          true;
    else
      return
          false;
  }

public static boolean isSubset(TFormula root){


    if ((root.fKind==predicator)&&(root.fInfo.charAt(0)==chSubset))
      return
          true;
    else
      return
          false;
  }

public static boolean isMemberOf(TFormula root){


    if ((root.fKind==predicator)&&(root.fInfo.charAt(0)==chMemberOf))
      return
          true;
    else
      return
          false;
  }

public static boolean isPair(TFormula root){


    if (root.fKind==pair)
      return
          true;
    else
      return
          false;
  }

public static boolean isUnion(TFormula root){


    if ((root.fKind==functor)&&(root.fInfo.charAt(0)==chUnion))
      return
          true;
    else
      return
          false;
  }

public static boolean isIntersection(TFormula root){


    if ((root.fKind==functor)&&(root.fInfo.charAt(0)==chIntersection))
      return
          true;
    else
      return
          false;
  }

public static boolean isComprehension(TFormula root){


    if (root.fKind==comprehension)
      return
          true;
    else
      return
          false;
  }
public static boolean isComplement(TFormula root){


    if ((root.fKind==functor)&&(root.fInfo.charAt(0)==chMinus))
      return
          true;
    else
      return
          false;
  }

public static boolean isUniquant(TFormula root){

 if ((root.fKind==quantifier)&&(root.fInfo.charAt(0)==chUniquant))
    return
       true;
 else
    return
       false;
}

public boolean isUniquantCh(char aChar){
return
 (aChar == chUniquant);   // we allow the parser to 'softly' parse alternative negations

}

public boolean isExiquantCh(char aChar){
return
 (aChar == chExiquant);   // we allow the parser to 'softly' parse alternative negations

}


public static boolean isTypedExiquant(TFormula root){

if ((root.fKind==typedQuantifier)&&(root.fInfo.charAt(0)==chExiquant))
         return
            true;
      else
         return
            false;
}

public static boolean isTypedUniquant(TFormula root){

if ((root.fKind==typedQuantifier)&&(root.fInfo.charAt(0)==chUniquant))
          return
             true;
       else
          return
             false;
}


public boolean isUniqueQuant(TFormula root){


                                                if ((root.fKind==quantifier)&&(root.fInfo.charAt(0)==chUnique))
                                                  return
                                                      true;
                                                else
                                                  return
                                                      false;
                                              }




public static boolean isExiquant(TFormula root){


                                                if ((root.fKind==quantifier)&&(root.fInfo.charAt(0)==chExiquant))
                                                  return
                                                      true;
                                                else
                                                  return
                                                      false;
                                              }

public static boolean isUnique(TFormula root){

if ((root.fKind==quantifier)&&(root.fInfo.charAt(0)==chUnique))
 return
                                                                              true;
                                                                        else
                                                                          return
                                                                              false;
                                                                      }


public static boolean isXProd(TFormula root){


    if ((root.fKind==functor)&&(root.fInfo.charAt(0)==chXProd))
      return
          true;
    else
      return
          false;
  }




boolean functorInfix (String inf)
      {
      return (inf.equals(strMult) || 
    		  inf.equals(strAdd)|| 
    		  inf.equals(strMinus));
      }

boolean functorPostfix (String inf)
      {
      return (inf.equals(strSucc));
      }

/********* These are the binary infixes in order of precedence **********/

boolean infixBinFun2 (char ch) //infixbinaryfunctionsecondary
      {
        return  (ch==chMult||
        		 ch==chXProd);
      }

boolean infixBinFun3 (char ch) //infixbinaryfunctiontertiary
      {
        return  (ch==chAdd)||
                (ch==chMinus);
      }

boolean infixBinFun4 (char ch) //infixbinaryfunction4ary
{
  return  (ch==chIntersection);
}

boolean infixBinFun5 (char ch) //infixbinaryfunction5ary
{
  return  (ch==chUnion);
}

/******** These are the binary infixes in order of precedence ***********/





/*static*/ public boolean isFunctorInfix (char ch)   // probably should be in parser
         {
         return 
            ((gInfixFunctors.indexOf((int)ch)!=-1));
         
       //  ((ch==chMult) || (ch==chAdd));
         }

/*
  boolean isPredInfix (String inf)
         {
         return ((inf.equals(TConstants.kEquals)) || (inf.equals(TConstants.kLessThan)));
      }

*/


boolean isFunctorPostfix (char ch) //infixbinaryfunctiontertiary
 {
                return  (ch==chSucc);
      }



      public /*static*/ boolean isConnective (char ch){
         return
             ((gConnectives.indexOf((int)ch)!=-1));
      }



public static boolean isConstant (char ch){
 return
     ((gConstants.indexOf((int)ch)!=-1));
}

public boolean isContradiction (TFormula root){
 if (root.equalFormulas(root,TFormula.fAbsurd))
   return
       true;

 if (isAnd(root)&&
     TFormula.formulasContradict(root.getLLink(),root.getRLink()))
   return
       true;
  return
      false;
}




public static boolean isAtomicConstant (TFormula root)
  {
  if ((root.fKind==functor)&&
  (root.fLLink==null)&&
  (root.fRLink==null))
                   return
                       true;
                 else
                   return
                       false;
      }



/*************  Access relations for possible worlds ************************************************/

      /* an Access relation has special meaning the the modal logic, etrees */

public TFormula makeAnAccessRelation(String world1,String world2){      /*predicate Access(w1,w2)*/
         TFormula newnode = new TFormula();  // lot of running down the end in this one


         newnode.fKind = predicator;
         newnode.fInfo = "A";

         TFormula termnode = new TFormula();
         termnode.fInfo = "c";
         termnode.fKind = functor;
         newnode.append(termnode);

         termnode = new TFormula();
         termnode.fInfo = "c";
         termnode.fKind = functor;
         newnode.append(termnode);

         termnode = new TFormula();
         termnode.fInfo = "e";
         termnode.fKind = functor;
         newnode.append(termnode);

         termnode = new TFormula();
         termnode.fInfo = "s";
         termnode.fKind = functor;
         newnode.append(termnode);

         termnode = new TFormula();
         termnode.fInfo = "s";
         termnode.fKind = functor;

         {TFormula world=new TFormula();
         world.fInfo = (world1.length()>0)?world1.substring(0,1):"?";
         world.fKind = functor;

         termnode.appendToFormulaList(world);

         world=new TFormula();
            world.fInfo = (world2.length()>0)?world2.substring(0,1):"?";
            world.fKind = functor;

         termnode.appendToFormulaList(world);
         newnode.append(termnode);
         }
         return
             newnode;
       }


public String getAccessRelation(TFormula root){      /*predicate Access(w1,w2)*/

      String outStr="";

        if (root.fKind == predicator&&"A".equals(root.fInfo)){

         TFormula accessTerm= root.nthTopLevelTerm(5);

         if (accessTerm!=null){
           TFormula temp = accessTerm.nthTopLevelTerm(1);
           TFormula temp2 = accessTerm.nthTopLevelTerm(2);

           if (temp!=null&&temp2!=null){
             outStr=outStr+temp.fInfo+temp2.fInfo;

             if (outStr.length()==2)
                return outStr;
             else
                return "";
           }
         }
        }

       return
           outStr;
      }

public boolean isAccessRelation(TFormula root){
        return
            !(getAccessRelation(root).equals(""));
}


public String startWorld(){   // for premises of tree etc.
return
    "n";
}


/********************** end of Access relation **************************/

/********************** EAccess relation for epistemic logic**************************/

public TFormula makeAnEAccessRelation(String person, String world1,String world2){      /*predicate Access(w1,w2)*/
    TFormula newnode = new TFormula();  // lot of running down the end in this one


    newnode.fKind = predicator;
    newnode.fInfo = "E";
    
    TFormula termnode = new TFormula();
    termnode.fInfo = "a";
    termnode.fKind = functor;
    newnode.append(termnode);

    termnode = new TFormula();
    termnode.fInfo = "c";
    termnode.fKind = functor;
    newnode.append(termnode);

    termnode = new TFormula();
    termnode.fInfo = "c";
    termnode.fKind = functor;
    newnode.append(termnode);

    termnode = new TFormula();
    termnode.fInfo = "e";
    termnode.fKind = functor;
    newnode.append(termnode);

    termnode = new TFormula();
    termnode.fInfo = "s";
    termnode.fKind = functor;
    newnode.append(termnode);

    termnode = new TFormula();
    termnode.fInfo = "s";
    termnode.fKind = functor;

    {TFormula agent=new TFormula();
    agent.fInfo = (person.length()>0)?person.substring(0,1):"?";
    agent.fKind = functor;

    termnode.appendToFormulaList(agent);
    
    TFormula world=new TFormula();
    world.fInfo = (world1.length()>0)?world1.substring(0,1):"?";
    world.fKind = functor;

    termnode.appendToFormulaList(world);

    world=new TFormula();
       world.fInfo = (world2.length()>0)?world2.substring(0,1):"?";
       world.fKind = functor;

    termnode.appendToFormulaList(world);
    newnode.append(termnode);
    }
    return
        newnode;
  }


public String getEAccessRelation(TFormula root){      /*predicate Access(w1,w2)*/

 String outStr="";

   if (root.fKind == predicator&&"E".equals(root.fInfo)){

    TFormula accessTerm= root.nthTopLevelTerm(6);

    if (accessTerm!=null){
      TFormula temp = accessTerm.nthTopLevelTerm(1);
      TFormula temp2 = accessTerm.nthTopLevelTerm(2);
      TFormula temp3 = accessTerm.nthTopLevelTerm(3);

      if (temp!=null&&temp2!=null&&temp2!=null){
        outStr=outStr+temp.fInfo+temp2.fInfo+temp3.fInfo;

        if (outStr.length()==3)
           return outStr;
        else
           return "";
      }
    }
   }

  return
      outStr;
 }

public boolean isEAccessRelation(TFormula root){
   return
       !(getAccessRelation(root).equals(""));
}


/********************** end of Access relation **************************/





/************************ From TFormula *********************************/

public static String constantsInFormula(TFormula aFormula){   /*has JUnit Test */
	   String leftStr=strNull;
	   String rightStr=strNull;


	   if (aFormula.isSpecialPredefined())
	     return
	         strNull;

	   if (aFormula.getLLink()!=null)
	     leftStr= constantsInFormula(aFormula.getLLink());
	   if (aFormula.getRLink()!=null)
		     rightStr= constantsInFormula(aFormula.getRLink());

	   if ((aFormula.getLLink()==null)&&(aFormula.getRLink()==null)){

	     if (aFormula.getInfo().length()>0){

	       if (isConstant(aFormula.getInfo().charAt(0)))
	          leftStr = aFormula.getInfo();
	          //not sure of the next bit because isn't the length 1?

	     // I'M LEAVING IT OUT FOR NOW

	     }
	   }

	   if ((leftStr.length()>0)&&(rightStr.length()>0)){  //remove duplicates
	     for (int i = 0; i < rightStr.length(); i++)
	       if (leftStr.indexOf(rightStr.charAt(i))==-1) //not there yet
	         leftStr=leftStr+rightStr.charAt(i);

	     return
	         leftStr;

	   }

	return
	       leftStr+rightStr;

	}


//Dec 09 Need to update this. Does not take account of subscripts

public static Set <String> variablesInFormula(TFormula aFormula){  /*has JUnit Test*/ 
	Set<String> s = new TreeSet<String>();
	
	String leftStr=strNull;
	  String rightStr=strNull;


	  if (aFormula.isSpecialPredefined())
	    return
	        s;

	   if (aFormula.getLLink()!=null)
		   if (s.addAll(variablesInFormula(aFormula.getLLink())))
			   ;
	   if (aFormula.getRLink()!=null)
		   if (s.addAll(variablesInFormula(aFormula.getRLink())))
			   ;

	   if ((aFormula.fKind==variable))
	     if (aFormula.getInfo().length()>0)
	    	 if (s.add(aFormula.getInfo()))
	    		 ;
	return
	      s;
	}

 /*  public static String [] variablesInFormula(TFormula aFormula){  /*has JUnit Test 
	  String leftStr=strNull;
	  String rightStr=strNull;


	  if (aFormula.isSpecialPredefined())
	    return
	        strNull;

	   if (aFormula.getLLink()!=null)
		     leftStr= variablesInFormula(aFormula.getLLink());
	   if (aFormula.getRLink()!=null)
		     rightStr= variablesInFormula(aFormula.getRLink());

	   if ((aFormula.getLLink()==null)&&(aFormula.getRLink()==null)){

	     if (aFormula.getInfo().length()>0){

	       if (isVariable(aFormula.getInfo().charAt(0)))
	          leftStr = aFormula.getInfo();
	          //not sure of the next bit because isn't the length 1?

	     // I'M LEAVING IT OUT FOR NOW

	     }
	   }

	  if ((leftStr.length()>0)&&(rightStr.length()>0)){  //remove duplicates
	    for (int i = 0; i < rightStr.length(); i++)
	      if (leftStr.indexOf(rightStr.charAt(i))==-1) //not there yet
	        leftStr=leftStr+rightStr.charAt(i);

	    return
	        leftStr;

	  }

	return
	      leftStr+rightStr;

	} */

	public static String lambdaNamesInFormula(TFormula aFormula){  /*has JUnit Test*/
		  String leftStr=strNull;
		  String rightStr=strNull;


		  if (aFormula.isSpecialPredefined())
		    return
		        strNull;

		   if (aFormula.getLLink()!=null)
			     leftStr= lambdaNamesInFormula(aFormula.getLLink());
		   if (aFormula.getRLink()!=null)
			     rightStr= lambdaNamesInFormula(aFormula.getRLink());

		   if ((aFormula.getLLink()==null)&&(aFormula.getRLink()==null)){

		     if (aFormula.getInfo().length()>0){

		       if (isLambdaName(aFormula.getInfo().charAt(0)))
		          leftStr = aFormula.getInfo();
		          //not sure of the next bit because isn't the length 1?

		     // I'M LEAVING IT OUT FOR NOW

		     }
		   }

		  if ((leftStr.length()>0)&&(rightStr.length()>0)){  //remove duplicates
		    for (int i = 0; i < rightStr.length(); i++)
		      if (leftStr.indexOf(rightStr.charAt(i))==-1) //not there yet
		        leftStr=leftStr+rightStr.charAt(i);

		    return
		        leftStr;

		  }

		return
		      leftStr+rightStr;

		}


	 public static String constantsInListOfFormulas(ArrayList listOfFormulas ){ // returns empty string not null for none  /*has JUnit Test

	    String outputStr="";


	    if (listOfFormulas != null) {
	      Iterator iter = listOfFormulas.iterator();

	      while (iter.hasNext()) {
	        outputStr = outputStr + constantsInFormula(( (TFormula) iter.next()));

	      }

	      if (outputStr.length()>1){

	        outputStr = TUtilities.removeDuplicateChars(outputStr);

	        if (outputStr.length()>1){
	          char[] forSort = outputStr.toCharArray();

	          Arrays.sort(forSort);

	          outputStr = new String(forSort);
	        }
	      }

	    }

	    return
	        outputStr;

	}





/***********************************************************************/


public static TFormula newConstant(ArrayList formulas, ArrayList more){
String constants = "";
String constants2 = "";


if (formulas!=null)
  constants=constantsInListOfFormulas(formulas);

if (more!=null){
  constants2 = constantsInListOfFormulas(more);

  constants+=constants2;
}

char newCons= nthNewConstant(1,constants);

if (newCons!=' ')

return
    new TFormula(TFormula.functor,
    String.valueOf(nthNewConstant(1,constants)),
    null,
    null);

else
  return
      null;

}


public static char nthNewConstant(int n,String inHere){
char searchCh;
for (int i=0;i<gConstants.length();i++){
  searchCh=gConstants.charAt(i);
  if (inHere.indexOf(searchCh)==-1){
    if (n==1)
       return
         searchCh;
    else
       n-=1;
  }
}
 return
     ' ';
}

public static char nthNewLambdaName(int n,String notInHere){
char searchCh;
for (int i=0;i<gLambdaNames.length();i++){
  searchCh=gLambdaNames.charAt(i);
  if (notInHere.indexOf(searchCh)==-1){
    if (n==1)
       return
         searchCh;
    else
       n-=1;
  }
}
 return
     ' ';
}

//REVISE not giving subscripts

public static String nthNewVariable(int n,Set<String> notInHere){
	 char searchCh;
	 for (int i=0;i<fVariables.length();i++){
	   searchCh=fVariables.charAt(i);
	   if (!notInHere.contains(searchCh+"")){
	     if (n==1)
	        return
	          ""+searchCh;
	     else
	        n-=1;
	   }
	 }
	  return
	      "";
	}


/*public static char nthNewVariable(int n,String notInHere){
 char searchCh;
 for (int i=0;i<gVariables.length();i++){
   searchCh=gVariables.charAt(i);
   if (notInHere.indexOf(searchCh)==-1){
     if (n==1)
        return
          searchCh;
     else
        n-=1;
   }
 }
  return
      ' ';
} */


/********************** some lambda routines *********************************/


static public boolean alphaEqualFormulas(TFormula first, TFormula second){  // need to have two parameters, not a plain method, because either formula might be null

/* Sometimes we need to know that two formulas are equal apart from change of variable, so that,

for example, (Allx)(FximplicGx) is the same as (Ally)(FyimplicGy)

*/

   boolean value=false;
   Set<String> variables;
   String newVar;

   if ((second==null)&&(first==null))
     value=true;
   else
   {
     switch (first.fKind){
       case functor:
       case variable:
       case predicator:
       case equality:
         return
             first.equalFormulas(first, second);  // have to be exactly equal
       case unary:
         return
             first.equalFormulas(first.fRLink, second.fRLink); // have to be exactly equal
       case binary:
       case application:
         return
             first.equalFormulas(first.fLLink, second.fLLink)&&
             first.equalFormulas(first.fRLink, second.fRLink); // have to be exactly equal


       case typedQuantifier:
         if (first.quantType()!=second.quantType())
           return
               false;

         //if the types are different they are not the same, otherwise we drop through
         //and test them like ordinary quantifiers

       case quantifier:

         first=first.copyFormula();
         second=second.copyFormula();
         

         /*
         String firstVariables=variablesInFormula(first);
         String secondVariables=variablesInFormula(second);

         char newVar =nthNewVariable(1, firstVariables+secondVariables); */
         
         variables = new TreeSet<String>();
         if (variables.addAll(variablesInFormula(first)))
        	 ;
         if (variables.addAll(variablesInFormula(second)))
        	 ;
         
         newVar=nthNewVariable(1, variables);

         if (newVar.equals(""))
           return
               false;

         TFormula newVarForm = new TFormula(TFormula.variable,
                                            ""+newVar,
                                            null,
                                            null);

         TFormula firstScope=first.scope();
         TFormula.subTermVar(firstScope,
                 newVarForm,
                 first.quantVarForm());

         TFormula secondScope=second.scope();
         TFormula.subTermVar(secondScope,
                 newVarForm,
                second.quantVarForm());
     // we have re-written the scopes to use the same new variable

   return
      alphaEqualFormulas(firstScope,secondScope);


case lambda:  // similar to quantifier
  first=first.copyFormula();
  second=second.copyFormula();
/*
  firstVariables=variablesInFormula(first);
  secondVariables=variablesInFormula(second);

  newVar =nthNewVariable(1, firstVariables+secondVariables);

  if (newVar== ' ')
    return
        false;
*/
  
   variables = new TreeSet<String>();
  if (variables.addAll(variablesInFormula(first)))
 	 ;
  if (variables.addAll(variablesInFormula(second)))
 	 ;
  
  newVar =nthNewVariable(1, variables);

  if (newVar.equals(""))
    return
        false;
  
  newVarForm = new TFormula(TFormula.variable,
                                     ""+newVar,
                                     null,
                                     null);

           firstScope=first.scope();
           TFormula.subTermVar(firstScope,
                   newVarForm,

   first.lambdaVarForm());

secondScope=second.scope();
TFormula.subTermVar(secondScope,
                                     newVarForm,

    second.lambdaVarForm());

//we have re-written the scopes to use the same new variable

return
      alphaEqualFormulas(firstScope,secondScope);

 default:
   return
       false;
}
}



    return
        value;


}








/************/





public static boolean isFunctor (char ch)
      {
      return  ((gFunctors.indexOf((int)ch)!=-1));
      }

public static boolean isPowerSet (char ch)
{
return  (ch==chPowerSet);
}

public static boolean isSetTheoryConstant (char ch)
	{
	return  ((gSetTheoryConstants.indexOf((int)ch)!=-1));
	}

public static boolean isLambdaName (char ch)
              {
              return  ((gLambdaNames.indexOf((int)ch)!=-1));
      }


public static boolean isLambdaConstant (char ch)
      {
                      return  ((gLambdaConstant.indexOf((int)ch)!=-1));
      }



public static boolean isPredicate (char ch)
      {
      return  ((gPredicates.indexOf((int)ch)!=-1));
      }

public static boolean isTopBottomPredicate (char ch)
{
return  ((topBottomPredicate.indexOf((int)ch)!=-1));
}

public static boolean isVariable (char ch)
      {
      return  ((fVariables.indexOf((int)ch)!=-1));
      }

public static boolean isVariable (TFormula root)
 {
 if (root.fKind==variable)
                  return
                      true;
                else
                  return
                      false;
      }

boolean isPredInfix (String inf)
      {
      return (inf.equals(strEquals) ||
              inf.equals(strMemberOf) ||  //we'll permit these in the standard logic
              isMemberOfStr(inf) ||  //we'll permit these in the standard logic
              inf.equals(strNotMemberOf) ||  //we'll permit these in the standard logic
              inf.equals(strSubsetOf) ||  //we'll permit these in the standard logic
              inf.equals(strLessThan)||
              inf.equals(strGreaterThan));
      }

boolean isPredInfixSetTheory (String inf)
{
return (inf.equals(strMemberOf) ||  //we'll permit these in the standard logic
		isMemberOfStr(inf) ||  //we'll permit these in the standard logic
		inf.equals(strNotMemberOf) ||  //we'll permit these in the standard logic
        inf.equals(strSubsetOf));
}


/*********************** Accessors ************************************************/

public String getErrorString(){
        return fParserErrorMessage.toString();
      }

public void initializeErrorString(){
fParserErrorMessage = new StringWriter();
      }

/*********************** Parser core, recursive descent **********************************************/

boolean predicate(TFormula root){ /* predicate P<term1> <term2>... 
                                     (x<y) what about equals*/   /*Seems OK June25 03*/

      TFormula subterm;
      
      if (isTopBottomPredicate(fCurrCh)){   //special case for uptack false and downtack true
          root.fKind = predicator;
          root.fInfo=toInternalForm(fCurrCh);//String.valueOf(fCurrCh);

          skip(1);
          return WELLFORMED;
      
      	}
      

      if (isPredicate(fCurrCh))
          {
          root.fKind = predicator;
          root.fInfo=toInternalForm(fCurrCh);//String.valueOf(fCurrCh);

          skip(1);

          while (isFunctor(fCurrCh)||
        		  (fCurrCh == chSmallLeftBracket)||
        		  (fCurrCh == '<'))
                                       /*a term can start with a bracket
                                        * or be an ordered pair*/
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


private boolean infix5(TFormula root) {   /* what this is going to read is a union b
    and we have already read a as root.  So, we will read b. Then create a + node
    and make that the root and put a and b in its termlist*/
 
//{<term5ary>::= <term4ary> union <term5ary>| <term4ary>}
    	  
   boolean wellFormed=true;

   if (infixBinFun5(fCurrCh)){
     TFormula newRoot;

     newRoot = new TFormula(TFormula.functor,
                        String.valueOf(fCurrCh),  //probably add symbol
                        null,
                        null);


     skip(1); /*the intersect*/

     TFormula rightTerm=new TFormula();

     wellFormed=term5ary(rightTerm);

     if (wellFormed){
        TFormula  leftTerm = new TFormula(root.getKind(),
                                          root.getInfo(),
                                          root.getLLink(),
                                          root.getRLink());
        newRoot.appendToFormulaList(leftTerm);
        newRoot.appendToFormulaList(rightTerm);

        root.assignFieldsToMe(newRoot);   //surgery
     }
   }

   return
         wellFormed;
}


private boolean infix4(TFormula root) {   /* what this is going to read is a intersect b
    and we have already read a as root.  So, we will read b. Then create a + node
    and make that the root and put a and b in its termlist*/
 
//{<term4ary>::= <termtertiary> intersect <term4ary>| <termtertiary>}
    	  
   boolean wellFormed=true;

   if (infixBinFun4(fCurrCh)){
     TFormula newRoot;

     newRoot = new TFormula(TFormula.functor,
                        String.valueOf(fCurrCh),  //probably add symbol
                        null,
                        null);


     skip(1); /*the intersect*/

     TFormula rightTerm=new TFormula();

     wellFormed=term4ary(rightTerm);

     if (wellFormed){
        TFormula  leftTerm = new TFormula(root.getKind(),
                                          root.getInfo(),
                                          root.getLLink(),
                                          root.getRLink());
        newRoot.appendToFormulaList(leftTerm);
        newRoot.appendToFormulaList(rightTerm);

        root.assignFieldsToMe(newRoot);   //surgery
     }
   }

   return
         wellFormed;
}


private boolean infix3(TFormula root) {   /* what this is going to read is a + b
      and we have already read a as root.  So, we will read b. Then create a + node
      and make that the root and put a and b in its termlist*/
     boolean wellFormed=true;

     if (infixBinFun3(fCurrCh)){
       TFormula newRoot;

       newRoot = new TFormula(TFormula.functor,
                          String.valueOf(fCurrCh),  //probably add symbol
                          null,
                          null);


       skip(1); /*the plus*/

       TFormula rightTerm=new TFormula();

       wellFormed=termTertiary(rightTerm);

       if (wellFormed){
          TFormula  leftTerm = new TFormula(root.getKind(),
                                            root.getInfo(),
                                            root.getLLink(),
                                            root.getRLink());
          newRoot.appendToFormulaList(leftTerm);
          newRoot.appendToFormulaList(rightTerm);

          root.assignFieldsToMe(newRoot);   //surgery
       }
     }

     return
           wellFormed;
}




protected boolean infix2(TFormula root) {   
	// <termprimary> . <termsecondary>
	
	/* what this is going to read is a x b
and we have already read a as root.  So, we will read b. Then create a x node
and make that the root and put a and b in its termlist*/
	
	/*we come in here with a well formed term, say 1. What this needs to do is to
	 * swallow any multiplication sign returning WELLFORMED if there is none
	 * So, we enter with, say, 1.2<next> and leave
	 * with fChrrCh looking at <next>
	 */	
	
boolean wellFormed=true;

if (infixBinFun2(fCurrCh)){
   TFormula newRoot = new TFormula(TFormula.functor,
                   String.valueOf(fCurrCh),  //probably mult symbol
                   null,
                   null);


   skip(1); /*the mult*/

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
   }
}

return
    wellFormed;
}





private boolean postfixSuccessor (TFormula root) {
 /*what we are reading here is, say a'''' but we need to store this as
 ''''a ie all functions are prefix internally*/
	
/*we come in here with a well formed term, say 1. What this needs to do is to
 * swallow any successor checks that are there and leave returning WELLFORMED
 * which shows that it has been done. So, we enter with, say, 1'''<next> and leave
 * with fChrrCh looking at <next>
 */
 
	
TFormula newRoot;

 while (fCurrCh== chSucc) {

   newRoot = new TFormula(TFormula.functor,
                 String.valueOf(fCurrCh),  //succ symbol
                 null,
                 null);

   TFormula leftTerm = new TFormula(root.getKind(),
                                root.getInfo(),
                                root.getLLink(),
                                root.getRLink());


   newRoot.appendToFormulaList(leftTerm);  // so now we have 'a not a'

   root.assignFieldsToMe(newRoot);        //surgery

   skip(1); /*the successor symbol*/
 }
 return WELLFORMED;
}



private boolean termPrimary (TFormula root) {


//	{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }  //ie a comprehensino
//	{<termprimary>::= <powerset(one element termlist>)
//	{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)|<powerset(one element termlist>)  }
//	{<termprimary>::= < <a,b> ie ordered pair >}

   if (fCurrCh == '{')
		return
		termComprehension(root);
 
   if (isPowerSet(fCurrCh))
       return
          termPowerSet(root);
   
   if (fCurrCh == '<')
       return
          termOrderedPair(root);

 if (fCurrCh == '('){          // we are starting with a left bracket--next has to be a term
     // now trying for (term)
    skip(1); //the bracket
          if (!termFirstOrder(root)) { //  not well formed
            return
                ILLFORMED;
          }
          else { // now want the right bracket

            if (fCurrCh != ')') { // the matching right bracket
              writeError(CR + "( * The character '" + fCurrCh +
                         "' should be a ). *)");
              return
                  ILLFORMED;
            }
            else {
              skip(1); // the bracket
              return
                  WELLFORMED;
            }

          }
        }
 

 
 
 
 
//instead trying for <functor>|<functor(<nonempty termlist>

 //      subterm = null;

       if (isFunctor(fCurrCh)) /* tests whether functor*/
           {
         root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);

         if (isVariable(fCurrCh)){ /* tests whether a variable*/
           root.fKind = variable; /*some zero order functors are variables*/
           
           while (superScripts.indexOf((int)fLookAheadCh)!=-1){  /* we permit superscripted variables eg x1 */
        	   root.fInfo+=fLookAheadCh;
        	   skip(1);     	   
           }        
         }
         else
           root.fKind = functor;

         skip(1); /*looking at small left-bracket or next item*/

         if (fCurrCh == chSmallLeftBracket) {
           root.fKind = functor; /*cannot now be a variable*/

           skip(1); /*looking at next item*/


           if (fCurrCh==chSmallRightBracket){ /*must be non-empty, new Dec07*/
             writeError("(*The character '" + fCurrCh + "' should be a functor.*)");
             return ILLFORMED;
           }

/*
           while (fCurrCh != chSmallRightBracket) {
             subterm = new TFormula();
             if (this.termFirstOrder(subterm))
               root.appendToFormulaList(subterm);
             else
               return ILLFORMED;
           }  */

          if (termList(root))           //new Dec07
             ;                         /*continue*/
          else
             return ILLFORMED;

           skip(1); /*the small right-bracket*/
         }
       }
       else {
         writeError("(*The character '" + fCurrCh + "' should be a functor.*)");
         return ILLFORMED;
       }
       return WELLFORMED;
     }





protected boolean termSecondary (TFormula root){
//{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
 return
     termPrimary(root)&&
     postfixSuccessor(root)&&
     infix2(root);
}

private boolean termTertiary (TFormula root){
//{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}

 return
     termSecondary(root)&&
     infix3(root);
     }

private boolean term4ary (TFormula root){
	//{<term4ary>::= <termtertiary> intersect <term4ary>| <termtertiary>}

	 return
	     termTertiary(root)&&
	     infix4(root);
	     }

private boolean term5ary (TFormula root){
	//{<term5ary>::= <term4ary> intersect <term5ary>| <term4ary>}

	 return
	     term4ary(root)&&
	     infix5(root);
	     }
/************************ Set Theory ************************?
 * 
 * @param root
 * @return
 */

/*
private boolean termSetTheory (TFormula root){
    /*
{for terms}
{<functor>::= a..z|0|1|2|3    } basically variables or constants
{<termprimary>::=(<termST>)|<functor>|<setTheory constant eg empty set> }
{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> intersect <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> union <termtertiary>| <termsecondary>}


return
 termSetTheoryTertiary(root);
 } 

private boolean termSetTheoryPrimary (TFormula root) {

	//{<termprimary>::=(<term>)|<functor>|<setTheory constant eg empty set> }
	
	if (fCurrCh == '{')
		return
		termSetTheoryPrimaryComprehension(root);
		


	 if (fCurrCh == '('){          // we are starting with a left bracket--next has to be a term
	     // now trying for (term)
	    skip(1); //the bracket
	          if (!termSetTheory(root)) { //  not well formed
	            return
	                ILLFORMED;
	          }
	          else { // now want the right bracket

	            if (fCurrCh != ')') { // the matching right bracket
	              writeError(CR + "( * The character '" + fCurrCh +
	                         "' should be a ). *)");
	              return
	                  ILLFORMED;
	            }
	            else {
	              skip(1); // the bracket
	              return
	                  WELLFORMED;
	            }

	          }
	        }


	//instead trying for <functor>

	       if (isFunctor(fCurrCh)||
	    	   isSetTheoryConstant(fCurrCh)  ) /* tests whether functor
	           {
	         root.fInfo = String.valueOf(fCurrCh);

	         if (isVariable(fCurrCh)) /* tests whether a variable
	           root.fKind = variable; /*some zero order functors are variables
	         else
	           root.fKind = functor;

	         skip(1); /*looking at next item

	       }
	       else {
	         writeError("(*The character '" + fCurrCh + "' should be a functor.*)");
	         return ILLFORMED;
	       }
	       return WELLFORMED;
	     }

*/

boolean assembleComprehension(TFormula root,TFormula variable){
	/*                 {var: scope}|
	 *                 {var| scope} }
	 *                 where the scope is any T/F formula*/
	
	/*we'll organize this like a quantified formula */
	
	root.fKind=comprehension;  //already set by caller
	root.fInfo=toInternalForm(fCurrCh);//String.valueOf(fCurrCh); // the : or |
	root.fLLink=variable;
	
	skip(1); // the : or |
	
	TFormula scope= new TFormula();
	
//	System.out.print("Assemble");
	
	if ((top(scope))&&
		(fCurrCh== '}')){
			root.fRLink=scope;
			skip(1); // the }
	//		System.out.print("Two");
		return
		   WELLFORMED;		
	}
	
	
	return
	  ILLFORMED;
}

boolean comprehensionInner(TFormula root){  
	 /*                 {termlist}|    // not really a comprehension, but we'll understand it that way
	 *                 {var: scope}|
	 *                 {var| scope} }*/

	
/*we have read the opening { so we need to read the 
 * first term if there is one */	
	
if  (fCurrCh != '}') {
   TFormula firstTerm = new TFormula();
		 // if (this.termSetTheory(firstTerm)){
   if (this.termFirstOrder(firstTerm)){
			  
			/* there are 4 proper things that might happen, we can be looking at 
			 * a comma, a colon, a vert line, or the closing bracket*/
			  
			 if (((fCurrCh==chColon)||(fCurrCh==chVertLine))&&
				   firstTerm.fKind==variable)
				 return
				    assembleComprehension(root,firstTerm);
			 
			 root.appendToFormulaList(firstTerm);  // we put the first term in no matter what
			 
			 if (fCurrCh==chComma)// only comma permitted now, or closing } which gets caught below
				 skip(1);       // the comma
	
		  }
		else
		   return ILLFORMED;		  
		}	
	

while (fCurrCh != '}') {
TFormula subterm = new TFormula();
//  if (this.termSetTheory(subterm)){
   if (this.term(subterm)){
     root.appendToFormulaList(subterm);
     if (fCurrCh==chComma)   //separator
    	   skip(1);
  }
else
   return ILLFORMED;
}

skip(1);  // the }
return
   WELLFORMED;
}

private boolean termComprehension (TFormula root) {
	
	/* from Set Theory
	 * 
	 * {<termprimary>::={}|
	 *                 {termlist}|
	 *                 {var: scope}|
	 *                 {var| scope} }*/
	
	//System.out.print("Hello");
	
	if (fCurrCh == '{'){    
		
    	root.fKind=comprehension;
    	root.fInfo=strEmptySet;
		     
		skip(1); //the bracket
		
		return
		   comprehensionInner(root);

	/*	    if (fCurrCh != '}') { // the matching right bracket
		              writeError(CR + "( * The character '" + fCurrCh +
		                         "' should be a }. *)");
		              
		             // System.out.print("Two");
		              return
		                  ILLFORMED;
		    }
		    else{
		    	
		    	root.fKind=comprehension;
		    	root.fInfo=strEmptySet;
		    	
		    	//System.out.print("Three");
		    	
		    	skip(1); //the bracket
		    	
		    	return WELLFORMED;
		    } */

	}
	
	return ILLFORMED;
}

private boolean termOrderedPair (TFormula root) {
//	<termprimary>::= <a,b> two element termlist)
	if (fCurrCh=='<'){ 

	//   root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);  LEAVE IT BLANK
	   root.fKind = pair;

	   skip(1); /*looking at next item*/

	    if (termList(root)&&
	    	root.firstTerm()!=null&&
	    	root.secondTerm()!=null&&
	    	root.nthTopLevelTerm(3)==null){ //exactly two terms
	    	if (fCurrCh=='>'){
	    		skip(1);               /*the right-bracket*/
	    		return WELLFORMED;
	    	}else
	    		return ILLFORMED;
	    }else
	       return ILLFORMED;  
	   
	 }
	return ILLFORMED; 
}
	
private boolean termPowerSet (TFormula root) {
//	<termprimary>::= <powerset(one element termlist>)
	if (isPowerSet(fCurrCh)){ 

	   root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);
	   root.fKind = functor;

	   skip(1); /*looking at small left-bracket or next item*/

	   if (fCurrCh != chSmallLeftBracket) 
		   return ILLFORMED;    //has to be P(term)
	   else{

	     skip(1); /*looking at next item*/

	    if (termList(root)&&
	    	root.firstTerm()!=null&&
	    	root.secondTerm()==null){ //exactly one term
	    	 skip(1);               /*the small right-bracket*/
	    	 return WELLFORMED;
	    }else
	       return ILLFORMED;  
	   }
	 }
	return ILLFORMED; 
}


boolean infixBinFun2SetTheory (char ch) //infixbinaryfunctiontertiary
{
  return  (ch==chIntersection);
}

boolean infixBinFun3SetTheory (char ch) //infixbinaryfunctiontertiary
{
  return  (ch==chUnion);
}

/************************ End of Set Theory ************************/

private boolean termFirstOrder (TFormula root){
         /*
  Now, Jan09, trying to read everything
  
  {new grammar for terms}
{<functor>::= a..z|0|1 + <setTheory constant eg empty set>  NOTE MUST INCLUDE PHI AND U AMONG CONSTANTS    }
{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)  }
{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}
{<term4ary>::= <termtertiary> intersect <term4ary>| <termtertiary>}
{<term5ary>::= <<term4ary>> union <term5ary>| <<term4ary>>}  
     
 OLD    {for terms}
{<functor>::= a..z|0|1    }
{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)  }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}

     */

  return
     // termTertiary(root);
  term5ary(root);

      }


/*
{grammar for terms}
{<functor>::= a..z|0|1    }
{<termprimary>::=(<term>)|<functor>|<functor(<nonempty termlist>)  }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}

{grammar for SetTheory terms}
{<functor>::= a..z|0|1|2|3    } basically variables or constants
{<termprimary>::=(<termST>)|<functor>|<setTheory constant eg empty set> }
{<termprimary>::={}|{termlist}|{var: scope}|{var| scope} }
{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> intersect <termsecondary>| <termprimary>}
{<termtertiary>::= <termsecondary> union <termtertiary>| <termsecondary>}


Pascal {$S GentzenFormula}
procedure TParser.Term (var root: TFormula; var illformed: BOOLEAN);

begin

TermTertiary(root, illformed);
end;


      */




public boolean term (TFormula root, Reader aReader){    // sometimes called externally to parse term
	TFormula cCroot;
	//int defaultType=0;
	
	  if (fCCParser==null)
		  fCCParser=new CCParser(new java.io.BufferedReader(aReader),CCParser.DEFAULT);
	  else
		  fCCParser.reInit(new java.io.BufferedReader(aReader),CCParser.DEFAULT);

//	fCCParser=new CCParser(new java.io.BufferedReader(aReader));
	try {
		cCroot= fCCParser.term();
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
	
/*	
	
	fInput=aReader;
  // skip(1);
 initializeInputBuffer();


   return
       term (root); */
   }


boolean termList(TFormula root){  /* adjacent, no separators <term1><term2> etc
                                   but also will parse <term1>,<term2> etc ie comma separators*/

/* Dec 07 I haven't finished parsing lists of terms. It causes bad effects because we use comma separated lists (eg of formulas)
all over the place */

while (fCurrCh != chSmallRightBracket&&
	   fCurrCh != '>') {                   // an ordered list can be closed with a >
TFormula subterm = new TFormula();
if (this.term(subterm))
  root.appendToFormulaList(subterm);
else
  return ILLFORMED;

if (fCurrCh==chComma)   //separator
  skip(1);
}
return
  WELLFORMED;
}

/*private changed July08*/ boolean term (TFormula root)     /*seems ok June 26th*/
      {

        if    (fFirstOrder)                      // was Nov08 (TPreferences.fFirstOrder)
          return
              termFirstOrder(root);
        else {

          TFormula subterm = null;

          if (isFunctor(fCurrCh)) /* tests whether functor*/
              {
            root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);

            if (isVariable(fCurrCh)){ /* tests whether a variable*/
                root.fKind = variable; /*some zero order functors are variables*/
                
                while (superScripts.indexOf((int)fLookAheadCh)!=-1){  /* we permit superscripted variables eg x1 */
             	   root.fInfo+=fLookAheadCh;
             	   skip(1);     	   
                }        
              }
            else
              root.fKind = functor;

            skip(1); /*looking at small left-bracket or next item*/

            if (fCurrCh == chSmallLeftBracket) {
              root.fKind = functor; /*cannot now be a variable*/

              skip(1); /*looking at next item*/

              if (fCurrCh==chSmallRightBracket){ /*must be non-empty, new Dec07*/
                writeError("(*The character '" + fCurrCh + "' should be a functor.*)");
                return ILLFORMED;
              }

           /*   while (fCurrCh != chSmallRightBracket) {
                subterm = new TFormula();
                if (this.term(subterm))
                  root.appendToFormulaList(subterm);
                else
                  return ILLFORMED;
              }  */

              if (termList(root))
                ;       /*continue*/
              else
                return ILLFORMED;

              skip(1); /*the small right-bracket*/
            }
          }
          else {
            writeError("(*The character '" + fCurrCh + "' should be a functor.*)");
            return ILLFORMED;
          }
          return WELLFORMED;
        }
      }

/* termFirstOrder should go

     begin

TermTertiary(root, illformed);
end;

*/




boolean atomic(TFormula root)   /*seems ok June 26*/
/*

{    <predicate> < term1> < term2 > .. <termn>}
{  < term1> =< term2 > }
{< term1> << term2 > etc}
{<term1> epsilon <setTheoryTerm2>
term1> notEpsilon <setTheoryTerm2>


*/

      {
      if (isPredicate(fCurrCh)||isTopBottomPredicate(fCurrCh))
              return predicate(root);
      else
              return infixPredicate(root);

      }



class TermTest {
	  boolean testIt(boolean skipCurrent){                   //try to identify term
	    TFormula termTest = new TFormula();
	    boolean isTerm = false;
	    StringWriter savedErrors = fParserErrorMessage;
	    fParserErrorMessage = new StringWriter();

	    /* I had a bug here-- Dec 06 because I wanted to mark and reset, but skip calls 
	     * setlookahead which
	        also marks and resets. ie nested resets

	     No longer use any of this*/

	    StringBuffer tempBuffer = new StringBuffer();

	    try {

	      fInput.mark(Integer.MAX_VALUE);

	      char tempCh = chBlank;

	      for (int i = 0; i < 1000 && (tempCh != EOF); i++) {
	        tempCh = (char) fInput.read();
	        if (tempCh != EOF)
	          tempBuffer.append(tempCh);
	      }
	      fInput.reset();        // go back
	    }
	    catch (IOException e)
	{
	  System.err.println("Exception thrown in TermTest.testIt");
	}

	// now we have an up to 1000 char copy of inputStr

	StringReader altReader= new StringReader(tempBuffer.toString());

	char tempCurrCh=fCurrCh;
	char tempLookAheadCh=fLookAheadCh;
	char tempLookTwoAheadCh=fLookTwoAheadCh;
	Reader tempReader=fInput;  // and it has been reset

	// record how we were

	fInput=altReader;
	
	if (skipCurrent)  // sometimes we need to skip prior to text, eg when looking at .
		skip(1);      // sometimes we don't, eg when looking at (
	                  // the caller knows

	isTerm = term(termTest);  // do the test

	fInput=tempReader;
	fInputBuffer[0]=tempCurrCh;
	fInputBuffer[1]=tempLookAheadCh;
	fInputBuffer[2]=tempLookTwoAheadCh;
	fCurrCh=fInputBuffer[0];
	fLookAheadCh=fInputBuffer[1];
	fLookTwoAheadCh=fInputBuffer[2];

	return
	   isTerm;
	}
	}


boolean primary(TFormula root){

/* Primary::= (<top>) | <atomic>  note here that <atomic> can start (<term>)
   as in (a+b)=c etc... So looking at the left bracket is not enough. What we
do is look at the left bracket, then try*/

/*  THIS HAS BEEN REWRITTEN, SO IF THERE ARE MISTAKES YOU KNOW WHY */

/* TermTest used to be within the method
class TermTest {
  boolean testIt(){                   //try to identify term
    TFormula termTest = new TFormula();
    boolean isTerm = false;
    StringWriter savedErrors = fParserErrorMessage;
    fParserErrorMessage = new StringWriter();

    StringBuffer tempBuffer = new StringBuffer();

    try {

      fInput.mark(Integer.MAX_VALUE);

      char tempCh = chBlank;

      for (int i = 0; i < 1000 && (tempCh != EOF); i++) {
        tempCh = (char) fInput.read();
        if (tempCh != EOF)
          tempBuffer.append(tempCh);
      }
      fInput.reset();        // go back
    }
    catch (IOException e)
{
  System.err.println("Exception thrown in TermTest.testIt");
}

// now we have an up to 1000 char copy of inputStr

StringReader altReader= new StringReader(tempBuffer.toString());

char tempCurrCh=fCurrCh;
char tempLookAheadCh=fLookAheadCh;
char tempLookTwoAheadCh=fLookTwoAheadCh;
Reader tempReader=fInput;  // and it has been reset

// record how we were

fInput=altReader;

isTerm = term(termTest);  // do the test

fInput=tempReader;
fInputBuffer[0]=tempCurrCh;
fInputBuffer[1]=tempLookAheadCh;
fInputBuffer[2]=tempLookTwoAheadCh;
fCurrCh=fInputBuffer[0];
fLookAheadCh=fInputBuffer[1];
fLookTwoAheadCh=fInputBuffer[2];

return
   isTerm;
}
}  */


/*three acceptable possibilities: predicate,functor or left bracket*/

if (fCurrCh != '(') {  //gets the first two
  return
      atomic(root);
}
else{          // we are starting with a left bracket--next can be a term or a wff
  TermTest isTerm = new TermTest();
  
  boolean skipCurrent=true;

  if (isTerm.testIt(!skipCurrent))    //we lookahead, then come back and just need to call atomic which will sort out infix etc
    return
        atomic(root);
  else { // now trying for (wff)
    skip(1); //the bracket
    if (!top(root)) { //  not well formed
      return
          ILLFORMED;
    }
    else { // now want the right bracket

      if (fCurrCh != ')') { // the matching right bracket
        writeError(CR + "( * The character '" + fCurrCh +
                   "' should be a ). *)");
        return
            ILLFORMED;
      }
      else {
        skip(1); // the bracket
        return
            WELLFORMED;
      }

    }
  }
}
}




/****************** This is new code June 08 to parse typed variables ******************/

/*private*/ protected boolean type (TFormula root){


/*<variable>::= isVariable(atomic) | <variable>:<type lable> }
 <type lable> ::= <functor ie constant>*/


TFormula subterm = null;

skip(1); /*looking at type  */

if (!isFunctor(fCurrCh)) { /* tests whether a functor*/
 writeError("(*The character '" + fCurrCh + "' should be a type label.*)");
 return ILLFORMED;

}
else {

 root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);
 root.fKind = functor; /*some zero order functors are variables*/


    return WELLFORMED;
}
}



/****************** End of new code June 08 to parse typed variables ******************/

/****************** Secondary. This uses several inner classes to allow subclasses more flexibility ******************/

boolean secondary(TFormula root)  //seems ok

/*{<secondary>::= ~<secondary> | (Allv)<secondary>}
   {(Exv)<secondary>|<primary>}*/

/*added May 08 also{
 * <secondary>::= modalpossible<secondary> | modalnecessary<secondary>*/

/*added Jan 09 also{
 * <secondary>::= (v)<secondary>   Copi like systems omit the universal quantifier */

/*added April 09 also{
 * <secondary>::= kappa<term><secondary>*/

/*added May 09 also{
 * <secondary>::= rho<term><secondary>*/

{
 if (isNegationCh(fCurrCh)){
   Negtest tester =new Negtest();
   if (tester.testIt(root))
     return
        WELLFORMED;
   else
     return
        ILLFORMED;
    }
 else
 if (isModalPossibleCh(fCurrCh)){
ModalPossibletest tester =new ModalPossibletest();
if (tester.testIt(root))
  return
     WELLFORMED;
else
  return
     ILLFORMED;
 }
else
 if (isModalNecessaryCh(fCurrCh)){
ModalNecessarytest tester =new ModalNecessarytest();
if (tester.testIt(root))
  return
     WELLFORMED;
else
  return
     ILLFORMED;
 }
else
	if (isModalKappaCh(fCurrCh)){
		ModalKappatest tester =new ModalKappatest();
		if (tester.testIt(root))
		  return
		     WELLFORMED;
		else
		  return
		     ILLFORMED;
		 }
		else
			if (isModalRhoCh(fCurrCh)){
				ModalRhotest tester =new ModalRhotest();
				if (tester.testIt(root))
				  return
				     WELLFORMED;
				else
				  return
				     ILLFORMED;
				 }
				else			

 if ((fCurrCh == '(')&&                 // we need that
	 ((fLookAheadCh == chUniquant||fLookAheadCh == chExiquant)||  //ordinary case for most grammare
	 (isVariable(fLookAheadCh)&& fLookTwoAheadCh == ')'))){   //odd case for Copi like grammar which omit uniquant
   QuantTest tester =new QuantTest();                         // so this has to look (var)
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

/******* inner classes for secondary ****************************/

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

class ModalKappatest {  // Kappa<term><formula>
	boolean testIt(TFormula kappaRoot){
	TFormula newnode= new TFormula(modalKappa,String.valueOf(chKappa),null,null);
	
	TFormula index=new TFormula();

	skip(1);   // the kappa
	
	//if (term(index)){
	if (isFunctor(fCurrCh)){   //looking for single character index
		
		index.fInfo = toInternalForm(fCurrCh);

        if (isVariable(fCurrCh)) { /* tests whether a variable*/
            index.fKind = variable; /*some zero order functors are variables*/
            
            while (superScripts.indexOf((int)fLookAheadCh)!=-1){  /* we permit superscripted variables eg x1 */
         	   index.fInfo+=fLookAheadCh;
         	   skip(1);     	   
            }        
          }
        else
        	index.fKind = functor;
				
		 newnode.fLLink = index;
		 
		 skip(1); /*over index to next item*/
		 
		 TFormula formula=new TFormula();
		 
		  if (secondary(formula)){
		 //if (top(formula)){                  //entirely new wff?
				 newnode.fRLink = formula;
				 kappaRoot.assignFieldsToMe(newnode);
				 newnode=null;
				 return
				 WELLFORMED;
				}
				else
				 return
				     ILLFORMED;
				}
		else
		 return
		     ILLFORMED;
	}
	}

class ModalRhotest {  // Kappa<term><formula>
	boolean testIt(TFormula rhoRoot){
	TFormula newnode= new TFormula(modalRho,String.valueOf(chRho),null,null);
	
	TFormula index=new TFormula();

	skip(1);   // the kappa
	
	//if (term(index)){
	if (isFunctor(fCurrCh)){   //looking for single character index
		
		index.fInfo = toInternalForm(fCurrCh);

        if (isVariable(fCurrCh)){ /* tests whether a variable*/
            index.fKind = variable; /*some zero order functors are variables*/
            
            while (superScripts.indexOf((int)fLookAheadCh)!=-1){  /* we permit superscripted variables eg x1 */
         	   index.fInfo+=fLookAheadCh;
         	   skip(1);     	   
            }        
          }
        else
        	index.fKind = functor;
				
		 newnode.fLLink = index;
		 
		 skip(1); /*over index to next item*/
		 
		 TFormula formula=new TFormula();
		 
		  if (secondary(formula)){
		 //if (top(formula)){                  //entirely new wff?
				 newnode.fRLink = formula;
				 rhoRoot.assignFieldsToMe(newnode);
				 newnode=null;
				 return
				 WELLFORMED;
				}
				else
				 return
				     ILLFORMED;
				}
		else
		 return
		     ILLFORMED;
	}
	}

	class QuantTest {  //{take care here for this might be Ex! for the unique quantifier}
		
		boolean doNoQuantifier(TFormula quantRoot){   //odd case for Copi like grammar which omit uniquant (var)
			TFormula variableNode = new TFormula(variable,
    				String.valueOf(fLookAheadCh), 
    				null, null);
			TFormula newnode = new TFormula(quantifier, 
				String.valueOf(chUniquant), 
				variableNode, 
				null);  //this uniquant never printed
			skip(3);  //(var)
			
		       TFormula scope = new TFormula();

		       if (secondary(scope)) {       //the scope of the quantified expression
		         newnode.fRLink = scope;
		         quantRoot.assignFieldsToMe(newnode);
		         newnode = null;
		         return
		             WELLFORMED;
		       }
		       else
		         return
		             ILLFORMED;
		}
		
		boolean testIt(TFormula quantRoot) {
			TFormula variableNode;
			TFormula newnode;
		   
			if (fCurrCh == '('&& 
				isVariable(fLookAheadCh)&& 
				fLookTwoAheadCh == ')'){   //odd case for Copi like grammar which omit uniquant (var)
				
				return
					doNoQuantifier(quantRoot);

			}
			else{
		   
	     skip(1); // the opening bracktet, now looking at quantifier

	     if ((fCurrCh == chExiquant) && (fLookAheadCh == chUnique)) // {unique case}
	       skip(1); // {now looking at exclamation mark}

	     newnode = new TFormula(quantifier, String.valueOf(fCurrCh), null, null);
	     variableNode = new TFormula(variable,
	                                          String.valueOf(fLookAheadCh), null, null);
	     skip(1); //now looking at variable


	     /*<variable>::= isVariable(atomic) | <variable>:<type lable> }
	       <type lable> ::= <functor ie constant>*/

	     if (!(isVariable(fCurrCh))) {
	        writeError(CR + "( *'"+fCurrCh +
	           "' should be a variable. *)");
	     return
	       ILLFORMED;
	     }

	     if (fLookAheadCh == chColon){    //typed variable/quantifier
	       /*typing of variables only occurs in the presence of a quantifier, so it is clearer to say that
	       the quantifier is typed */

	       newnode.fKind=typedQuantifier;  //change from quantifier to typedQuantifier

	       skip(1);   //current looking at colon, lookahead looking at type label

	       TFormula typeNode = new TFormula(functor, String.valueOf(fLookAheadCh), null, null);

	       if (!type(typeNode)){    //new  June 08
	          writeError(CR + "( *'" + fCurrCh +
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

	       //new to here  */

	     }   // end of typed variable

	     else
	    	 newnode.fLLink = variableNode;                    //no type

	/*old
	     if (!(isVariable(fCurrCh)) || (fLookAheadCh != ')')) {
	       writeError(gCR + "( *Either '" + fCurrCh +
	                  "' should be a variable or a ) is missing. *)");
	       return
	           ILLFORMED;
	     }   end of old*/

	     if ((fLookAheadCh != ')')) {   //new
	       writeError(CR + "( *'" + fCurrCh +
	                  "' should be a ')' . *)");
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
	   }
		}
	 }

/****************** End of Secondary. ******************/



boolean tertiary(TFormula root){  //seems ok

/*{<tertiary>::= <secondary> connective <tertiary>| <secondary>}*/

      if (!secondary(root))
         return
             ILLFORMED;  //we're out of here
       else {            // we have secondary successfully parsed, moving on
           if (isAndCh(fCurrCh)||
               isOrCh(fCurrCh) ||
               isImplicCh(fCurrCh))
              {
               TFormula lLink=new TFormula();
               TFormula rLink=new TFormula();
               String cachedInfo=toInternalForm(fCurrCh);  //  String.valueOf(fCurrCh);

               skip(1);  //the connective

               if (tertiary(rLink)) {
                 lLink.assignFieldsToMe(root); // values of old root go on left

                 root.fKind=binary;            // alter caller
                 root.fInfo= cachedInfo;
                 root.fLLink = lLink;
                 root.fRLink = rLink;

                 return
                  WELLFORMED;   // good one with connective
               }
               else
                 return ILLFORMED; // bad one right link illformed
              }
            else
              return
                  WELLFORMED;  //no connective
          }
      }




boolean top(TFormula root){  //seems ok

/* {<top>::= <tertiary> equiv <top>| <tertiary>}*/

 if (!tertiary(root))
    return
        ILLFORMED;      // must start with tertiary
 else {
   if (isEquivCh(fCurrCh)) {

        TFormula lLink=new TFormula();
               TFormula rLink=new TFormula();
               String cachedInfo=toInternalForm(fCurrCh);

               skip(1);  //the connective

               if (top(rLink)) {
                 lLink.assignFieldsToMe(root); // values of old root go on left

                 root.fKind=binary;            // alter caller
                 root.fInfo= cachedInfo;
                 root.fLLink = lLink;
                 root.fRLink = rLink;

                 return
                  WELLFORMED;   //  good equivalence
               }
               else
                 return ILLFORMED; // tertiary left branch, illformed on right
              }


 else
   return
       WELLFORMED;        // good tertiary with no equivalence
  }
}




private boolean wffCheck (TFormula root, ArrayList newValuation){

/* {RECURSIVE DESCENT WITH PRECEDENCE AND RIGHT ASSOCIATION}

   {<top>::= <tertiary> equiv <top>| <tertiary>}  */

   if (top(root))
      {
      if (fCurrCh == chBlank)
         return
             WELLFORMED;
      else
          {
          if (fCurrCh == chLSqBracket)  // {valuation}
              {
              if (getValuation(newValuation))
                return
                    WELLFORMED;
              else
                return
                    ILLFORMED;
              }
          else{
              writeError(CR+"(*The extra character '"+
                                   fCurrCh+
                                   "' should be a blank.*)" );
              return
                  ILLFORMED;
              }
          }
          }
      else
       return
           ILLFORMED;
   }

   public boolean lambdaWffCheck (TFormula root, ArrayList newValuation,Reader aReader){

        initializeErrorString();   //new Dec07
        fInput=aReader;

        initializeInputBuffer();
        //skip(1);  // I think you need this to initialize lookaheads

        return
            lambdaWffCheck (root,newValuation);
   }

   public boolean wffCheck (TFormula root, Reader aReader){
	   TFormula cCroot;

	  // int defaultType=0;

	   if (fCCParser==null)
	   	  fCCParser=new CCParser(new java.io.BufferedReader(aReader));
	   else
	   	  fCCParser.reInit(new java.io.BufferedReader(aReader),CCParser.DEFAULT);

	   fCCParser=new CCParser(new java.io.BufferedReader(aReader));
	   	try {
	   		cCroot= fCCParser.wffCheck();
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

public boolean wffCheck (TFormula root, ArrayList<TFormula> newValuation,Reader aReader){
TFormula cCroot;

//int defaultType=0;

if (fCCParser==null)
	  fCCParser=new CCParser(new java.io.BufferedReader(aReader));
else
	  fCCParser.reInit(new java.io.BufferedReader(aReader),CCParser.DEFAULT);

fCCParser=new CCParser(new java.io.BufferedReader(aReader));
	try {
		cCroot= fCCParser.wffCheckWithValuation(newValuation);
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
/*	
	initializeErrorString();   //new Dec07
   fInput=aReader;

   initializeInputBuffer();
   //skip(1);  // I think you need this to initialize lookaheads

   return
       wffCheck (root,newValuation);  */
   }



boolean getValuation(ArrayList<TFormula> valuation) {
boolean illFormed=false;

valuation.clear();

skip(1); // go past the opening bracket

while ((fCurrCh!=chRSqBracket)&&!illFormed){
  TFormula aFormula=new TFormula(kons,strNull,null,null);

  if ((isConstant(fCurrCh) &&(fLookAheadCh == chSlash))){


    aFormula.fInfo=new String(String.valueOf(fCurrCh)+String.valueOf(chSlash));

    skip(2);     //now looking for variable

    if (isVariable(fCurrCh))
     aFormula.fInfo= aFormula.fInfo+ String.valueOf(fCurrCh);
    else{
      illFormed = true;
      writeError(String.valueOf(fCurrCh) + fErrors14); //{not at variable}
    }
    if (!illFormed){
        skip(1);
    }

    if (fCurrCh == chComma) // {when the list is read in}
       skip(1); // {the items are separated by commas}
   }
   else{
     illFormed = true;
     writeError(String.valueOf(fCurrCh) + fErrors13); //{not at cosntant}
   }

   if (!illFormed)
     valuation.add(aFormula);
}

if (!illFormed){
       skip(1);  //move on to blank
   }

return
    (!illFormed);
}



public boolean addToValuation(char constant,char variable,ArrayList<TFormula>valuation){
/*
   {the newvaluation is stored in a list of formulas}
{using the info field of the formula in format 'term/term'}
*/

if ((isConstant(constant)||
   constant=='?') &&        // this is a special case that we permit for Endorse Deny
  (isVariable(variable))){

  TFormula aFormula = new TFormula(kons,
                                   String.valueOf(constant) + chSlash +
                                   variable,
                                   null,
                                   null);
  valuation.add(aFormula);
  return
      true;
}
return
    false;
}



/*********************** From TFormula *************************/

/********************** Writing routines *********************************/

protected int fWrapBreak=60; //fWrapBreak=120; // trying to stop the formulas getting too long
                             // 50 seems too big
                             // 42 used to be fine, but now splitting on And

public void setWrapBreak(int value){
	fWrapBreak=value;
}

/*we use only one internal representation, but some of the other parsers use different symbols */

public String translateConnective(String connective){
return
    connective;
}

public String writeFormulaToString (TFormula root, int maxChars){
String outPutStr=writeFormulaToString (root);  // see what we have, and its length
int oldWrapBreak=fWrapBreak;                  //fWrapBreak class method keeps track of running total

if (maxChars>16){
   while ((outPutStr.length()>maxChars)&&   // too long we'll shorten   
          (fWrapBreak>5)){   // part of the problem is that what we put in "&lt;left&gt;" has many chars
      fWrapBreak -= 5;
      outPutStr = writeFormulaToString(root);
   }
   fWrapBreak=oldWrapBreak;
}
else
	outPutStr=fLongTruncate;
return
    outPutStr;
}

public String writeFormulaAndWrap(TFormula root){
//puts outer brackets around binary and equality

String outputStr=writeFormulaToString (root);

if ((root.fKind==equality)||(root.fKind==binary))
   outputStr="("+outputStr+")";

return
   outputStr;
}

public String writeFormulaToString (TFormula root){
if (root==null)
  return
      strNull;
else{
String leftString = new String();
String rightString = new String();
String termString = new String();
String compString = new String();

      switch (root.fKind) {

        case predicator:
          return
              writePredicateToString(root);
        case functor:
        case variable:{
        	termString=writeTermToString(root);
        	if (termString.length() > fWrapBreak)   // terms can be arbitrarily long
        		termString = fTermTruncate;
        	
          return
             termString;
        }

        case equality: {
          leftString = writeTermToString(root.firstTerm());
          if (leftString.length() > fWrapBreak)
            leftString = fLeftTruncate;
          rightString = writeTermToString(root.secondTerm());
          if (rightString.length() > fWrapBreak)
            rightString = fRightTruncate;
          
          if (leftString.length()+rightString.length() > fWrapBreak)
              rightString = fRightTruncate;

          return
              (leftString +
               root.fInfo +
               rightString);
        }

        case unary: {
      
        if (isNotMemberOf(root)){             //special case from set theory
           // System.out.print("write " + writeFormulaToString(term.fLLink));
        	leftString = writeInner(root.fRLink.firstTerm());
        	rightString = writeInner(root.fRLink.secondTerm());
            if (leftString.length() > fWrapBreak)
                leftString = fLeftTruncate;
            if (rightString.length() > fWrapBreak)
                rightString = fRightTruncate;

            if (leftString.length()+rightString.length() > fWrapBreak)
                rightString = fRightTruncate;
        	
        	
        	return
        	   leftString + strNotMemberOf + rightString;
        	
        }
        	
        
        rightString = writeInner(root.fRLink);
          if (rightString.length() > fWrapBreak)
            rightString = fRightTruncate;
          return
              (translateConnective(root.fInfo) +
               rightString);
        }

        case binary: {
          leftString = writeInner(root.fLLink);
          if (leftString.length() > fWrapBreak)
            leftString = fLeftTruncate;
          rightString = writeInner(root.fRLink);
          if (rightString.length() > fWrapBreak)
            rightString = fRightTruncate;
          
          if (leftString.length()+rightString.length() > fWrapBreak)
              rightString = fRightTruncate;
          
          return
              (leftString +
               translateConnective(root.fInfo) +
               rightString);

        }
        
        
        case modalKappa: 
        case modalRho:{                           // like prefix binary
            leftString = writeInner(root.fLLink);
            if (leftString.length() > fWrapBreak)
              leftString = fLeftTruncate;
            rightString = writeInner(root.fRLink);
            if (rightString.length() > fWrapBreak)
              rightString = fRightTruncate;
            
            if (leftString.length()+rightString.length() > fWrapBreak)
                rightString = fRightTruncate;
            
            return
                (translateConnective(root.fInfo) +
                 leftString +              
                 rightString);

          }
        
        
        case quantifier: {

          return
              writeQuantifierToString(root);
        }




        case typedQuantifier:
          return
              writeTypedQuantifierToString(root);

        case application: {      //lambdas do not call inner at all
          leftString = writeFormulaToString(root.fLLink);
          if (leftString.length() > fWrapBreak)
            leftString = fLeftTruncate;
          rightString = writeFormulaToString(root.fRLink);
          if (rightString.length() > fWrapBreak)
            rightString = fRightTruncate;
          
          if (leftString.length()+rightString.length() > fWrapBreak)
              rightString = fRightTruncate;


   //      leftString=isLambda(root.fLLink)? "("+leftString+")": leftString;


          return
              ("("+leftString +
        //       "[" +


        (fVerbose?"@":" ")+    // must write a blank in


        rightString +")"
//(isApplication(root.fRLink)? "("+rightString+")": rightString) //to get r assoc // no need all apps are bracketd
     //     +     "]");
          );

        }


        case lambda: {  //lambdas do not call inner at all
          String prefix = new String();
          String scope = new String();

          prefix = (translateConnective(root.fInfo) + root.lambdaVar() + ".");

          scope = writeFormulaToString(root.scope());

          if (scope.length() > fWrapBreak)
            scope = fScopeTruncate;
          return
         //     "("+
              (prefix + scope);
      //        +")";
        }
        
        case comprehension: { 
           	compString=writeComprehensionToString(root);
        	if (compString.length() > fWrapBreak)   // terms can be arbitrarily long
        		compString = fCompTruncate;

            return
            compString;

          }
        
        case pair:{

        		return
        		   writePairToString(root);}


        default:
          return strNull;
      }

}
}



public String writeInner (TFormula inner){   // just puts brackets around the inner stuff
// String leftString = new String();
//String rightString = new String();

  switch (inner.fKind) {
    case predicator:
    	if (isPredInfix(inner.fInfo))  // eg x<y x epsilon y etc
    		return
               ("("+writeFormulaToString(inner)+")");
    	else
    		return
    		   writeFormulaToString(inner);
    
    case unary:
        if (isNotMemberOf(inner)){             //special case from set theory
        	return
        	   ("("+writeInner(inner.fRLink.firstTerm()) + 
        	    strNotMemberOf + writeInner(inner.fRLink.secondTerm())+")");  	
        }
    	else
    		return
    		   writeFormulaToString(inner);// not special case same as default	
    case functor:
    case variable:

    case quantifier:
    case typedQuantifier:

    case lambda:
    case application:
    case comprehension:
    case pair:
              return
                  writeFormulaToString(inner);

    case binary:
    case modalKappa:
    case modalRho:
    case equality:

              return
                  ("("+writeFormulaToString(inner)+")");

    default:
      return
          strNull;

}
}


//NEED TO LOOK AT NEXT FOR INFIX, SET THEORY ETC.
public int indexOfMainConnective(TFormula root){   //if the whole formula is written to a string this tells of the index

if (root==null)
  return
      -1;
else{
String leftString;
String rightString = new String();

      switch (root.fKind) {

        case predicator:
        case functor:
        case variable:   //degenerate case
        case unary:
          return
              0;

        case equality: {
          leftString = writeTermToString(root.firstTerm());
          if (leftString.length() > fWrapBreak)
            leftString = fLeftTruncate;

          return
              leftString.length();
        }
        case binary: {
          leftString = writeInner(root.fLLink);
          if (leftString.length() > fWrapBreak)
            leftString = fLeftTruncate;
          return
              leftString.length();
        }
        case quantifier:
        case typedQuantifier:{

          return
              1;
        }

        default:
          return -1;
      }

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
      }

String writeListOfSetTheoryTermsToString (TFormula head)
{String outPutStr=strNull, tempStr=strNull;

boolean first=true;

while ((head!=null) && (outPutStr.length() < 128))
        {
	if (first)
		first=false;
	else
		outPutStr=outPutStr+',';
    tempStr= writeTermToString(head.fLLink);
    if (tempStr.length() > 96)
                     tempStr = "<term>";
    outPutStr= outPutStr+tempStr;
    head = head.fRLink;
        }
if (outPutStr.length() > 127)
           outPutStr = "<terms>";
   return outPutStr;
}

public String writeQuantifierToString(TFormula root){

          String prefix = new String();
          String scope = new String();

          if (root.fInfo.equals(String.valueOf(chUnique)))
            prefix = ("(" + String.valueOf(chExiquant) +
                      String.valueOf(chUnique) + root.quantVar() + ")");
          else
            prefix = ("(" + translateConnective(root.fInfo) + root.quantVar() + ")");

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
                      prefix = ("(" + String.valueOf(chExiquant) +
                                String.valueOf(chUnique) + root.quantVar() + ":"+typeStr+")");
                    else
                      prefix = ("(" + translateConnective(root.fInfo) + root.quantVar() + ":"+typeStr+")");

                    scope = writeInner(root.scope());

                    if (scope.length() > fWrapBreak)
                      scope = fScopeTruncate;
                    return
                        (prefix + scope);
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

public String writeComprehensionToString (TFormula term){
	
	/* a comprehension can have four forms {} {a,b,c} {x:<scope} {x|<scope} */
	
	if (term.fLLink==null)  //first two forms
		return
		"{"+ (term.fRLink!=null?writeListOfSetTheoryTermsToString(term.fRLink):"") +"}";
	else{
		
	//	System.out.print("write " + writeFormulaToString(term.fLLink));
		
		return
		"{"+ writeFormulaToString(term.fLLink) + term.fInfo + writeInner(term.scope())+"}";
	}

}

public String writePairToString (TFormula term){
	
	/* <a,b> */
		
		return
		"<"+ writeFormulaToString(term.firstTerm()) + "," + writeFormulaToString(term.secondTerm())+">";
	}


public String writeTermToString (TFormula term){

/*Sometimes a term can have an insert marker in front of it eg >a or (1 + >x)
This marker is temporarily prefixed into a terms fInfo field. In that position it
 interferes with our tests whether the term is prefix, infix etc.
 */

/*In set theory it is possible for a term to be a comprehension and that is a special case */

if (term.fKind==comprehension){

	return
	   writeComprehensionToString(term);}

if (term.fKind==pair){

	return
	   writePairToString(term);}

//otherwise

      {String temp;
       String prefix="";
       String fInfo = term.fInfo;

       if (fInfo.charAt(0)==chInsertMarker){
         prefix+=chInsertMarker;
         fInfo=fInfo.substring(1);
       }

      if (term.fRLink == null)
              {
              return
                  prefix+fInfo; 			/*atomic functor*/
              }
      else
              {
              if (isFunctorInfix(fInfo.charAt(0))) /*infix, binary*/
                      return
                          prefix+
                          (chSmallLeftBracket+
                                      writeTermToString((term.firstTerm()))+
                                      fInfo+
                                      writeTermToString(term.secondTerm())+
                                      chSmallRightBracket);
              else
                      {

                        if (isFunctorPostfix(fInfo.charAt(0))) /*postfix, successor*/
                          return
                              prefix+
                              (writeListOfTermsToString(term.fRLink) +
                                  fInfo);
                        else {

                          temp = writeListOfTermsToString(term.fRLink);

                          /*
                           if FunctorPostfix(inf) then   (*postfix*)
                                  outPutStr := concat(tempStr, inf)
                                 else
                           outPutStr := concat(inf, chSmallLeftBracket, tempStr, chSmallRightBracket);

                           */

                          return
                              prefix+
                              (fInfo + chSmallLeftBracket + temp +
                                  chSmallRightBracket);
                        }
                      }
              }
      }
}




/*********************** End of from TFormula ******************/








public void writePredicate(TFormula predicateForm)
      {
      if (isPredInfix(predicateForm.fInfo))
                {
          write(chSmallLeftBracket);
          writeTerm(predicateForm.firstTerm());
          write(predicateForm.fInfo);
              writeTerm(predicateForm.secondTerm());
               write(chSmallRightBracket);
              }
        else
                {
          write(predicateForm.fInfo);
          if (predicateForm.fRLink != null)
                   writeListOfTerms(predicateForm.termsList());
                }
      }



public void writeTerm(TFormula termForm)
      {
      if (termForm.fRLink == null)
              write(termForm.fInfo); 			/*atomic functor*/

      else
              {
              if (functorInfix(termForm.fInfo)) /*infix, binary*/
                      {
                      write(chSmallLeftBracket);
                      writeTerm(termForm.firstTerm());
                      write(termForm.fInfo);
                      writeTerm(termForm.secondTerm());
                      write(chSmallRightBracket);
                      }
              else
                      {
                      if (functorPostfix(termForm.fInfo))   /*postfix*/
                              {
                              writeListOfTerms(termForm.fRLink);
                             write(termForm.fInfo);
                             }
                    else
                            {
                            write(termForm.fInfo);
                            write(chSmallLeftBracket);
                            writeListOfTerms(termForm.fRLink);
                            write(chSmallRightBracket);
                            }

                      }

              }

      }


public void writeListOfTerms(TFormula head)
      {
      while (head!=null)
              {
              writeTerm(head.fLLink);
          head = head.fRLink;
              }
      }

      public String writeListOfFormulas(TFormula head)
              {String output="";
              while (head!=null)
                      {
                      output+=writeFormulaToString(head.fLLink);
                      head = head.fRLink;
                      if (head!=null)
                        output+=", ";
                      }
      return
    output;}



//must call this prior to parse

private void initializeInputBuffer(){

fInputBuffer= new char[3];
fInputBuffer[0]=chBlank;
fInputBuffer[1]=chBlank;
fInputBuffer[2]=chBlank;

for (int i=0;i<3;i++){
  char tempCh;

  try {
       tempCh = (char) fInput.read();
       fInputBuffer[i]=tempCh;
  }
  catch (IOException e){
       //{System.err.println("Exception thrown in initializeInput");
  }

fCurrCh=fInputBuffer[0];
fLookAheadCh=fInputBuffer[1];
fLookTwoAheadCh=fInputBuffer[2];
}
}

protected void skip(int i){   // it would be better to have a 3 char input/lookahead buffer

char tempCh=chBlank;

while ((i>0)/*&&(tempCh!=EOF) don't test for this because skip(2) at EOF still needs to advance current   */)
    {
    try {
      fInputBuffer[0]=fInputBuffer[1];
      fInputBuffer[1]=fInputBuffer[2];
      tempCh = (char) fInput.read();
      fInputBuffer[2]=tempCh;
    }
    catch (IOException e)
        {/*System.err.println("Exception thrown in skip" not an error just EOF);*/
         fInputBuffer[2]=tempCh;
        }
    i = i - 1;

    }

fCurrCh=chBlank;
fLookAheadCh=chBlank;
fLookTwoAheadCh=chBlank;

if (fInputBuffer[0]!=EOF)
 fCurrCh=fInputBuffer[0];
if (fInputBuffer[1]!=EOF)
 fLookAheadCh=fInputBuffer[1];
if (fInputBuffer[2]!=EOF)
 fLookTwoAheadCh=fInputBuffer[2];

      }



protected void skipSpace(){

/*some input has spaces, we may want to go past these to real content, but not EOF */


while (fCurrCh==chBlank&&fInputBuffer[0]!=EOF)
 skip(1);
      }






public void setLookAheads(){
  boolean good = false;
  fLookAheadCh=chBlank;
  fLookTwoAheadCh=chBlank;
  try
     {


     fInput.mark(Integer.MAX_VALUE);

     fLookAheadCh=(char)fInput.read();

     if (fLookAheadCh==EOF)			//eof
         {fLookAheadCh=chBlank;
         fLookTwoAheadCh=chBlank;
         fInput.reset();
         return;}


      fLookTwoAheadCh=(char)fInput.read();        //

      if (fLookTwoAheadCh==EOF)			//eof
           {
           fLookTwoAheadCh=chBlank;
           fInput.reset();
           return;}

      fInput.reset();
     }


      catch (IOException e)
              {
                System.err.println("Exception thrown in setLookAheads");
              }

}



void writeError(String message)
      {
        fParserErrorMessage.write(message);
      }

void write(char aChar)
      {
      try
              {
              fWrittenOutput.write(aChar);
              }
      catch (IOException e)
              {
              }

      }

void write(String message)
      {
      try
              {
              fWrittenOutput.write(message);
              }
      catch (IOException e)
              {
              }

      }



boolean infixPredicate(TFormula root)
  /*<term1> =<term2> or whatever*/
  /*<term1> epsilon <setTheoryterm2> or whatever*/
  /*<term1> not epsilon <setTheoryterm2>*/
  {
  TFormula leftTerm, rightTerm;

  leftTerm = new TFormula();
  if (this.term(leftTerm))
      root.appendToFormulaList(leftTerm);
  else
      return ILLFORMED;

 if (isPredInfix(fCurrCh+""))                      //oct 08

      {
      if (fCurrCh == chEquals)
          root.fKind = equality;
      else
          root.fKind = predicator;

      root.fInfo = toInternalForm(fCurrCh);//String.valueOf(fCurrCh);    /*=,< or whatever}*/

      skip(1);
      }
  else
      {

      writeError("(*The character '"+fCurrCh+"' should be one of " +strInfixPreds + " .*)");

      return ILLFORMED;
      }


  rightTerm = new TFormula();
  
  // if we are doing set theory here we want the right term to be a set theoretic term
  // otherwise any term will do
  
  if (!isPredInfixSetTheory(root.fInfo))   //ordinary
  
  	{if (this.term(rightTerm))
      	{
  		root.appendToFormulaList(rightTerm);
  		return WELLFORMED;
      	}
  	else
  		return ILLFORMED;
  	}
  else
  	//{if (this.termSetTheory(rightTerm))    // set theory  HERE
      {if (this.term(rightTerm))            // Jan09, now parsing anything
  		{
  		
  		root.appendToFormulaList(rightTerm);
  		
  		if (root.fInfo.equals(strNotMemberOf)){ // special case, in set theory not member is abbreviation
  			                                    // we make it into negation to get proper semantics for rules
  			root.fInfo=strMemberOf;
  			TFormula rLink= root.copyFormula();
  			TFormula negation= new TFormula(unary,String.valueOf(chNeg),null,rLink);
  		
  			root.assignFieldsToMe(negation);   //surgery
  		
  		
  		}
  		
  		
  		return WELLFORMED;
  		}
  	else
  		return ILLFORMED;
  	}
  }


class Filter{                  //this is for filtering characters in the input
 boolean badChar(char character){
   if (character==chBlank)
     return
         true;
   else
     return
       false;
 }
}




/*********************  Lambda calculus ***********************/

private boolean lambdaWffCheck (TFormula root, ArrayList newValuation){

 /*I have rewritten this for a different grammar June 22 08. The old one
 is below*/


/* {RECURSIVE DESCENT WITH PRECEDENCE AND LEFT ASSOCIATION}

    <expression> := <name> | <function> | <application> | <defined constant>
    <function> :=  lambda  <name>.<scope>
    <application> := (<function expression> <argument expression>)
    <scope>:= <expression>
    <function expression>:= <expression>
    <argument expression>:= <expression>
    <name>:= any string of characters starting with a lower case letter
    <defined constant>:= any string of characters starting with an upper case letter


*/

    skipSpace();  //leading


    if (newExpression(root))
       {

       skipSpace(); // This added Feb 2013 should be no junk on end  ,no 'cos want blanks for applications

       if (fCurrCh == chBlank)
          return
              WELLFORMED;
       else
           {
           if (fCurrCh == chLSqBracket)  // {valuation}
               {
               if (getValuation(newValuation))
                 return
                     WELLFORMED;
               else
                 return
                     ILLFORMED;
               }
           else{
               writeError(CR+"(*The extra character '"+
                                    fCurrCh+
                                    "' should be a blank.*)" );
               return
                   ILLFORMED;
               }
           }
           }
       else
        return
            ILLFORMED;
   }


private String readName(char start){
String outStr="";

/*our identifiers start  */

if (isLambdaName(start)){ outStr=""+start;

  while ((fLookAheadCh != chBlank)&&
         (fLookAheadCh != EOF)&&
      (fLookAheadCh != chLambda)&&
      (fLookAheadCh != '.')&&
      (fLookAheadCh != chSmallLeftBracket)&&
      (fLookAheadCh != chSmallRightBracket))


  {
    skip(1);
    if (fCurrCh!=chBlank) // never occurs anyway
      outStr += fCurrCh;
  }
}

return
    outStr;
}


private boolean newExpression (TFormula root)
               {
     //<expression> := <name> | <function> | <application> | (expression)





   if (isLambdaName(fCurrCh)) /* tests whether name*/
    //string starting lowercase
                       {
                         String identifier = readName(fCurrCh);

                         if (!identifier.equals("")) {

                           root.fInfo = String.valueOf(identifier);
                           root.fKind = variable; /*treat lambda names as variables*/

                           skip(1); /*looking at next item*/

                           return
                               WELLFORMED; // atom

                         }
                       }
   if (isLambdaConstant(fCurrCh)) /* tests whether functor ie defined constant*/
                                       {
                                     root.fInfo = String.valueOf(fCurrCh);
                                     root.fKind = functor; /*treat lambda names as variables*/

                                     skip(1); /*looking at next item*/


                                       return
                                           WELLFORMED; // atom


                   }




   if (fCurrCh == chSmallLeftBracket) {

//<application> := (<function> <argument> )

      skip(1); /*looking at next item*/
      skipSpace();

      TFormula function= new TFormula();

      if (!newExpression(function))
        return
           ILLFORMED;

      if (!((fCurrCh == chBlank)|| (fCurrCh == chAt)) )
        return
            ILLFORMED;

      skipSpace();

      TFormula argument= new TFormula();

      if (!newExpression(argument))
        return
            ILLFORMED;


      TFormula newRoot = new TFormula(
                      application,
                      "",
                      function, //for root
                      argument);

      root.assignFieldsToMe(newRoot);

    //  skip(1); /*looking at next item*/
      skipSpace();

      if (fCurrCh!=chSmallRightBracket){
        writeError("(*The character '" + fCurrCh + "' should be ')'.*)");
        return
            ILLFORMED;
      }

      skip(1); /*the small right-bracket*/

        return
            WELLFORMED; // atom
    }



    //<expression> := lambda <name> . <expr>

    if (fCurrCh == chLambda) {

      skip(1); /*looking at next item*/
      skipSpace();

      if (isLambdaName(fCurrCh)){ /* tests whether functor*/

        String identifier = readName(fCurrCh);

        if (!identifier.equals("")) {


          {
            TFormula nameRoot = new TFormula();
            TFormula scope = new TFormula();

            nameRoot.fInfo = identifier;
            nameRoot.fKind = variable; /*treat lambda names as variables*/

            skip(1); /*looking at next item, the period*/
            skipSpace();

            if (fCurrCh != '.')
              return
                  ILLFORMED;

            skip(1); /*looking at next item, the scope expression*/
            skipSpace();

            if (!newExpression(scope))
              return
                  ILLFORMED;

            // skip(1); /*looking at next item*/

            root.fKind = lambda; // alter caller
            root.fInfo = String.valueOf(chLambda);
            root.fLLink = nameRoot;
            root.fRLink = scope;

            return
                WELLFORMED; // atom
          }
          }
        }
    }


                   return ILLFORMED;
            }




/*************** Old Lambda Parsing *******************************************/




public boolean lambdaChangeVariable(TFormula root){
//this is to avoid capturing, it changes all the function variables to new ones


switch (root.fKind) {

  case functor:
    break;

  case application:
    lambdaChangeVariable(root.fLLink);
    lambdaChangeVariable(root.fRLink);
    break;

  case lambda:
    TFormula scope=root.scope();
    TFormula var=root.lambdaVarForm();

    lambdaChangeVariable(scope);

    char newVariable=nthNewLambdaName(1,lambdaNamesInFormula(root));

    if (newVariable==' ')
      return
          false;                     //ran out

    TFormula newVar = new TFormula(
                                variable,
                                ""+newVariable,
                                  null,
                                  null);

    scope.subTermVar(scope, newVar, var);

    var.fInfo=""+newVariable;

      break;


}

return
    true;

}



/*******************  End of lambda ***************************/

int subscriptCharToNum(char subscript){

	if (subscript==chSubscript0)
		return
		intSubscript0;
	if (subscript==chSubscript1)
		return
		intSubscript1;
	if (subscript==chSubscript2)
		return
		intSubscript2;
	if (subscript==chSubscript3)
		return
		intSubscript3;
	if (subscript==chSubscript4)
		return
		intSubscript4;
	if (subscript==chSubscript5)
		return
		intSubscript5;
	if (subscript==chSubscript6)
		return
		intSubscript6;
	if (subscript==chSubscript7)
		return
		intSubscript7;
	if (subscript==chSubscript8)
		return
		intSubscript8;
	if (subscript==chSubscript9)
		return
		intSubscript9;
	
return
   -1;
}

int subscriptToNumber(String subString)
//this converts a string of subscripts to numbers
{int length=subString.length();

if (length<1)
	return
	-1;
int index=length-1;

int total=subscriptCharToNum(subString.charAt(index));
int base=10;

for(int i=index-1;i>-1;i--){
	total+=base*subscriptCharToNum(subString.charAt(i));
	base*=10;
}

return
   total;	
}

String numberToSubscript(int num)
//this converts a string of subscripts to numbers
{
	if (num<0)
		return
		"";
	
	String numStr= Integer.toString(num);
	numStr=numStr.replaceAll("0",chSubscript0+"");
	numStr=numStr.replaceAll("1",chSubscript1+"");
	numStr=numStr.replaceAll("2",chSubscript2+"");
	numStr=numStr.replaceAll("3",chSubscript3+"");
	numStr=numStr.replaceAll("4",chSubscript4+"");
	numStr=numStr.replaceAll("5",chSubscript5+"");
	numStr=numStr.replaceAll("6",chSubscript6+"");
	numStr=numStr.replaceAll("7",chSubscript7+"");
	numStr=numStr.replaceAll("8",chSubscript8+"");
	numStr=numStr.replaceAll("9",chSubscript9+"");
return	
 numStr;	
}






}
/****************** OLD ******************************/

/*
private boolean termSetTheorySecondary (TFormula root){
	//{<termsecondary>::= <termprimary><nonempty list of '''>|<termprimary> . <termsecondary>| <termprimary>}
	 return
	     termSetTheoryPrimary(root)&&
	     postfixSuccessor(root)&&
	     infix2SetTheory(root);
	}

private boolean termSetTheoryTertiary (TFormula root){
	//{<termtertiary>::= <termsecondary> + <termtertiary>| <termsecondary>}

	 return
	     termSetTheorySecondary(root)&&
	     infix3SetTheory(root);
	     } */

/*private boolean term4Tertiary (TFormula root){
	//{<term4ary>::= <termtertiary> intersect <term4ary>| <termtertiary>}

	 return
	     termSetTheorySecondary(root)&&
	     infix3SetTheory(root);
	     } */


/*
private boolean infix2SetTheory(TFormula root) {   
	// <termprimary> . <termsecondary>
	
	/* what this is going to read is a x b
and we have already read a as root.  So, we will read b. Then create a x node
and make that the root and put a and b in its termlist*/
	
	/*we come in here with a well formed term, say 1. What this needs to do is to
	 * swallow any multiplication sign returning WELLFORMED if there is none
	 * So, we enter with, say, 1.2<next> and leave
	 * with fChrrCh looking at <next>
	 */	
/*	
boolean wellFormed=true;

if (infixBinFun2SetTheory(fCurrCh)){
   TFormula newRoot = new TFormula(TFormula.functor,
                   String.valueOf(fCurrCh),  //probably mult symbol
                   null,
                   null);


   skip(1); /*the mult

   TFormula rightTerm=new TFormula();

   wellFormed=termSetTheorySecondary(rightTerm);

   if (wellFormed){
      TFormula  leftTerm = new TFormula(root.getKind(),
                                     root.getInfo(),
                                     root.getLLink(),
                                     root.getRLink());
      newRoot.appendToFormulaList(leftTerm);
      newRoot.appendToFormulaList(rightTerm);

      root.assignFieldsToMe(newRoot);   //surgery
   }
}

return
    wellFormed;
}
 */

/*
private boolean infix3SetTheory(TFormula root) {   /* what this is going to read is a + b
    and we have already read a as root.  So, we will read b. Then create a + node
    and make that the root and put a and b in its termlist
	
	//<termsecondary> union <termtertiary>
   boolean wellFormed=true;

   if (infixBinFun3SetTheory(fCurrCh)){
     TFormula newRoot;

     newRoot = new TFormula(TFormula.functor,
                        String.valueOf(fCurrCh),  //probably add symbol
                        null,
                        null);


     skip(1); /*the union

     TFormula rightTerm=new TFormula();

     wellFormed=termSetTheoryTertiary(rightTerm);

     if (wellFormed){
        TFormula  leftTerm = new TFormula(root.getKind(),
                                          root.getInfo(),
                                          root.getLLink(),
                                          root.getRLink());
        newRoot.appendToFormulaList(leftTerm);
        newRoot.appendToFormulaList(rightTerm);

        root.assignFieldsToMe(newRoot);   //surgery
     }
   }

   return
         wellFormed;
} */




