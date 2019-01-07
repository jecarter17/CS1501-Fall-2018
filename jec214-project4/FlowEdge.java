public class FlowEdge {  //modified code from textbook

	private final int v;             // from
    private final int w;             // to 
    private final int capacity;   // capacity
    private int flow;
	private FlowEdge next;

    public FlowEdge(int v, int w, int capacity) {
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        flow      = 0;
        next = null;
    }
	    
    public void setNext(FlowEdge next) {
    	this.next = next;
    }

	public int from() {
        return v;
    }  

    public int to() {
        return w;
    }  

    public int capacity() {
        return capacity;
    }

    public int flow() {
        return flow;
    }
	    
	public FlowEdge next() {
	   	return next;
	}

	public int other(int vertex) {
	    if      (vertex == v) return w;
	    else if (vertex == w) return v;
	    else throw new IllegalArgumentException("invalid endpoint");
	}

	public int residualCapacityTo(int vertex) {
	    if      (vertex == v) return flow;              // backward edge
	    else if (vertex == w) return capacity - flow;   // forward edge
	    else throw new IllegalArgumentException("invalid endpoint");
	}

	public void addResidualFlowTo(int vertex, int delta) {
	    if (!(delta >= 0)) throw new IllegalArgumentException("Delta must be nonnegative");
	      
	    if      (vertex == v) flow -= delta;           // backward edge
		else if (vertex == w) flow += delta;           // forward edge
	    else throw new IllegalArgumentException("invalid endpoint");

	    if (!(flow >= 0.0))      throw new IllegalArgumentException("Flow is negative");
	    if (!(flow <= capacity)) throw new IllegalArgumentException("Flow exceeds capacity");
	}
}