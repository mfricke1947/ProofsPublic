

/*
 The deriver document is both data and an intermediary between the three data panes (the journal,
 the proof, and the drawing).


 The deriver document has all the data (conceptually a file). There is a browser that
 looks at the data. One browser can change data from one document to another document, or you can
 have several browsers each with their own data


 Feb 06 Adding a fourth data panel, for Trees


 */


package us.softoption.editor;




public class TGWTProofDocument{
	TJournal fJournal;
	
public TGWTProofDocument(TJournal itsJournal){
	fJournal=itsJournal;
}

}



