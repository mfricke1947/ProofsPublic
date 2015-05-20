package us.softoption.infrastructure;

/*This is a 'trick' for using var paramaters with a boolan*/


public class TFlag{
  private boolean value=false;

  public TFlag(){
}


  public TFlag(boolean initial){
    value=initial;
  }


 public boolean getValue(){
   return
       value;
 }

  public void setValue(boolean set){
    value=set;
  }
}
