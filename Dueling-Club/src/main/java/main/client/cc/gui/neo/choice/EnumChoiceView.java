package main.client.cc.gui.neo.choice;

import main.content.ContentManager;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;
import main.swing.components.panels.page.info.element.ListTextItem;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public class EnumChoiceView<T extends Enum<?>> extends ChoiceView<T> implements
        ListCellRenderer<T> {

    private static final int X_OFFSET = -200;
    private boolean blocked = false;
    private Class<T> CLASS;
    private PROPERTY prop;

    public EnumChoiceView(ChoiceSequence sequence, Unit hero,
                          Class<T> CLASS) {
        super(sequence, hero);
        this.CLASS = CLASS;
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    protected int getPageSize() {
        return CLASS.getEnumConstants().length;
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    protected int getPagePosX() {
        return super.getPagePosX() + X_OFFSET;
    }

    public boolean isOkBlocked() {
        if (blocked) {
            return true;
        }
        return super.isOkBlocked();
    }

    @Override
    protected void initData() {
        data = new LinkedList<>(Arrays.asList(CLASS.getEnumConstants()));
    }

    @Override
    protected void applyChoice() {
        if (getProperty() != null) {
            hero.setProperty(getProperty(), getSelectedItem().toString());
        }
        // save

    }

    protected PROPERTY getProperty() {
        if (prop == null) {
            prop = ContentManager.getPROP(CLASS.getSimpleName());
        }
        return prop;
    }

    @Override
    public String getInfo() {
        return "Choose "
                + StringMaster.getWellFormattedString(CLASS.getSimpleName());
    }

    @Override
    protected PagedSelectionPanel<T> createSelectionComponent() {
        PagedSelectionPanel<T> selectionComp = new PagedSelectionPanel<T>(this,
                getPageSize(), getItemSize(), getColumnsCount()) {

            public int getPanelHeight() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * pageSize / wrap;
            }

            public int getPanelWidth() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * wrap;
            }
        };
        selectionComp.setCustomRenderer(this);
        return selectionComp;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
                                                  T value, int index, boolean isSelected, boolean cellHasFocus) {
        boolean blocked = checkBlocked(value);
        VISUALS V = (isSelected) ? VISUALS.ENUM_CHOICE_COMP_SELECTED
                : VISUALS.ENUM_CHOICE_COMP;
        return new ListTextItem<>(V, value, isSelected, blocked);
    }
}
