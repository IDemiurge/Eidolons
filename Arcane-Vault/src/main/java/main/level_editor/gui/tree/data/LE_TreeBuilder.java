package main.level_editor.gui.tree.data;

import java.util.stream.Collectors;

public class LE_TreeBuilder<T extends LayeredData<T>> {

    private final LE_Node node;

    public LE_TreeBuilder(T t) {
      node =   create((LayeredData<LayeredData<T>>) t);

    }

    public LE_Node getNode() {
        return node;
    }

    public LE_Node create(LayeredData<LayeredData<T>> data){
        if (data.getChildren()==null) {
            return new LE_Node(data);
        }
        LE_Node node = new LE_Node(data, data.getChildren().stream().map(
                child -> create((LayeredData<LayeredData<T>>) child)).collect(Collectors.toSet())
        );
        return node;
    }
}
