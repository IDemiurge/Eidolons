package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 */
public interface ShotDatasource {

    public List<TablePanel> getTabs();
    public List<TablePanel> getGroupLists(String tabName);
    public List<ValueContainer> getTextures(String groupList);
    public ValueContainer getName();
    public ValueContainer getGold();
    public ValueContainer getIcon();
}
