package eidolons.game.exploration.dungeons.generator.graph.dijkstra;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UndirectedGraph<V, VertexData, EdgeData> {
    private final Map<V, VertexData> node;
    private final Map<V, Map<V, EdgeData>> adj;
    // constructor
    public UndirectedGraph() {
        this.node = new ConcurrentHashMap<>();
        this.adj = new ConcurrentHashMap<>();
    }
    // adds a node to the graph
    public void addNode(V n, VertexData d) {
        this.adj.put(n, new ConcurrentHashMap<>());
        this.node.put(n, d);
    }
    // removes a node from the graph
    public void removeNode(V n) {
        Set<V> nbrs = this.getNeighbors(n);
        for (V u : nbrs) {
            this.adj.get(u).remove(n);
        }
        this.adj.remove(n);
        this.node.remove(n);
    }

    public void addEdge(V u, V v, EdgeData e) {
        this.adj.get(u).put(v, e);
        this.adj.get(v).put(u, e);
    }

    public void removeEdge(V u, V v) {
        this.adj.get(u).remove(v);
        if (u != v) { // if not self-loop
            this.adj.get(v).remove(u);
        }
    }

    public Set<V> getNeighbors(V n) {
        return this.adj.get(n).keySet();
    }

    public int getNumNodes() {
        return node.size();
    }

    public Set<V> getNodes() {
        return this.node.keySet();
    }

    public int getNumEdges() {
        // return number of edges in entire graph
        int total = 0;
        for (V n : this.adj.keySet()) {
            total += this.adj.get(n).size();
        }
        return total/2;
    }

    public void printGraph() {
        System.out.println("Node: \t Edges: ");
        for (V n : this.adj.keySet()) {
            System.out.format("%s :  %s %n", n, this.adj.get(n).keySet());
        }
    }

}
