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


import static us.softoption.infrastructure.Symbols.chArrow;
import static us.softoption.infrastructure.Symbols.chEquiv;
import static us.softoption.infrastructure.Symbols.chHArr;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chNotSign;

import java.io.Reader;


public class TJeffreyParser extends TPriestParser{

  /*We want several different parsers of similar structure; we define generic procedures
   which are called in the normal way.  Most documents and windows have fParser fields to
   ensure the correct reading and writing.*/
	
	/*
	 *  Jeffrey uses no brackets for quantiers, no brackets for arguments
	 *  ie like Priesst
	 *  
	 *  but symbols like Howson
	 */


  /*Nov 8th 2003 Old versions of this used to have the filtering input mechanism written into the code
   but Javas regular expression powers allow this to be done to the input before it ever gets to skip
   or to the parser*/

  /*constants*/
	
public static byte JEFFREY= CCParser.BRACKETFREEQUANTS;	

  public TJeffreyParser() {
	  fMinCellWidth=14;
  }

  public TJeffreyParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
      fMinCellWidth=14;
      }


@Override
  public String renderNot() {
    return
        String.valueOf(chNotSign);
  }

 @Override 
  public String renderImplic() {
  return
      String.valueOf(chArrow);
}
@Override
public String renderEquiv() {
return
    String.valueOf(chHArr);
}
@Override
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
}


/************************** End of Version *************************************/
