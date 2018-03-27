package main.game.module.adventure.gui;

import main.content.DC_TYPE;
import main.content.HC_ValuePages;
import main.content.VALUE;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.generic.components.G_Panel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MacroInfoPanel extends G_Panel {
    DC_PagedInfoPanel pages;
    JLabel label;

    // ++ custom header with ValueIcons of sorts

    public MacroInfoPanel(Entity infoObj) {
        pages = new DC_PagedInfoPanel() {
            protected List<List<VALUE>> getPageData() {
                if (getObj() == null) {
                    return new ArrayList<>();
                }
                if (getObj().getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
                    return HC_ValuePages.getPageLists(getObj()
                            .getOBJ_TYPE_ENUM());
                }
                return null;
//				return MacroValuePages.getPageLists((MACRO_OBJ_TYPES) getObj()
//						.getOBJ_TYPE_ENUM());
            }
        };
        pages.setEntity(infoObj);
        if (infoObj == null) {
            label = new JLabel();
        } else {
            label = new JLabel(infoObj.getIcon());
        }
        addComps();
    }

    @Override
    public void refresh() {
        // removeAll();
        pages.refresh();
        // timeComp.refresh();
        // addComps();
        // revalidate();
    }

    private void addComps() {
        add(label, "id label, pos 0 0");
        add(pages, "pos 0 label.y2");
        // if (timeDisplayed) some other things there?
        // add(timeComp, "pos label.x2 0");
    }

    public void setInfoObj(Entity entity) {
        pages.setObj((Obj) entity);
        pages.setEntity(entity);
        label.setIcon(entity.getIcon());
    }

}
