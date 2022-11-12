package data;

import java.util.List;

public class C3Node {
    protected Object value;
    protected List<C3Node> children;

    public C3Node(Object value, List<C3Node> children) {
        this.value = value;
        this.children = children;
    }
}
