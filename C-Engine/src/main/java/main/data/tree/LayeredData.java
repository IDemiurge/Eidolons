package main.data.tree;

import java.util.Set;

public interface LayeredData<T extends LayeredData> {

    Set<T> getChildren();

    default Object getLevelLayer() {
        return null;
    }
}
