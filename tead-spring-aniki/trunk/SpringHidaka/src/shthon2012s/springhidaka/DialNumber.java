package shthon2012s.springhidaka;

public class DialNumber {

	private String Name;
	private String Number;


	public DialNumber(String s1,String s2){
		Name=s1;
		Number=s2;
	}

	public String getName(){
		return Name;
	}
	public String getNumber(){
		return Number;
	}
}
