package eidolons.libgdx.bf.decor.woods;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.decor.CellDecor;
import eidolons.libgdx.bf.decor.DecorData;
import eidolons.libgdx.bf.decor.DecorFactory;
import eidolons.libgdx.bf.decor.wall.WallMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.handlers.GridHandler;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.DungeonEnums;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.List;

    /*
    this is a decorator for WALL objects in a wood-environment

    roots under
    branches over
    wisp-lights in between

    pillars - anything special?
    :: Corners should be uneven at least
    roots should go down - and so, increase the HEIGHT

applying lighting
based on decor?
sync with shards?

we wanna use existing class/container of course
     */

public class Woods extends GridHandler {
    private static final String BRANCHES = "branches";
    private static final String ROOTS = "roots";

    public Woods(GridPanel grid) {
        super(grid);
    }

    @Override
    public void afterLoaded() {
//TODO
        // for (Obj key : grid.getViewMap().keys()) {
        //     if (key instanceof BattleFieldObject) {
        //         BattleFieldObject wall= (BattleFieldObject) key;
        //     if (isTreeWall(wall)){
        //         List<DIRECTION> list =
        //                 DC_Game.game.getBattleFieldManager().getWallMap().get(key.getCoordinates());
        //         initWallDecor(wall, list);
        //     }
        //     }
        // }

    }

    private boolean isTreeWall(BattleFieldObject wall) {
        if (wall.isWall()) {
            return WallMaster.getSet(wall.getCoordinates()) == DungeonEnums.CELL_SET.woods;
        }
        return false;
    }


    public void initWallDecor(BattleFieldObject wall, List<DIRECTION> wallJoints) {
        BaseView wallView = grid.getViewMap().get(wall);
        Coordinates c = wall.getCoordinates();
        CellDecor decor = initBranches(c, wallJoints);
        addWisps(c, wallView);
        // decor.setLinkedView(wallView);
    }

    public void initRoots(Coordinates c, List<DIRECTION> wallJoints) {
        //just random rotation if adjacent to non-wall
    }

    public void addWisps(Coordinates c, BaseView wallView) {
        //sprite and vfx?
        GraphicData data = new GraphicData("");
        data.setValue(GraphicData.GRAPHIC_VALUE.sprite, Sprites.FLOAT_WISP);
        data.setValue(GraphicData.GRAPHIC_VALUE.vfx, GenericEnums.VFX.WISPS);

        CellDecor decor = DecorFactory.createDecor(c, data);
        // wallView.addLinkedDecor(decor);
        grid.addDecor(c, decor, DecorData.DECOR_LEVEL.OVER_MAPS);

    }

    /*
    add to the side farthest from exposed

     */
    public CellDecor initBranches(Coordinates c, List<DIRECTION> wallJoints) {
        //cases - just rotation?
        int size = wallJoints.size();
        if (size >= 3) {
            //special case
            return null;
        }
        Vector2 offset = new Vector2(0, 0);
        float rotation = 0;
        int version = 0;

        DIRECTION alignDirection = null;
        if (size == 2) {
            DIRECTION d1 = wallJoints.get(0);
            DIRECTION d2 = wallJoints.get(1);

            if (d1.isDiagonal() && d2.isDiagonal()) {
                //diag line
                rotation = d1.getDegrees() + d2.getDegrees();
                alignDirection = d1; //TODO
            } else if (d1.isVertical() != d2.isVertical()) {
                //corner
            } else {
                //line; need perpendicular

            }
            version = RandomWizard.getRandomInt(2);

        } else {
            //this should be easy, it's an end-line, so whichever direction it goes, we add to the root of it
            DIRECTION d = wallJoints.get(0);
            alignDirection=d;
            d = d.flip();
            //default rotation is RIGHT
            rotation = d.getDegrees();

        }
        GraphicData data = new GraphicData("");
        //coordinates or maybe anchor?
        data.setValue(GraphicData.GRAPHIC_VALUE.origin,  alignDirection);
        data.setValue(GraphicData.GRAPHIC_VALUE.rotation, rotation + "");
        data.setValue(GraphicData.GRAPHIC_VALUE.texture,
                PathFinder.getTexturesPath() + BRANCHES
                        + version);

        CellDecor decor = DecorFactory.createDecor(c, data);
        grid.addDecor(c, decor, DecorData.DECOR_LEVEL.TOP);
        return decor;
    }

}
























