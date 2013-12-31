package us.softoption.infrastructure.undo;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class RedoButton extends Button {
	
	
	
	
	boolean fCanRedo=false;
	
	
	
	public RedoButton(){;}
	
	public RedoButton(String label, ClickHandler handler){
		
		super(label,handler);
	}
	

public void updateRedoState(){
	if (!fCanRedo){
		fCanRedo=true;
		this.setEnabled(true);		
	}
	else{
		fCanRedo=false;
		this.setEnabled(false);		
	}
	
}
	
	
	
	
	
	
	
	
}