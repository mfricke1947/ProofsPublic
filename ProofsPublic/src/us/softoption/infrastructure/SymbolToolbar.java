/*
Copyright (C) 2015 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

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

//scanned through 5/22/2015

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.JTextComponent;

public class SymbolToolbar extends JToolBar{
	
	//JPanel fEnclosure;
	String fSymbols;
	//JTextField fText;
	JTextComponent fText;
	JTextArea fTArea;

public SymbolToolbar(String symbols, JTextComponent text){


	fSymbols=symbols;
	fText=text;

	initialize();

}

void initialize(){
	
	String subStr;
	JButton newone;
	
	for (int i=0;i<fSymbols.length();i++){
		  subStr=fSymbols.substring(i, i+1);
		  newone= new JButton(subStr); 		  
         initializeSymbolButton(newone,subStr);
		  add(newone);
	      }

	
}
	
void initializeSymbolButton(JButton button, final String symbol){	
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			fText.replaceSelection(symbol);
			fText.requestFocus();
			
			
		}});
	
}	
	
	
	
	
	
}