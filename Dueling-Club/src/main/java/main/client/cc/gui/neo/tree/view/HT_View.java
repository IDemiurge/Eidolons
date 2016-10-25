package main.client.cc.gui.neo.tree.view;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.client.cc.gui.neo.tree.HC_Tree;
import main.client.cc.gui.neo.tree.HC_Tree.TREE_VIEW_MODE;
import main.client.cc.gui.neo.tree.HT_Node;
import main.client.cc.gui.neo.tree.logic.HT_MapBuilder;
import main.client.cc.gui.neo.tree.logic.TreeMap;
import main.client.cc.gui.neo.tree.logic.TreeMap.LINK_VARIANT;
import main.client.cc.gui.views.HeroView;
import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Writer;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.TextEditor;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HT_View extends HeroView implements TabChangeListener, MouseListener {
    private static boolean workspaceLaunch;
    protected G_Component panel;
    protected G_Component bottomPanel;
    protected HT_ControlPanel controlPanel;
    protected TextCompDC infoText;
    protected HC_Tree tree;
    protected HC_TabPanel tabs;
    protected Map<Object, HC_Tab> tabMap = new HashMap<>();
    protected HC_TabPanel altTabs;
    protected boolean altMode;
    protected List<HC_Tab> tabList;
    /*
    so the triple-tree view will use 3 of these?
     */
    Object arg;
    Map<Object, HC_Tree> treeCache = new HashMap<>();
    private boolean viewMode;
    private TREE_VIEW_MODE mode;
    private boolean workspaceMode;
    private HC_TabPanel workspaceTabs;

    public HT_View(Object arg, DC_HeroObj hero) {
        super(hero);
        this.arg = arg;
        init();
    }

    public static boolean isWorkspaceLaunch() {
        return workspaceLaunch;
    }

    public static void setWorkspaceLaunch(boolean workspaceLaunch) {
        HT_View.workspaceLaunch = workspaceLaunch;
    }

    public HC_TabPanel getTabbedPanel() {
        return tabs;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    /*
     *
     * background image for the tree...
     * refresh will update overlays - borders, info-icons, links, sized icons
     *
     */
    public void init() {
        if (workspaceLaunch) {
            setWorkspaceMode(true);
        }
        tabList = initTabList();
        tabs = initTabPanel(tabList);
        // bottomPanel = createBottomPanel();
        // infoText = new TextComp(null);
        add(tabs, "pos 0 0");
        tabs.refresh();
        controlPanel = new HT_ControlPanel(this);

    }

    protected abstract int getTabCompWidth();

    protected abstract int getTabCompHeight();

    protected abstract int getTabPageSize();

    protected HC_TabPanel initTabPanel(List<HC_Tab> tabList) {
        HC_TabPanel tabPanel = new HC_TabPanel(tabList) {
            @Override
            public void notifyListener() {
                listener.tabSelected(getSelectedTabName());
            }

            @Override
            public Component generateEmptyTabComp() {
                return new GraphicComponent(ImageManager.getNewBufferedImage(getTabCompWidth(),
                        getTabCompHeight()));
            }

            @Override
            protected STD_SOUNDS getClickSound() {
                return STD_SOUNDS.ON_OFF;
                // return STD_SOUNDS.DIS__OPEN_MENU;
            }

            @Override
            public Component getSelectedTabComponent() {
                return super.getSelectedTabComponent();
            }

            @Override
            public ComponentVisuals getTAB() { // empty
                if (TAB == null) {
                    TAB = new CompVisuals(ImageManager.getNewBufferedImage(getTabCompWidth(),
                            getTabCompHeight()));
                }
                return TAB;
            }

            public int getPageSize() {
                return getTabPageSize();
            }
        };
        tabPanel.setChangeListener(this);
        return tabPanel;
    }

    protected abstract List<HC_Tab> initTabList();

    @Override
    public void tabSelected(String name) {
        getDisplayedTabPanel().selected(name);
        arg = getArg(name);
        panel = getDisplayedTabPanel().getCurrentComp();
        if (!CoreEngine.isArcaneVault())
            if (panel.getComponentCount() > 1) {
                panel.refreshComponents();
                tree.refresh();
                return;
            }
        resetTree(name);

    }

    public void rebuildAndSetTree() {
        resetTree(arg.toString(), true);
    }

    public void resetTree(String name) {
        resetTree(name, false);
    }

    public void resetTree(String name, boolean forceRebuild) {
        panel.removeAll();
        panel.setAutoZOrder(true);
        if (forceRebuild)
            treeCache.remove(getArg(name));
        tree = getTree(name);

        Component bottom = null;
        int bottomY = 45;
        int bottomX = 40;
        if (!CoreEngine.isArcaneVault())
            try {
                bottom = initBottomPanel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            if (bottomPanel == null) {
                bottom = new TreeControlPanel(this);
                bottomY += 15;
            } else
                bottom = bottomPanel;
        }
        // int i = panel.getComponentCount();

        if (bottom != null) {
            panel.add(bottom, "pos " + bottomX + " tree.y2-" + bottomY);
            // panel.setComponentZOrder(bottom, i);
            // i++;
        }
        tree.refresh();
        panel.add(tree.getPanel(), "id tree, pos 40 50");

        panel.add(new TextCompDC() {
            @Override
            protected Font getDefaultFont() {
                return FontMaster.getFont(FONT.AVQ, 18, Font.PLAIN);
            }

            protected String getText() {
                return arg.toString(); // .replace(" Mastery", "")
            }
        }, "id text, pos 45 10");
        // panel.setComponentZOrder(controlPanel, 1);
        // panel.setComponentZOrder(tree.getPanel(), i);
        // i++;
        // panel.add(controlPanel, "id cp, pos 70 90");

        panel.refreshComponents();
        panel.revalidate();
        // refreshButtomPanel();
    }

    public abstract Object getArg(String name);

    public abstract Component initBottomPanel();

    protected void handleSpecialClick(MouseEvent e) {
        ObjType type = null;
        for (Rectangle rect : tree.getRankBoostMouseMap().keySet()) {
            if (rect.contains(e.getPoint())) {
                type = tree.getRankBoostMouseMap().get(rect).getType();
                break;
            }
        }
        if (type == null)
            return;
        if (tryIncrementRank(type)) {
            SoundMaster.playStandardSound(STD_SOUNDS.ButtonUp);
            refreshComponents();
        } else {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            tree.setDisplayRequirements(true);
            tree.setReqTextType(type);
            tree.refresh();
            tree.getPanel().repaint();
        }
    }

    @Override
    public void refreshComponents() {
        if (tree != null)
            tree.refresh();
        if (bottomPanel != null)
            bottomPanel.refresh();
    }

    protected boolean tryIncrementRank(ObjType type) {
        return CharacterCreator.getHeroManager().tryIncrementRank(hero, type);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        tree = treeCache.get(arg);
        getTree().setDisplayRequirements(false);
        if (e.getSource() instanceof CustomButton) {
            CustomButton customButton = (CustomButton) e.getSource();
            if (e.isAltDown())
                customButton.handleAltClick();
            else
                customButton.handleClick();
            return;
        }
        if (e.getClickCount() > 1) {
            // if (CoreEngine.isArcaneVault()) {
            //
            // } else
            // undoAvOperation()
            // else
            if (SwingUtilities.isRightMouseButton(e)) {
                // toggleViewMode();
                cycleViewMode();
                return;
            }

        }

        Point point = e.getPoint();
        HT_Node node = null;
        for (Rectangle rect : tree.getMap().getMouseMap().keySet()) {
            if (rect.contains(point)) {
                node = tree.getMap().getMouseMap().get(rect);
                break;
            }
        }
        if (node == null) {
            handleSpecialClick(e);
            return;
        }
        ObjType type = node.getValue();

        if (CoreEngine.isArcaneVault()) {
            // ARCANE VAULT ARCANE VAULT ARCANE VAULT
            if (e.isControlDown()) {
                Boolean vertical_horizontal_manual = null;
                // if (e.isShiftDown())
                // vertical_horizontal_manual =
                // SwingUtilities.isRightMouseButton(e);
                adjustLink(vertical_horizontal_manual, node);
                return;
            } else {
                if (e.isAltDown())
                    editDefaultProp(type);
                else if (e.isShiftDown()) {
                    // Boolean x_y = null;
                    // x_y = SwingUtilities.isRightMouseButton(e);
                    // adjustOffset(x_y, node);

                    if (TreeControlPanel.setProp(e.isAltDown(), type))
                        rebuildAndSetTree();
                    return;
                }
            }

            if (e.getClickCount() > 1) {
                if (!SwingUtilities.isRightMouseButton(e))
                    editImage(type);
                // if (e.isAltDown()) {
                // editDefaultProp(type);
                // } else {
                // editImage(type);
                // return;
                // }

                ID_OPERATION operation = null;
                if (SwingUtilities.isRightMouseButton(e)) {
                    operation = ID_OPERATION.SWAP;
                } else {
                    // TODO
                }
                if (operation != null) {
                    adjustIds(operation, DataManager.getSublings(type, tree.getTypes()));
                    return;
                }
            }
            // NON-AV
        } else if (e.isAltDown() || e.getClickCount() > 1) {
            boolean result = CharacterCreator.getHeroManager().addItem(hero, type, getTYPE(),
                    getPROP());
            if (result) {
                added(type);

            } else {
                SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                getTree().setDisplayRequirements(true);
            }

        }

        select(node, type);
        // node.refresh();

    }

    private void editDefaultProp(ObjType type) {
        String value = new TextEditor().launch(type.getProperty(getDefaultEditProp()));
        if (value != null) {
            type.setProperty(getDefaultEditProp(), value);
            rebuildAndSetTree();
        }
    }

    private void showHelpInfo() {
        DialogMaster.inform("setProp = shift;" + "" + "" + "");
    }

    private G_PROPS getDefaultEditProp() {
        return G_PROPS.DESCRIPTION;
    }

    private void editImage(ObjType type) {
        String value = new ImageChooser().launch(type.getImagePath(), "");
        if (value != null) {
            type.setProperty(G_PROPS.IMAGE, value);
            rebuildAndSetTree();
        }
    }

    private void undoAvOperation() {
        // TODO Auto-generated method stub

    }

    protected void select(HT_Node node, ObjType type) {
        for (HT_Node n : tree.getMap().getNodeMap().values()) {
            n.setSelected(false);
        }
        node.setSelected(true);
        if (!CoreEngine.isArcaneVault())
            CharacterCreator.typeSelected(type);
        else {
            WaitMaster.receiveInput(
                    type.getOBJ_TYPE_ENUM() == OBJ_TYPES.SKILLS ? WAIT_OPERATIONS.SELECTION
                            : WAIT_OPERATIONS.CUSTOM_SELECT, type.getName(), false);
        }
        SoundMaster.playStandardSound(STD_SOUNDS.SLOT);
        // if (node.getParentType() != null)
        // SoundMaster.playStandardSound(STD_SOUNDS.MOVE);

        HC_Master.setSelectedTreeNode(node);
        tree.refresh();
        tree.getPanel().repaint();

    }

    protected void added(ObjType type) {
        PARAMETER mastery = ContentManager.getPARAM(type.getProperty(G_PROPS.MASTERY));
        String aspect = type.getProperty(G_PROPS.SKILL_GROUP);
        String rank = "";
        // type.getIntParam(params.circle)>4
        SoundMaster.playSkillAddSound(type, mastery, aspect, rank);
        tree.refresh();
        tree.getPanel().repaint();
    }

    protected PROPS getPROP() {
        return PROPS.SKILLS;
    }

    protected OBJ_TYPES getTYPE() {
        return OBJ_TYPES.SKILLS;
    }

    protected HC_Tree getTree(String name) {
        tree = treeCache.get(arg);
        if (tree == null) {
            // super lazy! :) but maybe I could run this in background at some
            // point?
            TreeMap map = buildTree();
            tree = new HC_Tree(hero, map, arg);
            tree.setViewMode(viewMode);
            tree.setMode(mode);
            // new HC_TreeBuilder().build(map);
            treeCache.put(arg, tree);
            tree.getPanel().addMouseListener(this);
        }
        return tree;
    }

    public void adjustOffset(Boolean x_y, HT_Node node) {
        adjustOffset(false, x_y, node);
    }

    public void adjustOffset(Boolean node_link, Boolean x_y) {
        adjustOffset(node_link, x_y, HC_Master.getSelectedTreeNode());
    }

    public void adjustOffset(Boolean node_link, Boolean x_y, HT_Node node) {
        ObjType type = node.getType();
        PARAMS param = node_link ? x_y ? PARAMS.TREE_NODE_OFFSET_X : PARAMS.TREE_NODE_OFFSET_Y
                : x_y ? PARAMS.TREE_LINK_OFFSET_X : PARAMS.TREE_LINK_OFFSET_Y;
        Integer offset = type.getIntParam(param);
        offset = DialogMaster.inputInt(offset);
        if (offset == null)
            return;
        type.setParameter(param, offset);
        rebuildAndSetTree();
        XML_Writer.writeXML_ForType(type, isSkill() ? OBJ_TYPES.SKILLS : OBJ_TYPES.CLASSES);
    }

    public void adjustLink(Boolean vertical_horizontal_manual, HT_Node node) {
        ObjType type = node.getType();
        LINK_VARIANT variant = null;
        if (tree.getMap().getLinkForChildType(type) != null)
            variant = tree.getMap().getLinkForChildType(type).getVariant();

        if (variant == null || vertical_horizontal_manual == null)
            variant = new EnumMaster<LINK_VARIANT>().retrieveEnumConst(LINK_VARIANT.class,
                    new ListChooser(SELECTION_MODE.SINGLE, LINK_VARIANT.class).choose());
        else
            variant = HT_MapBuilder.getShiftedLinkVariant(variant, vertical_horizontal_manual);
        if (variant == null) {
            main.system.auxiliary.LogMaster.log(1, type + "'s LINK_VARIANT null !!! ");
            return;
        }
        type.setProperty(PROPS.LINK_VARIANT, variant.toString());
        if (!CoreEngine.isArcaneVault())
            XML_Writer.writeXML_ForType(type, isSkill() ? OBJ_TYPES.SKILLS : OBJ_TYPES.CLASSES);
        main.system.auxiliary.LogMaster.log(1, type + "'s LINK_VARIANT set for " + variant);

        rebuildAndSetTree();
    }

    public void editAltBaseLink(ObjType type) {
        // replace prop part
        String property = type.getProperty(PROPS.ALT_BASE_LINKS);
        // String baseType = selectAltBase(type);
        int index = DialogMaster.optionChoice(StringMaster.openContainer(property).toArray(),
                "select");
        if (index == -1)
            return;
        String fullSubString = StringMaster.openContainer(property).get(index);
        String baseType = VariableManager.removeVarPart(fullSubString);
        String newString = selectAltLink(baseType, fullSubString);
        // separate offsets! retain old ...
        if (newString == null)
            return;

        String value = property.replace(fullSubString, newString);

        type.setProperty(PROPS.ALT_BASE_LINKS, value);
    }

    public void addAltBase(ObjType type) {
        String name = selectAltBase(type);
        if (name == null)
            return;
        String value = selectAltLink(name);
        type.addProperty(PROPS.ALT_BASE_LINKS, value);

        rebuildAndSetTree();
    }

    private String selectAltBase(ObjType type) {
        List<ObjType> types = new LinkedList<>(tree.getTypes());
        for (ObjType t : tree.getTypes()) {
            if (t.getIntParam(PARAMS.CIRCLE) >= type.getIntParam(PARAMS.CIRCLE))
                types.remove(t);
        }
        types.remove(type);

        String name = new ListChooser(SELECTION_MODE.SINGLE,
                DataManager.toStringList(types), isSkill() ? OBJ_TYPES.SKILLS
                : OBJ_TYPES.CLASSES).choose();
        return name;
    }

    private String selectAltLink(String name) {
        return selectAltLink(name, "");
    }

    private String selectAltLink(String name, String prevData) {
        LINK_VARIANT variant = null;
        variant = new EnumMaster<LINK_VARIANT>().retrieveEnumConst(LINK_VARIANT.class,
                new ListChooser(SELECTION_MODE.SINGLE, LINK_VARIANT.class).choose());
        if (variant == null)
            if (prevData != null) {

            } else
                return null;
        String offsetX = "" + DialogMaster.inputInt("offset x", 0);
        String offsetY = "" + DialogMaster.inputInt("offset y", 0);

        String offsets = offsetX + "," + offsetY;

        String suffix = (variant + "");

        suffix += "=" + offsets;

        String value = name + StringMaster.wrapInParenthesis(suffix);
        return value;
    }

    public void adjustIds(ID_OPERATION operation, List<ObjType> children) {
        int size = children.size();
        ObjType selectedType = HC_Master.getSelectedTreeNode().getType();
        HT_MapBuilder.sortDefault(children);
        switch (operation) {
            case SET_0:

                break;
            case RESET:
                break;
            case SHUFFLE:
                for (ObjType type : children) {
                }
                break;
            case SWAP:
                int swapIndex1 = 0;
                int swapIndex2 = 0;
                if (size == 2) {
                    swapIndex1 = 0;
                    swapIndex2 = 1;
                } else {
                    if (size == 3) {
                        // check selected
                        swapIndex1 = (children.get(0) == selectedType) ? 1 : 0;
                        swapIndex2 = (children.get(2) == selectedType) ? 1 : 2;
                    } else {
                        swapIndex1 = children.indexOf(selectedType);
                        swapIndex2 = 0;
                    }
                }

                ObjType objType = children.get(swapIndex1);
                ObjType objType2 = children.get(swapIndex2);
                Integer id1 = StringMaster.getInteger(objType.getProperty(G_PROPS.ID));
                Integer id2 = StringMaster.getInteger(objType2.getProperty(G_PROPS.ID));

                if (id1 % 2 == 0 || id2 % 2 == 0) {
                    if (children.indexOf(objType) > children.indexOf(objType2)) {
                        id1 = size == 3 ? 5 : 3;
                        id2 = 1;
                    } else {
                        id1 = 1;
                        id2 = size == 3 ? 5 : 3;
                    }
                    // 1 3 5 for 3 ???
                }
                if (size == 3) {
                    // to middle
                    children.get(children.indexOf(selectedType)).setProperty(G_PROPS.ID, 3 + "");
                }
                objType.setProperty(G_PROPS.ID, id2 + "");
                objType2.setProperty(G_PROPS.ID, id1 + "");

                main.system.auxiliary.LogMaster.log(1, id2 + " id set for " + objType);
                main.system.auxiliary.LogMaster.log(1, id1 + " id set for " + objType2);

                if (!CoreEngine.isArcaneVault()) {
                    XML_Writer.writeXML_ForType(objType, isSkill() ? OBJ_TYPES.SKILLS
                            : OBJ_TYPES.CLASSES);
                    XML_Writer.writeXML_ForType(objType2, isSkill() ? OBJ_TYPES.SKILLS
                            : OBJ_TYPES.CLASSES);
                }
                break;
            default:
                break;

        }

        rebuildAndSetTree();

    }

    protected TreeMap buildTree() {
        return new HT_MapBuilder(isSkill(), arg).build();
    }

    protected boolean isAutoZOrder() {
        return true;
    }

    protected VISUALS getPanelVisuals() {
        return VISUALS.TREE_VIEW;
    }

    public void altToggled() {
        altMode = !altMode;
        refresh();
        int i = getDisplayedTabPanel().getIndex();
        if (i == -1) {
            i = 0;
            getDisplayedTabPanel().select(i);
        }
    }

    @Override
    public void refresh() {
        removeAll();
        add(getDisplayedTabPanel());

        getDisplayedTabPanel().refresh(); // ?
        getDisplayedTabPanel().getTabPanel().getCurrentComponent().refreshComponents();
        refreshComponents();
        // if (tree != null)
        // tree.refresh();
        // if (bottomPanel != null)
        // bottomPanel.refresh();
        revalidate();
    }

    public HC_TabPanel getDisplayedTabPanel() {

        HC_TabPanel tabsPanel = tabs;

        if (isWorkspaceMode()) {
            if (workspaceTabs == null)
                workspaceTabs = initTabPanel(initTabList());
            tabsPanel = workspaceTabs;
        } else if (isAltMode()) {
            if (altTabs == null)
                altTabs = initTabPanel(initTabList());
            tabsPanel = altTabs;
        }
        return tabsPanel;
    }

    public HC_TabPanel getAltTabs() {
        return altTabs;
    }

    public void setAltTabs(HC_TabPanel altTabs) {
        this.altTabs = altTabs;
    }

    public HC_TabPanel getWorkspaceTabs() {
        return workspaceTabs;
    }

    public void setWorkspaceTabs(HC_TabPanel workspaceTabs) {
        this.workspaceTabs = workspaceTabs;
    }

    public boolean isWorkspaceMode() {
        return workspaceMode;
    }

    public void setWorkspaceMode(boolean workspaceMode) {
        this.workspaceMode = workspaceMode;
    }

    public boolean isAltMode() {
        return altMode;
    }

    public G_Component getBottomPanel() {
        return bottomPanel;
    }

    public void setBottomPanel(G_Component bottomPanel) {
        this.bottomPanel = bottomPanel;
    }

    public boolean isSkill() {
        return false;
    }

    @Override
    public void activate() {

    }

    @Override
    public PARAMS getPoolParam() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void tabSelected(int index) {
        tabSelected(tabList.get(index).getName());

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public List<HC_Tab> getTabList() {
        return tabList;
    }

    public void setTabList(List<HC_Tab> tabList) {
        this.tabList = tabList;
    }

    public void cycleViewMode() {
        int index = EnumMaster.getEnumConstIndex(TREE_VIEW_MODE.class, mode);
        index++;
        if (index >= TREE_VIEW_MODE.values().length)
            index = 0;
        mode = TREE_VIEW_MODE.values()[index];
        tree.setMode(mode);
        SoundMaster.playStandardSound(STD_SOUNDS.SLING);
        tree.refresh();
        tree.getPanel().repaint();
    }

    public void toggleViewMode() {
        viewMode = !viewMode;
        tree.toggleViewMode();
    }

    public HC_Tree getTree() {
        return tree;
    }

    public Map<Object, HC_Tree> getTreeCache() {
        return treeCache;
    }

    public Map<Object, HC_Tab> getTabMap() {
        return tabMap;
    }

    public Object getArg() {
        return arg;
    }

    public G_Component getPanel() {
        return panel;
    }

    public HT_ControlPanel getControlPanel() {
        return controlPanel;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public abstract Object[] getWorkspace();

    public void setWorkspace(Object[] ARRAY) {

    }

    public abstract List getWorkspaces();

    public enum ID_OPERATION {
        SWAP, RESET, SHUFFLE, SET_0, SET_1, SET_2, SET_3, SET_4, SET_5

    }

}
