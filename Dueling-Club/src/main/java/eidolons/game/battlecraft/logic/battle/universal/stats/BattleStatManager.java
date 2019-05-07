package eidolons.game.battlecraft.logic.battle.universal.stats;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingRunner;
import eidolons.game.battlecraft.logic.battle.universal.Battle;
import eidolons.game.battlecraft.logic.battle.universal.BattleHandler;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.Obj;
import main.game.core.game.StatMaster;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.Map;

/**
 * Created by JustMe on 5/7/2017.
 * <p>
 * in demo, what stats will we want?
 * not only battle stats...
 * Usability stats!
 */
public class BattleStatManager<E extends Battle> extends BattleHandler<E> implements StatMaster {
    protected BattleStats stats;

    public BattleStatManager(BattleMaster master) {
        super(master);
        initializeBattle();
    }

    protected void attachStatWatcher() {

    }

    protected void initializeBattle() {
        stats = new BattleStats(game);
        //        battle.setValue(BATTLE_STATS.PLAYER_STARTING_PARTY, game.getPlayerParty());
        //        battle.setValue(BATTLE_STATS.LEVEL, getBattleLevel() + "");
        //        battle.setValue(BATTLE_STATS.ROUND, "1");

    }


    public BattleStats getStats() {
        return stats;
    }

    @Override
    public void eventBeingHandled(Event event) {
        if (!(event.getRef().getSourceObj() instanceof Unit))
            return;
        Unit source = (Unit) event.getRef().getSourceObj();

        Unit target = null;
        if ((event.getRef().getTargetObj() instanceof Unit))
            target = (Unit) event.getRef().getTargetObj();

        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            STANDARD_EVENT_TYPE eventType = (STANDARD_EVENT_TYPE) event.getType();
            switch (eventType) {
                case UNIT_FALLS_UNCONSCIOUS:
                    modifyUnitStat(COMBAT_STATS.DIED, source, 1);
                    break;
                case UNIT_HAS_BEEN_DEALT_PURE_DAMAGE: {
                    if (target != null)
                        unitDealtDamage(source, target, event.getRef().getAmount());
                    break;
                }
                case UNIT_HAS_BEEN_KILLED: {
                    if (target != null)
                        unitKilled(target, source);
                }
                //                case ACTION_ACTIVATED:
                case UNIT_ACTION_COMPLETE:
                case ATTACK_CRITICAL:
                case ATTACK_SNEAK:
                case ATTACK_DODGED:

                case ATTACK_PARRIED:

                case ATTACK_BLOCKED:

                case ATTACK_MISSED:

                case ATTACK_OF_OPPORTUNITY:

                case ATTACK_COUNTER:

                case ATTACK_INSTANT:

                case ACTION_MISSED:
                case UNIT_HAS_FALLEN_UNCONSCIOUS:
                case UNIT_HAS_BEEN_ANNIHILATED:
                case UNIT_HAS_CHANGED_FACING:
                case ITEM_BROKEN:
                case ITEM_ACQUIRED:
                case NEW_ROUND:
                case SPELL_RESOLVED:
                case UNIT_HAS_USED_QUICK_ITEM:

                case TIME_ELAPSED:

                case INTERACTIVE_OBJ_USED:

                case SECRET_FOUND: {
                    String name = getStatName(eventType);
                    Integer n = getStatAmount(event, eventType);
                    MapMaster.addToIntegerMap(stats.getMainStatMap(), name, n);
                    MapMaster.addToIntegerMap(stats.getUnitStats(
                     source).getGeneralStats(), name, n);

                    if (source.isPlayerCharacter())
                        checkAddGlory(target, event, eventType, n);
                }

            }
        } else if (AiTrainingRunner.running)
            if (event.getRef().getAmount() != null) {
                if (event.getType() instanceof EventType) {
                    modifyUnitModStat(target.isEnemyTo(source.getOwner()), (event.getType()).getArg()
                     , source, event.getRef().getAmount());

                }
            }

    }

    protected void checkAddGlory(Unit target, Event event, STANDARD_EVENT_TYPE eventType, Integer n) {
    }

    protected String getStatName(STANDARD_EVENT_TYPE eventType) {
        return StringMaster.getWellFormattedString(eventType.name());
    }

    protected Integer getStatAmount(Event event, STANDARD_EVENT_TYPE eventType) {
        switch (eventType) {

        }
        return 1;
    }


    protected void unitDealtDamage(Unit source, Unit target, Integer amount) {
        if (source==null || target==null ){
            return;
        }
        if (source.isEnemyTo(target.getOwner())) {
            modifyUnitStat(COMBAT_STATS.DAMAGE_DEALT_ENEMIES, source, amount);
        } else {
            if (source.isAlliedTo(target.getOwner()))
                modifyUnitStat(COMBAT_STATS.DAMAGE_DEALT_ALLIES, source, amount);
        }
        modifyUnitStat(COMBAT_STATS.DAMAGE_TAKEN, target, amount);

        modifyPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_DEALT,
         source.getOwner(), amount);
        modifyPlayerStat(PLAYER_STATS.ALLIES_DAMAGE_TAKEN,
         target.getOwner(), amount);
    }

    protected void modifyUnitModStat(boolean hostile, String stat, Unit sourceObj, int mod) {
        PARAMETER p = ContentValsManager.getPARAM(stat);
        Map<PARAMETER, Integer> map = hostile ? stats.getUnitStats(sourceObj).getEnemyModMap()
         : stats.getUnitStats(sourceObj).getAllyModMap();
        MapMaster.addToIntegerMap(
         map, p, mod);

    }

    public void unitKilled(Unit killed, Unit killer) {
        stats.getUnitStats(killer).getKillsMap().put(killed, getGame().getState().getRound());
        modifyUnitStat(COMBAT_STATS.UNITS_SLAIN, killer, 1);
        modifyUnitStat(COMBAT_STATS.DIED, killed, 1);
        if (killed.isEnemyTo(killer.getOwner())) {
            modifyPlayerStat(PLAYER_STATS.ALLY_ENEMIES_KILLED,
             killer.getOwner(), 1);
            modifyPlayerStat(PLAYER_STATS.ALLY_ENEMIES_KILLED_POWER,
             killer.getOwner(), killed.calculatePower());
            modifyUnitStat(COMBAT_STATS.ENEMIES_KILLED, killer, 1);
            modifyUnitStat(COMBAT_STATS.ENEMIES_KILLED_POWER, killer, killed.calculatePower());
        }

        modifyPlayerStat(PLAYER_STATS.ALLIES_DIED,
         killed.getOwner(), 1);
        modifyPlayerStat(PLAYER_STATS.ALLIES_DIED_POWER,
         killed.getOwner(), killed.calculatePower());
        if (killed.isHero())
            modifyPlayerStat(PLAYER_STATS.ALLY_HEROES_DIED,
             killed.getOwner(), 1);
    }

    protected void modifyUnitStat(STAT stat, Unit sourceObj, int mod) {
        MapMaster.addToIntegerMap(
         stats.getUnitStats(sourceObj).getStatMap(), stat, mod);
    }

    protected void modifyPlayerStat(PLAYER_STATS stat, DC_Player owner, int i) {
        MapMaster.addToIntegerMap(
         stats.getPlayerStats(owner).getStatsMap(), stat, i);
    }

    protected void stat(STAT stat, Obj sourceObj, Obj targetObj) {

    }

    public enum COMBAT_STATS implements STAT {
        ACTION_USED,
        DAMAGE_CREATED,
        UNITS_SLAIN,

        DAMAGE_DEALT_ALLIES,
        DAMAGE_DEALT_ENEMIES,
        ENEMIES_KILLED,
        ENEMIES_KILLED_POWER,
        DAMAGE_TAKEN,
        FALLEN_UNCONSCIOUS,
        DIED
    }

    public enum PLAYER_STATS implements STAT {
        ALLIES_DIED,
        ALLIES_DIED_POWER,
        ALLIES_DAMAGE_TAKEN,
        ALLIES_DAMAGE_DEALT,
        ALLY_HEROES_DIED,
        ALLY_ENEMIES_KILLED,
        ALLY_ENEMIES_KILLED_POWER,;
    }

    public enum STAT_WATCHER {
        COMBAT,
        USABILITY,
        AI,

    }

    // use AspectJ !
    public enum USABILITY_STATS {
        INFO_PANEL_OPENED,
        RADIAL_MENU_OPENED,
        GAME_MENU,
        OPTIONS_MENU,
        MAIN_MENU,

        BOTTOM_SPELL_CLICKED,


    }

    public interface STAT {

    }

}
