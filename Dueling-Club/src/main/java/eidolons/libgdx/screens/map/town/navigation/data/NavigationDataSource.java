package eidolons.libgdx.screens.map.town.navigation.data;

import java.util.Set;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigationDataSource {
    Navigable root;
    Set<Navigable> children;
    Set<Navigable> path;


    public NavigationDataSource(Navigable root, Set<Navigable> children, Set<Navigable> path) {
        this.root = root;
        this.children = children;
        this.path = path;
    }

    public Set<Navigable> getChildren() {
        return children;
    }

    public Set<Navigable> getPath() {
        return path;
    }

    public Navigable getRoot() {
        return root;
    }


    public boolean canEnter(Navigable navigable) {
        return true;
    }
}
