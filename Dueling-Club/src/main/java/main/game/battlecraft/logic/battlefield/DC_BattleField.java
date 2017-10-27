package main.game.battlecraft.logic.battlefield;

import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.SwingBattleField;
import main.game.core.state.DC_GameState;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.drawing.DrawMasterStatic;

import java.util.Set;
import java.util.stream.Collectors;

// just a utility wrapped for Grid

public class DC_BattleField extends SwingBattleField {

    private Set<Coordinates> coordinatesSet;

    public DC_BattleField(DC_BattleFieldGrid grid) {
        super(grid.getGame().getState());
        setGrid(grid);
    }


    @Override
    public void selectInfoObj(Obj obj, boolean b) {
        obj.setInfoSelected(true);
        this.setInfoSelectedObj(obj);
        if (obj instanceof BfObj) {
            getBuilder().refresh();
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
//        getState().getGame().getVisionMaster().refresh();

//        if (VisionManager.checkVisible((DC_Obj) obj)) {
//            centerCameraOn(obj); // TODO [QUICK FIX]
//        }
        if (obj.isMine()) {
            refresh();
        }
    }

    @Override
    public void deselectActiveObj(Obj selectedObj, boolean b) {
        selectedObj.setActiveSelected(false);
        this.setActiveSelectedObj(null);
        //
    }

    // public BattleFieldGrid getGrid() {
    // return getBuilder().getGrid(getState().getGame().getDungeon().getZ());
    // }
    public DC_BattleFieldGrid getGrid() {
        return (DC_BattleFieldGrid) grid;
    }


    @Override
    public DC_GameState getState() {
        return (DC_GameState) super.getState();
    }


    public void centerCameraOn(Obj selected) {
        if (grid == null) {
            return;
        }
        grid.manualOffsetReset();
        grid.setCameraCenterCoordinates(selected.getCoordinates());

    }

    public Set<Coordinates> getCoordinatesSet() {
        if (coordinatesSet == null) {
            coordinatesSet =
            getGrid().getGame().getCells().stream().map(c -> c.getCoordinates()).collect(Collectors.toSet());
        }
        return coordinatesSet;
    }

    public void setCoordinatesSet(Set<Coordinates> coordinatesSet) {
        this.coordinatesSet = coordinatesSet;
    }
}
