package us.softoption.infrastructure;

import com.google.gwt.user.client.ui.TextArea;

///18/11 tidied up some errors in this

// 11/1/08

//some scrap at the bottom

public class TGwtUtilities{
	
static public void writeOverJournalSelection(TextArea journal, String message){

        int current = journal.getCursorPos(); //if there isn't one it's dot which is the old one

        int selLength = journal.getSelectionLength();
        
        int messageLength = message.length();
        
        String text= journal.getText();
        
        text=text.substring(0,current)+
                           message +
                           text.substring(current+selLength);
        
       // journal.setCursorPos(current+messageLength); //after new sel
       // journal.setSelectionRange(current+messageLength,0); //pos, length
        
        journal.setText(text);
        
        journal.setCursorPos(current+messageLength);
        journal.setSelectionRange(current+messageLength,0);
        journal.setFocus(true);
        
     //   journal.setFocus(true);

     }	

 static public void writeToJournal(TextArea journal, String message, boolean highlight){

	// before aaa<sel>bbb
	 // after aaa<new>Ibbb or
	 // after aaaI<new>bbb with new selected
         
	 
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
         
      //   text=text.substring(0,newCaretPosition)+"Hello";
         
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
	
 /*

private void writeToJournal(String message, boolean highlight,boolean toMarker){

        int newCaretPosition = fJournalPane.getSelectionEnd(); //if there isn't one it's dot which is the old one

        int messageLength = message.length();

        if (messageLength>0) {

          fJournalPane.setSelectionStart(newCaretPosition);
          fJournalPane.setCaretPosition(newCaretPosition);    //leave existing selection and do everything after

          fJournalPane.replaceSelection(message);

          if (highlight) {
            fJournalPane.setSelectionStart(newCaretPosition);
            fJournalPane.setSelectionEnd(newCaretPosition+messageLength);

          }

        }
     }		
}



  * */

 
 
 
 
 
 

}