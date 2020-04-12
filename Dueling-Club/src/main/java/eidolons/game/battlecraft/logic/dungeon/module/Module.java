package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Module extends LevelStruct<LevelZone, LevelZone> {

    private List<LevelZone> zones;
    private List<Encounter> encounters;
    private Integer id;
    private static Integer ID = 0;
    private Set<Coordinates> voidCells = new LinkedHashSet<>();

    public Module(Coordinates origin, int width, int height, String name) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.name = name;
        id = ID++;
    }

    public Module() {
        super();
    }

    @Override
    protected LevelStruct getParent() {
        return DC_Game.game.getDungeonMaster().getDungeonWrapper();
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
                zones.size() +
                " zones" + ", Data: " + getData();
    }

    public int getEffectiveHeight(boolean buffer) {
        if (getData() == null) {
            return getHeight();
        }
        return getHeight() +
                (buffer ? getData().getIntValue(LevelStructure.MODULE_VALUE.height_buffer) : 0) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }

    public int getEffectiveHeight() {
        return getEffectiveHeight(false);
    }

    public int getEffectiveWidth() {
        return getEffectiveWidth(false);
    }

    public int getEffectiveWidth(boolean buffer) {
        if (getData() == null) {
            return getWidth();
        }
        return getWidth() +
                (buffer ? getData().getIntValue(LevelStructure.MODULE_VALUE.width_buffer) : 0) +
                getData().getIntValue(LevelStructure.MODULE_VALUE.border_width);
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public Integer getId() {
        return id;
    }

    public Set<Coordinates> getVoidCells() {
        return voidCells;
    }
}
