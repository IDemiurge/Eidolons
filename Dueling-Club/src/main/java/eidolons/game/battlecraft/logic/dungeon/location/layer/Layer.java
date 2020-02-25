package eidolons.game.battlecraft.logic.dungeon.location.layer;

import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Layer {

    String name;
    Set<Integer> ids;
    boolean on;

    Map<Coordinates, String> scripts;
    Map<Coordinates, GenericEnums.VFX> vfxMap;

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

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public Map<Coordinates, String> getScripts() {
        return scripts;
    }

    public Map<Coordinates, GenericEnums.VFX> getVfxMap() {
        return vfxMap;
    }

}
