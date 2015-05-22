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

package us.softoption.interpretation;

import static us.softoption.infrastructure.Symbols.chAnd;
import static us.softoption.infrastructure.Symbols.chBlank;
import static us.softoption.infrastructure.Symbols.chExiquant;
import static us.softoption.infrastructure.Symbols.chImplic;
import static us.softoption.infrastructure.Symbols.chNeg;
import static us.softoption.infrastructure.Symbols.chTherefore;
import static us.softoption.infrastructure.Symbols.chUniquant;
import static us.softoption.infrastructure.Symbols.strCR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import us.softoption.infrastructure.TFlag;
import us.softoption.infrastructure.TUtilities;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;


/*

In the Pascale version, there was no display and the whole tree was built out of TTestNodes
using their left and right links.  TTestNodes were all there were.

In Swing, JTree is going to display the tree, and the data Model will be a DefaultTreeModel
built out of DefaultTreeNodes. For this, our old TTestNodes are just going to have the data on
the formula lists etc. These TTestNodes have their toString method overrided so they can draw themselves
as part of JTree.

This means that if you want the left and right children, say, the TTestNodes know nothing about these. You have
to get the fSwingTreeNode (which is the node that holds the TTestNode) then do something like this

       DefaultMutableTreeNode leftChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(0);
       leftChildNode=(TTestNode)(leftChild.getUserObject());

       DefaultMutableTreeNode rightChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(1);
       rightChildNode=(TTestNode)(rightChild.getUserObject());

*/

/*

Careful: there are TTreeModels, TreeNodes, TTestNodes etc. and these are different

Note that when you start one of these TTestNodes from, say, root, there has to be and almost circular initialization.
There needs to be a TTreeModel and root's TreeNode needs to refer to it. And the TTreeModel itself needs to know
what root TreeNode it is talking about. So typically you do something like

   TTestNode aTestRoot = new TTestNode(fDeriverDocument.getParser(),null);  //does not initialize TreeModel

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fSwingTreeNode);       // produces a TreeModel and points it to root's TreeNode

   aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now


*/


/*

In Pascal, there just used to be the nodes and some globals (for example, to say whether change of
variable is needed). And the 'assumption' was that there was only one tree going at once and so
the globals were used sequentially for tree after tree. But any particular proof (tree) may involve little
test proofs of its own (ie independent trees).  These cannot share global variables. This was hacked around
in Pascal. It would be better if each tree had its own context. Now, each node knows of the TreeDataModel
of the tree it belongs to. So, we just subclass that to form TTreeDataModel and put the context there.


The 'stringstore' is there.

*/


/*
Change of variable...

If you have, say, Fx,(Ex)Gx in the antecedents


*/


public class TTestNode{

  public static TTestNode gOpenNode = null;

  /*constants*/
  /*argumenttype = (valid, notvalid, notknown);*/

  public static final int valid = 1;
  public static final int notValid = 2;
  public static final int notKnown = -1;

  public static final int notFound = -1;

  public static final int absurd = 1; // this and following used to identify the kind of node
  public static final int atomic = 2; // DO NOT ALTER THE ORDER OF THESE, WE RELY ON THE S
  public static final int atomicS = 3; // CONSTANTS BEING THE SUCCESSOR OF THE ORDINARY ONES
  public static final int negatomic = 4;
  public static final int negatomicS = 5;
  public static final int doubleneg = 6;
  public static final int doublenegS = 7;
  public static final int aand = 8;
  public static final int aandS = 9;
  public static final int negand = 10;
  public static final int negandS = 11;
  public static final int ore = 12;
  public static final int oreS = 13;
  public static final int nore = 14;
  public static final int noreS = 15;
  public static final int implic = 16;
  public static final int arrowS = 17;
  public static final int negarrow = 18;
  public static final int negarrowS = 19;
  public static final int equivv = 20;
  public static final int equivvS = 21;
  public static final int nequiv = 22;
  public static final int nequivS = 23;
  public static final int uni = 24;
  public static final int uniS = 25;
  public static final int uniSCV = 26;
  public static final int uniSpec = 27;
  public static final int neguni = 28;
  public static final int neguniS = 29;
  public static final int exi = 30;
  public static final int exiS = 31;
  public static final int exiCV = 32;
  public static final int negexi = 33;
  public static final int negexiS = 34;
  public static final int unique = 35;
  public static final int negunique = 36;
  public static final int unknown = 37;
  public static final int unknownS = 38;
  public static final int startroot = 39;

  public static final int modalNecessary = 40;
  public static final int notNecessary = 41;
  public static final int modalPossible = 42;
  public static final int notPossible = 43;

  public static final int typedUni = 44;
  public static final int typedUniS = 45;
  public static final int negTypedUni = 46;
  public static final int negTypedUniS = 47;

  public static final int typedExi = 48;
  public static final int typedExiS = 49;
  public static final int negTypedExi = 50;
  public static final int negTypedExiS = 51;
  
  public static final int modalKappa = 52;
  public static final int notModalKappa = 53;
  public static final int modalRho = 54;
  public static final int notModalRho = 55;
  public static final int modalDoubleKappa = 56;
  
  

  public static final int kMaxTreeDepth = 256;  //June10 was 256

  //public static boolean gExCV = false;
  //public static boolean gExCVFlag = false; // used for showing that quantifiers need to change variables

  public TParser fParser;

  /*formulatype = (absurd, atomic, atomicS, negatomic, negatomicS, doubleneg, doublenegS, aand, aandS, negand, negandS, ore, oreS, nore, noreS, aroww, arrowS, negarrow, negarrowS, equivv, equivvS, nequiv, nequivS, uni, uniS, uniSCV, uniSpec, neguni, neguniS, exi, exiS, exiCV, negexi, negexiS, unique, negunique, unknown, unknownS, startroot);  */

  public boolean fClosed = false;
  public boolean fDead = false;
  public ArrayList fAntecedents = new ArrayList();
  public ArrayList fSuccedent = new ArrayList();
  public int fStepType = unknown;
  public int fStepsToExpiry = 0;
  public int fNodeDepth = 0;

  public boolean fRecurse=true;

  public char fNewVariable=chBlank;   // for changing variables with quantifier

  // TTestNode fLLink=null;    SEE NOTES ABOVE ABOUT THIS, USED IN PASCAL BUT NOT HERE
  // TTestNode fRLink=null;

   public TTreeModel fTreeModel=null;   // the tree it belongs to, we subclass default to add context
   public DefaultMutableTreeNode fSwingTreeNode= new DefaultMutableTreeNode(this); // the node that holds me
   
   /*about 2008 the name for the above field was changed from 'fTreeNode' to 'fSwingTreeNode' for clarity (hem). Older
   code will have fTreeNode. There are 30 or more occurrences of this in this file.*/

/*TTestnode = object(TObject)
    fClosed: boolean;
    fDead: boolean;
    fAntecedents: TList;  (*list of antecedents*)
    fSucceedent: TList;
    fSteptype: formulatype;
    fLlink: TTestnode;
    fRlink: TTestnode;  */


public TTestNode (){

}



public TTestNode (TParser aParser,TTreeModel aTreeModel){
  fParser=aParser;
  fTreeModel=aTreeModel;
}

/*******************  Factory *************************/

public TTestNode supplyTTestNode (TParser aParser,TTreeModel aTreeModel){         // so we can subclass
  return
      new TTestNode (aParser,aTreeModel);
}


/******************************************************/


/************* getters and setters for Beans and File IO ******************/


public boolean getClosed(){
  return
      fClosed;
}

public void setClosed(boolean closed){

      fClosed=closed;
}

public boolean getDead(){
  return
      fDead;
}

public void setDead(boolean dead){

      fDead=dead;
}

public ArrayList getAntecedents(){
      return
          fAntecedents;
    }

public void setAntecedents(ArrayList antecedents){

          fAntecedents=antecedents;
}

public ArrayList getSuccedent(){
              return
                  fSuccedent;
            }

public void setSuccedent(ArrayList succedents){

                  fSuccedent=succedents;
}

public TTreeModel getTreeModel(){
  return
      fTreeModel;
}

public void setTreeModel(TTreeModel aTreeModel){
      fTreeModel=aTreeModel;
}

public DefaultMutableTreeNode getTreeNode(){
      return
          fSwingTreeNode;
    }

public void setTreeNode(DefaultMutableTreeNode aTreeNode){
          fSwingTreeNode=aTreeNode;
}

public TParser getParser(){
   return
       fParser;
}

public void setParser(TParser aParser){
                  fParser=aParser;
        }



/************************************************/



public void addToAntecedents(TFormula theFormula){

  fAntecedents.add(theFormula);

}



boolean anteEqSucc(){

  /*
  {one in antecedent list is equal to one in succdent list }

  */

 int length=fSuccedent.size();

 if (length==0)
   return
       false;
 else{
   boolean found=false;
   int i=0;

   while ((i<length)&& (!found))
     {
     found= ((TFormula)(fSuccedent.get(i))).formulaInList(fAntecedents);
     i++;
   }

   return
      found;
 }
}

/*

 function TTestnode.AnteEqSucc: boolean;

{one in antecedent list identical to succeedent }

  var
   found: boolean;
   succeedentformula: TFormula;

  function TestItem (item: TObject): boolean;

   var
    antecedentformula: TFormula;

  begin
   antecedentformula := TFormula(item);
   TestItem := SpecialEqualFormulas(antecedentformula, succeedentformula);
  end;

 begin
  found := false;

  if fSucceedent.fsize <> 0 then
   begin
    succeedentformula := TFormula(fSucceedent.First);
    if fAntecedents.fsize <> 0 then
     if (fAntecedents.FirstThat(TestItem) <> nil) then
      found := true;
   end;
  AnteEqSucc := found;
 end;



*/


public int treeDepth(){  //from this as root
	  TTestNode leftChildNode=null, rightChildNode=null;
	  int depth=this.fNodeDepth;
	  int rightDepth=0;
	  

	     int numChildren= fSwingTreeNode.getChildCount();

	     if (numChildren>0){
	       DefaultMutableTreeNode leftChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(0);
	       leftChildNode=(TTestNode)(leftChild.getUserObject());
	       
	       depth=leftChildNode.treeDepth();
	       

	        if (numChildren>1){
	          DefaultMutableTreeNode rightChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(1);
	          rightChildNode=(TTestNode)(rightChild.getUserObject());
	          rightDepth=rightChildNode.treeDepth();
	        }
	     }
if (rightDepth>depth)
	return rightDepth;
	else
	  return
	      depth;
	}





public TTestNode aNodeOpen(){
  TTestNode returnNode=null,leftChildNode=null, rightChildNode=null;

  if (fClosed)
     return
        null;
  else{

     int numChildren= fSwingTreeNode.getChildCount();

     if (numChildren>0){
       DefaultMutableTreeNode leftChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(0);
       leftChildNode=(TTestNode)(leftChild.getUserObject());
       returnNode=leftChildNode.aNodeOpen();

        if ((returnNode==null)&&(numChildren>1)){
          DefaultMutableTreeNode rightChild = (DefaultMutableTreeNode)fSwingTreeNode.getChildAt(1);
          rightChildNode=(TTestNode)(rightChild.getUserObject());
          returnNode=rightChildNode.aNodeOpen();
        }
     }
     if ((returnNode==null) && (numChildren==0) &&(fDead))  // terminal dead and not closed
        returnNode=this;
  }
  return
      returnNode;
}


/*

 function TTestnode.ANodeOpen (var openNode: TTestnode): boolean;

 {finds whether there is a terminal leaf which is open}

   var
    found: boolean;

  begin
   found := false;
   if not fClosed then
    begin
     if (fLlink <> nil) then
      found := found or fLlink.ANodeOpen(openNode);
     if not found then
      if (fRlink <> nil) then
       found := found or fRlink.ANodeOpen(openNode);
     if (fLlink = nil) and (fRlink = nil) and not fClosed and fDead then
      begin
       found := true;
       openNode := SELF;

      end;
    end;
   ANodeOpen := found;
  end;


*/





public boolean closeSequent(){

  /*
     {This checks whether 'branch' is closed and closes its node}
     {two cases: two antecedents contradict, or an antecedent equals the consequent}


  */

 boolean closedIt=false;
 TFormula dummyOne= new TFormula(), dummyTwo= new TFormula();

 if (anteEqSucc()){
   fClosed=true;
   fStepType=atomic;
   closedIt=true;
 }

 else

 if (TFormula.twoInListContradict(fAntecedents,dummyOne,dummyTwo)){
  fClosed=true;
  fStepType=atomicS;
  closedIt=true;
}

else
if (containsAbsurd()){
fClosed=true;
fStepType=absurd;
closedIt=true;
}




return
   closedIt;
}

/*
 function TTestnode.CloseSequent;

  var
   tempbool: boolean;
   firstone, secondone: TFormula;

          {This checks whether 'branch' is closed and closes its node}
          {two cases: two antecedents contradict, or an antecedent equals the consequent}

 begin
  tempbool := false;
  if SELF.AnteEqSucc then
   begin
    fClosed := true;
    fSteptype := atomic;
    tempbool := true;
   end
  else
   begin
    if SELF.TwoContradict(firstone, secondone) then
     begin
      fClosed := true;
      fSteptype := atomicS;
      tempbool := true;
     end
    else if SELF.ContainsAbsurd then
     begin
      fClosed := true;
      fSteptype := absurd;
      tempbool := true;
     end
   end;
  CloseSequent := tempbool;
 end;


*/

boolean containsAbsurd(){

  return
      TFormula.fAbsurd.formulaInList(fAntecedents);

}

/*

 function TTestnode.ContainsAbsurd: boolean;

{one in antecedent list identical to Absurd}

  var
   found: boolean;

  function TestItem (item: TObject): boolean;
   var
    antecedentformula: TFormula;
  begin
   antecedentformula := TFormula(item);
   TestItem := SpecialEqualFormulas(antecedentformula, gAbsurdFormula);
  end;

 begin
  found := false;
  if fAntecedents.fsize <> 0 then
   if (fAntecedents.FirstThat(TestItem) <> nil) then
    found := true;
  ContainsAbsurd := found;
 end;


*/

public TTestNode copyNode(){

  // this copies the node, but uses the same parser and treemodel

  TTestNode newNode=supplyTTestNode(fParser,fTreeModel);

  newNode.fClosed=fClosed;
  newNode.fDead=fDead;

  newNode.fAntecedents=(ArrayList)fAntecedents.clone();  //shallow copy
  newNode.fSuccedent=(ArrayList)fSuccedent.clone();

  newNode.fStepType=fStepType;
  newNode.fStepsToExpiry=fStepsToExpiry;
  newNode.fNodeDepth=fNodeDepth;

  newNode.fRecurse=fRecurse;
  newNode.fNewVariable=fNewVariable;

  //does not set its fSwingTreeNode

  return
     newNode;

}


/*

 function TTestnode.CopyNode: TTestnode;

{the actual formulas do not get copied}

  var
   newnode: TTestnode;

 begin
  New(newnode);
  FailNIL(newnode);
  newnode.ITestnode; {creates lists}
  newnode.fAntecedents.Free;  {check new}
  newnode.fSucceedent.Free;

  with newnode do
   begin
    fClosed := SELF.fClosed;
    fDead := SELF.fDead;
    fAntecedents := CopyFormList(SELF.fAntecedents);
    fSucceedent := CopyFormList(SELF.fSucceedent);
    fSteptype := SELF.fsteptype; {  check June 5 unknown;  thisnode.fsteptype;}
    fLlink := SELF.fLlink;
    fRlink := SELF.fRlink;
   end;

  CopyNode := newnode;
  newnode := nil;
 end;



*/

public TTestNode copyNodeInFull(){


  TTestNode temp=this.copyNode();

  if ((temp.fAntecedents != null)&& temp.fAntecedents.size()>0){
    for (int i=0;i<temp.fAntecedents.size();i++){
      temp.fAntecedents.set(i,((TFormula)temp.fAntecedents.get(i)).copyFormula());
    }
  }

  if ((temp.fSuccedent != null)&& temp.fSuccedent.size()>0){
      for (int i=0;i<temp.fSuccedent.size();i++){
        temp.fSuccedent.set(i,((TFormula)temp.fSuccedent.get(i)).copyFormula());
      }
    }

  return

      temp;
}


/*
 function TTestnode.CopyNodeinFull: TTestnode;

   var
    temp: TTestnode;

           {copies formulas as well, but not instantiating info}

   procedure CopyAnteItem (item: TObject);

    var
     tempformula: TFormula;

   begin
    tempformula := TFormula(item).CopyFormula;
    if (tempformula.fkind = quantifier) then {quant}
     tempformula.finfo := Copy(tempformula.finfo, 1, 1);
                {removes extra instantiating information, which we do not want for a secondtest}
    temp.fAntecedents.InsertLast(tempformula);
   end;

   procedure CopySuccItem (item: TObject);

    var
     tempformula: TFormula;

   begin
    tempformula := TFormula(item).CopyFormula;
    if tempformula.fkind = quantifier then {quant}
     tempformula.finfo := Copy(tempformula.finfo, 1, 1);
                {removes extra instantiating information, which we do not want for a secondtest}
    temp.fSucceedent.InsertLast(tempformula);
   end;

  begin
   temp := SELF.CopyNode;

   temp.fAntecedents.DeleteAll;
   temp.fSucceedent.DeleteAll;

   SELF.fAntecedents.Each(CopyAnteItem); {rebuilds lists}
   SELF.fSucceedent.Each(CopySuccItem);

   CopyNodeinFull := temp;
   temp := nil;
  end;




*/




public TTestNode copyNodeInFullWithInstInfo(){
  //{copies the node, the formulas, and instantiating info}

  /*There is an issue here with context or instantiations. The instantiaton are stored in
  a hash table keyed by the formula. But if you copy a formula, that formula is no longer a key.
Hence you have to reinsert.*/

  TTestNode temp=this.copyNode();
  TFormula copyFormula, originalFormula;
  String instants;

  if ((temp.fAntecedents != null)&& temp.fAntecedents.size()>0){
    for (int i=0;i<temp.fAntecedents.size();i++){
      originalFormula=(TFormula)temp.fAntecedents.get(i);
      copyFormula=originalFormula.copyFormula();

      if (fTreeModel.getOldInstantiations().containsKey(originalFormula)){
        instants = (String) (fTreeModel.getOldInstantiations().get(originalFormula));
        fTreeModel.getOldInstantiations().put(copyFormula,instants);
      }
      temp.fAntecedents.set(i,copyFormula);
    }
  }

  if ((temp.fSuccedent != null)&& temp.fSuccedent.size()>0){
      for (int i=0;i<temp.fSuccedent.size();i++){
        originalFormula=(TFormula)temp.fSuccedent.get(i);
      copyFormula=originalFormula.copyFormula();

      if (fTreeModel.getOldInstantiations().containsKey(originalFormula)){
        instants = (String) (fTreeModel.getOldInstantiations().get(originalFormula));
        fTreeModel.getOldInstantiations().put(copyFormula, instants);
      }
        temp.fSuccedent.set(i,copyFormula);
      }
    }

    return  /*/*Tif (fTreeModel.getOldInstantiations().containsKey(quantform))
     oldInstants=(String)(fTreeModel.getOldInstantiations().get(quantform));

   instants=TUtilities.stringDifference(instants, oldInstants);

   if (instants.length()==0)
     return
         false;
   else{
     fTreeModel.getNewInstantiations().put(quantform,instants);
 */

      temp;
}


/*

 function TTestnode.CopyNodeinFullWithInstInfo: TTestnode;

   var
    temp: TTestnode;

           {copies formulas as well, and instantiating info}

   procedure CopyAnteItem (item: TObject);

    var
     tempformula: TFormula;

   begin
    tempformula := TFormula(item).CopyFormula;
    temp.fAntecedents.InsertLast(tempformula);
   end;

   procedure CopySuccItem (item: TObject);

    var
     tempformula: TFormula;

   begin
    tempformula := TFormula(item).CopyFormula;
    temp.fSucceedent.InsertLast(tempformula);
   end;

  begin
   temp := SELF.CopyNode;

   temp.fAntecedents.DeleteAll;
   temp.fSucceedent.DeleteAll;

   SELF.fAntecedents.Each(CopyAnteItem); {rebuilds lists}
   SELF.fSucceedent.Each(CopySuccItem);

   CopyNodeinFullWithInstInfo := temp;
   temp := nil;
  end;



*/





public TTestNode getLeftChild(){
  TTestNode leftChildNode=null;

  int numChildren= fSwingTreeNode.getChildCount();

  if (numChildren>0) {
    DefaultMutableTreeNode leftChild = (DefaultMutableTreeNode) fSwingTreeNode.getChildAt(0);
    leftChildNode = (TTestNode) (leftChild.getUserObject());
  }
  return
     leftChildNode;
}
public TTestNode getRightChild(){
  TTestNode rightChildNode=null;

  int numChildren= fSwingTreeNode.getChildCount();

  if (numChildren>1) {
    DefaultMutableTreeNode rightChild = (DefaultMutableTreeNode) fSwingTreeNode.
        getChildAt(1);
    rightChildNode = (TTestNode) (rightChild.getUserObject());
  }
  return
     rightChildNode;
}

String termsToTry (TFormula quantform,TFlag capturing){

  /*

     warns if there is capturing of variables}
   {This is to help find instantiations-- these have to be terms that occur }
   {(Herbrand Universe) and which are legitimate substitutions, no capturing}
*/
  String totalTerms=TFormula.freeAtomicTermsInListOfFormulas(fAntecedents)
                    + TFormula.freeAtomicTermsInListOfFormulas(fSuccedent);
  totalTerms=TUtilities.removeDuplicateChars(totalTerms);

  TFormula termForm= new TFormula(TFormula.variable,"",null,null);

 for (int i=totalTerms.length()-1;i>-1;i--){
   termForm.fInfo=totalTerms.substring(i);

   if (!(quantform.fRLink.freeForTest(termForm, quantform.quantVarForm()))){

      totalTerms=totalTerms.substring(0,i-1); //{should prevent capturing}
      capturing.setValue(true);
   }

 }
 return
     totalTerms;
}


/*
function TTestnode.TermsToTry (quantform: TFormula; var capturing: boolean): str255;
{warns if there is capturing of variables}
{This is to help find instantiations-- these have to be terms that occur }
{(Herbrand Universe) and which are legitimate substitutions, no capturing}

  var
   totalterms: str255;
   i: integer;
   termForm: TFormula;



 begin
  supplyFormula(termForm);
  termForm.fKind := variable; (*check*)

  totalterms := concat(FreeTermsInList(fAntecedents), FreeTermsInList(fSucceedent));

  RemoveDuplicates(totalterms);
  for i := length(totalterms) downto 1 do
   begin
    termForm.fInfo := totalterms[i];
    if not quantform.fRlink.FreeForTest(termForm, quantform.QuantVarForm) then
     begin
      Delete(totalterms, i, 1); {should prevent capturing}
      capturing := true;
     end;
   end;
  termForm.DismantleFormula;
  TermsToTry := totalterms;

                {Debugging}
{$IFC myDebugging}
  if FALSE then
   writeln('constantsottry ', totalterms);
{$ENDC}
 end;


*/


boolean prepareQuant(TFormula quantform, TFlag exhausted, TFlag capturing){
  /*
     {This prepares a node for UI or EG. It finds the instantiation constants}
     {the checks whether the formula has been instantiated before}
     {If it has the earlier instantiating constants are checked if there }
     {are no new ones, false is returned.  If there are new ones, they are
     inserted in the stringtable hashmap. And a copy of the quantformula is altered}
     {appropriately.}


  */

 String instants=this.termsToTry(quantform,capturing);

 int length =instants.length();

 if (length==0)
   instants+=quantform.quantVar();  //if none, give it its own variable,
                                    //the subclass does not do this because termsToTry always returns something

 String oldInstants="";

 if (fTreeModel.getOldInstantiations().containsKey(quantform))
   oldInstants=(String)(fTreeModel.getOldInstantiations().get(quantform));

 instants=TUtilities.stringDifference(instants, oldInstants);

 if (instants.length()==0)
   return
       false;
 else{
   fTreeModel.getNewInstantiations().put(quantform,instants);
   return
       true;
 }



}



 /*
  function TTestnode.PrepareQuant (var quantform: TFormula; var exhausted, capturing: boolean): boolean;

  {This prepares a node for UI or EG. It finds the instantiation constants}
  {the checks whether the formula has been instantiated before}
  {If it has the earlier instantiating constants are checked if there }
  {are no new ones, false is returned.  If there are new ones, spare characters}
  {in the info field are used to indicate their number and the index to a list of all}
  {of them as a new insert in the stringtable. And a copy of the quantformula is altered}
  {appropriately.}

    var
     instants, oldinstants: str255;
     lengthInst, newinst, i: integer;
     tempbool: boolean;
     searchStr: string[1];

   begin
    tempbool := false;
    searchStr := 'a';

    instants := SELF.TermsToTry(quantform, capturing);
    lengthInst := length(instants);

    if lengthInst = 0 then {gives it one instantion, its own variable}
     begin
      instants := quantform.QuantVar;
      lengthInst := 1;
     end;

    if (NoOfInstantiations(quantform) = 0) then {not instantiated before}
     tempbool := true
    else { instantiated before}
     begin
      oldInstants := gStringstore[StringStoreIndex(quantform)];

      for i := 1 to length(oldInstants) do   (*removes oldinstants from new instants list*)
       begin
        searchStr[1] := oldInstants[i];
        while pos(searchStr, instants) <> 0 do
         Delete(instants, pos(searchStr, instants), 1);
       end;

      newinst := length(instants);


                { RemoveDuplicates(instants); new instants on tail}

                                                  {$IFC myDebugging}
      if FALSE then
       begin
        writeln('prepare quant after remove instants ', instants);
       end;
  {$ENDC}

      if newinst > 0 then
       begin
        instants := concat(instants, gStringstore[StringStoreIndex(quantform)]);
        lengthInst := newinst;
        tempbool := true;
       end;
     end;

    if tempbool then
     begin
      if gStringtop >= kMaxInst then
       begin
        exhausted := true;
        tempbool := false;
                      {BugAlert('', '', '', 'Too many instantiations of variables.');}
       end
      else
       begin
        gStringstore[gStringtop] := instants;
        gStringtop := gStringtop + 1;
       end;
     end;

    if tempbool then
     begin
      quantform := quantform.CopyFormula;  {check, makes garbage alter copy}

      StoreNoOfInstantiationsAndIndex(quantform, lengthInst, (gStringtop - 1));

                                  {quantform.finfo := concat(Copy(quantform.finfo, 1, 1), twocharStr); }


     end;

                          {$IFC myDebugging}
    if FALSE then
     begin
      writeln('prepare quanto ', quantform.fInfo, 'instants ', instants, 'length ', lengthInst);
      if tempbool then
       writeln('preparequanttrue')
      else
       writeln('preparequant false')

     end;
  {$ENDC}


    PrepareQuant := tempbool;
   end;


*/



static public ArrayList decidableFormulaSatisfiable(TParser aParser,TFormula theFormula){
  /*returns either null or a list of atomic formulas and their negations*/

  /*used by the 'games' */

   TTestNode aTestRoot = new TTestNode(aParser,null);  //does not initialize TreeModel
                                                       // notice not using factory here because static

   TTreeModel aTreeModel= new TTreeModel(aTestRoot.fSwingTreeNode);

   aTestRoot.fTreeModel=aTreeModel;

   aTestRoot.addToAntecedents(theFormula);

   if (aTestRoot.closeSequent()){  //never happens with a single formula!?
    return
        null;                    //not satisfiable
  }


   int answer=    aTestRoot.treeValid(aTreeModel,TTestNode.kMaxTreeDepth);


   if (answer==valid)
     return
         null;
   else{         //in the prop case there should be no 'unknown' this is invalid ie satisfiable
     TTestNode openNode = aTestRoot.aNodeOpen();
     if (openNode == null)
       return
           null; //cannot happen
     else
       return
           openNode.createInterpretationList();
   }
 }



public ArrayList createInterpretationList(){

   /*
     {this takes gOpen Node (DNO'T USE GLOBAL ANY MORE) and makes an interpretationlist of formulas from it}
    {seems ok may 90}

*/
   TFormula nextFormula;

   ArrayList returnList= new ArrayList();


     if (fAntecedents != null) {
         Iterator iter = fAntecedents.iterator();

         while (iter.hasNext()) {
           nextFormula=(TFormula) iter.next();

           switch (nextFormula.fKind){

             case TFormula.unary:
               if (TParser.isPredicator(nextFormula.fRLink))
                 returnList.add(0,nextFormula);
               break;

             case TFormula.predicator:   //{check equality, not used yet}
               returnList.add(0,nextFormula);                          //THE PASCALIS MORE COMPLEX CHECKING PREDICATE

               break;


             default: ;
           }

         }

       }


   TFormula.removeDuplicateFormulas(returnList);


   return
       returnList;

}


/*

    procedure TJournalWindow.CreateInterpretationList;
    {this takes gOpen Node and makes an interpretationlist of formulas from it}
    {seems ok may 90}

      procedure AddToList (item: TObject);

       var
        tempformula: TFormula;
        predCh: CHAR;

       function Equals (item: TObject): boolean;

        var
         found: boolean;

       begin
        found := EqualFormulas(tempformula, TFormula(item));
        Equals := found;
       end;

      begin
       tempformula := TFormula(item);
       case tempformula.fkind of
        unary, predicator, equality: {check equality, not used yet}
         begin
          if tempformula.fKind = unary then
           predCh := tempformula.fRLink.fInfo[1]
          else
           predCh := tempformula.fInfo[1];
          if predCh in gPredicates then
           begin
           if fInterpretationList.FirstThat(Equals) = nil then {no duplicates}
           begin
           tempformula := tempformula.CopyFormula; {for garbage}
           fInterpretationList.InsertFirst(tempformula);
           tempformula := nil;
           end;
           end;
         end;

        otherwise
       end;
      end;

     begin
      DismantleInterpretationList;
      if gOpenNode <> nil then
       begin


        if gOpenNode.fAntecedents.fSize <> 0 then
         gOpenNode.fAntecedents.Each(AddToList);
        if gOpenNode.fSucceedent.fSize <> 0 then
         gOpenNode.fSucceedent.Each(AddToList);  {check this, shouldn't these be negations,}
    {				although this list is always empty}
       end;


     end;


*/



public static String interpretationListToString(ArrayList interpretation){


 //  String outputStr=TFormula.atomicTermsInListOfFormulas(interpretation);
   
   Set <String> outputStrSet=TFormula.atomicTermsInListOfFormulas(interpretation);
   
   String outputStr="";
   
   for (Iterator i=outputStrSet.iterator();i.hasNext();)
	   outputStr+=i.next();

   outputStr=TUtilities.separateStringWithCommas(outputStr);

   String firstStr= "Universe= { "
                    +outputStr
                    + " }"
                    +strCR;

   String secondStr="";

   outputStr=TFormula.trueAtomicFormulasInList(interpretation);

   if (outputStr.length()>0){
     outputStr = TUtilities.separateStringWithCommas(outputStr);
     secondStr="True Propositions= { "
                                     + outputStr
                                     + " }"
                                     + strCR;
   }

   String thirdStr="";

   outputStr=TFormula.falseAtomicFormulasInList(interpretation);

   if (outputStr.length()>0){
     outputStr = TUtilities.separateStringWithCommas(outputStr);
     thirdStr="False Propositions= { "
                                     + outputStr
                                     + " }"
                                     + strCR;
   }


String fourthStr="";

int length = TParser.gPredicates.length();
String predicate;

int count=0;

for (int i=0;i<length;i++){
  predicate=TParser.gPredicates.substring(i,i+1);

  outputStr=TFormula.extensionOfUnaryPredicate(interpretation, predicate);

  if ((outputStr!=null)&&(outputStr.length()>0)){
     count+=1;
     outputStr = TUtilities.separateStringWithCommas(outputStr);
     fourthStr+=predicate
                                     + " = { "
                                     + outputStr
                                     + " }"
                                     + strCR;
  }
}



String fifthStr="";

count=0;

for (int i=0;i<length;i++){
  predicate=TParser.gPredicates.substring(i,i+1);

  outputStr=TFormula.extensionOfBinaryPredicate(interpretation, predicate);

  if ((outputStr!=null)&&(outputStr.length()>0)){
    count+=1;
     outputStr = TUtilities.intoOrderedPairs(outputStr);
     fifthStr+=predicate
                                     + " = { "
                                     + outputStr
                                     + " }"
                                     + strCR;
  }

}




// NEED THE EXTRA BIT HERE ABOUT CYCLES



    return
        firstStr
                                   +secondStr
                                   +thirdStr
                                   +fourthStr
                                   +fifthStr;



        }









boolean deriveIfClause(TFormula arrow){ // June 2010 hack to preserv heap
	return
	
	false;
}



boolean deriveIfClauseCommentedOut(TFormula arrow){

  /*{This is test that permits arrow elim (A->B) only if we have the if clause ie A}.

   But when we are trying to derive A, and we encounter say D->F we don't then want to
   see if we can derive D.

   In Pascal, we used gMaxrecurse to sort this out. Here we just put a flag
   fRecurse on TTestNode*/

 
	
	
	
	TTestNode test =this.copyNodeInFullWithInstInfo(); //node, formulas, and context

   /*This might look different, but in the old days the context went withe the formula
   now it needs expplicit copying*/


   test.fRecurse=false;              // don't recurse

 /*  int index =test.fAntecedents.indexOf(arrow);
   test.fAntecedents.remove(index);     */

   for(int i=test.fAntecedents.size()-1;i>-1;i--){
     TFormula testFormula = (TFormula) fAntecedents.get(i); //remove the copy of the arrow
     if (TFormula.equalFormulas(testFormula,arrow))
       test.fAntecedents.remove(i);
   }

   test.fSuccedent.clear();
   test.fSuccedent.add(arrow.fLLink.copyFormula());        // the target is A

   TTreeModel aTreeModel= new TTreeModel(test.fSwingTreeNode);
   test.fTreeModel=aTreeModel;                             //  new context

    //startSatisfactionTree??

   if (test.treeValid(aTreeModel,5)==valid)      //only do 5 steps
	   {test=null;   //june 10 unsure about freeing heap space
	   aTreeModel=null;
      return
         true;}
else
	{test=null;   //june 10 unsure about freeing heap space
	aTreeModel=null;
	return
		false;}
}

/*

 function DeriveIfClause: boolean;
      {This is test that permits arrow elim only if we have the if clause}

    var
     ifDerivable, tempexCV, tempexCVflag, tempuniCV, tempuniCVflag: boolean;
     temptest: TTestnode;
     ifFormula, implicFormula: TFormula;

    function EqualsImplic (item: TObject): boolean;

    begin
     EqualsImplic := EqualFormulas(implicFormula, TFormula(item));
    end;

    function EqualsIfClause (item: TObject): boolean;
    begin
     EqualsIfClause := EqualFormulas(implicFormula.fLlink, TFormula(item));
    end;

    procedure AssembleFirstTest; {same as other}
           {This is a test of whether we can prove A from the rest of the premises}
           {without (A>B) }
     var
      garbageformula: TFormula;

    begin
     temptest := nil;
     temptest := SELF.CopyNodeinFull;

     with temptest do
      begin
       fSteptype := unknown;
       fDead := false; {must reset it for a new test}
       fClosed := false;
       fLlink := nil;
       fRlink := nil;
      end;

     garbageFormula := TFormula(temptest.fAntecedents.FirstThat(EqualsImplic));

     temptest.fAntecedents.Delete(garbageFormula);

     garbageFormula.DismantleFormula;

     ifFormula := implicFormula.fLlink.CopyFormula; {A}

     if temptest.fSucceedent.fSize > 0 then
      begin

       garbageFormula := TFormula(temptest.fSucceedent.First);
       garbageFormula.DismantleFormula;
      end;

     temptest.fSucceedent.DeleteAll;
     temptest.fSucceedent.InsertFirst(ifFormula);
     ifFormula := nil;

    end;

   begin
    ifDerivable := false;
                {just checking whether if-clause is present, immediately}

    implicFormula := testformula; {A>B set by ArrowItem}

    if fAntecedents.FirstThat(EqualsIfClause) <> nil then
     ifDerivable := true;
                {looking in more depth, but not recursing}

    if (gMaxrecurse > 0) and not ifDerivable then
     begin
      gMaxrecurse := gMaxrecurse - 1; {do not wish to recurse with this}
      tempexCV := gExCV; {do not wish to changethese}
      tempexCVflag := gExCVflag;
      tempUniCV := gUniCV; {do not wish to changethese}
      tempUniCVflag := gUniCVflag;

      AssembleFirstTest;

      if temptest.TreeValid(5) = valid then {5 is a guess}
       ifDerivable := true;

      DismantleTestTree(temptest);

      gMaxrecurse := gMaxrecurse + 1;
      gExCV := tempexCV; {returns to former state}
      gExCVflag := tempexCVflag;
      gUniCV := tempUniCV; {returns to former state}
      gUniCVflag := tempUniCVflag;
     end;
    DeriveIfClause := ifDerivable;
   end;


*/


/* June 10. kill calls extend which does one step, then calls kill on its
 * children with stopDepth decremented.
 */


boolean kill(int stopDepth){
  /*{extends entire tree to completion, if possible}*/
  TFlag exhausted=new TFlag(false);
  
  if (stopDepth<1)
	  return
	    false;   //we've run out of steps

  if (!fDead){
    fStepsToExpiry--;
    extend(exhausted);
  }

  if ((stopDepth>0)&&fStepsToExpiry>0){

    int numChildren = fSwingTreeNode.getChildCount();
    boolean childrenKilled=true;

    if (numChildren > 0) {
      DefaultMutableTreeNode leftChild = (DefaultMutableTreeNode) fSwingTreeNode.
          getChildAt(0);
      childrenKilled=((TTestNode) (leftChild.getUserObject())).kill(stopDepth-1);

      if ((childrenKilled)&&(numChildren > 1)) {      // added the && children killed
        DefaultMutableTreeNode rightChild = (DefaultMutableTreeNode)
            fSwingTreeNode.getChildAt(1);
        childrenKilled=((TTestNode) (rightChild.getUserObject())).kill(stopDepth-1);
      }
    }
    return
        childrenKilled;
  }

  return
      false;          // expired, no more steps permitted
}


/*


 procedure TTestnode.Kill (var killed: boolean; var treesize: integer);

 {extends entire tree to completion, if possible}

   var
    exhausted: boolean;

  begin
   exhausted := false;

   while EventAvail(keyDownMask, gEvent) and (treesize < kMaxtreesize) and not gBaleOutFlag do
    begin
     gEvent.Message := BitAnd(gEvent.Message, $FF); {charcode mask}
     if (BitAnd(gEvent.Modifiers, CmdKey) = CmdKey) then
      begin
       gBaleOutFlag := true;
       treesize := kMaxtreesize;
      end
     else if GetNextEvent(keyDownMask, gEvent) then
      ; {removes it from queue}
    end;

   treesize := treesize + 1;

   if treesize <= kMaxtreesize then
    begin

     if not fDead then
      SELF.Extend(exhausted);

     if exhausted then
      begin
       treesize := kMaxtreesize + 1
      end
     else
      begin
       if (fLlink <> nil) then
        fLlink.Kill(killed, treesize);
       if (fRlink <> nil) then
        fRlink.Kill(killed, treesize);
      end;
    end;
  end;



*/




void extend(TFlag exhausted){
/*  {This forms a binary tree of sequences of formulas} */

   TTestNode ltemp,rtemp;
   TFormula newFormula, secondNewFormula,tempFormula;


  if (closeSequent())
    fDead=true;
  else{

    /*

      {The idea is to take a copy of the node. This copies the node and its formula list}
{but does not copy the formulas.  This copy will eventually be the left link}
{Then use FindFirst to see if there is an extendable formula}
 {in the copy. If there is, a second copy is taken to form the right link}
 {Then the extendable formula is copied then changed to form the product parts so that}
{the copied node becomes the next node; the type of change gets entered on the}
  {parent by First Formula and First Formula also brings the extendable formula}
  {to the head of the list}

FindFirst brings the extendable formula to the head of ltemp, not to the head of the node
itself.


    June 04-- if you understand that you're a better man (person) than I


   */

     fStepType=unknown;

     ltemp=this.copyNode();

     TFormula.removeDuplicateFormulas(ltemp.fAntecedents);
     TFormula.removeDuplicateFormulas(ltemp.fSuccedent);

     fStepType=ltemp.findFirst(exhausted);

     switch (fStepType) {


        case aand:
          newFormula = ((TFormula)ltemp.fAntecedents.get(0)).fLLink.copyFormula();
          secondNewFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.copyFormula();

          ltemp.fAntecedents.remove(0);   // the and

          ltemp.fAntecedents.add(0,secondNewFormula);
          ltemp.fAntecedents.add(0,newFormula);

          straightInsert(ltemp,null);

          break;

        case aandS:

          newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fLLink.copyFormula();
          secondNewFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();

          ltemp.fSuccedent.remove(0);
          rtemp = ltemp.copyNode();

          ltemp.fSuccedent.add(0,newFormula);
          rtemp.fSuccedent.add(0,secondNewFormula);

          splitInsert(ltemp,rtemp);

          break;

        /*
          aandS:
       begin
       newformula := TFormula(ltemp.fSucceedent.First).fLlink.CopyFormula;
       secondnewformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;

       ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
       ltemp.fSucceedent.InsertFirst(newformula);
       newformula := nil;

       rtemp.fSucceedent.Delete(rtemp.fSucceedent.First);
       rtemp.fSucceedent.InsertFirst(secondnewformula);
       secondnewformula := nil;

       SplitInsert;
       end;


   */

        case implic:   //{A>B}

           tempFormula = ((TFormula)ltemp.fAntecedents.get(0)).fLLink.copyFormula();  // going to be ~A

           newFormula = new TFormula();

           newFormula.fKind = TFormula.unary;
           newFormula.fInfo = String.valueOf(chNeg);
           newFormula.fRLink = tempFormula;

           tempFormula=null;

           secondNewFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.copyFormula();  // B

           ltemp.fAntecedents.remove(0);   // the arrow
           rtemp = ltemp.copyNode();

           ltemp.fAntecedents.add(0,newFormula);
           rtemp.fAntecedents.add(0,secondNewFormula);

           splitInsert(ltemp,rtemp);

           break;



           /*

               aroww: {A>B}

                  begin
                  secondnewformula := TFormula(ltemp.fAntecedents.First).fLlink.CopyFormula; {~A}

                  New(newformula);
                  FailNIL(newformula);
                  newformula.IFormula;

                  with newformula do
                  begin
                  fkind := unary;
                  finfo := chNeg;
                  fRlink := secondnewformula;
                  end;

                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
                  ltemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;
                  secondnewformula := nil; {notA}

                  newformula := TFormula(rtemp.fAntecedents.First).fRlink.CopyFormula;
                  rtemp.fAntecedents.Delete(rtemp.fAntecedents.First);
                  rtemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil; {B}

                  SplitInsert;
                  end;

   */

        case arrowS:
          newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fLLink.copyFormula();  // the A
          secondNewFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();  //the B

          ltemp.fSuccedent.remove(0);   // the arrow

          ltemp.fSuccedent.add(0,secondNewFormula);  // the B
          ltemp.fAntecedents.add(0,newFormula);      // the A

          straightInsert(ltemp,null);


           break;

        /*

            arrowS:
                begin
                newformula := TFormula(ltemp.fSucceedent.First).fLlink.CopyFormula;

                ltemp.fAntecedents.InsertFirst(newformula);
                newformula := nil;
                secondnewformula := nil; {A}

                newformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;

                ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
                ltemp.fSucceedent.InsertFirst(newformula);
                newformula := nil; {B}

                rtemp.DismantleTestNode;
                rtemp := nil;
                StraightInsert;
                end;


   */



       case doubleneg:
          newFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fRLink.copyFormula();

          ltemp.fAntecedents.remove(0);   // the doubleneg

          ltemp.fAntecedents.add(0,newFormula);

          straightInsert(ltemp,null);


          break;
          /*
                doubleneg:
                  begin
                  newformula := TFormula(ltemp.fAntecedents.First).fRlink.fRlink.CopyFormula;
                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
                  ltemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;

                  rtemp.DismantleTestNode;
                  rtemp := nil;
                  StraightInsert;
                  end;

    */

        case doublenegS:

          newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.fRLink.copyFormula();

          ltemp.fSuccedent.remove(0);   // no conclusion, reductio

          ltemp.fSuccedent.add(0,newFormula);

          straightInsert(ltemp,null);


          break;

          /*
                doublenegS:
                  begin
                  newformula := TFormula(ltemp.fSucceedent.First).fRlink.fRlink.CopyFormula;
                  ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
                  ltemp.fSucceedent.InsertFirst(newformula);
                  newformula := nil;

                  rtemp.DismantleTestNode;
                  rtemp := nil;
                  StraightInsert;
                  end;


     */

        case equivv:

          newFormula = ((TFormula)ltemp.fAntecedents.get(0)).copyFormula();
          newFormula.fInfo=String.valueOf(chImplic); // change to A->B

          secondNewFormula= new TFormula();

          secondNewFormula.fKind = TFormula.binary;
          secondNewFormula.fInfo = String.valueOf(chImplic);
          secondNewFormula.fLLink = newFormula.fRLink.copyFormula(); //{B}
          secondNewFormula.fRLink = newFormula.fLLink.copyFormula(); //{A}

          ltemp.fAntecedents.remove(0);   // the implic

          ltemp.fAntecedents.add(0,secondNewFormula);
          ltemp.fAntecedents.add(0,newFormula);

          straightInsert(ltemp,null);


          break;




       /*

          equivv:
               begin
               newformula := TFormula(ltemp.fAntecedents.First).CopyFormula;
               newformula.finfo := chImplic; {chagne to A > B }

               New(secondnewformula);
               FailNIL(secondnewformula);
               secondnewformula.IFormula;
               with secondnewformula do
               begin
               fkind := binary;
               finfo := chImplic;
               fLlink := newformula.fRlink.CopyFormula; {B}
               fRlink := newformula.fLlink.CopyFormula; {A}
               end;

               ltemp.fAntecedents.Delete(ltemp.fAntecedents.First); {equivv}
               ltemp.fAntecedents.InsertFirst(secondnewformula);
               ltemp.fAntecedents.InsertFirst(newformula);
               newformula := nil;
               secondnewformula := nil;

               rtemp.DismantleTestNode;
               rtemp := nil;
               StraightInsert;
               end;


    }

   */

   case equivvS:   //{A>B}

         newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fLLink.copyFormula();  // A
         secondNewFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();  // B

         ltemp.fAntecedents.add(0,newFormula);
         ltemp.fSuccedent.remove(0);
         ltemp.fSuccedent.add(0,secondNewFormula);

         rtemp = ltemp.copyNode();

         rtemp.fAntecedents.remove(0);          // the A
         rtemp.fAntecedents.add(0,secondNewFormula.copyFormula());  //B

         rtemp.fSuccedent.remove(0);     //B
         rtemp.fSuccedent.add(0,newFormula.copyFormula());


         splitInsert(ltemp,rtemp);

         break;



  /*
          equivvS:
       begin
       newformula := TFormula(ltemp.fSucceedent.First).fLlink.CopyFormula; {A}
       secondnewformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula; {B}

       ltemp.fAntecedents.InsertFirst(newformula);
       ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
       ltemp.fSucceedent.InsertFirst(secondnewformula);

       newformula := nil;
       secondnewformula := nil;

       newformula := TFormula(rtemp.fSucceedent.First).fRlink.CopyFormula;
       secondnewformula := TFormula(rtemp.fSucceedent.First).fLlink.CopyFormula;

       rtemp.fAntecedents.InsertFirst(newformula);
       rtemp.fSucceedent.Delete(rtemp.fSucceedent.First);
       rtemp.fSucceedent.InsertFirst(secondnewformula);

       SplitInsert;

       end;


          */

        case exi:


          doExi(ltemp);

       /*  newFormula = ((TFormula)ltemp.fAntecedents.get(0)).scope().copyFormula();

         // secondNewFormula= new TFormula();    //use quantvarform?

         secondNewFormula= ((TFormula)ltemp.fAntecedents.get(0)).quantVarForm().copyFormula();


          ltemp.fAntecedents.remove(0);   // the exi

          ltemp.fAntecedents.add(0,secondNewFormula);  // the variable
          ltemp.fAntecedents.add(0,newFormula);  // he scope

          straightInsert(ltemp,null);  */

          break;

          /*

                exi: { x is not free in ante or succ}
                  begin
                  New(secondnewformula);
                  FailNIL(secondnewformula);
                  secondnewformula.IFormula;
                  secondnewformula.fKind := functor;
                  secondnewformula.finfo := TFormula(ltemp.fAntecedents.First).QuantVar;

                  newformula := TFormula(ltemp.fAntecedents.First).fRlink.CopyFormula;
                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);

                  ltemp.fAntecedents.InsertFirst(secondnewformula);
                  ltemp.fAntecedents.InsertFirst(newformula);

                  rtemp.DismantleTestNode;
                  rtemp := nil;
                  StraightInsert;
                  end;


     */

      case typedExi:


        TFormula typedExiQuant=(TFormula)(ltemp.fAntecedents.get(0));

        TFormula plainExiQuant=fParser.expandTypeExi(typedExiQuant);

        ltemp.fAntecedents.remove(0);   // the typed exiquant

        ltemp.fAntecedents.add(0,plainExiQuant);  //append

        straightInsert(ltemp,null);

        break;



        case  exiCV:  //{to cope with x free in ante or succ}

          TFormula newVar,oldVar,exiQuant,scope;

          exiQuant = ((TFormula)ltemp.fAntecedents.get(0)); //eg (Ex)Fx don't copy here because it is the key to the store


          oldVar=exiQuant.quantVarForm();                   //eg x
          scope=exiQuant.scope();

          String newInfo=null;

          if (fTreeModel.getNewInstantiations().containsKey(exiQuant)){
            newInfo = (String) (fTreeModel.getNewInstantiations().get(exiQuant));
          }

          if (newInfo!=null){

            newVar = new TFormula(TFormula.functor,newInfo,null,null);

            TFormula.subTermVar(scope, newVar, oldVar);         // eg Fy


            ltemp.fAntecedents.remove(0); // the exi

            ltemp.fAntecedents.add(0, oldVar); // the variable
            ltemp.fAntecedents.add(0, newVar);
            ltemp.fAntecedents.add(0, scope);

            straightInsert(ltemp, null);

            System.out.print("exiCV called in extend");
          }
          break;

          /*

                exiCV: {to cope with x free in ante or succ}
                  begin
                  newformula := TFormula(ltemp.fAntecedents.First);  {copied in find first?}   eg (Ex)Fx
                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First); {removes first}

                  tempInfo := newformula.finfo;                                                eg x

                  oldCh := newformula.QuantVar;
                  newCh := NewVariable(newformula); {supplied by FindFirst}                    eg y

                  New(secondnewformula);
                  FailNIL(secondnewformula);
                  secondnewformula.IFormula;
                  secondnewformula.fKind := variable;
                  secondnewformula.finfo := oldCh;

                  oldVarForm := secondnewformula;                                             eg variable x

                  {Put in a dummy containing only information on old quantified variable}

                  ltemp.fAntecedents.InsertFirst(secondnewformula); {to be third}

                  secondnewformula := nil;

                  New(secondnewformula);
                  FailNIL(secondnewformula);
                  secondnewformula.IFormula;
                  secondnewformula.fKind := variable;
                  secondnewformula.finfo := newCh;

                  newVarForm := secondnewformula;

                  {Put in a dummy containing only information on new quantified variable}

                  ltemp.fAntecedents.InsertFirst(secondnewformula); {to be second}            eg variable y

                  secondnewformula := nil;

                                         {inserted second }

                  garbageFormula := newformula;

                  newformula := newformula.fRlink; {already copied in FindFirst}              eg old scope Fx

                  garbageFormula.fRlink := nil;
                  garbageFormula.DismantleFormula;  {the exiquant}

                  newvarForm := newvarForm.CopyFormula; {12/15/90}

                  NewSubTermVar(newformula, newvarForm, oldVarForm);

                  ltemp.fAntecedents.InsertFirst(newformula); {to be first}                   eg scope with new var Fy

                  newformula := nil;

                                         {inserted first }

                  rtemp.DismantleTestNode;
                  rtemp := nil;
                  StraightInsert;
                  end;


     */

  case  exiS:  //{FindFirst does this}
   // fLLink = ltemp;   //{puts in entire sub-tree}
      straightInsert(ltemp,null);


        break;


    /*
          exiS:
       begin {FindFirst does this}
       SELF.fLlink := ltemp;   {puts in entire sub-tree}
       SELF.fRlink := nil;
       rtemp.DismantleTestNode;
       rtemp := nil;
       end;


          */


       case typedExiS:

      typedExiQuant=(TFormula)(ltemp.fSuccedent.get(0));

      exiQuant=fParser.expandTypeExi(typedExiQuant);

       ltemp.fSuccedent.remove(0);   // the typed uniquant

       ltemp.fSuccedent.add(0,exiQuant);  //append

       straightInsert(ltemp,null);

            break;






       case negand:

           tempFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fLLink.copyFormula();  // going to be ~A

           newFormula = new TFormula();

           newFormula.fKind = TFormula.unary;
           newFormula.fInfo = String.valueOf(chNeg);
           newFormula.fRLink = tempFormula;

           tempFormula=null;

           tempFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fRLink.copyFormula();  // going to be ~B

           secondNewFormula = new TFormula();

           secondNewFormula.fKind = TFormula.unary;
           secondNewFormula.fInfo = String.valueOf(chNeg);
           secondNewFormula.fRLink = tempFormula;

           tempFormula=null;

           ltemp.fAntecedents.remove(0);   // the negand
           rtemp = ltemp.copyNode();

           ltemp.fAntecedents.add(0,newFormula);
           rtemp.fAntecedents.add(0,secondNewFormula);

           splitInsert(ltemp,rtemp);


          break;

          /*
                negand:
                  begin
                  secondnewformula := TFormula(ltemp.fAntecedents.First).fRlink.fLlink.CopyFormula;

                  New(newformula);
                  FailNIL(newformula);
                  newformula.IFormula;

                  with newformula do
                  begin
                  fkind := unary;
                  finfo := chNeg;
                  fRlink := secondnewformula;
                  end;

                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
                  ltemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;
                  secondnewformula := nil; {notA}

                  secondnewformula := TFormula(rtemp.fAntecedents.First).fRlink.fRlink.CopyFormula;

                  New(newformula);
                  FailNIL(newformula);
                  newformula.IFormula;

                  with newformula do
                  begin
                  fkind := unary;
                  finfo := chNeg;
                  fRlink := secondnewformula;
                  end;

                  rtemp.fAntecedents.Delete(rtemp.fAntecedents.First);
                  rtemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;
                  secondnewformula := nil;

                  SplitInsert;
                  end;


     */




  case negarrow:

         newFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fLLink.copyFormula();  // A

         {TFormula B = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fRLink.copyFormula();  // going to be ~B

         secondNewFormula = new TFormula();

         secondNewFormula.fKind = TFormula.unary;
         secondNewFormula.fInfo = String.valueOf(chNeg);
         secondNewFormula.fRLink = B;}


         ltemp.fAntecedents.remove(0);   // the negand

         ltemp.fAntecedents.add(0,secondNewFormula);
         ltemp.fAntecedents.add(0,newFormula);


         straightInsert(ltemp,null);


        break;

  /*
        negarrow:
       begin
       newformula := TFormula(ltemp.fAntecedents.First).fRlink.fLlink.CopyFormula; {if clause}

       ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
       ltemp.fAntecedents.InsertFirst(newformula);
       newformula := nil; {A}

       secondnewformula := TFormula(rtemp.fAntecedents.First).fRlink.fRlink.CopyFormula; {then clause}

       New(newformula);
       FailNIL(newformula);
       newformula.IFormula;

       with newformula do
       begin
       fkind := unary;
       finfo := chNeg;
       fRlink := secondnewformula; {not B}
       end;

       ltemp.fAntecedents.InsertBefore(2, newformula);
       newformula := nil;
       secondnewformula := nil;

       rtemp.DismantleTestNode;
       rtemp := nil;
       StraightInsert;
       end;


        */

     case negexi: //{want to change not is into all not}

            TFormula uniFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.copyFormula();  // Ex

            uniFormula.fInfo=String.valueOf(chUniquant); //changed exiquant to uniquant

            uniFormula.fRLink=new TFormula(TFormula.unary,
                                           String.valueOf(chNeg),
                                           null,
                                           uniFormula.fRLink);   //negated scope

            ltemp.fAntecedents.remove(0);   // the negexi

            ltemp.fAntecedents.add(0,uniFormula);

            straightInsert(ltemp,null);

    break;

/*
         negexi: {want to change not is into all not}

              begin

              newformula := TFormula(ltemp.fAntecedents.First).CopyFormula;

              secondnewformula := newformula.fRlink; { ex}

              newformula.fRlink := newformula.fRlink.fRlink; {not scope}

              secondnewformula.fRlink := newformula; {ex not scope}

              newformula := secondnewformula;

              secondnewformula := nil;

              newformula.finfo[1] := chUniquant;

              ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);

              ltemp.fAntecedents.InsertFirst(newformula);
              newformula := nil;

              rtemp.DismantleTestNode;
              rtemp := nil;
              StraightInsert;
              end;


  */

  case negTypedExi:
   TFormula negTypedExiQuant=(TFormula)(ltemp.fAntecedents.get(0));

   TFormula negExiQuant;

   negExiQuant=fParser.expandTypeExi(negTypedExiQuant.fRLink);

   negExiQuant = new TFormula(TFormula.unary,
                     String.valueOf(chNeg),
                     null,
                     negExiQuant);

     ltemp.fAntecedents.remove(0);   // the negtyped uniquant

     ltemp.fAntecedents.add(0,negExiQuant);  //append

     straightInsert(ltemp,null);

          break;


  case neguni: //{want to change not all into is not }

             TFormula exiFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.copyFormula();  // Ex

             exiFormula.fInfo=String.valueOf(chExiquant); //changed uniquant to  exiquant

             exiFormula.fRLink=new TFormula(TFormula.unary,
                                            String.valueOf(chNeg),
                                            null,
                                            exiFormula.fRLink);   //negated scope, thus is not

             ltemp.fAntecedents.remove(0);   // the negexi

             ltemp.fAntecedents.add(0,exiFormula);

             straightInsert(ltemp,null);

     break;



 /*
   negUni: {want to change not all into is not}

       begin

       newformula := TFormula(ltemp.fAntecedents.First).CopyFormula;

       secondnewformula := newformula.fRlink; { uni}

       newformula.fRlink := newformula.fRlink.fRlink; {not scope}

       secondnewformula.fRlink := newformula; {ex not scope}

       newformula := secondnewformula;

       secondnewformula := nil;

       newformula.finfo[1] := chExiquant;

       ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);

       ltemp.fAntecedents.InsertFirst(newformula);
       newformula := nil;

       rtemp.DismantleTestNode;
       rtemp := nil;
       StraightInsert;
       end;

    */


 case negTypedUni:
   TFormula negTypedUniQuant=(TFormula)(ltemp.fAntecedents.get(0));

   TFormula negUniQuant;

   negUniQuant=fParser.expandTypeUni(negTypedUniQuant.fRLink);

   negUniQuant = new TFormula(TFormula.unary,
                     String.valueOf(chNeg),
                     null,
                     negUniQuant);

     ltemp.fAntecedents.remove(0);   // the negtyped uniquant

     ltemp.fAntecedents.add(0,negUniQuant);  //append

     straightInsert(ltemp,null);

          break;



 /*

    */

  case nore:

       tempFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fLLink.copyFormula();  // A

       newFormula = new TFormula();       // going to be ~A

       newFormula.fKind = TFormula.unary;
       newFormula.fInfo = String.valueOf(chNeg);
       newFormula.fRLink = tempFormula;


       tempFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fRLink.copyFormula();  // going to be ~B

       secondNewFormula = new TFormula();

       secondNewFormula.fKind = TFormula.unary;
       secondNewFormula.fInfo = String.valueOf(chNeg);
       secondNewFormula.fRLink = tempFormula;


       ltemp.fAntecedents.remove(0);   // the negand

       ltemp.fAntecedents.add(0,secondNewFormula);
       ltemp.fAntecedents.add(0,newFormula);


       straightInsert(ltemp,null);





    break;



        case negatomicS:
        case negandS:
        case noreS:
        case negarrowS:
        case nequivS:
        case neguniS:
        case negexiS :
        case negTypedUniS:
        case negTypedExiS :

          newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();

          ltemp.fSuccedent.remove(0);   // no conclusion, reductio

          ltemp.fAntecedents.add(0,newFormula);

          straightInsert(ltemp,null);


         break;

         /*

           case SELF.fSteptype of

                 negatomicS, negandS, noreS, negarrowS, nequivS, negUniS, negExiS:
                 begin
                 newformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;
                 ltemp.fSucceedent.DeleteAll; {no conclusion-- reductio}

                 ltemp.fAntecedents.InsertFirst(newformula);
                 newformula := nil;

                 rtemp.DismantleTestNode;
                 rtemp := nil;
                 StraightInsert;
                 end;


    */

       case nequiv:   // heading for not((A->B)&(B->A))

     TFormula A= ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fLLink.copyFormula();
     TFormula B= ((TFormula)ltemp.fAntecedents.get(0)).fRLink.fRLink.copyFormula();

     TFormula AarrowB= new TFormula();

     AarrowB.fKind = TFormula.binary;
     AarrowB.fInfo = String.valueOf(chImplic);
     AarrowB.fLLink = A;
     AarrowB.fRLink = B;

     TFormula BarrowA= new TFormula();

     BarrowA.fKind = TFormula.binary;
     BarrowA.fInfo = String.valueOf(chImplic);
     BarrowA.fLLink = B.copyFormula();
     BarrowA.fRLink = A.copyFormula();

     TFormula and = new TFormula();

     and.fKind = TFormula.binary;
     and.fInfo = String.valueOf(chAnd);
     and.fLLink = AarrowB;
     and.fRLink = BarrowA;


     newFormula = new TFormula();

     newFormula.fKind = TFormula.unary;
     newFormula.fInfo = String.valueOf(chNeg);
     newFormula.fRLink = and;

     ltemp.fAntecedents.remove(0);   // the nequiv

     ltemp.fAntecedents.add(0,newFormula);

     straightInsert(ltemp,null);



         break;

         /*

              nequiv:
                 begin

                 New(secondnewformula);
                 FailNIL(secondnewformula);
                 secondnewformula.IFormula;
                 with secondnewformula do
                 begin
                 fkind := binary;
                 finfo := chAnd;
                 fLlink := TFormula(ltemp.fAntecedents.First).fRlink.CopyFormula;{A=B}
                 fRlink := TFormula(ltemp.fAntecedents.First).fRlink.CopyFormula;{A=B}
                 end;

                 secondnewformula.fLlink.finfo := chImplic; {A>B}
                 secondnewformula.fRlink.finfo := chImplic; {A>B}

                 newformula := secondnewformula.fRlink.fLlink;
                 secondnewformula.fRlink.fLlink := secondnewformula.fRlink.fRlink;
                 secondnewformula.fRlink.fRlink := newformula;
                 newformula := nil; {B>A}

                 New(newformula);
                 FailNIL(secondnewformula);
                 newformula.IFormula;

                 with newformula do
                 begin
                 fkind := unary;
                 finfo := chNeg;
                 fRlink := secondnewformula; {not A>BandB>A}
                 end;

                 ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
                 ltemp.fAntecedents.InsertFirst(newformula);
                 newformula := nil;
                 secondnewformula := nil;

                 rtemp.DismantleTestNode;
                 rtemp := nil;
                 StraightInsert;
                 end;



    */



        case uni:

          /*{FindFirst brings a Copy of uniquant to head and fills in details of the
    required new instantiations in the newInstantiations hash map [Actually I don't think
    it takes a copy any more, but this does not matter].

    This inserts all the instantiated formulas. Copies the uniquant, gets the old instantiations from
    the original appends the new instants puts that in the old instants hashmap for the copy.
    Moves the copy of the uniquant to the end of the list forcing Find First }
                                        {to instantiate other uniquants before coming here again}*/

    TFormula uniQuant=(TFormula)(ltemp.fAntecedents.get(0));
    String oldInstants="";

    if (fTreeModel.getOldInstantiations().containsKey(uniQuant))
       oldInstants=(String)(fTreeModel.getOldInstantiations().get(uniQuant));

    String newInstants="";

    if (fTreeModel.getNewInstantiations().containsKey(uniQuant))
       newInstants=(String)(fTreeModel.getNewInstantiations().get(uniQuant));  //actually, findFirst() guarantees them

     ltemp.fAntecedents.remove(0);   // the uniquant

     uniQuant=uniQuant.copyFormula();  //copy

     fTreeModel.getOldInstantiations().put(uniQuant,oldInstants+newInstants);  // add its instantiation info

     ltemp.fAntecedents.add(uniQuant);  //append

     TFormula variForm=uniQuant.quantVarForm();
     TFormula termForm;
     TFormula newInstance;

     for (int i=0;i<newInstants.length();i++){

       termForm=new TFormula(TFormula.functor,newInstants.substring(i,i+1),null,null);

       newInstance=uniQuant.scope().copyFormula();

       TFormula.subTermVar(newInstance, termForm, variForm);

       ltemp.fAntecedents.add(0,newInstance);  // put it first

     }

     straightInsert(ltemp,null);

    break;

 /*
  uni:
         begin
                  {FindFirst brings a Copy of uniquant to head and fills in details of instantiations}
               {number required at info2 and index to string at info3 This inserts the instantiations}
                    {2, 3, 4, etc. then moves the uniquant to the end of the list forcing Find First }
                                {to instantiate other uniquants before coming here again}

         tempInfo := TFormula(ltemp.fAntecedents.First).finfo;
         variCh := TFormula(ltemp.fAntecedents.First).QuantVar;

         variForm := TFormula(ltemp.fAntecedents.First).QuantVarForm;
         supplyFormula(termForm);
         termForm.fkind := functor;


                               { variable := tempInfo[2]; }
         numberOfInstantiations := NoOfInstantiations(TFormula(ltemp.fAntecedents.First));
         storeIndex := StringStoreIndex(TFormula(ltemp.fAntecedents.First));



         secondnewformula := TFormula(ltemp.fAntecedents.First); {moves}
  {                                   uniquant to end}
         ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
         ltemp.fAntecedents.InsertLast(secondnewformula);
         secondnewformula := nil;

         for j := 1 to numberOfInstantiations do
         begin
         newformula := TFormula(rtemp.fAntecedents.First).fRlink.CopyFormula; {scope}

         termForm.fInfo := gStringstore[storeIndex][j];

         NewSubTermVar(newformula, termForm, variForm);

         ltemp.fAntecedents.InsertFirst(newformula);
         newformula := nil;
         end;

         termform.DismantleFormula;
         rtemp.DismantleTestNode;
         rtemp := nil;
         StraightInsert;
         end;

          */



       case typedUni:


          TFormula typedUniQuant=(TFormula)(ltemp.fAntecedents.get(0));

          uniQuant=fParser.expandTypeUni(typedUniQuant);

           ltemp.fAntecedents.remove(0);   // the typed uniquant

           ltemp.fAntecedents.add(0,uniQuant);  //append

           straightInsert(ltemp,null);

          break;








       case ore:

           newFormula = ((TFormula)ltemp.fAntecedents.get(0)).fLLink.copyFormula();  //  A

           secondNewFormula = ((TFormula)ltemp.fAntecedents.get(0)).fRLink.copyFormula();  // B

           ltemp.fAntecedents.remove(0);   // the or
           rtemp = ltemp.copyNode();

           ltemp.fAntecedents.add(0,newFormula);
           rtemp.fAntecedents.add(0,secondNewFormula);

           splitInsert(ltemp,rtemp);


          break;

          /*
             ore:
                  begin
                  newformula := TFormula(ltemp.fAntecedents.First).fLlink.CopyFormula;
                  ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
                  ltemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;
                                         {A}
                  newformula := TFormula(rtemp.fAntecedents.First).fRlink.CopyFormula;
                  rtemp.fAntecedents.Delete(rtemp.fAntecedents.First);
                  rtemp.fAntecedents.InsertFirst(newformula);
                  newformula := nil;
                                         {B}
                  SplitInsert;
                  end;

   */
       case oreS:

          TFormula temp  = ((TFormula)ltemp.fSuccedent.get(0)).fLLink.copyFormula();

          newFormula = new TFormula();
          newFormula.fKind=TFormula.unary;
          newFormula.fInfo=String.valueOf(chNeg);
          newFormula.fRLink=temp;

          ltemp.fAntecedents.add(0,newFormula);  // not A

          secondNewFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();

          ltemp.fSuccedent.remove(0);   // the or

          ltemp.fSuccedent.add(0,secondNewFormula);  // the B

          straightInsert(ltemp,null);


         break;

         /*oreS:
       begin
       secondnewformula := TFormula(ltemp.fSucceedent.First).fLlink.CopyFormula;

       New(newformula);
       FailNIL(newformula);
       newformula.IFormula;

       with newformula do
       begin
       fkind := unary;
       finfo := chNeg;
       fRlink := secondnewformula;
       end;

       ltemp.fAntecedents.InsertFirst(newformula);
       newformula := nil;
       secondnewformula := nil; {notA}

       newformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;
       secondnewformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;

       ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
       ltemp.fSucceedent.InsertFirst(newformula);
       newformula := nil; {B}

       rtemp.DismantleTestNode;
       rtemp := nil;
       StraightInsert;
       end;
*/


case uniS:

    doUniS(ltemp);

 /*   newFormula = ((TFormula)ltemp.fSuccedent.get(0)).fRLink.copyFormula();

         ltemp.fSuccedent.remove(0);   // the uni

         ltemp.fSuccedent.add(0,newFormula);

         straightInsert(ltemp,null); */

       break;

       /*

          uniS:
               begin

               newformula := TFormula(ltemp.fSucceedent.First).fRlink.CopyFormula;
               ltemp.fSucceedent.Delete(ltemp.fSucceedent.First);
               ltemp.fSucceedent.InsertFirst(newformula);
               rtemp.DismantleTestNode;
               rtemp := nil;
               StraightInsert;
               end;


    }
   */

  case typedUniS:

    typedUniQuant=(TFormula)(ltemp.fSuccedent.get(0));

    uniQuant=fParser.expandTypeUni(typedUniQuant);

     ltemp.fSuccedent.remove(0);   // the typed uniquant

     ltemp.fSuccedent.add(0,uniQuant);  //append

     straightInsert(ltemp,null);

          break;






case unknown: ;



     }




   fDead=true;

  }

}

/*

 begin
   if SELF.CloseSequent then
    fDead := true
   else
    begin
                {The idea is to take a copy of the node. This copies the node and its formula list}
                {but does not copy the formulas.  This copy will eventually be the left link}
                {Then use FindFirst to see if there is an extendable formula}
                {in the copy. If there is, a second copy is taken to form the right link}
               {Then the extendable formula is copied then changed to form the product parts so that}
                {the copied node becomes the next node; the type of change gets entered on the}
                {parent by First Formula and First Formula also brings the extendable formula}
                {to the head of the list}

     SELF.fSteptype := unknown; {mf 12/16/90 I am worried here about an open node}
 {which cannot be extended-- if it has some other entry on it then the when the tree}
 {is dismantled formulas which have not been created will get collected}

     ltemp := SELF.CopyNode;

     ltemp.RemoveDuplicateFormulas;  (*formulas in ante list*)

 if ltemp.FindFirst(steptypeParam, exhausted) then
     begin
      SELF.fSteptype := steptypeParam;
      rtemp := ltemp.CopyNode; {findFirst brings active formula to head}



 aand:
        begin
        newformula := TFormula(ltemp.fAntecedents.First).fLlink.CopyFormula;
        secondnewformula := TFormula(ltemp.fAntecedents.First).fRlink.CopyFormula;

        ltemp.fAntecedents.Delete(ltemp.fAntecedents.First);
        ltemp.fAntecedents.InsertFirst(secondnewformula);
        ltemp.fAntecedents.InsertFirst(newformula);
        newformula := nil;
        secondnewformula := nil;

        rtemp.DismantleTestNode;
        rtemp := nil;
        StraightInsert;
        end;






    }
     */



  /*

    if (listOfFormulas != null) {
         Iterator iter = listOfFormulas.iterator();

         while (iter.hasNext()) {
           ( (TFormula) iter.next()).interpretFreeVariables(valuation);

         }

       }
     }


   function AndEEtcItem (item: TObject): boolean;

      var
       testformula: TFormula;

     begin
      testformula := TFormula(item);
      foundtype := testformula.TypeofFormula;
      found := ((foundtype = aand) or (foundtype = doubleneg) or (foundtype = equivv));
      AndEEtcItem := found;
     end;


*/


/********************  potential overrides for subclasses ************/

void doExi(TTestNode ltemp){
    TFormula newFormula = ((TFormula)ltemp.fAntecedents.get(0)).scope().copyFormula();

   // secondNewFormula= new TFormula();    //use quantvarform?

   TFormula secondNewFormula= ((TFormula)ltemp.fAntecedents.get(0)).quantVarForm().copyFormula();


   /*

    secondNewFormula.fKind = TFormula.functor;  //Dec 06 why functor not variable
    secondNewFormula.fInfo = String.valueOf(((TFormula)ltemp.fAntecedents.get(0)).quantVar()); */

    ltemp.fAntecedents.remove(0);   // the exi

    ltemp.fAntecedents.add(0,secondNewFormula);  // the variable
    ltemp.fAntecedents.add(0,newFormula);  // he scope

    straightInsert(ltemp,null);

  }




void doUniS(TTestNode ltemp ){
    TFormula newFormula = ( (TFormula) ltemp.fSuccedent.get(0)).fRLink.copyFormula();

    ltemp.fSuccedent.remove(0); // the uni

    ltemp.fSuccedent.add(0, newFormula);

    straightInsert(ltemp, null);
  }



/********************** end of overrides *****************************/




int firstSweep(int length){  /*seems ok April 05*/
  boolean found=false;
  TFormula testFormula=null;
  int index=0;


  while (index<length&&!found){
   testFormula=(TFormula)fAntecedents.get(index);

   if ((typeOfFormula(testFormula)==implic)
       &&fRecurse
       &&deriveIfClause(testFormula)){ //only attempt modus ponens if have both parts
     found=true;
     fAntecedents.remove(index);
     fAntecedents.add(0,testFormula);  // move to front
     return
         implic;
   }
   else
     index++;
 }
 return
     notFound;
}


/*

   function ArrowItem (item: TObject): boolean;

    begin
     testformula := TFormula(item);

     foundtype := testformula.TypeofFormula;
     found := (foundtype = aroww);
     if found then
      found := DeriveIfClause; {only attempts modus ponens if it has both}
  {                                                       bits}
     ArrowItem := found;

    end;


*/

 int secondSweep(int length){ /**seems ok April 05*/
   boolean found=false;
   TFormula testFormula=null;
   int index=0;
   int type=notFound;

   while (index<length&&!found){
      testFormula=(TFormula)fAntecedents.get(index);

      type=typeOfFormula(testFormula);

      if ((type==aand)|| (type==doubleneg)||(type==equivv)){  //andEEtrc
         found=true;
         fAntecedents.remove(index);
         fAntecedents.add(0,testFormula);  // move to front
         return
            type;
         }
         else
           index++;
       }
   return
     notFound;
}

int secondSweepSucc(int lengthSucc){  /**seems ok April 05*/

 boolean found=false;
 TFormula testFormula=null;
 int index=0;
 int type=notFound;

   while (index<lengthSucc&&!found){
     testFormula=(TFormula)fSuccedent.get(index);

     type=typeOfFormula(testFormula);

     if (   (type==equivv)
         || (type==implic)
         || (type==negatomic)
         || (type==negand)
         || (type==nore)
         || (type==negarrow)
         || (type==nequiv)
         || (type==neguni)
         || (type==negexi)){

       found=true;

       return
           type+1;                  // the types for the succedent are one more than the ordinary types

     }
     else
       index++;

   }
   return
       notFound;
}

   /*

     function ReductioEtcItem (item: TObject): boolean;

       var
        testformula: TFormula;

      begin
       testformula := TFormula(item);

       foundtype := testformula.TypeofFormula;
       found := ((foundtype = equivv) or (foundtype = aroww) or (foundtype = negatomic)
    or (foundtype = negand) or (foundtype = nore) or (foundtype = negarrow)
    or (foundtype = nequiv) or (foundtype = negUni) or (foundtype = negexi));

       ReductioEtcItem := found;
      end;


*/


   /*

        {Second sweep. Takes and-E, doublenegE, equivE,reductio, conditional proof, equivI}
 if not found then
  begin
   newHead := fAntecedents.FirstThat(AndEEtcItem);

   if found then
    begin
     steptype := foundtype;
     BringtoHead;

    end;

   if not found and (fSucceedent.fsize <> 0) then {reductio, conditional proof, equivI}
    begin
     newHead := fSucceedent.FirstThat(ReductioEtcItem);
     if found then
      begin
      steptype := Succ(foundtype); { this gives the right type for the succeedent}
{                                                       corresponding to}
      end;
    end;
  end;


*/

int thirdSweep(int length){ /*seems ok April 05*/


 boolean found=false;
 TFormula testFormula=null;
 int index=0;
 int type;

 while (index<length&&!found){
   testFormula=(TFormula)fAntecedents.get(index);

   type=typeOfFormula(testFormula);

   if (type==ore){

     found=true;

     fAntecedents.remove(index);

     fAntecedents.add(0,testFormula);  // move to front

     return
         type;

   }
   else
     index++;

 }
 return
     notFound;
}
 /*

      {Third sweep. vE }

if not found then
 begin
  newHead := fAntecedents.FirstThat(OrItem);

  if found then
   begin
    steptype := foundtype;
    BringtoHead;

   end;
 end;


*/

  int fourthSweepPlainEI(int length){
   /**************** Fourth sweep EI not CV *********************/

//THE CV NOT FULLY IMPLEMENTED YET IN EXTEND

  /*Nov 06
   First we need to find an Exi formula. This may cause us to flag Change Variables (CV).

   If there is no plain EXI the next sweep will pick up the CVs,

   If we do, then if CV (change variables) is flagged, we change
   variables. If not CV we try to do it ordinarily, we may be able to or we may have to unfind it and set CV

   */

  boolean found=false;
  TFormula testFormula=null;
  int index=0;
  int type;


   while (index<length&&!found){
     testFormula=(TFormula)fAntecedents.get(index);

     type=typeOfFormula(testFormula);

     if (type==exi){
        found=true;
        if (TFormula.varFree(testFormula.quantVarForm(),fAntecedents)  //free in antecednets
           ||TFormula.varFree(testFormula.quantVarForm(),fSuccedent)){ // or succedent

           fTreeModel.setExCVFlag(true); // signal for change of variable

           found=false;  //unfind it, it cannot be instantiated
           type=notFound;
           index++;      // move on to next
        }
     }
     else
       index++;
   }


   if (found) {                // good to go
     fAntecedents.remove(index);
     fAntecedents.add(0, testFormula); // move to front
     type=exi;
        return
           type;
        }
  else
    return
       notFound;
}


int fourthSweepEICV(int length){
 /**************** Fourth sweep EI or UG *********************/

//THE CV NOT FULLY IMPLEMENTED YET IN EXTEND

/*Nov 06
 First we need to find an Exi formula. If we do, then if CV (change variables) is flagged, we change
 variables. If not CV we try to do it ordinarily, we may be able to or we may have to unfind it and set CV

 */

boolean found=false;
TFormula testFormula=null;
int index=0;
int type=notFound;

if (fTreeModel.getExCV()){           //we only do this if we are changing variables


  while (index < length && !found) {
    testFormula = (TFormula) fAntecedents.get(index);

    type = typeOfFormula(testFormula);

    if (type == exi) {
      found = true;

      char tempCh = firstNewTerm();

      if (tempCh != chBlank) {

         fNewVariable = tempCh;

         testFormula = testFormula.copyFormula(); // we'll take a copy because there will be instanti
       // information
         fTreeModel.getNewInstantiations().put(testFormula, "" + tempCh); // we have it stored twice April28

         fAntecedents.remove(index);

         fAntecedents.add(0, testFormula); // move to front

         type = exiCV;
      }
      else{                     //no instantiating variable
        found = false;
        type = unknown;
      }
    }
    else
      index++;
  }

  if (found)// good to go
     return
            type;
      }


return
     notFound;
}

/*

function ExiItem (item: TObject): boolean;

  var
   testformula: TFormula;

 begin
  testformula := TFormula(item);

  foundtype := testformula.TypeofFormula;
  found := (foundtype = exi);

  if found then
   if NewVarFree(testformula.QuantVarForm, SELF.fAntecedents) or
      NewVarFree(testformula.QuantVarForm, SELF.fSucceedent) then
    begin
     found := false; {unfinds one that cannot be existentially instantiated}
     steptype := unknown;
     gExCVflag := true;
    end;

  ExiItem := found;
 end;



*/




/*

{Fourth sweep. EI UG}

if not found then
begin
 newHead := fAntecedents.FirstThat(ExiItem);

 if found then
  begin
   steptype := foundtype;
   if gExCV then
    begin
    tempCh := SELF.FirstNewTerm;
    if (tempCh <> ' ') then
    begin
    BringtoHead;
    testformula := TFormula(fAntecedents.First).CopyFormula;

    fAntecedents.Delete(fAntecedents.First);

    StoreNewVariable(testformula, tempCh);

{    quantInfo := 'a'; }
{    quantInfo[1] := tempCh;   check}

{    testformula.finfo := concat(testformula.finfo, quantInfo); var to}
{                                   change to}

    fAntecedents.InsertFirst(testformula);

    testformula := nil;
    quantInfo := '';
    steptype := exiCV;
    end
    else
    begin
    found := false; {no more new terms}
    steptype := unknown;
    end;
    end
   else
    BringtoHead;
  end;

*/

int fourthSweepSucc(int length){
 /**************** Fourth sweep EI or UG *********************/

//THE CV NOT FULLY IMPLEMENTED YET IN EXTEND

boolean found=false;
TFormula testFormula=null;
int index=0;
int type;


 while (index<length&&!found){
   testFormula=(TFormula)fSuccedent.get(index);

   type=typeOfFormula(testFormula);

   if (type==uni){
      found=true;
      type=uniS;

      if (TFormula.varFree(testFormula.quantVarForm(),fAntecedents)){  //free in antecednets
         found=false;  //unfind it, it cannot be instantiated
         type=notFound;
         index++;     // move on to next

         fTreeModel.setUniCVFlag(true); // signal for change of variable
      }
   }
   else
     index++;
 }


 if (found) {                // good to go

      if (fTreeModel.getUniCV()){

         char tempCh=firstNewTerm();

         if (tempCh!=chBlank){

           fNewVariable = tempCh;

           testFormula=testFormula.copyFormula();  // we'll take a copy because there will be instanti
                                                   // information
           fTreeModel.getNewInstantiations().put(testFormula,""+tempCh);    // we have it stored twice April28

           fSuccedent.remove(index);

           fSuccedent.add(0, testFormula); // move to front

           type=uniSCV;

           return
              type;
         }
         else{                  // no new term
           found=false;
           type=notFound;
         }
      }
      else{                     // found but no complications

        fSuccedent.remove(index);
        fSuccedent.add(0, testFormula); // move to front
        type=uniS;
        return
              type;

      }
}


return
     notFound;
}


  /**************** Fourth and a half sweep EI or UG *********************/
  /*

     function UniSItem (item: TObject): boolean;

      var
       testformula: TFormula;

     begin
      testformula := TFormula(item);

      foundtype := testformula.TypeofFormula;
      found := (foundtype = uni);
      if found then
       if NewVarFree(testformula.QuantVarForm, SELF.fAntecedents) then
        begin
         found := false;
         gUniCVflag := true;
        end;
      UniSItem := found;
     end;

*/

/*    if not found and (fSucceedent.fsize <> 0) then
    begin
     newHead := fSucceedent.FirstThat(UniSItem);
     if found then
      begin
      steptype := Succ(foundtype); { this gives the right type for the succeedent}
{                                                       corresponding to}
      end;

     if found then
      begin
      if gUniCV then
      begin
      tempCh := SELF.FirstNewTerm;
      if (tempCh <> ' ') then
      begin
      testformula := TFormula(fSucceedent.First).CopyFormula;
      fSucceedent.Delete(fSucceedent.First);

      StoreNewVariable(testformula, tempCh);

{testformula.finfo := concat(testformula.finfo, strofchar(tempCh)); instantiation ino}
      fSucceedent.InsertFirst(testformula);
      testformula := nil;
      steptype := uniSCV; {changing vars}
      end
      else
      begin
      found := false; {no more new terms}
      steptype := unknown;
      end;
      end
      end;
    end;
  end;


  */
  /*****************************/

  int fifthSweepSucc(int lengthSucc){

    /**************** Fifth sweep andS oreS doublenegS *********************/

    boolean found = false;
    TFormula testFormula = null;
    int index = 0;

    while (index < lengthSucc && !found) {
      testFormula = (TFormula) fSuccedent.get(index);  // there is only one succedent, but
                                                       // I suppose I am paving the way for generalization

      int type = typeOfFormula(testFormula);

      if ( (type == doubleneg)
          || (type == aand)
          || (type == ore)) {

        found = true;
        fSuccedent.remove(index);
        fSuccedent.add(0,testFormula);  // move to front

        return
            type + 1; // the types for the succedent are one more than the ordinary types
      }
      else
        index++;
    }
    return
        notFound;
  }



      /*
               {fifth sweep. andS oreS doublenegS }

      if not found and (fSucceedent.fsize <> 0) then
       begin
        newHead := fSucceedent.FirstThat(AndSEtcItem);
        if found then
         begin
          steptype := Succ(foundtype); { this gives the right type for the succeedent}
    {                                                  corresponding to}
         end;
       end;


       function AndSEtcItem (item: TObject): boolean;

          var
           testformula: TFormula;

         begin
          testformula := TFormula(item);

          foundtype := testformula.TypeofFormula;
          found := (foundtype = doubleneg) or (foundtype = aand) or (foundtype = ore);

          AndSEtcItem := found;
         end;



      */

int sixththSweep(int length,TFlag exhausted){
       boolean found=false;
        TFormula testFormula=null;
        int index=0;

          while (index<length&&!found){
            testFormula=(TFormula)fAntecedents.get(index);
            int type=typeOfFormula(testFormula);

            if (type==uni){

              if (prepareQuant(testFormula, exhausted, fTreeModel.getResetNeeded())){     //checks whether it cn be instantiated
                found=true;
                fAntecedents.remove(index);
                fAntecedents.add(0, testFormula); // move to front

                return
                    type;
              }
            }

            if (type==typedUni){

              found=true;
              fAntecedents.remove(index);
              fAntecedents.add(0, testFormula); // move to front

              return
                  type;

}







            index++;
          }
          fTreeModel.setUniCVFlag(fTreeModel.getUniCVFlag()||
                                  fTreeModel.getResetNeeded().getValue());
   return
      notFound;
}

       /*

        function UniItem (item: TObject): boolean;

         var
          testformula: TFormula;

        begin
         testformula := TFormula(item);

         foundtype := testformula.TypeofFormula;
         found := (foundtype = uni);
         if found then
          begin
           found := SELF.PrepareQuant(testformula, exhausted, gResetneeded);
               {This checks whether the uniquant has been fully instantiated already and unfinds it if so}
           quantInfo := testformula.finfo;


           if found then
            testformula.DismantleFormula;  (*PrepareQuant makes a copy which we dont want*)

          end;




         {sixth sweep and UI first then EG}

          if not found then
           begin
            newHead := fAntecedents.FirstThat(UniItem);

            if found then
             begin
              steptype := foundtype;
              BringtoHead;


              testformula := TFormula(fAntecedents.First); {preparequant copies}
              fAntecedents.Delete(fAntecedents.First); {check mf}

              testformula := testformula.CopyFormula; (*needed for garbage collector*)

              testformula.finfo := quantInfo; {instantiation ino}

                                                        {Debugging}
        {$IFC myDebugging}
              if FALSE then
               writeln('findfirst uniquantinfo ', quantInfo);
        {$ENDC}
              fAntecedents.InsertFirst(testformula);

              testformula := nil;
              quantInfo := '';
             end
            else
             gUniCVflag := gUniCVflag or gResetneeded; {MF}
*/


int sixthSweepSucc(int succLength,TFlag exhausted)
   {boolean found=false;
   TFormula testFormula=null;
   int index=0;
   int type=notFound;

   while (index<succLength&&!found){
      testFormula=(TFormula)fSuccedent.get(index);
      type=typeOfFormula(testFormula);

      if ((type==exi)&&
         (prepareQuant(testFormula, exhausted, fTreeModel.getResetNeeded()))){
         //{This checks whether the exiquant has been fully instantiated already and unfinds it if so}
            found=true;
            type=exiS;
      }
      else
         index++;
   }

/* The instantiations are in a hash map keyed of the formula. prepareQuant() checks whether there are any new ones available,
and, if so, adds them and returns true
*/


   if (found){
        fSuccedent.remove(index);        // usually only one succedent, this allows for the generalization
        fSuccedent.add(0, testFormula); // move test formula to front

        if (!canDoExiS()){             //exiS actually does it
          found=false;
          return
            notFound;
        }
        return
           exiS;
      }

    return
      notFound;



      //HERE CAN DO EXIS


      }

       /**************** then EG *********************/


 /*
      if not found and (fSucceedent.fsize <> 0) then {either no antecedents or not found}
       begin
        testformula := TFormula(fSucceedent.First);
        foundtype := testformula.TypeofFormula;
        found := (foundtype = exi); {exiS for EG}
        if found then
         begin
         steptype := Succ(foundtype); { this gives the right type for the succeedent}
  {                                                       corresponding to}

         found := SELF.PrepareQuant(testformula, exhausted, dummy);
           {This checks whether the exiquant has been fully instantiated already and unfinds it if so}
         quantInfo := testformula.finfo;

         if found then
         begin
         tempFormula := TFormula(fSucceedent.First); (*not garbage*)

         fSucceedent.Delete(fSucceedent.First);
         testformula.finfo := quantInfo; {instantiation ino}
         fSucceedent.InsertFirst(testformula);


         quantInfo := '';

         tempexCV := gExCV; {cando exis can change this}
         tempexCVflag := gExCVflag; {we do not wish it to until all else tried}

         if not SELF.CanDoExiS then {actually does it or unfinds it}
         begin
         fSucceedent.Delete(fSucceedent.First);  (*testformula*)
         fSucceedent.InsertFirst(tempformula);
         tempformula := nil;

         testformula.DismantleFormula;  (*prepareQuant makes testformula a copy*)

         found := false;
         steptype := unknown;
         gExCV := tempexCV; {returns to former state}
         gExCVflag := tempexCVflag;
         end
         else
         tempformula := nil;

         end;
         end;
       end;
     end;

*/



 int sixthSweepSuccTwo(int succLength)
    {boolean found=false;
    TFormula testFormula=null;
    int index=0;
    int type=notFound;

    while (index<succLength&&!found){
      testFormula = (TFormula) fSuccedent.get(index);
      type = typeOfFormula(testFormula);

      if (type == uni) {
        found=true;

        if (fTreeModel.getUniCV()) {

          char tempCh = firstNewTerm();

          if (tempCh != chBlank) {

            fNewVariable = tempCh;

            testFormula = testFormula.copyFormula(); // we'll take a copy because there will be instanti
            // information
            fTreeModel.getNewInstantiations().put(testFormula, "" + tempCh); // we have it stored twice April28
            fSuccedent.remove(index);
            fSuccedent.add(0, testFormula); // move to front

            type = uniSCV;
            return
              type;

          }
          else { // no new term
            found = false;
          }
        }
        else {
          fTreeModel.setUniCVFlag(true);
          type = uniSpec;
          return
              type;
        }
      }
          index++;
    }

     return
       notFound;

       }

       /*
                   {check this with D-Planner}

           if not found and (fSucceedent.fsize <> 0) then {either no antecedents or not found}
            begin
             testformula := TFormula(fSucceedent.First);
             foundtype := testformula.TypeofFormula;
             found := (foundtype = uni);

             if found then
              begin
               if gUniCV then
                begin
                tempCh := SELF.FirstNewTerm;
                if (tempCh <> ' ') then
                begin
                testformula := TFormula(fSucceedent.First).CopyFormula;
                fSucceedent.Delete(fSucceedent.First);
                StoreNewVariable(testformula, tempCh);
         {    testformula.finfo := concat(testformula.finfo, strofchar(tempCh)); instantiation}
         {                                   ino}
                fSucceedent.InsertFirst(testformula);
                testformula := nil;
                steptype := uniSCV; {changing vars}
                end
                else
                begin
                found := false; {no more new terms}
                steptype := unknown;
                end;
                end
               else
                begin
                gUniCVflag := true;
                steptype := uniSpec; {reduction uniS with free variables}
                end;

              end;

            end;

                                          {$IFC myDebugging}

           if FALSE then
            if found then
             writeln('found in sweep6');

                   {$ENDC}

        */






TTestNode canDeriveThis(TFormula thisFormula, int maxSteps){

  //{This is test that sees whether we can derive thisFormula }

  /*We are trying to see wheter we can derive Fa Fb Fc say as a precursor to
  proving ExFx.  This needs to be in the same context. But the problem here is with
failures not success. Each failure needs to backtrack and leave the context as it
was. (A success can change the context. The hash tables for instantiaions do not
matter as they are keyed on individual formulas (they could just end up with a few redundant
entries*/

    /*

   Careful: there are TTreeModels, TreeNodes, TTestNodes etc. and these are different

   Note that when you start one of these TTestNodes from, say, root, there has to be and almost circular initialization.
   There needs to be a TTreeModel and root's TreeNode needs to refer to it. And the TTreeModel itself needs to know
   what root TreeNode it is talking about. So typically you do something like

      TTestNode aTestRoot = new TTestNode(fDeriverDocument.getParser(),null);  //does not initialize TreeModel

      TTreeModel aTreeModel= new TTreeModel(aTestRoot.fSwingTreeNode);       // produces a TreeModel and points it to root's TreeNode

      aTestRoot.fTreeModel=aTreeModel;                                  //Tree Model initialized now


   */



   TTestNode test =this.copyNodeInFullWithInstInfo(); //node, formulas, and context

   test.fSuccedent.clear();
   test.fSuccedent.add(thisFormula.copyFormula());        // the target is A

   TTreeModel trialExtension= fTreeModel.shallowCopy(test.fSwingTreeNode);   // gives new model, copy of flags, pointer to instantiations

   test.fTreeModel=trialExtension;                            //  new context




   if (test.treeValid(trialExtension,maxSteps)==valid)      //only do maxSteps
      return
         test;
else
  return
      null;
}

void copySubTreeIn(TTestNode root){  /* the assumption here is that this root belongs to a
     different
  Tree Model ie the context is different, but we don't care */

  TTestNode leftChild=root.getLeftChild();
  TTestNode rightChild=root.getRightChild();

  root.fTreeModel=this.fTreeModel;              //change context
  root.fSwingTreeNode= new DefaultMutableTreeNode(root); // start afresch  //trouble here June05

  fSwingTreeNode.add(root.fSwingTreeNode);

  if (leftChild!=null)
    root.copySubTreeIn(leftChild);
  if (rightChild!=null)
    root.copySubTreeIn(rightChild);

}


boolean canDoExiS(){
  //{this searches for a suitable instantiation and makes it}
 //{or does nothing}
 boolean done=false;
 TTestNode successTree=null;

 TFormula exiQuant=(TFormula)(fSuccedent.get(0));

 String newInstants="";

 if (fTreeModel.getNewInstantiations().containsKey(exiQuant))
   newInstants=(String)(fTreeModel.getNewInstantiations().get(exiQuant));

 TFormula variForm=exiQuant.quantVarForm();
    TFormula termForm;
    TFormula newInstance;

    for (int i=0;(i<newInstants.length())&&!done;i++){

      termForm=new TFormula(TFormula.functor,newInstants.substring(i,i+1),null,null);

      newInstance=exiQuant.scope().copyFormula();

      TFormula.subTermVar(newInstance, termForm, variForm);

      successTree=canDeriveThis(newInstance,kMaxTreeDepth);

      if (successTree!=null)
        done=true;

    }

    if (done)     /* we now want to splice the new tree in, the contexts are different
                  but this does not matter because the new tree is closed and so all
                  instantiations have been used correctly*/
      {
        // we want to replace this node by successTree
      this.fClosed = successTree.fClosed;
      this.fDead = successTree.fDead;
      this.fAntecedents = successTree.fAntecedents;
      this.fSuccedent = successTree.fSuccedent;
      this.fStepType = successTree.fStepType;
      this.fStepsToExpiry = successTree.fStepsToExpiry;
      this.fNodeDepth = successTree.fNodeDepth;
      this.fNewVariable = successTree.fNewVariable;
     // this.fTreeModel = successTree.fTreeModel;      leave context
     // this.fSwingTreeNode = successTree.fSwingTreeNode;

     TTestNode leftChild=successTree.getLeftChild();
     TTestNode rightChild=successTree.getRightChild();

/* I used to have this June05 which was not working properly*/

     if (leftChild!=null)
        copySubTreeIn(leftChild);
     if (rightChild!=null)
        copySubTreeIn(rightChild);

/*but surely the next will do */

    }

  return
      done;
}

/*
  function TTestnode.CanDoExiS: boolean;
 {this searches for a suitable instantiation and makes it}
 {or does nothing}

   var
    temptest: TTestnode;
    i: integer;
    done, tempexCV, tempexCVflag: boolean;
    instants: string[15];
    garbageformula, termForm, variForm, firstForm: TFormula;
    variable: char;


   procedure AssembleTest (i: integer);
      {This is a test of whether we can prove an instantiation from the rest of the premises}

    var
     testformula: TFormula;

   begin
    temptest := nil;
    temptest := SELF.CopyNodeinFull;

    temptest.fDead := false; {must reset it for a new test}
    temptest.fClosed := false;
    temptest.fLlink := nil;
    temptest.fRlink := nil;

    testformula := TFormula(temptest.fSucceedent.First).fRlink.CopyFormula; {scope}

    termForm.fInfo := instants[i];

                {variable := selfInfo[2]; }

    NewSubTermVar(testformula, termForm, variForm);

    garbageformula := TFormula(temptest.fSucceedent.First);
    temptest.fSucceedent.Delete(temptest.fSucceedent.First);
    garbageformula.DismantleFormula;

    temptest.fSucceedent.InsertFirst(testformula);
    testformula := nil;
   end;

  begin
   tempexCV := gExCV; {indicates need to change variables}
   tempexCVflag := gExCVflag;
   done := false;


   SupplyFormula(termForm);
   termForm.fKind := functor;

   firstForm := TFormula(fSucceedent.First);  (*prepare quant has filled this in*)

   variForm := firstForm.QuantVarForm;
   i := NoOfInstantiations(firstForm); {the number of instantiations to try}
   instants := gStringstore[StringStoreIndex(firstForm)];

   while not done and (i > 0) do
    begin
     AssembleTest(i);
     if temptest.TreeValid(kMaxtreesize) = valid then
      done := true
     else
      DismantleTestTree(temptest);
     i := i - 1;
    end;

   if done then  {adds new tree in}
    begin
     if SELF.fAntecedents <> nil then
      begin
       SELF.fAntecedents.DeleteAll;
       SELF.fAntecedents.Free; (*freeing list not formulas*)
       SELF.fAntecedents := nil;
      end;
     if SELF.fSucceedent <> nil then
      begin
       garbageformula := TFormula(SELF.fSucceedent.First);
       garbageformula.DismantleFormula;  (*the original existential is itself a copy*)
       SELF.fSucceedent.DeleteAll;
       SELF.fSucceedent.Free; (*freeing list not formulas*)
       SELF.fSucceedent := nil;
      end;
                         {we have two copies of this}


     SELF.fClosed := temptest.fClosed;
     SELF.fDead := temptest.fDead;
     SELF.fAntecedents := temptest.fAntecedents;
     SELF.fSucceedent := temptest.fSucceedent;
     SELF.fSteptype := temptest.fSteptype;
     SELF.fLlink := temptest.fLlink;
     SELF.fRlink := temptest.fRlink;


     temptest.Free; {we have two copies of this}

     temptest := nil;
    end;

   gExCV := tempexCV; {returns to former state}
   gExCVflag := tempexCVflag;

   termForm.DismantleFormula;

   CanDoExiS := done;
  end;


*/

/*

 function NorEtcItem (item: TObject): boolean;

    var
     testformula: TFormula;

   begin
    testformula := TFormula(item);

    foundtype := testformula.TypeofFormula;
    found := (foundtype = aroww)
 or (foundtype = nore)
 or (foundtype = negand)
 or (foundtype = nequiv)
 or (foundtype = negarrow);

    NorEtcItem := found;
   end;

        {Eighth arrow without if-clause, nor, negand,nequiv negarrow }
  if not found then
   begin
    newHead := fAntecedents.FirstThat(NorEtcItem);

    if found then
     begin
      steptype := foundtype;
      BringtoHead;

     end;
   end;


*/

/*

 function NeguniNegExitem (item: TObject): boolean;

    var
     testformula: TFormula;

   begin
    testformula := TFormula(item);

    foundtype := testformula.TypeofFormula;
    found := (foundtype = negUni) or (foundtype = negexi);

    NeguniNegExitem := found;
   end;


        {seventh sweep : neguni, negexi}

  if not found then
   begin
    newHead := fAntecedents.FirstThat(NeguniNegExitem);

    if found then
     begin
      steptype := foundtype;
      BringtoHead;
     end;
   end;


*/

int seventhSweep(int length){

boolean found=false;
  TFormula testFormula=null;
  int index=0;


while (index<length&&!found){
  testFormula=(TFormula)fAntecedents.get(index);

  int type=typeOfFormula(testFormula);

  if ((type==neguni)
      ||(type==negexi)){

    found=true;
    fAntecedents.remove(index);
    fAntecedents.add(0,testFormula);  // move to front
    return
        type;
  }
  else
    index++;
}
return
   notFound;
}


/************** Seventh sweep ********************/


  /*

        {seventh sweep : neguni, negexi}

  if not found then
   begin
    newHead := fAntecedents.FirstThat(NeguniNegExitem);

    if found then
     begin
      steptype := foundtype;
      BringtoHead;
     end;
   end;

   function NeguniNegExitem (item: TObject): boolean;

   var
    testformula: TFormula;

  begin
   testformula := TFormula(item);

   foundtype := testformula.TypeofFormula;
   found := (foundtype = negUni) or (foundtype = negexi);

   NeguniNegExitem := found;
  end;



   */


/**************** Eight sweep   rrow without if-clause, nor, negand,nequiv negarrow *********************/

int eighthSweep(int length){

boolean found=false;
  TFormula testFormula=null;
  int index=0;


while (index<length&&!found){
  testFormula=(TFormula)fAntecedents.get(index);

  int type=typeOfFormula(testFormula);

  if ((type==implic)
      || (type==nore)
      || (type==negand)
      || (type==nequiv)
      ||(type==negarrow)){

    found=true;

    fAntecedents.remove(index);

    fAntecedents.add(0,testFormula);  // move to front

    return
        type;
  }
  else
    index++;
}
return
   notFound;
}

/*

 function NorEtcItem (item: TObject): boolean;

   var
    testformula: TFormula;

  begin
   testformula := TFormula(item);

   foundtype := testformula.TypeofFormula;
   found := (foundtype = aroww) or (foundtype = nore) or (foundtype = negand) or (foundtype = nequiv) or (foundtype = negarrow);

   NorEtcItem := found;
  end;


 */

/*

{Eighth arrow without if-clause, nor, negand,nequiv negarrow }
if not found then
begin
newHead := fAntecedents.FirstThat(NorEtcItem);

if found then
begin
steptype := foundtype;
BringtoHead;

end;
end;

*/

int ninethSweep(int length){

boolean found=false;
  TFormula testFormula=null;
  int index=0;


while (index<length&&!found){
  testFormula=(TFormula)fAntecedents.get(index);

  int type=typeOfFormula(testFormula);

  if ((type!=neguni)
      && (type!=negexi)
      && (type!=atomic)
      && (type!=negatomic)
      &&(type!=uni)
      &&(type!=exi)){

    found=true;

    fAntecedents.remove(index);

    fAntecedents.add(0,testFormula);  // move to front

    return
        type;
  }
  else
    index++;
}
return
   notFound;
}


/**************** Nineth sweep   Rest *********************/


/*

 function RestItem (item: TObject): boolean;

   var
    testformula: TFormula;

  begin
   testformula := TFormula(item);

   foundtype := testformula.TypeofFormula;
   found := (foundtype <> negUni) and (foundtype <> negexi) and ((foundtype <> atomic) and (foundtype <> negatomic)
 and (foundtype <> uni) and (foundtype <> exi));

   RestItem := found;
  end;


 }

*/


/**************** Nine and a half sweep   RestS *********************/

int ninethSweepSucc(int lengthSucc){

    boolean found = false;
    TFormula testFormula = null;
    int index = 0;

    while (index < lengthSucc && !found) {
      testFormula = (TFormula) fSuccedent.get(index);

      int type = typeOfFormula(testFormula);

      if ( (type != atomic)
          && (type != negatomic)
          && (type != exi)) {

        found = true;

        return
            type + 1; // the types for the succedent are one more than the ordinary types

      }
      else
        index++;

    }
    return
        notFound;
  }







/*
 function RestSItem (item: TObject): boolean;

    var
     testformula: TFormula;

   begin
    testformula := TFormula(item);

    foundtype := testformula.TypeofFormula;
    found := (foundtype <> atomic) and (foundtype <> negatomic) and (foundtype <> exi);

    RestSItem := found;
   end;

*****
 if not found and (fSucceedent.fsize <> 0) then
   begin
    newHead := fSucceedent.FirstThat(RestSItem);
    if found then
     begin
      steptype := Succ(foundtype); { this gives the right type for the succeedent}
{                                                  corresponding to}
     end;
   end;


*/



  int findFirst(TFlag exhausted){


    int length=fAntecedents.size();
    int lengthSucc=fSuccedent.size();
    int index=0;

    int value=notFound;

    value=firstSweep(length);
    if (value!=notFound)
      return
         value;

    value=secondSweep(length);
    if (value!=notFound)
      return
         value;

    value=secondSweepSucc(lengthSucc);
    if (value!=notFound)
      return
         value;

    value=thirdSweep(length);
    if (value!=notFound)
      return
         value;

    value=fourthSweepPlainEI(length);
    if (value!=notFound)
       return
          value;

    value=fourthSweepEICV(length);
    if (value!=notFound)
       return
          value;

    value=fourthSweepSucc(lengthSucc);
     if (value!=notFound)
       return
          value;

    value=fifthSweepSucc(lengthSucc);
         if (value!=notFound)
           return
              value;

    value=sixththSweep(length,exhausted);
    if (value!=notFound)
      return
         value;

    value=sixthSweepSucc(lengthSucc, exhausted);
    if (value!=notFound)
      return
        value;

    value=sixthSweepSuccTwo(lengthSucc);
    if (value!=notFound)
      return
        value;

  value=seventhSweep(length);
    if (value!=notFound)
       return
          value;





  value=eighthSweep(length);
  if (value!=notFound)
    return
       value;

 value=ninethSweep(length);
   if (value!=notFound)
     return
        value;

  value=ninethSweepSucc(lengthSucc);
    if (value!=notFound)
      return
         value;



/* NOTE HERE THERE IS MORE TO COME ON QUANTIFIERS-- CHECK PASCAL*/



   return
       notFound;


  }

  /*

     function TTestnode.FindFirst (var steptype: formulatype; var exhausted: boolean): boolean;

   {This is important. It controls the overall strategy. It finds first preferred formula to }
   {process in antecedent list or  in succ list and shifts it to head of its list and fills in type }
   {of inference on node. There are perhaps 32 types of inference. It prefers some }
   {to others}

     var
      foundtype: formulatype;
      found, tempexCV, tempexCVflag, tempUniCV, tempUniCVflag, dummy: boolean;
      tempCh: char;
      newHead: TObject;
      testformula, tempformula: TFormula;
      quantInfo: str255;
      newtree: TTestnode;


  */

 /*

   begin
    found := false;

            {first sweep for arrow, this is to stop decomposing the if clause then putting it back}

    newHead := fAntecedents.FirstThat(ArrowItem);

                   {$IFC myDebugging}

    if FALSE then
     if found then
      begin
       writeln('found in sweep1 and this is the result');
       SELF.Buglook
      end;
               {$ENDC}

    if found then
     begin
      steptype := foundtype;
      BringtoHead;
     end;

            {Second sweep. Takes and-E, doublenegE, equivE,reductio, conditional proof, equivI}
    if not found then
     begin
      newHead := fAntecedents.FirstThat(AndEEtcItem);

      if found then
       begin
        steptype := foundtype;
        BringtoHead;

       end;


*/


char firstNewTerm(){
  String termsPresent= termsInNode();

  int length = TParser.fVariables.length();

  for (int i=0;i<length;i++){
     char ch=TParser.fVariables.charAt(i);

     if (termsPresent.indexOf(ch)==-1)
       return
           ch;
  }
  return
      chBlank;

}


/*
    function TTestnode.FirstNewTerm: char;

   var
    termspresent: str255;
    newterm: string[1];
    found: boolean;
    i, j: integer;

  begin
   termspresent := SELF.TermsinNode;
   found := false;
   newterm := ' ';
   i := ord('m');
   j := ord('z');

   while not found and not (i > j) do
    begin
     newterm[1] := chr(i);
     if pos(newterm, termspresent) = 0 then
      found := true;
     i := i + 1;
    end;

   if found then
    FirstNewTerm := newterm[1]
   else
    FirstNewTerm := ' ';
  end;


  */


 public void initializeContext /*startSatisfactionTree*/(TTreeModel aTreeModel){

   //fOldInstantiations= new HashMap();     //of quantified formulas
   //fNewInstantiations= new HashMap();     //of quantifiers

   //fTreeModel=aTreeModel;  treeValid does this

   aTreeModel.setExCVFlag(false);
 }


    /* public void startSatisfactionTree(){  //MORE TO COME
       fTestRoot= new TTestNode(fParser);

     } */

      /*
       procedure StartSatisfactionTree;

         var
          testformula: TFormula;

        begin

         InitStringStore;

         gExCV := false; {indicates need to change variables}
         gExCVflag := false;
         gUniCV := false; {indicates need to change variables}
         gUniCVflag := false;

         gMaxrecurse := 1; {permits one go at modus ponens test in FindFirst}

         New(gTestroot);
         FailNIL(gTestroot);
         gTestroot.ITestnode;

         gConstantsInTestRoot := '';

        end;


     */


public void splitInsert(TTestNode leftNode, TTestNode rightNode){

    /*adds immediately after thisone*/
	
	  leftNode.fNodeDepth=this.fNodeDepth+1;

      fSwingTreeNode.add(leftNode.fSwingTreeNode);

      if(rightNode!=null){
    	rightNode.fNodeDepth=this.fNodeDepth+2;
        fSwingTreeNode.add(rightNode.fSwingTreeNode);
      }
    }


public void straightInsert(TTestNode leftNode, TTestNode rightNode){

/*adds immediately after thisone*/
	
	leftNode.fNodeDepth=this.fNodeDepth+1;

  fSwingTreeNode.add(leftNode.fSwingTreeNode);

  if(rightNode!=null){
	rightNode.fNodeDepth=this.fNodeDepth+2;
    leftNode.fSwingTreeNode.add(rightNode.fSwingTreeNode);

  }

}


/*

     procedure StraightInsert;
         {adds immediately after thisone}

      begin
       ltemp.fLlink := fLlink;
       ltemp.fRlink := fRlink;
       fLlink := ltemp;
       fRlink := nil;

               {Debugging}
                   {$IFC myDebugging}
       if FALSE then
        begin
         writeln('exiting ing extend ltemp is');
         ltemp.Buglook;

        end;

                   {$ENDC}
       ltemp := nil;

       if rtemp <> nil then
        begin
         rtemp.fLlink := fLlink;
         rtemp.fRlink := fRlink;
         fLlink := rtemp;
         fRlink := nil;
         rtemp := nil;
        end;
      end;


 */

String termsInNode(){


return
      "";
}


/*

 function TTestnode.TermsinNode: str255;

  var
   tempterms: str255;

 begin
  tempterms := '';

  tempterms := concat(TermsinList(fAntecedents), TermsinList(fSucceedent));
  RemoveDuplicates(tempterms);

  TermsinNode := tempterms;
 end;


*/


public String toString(){
  String returnStr="";
  int length=fAntecedents.size();

  for (int i=0;i<length;i++){
    returnStr= returnStr
        + fParser.writeFormulaToString((TFormula)fAntecedents.get(i));

  if (i<(length-1))
    returnStr= returnStr +", ";

  }

  returnStr+=" "+ chTherefore;

  length=fSuccedent.size();

  for (int i=0;i<length;i++){
    returnStr= returnStr
        + fParser.writeFormulaToString((TFormula)fSuccedent.get(i));

  if (i<(length-1))
    returnStr= returnStr +", ";

  }



  if (fDead)
    returnStr+=" dead";

  if (fClosed)
    returnStr+=" closed";
  else
    returnStr+=" open";

  returnStr+=" " + fStepType;


  return
      returnStr;


}

private void resetForCV(){
  /* we are going to start again, but in change variable mode */

  //reset it
  fClosed=false;
  fDead=false;
  fStepType = unknown;
  fStepsToExpiry = 0;
  fNodeDepth = 0;

  //reset its model
  fTreeModel.resetForCV(); //  new context


}

public int treeValid(TTreeModel aTreeModel, int maxSteps){   //NEED TO FINSIH MAXSREPS CODE



  /* expects a properly filled in root
   The bare node has to belong to a tree model.

   */

  /*May 05 This operates in two modes: ordinary and change variable. When running
in ordinary, this can set a flag for change variable. If the tree turns out
 to be valid, this does not matter. But otherwise the whole thing needs to be run
again with variables changed.*/
	
	/*This needs rewriting June 10, there are several redunancies when checking depth
	 * and CV other routine
	 */



   fTreeModel=aTreeModel;
  fStepsToExpiry=maxSteps;
  boolean killed;
  int depth=0;

  boolean modeOrdinary=!fTreeModel.getExCV();  //ie don't change variable

  if (modeOrdinary){


  killed=kill(maxSteps);   // the main processing
  
  depth=treeDepth();  //not used
  
  /*we process in order down a branch, so typically a branch is much longer than what we have processed*/

  if (!killed){  //either CV or out of steps
    if (fTreeModel.getExCVFlag()){  // wants change variables
       fTreeModel.resetForCV(); //  new context
       modeOrdinary=false;      // now the second part will do it again
      }
      else
    return
        notKnown;      // just run out of steps
  }
  
  
 if (killed){
    gOpenNode = aNodeOpen();

    if (gOpenNode == null) //closed and complete
      return
          valid;
    else{
      if (fTreeModel.getExCVFlag()){  // wants change variables
         resetForCV();             // start again with new context
         modeOrdinary=false;      // now the second part will do it again
      }
      else{ //noCV flag, open node
          //open complete
        return
            notValid;
      }
    }
  }
  }


  if (!modeOrdinary){   // now we are changing variables to do it all again
	  
	  return
	     treeValidChangeVariables(aTreeModel,maxSteps);
  }
  
  return
     notKnown;

}

public int treeValidChangeVariables(TTreeModel aTreeModel, int maxSteps){   //NEED TO FINSIH MAXSREPS CODE



	    fStepsToExpiry=maxSteps;  // allow it the same number of steps as the original
	    fNodeDepth=0;
	    boolean killed;
	    int depth=0;


	  killed=kill(maxSteps);   // the main processing

	  if (depth>maxSteps/*fStepsToExpiry<1*/)
	    return
	      notKnown;
	  else{
	    gOpenNode = aNodeOpen();

	    if (gOpenNode == null) //closed and complete
	      return
	          valid;
	    else{
	      if (killed) //open complete
	        return
	            notValid;
	      else
	        return
	            notKnown; // open but not complete
	    }
	  }

	}



/*

 function TTestnode.TreeValid;
{var root: boolean, maxstesps integer}

{expects a properly filled in rootnode}

  var
   killed, stilltesting: boolean;
   treesize: integer;

 begin
  killed := true;
  stilltesting := true;
  gBaleOutFlag := false;

  treesize := kMaxtreesize - maxsteps; {only runs for maxsteps nodes}

  SELF.Kill(killed, treesize);

          {    WriteTree(root);}

  if (treesize > kMaxtreesize) or gBaleOutFlag then
   TreeValid := notknown

  else

   begin

    while stilltesting do
     begin
      if SELF.ANodeOpen(gOpenNode) then
       begin
       if killed then
       begin
       TreeValid := notvalid;
       stilltesting := false;
       end
       else
       begin {open live branch}
       TreeValid := notknown; {not known at this point}
       stilltesting := false;
       end;
       end
      else
       begin
       TreeValid := valid; {valid}
       stilltesting := false;
       end;

     end;
   end;

 end;



*/


/*
 procedure NodeSatisfiable;

        var
         argtype: argumenttype;
         tempTest: TTestNode;

       begin
        if gTestroot.CloseSequent then
         begin
          gTestroot.fClosed := TRUE;
          gTestroot.fDead := TRUE;
         end;


        argtype := gTestroot.TreeValid(128);

        if gExCVFlag then {falgging for change of variable}
         if not (argtype = valid) then
          gExCV := TRUE;

        if gExCV then
         begin
          tempTest := gTestroot.CopyNodeinFull;

          DismantleTestTree(gTestroot);

          StartSatisfactionTree; {re-initializes, including gExCV}

          gExCV := TRUE;

          tempTest.fClosed := FALSE;
          tempTest.fDead := FALSE;
          tempTest.fSteptype := unknown;
          tempTest.fLLink := nil;
          tempTest.fRLink := nil;

          gTestroot := tempTest;
          tempTest := nil;

          argtype := gTestroot.TreeValid(128);

         end;

        case argtype of
         notvalid:
          begin
           fDeriverDocument.WriteToJournal(concat(gCr, '(*Satisfiable.*)'), TRUE, FALSE);
           CreateInterpretationList;

           if MEMBER(gApplication.fLastCommand, TShapeClearCommand) then
            gApplication.CommitLastCommand; {the idea here is that the}
     {							user may try to clear the previous drawing, but it is not really}
     {							cleared until committed}

           drawable := TRUE;

           WriteInterpretationList(drawable);

           if drawable then
            begin
            if (fDeriverDocument.fShapeList.fSize < 2) then
            begin
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation drawn.*)'), TRUE, FALSE);
            ConstructDrawing;
            end
            else
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation can be drawn, if the existing drawing is cleared.*)'), TRUE, FALSE);

            end
           else
            fDeriverDocument.WriteToJournal(concat(gCr, '(*Interpretation not drawable.*)'), TRUE, FALSE);
          end;
         valid:
          fDeriverDocument.WriteToJournal(concat(gCr, '(*Not satisfiable.*)'), TRUE, FALSE);

         notknown:
          fDeriverDocument.WriteToJournal(concat(gCr, '(*Not known whether satisfiable.*)'), TRUE, FALSE);

         otherwise
        end;
       end;


*/





/*
   function TTestnode.TwoContradict (var firstone, secondone: TFormula): boolean;
{var thisone : testptr;head : frmlaListptr;}
{var firstone, secondone : TFormula}

{This checks whether there are two contradictory formulas among the}
{antecedent list; if there are their indices is passed back.}

{at the moment tests twice}

  var
   found: boolean;

  function Contradicts (First, second: TFormula): boolean;

  begin
   Contradicts := false;
   if (First.fkind = unary) then {a negation}
    if SpecialEqualFormulas(First.fRlink, second) then
     Contradicts := true;
   if (second.fkind = unary) then {a negation}
    if SpecialEqualFormulas(second.fRlink, First) then
     Contradicts := true;
  end;

  procedure DoToItem (item: TObject);

   function TestItem (seconditem: TObject): boolean;

   begin
    secondone := TFormula(seconditem);
    TestItem := Contradicts(firstone, secondone);
   end;

  begin
   if not found then
    begin
     firstone := TFormula(item);

     if fAntecedents.FirstThat(TestItem) <> nil then
      found := true;
    end;
  end;

 begin
  found := false;

  fAntecedents.Each(DoToItem);

  TwoContradict := found;

 end;


     */



public int  typeOfFormula(TFormula aFormula){

  int  returnType=unknown;

  switch (aFormula.fKind){

    case (TFormula.unary): { //negation (or modalNecessary modalPossible
      if (fParser.isModalNecessary(aFormula))
        return
            modalNecessary;
      if (fParser.isModalPossible(aFormula))
        return
            modalPossible;
      


      if (fParser.isNegation(aFormula)) {

        if (fParser.isModalNecessary(aFormula.fRLink))
          return
              notNecessary;
        if (fParser.isModalPossible(aFormula.fRLink))
          return
              notPossible;
        
        if (fParser.isModalKappa(aFormula.fRLink))
            return
                notModalKappa;
          if (fParser.isModalRho(aFormula.fRLink))
            return
                notModalRho;
        
        
        
        
      }

      if (fParser.isNegation(aFormula)) {

        switch (typeOfFormula(aFormula.fRLink)) {
          case atomic:
            return
                negatomic;
          case negatomic:
          case doubleneg:
          case negarrow:
          case negand:
          case nore:
          case nequiv:
          case neguni:
          case negexi:
          case notNecessary:
          case notPossible:

            return
                doubleneg;
          case implic:
            return
                negarrow;
          case aand:
            return
                negand;
          case ore:
            return
                nore;
          case equivv:
            return
                nequiv;
          case uni:
            return
                neguni;
          case exi:
            return
                negexi;
          case unique:
            return
                negunique;
          case typedUni:
            return
                negTypedUni;
          case typedExi:
            return
                negTypedExi;


          default:
            return
                unknown;

        }
      }
    }

    case TFormula.modalRho:
        if (fParser.isModalRho(aFormula))
            return
                modalRho;
    
    case TFormula.modalKappa:{

        if (fParser.isModalKappa(aFormula)){
        	if (fParser.isModalKappa(aFormula.fRLink))
                return
                    modalDoubleKappa;
        	else
        		return
                modalKappa;}
    }


    	
      case TFormula.binary:  {
        if (fParser.isImplic(aFormula))
           return
              implic;
        else
        if (fParser.isAnd(aFormula))
           return
              aand;
        else
        if (fParser.isOr(aFormula))
           return
              ore;
        else
        if (fParser.isEquiv(aFormula))
           return
              equivv;
        }

      case TFormula.quantifier:{
        if (fParser.isUniquant(aFormula))
           return
              uni;
        else
        if (fParser.isExiquant(aFormula))
           return
              exi;
        else
        if (fParser.isUnique(aFormula))
           return
              unique;

      }

      case TFormula.typedQuantifier:{
  if (fParser.isTypedUniquant(aFormula))
     return
        typedUni;
  else
  if (fParser.isTypedExiquant(aFormula))
     return
        typedExi;


}

  }




   return
      atomic;
 }

    /*

    function TFormula.TypeofFormula: formulatype;

    begin
     TypeofFormula := unknown;

     case SELF.fKind of
      unary:
       case SELF.fRLink.TypeofFormula of
        atomic:
         TypeofFormula := negatomic;
        negatomic, doubleneg, negarrow, negand, nore, nequiv, neguni, negexi:
         TypeofFormula := doubleneg;
        aroww:
         TypeofFormula := negarrow;
        aand:
         TypeofFormula := negand;
        ore:
         TypeofFormula := nore;
        equivv:
         TypeofFormula := nequiv;
        uni:
         TypeofFormula := neguni;
        exi:
         TypeofFormula := negexi;
        unique:
         TypeofFormula := negunique;


        otherwise
       end;
      binary:
       begin
        if (fInfo[1] = chImplic) then
         TypeofFormula := aroww
        else if (fInfo[1] = chAnd) then
         TypeofFormula := aand
        else if (fInfo[1] = chOr) then
         TypeofFormula := ore
        else if (fInfo[1] = chEquiv) then
         TypeofFormula := equivv;
       end;

      quantifier:
       begin
        if (fInfo[1] = chUniquant) then
         TypeofFormula := uni
        else if (fInfo[1] = chExiquant) then
         TypeofFormula := exi
        else if (fInfo[1] = chUnique) then
         TypeofFormula := unique;
       end;

      otherwise
       TypeofFormula := atomic;
     end;

    end;


        */


}
