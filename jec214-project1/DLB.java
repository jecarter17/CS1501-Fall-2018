import java.util.*;
public class DLB{
	
	private final char TERMINATE_CHAR = '.';
	private DLBNode rootNode;
	
	public DLB(){
		rootNode = new DLBNode();
	}
	
	public boolean search(String s){
		
		//System.out.println("\n\n\n\nSearching for \"" + s + "\"...");
				
		//add terminating char for easier search
		s += TERMINATE_CHAR;
		
		DLBNode currNode = rootNode;
		char currChar;
		
		for(int i=0;i<s.length();i++) { 
			currChar = s.charAt(i);
			
			//follow siblings until character is matched or null is found
			while(currNode != null && currNode.getVal() != currChar){
				currNode = currNode.getSibling();
			}
			
			if(currNode == null){
				//string not found
				return false;
			}
			else{				
				//check for end of string, navigate to child
				if(currChar == TERMINATE_CHAR){
					return true;
				}
				currNode = currNode.getChild();
			}
		}
		
		return false;
	}
	
	public boolean add(String s){
		
		//do nothing with empty string
		if(s.equals("")){
			return false;
		}
		
		if(search(s)){
			//string already stored, no need to add
			return false;
		}
		
		s += TERMINATE_CHAR;
		char currChar;
		DLBNode currNode, prevNode;
		
		//if DLB is empty, add at root
		if(rootNode.getVal() == '\u0000'){
				int i = 0;
				rootNode.setVal(s.charAt(i));
				currNode = rootNode;
				while(i<s.length()-1){
					currNode.setChild(new DLBNode(s.charAt(i+1)));
					currNode = currNode.getChild();
					i++;
				}
				return true;
		}
		
		currNode = rootNode;
		prevNode = rootNode;		
		boolean first = true;
		
		for(int i=0;i<s.length();i++) { 
			currChar = s.charAt(i);			
			
			//follow siblings until character is matched or null is found
			while(currNode != null && currNode.getVal() != currChar){
				if(first){prevNode = currNode;}
				else{prevNode = prevNode.getSibling();}
				currNode = currNode.getSibling();
				
			}
			
			if(currNode == null){
				//reached end of prefix begin adding
				prevNode.setSibling(new DLBNode(currChar));
				currNode = prevNode.getSibling();
				while(i<s.length()-1){
					currNode.setChild(new DLBNode(s.charAt(i+1)));
					currNode = currNode.getChild();
					i++;
				}
				return true;
			}
			else{
				currNode = currNode.getChild();
			}
		}
		
		return false;
	}
	
	//returns DLB node at end of given prefix, returns null if prefix not found
	public DLBNode getPrefixNode(String prefix){
		
		DLBNode currNode = rootNode;
		char currChar;
		
		for(int i=0;i<prefix.length();i++) {
			
			currChar = prefix.charAt(i);
			
			//follow siblings until character is matched or null is found
			while(currNode != null && currNode.getVal() != currChar){
				currNode = currNode.getSibling();
			}
			
			//if hit null node -> prefix not found -> return null
			if(currNode == null){
				return null;
			}
			else{				
				//node exists, if last character -> found match -> return node, otherwise continue to child
				if(i == prefix.length() - 1){
					return currNode;
				}
				currNode = currNode.getChild();
			}
		}
		
		return null;
	}
	
	public DLBNode getRoot(){
		return rootNode;
	}
}