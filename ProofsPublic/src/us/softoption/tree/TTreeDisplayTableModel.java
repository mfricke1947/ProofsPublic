package us.softoption.tree;






/*

The data structure is a tree, which we are going to display in the standard 'inverted tree' form.

We need to map the tree to the table. The levels in the tree are just going to be the rows in the table.
But then, for any particular level or row, it is going to go <blank,data,blank, data etc blank> or
 <blank,blank,blank,data,blank, data etc blank,blank,blank> ie the data fills from the middle


About the tree structure....  the Nodes in the tree are TGWTTestNodes

*/

public class TTreeDisplayTableModel /*extends AbstractTableModel*/{

int fColumnCount=0;
int fRowCount=0;
int fLeaves=0;

public Object [][] fData=new Object[1][1] ;  //mf appril 12 public for testing

TGWTTestNode fHostRoot=new TGWTTestNode();  //dummy
TGWTTestNode fRoot=null;  // this is the data going with the fHostRoot
TTreeDisplayCellTable fDisplay=null;

//TTreeModel fTreeModel=null; FEB08        // like DefaultTreeModel, but with context for variables




  public TTreeDisplayTableModel(){

/* OLD TEST DATA

       TGWTTestNode leaf1=new TGWTTestNode("Leaf1");
 TGWTTestNode leaf2=new TGWTTestNode("LeftDiag");
 TGWTTestNode leaf3=new TGWTTestNode("RightDiag");
 TGWTTestNode leaf4=new TGWTTestNode("Leaf4");
  TGWTTestNode leaf5=new TGWTTestNode("Leaf5");
  TGWTTestNode leaf6=new TGWTTestNode("Leaf6");
  TGWTTestNode leaf7=new TGWTTestNode("Leaf7");

 leaf1.add(leaf2);
 leaf1.add(leaf3);
  leaf3.add(leaf4);
  leaf4.add(leaf5);

  leaf2.add(leaf6);
  leaf6.add(leaf7); */

  //  fHostRoot.add(leaf1);


 updateCache();  // need this FEB08

  }
  
  public TTreeDisplayTableModel(TTreeDisplayCellTable itsDisplay){
	fDisplay=itsDisplay;

 updateCache(); 

  }
  
  public void setDisplay(TTreeDisplayCellTable itsDisplay){
		fDisplay=itsDisplay;
  }
  
  public TTreeDisplayCellTable getDisplay(){
		return
				fDisplay;
}
  
  public void tempTest(){
	  TGWTTestNode leaf1=new TGWTTestNode("Leaf1");
	  TGWTTestNode leaf2=new TGWTTestNode("LeftDiag");
	  TGWTTestNode leaf3=new TGWTTestNode("RightDiag");
	  TGWTTestNode leaf4=new TGWTTestNode("Leaf4");
	   TGWTTestNode leaf5=new TGWTTestNode("Leaf5");
	   TGWTTestNode leaf6=new TGWTTestNode("Leaf6");
	   TGWTTestNode leaf7=new TGWTTestNode("Leaf7");

	  leaf1.addItem(leaf2);leaf2.addItem(leaf6); leaf6.addItem(leaf7); 
	  leaf1.addItem(leaf3);leaf3.addItem(leaf4);leaf4.addItem(leaf5);

	   
	  

	  fHostRoot.addItem(leaf1);
	  
      updateCache();
	  
  }
  
public void changeTestData(){
	  
	  TGWTTestNode leaf1=new TGWTTestNode("LeafA");
	  TGWTTestNode leaf2=new TGWTTestNode("LeftDiag");
	  TGWTTestNode leaf3=new TGWTTestNode("RightDiag");
	  TGWTTestNode leaf4=new TGWTTestNode("LeafB");
	   TGWTTestNode leaf5=new TGWTTestNode("LeafC");
	   TGWTTestNode leaf6=new TGWTTestNode("LeafD");
	   TGWTTestNode leaf7=new TGWTTestNode("LeafE");
	   TGWTTestNode leaf8=new TGWTTestNode("LeafF");

	  leaf1.addItem(leaf2);leaf2.addItem(leaf6); leaf6.addItem(leaf7);leaf7.addItem(leaf8);  
	  leaf1.addItem(leaf3);leaf3.addItem(leaf4);leaf4.addItem(leaf5);
	  
	  fHostRoot=new TGWTTestNode();
	  

	  fHostRoot.addItem(leaf1);
	  
	  updateCache();
	  
	  
  }

  public TTreeDisplayTableModel(/*TTreeModel aModel,*/ TGWTTestNode aRoot){
 //   fTreeModel=aModel;
    fRoot=aRoot;

 //   fHostRoot=fRoot.fSwingTreeNode;

    updateCache();

  }

/*  public void setHost(JTable host){
     fHostTable=host;

  } */


 /************* getters and setters for Beans and File IO ******************/

 public TGWTTestNode getHostRoot(){
   return
       fHostRoot;
 }

public void setHostRoot(TGWTTestNode aRoot){
      fHostRoot=aRoot;
}

public TGWTTestNode getRoot(){
      return
          fRoot;
    }

public void setRoot(TGWTTestNode aRoot){
         fRoot=aRoot;
}

 /************* needed override/implementation methods ******************/



public int getColumnCount(){             /*branches*2 +1 */
   return
       fColumnCount;

/*  int branches = fHostRoot.getLeafCount();   //should cache
  return

      (branches*2)+1;*/
}

public int getColumnMaxWidth(int index){  //returns maxwidth in number of characters
	int max=0;
	TGWTTestNode value;
	String valueStr;
	
	if (index>=0&&index<fColumnCount){
		for(int i=0;i<fRowCount;i++){
			if (isTestNode(i,index)){
				value =(TGWTTestNode)getValueAt(i,index);
				
				valueStr=value.toString();
				
				if (valueStr.length()>max)
					max=valueStr.length();
				
				
		 		  if ( ((TGWTTestNode)value).fDead)
	    			  max+=1; //squareRoot;        // tick the dead ones	
			
			}
		}
	}
	

	return
			max;
}

public int getRowCount(){
  return
      fRowCount;
/*  return
      fHostRoot.getDepth()+1; */
}


/*

We need to map the tree to the table. The levels in the tree are just going to be the rows in the table.
But then, for any particular level or row, it is going to go <blank,data,blank, data etc blank> or
 <blank,blank,blank,data,blank, data etc blank,blank,blank> ie the data fills from the middle


*/
public Object getValueAt(int row, int column){

  if ((row<0||row>fRowCount-1)||
      (column<0||column>fColumnCount-1))
    return
        null;
  else
  return
      fData[row][column];
}

public boolean isTestNode(int row, int column){
	return
			!isSelectable(row,column);
	
}


public boolean isSelectable(int row, int column){

  /*The label nodes (blanks, vertical lines, etc. are not selectable */
	
	TGWTTestNode value =(TGWTTestNode)getValueAt(row,column);

    return
        !value.fLabelOnly;
}

/************* End of needed override/implementation methods ******************/



/***************   Data cache ************************************/

/*I'm struggling with the placement algorithm.  Here are some requirements

 a) every leaf in its own colum.
 b) small number of colums
 c) roughly central and symmetrical


 Try by merging subtrees from leaves and recursing. What I need to know is an array of the
 subtree and column the root is in.

 */

class PlacementData {
  Object [][] fData=new Object[1][1];
  int fRootIndex=0;
}


PlacementData join(TGWTTestNode joinNode,PlacementData subtree1,PlacementData subtree2){
 
/* we have here two subtrees, each with their own line numbers and justification
 * we want to merge them */	
	
	
	
	if (subtree1==null)
    return
        subtree2;
  if (subtree2==null)
   return
       subtree1;

  PlacementData output = new PlacementData();

  /*side by side subtrees */

  int subtree1Cols=subtree1.fData[0].length;
  int subtree2Cols=subtree2.fData[0].length;

  int cols = subtree1Cols +
             subtree2Cols +
             1                            //extra column for join, and join node
             -2;                          // but need only one lineNo col and justCol
  int rows=subtree1.fData.length>subtree2.fData.length?
           subtree1.fData.length:
           subtree2.fData.length;         // need max rows

  rows+=1;                               // for join


  output.fData= new Object[rows][cols];  // initialize

  for (int i=0;i<rows;i++){
    for (int j=0;j<cols;j++){
    output.fData[i][j]="";
  }
}

  
  //start with the left subtree and copy in, except for line No and justification

  for(int row=0;row<subtree1.fData.length;row++){
    for (int col = 1; col < subtree1Cols-1; col++) {   //omit lineNo and Just
      output.fData[row + 1][col] = subtree1.fData[row][col];
    }
    // now for lineNo and justification
    // proper prooflines have them but blanklines, diagonals etc. do not
    
    Object maybeLineNo = subtree1.fData[row][0];
    Object maybeJustification = subtree1.fData[row][subtree1Cols-1];
    
    // if we have a lineNO, we'll put it and justification in

    if (maybeLineNo instanceof Integer){
      output.fData[row + 1][0]=maybeLineNo;
      output.fData[row + 1][cols-1]=maybeJustification;
    }

  }
  
  // the same for the right hand tree

  int colOffset=subtree1Cols-1;

 for(int row=0;row<subtree2.fData.length;row++){
   for(int col=1;col<subtree2Cols-1;col++){            //omit lineNo and Just
     output.fData[row+1][col+colOffset]=subtree2.fData[row][col];
   }
   // now for lineNo and justification
   Object maybeLineNo = subtree2.fData[row][0];
   Object maybeJustification = subtree2.fData[row][subtree2Cols-1];

   if (maybeLineNo instanceof Integer){
     output.fData[row + 1][0]=maybeLineNo;
     output.fData[row + 1][cols-1]=maybeJustification;

}

  }

  output.fData[0][subtree1Cols-1] = joinNode;   // goes where the just used to be in the left tree
  output.fRootIndex=subtree1Cols-1;    			//put it between two

  if(!joinNode.fLabelOnly){      //can be String

    int lineNo=joinNode.fLineno;
    int justNo=joinNode.fFirstjustno;
    int secondJustNo=joinNode.fSecondjustno;
    String justification = replaceBreakingWithNonBreakingSpace(joinNode.fJustification); 


    if (lineNo>0)
      output.fData[0][0]= new Integer(lineNo);
// a lineNo of 0 is an error, also the closing Xs of closed branches
// it can be a node, the closing X of a closed branch -> no lineNo
// it can be the string "Vertical" for vertical line, no lineNumber from the line

    output.fData[0][cols-1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
                              ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
                              justification; //if a row has formula nodes all of them have the same justification

}

 //the column below the actual join was initialized to ""
  // but we will mark it
  
  output.fData[1][output.fRootIndex]= new String("Center");


 return
     output;
}

PlacementData extend(TGWTTestNode extendNode,PlacementData subtree1){

    /*put node on top 
     * 
     * So what we have is an array of prooflines, with one column
     * being the line no, another the justification, and the actual
     * nodes in the middle
     * Then there is an 'extendNode' which we are going to put on top
     * 
     * The rootnode might be in any of several columns in the middle
     * but the field fRootIndex tells us where it is, and that is where
     * the extend node will go*/
	
	

    PlacementData output = new PlacementData();

    int subtree1Cols=subtree1.fData[0].length;

    int cols = subtree1Cols;
    int rows=subtree1.fData.length;
    
    //we know the number of rows and cols in the subtree
    //we'll add another row and set everything empty

    output.fData= new Object[rows+1][cols];  // initialize
    for (int i=0;i<rows+1;i++){
      for (int j=0;j<cols;j++){
        output.fData[i][j]="";
      }
    }

    // copy the subtree onto the bottom

    for (int i=0;i<rows;i++){
      for (int j=0;j<cols;j++){

      output.fData[i + 1][j] = subtree1.fData[i][j];
    }
  }

    //put the extendNode and the right place and note its index
    
    output.fData[0][subtree1.fRootIndex] = extendNode;
    output.fRootIndex=subtree1.fRootIndex;

   // now to add line numbers and justification if needed

    if(!extendNode.fLabelOnly){      //can be, eg, the X of a closing node
    								// it can be the string "Vertical" for vertical line, 
    								//no lineNumber from the line
      int lineNo=extendNode.fLineno;
      int justNo=extendNode.fFirstjustno;
      int secondJustNo=extendNode.fSecondjustno;
      String justification = replaceBreakingWithNonBreakingSpace(extendNode.fJustification);

      if (lineNo>0)
        output.fData[0][0]= new Integer(lineNo);
     // a lineNo of 0 is an error, also the closing Xs of closed branches
     // it can be a node, the closing X of a closed branch -> no lineNo
     // it can be the string "Vertical" for vertical line, no lineNumber from the line

      	//now the justification
      	output.fData[0][cols-1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
          ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
          justification; //if a row has formula nodes all of them have the same justification
    } 

  //TO DO  
 

    if(extendNode.fLabelOnly){               //can be String
      if (extendNode.fLabel.equals("LeftDiag"))
          {   // put spaces in if it is spreading horizontally

        for (int i = output.fRootIndex + 1; i < cols; i++)  //spaces right
          output.fData[0][i] = new String("Horizontal");
      }
      if ( extendNode.fLabel.equals("RightDiag"))
      {

        for (int i = 0; i < output.fRootIndex; i++)        //spaces left
          output.fData[0][i] = new String("Horizontal");
      }

    // The column of fRootIndex has extendNode in row 0, possible with "" left and
    // from the initialization, and possibly
      
      
      /*experiment  May 2012
       * 
       * if we have a left diagonal, we'll add a blank column
       * */
      
      /*
    
      if ( extendNode.fLabel.equals("LeftDiag")){
    	  
    	  PlacementData experiment = new PlacementData();
    	  experiment.fData= new Object[rows+1][cols+1];  // extra column
    	  for (int i=0;i<rows+1;i++){
    		  for (int j=0;j<cols;j++){
    			  experiment.fData[i][j]=output.fData[i][j];
    	      }
    	    }
    	  
    	  for (int i=0;i<rows+1;i++){
    		  experiment.fData[i][cols]=new String("Horizontal"); //put blank on right
    	  }
    	  
    	  output=experiment;
      }
    */
    }
    
   


    return
    	     output;
}

PlacementData leaf(TGWTTestNode leaf){
	
/*This is making a linear, numbered, proofline out of a Node.
 * 
 * The proofline is going to consist of a row in an array with
 * three columns; the line number, the leaf, and the justification
 * */	
	
	
  PlacementData output = new PlacementData();

  output.fData= new Object[1][3];
  output.fData[0][0]= ""; //TGWTTestNode.supplyBlank();// "";  May 2012
  output.fData[0][1]=leaf;
  output.fData[0][2]=""; //TGWTTestNode.supplyBlank();// "";
  output.fRootIndex= 1;

  if(!leaf.fLabelOnly){      //can be String
    int lineNo=leaf.fLineno;
    int justNo=leaf.fFirstjustno;
    int secondJustNo=leaf.fSecondjustno;
    String justification = replaceBreakingWithNonBreakingSpace(leaf.fJustification);

    if (lineNo>0)
      output.fData[0][0]= new Integer(lineNo);
// a lineNo of 0 is an error, also the closing Xs of closed branches
// it can be a node, the closing X of a closed branch -> no lineNo
// it can be the string "Vertical" for vertical line, no lineNumber from the line

    output.fData[0][2] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
        ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
      justification; //if a row has formula nodes all of them have the same justification

}


  return
     output;
}

PlacementData newPlaceDescendants(TGWTTestNode start){
  int children = start.getChildCount();

   switch (children){
     case 0:
       return
           leaf(start);

     case 1:
     TGWTTestNode onlyChild=(TGWTTestNode)(start.getChild(0)); 
     
     //to do DONE (TGWTTestNode)(start.getFirstChild());

       return
           extend(start,newPlaceDescendants(onlyChild));


     case 2:
       TGWTTestNode leftChild=(TGWTTestNode)(start.getChild(0)); //to do DONE (TGWTTestNode)(start.getFirstChild());
       TGWTTestNode rightChild=(TGWTTestNode)(start.getChild(1)); //to do DONE (TGWTTestNode)(start.getChildAt(1));
       return
           join(start,newPlaceDescendants(leftChild),newPlaceDescendants(rightChild));
   }
   return
       null;

}

/******************************/

/*
void placeDescendants(TGWTTestNode start,int row,int column){
  int children = start.getChildCount();

  switch (children){
    case 0:
      return;

    case 1:
      TGWTTestNode onlyChild=(TGWTTestNode)(start.getChild(0));//to do  (TGWTTestNode)(start.getFirstChild());


      fData[row+1][column]=onlyChild;

      if(onlyChild.getUserObject() instanceof TGWTTestNode){      //can be String
        TGWTTestNode data=(TGWTTestNode) (onlyChild.getUserObject());

        int lineNo=data.fLineno;
        int justNo=data.fFirstjustno;
        int secondJustNo=data.fSecondjustno;
        String justification = data.fJustification;

        fData[row+1][0]= new Integer(lineNo); // it can be the string "Vertical" for vertical line, no lineNumber from the line
        fData[row+1][fColumnCount - 1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
            ( (secondJustNo == 0) ? "" : ","+ String.valueOf(secondJustNo)) +
            justification; //if a row has formula nodes all of them have the same justification

      }

      placeDescendants(onlyChild,row+1, column);

      return;

    case 2:
      TGWTTestNode leftChild=(TGWTTestNode)(start.getChild(0));//to do  (TGWTTestNode)(start.getFirstChild());
      TGWTTestNode rightChild=(TGWTTestNode)(start.getChild(1));//to do  (TGWTTestNode)(start.getChildAt(1));

      fData[row+1][column-1]=leftChild;                //the immediate children are left and right diagonals
      fData[row+1][column+1]=rightChild;               //ie they are a blank line and have no line number, and no justification

      placeDescendants(leftChild,row+1, column-1);
      placeDescendants(rightChild,row+1, column+1);
      return;

  }

}

*/

/********************* Selection *******************************************/

/* The DisplayCellTable looks after the physical selection on mouse click, but we wish to
 * know which TGWTTestNodes in the tree have been selected
 */

int[][] selectedIndices() { //returned as row index in [0] , col index in [1]

   
	
//	Set <Object[]> selected=fDisplay.fSelectionModel.getSelectedSet();
	
	if (true) ;
	
/*	
	// int maxRows = getRowCount();
    // int maxCols = getColumnCount();

    int selRows[] = getSelectedRows();
    int selRowCount = selRows.length;
    int selCols[] = getSelectedColumns();
    int selColCount = selCols.length;

    if (selRowCount == 0 || selColCount == 0)
      return
          null;

    int num = (selRowCount > selColCount ? selRowCount : selColCount); // two selected cells might be in same row (or col)

    int realCount = 0;

    for (int r = 0; (r < selRowCount) && realCount < num; r++) { // this is important because selRows and selCols return underlying selection, but we
      for (int c = 0; (c < selColCount) && realCount < num; c++) { // override isCellSelected to unselect some of these
        if (isCellSelected(selRows[r], selCols[c])) {
          realCount++;
        }
      }
    }

    num = realCount;

    if (num == 0)
      return
          null;

    int selected[][] = new int[2][num];

    int index = 0;

    for (int r = 0; (r < selRowCount) && index < num; r++) {
      for (int c = 0; (c < selColCount) && index < num; c++) {
        if (isCellSelected(selRows[r], selCols[c])) {
          selected[0][index] = selRows[r];
          selected[1][index] = selCols[c];

          index++;
        }
      }
    }

    return
        selected; */
	return
			new int[2][2];
  }




/***************************************************************************/
public int getTreeDepth(){
  return
		
		  
      1;//to do  fHostRoot.getDepth();
}


int stepsLeft(TGWTTestNode node){

// counting leaves to the left of the root

if (node.getChildCount()==0)
    return
        0;                            // none

TGWTTestNode firstChild=(TGWTTestNode)(node.getChild(0));; //to do  (TGWTTestNode)(node.getFirstChild());

if (node.getChildCount()==1)
    return
       stepsLeft(firstChild);    // keep going down

TGWTTestNode secondChild=(TGWTTestNode)(node.getChild(1));//to do  (TGWTTestNode)(node.getChildAt(1));


return
     ((stepsLeft(firstChild)+1)>(stepsLeft(secondChild)-2)?
      (stepsLeft(firstChild)+1):
     (stepsLeft(secondChild)-2));    // possible to step one right, then a whole bunch left

}

int indexOfRoot(){
  int leftLeaves=stepsLeft(fHostRoot);

  /*then index 0 is lineNo,so root wants to get stepsLeft +1 in*/

return
     leftLeaves+1;
}


public void updateCache(){         //inefficient algorithm, but rarely executed

 if (fHostRoot==null)
   return;



PlacementData data = newPlaceDescendants(fHostRoot);

 fData=data.fData;
 fRowCount=fData.length;
 fColumnCount=fData[0].length;


/* //OLD
 fRowCount= fHostRoot.getDepth()+1;
            // + numBlankRows();

 fLeaves= fHostRoot.getLeafCount();
// fLeftLeaves=leftLeafCount(fHostRoot);

 fColumnCount= (fLeaves==1?3:fLeaves+1+2);  //leaves+1 for leaves, 2 colums for No and just, and root is a special case

  fData= new Object[fRowCount][fColumnCount];

  for (int i=0;i<fRowCount;i++){
    for (int j=0;j<fColumnCount;j++){

      fData[i][j]="";
    }
  }

  int lineNo=((TGWTTestNode) fHostRoot.getUserObject()).fLineno;

  int hostIndex=indexOfRoot();

  fData[0][hostIndex]=fHostRoot;   //put the root hard to the left
  fData[0][0]= new Integer(lineNo);

  int justNo=((TGWTTestNode) fHostRoot.getUserObject()).fFirstjustno;
  String justification = ((TGWTTestNode) fHostRoot.getUserObject()).fJustification;
  fData[0][fColumnCount - 1] = ( (justNo == 0) ? "" : String.valueOf(justNo)) +
    justification; //if a row has formula nodes all of them have the same justification


  placeDescendants(fHostRoot,0,hostIndex);

End of OLD */
}

/*OLD

  //now to fill data

Enumeration  breadthFirst=fHostRoot.breadthFirstEnumeration();

 ArrayList l;
 int level=0;
 int numOnLevel=0;
 int paddingOnEnds=0;
 int lineNo=1;

 TGWTTestNode next=null;

 if (breadthFirst.hasMoreElements())
   next=(TGWTTestNode)(breadthFirst.nextElement());


 boolean spacerRow=false;

 for (int row=0;row<fRowCount;row++){

   if (!spacerRow){
     fData[row][0]= new Integer(lineNo);           // row/line number
     lineNo+=1;
   }
   else
     spacerRow=false;                             // no lineno and reset

/*The row after a row with multiple children is a spacer. So we detect spacer
lower down, and act and reset on entry

   l=new ArrayList();

   while (level==row){
     l.add(next);

    if (next.getChildCount()>1)
         spacerRow=true;

     if (breadthFirst.hasMoreElements()){
       next = (TGWTTestNode) (breadthFirst.nextElement());
       level=next.getLevel();
     }
     else
       break;
   }

   /*we now have all of one level in our array list
    and we start transferring them, with spacing blanks into our data structure

   int tempIndex=0;
   Object colData;

   for (int column=0;column<fColumnCount;column++){

      numOnLevel=l.size();

      paddingOnEnds= (fColumnCount - numOnLevel - (numOnLevel-1))/2;

      if (column<paddingOnEnds||(fColumnCount-1)-column<paddingOnEnds){
      //  fData[row][column] = new TGWTTestNode("padding"); //new String("padding");  //
        continue;
      }

      // blanks on left and right ends

 //at this stage we've done all the evens ie the interbranch padding, and left and right padding

 //now to map one index to the other

     tempIndex=column;

     tempIndex-=paddingOnEnds; // this will give us zero based indices eg 0,1,2,3 4

     if (tempIndex%2==1){            // all odd columns are blank, the spaces between entries
      // fData[row][column] = "more padding";  // initialized balnk
       continue;
     }

     tempIndex/=2;

     colData=l.get(tempIndex);

     if (colData instanceof TGWTTestNode){
       TGWTTestNode temp=(TGWTTestNode)colData;
       if(temp.getUserObject() instanceof TGWTTestNode){
         int justNo=((TGWTTestNode) temp.getUserObject()).fFirstjustno;
         String justification = ((TGWTTestNode) temp.getUserObject()).fJustification;


         fData[row][fColumnCount -1] = ((justNo==0)?"":String.valueOf(justNo)) + justification; //if a row has formula nodes all of them
         // have the same justification
       }
     }


     fData[row][column] = colData;

     int dummy=0;  //for debug stop
   }


 } */

 /****** OLD *******/

/*  for (int row=0;row<fRowCount;row++){
    for (int col=0;col<fRowCount;col++){

      fData[row][col]= getDataAt(row,col);

    }

  }


} */

/*  OLD

private Object getDataAt(int row, int column) {
    int level=0;

     Enumeration  breadthFirst=fHostRoot.breadthFirstEnumeration();   //unfortunately an enumeration can be used only once, so
                                                                 // I am repeating lots here. Needs rewrite

 ArrayList l=new ArrayList();

 while ((breadthFirst.hasMoreElements()&&level<=row)){
   TGWTTestNode next=(TGWTTestNode)(breadthFirst.nextElement());
   level=next.getLevel();
   if (level==row)
     l.add(next);
 }

 int numOnLevel=l.size();
 int totalColumns=getColumnCount();   // now need to fill odd columns centered on middle

 int paddingOnEnds= (totalColumns - numOnLevel - (numOnLevel-1))/2;

 if (column<paddingOnEnds||(totalColumns-1)-column<paddingOnEnds)
    return
       "";   // blanks on left and right ends

 //at this stage we've done all the evens ie the interbranch padding, and left and right padding

 //now to map one index to the other

 column-=paddingOnEnds; // this will give us zero based indices eg 0,1,2,3 4

 if (column%2==1)            // all odd columns are blank, the spaces between entries
   return
       "";
                         // this leaves us with 0,2,4

 column/=2;             // this will remove allowance for the blanks between branches eg 0,1,2

  return
     l.get(column);
}

*/



public String getColumnName(int c){

  return
      " ";                               // blank (not null) headers,

}

public int getItsColumn(Object thisOne){
  int value=-1;

 for (int i=0;i<fRowCount;i++){
   for (int j=0;j<fColumnCount;j++){

     if (fData[i][j] == thisOne)
       return
           j;
   }
 }

 return
     value;
}

public static int ROWCHANGE=1;
public static int COLCHANGE=2;

public void treeChanged(int type, Object entry ){
  
	updateCache();

if (type==ROWCHANGE){
 //to do fireTableRowsInserted(0, 1);

//fireTableDataChanged();
}

if (type==COLCHANGE){
  //to dofireTableRowsInserted(0, 1);

  //to do fireTableStructureChanged();

 // fireTableRowsInserted(0, 1);

 // fireTableDataChanged();

//  TableModelEvent e= new TableModelEvent(this, 0, 0, getItsColumn(entry));

//  fireTableChanged(e);

}

  //fireTableStructureChanged();

fDisplay.synchronizeViewToData();
}



/*Sometimes their entirely separate column width algorithm wraps on 
 * space and makes columns too narrow
 */

String replaceBreakingWithNonBreakingSpace(String s){
	return
	 s.replaceAll(" ","\u00a0");  
}

}





