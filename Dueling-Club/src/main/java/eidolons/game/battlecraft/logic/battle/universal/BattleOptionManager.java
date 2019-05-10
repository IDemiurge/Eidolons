package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import eidolons.libgdx.gui.menu.selection.difficulty.DifficultySelectionPanel;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DIFFICULTY;
import main.content.enums.entity.SkillEnums.ATTRIBUTE;
import main.content.values.parameters.PARAMETER;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class BattleOptionManager<E extends Battle> extends BattleHandler<E> {

    public static   float CHEAT_MODIFIER = 1.0f;
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

    public BattleOptions getOptions() {
        return options;
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

    public void applyDifficulty(Unit unit) {
        if (unit.isMine()){
            return;
        }
        if (unit.isNeutral()){
            return;
        }
        if (!unit.isEnemyTo(game.getPlayer(true)))
            return;
        PARAMETER[] attrs = DC_ContentValsManager.getFinalAttributes().toArray(
         new PARAMETER[ATTRIBUTE.values().length]);
        unit.modifyParamByPercent(attrs, getDifficulty().getAttributePercentage()-100);

        attrs = DC_ContentValsManager.getMasteryScores() .toArray(
         new PARAMETER[ATTRIBUTE.values().length]);
        unit.modifyParamByPercent(attrs, getDifficulty().getMasteryPercentage()-100);

        // sight,
    }
}