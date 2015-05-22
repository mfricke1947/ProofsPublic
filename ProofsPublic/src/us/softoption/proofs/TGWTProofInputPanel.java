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

package us.softoption.proofs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/*This is the little input panel that gets stuck at the top of proofs. We are actually going to do
two styles, because the layout of the rewrite one is slightly different. We'll use the number of parameters
 in the constructor to differentiate them*/

/*I am going to try to attach a symbol palette. It's done via a new constructor
 * which has String symbols as a parameter.*/


public class TGWTProofInputPanel extends Composite{

  private Label fLabel1 = new Label();
  private Label fLabel2 = new Label();
  private TextBox fText1 = new  TextBox();
  private TextBox fText2 = new TextBox();

  private VerticalPanel fOuter = new VerticalPanel();
  private HorizontalPanel fSymbolPalette = new HorizontalPanel();
  private HorizontalPanel fSymbolToolbar = new HorizontalPanel();
  
  private HorizontalPanel fComponentsPanel = new HorizontalPanel();
  
  
  /*One label no palette */
  
  
  public TGWTProofInputPanel(String label,         //label
		  					TextBox textField,     // text input
		  					Widget [] components) {   // cancel, go buttons +

    fLabel1= new Label(label);   //new Jan 09

    fText1 = textField;

    prepareComponentsPanel(components);

    fOuter.add(fLabel1);
    fText1.setWidth("440px");
    
    fOuter.add(fText1);
    fOuter.add(fComponentsPanel);
    
    initWidget(fOuter);
    
    }
  
  /*One label with palette */
  
  public TGWTProofInputPanel(String label, 
		                  TextBox textField, 
		                  Widget [] components,
		                  String symbols) {   // palette

	   fLabel1= new Label(label);

	    fText1 = textField;

	 //   fText1.setDragEnabled(true);
	    
	    
	    initializeSymbolPalette(symbols);
	    initializeSymbolToolbar(symbols);
	    
		prepareComponentsPanel(components);
	    
	    fText1.setWidth("440px");

        fOuter.add(fLabel1);
	    fOuter.add(fSymbolPalette);  //above text?
	  
	    fOuter.add(fText1);
	    fOuter.add(fComponentsPanel);
	    
	    initWidget(fOuter);
	    	    
	    }

 /*We have here a vertical grid of 5 items label, TextBox, label, TextBox, 
  * panel of horizontal grid of buttons, which can be Cancel Go
  * and list of rewrite rules*/
  /*Used for rewrites
/*  Two Labels */

    public TGWTProofInputPanel(String label1,
    		                TextBox textField1,
                            String label2,
                           TextBox /*RichTextArea*/ textField2,
                            Widget [] components) {   // mf code not JBuilder

      
      fLabel1= new Label(label1);  // new Jan 09

      fText1 = textField1;    
      fLabel2= new Label(label2);  // new Jan 09

      fText2 = textField2;

      prepareComponentsPanel(components);

      fText1.setWidth("500px");
      fText2.setWidth("500px");

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

    }

   
    
  void prepareComponentsPanel(Widget [] components){
		fComponentsPanel.setStyleName("inputButtons");
		
	  	fComponentsPanel.setHeight("30px");
	  	
	  	fComponentsPanel.setSpacing(10);
	      
	      for (int i=0;i<components.length;i++){
	    	  if (components[i]!=null)
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
	
	fSymbolPalette.setStyleName("inputSymbolPalette");
	
	//fSymbolPalette.setHeight("22px");  prefer to css this, but cannot seem to do it
	
	fSymbolPalette.setSpacing(1);
	
	if(symbols!=null&&!symbols.equals("")){
		String subStr;
		PushButton b;
		
		
		
		for (int i=0;i<symbols.length();i++){
			  subStr=symbols.substring(i, i+1);
			  b=createSymbolButton(subStr); 
			  b.setSize("10px","14px");
			  fSymbolPalette.add(b);    
			  }


	}
}


PushButton createSymbolButton(final String symbol){
	PushButton b=new PushButton(symbol, new ClickHandler() {
	      public void onClick(ClickEvent event) {
//	        Window.alert("How high?");
	        
	        int selStart=fText1.getCursorPos();
	        String selection=fText1.getSelectedText();
	        int selEnd= selStart+ selection.length();
	        String oldText=fText1.getText();
	        
	        String newText= oldText.substring(0,selStart) +
	        				symbol+
	        				oldText.substring(selEnd);
	        fText1.setText(newText);	        
	      }
	    });
	return b;
}

void initializeSymbolButton(Widget button, final String symbol){
	
}

/**********************Symbol Input Toolbar ***************************/

// We take a String of symbols and create a pallette of buttons for each of them

void initializeSymbolToolbar(String symbols){

if (symbols==null)
	symbols="";

if(symbols!=null){

}

}

void initializeToolbarButton(Widget button, final String symbol){

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

}


}


