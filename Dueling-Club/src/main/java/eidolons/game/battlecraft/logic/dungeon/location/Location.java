package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class Location extends DungeonWrapper {

    private List<Entrance> entrances; //TODO
    private String entranceData;
    private Entrance mainEntrance;
    private Entrance mainExit;

    public Location(DungeonMaster master, Dungeon dungeon) {
        super(dungeon, master);
        this.master = master;
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
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


    public void initEntrances() {

        if (StringMaster.isEmpty(entranceData)) {
            entranceData = getProperty(PROPS.DUNGEON_MAIN_ENTRANCES, true);
        }
        if (StringMaster.isEmpty(entranceData) && (!getProperty(PROPS.ENTRANCE_COORDINATES, true).isEmpty())) {
            entranceData = getProperty(PROPS.ENTRANCE_COORDINATES, true);
        }
        if (StringMaster.isEmpty(entranceData)) {
            return;
        }
        String enterData = entranceData.split(";")[0];
        String name = VariableManager.removeVarPart(enterData);
        Coordinates c = Coordinates.get(true, VariableManager.getVarIfExists(enterData));
        if (name.contains("=")) {
            name = name.split("=")[1];
        }
        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainEntrance(e);
                }
            }
        }
        if (getMainEntrance() == null) {
            ObjType type = DataManager.getType(name, DC_TYPE.BF_OBJ);
            if (type == null) {
                type = getEntranceType();
            }
            setMainEntrance(new Entrance(c.x, c.y, type, getGame() ));
        }
        if (entranceData.split(";").length < 2) {
            return;
        }
        String exitData = entranceData.split(";")[1];
        name = VariableManager.removeVarPart(exitData);
        c = Coordinates.get(true, VariableManager.getVarIfExists(exitData));
        if (name.contains("=")) {
            name = name.split("=")[1];
        }
        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainExit(e);
                }
            }
        }

        if (getMainExit() == null) {
            ObjType type = DataManager.getType(name, DC_TYPE.BF_OBJ);
            if (type == null) {
                type = getExitType();
            }
            setMainExit(new Entrance(c.x, c.y, type, getGame()));
        }
        if (CoreEngine.isReverseExit()) {
            Entrance e = getMainEntrance();
            setMainEntrance(getMainExit());
            setMainExit(e);

        }
    }

    protected ObjType getEntranceType() {
        return DataManager.getType(getGame().getMetaMaster().getDungeonMaster().getDefaultEntranceType(), DC_TYPE.BF_OBJ);
    }

    protected ObjType getExitType() {
        return DataManager.getType(getGame().getMetaMaster().getDungeonMaster().
                getDefaultExitType(), DC_TYPE.BF_OBJ);
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public Entrance getMainEntrance() {
        return mainEntrance;
    }

    public void setMainEntrance(Entrance mainEntrance) {
        this.mainEntrance = mainEntrance;
        if (mainEntrance != null) {
            mainEntrance.setMainEntrance(true);
        }
    }

    public Entrance getMainExit() {
        return mainExit;
    }

    public void setMainExit(Entrance mainExit) {
        this.mainExit = mainExit;
        if (mainExit != null) {
            mainExit.setMainExit(true);
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
}
