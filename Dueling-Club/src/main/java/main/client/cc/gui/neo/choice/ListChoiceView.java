package main.client.cc.gui.neo.choice;

import main.swing.components.panels.page.info.element.TextCompDC;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import javax.swing.*;
import java.awt.*;

public class ListChoiceView extends ChoiceView<String> implements ListCellRenderer<String> {

    private static final VISUALS V = VISUALS.VALUE_BOX_BIG;
    private String info;

    public ListChoiceView(ChoiceSequence cs, String info, String data) {
        super(StringMaster.openContainer(data), null, cs, null);
        this.info = info;
    }

    @Override
    protected PagedSelectionPanel<String> createSelectionComponent() {
        PagedSelectionPanel<String> panel = new PagedSelectionPanel<String>(this, getPageSize(),
         getItemSize(), getColumnsCount()) {

            public int getPanelHeight() {
                return V.getHeight() * pageSize / wrap;
            }

            public int getPanelWidth() {
                return V.getHeight() * wrap;
            }
        };
        panel.setCustomRenderer(this);
        return panel;
    }

    @Override
    protected int getPageSize() {
        return super.getPageSize();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void applyChoice() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        int fontSize = 16; // size
        int style = Font.PLAIN;
        if (isSelected) {
            fontSize++;
            style = Font.BOLD;
        }
        // box vs hl_box ++ size
        // flexibility/independence?
        TextCompDC comp = new TextCompDC(V, value);
        comp.setFont(FontMaster.getFont(FONT.NYALA, fontSize, style));
        return comp;
    }

}
