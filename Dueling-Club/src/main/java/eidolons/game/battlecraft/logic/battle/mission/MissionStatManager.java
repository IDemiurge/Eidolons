package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStats;
import eidolons.game.core.Eidolons;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;

import java.util.Map;

/**
 * Created by JustMe on 5/10/2017.
 */
public class MissionStatManager extends BattleStatManager<MissionBattle> {
    public MissionStatManager(MissionBattleMaster missionBattleMaster) {
        super(missionBattleMaster);
    }

    public static String getGameStatsText() {
        BattleStats stats = Eidolons.getGame().getBattleMaster().getStatManager().getStats();
        String text = "";
        text += "\n Glory rating:" + stats.getGlory() + StringMaster.wrapInParenthesis(
         getCodename(stats.getGlory())
        );
        text += "\n Units Slain:" + stats.getUnitsSlain();
        text += "\n Damage dealt:" + stats.getDAMAGE_DEALT();
        text += "\n Damage taken:" + stats.getDAMAGE_TAKEN();
        Map<String, Integer> map = stats.getHeroStats().getGeneralStats();
        for (String s : map.keySet()) {
            text += "\n" + s + ": " + map.get(s);
        }
        //        for (String s : stats.getHeroStats().getStatMap()) {
        //            stats+="\n" + s + ": " + stats.getMainStats().get(s);
        //        }
        //class outcome!
        return text;
    }

    public static String getCodename(Integer glory) {
        //just a formula for N => enumeration...
        int rank = glory / 100;
        for (int i = rank; i <= rank * 5; i++) {
            switch (rank) {
                case 0:
                    return "Beardless";
                case 1:
                    return "Ill-Starred";
                case 3:
                    return "Forlorn";
                case 6:
                    return "Disfavored";
                case 10:
                    return "Unforgiven";
                case 15:
                    return "Scarred";
                case 22:
                    return "Ambitious";
                case 35:
                    return "Warsworn";
                case 52:
                    return "Fighting Man";
                case 86:
                    return "Sleepless Evil";
                case 124:
                    return "Einherjar";
            }
        }
        return "God Emperor";
    }

    @Override
    protected void checkAddGlory(Unit target, Event event, STANDARD_EVENT_TYPE eventType, Integer n) {
        super.checkAddGlory(target, event, eventType, n);
        switch (eventType) {
            case UNIT_HAS_BEEN_KILLED:
                stats.addGlory(target.getPower() / 10);
                break;
        }
    }

}
