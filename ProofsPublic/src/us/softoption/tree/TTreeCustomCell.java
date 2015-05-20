package us.softoption.tree;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;


//TO DO superclass handlesselection

/*If I understand this correctly we have one 'cell' per column,
 * so, in a way, this are column renderers which run through the rows,
 */

public class TTreeCustomCell extends AbstractCell<Object>/*<String>*/ {
	TTreeRefreshListener fListener=null;
	
	String grey="153,153,135";
	String blue="102,255,155";
	String red="251,51,255";
	String black="0,0,0";
	String realRed="251,0,0";
	String realBlue="0,0,255";
	String white="255,255,255";

	char squareRoot= '\u221A';
	char largeX= '\u2716';
	
	
	static String padding="     "; //let css draw it
	
	
		
			
			
	/*		"<svg id=\"horizontal\" height=\"16\" width=\"16\" xmlns=\"http://www.w3.org/2000/svg\">"
		    +"<line x1=\"0\" y1=\"16\" x2=\"16\" y2=\"16\""
		          +"style=\"stroke:black;stroke-width:2\"/></svg>"; */
	
	//height=\"20\" 

	
	static String svgLeftDiag="<img "
		    +"src=\"images/leftDiag.png\" style=\"width:100%; height:16px;\">"
	        +"</img>";
	        
	/*old
	static String svgLeftDiag="<hr "
		    +"style=\"height:10px;border:none;"
			+"transform:rotate(-45deg);transform-origin: top right; "
			+"-ms-transform:rotate(-45deg); -ms-transform-origin: top right;"
		    +"-moz-transform:rotate(-45deg); -moz-transform-origin: top right; "
			+"-webkit-transform:rotate(-45deg);-webkit-transform-origin: top right; "
		    +"-o-transform:rotate(-45deg);-o-transform-origin: top right;\" "
	        +"/>";	
*/
	        
	        
	static String svgVertical="<img "
		    +"src=\"images/vertical.png\" style=\"width:40%;height:100%;\">"
	        +"</img>";  //don't make it width 100%, don't want it fat
						// have left the width
	
	//Feb 2014 width new
	
/*old	
	static String svgRightDiag="<img "
		    +"src=\"images/rightDiag.png\" style=\"width:100%; height:100%;\">"
	        +"</img>";
	
*/	
	
	static String svgRightDiag="<img "
		    +"src=\"images/rightDiag.png\" style=\"width:100%; height:16px;\">"
	        +"</img>";
			
			
			/*"<hr "
			 +"style=\"height:10px;"
			 + "transform:rotate(45deg);transform-origin: top left; "
				+"-ms-transform:rotate(45deg); -ms-transform-origin: top left;"
			    +"-moz-transform:rotate(45deg); -moz-transform-origin: top left; "
				+"-webkit-transform:rotate(45deg);-webkit-transform-origin: top left; "
			    +"-o-transform:rotate(45deg);-o-transform-origin: top left;\" "
		        +"/>";*/
	
	/*
	 * transform:rotate(45deg);
-ms-transform:rotate(5deg); 
-moz-transform:rotate(5deg); 
-webkit-transform:rotate(5deg);
-o-transform:rotate(5deg);
	 * 
	 */
	
	
	static String svgHorizontal="<img "
		    +"src=\"images/horizontal.png\" style=\"width:100%; height:16px;\">"
	        +"</img>";
	
//	images used to be .svg not .png
	
	



	public TTreeCustomCell(){
		  /*
	       * Sink the click and keydown events. We handle click events in this
	       * class. AbstractCell will handle the keydown event and call
	       * onEnterKeyDown() if the user presses the enter key while the cell is
	       * selected.
	       */
	     //super("click", "keydown");
		
		super("click");   //May 23
		
		
	}
	
	public void setRefreshListener(TTreeRefreshListener aListener){    
	      fListener=aListener;
	}
	
	
	
	
	
    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
      /**
       * The template for this Cell, which includes styles and a value.
       * 
       * @param styles the styles to include in the style attribute of the div
       * @param value the safe value. Since the value type is {@link SafeHtml},
       *          it will not be escaped before including it in the template.
       *          Alternatively, you could make the value type String, in which
       *          case the value would be escaped.
       * @return a {@link SafeHtml} instance
       */
   /*   @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
      SafeHtml cell(SafeStyles styles, SafeHtml value); */
      
      @SafeHtmlTemplates.Template("<div style=\"color:red\">{0}</div>")
      SafeHtml cell(SafeHtml value);
    }                                //#00FF00

    /** 
     * Create a singleton instance of the templates used to render the cell.
     */
    private static Templates templates = GWT.create(Templates.class);	
    
    /*
    interface MFTemplates extends SafeHtmlTemplates {
   
        
        @SafeHtmlTemplates.Template("<canvas id=\"c\" style=\"border: 1px solid #F00;\" width=\"200\" height=\"200\"></canvas>")
        SafeHtml cell(SafeHtml value);
      }                                //#00FF00

      /**
       * Create a singleton instance of the templates used to render the cell.
       
      private static MFTemplates mfTemplates = GWT.create(Templates.class);
    */
    
    @Override public boolean handlesSelection(){  // however, I'm not sure this does anything
    	return
    			true;
    }
	
    @Override
    public void render(Context context, /*String*/Object value, SafeHtmlBuilder sb) {
      /*
       * Always do a null check on the value. Cell widgets can pass null to
       * cells if the underlying data contains a null, or if the data arrives
       * out of order.
       */
      if (value == null) {
        return;
      }
      
    
      //value= "<div>"+value+"</div>";

      // If the value comes from the user, we escape it to avoid XSS attacks
      String valueStr=value.toString();
 
      SafeHtml safeValue;
/*
      // Use the template to create the Cell's html.
      SafeStyles styles = SafeStylesUtils.fromTrustedString(safeValue.asString());
      SafeHtml rendered = templates.cell(styles, safeValue);
      sb.append(rendered); 
      */
      
      /*Quite a lot can happen next, there are TestNodes and other things (numbers and strings)*/
      
      
      
      if (value instanceof TGWTTestNode){  // right now we have the formula as a string
    	 
    	  if (((TGWTTestNode)value).fLabelOnly)
    		  renderLabel((TGWTTestNode)value,sb);  // let someone else do it
    	  else{
    		  //if it is dead tick it 
    		  if ( ((TGWTTestNode)value).fDead)
    			  valueStr+=squareRoot;        // tick the dead ones	 
    	  
    		  //if it is closed cross it 
    	 
    		  if (((TGWTTestNode)value).fClosed)
    	        valueStr+=largeX;              // cross at bottom of closed branch

    		 // valueStr=padding+valueStr;       //pad it
    		  safeValue = SafeHtmlUtils.fromString(valueStr); //make it safe
    	 
    	 
    		  if (((TGWTTestNode)value).fSelected){
    			  SafeHtml rendered = templates.cell(safeValue);   //This colors it red
    			  sb.append(rendered /*safeValue*/); }
    		  else
    			  sb.append(safeValue);           // Just draw ifnot selected, not red
    	  	}
    	 }
      else   // not one of our testnodes at all
      
      {
    	renderString (valueStr,sb);
    	  
    	  /* SafeHtml mfSafeValue = SafeHtmlUtils.fromString("<canvas id=\"c\" style=\"border: 1px solid #F00;\" width=\"200\" height=\"200\"></canvas>");
    	 
    	        SafeStyles styles = SafeStylesUtils.fromTrustedString(mfSafeValue.asString());
    	        SafeHtml rendered = mfTemplates.cell(mfSafeValue);
    	  */
    	  
    	  
    	  //String experiment = "<canvas id=\"c\" style=\"border: 1px solid #F00;\" width=\"200\" height=\"200\"></canvas>";
    	 
   /* 	  if (valueStr.equals("Horizontal"))
    		  valueStr="";
    	 
    	  
    	 safeValue = SafeHtmlUtils.fromString(valueStr); // make the unchanged valueStr safe
    	 sb.append(safeValue);              // not a TGWT node
    	  
    	 // sb.append(rendered);              // not a TGWT node */
    	 
    	
    	 
    	 
    	// ("<img src=\"" + value +"\" />");
      }
    }
  
    
    
    public void renderString(String valueStr, SafeHtmlBuilder sb){
    	
    //There are some special cases	
    	
    	if (valueStr.equals("Center"))
    		; //sb.appendHtmlConstant(svgWild/*svgCenter*/);
    	else if (valueStr.equals("Horizontal")){
      		  //valueStr="";
    		sb.appendHtmlConstant(svgHorizontal);
    	//	SafeHtml safeValue = SafeHtmlUtils.fromString(svgHorizontal); // make the unchanged valueStr safe
    	//	sb.append(safeValue);              // not a TGWT node	
    	
    	}
    	else if (valueStr.equals("Vertical")){
    		sb.appendHtmlConstant(svgVertical);
  	
    	}
    	else if (valueStr.equals("LeftDiag")){
    		sb.appendHtmlConstant(svgLeftDiag);
  	
    	}
    	else if (valueStr.equals("RightDiag")){
    		sb.appendHtmlConstant(svgRightDiag);
  	
    	}
    	else{
      	  
    		SafeHtml safeValue = SafeHtmlUtils.fromString(valueStr); // make the unchanged valueStr safe
    		sb.append(safeValue);              // not a TGWT node	
    		
    		
    	}
    	
    }
    
    
    public void renderLabel(TGWTTestNode value, SafeHtmlBuilder sb){
    	
    	if (value.fLabelOnly){
    		 if (value.fLabel.equals("LeftDiag"))
    			 sb.appendHtmlConstant(svgLeftDiag);
    		 else
    			 if (value.fLabel.equals("RightDiag"))
        			 sb.appendHtmlConstant(svgRightDiag);
        	 else
        			 if (value.fLabel.equals("Vertical"))
            			 sb.appendHtmlConstant(svgVertical);
    		 
/*
    	   if (value.fLabel.equals("LeftDiag")){
    			 sb.appendHtmlConstant(svgLeftDiag/*"<canvas id=\"c\" style=\"border: 1px solid #F00;\" " +
	 		               "width=\"50\" height=\"20\"></canvas>" +
	 		               "<script type=\"text/javascript\"> " +
	 		               "var c=document.getElementById(\"c\");" +
	 		               "var ctx=c.getContext(\"2d\");" +
	 		               "ctx.fillStyle=\"red\";" +
	 		               "ctx.fillRect(0,0,20,20); </script>"); 
    		 }
    		 else if (value.fLabel.equals("RightDiag")){
    	    			 sb.appendHtmlConstant(svgRightDiag
    	    					 
    	    					//*"<canvas id=\"d\" style=\"border: 1px solid #F00;\" " +
    		 		             //  "width=\"50\" height=\"20\"></canvas>");	
    	
    		 } */
    	}
    	
    }
    
    
    
    @Override
    public void onBrowserEvent(Context context, Element parent, Object value, NativeEvent event,
        ValueUpdater<Object> valueUpdater) {
      // Let AbstractCell handle the keydown event.
    	
   //May 23   super.onBrowserEvent(context, parent, value, event, valueUpdater);

      // Handle the click event.
      // We are interested only in selecting, and then only in selecting our tree
      // Nodes ie TWTTestNodes
      
      
      if ("click".equals(event.getType())){
    		  if (value instanceof TGWTTestNode) {
        // Ignore clicks that occur outside of the outermost element.
      /*  EventTarget eventTarget = event.getEventTarget();
        if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget)))*/ 
        	
        //toggles on and off to allow selection by finger
        	
    			  TGWTTestNode valueNode=(TGWTTestNode)value;
    			  if (valueNode.fSelected==true)
    				  valueNode.fSelected=false;
    			  else
    				  valueNode.fSelected=true;
    			  boolean redrawOnly=true;
    			  doAction(value, valueUpdater,redrawOnly); // this causes the undersirable autoscrolling
    		  }
    		  else	 // new Feb 2014
    			  this.resetFocus(context, parent, valueUpdater);  //get the focus for scrolling
      }      
    }   
    
    private void doAction(Object value, ValueUpdater<Object> valueUpdater, boolean redrawOnly) {
        // Alert the user that they selected a value.
    //    Window.alert("You selected the color " + value);

        // Trigger a value updater. In this case, the value doesn't actually
        // change, but we use a ValueUpdater to let the app know that a value
        // was clicked.
       
        if (fListener!=null)
        	fListener.doRefresh(redrawOnly);
        
        //the refresh listener is the indexed column in TTreeDisplayCellTable and that calls a full
        //synchronize which is too much, now we have redraw only
        
        //valueUpdater.update(value);
      }
    
    
  }	


/************** svg experiments **********************/

/*
static String svgLeftDiag1="<svg id=\"left\" height=\"16\" width=\"16\"xmlns=\"http://www.w3.org/2000/svg\">"
	    +"<line x1=\"0\" y1=\"16\" x2=\"16\" y2=\"0\""
	          +"style=\"stroke:black;stroke-width:2\"/></svg>";
		
		static String svgRightDiag1="<svg id=\"right\" height=\"16\" width=\"16\" xmlns=\"http://www.w3.org/2000/svg\">"
			    +"<line x1=\"0\" y1=\"0\" x2=\"16\" y2=\"16\""
			          +"style=\"stroke:black;stroke-width:2\"/></svg>";	
		
		static String svgCenter="<svg id=\"right\" height=\"16\" width=\"25\" xmlns=\"http://www.w3.org/2000/svg\">"
			    +"<line x1=\"0\" y1=\"10\" x2=\"12\" y2=\"0\""
			          +"style=\"stroke:black;stroke-width:2\"/>" +
			          "<line x1=\"10\" y1=\"0\" x2=\"22\" y2=\"10\""
			          +"style=\"stroke:black;stroke-width:2\"/>" +
			          "" +
			          "" +
			          "</svg>";	
		
		static String svgHorizontal1="<img height=\"100%\" width=\"100%\""
			    +"src=\"Blue.jpg\">"
		          +"</img>";
	static String leftDiagonal="<div class=\"leftdiagonal\"></div>"; //let css draw it
		
		
		static String svgExample= "<svg id=\"svgelem\" height=\"20\" xmlns=\"http://www.w3.org/2000/svg\">"
	    +"<circle id=\"redcircle\" cx=\"0\" cy=\"0\" r=\"10\" fill=\"red\" /> </svg>";

/*String svgWild=	"<img "
	    +"src=\"images/rightDiag.svg\" style=\"width:100%; height:100%;\">"
        +"</img>";  
	 
String svgWild2=	"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"  "
		+"  viewBox=\"0 0 100 100\" preserveAspectRatio=\"xMidYMid slice\"  "
		+"  style=\"width:100%; height:100%; position:absolute; top:0; left:0; z-index:-1;\">  "
		+"  <linearGradient id=\"gradient\">  "
			+"	    <stop class=\"begin\" offset=\"0%\"/>  "
			+"	    <stop class=\"end\" offset=\"100%\"/>  "
			+"	  </linearGradient>  "
		+"		  <rect x=\"0\" y=\"0\" width=\"100\" height=\"100\" style=\"fill:url(#gradient)\" />  "
		+"		  <circle cx=\"50\" cy=\"50\" r=\"30\" style=\"fill:url(#gradient)\" />  "
						  +"		</svg>";	
	
	
String svgWild3=	"<div><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"  "
		+"  viewBox=\"0 0 20 12\" preserveAspectRatio=\"xMidYMid slice\"  "
		+"  style=\"width:100%; height:100%; position:absolute; top:0; left:0;\">  "
		+"  <linearGradient id=\"gradient\">  "
			+"	    <stop class=\"begin\" offset=\"0%\"/>  "
			+"	    <stop class=\"end\" offset=\"100%\"/>  "
			+"	  </linearGradient>  "
		+"		  <rect x=\"0\" y=\"0\" width=\"20\" height=\"10\" style=\"fill:green\" />  "
		+"		  <circle cx=\"10\" cy=\"10\" r=\"5\" style=\"fill:red\" />  "
						  +"		</svg></div>";	
String svgWild4=	"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"  "
		+"  viewBox=\"0 0 20 12\" preserveAspectRatio=\"xMidYMid slice\"  "
		+"  style=\"width:100%; height:100%;\">  "
		+"  <linearGradient id=\"gradient\">  "
			+"	    <stop class=\"begin\" offset=\"0%\"/>  "
			+"	    <stop class=\"end\" offset=\"100%\"/>  "
			+"	  </linearGradient>  "
		+"		  <rect x=\"0\" y=\"0\" width=\"20\" height=\"10\" style=\"fill:green\" />  "
		+"		  <circle cx=\"10\" cy=\"10\" r=\"5\" style=\"fill:red\" />  "
						  +"		</svg>";	

String svgWild5=	" <img src=\"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"  "
		+"  viewBox=\"0 0 20 12\" preserveAspectRatio=\"xMidYMid slice\"  "
		+"  style=\"width:100%; height:100%;\">  "
		+"  <linearGradient id=\"gradient\">  "
			+"	    <stop class=\"begin\" offset=\"0%\"/>  "
			+"	    <stop class=\"end\" offset=\"100%\"/>  "
			+"	  </linearGradient>  "
		+"		  <rect x=\"0\" y=\"0\" width=\"20\" height=\"10\" style=\"fill:green\" />  "
		+"		  <circle cx=\"10\" cy=\"10\" r=\"5\" style=\"fill:red\" />  "
						  +"		</svg>\"  </img>";
	
	*
	*
	*
	*
	*/
