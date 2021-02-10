package libgdx.gui.panels.dc.menus.outcome;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager.PLAYER_STATS;
import eidolons.game.battlecraft.logic.mission.universal.stats.PlayerStats;
import eidolons.game.battlecraft.logic.mission.universal.stats.UnitStats;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import libgdx.gui.generic.ValueContainer;
import libgdx.texture.TextureCache;
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
        return Arrays.stream(getDisplayedPlayerStats()).map(this::getPlayerStatsContainer).collect(Collectors.toList());
    }

    private ValueContainer getPlayerStatsContainer(PLAYER_STATS stat) {
        return new ValueContainer(StringMaster.format(stat.toString()), getPlayerStat(stat) + "");
    }

    private PLAYER_STATS[] getDisplayedPlayerStats() {
        return new PLAYER_STATS[]{
        };
    }

    public UnitStats getHeroStats() {
        return game.getMissionMaster().getStatManager().
         getStats().getUnitStats(Eidolons.getMainHero());
    }
        private PlayerStats getPlayerStats() {
        return game.getMissionMaster().getStatManager().
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
        return game.getMissionMaster().getStatManager().getStats().getGlory();
    }

    public Boolean getOutcome() {

        //null for surrender?
        return game.getMissionMaster().getOutcomeManager().getOutcome();
    }

    public List<TextureRegion> getStatUnitsPortraits() {
        return game.getMetaMaster().getPartyManager().getParty().getMembers()
         .stream().map(unit -> TextureCache.getOrCreateR(unit.getImagePath()
         )).collect(Collectors.toList());
    }

}
