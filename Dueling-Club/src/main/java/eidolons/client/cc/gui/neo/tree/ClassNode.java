package eidolons.client.cc.gui.neo.tree;

import eidolons.content.PROPS;
import main.entity.type.ObjType;

public class ClassNode extends HT_Node {

    public ClassNode(ObjType type, int size, ObjType parent) {
        super(type, size, parent);
    }

    @Override
    protected PROPS getContainerProperty() {
        return PROPS.CLASSES;
    }

    @Override
    public boolean isAcquired() {
        return super.isAcquired();
    }

}
