package eidolons.game.exploration.dungeon.generator.graph.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;

public class SinglePaths<V> {
    private final HashMap<V, HashMap<V, LinkedList<V>>> data;

    public SinglePaths() {
        data = new HashMap<>();

    }
    public void addPath(V source, V target, LinkedList<V> path) {
        if (!this.data.containsKey(source)) {
            this.data.put(source, new HashMap<>());
        }
        this.data.get(source).put(target, path);
    }
    public LinkedList<V> getPath(V source, V target) {
        if (!this.data.containsKey(source) || !this.data.get(source).containsKey(target)) {
            return new LinkedList<>();
        }
        return this.data.get(source).get(target);
    }
    public void printPaths() {
        for (V source: this.data.keySet()) {
            for (V target : data.get(source).keySet()) {
                System.out.format("%s-%s : %s%n", source.toString(),
                 target.toString(), getPath(source, target).toString());
            }
        }
    }
}