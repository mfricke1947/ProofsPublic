package us.softoption.parser;



import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chEquals;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.chUnique;
import static us.softoption.infrastructure.Symbols.strNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import us.softoption.infrastructure.Symbols;
import us.softoption.infrastructure.TUtilities;

//12/30/08 quick scan through,  ok for now

public class TFormula{
	

/*These nodes are used to represent formulas, predicators, functors, expressions,
lists of formulas, and lists of other things. The kind tells what, the info is
the info. The l and r links are the sub-formulas. If P is a predicator or a functor
then the rlink is the list of terms; for example P<term1><term2> has P on the root
and <term1><term2> in a list on rlink. For quantified formulas the Llink is
the variable and the Rlink the scope. When these are used for lists the
LLink points to the item and the Rlink points to next node. There are some list
processing functions further down*/

/*We normally let a parser write a true logic formula out. This is because these TFormulas are
  building blocks and the different logical systems use them in different ways. Only the
 parser that deals with a logical systems knows exactly how a formula should be written.*/

/*types*/

   public static final short binary=1; // this and following used to identify the kind of node
   public static final short kons=2;
   public static final short equality=3;
   public static final short functor=4;
   public static final short predicator=5;
   public static final short quantifier=6;
   public static final short unary=7;
   public static final short variable=8;         // it's the parser which decides what the type is
   public static final short typedQuantifier=9;
   public static final short application=10;     //for lambda calculus
   public static final short lambda=11;
   public static final short comprehension=12;   //for set theory
   public static final short pair=13;            // ordered pair for set theory
   public static final short modalKappa=14;      // epistemic logic
   public static final short modalRho=15;      // epistemic logic

 /*booleans*/

   static final boolean kAllowDuplicates = true;

/*predefineds*/

   public static TFormula fAbsurd;
   public static TFormula fTruth;
   public static TFormula fFalsehood;
   public static TFormula fTop;
   public static TFormula fBottom;

        static{
          fAbsurd=makeAnAbsurd();
          fTruth=makeATruth();
          fFalsehood=makeAFalsehood();
          fTop=makeATop();
          fTop=makeABottom();
        }


/*fields*/

    public short fKind=kons;
    public String fInfo=strNull;
    public TFormula fLLink=null;
    public TFormula fRLink=null;



public TFormula(){

}

public TFormula(short kind, String info, TFormula Llink, TFormula Rlink){
   fKind=kind;
   fInfo=info;
   fLLink=Llink;
   fRLink=Rlink;
}



public void setInfo(String info){
  fInfo=info;
}


public void setKind(short kind){
  fKind=kind;
}

public void setLLink(TFormula left){
  fLLink=left;
}

public void setRLink(TFormula right){
  fRLink=right;
}


public String getInfo (){
  return
      fInfo;
}


public short getKind(){
  return
      fKind;
}

public TFormula getLLink(){
  return
      fLLink;
}

public TFormula getRLink(){
  return
      fRLink;
}

/********* Field 'setters' ***************************/

public void assignFieldsToMe(TFormula newnode){
   setKind(newnode.getKind());     // alter the caller, workaround for the VAR param in Pascal
   setInfo(newnode.getInfo()); 
   setLLink(newnode.getLLink()); 
   setRLink(newnode.getRLink()); 
}



 /*********************  Special Predefineds Static ***************************************/

/* We need to be able to parse these, for example 'Absurd' into a formula */

 public static TFormula makeAnAbsurd(){      /*predicate P<term1> <term2>... */
   TFormula newnode = new TFormula();  // lot of running down the end in this one


   newnode.fKind = predicator;
   newnode.fInfo = "A";

   TFormula termnode = new TFormula();
   termnode.fInfo = "b";
   termnode.fKind = functor;
   newnode.append(termnode);

   termnode = new TFormula();
   termnode.fInfo = "s";
   termnode.fKind = functor;
   newnode.append(termnode);

   termnode = new TFormula();
   termnode.fInfo = "u";
   termnode.fKind = functor;
   newnode.append(termnode);

   termnode = new TFormula();
   termnode.fInfo = "r";
   termnode.fKind = functor;
   newnode.append(termnode);

   termnode = new TFormula();
   termnode.fInfo = "d";
   termnode.fKind = functor;
   newnode.append(termnode);

   return
       newnode;
 }

 public static TFormula makeAFalsehood(){      /*predicate P<term1> <term2>... */
	   TFormula newnode = new TFormula();  // lot of running down the end in this one


	   newnode.fKind = predicator;
	   newnode.fInfo = "F";

	   TFormula termnode = new TFormula();
	   termnode.fInfo = "a";
	   termnode.fKind = functor;
	   newnode.append(termnode);

	   termnode = new TFormula();
	   termnode.fInfo = "l";
	   termnode.fKind = functor;
	   newnode.append(termnode);

	   termnode = new TFormula();
	   termnode.fInfo = "s";
	   termnode.fKind = functor;
	   newnode.append(termnode);

	   termnode = new TFormula();
	   termnode.fInfo = "e";
	   termnode.fKind = functor;
	   newnode.append(termnode);


	   return
	       newnode;
	 }

public static TFormula makeATruth(){      /*predicate P<term1> <term2>... */
  TFormula newnode = new TFormula();  // lot of running down the end in this one


  newnode.fKind = predicator;
  newnode.fInfo = "T";

  TFormula termnode = new TFormula();
  termnode.fInfo = "r";
  termnode.fKind = functor;
  newnode.append(termnode);

  termnode = new TFormula();
  termnode.fInfo = "u";
  termnode.fKind = functor;
  newnode.append(termnode);

  termnode = new TFormula();
  termnode.fInfo = "e";
  termnode.fKind = functor;
  newnode.append(termnode);


  return
      newnode;
}

public static TFormula makeATop(){      /*T */
	return
	   new TFormula(predicator,Symbols.strDownTack,null,null);  // lot of running down the end in this one
	}

public static TFormula makeABottom(){      /*|*/
	return
	   new TFormula(predicator,Symbols.strUpTack,null,null);  // lot of running down the end in this one
	}

public boolean isSpecialPredefined(){

  return (equalFormulas(this,fAbsurd)||
          equalFormulas(this,fTruth)||
          equalFormulas(this,fFalsehood)||
          equalFormulas(this,fTop)||
          equalFormulas(this,fBottom));

}

/************************* Other Static methods ****************************/

public static void subTermVar(TFormula root, TFormula termForm, TFormula variForm){
	/*  {This takes a formula, substitutes a term for a variable in the}
	{original, changing it. This will work for non-variable kind}
	{variables to substitute one functor for another, but it is usually}
	{called with variForm being a variable.} */

	  TFormula temp,tempLLink,lLink,rLink;

	if (!equalFormulas(termForm,variForm)&&(root!=null)){

	  if (equalFormulas(root,variForm)){        // the actual substitution
	    termForm=termForm.copyFormula();

	    root.fKind=termForm.fKind;             // altering root, var parameters not available in Java
	    root.fInfo=termForm.fInfo;
	    root.fLLink=termForm.fLLink;
	    root.fRLink=termForm.fRLink;
	  }
	  else{
	    switch (root.fKind){
	        case variable:
	           break;

	         case predicator: // cchange here from Pascal, rolled in with functor
	         case equality:


	         case functor:  //{watchout for sequential substitution! I hope I have avoided it}

	          temp = root.fRLink;
	          while (temp !=null){
	            tempLLink = temp.fLLink;
	            subTermVar(tempLLink, termForm, variForm);
	            temp.fLLink = tempLLink;
	            temp = temp.fRLink;
	            }

	           break;

	          case unary:
	            rLink = root.fRLink;
	            subTermVar(rLink,termForm, variForm);
	            root.fRLink = rLink;
	            break;


	          case binary:
	          case application:

	            rLink = root.fRLink;
	            lLink = root.fLLink;
	            subTermVar(lLink, termForm, variForm);
	            subTermVar(rLink, termForm, variForm);
	            root.fRLink = rLink;
	            root.fLLink = lLink;
	            break;

	          case quantifier:
	          case typedQuantifier:

	            if (!equalFormulas(variForm, root.quantVarForm())){
	              /*{if the subs variable is distinct from the quantified one} */

	              rLink = root.fRLink;
	              subTermVar(rLink, termForm, variForm);
	              root.fRLink = rLink;
	            }

	            break;

	          case lambda:

	            if (!equalFormulas(variForm, root.lambdaVarForm())){
	              /*{if the subs variable is distinct from the quantified one} */

	              rLink = root.fRLink;
	              subTermVar(rLink, termForm, variForm);
	              root.fRLink = rLink;
	            }

	            break;

	        default: ;
	      }

	  }


	}


	}



	public static void interpretFreeVariables(ArrayList valuation, ArrayList listOfFormulas){ // does surgery?

	    if (listOfFormulas != null) {
	      Iterator iter = listOfFormulas.iterator();

	      while (iter.hasNext()) {
	        ( (TFormula) iter.next()).interpretFreeVariables(valuation);

	      }

	    }
	  }




	public static boolean  varFree(TFormula variform, ArrayList listOfFormulas){  //called newVarFree in Pascal
	  boolean freeFound=false;

	    if (listOfFormulas != null) {
	        Iterator iter = listOfFormulas.iterator();

	        while ((iter.hasNext())&&!freeFound) {
	          freeFound=( (TFormula) iter.next()).freeTest(variform);

	        }

	      }


	    return
	        freeFound;
	  }

/*maybe these routines should be in the parser. No, use universal internal representation*/

static public TFormula conjoinFormulas (TFormula first,TFormula second ){

   return
       new TFormula(binary,String.valueOf(chAnd), first,second);
}

 static public TFormula negateFormula (TFormula first ){

    return
        new TFormula(unary,String.valueOf(chNeg), null,first);
}

  static public TFormula equateTerms (TFormula term1,TFormula term2 ){

    // internal form =t1t2

    TFormula identity=new TFormula(equality,String.valueOf(chEquals), null,null);
    identity.appendToFormulaList(term1);
    identity.appendToFormulaList(term2);

   return
     identity;
}


static public boolean equalFormulas(TFormula first, TFormula second){  // need to have two parameters, not a plain method, because either formula might be null
	   boolean value=false;

	   if ((second==null)&&(first==null))
	     value=true;
	   else
	     if(((first!=null)&&(second!=null)) &&
	       ((first.fInfo).equals(second.fInfo))&&
	       (equalFormulas(first.fLLink, second.fLLink))&&
	       (equalFormulas(first.fRLink, second.fRLink)))
	         value=true;

	    return
	        value;


	}

	static public boolean formulasContradict(TFormula firstFormula,TFormula secondFormula){
	   if ((firstFormula.fKind == unary)&&
	       (equalFormulas(firstFormula.fRLink,secondFormula)))
	     return
	         true;

	   if ((secondFormula.fKind == unary)&&
	       (equalFormulas(secondFormula.fRLink,firstFormula)))
	     return
	         true;

	   return
	       false;

	  }
	
	public static String freeAtomicTermsInListOfFormulas(ArrayList listOfFormulas ){ // returns empty string not null for none

		   String outputStr="";


		   if (listOfFormulas != null) {
		     Iterator iter = listOfFormulas.iterator();

		     while (iter.hasNext()) {
		       outputStr = outputStr + ( (TFormula) iter.next()).freeAtomicTermsInFormula();

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


	
	Set<String> s = new TreeSet<String>();
	
public static Set <String> atomicTermsInListOfFormulas(ArrayList listOfFormulas ){ // returns empty string not null for none
		//String outputStr="";
		Set<String> s = new TreeSet<String>();


	   if (listOfFormulas != null) {
	     Iterator iter = listOfFormulas.iterator();

	     while (iter.hasNext()) {
	       s.addAll(( (TFormula) iter.next()).atomicTermsInFormula());
	   //    outputStr = outputStr + ( (TFormula) iter.next()).atomicTermsInFormula();

	     }

/*	     if (outputStr.length()>1){

	       outputStr = TUtilities.removeDuplicateChars(outputStr);

	       if (outputStr.length()>1){
	         char[] forSort = outputStr.toCharArray();

	         Arrays.sort(forSort);

	         outputStr = new String(forSort);
	       }
	     } */

	   }

	   return
	       s;
	}	
	

	/*	public static String atomicTermsInListOfFormulas(ArrayList listOfFormulas ){ // returns empty string not null for none
			String outputStr="";


		   if (listOfFormulas != null) {
		     Iterator iter = listOfFormulas.iterator();

		     while (iter.hasNext()) {
		       outputStr = outputStr + ( (TFormula) iter.next()).atomicTermsInFormula();

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
*/

		 /* the next is a helper for the semantics*/

		  public static String falseAtomicFormulasInList(ArrayList listOfFormulas ){ // returns empty string not null for none

		     String outputStr="";


		     if (listOfFormulas != null) {
		       Iterator iter = listOfFormulas.iterator();

		       while (iter.hasNext()) {

		         TFormula nextFormula=( (TFormula) iter.next());

		         if ((nextFormula.fKind == unary)
		            &&(nextFormula.fRLink.fKind == predicator)
		            &&(nextFormula.fRLink.arity() == 0))
		            outputStr = outputStr + nextFormula.fRLink.fInfo;

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


		/* the next is a helper for the semantics*/

		 public static String trueAtomicFormulasInList(ArrayList listOfFormulas ){ // returns empty string not null for none

		    String outputStr="";


		    if (listOfFormulas != null) {
		      Iterator iter = listOfFormulas.iterator();

		      while (iter.hasNext()) {

		        TFormula nextFormula=( (TFormula) iter.next());

		        if ((nextFormula.fKind == predicator)
		           &&(nextFormula.arity() == 0))
		           outputStr = outputStr + nextFormula.fInfo;

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

		 public static String extensionOfUnaryPredicate(ArrayList listOfFormulas, String predicate ){
		 // runs through a list of formulas, say Fa, Gb, Fc and returns for a predicate, say F, its
		// extension ie ac. returns empty string not null for none

		    String outputStr="";


		    if (listOfFormulas != null) {
		      Iterator iter = listOfFormulas.iterator();

		      while (iter.hasNext()) {

		        TFormula nextFormula=( (TFormula) iter.next());

		        if ((nextFormula.fKind == predicator)
		           &&(nextFormula.arity() == 1)
		           &&(nextFormula.fInfo.equals(predicate)))
		              outputStr = outputStr + nextFormula.firstTerm().fInfo;

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


		public static String extensionOfBinaryPredicate(ArrayList listOfFormulas, String predicate ){
		 // runs through a list of formulas, say Fac, Gb, Fcd and returns for a predicate, say F, its
		// extension ie ac. returns empty string not null for none

		    String outputStr="";


		    if (listOfFormulas != null) {
		      Iterator iter = listOfFormulas.iterator();

		      while (iter.hasNext()) {

		        TFormula nextFormula=( (TFormula) iter.next());

		        if ((nextFormula.fKind == predicator)
		           &&(nextFormula.arity() == 2)
		           &&(nextFormula.fInfo.equals(predicate)))
		              outputStr = outputStr
		                          + nextFormula.firstTerm().fInfo
		                          + nextFormula.secondTerm().fInfo;

		      }

		      if (outputStr.length()>2){

		        outputStr = TUtilities.removeDuplicatePairsOfChars(outputStr);

		        outputStr = TUtilities.sortPairs(outputStr);
		        }
		      }

		    return
		        outputStr;
		 }
	
		
	      public static boolean isExiquant(TFormula root){


              if ((root.fKind==quantifier)&&(root.fInfo.charAt(0)==chExiquant))
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

public static   boolean isEquality(TFormula root){


    if (root.fKind==equality)
      return
          true;
    else
      return
          false;
}


/************************** Standard methods  ****************************/


/*
public static Set <String> variablesInFormula(TFormula aFormula){  
	Set<String> s = new TreeSet<String>();
	
	String leftStr=strNull;
	  String rightStr=strNull;


	  if ((aFormula.fKind!=variable)||(aFormula.isSpecialPredefined()))
	    return
	        s;

	   if (aFormula.getLLink()!=null)
		   if (s.addAll(variablesInFormula(aFormula.getLLink())))
			   ;
	   if (aFormula.getRLink()!=null)
		   if (s.addAll(variablesInFormula(aFormula.getRLink())))
			   ;

	   if ((aFormula.fKind!=variable))
	     if (aFormula.getInfo().length()>0)
	    	 if (s.add(aFormula.getInfo()))
	    		 ;
	return
	      s;
	}
 */

public Set <String> atomicTermsInFormula(){   // returns empty string not null for none
	Set<String> s = new TreeSet<String>();
    int degree= arity();

    if (!isSpecialPredefined()){
          switch (fKind){
            case functor:
              if (degree==0)
            	  if (s.add(fInfo))
     	    		 ;
               break; // no compound terms
            case variable:
            	if (s.add(fInfo))
    	    		 ;
               break;
             case predicator:
             case equality:

                if (degree>0) {

                   for(int i=1;i<=degree;i++){
                	   if (s.addAll(nthTopLevelTerm(i).atomicTermsInFormula()))
                       	;
                   }
                 }
                break;
              case unary:
                 s=fRLink.atomicTermsInFormula();

                break;
              case binary:
            	  
            	s=fLLink.atomicTermsInFormula();

                if (s.addAll(fRLink.atomicTermsInFormula()))
                	;
                break;
              case quantifier:
              case typedQuantifier:
            	  
                	s=quantVarForm().atomicTermsInFormula();

                    if (s.addAll(scope().atomicTermsInFormula()))
                    	;
             break;
            default: ;
          }

       };
   return
      s;
}

/*
public String atomicTermsInFormula(){   // returns empty string not null for none
    String returnStr="";
    int degree= arity();

    if (!isSpecialPredefined()){
          switch (fKind){
            case functor:
              if (degree==0)
                returnStr= fInfo;

               break; // no compound terms
            case variable:
               returnStr= fInfo;
               break;


             case predicator:
             case equality:

                if (degree>0) {

                   for(int i=1;i<=degree;i++){
                      returnStr=returnStr+nthTopLevelTerm(i).atomicTermsInFormula();
                   }

                   returnStr=TUtilities.removeDuplicateChars(returnStr);
                 }
                break;

              case unary:
                 returnStr=fRLink.atomicTermsInFormula();

                break;


              case binary:

                returnStr=fLLink.atomicTermsInFormula()+fRLink.atomicTermsInFormula();

                returnStr=TUtilities.removeDuplicateChars(returnStr);

                break;

              case quantifier:
              case typedQuantifier:

                returnStr=quantVarForm().atomicTermsInFormula() + scope().atomicTermsInFormula();

                returnStr=TUtilities.removeDuplicateChars(returnStr);


                break;


            default: ;
          }

       };


   return
      returnStr;
}
*/


public String freeAtomicTermsInFormula(){   // returns empty string not null for none
    String returnStr="";
    int degree= arity();

    if (!isSpecialPredefined()){
          switch (fKind){
            case functor:
              if (degree==0)
                returnStr= fInfo;

               break; // no compound terms
            case variable:
               returnStr= fInfo;
               break;


             case predicator:
             case equality:

                if (degree>0) {

                   for(int i=1;i<=degree;i++){
                      returnStr=returnStr+nthTopLevelTerm(i).freeAtomicTermsInFormula();
                   }

                   returnStr=TUtilities.removeDuplicateChars(returnStr);
                 }
                break;

              case unary:
                 returnStr=fRLink.freeAtomicTermsInFormula();

                break;


              case binary:

                returnStr=fLLink.freeAtomicTermsInFormula()+fRLink.freeAtomicTermsInFormula();

                returnStr=TUtilities.removeDuplicateChars(returnStr);

                break;

              case quantifier:
              case typedQuantifier:       // not checking type

                returnStr=scope().freeAtomicTermsInFormula();

                //String variable=quantVarForm().atomicTermsInFormula();
                
                String variable=quantVarForm().fInfo;  //Dec09

                returnStr=returnStr.replaceAll(variable,strNull);  // any term within
                  //scope of quantifier is bound   // watch out for regex syntax and the need for escape characters

                returnStr=TUtilities.removeDuplicateChars(returnStr);


                break;


            default: ;
          }

       };


   return
      returnStr;
}


// next revised Feb08

public TFormula termsInFormula(){    //returns a list of terms  CHECK THIS FOR FUNCTIONAL TERMS
   TFormula head=null,secondHead=null;
   int degree;

   if (!isSpecialPredefined()){
      switch (fKind){
        case functor:
        case variable:

          head = new TFormula();   //start a list  Feb08
          head.fKind = kons;
          head.fLLink = this;


        //  head=this;


           degree= arity();

           for(int i=1;i<=degree;i++){                         //changed i<degree to i<=degree Jan06
              secondHead=nthTopLevelTerm(i).termsInFormula();
              head=concatLists(head,secondHead,!kAllowDuplicates);
           }
           break;

         case predicator:
         case equality:

            degree= arity();

            for(int i=1;i<=degree;i++){                      //changed i<degree to i<=degree Jan06
               secondHead=nthTopLevelTerm(i).termsInFormula();

            if (head==null)
               head=secondHead;
            else
               head=concatLists(head,secondHead,!kAllowDuplicates);
            }
            break;

          case unary:
             head=fRLink.termsInFormula();

            break;


          case binary:

            head=fLLink.termsInFormula();
            secondHead=fRLink.termsInFormula();

            head=concatLists(head,secondHead,!kAllowDuplicates);

            break;

          case quantifier:
          case typedQuantifier:    // not checking types

            head=quantVarForm().termsInFormula();
            secondHead=scope().termsInFormula();

            head=concatLists(head,secondHead,!kAllowDuplicates);

            break;


        default: ;
      }

   }

  return
     head;
  }


//written Feb08
public TFormula closedTermsInFormula(){    //returns a list of terms  CHECK THIS FOR FUNCTIONAL TERMS
   TFormula head=null,secondHead=null;
   int degree;

   if (!isSpecialPredefined()){  //SpecialPredefined has none
      switch (fKind){
        case functor:
        case variable:   // gets caught in next line 

          if (this.isClosedTerm()){  //a closed term cannot contain a variable. This could be f(ab)


            head = new TFormula(); //start a list  Feb08
            head.fKind = kons;
            head.fLLink = this;   //add it

            degree = arity();    // look through subterms

            for (int i = 1; i <= degree; i++) { //changed i<degree to i<=degree Jan06
              secondHead = nthTopLevelTerm(i).closedTermsInFormula();
              head=concatLists(head, secondHead, !kAllowDuplicates);
            }
          }
           break;

         case predicator:
         case equality:

            degree= arity();   // look through subterms

            for(int i=1;i<=degree;i++){                      //changed i<degree to i<=degree Jan06
               secondHead=nthTopLevelTerm(i).closedTermsInFormula();

            if (head==null)
               head=secondHead;
            else
               head=concatLists(head,secondHead,!kAllowDuplicates);
            }
            break;

          case unary:
             head=fRLink.closedTermsInFormula();

            break;


          case binary:

            head=fLLink.closedTermsInFormula();
            secondHead=fRLink.closedTermsInFormula();

            head=concatLists(head,secondHead,!kAllowDuplicates);

            break;

          case quantifier:
          case typedQuantifier:


            head=scope().closedTermsInFormula();



            break;


        default: ;
      }

   }

  return
     head;
  }





/********* Formula Accessors***************************/

public TFormula firstTerm(){
        TFormula temp=this.fRLink;

        if (temp!=null)
                return temp.fLLink;
        else
                return null;
}

public TFormula nthTopLevelTerm(int n){   //this is in an atomic formula only, start counting at 1?
  TFormula temp=this.fRLink;              // a formula might be Pf(a)b in which case f(a) and b
                                          // are the top level terms, but a occurs in there
  while ((temp!=null)&&(n>1)){
    temp=temp.fRLink;
    n-=1;
  }

  if (temp!=null)
    return
        temp.fLLink;
  else
    return
        null;
}
 

/*This is more complex than it was originally written. Originally the thought was
  Pxax and we go through looking for the second x, say.

 But if you allow functional terms it may be

 Pf(x,x)ax

 in which case the second occurrence is a subterm of the first term
  */


 public TFormula nthOccurenceInAtomic(TFormula term, int n){   //this is in an atomic formula only, start counting at 1?
  TFormula target=null;
  TFormula temp=this.fRLink;

  while ((temp!=null)&&(n>0)){
    target=temp.fLLink;
    if (equalFormulas(term,target))
       n-=1;
    else
      target=null;
    temp=temp.fRLink;
  }

  return
      target;
}


class Count{
  int fCount=0;
  public Count (int n){
      fCount=n;
}
}

public TFormula depthFirstNthOccurence(TFormula subTerm, int n){
  return
      depthFirstNthOccurence(subTerm, new Count(n));
}

private TFormula depthFirstNthOccurence(TFormula subTerm, Count count){   // could use accumulating parameter

 /* This is looking for nth occurrence free of a term in a formula

  so it needs to be able to find, say a in

  Faa
  Fag(a)
  Fh(g(a))a

  ~Fh(g(a))a

  Fh(g(a))a^Fag(a)

  Fh(g(a))a^(All a)Fag(a)

  It will count depth first l to r on atomic, similar for compound, but will not count within bound quantifier

  */


  TFormula found=null;
  int degree;

  if (equalFormulas(this,subTerm))
     {if (count.fCount==1)
         return
             this;               // I am it
     else{
       count.fCount-=1;         // I am an occurrence (therefore none of my subterms can be) but not the one sought
         return
             null;
     }
   }

  switch (this.fKind){

    case functor:
    case variable:
    case predicator:
    case equality:

      degree = arity();

      for (int i = 1; i <= degree; i++) {  // work through the subterms
        found = nthTopLevelTerm(i).depthFirstNthOccurence(subTerm,count);
        if (found!=null)
          return
              found;
      }
      break;

    case unary:
      return
          this.fRLink.depthFirstNthOccurence(subTerm,count);

    case binary:
      found = this.fLLink.depthFirstNthOccurence(subTerm,count);
        if (found!=null)
          return
              found;
        else
          return
              this.fRLink.depthFirstNthOccurence(subTerm,count);


      case quantifier:
      case typedQuantifier:
        if (equalFormulas(this.quantVarForm(),subTerm))  // the term is bound
          return
              null;
        else
          return
             this.scope().depthFirstNthOccurence(subTerm,count);

  }
  return
      found;   // null if it gets to here
}

public int numConnectives(){
  switch (fKind){
        case functor:
        case predicator:
        case equality:

            return
                0;


          case unary:
          case quantifier:
          case typedQuantifier:       // do we want to add 1 for this?
             return
                 1+ this.fRLink.numConnectives();

          case binary:
               return
                   1+ this.fLLink.numConnectives()+this.fRLink.numConnectives();
         default:
           return
              0;
      }

}


public int numInPredOrTerm(TFormula term){
  int total = 0;
  int degree;

  if (equalFormulas(this,term))
      total=1;                  // if I am one, none of my subterms can be
    else
      switch (fKind){
          case functor:
          case predicator:
          case equality:

            degree = arity();

            for (int i = 1; i <= degree; i++) {  // work sideways through the subterms
              total += nthTopLevelTerm(i).numInPredOrTerm(term);
            }

            break;
      }
  return
      total;
}


public TFormula nthFreeOccurence(TFormula term, int n){  //equal to term

   if (n<1)
     return
         null;

  if ((n==1)&&(equalFormulas(this,term)))
    return
        this;
  else
    switch (fKind){
        case functor:
        case predicator:
        case equality:

          /*all the terms are free, but not all equal to term*/

            return
                this.nthOccurenceInAtomic(term,n);


          case unary:
             return
                 this.fRLink.nthFreeOccurence(term,n);

          case binary:
             int divide=this.fLLink.numOfFreeOccurrences(term);

             if (n<=divide)
               return
                   this.fLLink.nthFreeOccurence(term,n);
             else
               return
                   this.fRLink.nthFreeOccurence(term,n-divide);

          case quantifier:
          case typedQuantifier:

            if (equalFormulas(term,this.quantVarForm()))
              return
                  null;  // cannot be any free ones
            else
              return
                 this.fRLink.nthFreeOccurence(term,n);


         default:
           return
              null;
      }


}




public int numOfFreeOccurrences(TFormula term){
  int total=0;

  if (equalFormulas(this,term))
    total=1;
  else
    switch (fKind){
        case functor:
        case predicator:
        case equality:
            total=this.numInPredOrTerm(term);  //its an atomic (sub)formula not equal to term
            break;

          case unary:
             total=this.fRLink.numOfFreeOccurrences(term);

            break;


          case binary:
             total=this.fLLink.numOfFreeOccurrences(term) + this.fRLink.numOfFreeOccurrences(term);


            break;

          case quantifier:
          case typedQuantifier:
            if (equalFormulas(term,this.quantVarForm()))
              total=0;
            else
              total=this.fRLink.numOfFreeOccurrences(term);
            break;


        default:
          total=0;
      }
   return
      total;

}


public char quantType(){
  TFormula var=quantTypeForm();

  if (var != null)
    return
        var.fInfo.charAt(0);

  return
  chBlank;
}

public TFormula quantVarForm(){
    if (fKind==quantifier)
      return
          fLLink;

    if ((fKind==typedQuantifier&&fLLink!=null))
      return
      nthFormula(1,fLLink);

return
    null;
}

  public TFormula quantTypeForm(){

     if ((fKind==typedQuantifier&&fLLink!=null))
        return
        nthFormula(2,fLLink);

  return
      null;
}

  public void setQuantType(TFormula type){

	     if ((fKind==typedQuantifier&&fLLink!=null))    	 
	    	 setNthFormula(2,fLLink,type);
	}

public TFormula secondTerm(){
        TFormula temp=this.fRLink;

        if (temp!=null)
                {
                temp=temp.fRLink;
                if (temp!=null)
                        return temp.fLLink;
                else
                        return null;
                }
        else
                return null;
}

public TFormula termsList()
        {
        return fRLink;
        }

public String lambdaVar(){
  if ((fKind==lambda)&&(fLLink!=null))
    return
        fLLink.fInfo;
    return
        "";
}

public TFormula lambdaVarForm(){
  if (fKind == lambda)
    return
        fLLink;
  return
      null;
}

//mf Dec 09, the variables can be strings with subscripts eg x12 so chars won't cut it
 /* public char quantVar(){
  if ((fKind==quantifier)&&(fLLink!=null))
    return
        fLLink.fInfo.charAt(0);
  else
  if (fKind==typedQuantifier){
    TFormula var=quantVarForm();
    if (var!=null)
    return
        var.fInfo.charAt(0);
  }

    return
    chBlank;
} */

public String quantVar(){
	  if ((fKind==quantifier)&&(fLLink!=null))
	    return
	        fLLink.fInfo;
	  else
	  if (fKind==typedQuantifier){
	    TFormula var=quantVarForm();
	    if (var!=null)
	    return
	        var.fInfo;
	  }

	    return
	    "";
	} 

public TFormula scope(){  //both quantifers and lambdas use this so don't check type
  return
      fRLink;
}





/**********************Boolean methods*********************************/






public char propositionName(){
  return
      fInfo.charAt(0);
}



public char propertyName(){
  return
      fInfo.charAt(0);
}


boolean subFormulaOccursInList(TFormula subformula, TFormula head){

  if (head==null)
    return
        false;
  else{
    if (subFormulaOccursInFormula(subformula,head.fLLink))
      return
         true;
    else
      return
         subFormulaOccursInList(subformula, head.fRLink);
  }
}


 public boolean subFormulaOccursInFormula (TFormula subformula, TFormula formula)  //rewrite to get functional terms Jan06
 {
   if (equalFormulas(subformula,formula))
     return
         true;
   else
   {
      switch (formula.fKind){
        case functor:
        case variable:
        case predicator:
        case equality:
          return
             subFormulaOccursInList(subformula, formula.fRLink);
        case unary:
          return
             subFormulaOccursInFormula(subformula, formula.fRLink);
        case binary:
        case quantifier:
        case application:
          return
             (subFormulaOccursInFormula(subformula, formula.fLLink)||
              subFormulaOccursInFormula(subformula, formula.fRLink));
       case typedQuantifier:
         return
             (subFormulaOccursInFormula(subformula, formula.quantVarForm())||    //not looking at type at present June 08
              subFormulaOccursInFormula(subformula, formula.scope()));

       case lambda:
          return
              (subFormulaOccursInFormula(subformula, formula.lambdaVarForm())||
               subFormulaOccursInFormula(subformula, formula.scope()));


        default:
          return
              false;
      }
   }

 }


 


public boolean freeForTest(TFormula termForm, TFormula variForm){

   switch (fKind){

      case variable:
      case predicator:
      case functor:
      case equality:
              return
                  true;
      case unary:
              return
                 fRLink.freeForTest(termForm,variForm);
      case binary:
      case application:
              return
                 (fLLink.freeForTest(termForm,variForm)&&fRLink.freeForTest(termForm,variForm));
      case quantifier:
      case typedQuantifier:
              if ((!subFormulaOccursInFormula(quantVarForm(),termForm)&&
                   fRLink.freeForTest(termForm,variForm))||
                  !this.freeTest(variForm))
            return
               true;
         else
            return
               false;

      case lambda:
             if ((!subFormulaOccursInFormula(lambdaVarForm(),termForm)&&
                  fRLink.freeForTest(termForm,variForm))||
                 !this.freeTest(variForm))
           return
              true;
        else
           return
              false;




       default:
     return
         true;
}


 }





public boolean freeTest(TFormula variForm){  //not written yet

switch (fKind){

  case variable:
              return
                  (equalFormulas(this,variForm));

  case predicator:
  case functor:
  case equality:
              if (isSpecialPredefined())
                return
                    false;
              else
                return
                    subFormulaOccursInList(variForm, fRLink);
  case unary:
          return
              fRLink.freeTest(variForm);
  case binary:
  case application:
         return
           (fLLink.freeTest(variForm)||fRLink.freeTest(variForm));
  case quantifier:
  case typedQuantifier:
         if (equalFormulas(variForm,quantVarForm()))
            return
               false;
         else
            return
               scope().freeTest(variForm);

  case lambda:
      if (equalFormulas(variForm,lambdaVarForm()))
        return
            false;
      else
        return
            scope().freeTest(variForm);




       default:
     return
         false;
}

}









public int arity(){
  int total =0;
  TFormula temp;

  switch (fKind){
    case equality:
    case functor:
    case predicator:
    case variable:   //variable always zero, of course

       temp=fRLink;
       if ((temp!=null)&& !equalFormulas(this,fTruth)
                       && !equalFormulas(this,fFalsehood)
                       && !equalFormulas(this,fAbsurd)
                       && !equalFormulas(this,fTop)
                       && !equalFormulas(this,fBottom)){  //not the Absurd predefined
         while (temp!=null){
           total+=1;
           temp=temp.fRLink;
         }
       }
       break;

    default:
      break;
  }


  return
      total;
}



/*list processing utilities*/

public ArrayList allSubFormulas(){
  ArrayList returnList=null;


    switch (fKind) {

      case variable:
      case predicator:
      case functor:
      case equality:
        returnList=new ArrayList();
        returnList.add(this);
        return
            returnList;
      case unary:
        returnList=fRLink.allSubFormulas();
        returnList.add(this);
        return
            returnList;
      case binary:
        returnList=fRLink.allSubFormulas();
        returnList.addAll(fLLink.allSubFormulas());
        removeDuplicateFormulas(returnList);
        returnList.add(this);
        return
            returnList;

      case quantifier:
          case typedQuantifier:
        returnList=(this.scope()).allSubFormulas();
        returnList.add(this);
        return
            returnList;

      default:
        ;
    }

  return
            returnList;
}

private boolean isNegation(TFormula root){   // here we rely on unique internal form

                      return
                          ((root.fKind == unary) && (root.fInfo.charAt(0)==chNeg));

                              }


public ArrayList allSubFormulasWhichAreNegations() //wow, what a title! RETURN ALWAYS NON NULL
{ ArrayList returnList=new ArrayList();

  ArrayList allSubFormulas=this.allSubFormulas();

  if (allSubFormulas != null) {
     Iterator iter = allSubFormulas.iterator();

     while (iter.hasNext()) {
        TFormula nextFormula=(TFormula)iter.next();

           if (isNegation(nextFormula))
             returnList.add(nextFormula);
     }
   }
return
   returnList;
}










public void appendToFormulaList(TFormula item)  // AddItemOnEnd (var head: TFormula; item: TFormula);
        {TFormula node,temp;

        node= new TFormula(kons,strNull,item,null);

        temp=this;

        while (temp.fRLink!=null)
                        temp=temp.fRLink;

        temp.fRLink=node;

        }




public TFormula nthFormula(int n, TFormula list){
      TFormula temp=list;

      while ((temp!=null)&&(n>1)){
        temp=temp.fRLink;
        n-=1;
      }

      if (temp!=null)
        return
            temp.fLLink;
      else
        return
            null;
}

public void setNthFormula(int n, TFormula list, TFormula replacement){
    TFormula temp=list;

    while ((temp!=null)&&(n>1)){
      temp=temp.fRLink;
      n-=1;
    }

    if (temp!=null)
          temp.fLLink=replacement;
}

public TFormula copyFormula (){
   TFormula copy= new TFormula();

   copy.fKind = this.fKind;
   copy.fInfo = this.fInfo;

   if (fLLink != null)
          copy.fLLink = fLLink.copyFormula();
    if (fRLink != null)
          copy.fRLink = fRLink.copyFormula();

   return
       copy;
}



public String boundVariablesInFormula(){   // returns empty string not null for none
    String returnStr="";
    int degree= arity();

    if (!isSpecialPredefined()){
          switch (fKind){
            case functor:
            case variable:
            case predicator:
            case equality:
                break;

            case unary:
                 returnStr=fRLink.boundVariablesInFormula();
                break;


              case binary:

                returnStr=fLLink.boundVariablesInFormula()+fRLink.boundVariablesInFormula();

                returnStr=TUtilities.removeDuplicateChars(returnStr);

                break;

              case quantifier:
          case typedQuantifier:

                returnStr+=quantVar();
                break;


            default: ;
          }

       };


   return
      returnStr;
}


public boolean isClosedTerm(){  //cannot contain any variables

int degree;

   switch (fKind){
     case variable:
       return
           false;     // if a variable, not
     case functor:
       degree = arity();

       for (int i = 1; i <= degree; i++) {
         if (!nthTopLevelTerm(i).isClosedTerm()) //if subterms not closed, not
           return
               false;
       }

       break;

     default: ;
          }

 return
     true;
}





  

/********************  List Processing ********************************************/

//NOTICE THAT THIS NEW ROUTINE HAS A HEAD ON THE FRONT




void append(TFormula item){  //oldname addItemOnEnd
  TFormula temp=this;

  TFormula newnode = new TFormula();
  newnode.fKind = kons;
  newnode.fLLink = item;

  while (temp.fRLink!=null)
    temp=temp.fRLink;        // now points at last one

  temp.fRLink=newnode;

}



public void appendIfNotThere(TFormula item){  //oldname addItemOnEndIfNotThere
  TFormula temp=this;
  boolean there=false;

  TFormula newnode = new TFormula();
  newnode.fKind = kons;
  newnode.fLLink = item;

  while ((temp.fRLink!=null)&&!there){
    if (equalFormulas(item,temp.fLLink))
        there=true;
    else
       temp=temp.fRLink;        // now points at last one

  }

  if (equalFormulas(item,temp.fLLink))  // last one
        there=true;

  if (!there)
    temp.fRLink=newnode;

}


public static TFormula concatLists(TFormula firstHead,TFormula secondHead, boolean allowDuplicates){

  if (firstHead==null){
    return
        secondHead;}

  else{

    TFormula search = secondHead;

    while (search != null) {
      if (allowDuplicates)
        firstHead.append(search.fLLink);
      else
        firstHead.appendIfNotThere(search.fLLink);
      search = search.fRLink;
    }
  }
  return
      firstHead;
}



public boolean formulaInList(ArrayList list){

 if (list==null)
   return
       false;

 int length=list.size();
 boolean found=false;
 int i=0;

 while(!found&&(i<length)){

    if (equalFormulas(this,(TFormula)list.get(i)))
       found= true;
  else
     i+=1;

 }

 return
    found;

}

public static void removeDuplicateFormulas (ArrayList aList){

if (aList!=null){

  int length = aList.size();
  boolean found = false;
  TFormula key, target;
  int i,j;

  for (i=length-1; i>0;i--){                // need two to have a duplicate

    key=(TFormula)aList.get(i);

    for (j=i-1;j>-1;j--){
      target=(TFormula)aList.get(j);

      if (target.equalFormulas(key,target)){
        aList.remove(i);
        break;                              // we work backwards through the list perhaps removing the key
      }
    }






  }


}


}


  static public boolean twoInListContradict(ArrayList aList,TFormula firstOne, TFormula secondOne){
  /*The formula objects need to be created elsewhere*/



    /*This checks whether there are two contradictory formulas among the}
{antecedent list; if there are they are is passed back

   returns the positive first

   Watch out for instantiating info added to fInfo field and specialequalformulas*/

  boolean found=false;
  TFormula first=null, second=null;

  int length = aList.size();

  if (length>1){
    int i=0;
    int j=1;

    while ((i<(length-1))&&!found){

      first=(TFormula)aList.get(i);

      j=i+1;

      while ((j<length)&&!found){

         second=(TFormula)aList.get(j);

         if (TFormula.formulasContradict(first,second))


             /*(((first.fKind==TFormula.unary)
             && first.fRLink.equalFormulas(first.fRLink,second))
             ||
            ((second.fKind==TFormula.unary)
             && second.fRLink.equalFormulas(first.fRLink,first)))*/

            found=true;

        j++;

       }

       i++;

    }

    if (found) {
      i = 0;
      j = 0;
      TFormula search = firstOne;

      while (search.fKind == TFormula.unary) {
        i += 1;
        search = search.fRLink;
      }

      search = secondOne;

      while (search.fKind == TFormula.unary) {
        j += 1;
        search = search.fRLink;
      }

      if (j > i) {                       // the second one is the negation of the first

        firstOne.assignFieldsToMe(first); // workaround for Pascal var parameters
        secondOne.assignFieldsToMe(second);
      }
      else{
        firstOne.assignFieldsToMe(second); // workaround for Pascal var parameters
        secondOne.assignFieldsToMe(first);
      }
    }
  }
  return
     found;

}




static public boolean subset(ArrayList subset,ArrayList superset){
  boolean allFound=true, found=false;

  if ((subset!=null)&&(superset!=null)){
    if (subset.size()==0)
      return
          true;

    if (superset.size()==0)
      return
          false;

   int i=0;
   int length=subset.size();

   while (allFound && (i<length)){

     Object debug=subset.get(i);


     allFound=allFound&&(((TFormula)(subset.get(i))).formulaInList(superset));
     i+=1;
   }

   return
       allFound;
  }



  return
      allFound;
}





/********************** Writing routines, with surgery *********************************/



public class MarkerData{

  TFormula fTermForm=null;
  int fOccurrence;
  int fMetSoFar;
  TFormula fRoot=null;
  TFormula fCopy=null;
  boolean fDone;
  TFormula fCurrentNode=null;
  TFormula fCurrentCopyNode=null;

  MarkerData(){

  }

  MarkerData (TFormula termForm,
     int occurrence,
     int metSoFar,
     TFormula root,
     TFormula copy,
     boolean done,
     TFormula currentNode,
     TFormula currentCopyNode){


  fTermForm=termForm;
  fOccurrence=occurrence;
  fMetSoFar=metSoFar;
  fRoot=root;
  fCopy=copy;
  fDone=done;
  fCurrentNode=currentNode;
  fCurrentCopyNode=currentCopyNode;

 }






}

  public MarkerData supplyMarkerData(
     TFormula termForm,
     int occurrence,
     int metSoFar,
     TFormula root,
     TFormula copy,
     boolean done,
     TFormula currentNode,
     TFormula currentCopyNode
 ){


 return
    new MarkerData(termForm,
      occurrence,
      metSoFar,
      root,
      copy,
      done,
      currentNode,
      currentCopyNode
 );
  }


public  void newInsertMarker(MarkerData data){

  /*

   {This works on two formulas: root and copy.  It searches root for the occurrenceth }
   {free occurrence of termForm and it duplicates its search with copy.}
   {It puts in a marker to indicate the occurrenceth occurrence of termStr in root}
        {and also points the currentNode and currentCopyNode and currentIndex at them}
           {these occurences must be free hence we don't search the right branch of quantified}
   {	formulas if termForm and the quantified var are the same. This is to make it easy to remove}
   {	the marker and alter the copy.}

  */

 if (!data.fDone){

   if (data.fTermForm.equalFormulas(data.fTermForm, data.fRoot)){

       data.fMetSoFar+=1;

      if (data.fMetSoFar == data.fOccurrence){

        data.fRoot.fInfo= chInsertMarker+ data.fRoot.fInfo;
        data.fDone = true;
        data.fCurrentNode = data.fRoot;
        data.fCurrentCopyNode = data.fCopy;
      }
   }
   else{
     switch (data.fRoot.fKind){

       case predicator:

       case functor:

       case variable:

       case equality:

         TFormula temp, tempCopy, LLink;

         temp=data.fRoot.fRLink;
         tempCopy=data.fCopy.fRLink;

         while ((temp!=null)&&!data.fDone){

         }


       default: ;
     }
   }
 }


}








  

public void interpretFreeVariables(ArrayList valuation){   // does surgery?

  if (valuation!=null){

    TFormula valuForm;
    TFormula constantForm = new TFormula();
    TFormula variForm = new TFormula();

    Iterator iter = valuation.iterator();

    constantForm.fKind = functor;
    variForm.fKind = variable;

    /*the info on a valuation looks like this "a/x"
     and we want to substitute the constant a for the variable
     x throughout the formula*/

    while (iter.hasNext()) {
      valuForm = (TFormula) iter.next();

      constantForm.fInfo = valuForm.fInfo.substring(0, 1);
      variForm.fInfo = valuForm.fInfo.substring(2, 3);

      subTermVar(this, constantForm, variForm);
    }
  }



}




//These are also defined in the parser!

     public boolean isAnd(TFormula root){    //should not be static because of different parsers
                                             //(although Dec06 using same underlying representation


               if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chAnd))
                 return
                     true;
               else
                 return
                     false;
        }

  

public boolean isImplic(TFormula root){


                  if ((root.fKind==binary)&&(root.fInfo.charAt(0)==chImplic))
                    return
                        true;
                  else
                    return
                        false;
                }









public boolean isNegIdentity (TFormula root){
                                          if (!isNegation(root))
                                            return
                                               false;

                                          if (!isEquality(root.fRLink))
                                            return
                                                false;

                                          if (!root.fRLink.nthTopLevelTerm(1).equalFormulas(
                                              root.fRLink.nthTopLevelTerm(1),
                                              root.fRLink.nthTopLevelTerm(2)
                                              ))
                                            return
                                                false;

                                          return
                                              true;
                                      }





                                        public boolean isUnique(){

if ((fKind==quantifier)&&(fInfo.charAt(0)==chUnique))
   return
                                                                                true;
                                                                          else
                                                                            return
                                                                                false;
                                                                        }



public boolean canAbbrevUnique(){
  //{This is to form ex! from a suitable formula ie ExFx^AlluAllv(Fu^Fvhooku=v)}
 boolean suitable=false;
 TFormula xvar=null, uvar=null, vvar=null, Pu, Pv, Px=null, newform=null;

suitable=isAnd(this);
if (suitable)
  suitable=isExiquant(fLLink);
if (suitable)
  suitable=isUniquant(fRLink);
if (suitable)
  suitable=isUniquant(fRLink.scope());
if (suitable)
  suitable=isImplic(fRLink.scope().scope());
if (suitable)
  suitable=isAnd(fRLink.scope().scope().fLLink);
if (suitable)
  suitable=isEquality(fRLink.scope().scope().fRLink);

if (suitable){
  Px = fLLink.scope();
  xvar = fLLink.quantVarForm();
  uvar = fRLink.quantVarForm();
  vvar = fRLink.scope().quantVarForm();
  suitable = ! equalFormulas(uvar, vvar); //( *to avoid silly double same variable case * )
}

if (suitable){
   Pu= Px.copyFormula();
   subTermVar(Pu, uvar, xvar);//( * P(u) * )
   Pv = Px.copyFormula();
   subTermVar(Pv, vvar, xvar); //( * P(v) * )

   suitable = equalFormulas(Pu, fRLink.scope().scope().fLLink.fLLink) &&
              equalFormulas(Pv, fRLink.scope().scope().fLLink.fRLink);

  if (!suitable) //( * happy to have Pu and Pv either way around * )
     suitable = equalFormulas(Pv, fRLink.scope().scope().fLLink.fLLink) &&
                equalFormulas(Pu, fRLink.scope().scope().fLLink.fRLink);

    if (suitable){
       suitable = equalFormulas(uvar, fRLink.scope().scope().fRLink.firstTerm()) &&
        equalFormulas(vvar, fRLink.scope().scope().fRLink.secondTerm());

      if (!suitable) //( * happy to have u and v either way around * )
          suitable= equalFormulas(vvar, fRLink.scope().scope().fRLink.firstTerm()) &&
          equalFormulas(uvar,fRLink.scope().scope().fRLink.secondTerm());
    }
}
return
    suitable;
}

public TFormula abbrevUnique(){
  //{This is to form ex! from a suitable formula ie ExFx^AlluAllv(Fu^Fvhooku=v)}


   TFormula newform=null;

if (canAbbrevUnique()){
  newform = fLLink.copyFormula();
  newform.fInfo = String.valueOf(chUnique);

}

 return
     newform;
}

public TFormula expandUnique(){
    //{This is to from ex! to ie ExFx^AlluAllv(Fu^Fvhooku=v)
 TFormula scopeForm, uvar, vvar, Pu, Pv,exiform, formulanode=null, doubleQuant,identity=null;

 if (!isUnique())
   return
       null;
 else{
	 
/*
         variables = new TreeSet<String>();
         if (variables.addAll(variablesInFormula(first)))
        	 ;
         if (variables.addAll(variablesInFormula(second)))
        	 ;
         
         newVar=nthNewVariable(1, variables);

         if (newVar.equals("")) 
 */
	 
	   Set <String> oldTerms=atomicTermsInFormula();
	   String newVar =TParser.nthNewVariable(1,oldTerms);

	   if (!newVar.equals("")) 
	 
	/* 
   String oldTerms=atomicTermsInFormula();
   char newVar =TParser.nthNewVariable(1,oldTerms);

   if (newVar!=' ') */
   
   {

     uvar=quantVarForm();
     vvar=new TFormula (variable,String.valueOf(newVar),null,null);

     exiform= new TFormula(quantifier,
                          String.valueOf(chExiquant),     //ExFx
                          uvar.copyFormula(),
                          scope().copyFormula());

    Pu=scope().copyFormula();
    Pv=scope().copyFormula();
    subTermVar(Pv,vvar,uvar);



    identity= new TFormula(equality,
                                           String.valueOf(chEquals),
                                           null,
                                           null);
identity.appendToFormulaList(uvar.copyFormula());
identity.appendToFormulaList(vvar.copyFormula());


     scopeForm= new TFormula(binary,                            //(Fu^Fvhooku=v)
                          String.valueOf(chImplic),
                          new TFormula(binary,
                               String.valueOf(chAnd),
                               Pu,
                               Pv),
                          identity);

  doubleQuant=new TFormula(quantifier,
                        String.valueOf(chUniquant),
                        uvar.copyFormula(),
                        new TFormula(quantifier,
                           String.valueOf(chUniquant),
                           vvar.copyFormula(),
                           scopeForm));                       // AlluAllv(Fu^Fvhooku=v)

     formulanode= new TFormula(binary,
                               String.valueOf(chAnd),
                               exiform,
                               doubleQuant);
   }
 }

 return
     formulanode;
}	

}    //END OF CLASS

/*	

public String variablesInFormula(){
 String leftStr=strNull;
 String rightStr=strNull;


 if (isSpecialPredefined())
   return
       strNull;

 if (fLLink!=null)
   leftStr=fLLink.variablesInFormula();

 if (fRLink!=null)
   rightStr=fRLink.variablesInFormula();

 if ((fLLink==null)&&(fRLink==null)){

   if (fInfo.length()>0){

     if (TParser.isVariable(fInfo.charAt(0)))
        leftStr = fInfo;
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

*/

/*
public String lambdaNamesInFormula(){
 String leftStr=strNull;
 String rightStr=strNull;


 if (isSpecialPredefined())
   return
       strNull;

 if (fLLink!=null)
   leftStr=fLLink.lambdaNamesInFormula();

 if (fRLink!=null)
   rightStr=fRLink.lambdaNamesInFormula();

 if ((fLLink==null)&&(fRLink==null)){

   if (fInfo.length()>0){

     if (TParser.isLambdaName(fInfo.charAt(0)))
        leftStr = fInfo;
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

*/

/*
public static String constantsInListOfFormulas(ArrayList listOfFormulas ){ // returns empty string not null for none

   String outputStr="";


   if (listOfFormulas != null) {
     Iterator iter = listOfFormulas.iterator();

     while (iter.hasNext()) {
       outputStr = outputStr + ( (TFormula) iter.next()).constantsInFormula();

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

*/
/*	
public static boolean freeInterpretFreeVariables(ArrayList interpretation){

   /*This will do surgery on a list of atomic formulas, say Fx, Gy and change the free varaibles
   into arbitrary constants eg Fa, Gb */


   /* What we have here is a consistent list of positive and negative atomic formulas,
    which are true, we need
      to pull out the atomic terms and make them the universe, then interpret the  predicates and relations
  suitably
 But they may have free variables. Any new constant will do here*/

/*	    String universe=TFormula.atomicTermsInListOfFormulas(interpretation);
   char searchCh;
   char constant;
   ArrayList valuation=new ArrayList();
   int n=1;
   TFormula valuForm;

   for (int i=0;i<universe.length();i++){

     searchCh=universe.charAt(i);

     if (TParser.isVariable(searchCh)){
       constant=TParser.nthNewConstant(n,universe);
       n+=1;
       if (constant==' ')
         return
             false;
       else{
            valuForm=new TFormula((short)0,constant +"/" + searchCh,null,null);
            /*the info on a valuation looks like this "a/x"
               and we want to substitute the constant a for the variable
               x throughout the formula*/
/*		             valuation.add(valuForm);
       }
     }

   }

   if (valuation.size()>0)
     TFormula.interpretFreeVariables(valuation, interpretation);  //surgery

  return
      true;
}  */
/*	
public char firstFreeVar(){
 TFormula chForm= new TFormula();
 String gVariables=TParser.gVariables;
 boolean found=false;
 int i=0;

 chForm.fKind = variable;

 while ((!found)&&(i<gVariables.length())){
   chForm.fInfo=gVariables.substring(i,i+1);

   if (freeTest(chForm))
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

*/
/*
public String constantsInFormula(){
  String leftStr=strNull;
  String rightStr=strNull;


  if (isSpecialPredefined())
    return
        strNull;

  if (fLLink!=null)
    leftStr=fLLink.constantsInFormula();

  if (fRLink!=null)
    rightStr=fRLink.constantsInFormula();

  if ((fLLink==null)&&(fRLink==null)){

    if (fInfo.length()>0){

      if (TParser.isConstant(fInfo.charAt(0)))
         leftStr = fInfo;
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

*/	

/**** should be in Parser as depends on variables

public char firstFreeVar(){
  TFormula chForm= new TFormula();
  String gVariables=TParser.gVariables;
  boolean found=false;
  int i=0;

  chForm.fKind = variable;

  while ((!found)&&(i<gVariables.length())){
    chForm.fInfo=gVariables.substring(i,i+1);

    if (freeTest(chForm))
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

*/

	

