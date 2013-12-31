package us.softoption.infrastructure.undo;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class UndoButton extends Button {
	boolean fCanUndo=false;
	
	
	
	public UndoButton(){;}
	
	public UndoButton(String label, ClickHandler handler){
		
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