
3. 1/10/2014 Barwise Tree Controller finished (and working, though not thoroughly tested)

2. 1/5/2014 Initial Commit to GIT (no Barwise Anacon Tree)

--------------------------------------------

Java Swing has the facilities for making trees using

JTree  (the visual side of a tree)  

and

DefaultMutableTreeNode (which are the nodes in it).

We do not use JTree at all, but we do use

DefaultMutableTreeNode

to build the trees. They can be added to, siblings found, etc.

DefaultMutableTreeNode implements the Swing Interface TreeNode

[Note, softoption classes start with Upper Case T, so none of JTree, DefaultMutableTreeNode,
and (the Interface) TreeNode are softoptionclasses.]

Now, DefaultMutableTreeNode can contain data, strings etc. or, in the more general case UserObjects

So, you stuff a UserObject (say the name of a file) into DefaultMutableTreeNode, do this several times, 
building a tree. Thus you can build a tree of filenames.

In our case, we want the UserObject to have information lists of formulas as premises, formulas as 
conclusions etc. and we have a data structure to do this i.e.

TTestNode

This is a data structure that has been in use since the code was it Pascal. It was for building semantic
trees for some internal tests (hence it is in us.softoftion.interpretation)

When this code was brought into Java/Swing it was convenient to let Swing do the tree building and so
there is a field on TTestNode

public DefaultMutableTreeNode fTreeNode= new DefaultMutableTreeNode(this);

But actually we want to add even more fields to this so we subclass it to get in TTreeDataNode.java

TTreeDataNode subclass of TTestNode

So the trees are built of DefaultMutableTreeNode and the UserObjects in these are TTreeDataNodes

So, for example, when starting a Tree from a string (say in Load(str) routine), you will see code like

TTreeDataNode newNode= (TTreeDataNode)(fTreeDataRoot.supplyTTestNode(fParser,fTreeModel));   //using one node for each formula
              newNode.addToAntecedents(root);

which is creating our 'data', then later there might be a procedure StraightInsert that puts it into a tree

straightInsert(currentNode,newNode,null);

which uses

void straightInsert(TTreeDataNode at, TTreeDataNode left, TTreeDataNode right) into a tree by
calling the TTreeDataNode method

public void straightInsert(TTestNode leftNode, TTestNode rightNode, DefaultMutableTreeNode hostRoot, 
int depth)

which puts it/them (or at least the value of the requisite field in a tree).

Now the field fTreeNode on TTreeDataNode

public DefaultMutableTreeNode fTreeNode= new DefaultMutableTreeNode(this);

tells which is the underlying Swing node. And, going the other way

TTreeDataNode data = (TTreeDataNode) (swingNode.getUserObject) will get it back.

-------------------

Swing Trees usually have TreeModels (Interface TreeModel, with Default Tree Model Class) to pick out 
the root of a tree, get the children, add listeners,
fire TreeModel events when the tree is changed etc.

We have such a thing, TTreeMode in us.softoftion.interpretation

TTreeModel subclass of DefaultTreeModel

its main use is for theorem provided.

You may see fTreeModel as a field used in various routines. It is this that will produce the Swing
root to a tree, deal with listeners etc.

------------------
DISPLAY

for one reason or another we want to display the tree in a table, so this is like a spreadsheet or
... a table.

To do this we use a GWT CellTable but we don't know how many columns it might have so we have
to use a trick to parameterize these. And we subclass it, the result is

public class TTreeDisplayCellTable extends CellTable<Object[]>{

That also has, or usually has a model and that is

public class TTreeDisplayTableModel extends AbstractTableModel{

this looks after mapping an inverted tree into a table

-----------
LISTENERS AND UPDATES

changes in the swing tree need to cause changes in the display (but there is no direct
entry into the display, so changes in the display do not have to cause changes in underlying
data. Changes to the data are produced by menu choice.

So, a menu choice might logically add a line. That changes the Swing Tree and its TreeModel knows about 
that and fires a something-has-changed. The TTreeDisplayTableModel is listening for changes in the tree.
If there are any it brings itself into synch. Similarly TTreeDisplayCellTable is listening to its model
(TTreeDisplayTableModel), and changes there cause it to update itself.

----------

DOCUMENT

There is also TTreeDocument to tie all these things together.

----------
TYPICAL LAUNCH SEQUENCE

	((TTreeDisplayCellTable)fTreeCellTable).startTree(fFilteredInput);  is called on 
	
	TTreeDisplayCellTable
	
	
	
	--------------
	
	TROUBLE WITH SOURCE CODE DEPENCIES
	
	No source code is available for type 
com.google.gwt.user.server.rpc.RemoteServiceServlet;  

You have the following error:
[ERROR] Line 11: No source code is available for type com.google.gwt.junit.client.GWTTestCase;
did you forget to inherit a required module?


Solution :

In your module file(*.gwt.xml), simply add :
"< name="com.google.gwt.junit.JUnit">"

<name="javax.swing.tree.DefaultTreeModel"/>

seems not to work

-------------------
-------------------

package us.softoption.tree;



/*
 This is to provide 'backroom' support for Trees in javascript


 */





import static us.softoption.infrastructure.Symbols.chDoubleArrow;
import static us.softoption.infrastructure.Symbols.chInsertMarker;
import static us.softoption.infrastructure.Symbols.chLambda;
import static us.softoption.infrastructure.Symbols.chModalNecessary;
import static us.softoption.infrastructure.Symbols.chModalPossible;
import static us.softoption.infrastructure.Symbols.chSmallLeftBracket;
import static us.softoption.infrastructure.Symbols.chSmallRightBracket;
import static us.softoption.infrastructure.Symbols.setTheorySymbols;
import static us.softoption.infrastructure.Symbols.strCR;
import static us.softoption.infrastructure.Symbols.strNull;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.softoption.editor.TJournal;
import us.softoption.infrastructure.Symbols;
import us.softoption.infrastructure.TConstants;
import us.softoption.infrastructure.TPreferencesData;
import us.softoption.interpretation.TTreeModel;
import us.softoption.parser.TFormula;
import us.softoption.parser.TParser;

import com.google.gwt.user.cellview.client.CellTable;


public class TTreeController{


//  public TDeriverApplication fApplication=null;


  private TJournal fJournal;       //interface to receive writing  (sometimes a Browser sometimes an applet)
                                   // can be null in some cases, so check for it.


 // public TTreeCellTable fGridPanel; 
  
  public CellTable<Object[]> fGridPanel= null;//new TTreeCellTable();  //compiler won't accept
  //TTreeCellTaable as typep here


  protected TParser fParser;
  public String fParserName="";        //default, gets shown in title bar of browser, subclasses change

  /*
   fInterpretationList: TList; {list of formulas generated by open}
{                                                                  node in statisfaiable}

  */
  
  String fStartStr="";
  String fTreeStr="";
  TTreeDataNode fTreeDataRoot = null;  // the root of the tree, but as a soft option TestNode, not as the 
  									// Swing Node that holds it
  TTreeModel fTreeModel= null;
  
  TTreeDisplayTableModel fTreeTableModel=null;

  private boolean fDirty = false;  // changes not saved

  public static boolean kHIGHLIGHT=true;
  public static boolean kTO_MARKER=true;

  public static int fVersion=1;    //file format version

  public String fBasicPalette= "";

  public String fDefaultPaletteText="";

  boolean fUseIdentity=false;    // for getting more menu items independently of Preferences


  
  

  
  
  
  
  public TTreeController(){
    
	  initializeParser();
	  initializePalettes();

	  fGridPanel = new TTreeDisplayCellTable(this);



  }



  //public TDeriverDocument(TDeriverApplication itsApplication/*, TJournal itsJournal*/){

  // this();

      //   fApplication=itsApplication;


    /*at the moment the JournalPane is just text (not subclassed) so, for
    the present it is part of the Browser. The main edit menu (and other menus)
    in the Browser, refer to the journalPane. So it is natural to leave it there,*/


 // fBrowser= new TBrowser(this,itsApplication);
//  fJournal=new TBrowser(this,itsApplication); /*fBrowser;*/
 // }

  public TTreeController(TJournal itsJournal){   // when it is running without a Browser, eg in an applet

 this();

    //   fApplication=itsApplication;

  /*at the moment the JournalPane is just text (not subclassed) so, for
  the present it is part of the Browser. The main edit menu (and other menus)
  in the Browser, refer to the journalPane. So it is natural to leave it there,*/

//fBrowser= null;
fJournal=itsJournal;
}

  public TTreeController(TJournal itsJournal,boolean wantsIdentity){
    fUseIdentity=wantsIdentity;

    initializeParser();



initializePalettes();

/*

fTreePanel = new TTreePanel(this);

 */



fJournal=itsJournal;
}



  void initializePalettes(){  // for override

   fBasicPalette= " \u223C  "+fParser.renderAnd()+"  \u2228  \u2283  \u2261  \u2200  \u2203  \u2234 ";

   fDefaultPaletteText=strCR+   /* F therefore F and G not  and  or  implic  bi  all  exists  therefore   */

                          "F \u2234 F "+fParser.renderAnd()+" G"+

                          " \u223C  "+fParser.renderAnd()+"  \u2228  \u2283  \u2261  \u2200  \u2203  \u2234 " +

                          (TPreferencesData.fModal?(chModalNecessary+" "+chModalPossible+" "):"")+
                          (TPreferencesData.fLambda?(chLambda+" " +chDoubleArrow):"")+
                          (TPreferencesData.fSetTheory?(" " +setTheorySymbols):"")+
                          strCR+
                          strCR+

                     " Rxy[a/x,b/y] (\u2200x)(Fx \u2283 Gx)"
;


  }



  public void initializeParser(){

     fParser= new TParser(); // default

  }

  public void setDirty(boolean dirty){
    fDirty=dirty;
  }

  public boolean isDirty(){
   return
       fDirty;
 }

 public TJournal getJournal(){
  return
      fJournal;
}

public void setJournal(TJournal itsJournal){
  fJournal=itsJournal;

}


/************ Starting a Tree *******************/


void removeInputPane(){
	
/*	
	
    if (fInputPane!=null){
      fInputPane.setVisible(false);
       this.remove(fInputPane);

        fInputPane=null;


  enableMenus();
    }
    
    */
  }



//public void startTree(String inputStr){
 // fTreePanel.startTree(inputStr);
//}
//
public void startTree(String inputStr){


	   // dismantleProof(); //{previous one}

	    removeInputPane();

	    initTree();

	   if (load(inputStr))
	   //   startUp()
	   ;

	  fStartStr=inputStr;  //for restart

	  }






void initTree(){
//	TO DO   jScrollPane1.getViewport().remove(fTreeTableView);

	   initializeTreeModel();

//		TO DO     fTreeTableModel = new TTreeTableModel(/*fTreeModel,*/ /*fTreeDataRoot); // this is where the data is

//		TO DO  	   fTreeTableView = new TTreeTableView(fTreeTableModel);

//		TO DO  	   jScrollPane1.getViewport().add(fTreeTableView, null); 
	 }



/*The next bit is a kludge. Unfortunately the premises are separated by commas, and also subterms within
compound terms eg Pf(a,b),Hc.

Also in some systems a relation Lxy is written L(x,y) ie also with commas


We want to separate the premises but not the terms. So we will change the
premise comma separators to another character. For the moment '!'*/


private static char chSpecialSeparator='#';  //was ! ie unique!

private String changeListSeparator(String input){

int nested=0;
char currCh;

StringBuffer output= new StringBuffer(input);
for (int i=0;i<input.length();i++){
currCh=output.charAt(i);

if (currCh==chSmallLeftBracket)
nested++;
if (currCh==chSmallRightBracket)
nested--;

if ((nested<1)&&(currCh==Symbols.chComma))    //commas separating the list of premises are not nested
output.setCharAt(i,chSpecialSeparator);
}

return
output.toString();
}


boolean load(String inputStr){                       //similar routine in TMyProofPanel

	   TTreeDataNode currentNode=null;
	   int lineNo=1;

	  fParser.initializeErrorString();



	   ArrayList dummy=new ArrayList();
	   boolean wellformed = true;

	   fTreeStr="";  //re-initialize; the old proof may still be there and if this turns out to be illformed will stay there


	        if ((inputStr==null)||inputStr==strNull){
	          return
	              false;                // cannot be empty for a tree
	        }

	 String[]premisesAndConclusion = inputStr.split(String.valueOf(Symbols.chTherefore),2);  /* they may input two
	         therefore symbols, in which case we'll split at the first and let the parser report the second */

	    if (premisesAndConclusion[0]!=strNull){  // there are premises


	      premisesAndConclusion[0]=changeListSeparator(premisesAndConclusion[0]);  //kludge


	      StringTokenizer premises = new StringTokenizer(premisesAndConclusion[0],String.valueOf(chSpecialSeparator));

	   while ((premises.hasMoreTokens())&&wellformed){
	      inputStr=premises.nextToken();

	      if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
	             TFormula root = new TFormula();
	             StringReader aReader = new StringReader(inputStr);


	             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

	             if (!wellformed){

	              /* fTreeDocument.*/writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
	            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
	             }
	             else
	                 {//addPremise(root);

	                  if (currentNode==null)          //first one is a special case
	                  {fTreeDataRoot.addToAntecedents(root);
	                   fTreeDataRoot.fJustification=setMember;
	                   if (fParser.containsModalOperator(root))
	                     fTreeDataRoot.fWorld=fParser.startWorld();
	                   fTreeDataRoot.fLineno=lineNo;

	                   lineNo++;

	                   fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,fTreeDataRoot);              //need a listener for this, need it no Insert
	                    currentNode=fTreeDataRoot;
	                  }
	                  else{
	                    TTreeDataNode newNode =(TTreeDataNode)(fTreeDataRoot.supplyTTestNode(fParser,fTreeModel));   //using one node for each formula
	                    newNode.addToAntecedents(root);
	                    newNode.fJustification=setMember;
	                    if (fParser.containsModalOperator(root))
	                     newNode.fWorld=fParser.startWorld();
	                    newNode.fLineno=lineNo;
	                    straightInsert(currentNode,newNode,null);  // change
	                 //   fTreeTableModel.treeChanged(TTreeTableModel.ROWCHANGE,newNode);              //need a listener for this
	                    currentNode=newNode;

	                    lineNo++;
	                  }



	                 if (fTreeStr.length()==0)
	                   fTreeStr=inputStr;
	                 else
	                   fTreeStr+=Symbols.chComma+inputStr;
	                 }
	             }
	      }          // done with premises

	      if (premisesAndConclusion.length>1){  // if there is no therefore the original 'split' won't split the input
	        inputStr = premisesAndConclusion[1];

	        if (inputStr!=strNull){   // can be nullStr if input starts with therefore, or they put two commas togethe,should just skip
	             TFormula root = new TFormula();
	             StringReader aReader = new StringReader(inputStr);

	             wellformed=fParser.wffCheck(root, /*dummy,*/ aReader);

	             if (!wellformed){
	             //  (fParser.fParserErrorMessage.toString()).replaceAll(strCR, "");
	              /* fTreeDocument.*/writeToJournal(fParser.fCurrCh + TConstants.fErrors12 + 
	            		   (fParser.fParserErrorMessage.toString()).replaceAll(strCR, ""), TConstants.HIGHLIGHT, !TConstants.TO_MARKER);
	             }else
	                 {//addConclusion(root);

	                   TFormula newFormula = new TFormula();

	                   newFormula.fKind = TFormula.unary;
	                   newFormula.fInfo = String.valueOf(Symbols.chNeg);
	                   newFormula.fRLink = root;

	                   root=newFormula;
	                   // add its negation
	                   if (currentNode==null)          //first one is a special case
	                     {fTreeDataRoot.addToAntecedents(root);
	                       fTreeDataRoot.fJustification=setMember;
	                       if (fParser.containsModalOperator(root))
	                          fTreeDataRoot.fWorld=fParser.startWorld();
	                       fTreeDataRoot.fLineno=lineNo;

	                       lineNo++;   // really no need, there are no more

	                       fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,fTreeDataRoot);              //need a listener for this, need it no Insert
	                       currentNode=fTreeDataRoot;
	                     }
	                     else{

	                       TTreeDataNode newNode = (TTreeDataNode) (fTreeDataRoot.supplyTTestNode(fParser,
	                           fTreeModel)); //using one node for each formula
	                       newNode.addToAntecedents(root);
	                       newNode.fJustification = setMember;
	                       if (fParser.containsModalOperator(root))
	                          newNode.fWorld=fParser.startWorld();
	                       newNode.fLineno = lineNo;
	                       straightInsert(currentNode,newNode, null);
	                       fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE, newNode); //need a listener for this
	                       currentNode = newNode;
	                     }

	                 fTreeStr+=Symbols.chTherefore+inputStr;
	                 }
	             }

	      }

	    };

	        return
	            wellformed;

	     }




/************/



public boolean getUseIdentity(){
    return
        fUseIdentity;
  }

public void setUseIdentity(boolean use){
          fUseIdentity=use;
  }


/*************************************************************************/	


void straightInsert(TTreeDataNode at, TTreeDataNode left, TTreeDataNode right){

  /*had a lot of trouble getting the update to display properly. Found, and fixed, the bug, Dec 23*/

  int depth = fTreeTableModel.getTreeDepth();

  at.straightInsert(left, right,fTreeDataRoot.fSwingTreeNode,depth); //this updates the tree

  fTreeTableModel.updateCache();
  fTreeTableModel.treeChanged(TTreeDisplayTableModel.ROWCHANGE,null);  //need/have a listener for this

 // fTreeTableView.resetWidths();

//  fTreeTableView.doLayout();
  
  /*  TO DO

deSelectAll();
tellListeners(new UndoableEditEvent(this,null));

*/

}

void splitInsert(TTreeDataNode at, TTreeDataNode left, TTreeDataNode right){

/*had a lot of trouble getting the update to display properly. Found, and fixed, the bug, Dec 23*/

  int depth = fTreeTableModel.getTreeDepth();

  at.splitInsert(left,right,fTreeDataRoot.fSwingTreeNode,depth);                       //this updates the tree

 // int [] newWidths = fTreeTableView.calculateWidths(fTreeTableModel.getItsColumn(at.fSwingTreeNode));  // column widths, uses oldCache

  fTreeTableModel.updateCache();                    // this updates the table data based on the tree

  fTreeTableModel.treeChanged(TTreeDisplayTableModel.COLCHANGE,null);              //need a listener for this

  /* TO DO
  fTreeTableView.resetWidths2(fTreeDataRoot);

  fTreeTableView.doLayout();

  deSelectAll();
  tellListeners(new UndoableEditEvent(this,null));
  
  */
}

void splitInsertTwo(TTreeDataNode at, TTreeDataNode left,TTreeDataNode left2,
                    TTreeDataNode right,TTreeDataNode right2){

 int depth = fTreeTableModel.getTreeDepth();

 at.splitInsertTwo(left,left2,right,right2,fTreeDataRoot.fSwingTreeNode,depth);                       //this updates the tree

 fTreeTableModel.updateCache();                    // this updates the table data based on the tree

 fTreeTableModel.treeChanged(TTreeDisplayTableModel.COLCHANGE,null);              //need a listener for this

 /*  TO DO
 
 fTreeTableView.resetWidths2(fTreeDataRoot);

 fTreeTableView.doLayout();

 deSelectAll();
 tellListeners(new UndoableEditEvent(this,null));
 
 */
}


/**************************** End of Extension rules *****************************/






/*

    class TreeQuiz extends JMenuItem{

          public TreeQuiz(final TDeriverDocument aDocument) {
            setText("Tree Quiz");
            addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {

                if (TTreeQuiz.fNumOpen == 0) {



                  TTreeQuiz quiz = new TTreeQuiz(fParser, aDocument);
                  quiz.setVisible(true);
                }
              }

            });
          }
         }
         
 */        

/****************************/


void clearProofAndDrawing(){
  
 // fTreePanel.resetToEmpty();
}


  public void writeToJournal(String message, boolean highlight,boolean toMarker){
    if (fJournal==null)
      System.out.println("writeToJournal() called with null Journal. With applets might not be an error.");
    else
      fJournal.writeToJournal(message, highlight, toMarker);
  }




/*

 procedure TDeriverDocument.WriteTruePropositions;
   var
    lengthofStr: integer;
    tempStr: str255;
    charIndex: CHAR;
  begin
   tempStr := strNull;

   for charIndex := 'A' to 'Z' do
    if fCurrentPropositions[charIndex] then
     tempStr := concat(tempStr, charIndex);

   lengthofStr := length(tempStr);
   if lengthofStr > 0 then
    begin
     while lengthofStr > 1 do
      begin
       insert(',', tempStr, lengthofStr);
       lengthofStr := lengthofStr - 1;
      end;

     tempStr := concat('True Atomic Propositions = { ', tempStr, ' }', gCR);

     WriteToJournal(tempStr, kHIGHLIGHT, not kTConstants.TO_MARKER);
    end
   else
    begin
     tempStr := concat('All atomic propositions are assigned false.');

     WriteToJournal(tempStr, kHIGHLIGHT, not kTConstants.TO_MARKER);
    end;
  end;


*/



public TParser getParser(){
  return
      fParser;
}

 











/*

  function TDeriverDocument.FormulaTrue (root: TFormula): BOOLEAN;

var
 itsTruth: BOOLEAN;
 itsMainConnective, dummy: CHAR;

begin
itsTruth := FALSE;

case root.fKind of
 predicator:
  itsTruth := AtomicFormulaTrue(root); {atomic}
 equality:
  itsTruth := EqualityTrue(root); {equality}
 unary:
  itsTruth := not FormulaTrue(root.fRlink); {negation}
 binary:
  begin
   itsMainConnective := root.fInfo[1];
   case itsMainConnective of
    chAnd:
     itsTruth := FormulaTrue(root.fLlink) and FormulaTrue(root.fRlink);
    chOr:
     itsTruth := FormulaTrue(root.fLlink) or FormulaTrue(root.fRlink);
    chImplic:
     itsTruth := (not FormulaTrue(root.fLlink)) or FormulaTrue(root.fRlink);
    chEquiv:
     itsTruth := ((not FormulaTrue(root.fLlink)) or FormulaTrue(root.fRlink)) and ((not FormulaTrue(root.fRlink)) or FormulaTrue(root.fLlink));

    otherwise
   end;
  end;
 quantifier:
  begin
   itsMainConnective := root.fInfo[1];
   case itsMainConnective of
    chUniquant:
     itsTruth := UniquantTrue(root, dummy);
    chExiquant:
     itsTruth := ExiquantTrue(root, dummy);

    otherwise
   end;
  end;

 otherwise

end;
FormulaTrue := itsTruth;
end;


*/






  

  //CHECK MAYBE ERROR HERE IF SEVERAL FREE VARIABLES

void placeMarker(){
  writeToJournal(""+chInsertMarker, !TConstants.HIGHLIGHT, TConstants.TO_MARKER);
}

}







