import java.util.*;
import java.io.*;

public class AptTracker{

	public static void main(String[] args){
		
		Tracker tracker = new Tracker();
		
		System.out.println("\n\nInitializing tracker...");
		
		//add apartments from file		
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("apartments.txt")));
			String line = null;
			String[] sects = new String[6];
			in.readLine();//ignore header line
			while (((line = in.readLine()) != null)){
				//split on file delimiter
				sects = line.split(":");
				//System.out.println("\n*******************Add #"+count+"************************************\n");
				tracker.add(new Apartment(sects[0], sects[1], sects[2], Integer.parseInt(sects[3]), Integer.parseInt(sects[4]), Integer.parseInt(sects[5])));
				//tracker.show();
			}
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
        }
		
		boolean cont = true;
		int choice = 0;
		Scanner input = new Scanner(System.in);
		
		while(cont){			
			
			//display menu
			System.out.println("\n\nPlease select an option:");
			System.out.println("\t1)Add apartment");
			System.out.println("\t2)Update an apartment");
			System.out.println("\t3)Remove apartment");
			System.out.println("\t4)Retrieve lowest price apartment");
			System.out.println("\t5)Retrieve highest sqft apartment");
			System.out.println("\t6)Retrieve lowest price apartment by city");
			System.out.println("\t7)Retrieve highest sqft apartment by city");
			System.out.println("\t8)Exit");
			
			choice = input.nextInt();
			input.nextLine();
			while((choice < 1) || (choice > 8)){
				System.out.println("Invalid choice - please select a valid option 1-7");
				System.out.println("Please select an option:");
				choice = input.nextInt();
				input.nextLine();
			}
			
			if(choice == 1){
				
				//Add apartment
				
				//prompt user for fields
				System.out.println("Enter the street address of the new apartment:");
				String sa = input.nextLine();
				
				System.out.println("Enter the apt number of the new apartment:");
				String num = input.nextLine();
				
				System.out.println("Enter the city of the new apartment:");
				String c = input.nextLine();				
				
				System.out.println("Enter the zip code of the new apartment:");
				int z = input.nextInt();
				input.nextLine();
				
				System.out.println("Enter the price per month of the new apartment:");
				int p = input.nextInt();
				input.nextLine();
				
				System.out.println("Enter the square footage of the new apartment:");
				int sq = input.nextInt();
				input.nextLine();
				
				//add to data structure
				tracker.add(new Apartment(sa, num, c, z, p, sq));
				//tracker.show();
				
				
			}else if(choice == 2){
				
				//Update apartment			
				
				//prompt user for fields
				System.out.println("Enter the street address of apartment to update:");
				String sa = input.nextLine();
				
				System.out.println("Enter the apt number of apartment to update:");
				String num = input.nextLine();
				
				System.out.println("Enter the zip code of apartment to update:");
				int z = input.nextInt();
				input.nextLine();
				
				System.out.println("Enter the new price of apartment to update:");
				int p = input.nextInt();
				input.nextLine();
				
				//set new apartment price
				tracker.update(sa, num, z, p);
				
				
				
			}else if(choice == 3){
				
				//Remove apartment			
				
				//prompt user for fields
				System.out.println("Enter the street address of apartment to remove:");
				String sa = input.nextLine();
				
				System.out.println("Enter the apt number of apartment to remove:");
				String num = input.nextLine();
				
				System.out.println("Enter the zip code of apartment to remove:");
				int z = input.nextInt();
				input.nextLine();
				
				//remove apartment from data structure based on entry
				tracker.remove(sa, num, z);
				
			}else if(choice == 4){
				
				//Retrieve lowest price apartment
				
				Apartment apt = tracker.lowestPrice();
				
				System.out.println(apt);
				
			}else if(choice == 5){
				
				//Retrieve highest sqft apartment
				
				Apartment apt = tracker.highestArea();
				
				System.out.println(apt);
				
			}else if(choice == 6){
				
				//Retrieve lowest price apartment by city
				
				//prompt user for fields
				System.out.println("Enter the city you wish to search:");
				String city = input.nextLine();
				
				Apartment apt = tracker.cityLowestPrice(city);
				if(apt != null){
					System.out.println(apt);
				}
				else{
					System.out.println("No apartments found in this city!!!");
				}
				
			}else if(choice == 7){
				
				//Retrieve highest sqft apartment by city
				
				//prompt user for fields
				System.out.println("Enter the city you wish to search:");
				String city = input.nextLine();
				
				Apartment apt = tracker.cityHighestArea(city);
				if(apt != null){
					System.out.println(apt);
				}
				else{
					System.out.println("No apartments found in this city!!!");
				}
				
			}else{
				cont = false;
			}
		}
		
		System.out.println("Program Closing...");
	}
	
}