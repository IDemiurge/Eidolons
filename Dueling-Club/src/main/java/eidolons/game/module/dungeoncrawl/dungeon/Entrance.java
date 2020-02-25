package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

//can be a door, a tunnel, a staircase, a portal... possibly trapped
public class Entrance extends Structure { //ScenarioUnit
    Dungeon dungeon, sublevel;

    public Entrance(int x, int y, ObjType type, Dungeon dungeon,
                    Dungeon sublevel) {
        super(type, x, y, DC_Player.NEUTRAL, dungeon.getGame(),
         new Ref());
        this.dungeon = dungeon;
        this.sublevel = sublevel;
        // door/trap can be on both sides... enter() will affect Z but not
        // coordinate, so if it's flat there can be a setback with a locked door
        // sublevel.getProperty(PROPS.DOOR_TYPE), OBJ_TYPES.BF_OBJ);
        // int lockLevel;
        // ObjType trapType = DataManager.getType(ENTRANCE_TYPE,
        // OBJ_TYPES.BF_OBJ);
        // int trapLevel;
        // bfObjType.setImage(sublevel.getImagePath()); // default
    }

    @Override
    public PLAYER_VISION getPlayerVisionStatus(boolean active) {
        return PLAYER_VISION.DETECTED;
    }

    @Override
    public PLAYER_VISION getPlayerVisionStatus() {
        return PLAYER_VISION.DETECTED;
    }

//    @Override
//    public VISIBILITY_LEVEL getVisibilityLevel() {
//        return VISIBILITY_LEVEL.CLEAR_SIGHT;
//    }

    @Override
    public DC_Game getGame() {
        return   super.getGame();
    }

    public void enter(Unit unit, Coordinates coordinates) {
        // each unit can really be independent; refresh will update... but
        // admittedly, with gameplay as it is now, it could be a hassle...

        // unit.setCurrentInstance(this);
        // coordinates could be generated by template - middle/side/rim ++
        // side
//        boolean back = (unit.getDungeon() == sublevel);
//        Dungeon targetDungeon = back ? dungeon : sublevel;
//        unit.setCoordinates(getOffsetCoordinates(back));
//        getGame().getDungeonMaster().getInitializer(). initSublevel(targetDungeon);
    }


    private Coordinates getOffsetCoordinates(boolean back) {
        if (back) {
            return getCoordinates(); // TODO so it's the same object for both
        }
        // dungeons?
        int offsetX = dungeon.getCellsX() - sublevel.getCellsX();
        int offsetY = dungeon.getCellsY() - sublevel.getCellsY();
        int x = getCoordinates().x + offsetX;
        int y = getCoordinates().y + offsetY;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        return Coordinates.get(x, y);
    }

    public boolean isOpen() {
        return true;
    }

}
