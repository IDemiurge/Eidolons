package main.game.logic.macro.travel;

import main.client.battle.Wave;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.map.Route;
import main.system.auxiliary.StringMaster;

import java.util.List;

public class Encounter {
    Route route;
    Route place;
    Integer powerMod;
    Integer presetPower; // by Time
    /*
     * allow for quick-resolution via 'diplomacy'
     */
    private int hoursIntoTheTurn; // what if 12+? pass turn? TODO for TImeMaster
    private String typeNames;
    private List<Wave> waves;
    private MacroParty defendingParty;
    private Integer progress;
    private boolean surrounded;
    private List<MacroGroup> groups;

    public Encounter(Route route, MacroParty party, List<MacroGroup> groups) {
        this.defendingParty = party;
        this.route = route;
        this.groups = groups;
        // for (MacroGroup group: groups)
        // typeNames+= group.getGroupName()
        this.typeNames = StringMaster.constructStringContainer(groups);

    }

    public Integer getProgressMadeBeforeBattle() {
        return getProgress();
    }

    public MacroParty getDefendingParty() {
        return defendingParty;
    }

    public Route getPlace() {
        return place;
    }

    public Route getRoute() {
        return route;
    }

    public MacroRef getRef() {
        return defendingParty.getRef();
    }

    public Integer getPowerMod() {
        return powerMod;
    }

    public Integer getPresetPower() {
        return presetPower;
    }

    public int getHoursIntoTheTurn() {
        return hoursIntoTheTurn;
    }

    public String getTypeNames() {
        return typeNames;
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public boolean isSurrounded() {
        return surrounded;
    }

    public void setSurrounded(boolean b) {
        surrounded = b;
    }

    public List<MacroGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<MacroGroup> groups) {
        this.groups = groups;
    }

}
