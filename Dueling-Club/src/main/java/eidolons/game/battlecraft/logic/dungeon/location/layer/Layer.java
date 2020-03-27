package eidolons.game.battlecraft.logic.dungeon.location.layer;

import main.content.enums.GenericEnums;
import main.elements.triggers.Trigger;
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
    private boolean hidden;
    private String triggerText;
    private Trigger trigger;


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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getTriggerText() {
        return triggerText;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public void setTriggerText(String triggerText) {
        this.triggerText = triggerText;
    }

    public void setIds(Set<Integer> toIdSet) {
        ids = toIdSet;
    }
}
