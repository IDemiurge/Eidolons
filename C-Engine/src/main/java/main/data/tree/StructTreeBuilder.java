package main.data.tree;

import java.util.stream.Collectors;

public class StructTreeBuilder<T extends LayeredData<T>> {

    private final StructNode root;

    public StructTreeBuilder(T t) {
      root =   create((LayeredData<LayeredData<T>>) t);

    }

    public StructNode getRoot() {
        return root;
    }

    public StructNode create(LayeredData<LayeredData<T>> data){
        if (data.getChildren()==null) {
            return new StructNode(data);
        }
        StructNode node = new StructNode(data, data.getChildren().stream().map(
                child -> create((LayeredData<LayeredData<T>>) child)).collect(Collectors.toSet())
        );
        return node;
    }
}
