package us.softoption.tree;

import java.util.HashMap;
import java.util.Map;

import us.softoption.infrastructure.TFlag;

import com.google.gwt.user.client.ui.Tree;

/*This is the GWT substitute fore us.soft.option.interpretation.TGWTTree (which depends on Swing) */


public class TGWTTree extends Tree{
	
	private Map fOldInstantiations= new HashMap();     //of quantified formulas
	private Map fNewInstantiations= new HashMap();     //of quantifiers

	  // The next replace globals in Pascal
	private boolean fExCV = false;     //{indicates need to change variables}
	private boolean fExCVFlag = false;
	private boolean fUniCV = false;    //{indicates need to change variables}
	private boolean fUniCVFlag = false;
	private TFlag fResetNeeded= new TFlag(false);  // used by PrepareQuant and TermsToTry tp show capturing

	 TGWTTestNode fRoot = null;

	
	
	/*A tree can be produced in two 'modes': an ordinary mode and a change variable mode. When trees are
	  produced, eg by doDerive() or selectionSatisfiable() they first run in ordinary mode. But if they detect
	  that there is a problem with capturing of variables they set either fCVFlag or fUniCVFlag. If the
	  tree then turns out to be open (ie not valid), the whole tree, or whole test, can be run again, this time
	  with fExCV or fUniCV set and the whole tree will run in change variable mode. So the flags signal the need
	  for change of mode, then the calling routines use that if they want to.*/


	 public TGWTTree(){
		//TO DO	    super(root);

			  }
	  
	 
	 public TGWTTree(TGWTTestNode root){
//TO DO	    super(root);

	  }

	  public TGWTTree(TGWTTestNode root, boolean asksAllowsChildren){
		//TO DO		    super(root,asksAllowsChildren);
	  }


	  
	public TGWTTree shallowCopy(TGWTTestNode root){
	  TGWTTree shallow= new TGWTTree(root);

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

	public void resetState(TGWTTree oldState){
	  fOldInstantiations= oldState.fOldInstantiations;     //of quantified formulas
	  fNewInstantiations= oldState.fNewInstantiations;     //of quantifiers

	  fExCV = oldState.fExCV;     //{indicates need to change variables}
	  fExCVFlag = oldState.fExCVFlag;
	  fUniCV = oldState.fUniCV;    //{indicates need to change variables}
	  fUniCVFlag = oldState.fUniCVFlag;
	  fResetNeeded= oldState.fResetNeeded;
	}


	 
	public int getDepth(){
		if (fRoot!=null)
			return fRoot.getDepth();
		else
			return
					0;
	}
	
	public void resetForCV(){  //detach the children and give new context
		//TO DO		    Object root=getRoot();
		//TO DO		    int count= getChildCount(getRoot());

		//TO DO		    for (int i=count-1;i>-1;i--){
	    	//TO DO		      removeNodeFromParent((MutableTreeNode)getChild(root,i));
		//TO DO	   }

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