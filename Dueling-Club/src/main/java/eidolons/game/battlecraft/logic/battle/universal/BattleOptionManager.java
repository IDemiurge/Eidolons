package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.menu.selection.difficulty.DifficultySelectionPanel;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DIFFICULTY;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleOptionManager<E extends Battle> extends BattleHandler<E> {

    BattleOptions options;
    DIFFICULTY defaultDifficulty = GenericEnums.DIFFICULTY.NOVICE;
    DIFFICULTY difficulty;

    public BattleOptionManager(BattleMaster<E> master) {
        super(master);
        try {
            defaultDifficulty =
             new EnumMaster<DIFFICULTY>().retrieveEnumConst(
              DIFFICULTY.class, OptionsMaster.getGameplayOptions().
               getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        options = new BattleOptions();
        options.setValue(ARENA_GAME_OPTIONS.DIFFICULTY, defaultDifficulty.name());

        if (CoreEngine.isIDE()) {
            difficulty = defaultDifficulty;
        }
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
//        List<? extends Obj> units = new ArrayList<>(game.getPlayer(true).getControlledUnits());
//        if (units.isEmpty() && game.getParty() != null) {
//            units = new ArrayList<>(game.getParty().getMembers());
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

    public static final float CHEAT_MODIFIER = 1.25f;

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
                mod = (int) (mod*CHEAT_MODIFIER);
            } else {
                mod = getDifficulty().getHealthPercentageAlly();
                mod = (int) (mod*CHEAT_MODIFIER);
            }
        } else
        {
            mod = getDifficulty().getHealthPercentageEnemy();
            mod = (int) (mod/CHEAT_MODIFIER);
        }



        unit.multiplyParamByPercent(PARAMS.ENDURANCE, mod, false);
        unit.multiplyParamByPercent(PARAMS.TOUGHNESS, mod, false);
        unit.multiplyParamByPercent(PARAMS.ATTACK_MOD, mod / 2, false);
        unit.multiplyParamByPercent(PARAMS.DEFENSE_MOD, mod / 2, false);
        unit.multiplyParamByPercent(PARAMS.SPELLPOWER, mod / 3, false);
        unit.multiplyParamByPercent(PARAMS.OFFHAND_ATTACK_MOD, mod / 2, false);

        unit.multiplyParamByPercent(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD, mod / 2, false);

        if (unit.isMine())
        {
            unit.multiplyParamByPercent(PARAMS.STAMINA, mod / 2, false);
            if (mod>100)
                if (ExplorationMaster.isExplorationOn())
                {
                    int amount = unit.getIntParam(PARAMS.STAMINA) * mod / 1000;
                    unit.modifyParameter(PARAMS.STAMINA_REGEN,             amount, false);
                }
        }
        else {
            unit.multiplyParamByPercent(PARAMS.STEALTH, mod / 2, false);
        }
        unit.modifyParamByPercent(PARAMS.N_OF_ACTIONS, mod / 4);
    }

    public boolean chooseDifficulty(boolean forced) {
        if (!forced)
            if (difficulty != null) {
                return true;
        }
        GuiEventManager.trigger(
         GuiEventType.SHOW_DIFFICULTY_SELECTION_PANEL);
        try {
            difficulty = new EnumMaster<DIFFICULTY>().retrieveEnumConst(DIFFICULTY.class,
             WaitMaster.
              waitForInput(DifficultySelectionPanel.WAIT_OPERATION).toString());
            main.system.auxiliary.log.LogMaster.log(1, "+++++++++selected difficulty = " + difficulty);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return true;
    }
}
