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











