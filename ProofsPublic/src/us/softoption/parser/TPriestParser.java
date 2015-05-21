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



public class TPriestParser extends TParser{

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
  
    public TPriestParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
 

    fPossibleWorlds = "0123456789"+"klmnopqrstuvwxyzabcdefghij";

  }

  @Override
  public boolean term (TFormula root, Reader aReader){    // sometimes called externally to parse term
		TFormula cCroot;
		
		  if (fCCParser==null)
			  fCCParser=new CCParser(aReader,PRIEST);
		  else
			  fCCParser.reInit(aReader,PRIEST);
		 
		try {
			cCroot= fCCParser.term();
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
public boolean wffCheck (TFormula root, Reader aReader){
	  TFormula cCroot;
	  
	  if (fCCParser==null)
		  fCCParser=new CCParser(aReader,PRIEST);
	  else
		  fCCParser.reInit( aReader,PRIEST);

	  try {
	  		cCroot= fCCParser.wffCheck();
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
  




  /*************  Access relations for possible worlds *********************/

          /* an Access relation has special meaning the the modal logic, etrees */

   @Override 
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

  @Override
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

    @Override
public String startWorld(){   // for premises of tree etc.
            return
                "0";
}

/********************** end of Access relation **************************/




  @Override
  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }
  @Override
  public String renderAnd() {
	    return
	        String.valueOf(chAnd);
	  }
    @Override
  public String renderImplic() {
	    return
	        String.valueOf(chImplic);
	  }
    @Override
  public String renderEquiv() {
	    return
	        String.valueOf(chEquiv);
	  }
  @Override
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


  @Override
  boolean isPredInfix (String inf)

    /* note this allows in more for Access than numerals ie 0r1 is wff so is rrr  */

        {
       if (inf.equals(fAccessPred) )
         return
             true;
       else

          return super.isPredInfix(inf);  // = or <
        }

  
  @Override
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

  @Override
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

                      scope = writeInner(root.scope());

                      if (scope.length() > fWrapBreak)
                        scope = fScopeTruncate;
                      return
                          (prefix + scope);
                  }        

}




/************************** End of Version *************************************/
