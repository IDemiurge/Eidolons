package main.swing.builders;

import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.DC_UnitAction;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_GameState;
import main.game.battlefield.BattleFieldGrid;
import main.game.battlefield.map.DC_Map;
import main.game.battlefield.map.DungeonMapGenerator;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.minimap.Minimap;
import main.game.turn.TurnTimer;
import main.swing.SwingMaster;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.panels.*;
import main.swing.components.panels.DC_UnitActionPanel.ACTION_DISPLAY_GROUP;
import main.swing.components.panels.page.DC_PagedQuickItemPanel;
import main.swing.components.panels.page.DC_PagedSpellPanel;
import main.swing.components.panels.page.DC_PagedUnitActionPanel;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.components.panels.secondary.ActionModePanel;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.services.dialog.DialogPanel;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.LogMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_Builder extends Builder {

    public static final int OFFSET_X = 18;
    /*
     * builder array? bf grid player panels control panel ...
     */
    private static int cellSize;
    private DC_BattleFieldGrid grid;
    private Map<Dungeon, DC_BattleFieldGrid> levelGrids;

    private DC_TopPanel topPanel;
    private DC_UAP_Holder uap;
    private Container parent;
    private int smallSize;
    // private DC_TimerPanel tp;
    private TurnTimer timer;
    private DC_GameState state;
    private DC_ActiveUnitPanel aup;
    private boolean refreshing;
    private DC_BattleFieldGrid lastGrid;
    private Minimap minimap;
    private boolean minimapDisplayed;
    private Map<Dungeon, Minimap> minimaps;
    private G_Panel ipHolder; //holder for cip and uip
    private DC_CellInfoPanel cip;
    private DC_UnitInfoPanel uip;
    private boolean dungeonsPanelDisplayed;
    private DungeonsPanel dungeonsPanel;
    private ActionModePanel actionModePanel;
    private DialogPanel dialog;

    public DC_Builder(DC_GameState state) {
        this.state = state;
        setCellSize(GuiManager.getSquareCellSize());
        smallSize = GuiManager.getSmallObjSize();

        comp = createMainComponent();

        comp.setBackground(ColorManager.getTranslucent(ColorManager.OBSIDIAN, 10));
        levelGrids = new HashMap<Dungeon, DC_BattleFieldGrid>();
        newDungeon((state.getGame().getDungeonMaster().getDungeon()));
    }

    public static int getBfGridPosY() {
        // return VISUALS.PORTRAIT_BORDER.getHeight();
        return GuiManager.getSquareCellSize();
    }

    public static int getBfGridPosX() {
        return VISUALS.PORTRAIT_BORDER.getWidth() + OFFSET_X;
        // return getSpellbookPanel().getPanelWidth();
    }

    public static int getBfGridPosY2() {
        return getBfGridPosY() + GuiManager.getBattleFieldHeight();
    }

    public static int getBfGridPosX2() {
        return getBfGridPosX() + GuiManager.getBattleFieldWidth();
    }

    public static int getCellSize() {
        return cellSize;
    }

    private static void setCellSize(int cellSize) {
        DC_Builder.cellSize = cellSize;
    }

    private G_Panel createMainComponent() {
        return new G_Panel(true) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintListOverlays(g);
                getGrid().getGame().getToolTipMaster().drawToolTips(this);

                if (dialog != null)
                    if (dialog.isVisible())
                        dialog.paint(g);

                // getGrid().getGame().getGuiMaster().drawDynamicButtons();
            }
        };
    }

    public DialogPanel getDialog() {
        return dialog;
    }

    public void setDialog(DialogPanel d) {
        dialog = d;
    }

    public void newDungeon(Dungeon subLevel) {
        if (levelGrids.containsKey(subLevel))
            return;
        lastGrid = grid;
        grid = new DC_BattleFieldGrid(subLevel);
        if (subLevel.isSublevel()) {
            DC_Map map = new DungeonMapGenerator().generateMap(subLevel);
            if (map == null)
                return;
            grid.setMap(map);
            // map.setBackground(getBackground());
            // map.setName(getMapName());
            // grid.build();
        }
        levelGrids.put(subLevel, grid);
    }

    @Override
    public void init() {
        ipHolder = new G_Panel() {
            @Override
            public void refresh() {
                removeAll();
                if (state.getManager().getInfoObj() instanceof DC_Cell) {
                    add(getCellInfoPanel(), "pos " + getInfoPanelOffsetX() + " 0");
                } else {
                    add(getUnitInfoPanel(), "pos 0 0");
                }
                revalidate();
                if (getUnitInfoPanel().getParent() == this)
                    getUnitInfoPanel().refresh();
                else if (getCellInfoPanel().getParent() == this)
                    getCellInfoPanel().refresh();
                // TODO repaint(Rectangle r)
                repaint();
                // TODO repaint(x, y, width, height);
            }
        };
        setUip(new DC_UnitInfoPanel(ipHolder, state.getGame()));
        cip = new DC_CellInfoPanel(ipHolder, state.getGame());
        timer = state.getTimer();
        topPanel = new DC_TopPanel(state.getGame());
        uap = new DC_UAP_Holder(state);
        aup = new DC_ActiveUnitPanel(state.getGame());
        builderArray = new Builder[]{

        };

        compArray = new JComponent[]{grid.getComp() ,
                aup, ipHolder, uap, topPanel};

        infoArray = new String[]{

        };

        cInfoArray = new String[]{
                getGridPos(),
                "id aup, pos " + 0 + " 0",
                "id uip, pos bf.x2" + "-"
                        + (GuiManager.getSquareCellSize() + getInfoPanelOffsetX()) + " 0",
                "id uap, pos bf.x bf.y2 ",
                // "id tB, pos bf.x2 uip.y2",
                "id topPanel, pos "
                        // + "@center_x+("+ (VISUALS.TOP.getWidth() / 2 - 2.5 *
                        // GuiManager.getCellWidth())+ ") 0"
                        + "aup.x2-" + getInfoPanelOffsetX() + " 0"};

        initMap();
        this.initialized = true;
    }

    private int getInfoPanelOffsetX() {
        return 69;
    }

    public DC_CellInfoPanel getCellInfoPanel() {
        return cip;
    }

    private String getMinimapPos() {
        return "id bf, pos " + getBfGridPosX() + "-24 " + GuiManager.getSquareCellSize() + ""
                + ", w " + cellSize + "*" + GuiManager.getBF_CompDisplayedCellsX() + "!, h "
                + GuiManager.getCellHeight() + "*" + GuiManager.getBF_CompDisplayedCellsY() + "!";
    }

    private String getGridPos() {
        return "id bf, pos " + getBfGridPosX()
                + " "
                + getBfGridPosY() // +
                // getUnitNameLabelHeight()
                + "" + ", w " + cellSize + "*" + GuiManager.getBF_CompDisplayedCellsX() + "!, h "
                + GuiManager.getCellHeight() + "*" + GuiManager.getBF_CompDisplayedCellsY() + "!";
    }

    private int getUnitNameLabelHeight() {
        return 15;
    }

    public void refreshPanels() {
        // grid.setDirty(false);
        refresh();
        // grid.setDirty(true);
    }

    public void toggleDungeonsPanel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        if (refreshing)
            return;
        refreshing = true;
        try {
            Dungeon dungeon = state.getGame().getManager().getActiveObj().getDungeon();
            state.getGame().getDungeonMaster().initSublevel(dungeon);

            for (DC_BattleFieldGrid levelGrid : levelGrids.values())
                if (levelGrid != null)
                    getComp().remove(levelGrid.getComp());
            for (Minimap minimap : getMinimaps().values())
                if (minimap != null)
                    getComp().remove(minimap.getComp());
            if (minimapDisplayed) {
                minimap = getMinimaps().get(dungeon);
                if (minimap == null) {
                    minimap = new Minimap(dungeon);
                    minimap.init();
                    getMinimaps().put(dungeon, minimap);
                } else
                    minimap.getGrid().refresh();
                getComp().add(minimap.getComp(), getMinimapPos());
            } else {
                if (lastGrid != grid)
                    state.getGame().getManager().resetWallMap();
                lastGrid = grid;
                grid = levelGrids.get(dungeon);
                state.getGame().getGUI().resetBgIcon(grid.getMap().getBackground());
                getComp().add(grid.getComp(), getGridPos());
                // if (grid.isDirty())
                grid.reset();
            }

            super.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            refreshing = false;
        }

        if (dungeonsPanelDisplayed) {
            getDungeonsPanel().refresh();
            getComp().add(getDungeonsPanel().getPanel(), "");
            // getComp().setComponentZOrder(aup, index)
        } else {
            getComp().remove(getDungeonsPanel().getPanel());
        }

    }

    @Override
    public JComponent build() {
        parent = this.comp.getParent();
        LogMaster.log(1, "Battlefield being built...");
        JComponent component = super.build();
        // new BufferStrateg
        // BufferStrategy ???
        component.setIgnoreRepaint(true);
        return component;
    }

    public BattleFieldGrid getGrid(Integer zLevel) {
        if (zLevel == null)
            return grid;
        for (Dungeon d : levelGrids.keySet()) {
            if (d.getZ() == zLevel)
                return levelGrids.get(d);
        }
        return null;
    }

    public DC_BattleFieldGrid getGrid() {
        return grid;
    }

    public DC_PagedSpellPanel getSpellbookPanel() {
        return aup.getSbp();
    }

    public DC_PagedQuickItemPanel getQuickItemPanel() {
        return aup.getQip();
    }

    public DC_TopPanel getGp() {
        return topPanel;
    }

    public DC_UAP_Holder getUap() {
        return uap;
    }

    public Container getParent() {
        return parent;
    }

    public int getSmallSize() {
        return smallSize;
    }

    private DungeonsPanel getDungeonsPanel() {
        if (dungeonsPanel == null)
            setDungeonsPanel(new DungeonsPanel(state.getGame()));
        return dungeonsPanel;
    }

    private void setDungeonsPanel(DungeonsPanel dungeonsPanel) {
        this.dungeonsPanel = dungeonsPanel;
    }

    public void toggleDisplayActionModePanel(DC_ActiveObj activeObj) {
        toggleDisplayActionModePanel(activeObj, false);
    }

    public void toggleDisplayActionModePanel(DC_ActiveObj activeObj, boolean closeOnly) {
        if (actionModePanel == null)
            actionModePanel = new ActionModePanel();
        comp.repaint();

        if (actionModePanel.getParent() == comp) {
            comp.remove(actionModePanel);
            if (actionModePanel.getAction() == activeObj) {
                SoundMaster.playStandardSound(STD_SOUNDS.CLOSE);
                return;
            }
        }
        if (closeOnly)
            return;
        SoundMaster.playStandardSound(STD_SOUNDS.DIS__OPEN_MENU);
        int column = -1;
        DC_PagedUnitActionPanel actionPanel = null;
        for (ACTION_DISPLAY_GROUP group : getUap().getUapMap().keySet()) {
            actionPanel = getUap().getUapMap().get(group);
            DC_UnitActionPanel panel = (DC_UnitActionPanel) actionPanel.getCurrentComponent();
            List<DC_UnitAction> data = panel.getData();
            if (data != null)
                if (data.contains(activeObj)) {
                    column = data.indexOf(activeObj);
                    break;
                }
        }
        if (column == -1)
            return;
        actionModePanel.setAction(activeObj);
        int x = uap.getX()
                + actionPanel.getX()
                + (column)
                * 64
                - actionModePanel.getWidth()
                / 2
                + 64
                - ImageManager.getArrowImage(false, true, DC_PagedUnitActionPanel.ARROW_VERSION)
                .getWidth(null);
        int y = uap.getY() + actionPanel.getY() - actionModePanel.getHeight();

        comp.add(actionModePanel, "pos " + x + " " + y);
        SwingMaster.autoResetZOrder(comp);
        comp.revalidate();
        comp.repaint();
    }

    private void paintListOverlays(Graphics g) {
        // [OPTIMIZE] - why not set x/y for non-unit objects to their screen
        // coordinates?
        if (state.getManager().getInfoObj() instanceof DC_ActiveObj) {
            DC_ActiveObj activeObj = (DC_ActiveObj) state.getManager().getInfoObj();
            Image image = BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImage();
            drawActionListOverlay(activeObj, g, image);
        }
        if (state.getManager().getActivatingAction() instanceof DC_ActiveObj) {
            DC_ActiveObj activeObj = (DC_ActiveObj) state.getManager().getActivatingAction();
            Image image = BORDER.NEO_CYAN_HIGHLIGHT_SQUARE_64.getImage();
            drawActionListOverlay(activeObj, g, image); // offset here
        }

    }

    private void drawActionListOverlay(DC_ActiveObj activeObj, Graphics g, Image image) {
        Point p = getScreenPointForAction(activeObj);
        if (p == null)
            return;
        int offsetX = image.getWidth(null) / 2 - (image.getWidth(null) - 64);
        int offsetY = -(image.getHeight(null) - 64) / 2;
        g.drawImage(image, p.x + offsetX, p.y + offsetY, null);
    }

    private Point getScreenPointForQuickItem(DC_ItemActiveObj activeObj) {
        int x = getQuickItemPanel().getX();
        int y = getQuickItemPanel().getY();

        int wrap = getQuickItemPanel().getWrap();
        int i = getQuickItemPanel().getData().indexOf(activeObj.getItem());
        y += GuiManager.getSmallObjSize() * (i / wrap);
        x += i % wrap * GuiManager.getSmallObjSize();
        return new Point(x, y);
    }

    private Point getScreenPointForSpell(DC_SpellObj spell) {
        int x = getSpellbookPanel().getX();
        int y = getSpellbookPanel().getY();
        int wrap = getSpellbookPanel().getWrap();
        int i = getSpellbookPanel().getData().indexOf(spell);
        y += GuiManager.getSmallObjSize() * (i / wrap);
        x += i % wrap * GuiManager.getSmallObjSize();
        return new Point(x, y);
    }

    public Point getScreenPointForAction(DC_Obj activeObj) {
        if (activeObj instanceof DC_SpellObj) {
            return getScreenPointForSpell((DC_SpellObj) activeObj);
        }
        if (activeObj instanceof DC_ItemActiveObj) {
            return getScreenPointForQuickItem((DC_ItemActiveObj) activeObj);
        }
        DC_PagedUnitActionPanel unitActionPanel = getUap().getPanelForAction(
                (DC_ActiveObj) activeObj);
        if (unitActionPanel == null)
            return null;
        DC_UnitActionPanel panel = (DC_UnitActionPanel) unitActionPanel.getCurrentComponent();
        int column = panel.getData().indexOf(activeObj);
        int x = uap.getX()
                + unitActionPanel.getX()
                + (column)
                * 64
                - ImageManager.getArrowImage(false, true, DC_PagedUnitActionPanel.ARROW_VERSION)
                .getWidth(null);
        int y = uap.getY() + unitActionPanel.getY();
        Point p = new Point(1, 1);
        return p;
    }

    public TurnTimer getTimer() {
        return timer;
    }

    public DC_PagedInfoPanel getInfoPanel() {
        return getUnitInfoPanel().getInfo();
    }

    public DC_UnitInfoPanel getUnitInfoPanel() {
        return uip;
    }

    private void setUip(DC_UnitInfoPanel uip) {
        this.uip = uip;
    }

    public void toggleMinimap() {
        minimapDisplayed = !minimapDisplayed;
    }

    private Map<Dungeon, Minimap> getMinimaps() {
        if (minimaps == null)
            minimaps = new HashMap<>();
        return minimaps;
    }

    public DC_TopPanel getTopPanel() {
        return topPanel;
    }

    public DC_ActiveUnitPanel getActiveUnitPanel() {
        return aup;
    }

}
