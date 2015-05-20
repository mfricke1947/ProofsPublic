package us.softoption.infrastructure;

//11/1/08

public interface FunctionalParameter{
	void execute (Object parameter);   /* This is a bit of 'cleverness' to allow procedural and
	     functional parameters. Elsewhere we will subclass a class (usually as an inner class) to implement
	      this interface. Then that object will get passed as a parameter to something else which will just
	call execute on it. So, for example, we could have EachItemDo(function) which runs down a list applying
	whatever function is passed*/

	boolean testIt(Object parameter);

	}