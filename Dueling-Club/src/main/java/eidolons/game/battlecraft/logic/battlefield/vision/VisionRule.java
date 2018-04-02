package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.ConcealmentRule;
import eidolons.game.battlecraft.rules.mechanics.IlluminationRule;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.test.debug.DebugMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;

/**
 * Created by JustMe on 4/1/2018.
 * <p>
 * what are the annoying cases now?
 * <p>
 * shifting outlines - back and forth
 * especially on BF
 * new outline get()
 * <p>
 * VIS LEVEL
 * <p>
 * SIGHT
 * <p>
 * PLAYER STATUS
 * <p>
 * use cases:
 * <p>
 * AI
 * Targeting
 * Information (examine/ tooltip)
 * Location
 * Sneak Attacks
 * Aggro
 */
public class VisionRule {
    VisionMaster master;
    VisionController controller;

    public VisionRule(VisionMaster master) {
        this.master = master;
        this.controller = master.getVisionController();
    }

    public static boolean isSightInfoAvailable(BattleFieldObject observer) {
        if (observer.isMine())
            return true;
        return observer.getPlayerVisionStatus() == PLAYER_VISION.DETECTED;
    }

    public VISIBILITY_LEVEL visibility(Unit source, DC_Obj object) {
        UNIT_VISION sight = controller.getUnitVisionMapper().get(source, object);
        boolean landmark = false;
        if (object instanceof BattleFieldObject) {
            landmark = ((BattleFieldObject) object).isWall() || ((BattleFieldObject) object).isLandscape();
        }

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
                if (object.isWall()) {
                    if (object.isDetected(source.getOwner())) {
                        return PLAYER_VISION.KNOWN;
                    }
                }
            case UNSEEN:
                hide(source, object);
                return PLAYER_VISION.INVISIBLE;
//                case VAGUE_OUTLINE:
//                    break;
        }

        return null;
    }

    public boolean isDisplayedOnGrid(Unit source, BattleFieldObject object) {
        if (object.isMine())
            return true;
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
        controller.getDetectionMapper().set(source.getOwner(), object, true);
    }

    private void hide(Unit source, BattleFieldObject object) {
        controller.getDetectionMapper().set(source.getOwner(), object, false);
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
            return master.getOutlineMaster().getOutline(object, source);
        }

        if (
         controller.getDetectionMapper().get(source.getOwner(), object) ||
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
}
