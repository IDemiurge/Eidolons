package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

//can be a door, a tunnel, a staircase, a portal... possibly trapped
public class Entrance extends Structure { //ScenarioUnit
    private boolean mainEntrance;
    private boolean mainExit;
    private Coordinates targetCoordinates;
    private Module targetModule;

    public Entrance(int x, int y, ObjType type, DC_Game game) {
        super(type, x, y, DC_Player.NEUTRAL, game,         new Ref());
    }

    @Override
    public PLAYER_VISION getPlayerVisionStatus(boolean active) {
        return PLAYER_VISION.DETECTED;
    }

    @Override
    public PLAYER_VISION getPlayerVisionStatus() {
        return PLAYER_VISION.DETECTED;
    }

    public boolean isOpen() {
        return true;
    }

    public void setMainEntrance(boolean mainEntrance) {
        this.mainEntrance = mainEntrance;
    }

    public boolean isMainEntrance() {
        return mainEntrance;
    }

    public void setMainExit(boolean mainExit) {
        this.mainExit = mainExit;
    }

    public boolean isMainExit() {
        return mainExit;
    }

    public void setTargetCoordinates(Coordinates targetCoordinates) {
        this.targetCoordinates = targetCoordinates;
    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public void setTargetModule(Module targetModule) {
        this.targetModule = targetModule;
    }

    public Module getTargetModule() {
        return targetModule;
    }
}
