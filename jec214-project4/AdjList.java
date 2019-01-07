import java.util.*;

public class AdjList{
	
	private AdjListNode[] list;
	private boolean[] marked;
	
	public AdjList(int num){
		list = new AdjListNode[num];
	}
	
	public void buildFromEdges(EdgeList e){
		
		for(int i=0;i<e.getEdges().size();i++){
			addEdge(e.getEdges().get(i).getSrc(), e.getEdges().get(i).getDest());
		}
	}
	
	public int size(){
		return list.length;
	}
	
	public void addEdge(int v1, int v2){
		
		if(list[v1] == null){
			list[v1] = new AdjListNode(v2, null);
		}else{
			AdjListNode curr = list[v1];		
		
			while(curr.getNext() != null){
				curr = curr.getNext();
			}
			curr.setNext(new AdjListNode(v2, null));
		}

		if(list[v2] == null){
			list[v2] = new AdjListNode(v1, null);
		}else{
			AdjListNode curr = list[v2];		
		
			while(curr.getNext() != null){
				curr = curr.getNext();
			}
			curr.setNext(new AdjListNode(v1, null));
		}
	}
	
	public void printGraph(){        
        for(int v = 0; v < list.length; v++) 
        { 
            System.out.println("Adjacency list of vertex "+ v); 
            System.out.print("head");
			AdjListNode curr = list[v];
			while(curr != null){
				System.out.print(" -> "+curr.getVert());
				curr = curr.getNext();
			}
            System.out.println("\n"); 
        } 
    }
	
	//use dfs to get spanning tree (adaptation of DepthFirstSearch.java)
	public boolean testConnectivity(){
		marked = new boolean[size()];
		int count = dfsHelper(0, list[0], 0);
		if(count == size()){
			return true;
		}
		return false;
    }
	
	// dfs from v
    private int dfsHelper(int v, AdjListNode curr, int count){
				
		if(curr == null || marked[v]){
			if(curr == null){
				System.out.println("Hit null");
			}
			else{
				System.out.println(v+" is marked");
			}
			return count;
		}
		
		System.out.println("At vertex "+v);
		
        count++;
        marked[v] = true;
		
		while(curr != null){
			if(!marked[curr.getVert()]){
				count = dfsHelper(curr.getVert(), list[curr.getVert()], count);
			}
			curr = curr.getNext();
		}
		
		return count;
    }
	
	public boolean testConnectivityRemoved(int rem1, int rem2){
		marked = new boolean[size()];
		int count = 0;
		for(int i=0;i<size();i++){
			if(i != rem1 && i != rem2){
				count = dfsHelper(i, list[i], 0);
				break;
			}
		}
		if(count == size()-2){
			return true;
		}
		return false;
    }
	
	public void lowestLatencyPath(int start, int end, EdgeList edgeList){
		
		double[] times = new double[size()];  //keeps track of min time(sec) between start point and other vertices
		boolean[] visited = new boolean[size()];  //says which vertices have been visited
		int[] via = new int[size()];   //stores path data	
		
		for (int i=0;i<size();i++) {
			times[i] = Integer.MAX_VALUE;   //set max_int for all vertices
			visited[i] = false;  //set all vertices as not visited
		}
		
		times[start] = 0;  //except the start
		
		int curr = start;
		while(curr != end && curr != -1){
			
			AdjListNode neighbor = list[curr];
			
			//iterate through neighbors
			while(neighbor != null){
				
				if(!visited[neighbor.getVert()]){
					
					//compute new tentative time
					double tentTime;
					Edge found = findEdge(curr, neighbor.getVert(), edgeList);					
					
					if (found.getCable().equals("copper")) {
						tentTime = found.getLength()/230000000.0;  //time using copper cable
					}
					else {
						tentTime = found.getLength()/200000000.0;  //time using fiber optic cable
					}
					double path = times[curr] + tentTime;  //compute new time
					
					if (path < times[neighbor.getVert()]) {	//relax time
						times[neighbor.getVert()] = path;  	//update time
						
						via[neighbor.getVert()] = curr;	  	//update via
					}
				}
				neighbor = neighbor.getNext();
			}
			visited[curr] = true;  //mark cur as visited
			curr = getNextMin(times, visited);
		}
		
		if(start == end){
			System.out.println("Start and end on same node!!!");
		}
		else{
			ArrayList<Integer> stack = new ArrayList<Integer>();
			
			int temp = end;
			int v = via[end];
			stack.add(new Integer(v));
			
			int minBW = Integer.MAX_VALUE;
			int bw = findEdge(v, temp, edgeList).getBandwidth();
			if(bw < minBW){
				minBW = bw;
			}			
			
			while(v != start){
				
				temp = v;
				v = via[v];				
				
				if(bw < minBW){
					minBW = bw;
				}
				
				stack.add(new Integer(v));
			}
			
			System.out.println("Lowest latency path from "+start+" to "+end+":");
			while(stack.size() > 0){
				System.out.print(stack.remove(stack.size()-1).intValue() + " -> ");
			}
			System.out.println(end);
			System.out.println("Available bandwidth: "+minBW+" Mb/s");
		}
	}
	
	private int getNextMin(double[] times, boolean[] visited) {
		int answer = -1;   //return -1 if all vertices have been visited
		double minTime = Integer.MAX_VALUE;
		for (int i = 0; i < times.length; i++) {
			if (times[i] <= minTime && visited[i] == false) {  //if this is the min time unvisited index
				answer = i;
				minTime = times[i];
			}
		}
		return answer;
	}
	
	private Edge findEdge(int src, int dest, EdgeList edgeList){
		
		for(int i=0;i<edgeList.getEdges().size();i++){
			if((edgeList.getEdges().get(i).getSrc() == src && edgeList.getEdges().get(i).getDest() == dest) || (edgeList.getEdges().get(i).getSrc() == dest && edgeList.getEdges().get(i).getDest() == src)){
				return edgeList.getEdges().get(i);
			}
		}
		return null;
	}
	
	public void getMaxFlow(int src, int dest, EdgeList edgeList) {
		FlowEdge[] G = new FlowEdge[edgeList.getEdges().size()];
		for (int i = 0; i < size(); i++) {  //create flow network
			AdjListNode curr = list[i];
			Edge found = null;
			if(curr != null)
				found = findEdge(curr.getVert(), i, edgeList);
			if (found != null) {
				FlowEdge add = new FlowEdge(found.getSrc(), found.getDest(), found.getBandwidth());
				G[i] = add;
				curr = curr.getNext();
				found = null;
				if(curr != null)
					found = findEdge(curr.getVert(), i, edgeList);
				while (found != null) {
					add.setNext(new FlowEdge(found.getSrc(), found.getDest(), found.getBandwidth()));
					add = add.next();
					curr = curr.getNext();
					found = null;
					if(curr != null)
						found = findEdge(curr.getVert(), i, edgeList);
				}
			}
		}
		FordFulkerson flow = new FordFulkerson(G, src, dest);
		int maxflow = flow.value();
		
		System.out.print("\nThe max flow from " + src + " to " +dest + " is " + maxflow + " megabits per second.\n");
	}
	
	private class AdjListNode{
		int vertex;
		AdjListNode next;		
		
		public AdjListNode(int v, AdjListNode a){
			vertex = v;
			next = a;
		}
		
		private int getVert(){
			return vertex;
		}
		
		private AdjListNode getNext(){
			return next;
		}
		
		private void setNext(AdjListNode a){
			next = a;
		}
	}
}