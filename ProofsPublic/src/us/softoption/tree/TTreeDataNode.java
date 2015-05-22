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

import static us.softoption.infrastructure.Symbols.strNull;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import us.softoption.interpretation.TTestNode;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

/*For modal logic, these tableaux are semantic tableaux so each DataNode has a 'world',
this is the fWorld field. For the most part we leave it blank which means any world */



public class TTreeDataNode extends TTestNode{

 public static final String nullWorld= "";
	
  String fJustification="";
  String fWorld=nullWorld;
  int fLineno=0, fFirstjustno=0,fSecondjustno=0;
  ArrayList fInstantiations=new ArrayList(); // for instantions of Universal Formulas

public TTreeDataNode(){

}

public TTreeDataNode(TParser aParser,TTreeModel aTreeModel){
  super(aParser,aTreeModel);

}

/*******************  Factory *************************/

public TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
  return
      new TTreeDataNode (aParser,aTreeModel);
}

DefaultMutableTreeNode supplyLeftDiagonal(){return new DefaultMutableTreeNode(new String("LeftDiag"));}
DefaultMutableTreeNode supplyRightDiagonal(){return new DefaultMutableTreeNode(new String("RightDiag"));}
DefaultMutableTreeNode supplyVertical(){return new DefaultMutableTreeNode(new String("Vertical"));}

public TTestNode copyNode(){

  // this copies the node, but uses the same parser and treemodel

  TTreeDataNode newNode=(TTreeDataNode)(supplyTTestNode(fParser,fTreeModel));

  newNode.fClosed=fClosed;
  newNode.fDead=fDead;

  newNode.fAntecedents=(ArrayList)fAntecedents.clone();  //shallow copy
  newNode.fSuccedent=(ArrayList)fSuccedent.clone();

  newNode.fStepType=fStepType;
  newNode.fStepsToExpiry=fStepsToExpiry;

  newNode.fRecurse=fRecurse;
  newNode.fNewVariable=fNewVariable;

  newNode.fJustification=fJustification;
  newNode.fWorld=fWorld;
  newNode.fLineno=fLineno;
  newNode.fFirstjustno=fFirstjustno;
  newNode.fSecondjustno=fSecondjustno;

  //does not set its fSwingTreeNode

  return
     newNode;

}

/************* getters and setters for Beans and File IO ******************/


public String getJustification(){
    return
        fJustification;
  }

  public void setJustification(String aJustification){

      fJustification=aJustification;
}

    public String getWorld(){
        return
            fWorld;
      }

      public void setWorld(String aWorld){

          fJustification=aWorld;
}

public int getLineno(){
  return
fLineno;
}

public void setLineno(int lineNo){

fLineno=lineNo;
}

public int getFirstJustno(){
  return
fFirstjustno;
}

public void setFirstJustno(int justNo){

fFirstjustno=justNo;
}

public int getSecondJustno(){
  return
fSecondjustno;
}

public void setSecondJustno(int justNo){

fSecondjustno=justNo;
}

/******************************************************/

  public TTreeDataNode returnOpenLeaf(){


 Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

   DefaultMutableTreeNode next=null;

   if (breadthFirst.hasMoreElements())
     next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

   while (next!=null){
     Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

     if (userObject instanceof TTreeDataNode){  // we act only on these
       TTreeDataNode data = (TTreeDataNode) userObject;

       if (!data.fClosed && next.getChildCount() == 0) { //open leaf

         return
             data;
       }
     }
       if (breadthFirst.hasMoreElements())
         next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
       else
         next = null;

   }

   return
       null;
}





  public boolean isTreeClosed(){

    return
        (returnOpenLeaf()==null);


/*
   boolean closed=true;

  Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

    DefaultMutableTreeNode next=null;

    if (breadthFirst.hasMoreElements())
      next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

    while (next!=null){
      Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

      if (userObject instanceof TTreeDataNode){  // we act only on these
        TTreeDataNode data = (TTreeDataNode) userObject;

        if (!data.fClosed && next.getChildCount() == 0) { //open leaf

          closed=false;
        }
      }
        if (breadthFirst.hasMoreElements())
          next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
        else
          next = null;

    }

    return
        closed;
*/
}

/*public void selectOpenBranch(){

} */

public void addToInstantiations(TFormula instantion){

  fInstantiations.add(instantion);

  }

public ArrayList getInstantiations(){

	  return
	    fInstantiations;

	  }

  public TFormula closedTermsInBranch(TreeNode[] branch){
    TFormula head=null;
    TFormula temp=null;
    boolean duplicates=true;

    Object userObject;

    for (int i=0;(i<branch.length);i++){
      userObject = ( (DefaultMutableTreeNode) branch[i]).getUserObject();
      if (userObject instanceof TTreeDataNode) { // we act only on these
        TTreeDataNode data = (TTreeDataNode) userObject;

       TFormula nodeFormula = (TFormula) (data.fAntecedents.get(0));

       if (nodeFormula!=null){
         temp=nodeFormula.closedTermsInFormula();

         head=TFormula.concatLists(head,temp,!duplicates);

       }
      }
    }

    return
        head;
  }
  
  
  public TFormula formulasInBranch(TreeNode[] branch){  //returns list of formulas
	    TFormula head=null;
	    head = new TFormula(TFormula.kons,strNull,null,null); //start a list  May 10

	    Object userObject;

	    for (int i=0;(i<branch.length);i++){
	      userObject = ( (DefaultMutableTreeNode) branch[i]).getUserObject();
	      if (userObject instanceof TTreeDataNode) { // we act only on these
	        TTreeDataNode data = (TTreeDataNode) userObject;

	       TFormula nodeFormula = (TFormula) (data.fAntecedents.get(0));

	       if (nodeFormula!=null){

	         head.appendIfNotThere(nodeFormula);

	       }
	      }
	    }

	    return
	        head;
	  }
  
  
  public TFormula atomicOrNegatomicFormulasInBranch(TreeNode[] branch){  //returns list of formulas
	    TFormula head=null;
	    head = new TFormula(TFormula.kons,strNull,null,null); //start a list  May 10

	    Object userObject;

	    for (int i=0;(i<branch.length);i++){
	      userObject = ( (DefaultMutableTreeNode) branch[i]).getUserObject();
	      if (userObject instanceof TTreeDataNode) { // we act only on these
	        TTreeDataNode data = (TTreeDataNode) userObject;

	       TFormula nodeFormula = (TFormula) (data.fAntecedents.get(0));

	       if (nodeFormula!=null&&
	    	   TParser.isAtomicOrNegAtomic(nodeFormula)   ){

	         head.appendIfNotThere(nodeFormula);

	       }
	      }
	    }

	    return
	        head;
	  } 
  
  
  

  boolean everyClosedTermInstantiated(TFormula closedTerms, ArrayList instantiations){

   TFormula search=closedTerms;  // the closed terms are in a linked list with the value off left and the link right

   while (search!=null){
      if (!search.fLLink.formulaInList(instantiations))
             return
                false;
      search=search.fRLink;

   }

    return
        true;
  }




public boolean branchClosable(TreeNode[] branch){

   /*This is to detect where a branch contains a formula and its negation, so is closable, yet the User might not have
  spotted this and thus specifically closed it

    */


    boolean closable=false;
    Object userObject;

    // we get the formulas in the branch

    ArrayList branchFormulas= new ArrayList();
    TFormula search=null;

    for (int i=0;(i<branch.length);i++){
      userObject=((DefaultMutableTreeNode)branch[i]).getUserObject();
      if (userObject instanceof TTreeDataNode){  // we act only on these
        TTreeDataNode data = (TTreeDataNode) userObject;
        search=(TFormula)(data.fAntecedents.get(0));
        branchFormulas.add(search);
      }
    }

    if (branchFormulas.size()>1){
      TFormula dummy1= new TFormula();
      TFormula dummy2= new TFormula();
      return
          TFormula.twoInListContradict(branchFormulas, dummy1,
                                   dummy2);
    }
 return
     closable;
  }







  public boolean branchComplete(TreeNode[] branch){

   /*Three conditions

    a) every normal non-literal extended
    b) every universal instantiated
    c) every universal instantiated to every constant or closed term in branch

    */


    boolean complete=true;
    Object userObject;

    for (int i=0;(i<branch.length)&&complete;i++){
      userObject=((DefaultMutableTreeNode)branch[i]).getUserObject();
      if (userObject instanceof TTreeDataNode){  // we act only on these
        TTreeDataNode data = (TTreeDataNode) userObject;

        int type=typeOfFormula((TFormula)(data.fAntecedents.get(0)));

        if (!data.fDead&&      // a non universal not extended
            type!=uni&&
            type!=atomic&&
            type!=negatomic)
               complete=false;


        if (!data.fDead&&
            type==uni&&
            data.fInstantiations.size()==0)  // not instantiated
               complete=false;

        if (!data.fDead&&
         type==uni&&
         data.fInstantiations.size()!=0){


         if (!everyClosedTermInstantiated(closedTermsInBranch(branch),data.fInstantiations))
           complete=false;   // all terms not instantiated

       //complete = false;
     }

      }


    }

 return
     complete;
  }

public boolean isABranchOpenAndClosable(){

/*There needs to be an open branch, which contains a formula and its negation*/

 boolean open=false;
 TreeNode[] branch;

Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

  DefaultMutableTreeNode next=null;

  if (breadthFirst.hasMoreElements())
    next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

  while ((next!=null)&&!open){
    Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

    if (userObject instanceof TTreeDataNode){  // we act only on these
      TTreeDataNode data = (TTreeDataNode) userObject;

      if (!data.fClosed && next.getChildCount() == 0) { //open leaf ie open branch

        branch=data.fSwingTreeNode.getPath();                //get its branch

        if (branchClosable(branch)){

          open = true;
        }

      }
    }
      if (breadthFirst.hasMoreElements())
        next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
      else
        next = null;

  }

  return
      open;

}


  public boolean isABranchOpenAndComplete(){

  /*There needs to be an open branch, then every open branch must be complete*/
	  
  /*The above is not the right condition. June 09 There needs to be an open branch, which is complete*/

   boolean open=false;
   TreeNode[] branch;

  Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

    DefaultMutableTreeNode next=null;

    if (breadthFirst.hasMoreElements())
      next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

    while ((next!=null)&&!open){
      Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

      if (userObject instanceof TTreeDataNode){  // we act only on these
        TTreeDataNode data = (TTreeDataNode) userObject;

        if (!data.fClosed && next.getChildCount() == 0) { //open leaf

          branch=data.fSwingTreeNode.getPath();                //get its branch

          if (branchComplete(branch)){

            open = true;
          }

        }
      }
        if (breadthFirst.hasMoreElements())
          next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
        else
          next = null;

    }

    return
        open;

}



private boolean isLevelBlank(DefaultMutableTreeNode hostRoot,int level){

  /*The condition is: no TreeDataNodes on the level (and there always is something on each level)*/

   boolean blank=true;

  if ((hostRoot==null)||(level<1))
    return
        !blank;                         //if empty not blank

  Enumeration breadthFirst = hostRoot.breadthFirstEnumeration();

  DefaultMutableTreeNode next = (DefaultMutableTreeNode) (breadthFirst.nextElement());

  int searchLevel= next.getLevel();

  while ((next!=null)&&
         (searchLevel<=level)){

    if ((searchLevel==level)&&
        (next.getUserObject() instanceof TTreeDataNode&&
        !((TTreeDataNode)next.getUserObject()).fClosed))   // we don't count the Xs that close branches
      return
        !blank;                       //if DataNode not blank
  else{
    if (breadthFirst.hasMoreElements()) {
      next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
      searchLevel = next.getLevel();
    }
    else
      next=null;;                      // if end of level, not non-blanck
  }


  }
  return
          blank;
  }

public void straightInsert(TTestNode leftNode, TTestNode rightNode, DefaultMutableTreeNode hostRoot, int depth){

/*we are trying to add to every terminal leaf that is not closed. And
  we have to increase depth so that they are added at the next level*/

Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

  DefaultMutableTreeNode next=null;
  TTestNode leftCopy=leftNode;
  TTestNode rightCopy=rightNode;

  if (breadthFirst.hasMoreElements())
    next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

  while (next!=null){
    Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

    if (userObject instanceof TTreeDataNode){  // we act only on these
      TTreeDataNode data = (TTreeDataNode) userObject;

      if (!data.fClosed && next.getChildCount() == 0) { //open leaf

        int level = next.getLevel();
        //int lineNo = ( (TTreeDataNode) leftCopy).fLineno = data.fLineno + 1;
        int lineNo = data.fLineno + 1;
        ( (TTreeDataNode) leftCopy).fLineno = lineNo;

        while (level < depth) { // padding down to the right insert level
          DefaultMutableTreeNode vertical = supplyVertical();
          next.add(vertical);
          next = vertical;
          if (!isLevelBlank(hostRoot, level+1)) // the vertical is on level+1
            lineNo += 1;
          level += 1;
        }

        //  ((TTreeDataNode)leftCopy).fLineno=((TTreeDataNode)(next.getUserObject())).fLineno+1;  //set its line number
        ( (TTreeDataNode) leftCopy).fLineno = lineNo; //set its line number

        next.add(leftCopy.fSwingTreeNode);

        if (rightCopy != null) {
          //  ((TTreeDataNode)rightCopy).fLineno=((TTreeDataNode)leftCopy).fLineno+1;  //set its line number
          ( (TTreeDataNode) rightCopy).fLineno = lineNo+1; //set its line number
          leftCopy.fSwingTreeNode.add(rightCopy.fSwingTreeNode); //straight extension of two items down left branch
          rightCopy = rightCopy.copyNodeInFull();
        }

        leftCopy = leftCopy.copyNodeInFull();
      }
    }
      if (breadthFirst.hasMoreElements())
        next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
      else
        next = null;

  }

}


public void splitInsert(TTestNode leftNode, TTestNode rightNode,DefaultMutableTreeNode hostRoot,int depth){

/*we are trying to add to every terminal leaf that is not closed*/

Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

  DefaultMutableTreeNode next=null;
  TTestNode leftCopy=leftNode;
  TTestNode rightCopy=rightNode;
  DefaultMutableTreeNode leftDiagonal=null;
  DefaultMutableTreeNode rightDiagonal=null;

  if (breadthFirst.hasMoreElements())
    next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

  while (next!=null){
    Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

    if (userObject instanceof TTreeDataNode){  // we act only on these
      TTreeDataNode data = (TTreeDataNode) userObject;

      if (!data.fClosed && next.getChildCount() == 0) {

        int level = next.getLevel();

        //int lineNo = ( (TTreeDataNode) leftCopy).fLineno = data.fLineno + 1;
      int lineNo = data.fLineno + 1;
      ( (TTreeDataNode) leftCopy).fLineno = lineNo;


        while (level < depth) { // padding down to the right insert level
          DefaultMutableTreeNode vertical = supplyVertical();
          next.add(vertical);
          next = vertical;
          if (!isLevelBlank(hostRoot, level+1))
            lineNo += 1;
          level += 1;
        }

        ( (TTreeDataNode) leftCopy).fLineno = lineNo; //set its line number
        ( (TTreeDataNode) rightCopy).fLineno = lineNo; //set its line number

        leftDiagonal = supplyLeftDiagonal(); // diagonals don't have and don't increment lineNos
        next.add(leftDiagonal);
        leftDiagonal.add(leftCopy.fSwingTreeNode);
        rightDiagonal = supplyRightDiagonal();
        next.add(rightDiagonal);
        rightDiagonal.add(rightCopy.fSwingTreeNode);

        leftCopy = leftCopy.copyNodeInFull();
        rightCopy = rightCopy.copyNodeInFull();
        depth+=2;                           // if we do this more than once we must make later on different levels
                                            // they can share the central column but not the central cell.
                                            // must also clear diagonals
      }
    }
      if (breadthFirst.hasMoreElements())
        next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
      else
        next = null;

  }

}


public void splitInsertTwo(TTestNode leftNode,TTestNode left2Node,
                           TTestNode rightNode,TTestNode right2Node,
                           DefaultMutableTreeNode hostRoot,int depth){

/*we are trying to add to every terminal leaf that is not closed*/

Enumeration  breadthFirst=this.fSwingTreeNode.breadthFirstEnumeration();

  DefaultMutableTreeNode next=null;
  TTestNode leftCopy=leftNode;
  TTestNode left2Copy=left2Node;
  TTestNode rightCopy=rightNode;
  TTestNode right2Copy=right2Node;
  DefaultMutableTreeNode leftDiagonal=null;
  DefaultMutableTreeNode rightDiagonal=null;

  if (breadthFirst.hasMoreElements())
    next=(DefaultMutableTreeNode)(breadthFirst.nextElement());   //me

  while (next!=null){
    Object userObject=next.getUserObject();    // this can be DataNode, but it can be a string

    if (userObject instanceof TTreeDataNode){  // we act only on these
      TTreeDataNode data = (TTreeDataNode) userObject;

      if (!data.fClosed && next.getChildCount() == 0) {

        int level = next.getLevel();
        //int lineNo = ( (TTreeDataNode) leftCopy).fLineno = data.fLineno + 1;
      int lineNo = data.fLineno + 1;
      ( (TTreeDataNode) leftCopy).fLineno = lineNo;
      ( (TTreeDataNode) left2Copy).fLineno = lineNo+1;


        while (level < depth) { // padding down to the right insert level
          DefaultMutableTreeNode vertical = supplyVertical();
          next.add(vertical);
          next = vertical;
          if (!isLevelBlank(hostRoot, level+1))
            lineNo += 1;
          level += 1;
        }

        ( (TTreeDataNode) leftCopy).fLineno = lineNo; //set its line number
        ( (TTreeDataNode) left2Copy).fLineno = lineNo+1; //set its line number
        ( (TTreeDataNode) rightCopy).fLineno = lineNo; //set its line number
        ( (TTreeDataNode) right2Copy).fLineno = lineNo+1; //set its line number

        leftDiagonal = supplyLeftDiagonal(); // diagonals don't have and don't increment lineNos
        next.add(leftDiagonal);
        leftDiagonal.add(leftCopy.fSwingTreeNode);
        leftCopy.fSwingTreeNode.add(left2Copy.fSwingTreeNode);

        rightDiagonal = supplyRightDiagonal();
        next.add(rightDiagonal);
        rightDiagonal.add(rightCopy.fSwingTreeNode);
        rightCopy.fSwingTreeNode.add(right2Copy.fSwingTreeNode);

        leftCopy = leftCopy.copyNodeInFull();
        left2Copy = left2Copy.copyNodeInFull();
        rightCopy = rightCopy.copyNodeInFull();
        right2Copy = right2Copy.copyNodeInFull();
        depth+=3;                           // if we do this more than once we must make later on different levels
                                    // they can share the central column but not the central cell.
                                    // must clear two entries and diagonals

      }
    }
      if (breadthFirst.hasMoreElements())
        next = (DefaultMutableTreeNode) (breadthFirst.nextElement());
      else
        next = null;

  }

}






public String toString(){
  String returnStr="";
  int length=fAntecedents.size();

  for (int i=0;i<length;i++){
    returnStr= returnStr
        + fParser.writeFormulaToString((TFormula)fAntecedents.get(i));

  if (i<(length-1))
    returnStr= returnStr +", ";

  }

  if (fWorld.length()>0)
    returnStr+=" ("+fWorld+")";

/*  returnStr+=" "+ chTherefore;

  length=fSuccedent.size();

  for (int i=0;i<length;i++){
    returnStr= returnStr
        + fParser.writeFormulaToString((TFormula)fSuccedent.get(i));

  if (i<(length-1))
    returnStr= returnStr +", ";

  } */


/*
  if (fDead)
    returnStr+=" dead";

  if (fClosed)
    returnStr+=" closed";
  else
    returnStr+=" open";

  returnStr+=" " + fStepType;
*/

  return
      returnStr;


}

}
