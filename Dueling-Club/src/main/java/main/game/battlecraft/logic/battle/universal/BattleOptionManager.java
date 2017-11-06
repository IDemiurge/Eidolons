package main.game.battlecraft.logic.battle.universal;

import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.rules.combat.damage.Damage;
import main.system.auxiliary.EnumMaster;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleOptionManager<E extends Battle> extends BattleHandler<E> {

    BattleOptions options;
    DIFFICULTY defaultDifficulty = DIFFICULTY.NOVICE;
    DIFFICULTY difficulty;

    public BattleOptionManager(BattleMaster<E> master) {
        super(master);
        try {
            defaultDifficulty =
             new EnumMaster<DIFFICULTY>().retrieveEnumConst(
              DIFFICULTY.class, OptionsMaster.getGameplayOptions().
               getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        options = new BattleOptions();
        options.setValue(ARENA_GAME_OPTIONS.DIFFICULTY, defaultDifficulty.name());
    }

    public void difficultySet(String value) {
        DIFFICULTY newDifficulty = new EnumMaster<DIFFICULTY>().retrieveEnumConst(
         DIFFICULTY.class, value);
        if (newDifficulty == difficulty)
            return;
        difficulty = newDifficulty;
        getGame().getManager().reset();
    }

    public void selectDifficulty() {

    }

    public DIFFICULTY getDifficulty() {
        if (difficulty == null) {
            difficulty = new EnumMaster<DIFFICULTY>().retrieveEnumConst(
             DIFFICULTY.class, OptionsMaster.getGameplayOptions().
              getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
        }
        return difficulty;
//        return   new EnumMaster<DIFFICULTY>().retrieveEnumConst(
//         DIFFICULTY.class,OptionsMaster.getGameplayOptions().
//          getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
    }
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
//        if (ally_enemy_neutral == null) {
//
//        } else if (ally_enemy_neutral) {
//            damage.setAmount(damage.getAmount() * getOptions().getDifficulty().getDamagePercentageTakenAllies());
//        } else if (ally_enemy_neutral) {
//            damage.setAmount(damage.getAmount() * getOptions().getDifficulty().
//     getDamagePercentageTakenEnemies());
//}
////TODO maybe easier to modify endurance/tough
//
    }

    public void applyDifficultyMods(BattleFieldObject unit) {
        Boolean ally_enemy_neutral = null;
        if (unit.isMine())
            ally_enemy_neutral = true;
        if (unit.isEnemyTo(game.getPlayer(true)))
            ally_enemy_neutral = false;
        if (ally_enemy_neutral == null)
            return;
        int mod = 100;
        if (ally_enemy_neutral) {
            if (unit.isMainHero()) {
                mod = getDifficulty().getHealthPercentageMainHero();
            } else {
                mod = getDifficulty().getHealthPercentageAlly();
            }
        } else
            mod = getDifficulty().getHealthPercentageEnemy();

        unit.multiplyParamByPercent(PARAMS.ENDURANCE, mod, false);
        unit.multiplyParamByPercent(PARAMS.TOUGHNESS, mod, false);
        if (unit.isMine())
            unit.multiplyParamByPercent(PARAMS.STAMINA, mod/2, false);

        unit.modifyParamByPercent(PARAMS.N_OF_ACTIONS, mod / 4);
    }

}
