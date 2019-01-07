public class DLBNode{
	
	private char val;
	private DLBNode child, sibling;
		
	//default constructor
	public DLBNode(){
		val = '\u0000';
		child = null;
		sibling = null;
	}
		
	//constructor with val
	public DLBNode(char c){
		super();
		setVal(c);
	}
		
	//******************************************************
	//ACCESSOR AND MUTATOR METHODS
	//******************************************************
				
	public char getVal(){
		return val;
	}
		
	public void setVal(char c){
		val = c;
	}
		
	public DLBNode getSibling(){
		return sibling;
	}
		
	public void setSibling(DLBNode sib){
		sibling = sib;
	}
		
	public DLBNode getChild(){
		return child;
	}
		
	public void setChild (DLBNode ch){
		child = ch;
	}
	
	public boolean hasSibling(){
		if(sibling != null)
			return true;
		return false;
	}
	
	public boolean hasChild(){
		if(child != null)
			return true;
		return false;
	}
}