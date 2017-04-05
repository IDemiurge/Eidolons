package main.libgdx.gui.controls.radial;

import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.Collections;
import java.util.List;

public interface MenuNodeDataSource {
    ActionValueContainer getCurrent();

    default List<MenuNodeDataSource> getChilds() {
        return Collections.EMPTY_LIST;
    }
}
