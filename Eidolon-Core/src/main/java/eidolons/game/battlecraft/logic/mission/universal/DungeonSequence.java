package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import main.data.filesys.PathFinder;
import main.system.PathUtils;

import java.util.Collection;

public class DungeonSequence extends LevelStruct<Location, Location> {
    Scenario scenario;
    Floor floor;

    public DungeonSequence(Scenario scenario) {
        this.scenario = scenario;
    }

    public DungeonSequence() {
    }

    public String getResourceFolderPath() {
        return PathUtils.buildPath(PathFinder.getScenariosPath(),
                scenario.getName());
    }

    @Override
    public Collection<Location> getChildren() {
        return super.getChildren();
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Scenario getScenario() {
        return scenario;
    }

    @Override
    public LevelStruct getParent() {
        return null;
    }


//    @Override
//    public String getToolTip() {
//        return "Travel to " + getMissionLocation().getName();
//    }
}
