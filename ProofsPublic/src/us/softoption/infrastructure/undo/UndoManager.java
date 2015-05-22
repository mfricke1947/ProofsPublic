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