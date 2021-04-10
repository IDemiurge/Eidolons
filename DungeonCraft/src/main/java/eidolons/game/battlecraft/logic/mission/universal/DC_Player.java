package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.core.game.DC_Game;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;
import main.system.data.PlayerData.ALLEGIENCE;
import main.system.graphics.ColorManager.FLAG_COLOR;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Regulus
 */
public class DC_Player extends Player {

    public static DC_Player NEUTRAL=new DC_Player();

    protected ALLEGIENCE allegiance;
    private Map<DC_Obj, Coordinates> detectionCache = new HashMap<>();
    private String partyDataString;
    private UnitsData unitData;
    private FLAG_COLOR flagColorAlt;

    public DC_Player() {
        this("neutral", Color.red, false);
    }
    public DC_Player(String name, Color color, boolean me) {
//        int volume = sound.getVolume() * OptionsMaster.getSoundOptions().
//         getIntValue(SOUND_OPTION.MUSIC_VOLUME) / 100;
        super(name, color, me, null);
    }

    public DC_Player(String name, Color color, boolean me, String portrait) {
        super(name, color, me, portrait);
    }


    public DC_Player(String name, FLAG_COLOR color,
                     String emblem, String portrait, ALLEGIENCE allegience) {
        super(name, color.getColor(), allegience.isNeutral(), allegience.isMe()
         , portrait, emblem);
    }

    @Override
    public boolean isAi() {
        return super.isAi();
    }

    public ALLEGIENCE getAllegiance() {
        return allegiance;
    }

    public void setAllegiance(ALLEGIENCE allegiance) {
        this.allegiance = allegiance;
    }

    public String getPartyDataString() {
        return partyDataString;
    }

    public void setPartyDataString(String partyDataString) {
        this.partyDataString = partyDataString;
    }

    public DC_Game getGame() {
        if (game == null) {
            game = DC_Game.game;
        }
        return (DC_Game) super.getGame();
    }

    public Set<Unit>  collectControlledUnits_() {
        return getGame().getUnits().stream().filter(unit -> unit.isOwnedBy(this))
         .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Obj> collectControlledUnits() {
        return getGame().getUnits().stream().
         filter(unit -> unit.isOwnedBy(this)).collect(Collectors.toCollection(LinkedHashSet::new));
//        Set<Obj> units = new LinkedHashSet<>();
//        for (Unit unit : (getGame().getUnits())) {
//            if (unit.getOwner() == this) {
//                units.add(unit);
//            }
//        return units;

    }

    public Map<DC_Obj, Coordinates> getLastSeenCache() {
        return detectionCache;
    }

    public UnitsData getUnitData() {
        return unitData;
    }

    public void setUnitData(UnitsData unitData) {
        this.unitData = unitData;
    }

    public void setFlagColorAlt(FLAG_COLOR flagColorAlt) {
        this.flagColorAlt = flagColorAlt;
    }

    public FLAG_COLOR getFlagColorAlt() {
        return flagColorAlt;
    }
}
