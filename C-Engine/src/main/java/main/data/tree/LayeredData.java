package main.data.tree;

import java.util.Collection;

public interface LayeredData<T> {

    Collection<T> getChildren();

    default Object getLevelLayer() {
        return null;
    }

}
