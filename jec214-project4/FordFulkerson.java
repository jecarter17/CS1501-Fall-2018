import java.util.*;

public class FordFulkerson {  //modified code from textbook
	    
	private static final double FLOATING_POINT_EPSILON = 1E-11;

	private final int V;          // number of vertices
	private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
	private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
	private int value;         // current value of max flow
	  
	public FordFulkerson(FlowEdge[] G, int s, int t) {
	    V = G.length;
	    validate(s);
	    validate(t);
	    if (s == t)               throw new IllegalArgumentException("Source equals sink");
	    if (!isFeasible(G, s, t)) throw new IllegalArgumentException("Initial flow is infeasible");

	    // while there exists an augmenting path, use it
	    value = localEq(G, t);
	    while (hasAugmentingPath(G, s, t)) {

	        // compute bottleneck capacity
	        int bottle = Integer.MAX_VALUE;
	        for (int v = t; v != s; v = edgeTo[v].other(v)) {
	            bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
	        }

	        // augment flow
	        for (int v = t; v != s; v = edgeTo[v].other(v)) {
	            edgeTo[v].addResidualFlowTo(v, bottle); 
	        }

	        value += bottle;
	    }
	}

	public int value()  {
	    return value;
	}

	// throw an IllegalArgumentException if v is outside prescribed range
	private void validate(int v)  {
	    if (v < 0 || v >= V)
	        throw new IllegalArgumentException("switch " + v + " is not between 0 and " + (V-1));
	}

	private boolean hasAugmentingPath(FlowEdge[] G, int s, int t) {
	    edgeTo = new FlowEdge[G.length];
	    marked = new boolean[G.length];
	    // breadth-first search
	    ArrayList<Integer> queue = new ArrayList<Integer>();
	    queue.add(s);
	    marked[s] = true;
	    while (!queue.isEmpty() && !marked[t]) {
	        int v = queue.remove(0);
	        FlowEdge e = G[v];
	        while (e != null) {
	            int w = e.other(v);
	            // if residual capacity from v to w
	            if (e.residualCapacityTo(w) > 0) {
	                if (!marked[w]) {
	                    edgeTo[w] = e;
	                    marked[w] = true;
	                    queue.add(w);
	                }
	            }
	            e = e.next();
	        }
	    }
	    // is there an augmenting path?
	    return marked[t];
	}

	// return excess flow at vertex v
	private int localEq(FlowEdge[] G, int v) {
	    int excess = 0;
	    FlowEdge e = G[v];
	    while (e != null) {
	        if (v == e.from()) excess -= e.flow();
	        else               excess += e.flow();
			e = e.next();
	    }
	    return excess;
	}

	// return excess flow at vertex v
	private boolean isFeasible(FlowEdge[] G, int s, int t) {
        // check that capacity constraints are satisfied
        for (int v = 0; v < G.length; v++) {
			FlowEdge e = G[v];
			while (e != null) {
	            if (e.flow() < -FLOATING_POINT_EPSILON || e.flow() > e.capacity() + FLOATING_POINT_EPSILON) {
	                System.err.println("Edge does not satisfy capacity constraints: " + e);
	                return false;
	            }
	            e = e.next();
	        }
	    }

	    // check that net flow into a vertex equals zero, except at source and sink
	    if (Math.abs(value + localEq(G, s)) > FLOATING_POINT_EPSILON) {
	        System.err.println("Excess at source = " + localEq(G, s));
	        System.err.println("Max flow         = " + value);
	        return false;
	    }
	    if (Math.abs(value - localEq(G, t)) > FLOATING_POINT_EPSILON) {
	        System.err.println("Excess at sink   = " + localEq(G, t));
	        System.err.println("Max flow         = " + value);
	        return false;
	    }
	    for (int v = 0; v < G.length; v++) {
	        if (v == s || v == t) continue;
	        else if (Math.abs(localEq(G, v)) > FLOATING_POINT_EPSILON) {
	            System.err.println("Net flow out of " + v + " doesn't equal zero");
	            return false;
	        }
	    }
	    return true;
	}
}