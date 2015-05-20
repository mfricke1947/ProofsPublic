package us.softoption.editor;



/*We don't always know what it is we are going to write to (it may be an applet, in may be a panel in an application)
so we define what we need and force our applets etc to implement it. */



public class TJournalStub implements TJournal{

  public void writeHTMLToJournal(String message,boolean append){};
  public void writeOverJournalSelection(String message){};
  public void writeToJournal(String message, boolean highlight,boolean toMarker){};

}
