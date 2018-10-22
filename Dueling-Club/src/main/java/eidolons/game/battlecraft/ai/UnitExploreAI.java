package eidolons.game.battlecraft.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior.BEHAVIOR_METHOD;

import java.util.List;

/**
 * Created by JustMe on 10/17/2018.
 */
public class UnitExploreAI {


    Unit unit;
    private boolean pathBlocked;
    private float explorationTimePassed;
    private float explorationTimeOfLastAction;
    private float explorationTimeOfModeEffect;
    private float explorationTimeOfRegenEffects;
    private float explorationMoveSpeedMod = 1;
    private boolean autoFollow; // re


    AiBehavior activeBehavior;
    List<AiBehavior> behaviors;

    BEHAVIOR_METHOD method;

    public void act(float delta) {
        activeBehavior = updateBehavior();

        activeBehavior.act(delta);
    }

    private AiBehavior updateBehavior() {
        return behaviors.get(0);
    }

    public UnitExploreAI(Unit unit) {
        this.unit = unit;

    }

    public AiBehavior getActiveBehavior() {
        return activeBehavior;
    }

    public void setActiveBehavior(AiBehavior activeBehavior) {
        this.activeBehavior = activeBehavior;
    }

    public List<AiBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<AiBehavior> behaviors) {
        this.behaviors = behaviors;
    }

    public BEHAVIOR_METHOD getMethod() {
        return method;
    }

    public void setMethod(BEHAVIOR_METHOD method) {
        this.method = method;
    }

    public boolean isPathBlocked() {
        return pathBlocked;
    }

    public void setPathBlocked(boolean pathBlocked) {
        this.pathBlocked = pathBlocked;
    }

    public float getExplorationTimePassed() {
        return explorationTimePassed;
    }

    public void setExplorationTimePassed(float explorationTimePassed) {
        this.explorationTimePassed = explorationTimePassed;
    }

    public float getExplorationTimeOfLastAction() {
        return explorationTimeOfLastAction;
    }

    public void setExplorationTimeOfLastAction(float explorationTimeOfLastAction) {
        this.explorationTimeOfLastAction = explorationTimeOfLastAction;
    }

    public float getExplorationTimeOfModeEffect() {
        return explorationTimeOfModeEffect;
    }

    public void setExplorationTimeOfModeEffect(float explorationTimeOfModeEffect) {
        this.explorationTimeOfModeEffect = explorationTimeOfModeEffect;
    }

    public float getExplorationTimeOfRegenEffects() {
        return explorationTimeOfRegenEffects;
    }

    public void setExplorationTimeOfRegenEffects(float explorationTimeOfRegenEffects) {
        this.explorationTimeOfRegenEffects = explorationTimeOfRegenEffects;
    }

    public boolean isAutoFollow() {
        return autoFollow;
    }

    public void setAutoFollow(boolean autoFollow) {
        this.autoFollow = autoFollow;
    }

    public float getExplorationMoveSpeedMod() {
        return explorationMoveSpeedMod;
    }

    public void setExplorationMoveSpeedMod(float explorationMoveSpeedMod) {
        this.explorationMoveSpeedMod = explorationMoveSpeedMod;
    }

}
