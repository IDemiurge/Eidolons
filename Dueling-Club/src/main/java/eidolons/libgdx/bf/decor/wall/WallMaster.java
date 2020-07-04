package eidolons.libgdx.bf.decor.wall;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.decor.DecorData;
import eidolons.libgdx.bf.decor.pillar.Pillars;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.List;

import static main.content.enums.DungeonEnums.CELL_SET;

/*
ObjTypes:
crumbling
secret (dynamic)
normal (indestructible!)

alt vs main wall type?
well, just good to have a way there...
> Walls with alpha?

Placeholders will be unnecessary :)

And that's it - the image setting logic will be taken over by this class.

 */
public class WallMaster {

    private static final String WALL = "walls";
    private static final String PILLARS = "pillars";
    private static final String CELLS = "cells";
    private static final String ROOT = "ui/cells/set/";

    public static String getCellImage(Coordinates c, int variant) {
        CELL_SET set = getSet(c);
        if (set == null) {
            set = CELL_SET.beige;
        }
        // from pattern, or custom-set via script map!


        return getImage(c, CELLS, set, 1);
    }

    public static String getWallImage(Coordinates c, int variant) {
        CELL_SET set = getSet(c);
        if (set == null) {
            set = CELL_SET.beige;
        }
        return getImage(c, WALL, set, variant);
    }

    public static String getPillarImage(Coordinates c, Pillars.PILLAR variant) {
        CELL_SET set = getSet(c);
        if (set == null) {
            set = CELL_SET.beige;
        }
        return getImage(c, PILLARS, set, variant.toString());
    }

    public static String getImage(Coordinates c, String type, CELL_SET set, Object fileName) {
        int variant = getVariant(c);
        return new StringBuilder().append(ROOT).append(set).append("/").append(type).append("/").
                append(variant).append("/").append(fileName).append(".png").toString();
    }

    private static int getVariant(Coordinates c) {
        return DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c).getCellSetVariant();
    }

    private static CELL_SET getSet(Coordinates c) {
        DecorData data = DC_Game.game.getDungeonMaster().getFloorWrapper().getDecorMap().get(c);
        // data.
        // check custom set - from script map?
        return DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c).getCellSet();
    }


    public static void resetWall(BattleFieldObject wall, List<DIRECTION> list) {
        int v = getType(list);
        String image = getWallImage(wall.getCoordinates(), v);
        if (!wall.getImagePath().equalsIgnoreCase(image)) {
            wall.setImage(image);
        }
        //rotation/flip?
    }


    public static int getType(List<DIRECTION> joints) {
        if (joints.isEmpty()) {
            return 2;
        }
        if (joints.size() >= 4) {
            return 3;
        }
        return 1;
    }


}
