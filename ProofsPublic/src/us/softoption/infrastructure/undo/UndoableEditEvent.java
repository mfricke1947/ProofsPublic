package us.softoption.infrastructure.undo;

import us.softoption.proofs.TProofController;

public class UndoableEditEvent {
	UndoableEdit fEdit;
	
public UndoableEditEvent(){
		;		
	}	
	
public UndoableEditEvent(TProofController controller,UndoableEdit edit){
	fEdit=edit;		
}

public UndoableEdit getEdit(){
	return
			fEdit;
	
}


	
}