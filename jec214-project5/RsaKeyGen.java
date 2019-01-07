import java.util.*;
import java.io.*;

public class RsaKeyGen{

	static final byte[] ONE = {(byte) 1};
	
	public static void main(String[] argv){
		
		Random r = new Random();
		LargeInteger p = new LargeInteger(256, r);
		LargeInteger q = new LargeInteger(256, r);
		LargeInteger n = p.multiply(q);
		
		while (n.isNegative()) {
			p = new LargeInteger(256, r);
			q = new LargeInteger(256, r);
			n = p.multiply(q);
        }
		
		LargeInteger pmin1 = p.subtract(new LargeInteger(ONE));
		LargeInteger qmin1 = q.subtract(new LargeInteger(ONE));
		LargeInteger phin = pmin1.multiply(qmin1);
		
		boolean found_e = false;
		LargeInteger e = new LargeInteger(512, r);
		LargeInteger d = new LargeInteger(ONE);
		while(!found_e){
			LargeInteger temp = e.subtract(new LargeInteger(ONE));
			if(!temp.equalsZero() && !temp.isNegative()){
				
				LargeInteger[] xgcd = e.XGCD(phin);
				LargeInteger gcd = xgcd[0];
				if(gcd.subtract(new LargeInteger(ONE)).equalsZero()){
					//gcd = 1 and 1<e<phi(n)
					found_e = true;
					d = xgcd[1];
				}else{
					e = new LargeInteger(512, r);
					System.out.println("Trying new e...");
				}
			}else{
				e = new LargeInteger(512, r);
				System.out.println("Trying new e...");
			}
		}
		
		while (d.isNegative()) {
        	d = d.add(phin);
        }
		
		e = e.signCompress(e);
		d = d.signCompress(d);
		n = n.signCompress(n);
		
		while(e.length() < 64){
			e.extend((byte) 0);
		}
		
		while(d.length() < 64){
			d.extend((byte) 0);
		}	
		
		while(n.length() < 64){
			n.extend((byte) 0);
		}
		
		/*
		System.out.println("e = "+e.toString());
		System.out.println("d = "+d.toString());
		System.out.println("n = "+n.toString());
		System.out.println("e is length "+e.length());
		System.out.println("d is length "+d.length());
		System.out.println("n is length "+n.length());*/
		
		String fileName = "pubkey.rsa";
		try{			
			BufferedWriter pubWriter = new BufferedWriter(new FileWriter(fileName));
			pubWriter.write(e.toBinary());
			pubWriter.newLine();
			pubWriter.write(n.toBinary());    
			pubWriter.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}

		fileName = "privkey.rsa";
		try{
			BufferedWriter privWriter = new BufferedWriter(new FileWriter(fileName));
			privWriter.write(d.toBinary());
			privWriter.newLine();
			privWriter.write(n.toBinary());    
			privWriter.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}