package us.softoption.interpretation;

import us.softoption.parser.TParser;
import us.softoption.infrastructure.*; import static us.softoption.infrastructure.Symbols.*; import static us.softoption.parser.TFormula.*;
import us.softoption.parser.TFormula;



/*

The only differences here are with UI and EI. For UI, we can have any constant as an instantiation but not a variable.
For EI we have a single new constant.


 uni       ok
 uniS      ok
 exi       ok
 exiS      unknown
 negUni    unknown
 negUniS   unknown
 negExi    unknown
 negExiS   unknown



*/


public class TBergmannTestNode extends TTestNode{

  public TBergmannTestNode (TParser aParser,TTreeModel aTreeModel){
  super(aParser,aTreeModel);
}

/*******************  Factory *************************/

public TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
  return
      new TBergmannTestNode (aParser,aTreeModel);
}


/******************************************************/


/********************  potential overrides for subclasses ************/

void doExi(TTestNode ltemp){
    TFormula newInstance = ((TFormula)ltemp.fAntecedents.get(0)).scope().copyFormula();

   TFormula variForm= ((TFormula)ltemp.fAntecedents.get(0)).quantVarForm().copyFormula();

   TFormula newConstant= TParser.newConstant(ltemp.fAntecedents,ltemp.fSuccedent);

if (newConstant!=null){
	TFormula.subTermVar(newInstance, newConstant, variForm);

  ltemp.fAntecedents.remove(0); // the exi

  ltemp.fAntecedents.add(0, newConstant); // the constant-- need this to go backwards
  ltemp.fAntecedents.add(0, variForm); // the variable
  ltemp.fAntecedents.add(0, newInstance); // the scope with a new constant

  straightInsert(ltemp, null);
}
  }

void doUniS(TTestNode ltemp ){
    TFormula newInstance = ( (TFormula) ltemp.fSuccedent.get(0)).fRLink.copyFormula();
    TFormula variForm = ( (TFormula) ltemp.fSuccedent.get(0)).quantVarForm();

    TFormula newConstant= TParser.newConstant(ltemp.fAntecedents,ltemp.fSuccedent);

    if (newConstant!=null){

    	TFormula.subTermVar(newInstance, newConstant, variForm);

      ltemp.fSuccedent.remove(0); // the uni

      ltemp.fSuccedent.add(0, newInstance);

      straightInsert(ltemp, null);
    }
  }



/********************** end of overrides *****************************/





String termsToTry (TFormula dummy1,TFlag dummy2){

  /*

     warns if there is capturing of variables}
   {This is to help find instantiations-- these have to be terms that occur }
   {(Herbrand Universe) and which are legitimate substitutions, no capturing}
*/
  String totalTerms=fParser.constantsInListOfFormulas(fAntecedents)
                    + fParser.constantsInListOfFormulas(fSuccedent);

      /*          String totalTerms=TFormula.freeAtomicTermsInListOfFormulas(fAntecedents)
                    + TFormula.freeAtomicTermsInListOfFormulas(fSuccedent);   */


  totalTerms=TUtilities.removeDuplicateChars(totalTerms);


  /*This cannot be of length 0, if it is we'll choose an arbitrary one */

  if ((totalTerms==null)||totalTerms.length()==0)
    totalTerms = TParser.gConstants.substring(0,1);

/*  TFormula termForm= new TFormula(TFormula.variable,"",null,null);

 for (int i=totalTerms.length()-1;i>-1;i--){
   termForm.fInfo=totalTerms.substring(i);

   if (!(quantform.fRLink.freeForTest(termForm, quantform.quantVarForm()))){

      totalTerms=totalTerms.substring(0,i-1); //{should prevent capturing}
      capturing.setValue(true);
   }

 } */
 return
     totalTerms;
}



}
