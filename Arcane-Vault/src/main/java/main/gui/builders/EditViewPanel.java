package main.gui.builders;

import eidolons.content.PARAMS;
import eidolons.content.DC_Formulas;
import main.ability.AE_Manager;
import main.ability.gui.AE_MainPanel;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.TableDataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.gui.components.table.AV_TableCellRenderer;
import main.gui.components.table.AvTable;
import main.gui.components.table.TableMouseListener;
import main.handlers.control.AvSelectionHandler;
import main.handlers.mod.AvSaveHandler;
import main.handlers.mod.AvVersionHandler;
import main.handlers.types.SimulationHandler;
import main.launch.ArcaneVault;
import main.launch.AvConsts;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.G_Table;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.ColorManager;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//size? display mode and other configs? 
public class EditViewPanel implements TableModelListener {

    public static final String NAME = "Name";
    public static final String VALUE = "Value";
    public boolean AE_VIEW_TOGGLE = false;
    public boolean AE_VIEW_TOGGLING = false;
    protected AvTable table;
    protected DefaultTableModel model;
    protected AvTable secondTable;
    protected DefaultTableModel secondModel;
    protected boolean twoTableMode = isTwoTableModeEnabled();
    protected boolean widthSet;
    protected boolean AE_VIEW = false;
    protected boolean secondTableMode;
    protected AE_MainPanel ae_view;
    protected G_Panel panel;
    protected boolean heroView = false;
    protected boolean treeView = false;
    // protected JScrollPane scrollPane;
    Map<String, JTable> tableMap = new HashMap<>();
    Vector<String> names = new Vector<>();
    private boolean dirty = true;
    private boolean reload;

    OBJ_TYPE selectedType;

    // protected HeroTabs heroTabs;

    public EditViewPanel() {
        setPanel(new G_Panel() {
            protected boolean isAutoZOrder() {
                return true;
            }

        });
        names.add(NAME);
        names.add(VALUE);
        initTable(false);
        AE_VIEW = false;
        if (isColorsInverted()) {
            getPanel().setBackground(ColorManager.BACKGROUND);
        }

        menu = new AV_Menu();
    }

    public boolean isColorsInverted() {
        return ArcaneVault.isColorsInverted();
    }

    public boolean isTwoTableModeEnabled() {
        return true;
    }

    public void setTableView() {
        if (table == null) {
            initTable(false);
        }
        if (twoTableMode) {
            if (secondTable == null) {
                initTable(true);
            }
        }

        if (AE_VIEW) {
            setDirty(true);
        }
        AE_VIEW = false;
        refresh();

    }

    public void setAE_View(String abilName) {
        if (table == null) {
            initTable(false);
        }

        ae_view = AE_Manager.getAE_View(abilName);
        if (!AE_VIEW) {
            setDirty(true);
        }
        AE_VIEW = true;

        refresh();
    }

    public void refresh() {
        getPanel().requestFocusInWindow();
        getPanel().removeAll();
        int width;
        int height;
        width = getWidth(); // /2
        height = getHeight();
        if (!AE_VIEW) {
            // width *= 2;
            getPanel().add(table, "id table, pos 0 menu.y2, w  " + width + ", h " + height);
        }
        // add(background); setZOrder()
        if (ArcaneVault.getMainBuilder() != null) {
            getTable().setName(ArcaneVault.getMainBuilder().getSelectedTabName());
        }
        setWidth(getTable());

        if (AE_VIEW) {
            JSplitPane sp;

            if (AE_VIEW_TOGGLE) {
                sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ae_view, table);
            } else {
                sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, table, ae_view);
            }
            sp.setDividerLocation(0.5);
            getPanel().add(sp, "pos 0 menu.y2 " + getWidth() * 5 / 3 + " " + AvConsts.AE_HEIGHT);

            LogMaster.log(1, "AE added!");
        } else {
            if (secondTable != null) {
                secondTable.setName(ArcaneVault.getMainBuilder().getPreviousSelectedTabName());
                getPanel().add((secondTable),
                        "id secondTable, pos table.x2 menu.y2, w  " + width + ", h " + height);
            }
        }
        table.updateButtons(selectedType);
        if (secondTable != null) {
            secondTable.updateButtons(selectedType);
        }

        getPanel().revalidate();
        getPanel().repaint();
        if (isTreeView()) {
            setDirty(false);
        }
    }

    private boolean isButtonsHidden() {
        return false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    protected int getHeight() {
        return AvConsts.TABLE_HEIGHT;
    }

    protected int getWidth() {
        return AvConsts.TABLE_WIDTH;
    }

    protected void setWidth(JTable table) {
        if (widthSet) {
            return;
        }
        widthSet = true;
        int width = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            Component comp = table.prepareRenderer(getTableRenderer(), row,
                    TableDataManager.NAME_COLUMN);
            width = Math.max(comp.getPreferredSize().width, width);
        }

        table.getColumn(EditViewPanel.NAME).setMaxWidth(width * 3 / 2);
        table.getColumn(EditViewPanel.NAME).setPreferredWidth(width);

        table.getColumn(EditViewPanel.NAME).setMinWidth(width);

    }

    protected void initTable(boolean second) {
        secondTableMode = second;
        setModel(new DefaultTableModel(null, names));
        setTable(new AvTable(getModel(), true, second));
        getTable().addMouseListener(new TableMouseListener(getTable(), second));

        getTable().setDefaultRenderer(table.getTable().getColumnClass(table.getTable().getColumn(NAME).getModelIndex()),
                getTableRenderer());

        getTable().setDefaultRenderer(table.getTable().getColumnClass(table.getTable().getColumn(VALUE).getModelIndex()),
                getTableRenderer());

        getTable().setRowHeight(TableDataManager.ROW_HEIGHT);
        getTable().setRowHeight(TableDataManager.IMG_ROW, TableDataManager.ROW_HEIGHT * 2);
        getModel().addTableModelListener(this);

        getTable().getColumnModel().getColumn(0).setWidth(200);
        secondTableMode = false;
        // scrollPane = new JScrollPane(table);
        // if (isColorsInverted()) {
        //     scrollPane.setBackground(ColorManager.BACKGROUND);
        // }
    }

    protected AV_TableCellRenderer getTableRenderer() {
        return new AV_TableCellRenderer(this);
    }

    // aha! here's all that caching and fast gui re-builds. G-ENGINE?
    // as for Data... maybe C-Engine based?
    public void resetData(Entity type) {
        resetData(false, type);
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public enum ValueSet{
        full,
        arena,
    }

    ValueSet valueSet = ValueSet.arena;
    public void resetData(boolean quietly, Entity type) {
        selectedType = type.getOBJ_TYPE_ENUM();

        Vector<Vector<String>> data = TableDataManager.getTypeData(type, valueSet);
        Vector<?> oldData = getModel().getDataVector();
        if (!quietly) {
            if (secondModel != null) {
                secondTableMode = true;
                secondModel.setDataVector((Vector<? extends Vector>) oldData, names);
            }
        }
        secondTableMode = false;
        getModel().setDataVector(data, names);
        refresh();
    }

    public void copySelectedValues() {
        G_Table sourceTable = table.getTable();
        G_Table targetTable = secondTable.getTable();
        int[] rows = table.getTable().getSelectedRows();
        if (rows.length < 2) {
            sourceTable = secondTable.getTable();
            targetTable = table.getTable();
            rows = secondTable.getTable().getSelectedRows();
        }
        for (int row : rows) {
            targetTable.setValueAt(sourceTable.getValueAt(row, TableDataManager.VALUE_COLUMN), row,
                    TableDataManager.VALUE_COLUMN);
        }

    }

    public void tableChanged(TableModelEvent e) {
        table.requestFocusInWindow();
        changeTable(e);

        ArcaneVault.setDirty(true);

    }

    protected synchronized void changeTable(TableModelEvent e) {
        Object source = e.getSource();
        boolean alt = ArcaneVault.isAltPressed();

        // TreePath path = ArcaneVault.getMainBuilder().getTree()
        // .getSelectionPath();
        ObjType type = ArcaneVault.getSelectedType();
        // ((DefaultMutableTreeNode) path
        // .getLastPathComponent()).toString();
        if (secondTable != null) {
            if (source == getSecondTable( ).getTable().getModel()) {
                secondTableMode = true;
                type = ArcaneVault.getPreviousSelectedType();
            }
        }
        if (e.getFirstRow() < 0) {
            return;
        }
        if (e.getColumn() < 0) {
            return;
        }
        String newValue = (String) getTable().getValueAt(e.getFirstRow(), e
                .getColumn());
        for (int row : getTable().getSelectedRows()) {
            modify(row, e.getColumn(), newValue, type);
        }
        if (getTable().getSelectedRows().length > 1) {
            resetData(type);
        }

        if (reload) {
            ArcaneVault.getMainBuilder().getTreeBuilder().reload();
            reload = false;
        }
    }

    public void modify(int row, int col, String newValue, ObjType type) {
        String valName = (String) getTable().getValueAt(row, col - 1);
        if (isLevelEditor())
            return;

        if (!AV2.getModelHandler().modified(type, valName, newValue)) {
            return;
        }

        //for UNDO
        AvSaveHandler.save(type, valName);

        String grpName = (secondTableMode) ? ArcaneVault.getPreviousSelectedType()
                .getOBJ_TYPE() : ArcaneVault.getMainBuilder().getSelectedTabName();

        if (ListMaster.isNotEmpty(ArcaneVault.getSelectedTypes())) {
            if ((ArcaneVault.getSelectedTypes()).size() > 1) {
                for (ObjType t : ArcaneVault.getSelectedTypes()) {
                    t.setValue(valName, newValue);
                    t.setProperty(G_PROPS.VERSION, AvVersionHandler.getVersion(t));
                }
            } else {
                type.setValue(valName, newValue);
                type.setProperty(G_PROPS.VERSION, AvVersionHandler.getVersion(type));

            }
        }
        if (C_OBJ_TYPE.BF_OBJ.equals(type.getOBJ_TYPE_ENUM())
                || SimulationHandler.isUnitType(grpName)) {

            //TODO SIMULATION HANDLER
            // type.setParam(PARAMS.LEVEL,
                    // DC_MathManager.getLevelForPower(type.getIntParam(PARAMS.POWER))
                    // DC_Formulas.getLevelForXp((type.getIntParam(PARAMS.POWER) + 1)
                    //         * DC_Formulas.POWER_XP_FACTOR));
            if (ArcaneVault.isSimulationOn()) {
                SimulationHandler.getUnit(type).setValue(valName, newValue);
                SimulationHandler.refreshType(type);
                resetData(true, type);
            }

        }
        // ?
        secondTableMode = false;
    }


    public G_Panel getPanel() {
        return panel;
    }

    public void setPanel(G_Panel panel) {
        this.panel = panel;
    }

    public boolean isHeroView() {
        return heroView;
    }

    public void setHeroView(boolean heroView) {
        // if (this.menuHidden!=menuHidden) setDirty(true);
        this.heroView = heroView;
    }

    public G_Table getTable() {
        return getTable(secondTableMode);
    }
        public G_Table getTable(boolean secondTableMode) {
        if (secondTableMode) {
            return secondTable.getTable();
        } else {
            return table.getTable();
        }
    }

    public AvTable getSecondTable() {
        return secondTable;
    }

    public void setTable(AvTable table) {
        if (secondTableMode) {
            this.secondTable = table;
        } else {
            this.table = table;
        }
    }

    public DefaultTableModel getModel() {
        if (secondTableMode) {
            return secondModel;
        } else {
            return model;
        }
    }

    public void setModel(DefaultTableModel model) {
        if (secondTableMode) {
            this.secondModel = model;
        } else {
            this.model = model;
        }
    }

    public void toggleAE_VIEW() {
        if (AE_VIEW_TOGGLING) {
            AE_VIEW_TOGGLE = !AE_VIEW_TOGGLE;
        }

    }

    public void selectType(ObjType type) {
        selectType(false, type);

    }

    public void selectType(boolean fromHT, ObjType type) {
        toggleAE_VIEW();
        if (ArcaneVault.getSelectedType() != null) {
            if (type.getOBJ_TYPE_ENUM().isHeroTreeType()) {
                if (type.getOBJ_TYPE_ENUM() != ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM()) {

                    setTreeView(false);
                    setDirty(true);
                }
            }
        }
        ArcaneVault.setSelectedType(type);
        if (fromHT) {
            try {
                AvSelectionHandler.adjustTreeTabSelection(type, false);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else {

        }


        try {
            resetData(type);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (type.getOBJ_TYPE_ENUM().isTreeEditType()) {
            setAE_View(type.getName());
        } else {
            setTableView();
        }

    }


    public boolean isLevelEditor() {
        return false;
    }

    public boolean isTreeView() {
        return treeView;
    }

    public void setTreeView(boolean treeView) {
        if (this.treeView != treeView) {
            setDirty(true);
        }
        this.treeView = treeView;

    }


}
