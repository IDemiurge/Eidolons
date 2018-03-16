package main.game.battlecraft.logic.battle.universal;

import main.content.enums.system.AiEnums;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.PlayerAI;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingRunner;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.module.adventure.faction.Faction;
import main.system.data.PlayerData.ALLEGIENCE;
import main.system.graphics.ColorManager.FLAG_COLOR;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Regulus
 */
public class DC_Player extends Player {

    public static DC_Player NEUTRAL;

    protected ALLEGIENCE allegiance;
    private Map<DC_Obj, Coordinates> detectionCache = new HashMap<>();
    private PlayerAI playerAI;
    private String partyDataString;
    private UnitData unitData;
    private Faction faction;

    public DC_Player(String name, Color color, boolean me) {
//        int volume = sound.getVolume() * OptionsMaster.getSoundOptions().
//         getIntValue(SOUND_OPTION.MUSIC_VOLUME) / 100;
        super(name, color, me, null);
    }

    public DC_Player(String name, Color color, boolean me, String portrait) {
        super(name, color, me, portrait);
    }


    public DC_Player(String name, FLAG_COLOR color,
                     String emblem, String portrait,  ALLEGIENCE allegience) {
        super(name, color.getColor(), allegience.isNeutral(), allegience.isMe()
                , portrait, emblem);
    }

    @Override
    public boolean isAi() {
        if (AiTrainingRunner.running)
            return true;
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

    public Set<Unit> getControlledUnits_() {
        return getGame().getUnits().stream().filter(unit -> unit.isOwnedBy(this))
         .collect(Collectors.toSet());
    }
        public Set<Obj> getControlledUnits() {
           return getGame().getUnits().stream().
            filter(unit -> unit.isOwnedBy(this)).collect(Collectors.toSet());
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

    public PlayerAI getAI() {
        return getPlayerAI();
    }

    public PlayerAI getPlayerAI() {
        if (playerAI == null) {
            playerAI = new PlayerAI(AiEnums.PLAYER_AI_TYPE.NORMAL);
        }
        return playerAI;
    }

    public void setPlayerAI(PlayerAI playerAI) {
        this.playerAI = playerAI;
    }

    public UnitData getUnitData() {
        return unitData;
    }

    public void setUnitData(UnitData unitData) {
        this.unitData = unitData;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }
}
