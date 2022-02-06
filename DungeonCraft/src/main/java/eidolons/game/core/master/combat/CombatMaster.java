package eidolons.game.core.master.combat;

import eidolons.entity.mngr.action.ActionInitializer;
import eidolons.entity.mngr.action.DC_ActionManager;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.DC_GraveyardManager;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.damage.armor.ArmorMaster;
import eidolons.game.core.GenericTurnManager;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.GraveyardManager;
import main.game.bf.MovementManager;
import main.system.launch.Flags;

/**
 * Created by JustMe on 6/2/2017.
 */
public class CombatMaster {

    protected DC_AttackMaster attackMaster;
    protected ArmorMaster armorMaster;
    protected ArmorMaster armorSimulator;
    protected GenericTurnManager turnManager;
    protected MovementManager movementManager;
    protected GraveyardManager graveyardManager;
    private final DC_ActionManager actionManager;
    private boolean chancesOff;
    private boolean diceAverage;
    private boolean rollsAverage;
    private boolean fullManualControl;

    public CombatMaster(DC_Game game) {
        armorMaster = new ArmorMaster(false, game);
        armorSimulator = new ArmorMaster(true, game);
        attackMaster = new DC_AttackMaster(game);
        actionManager = new ActionInitializer(game);
        turnManager =  new AtbTurnManager(game) ;
        movementManager = new DC_MovementManager(game);
        graveyardManager = new DC_GraveyardManager(game);

    }

    public DC_ActionManager getActionManager() {
        return actionManager;
    }

    public DC_AttackMaster getAttackMaster() {
        return attackMaster;
    }

    public ArmorMaster getArmorMaster() {
        return armorMaster;
    }

    public ArmorMaster getArmorSimulator() {
        return armorSimulator;
    }

    public GenericTurnManager getTurnManager() {
        return turnManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }

    public GraveyardManager getGraveyardManager() {
        return graveyardManager;
    }



    public boolean isChancesOff() {
        return chancesOff;
    }

    public void setChancesOff(boolean chancesOff) {
        this.chancesOff = chancesOff;
    }

    public boolean isDiceAverage() {
        return diceAverage;
    }

    public void setDiceAverage(boolean diceAverage) {
        this.diceAverage = diceAverage;
    }

    public boolean isRollsAverage() {
        return rollsAverage;
    }

    public void setRollsAverage(boolean rollsAverage) {
        this.rollsAverage = rollsAverage;
    }

    public boolean isActionBlocked(ActiveObj activeObj) {
        return false;
    }

    public boolean isFullManualControl() {
        if (Flags.isIDE()) {
            return true;
        }
        return  OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.MANUAL_CONTROL);
    }


}
