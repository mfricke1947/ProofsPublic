package us.softoption.infrastructure.undo;

import java.util.ArrayList;

public class UndoManager {
	
int fAddIndex=0;
ArrayList fEdits= new ArrayList();

public UndoManager(){;}

public void addEdit (UndoableEdit edit){
fEdits.add(edit);
fAddIndex+=1;

}

protected UndoableEdit nextLiveEdit()
{
  UndoableEdit result;

  /*
  
 if (fAddIndex>0&&fAddIndex<(fEdits.size()+1))
	 return
			(UndoableEdit)(fEdits.get(fAddIndex-1));  */
  
  if (fEdits.size()>0)
		 return
				(UndoableEdit)(fEdits.get(fEdits.size()-1));
 else
	 return null;
}
	

public boolean	canRedo(){
	  UndoableEdit edit;
	    edit = nextLiveEdit();
	    return edit != null && edit.canRedo();	}

public boolean	canUndo(){
	
	  UndoableEdit edit;
	    edit = nextLiveEdit();
	    return edit != null && edit.canUndo();	
	} 

public void	die(){}; 

public String	getPresentationName(){return "";}

public String	getRedoPresentationName(){return "Redo";}

public String	getUndoPresentationName(){return "Undo";} 

public void	redo(){
	  UndoableEdit edit;
	    edit =nextLiveEdit();
	    if (edit!= null) 
	    	edit.redo();	
}; 

public void	undo(){
	
	  UndoableEdit edit;
	    edit = nextLiveEdit();
	    if (edit!= null) 
	    	edit.undo();	
}; 
		
	
}