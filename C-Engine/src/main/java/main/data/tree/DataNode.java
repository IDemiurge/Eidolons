package main.data.tree;

import java.util.Set;

public class DataNode<T, N> {
    protected boolean leaf;
    protected T data;
    protected Set<N> children;

    public boolean isLeaf() {
        return leaf;
    }

    public T getData() {
        return data;
    }

    public Set<? extends N> getChildren() {
        return children;
    }
}
