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


import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import us.softoption.infrastructure.TFlag;

/*

In Pascal .....

(***********fInfo Accessors ******************)

{occasionally we use spare portions of the fInfo field to store information.}
{Usually the fInfo is of length1 but sometimes index 2 tells of the number of}
{instantiations, and index 3 tells of the index to the stringstore which contains}
{a list of them. Sometimes when changing variables the field is used to store}
{info on the new variable.}

{$S semantictest}

 function NewVariable (root: TFormula): char;
 begin
  NewVariable := root.fInfo[2];
 end;

 procedure StoreNewVariable (var root: TFormula; newvariable: char);
  var
   temp: string[1];
 begin
  temp := 'a';
  temp[1] := newvariable;
  root.fInfo := concat(copy(root.fInfo, 1, 1), temp);
 end;

 function NoOfInstantiations (root: TFormula): integer;
 begin
  if (length(root.fInfo) < 2) then
   NoOfInstantiations := 0
  else
   NoOfInstantiations := ord(root.fInfo[2]);
 end;

 procedure RemoveInstantiatingInfo (var root: TFormula);
  var
   temp, temp2: string[1];
 begin
  root.fInfo := copy(root.fInfo, 1, 1);
 end;

 procedure StoreNoOfInstantiationsAndIndex (var root: TFormula; no, index: integer);
  var
   temp, temp2: string[1];
 begin
  temp := 'a';
  temp[1] := chr(no);
  temp2 := 'a';
  temp2[1] := chr(index);
  root.fInfo := concat(copy(root.fInfo, 1, 1), temp, temp2);
 end;


 function StringStoreIndex (root: TFormula): integer;
 begin
  if (length(root.fInfo) < 3) then
   StringStoreIndex := 0
  else
   StringStoreIndex := ord(root.fInfo[3]);
 end;

(****************************************)


*/



/*

In Java.....

We are just going to use a map from the objects (ie  TFormulas) to strings.

static Map gStringstore= new HashMap();    //  (*used for the substitutions in quantifiers*)

Take care if you copy a formula, for you need also to look after a new map entry.



*/



public class TTreeModel extends DefaultTreeModel{

private Map fOldInstantiations= new HashMap();     //of quantified formulas
private Map fNewInstantiations= new HashMap();     //of quantifiers

  // The next replace globals in Pascal
private boolean fExCV = false;     //{indicates need to change variables}
private boolean fExCVFlag = false;
private boolean fUniCV = false;    //{indicates need to change variables}
private boolean fUniCVFlag = false;
private TFlag fResetNeeded= new TFlag(false);  // used by PrepareQuant and TermsToTry tp show capturing

  /*A tree can be produced in two 'modes': an ordinary mode and a change variable mode. When trees are
  produced, eg by doDerive() or selectionSatisfiable() they first run in ordinary mode. But if they detect
  that there is a problem with capturing of variables they set either fCVFlag or fUniCVFlag. If the
  tree then turns out to be open (ie not valid), the whole tree, or whole test, can be run again, this time
  with fExCV or fUniCV set and the whole tree will run in change variable mode. So the flags signal the need
  for change of mode, then the calling routines use that if they want to.*/



  public TTreeModel(TreeNode root){
    super(root);

  }

  public TTreeModel(TreeNode root, boolean asksAllowsChildren){
    super(root,asksAllowsChildren);
  }


public TTreeModel shallowCopy(TreeNode root){
  TTreeModel shallow= new TTreeModel(root);

  shallow.fOldInstantiations= fOldInstantiations;     //of quantified formulas
  shallow.fNewInstantiations= fNewInstantiations;     //of quantifiers

  shallow.fExCV = fExCV;     //{indicates need to change variables}
  shallow.fExCVFlag = fExCVFlag;
  shallow.fUniCV = fUniCV;    //{indicates need to change variables}
  shallow.fUniCVFlag = fUniCVFlag;
  shallow.fResetNeeded= fResetNeeded;

  return
      shallow;
}

public void resetState(TTreeModel oldState){
  fOldInstantiations= oldState.fOldInstantiations;     //of quantified formulas
  fNewInstantiations= oldState.fNewInstantiations;     //of quantifiers

  fExCV = oldState.fExCV;     //{indicates need to change variables}
  fExCVFlag = oldState.fExCVFlag;
  fUniCV = oldState.fUniCV;    //{indicates need to change variables}
  fUniCVFlag = oldState.fUniCVFlag;
  fResetNeeded= oldState.fResetNeeded;
}


  public void resetForCV(){  //detach the children and give new context
    Object root=getRoot();
    int count= getChildCount(getRoot());

    for (int i=count-1;i>-1;i--){
      removeNodeFromParent((MutableTreeNode)getChild(root,i));
    }

    fOldInstantiations= new HashMap();     //of quantified formulas
    fNewInstantiations= new HashMap();     //of quantifiers

    fExCV = true;     //{indicates need to change variables}

    fResetNeeded.setValue(false);


  }

  public Map getNewInstantiations(){
    return
        fNewInstantiations;
  }

  public Map getOldInstantiations(){
    return
        fOldInstantiations;
  }

  TFlag getResetNeeded(){
    return
        fResetNeeded;
  }

  void setExCVFlag(boolean value){
    fExCVFlag=value;

   }

   void setUniCVFlag(boolean value){
    fUniCVFlag=value;

   }


public boolean getExCV(){
       return
           fExCV;

      }

public boolean getExCVFlag(){
             return
                 fExCVFlag;

            }

            public boolean getUniCV(){
                   return
                       fUniCV;

                  }
                  public boolean getUniCVFlag(){
                               return
                                   fUniCVFlag;

                              }


}
