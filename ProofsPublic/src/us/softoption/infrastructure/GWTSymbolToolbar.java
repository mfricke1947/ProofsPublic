/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package us.softoption.infrastructure;



import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI
 * for all rich text formatting, dynamically displayed only for the available
 * functionality.
 * 
 * http://google-web-toolkit.googlecode.com/svn-history/r1137/trunk/samples/kitchensink/src/com/google/gwt/sample/kitchensink/client/RichTextToolbar.java
 */
// @SuppressWarnings("deprecation")
public class GWTSymbolToolbar extends Composite {
	
	String fSymbols="ABCD";
//	Formatter fFormatter;
	
	private Strings strings = (Strings) GWT.create(Strings.class);


	  private RichTextArea fRichText;
	  private Formatter fFormatter;
	  
	  private boolean fHTMLMode=false;

	  private VerticalPanel outer = new VerticalPanel();
	  private HorizontalPanel topPanel = new HorizontalPanel();
	  private HorizontalPanel bottomPanel = new HorizontalPanel();
//	  private HorizontalPanel buttonsPanel = new HorizontalPanel();

/*The outer contains the topPanel and the bottom Panel. The topPanel
 * contains the buttons. The bottom Panel contains some Menus. And the 
 * Richtext is not placed here. Rather, the calling routine creates
 * richtext, places it by CSS and passes it here.*/

	  private ListBox backColors;
	  private ListBox foreColors;
	
    
	
	private EventHandler fHandler = new EventHandler();
	
	

  /**
   * This {@link ClientBundle} is used for all the button icons. Using a bundle
   * allows all of these images to be packed into a single image, which saves a
   * lot of HTTP requests, drastically improving startup time.
   */
  public interface Images extends ClientBundle {

   @Source("com/google/gwt/sample/showcase/client/content/text/bold.gif")
    ImageResource bold();

 
    
 

  }

  /**
   * This {@link Constants} interface is used to make the toolbar's strings
   * internationalizable.
   */
  public interface Strings extends Constants {



    String bold();
    
    String black();

    String blue();

    String color();

    String green();

    String red();

    String white();

    String yellow();

    
  }

  /**
   * We use an inner EventHandler class to avoid exposing event methods on the
   * RichTextToolbar itself.
   */
  private class EventHandler implements ClickHandler, ChangeHandler,
      KeyUpHandler {

    public void onChange(ChangeEvent event) {
      Widget sender = (Widget) event.getSource();

      if (sender == backColors) {
        fFormatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
        backColors.setSelectedIndex(0);
      } else if (sender == foreColors) {
        fFormatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
        foreColors.setSelectedIndex(0);
      } 
    }

    public void onClick(ClickEvent event) {
      Widget sender = (Widget) event.getSource();
      
if (sender instanceof SymbolPushButton){
	String insertStr=((SymbolPushButton)sender).fInsertStr;
	fFormatter.insertHTML(insertStr);
	
/*	String test=getSelection();

//	fFormatter.createLink("Hello");
	
	String allText=richText.getHTML();
	
	
	int startSel=allText.indexOf("<a href=\"Hello\">");
	int endSel=allText.indexOf("</a>", startSel);
	
	//String selStr=allText.substring(startSel+16, endSel);
	
//	fFormatter.removeLink();
	
	allText=richText.getHTML();
	
	allText=richText.getHTML();
	
	// Th<a href="Hello">eca</a>t
	
	
	*/
	

	
	
	
	/*   if (sender == bold) {  	  
    	  String insertStr=((SymbolPushButton)sender).fInsertStr;
    	  
    	 // sender.
    	  
    	  fFormatter.insertHTML(insertStr);
    	  
    	  */
    	  
    	  
       // basic.toggleBold();
      }  
else if (sender == fRichText) {
        // We use the RichTextArea's onKeyUp event to update the toolbar status.
        // This will catch any cases where the user moves the cursur using the
        // keyboard, or uses one of the browser's built-in keyboard shortcuts.
        updateStatus();
      }
}

   
    public void onKeyUp(KeyUpEvent event) {
      Widget sender = (Widget) event.getSource();
      if (sender == fRichText) {
        // We use the RichTextArea's onKeyUp event to update the toolbar status.
        // This will catch any cases where the user moves the cursur using the
        // keyboard, or uses one of the browser's built-in keyboard shortcuts.
        updateStatus();
      }
    }
  }




  


  /**
   * Creates a new toolbar that drives the given rich text area.
   * 
   * @param richText the rich text area to be controlled
   */
  public GWTSymbolToolbar(RichTextArea richText, String symbols) {
    this.fRichText = richText;
    this.fSymbols=symbols;

    
    this.fFormatter = richText.getFormatter();

    outer.add(topPanel);
    outer.add(bottomPanel);
   // topPanel.setWidth("100%");
    topPanel.setSpacing(2);
    
    bottomPanel.setWidth("100%");
    
    outer.setWidth("100%");
    
    

    initWidget(outer);
    setStyleName("gwt-RichTextToolbar");
    fRichText.addStyleName("hasRichTextToolbar");

    if (fFormatter != null) {
     // topPanel.add(bold = createPushButton("X","X", "Insert X"));
    	
   
 	   String subStr;
 	   SymbolPushButton newone;   
 	   for (int i=0;i<fSymbols.length();i++){
 		   subStr=fSymbols.substring(i, i+1);
 		   newone= createPushButton(subStr,subStr, "Insert "+subStr); 		  
    //     initializeSymbolButton(newone,subStr);
 		  topPanel.add(newone);
 		  	  
 	      }
 	  // topPanel.add(buttonsPanel);
      
    }

    if (fFormatter != null) {

      ;
    }

    if (fFormatter != null) {
  // temp disabled    bottomPanel.add(backColors = createColorList("Background"));


      // We only use these handlers for updating status, so don't hook them up
      // unless at least basic editing is supported.
      fRichText.addKeyUpHandler(fHandler);
      fRichText.addClickHandler(fHandler);
    }
    
//	String subStr;
//	JButton newone;   
//    for (int i=0;i<fSymbols.length();i++){
//		  subStr=fSymbols.substring(i, i+1);
//		  newone= new JButton(subStr); 		  
  //     initializeSymbolButton(newone,subStr);
	//	  add(newone);
//	      }
    
    
//  }
  
	
	//button.putClientProperty( "JButton.buttonType", "square"  ); // for Mac Aqua
	
/*	button.setSize(32, 30);  // March 09 was 22x20
	button.setPreferredSize(new Dimension(32, 30));   // was 21x20
	button.setMinimumSize(new Dimension(32, 30));   // was 21x20   new
	
	button.setMargin(new Insets(0, 0, 0, 0));

	
	button.setText(symbol);
	*/
	
	
	
	
	//button.putClientProperty( "JButton.buttonType", "toolbar"  );  March 09, not displaying well on Aqua

	//button.putClientProperty( "JComponent.sizeVariant", "mini" );  //March09
	
	/*
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			fText.replaceSelection(symbol);
			fText.requestFocus();
			
		
		}}); */
	
}
  
  

  private ListBox createColorList(String caption) {
    ListBox lb = new ListBox();
    lb.addChangeHandler(fHandler);
    lb.setVisibleItemCount(1);

    lb.addItem(caption);
    lb.addItem(strings.white(), "white");
    lb.addItem(strings.black(), "black");
    lb.addItem(strings.red(), "red");
    lb.addItem(strings.green(), "green");
    lb.addItem(strings.yellow(), "yellow");
    lb.addItem(strings.blue(), "blue");
    return lb;
  }

  
 class SymbolPushButton extends PushButton{
	 String fInsertStr;
	 
	public SymbolPushButton(String insertText,String upText, String tip) {
		super(upText,tip);
		fInsertStr=insertText;
	    //setSize("14px", "14px");
	}
 }
  
  
  private SymbolPushButton createPushButton(String insertText,String upText, String tip) {
	    SymbolPushButton pb = new SymbolPushButton(insertText,upText,tip);
	       	    
	    pb.addClickHandler(fHandler);

	    return pb;
	  }
  
  
 /**************************  toggle HTML *******************************/
/* we can use both we need to know which we are using */
  
  public void useHTML(boolean use){
	if (!use){
		fRichText.setText(fRichText.getHTML());
		fHTMLMode=false;
	}
	else {
		fRichText.setHTML(fRichText.getText());
		fHTMLMode=true;				
		
	}
		
	 	  
  }
  
 /**************************  Selection *******************************/
  
  /** Native JavaScript that returns the selected text and position of the start **/
 
  /*code from RichTextToolbar, should be good */
  
  public static native JsArrayString getSelection(Element elem) /*-{
  	var txt = "";
  	var pos = 0;
  	var range;
  	var parentElement;
  	var container;

      if (elem.contentWindow.getSelection) {
      	txt = elem.contentWindow.getSelection();
      	pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
      } else if (elem.contentWindow.document.getSelection) {
      	txt = elem.contentWindow.document.getSelection();
      	pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
  		} else if (elem.contentWindow.document.selection) {
  			range = elem.contentWindow.document.selection.createRange();
      		txt = range.text;
      		parentElement = range.parentElement();
      		container = range.duplicate();
      		container.moveToElementText(parentElement);
      		container.setEndPoint('EndToEnd', range);
      		pos = container.text.length - range.text.length;
      	}
  		return [""+txt,""+pos];
  }-*/;
  
	/** Method called to toggle the style in HTML-Mode **/
public void changeHtmlStyle(String startTag, String stopTag) {
		JsArrayString tx = getSelection(fRichText.getElement());
		String txbuffer = fRichText.getText();
		Integer startpos = Integer.parseInt(tx.get(1));
		String selectedText = tx.get(0);
		fRichText.setText(txbuffer.substring(0, startpos)+startTag+selectedText+stopTag+txbuffer.substring(startpos+selectedText.length()));
	}   
  

  public String getSelectionAsHTMLOlder(){  //This was a real hack!
	//This is a hack to get the selection by putting a dummy marker around it then removing it	
		
	String fakeUrl=	"H1e2l3l4o";
	String tag= "<a href=\""+fakeUrl+"\">";

	int tagLength= tag.length();
		
	fFormatter.createLink(fakeUrl);
		
		String allText=fRichText.getHTML();
		
		
		int startSel=allText.indexOf(tag);
		int endSel=allText.indexOf("</a>", startSel);
		
		String selStr=allText.substring(startSel+tagLength, endSel);
		
		fFormatter.removeLink();
		
		//There is a problem, if there was no selection, the text of the link will be
		// inserted as extra text changing it.
		
		 if (selStr.equals(fakeUrl)){  // we have a problem (and we are assuming that fakeUrl
			                           // does not actually occur in the text
			 selStr="";                //We are going to return nothing
			 
			 allText=fRichText.getHTML(); //start again with the altered text
			 
			 String beforeStr=allText.substring(0, startSel);
			 String afterStr=allText.substring(startSel+fakeUrl.length());
			 
			 if (allText.substring(startSel, startSel+fakeUrl.length()).equals(fakeUrl))
				 allText=beforeStr+afterStr; // remove insertion
			 
			 fRichText.setHTML(allText);
			 
			 //works, but removes focus (don't worry about it)
		
		 }
		
	//	allText=richText.getHTML();
		

	return
			selStr;
	}
  
  public String getSelectionAsHTMLOld(){
		//This is a hack to get the selection some dummy in then removing it, gwt surrounds the dummy with divs	
		
	  // The problem with it is that it removes the selStart selEnd selection markers June 2103
	  
		String fakeHTML=	"H1e2l3l4o";;
		int    tagLength= fakeHTML.length();
						
		String allText=fRichText.getHTML();
		
		int allLength=allText.length();
		
		fFormatter.insertHTML(fakeHTML);   // this removes the selection and inserts the fake
		
		String modifiedText=fRichText.getHTML();
		int modLength=modifiedText.length();  
		
		int selLength=allLength+tagLength-modLength;
		
		int selStart=modifiedText.indexOf(fakeHTML);
		
		int selEnd=selStart+selLength;
		
		String selStr=allText.substring(selStart, selEnd);
	
		fRichText.setHTML(allText);	//set it back as it was
				

		return
				selStr;
		}
  
  
  /* public String getSelectionAsTextOld(){  // !

	  String outStr;
	  String htmlStr;
	  
	  htmlStr=getSelectionAsHTML();
	  
	  RichTextArea dummyText=new RichTextArea();
	  dummyText.setHTML(htmlStr);
	  outStr=dummyText.getText();
	  
	return
			outStr;

  } */

  
  public String getSelectionAsText(){  // !

	  String outStr="";
	  String htmlStr="";
	  
	  Element elem=fRichText.getElement();
	  
	  try {
	  
	  JsArrayString txt = getSelection(elem);
	  outStr=txt.get(0);
	  }	  
	  catch (Exception e) {};
	    
	return
			outStr;

  }
  
  
  /**
   * Updates the status of all the stateful buttons.
   */
  private void updateStatus() {
    if (fFormatter != null) {
;//      bold.setDown(basic.isBold());

    }

    if (fFormatter != null) {
     ; //strikethrough.setDown(extended.isStrikethrough());
    }
    
   
    
  }
}
