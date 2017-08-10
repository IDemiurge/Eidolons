package main.game.battlecraft.logic.battle.universal;

import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.rules.combat.damage.Damage;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleOptionManager<E extends Battle> extends BattleHandler<E> {

    BattleOptions options;
    DIFFICULTY defaultDifficulty = DIFFICULTY.NOVICE;

    public BattleOptionManager(BattleMaster<E> master) {
        super(master);
        options = new BattleOptions();
    }

//    public int getBattleLevel() {
//        battleLevel = 0;
//
//        List<? extends Obj> units = new LinkedList<>(game.getPlayer(true).getControlledUnits());
//        if (units.isEmpty() && game.getParty() != null) {
//            units = new LinkedList<>(game.getParty().getMembers());
//        }
//        for (Obj unit : units) {
//            battleLevel += unit.getIntParam(PARAMS.POWER);
//        }
//
//        return battleLevel;
//    }

    public BattleOptions getOptions() {
        return options;
    }

    public void applyDifficulty(Damage damage) {
//        Boolean friendlyFireAllies_enemies_none;
//
//        Boolean ally_enemy_neutral = damage.getTarget().isMine()
//         && damage.getSource().isHostileTo(damage.getTarget().getOwner());
//if (ally_enemy_neutral==null ){
//
//} else if (ally_enemy_neutral) {
//    damage.setAmount(damage.getAmount() * getOptions().getDifficulty().getDamagePercentageTakenAllies());
//}   else if (ally_enemy_neutral) {
//    damage.setAmount(damage.getAmount() * getOptions().getDifficulty().
////     getDamagePercentageTakenEnemies());
//}
////TODO maybe easier to modify endurance/tough
//
    }
}
