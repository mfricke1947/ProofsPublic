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

import com.google.gwt.user.client.ui.TextArea;

///18/11 tidied up some errors in this

// 11/1/08

//some scrap at the bottom

public class TGWTUtilities{
	
static public void writeOverJournalSelection(TextArea journal, String message){

        int current = journal.getCursorPos(); //if there isn't one it's dot which is the old one

        int selLength = journal.getSelectionLength();
        
        int messageLength = message.length();
        
        String text= journal.getText();
        
        text=text.substring(0,current)+
                           message +
                           text.substring(current+selLength);
        
        journal.setText(text);
        
        journal.setCursorPos(current+messageLength);
        journal.setSelectionRange(current+messageLength,0);
        journal.setFocus(true);
        

     }	

 static public void writeToJournal(TextArea journal, String message, boolean highlight){
         
	 
	 int oldCaretPos = journal.getCursorPos();

	 int oldSelLength=journal.getSelectionLength();
	 
	 int newCaretPos=oldCaretPos+oldSelLength;
	 
	 int messageLength = message.length();
         
               
         String text= journal.getText();
         String before=text.substring(0,oldCaretPos);
         String after=text.substring(oldCaretPos+oldSelLength);
         
         text=before+
              message +
              after; // we con't want to include the original selection
        
         
         journal.setText(text);

         if (messageLength>0) {
        	 
        	 // before aaa<sel>bbb
        	 // after aaa<new>Ibbb or
        	 // after aaa<new>bbb with new selected

        	// journal.setSelectionRange(newCaretPos,messageLength); new Nov 11
        	// journal.setSelectionRange(newCaretPos,0);
        	// journal.setCursorPos(newCaretPos);    //leave existing selection and do everything after;

           if (highlight) {
        	 newCaretPos=oldCaretPos;
        	 journal.setCursorPos(newCaretPos);
           	 journal.setSelectionRange(newCaretPos,messageLength);
           	 journal.setFocus(true);
           }
           else{
        	   newCaretPos=oldCaretPos+messageLength;
        	   journal.setCursorPos(newCaretPos);
        	   journal.setSelectionRange(newCaretPos,0);
           }
         }
      }	
	
}