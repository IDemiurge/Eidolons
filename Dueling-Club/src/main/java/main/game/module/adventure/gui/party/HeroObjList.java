package main.game.module.adventure.gui.party;

import main.entity.obj.unit.Unit;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.auxiliary.data.ListMaster;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class HeroObjList<T> extends G_PagedListPanel<T> {

    private Collection<T> data;

    public HeroObjList(Collection<T> data, Unit hero) {
        super(6, false, 4);
        this.data = data;
        refresh();
    }

    protected G_Component createPageComponent(List<T> list) {

        return new G_ListPanel<T>(list) {
            public void setInts() {
                layoutOrientation = JList.VERTICAL_WRAP;
                rowsVisible = 2;
                minItems = 6;
            }
        };
    }

    @Override
    protected List<List<T>> getPageData() {
        return new ListMaster<T>().splitList(getPageSize(), data);
    }

}