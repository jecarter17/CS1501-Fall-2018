import java.util.*;

public class EdgeList{
	
	private ArrayList<Edge> edges;
	
	public EdgeList(){
		edges = new ArrayList<Edge>();
	}
	
	public void addEdge(Edge e){
		edges.add(e);
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
}