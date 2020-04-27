package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.data.filesys.PathFinder;
import main.system.PathUtils;

import java.util.Collection;

public class DungeonSequence extends LevelStruct<Location, Location> {
    Scenario scenario;
    Dungeon floor;

    public DungeonSequence(Scenario scenario) {
        this.scenario = scenario;
    }

    public String getResourceFolderPath() {
        return PathUtils.buildPath(PathFinder.getScenariosPath(),
                scenario.getName());
    }

    @Override
    public Collection<Location> getChildren() {
        return super.getChildren();
    }

    public Dungeon getFloor() {
        return floor;
    }

    public void setFloor(Dungeon floor) {
        this.floor = floor;
    }

    public Scenario getScenario() {
        return scenario;
    }

    @Override
    protected LevelStruct getParent() {
        return null;
    }


//    @Override
//    public String getToolTip() {
//        return "Travel to " + getMissionLocation().getName();
//    }
}
