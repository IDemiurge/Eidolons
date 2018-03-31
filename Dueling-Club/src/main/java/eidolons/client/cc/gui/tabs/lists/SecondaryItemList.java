package eidolons.client.cc.gui.tabs.lists;

import eidolons.client.cc.gui.pages.HC_PagedListPanel;
import eidolons.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import eidolons.entity.obj.unit.Unit;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.client.cc.gui.misc.PoolComp;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;
import main.system.graphics.MigMaster;

import java.util.List;

public abstract class SecondaryItemList extends G_Panel {
    protected HC_PagedListPanel list;
    protected ItemListManager itemListManager;
    protected Unit hero;
    protected PoolComp poolComp;
    protected List<ObjType> data;

    public SecondaryItemList(Unit hero, ItemListManager itemListManager) {
        super("flowy");
        this.hero = hero;
        this.itemListManager = itemListManager;
        init();
        addComps();

        // try {
        // list.getCurrentList().getList().setSelectedIndex(0);
        // } catch (Exception e) {
        // }
    }

    protected void init() {
        data = initData();
        // itemListManager.remove(list);
        if (list == null) {
            list = initList();
            list.setEmptyIcon(getEmptyIcon());
        } else {
            list.setData(data);
            list.initPages();
            list.refresh();
        }
        itemListManager.add(list);
        if (isRemovable()) {
            itemListManager.setPROP2(getPROP());
            itemListManager.addRemoveList(list);
        }

        if (getPoolParam() != null) {
            if (poolComp != null) {
                poolComp.setText(getPoolText());
            } else {
                poolComp = new PoolComp(getPoolText(), getPoolTooltip(),
                 isPoolC());

            }
        }
    }

    protected HC_PagedListPanel initList() {
        list = new HC_PagedListPanel(getTemplate(), hero, itemListManager,
         data, getTitle());
        return list;
    }

    protected String getEmptyIcon() {
        return null;
    }

    protected List<ObjType> initData() {
        data = DataManager
         .toTypeList(hero.getProperty(getPROP()), getTYPE());
        return data;
    }

    protected abstract String getPoolText();

    protected boolean isPoolC() {
        return false;
    }

    protected boolean isRemovable() {
        return true;
    }

    protected abstract PROPERTY getPROP();

    protected abstract OBJ_TYPE getTYPE();

    protected abstract String getTitle();

    protected int getColumnCount() {
        return getTemplate().getTemplate().getColumns();
    }

    protected int getRowCount() {
        return getTemplate().getTemplate().getRows();
    }

    protected PARAMETER getPoolParam() {
        return null;
    }

    protected int getPoolWidth() {
        return 0;
    }

    protected String getPoolTooltip() {
        return null;
    }

    protected void addComps() {
        // add(new JLabel(getTitle()), "id title, pos "
        // + MigMaster.getCenteredTextPosition(getTitle()) + " "
        // + GuiManager.getSmallObjSize() / 4);
        add(list, "id list, pos 0 title.y2+" + GuiManager.getSmallObjSize() / 4);
        if (poolComp != null) {
            add(poolComp, "id pool, pos "
             + MigMaster.getCenteredPosition(list.getVisuals()
             .getWidth(), poolComp.getVisuals().getWidth())
             + " list.y2");
        }
    }

    @Override
    public void refresh() {

        removeAll();
        // if (!hero.isDirty())
        // return;
        init();
        addComps();
        revalidate();
        repaint();
    }

    protected boolean isVertical() {
        return false;
    }

    protected abstract HC_LISTS getTemplate();

    public Unit getHero() {
        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

}
