package eidolons.game.module.generator.graph.dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by JustMe on 7/29/2018.
 */
public class Dijkstra {

    /*
     * Provides some static methods for computing shortest paths between all
     * pairs of nodes in a graph G with respect to some source node
     * We will run Dijkstra's on each node
     */
    // returns minimum dist
    static <V> V getMinV(HashSet<V> unvisited, HashMap<V, Integer> dist) {
        Iterator<V> it = unvisited.iterator();
        V minU = it.next();
        Integer minDist = dist.get(minU);
        for (V u : unvisited) {
            int alt = dist.get(u);
            if (alt <= minDist) {
                minU = u;
                minDist = alt;
            }
        }
        return minU;
    }

    // compute shortest paths from each node to each other node
    // in the graph
    static <V> DijkstraResult<V> runDijkstra(V source, UndirectedGraph<V, ?, ?> G) {
        // shortest distances between source and other nodes in graph
        HashMap<V, Integer> dist = new HashMap<V, Integer>();
        // stores all prev nodes that each node can backtrack to to getVar to the source
        HashMap<V, V> prev = new HashMap<V, V>();
        // keep track of visited nodes
        HashSet<V> unvisited = new HashSet<V>();

        dist.put(source, 0);
        for (V v : G.getNodes()) {
            // initialize prev data
            if (!v.equals(source)) {
                dist.put(v, Integer.MAX_VALUE);
                prev.put(v, null);
            }
            unvisited.add(v);
        }

        while (!unvisited.isEmpty()) {
            // node with the minimum distance to source in
            // the set of unvisited nodes
            V u = getMinV(unvisited, dist);
            //System.out.format("%d%n", u);
            unvisited.remove(u);

            //  if min distance is infinite, it means source disconnected
            if (dist.get(u).equals(Integer.MAX_VALUE)) {
                break;
            }

            for (V v : G.getNeighbors(u)) {
                Integer alt = dist.get(u) + 1;

                if (alt < dist.get(v)) { // a shorter path has been found
                    dist.put(v, alt);
                    prev.put(v, u);
                }
            }
        }
        DijkstraResult<V> D = new DijkstraResult<V>();
        D.dist = dist;
        D.prev = prev;
        return D;
    }

    // computes shortest paths between all pairs
    // of nodes in a graph
    public static <V> SinglePaths<V> getSingleShortestPaths(UndirectedGraph<V, ?, ?> G) {
        // run dijkstra, then return first path in each
        // returns a list of lists for each node
        SinglePaths<V> paths = new SinglePaths<V>();
        for (V source : G.getNodes()) {
            if (source.equals(3)) {
                int foo = 1;
            }
            System.out.format("%d%n", source);
            DijkstraResult<V> D = runDijkstra(source, G);
            for (V target : G.getNodes()) {
                LinkedList<V> path = new LinkedList<V>();
                V u = target;
                while (D.prev.get(u) != null) {
                    path.addFirst(u);
                    u = D.prev.get(u);
                }
                paths.addPath(source, target, path);
            }
        }
        return paths;
    }

    static  class  DijkstraResult<V> {
        public HashMap<V, Integer> dist;
        public HashMap<V, V> prev; // all prev nodes with the given dist
    }

}
