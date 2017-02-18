package main.game.logic.macro.gui.party;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.swing.components.panels.ValueBox;
import main.swing.components.panels.ValueIconPanel;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class Header extends ValueIconPanel {

    private List<VALUE> list;
    private VALUE[][] valueColumns;

    public Header(List<VALUE> list, Entity entity) {
        super(entity);
        this.list = list;
        createBoxes();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    protected ValueBox getValueBox(final VALUE value) {
        return new ValueBox(value) {
            protected String getText() {
                return "" + entity.getIntParam((PARAMETER) value);
            }

            @Override
            protected boolean isCheckType() {
                return false;
            }
        };
    }

    @Override
    public VALUE[][] getValueColumns() {
        if (valueColumns == null) {
            List<List<VALUE>> splitList = new ListMaster<VALUE>().splitList(
                    getColumnCount(), list);
            valueColumns = new VALUE[splitList.size()][];
            int i = 0;
            for (List<VALUE> row : splitList) {
                valueColumns[i] = row.toArray(new VALUE[row.size()]);
                i++;
            }
        }
        return valueColumns;
    }

    protected int getColumnCount() {
        return 2;
    }

}
