import java.io.*;
import java.util.*;

public class ac_test{
	
	//write user entered word to user history file
	private static void writeToHistory(String word) throws IOException
	{
		FileWriter fileOut = new FileWriter("user_history.txt", true);
		PrintWriter writer = new PrintWriter(fileOut);
		writer.println(word);
		writer.close();
	}
	
	//return array of 5 predictions depending on current user-entered word, user-history, and dictionary suggestions
	private static String[] getPredictions(String prefix, DLB history, DLB dictionary, ArrayList<String> allUserWords, ArrayList<Integer> allUserFreq){
		
		String[] predictions = {"","","","",""};
		
		DLBNode prefixNode;
		ArrayList<String> userList = new ArrayList<String>();
		ArrayList<String> dictList = new ArrayList<String>();
		
		//navigate to prefix in user history DLB
		prefixNode = history.getPrefixNode(prefix);
		
		//gather predictions from user history
		if(prefixNode != null){
			userList = predictionHelper(userList, prefix, prefixNode.getChild());
		}
		
		//navigate to prefix in dictionary DLB
		prefixNode = dictionary.getPrefixNode(prefix);
		
		
		//gather predictions from dictionary
		if(prefixNode != null){
			dictList = predictionHelper(dictList, prefix, prefixNode.getChild());
		}
		
		//sort list of user_history predictions in order of descending frequency
		userList = sortByFrequency(userList, allUserWords, allUserFreq);
		
		//fill prediction array with user history suggestions if possible
		int count = 0;
		for(int i=0;i<5;i++){
			if(i<userList.size()){
				predictions[i] = userList.get(i);
				count++;
			}
		}
		
		//fill remaining prediction slots with dictionary suggestions if possible
		int index = 0;
		boolean dupe;
		while(count < 5 && index<dictList.size()){
			
			//check if dictionary suggestion overlaps with previous user suggestions
			dupe = false;
			for(int j=0;j<count;j++){
				if(dictList.get(index).equals(predictions[j])){			
					dupe = true;
				}				
			}
			
			//add non-duplicate suggestions
			if(!dupe){
				predictions[count] = dictList.get(index);
				count++;
			}
			index++;
		}
		
		return predictions;
	}
	
	//return list of words sorted in descending order of frequency
	private static ArrayList<String> sortByFrequency(ArrayList<String> words, ArrayList<String> allUserWords, ArrayList<Integer> allUserFreq){
		
		//selection sort of user-history suggestions based on frequency
		for (int i = 0; i < words.size(); i++) {
            int pos = i;
            for (int j = i; j < words.size(); j++) {
				int jFreq = getFrequency(words.get(j), allUserWords, allUserFreq);
				int posFreq = getFrequency(words.get(pos), allUserWords, allUserFreq);
				if (jFreq > posFreq)
                    pos = j;
            }
            String maxWord = words.get(pos);			
            words.set(pos, words.get(i));
            words.set(i, maxWord);
        }
		
		return words;
	}
	
	//helper for above method
	private static int getFrequency(String word, ArrayList<String> allUserWords, ArrayList<Integer> allUserFreq){
		int index = allUserWords.indexOf(word);
		int freq = allUserFreq.get(index).intValue();
		return freq;
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
		
		boolean found = false, added = false;		
		DLB dict = new DLB();
		DLB hist = new DLB();
		ArrayList<String> userWords = new ArrayList<String>();
		ArrayList<Integer> userFreq = new ArrayList<Integer>();
		
		//create user history file if doesn't already exist
		try{
			File file = new File("user_history.txt");
			if (file.createNewFile()) {            
				System.out.println("User history file has been created.");
			}
			else{        
				System.out.println("User history file already exists.");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println("\n\nBuilding user history DLB...");
		
		//add user history words		
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("user_history.txt")));
			String line = null;
			while (((line = in.readLine()) != null)){
								
				//add current word to user history
				if(!line.equals("")){
					int index = userWords.indexOf(line);
					if(index == -1){
						userWords.add(line);
						userFreq.add(new Integer(1));
						added = hist.add(line);
					}
					else{					
						//if repeated occurence, increment corresponding frequency
						userFreq.set(index, userFreq.get(index).intValue() + 1);
					}
				}
			}
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
        }
		
		System.out.println("\n\nBuilding dictionary DLB...");
		
		//add dictionary words		
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("dictionary.txt")));
			String line = null;
			while (((line = in.readLine()) != null)) {
				added = dict.add(line);
			}
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
        }
		
		Scanner input = new Scanner(System.in);
		DLBNode currNode;
		int numTimes = 0;
		char c;
		String currWord;
		double timeStart, timeStop, time, sumTimes = 0.0, avgTime = 0.0;
		boolean wordCompleted, firstChar;
		
		while(true){
			
			currNode = dict.getRoot();
			currWord = "";
			wordCompleted = false; firstChar = true;
			String[] predictions = new String[5];
			
			while(true){
				
				c = '\u0000';
				
				//get character input
				if(firstChar){
					System.out.print("Enter your first character: ");
				}
				else{System.out.print("Enter your next character: ");}
				
				c = input.next().charAt(0);
				input.nextLine();
				
				//exit program on '!'
				if(c == '!'){
					System.out.println("Average Time:  "+avgTime+" s");
					System.out.println("Bye!");
					System.exit(0);
				}
				else if(c == '$'){//terminate current word
					
					//add current word to user history and break to beginning of next word input
					if(!currWord.equals("")){
						try{
							writeToHistory(currWord);
						}
						catch(IOException e){
							e.printStackTrace();
						}
						int index = userWords.indexOf(currWord);
						if(index == -1){
							userWords.add(currWord);
							userFreq.add(new Integer(1));
							hist.add(currWord);
						}
						else{					
							//if repeated occurence, increment corresponding frequency
							userFreq.set(index, userFreq.get(index).intValue() + 1);
						}
					}
					break;					
				}
				else if((int)c >= 49 && (int)c <= 53 && !firstChar){//select prediction
					
					String chosenWord = predictions[Character.getNumericValue(c) - 1];
					System.out.println("WORD COMPLETED: "+chosenWord);
					
					//add current word to user history
					if(!chosenWord.equals("")){
						try{
							writeToHistory(chosenWord);
						}
						catch(IOException e){
							e.printStackTrace();
						}
						int index = userWords.indexOf(chosenWord);
						if(index == -1){
							userWords.add(chosenWord);
							userFreq.add(new Integer(1));
							hist.add(chosenWord);
						}
						else{					
							//if repeated occurence, increment corresponding frequency
							userFreq.set(index, userFreq.get(index).intValue() + 1);
						}
					}
					
					break;
				}
				else{
					currWord += c;				
				
					//start timer
					timeStart = System.nanoTime();
				
					//get predictions
					predictions = getPredictions(currWord, hist, dict, userWords, userFreq);
				
					//stop timer, calc elapsed time and avgTime
					timeStop = System.nanoTime();
					time = (timeStop - timeStart)/1000000000;
					sumTimes += time;
					numTimes++;
					avgTime = sumTimes/numTimes;
				
					System.out.println("("+time+" s)");
					System.out.println("Current word: "+currWord);
					System.out.println("Predictions:");
				
					//show predictions
					if(predictions[0].equals("")){
						System.out.println("NO PREDICTIONS AVAILABLE");
					}
					else{
						for(int i=0;i<predictions.length;i++){
							if(!predictions[i].equals("")){
								System.out.print("("+(i+1)+")"+predictions[i]);
							}							
						}
					}
					System.out.println("\n\n");
				}
				firstChar = false;
			}
		}		
	}
}