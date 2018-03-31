package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import main.content.DC_TYPE;
import eidolons.content.PROPS;
import main.content.enums.DungeonEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import eidolons.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import eidolons.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.Coordinates;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevelMaster;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevelMaster.ENTRANCE_POINT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class Location extends DungeonWrapper {

    private boolean rotated;
    private boolean flippedY;
    private boolean flippedX;
    private Integer nextZ;
    private List<Dungeon> subLevels;
    private boolean sublevel;
    private Dungeon parent;
    private List<Entrance> entrances; //TODO
    private String entranceData;
    private DungeonPlan plan;
    private Entrance mainEntrance;
    private Entrance mainExit;
    private BuildParameters buildParams;

    public Location(LocationMaster master, Dungeon dungeon) {
        super(dungeon, master);
        this.master = master;
        this.sublevel = sublevel;
        if (!sublevel) {
            generateSublevels();
        }
//        initEntrances();
    }

    public Coordinates getPlayerSpawnCoordinates() {
        if (getMainEntrance() != null)
            return getMainEntrance().getCoordinates();
        return super.getPlayerSpawnCoordinates();
    }

    public List<Entrance> getEntrances() {
        if (entrances == null) {
            entrances = new ArrayList<>();
        }
        return entrances;
    }

    public void generateSublevels() {
        entrances = new ArrayList<>();
        if (DungeonLevelMaster.isSublevelTestOn() && !sublevel) {
            setProperty(PROPS.SUBLEVELS, DungeonLevelMaster.TEST_ENTRANCE_DATA, true);
        }
        subLevels = new ArrayList<>();
        for (String sublevel : StringMaster.open(getProperty(PROPS.SUBLEVELS))) {
            Dungeon dungeon = new Dungeon(VariableManager.removeVarPart(sublevel), true);
//            getMaster().getDungeons().add(dungeon);
            addSublevel(sublevel, dungeon);
        }

        // by default?same bf types, smaller size down
        // in most cases, some levels at least should be set...
        // other dungeon types should be eligible as sublevels for
        // multi-dungeons
        // new spawning std - big level with 2R1E, medium level with E and small
        // with B.
    }

    public int getNextZ() {
        if (nextZ == null) {
            nextZ = getZ() + DungeonLevelMaster.getNextZ(true);
        }
        return nextZ;
    }

    public void addSublevel(String sublevel, Dungeon dungeon) {
        subLevels.add(dungeon); // mark if can go
        // deeper
//        dungeon.setEntranceData(StringMaster
//                .cropParenthesises(VariableManager.getVarPart(sublevel)));
//        if (!dungeon.getEntranceData().contains(StringMaster.VAR_SEPARATOR)) {
//            DungeonLevelMaster.generateEntranceData(dungeon);
//        }
        int z = dungeon.getIntParam(G_PARAMS.Z_LEVEL);
        if (z == 0) {
            z = getNextZ();
        }
        dungeon.setZ(z);
        nextZ = null;
    }

    public void initEntrances() {
        if (StringMaster.isEmpty(entranceData)) {
            entranceData = getProperty(PROPS.DUNGEON_MAIN_ENTRANCES, true);
        }
        if (StringMaster.isEmpty(entranceData)) {
            return;
        }
        String enterData = entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR)[0];
        String name = VariableManager.removeVarPart(enterData);
        Coordinates c = new Coordinates(true, VariableManager.getVar(enterData));

        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainEntrance(e);
                }
            }
        }
        if (getMainEntrance() == null) {
            setMainEntrance(new Entrance(c.x, c.y, DataManager.getType(name, DC_TYPE.BF_OBJ),
             getDungeon(), getDungeon()));
        }
        if (entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR).length < 2) {
            return;
        }
        String exitData = entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR)[1];
        name = VariableManager.removeVarPart(exitData);
        c = new Coordinates(true, VariableManager.getVar(exitData));
        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainExit(e);
                }
            }
        }

        if (getMainExit() == null) {
            setMainExit(new Entrance(c.x, c.y, DataManager.getType(name, DC_TYPE.BF_OBJ),
             getDungeon(), getDungeon()));
        }
    }

    public boolean isUnderground() {
        return checkProperty(G_PROPS.DUNGEON_TYPE, DungeonEnums.DUNGEON_TYPE.UNDERGROUND + "");
    }

    public DungeonPlan getPlan() {
        return plan;
    }

    public void setPlan(DungeonPlan plan) {
        this.plan = plan;
    }

    public Entrance getMainEntrance() {
        return mainEntrance;
    }

    public void setMainEntrance(Entrance mainEntrance) {
        this.mainEntrance = mainEntrance;
    }

    public Entrance getMainExit() {
        return mainExit;
    }

    public void setMainExit(Entrance mainExit) {
        this.mainExit = mainExit;
    }

    public boolean isRotated() {
        return rotated;
    }

    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    public boolean isFlippedY() {
        return flippedY;
    }

    public void setFlippedY(boolean flippedY) {
        this.flippedY = flippedY;
    }

    public boolean isFlippedX() {
        return flippedX;
    }

    public void setFlippedX(boolean flippedX) {
        this.flippedX = flippedX;
    }

    public String getEntranceData() {
        return entranceData;
    }

    public void setEntranceData(String entranceData) {
        this.entranceData = entranceData;
    }

    public ENTRANCE_POINT_TEMPLATE getEntranceTemplate() {
        return null;
    }

    public List<Dungeon> getSubLevels() {
        if (subLevels == null) {
            subLevels = new ArrayList<>();
        }
        return subLevels;
    }

    public void setSubLevels(List<Dungeon> subLevels) {
        this.subLevels = subLevels;
    }

    public boolean isSublevel() {
        return sublevel;
    }

    public void setSublevel(boolean sublevel) {
        this.sublevel = sublevel;
    }

    public Dungeon getParent() {
        return parent;
    }

    public void setParent(Dungeon parent) {
        this.parent = parent;
    }

    public BuildParameters getBuildParams() {
        return buildParams;
    }

    public void setBuildParams(BuildParameters buildParams) {
        this.buildParams = buildParams;
    }
}
