package main.gui.builders;

import main.ability.AE_Manager;
import main.ability.gui.AE_MainPanel;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tree.HT_Node;
import main.client.cc.gui.neo.tree.view.HT_View;
import main.client.cc.gui.neo.tree.view.TreeControlPanel;
import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.TableDataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.gui.components.controls.ModelManager;
import main.gui.components.menu.AV_Menu;
import main.gui.components.table.AV_TableCellRenderer;
import main.gui.components.table.TableMouseListener;
import main.launch.ArcaneVault;
import main.simulation.SimulationManager;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.G_Table;
import main.system.DC_Formulas;
import main.system.graphics.ColorManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

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
    protected G_Table table;
    protected DefaultTableModel model;
    protected G_Table secondTable;
    protected DefaultTableModel secondModel;
    protected boolean twoTableMode = isTwoTableModeEnabled();
    protected boolean widthSet;
    protected boolean AE_VIEW = false;
    protected boolean secondTableMode;
    protected AE_MainPanel ae_view;
    protected G_Panel panel;
    protected boolean heroView = false;
    protected boolean treeView = false;
    protected HT_View treeViewComp;
    protected HT_View classTreeViewComp;
    protected HT_View skillTreeViewComp;
    protected boolean menuHidden;
    protected AV_Menu menu;
    protected JScrollPane scrollPane;
    Map<String, JTable> tableMap = new HashMap<>();
    Vector<String> names = new Vector<>();
    private TreeControlPanel treeControlPanel;
    private boolean dirty = true;

    // protected HeroTabs heroTabs;

    public EditViewPanel() {
        setPanel(new G_Panel() {
            protected boolean isAutoZOrder() {
                return true;
            }

            ;
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
//        if (!isDirty()) {
//            getPanel().repaint();
//            return;
//        }
        getPanel().removeAll();
        if (!isMenuHidden()) {
            getPanel().add(menu.getBar(), "id menu, pos 0 0");
        }
        if (isHeroView()) {
            // getPanel().add(heroTabs, "pos 0 0");
            // setHeroView(false);
            // getPanel().revalidate();
            // getPanel().repaint();
            // getPanel().repaint();
            // return;
        }
        int width;
        int height;
        width = getWidth(); // /2
        height = getHeight();
        // offset instead! visual.x2 visual.xy2
        // DISPLAY_MODES.MULTI_TABLE

        // scrollPane.removeAll();
        // scrollPane.add(getTable());
        // scrollPane.revalidate();
        if (!AE_VIEW) {
            // width *= 2;
            getPanel().add(scrollPane, "id table, pos 0 menu.y2, w  " + width + ", h " + height);
        }
        // add(background); setZOrder()
        if (ArcaneVault.getMainBuilder() != null) {
            getTable().setName(ArcaneVault.getMainBuilder().getSelectedTabName());
        }
        setWidth(getTable());

        if (AE_VIEW) {
            JSplitPane sp;

            if (AE_VIEW_TOGGLE) {
                sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ae_view, scrollPane);
            } else {
                sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, ae_view);
            }
            sp.setDividerLocation(0.5);
            getPanel().add(sp, "pos 0 menu.y2 " + getWidth() * 5 / 3 + " " + ArcaneVault.AE_HEIGHT);

            LogMaster.log(1, "AE added!");
        } else {
            if (isTreeView()) {

                treeViewComp = ArcaneVault.getSelectedOBJ_TYPE() == DC_TYPE.SKILLS ? skillTreeViewComp
                        : classTreeViewComp;
                if (treeViewComp == null) {
                    treeViewComp = skillTreeViewComp;
                }
                if (treeViewComp != null) {
                    treeViewComp.refresh();
                    getPanel().add(treeViewComp, "id secondTable, pos table.x2 menu.y2");

                    if (treeControlPanel == null) {
                        treeControlPanel = new TreeControlPanel() {
                            @Override
                            protected void workspaceCycle(boolean alt) {
                                if (alt) {
                                    ModelManager.addToWorkspace(true);
                                    return;
                                }
                                super.workspaceCycle(alt);
                            }
                        };
                    }
                    treeViewComp.setBottomPanel(treeControlPanel);
                    treeControlPanel.setView(treeViewComp);
                    // getPanel().add(
                    // treeControlPanel,
                    // "id controls, pos table.x2+70 menu.y2+"
                    // + (HT_MapBuilder.defTreeHeight + 100));
                    // getPanel().setComponentZOrder(treeControlPanel, 0);
                }
            } else if (secondTable != null) {
                secondTable.setName(ArcaneVault.getMainBuilder().getPreviousSelectedTabName());
                getPanel().add(new JScrollPane(secondTable),
                        "id secondTable, pos table.x2 menu.y2, w  " + width + ", h " + height);
            }
        }
        getPanel().revalidate();
        getPanel().repaint();
        if (isTreeView()) {
            setDirty(false);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    protected int getHeight() {
        return ArcaneVault.TABLE_HEIGHT;
    }

    protected int getWidth() {
        return ArcaneVault.TABLE_WIDTH;
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
        setTable(new G_Table(getModel(), true));
        getTable().addMouseListener(new TableMouseListener(getTable(), second));

        getTable().setDefaultRenderer(table.getColumnClass(table.getColumn(NAME).getModelIndex()),
                getTableRenderer());

        getTable().setDefaultRenderer(table.getColumnClass(table.getColumn(VALUE).getModelIndex()),
                getTableRenderer());

        getTable().setRowHeight(TableDataManager.ROW_HEIGHT);
        getTable().setRowHeight(TableDataManager.IMG_ROW, TableDataManager.ROW_HEIGHT * 2);
        getModel().addTableModelListener(this);

        secondTableMode = false;

        scrollPane = new JScrollPane(table);
        if (isColorsInverted()) {
            scrollPane.setBackground(ColorManager.BACKGROUND);
        }
    }

    protected AV_TableCellRenderer getTableRenderer() {
        return new AV_TableCellRenderer(this);
    }

    // aha! here's all that caching and fast gui re-builds. G-ENGINE?
    // as for Data... maybe C-Engine based?
    public void resetData(Entity type) {
        resetData(false, type);
    }

    public void resetData(boolean quietly, Entity type) {

        Vector<Vector<String>> data = TableDataManager.getTypeData(type);
        Vector<?> oldData = getModel().getDataVector();
        if (!quietly) {
            if (secondModel != null) {
                secondModel.setDataVector(oldData, names);
            }
        }
        getModel().setDataVector(data, names);
        refresh();
    }

    public void copySelectedValues() {
        G_Table sourceTable = table;
        G_Table targetTable = secondTable;
        int[] rows = table.getSelectedRows();
        if (rows.length < 2) {
            sourceTable = secondTable;
            targetTable = table;
            rows = secondTable.getSelectedRows();
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
        try {
            Object source = e.getSource();
            boolean alt = ArcaneVault.isAltPressed();

            // TreePath path = ArcaneVault.getMainBuilder().getTree()
            // .getSelectionPath();
            ObjType type = ArcaneVault.getSelectedType();
            // ((DefaultMutableTreeNode) path
            // .getLastPathComponent()).toString();
            if (secondTable != null) {
                if (source == secondTable.getModel()) {
                    secondTableMode = true;
                    type = ArcaneVault.getPreviousSelectedType();
                }
            }

            String typeName = type.getName();

            String newValue = (String) ((JTable) getTable()).getValueAt(e.getFirstRow(), e
                    .getColumn());
            String valName = (String) ((JTable) getTable()).getValueAt(e.getFirstRow(), e
                    .getColumn() - 1);
            if (!modified(type, valName, newValue)) {
                return;
            }
            if (isLevelEditor())
            // grpName =
            // LevelEditor.getSimulation().getSelectedEntity().getOBJ_TYPE();
            {
                return;
            }
            try {
                ModelManager.save(type, valName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String grpName = (secondTableMode) ? ArcaneVault.getPreviousSelectedType()
                    .getOBJ_TYPE() : ArcaneVault.getMainBuilder().getSelectedTabName();
            LogMaster.log(0, valName + " = " + newValue + " for " + grpName
                    + "." + typeName);

            if (valName.equals(G_PROPS.BASE_TYPE.getName())) {
                ArcaneVault.getMainBuilder().getTreeBuilder().reload();
            } else if (valName.equals(G_PROPS.NAME.getName())) {
                String oldName = type.getName();
                DataManager.renameType(type, newValue);
                if (type.getOBJ_TYPE_ENUM().isTreeEditType()) {
                    AE_Manager.typeRename(newValue, oldName);
                }
                if (!ArcaneVault.getMainBuilder().getTreeBuilder().nameChanged(oldName, newValue)) {
                    for (TabBuilder t : ArcaneVault.getAdditionalTrees()) {
                        if (t.getTreeBuilder().nameChanged(oldName, newValue)) {
                            break;
                        }
                    }
                }
                // ArcaneVault.getMainBuilder().getTree().setSelectionPath(path);
            } else if (valName.equalsIgnoreCase(G_PROPS.WORKSPACE_GROUP.getName())) {
                if (ArcaneVault.getWorkspaceManager().isDefaultTypeWorkspacesOn()) {
                    if (ArcaneVault.getWorkspaceManager().getActiveWorkspace() != null) {
                        ArcaneVault.getWorkspaceManager().getActiveWorkspace().setDirty(true);
                    }
                }
            }

            if (ListMaster.isNotEmpty(ArcaneVault.getSelectedTypes())) {
                if ((ArcaneVault.getSelectedTypes()).size() > 1) {
                    for (ObjType t : ArcaneVault.getSelectedTypes()) {
                        if (alt) {
                            if (ContentManager.isParameter(valName)) {
                                int amount = Integer.valueOf(newValue);
                                t.modifyParameter(ContentManager.getPARAM(valName), amount);

                                t.setProperty(G_PROPS.VERSION, CoreEngine.VERSION);
                                t.setProperty(G_PROPS.LAST_EDITOR, System.getProperty("user.name"));
                            } else {
                                t.addProperty(ContentManager.getPROP(valName), newValue);

                                t.setProperty(G_PROPS.VERSION, CoreEngine.VERSION);
                            }
                        } else {
                            t.setValue(valName, newValue);
                            t.setProperty(G_PROPS.VERSION, CoreEngine.VERSION);
                        }
                    }
                } else {
                    type.setValue(valName, newValue);
                    type.setProperty(G_PROPS.VERSION, CoreEngine.VERSION);

                }
            }
            if (C_OBJ_TYPE.BF_OBJ.equals(type.getOBJ_TYPE_ENUM())
                    || SimulationManager.isUnitType(grpName)) {
                type.setParam(PARAMS.LEVEL,
                        // DC_MathManager.getLevelForPower(type.getIntParam(PARAMS.POWER))
                        DC_Formulas.getLevelForXp((type.getIntParam(PARAMS.POWER) + 1)
                                * DC_Formulas.POWER_XP_FACTOR));
                if (ArcaneVault.isSimulationOn()) {
                    SimulationManager.getUnit(type).setValue(valName, newValue);
                    SimulationManager.refreshType(type);

                    // SimulationManager.getUnit(type).resetDefaultAttrs(); in
                    // save() instead!
                    resetData(true, type);
                }
            } else if (type.getOBJ_TYPE_ENUM() == DC_TYPE.SPELLS) {
                if (type.getIntParam(PARAMS.XP_COST) == 0) {
                    if (type.getGroup().equals("Standard"))
                    // type.setParam(PARAMS.XP_COST,
                    // DC_MathManager.getSpellXpCost(type));

                    {
                        secondTableMode = false;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {

        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            secondTableMode = false;
        }
    }

    protected boolean modified(ObjType type, String valName, String newValue) {
        return true;
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
        if (secondTableMode) {
            return secondTable;
        } else {
            return table;
        }
    }

    public void setTable(G_Table table) {
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
                    if (getSkillTreeViewComp() == null || getClassTreeViewComp() == null) {
                        setTreeView(false);
                    } else {
                        setDirty(true);
                    }
                }
            }
        }

        ArcaneVault.setSelectedType(type);
        if (fromHT) {
            try {
                ModelManager.adjustTreeTabSelection(type, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (isTreeView()) {
                if (getTreeViewComp() != null) {
                    HT_Node node = null;
                    try {
                        node = getTreeViewComp().getTree().getMap().getNodeForType(type);
                    } catch (Exception e) {
                        e.printStackTrace();
                        setTreeView(false);
                        selectType(fromHT, type);
                    }
                    if (node != null) {
                        HC_Master.setSelectedTreeNode(node);
                        getTreeViewComp().getTree().getPanel().repaint();
                    }
                }
            }
        }
        if (isTreeView()) {

            HT_View viewComp = type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS ? getSkillTreeViewComp()
                    : getClassTreeViewComp();
            if (viewComp != null) {
                String key = type.getSubGroupingKey();
                if (!key.equalsIgnoreCase(viewComp.getDisplayedTabPanel().getSelectedTabName())) {
                    if (!key.equalsIgnoreCase("Multiclass")) {
                        try {
                            viewComp.tabSelected(key);
                            viewComp.repaint();
                            setDirty(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        try {
            resetData(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type.getOBJ_TYPE_ENUM().isTreeEditType()) {
            setAE_View(type.getName());
        } else {
            setTableView();
        }

    }

    public boolean isMenuHidden() {
        return menuHidden;
    }

    public void setMenuHidden(boolean menuHidden) {
        if (this.menuHidden != menuHidden) {
            setDirty(true);
        }
        this.menuHidden = menuHidden;
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

    public HT_View getTreeViewComp() {
        return treeViewComp;
    }

    public void setTreeViewComp(HT_View treeViewComp) {
        this.treeViewComp = treeViewComp;
        setDirty(true);
    }

    public HT_View getClassTreeViewComp() {
        return classTreeViewComp;
    }

    public void setClassTreeViewComp(HT_View classTreeViewComp) {
        this.classTreeViewComp = classTreeViewComp;
        setDirty(true);
    }

    public HT_View getSkillTreeViewComp() {
        return skillTreeViewComp;
    }

    public void setSkillTreeViewComp(HT_View skillTreeViewComp) {
        this.skillTreeViewComp = skillTreeViewComp;
        setDirty(true);
    }

}
