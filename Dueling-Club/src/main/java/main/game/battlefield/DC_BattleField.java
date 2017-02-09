package main.game.battlefield;

import main.entity.Entity;
import main.entity.obj.BattlefieldObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_GameState;
import main.game.MicroGameState;
import main.game.battlefield.map.DC_Map;
import main.game.player.Player;
import main.swing.builders.DC_Builder;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.drawing.DrawMasterStatic;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.LogMaster;
import main.system.hotkey.DC_KeyManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Set;

public class DC_BattleField extends SwingBattleField {

    private DC_Builder dc_builder;
    private DC_Map map;
    private DC_KeyManager keyListener;

    public DC_BattleField(Player player1, Player player2, DC_GameState state) {
        super(player1, player2, state);
        builder = new DC_Builder(state);
        keyListener = new DC_KeyManager(state.getGame().getManager());
        keyListener.init();
        builder.setKeyListener(keyListener);
        state.getGame().setGui(builder);
        dc_builder = (DC_Builder) builder;
    }

    public DC_BattleField(Player player1, Player player2, MicroGameState state, boolean sim) {
        super(player1, player2, state);

    }

    public DC_BattleField(DC_Map map, Player player1, Player player2, DC_GameState state) {
        this(player1, player2, state);
        this.map = map;
    }

    @Override
    public void init() {
        if (map != null) {
            getGrid().setMap(map);
        }

        if (builder != null) {
            Chronos.mark("BF BUILDER INIT");
            builder.init();
            Chronos.logTimeElapsedForMark("BF BUILDER INIT");
        }
        if (c == null) {

            Chronos.mark("BF BUILDING");
            c = builder.build();
            Chronos.logTimeElapsedForMark("BF BUILDING");
        }
        c.setIgnoreRepaint(true);
        setGrid(getGrid());
        // getGrid().getGame().getManager().reset(); //too early...
        // game.getGraveyardManager().init();

        Chronos.logTimeElapsedForMark("GAME LAUNCHED");
        LogMaster.log(1, "Battlefield ready!");
        getGrid().getGridComp().getBfMouseListener().startTooltipUpdateThread();

        getGrid().getGridComp().getBfMouseListener().setComponent(c);
        WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
        // [Deprecated]
        // addMouseWheelListener(new BF_MouseWheelListener(dc_builder));

    }

    public DC_Builder getBuilder() {
        return (DC_Builder) builder;
    }

    public void setBuilder(DC_Builder builder) {
        this.builder = builder;
    }

    public boolean canMoveOnto(Entity unit, Coordinates c) {
        if (getGrid().getCell(c) == null) {
            return false;
        }
        return cellHasSpaceForUnit(unit, c);
    }

    public boolean cellHasSpaceForUnit(Entity unit, Coordinates c) {
        return getGrid().getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
    }

    public void createUnit(DC_HeroObj obj) {
        if (obj.getRef().getActive() instanceof DC_ActiveObj) {
            // if (!canMoveOnto(obj, obj.getCoordinates())) {
            // DC_HeroObj collideObj = (DC_HeroObj)
            // ((BF_Builder) builder).getGrid().getObj(
            // obj.getCoordinates());
            // DC_ActiveObj activeObj = (DC_ActiveObj)
            // obj.getRef().getActive();
            // Coordinates newCoordinates =
            // CollisionRule.collision(obj.getRef(), activeObj,
            // (DC_HeroObj) obj, collideObj, true);
            // if (obj.isDead())
            // return;
            // if (newCoordinates != null)
            // obj.setCoordinates(newCoordinates);
            // }
        }

        if (isInitialized()) {
            refreshGrid();
        }

    }

    private void refreshGrid() {
        if (getActiveSelectedObj() != null) {
            getGrid().refresh();
        }
    }

    @Override
    public void createObj(Obj obj) {
        createUnit((DC_HeroObj) obj);
    }

    @Override
    public void selectInfoObj(Obj obj, boolean b) {
        obj.setInfoSelected(true);
        // DrawMaster.getObjImageCache().remove(obj);
        this.setInfoSelectedObj(obj);
        // grid.setCameraCenterCoordinates(obj.getCoordinates());
        if (obj instanceof BattlefieldObj) {
            getBuilder().refresh();

            // getGrid().getGridComp().getPanel().repaint();
            // getBuilder().getUnitInfoPanel().refresh();
            // SwingUtilities.invokeLater(new Runnable() {
            //
            // @Override
            // public void run() {
            // getBuilder().refresh();
            // }
            // });
        }
    }

    @Override
    public void deselectInfoObj(Obj selectedObj, boolean b) {
        DrawMasterStatic.getObjImageCache().remove(selectedObj);
        selectedObj.setInfoSelected(false);
        this.setInfoSelectedObj(null);
    }

    @Override
    public void selectActiveObj(Obj obj, boolean b) {
        obj.setActiveSelected(true);
        this.setActiveSelectedObj(obj);
        getState().getGame().getVisionManager().refresh();

        if (VisionManager.checkVisible((DC_Obj) obj)) {
            centerCameraOn(obj); // TODO [QUICK FIX]
        }
        if (obj.isMine()) {
            refresh();
        }
    }

    // public BattleFieldGrid getGrid() {
    // return getBuilder().getGrid(getState().getGame().getDungeon().getZ());
    // }
    public DC_BattleFieldGrid getGrid() {
        return ((DC_Builder) builder).getGrid();
    }

    public BattleFieldGrid getGrid(Integer z) {
        return ((DC_Builder) builder).getGrid(z);
    }

    @Override
    public DC_GameState getState() {
        return (DC_GameState) super.getState();
    }

    @Override
    public void deselectActiveObj(Obj selectedObj, boolean b) {
        selectedObj.setActiveSelected(false);
        this.setActiveSelectedObj(null);
        //
    }

    @Override
    public void moveBattleFieldObj(Obj obj, int x, int y) {
        obj.setX(x);
        obj.setY(y);
        getGrid().refresh();
    }

    @Deprecated
    @Override
    public void remove(Obj obj) {
        // getGrid().refresh();

    }

    @Override
    public void refreshSpellbook() {
        if (CoreEngine.isSwingOn()) {
            dc_builder.getSpellbookPanel().refresh();
        } else {
//            TODO
        }
    }

    @Override
    public void highlight(Set<Obj> set) {
        getGrid().highlight(set);
        dc_builder.getSpellbookPanel().highlight(set);
        dc_builder.getQuickItemPanel().highlight(set);
        refresh();
    }

    @Override
    public void highlightsOff() {
        getGrid().highlightsOff();
        dc_builder.getSpellbookPanel().highlightsOff();
        dc_builder.getQuickItemPanel().highlightsOff();
        // refresh();
    }

    public void refreshInitiativeQueue() {
        if (dc_builder != null) {
            if (CoreEngine.isSwingOn()) {
                dc_builder.getUnitInfoPanel().getPriorityListPanel().refresh();
            }
        }

    }

    public void centerCameraOn(Obj selected) {
        if (grid == null) {
            return;
        }
        grid.manualOffsetReset();
        grid.setCameraCenterCoordinates(selected.getCoordinates());

    }

    public DC_KeyManager getKeyListener() {
        return keyListener;
    }
}
