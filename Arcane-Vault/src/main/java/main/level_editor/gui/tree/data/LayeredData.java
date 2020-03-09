package main.level_editor.gui.tree.data;

import java.util.Set;

public interface LayeredData<T extends LayeredData> {

    Set<T> getChildren();
}
