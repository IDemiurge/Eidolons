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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GridSubParts {
//cached per module

    public ObjectMap<Obj, BaseView> viewMap= new ObjectMap<>(500);
    public Array<GroupWithEmitters> emitterGroups = new Array<>(true, 125);
    public List<OverlayView> overlays = new ArrayList<>();
    public Manipulator[][] manipulators;
    public Set<GridObject>[][] gridObjects ;
    public GroupX[][] customOverlayingObjects;
    public GroupX[][] customOverlayingObjectsTop;
    public GroupX[][] customOverlayingObjectsUnder;

    public GridSubParts(int width  , int height) {
        manipulators=new Manipulator[width][height];
        gridObjects =new LinkedHashSet[width][height];
        customOverlayingObjects =new GroupX[width][height] ;
        customOverlayingObjectsTop=new GroupX[width][height] ;
        customOverlayingObjectsUnder =new GroupX[width][height] ;
    }
}
