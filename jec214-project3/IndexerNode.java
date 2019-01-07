public class IndexerNode{
		
	private Apartment apt;
	private int pricePos;
	private int areaPos;
		
	public IndexerNode(Apartment a, int pp, int ap){
			
		//clone apartment to avoid reference confusion
		apt = new Apartment(a.getAddress(), a.getNum(), a.getCity(), a.getZip(), a.getPrice(), a.getArea());
		pricePos = pp;
		areaPos = ap;
	}
		
	//accessor methods
	public Apartment getApartment(){
		return apt;
	}
		
	public int getPricePos(){
		return pricePos;
	}
		
	public int getAreaPos(){
		return areaPos;
	}
	
	//mutator methods
	public void setPricePos(int pp){
		pricePos = pp;
	}
		
	public void setAreaPos(int ap){
		areaPos = ap;
	}
}