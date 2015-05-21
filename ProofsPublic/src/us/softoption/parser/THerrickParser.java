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

//8/14/08

import static us.softoption.infrastructure.Symbols.chAnd;

import java.io.Reader;


public class THerrickParser extends TCopiParser{

public static final char chHerrickAnd = '&';// {for HerrickLogic}


  public THerrickParser(){
  }

  public THerrickParser(Reader aReader,boolean firstOrder)
  {
      super(aReader,firstOrder);
      }

@Override
public String renderAnd(){                    //need this for displaying the rewrite rules correctly see TRewrite
     return
         String.valueOf(chHerrickAnd);
         }

@Override
public String translateConnective(String connective){

  if (connective.equals(String.valueOf(chAnd)))
    return
        String.valueOf(chHerrickAnd);


 return
     super.translateConnective(connective);
}



}
