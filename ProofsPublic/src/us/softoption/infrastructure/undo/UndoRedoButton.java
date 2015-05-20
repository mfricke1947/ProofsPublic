package us.softoption.infrastructure.undo;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class UndoRedoButton extends Button {
	boolean fCanUndo=false;
	
	
	
	public UndoRedoButton(){;}
	
	public UndoRedoButton(String label, ClickHandler handler){
		
		super(label,handler);
	}
	

public void updateUndoState(){
	if (!fCanUndo){
		fCanUndo=true;
		this.setEnabled(true);		
	}
	else{
		fCanUndo=false;
		this.setEnabled(false);		
	}
	
}
	
	
}