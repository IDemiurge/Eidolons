package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.consts.CellData;
import eidolons.content.consts.DecorData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorWrapper;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.Entrance;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 5/8/2017.
 */
public class Location extends FloorWrapper {

    private Entrance mainEntrance;
    private Entrance mainExit;
    private String entranceData;
    private String exitData;
    private Map<Coordinates, CellScriptData> textDataMap = new LinkedHashMap<>();
    private final Map<Coordinates, DecorData> decorMap = new LinkedHashMap<>();
    private final Map<Coordinates, CellData> cellMap = new LinkedHashMap<>();


    private final Set<Entrance> transits = new LinkedHashSet<>();
    private boolean initialEdit;

    public Location(DungeonMaster master, Floor floor) {
        super(floor, master);
        this.master = master;
        floor.setLocation(this);
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    public Coordinates getDefaultPlayerSpawnCoordinates() {
        if (getMainEntrance() != null)
            return getMainEntrance().getCoordinates();
        return super.getDefaultPlayerSpawnCoordinates();
    }

    public void setEntranceData(String entranceData) {
        this.entranceData = entranceData;
    }

    public void initTransit(String data, boolean exit) {
        if (StringMaster.isEmpty(data)) {
            return;
        }
        Entrance entrance;
        if (!NumberUtils.isInteger(data)) {
            Coordinates c = Coordinates.get(data);
            String defaultEntrance = "The Light";
            ObjType type = DataManager.getType(defaultEntrance, DC_TYPE.BF_OBJ);
            entrance = (Entrance) getGame().createObject(type, c, DC_Player.NEUTRAL);
        } else {
            Integer id = Integer.valueOf(data);
            entrance = (Entrance) getGame().getMetaMaster()
                    .getDungeonMaster().getObjByOriginalModuleId(id);
        }

        if (exit) {
            setMainExit(entrance);
        } else {
            setMainEntrance(entrance);
        }
    }

    public void initMainExit() {
        initTransit(exitData, true);
    }

    public void initMainEntrance() {
        initTransit(entranceData, false);
    }

    @Override
    public DC_Game getGame() {
        return super.getGame();
    }

    public Entrance getMainEntrance() {
        return mainEntrance;
    }

    public void setMainEntrance(Entrance mainEntrance) {
        this.mainEntrance = mainEntrance;
        if (mainEntrance != null) {
            mainEntrance.setMainEntrance(true);
            log(LOG_CHANNEL.BUILDING, "Main Entrance: " + mainEntrance.getNameAndCoordinate());
        }
    }

    public Entrance getMainExit() {
        return mainExit;
    }

    public void setMainExit(Entrance mainExit) {
        this.mainExit = mainExit;
        if (mainExit != null) {
            mainExit.setMainExit(true);
            log(LOG_CHANNEL.BUILDING, "Main Exit: " + mainExit.getNameAndCoordinate());
        }
    }

    @Override
    public Collection<Module> getSubParts() {
        return getModules();
    }

    @Override
    public Collection<Module> getChildren() {
        return getSubParts();
    }

    public Collection<Module> getModules() {
        return getGame().getMetaMaster().getModuleMaster().getModules();
    }

    public void setExitData(String exitData) {
        this.exitData = exitData;
    }

    public String getExitData() {
        return exitData;
    }

    public Map<Coordinates, CellScriptData> getTextDataMap() {
        return textDataMap;
    }

    public void setTextDataMap(Map<Coordinates, CellScriptData> textDataMap) {
        this.textDataMap = textDataMap;
    }

    public Set<Entrance> getTransits() {
        return transits;
    }

    public void addTransit(Entrance e) {
        transits.add(e);
    }

    public void setInitialEdit(boolean initialEdit) {
        this.initialEdit = initialEdit;
    }

    public boolean isInitialEdit() {
        return initialEdit;
    }

    public void addTextDataMap(Map<Coordinates, CellScriptData> map) {
        textDataMap.putAll(map);
    }

    public Map<Coordinates, CellData> getCellMap() {
        return cellMap;
    }
    public Map<Coordinates, DecorData> getDecorMap() {
        return decorMap;
    }

    public void addDecorDataMap(Map<Coordinates, DecorData> map) {
        decorMap.putAll(map);
    }
    public void addCellDataMap(Map<Coordinates, CellData> map) {
        cellMap.putAll(map);
    }

}
