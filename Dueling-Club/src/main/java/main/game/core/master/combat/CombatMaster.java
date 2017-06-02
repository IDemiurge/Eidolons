package main.game.core.master.combat;

import main.entity.active.DC_ActionManager;
import main.entity.active.DC_ActiveObj;
import main.game.ai.BfAnalyzer;
import main.game.battlecraft.logic.battlefield.DC_GraveyardManager;
import main.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import main.game.battlecraft.rules.combat.damage.ArmorMaster;
import main.game.bf.GraveyardManager;
import main.game.bf.MovementManager;
import main.game.core.DC_TurnManager;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 6/2/2017.
 */
public class CombatMaster {

    private  DC_ActionManager actionManager;
    protected DC_AttackMaster attackMaster;
    protected ArmorMaster armorMaster;
    protected ArmorMaster armorSimulator;
    protected DC_TurnManager turnManager;
    protected MovementManager movementManager;
    protected GraveyardManager graveyardManager;
    private BfAnalyzer bfAnalyzer;
    private boolean chancesOff;
    private boolean diceAverage;
    private boolean rollsAverage;

    public CombatMaster(DC_Game  game) {
        armorMaster = new ArmorMaster(false);
        armorSimulator = new ArmorMaster(true);
        attackMaster = new DC_AttackMaster(game);
        actionManager = new DC_ActionManager(game);
        turnManager = new DC_TurnManager(game);
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

    public DC_TurnManager getTurnManager() {
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

    public void setChancesOff(boolean chancesOff) {
        this.chancesOff = chancesOff;
    }

    public boolean isChancesOff() {
        return chancesOff;
    }

    public void setDiceAverage(boolean diceAverage) {
        this.diceAverage = diceAverage;
    }

    public boolean isDiceAverage() {
        return diceAverage;
    }

    public void setRollsAverage(boolean rollsAverage) {
        this.rollsAverage = rollsAverage;
    }

    public boolean isRollsAverage() {
        return rollsAverage;
    }

    public boolean isActionBlocked(DC_ActiveObj activeObj) {
            return false;
        }
}
