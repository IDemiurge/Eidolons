package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowVisionMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.battlecraft.rules.mechanics.ConcealmentRule;
import eidolons.game.battlecraft.rules.mechanics.IlluminationRule;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.boss.entity.BossUnit;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.test.debug.DebugMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster;

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
            if (CoreEngine.isFastMode())
                playerUnseenMode = true;
            else
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
        BattleFieldObject[][][] array = master.getGame().getMaster().getObjCells();
        Set<Unit> filteredObserver = new HashSet<>();
        for (Unit observer : observers) {
            if (isObserverResetRequired(observer))
                filteredObserver.add(observer);
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                Set<BattleFieldObject> objects =
                        master.getGame().getMaster().getObjectsOnCoordinate(
                                Coordinates.get(i, j), false);
//                 master.getGame().getMaster().getObjects(i, j, true);
                DC_Cell cell = master.getGame().getCellByCoordinate(Coordinates.get(i, j));
                if (cell == null)
                    continue;
                for (Unit observer : filteredObserver) {
                    if (!isResetRequired(observer, cell))
                        continue;
                    if (isGammaResetRequired(observer, cell)) {
                        cell.setGamma(observer, master.getGammaMaster().getGamma(
                                observer, cell));
                    }
                    master.getSightMaster().resetUnitVision(observer, cell);
                    for (BattleFieldObject sub : objects) {
                        //check ignore?
                        if (!isObjResetRequired(observer, sub))
                            continue;
                        if (isGammaResetRequired(observer, sub))
                            sub.setGamma(observer, master.getGammaMaster().getGamma(observer, sub));
                        //                    master.getSightMaster().resetSightStatuses(observer);
                        master.getSightMaster().resetUnitVision(observer, sub);
                        //                        controller.getUnitVisionMapper()
                        //                        sub.setUnitVisionStatus(observer, master.getUnitVisibilityStatus(sub, observer));
                        controller.getVisibilityLevelMapper().set(observer, sub, visibility(observer, sub));
                        controller.getOutlineMapper().set(observer, sub, outline(observer, sub));
                        controller.getPlayerVisionMapper().set(observer.getOwner(), sub, playerVision(observer, sub));
                    }
                }
            }
        }

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
        if (
//         dst >          Eidolons.getMainHero().getMaxVisionDistance()&&
                dst > observer.getMaxVisionDistance()) {
            return false;
        }

        return true;
    }

    private boolean isGammaResetRequired(Unit observer, DC_Obj sub) {
        return true;
    }

    private boolean isObjResetRequired(Unit observer, DC_Obj sub) {
        if (sub instanceof BossUnit) {
            return true;
        }
        if (observer == ShadowMaster.getShadowUnit()) {
            return true;
        }

//        if (sub instanceof Unit) TODO I really think enemies don't need to know anything else....
        if (!observer.isPlayerCharacter())
            return observer.isHostileTo(sub.getOwner());

        if (sub.isDead())
            return false;
        if (sub.isVisibilityOverride())
            return false;
        return true;
    }

    public boolean isResetRequiredSafe(Unit observer, DC_Obj cell) {
        return isResetRequired(observer, cell, 1.2f);
    }

    public boolean isResetRequired(Unit observer, DC_Obj cell) {
        return isResetRequired(observer, cell, 1f);
    }

    public boolean isResetRequired(Unit observer, DC_Obj cell, float dstCoef) {

        //changed position
        //is close enough
        //is hostile

        if (observer.isPale() != cell.isPale())
            return false;

        if (ExplorationMaster.isExplorationOn())
            if (cell.isResetIgnored()) {
                return false;
            }
        if (cell instanceof BossUnit) {
            return true;
        }
        if (observer.isDead() || observer.isUnconscious())
            return false;
        if (observer.isMine()) {
            if (observer.getGame().isDebugMode()) {
                return true;
            }
            if (PositionMaster.getExactDistance(observer, cell) >
                    1 + observer.getMaxVisionDistance() * dstCoef) {
                if (master.getGame().getObjectByCoordinate(cell.getCoordinates()) instanceof Structure) {
                    Structure o = ((Structure) master.getGame().getObjectByCoordinate(cell.getCoordinates()));
                    if (o.isWall()) {
                        if (o.isPlayerDetected()) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return true;
        }
        if (observer.isUnconscious())
            return false;
        if (getPlayerUnseenMode()) {
            return false;
        }
        if (PositionMaster.getExactDistance(observer, cell) > observer.getMaxVisionDistance() * dstCoef) {
            return false;
        }
        return true;
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

        if (object instanceof BossUnit) {
            return VISIBILITY_LEVEL.CLEAR_SIGHT;
        }
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
        //            TODO case CONCEALED:
        //                break;

    }

    public PLAYER_VISION playerVision(Unit source, BattleFieldObject object) {
        if (DebugMaster.isOmnivisionOn()) {
            if (source.isMine()) {
                return PLAYER_VISION.DETECTED;

            }
        }
        //        if (object instanceof Unit) { TODO now in visibility!
        //            if (StealthRule.checkInvisible(object)) {
        //                return (PLAYER_VISION.INVISIBLE);
        //            }
        //        }

        VISIBILITY_LEVEL visibilityLevel = controller.getVisibilityLevelMapper().
                get(source, object);
        switch (visibilityLevel) {
            case CLEAR_SIGHT:
                reveal(source, object);
                return PLAYER_VISION.DETECTED;
            case OUTLINE:
                return PLAYER_VISION.UNKNOWN;
            case CONCEALED:
                return PLAYER_VISION.KNOWN;
            case BLOCKED:
                if (source.isMine())
                    if (object.isWall()) {
                        if (object.isDetectedByPlayer()) {
                            return PLAYER_VISION.KNOWN;
                        } else {
//                        main.system.auxiliary.log.LogMaster.log(1,"BLOCKED " +
//                         object + " DETECTED at" +
//                         object.getCoordinates() );
                            if (PositionMaster.getExactDistance(object, source) <= 3) {
                                object.setDetectedByPlayer(true);
                                return PLAYER_VISION.UNKNOWN;
                            }
                        }
                        break;
                    }
            case UNSEEN:
                hide(source, object);
                return PLAYER_VISION.INVISIBLE;
            //                case VAGUE_OUTLINE:
            //                    break;
        }

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
        if (controller.getPlayerVisionMapper().get(source.getOwner(), object)
                == PLAYER_VISION.INVISIBLE)
            return false;
        return true;
    }

    public boolean isExamineAllowed(Unit source, BattleFieldObject object) {
        PLAYER_VISION vision = controller.getPlayerVisionMapper().get(source.getOwner(), object);
        if (vision == PLAYER_VISION.INVISIBLE || vision == PLAYER_VISION.UNKNOWN)
            return false;
        return true;
    }

    private void reveal(Unit source, BattleFieldObject object) {
        if (Bools.isTrue(controller.getDetectionMapper()
                .get(source.getOwner(), object)))
            return;
        Boolean prev = controller.getDetectionMapper().get(source.getOwner(), object);
        if (Bools.isTrue(prev)) {
            return;
        }
        controller.getDetectionMapper().set(source.getOwner(), object, true);
        if (isDetectionLogged(source, object)) {
                master.getGame().getLogManager().logReveal(source, object);
        }
        if (isDetectionSoundOn(source, object)) {
            if (source.getGame().isStarted())
                if (!VisionManager.isCinematicVision())
//            if (!source.getGame().isFootageMode())
                    if (RandomWizard.chance(33)) {
                        DC_SoundMaster.playEffectSound(SoundMaster.SOUNDS.ALERT, source);
                    } else {
                        DC_SoundMaster.playEffectSound(SoundMaster.SOUNDS.SPOT, source);
                    }
        }
    }

    private boolean isDetectionSoundOn(Unit source, BattleFieldObject object) {
        if (source.isPlayerCharacter()) {
            if (!object.isSneaking())
                if (!object.isDisabled())
                    if (!object.isAlliedTo(source.getOwner())) {
                        return true;
                    }
        }
        return false;
    }

    private boolean isDetectionLogged(Unit source, BattleFieldObject object) {
        if (object instanceof Structure)
            return false;

        if (source != object)
            if (source.isMine())
                if (source.isHostileTo(object.getOwner()))
                    return true;
        return false;
    }

    private void hide(Unit source, BattleFieldObject object) {
        if (Bools.isFalse(controller.getDetectionMapper()
                .get(source.getOwner(), object)))
            return;
        if (object.isWall()) {
            return;
        }
        controller.getLastSeenMapper().set(source.getOwner(), object,
                object.getLastCoordinates());

        controller.getDetectionMapper().set(source.getOwner(), object, false);
        if (isDetectionLogged(source, object))
            master.getGame().getLogManager().logHide(source, object);
    }

    public OUTLINE_TYPE outline(Unit source, BattleFieldObject object) {
        if (DebugMaster.isOmnivisionOn()) {
            if (source.isMine())
                return null;
        }
        if (object.getGame().isSimulation() || object.getGame().isDebugMode()) {
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
            if (IlluminationRule.isConcealed(source, object)) {
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
        if (hero.isScion())
            return false; //TODO  no new aggro there

//        if (hero.isSneaking()) {
//            return false;
//        }

        UNIT_VISION vision = unit.getGame().getVisionMaster().getSightMaster().getUnitVisibilityStatus(hero, unit);
        switch (vision) {

            case IN_PLAIN_SIGHT:
                if (!hero.isSneaking())//TODO IGG HACK
                    return true;
            case IN_SIGHT:
                break;
            case BEYOND_SIGHT:
            case BLOCKED:
                return false;
            case CONCEALED:
                break;
        }
//        VISIBILITY_LEVEL visibility =    controller.getVisibilityLevelMapper().getVar(unit, hero);
//        switch (visibility) {
//            case CLEAR_SIGHT:
//                break;
//            case UNSEEN:
//                if (!isResetRequired(unit, hero, 0.5f))
//                    return false;
//            case OUTLINE:
//            case VAGUE_OUTLINE:
//            case CONCEALED:
//                if (!ExplorationMaster.isExplorationOn())
//                    break;
//                else
//                    return false;
//            case BLOCKED:
//                return false;
//        }
//        if (controller.getVisibilityLevelMapper().getVar(unit, hero) == VISIBILITY_LEVEL.CLEAR_SIGHT
//         || !ExplorationMaster.isExplorationOn()
//         &&  controller.getVisibilityLevelMapper().getVar(unit, hero) == VISIBILITY_LEVEL.BLOCKED
//         ) {

        if (hero.isSneaking()) {
            //add chance? not right...
            if (isResetRequired(unit, hero, 0.25f)) {
//                apply spotted?
                return true;
            }
            return false; //TODO IGG HACK
        }
        if (isResetRequired(unit, hero, 0.5f))
            return true;


        return false;
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
