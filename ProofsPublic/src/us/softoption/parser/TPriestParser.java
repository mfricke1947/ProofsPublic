package us.softoption.parser;

//8/9/06


import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotSign;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.parser.TFormula.functor;
import static us.softoption.parser.TFormula.predicator;

import java.io.Reader;
import java.util.ArrayList;



public class TPriestParser extends /*Howson*/TParser{

  /*We want several different parsers of similar structure; we define generic procedures
   which are called in the normal way.  Most documents and windows have fParser fields to
   ensure the correct reading and writing.*/


  /*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
   but Javas regular expression powers allow this to be done to the input before it ever gets to skip
   or to the parser*/
	
/* Priest does not bracket the quantifiers, nor the arguments
 * 
 * thus AllxFxy and he uses hook
 *  	
 */
	
	

  /*constants*/
	public static byte PRIEST= CCParser.BRACKETFREEQUANTS;


  String fAccessPred="r";  // we want to have 0r1 as wff to mean world 0 can access world 1
  char chAccess='r';  // we want to have 0r1 as wff to mean world 0 can access world 1

  public TPriestParser() {
  java.io.StringReader sr = new java.io.StringReader( "" );
	  
	  fCCParser=new CCParser(///*new java.io.BufferedReader(sr)*/ sr,
			  sr,
			  
			  PRIEST);  
	  
	  
    fPossibleWorlds = "0123456789"+"klmnopqrstuvwxyzabcdefghij";  // Priest wants numerals first

  }
  
  public boolean term (TFormula root, Reader aReader){    // sometimes called externally to parse term
		TFormula cCroot;
		
		  if (fCCParser==null)
			  fCCParser=new CCParser(/*new java.io.BufferedReader(aReader)*/ aReader,PRIEST);
		  else
			  fCCParser.reInit(/*new java.io.BufferedReader(aReader)*/ aReader,PRIEST);
		 
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
} 



public boolean wffCheck (TFormula root, /*ArrayList<TFormula> newValuation,*/Reader aReader){
	  TFormula cCroot;
	  
	  if (fCCParser==null)
		  fCCParser=new CCParser(/*new java.io.BufferedReader(aReader)*/ aReader,PRIEST);
	  else
		  fCCParser.reInit(/*new java.io.BufferedReader(aReader)*/ aReader,PRIEST);

	  try {
	  		cCroot= fCCParser.wffCheck();
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
  

  public TPriestParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
 

    fPossibleWorlds = "0123456789"+"klmnopqrstuvwxyzabcdefghij";

  }


  /*************  Access relations for possible worlds *********************/

          /* an Access relation has special meaning the the modal logic, etrees */

  public TFormula makeAnAccessRelation(String world1,String world2){      /*predicate 1r2*/
             TFormula newnode = new TFormula();  // lot of running down the end in this one


             newnode.fKind = predicator;
             newnode.fInfo = "r";




             {TFormula world=new TFormula();
               world.fInfo = (world1.length()>0)?world1.substring(0,1):"?";
               world.fKind = functor;

               newnode.append(world);

               world=new TFormula();
               world.fInfo = (world2.length()>0)?world2.substring(0,1):"?";
               world.fKind = functor;

                newnode.append(world);

}



             return
                 newnode;
           }


  public String getAccessRelation(TFormula root){      /*predicate Access(w1,w2)*/

          String outStr="";

            if ((root!=null)&&
                root.fKind == predicator&&
                "r".equals(root.fInfo)){

               TFormula temp = root.nthTopLevelTerm(1);
               TFormula temp2 = root.nthTopLevelTerm(2);

               if (temp!=null&&temp2!=null){
                 outStr=outStr+temp.fInfo+temp2.fInfo;

                 if (outStr.length()==2)
                    return outStr;
                 else
                    return "";
               }
             }


           return
               outStr;
          }

public String startWorld(){   // for premises of tree etc.
            return
                "0";
}

/********************** end of Access relation **************************/





  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }

  public String renderAnd() {
	    return
	        String.valueOf(chAnd);
	  }
  public String renderImplic() {
	    return
	        String.valueOf(chImplic);
	  }
  public String renderEquiv() {
	    return
	        String.valueOf(chEquiv);
	  }

  public String translateConnective(String connective) {

    if (connective.equals(String.valueOf(chNeg)))
      return
          String.valueOf(chNotSign);
    if (connective.equals(String.valueOf(chAnd)))
        return
        	String.valueOf(chAnd);
    if (connective.equals(String.valueOf(chImplic)))
        return
        	String.valueOf(chImplic);
    if (connective.equals(String.valueOf(chEquiv)))
        return
        	String.valueOf(chEquiv);

    return
        connective;
  }



  boolean isPredInfix (String inf)

    /* note this allows in more for Access than numerals ie 0r1 is wff so is rrr  */

        {
       if (inf.equals(fAccessPred) )
         return
             true;
       else

          return super.isPredInfix(inf);  // = or <
        }

  /*
        boolean infixPredicate(TFormula root)
            /*<term1> =<term2> or <term1> <<term2> or <term1>r<term2>whatever*/
 /*           {
           TFormula leftTerm, rightTerm;

            leftTerm = new TFormula();
            if (this.term(leftTerm))
                root.appendToFormulaList(leftTerm);
            else
                return ILLFORMED;

           if (fCurrCh==chEquals||fCurrCh==chLessThan||fCurrCh==chAccess)
                {
                if (fCurrCh == chEquals)
                    root.fKind = equality;
                else
                    root.fKind = predicator;

                root.fInfo = String.valueOf(fCurrCh);    /*=,< or whatever}*/

/*                skip(1);
                }
            else
                {

                writeError("(*The character '"+fCurrCh+"' should be = or < or r.*)");

                return ILLFORMED;
                }


            rightTerm = new TFormula();
            if (this.term(rightTerm))
                {
                root.appendToFormulaList(rightTerm);
                return WELLFORMED;
                }
            else
                return ILLFORMED;
            }

*/

public void writePredicate(TFormula predicateForm)
                    {
                    if (fAccessPred.equals(predicateForm.fInfo))
                              {
                    //    write(chSmallLeftBracket);
                        writeTerm(predicateForm.firstTerm());
                        write(predicateForm.fInfo);
                            writeTerm(predicateForm.secondTerm());
                    //         write(chSmallRightBracket);
                            }
                      else
                              {
                       super.writePredicate(predicateForm);
                              }
        }


        public String writePredicateToString (TFormula predicate){

        if (fAccessPred.equals(predicate.fInfo))
            {
            return
          //     (TConstants.chSmallLeftBracket+
               writeTermToString(predicate.firstTerm())+
               predicate.fInfo+
               writeTermToString(predicate.secondTerm());
         //      TConstants.chSmallRightBracket);

            }

        else
                {
            return

            super.writePredicateToString(predicate);

            }
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

                      scope = writeInner(root.scope());

                      if (scope.length() > fWrapBreak)
                        scope = fScopeTruncate;
                      return
                          (prefix + scope);
                  }        

}




/************************** End of Version *************************************/
