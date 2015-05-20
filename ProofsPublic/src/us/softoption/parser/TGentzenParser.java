package us.softoption.parser;

import java.io.Reader;

/*We subclass here only to get the name 'Gentzen' on for clear code elsehwere Jan 2013*/


public class TGentzenParser extends TParser{
	
	/************************** constructors **********************************/

	public TGentzenParser(){

	}

	public TGentzenParser(Reader aReader, boolean firstOrder)
	      {
	      super(aReader,firstOrder);
	      }
	
	
}





