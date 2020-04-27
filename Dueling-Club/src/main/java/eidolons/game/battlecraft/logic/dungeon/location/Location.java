package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 5/8/2017.
 */
public class Location extends DungeonWrapper {

    private Entrance mainEntrance;
    private Entrance mainExit;
    private String entranceData;
    private String exitData;
    private Map<Coordinates, CellScriptData> textDataMap = new HashMap<>();
    private Set<Entrance> transits = new LinkedHashSet<>();
    private boolean initialEdit;

    public Location(DungeonMaster master, Dungeon dungeon) {
        super(dungeon, master);
        this.master = master;
        dungeon.setLocation(this);
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
        Integer id = Integer.valueOf(data);
        Entrance entrance = (Entrance) getGame().getMetaMaster()
                .getDungeonMaster().getObjByOriginalModuleId(id);

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
}
