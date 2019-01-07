import java.util.*;

public class Tracker{
	
	private ArrayList<Apartment> pricePQ;
	private ArrayList<Apartment> areaPQ;
	private Indexer indexer;
	
	//constructor
	public Tracker(){
		pricePQ = new ArrayList<Apartment>();
		areaPQ = new ArrayList<Apartment>();
		indexer = new Indexer();
	}
	
	//************************
	//Primary methods
	//************************
	
	public void add(Apartment apt){
		
		//add to indirection structure
		indexer.add(apt, pricePQ.size(), areaPQ.size());
		
		//add to pricePQ
		int newPricePos = addPrice(apt);
		
		//add to areaPQ
		int newAreaPos = addArea(apt);	
		
		//updates indirection structure after add
		ArrayList<IndexerNode> indexes = indexer.getIndexes();
		indexes.get(indexes.size()-1).setPricePos(newPricePos);
		indexes.get(indexes.size()-1).setAreaPos(newAreaPos);
	}
	
	//update apartment price
	public void update(String addr, String num, int zip, int newPrice){
		
		//search for apartment
		IndexerNode aptNode = indexer.search(addr, num, zip);
		
		if(aptNode != null){		
			
			//get position information from found node
			int currPricePos = aptNode.getPricePos();
			int currAreaPos = aptNode.getAreaPos();
			int currPrice = aptNode.getApartment().getPrice();
			
			//update price in indexer, pricePQ, and areaPQ
			aptNode.getApartment().setPrice(newPrice);
			pricePQ.get(currPricePos).setPrice(newPrice);
			areaPQ.get(currAreaPos).setPrice(newPrice);
		
			int newPricePos = currPricePos;
			//reheapify pricePQ(sink or swim)
			if(newPrice < currPrice){
				//node increases in priority, check for disorder of minheap
				newPricePos = swim(currPricePos, 0);
			}else if(newPrice > currPrice){
				//node decreases in priority, check for disorder of minheap
				newPricePos = sink(currPricePos, 0);
			}
		
			//update indexer
			aptNode.setPricePos(newPricePos);
		}
		else{
			//can't find apartment based on user entry
			System.out.println("This apartment does not exist!!!");
		}
	}
	
	public void remove(String addr, String num, int zip){
		
		//attempted find based on given info
		IndexerNode aptNode = indexer.search(addr, num, zip);
		
		if(aptNode != null){	
			
			//get position information from found node
			int currPricePos = aptNode.getPricePos();
			int currAreaPos = aptNode.getAreaPos();
			
			//get price and area of apartment to be removed
			int removedPrice = pricePQ.get(currPricePos).getPrice();
			int removedArea = pricePQ.get(currAreaPos).getArea();
			
			//swap to end of pricePQ and areaPQ before removing
			swapPrice(currPricePos, pricePQ.size()-1);
			pricePQ.remove(pricePQ.size()-1);			
			swapArea(currAreaPos, areaPQ.size()-1);
			areaPQ.remove(areaPQ.size()-1);
			
			if(currPricePos < pricePQ.size()-1){
				//get price and area of apartment that was swapped to fill gap
				Apartment swappedApt = pricePQ.get(currPricePos);
				int swappedPrice = swappedApt.getPrice();
				IndexerNode swappedNode = indexer.search(swappedApt.getAddress(), swappedApt.getNum(), swappedApt.getZip());
				
				//reheapify pricePQ depending on change in priority
				int newPricePos = currPricePos;
				if(swappedPrice < removedPrice){
					//space increases in priority, check for disorder of minheap
					newPricePos = swim(currPricePos, 0);
				}else if(swappedPrice > removedPrice){
					//space decreases in priority, check for disorder of minheap
					newPricePos = sink(currPricePos, 0);
				}			
				swappedNode.setPricePos(newPricePos);
			
			}
			
			if(currAreaPos < areaPQ.size()-1){
				Apartment swappedApt = areaPQ.get(currAreaPos);
				int swappedArea = swappedApt.getArea();
				IndexerNode swappedNode = indexer.search(swappedApt.getAddress(), swappedApt.getNum(), swappedApt.getZip());
				
				//reheapify areaPQ depending on change in priority				
				int newAreaPos = currAreaPos;
				if(swappedArea > removedArea){
					//space increases in priority, check for disorder of minheap
					newAreaPos = swim(currAreaPos, 1);
				}else if(swappedArea < removedArea){
					//space decreases in priority, check for disorder of minheap
					newAreaPos = sink(currAreaPos, 1);
				}
				swappedNode.setAreaPos(newAreaPos);
			}
			
			//remove from indirection structure
			indexer.remove(addr, num, zip);			
		}
		else{
			//can't find apartment based on user entry
			System.out.println("This apartment does not exist!!!");
		}
	}
	
	public Apartment lowestPrice(){
		if(pricePQ.size() == 0)
			return null;
		else
			return pricePQ.get(0);
	}
	
	public Apartment highestArea(){
		if(areaPQ.size() == 0)
			return null;
		else
			return areaPQ.get(0);
	}
	
	public Apartment cityLowestPrice(String city){
		int minPricePos = cityPriceHelper(0, city);
		if(minPricePos > -1)
			return pricePQ.get(minPricePos);
		return null;
	}
	
	//finds index of max apartment from city
	public int cityPriceHelper(int currPricePos, String city){
		//if node 
		if (currPricePos > pricePQ.size() -1) {  //check if position index is within the limits of the heap
			return -1;
		}
		else if (pricePQ.get(currPricePos).getCity().equals(city) ) { //check root
			return currPricePos;
		}
		else { //DFS binary search
			int leftMax = cityPriceHelper((2*currPricePos + 1), city);  //check left child
			int rightMax = cityPriceHelper((2*currPricePos + 2), city);  //check right child
			
			
			if (leftMax == -1 && rightMax == -1) {  //if not found in either child
				return -1;
			}
			else if (leftMax > -1) {
				if(rightMax > -1){
					//compare prices to decide which side to return
					int leftPrice = pricePQ.get(leftMax).getPrice();
					int rightPrice = pricePQ.get(rightMax).getPrice();
					if(leftPrice >= rightPrice){
						return leftMax;
					}
					return rightMax;
				}
				//only found in left child
				return leftMax;
			}else{  //only found in the right child
				return rightMax;
			}
		}
	}	
	
	public Apartment cityHighestArea(String city){
		int maxAreaPos = cityAreaHelper(0, city);
		if(maxAreaPos > -1)
			return areaPQ.get(maxAreaPos);
		return null;
	}
	
	//finds index of max apartment from city
	public int cityAreaHelper(int currAreaPos, String city){
		//if node 
		if (currAreaPos > areaPQ.size() -1) {  //check if position index is within the limits of the heap
			return -1;
		}
		else if (areaPQ.get(currAreaPos).getCity().equals(city) ) { //check root
			return currAreaPos;
		}
		else { //DFS binary search
			int leftMax = cityAreaHelper((2*currAreaPos + 1), city);  //check left child
			int rightMax = cityAreaHelper((2*currAreaPos + 2), city);  //check right child			
			
			if (leftMax == -1 && rightMax == -1) {  //if not found in either child
				return -1;
			}
			else if (leftMax > -1) {
				if(rightMax > -1){
					//compare prices to decide which side to return
					int leftArea = areaPQ.get(leftMax).getArea();
					int rightArea = areaPQ.get(rightMax).getArea();
					if(leftArea >= rightArea){
						return leftMax;
					}
					return rightMax;
				}
				//only found in left child
				return leftMax;
			}else{  //only found in the right child
				return rightMax;
			}
		}
	}
	
	//************************
	//Helper methods
	//************************
	
	//add to price PQ subprocess
	public int addPrice(Apartment apt){
		
		int currPos = pricePQ.size();
		pricePQ.add(apt);
		
		//minheapify pricePQ
		int newPos = swim(currPos, 0);
		
		//return position of apartment after add
		return newPos;
	}
	
	//swap in price PQ indices
	public int swapPrice(int currPos, int newPos){
		
		//System.out.println("Swapping price ("+currPos+", "+newPos+")...");
		
		Apartment curr = pricePQ.get(currPos);
		Apartment temp = pricePQ.get(newPos);
		pricePQ.set(newPos, curr);
		pricePQ.set(currPos, temp);
		
		//update indexer
		IndexerNode in = indexer.search(curr.getAddress(), curr.getNum(), curr.getZip());
		in.setPricePos(newPos);
		
		in = indexer.search(temp.getAddress(), temp.getNum(), temp.getZip());
		in.setPricePos(currPos);
		
		return currPos;
	}
	
	public int addArea(Apartment apt){
		
		int currPos = areaPQ.size();
		areaPQ.add(apt);
		
		//minheapify areaPQ
		int newPos = swim(currPos, 1);
		
		//return position of apartment after add
		return newPos;
	}
	
	//bottom up restore of pricePQ(min,0) or areaPQ(max,1)
	private int swim(int currPos, int flag) {
		
		if(currPos<0){
			return -1;
		}
		
		//System.out.println("Swimming ("+currPos+", "+flag+")...");
		
		if(flag == 0){
			int currPrice = pricePQ.get(currPos).getPrice();
			int parentPrice = pricePQ.get((currPos-1)/2).getPrice();
			while (currPos >= 0 && parentPrice > currPrice){
				
				//move to parent
				swapPrice(currPos, (currPos-1)/2);
				currPos = (currPos-1)/2;
				parentPrice = pricePQ.get((currPos-1)/2).getPrice();				
			}
		}
		else{
			int currArea = areaPQ.get(currPos).getArea();
			int parentArea = areaPQ.get((currPos-1)/2).getArea();
			while (currPos > 0 && parentArea < currArea){
				
				//swap with parent, update currPos and parentArea to after swap
				swapArea(currPos, (currPos-1)/2);
				currPos = (currPos-1)/2;
				parentArea = areaPQ.get((currPos-1)/2).getArea();				
			}
		}
		
		return currPos;
	}
	
	//top down restore of maxheap
	private int sink(int currPos, int flag) {
		
		if(currPos < 0){
			return -1;
		}
		
		//System.out.println("Sinking ("+currPos+", "+flag+")...");
		
		if(flag == 0){
			//while has children to compare against
			while (2*currPos + 1 < pricePQ.size()) {
				
				int leftPos = 2*currPos + 1;
				int leftPrice = pricePQ.get(leftPos).getPrice();
				
				int currPrice = pricePQ.get(currPos).getPrice();
				
				
				if(leftPos + 1 < pricePQ.size()){
					//right child exists
					int rightPos = leftPos + 1;
					int rightPrice = pricePQ.get(rightPos).getPrice();
					
					if(currPrice > leftPrice || currPrice > rightPrice){
						if(leftPrice < rightPrice){
							swapPrice(currPos, leftPos);
							currPos = leftPos;
						}
						else{
							swapPrice(currPos, rightPos);
							currPos = rightPos;
						}
					}else{
						break;
					}
				}else{
					//right child does not exist
					if(currPrice > leftPrice){						
						swapPrice(currPos, leftPos);
						currPos = leftPos;					
					}
					else{
						break;
					}
				}				
			}
		}else{
			//while has children to compare against
			while (2*currPos + 1 < areaPQ.size()) {
				
				int leftPos = 2*currPos + 1;
				int leftArea = areaPQ.get(leftPos).getArea();
				
				int currArea = areaPQ.get(currPos).getArea();
				
				
				if(leftPos + 1 < pricePQ.size()){
					//right child exists
					int rightPos = leftPos + 1;
					int rightArea = areaPQ.get(rightPos).getArea();
					
					if(currArea < leftArea || currArea < rightArea){
						if(leftArea > rightArea){
							swapArea(currPos, leftPos);
							currPos = leftPos;
						}
						else{
							swapArea(currPos, rightPos);
							currPos = rightPos;
						}
					}else{
						break;
					}
				}else{
					//right child does not exist
					if(currArea < leftArea){						
						swapArea(currPos, leftPos);
						currPos = leftPos;					
					}
					else{
						break;
					}
				}				
			}
		}	
		
		return currPos;
	}
	
	//swap of two PQ indices, updates indexer as well
	public int swapArea(int currPos, int newPos){
		
		//System.out.println("Swapping area ("+currPos+", "+newPos+")...");
		
		Apartment curr = areaPQ.get(currPos);
		Apartment temp = areaPQ.get(newPos);
		areaPQ.set(newPos, curr);
		areaPQ.set(currPos, temp);
		
		//update indexer
		IndexerNode in = indexer.search(curr.getAddress(), curr.getNum(), curr.getZip());
		in.setAreaPos(newPos);
		
		in = indexer.search(temp.getAddress(), temp.getNum(), temp.getZip());
		in.setAreaPos(currPos);
		
		return currPos;
	}
	
	//used for debugging
	public void show(){
		System.out.println("\n\nPricePQ:");
		for(int i=0;i<pricePQ.size();i++){
			System.out.println(i+"\n"+pricePQ.get(i).toString());
		}
		
		System.out.println("\nAreaPQ:");
		for(int i=0;i<areaPQ.size();i++){
			System.out.println(i+"\n"+areaPQ.get(i).toString());
		}
		
		System.out.println("\nIndexer:");
		ArrayList<IndexerNode> ind = indexer.getIndexes();
		for(int i=0;i<ind.size();i++){
			System.out.println(i+"\n"+ind.get(i).getApartment());
		}
	}
}