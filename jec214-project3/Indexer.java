import java.util.*;

public class Indexer{
	
	private ArrayList<IndexerNode> indexes;
	
	public Indexer(){		
		indexes = new ArrayList<IndexerNode>();
	}
	
	//add node to indexer
	public void add(Apartment a, int pp, int ap){
		
		IndexerNode ind = new IndexerNode(a, pp, ap);
		indexes.add(ind);
	}
	
	//remove node based street address, apt num, and zip
	public void remove(String addr, String num, int zip){
		
		for(int i=0;i<indexes.size();i++){
			Apartment temp = indexes.get(i).getApartment();
			if(temp.getAddress().equals(addr) && temp.getNum().equals(num) && temp.getZip() == zip){
				indexes.remove(i);
			}
		}
	}
	
	//search based on user entry
	public IndexerNode search(String addr, String num, int zip){
		
		for(int i=0;i<indexes.size();i++){
			Apartment temp = indexes.get(i).getApartment();
			if(temp.getAddress().equals(addr) && temp.getNum().equals(num) && temp.getZip() == zip){
				return indexes.get(i);
			}
		}		
		return null;
	}
	
	public ArrayList<IndexerNode> getIndexes(){
		return indexes;
	}
}