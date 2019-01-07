public class Edge{
		
	private int src;  //start point of edge
	private int dest;  //end point of edge
	private String cable;  //cable type
	private int bandwidth;  //bandwidth in megabits per second
	private int length;  //length of the cable in meters
	
	public Edge(int s, int d, String c, int bw, int l) {
		src = s;
		dest = d;
		cable = c;
		bandwidth = bw;
		length = l;
	}
		
	public int getSrc() {
		return src;
	}
	
	public int getDest() {
		return dest;
	}
		
	public String getCable() {
		return cable;
	}
		
	public int getBandwidth() {
		return bandwidth;
	}
		
	public int getLength() {
		return length;
	}
		
	public String toString() {
		return "A " + length + " meter " + cable + " cable bewteen switch " + src + " and switch " + dest + " with a bandwidth of " + bandwidth + " megabits per second";
	}
}