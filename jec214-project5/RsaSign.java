import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class RsaSign {
	
	static final byte[] ONE = {(byte) 1};
	
	public static void sign(String filename) throws IOException, NoSuchAlgorithmException {
		
		String keyFilename = "privkey.rsa";
		File keyFile = new File(keyFilename);
		if (keyFile.exists()) {
			
			//*****fetch d and n*****
			String dString = "";
			String nString = "";
			System.out.println("Retreiving private keys...");
			try{			
				BufferedReader privReader = new BufferedReader(new FileReader(keyFilename));
				dString = privReader.readLine();
				//System.out.println("d is "+dString.length()+"bits");
				nString = privReader.readLine();
				//System.out.println("n is "+nString.length()+"bits");
				privReader.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		
			byte[] dBytes = new byte[dString.length()/8];
			byte[] nBytes = new byte[nString.length()/8];
		
			int pos = 0;
			for(int i=0;i<dBytes.length;i++){
				String temp = dString.substring(pos, pos+8);
				dBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
				pos+=8;
			}
			
			pos = 0;
			for(int i=0;i<nBytes.length;i++){
				String temp = nString.substring(pos, pos+8);
				nBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
				pos+=8;
			}
		
			LargeInteger d = new LargeInteger(dBytes);
			LargeInteger n = new LargeInteger(nBytes);
			//System.out.println("d = "+d.toString());
			//System.out.println("n = "+n.toString());
			LargeInteger hash = new LargeInteger(ONE);

			//*****generate hash of private key*****
			try {
				// read in the file to hash
				Path path = Paths.get(filename);
				byte[] data = Files.readAllBytes(path);

				// create class instance to create SHA-256 hash
				MessageDigest md = MessageDigest.getInstance("SHA-256");

				// process the file
				md.update(data);
				
				// generate a hash of the file
				byte[] digest = md.digest();
				System.out.print("SHA-256 hash = ");
				for (byte b : digest) {
					System.out.print(String.format("%02x", b));
				}
				System.out.println();
				hash = new LargeInteger(digest);
			}catch(Exception e){
				System.out.println(e.toString());
			}
			
			System.out.println("Decrypting hash...");
			LargeInteger decrypt = hash.modularExp(d, n);
			//System.out.println("decrypt = " + decrypt.toString());
			
			System.out.println("Saving signature...");
		    BufferedWriter sigWriter = new BufferedWriter(new FileWriter(filename+".sig"));
		    sigWriter.write(decrypt.toBinary());
		    sigWriter.close();
		
		}else{
			System.out.println("No private key found!");
		}
	}
	
	public static void verify(String filename) throws IOException, NoSuchAlgorithmException {
		
		String keyFilename = "pubkey.rsa";
		File keyFile = new File(keyFilename);
		if (keyFile.exists()) {
			
			//*****fetch e and n*****
			String eString = "";
			String nString = "";
			System.out.println("Retrieving public keys...");
			try{			
				BufferedReader pubReader = new BufferedReader(new FileReader(keyFilename));
				eString = pubReader.readLine();
				//System.out.println("e is "+eString.length()+"bits");
				nString = pubReader.readLine();
				//System.out.println("n is "+nString.length()+"bits");
				pubReader.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		
			byte[] eBytes = new byte[eString.length()/8];
			byte[] nBytes = new byte[nString.length()/8];
		
			int pos = 0;
			for(int i=0;i<eBytes.length;i++){
				String temp = eString.substring(pos, pos+8);
				eBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
				pos+=8;
			}
			
			pos = 0;
			for(int i=0;i<nBytes.length;i++){
				String temp = nString.substring(pos, pos+8);
				nBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
				pos+=8;
			}
		
			LargeInteger e = new LargeInteger(eBytes);
			LargeInteger n = new LargeInteger(nBytes);
			//System.out.println("e = "+e.toString());
			//System.out.println("n = "+n.toString());
			

			//*****hash contents of original file*****
			LargeInteger hash = new LargeInteger(ONE);
			
			try {
				// read in the file to hash
				Path path = Paths.get(filename);
				byte[] data = Files.readAllBytes(path);

				// create class instance to create SHA-256 hash
				MessageDigest md = MessageDigest.getInstance("SHA-256");

				// process the file
				md.update(data);
				
				// generate a hash of the file
				byte[] digest = md.digest();
				System.out.print("SHA-256 hash = ");
				for (byte b : digest) {
					System.out.print(String.format("%02x", b));
				}
				System.out.println();
				hash = new LargeInteger(digest);
			}catch(Exception ex){
				System.out.println(ex.toString());
			}
			
			String signature = "";
			try{
				System.out.println("Retreiving signature...");
				BufferedReader sigReader = new BufferedReader(new FileReader(filename+".sig"));
				signature = sigReader.readLine();
				sigReader.close();
			}catch(Exception ex){
				System.out.println(ex.toString());
			}
			byte[] sigBytes = new byte[64];
		
			pos = 0;
			for(int i=0;i<sigBytes.length;i++){
				String temp = signature.substring(pos, pos+8);
				sigBytes[i] = (byte)(Integer.parseInt(temp, 2) & 0xff);
				pos+=8;
			}
			
			LargeInteger sig = new LargeInteger(sigBytes);			
					
			System.out.println("Encrypting signature...");
			LargeInteger encrypt = sig.modularExp(e, n);
			//System.out.println("encrypt = "+encrypt.toString());
					
			System.out.println("Checking if the signautre is valid...");
			if(encrypt.subtract(hash).equalsZero()) {
				System.out.println("\tThe signature is valid!");
			}else{
				System.out.println("\tThe signature is not valid");
			}
		
		}else{
			System.out.println("No public key found!");
		}
	}
		
	public static void main(String[] argv) throws NoSuchAlgorithmException, IOException{		
		
		if (argv.length == 2){
			
			String flag = argv[0];
			String filename = argv[1];
			
			File message = new File(filename);
			if(message.exists()){				
				if (flag.equals("s")){ 
					sign(filename);
				}else if(flag.equals("v")){
					verify(filename);
				}
				else{
					System.out.println("Invalid Flag!!!");
				}	
			}
			else{
				System.out.println("Message file does not exist!!!");
			}
		}else{
			System.out.println("Invalid number of arguments!!!");
		}
	}
}