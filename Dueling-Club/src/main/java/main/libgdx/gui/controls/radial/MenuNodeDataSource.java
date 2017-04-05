package main.libgdx.gui.controls.radial;

import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.List;

public interface MenuNodeDataSource {
    ActionValueContainer getCurrent();

    List<ActionValueContainer> getChilds();
}
