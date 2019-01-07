public class Test{

	public static void main(String[] argv){
		
		int numBits = 4;
		byte[] xBytes = new byte[numBits];
		byte[] yBytes = new byte[numBits];
		byte[] nBytes = new byte[numBits];
		
		
		//manually set bytes
		/*
		byte[] xBytes = {(byte) 0x76};
		byte[] yBytes = {(byte) 0x48};
		byte[] nBytes = {(byte) 0x17};*/
		
		
		//randomly generate bytes
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
		
		
		//XGCD
		/*
		LargeInteger[] xgcd = x.XGCD(y);
		LargeInteger d = xgcd[0];
		LargeInteger a = xgcd[1];
		LargeInteger b = xgcd[2];
		
		System.out.println("d = " + d.toString());
		System.out.println("a = " + a.toString());
		System.out.println("b = " + b.toString());
		*/
		
		//modularExp
		/*
		LargeInteger modExp = x.modularExp(y,n);
		System.out.println("(x^y)%n = " + modExp.toString());*/
		
		
	 }
}