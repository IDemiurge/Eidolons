package eidolons.game.battlecraft.logic.dungeon.location.layer;

import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Layer {

    String name;
    Set<Integer> ids;
    boolean active;

    Map<Coordinates, String> scripts;
    Map<Coordinates, List<GenericEnums.VFX>> vfxMap;


    public Layer(String name, Set<Integer> ids) {
        this.name = name;
        this.ids = ids;
        vfxMap = new LinkedHashMap<>();
        scripts = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<Coordinates, String> getScripts() {
        return scripts;
    }

    public Map<Coordinates, List<GenericEnums.VFX>> getVfxMap() {
        return vfxMap;
    }
}
