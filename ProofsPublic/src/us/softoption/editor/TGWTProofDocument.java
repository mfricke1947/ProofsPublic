/*
Copyright (C) 2015 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

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



