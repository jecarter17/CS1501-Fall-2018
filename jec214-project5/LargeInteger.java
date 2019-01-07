import java.util.*;
import java.math.BigInteger;

public class LargeInteger {
	
	private final byte[] TWO = {(byte) 2};
	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};
	private final byte[] NEGONE = {(byte) 0xff};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)	
		
		//copy both (easier to refer to as x and y)
		LargeInteger x = this;
		LargeInteger y = other;		
		
		//want all positive #s, decide if we have to negate ans
		boolean xNegated = false;
		boolean yNegated = false;
		boolean negateAns = false;
		
		if(isNegative()){			
			x = x.negate();
			xNegated = true;			
		}
		
		if(y.isNegative()){
			y = y.negate();
			yNegated = true;
		}
		
		negateAns = xNegated ^ yNegated;
		
		byte[] xbytes = x.getVal();
		byte[] ybytes = y.getVal();
		
		int numRows = xbytes.length * ybytes.length;
		int resultLength = xbytes.length + ybytes.length;
		
		byte[] resultBytes = new byte[resultLength];		
		int[][] temp = new int[numRows][resultLength];
		
		//do multiplication
		int count = 0;
		for(int i=0;i<xbytes.length;i++){
			for(int j=0;j<ybytes.length;j++){				
				int shift = (xbytes.length-i-1) + (ybytes.length-j-1);
				
				int product = ((int)xbytes[i] & 0xff) * ((int)ybytes[j] & 0xff);
				//System.out.print(String.format("0x%02X * 0x%02X = %d; shift = %d\n", xbytes[i], ybytes[j], product, shift));
				int rem = product % 256;
				int carry = product/256;
				temp[count][resultLength-shift-1] = rem;
				temp[count][resultLength-shift-2] = carry;
				
				count++;
			}
		}
		
		int carry = 0;
		//sum all temporary calculations
		for(int c=temp[0].length-1;c>=0;c--){
			
			int sum = carry;	
			
			for(int r=0;r<temp.length;r++){
				sum += temp[r][c];
			}
			
			int rem = sum%256;
			resultBytes[c] = (byte) rem;
			carry = sum/256;
		}
		
		//revert all changes
		if(xNegated){
			x = x.negate();
		}
		
		if(yNegated){
			y = y.negate();
		}
		
		LargeInteger result = new LargeInteger(resultBytes); 
		
		if(negateAns){
			result = result.negate();
		}
		
		return result;
	}
	
	//ASSUMING NO NEGATIVE DIVISORS OR MODULO OPERATORS
	public LargeInteger[] divide(LargeInteger other) {
		
		LargeInteger[] results = new LargeInteger[2];
		LargeInteger rem = new LargeInteger(ZERO);
		LargeInteger dividend = new LargeInteger(getVal());
		LargeInteger divisor = new LargeInteger(other.getVal());
		LargeInteger quo = new LargeInteger(ZERO);
		
		//System.out.println("Start*************************************************************************************************************************************");
		//System.out.println("quo = " + quo.toString());
		
		if(!dividend.isNegative() && !divisor.isNegative()){
		
			while(!dividend.subtract(divisor).isNegative()){
				//System.out.println("In loop...");
				dividend = dividend.subtract(divisor);
				quo = quo.add(new LargeInteger(ONE));
				//System.out.println("dvdnd = " + dividend.toString());
				//System.out.println("quo = " + quo.toString());
			}
			rem = new LargeInteger(dividend.getVal());
			//System.out.println("rem = " + rem.toString());		
			
		}else if (dividend.isNegative() && !divisor.isNegative()){
			
			while(dividend.add(divisor).isNegative()){
				//System.out.println("In loop...");
				dividend = dividend.add(divisor);
				quo = quo.subtract(new LargeInteger(ONE));
			}
			rem = dividend.add(divisor);
			
		}else if (!dividend.isNegative() && divisor.isNegative()){
			
			while(!dividend.add(divisor).isNegative()){
				//System.out.println("In loop...");
				dividend = dividend.add(divisor);
				quo = quo.subtract(new LargeInteger(ONE));
			}
			rem = dividend.add(divisor);
			
		}else if (dividend.isNegative() && divisor.isNegative()){ //if both are negative
		
			while(dividend.subtract(divisor).isNegative()){
				//System.out.println("In loop...");
				dividend = dividend.subtract(divisor);
				quo = quo.add(new LargeInteger(ONE));
			}
			rem = new LargeInteger(dividend.getVal());
		}
		
		results[0]= quo;
		results[1]= rem;
		
		return results;
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		
		LargeInteger x = new LargeInteger(getVal());
		LargeInteger y = new LargeInteger(other.getVal());
		
		//if y == 0
		if (y.subtract(new LargeInteger(ONE)).isNegative())
			return new LargeInteger[] {x, new LargeInteger(ONE), new LargeInteger(ZERO)};
		
		LargeInteger[] temp = x.divide(y);
		LargeInteger xmody = temp[1];
		LargeInteger[] vals = other.XGCD(xmody);
		
		LargeInteger d = vals[0];
		
		LargeInteger a = vals[2];
		
		temp = x.divide(y);
		LargeInteger xdivy = temp[0];
		LargeInteger b = vals[1].subtract(xdivy.multiply(vals[2])); //b = vals[1] - (p / q) * vals[2]
		
		return new LargeInteger[] {d, a, b};
	 }
	 
	 public boolean equalsZero(){
		for (byte b  : val) {
			if (b!= 0) {
				return false;
			}
		}
		return true;
	}

	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	 /*
	 //other failed tries at modularExp
	 public LargeInteger tempModularExp(LargeInteger y, LargeInteger n) {
		// YOUR CODE HERE (replace the return, too...)
		
		int numBits = 0;
		LargeInteger q = new LargeInteger(y.getVal());
		
		String bitString = y.toBinary();
		System.out.println(bitString);
		numBits = bitString.length();
		
		LargeInteger[] memVals = new LargeInteger[numBits];
		
		LargeInteger curr = new LargeInteger(ONE);
		boolean first = true;
		
		for(int i=0;i<numBits;i++){			
			//calculate curr = curr^2 % n;
			LargeInteger temp;
			
			if(first){
				temp = curr.multiply(this);
				first = false;
			}else{
				temp = curr.multiply(curr);
			}
						
			temp = temp.signCompress(temp);
			LargeInteger[] div = temp.divide(n);
			curr = div[1];
			memVals[i] = curr;
			System.out.println("memVals["+i+"] = " + curr.toString());
		}
		
		LargeInteger result = new LargeInteger(ONE);
		for(int i=0;i<bitString.length();i++){
			System.out.println("i = "+i);
			if(bitString.charAt(i) == '1'){				
				result = result.multiply(memVals[bitString.length()-i-1]);
				result = result.signCompress(result);
			}
		}
		System.out.println("result = " + result.toString());
		LargeInteger[] temp2 = result.divide(n);
		System.out.println("Hello");
		LargeInteger mod = temp2[1];
		
		return mod;
	 }
	 
	 LargeInteger helpModularExp(LargeInteger y, LargeInteger p){ 
		LargeInteger result = new LargeInteger(ONE);      // Initialize result 
		LargeInteger x = new LargeInteger(getVal());
		
		x = x.divide(p)[1];  // Update x if it is more than or equal to p 
		//System.out.println("x = " + x.toString());
  
		while (!y.equalsZero()) 
		{ 
			byte[] yBytes = y.getVal();
			// If y is odd, multiply x with result 
			if ((yBytes[yBytes.length-1] & (byte)1) == 1){
				//System.out.println("y is odd = " + y.toString());
				result = result.multiply(x); 
				result = result.signCompress(result);
				result = result.divide(p)[1];
				//System.out.println("result = " + result.toString());
			}
  
			// y must be even now 
			y = y.shiftRight(1); // y = y/2
			//System.out.println("y = " + y.toString());
			x = x.multiply(x);
			//System.out.println("x*x = " + x.toString());
			x = x.signCompress(x);
			x = x.divide(p)[1];
			//System.out.println("(x*x)%p = " + x.toString());
			//System.out.println("x = " + x.toString());
			//System.out.println();
		} 
		return result; 
	}*/
	
	//final attempt
	public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
		
		LargeInteger ans = new LargeInteger(ONE);
		LargeInteger x = new LargeInteger(getVal());
		x = x.divide(n)[1];
			
		String yBitString = y.toBinary();
		for(int i = 0; i < y.length(); i++){
			char bit = yBitString.charAt(i);
			ans = ans.divide(n)[1];  //ans mod n
			ans = ans.modHelper(ans, n);
				
			if (ans.length() > n.length()) {
				byte[] ansVal = new byte[64];
				System.arraycopy(ans.getVal(), ans.length()-64, ansVal, 0, 64);
				ans = new LargeInteger(ansVal);
			}
			
			if(bit == '1'){
				ans = ans.divide(n)[1];
				ans = ans.modHelper(x, n);
	
				if (ans.length() > n.length()) {
					byte[] ansVal = new byte[64];
					System.arraycopy(ans.getVal(), ans.length()-64, ansVal, 0, 64);
					ans = new LargeInteger(ansVal);
				}
			}
		}
		return ans;	
	}
	
	private LargeInteger modHelper(LargeInteger other, LargeInteger n) {
		LargeInteger tempSum = new LargeInteger(getVal());
		LargeInteger num = new LargeInteger(other.getVal());
			
		ArrayList<LargeInteger> adding = new ArrayList<LargeInteger>();
		
		String multplcnd = num.toBinary();
		int count = 0;
		for(int i = multplcnd.length()-1; i >= 0; i--){
			if(Integer.parseInt(String.valueOf(multplcnd.charAt(i)))==1){
				adding.add(tempSum);
			}
			count++;
		}

		LargeInteger runningSum = new LargeInteger(ZERO);
		for(LargeInteger temp: adding) {
			runningSum = runningSum.add(temp);
			if (!runningSum.isNegative()) {
				while (!runningSum.subtract(n).isNegative()) {
					runningSum = runningSum.subtract(n);
				}
			}
			else if (runningSum.isNegative()) {
				while (runningSum.add(n).isNegative()) {
					runningSum = runningSum.add(n);
				}
			}
		}
			
		return runningSum;
	}
	 
	public LargeInteger signCompress(LargeInteger x){	
		
		if(x.equalsZero()){
			return new LargeInteger(ZERO);
		}else if(x.add(new LargeInteger(ONE)).equalsZero()){
			return new LargeInteger(NEGONE);
		}
		
		byte[] xBytes = x.getVal();
		byte[] newv;
		LargeInteger ret;
		
		int pos = 0;
		if(x.isNegative()){			
			while(xBytes[pos] == (byte) 0xff){
				pos++;
			}
			
			newv = new byte[val.length - pos];
			for (int i = pos; i < val.length; i++) {
				newv[i-pos] = val[i];
			}
			
			ret = new LargeInteger(newv);
			if(!ret.isNegative()){
				ret.extend((byte) 0xff);
			}
			
		}else{
			while(xBytes[pos] == (byte) 0){
				pos++;
			}
			
			newv = new byte[val.length - pos];
			for (int i = pos; i < val.length; i++) {
				newv[i-pos] = val[i];
			}
			
			ret = new LargeInteger(newv);
			if(ret.isNegative()){
				ret.extend((byte) 0);
			}
		}
		
		return ret;
	}
	
	public LargeInteger shiftRight(int shift){
		
		LargeInteger result;
		String bitString = this.toBinary();
		if(shift >= bitString.length()){
			return new LargeInteger(ZERO);
		}
		
		//bit used for sign extension
		char sign = '0';
		if(this.isNegative()){
			sign = '1';
		}
		
		//"shift" by removing last characters of representative bitstring
		bitString = bitString.substring(0, bitString.length() - shift);
		
		//
		while((bitString.length() % 8) != 0){
			bitString = sign + bitString;
		}
		
		byte[] resBytes = new byte[bitString.length()/8];
		
		int pos = 0;
		for(int i=0;i<resBytes.length;i++){
			String temp = bitString.substring(pos, pos+8);
			resBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
			pos+=8;
		}
		
		result = new LargeInteger(resBytes);
		return result;
	}
	 
	public String toString(){
		String temp="";
		for(byte b : val){
			temp += String.format("0x%02X ", b);
		}
		return temp;

	}
	
	public String toBinary(){
		String temp="";
		for(byte b : val){
			temp += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
		}
		return temp;
	}
	 
	//used for testing
	public static void main(String[] argv){
		
		int numBits = 2;
		byte[] xBytes = new byte[numBits];
		byte[] yBytes = new byte[numBits];
		byte[] nBytes = new byte[numBits];
		
		/*
		byte[] xBytes = {(byte) 0x76};
		byte[] yBytes = {(byte) 0x48};
		byte[] nBytes = {(byte) 0x17};*/
		
		/*
		byte[] d1 = {(byte) 0x00, (byte) 0x90};
		byte[] d2 = {(byte) 0x17};
		
		LargeInteger dvdnd = new LargeInteger(d1);
		LargeInteger dvsr = new LargeInteger(d2);
		
		System.out.println("d1 = " + dvdnd.toString());
		System.out.println("d2 = " + dvsr.toString());
		
		dvdnd = dvdnd.signCompress(dvdnd);
		LargeInteger[] ans = dvdnd.divide(dvsr);
		System.out.println("d1/d2 = " + ans[0].toString());
		System.out.println("d1%d2 = " + ans[1].toString());*/
		
		
		for(int i=0;i<xBytes.length;i++){
			xBytes[i] = (byte)((int)(Math.random()*128));
			//xBytes[i] = (byte)((int)(Math.random()*256));
		}
		
		for(int i=0;i<yBytes.length;i++){	
			yBytes[i] = (byte)((int)(Math.random()*128));
			//yBytes[i] = (byte)((int)(Math.random()*256));
		}
		
		for(int i=0;i<nBytes.length;i++){	
			nBytes[i] = (byte)((int)(Math.random()*128));
			//nBytes[i] = (byte)((int)(Math.random()*256));
		}
		
		LargeInteger x = new LargeInteger(xBytes);
		LargeInteger y = new LargeInteger(yBytes);
		LargeInteger n = new LargeInteger(nBytes);
		
		System.out.println("x = " + x.toString());
		System.out.println("y = " + y.toString());
		System.out.println("n = " + n.toString());
		System.out.println();
		
		/*
		LargeInteger[] xgcd = x.XGCD(y);
		LargeInteger d = xgcd[0];
		LargeInteger a = xgcd[1];
		LargeInteger b = xgcd[2];
		
		System.out.println("d = " + d.toString());
		System.out.println("a = " + a.toString());
		System.out.println("b = " + b.toString());
		*/
		
		LargeInteger modExp = x.modularExp(y,n);
		System.out.println("(x^y)%n = " + modExp.toString());
		
		
	 }	 
}