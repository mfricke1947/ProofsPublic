package us.softoption.infrastructure.undo;

public class UndoableEdit {
	boolean fDead=false;	
	boolean fCanUndo=true; // false; not sure
	boolean fCanRedo=false; // false; not sure
	
	
	
	public UndoableEdit(){;}
	


	public boolean	canUndo(){return fCanUndo;}
	public boolean	canRedo(){return fCanRedo;}

public void die(){
	fDead=true;
}

public String	getPresentationName(){return "";}

public String	getRedoPresentationName(){return "Redo";}

public String	getUndoPresentationName(){return "Undo";} 

public void	redo(){}; 

public void	undo(){}; 

public void	setRedoEnabled(boolean value){fCanRedo=value;}; 

public void	setUndoEnabled(boolean value){fCanUndo=value;}; 
			
}