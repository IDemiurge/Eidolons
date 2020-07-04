package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.battlecraft.rules.mechanics.ConcealmentRule;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.netherflame.main.death.ShadowMaster;
import eidolons.game.netherflame.main.death.ShadowVisionMaster;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.Obj;
import main.system.auxiliary.secondary.Bools;
import main.system.math.PositionMaster;

import java.util.HashSet;
import java.util.Set;

public class VisionRule {
    private static Boolean playerUnseenMode;
    VisionMaster master;
    VisionController controller;

    public VisionRule(VisionMaster master) {
        this.master = master;
        this.controller = master.getVisionController();
    }

    public static Boolean getPlayerUnseenMode() {
        if (playerUnseenMode == null) {
            // if (CoreEngine.isFastMode())
            //     playerUnseenMode = true;
            // else
            playerUnseenMode = OptionsMaster.getGameplayOptions().
                    getBooleanValue(GAMEPLAY_OPTION.GHOST_MODE);
        }
        return playerUnseenMode;
    }

    public static void setPlayerUnseenMode(Boolean playerUnseenMode) {
        VisionRule.playerUnseenMode = playerUnseenMode;
    }

    public static boolean isSightInfoAvailable(BattleFieldObject observer) {
        if (observer.isMine())
            return true;
        return observer.getPlayerVisionStatus() == PLAYER_VISION.DETECTED;
    }


    public void fullReset(Unit... observers) {
        DC_Cell[][] array = master.getGame().getObjMaster().getCells();
        Set<Unit> filteredObserver = new HashSet<>();
        for (Unit observer : observers) {
            if (isObserverResetRequired(observer))
                filteredObserver.add(observer);
        }
        int offsetX = master.getGame().getModule().getX();
        int offsetY = master.getGame().getModule().getY();
        int x2 = master.getGame().getModule().getX2();
        int y2 = master.getGame().getModule().getY2();
        for (Unit observer : filteredObserver) {
            //            DequeImpl<Coordinates> coordinates = new DequeImpl(master.getGame().getCoordinates());
            //                    master.getSightMaster().getVisibleCoordinatesSecondary(observer);
            // main.system.auxiliary.log.LogMaster.log(1, "Vision Reset for " + observer);

            for (int i = offsetX; i < x2; i++) {
                for (int j = offsetY; j < y2; j++) {
                    BattleFieldObject[] objects = master.getGame().getObjMaster().getObjects(
                            i, j);
                    if (objects == null) {
                        objects = master.getGame().getObjMaster().getObjects(
                                i, j, false);
                    }
                    DC_Cell cell = array[i][j];

                    if (!isResetRequired(observer, cell))
                        continue;

                    if (isGammaResetRequired(observer, cell)) {
                        cell.setGamma(observer, master.getGammaMaster().getGamma(
                                observer, cell));
                    }
                    master.getSightMaster().resetUnitVision(observer, cell);
                    //                    log(LOG_CHANNEL.VISIBILITY_DEBUG, cell.getNameAndCoordinate()+
                    //                            " - Vision reset: " + cell.getVisionInfo());

                    for (BattleFieldObject sub : objects) {
                        //check ignore?
                        if (isObjResetRequired(observer, sub))
                            resetVision(observer, sub);

                    }
                }
            }
        }


    }

    private boolean newVision() {
        return true;
    }

    private void resetVision(Unit observer, BattleFieldObject sub) {
        if (isGammaResetRequired(observer, sub))
            sub.setGamma(observer, master.getGammaMaster().getGamma(observer, sub));
        master.getSightMaster().resetUnitVision(observer, sub);
        //                        controller.getUnitVisionMapper()
        //                        sub.setUnitVisionStatus(observer, master.getUnitVisibilityStatus(sub, observer));
        controller.getVisibilityLevelMapper().set(observer, sub, visibility(observer, sub));
        controller.getOutlineMapper().set(observer, sub, outline(observer, sub));
        controller.getPlayerVisionMapper().set(observer.getOwner(), sub, playerVision(observer, sub));
        //        log(LOG_CHANNEL.VISIBILITY_DEBUG, sub.getNameAndCoordinate() + " - Vision reset: " + sub.getVisionInfo());
    }

    private boolean isObserverResetRequired(Unit observer) {
        if (observer.isDead() || observer.isUnconscious())
            return false;
        //TODO not really but...
        if (observer.isMine()) {
            return true;
        }
        //        if (observer.isPlayerCharacter())
        //            return true;
        //        if (observer.isScion())
        //            return true;
        if (observer.isUnconscious())
            return false;
        else if (getPlayerUnseenMode()) {
            return false;
        }
        double dst = PositionMaster.getExactDistance(observer, Eidolons.getMainHero());
        //         dst >          Eidolons.getMainHero().getMaxVisionDistance()&&
        return !(dst > observer.getMaxVisionDistance());
    }

    private boolean isGammaResetRequired(Unit observer, DC_Obj sub) {
        return true;
    }

    private boolean isObjResetRequired(Unit observer, DC_Obj sub) {
        if (observer == ShadowMaster.getShadowUnit()) {
            return true;
        }

        //        if (sub instanceof Unit) TODO I really think enemies don't need to know anything else....
        if (!observer.isPlayerCharacter())
            return observer.isHostileTo(sub.getOwner());

        if (sub.isDead())
            return false;
        return !sub.isVisibilityOverride();
    }

    public boolean isResetRequiredSafe(Unit observer, DC_Obj cell) {
        return isResetRequired(observer, cell, 1.2f);
    }

    public boolean isResetRequired(Unit observer, DC_Obj cell) {
        return isResetRequired(observer, cell, 1f);
    }

    public boolean isResetRequired(Unit observer, DC_Obj cell, float dstCoef) {
        ////TODO should we reset if observer is dead/unc.?
        if (observer.isMine()) {
            if (observer.getGame().isDebugMode()) {
                return true;
            }
            if (PositionMaster.getExactDistance(observer, cell) >
                    1 + observer.getMaxVisionDistance() * dstCoef) {
                if (master.getGame().getGrid().isWallCoordinate(cell.getCoordinates())) {
                    Boolean aBoolean = observer.getSeenMapper().get(cell);
                    if (aBoolean == null) {
                        return false;
                    }
                    return aBoolean;

                }
                return false;
            }
            return true;
        } else {

            if (observer.isUnconscious())
                return false;
            if (getPlayerUnseenMode()) {
                return false;
            }
            return !(PositionMaster.getExactDistance(observer, cell) > observer.getMaxVisionDistance() * dstCoef);
        }
    }

    public VISIBILITY_LEVEL visibility(Unit source, BattleFieldObject object) {
        if (Cinematics.ON) {
            if (object.isRevealed()) {
                return VISIBILITY_LEVEL.CLEAR_SIGHT;
            }
        }
        if (object.isHidden()) {
            return VISIBILITY_LEVEL.UNSEEN;
        }
        UNIT_VISION sight = controller.getUnitVisionMapper().get(source, object);
        boolean landmark = object instanceof Structure;
        //        if (object instanceof BattleFieldObject) {
        //            landmark = ((BattleFieldObject) object).isWall() || ((BattleFieldObject) object).isLandscape();
        //        }

        if (source == ShadowMaster.getShadowUnit()) {
            return ShadowVisionMaster.getVisibility(sight, object);

        }
        if (master.getGame().getRules().getStealthRule().
                checkInvisible(source.getOwner(), object))
            return VISIBILITY_LEVEL.UNSEEN;

        switch (sight) {
            case IN_PLAIN_SIGHT:
                return VISIBILITY_LEVEL.CLEAR_SIGHT;
            case BLOCKED:
                //if
                return VISIBILITY_LEVEL.BLOCKED;
            case IN_SIGHT:
                if (landmark)
                    if (controller.getDetectionMapper().get(source.getOwner(), object)) {
                        return VISIBILITY_LEVEL.CONCEALED;
                    }

                return VISIBILITY_LEVEL.OUTLINE;
            case BEYOND_SIGHT:

                if (landmark) {
                    if (controller.getDetectionMapper().get(source.getOwner(), object)) {
                        return VISIBILITY_LEVEL.CONCEALED;
                    }
                }
        }
        return VISIBILITY_LEVEL.UNSEEN;


    }

    public PLAYER_VISION playerVision(Unit source, BattleFieldObject object) {
        if (object.isWall())
            return PLAYER_VISION.DETECTED;

        VISIBILITY_LEVEL visibilityLevel = controller.getVisibilityLevelMapper().
                get(source, object);
        switch (visibilityLevel) {
            case CLEAR_SIGHT:
                reveal(source, object);
                return PLAYER_VISION.DETECTED;
            case OUTLINE:
                alert(source, object);
                return PLAYER_VISION.UNKNOWN;
            case CONCEALED: //'for of war?'
                return PLAYER_VISION.KNOWN;
            case BLOCKED:
                if (source.isMine())
                            if (PositionMaster.getExactDistance(object, source) <= 3) {
                                object.setDetectedByPlayer(true);
                                return PLAYER_VISION.UNKNOWN;
                            }
                        break;
            case UNSEEN:
                hide(source, object);
                return PLAYER_VISION.INVISIBLE;
            //                case VAGUE_OUTLINE:
            //                    break;
        }
        //TODO why not do Hide?
        return PLAYER_VISION.INVISIBLE;
    }


    public boolean isDisplayedOnGrid(Unit source, BattleFieldObject object) {
        if (object.isHidden())
            return false;
        if (object.isMine())
            return true;
        if (Cinematics.ON) {
            if (!object.isLightEmitter())
                if (object.isOverlaying()) {
                    return false;
                }
        }
        if (object.isOverlaying()) {
            return controller.getPlayerVisionMapper().get(source.getOwner(), object) ==
                    PLAYER_VISION.DETECTED;
        }
        return controller.getPlayerVisionMapper().get(source.getOwner(), object) != PLAYER_VISION.INVISIBLE;
    }

    public boolean isExamineAllowed(Unit source, BattleFieldObject object) {
        PLAYER_VISION vision = controller.getPlayerVisionMapper().get(source.getOwner(), object);
        return vision != PLAYER_VISION.INVISIBLE && vision != PLAYER_VISION.UNKNOWN;
    }

    private void reveal(Unit source, BattleFieldObject object) {
        Boolean prev = controller.getDetectionMapper().get(source.getOwner(), object);
        if (Bools.isTrue(prev)) {
            return;
        }
        if (source == object) {
            return;
        }
        if (source.getOwner() == object.getOwner()) {
            return;
        }
        controller.getDetectionMapper().set(source.getOwner(), object, true);
        master.getEngagementHandler().detected(source, object);

    }

    private void alert(Unit source, BattleFieldObject object) {

        if (object instanceof Structure) {
            if (object.isLandscape() || object.isWall()) {
                return;
            }
            if (object.checkClassification(UnitEnums.CLASSIFICATIONS.HUMANOID)) {
                master.getEngagementHandler().alert(source, object);
            } else if (object.checkClassification(UnitEnums.CLASSIFICATIONS.TALL)) {
                master.getEngagementHandler().alert(source, object);
            } else
                return;
        }
        master.getEngagementHandler().alert(source, object);
    }

    private void hide(Unit source, BattleFieldObject object) {
        if (Bools.isFalse(controller.getDetectionMapper()
                .get(source.getOwner(), object)))
            return;
        if (object.isWall() || object.isLightEmitter()) {
            return;
        }
        master.getEngagementHandler().lostSight(source, object);
        controller.getLastSeenMapper().set(source.getOwner(), object,
                object.getLastCoordinates());
        if (object instanceof Structure) {
            return;
        }
        controller.getDetectionMapper().set(source.getOwner(), object, false);
    }

    public OUTLINE_TYPE outline(Unit source, BattleFieldObject object) {
        if (object.getGame().isSimulation() || object.getGame().isDebugMode()) {
            return null;
        }
        if (object.isWall()) {
            return null;
        }
        if (object instanceof Entrance) {
            return null;
        }

        VISIBILITY_LEVEL visibility = controller.getVisibilityLevelMapper().get(source, object);

        if (visibility == VISIBILITY_LEVEL.OUTLINE) {
            OUTLINE_TYPE outline = master.getOutlineMaster().getOutline(object, source);
            if (outline == null) {
                if (source.isMine())
                    //TODO QUICK FIX - NOW ENEMIES HAVE 100% CLEARSHOT AND WILL AGGRO IF THIS WORKS FOR THEM
                    object.setVisibilityLevel(source, VISIBILITY_LEVEL.CLEAR_SIGHT);
            }
            return outline;
        }

        if (controller.getDetectionMapper().get(source.getOwner(), object) ||
                visibility == VISIBILITY_LEVEL.CLEAR_SIGHT) {
            if (ConcealmentRule.isConcealed(source, object)) {
                return OUTLINE_TYPE.DEEPER_DARKNESS;
            }
            if (Illumination.isConcealed(source, object)) {
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
            return null;

        }
        return null;
    }


    public boolean isAggro(Unit hero, Unit unit) {
        if (hero.isDead()) {
            return false;
        }
        UNIT_VISION vision = unit.getGame().getVisionMaster().getSightMaster().getUnitVisibilityStatus(hero, unit);
        switch (vision) {

            case IN_PLAIN_SIGHT:
                if (!hero.isSneaking())//TODO DC Review - how does stealth work anyway?
                    return true;
            case IN_SIGHT:
            case CONCEALED:
                break;
            case BEYOND_SIGHT:
            case BLOCKED:
                return false;
        }
        if (hero.isSneaking()) {
            //add chance? not right...
            //                apply spotted?
            return isResetRequired(unit, hero, 0.25f);
        }
        return isResetRequired(unit, hero, 0.5f);
    }


    public void togglePlayerUnseenMode() {
        playerUnseenMode = !getPlayerUnseenMode();
    }

    public void resetIgnore() {
        float dstCoef = 1.5f;
        Unit observer = Eidolons.getMainHero();
        for (Obj c : master.getGame().getCells()) {
            DC_Obj cell = (DC_Obj) c;
            cell.setResetIgnored(true);
            if (master.getGame().getObjectByCoordinate(cell.getCoordinates()) instanceof Structure) {
                if (((Structure) master.getGame().getObjectByCoordinate(cell.getCoordinates())).isWall()) {
                    cell.setResetIgnored(false);
                    continue;
                }
            }
            //            for (Unit observer : master.getGame().getPlayer(true).collectControlledUnits_()) {
            if (PositionMaster.getExactDistance(observer, cell) < observer.getMaxVisionDistance() * dstCoef) {
                cell.setResetIgnored(false);
            }
            //            }
            for (Unit unit : master.getGame().getUnitsForCoordinates(c.getCoordinates())) {
                unit.setResetIgnored(cell.isResetIgnored());
            }
        }
    }
}
