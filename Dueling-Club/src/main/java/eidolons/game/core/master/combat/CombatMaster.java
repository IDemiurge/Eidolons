package eidolons.game.core.master.combat;

import eidolons.entity.active.DC_ActionManager;
import eidolons.game.battlecraft.logic.battlefield.DC_GraveyardManager;
import eidolons.entity.active.DC_ActiveObj;
import main.game.ai.BfAnalyzer;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.damage.ArmorMaster;
import main.game.bf.GraveyardManager;
import main.game.bf.MovementManager;
import eidolons.game.core.GenericTurnManager;
import eidolons.game.core.PtsTurnManager;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.game.DC_Game;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;

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
    private DC_ActionManager actionManager;
    private BfAnalyzer bfAnalyzer;
    private boolean chancesOff;
    private boolean diceAverage;
    private boolean rollsAverage;
    private boolean fullManualControl;

    public CombatMaster(DC_Game game) {
        armorMaster = new ArmorMaster(false);
        armorSimulator = new ArmorMaster(true);
        attackMaster = new DC_AttackMaster(game);
        actionManager = new DC_ActionManager(game);
        turnManager = DC_Engine.isAtbMode() ? new AtbTurnManager(game) : new PtsTurnManager(game);
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

    public BfAnalyzer getBfAnalyzer() {
        return bfAnalyzer;
    }

    public void setBfAnalyzer(BfAnalyzer bfAnalyzer) {
        this.bfAnalyzer = bfAnalyzer;
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

    public boolean isActionBlocked(DC_ActiveObj activeObj) {
        return false;
    }

    public boolean isFullManualControl() {
        return OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.MANUAL_CONTROL);
    }


}