package us.softoption.infrastructure;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

//not using this much, preferring SymbolToolbar

public class Palette extends JPanel{
	
	//JPanel fEnclosure;
	String fSymbols;
	//JTextField fText;
	JTextComponent fText;
	JTextArea fTArea;

public Palette(String symbols, JTextComponent text/*, boolean ok */){


	//fEnclosure=enclosure;
	fSymbols=symbols;
	fText=text;
	setSize(new Dimension(330, 32));           //default  was 22
	setMaximumSize(new Dimension(330, 32));
	setMinimumSize(new Dimension(330, 32));
	setPreferredSize(new Dimension(330, 32));
	initialize();
}

void initialize(){
	
    setLayout(new GridBagLayout()); 
	
	String subStr;
	JButton newone;
	
	for (int i=0;i<fSymbols.length();i++){
		  subStr=fSymbols.substring(i, i+1);
		  newone= new JButton(subStr); 		  
		  initializeSymbolButton(newone,subStr);	  
		  add(newone,
				  new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
				           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 1));
	      }

	
}
	
void initializeSymbolButton(JButton button, final String symbol){
	
	//button.putClientProperty( "JButton.buttonType", "square"  ); // for Mac Aqua
	
	button.setSize(32, 30);  // March 09 was 22x20
	button.setPreferredSize(new Dimension(32, 30));   // was 21x20
	button.setMinimumSize(new Dimension(32, 30));   // was 21x20   new
	
	button.setMargin(new Insets(0, 0, 0, 0));

	
	button.setText(symbol);
	//button.set
	
	
	
	
	//button.putClientProperty( "JButton.buttonType", "toolbar"  );  March 09, not displaying well on Aqua

	//button.putClientProperty( "JComponent.sizeVariant", "mini" );  //March09
	
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			fText.replaceSelection(symbol);
			fText.requestFocus();
			
			
		}});
	
}	
	
	
	
	
	
}