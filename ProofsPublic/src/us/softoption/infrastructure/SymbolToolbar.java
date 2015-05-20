package us.softoption.infrastructure;

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
	
	//button.putClientProperty( "JButton.buttonType", "square"  ); // for Mac Aqua
	
/*	button.setSize(32, 30);  // March 09 was 22x20
	button.setPreferredSize(new Dimension(32, 30));   // was 21x20
	button.setMinimumSize(new Dimension(32, 30));   // was 21x20   new
	
	button.setMargin(new Insets(0, 0, 0, 0));

	
	button.setText(symbol);
	*/
	
	
	
	
	//button.putClientProperty( "JButton.buttonType", "toolbar"  );  March 09, not displaying well on Aqua

	//button.putClientProperty( "JComponent.sizeVariant", "mini" );  //March09
	
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			fText.replaceSelection(symbol);
			fText.requestFocus();
			
			
		}});
	
}	
	
	
	
	
	
}