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

import static us.softoption.infrastructure.Symbols.chNotSign;
import static us.softoption.infrastructure.Symbols.strCR;

import java.io.StringReader;
import java.util.ArrayList;

import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class TBarwiseTreeController extends  TTreeController{
//	  JMenuItem anaConMenuItem = new JMenuItem();
	  String anaConJustification = " Ana Con";
	  
	  
public TBarwiseTreeController(TParser aParser, TReset aClient,TJournal itsJournal, VerticalPanel inputPanel,
				 TGWTTree itsTree,TTreeDisplayCellTable itsDisplay){
	super(aParser, aClient,itsJournal, inputPanel,
			 itsTree, itsDisplay);
}


@Override
public void executeAnaCon(){
	TGWTTestNode firstSelected=null;
	TGWTTestNode secondSelected=null;
	TFormula firstFormula = null;
	TFormula secondFormula = null;
	
	int rootDepth= 0;
	int selectedDepth= 0;
	int depthIncrement= 0;
	

//    TGWTTestNode selected=null;
    
    ArrayList <TGWTTestNode> selectedNodes= selectedNodes(fRoot);

    if ((selectedNodes!=null)&&(selectedNodes.size()==1))
    	{
    	firstSelected= selectedNodes.get(0);
    	if ((firstSelected.fAntecedents != null &&
    		 firstSelected.fAntecedents.size() == 1)) {
    		    firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
    		}
    	
    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	selectedDepth= fRoot.getNodeDepth(firstSelected);
    	depthIncrement= rootDepth - selectedDepth;
    	}
    	
    if ((selectedNodes!=null)&&(selectedNodes.size()==2))
		{
    	firstSelected= selectedNodes.get(0);
    	if ((firstSelected.fAntecedents != null &&
    			firstSelected.fAntecedents.size() == 1)) {
		    	firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
			}
    	secondSelected= selectedNodes.get(1);
    	if ((secondSelected.fAntecedents != null &&
    			secondSelected.fAntecedents.size() == 1)) {
				secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));
			}
    	
    	rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	selectedDepth= fRoot.getNodeDepth(secondSelected);
    	depthIncrement= rootDepth - selectedDepth;
		} 	
  //  	NEED TO SORT
 /*     	int rootDepth= fRoot.getLiveDepth();// May 2012 fRoot.getDepth();
    	int selectedDepth= fRoot.getNodeDepth(selected);
    	int depthIncrement= rootDepth - selectedDepth; */
    	
    	if (firstSelected!=null)
    		firstSelected.deSelectAll(); 
      	    	
    	doAnaCon(firstSelected,secondSelected,firstFormula,secondFormula, depthIncrement);
    	
    
	{TGWTTestNode dummy =fRoot;
	
	//TO DO it may be better to do this somewhere else
	 fDisplayTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,dummy);
	}
}


/*********************** Ana Con for Barwise ***************************/

void doAnaCon(TGWTTestNode selected,TGWTTestNode selected2,TFormula theFormula,
		TFormula theFormula2, int depthIncrement){
	
    Button defaultButton;
    TGWTTreeInputPanel inputPane;

    TextBox text = new TextBox();
    text.setText("Target? (atomic formula or negation of atomic formula) Use "+ chNotSign + " or ~ for negation.");
    text.selectAll();

    defaultButton = new Button("Go");
    defaultButton.addClickHandler(new AnaConAction(text, selected,
    		selected2,theFormula,theFormula2,depthIncrement));
 
    Button[]buttons = {cancelButton(), defaultButton };  // put cancel on left
    inputPane = new TGWTTreeInputPanel("Doing Ana Con", text, buttons);


    addInputPane(inputPane,SELECT);
	
		}


public class AnaConAction implements ClickHandler/*AbstractAction*/ {
	TextBox fText;


	TGWTTestNode fSelected=null;
	TGWTTestNode fSelected2=null;
	TFormula fFormula;
	TFormula fFormula2;
	int fDepthIncrement;
	
	 ArrayList <TFormula> fSemantics= new ArrayList();
	
public AnaConAction(TextBox text, 
		TGWTTestNode selected, TGWTTestNode selected2,
			   TFormula formula,TFormula formula2, int depthIncrement){
	     fText=text;
	     fSelected=selected;
	     fSelected2=selected2;
	     fFormula=formula;
	     fFormula2=formula2;
	     fDepthIncrement=depthIncrement;
}	
	
	public void onClick(ClickEvent event) {		{
			String aString = fText.getText();
			aString=TUtilities.defaultFilter(aString);

		     TFormula target = new TFormula();
		     StringReader aReader = new StringReader(aString);
		     boolean wellformed=false;

		  //   wellformed=fParser.term(term,aReader);
		     
		     wellformed=fParser.wffCheck(target, aReader);

		     if ((!wellformed)||
		    	  !(TParser.isAtomic(target)||(TParser.isNegation(target)&&TParser.isAtomic(target.fRLink))) 
		    	) {
		       String message = "The string is neither an atomic formula nor the negation of an atomic formula." +
		                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

		       bugAlert (message,"");
		       
		       
		       //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

//		                        fText.setText(message);
//		                        fText.selectAll();
//		                        fText.requestFocus();
		                      }

		                      else {   // we're good
		                    	  
		                        {
		                        	
		                        if (!testValidity(target)){
		                 	       String message = "We have not been able to find a proof of your formula.";

		                         bugAlert (message,"");
		                 	       
//		                 	    fText.setText(message);
//		                        fText.selectAll();
//		                        fText.requestFocus();
		                      }
		                        else{

		                          TGWTTestNode newDataNode = new TGWTTestNode(fParser,fGWTTree);
		                          newDataNode.fAntecedents.add(0,target /*scope*/ );
		                          newDataNode.fFirstjustno=fSelected.fLineno;
		                          
		                          if (fSelected2!=null)
		                        	  newDataNode.fSecondjustno=fSelected2.fLineno;
		                          
		                          
		                          newDataNode.fJustification= anaConJustification;
		                          newDataNode.fWorld= fSelected.fWorld;

		                         // selected.fDead=true;    don't make it dead


		                          fSelected.straightInsert(fSelected,newDataNode,null,fDepthIncrement);

		                          removeInputPanel();
		                          
		                          refreshDisplay();
		                        }
		                        }

		                      }

		                  }

		                }	


	   private TFormula cubeOrDodecOrTet(){
		   TFormula tetOrCube,tetOrCubeOrDodec;
		   TFormula tetX,cubeX,dodecX,allX=null;
			  
			  tetX=new TFormula(TFormula.predicator,"Tet",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null));
			  TFormula notTet=new TFormula(TFormula.unary,chNeg,null,
					  tetX);
			  cubeX=new TFormula(TFormula.predicator,"Cube",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),null));
			  TFormula notCube=new TFormula(TFormula.unary,chNeg,null,
					  cubeX);
			  dodecX=new TFormula(TFormula.predicator,"Dodec",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),null));
			  TFormula notDodec=new TFormula(TFormula.unary,chNeg,null,
					  dodecX);
			  TFormula cube=new TFormula(TFormula.binary,chAnd,cubeX,
					  new TFormula(TFormula.binary,chAnd,notTet,notDodec));
			  TFormula dodec=new TFormula(TFormula.binary,chAnd,dodecX,
					  new TFormula(TFormula.binary,chAnd,notCube,notTet));
			  TFormula tet=new TFormula(TFormula.binary,chAnd,tetX,
					  new TFormula(TFormula.binary,chAnd,notCube,notDodec));
			  
			  
			  
			  
		TFormula	  tetOrDodec=new TFormula(TFormula.binary,chOr,tet,dodec);
		TFormula	  cubeOrDodecOrTet=new TFormula(TFormula.binary,chOr,cube,tetOrDodec);
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  cubeOrDodecOrTet);
			  return
			     allX;
	   }
	   
	   
	   private TFormula cubeSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Cube",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Cube",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   

	   private TFormula tetSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Tet",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Tet",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}
	   
	   private TFormula dodecSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Dodec",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Dodec",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   
	   
	   private TFormula smallOrMediumOrLarge(){
		   TFormula tetOrCube,tetOrCubeOrDodec;
		   TFormula tetX,cubeX,dodecX,allX=null;
			  
			  tetX=new TFormula(TFormula.predicator,"Small",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null));
			  TFormula notTet=new TFormula(TFormula.unary,chNeg,null,
					  tetX);
			  cubeX=new TFormula(TFormula.predicator,"Medium",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),null));
			  TFormula notCube=new TFormula(TFormula.unary,chNeg,null,
					  cubeX);
			  dodecX=new TFormula(TFormula.predicator,"Large",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),null));
			  TFormula notDodec=new TFormula(TFormula.unary,chNeg,null,
					  dodecX);
			  TFormula cube=new TFormula(TFormula.binary,chAnd,cubeX,
					  new TFormula(TFormula.binary,chAnd,notTet,notDodec));
			  TFormula dodec=new TFormula(TFormula.binary,chAnd,dodecX,
					  new TFormula(TFormula.binary,chAnd,notCube,notTet));
			  TFormula tet=new TFormula(TFormula.binary,chAnd,tetX,
					  new TFormula(TFormula.binary,chAnd,notCube,notDodec));
			  
			  
			  
			  
		TFormula	  tetOrDodec=new TFormula(TFormula.binary,chOr,tet,dodec);
		TFormula	  cubeOrDodecOrTet=new TFormula(TFormula.binary,chOr,cube,tetOrDodec);
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  cubeOrDodecOrTet);
			  return
			     allX;
	   }
	   
	   private TFormula smallSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Small",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Small",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}
	   
	   private TFormula mediumSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Medium",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Medium",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}
	   
	   private TFormula largeSame(){
		   TFormula cubeX,sameYZ,cubeY, allX=null;
			  
		   cubeX=new TFormula(TFormula.predicator,"Large",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),null));
			  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
			  
			   cubeY=new TFormula(TFormula.predicator,"Large",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),null));

			  TFormula and=new TFormula(TFormula.binary,chAnd,cubeX,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,cubeY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   
	   
	   private TFormula sameSize1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameSize",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"x",null,null
							  					),null)));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	   
	   private TFormula sameSize2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameSize",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   private TFormula sameSize3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameSize",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"SameSize",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	   
	   private TFormula sameShape1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameShape",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"x",null,null
							  					),null)));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	   
	   private TFormula sameShape2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameShape",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   private TFormula sameShape3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameShape",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"SameShape",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }	   

	   private TFormula sameRow1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameRow",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"x",null,null
							  					),null)));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	   
	   private TFormula sameRow2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameRow",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"SameRow",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   private TFormula sameRow3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameRow",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameRow",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"SameRow",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	   
	   private TFormula sameCol1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameCol",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"x",null,null
							  					),null)));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	   
	   private TFormula sameCol2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameCol",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   private TFormula sameCol3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"SameCol",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }

private TFormula larger1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula larger2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Larger",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula larger3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Larger",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula larger4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Smaller",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }

	   private TFormula smaller1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula smaller2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Smaller",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula smaller3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Smaller",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula smaller4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Larger",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   } 
	   
	   private TFormula smallerSame1(){
		   TFormula smallerXY,sameXZ,smallerXZ, allX=null;
		   
		   smallerXY=new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
	  sameXZ=new TFormula(TFormula.predicator,"SameSize",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));
	   smallerXZ=new TFormula(TFormula.predicator,"Smaller",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));


			  TFormula and=new TFormula(TFormula.binary,chAnd,smallerXY,sameXZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,smallerXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   

	   private TFormula smallerSame2(){
		   TFormula smallerXY,sameYZ,smallerXZ, allX=null;
		   
		   smallerXY=new TFormula(TFormula.predicator,"Smaller",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
	  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"y",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));
	   smallerXZ=new TFormula(TFormula.predicator,"Smaller",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));


			  TFormula and=new TFormula(TFormula.binary,chAnd,smallerXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,smallerXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   

	   private TFormula largerSame1(){
		   TFormula smallerXY,sameXZ,smallerXZ, allX=null;
		   
		   smallerXY=new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
	  sameXZ=new TFormula(TFormula.predicator,"SameSize",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));
	   smallerXZ=new TFormula(TFormula.predicator,"Larger",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));


			  TFormula and=new TFormula(TFormula.binary,chAnd,smallerXY,sameXZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,smallerXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}	   

	   private TFormula largerSame2(){
		   TFormula smallerXY,sameYZ,smallerXZ, allX=null;
		   
		   smallerXY=new TFormula(TFormula.predicator,"Larger",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));
	  sameYZ=new TFormula(TFormula.predicator,"SameSize",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"y",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));
	   smallerXZ=new TFormula(TFormula.predicator,"Larger",null,
	  			new TFormula(TFormula.kons,"",	
	  					new TFormula(TFormula.variable,"x",null,null
	  					),new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),null)));


			  TFormula and=new TFormula(TFormula.binary,chAnd,smallerXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,smallerXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	}
	   private TFormula leftOf1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula leftOf2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"LeftOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula leftOf3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"LeftOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula leftOf4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"RightOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   } 
	   
	   private TFormula leftSame1(){
		   TFormula frontXY,sameYZ,frontXZ, allX=null;
			  
			  frontXY=new TFormula(TFormula.predicator,"LeftOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  frontXZ=new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	}

	private TFormula leftSame2(){
		   TFormula frontXY,sameXZ,frontZY, allX=null;
			  
			  frontXY=new TFormula(TFormula.predicator,"LeftOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameXZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  frontZY=new TFormula(TFormula.predicator,"LeftOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameXZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontZY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	}	   
	   
	   
	   
	   private TFormula rightOf1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula rightOf2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"RightOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula rightOf3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"RightOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula rightOf4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"LeftOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   } 
	   
	   private TFormula rightSame1(){
		   TFormula frontXY,sameYZ,frontXZ, allX=null;
			  
			  frontXY=new TFormula(TFormula.predicator,"RightOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  frontXZ=new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	}

	private TFormula rightSame2(){
		   TFormula frontXY,sameXZ,frontZY, allX=null;
			  
			  frontXY=new TFormula(TFormula.predicator,"RightOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameXZ=new TFormula(TFormula.predicator,"SameCol",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  frontZY=new TFormula(TFormula.predicator,"RightOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"z",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"y",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameXZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontZY);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	}	   
	   
	   
	   
	   private TFormula frontOf1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"FrontOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula frontOf2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"FrontOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"FrontOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula frontOf3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"FrontOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"FrontOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"FrontOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula frontOf4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"BackOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"FrontOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   private TFormula backOf1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"BackOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula backOf2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"BackOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"BackOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   }
	   
	   
	   private TFormula backOf3(){
		   TFormula sameXY,sameYZ,sameXZ, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"BackOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYZ=new TFormula(TFormula.predicator,"BackOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));
			  
			  sameXZ=new TFormula(TFormula.predicator,"BackOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"z",null,null
					  					),null)));

			  TFormula and=new TFormula(TFormula.binary,chAnd,sameXY,sameYZ);
			  TFormula implic=new TFormula(TFormula.binary,chImplic,and,sameXZ);

			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  new TFormula(TFormula.quantifier,chUniquant,
									  new TFormula(TFormula.variable,"z",null,null),
									  implic)));
			  return
			     allX;
	   }
	
	   private TFormula backOf4(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"FrontOf",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX=new TFormula(TFormula.predicator,"BackOf",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   } 
	
	   private TFormula adjoins1(){
		   TFormula sameXY, allX=null;
			  
			  sameXY=new TFormula(TFormula.unary,chNeg,null,new TFormula(TFormula.predicator,"Adjoins",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"x",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null))));
			  
			  
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  sameXY);
			  return
			     allX;
	   }
	 
private TFormula adjoins2(){
		   TFormula sameXY,sameYX, allX=null;
			  
			  sameXY=new TFormula(TFormula.predicator,"Adjoins",null,
					  			new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),new TFormula(TFormula.kons,"",	
							  					new TFormula(TFormula.variable,"y",null,null
							  					),null)));
			  sameYX= 
					  new TFormula(TFormula.predicator,"Adjoins",null,
			  			new TFormula(TFormula.kons,"",	
			  					new TFormula(TFormula.variable,"y",null,null
			  					),new TFormula(TFormula.kons,"",	
					  					new TFormula(TFormula.variable,"x",null,null
					  					),null)));

			  TFormula implic=new TFormula(TFormula.binary,chImplic,sameXY,sameYX);
			  
			  
			  
			  allX=new TFormula(TFormula.quantifier,chUniquant,
					  new TFormula(TFormula.variable,"x",null,null),
					  new TFormula(TFormula.quantifier,chUniquant,
							  new TFormula(TFormula.variable,"y",null,null),
							  implic));
			  return
			     allX;
	   } 

private TFormula between1(){
	   TFormula betweenXYY, notBetween, allX=null;
		  
	   betweenXYY=new TFormula(TFormula.predicator,"Between",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"y",null,null
						  					),new TFormula(TFormula.kons,"",	
								  					new TFormula(TFormula.variable,"y",null,null
								  					),null))));
notBetween= new TFormula(TFormula.unary,chNeg,null,betweenXYY);


		  
		  
allX=new TFormula(TFormula.quantifier,chUniquant,
		  new TFormula(TFormula.variable,"x",null,null),
		  new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"y",null,null),
				  notBetween));
		  return
		     allX;
}

private TFormula between2(){
	   TFormula betweenXXY, notBetween, allX=null;
		  
	   betweenXXY=new TFormula(TFormula.predicator,"Between",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"x",null,null
						  					),new TFormula(TFormula.kons,"",	
								  					new TFormula(TFormula.variable,"y",null,null
								  					),null))));
notBetween= new TFormula(TFormula.unary,chNeg,null,betweenXXY);


		  
		  
allX=new TFormula(TFormula.quantifier,chUniquant,
		  new TFormula(TFormula.variable,"x",null,null),
		  new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"y",null,null),
				  notBetween));
		  return
		     allX;
}

private TFormula frontSame1(){
	   TFormula frontXY,sameYZ,frontXZ, allX=null;
		  
		  frontXY=new TFormula(TFormula.predicator,"FrontOf",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"y",null,null
						  					),null)));
		  sameYZ=new TFormula(TFormula.predicator,"SameRow",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"y",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));
		  
		  frontXZ=new TFormula(TFormula.predicator,"FrontOf",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));

		  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameYZ);
		  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontXZ);

		  
		  
		  allX=new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"x",null,null),
				  new TFormula(TFormula.quantifier,chUniquant,
						  new TFormula(TFormula.variable,"y",null,null),
						  new TFormula(TFormula.quantifier,chUniquant,
								  new TFormula(TFormula.variable,"z",null,null),
								  implic)));
		  return
		     allX;
}

private TFormula frontSame2(){
	   TFormula frontXY,sameXZ,frontZY, allX=null;
		  
		  frontXY=new TFormula(TFormula.predicator,"FrontOf",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"y",null,null
						  					),null)));
		  sameXZ=new TFormula(TFormula.predicator,"SameRow",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));
		  
		  frontZY=new TFormula(TFormula.predicator,"FrontOf",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"z",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"y",null,null
				  					),null)));

		  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameXZ);
		  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontZY);

		  
		  
		  allX=new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"x",null,null),
				  new TFormula(TFormula.quantifier,chUniquant,
						  new TFormula(TFormula.variable,"y",null,null),
						  new TFormula(TFormula.quantifier,chUniquant,
								  new TFormula(TFormula.variable,"z",null,null),
								  implic)));
		  return
		     allX;
}

private TFormula backSame1(){
	   TFormula frontXY,sameYZ,frontXZ, allX=null;
		  
		  frontXY=new TFormula(TFormula.predicator,"BackOf",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"y",null,null
						  					),null)));
		  sameYZ=new TFormula(TFormula.predicator,"SameRow",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"y",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));
		  
		  frontXZ=new TFormula(TFormula.predicator,"BackOf",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));

		  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameYZ);
		  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontXZ);

		  
		  
		  allX=new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"x",null,null),
				  new TFormula(TFormula.quantifier,chUniquant,
						  new TFormula(TFormula.variable,"y",null,null),
						  new TFormula(TFormula.quantifier,chUniquant,
								  new TFormula(TFormula.variable,"z",null,null),
								  implic)));
		  return
		     allX;
}

private TFormula backSame2(){
	   TFormula frontXY,sameXZ,frontZY, allX=null;
		  
		  frontXY=new TFormula(TFormula.predicator,"BackOf",null,
				  			new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"x",null,null
				  					),new TFormula(TFormula.kons,"",	
						  					new TFormula(TFormula.variable,"y",null,null
						  					),null)));
		  sameXZ=new TFormula(TFormula.predicator,"SameRow",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"x",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"z",null,null
				  					),null)));
		  
		  frontZY=new TFormula(TFormula.predicator,"BackOf",null,
		  			new TFormula(TFormula.kons,"",	
		  					new TFormula(TFormula.variable,"z",null,null
		  					),new TFormula(TFormula.kons,"",	
				  					new TFormula(TFormula.variable,"y",null,null
				  					),null)));

		  TFormula and=new TFormula(TFormula.binary,chAnd,frontXY,sameXZ);
		  TFormula implic=new TFormula(TFormula.binary,chImplic,and,frontZY);

		  
		  
		  allX=new TFormula(TFormula.quantifier,chUniquant,
				  new TFormula(TFormula.variable,"x",null,null),
				  new TFormula(TFormula.quantifier,chUniquant,
						  new TFormula(TFormula.variable,"y",null,null),
						  new TFormula(TFormula.quantifier,chUniquant,
								  new TFormula(TFormula.variable,"z",null,null),
								  implic)));
		  return
		     allX;
}

private void createSemantics(TFormula target){
	  
	  //we need add only axioms relevant to target
	  
	  int type=identifyInference(target);
	  
	  switch (type)
	  {
	  case 1:  if (fSemantics.add(cubeOrDodecOrTet())) ;
	           break;
	           
	  case 2:  if (fSemantics.add(smallOrMediumOrLarge())) ;
    			break;
    
	  case 3:  	if (fSemantics.add(sameSize1())) ;
	  			if (fSemantics.add(sameSize2())) ;
	  			if (fSemantics.add(sameSize3())) ;
	  			break;
	  			
	  case 4:  	if (fSemantics.add(sameShape1())) ;
		if (fSemantics.add(sameShape2())) ;
		if (fSemantics.add(sameShape3())) ;
		break;

	  case 5:  	if (fSemantics.add(sameCol1())) ;
		if (fSemantics.add(sameCol2())) ;
		if (fSemantics.add(sameCol3())) ;
		break;

	  case 6:  	if (fSemantics.add(sameRow1())) ;
		if (fSemantics.add(sameRow2())) ;
		if (fSemantics.add(sameRow3())) ;	  
	  break;

	  case 7:  	if (fSemantics.add(larger1())) ;
				if (fSemantics.add(larger2())) ;
				if (fSemantics.add(larger3())) ;  
				if (fSemantics.add(larger4())) ;  
	  break;
	  case 8:  	if (fSemantics.add(smaller1())) ;
		if (fSemantics.add(smaller2())) ;
		if (fSemantics.add(smaller3())) ;  
		if (fSemantics.add(smaller4())) ;  
		break;
	  case 9:  	if (fSemantics.add(leftOf1())) ;
		if (fSemantics.add(leftOf2())) ;
		if (fSemantics.add(leftOf3())) ;  
		if (fSemantics.add(leftOf4())) ;  
		break;
	  case 10:  	if (fSemantics.add(rightOf1())) ;
		if (fSemantics.add(rightOf2())) ;
		if (fSemantics.add(rightOf3())) ;  
		if (fSemantics.add(rightOf4())) ;  
		break;
	  
	  case 11:  	if (fSemantics.add(frontOf1())) ;
		if (fSemantics.add(frontOf2())) ;
		if (fSemantics.add(frontOf3())) ;  
		if (fSemantics.add(frontOf4())) ;  
		break;
	  case 12:  	
		if (fFormula2==null)            // one selected
			{
			if (fSemantics.add(backOf1())) ;
			if (fSemantics.add(backOf2())) ; 
			if (fSemantics.add(backOf4())) ; 
			}
		else
			if (fSemantics.add(backOf3())) ;  // two selected
		break;
	  case 13:  	if (fSemantics.add(adjoins1())) ;
		if (fSemantics.add(adjoins2())) ;

		break;
	  case 14:  	if (fSemantics.add(between1())) ;
		if (fSemantics.add(between2())) ;

		break;
		
	  case 101:  	if (fSemantics.add(cubeSame())) ;
	  if (fSemantics.add(tetSame())) ;
	  if (fSemantics.add(dodecSame())) ;
	  	break;

	  case 102:  	if (fSemantics.add(smallSame())) ;
	  if (fSemantics.add(mediumSame())) ;
	  if (fSemantics.add(largeSame())) ;
	  	break;
	  	
	  case 107:  	if (fSemantics.add(largerSame1())) ;
	  if (fSemantics.add(largerSame2())) ;
	  	break;
	  case 108:  	if (fSemantics.add(smallerSame1())) ;
	  if (fSemantics.add(smallerSame2())) ;
	  	break;
	  case 109:  	if (fSemantics.add(leftSame1())) ;
	  if (fSemantics.add(leftSame2())) ;
	  	break;
	  case 110:  	if (fSemantics.add(rightSame1())) ;
	  if (fSemantics.add(rightSame2())) ;
	  	break;

	  case 111:  	if (fSemantics.add(frontSame1())) ;
		if (fSemantics.add(frontSame2())) ;

		break;

	  case 112:  	if (fSemantics.add(backSame1())) ;
		if (fSemantics.add(backSame2())) ;

		break;

	  
	  }
	  
	 
	  
	/*  
	  
	  if (fSemantics.add(cubeOrDodecOrTet())) ;
	  if (fSemantics.add(smallOrMediumOrLarge())) ; */
	  
	//  if (fSemantics.add(sameSize1())) ;
	//  if (fSemantics.add(sameSize2())) ;
//	  if (fSemantics.add(sameSize3())) ;
	  
  }

private int identifyInference(TFormula target){
	 if (target!=null){
		 String label=target.fInfo;
		 String premise1="";
		 String premise2="";
		 
		 if (fFormula!=null)
			 premise1=fFormula.fInfo;
		 if (fFormula2!=null)
			 premise2=fFormula2.fInfo;
		 
		 
		 if (TParser.isNegation(target)&&target.fRLink!=null)
			 label=target.fRLink.fInfo;
		 
		if (label.equals("Cube")||label.equals("Tet")||label.equals("Dodec")) {
			if (premise1.equals("SameShape")||
					premise2.equals("SameShape"))
				return
					101;

			return 1;}
		
		if (label.equals("Small")||label.equals("Medium")||label.equals("Large")) {
			if (premise1.equals("SameSize")||
					premise2.equals("SameSize"))
				return
					102;
			
			
			
			return 2;	}	
		
		if (label.equals("SameSize")) 
			return 3;
		 
		if (label.equals("SameShape")) 
			return 4;
		if (label.equals("SameCol")) 
			return 5;
		if (label.equals("SameRow")) 
			return 6;
		if (label.equals("Larger")){ 
			if (premise1.equals("SameSize")||
					premise2.equals("SameSize"))
				return
					107;
			return 7;}
		if (label.equals("Smaller")){ 
			if (premise1.equals("SameSize")||
					premise2.equals("SameSize"))
				return
					108;
			return 8;}
		if (label.equals("LeftOf")){ 
			if (premise1.equals("SameCol")||
					premise2.equals("SameCol"))
				return
					109;

			return 9;}
		if (label.equals("RightOf")){ 
			if (premise1.equals("SameCol")||
					premise2.equals("SameCol"))
				return
					110;

			return 10;}
		if (label.equals("FrontOf")){
			if (premise1.equals("SameRow")||
					premise2.equals("SameRow"))
				return
					111;
			
			
			return 11;}
		if (label.equals("BackOf")) {
			if (premise1.equals("SameRow")||
					premise2.equals("SameRow"))
				return
					112;

			
			
			return 12;}
		if (label.equals("Adjoins")) 
			return 13;
		if (label.equals("Between")) 
			return 14;
	 }
	 
	 
	 
return
  0;
}

int treeValid(TGWTTestNode aTestRoot,TGWTTree aTreeModel, int maxDepth, int timeout) throws Exception{
	int isValid;
	
	  Timer t = new Timer() {
	      @Override
	      public void run() {
	    	  throw new IllegalArgumentException("Timeout");
	      }
	    };
	    
	    t.schedule(timeout * 1000); // timeout is in milliseconds
		
	
	isValid=aTestRoot.treeValid(aTreeModel,maxDepth);
	
	t.cancel();
	t=null;
		
	return
			isValid;		
};

//void treeValid(TGWTTestNode aTestRoot,TGWTTree aTreeModel,AsyncCallback<int> callback);

private  boolean testValidity(TFormula target){
final int valid = 1;
final int notValid = 2;
final int notKnown = -1;
int answer=-2;

TGWTTestNode aTestRoot = new TGWTTestNode(fParser,null);  //does not initialize TreeModel
// notice not using factory here because static

TGWTTree aTreeModel= new TGWTTree();       //new TTreeModel(aTestRoot.fSwingTreeNode);

//pre June 2012   aTestRoot.fGWTTree=aTreeModel;
aTestRoot.fGWTTree=aTreeModel;


if (fFormula!=null)
	aTestRoot.addToAntecedents(fFormula);  //adding both selected formulas
if (fFormula2!=null)
	aTestRoot.addToAntecedents(fFormula2);

fSemantics= new ArrayList();  //start afresh
createSemantics(target);
aTestRoot.fAntecedents.addAll(fSemantics);


aTestRoot.fSuccedent.add(target);

//answer= aTestRoot.treeValid(aTreeModel,TGWTTestNode.kMaxTreeDepth);

//NEED MF TIMEOUT LOGIC HERE

int TIMEOUT=15;
try{
	answer= treeValid(aTestRoot,aTreeModel,TGWTTestNode.kMaxTreeDepth,TIMEOUT);
	
} catch(Exception e)  //timeout
{
	answer=notKnown;
}

	 
if (answer==valid)
return
	true;
else
return
	false;

}
}
}



/*

int i;

i=1;

final TTestNode aTestRoot = new TTestNode(fParser,null);  //does not initialize TreeModel
// notice not using factory here because static

final TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);

aTestRoot.fTreeModel=aTreeModel;

/*   aTestRoot.fAntecedents=antecedents.toArrayList();
*  now just taking both selected formulas
*/

/*
aTestRoot.fAntecedents= new ArrayList();
if(fFormula!=null)
  aTestRoot.fAntecedents.add(fFormula);
if(fFormula2!=null)
  aTestRoot.fAntecedents.add(fFormula2);

fSemantics= new ArrayList();  //start afresh
createSemantics(target);
aTestRoot.fAntecedents.addAll(fSemantics);


aTestRoot.fSuccedent.add(target);



class ValidTask implements Callable <Integer>{	   
  public Integer call(){
	   
	  int answer= aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);
	   return
	   new Integer(answer);
  }	   
}
class TimeOutTask implements Callable <Integer>{	   
  public Integer call(){
	   try{
	   Thread.sleep(5000);  //5 seconds
	   }
		 catch (Exception e){}
	   return
	   1;
  }	   
}  


ExecutorService exec =Executors.newCachedThreadPool();


ValidTask validTask=new ValidTask(); 
Future validFuture=exec.submit(validTask);

TimeOutTask aTask2=new TimeOutTask(); 
Future aFuture2=exec.submit(aTask2);


while (!validFuture.isDone()&&!aFuture2.isDone()){ } //run to completion or time out


{if (aFuture2.isDone()){
	 
	 Integer value= new Integer(-2);
	 
	 try{
	 value=(Integer)(aFuture2.get());
	 }
	 catch (Exception e){}
	 
	// System.out.format("Timed Out");
	 
	 answer=value.intValue();
	 }

if (validFuture.isDone()){

Integer value= new Integer(-2);

try{
value=(Integer)(validFuture.get());
}
catch (Exception e){}

// System.out.format("Valid Test Completed");

answer=value.intValue();
}
}
	        

//  answer=    aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);


if (answer==valid)
return
	true;
else
return
	false;
}



	

}





}



/*
 * 	void doAnaCon(TTreeDataNode selected,TTreeDataNode selected2,TFormula theFormula,
			TFormula theFormula2){
		   JButton defaultButton;
		   TProofInputPanel inputPane;

		   JTextField text = new JTextField("Target? (atomic formula or negation of atomic formula) Use "+ chNotSign + " or ~ for negation.");
		   text.selectAll();

		   defaultButton = new JButton(new AnaConAction(text,"Go", selected,selected2, theFormula,theFormula2));

		   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
		   inputPane = new TProofInputPanel("Doing Ana Con", text, buttons);


		   addInputPane(inputPane);
		   inputPane.getRootPane().setDefaultButton(defaultButton);
		   fInputPane.setVisible(true); // need this
		   text.requestFocus();         // so selected text shows
		};
 * */





/*
	  
	  
	  

public TBarwiseTreePanel() {	 

	  }	  
	  
public TBarwiseTreePanel(TDeriverDocument itsDeriverDocument) {	
	super(itsDeriverDocument);
	
	addAnaConMenuItem();

}
	  
	
private void addAnaConMenuItem(){

		    if (true||
		            fUseIdentity){
		     // fActionsMenu.addSeparator();
		    	

		      fActionsMenu.add(anaConMenuItem,1); // 2nd item down

		      anaConMenuItem.setText("Ana Con");
		      anaConMenuItem.addActionListener(new
		    		  TBarwiseTreePanel_anaConMenuItem_actionAdapter(this));
		    }

		
	}
	
	void anaConMenuItem_actionPerformed(ActionEvent e) {
		  TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();

		  TTreeDataNode firstSelected=null;
		  TTreeDataNode secondSelected=null;
		  TFormula firstFormula = null;
		   TFormula secondFormula = null;
		  
		  if ((selectedNodes!=null)&&(selectedNodes.length==1))
		{
		  firstSelected = selectedNodes[0];
		  if ((firstSelected.fAntecedents != null &&
			      firstSelected.fAntecedents.size() == 1)) {
			    firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
		  
		}
		}
		
		if ((selectedNodes!=null)&&(selectedNodes.length==2))
		{
		  firstSelected = selectedNodes[0];
		  secondSelected = selectedNodes[1];
		  if ((firstSelected.fAntecedents != null &&
			      firstSelected.fAntecedents.size() == 1)&&
			     (secondSelected.fAntecedents != null &&
			      secondSelected.fAntecedents.size() == 1)) {
			    firstFormula = (TFormula) (firstSelected.fAntecedents.get(0));
			    secondFormula = (TFormula) (secondSelected.fAntecedents.get(0));


		}
		}
		  
		    doAnaCon(firstSelected,secondSelected,firstFormula,secondFormula);
		  
		  }	
	
	 void doSetUpActionsMenu(){
		 
		    TTreeDataNode [] selectedNodes= fTreeTableView.selectedDataNodes();


		    if ((selectedNodes!=null)&&
		    		((selectedNodes.length==1)||(selectedNodes.length==2))){
		        anaConMenuItem.setEnabled(true);
		      }
		       else{
		         anaConMenuItem.setEnabled(false);
		      }
		    
		    super.doSetUpActionsMenu();
	 }
	
	
	/************************ Ana Con Action****************
	 
	

	public class AnaConAction extends AbstractAction{
	   JTextComponent fText;
	   TTreeDataNode fSelected=null;
	   TTreeDataNode fSelected2=null;
	   TFormula fFormula=null;
	   TFormula fFormula2=null;
	   
	   
	   ArrayList <TFormula> fSemantics= new ArrayList();
	   

	   public AnaConAction(JTextComponent text, String label, 
			   TTreeDataNode selected, TTreeDataNode selected2,
			   TFormula formula,TFormula formula2){
	     putValue(NAME, label);

	     fText=text;
	     fSelected=selected;
	     fSelected2=selected2;
	     fFormula=formula;
	     fFormula2=formula2;
	     
	   }
	   



	  private void createSemantics(TFormula target){
		  
		  //we need add only axioms relevant to target
		  
		  int type=identifyInference(target);
		  
		  switch (type)
		  {
		  case 1:  if (fSemantics.add(cubeOrDodecOrTet())) ;
		           break;
		           
		  case 2:  if (fSemantics.add(smallOrMediumOrLarge())) ;
          			break;
          
		  case 3:  	if (fSemantics.add(sameSize1())) ;
		  			if (fSemantics.add(sameSize2())) ;
		  			if (fSemantics.add(sameSize3())) ;
		  			break;
		  			
		  case 4:  	if (fSemantics.add(sameShape1())) ;
			if (fSemantics.add(sameShape2())) ;
			if (fSemantics.add(sameShape3())) ;
			break;

		  case 5:  	if (fSemantics.add(sameCol1())) ;
			if (fSemantics.add(sameCol2())) ;
			if (fSemantics.add(sameCol3())) ;
			break;

		  case 6:  	if (fSemantics.add(sameRow1())) ;
			if (fSemantics.add(sameRow2())) ;
			if (fSemantics.add(sameRow3())) ;	  
		  break;
 
		  case 7:  	if (fSemantics.add(larger1())) ;
					if (fSemantics.add(larger2())) ;
					if (fSemantics.add(larger3())) ;  
					if (fSemantics.add(larger4())) ;  
		  break;
		  case 8:  	if (fSemantics.add(smaller1())) ;
			if (fSemantics.add(smaller2())) ;
			if (fSemantics.add(smaller3())) ;  
			if (fSemantics.add(smaller4())) ;  
			break;
		  case 9:  	if (fSemantics.add(leftOf1())) ;
			if (fSemantics.add(leftOf2())) ;
			if (fSemantics.add(leftOf3())) ;  
			if (fSemantics.add(leftOf4())) ;  
			break;
		  case 10:  	if (fSemantics.add(rightOf1())) ;
			if (fSemantics.add(rightOf2())) ;
			if (fSemantics.add(rightOf3())) ;  
			if (fSemantics.add(rightOf4())) ;  
			break;
		  
		  case 11:  	if (fSemantics.add(frontOf1())) ;
			if (fSemantics.add(frontOf2())) ;
			if (fSemantics.add(frontOf3())) ;  
			if (fSemantics.add(frontOf4())) ;  
			break;
		  case 12:  	
			if (fFormula2==null)            // one selected
				{
				if (fSemantics.add(backOf1())) ;
				if (fSemantics.add(backOf2())) ; 
				if (fSemantics.add(backOf4())) ; 
				}
			else
				if (fSemantics.add(backOf3())) ;  // two selected
			break;
		  case 13:  	if (fSemantics.add(adjoins1())) ;
			if (fSemantics.add(adjoins2())) ;

			break;
		  case 14:  	if (fSemantics.add(between1())) ;
			if (fSemantics.add(between2())) ;

			break;
			
		  case 101:  	if (fSemantics.add(cubeSame())) ;
		  if (fSemantics.add(tetSame())) ;
		  if (fSemantics.add(dodecSame())) ;
		  	break;

		  case 102:  	if (fSemantics.add(smallSame())) ;
		  if (fSemantics.add(mediumSame())) ;
		  if (fSemantics.add(largeSame())) ;
		  	break;
		  	
		  case 107:  	if (fSemantics.add(largerSame1())) ;
		  if (fSemantics.add(largerSame2())) ;
		  	break;
		  case 108:  	if (fSemantics.add(smallerSame1())) ;
		  if (fSemantics.add(smallerSame2())) ;
		  	break;
		  case 109:  	if (fSemantics.add(leftSame1())) ;
		  if (fSemantics.add(leftSame2())) ;
		  	break;
		  case 110:  	if (fSemantics.add(rightSame1())) ;
		  if (fSemantics.add(rightSame2())) ;
		  	break;

		  case 111:  	if (fSemantics.add(frontSame1())) ;
			if (fSemantics.add(frontSame2())) ;

			break;

		  case 112:  	if (fSemantics.add(backSame1())) ;
			if (fSemantics.add(backSame2())) ;

			break;

		  
		  }
		  
		 
		  
		/*  
		  
		  if (fSemantics.add(cubeOrDodecOrTet())) ;
		  if (fSemantics.add(smallOrMediumOrLarge())) ; */
		  
		//  if (fSemantics.add(sameSize1())) ;
		//  if (fSemantics.add(sameSize2())) ;
	//	  if (fSemantics.add(sameSize3())) ;
		  
	/*  }
	   
	 
	 private int identifyInference(TFormula target){
		 if (target!=null){
			 String label=target.fInfo;
			 String premise1="";
			 String premise2="";
			 
			 if (fFormula!=null)
				 premise1=fFormula.fInfo;
			 if (fFormula2!=null)
				 premise2=fFormula2.fInfo;
			 
			 
			 if (TParser.isNegation(target)&&target.fRLink!=null)
				 label=target.fRLink.fInfo;
			 
			if (label.equals("Cube")||label.equals("Tet")||label.equals("Dodec")) {
				if (premise1.equals("SameShape")||
						premise2.equals("SameShape"))
					return
						101;

				return 1;}
			
			if (label.equals("Small")||label.equals("Medium")||label.equals("Large")) {
				if (premise1.equals("SameSize")||
						premise2.equals("SameSize"))
					return
						102;
				
				
				
				return 2;	}	
			
			if (label.equals("SameSize")) 
				return 3;
			 
			if (label.equals("SameShape")) 
				return 4;
			if (label.equals("SameCol")) 
				return 5;
			if (label.equals("SameRow")) 
				return 6;
			if (label.equals("Larger")){ 
				if (premise1.equals("SameSize")||
						premise2.equals("SameSize"))
					return
						107;
				return 7;}
			if (label.equals("Smaller")){ 
				if (premise1.equals("SameSize")||
						premise2.equals("SameSize"))
					return
						108;
				return 8;}
			if (label.equals("LeftOf")){ 
				if (premise1.equals("SameCol")||
						premise2.equals("SameCol"))
					return
						109;
	
				return 9;}
			if (label.equals("RightOf")){ 
				if (premise1.equals("SameCol")||
						premise2.equals("SameCol"))
					return
						110;

				return 10;}
			if (label.equals("FrontOf")){
				if (premise1.equals("SameRow")||
						premise2.equals("SameRow"))
					return
						111;
				
				
				return 11;}
			if (label.equals("BackOf")) {
				if (premise1.equals("SameRow")||
						premise2.equals("SameRow"))
					return
						112;

				
				
				return 12;}
			if (label.equals("Adjoins")) 
				return 13;
			if (label.equals("Between")) 
				return 14;
		 }
		 
		 
		 
	return
	   0;
	 }
	 

	 
 

	 
	 
	 
	 
/*	 
	 
	 
	   
	 private  boolean testValidity(TFormula target){
	final int valid = 1;
	final int notValid = 2;
	final int notKnown = -1;
    int answer=-2;
		 
  TreeNode[] branch = fSelected.fTreeNode.getPath();
  TFormula antecedents=fSelected.atomicOrNegatomicFormulasInBranch(branch);
	
	int i;
	
	i=1;
	
	final TTestNode aTestRoot = new TTestNode(fParser,null);  //does not initialize TreeModel
    // notice not using factory here because static

   final TTreeModel aTreeModel= new TTreeModel(aTestRoot.fTreeNode);

   aTestRoot.fTreeModel=aTreeModel;

/*   aTestRoot.fAntecedents=antecedents.toArrayList();
 *  now just taking both selected formulas
 */

/*
   aTestRoot.fAntecedents= new ArrayList();
   if(fFormula!=null)
	   aTestRoot.fAntecedents.add(fFormula);
   if(fFormula2!=null)
	   aTestRoot.fAntecedents.add(fFormula2);
   
   fSemantics= new ArrayList();  //start afresh
   createSemantics(target);
   aTestRoot.fAntecedents.addAll(fSemantics);
  
   
   aTestRoot.fSuccedent.add(target);


   
   class ValidTask implements Callable <Integer>{	   
	   public Integer call(){
		   
		  int answer= aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);
		   return
		   new Integer(answer);
	   }	   
   }
   class TimeOutTask implements Callable <Integer>{	   
	   public Integer call(){
		   try{
		   Thread.sleep(5000);  //5 seconds
		   }
			 catch (Exception e){}
		   return
		   1;
	   }	   
   }  
  
   
   ExecutorService exec =Executors.newCachedThreadPool();
   
   
   ValidTask validTask=new ValidTask(); 
   Future validFuture=exec.submit(validTask);
   
   TimeOutTask aTask2=new TimeOutTask(); 
   Future aFuture2=exec.submit(aTask2);

   
 while (!validFuture.isDone()&&!aFuture2.isDone()){ } //run to completion or time out
	
 
 {if (aFuture2.isDone()){
		 
		 Integer value= new Integer(-2);
		 
		 try{
		 value=(Integer)(aFuture2.get());
		 }
		 catch (Exception e){}
		 
		// System.out.format("Timed Out");
		 
		 answer=value.intValue();
		 }

	 if (validFuture.isDone()){
	 
	 Integer value= new Integer(-2);
	 
	 try{
	 value=(Integer)(validFuture.get());
	 }
	 catch (Exception e){}
	 
	// System.out.format("Valid Test Completed");
	 
	 answer=value.intValue();
	 }
 }
		        

 //  answer=    aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);


if (answer==valid)
	return
		true;
else
	return
		false;
}

	   public void actionPerformed(ActionEvent ae){

	     //boolean useFilter = true;
	     //ArrayList dummy = new ArrayList();

	     String aString = TSwingUtilities.readTextToString(fText, TUtilities.defaultFilter);

	     TFormula target = new TFormula();
	     StringReader aReader = new StringReader(aString);
	     boolean wellformed=false;

	  //   wellformed=fParser.term(term,aReader);
	     
	     wellformed=fParser.wffCheck(target, aReader);

	     if ((!wellformed)||
	    	  !(TParser.isAtomic(target)||(TParser.isNegation(target)&&TParser.isAtomic(target.fRLink))) 
	    	) {
	       String message = "The string is neither an atomic formula nor the negation of an atomic formula." +
	                            (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""); //filter out returns

	                        //      "'The string is illformed.', RemoveReturns(gParserErrorMessage))";

	                        fText.setText(message);
	                        fText.selectAll();
	                        fText.requestFocus();
	                      }

	                      else {   // we're good
	                    	  
	                        {
	                        	
	                        if (!testValidity(target)){
	                 	       String message = "We have not been able to find a proof of your formula.";

	                        fText.setText(message);
	                        fText.selectAll();
	                        fText.requestFocus();
	                      }
	                        else{

	                          TTreeDataNode newDataNode = new TTreeDataNode(fParser,fTreeModel);
	                          newDataNode.fAntecedents.add(0,target /*scope*//* );
	                          newDataNode.fFirstjustno=fSelected.fLineno;
	                          
	                          if (fSelected2!=null)
	                        	  newDataNode.fSecondjustno=fSelected2.fLineno;
	                          
	                          
	                          newDataNode.fJustification= anaConJustification;
	                          newDataNode.fWorld= fSelected.fWorld;

	                         // selected.fDead=true;    don't make it dead

	                //          fSelected.addToInstantiations(term.copyFormula());

	                          straightInsert(fSelected,newDataNode,null);

	                          removeInputPane();
	                        }
	                        }

	                      }

	                  }

	                }


	              /************************ End of AnaCon Action *********/

	/*********************** Ana Con for Barwise ***************************

	void doAnaCon(TTreeDataNode selected,TTreeDataNode selected2,TFormula theFormula,
			TFormula theFormula2){
		   JButton defaultButton;
		   TProofInputPanel inputPane;

		   JTextField text = new JTextField("Target? (atomic formula or negation of atomic formula) Use "+ chNotSign + " or ~ for negation.");
		   text.selectAll();

		   defaultButton = new JButton(new AnaConAction(text,"Go", selected,selected2, theFormula,theFormula2));

		   JButton[]buttons = {new JButton(new CancelAction()), defaultButton };  // put cancel on left
		   inputPane = new TProofInputPanel("Doing Ana Con", text, buttons);


		   addInputPane(inputPane);
		   inputPane.getRootPane().setDefaultButton(defaultButton);
		   fInputPane.setVisible(true); // need this
		   text.requestFocus();         // so selected text shows
		};
	
	
/************
		
	     class TBarwiseTreePanel_anaConMenuItem_actionAdapter implements java.awt.event.ActionListener {
	    	 TBarwiseTreePanel adaptee;

	    	  TBarwiseTreePanel_anaConMenuItem_actionAdapter(TBarwiseTreePanel adaptee) {
	    	    this.adaptee = adaptee;
	    	  }
	    	  public void actionPerformed(ActionEvent e) {
	    	    adaptee.anaConMenuItem_actionPerformed(e);
	    	  }
	    	} 
	
}

*/