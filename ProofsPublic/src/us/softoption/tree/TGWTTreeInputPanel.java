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

package us.softoption.tree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/*This is the little input panel that gets stuck at the top of proofs. We are actually going to do
two styles, because the layout of the rewrite one is slightly different. We'll use the number of parameters
 in the constructor to differentiate them*/

/*I am going to try to attach a symbol palette. It's done via a new constructor
 * which has String symbols as a parameter.*/


public class TGWTTreeInputPanel extends Composite{

  private Label fLabel1 = new Label();
  private Label fLabel2 = new Label();
  private TextBox fText1 = new  TextBox();
  private RichTextArea fText2 = new RichTextArea();


 // JPanel fSymbolPalette = new JPanel();  //usually buttons
 // SymbolToolbar fSymbolToolbar= null;
 // JPanel fComponentsPanel = new JPanel();  //usually buttons

  private VerticalPanel fOuter = new VerticalPanel();
  private HorizontalPanel fSymbolPalette = new HorizontalPanel();
  private HorizontalPanel fSymbolToolbar = new HorizontalPanel();
  private HorizontalPanel fComponentsPanel = new HorizontalPanel();
  
  /*
    private RichTextArea richText;

 // private RichTextArea.Formatter fFormatter;

  private VerticalPanel outer = new VerticalPanel();
  private HorizontalPanel topPanel = new HorizontalPanel();
  private HorizontalPanel bottomPanel = new HorizontalPanel();
  private HorizontalPanel buttonsPanel = new HorizontalPanel();
  
  */
  
  /*One label no palette */
  
  
  public TGWTTreeInputPanel(String label,  TextBox textField, Widget [] components) {   // mf code not JBuilder

 /*   this.setMaximumSize(new Dimension(32767, 32767));
    this.setMinimumSize(new Dimension(100, 81));
    this.setPreferredSize(new Dimension(100, 81));
    this.setLayout(new GridBagLayout());                   // the outer grid is a column of 3
*/
   // fLabel1.setMinimumSize(new Dimension(45, 16));
   // fLabel1.setText(label);
    
    fLabel1= new Label(label);   //new Jan 09

    fText1 = textField;

/*    fText1.setDragEnabled(true);
    
    fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
    fComponentsPanel.setMinimumSize(new Dimension(405, 30));
    fComponentsPanel.setPreferredSize(new Dimension(405, 30));

    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons
*/
    prepareComponentsPanel(components);

    fOuter.add(fLabel1);
    fText1.setWidth("440px");
    
    fOuter.add(fText1);
    fOuter.add(fComponentsPanel);
    
    initWidget(fOuter);
    
    }
  
  /*One label with palette */
  
  public TGWTTreeInputPanel(String label, 
		                  TextBox textField, 
		                  Widget [] components,
		                  String symbols) {   // mf code not JBuilder
/*
	    this.setMaximumSize(new Dimension(32767, 32767));
	    this.setMinimumSize(new Dimension(100, 81));
	    this.setPreferredSize(new Dimension(100, 81));
	    this.setLayout(new GridBagLayout());                   // the outer grid is a column of 3
*/
	   fLabel1= new Label(label);
	    
	/*    fLabel1.setMinimumSize(new Dimension(45, 16));
	    fLabel1.setPreferredSize(new Dimension(200, 16));   // using symbol palette-- keep to left 200
	    fLabel1.setMaximumSize(new Dimension(200, 16));
	    fLabel1.setSize(new Dimension(200, 16));
	    fLabel1.setText(label); */
	    
	   // fLabel1.setBorder(BorderFactory.createEtchedBorder());

	    fText1 = textField;

	 //   fText1.setDragEnabled(true);
	    
	    
	    initializeSymbolPalette(symbols);
	    initializeSymbolToolbar(symbols);
	    
	  //  fSymbolPalette.setBorder(BorderFactory.createEtchedBorder());

/*	    fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
	    fComponentsPanel.setMinimumSize(new Dimension(405, 30));
	    fComponentsPanel.setPreferredSize(new Dimension(405, 30));

	    fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons

*/
	    prepareComponentsPanel(components);

	 
        fOuter.add(fLabel1);
	    fOuter.add(fSymbolToolbar);
	    fOuter.add(fText1);
	    fOuter.add(fComponentsPanel);
	    
	    initWidget(fOuter);
	    
	    
	    }

 /*We have here a vertical grid of label, static text, label, static text, panel of horizontal grid of buttons*/
/*  Two Labels */

    public TGWTTreeInputPanel(String label1,
    		                TextBox textField1,
                            String label2,
                            RichTextArea textField2,
                            Widget [] components) {   // mf code not JBuilder
/*
      this.setMaximumSize(new Dimension(32767, 32767));
      this.setMinimumSize(new Dimension(100, 132));  //132
      this.setPreferredSize(new Dimension(100, 132));
      this.setLayout(new GridBagLayout());                   // vertical grid of 5
*/
    //  fLabel1.setMinimumSize(new Dimension(45, 16));
    //  fLabel1.setText(label1);
      
      fLabel1= new Label(label1);  // new Jan 09

      fText1 = textField1;
  //    fText1.setDragEnabled(true);
  //    fText1.setEditable(false);

   //   fLabel2.setMinimumSize(new Dimension(45, 16));
   //   fLabel2.setText(label2);
      
      fLabel2= new Label(label2);  // new Jan 09

      fText2 = textField2;
  /*    fText2.setDragEnabled(true);
      fText2.setEditable(false);

      fComponentsPanel.setMaximumSize(new Dimension(2147483647, 30));
      fComponentsPanel.setMinimumSize(new Dimension(405, 30));
      fComponentsPanel.setPreferredSize(new Dimension(405, 30));

      fComponentsPanel.setLayout(new GridBagLayout());               // the inner grid is a row of n components
*/

      prepareComponentsPanel(components);



fOuter.add(fLabel1);
fOuter.add(fText1);
fOuter.add(fLabel2);
fOuter.add(fText2);
fOuter.add(fComponentsPanel);

initWidget(fOuter);
    } 

    public void setLabel1(String theString){
        fLabel1.setText(theString);


    }
    public void setText1(String theString){
      fText1.setText(theString);
 //     fText1.selectAll();
//      fText1.requestFocus();

    }

   
    
  void prepareComponentsPanel(Widget [] components){
		fComponentsPanel.setStyleName("inputButtons");
		
	  	fComponentsPanel.setHeight("30px");
	  	
	  	fComponentsPanel.setSpacing(10);
	      
	      for (int i=0;i<components.length;i++){
	          fComponentsPanel.add(components[i]);
	      }	  
  }
    
/*************************/
    
 public TextBox getTextBox(){
	 return
			 this.fText1;
 }
 
 public void selectAllInTextBox(){
	 this.fText1.selectAll();
 }
    
/**********************Symbol Input Palette ***************************/
    
    // We take a String of symbols and create a pallette of buttons for each of them
    
void initializeSymbolPalette(String symbols){
	//Palette aPalette = new Palette(fSymbolPalette,symbols,fText1);
	
	if(symbols!=null){
/*		fSymbolPalette= new Palette(symbols,fText1);
		fSymbolPalette.setSize(new Dimension(300, 21));         //was 300
		fSymbolPalette.setMaximumSize(new Dimension(300, 21));
		fSymbolPalette.setMinimumSize(new Dimension(300, 21));
		fSymbolPalette.setPreferredSize(new Dimension(300, 21)); */
	}
	

	
	
	
//	fSymbolPalette.setPreferredSize(new Dimension(300, 21));
	
	/*
	if(symbols!=null){
	fSymbolPalette.setSize(new Dimension(300, 21));
	fSymbolPalette.setMaximumSize(new Dimension(300, 21));
	fSymbolPalette.setMinimumSize(new Dimension(300, 21));
	fSymbolPalette.setPreferredSize(new Dimension(300, 21));

	fSymbolPalette.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons
	
	//fSymbolPalette.setBackground(Color.BLACK);
	
	//fLabel1.setBackground(Color.BLACK);
	
	String subStr;
	JButton newone;
	
	for (int i=0;i<symbols.length();i++){
		  subStr=symbols.substring(i, i+1);
		  newone= new JButton(subStr); 		  
		  initializeSymbolButton(newone,subStr);	  
		  fSymbolPalette.add(newone,
				  new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
				           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 1));
	      }
	
	} */
}

void initializeSymbolButton(Widget button, final String symbol){
/*	button.setSize(20, 20);
	button.setPreferredSize(new Dimension(20, 20));  //was 20x20
	button.putClientProperty( "JButton.buttonType", "toolbar"  );
	
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		//	int start=fText1.getSelectionStart();
		//	int end=fText1.getSelectionEnd();
	//		fText1.replaceSelection(symbol);
	//		fText1.requestFocus();
		}}); */
	
}

/**********************Symbol Input Toolbar ***************************/

// We take a String of symbols and create a pallette of buttons for each of them

void initializeSymbolToolbar(String symbols){

if (symbols==null)
	symbols="";

if(symbols!=null){
//	fSymbolToolbar= new SymbolToolbar(symbols,fText1);
/*	fSymbolPalette.setSize(new Dimension(300, 21));         //was 300
	fSymbolPalette.setMaximumSize(new Dimension(300, 21));
	fSymbolPalette.setMinimumSize(new Dimension(300, 21));
	fSymbolPalette.setPreferredSize(new Dimension(300, 21)); */
}





//fSymbolPalette.setPreferredSize(new Dimension(300, 21));

/*
if(symbols!=null){
fSymbolPalette.setSize(new Dimension(300, 21));
fSymbolPalette.setMaximumSize(new Dimension(300, 21));
fSymbolPalette.setMinimumSize(new Dimension(300, 21));
fSymbolPalette.setPreferredSize(new Dimension(300, 21));

fSymbolPalette.setLayout(new GridBagLayout());               // the inner grid is a row of n buttons

//fSymbolPalette.setBackground(Color.BLACK);

//fLabel1.setBackground(Color.BLACK);

String subStr;
JButton newone;

for (int i=0;i<symbols.length();i++){
	  subStr=symbols.substring(i, i+1);
	  newone= new JButton(subStr); 		  
	  initializeSymbolButton(newone,subStr);	  
	  fSymbolPalette.add(newone,
			  new GridBagConstraints(i, 0, 1, 1, 0.0, 0.0
			           ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 1));
      }

} */
}

void initializeToolbarButton(Widget button, final String symbol){
/*button.setSize(20, 20);
button.setPreferredSize(new Dimension(20, 20));  //was 20x20
button.putClientProperty( "JButton.buttonType", "toolbar"  );

button.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e){
	//	int start=fText1.getSelectionStart();
	//	int end=fText1.getSelectionEnd();
	//	fText1.replaceSelection(symbol);
	//	fText1.requestFocus();
	}});
*/
}


public class CancelHandler implements ClickHandler{

    public CancelHandler(){
 //     putValue(NAME, "Cancel");
    }

     public void onClick(ClickEvent event ){


  //     removeInputPane();
     }

   }

public void cancel(){

//	clear(); //remove its contents, not it
    //enableMenus();
}


}


