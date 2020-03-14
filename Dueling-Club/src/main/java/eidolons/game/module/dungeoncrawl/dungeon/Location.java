package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.macro.map.Place;
import main.content.DC_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.data.filesys.PathFinder;
import main.entity.DataModel;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

public class Location {
    private Dungeon root;
    private Dungeon bossLevel;
    private Place place;
    private Scenario scenario;

    public Location(Place destination) {
        this.setPlace(destination);
    }

    public Location(Scenario scenario) {
        this.scenario = scenario;
    }

    public Dungeon construct() {
        // prefDepth = place.getIntParam(param);
        initRootLevel();
        initBossLevel();
        // List<Dungeon> list = new ArrayList<>();
        // constructSublevels();
        return root;
    }

    private void initBossLevel() {
        if (isScenario()) {
            String data = FileManager.readFile(PathFinder.getDungeonLevelFolder()
             + getPlaceOrScenario().getProperty(MACRO_PROPS.BOSS_LEVEL));
            if (data.isEmpty()) {
                return;
            }
            bossLevel = new LocationBuilder().loadDungeonMap(data).getDungeon();
        } else {
            ObjType type = RandomWizard.getObjTypeByWeight(getPlaceOrScenario().getProperty(
             MACRO_PROPS.BOSS_LEVEL_POOL), DC_TYPE.DUNGEONS);
            bossLevel = new Dungeon(type);
        }

    }

    private void initRootLevel() {
        // TODO entrance fit?
        if (isScenario()) {
            String data = FileManager.readFile(PathFinder.getDungeonLevelFolder()
             + getPlaceOrScenario().getProperty(MACRO_PROPS.ROOT_LEVEL));
            root = new LocationBuilder().loadDungeonMap(data).getDungeon();
        } else {
            ObjType type = RandomWizard.getObjTypeByWeight(getPlaceOrScenario().getProperty(
             MACRO_PROPS.ROOT_POOL), DC_TYPE.DUNGEONS);
            root = new Dungeon(type);
        }
    }

    private boolean isScenario() {
        return place == null && scenario != null;
    }

    public Dungeon getBossLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getPlaceOrScenario() {
        if (scenario != null) {
            return scenario;
        }
        return place;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

}
