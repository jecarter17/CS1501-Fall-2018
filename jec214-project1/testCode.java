import java.io.*;
import java.util.*;

public class testCode{
	
	private static ArrayList<String> getPredictions(String prefix, DLB history){
		
		//String[] predictions = new String[5];
		//String[] predictions = {"pred1","pred2","pred3","pred4","pred5"};
		
		ArrayList<String> userList = new ArrayList<String>();
		ArrayList<String> dictList = new ArrayList<String>();
		
		//navigate to prefix in user history DLB
		DLBNode prefixNode = history.getPrefixNode(prefix);
		System.out.println(prefixNode.getVal());
		
		//gather predictions from user history
		userList = predictionHelper(userList, prefix, prefixNode.getChild());
		
		//if necessary, gather <=5 predictions from dictionary
		
		return userList;
	}
	
	//returns array of numPreds predictions based on given prefix
	private static ArrayList<String> predictionHelper(ArrayList<String> currList, String currWord, DLBNode currNode){
		
		//catch-all incase initial call is null
		if(currNode == null){
			return currList;
		}
		
		//if find terminating character, add current traversal to the list
		//recursively traverse through children and siblings
		if(currNode.getVal() == '.'){
			currList.add(currWord);
		}
		else if(currNode.hasChild()){
			currList = predictionHelper(currList, (currWord+currNode.getVal()), currNode.getChild());
		}
		
		if(currNode.hasSibling()){
			currList = predictionHelper(currList, currWord, currNode.getSibling());
		}
		
		return currList;
	}
	
	public static void main(String[] args){
		
		String[] testWords = {"she","sells","sea","shells","by","the","sea","shore"};
		DLB hello = new DLB();
		for(int i=0;i<testWords.length;i++){
			hello.add(testWords[i]);
		}
		ArrayList<String> testList = getPredictions("s", hello);
		
		System.out.println();
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i));
		}
	}
}