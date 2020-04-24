package eidolons.libgdx.bf.grid;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.gui.generic.GroupX;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GridSubParts {

    public Map<BattleFieldObject, BaseView> viewMap= new LinkedHashMap<>();
    public List<Manipulator> manipulators = new ArrayList<>();
    public List<GridObject> gridObjects = new ArrayList<>();
    public List<GroupX> customOverlayingObjects = new ArrayList<>();
    public List<GroupX> customOverlayingObjectsTop = new ArrayList<>();
    public List<GroupX> customOverlayingObjectsUnder = new ArrayList<>(100);
    public List<GroupWithEmitters> emitterGroups = new ArrayList<>(125);
    public List<OverlayView> overlays = new ArrayList<>();

}
