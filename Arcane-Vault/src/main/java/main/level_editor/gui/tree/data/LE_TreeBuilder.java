package main.level_editor.gui.tree.data;

import java.util.stream.Collectors;

public class LE_TreeBuilder<T extends LayeredData<T>> {

    private final LE_DataNode root;

    public LE_TreeBuilder(T t) {
      root =   create((LayeredData<LayeredData<T>>) t);

    }

    public LE_DataNode getRoot() {
        return root;
    }

    public LE_DataNode create(LayeredData<LayeredData<T>> data){
        if (data.getChildren()==null) {
            return new LE_DataNode(data);
        }
        LE_DataNode node = new LE_DataNode(data, data.getChildren().stream().map(
                child -> create((LayeredData<LayeredData<T>>) child)).collect(Collectors.toSet())
        );
        return node;
    }
}
