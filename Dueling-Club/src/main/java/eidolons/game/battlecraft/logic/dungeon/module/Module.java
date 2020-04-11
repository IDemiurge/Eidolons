package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Floor;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;

import java.util.List;
import java.util.Set;

public class Module extends LevelStruct<LevelZone> {

    private List<LevelZone> zones;
    private LE_Floor floor;
    private List<Encounter> encounters;

    public Module(Coordinates origin, int width, int height, String name) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public Module() {
        super();
    }

    @Override
    protected LevelStruct getParent() {
        return DC_Game.game.getDungeonMaster().getDungeonLevel();
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
    public List<LevelZone> getSubParts() {
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

    @Override
    protected StructureData createData() {
        return                new ModuleData(new LE_Module(this));

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
        return  name +
                " - Module with " +
                zones.size() +
                " zones"+ ", Data: " + getData() ;
    }

    public int getEffectiveHeight() {
        if (getData() == null) {
            return getHeight();
        }
        return getHeight() +
                getData().getIntValue(LevelStructure.MODULE_VALUE.height_buffer) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }

    public int getEffectiveWidth() {
        if (getData() == null) {
            return getWidth();
        }
        return getWidth() +
                getData().getIntValue(LevelStructure.MODULE_VALUE.width_buffer) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }

    public void setFloor(LE_Floor floor) {
        this.floor = floor;
    }

    public LE_Floor getFloor() {
        return floor;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }
}
