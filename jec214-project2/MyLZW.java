import java.util.*;
/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width

    public static void compress(String arg) { 
		
		int mode = 0;
		long uncompSize = 0; 	//uncompressed data processed (in bits)
		long compSize = 0;		//compressed data processed (in bits)
		double oldRate = 0.0;
		double newRate = 0.0;
		double rateRatio = 0.0;
		boolean firstFill = true;
		
		String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
		
		//initialize according to mode
		if(arg.equals("n")){
			//System.out.println("Do Nothing Mode!!!");
			mode = 0;
		}
		else if(arg.equals("r")){
			//System.out.println("Reset Mode!!!");
			mode = 1;
		}
		else if(arg.equals("m")){
			//System.out.println("Monitor Mode!!!");
			mode = 2;
		}
		else{
			System.out.println("Invalid mode argument - closing");
			System.exit(0);
		}
        
		BinaryStdOut.write(arg, 8);	//write mode to file
		
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			
			
			String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
			
			compSize += W;
			uncompSize += (t * 8);
			
            if (t < input.length()){
				if(code >= L){				
					if(W == 16){
						// codebook is full - behavior depends on mode
					
						if(mode == 1){//Add s to symbol table, resizing according to mode
							//System.out.println("Codebook full - resetting!!!");
						
							//reset TST
							st = new TST<Integer>();
							for (int i = 0; i < R; i++)
								st.put("" + (char) i, i);
							code = R+1;  // R is codeword for EOF
						 
							W = 9;
							L = (int)Math.pow(2,W);							
						}
						else if(mode == 2){
							//monitor mode - start/continue monitoring
						
							//if not currently monitoring, start monitoring
							if(firstFill){
								oldRate = ((double) uncompSize)/((double) compSize);
								firstFill = false;
							}
							else{
								
								newRate = ((double) uncompSize)/((double) compSize);
								rateRatio = oldRate/newRate;								
								
								if(rateRatio >= 1.1){
									//reset codebook
									W = 9;
									L = (int)Math.pow(2,W);
						
									//reset array
									st = new TST<Integer>();
									for (int i = 0; i < R; i++)
										st.put("" + (char) i, i);
									code = R+1;  // R is codeword for EOF		
								
									firstFill = true;
								}
							}							
						}				
					}
					else{ //increment code width
						W++;
						L = (int) Math.pow(2,W);
						//System.out.println("Resizing to "+W+" bit code words");
					}
				}				
				
				if(code<L) st.put(input.substring(0, t + 1), code++);
			}
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
		
		long uncompSize = 0; 	//uncompressed data processed (in bits)
		long compSize = 0;		//compressed data processed (in bits)
		double oldRate = 0.0;
		double newRate = 0.0;
		double rateRatio = 0.0;
		boolean firstFill = true;
		
		char method;	
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
		
		method = BinaryStdIn.readChar();	//get mode from file
		
		//determine compression mode
		if(method == 'n'){
			//System.out.println("Do Nothing Mode!!!");
		}
		else if(method == 'r'){
			//System.out.println("Reset Mode!!!");
		}
		else if(method == 'm'){
			//System.out.println("Monitor Mode!!!");
		}
		else{
			System.out.println("Invalid method argument - closing");
			System.exit(0);
		}
		
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {            
			
			compSize += W;
			uncompSize += (val.length() * 8);
			
			if(i>=L){
				if(W >= 16){
					//codebook filled - determine what to do based on method
					
					if(method == 'r'){
						//reset mode - reset codebook
						
						W = 9;
						L = (int)Math.pow(2,W);
						
						//reset array
						st = new String[L];
						for (i = 0; i < R; i++)
							st[i] = "" + (char) i;
						st[i++] = ""; // (unused) lookahead for EOF							
					}
					else if(method == 'm'){
						//monitor mode - start/continue monitoring
						
						//if not currently monitoring, start monitoring
						if(firstFill){
							oldRate = ((double) uncompSize)/((double) compSize);
							firstFill = false;
						}
						else{
							newRate = ((double) uncompSize)/((double) compSize);
							rateRatio = oldRate/newRate;
							
							if(rateRatio > 1.1){
								//reset codebook
								W = 9;
								L = (int)Math.pow(2,W);
						
								//reset array
								st = new String[L];
								for (i = 0; i < R; i++)
									st[i] = "" + (char) i;
								st[i++] = ""; // (unused) lookahead for EOF		
								
								firstFill = true;
							}
						}
					}
				}
				else{
					//simple increment and resize
					W++;
					L = (int)Math.pow(2,W);
					st = Arrays.copyOf(st, L);
					//System.out.println("Resizing to "+W+" bit code words");
				}
			}
			
			BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
			if(i<L) st[i++] = val + s.charAt(0);
            val = s;
			
			
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
