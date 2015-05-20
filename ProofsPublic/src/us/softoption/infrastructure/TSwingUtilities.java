package us.softoption.infrastructure;


import javax.swing.text.JTextComponent;

public class TSwingUtilities extends TUtilities{


static  public String readSystemClipBoardToString(JTextComponent text, int filter){
// we want text to be created by the caller so that it is of the right type to
// extract what is wanted from the clipboard
 text.selectAll();    //any previous stuff?
 text.paste();        // rid of old in with new
 text.selectAll();    // all of new
 String input = text.getSelectedText();
 String outputStr=input;

             if (input==null)
                 return null;

             switch (filter){
               case noFilter:
                 break;
               case defaultFilter:
                 outputStr=defaultFilter(input);
                 break;
               case logicFilter:
                 outputStr=logicFilter(input);
                 break;
               case lispFilter:
                 outputStr=lispFilter(input);
                 break;


               case peculiarFilter:
                 outputStr=peculiarFilter(input);
                 break;

               default:;
             }

             return
                 outputStr;
           }




   static  public String readSelectionToString(JTextComponent text, int filter){
        String input = text.getSelectedText();
        String outputStr=input;

        if (input==null)
            return null;

        switch (filter){
          case noFilter:
            break;
          case defaultFilter:
            outputStr=defaultFilter(input);
            break;
          case logicFilter:
            outputStr=logicFilter(input);
            break;
          case lispFilter:
            outputStr=lispFilter(input);
            break;


          case peculiarFilter:
            outputStr=peculiarFilter(input);
            break;

          default:;
        }

        return
            outputStr;
      }
  
  static  public String readTextToString(JTextComponent text, int filter){
      String input = text.getText();
      String outputStr=input;

      if (input==null)
          return null;

      switch (filter){
        case noFilter:
          break;
        case defaultFilter:
          outputStr=defaultFilter(input);
          break;
        case logicFilter:
          outputStr=logicFilter(input);
          break;
        case lispFilter:
          outputStr=lispFilter(input);
          break;
        case peculiarFilter:
          outputStr=peculiarFilter(input);
          break;

        default:;
      }

      return
          outputStr;
    }



}











