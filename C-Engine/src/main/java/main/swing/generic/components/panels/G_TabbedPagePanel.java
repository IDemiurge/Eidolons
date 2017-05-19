package main.swing.generic.components.panels;

import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class G_TabbedPagePanel<E> extends G_PagePanel<E> {

    private G_Panel tabs;

    public G_TabbedPagePanel(int pageSize, boolean vertical, int version) {
        super(pageSize, vertical, version);
    }

    // getdefaultY()
    @Override
    public void refresh() {
        super.refresh();
        // new G_TabbedPanel();
        if (tabs == null) {
            tabs = new G_Panel(vertical ? "flowy" : "");
        } else {
            tabs.removeAll();
        }
        int i = 0;
        for (E sub : getTabData()) {
            tabs.add(createTab(sub, i));
            i++;
        }
        add(tabs);
    }

    @Override
    protected int getArrowOffsetY() {
        return super.getArrowOffsetY() + StringMaster.getInteger(getCompDisplacementY());
    }

    @Override
    protected int getArrowOffsetY2() {
        return super.getArrowOffsetY() + StringMaster.getInteger(getCompDisplacementY());
    }

    @Override
    protected int getArrowOffsetX() {
        return super.getArrowOffsetX() + StringMaster.getInteger(getCompDisplacementX());
    }

    @Override
    protected int getArrowOffsetX2() {
        return super.getArrowOffsetX2() + StringMaster.getInteger(getCompDisplacementX());
    }

    @Override
    protected String getCompDisplacementY() {
        return vertical ? "0" : "" + FontMaster.getFontHeight(TextComp.getDefaultFontAll());
    }

    @Override
    protected String getCompDisplacementX() {
        return !vertical ? "0" : ""
                + FontMaster.getStringWidth(TextComp.getDefaultFontAll(), StringMaster
                .getLongestString(ListMaster.toStringList(getTabData().toArray())));

    }

    private List<E> getTabData() {
        // pageData.get(currentIndex)

        return data;
    }

    protected Component createTab(E sub, final int i) {
        TextComp textComp = new TextComp(sub.toString(), Color.black) {

            @Override
            public int getBorderWidth() {
                return 1;
            }

            @Override
            public Color getBorderColor() {
                return currentIndex == i ? ColorManager.ESSENCE : super.getBorderColor();
            }

            @Override
            public int getPanelHeight() {
                return super.getPanelHeight() * 2 / 3;
            }
        };
        // textComp.getde
        textComp.addMouseListener(new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                tabClicked(i);
            }
        });

        return textComp;
    }

    public void tabClicked(int i) {
        if (currentIndex == i) {
            return;
        }
        currentIndex = i;
        refresh();
    }

}
