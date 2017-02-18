package main.gui.sub;

import main.ArcaneTower;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.logic.macro.gui.MacroGuiManager;
import main.gui.builders.EditViewPanel;
import main.gui.components.table.AV_TableCellRenderer;
import main.launch.ArcaneVault;
import main.logic.ArcaneEntity;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class InfoEditPanel extends EditViewPanel {
    private ObjType selectedType;

    // maybe TREE is not necessary for now?

    public InfoEditPanel() {
        super();
        twoTableMode = false;
    }

    @Override
    public void setPanel(G_Panel panel) {
        this.panel = new G_Panel(
                // VISUALS.FRAME_MENU
        );
    }

    @Override
    public boolean isColorsInverted() {
        return false;
    }

    public List<VALUE> filterValues(List<VALUE> values) {
        List<VALUE> filtered = new LinkedList<>();
        for (main.content.VALUE v : values) {
            if (v instanceof G_PROPS) {
                // switch
//				ContentManager.getInstance().checkAllApplies(p, type);
            }
            filtered.add(v);
        }
        return filtered;
    }

    protected void initTable(boolean second) {
        super.initTable(second);
        table.setTableHeader(null);
        table.setIgnoreRepaint(true);
        // scrollPane
        // .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    }

    @Override
    protected AV_TableCellRenderer getTableRenderer() {

        return new AV_TableCellRenderer(this) {
            @Override
            public Component getTableCellComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
                if (column > 0) {
                    value = ContentManager.getFormattedValue(ContentManager.getValue(table
                            .getValueAt(row, 0).toString()), value.toString());
                }
                return super.getTableCellComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
    }

    protected int getHeight() {
        return (int) MacroGuiManager.getMapHeight();
    }

    public boolean isMenuHidden() {
        return true;
    }

    protected int getWidth() {
        return (int) ((GuiManager.getScreenWidth() - MacroGuiManager.getMapWidth()) / 2);
    }

    // SYNC WITH MAP?
    public void selectType(ObjType type) {
        ArcaneVault.setSimulationOn(false);
        super.selectType(type);
        ArcaneVault.setSimulationOn(true);
        setSelectedType(type);

    }

    @Override
    public boolean isLevelEditor() {
        return true;
    }

    protected boolean modified(ObjType type, String valName, String newValue) {
        modify((type), valName, newValue);
        ArcaneEntity entity = ArcaneTower.getEntity(type);
        modify(entity, valName, newValue);
        return false;
    }

    private void modify(Entity entity, String valName, String newValue) {
        // obj vs type?
        entity.setValue(valName, newValue);
        ArcaneTower.getSessionWindow().refresh();
    }

    public void resetData(boolean quietly, Entity type) {

        super.resetData(quietly, type);
    }

    public ObjType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(ObjType selectedType) {
        this.selectedType = selectedType;
    }

}
