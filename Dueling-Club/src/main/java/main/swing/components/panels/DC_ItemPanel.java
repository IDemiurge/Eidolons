package main.swing.components.panels;

import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.swing.generic.components.list.CustomList;
import main.swing.generic.components.list.G_List;
import main.swing.generic.components.list.ListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.misc.BORDER_CHECKER;
import main.swing.renderers.SlotItem;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class DC_ItemPanel extends G_ListPanel<DC_HeroAttachedObj> implements BORDER_CHECKER {
    private int width;
    private DC_Game game;
    private boolean info;

    public DC_ItemPanel(DC_Game game) {
        this(game, false);
    }

    public DC_ItemPanel(DC_Game game, boolean info) {
        super(game.getState());
        panelSize = new Dimension(192, 64);
        this.game = game;
        this.info = info;
    }

    @Override
    public void refresh() {
        super.refresh();
        if (!data.isEmpty()) {
            getList().setEmptyIcon(ImageManager.getAltEmptyListIcon());
        } else {
            getList().setEmptyIcon(null);
        }

        getList().setBorderChecker(this);
    }

    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public void setInts() {
        // rowsVisible = 2;
        // minItems = 6;
        rowsVisible = 1;
        minItems = 3;
        wrap = 3;
        int w = minItems / rowsVisible * GuiManager.getSmallObjSize();
        int h = rowsVisible * GuiManager.getSmallObjSize();
        sizeInfo = "w " + w
         // + "/2" + ((width == 0) ? GuiManager.getCellSize() : width)
         + ", h " + h;

        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;

        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

    @Override
    protected G_List<DC_HeroAttachedObj> createList() {
        return new CustomList<DC_HeroAttachedObj>(data) {

            @Override
            protected ListItem<DC_HeroAttachedObj> getListItem(DC_HeroAttachedObj value,
                                                               boolean isSelected, boolean cellHasFocus) {

                return new SlotItem(value, isSelected, cellHasFocus);
            }

        };
    }

    @Override
    protected void resetData() {
        // if (info)
        // this.obj = state.getGame().getManager().getInfoObj();
        // else {
        // this.obj = state.getGame().getManager().getActiveObj();
        //
        // }
        super.resetData();
    }

    @Override
    public Collection<DC_HeroAttachedObj> getData() {
        if (obj instanceof Unit) {
            Unit hero = (Unit) obj;
            ArrayList<DC_HeroAttachedObj> items = new ArrayList<>();
            if (hero.getMainWeapon() == null && hero.getNaturalWeapon(false) != null) {
                items.add(hero.getNaturalWeapon(false));
            } else {
                items.add(hero.getMainWeapon());
            }

            items.add((hero).getArmor());

            if (hero.getOffhandWeapon() == null && hero.getNaturalWeapon(true) != null) {
                items.add(hero.getNaturalWeapon(true));
            } else {
                items.add((hero).getOffhandWeapon());
            }
            // boots =
            // hero.getBoots();

            return items;
        } else {
            return getEmptyData();
        }
    }

    @Override
    public BORDER getBorder(Entity value) {
        if (((DC_Obj) value).checkSelectHighlighted()) {
            return BORDER.HIGHLIGHTED;
        }
        return null;
    }

    // @Override
    // public void setObj(Obj obj) {
    // super.setObj(obj);
    // hero = (DC_HeroObj) obj;
    // }
}
