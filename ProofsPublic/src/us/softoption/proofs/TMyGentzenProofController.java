package us.softoption.proofs;

import us.softoption.editor.TJournal;
import us.softoption.editor.TReset;
import us.softoption.parser.TGentzenParser;
import us.softoption.parser.TParser;

import com.google.gwt.user.client.ui.VerticalPanel;

public class TMyGentzenProofController extends TMyProofController{
	
		
		
		public TMyGentzenProofController(){;

		}
		
		public TMyGentzenProofController(TParser aParser, TReset aClient,TJournal itsJournal, VerticalPanel inputPanel,
				 TProofDisplayCellTable itsDisplay){
			
			super(aParser,aClient,itsJournal,inputPanel,itsDisplay);
			
			
		}	
	
		@Override	
		void initializeParser(){
			  fParser=new TGentzenParser();
			};	
	
	

}