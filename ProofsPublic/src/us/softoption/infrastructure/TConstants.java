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

package us.softoption.infrastructure;

//11/1/08

import static us.softoption.infrastructure.Symbols.strCR;

public final class TConstants{

/*************Here are some 'compiler' flags ***************************/


public static final boolean DEBUG=false;     // flag  Set true to get extra menu items and windows (for programmer)
public static final boolean INSTRUCTOR_VERSION=false;

/************* Runtime flags *******************************************/

public static final long EXPIRY=1384898351 +60*864000; // when the code will expire (need to reset this), millisecs
public static final int APPLET_EXPIRY=2018;

/************* Miscellaneous *****************************************/


public static final boolean HIGHLIGHT=true;
public static final boolean TO_MARKER=true;

public static String fDefaultPaletteText= strCR+
"F \u2234 F \u2227 G"+
" \u223C  \u2227  \u2228  \u2283  \u2261  \u2200  \u2203  "+
 " \u2234 " +

strCR+
strCR+

" Rxy[a/x,b/y] (\u2200x)(Fx \u2283 Gx)";

/************* Errors ***********************************************/

public static final String fErrors1= Symbols.strCR+ "(*Universe must be non-empty.*)";
public static final String fErrors2= Symbols.strCR+ "(*The arity of a relation should be no greater than 2.*)"
                 +Symbols.strCR+"(*Any relation of arity n, n>2, can be represented*)"
                 +Symbols.strCR+"(*as a conjunction of n+1 2-arity relations.*)"+ Symbols.strCR;

public static final String fErrors3= Symbols.strCR+ "You win. You are right-- it is true!"+ Symbols.strCR;
public static final String fErrors4= Symbols.strCR+ "I win. You are mistaken-- it is false!"+ Symbols.strCR;
public static final String fErrors5= Symbols.strCR+ "You win. You are right-- it is false!"+ Symbols.strCR;
public static final String fErrors6= Symbols.strCR+ "I win. You are mistaken-- it is true!"+ Symbols.strCR;
public static final String fErrors7= Symbols.strCR+ "I win. I am right-- it is true!"+ Symbols.strCR;
public static final String fErrors8= Symbols.strCR+ "You win. I am mistaken-- it is false!"+ Symbols.strCR;
public static final String fErrors9= Symbols.strCR+ "I win. I am right-- it is false!"+ Symbols.strCR;
public static final String fErrors10= Symbols.strCR+ "You win. I am mistaken-- it is true!"+ Symbols.strCR;
public static final String fErrors11= Symbols.strCR+ "(* is not a term.*)"+ Symbols.strCR;
public static final String fErrors12= Symbols.strCR+ "(*Selection is illformed.*)"+ Symbols.strCR;
public static final String fErrors13= Symbols.strCR+ "(* is not a constant.*)"+ Symbols.strCR;
public static final String fErrors14= Symbols.strCR+ "(* is not a variable.*)"+ Symbols.strCR;
public static final String fErrors15= Symbols.strCR+
                  "(*The arity of a function should be no greater than 1.*)"+Symbols.strCR+
                  "(*Any function of arity n, n>1, can be 'Curried' to*)"+Symbols.strCR+
                  "(*unary functions-- here higher arguments are*)"+Symbols.strCR+
                  "(*just ignored.*)"+
                  Symbols.strCR;



}

