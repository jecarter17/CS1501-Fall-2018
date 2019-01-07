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

public class testCode {
    private static final int R = 256;        // number of input chars
    private static int L = (int) Math.pow(2, 9);       // number of codewords = 2^W
    private static int W = 9;         // codeword width

    public static void compress(String mode) { 
    	BinaryStdOut.write(mode, 8);	//write mode to file
    	
        String input = BinaryStdIn.readString();
        
        int uncompressed = 0;
        int compressed = 0;
      	double ratio = 0;
      	double oldRatio = 0;
      	boolean monitoring = false;
        
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)	//initialize codebook with all single characters
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
        	String s = st.longestPrefixOf(input);  // Find max prefix match s.	
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
        	
            uncompressed = uncompressed + s.length()*8;  //number of bits in uncompressed
            compressed = compressed + W; //number of compressed bits
            
            if (code == L) //if all codeword values have been used
            {
            	if (W < 16) //check if all codewords of previous size have been used
            	{
            		W++;		//increase the codeword width
                	L = (int) Math.pow(2,  W); 	//increase codebook
            	}
            	else if(mode.equals("r")) 
            	{
            		 st = new TST<Integer>();
            	     for (int i = 0; i < R; i++)	//initialize codebook with all single characters
            	    	 st.put("" + (char) i, i);
            	     code = R+1;  // R is codeword for EOF
            	     W = 9;	//restart at 9 bit code words
            	     L =  (int)Math.pow(2, W);
            	}
            	else if (mode.equals("m")) {
            		if (!monitoring) {
            			oldRatio = uncompressed/(double)compressed;
            			monitoring = true;
            		}
            		else {
            			ratio = uncompressed/(double)compressed;
            			if ((oldRatio/ratio) > 1.1) 
                		{
            				st = new TST<Integer>();
                 	        for (int i = 0; i < R; i++)	//initialize codebook with all single characters
                 	            st.put("" + (char) i, i);
                 	        code = R+1;  // R is codeword for EOF
                 	        W = 9;	//restart at 9 bit code words
                 	        L =  (int)Math.pow(2, W);
                 	        monitoring = false;
                		}
            		}            		
            	}
            }
            
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
    	char mode = 'a';
    	int uncompressed = 0;
        int compressed = 0;
      	double ratio = 0;
      	double oldRatio = 0;
      	boolean monitoring = false;		
    			
    	mode = BinaryStdIn.readChar();	//get mode from file
    	
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                         // (unused) lookahead for EOF

        
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
        	uncompressed = uncompressed + (val.length()*8);	//each letter is 8 bits, so length*8 gives the total number of bits
            compressed = compressed + W;	//each codeword has size W
            
        	if (i == L) //if all codeword values have been used
            {
            	if (W < 16) //check if all codewords of previous size have been used
            	{
            		W++;		//increase the codeword width
                	L = (int) Math.pow(2,  W); 	//increase codebook
                	String[] temp = new String[L];
                	for (int j = 0; j < st.length; j++)
                        temp[j] = st[j];
                    st = temp; 
            	}
            	else if(mode == 'r') 
            	{
            		W = 9;	//restart at 9 bit code words
            		L =  (int)Math.pow(2, W);
            		st = new String[L];
                    // initialize symbol table with all 1-character strings
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";  
            	}
            	else if (mode == 'm') {
            		if (!monitoring) {
            			oldRatio = uncompressed/(double)compressed;
            			monitoring = true;
            		}
            		else {
            			ratio = uncompressed/(double)compressed;					
						
            			if ((oldRatio/ratio) > 1.1) 
                		{
            				W = 9;	//restart at 9 bit code words
                    		L =  (int)Math.pow(2, W);
                    		st = new String[L];
                    		i = 0;
                            // initialize symbol table with all 1-character strings
                            for (i = 0; i < R; i++)
                                st[i] = "" + (char) i;
                            st[i++] = "";  
                            monitoring = false;
                		}
            		}            		
            	}
            }
            	
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if (args[0].equals("-")) 
        {
        	String mode = "n";  //default to do nothing mode
        	if (args[1].equals("n") || args[1].equals("r") || args[1].equals("m"))
        	{
        		mode = args[1];
        	}
        	else throw new IllegalArgumentException("Illegal command line argument");
        	compress(mode);
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument"); 
    }

}
