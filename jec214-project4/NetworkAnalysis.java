import java.io.*;
import java.util.*;

public class NetworkAnalysis{
	
	public static void main(String[] args){
		
		int size = 0;
		EdgeList edgeList = new EdgeList();
		System.out.println("\n\nInitializing ...");
		
		//add apartments from file
		
		try{
			
			BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
			String line = null;
			String[] sects = new String[5];
			size = Integer.parseInt(in.readLine());//ignore header line
			int count = 0;
			
			while (((line = in.readLine()) != null)){
				//split on file delimiter
				sects = line.split(" ");
				//graph.addEdge(Integer.parseInt(sects[0]), Integer.parseInt(sects[1]));
				edgeList.addEdge(new Edge(Integer.parseInt(sects[0]), Integer.parseInt(sects[1]), sects[2], Integer.parseInt(sects[3]), Integer.parseInt(sects[4])));
				//System.out.println(edgeList.getEdges().get(count));
				count++;
			}
			in.close();			
		}
		catch(IOException e){
			e.printStackTrace();
        }
		
		boolean cont = true;
		int choice = 0;
		Scanner input = new Scanner(System.in);
		
		AdjList graph = new AdjList(size);
		graph.buildFromEdges(edgeList);
		//graph.printGraph();
		
		while(cont){			
			
			//display menu
			System.out.println("\n\nPlease select an option:");
			System.out.println("\t1)Lowest Latency Path");
			System.out.println("\t2)Is Graph Copper Only Connected?");
			System.out.println("\t3)Max Flow between Two Vertices");
			System.out.println("\t4)Vertex Failures");
			System.out.println("\t5)Exit");
			
			choice = input.nextInt();
			input.nextLine();
			while((choice < 1) || (choice > 5)){
				System.out.println("Invalid choice - please select a valid option 1-4");
				System.out.println("Please select an option:");
				choice = input.nextInt();
				input.nextLine();
			}
			
			if(choice == 1){
				
				//Lowest latency path (implement Dijkstra's)
				
				//prompt user for two vertices				
				System.out.println("Choose the first vertex in the pair:");
				int v1 = input.nextInt();
				input.nextLine();
				
				System.out.println("Choose the second vertex in the pair:");
				int v2 = input.nextInt();
				input.nextLine();
				
				//print result
				graph.lowestLatencyPath(v1, v2, edgeList);
				
				
			}else if(choice == 2){
				
				//Copper connected (find spanning tree)
				
				//create new adj list using optical edges
				AdjList graphCopy = new AdjList(graph.size());
				EdgeList edgeCopy = new EdgeList();
				ArrayList<Edge> edges = edgeList.getEdges();
				
				for(int i=0;i<edges.size();i++){
					if(edges.get(i).getCable().equals("copper")){
						edgeCopy.addEdge(edges.get(i));
						System.out.println(edges.get(i));
					}
				}
				
				graphCopy.buildFromEdges(edgeCopy);
				graphCopy.printGraph();
				
				//test connectivity
				boolean connected = graphCopy.testConnectivity();
				
				if(connected){
					System.out.println("Copper connected!!!");
				}else{
					System.out.println("Not copper connected!!!");
				}
				
			}else if(choice == 3){
				
				//Max flow (FlowEdge)
				System.out.println("Chose option 3");

				//prompt user for two vertices				
				System.out.println("Choose the first vertex in the pair:");
				int v1 = input.nextInt();
				input.nextLine();
				
				System.out.println("Choose the second vertex in the pair:");
				int v2 = input.nextInt();
				input.nextLine();
				
				graph.getMaxFlow(v1, v2, edgeList);
				
			}else if(choice == 4){
				
				AdjList graphCopy = new AdjList(graph.size());
				EdgeList edgeCopy = new EdgeList();
				ArrayList<Edge> edges = edgeList.getEdges();
				
				boolean success = true;
				
				//Failure of any two vertices (remove from edge/adjacency lists, then find spanning tree)
				for(int i=0;i<graph.size();i++){
					for(int j=i+1;j<graph.size();j++){
						
						System.out.println("\n\nTest removing vertices "+i+","+j);
						
						graphCopy = new AdjList(graph.size());
						edgeCopy = new EdgeList();
						
						for(int k=0;k<edges.size();k++){
							if(edges.get(k).getSrc() != i && edges.get(k).getSrc() != j && edges.get(k).getDest() != i && edges.get(k).getDest() != j){
								edgeCopy.addEdge(edges.get(k));
								//System.out.println(edges.get(k));
							}
						}
						
						graphCopy.buildFromEdges(edgeCopy);
						//graphCopy.printGraph();
						
						//test connectivity
						boolean connected = graphCopy.testConnectivityRemoved(i,j);
						if(connected){
							System.out.println("Connected when "+i+", "+j+" removed");
						}
						else{
							System.out.println("Not connected when "+i+", "+j+" removed");
							success = false;
						}
					}
				}
				
				if(success){
					System.out.println("Removal test successful!!!");
				}
				else{
					System.out.println("Removal test failed!!!");
				}			
				
			}else{
				cont = false;
			}
		}
		
		System.out.println("Program Closing...");
	}
}