package eidolons.libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager.PLAYER_STATS;
import eidolons.game.battlecraft.logic.battle.universal.stats.PlayerStats;
import eidolons.game.battlecraft.logic.battle.universal.stats.UnitStats;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/15/2017.
 */
public class OutcomeDatasource {

    DC_Game game;

    public OutcomeDatasource(DC_Game game) {
        this.game = game;
    }

    public List<ValueContainer> getPlayerStatsContainers() {
        return Arrays.stream(getDisplayedPlayerStats()).map(stat ->
         getPlayerStatsContainer(stat)).collect(Collectors.toList());
    }

    private ValueContainer getPlayerStatsContainer(PLAYER_STATS stat) {
        return new ValueContainer(StringMaster.getWellFormattedString(stat.toString()), getPlayerStat(stat) + "");
    }

    private PLAYER_STATS[] getDisplayedPlayerStats() {
        return new PLAYER_STATS[]{
        };
    }

    public UnitStats getHeroStats() {
        return game.getBattleMaster().getStatManager().
         getStats().getUnitStats(Eidolons.getMainHero());
    }
        private PlayerStats getPlayerStats() {
        return game.getBattleMaster().getStatManager().
         getStats().getPlayerStats(game.getPlayer(true));
    }

    private Integer getPlayerStat(PLAYER_STATS stat) {
        return getPlayerStats().getStatsMap().get(stat);
    }

    public Integer getUnitsSlain() {
        return getPlayerStat(PLAYER_STATS.ALLY_ENEMIES_KILLED);
    }

    public Integer getALLIES_DIED() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DIED);
    }

    public Integer getDAMAGE_DEALT() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_DEALT);
    }

    public Integer getDAMAGE_TAKEN() {
        return getPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_TAKEN);
    }


    public Integer getGlory() {
        return game.getBattleMaster().getStatManager().getStats().getGlory();
    }

    public Boolean getOutcome() {

        //null for surrender?
        return game.getBattleMaster().getOutcomeManager().getOutcome();
    }

    public List<TextureRegion> getStatUnitsPortraits() {
        return game.getMetaMaster().getPartyManager().getParty().getMembers()
         .stream().map(unit -> TextureCache.getOrCreateR(unit.getImagePath()
         )).collect(Collectors.toList());
    }

}
