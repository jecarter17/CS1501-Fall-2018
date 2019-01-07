public class Apartment{
	
	private String stAdd;
	private String aptNum;	
	private String city;	
	
	private int zip;
	private int price;
	private int area;
	
	public Apartment(String sa, String num, String c, int z, int p, int a){
		stAdd = sa;
		aptNum = num;
		city = c;		
		zip = z;
		price = p;
		area = a;
	}
		
	//accessor methods
	
	public String getAddress(){
		return stAdd;
	}
	
	public String getNum(){
		return aptNum;
	}
	
	public String getCity(){
		return city;
	}	
	
	public int getZip(){
		return zip;
	}
	
	public int getPrice(){
		return price;
	}
	
	public int getArea(){
		return area;
	}	
	
	//mutator methods
	
	public void setAddress(String sa){
		stAdd = sa;
	}
	
	public void setNum(String num){
		aptNum = num;
	}
	
	public void setZip(int z){
		zip = z;
	}
	
	public void setPrice(int p){
		price = p;
	}
	
	public String toString(){
		return stAdd + ", Apt. " + aptNum + ", " + city + ", " + zip + "\nPrice: " + price + "\nArea " + area;
	}
	
	public static void main(String[] args){
		Apartment a = new Apartment("137 N Craig St", "7", "Pittsburgh", 15213, 970, 400);
		System.out.println(a);
	}
}