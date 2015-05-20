package us.softoption.infrastructure;

import java.io.StringReader;

import jscheme.InputPort;
import jscheme.Scheme;

/*a global Scheme for general use */


public class TScheme {
  static public Scheme fScheme  = new Scheme(null);


  static public Object globalLispEvaluate(String inputStr){
     try {
                   InputPort input = new InputPort(new StringReader(inputStr));

                   {
                     Object x;
                     Object result;
                     String resultStr;
                     if (input.isEOF(x = input.read()))
                       return
                           null;

                       result = fScheme.eval(x);
                  return
                      result;
                   }
                 }
               catch (Exception ex) {

             //    writeToJournal("Scheme Exception: " + ex, true, false);

                 System.err.println("Scheme Exception: " + ex);
                }

                return
                    null;

       }
}
