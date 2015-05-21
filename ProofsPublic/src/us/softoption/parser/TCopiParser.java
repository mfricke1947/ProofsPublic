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
public static final String gCopiVariables="uvwxyz";   //NEED {take care if you change these as some procedures use}
                                           //   their indices, e.g. TFormula.firstfreevar}


 public static final char chCopiAnd = '.';// {for CopiLogic}

 public TCopiParser(){
 }

 public TCopiParser(Reader aReader,boolean firstOrder)
 {
     super(aReader,firstOrder);
     }



@Override
public String renderAnd(){
   return
       String.valueOf(chCopiAnd);
         }
@Override
public String renderUniquant(){
	   return
	       "";
	         }

@Override
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


}






