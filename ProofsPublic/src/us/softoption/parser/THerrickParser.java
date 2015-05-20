package us.softoption.parser;

//8/14/08


//import java.io.*;
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


public String renderAnd(){                    //need this for displaying the rewrite rules correctly see TRewrite
     return
         String.valueOf(chHerrickAnd);
         }

public String translateConnective(String connective){

  if (connective.equals(String.valueOf(chAnd)))
    return
        String.valueOf(chHerrickAnd);


 return
     super.translateConnective(connective);
}



}
