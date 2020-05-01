package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

public class Module extends LevelStruct<LevelZone, LevelZone> {

    DC_Game game;
    private List<LevelZone> zones;
    private List<Encounter> encounters;
    private int id;
    private static Integer ID = 0;
    private Set<Coordinates> voidCells = new LinkedHashSet<>();

    private Map<Integer, BattleFieldObject> objIdMap = new LinkedHashMap<>();
    private Map<Integer, ObjType> idTypeMap = new LinkedHashMap<>();
    private String objectsData;
    private String borderObjectsData;
    private boolean firstInit;

    public Module(Coordinates origin, int width, int height, String name) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
        id = ID++;
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    public Module(DC_Game game) {
        this.game = game;
    }

    @Override
    public LevelStruct getParent() {
        return DC_Game.game.getDungeonMaster().getFloorWrapper();
    }

    public Module(ModuleData data) {
        this.data = data;
        name = data.getValue(LevelStructure.MODULE_VALUE.name);
    }

    public String getName() {
        if (StringMaster.isEmpty(name)) {
            name = "Main";
        }
        return name;
    }

    @Override
    public Set<Coordinates> initCoordinateSet(boolean buffer) {
        //TODO nullify this on reset!
        Set<Coordinates> coordinatesSet = new LinkedHashSet<>();
        Coordinates c =
                buffer ? getOrigin()
                        : getOrigin()
                        .getOffset(getWidthBuffer(), getHeightBuffer());
        Coordinates c1 = c.getOffset(getEffectiveWidth(false), getEffectiveHeight(false));
        coordinatesSet.addAll(CoordinatesMaster.getCoordinatesBetween(
                c,
                buffer ? c1.getOffset(getWidthBuffer(), getHeightBuffer())
                        : c1)
        );

        return coordinatesSet;
    }

    public int getWidthBuffer() {
        return getData().getIntValue(LevelStructure.MODULE_VALUE.width_buffer);
    }

    public int getHeightBuffer() {
        return getData().getIntValue(LevelStructure.MODULE_VALUE.height_buffer);
    }

    @Override
    public Collection<LevelZone> getSubParts() {
        return getZones();
    }

    @Override
    public Set<Coordinates> getCoordinatesSet() {
        return super.getCoordinatesSet();
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }

    public ModuleData getData() {
        return (ModuleData) data;
    }

    public void setData(ModuleData data) {
        this.data = data;
    }

    public String getValue(LevelStructure.MODULE_VALUE module_value) {
        return getData().getValue(module_value);
    }

    public String getValue(String name) {
        return getData().getValue(name);
    }

    @Override
    public String toString() {
        return name +
                " - Module with " +
                (zones == null ? "no" : zones.size()) +
                " zones" + ", Data: " + getData();
    }

    public int getEffectiveHeight(boolean buffer) {
        if (getData() == null) {
            return getHeight();
        }
        return getHeight() +
                (buffer ? getData().getIntValue(LevelStructure.MODULE_VALUE.height_buffer) * 2 : 0) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width) * 2;
    }

    public int getEffectiveHeight() {
        return getEffectiveHeight(true);
    }

    public int getEffectiveWidth() {
        return getEffectiveWidth(true);
    }

    public int getEffectiveWidth(boolean buffer) {
        if (getData() == null) {
            return getWidth();
        }
        return getWidth() +
                (buffer ? getData().getIntValue(LevelStructure.MODULE_VALUE.width_buffer) * 2 : 0) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width) * 2;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public int getId() {
        return id;
    }

    public Set<Coordinates> getVoidCells() {
        return voidCells;
    }

    public Map<Integer, BattleFieldObject> getObjIdMap() {
        return objIdMap;
    }

    public Map<Integer, ObjType> getIdTypeMap() {
        return idTypeMap;
    }

    public boolean isInitialModule() {
        return getId() == 0;
    }

    public void initBorderObjects() {
        game.getDungeonMaster().getObjInitializer().processBorderObjects(this, borderObjectsData);
    }

    public Map<Integer, BattleFieldObject> initObjects() {
        Map<Integer, BattleFieldObject> objectMap =
                game.getDungeonMaster().getObjInitializer().processObjects(this,
                        getIdTypeMap(),
                        new HashMap<>(), getObjectsData());

        log(LOG_CHANNEL.BUILDING, "Objects created: " + objectMap.size());
        getObjIdMap().putAll(objectMap);
        return objectMap;
    }

    public Module getModule() {
        return this;
    }

    public void setObjectsData(String objectsData) {
        this.objectsData = objectsData;
    }

    public String getObjectsData() {
        return objectsData;
    }

    public void setBorderObjectsData(String borderObjectsData) {
        this.borderObjectsData = borderObjectsData;
    }

    public String getBorderObjectsData() {
        return borderObjectsData;
    }

    public boolean isFirstInit() {
        return firstInit;
    }

    public void setFirstInit(boolean firstInit) {
        this.firstInit = firstInit;
    }
}
