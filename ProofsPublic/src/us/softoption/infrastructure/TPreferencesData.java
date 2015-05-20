package us.softoption.infrastructure;

import com.google.gwt.i18n.client.Dictionary;


public class TPreferencesData {


	 static public boolean fAdvancedMenu=true; //feb 2013
	 static public boolean fBlind=false;   /* suppresses justification on auto derivation. Not something for
    the User to set, but when teaching, with applets, its useful to help
    them think through it */
	 static public boolean fColorProof=true;                   //global values for the prefs.
     static public boolean fDerive=true;               //theorem prover
     static public boolean fEndorseMenu=true;
     static public boolean fGamesMenu=true;
     static public boolean fHTMLMenu=true;
     static public boolean fFirstOrder=false;          // first Order theories, induction
     static public String  fHome="";                   //global values for the prefs.
     static public boolean fIdentity=false;             //functional terms and identity
     static public String  fInputText="";
     static public boolean fInterpretation=true;
     static public boolean fLambda=false;
     static public boolean fModal=false;
     static public boolean fNoCommands=false;
     static public String  fPaletteText=TConstants.fDefaultPaletteText;
     static public String  fParser="default [barwise bergmann copi gentzen hausman herrick howson priest]"; /*the first word is the choice, all are possible values*/
     static public boolean fParseOnly=false;
     static public boolean fPrintDerived=true;       // for printing the "auto' in proofs if a line is derived
     static public boolean fProofs=true;
     static public boolean fPropLevel=false;
     static public boolean fReadFromClipboard=false;  // if no text is selected, will default to clipboard
     static public boolean fRewriteRules=true;
     static public int     fRightMargin=300;//250;
     static public boolean fSetTheory=false;
     static public boolean fSimpleFileMenu=false;
     static public String  fTitle="";  //April 2012
     static public boolean fTrees=true;
     static public boolean fTypeLabels=false;
     static public boolean fUseAbsurd=true;
     static private String fUser="";
     static public String  fJournalSize="";



     //========================================================== constructor
     TPreferencesData() {
    	 resetToDefaults();
     }


public static void readParameters(){
 		
    	 /*  put javascript in host page

    	  var Parameters = {
    	   inputText: "Hello",
    	   shadowColor: "#808080",
    	   errorColor: "#FF0000",
    	   errorIconSrc: "stopsign.gif"
    	 };  	  		
    	  */
	Dictionary params;
    	 		
	try{
		params = Dictionary.getDictionary("Parameters");}
    catch (Exception ex) {return;}
    	 		
    if (params!=null){
    		
       try{String value= params.get("fAdvancedMenu");
     	   if(value.equals("false"))fAdvancedMenu=false;}
       catch (Exception ex){}
       try{String value= params.get("fBlind");
       	   if(value.equals("true"))fBlind=true;}
       catch (Exception ex){}
       try{String value= params.get("fColorProof");
   	   	   if(value.equals("true"))fColorProof=true;}
       catch (Exception ex){}
       try{String value= params.get("fDerive");
	       if(value.equals("true"))fDerive=true;}
       catch (Exception ex){}
       try{String value= params.get("fEndorseMenu");
           if(value.equals("true"))fEndorseMenu=true;}
       catch (Exception ex){}
    	 
       try{String value= params.get("fGamesMenu");
   	   if(value.equals("true"))fGamesMenu=true;}
   catch (Exception ex){}
   try{String value= params.get("fHTMLMenu");
	   	   if(value.equals("true"))fHTMLMenu=true;}
   catch (Exception ex){}
   try{String value= params.get("fFirstOrder");
       if(value.equals("true"))fFirstOrder=true;}
   catch (Exception ex){}
   try{String value= params.get("fIdentity");
       if(value.equals("true"))fIdentity=true;}
   catch (Exception ex){}
 
   try{String value= params.get("fHome");
	   fHome=value;}
catch (Exception ex){}
try{String value= params.get("fInputText");
fInputText=value;}
catch (Exception ex){}
try{String value= params.get("fInterpretation");
   if(value.equals("true"))fInterpretation=true;}
catch (Exception ex){}
try{String value= params.get("fLambda");
if(value.equals("true"))fLambda=true;}
catch (Exception ex){}

try{String value= params.get("fModal");
if(value.equals("true"))fModal=true;}
catch (Exception ex){}
try{String value= params.get("fNoCommands");
if(value.equals("true"))fNoCommands=true;}
catch (Exception ex){}
try{String value= params.get("fPaletteText");
fPaletteText=value;}
catch (Exception ex){}
try{String value= params.get("fParser");
fParser=value;}
catch (Exception ex){}
try{String value= params.get("fParseOnly");
if(value.equals("true"))fParseOnly=true;}
catch (Exception ex){}

try{String value= params.get("fPrintDerived");
if(value.equals("true"))fPrintDerived=true;}
catch (Exception ex){}
try{String value= params.get("fProofs");
if(value.equals("true"))fProofs=true;}
catch (Exception ex){}
try{String value= params.get("fPropLevel");
if(value.equals("true"))fPropLevel=true;}
catch (Exception ex){}
try{String value= params.get("fReadFromClipboard");
if(value.equals("true"))fReadFromClipboard=true;}
catch (Exception ex){}
try{String value= params.get("fRewriteRules");
if(value.equals("true"))fRewriteRules=true;}
catch (Exception ex){}

try{String value= params.get("fRightMargin");
	int margin =300;

	try{margin = Integer.valueOf(value);}
	catch (Exception ex){}

fRightMargin=margin;}
catch (Exception ex){}
try{String value= params.get("fSetTheory");
if(value.equals("true"))fSetTheory=true;}
catch (Exception ex){}
try{String value= params.get("fSimpleFileMenu");
if(value.equals("true"))fSimpleFileMenu=true;}
catch (Exception ex){}

try{String value= params.get("fTitle");
   fTitle=value;}
catch (Exception ex){}

try{String value= params.get("fTrees");
if(value.equals("true"))fTrees=true;}
catch (Exception ex){}


try{String value= params.get("fTypeLabels");
if(value.equals("true"))fTypeLabels=true;}
catch (Exception ex){}
try{String value= params.get("fUseAbsurd");
if(value.equals("true"))fUseAbsurd=true;}
catch (Exception ex){}
try{String value= params.get("fUser");
fUser=value;}
catch (Exception ex){}
try{String value= params.get("fJournalSize");
fJournalSize=value;}
catch (Exception ex){}
    	 	}
}     
     
     
     
     

public static void resetToDefaults(){

// The applets no longer uses this to start with a clean slate


      fAdvancedMenu=true;
      fBlind=false;
      fColorProof=true;                   //global values for the prefs.
      fDerive=true;               //theorem prover
      fEndorseMenu=true;
      fGamesMenu=true;
      fHTMLMenu=true;
      fFirstOrder=false;          // first Order theories, induction
      fIdentity=false;             //functional terms and identity
      fHome="";                   //global values for the prefs.
      fInputText="";
      fInterpretation=true;
      fLambda=false;
      fModal=false;
      fNoCommands=false;
      fPaletteText=TConstants.fDefaultPaletteText;
      fParser="default [barwise bergmann copi gentzen hausman herrick howson priest]"; /*the first word is the choice, all are possible values*/
      fParseOnly=true;
      fPrintDerived=true;       // for printing the "auto' in proofs if a line is derived
      fProofs=true;
      fReadFromClipboard=false;  // if no text is selected, will default to clipboard
      fRewriteRules=true;
      fRightMargin=300;//250;
      fSetTheory=false;
      fSimpleFileMenu=false;
      fTitle="";
      fTrees=true;
      fTypeLabels=false;
      fUseAbsurd=true;
      fUser="";
      fJournalSize="small";

      


}

}//end class PrefTest
