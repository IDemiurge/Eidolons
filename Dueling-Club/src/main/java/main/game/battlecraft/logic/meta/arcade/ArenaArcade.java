package main.game.battlecraft.logic.meta.arcade;

import main.client.cc.logic.party.Party;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.module.adventure.faction.Faction;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

import java.util.List;

public class ArenaArcade extends Entity {
    List<BattleLevel> levels;
    String version;
    List<String> heroData;
    String path;
    Faction factionData;
    Party party;

    public ArenaArcade(ObjType type, DC_Game game) {
        super(type, Player.NEUTRAL, game, new Ref(game));
        toBase();
    }

    @Override
    public void init() {

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getHeroData() {
        return heroData;
    }

    public void setHeroData(List<String> heroData) {
        this.heroData = heroData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Faction getFactionData() {
        return factionData;
    }

    public void setFactionData(Faction factionData) {
        this.factionData = factionData;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

}
