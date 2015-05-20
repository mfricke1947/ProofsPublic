package us.softoption.infrastructure;

//11/1/08

import static us.softoption.infrastructure.Symbols.strCR;


public final class TConstants{

/*************Here are some 'compiler' flags ***************************/


public static final boolean DEBUG=false;     // flag  Set true to get extra menu items and windows (for programmer)
public static final boolean INSTRUCTOR_VERSION=false;

/************* Runtime flags *******************************************/

public static final long EXPIRY=1264721218 +30*864000; // when the code will expire (need to reset this)
public static final int APPLET_EXPIRY=2011;

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

/************* Symbols *********************************************/
/*
char chSmallLeftBracket = '(';
String kSmallLeftBracket = "(";
char chSmallRightBracket = ')';
String kSmallRightBracket = ")";

char chTherefore='\u2234';

final char chLSqBracket = '[';
final String kLSqBracket = "[";
final char chRSqBracket = ']';
final String kRSqBracket = "]";

public static char chMult = '.';
final String kMult = ".";

public static char chAdd = '+';
static final String kAdd = "+";

static final String kAt = "@";

public static final char chAnd = '\u2227';
public static final char chNeg= '\u223C';
public static final char chUniquant='\u2200';
public static final char chOr = '\u2228';
public static final char chExiquant='\u2203';
public static final char chImplic = '\u2283';
public static final char chEquiv = '\u2261';

public static final char chUnique = '!';

public static final char chBlank = ' ';
public static final String strNull="";
public static final String Symbols.strCR = "\n";  // need to change this for cross platform

static final char chComma = ',';
static final char chQuestionMark = '?';
static final char chSlash = '/';

public static final char chInsertMarker = '>';

public static final char chLessThan = '<';
public static final String kLessThan = "<";

public static final char chEquals = '=';
public static final String kEquals = "=";

*/

}

