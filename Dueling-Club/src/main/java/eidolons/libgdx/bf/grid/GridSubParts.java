package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.gui.generic.GroupX;
import main.entity.obj.Obj;

import java.util.ArrayList;
import java.util.List;

public class GridSubParts {

    public ObjectMap<Obj, BaseView> viewMap= new ObjectMap<>(500);
    public List<Manipulator> manipulators = new ArrayList<>();
    public List<GridObject> gridObjects = new ArrayList<>();
    public List<GroupX> customOverlayingObjects = new ArrayList<>();
    public List<GroupX> customOverlayingObjectsTop = new ArrayList<>();
    public List<GroupX> customOverlayingObjectsUnder = new ArrayList<>(100);
    public Array<GroupWithEmitters> emitterGroups = new Array<>(true, 125);
    public List<OverlayView> overlays = new ArrayList<>();

}
