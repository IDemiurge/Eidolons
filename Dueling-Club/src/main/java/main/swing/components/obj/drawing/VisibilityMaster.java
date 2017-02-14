package main.swing.components.obj.drawing;

import main.ability.conditions.special.ClearShotCondition;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.CONTENT_CONSTS.RACE;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.game.logic.battle.turn.DC_TurnManager;
import main.rules.mechanics.ConcealmentRule;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.math.PositionMaster;
import main.test.debug.DebugMaster;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class VisibilityMaster {

	/*
     * spell illumination identification level
	 * 
	 * out of sight range -> 'vague'? how to number this?
	 * 
	 * what about stacked units? 2+ -> vague outlineS ?
	 */

    private static final String TARGET = " t";
    private static final String INFO = " s";

    private static List<OUTLINE_HINT> getHints(DC_HeroObj unit, OUTLINE_IMAGE image) {
        List<OUTLINE_HINT> list = new LinkedList<>();
        if (unit.isSmall()) {
            list.add(OUTLINE_HINT.SMALL);
        }
        if (unit.isTall()) {
            list.add(OUTLINE_HINT.TALL);
        }
        if (unit.isHuge()) {
            list.add(OUTLINE_HINT.HUGE);
        }
        if (unit.isShort()) {
            list.add(OUTLINE_HINT.SHORT);
        }
        if (image != null) {
            switch (image) {
                case BEAST:
                    list.add(OUTLINE_HINT.BESTIAL);
                    list.add(OUTLINE_HINT.ANIMAL_LIKE);
                    break;
                case HORROR:
                    list.add(OUTLINE_HINT.ABHORRENT);
                    break;
                case HUMAN:
                    list.add(OUTLINE_HINT.HUMAN_LIKE);
                    break;
                case HUMANLIKE:
                    list.add(OUTLINE_HINT.HUMAN_LIKE);
                    break;
                case HUMANOID:
                    list.add(OUTLINE_HINT.HUMANOID);
                    break;
                case INSECT:
                    list.add(OUTLINE_HINT.INSECTOID);
                    break;
                case MONSTROUS:
                    list.add(OUTLINE_HINT.MONSTROUS);
                    break;
                case MONSTROUS_HUMANOID:
                    list.add(OUTLINE_HINT.MONSTROUS);
                    list.add(OUTLINE_HINT.HUMANOID);
                    break;
                case MULTIPLE:
                    break;
                case QUADRUPED:
                    break;
                case STRUCTURE:
                    break;

            }
        }
        // String hintText = StringMaster.constructStringContainer(list, " ");
        // unit.setProperty(PROPS.HINTS, hintText);
        // String text = "";
        // if (unit.getIntParam(PARAMS.ILLUMINATION) != 0)
        // text += StringMaster.getWellFormattedString("ILLUMINATION - ")
        // + unit.getParam(PARAMS.ILLUMINATION);
        // if (unit.getIntParam(PARAMS.CONCEALMENT) != 0)
        // text += StringMaster.getWellFormattedString(", CONCEALMENT - ")
        // + unit.getParam(PARAMS.CONCEALMENT);
        // hintText += " \nRelative visibility for "
        // + unit.getGame().getManager().getActiveObj().getName() + " :" +
        // unit.getGamma()
        // + StringMaster.wrapInParenthesis(text);

        return list;

    }

    public static String getTooltip(DC_Obj target) {
        if (target.getOutlineType() == null) {
            return null;
        }
        if (target.getOutlineType() == OUTLINE_TYPE.THICK_DARKNESS) {
            return "Impenetrable darkness...";
        }
        if (target.getOutlineType() == OUTLINE_TYPE.BLINDING_LIGHT) {
            return "Blinding light!";
        }
        if (target instanceof DC_HeroObj) {
            return getTooltipForUnit((DC_HeroObj) target);
        }
        return null;
    }

    public static String getTooltipForUnit(DC_HeroObj unit) {
        String hintString = getHintsString(unit);
        String tooltip = StringMaster.getWellFormattedString(unit.getOutlineType().toString()) + " of something "
                + hintString;
        DC_HeroObj activeUnit = DC_Game.game.getManager().getActiveObj();
        if (unit.getOwner().equals(activeUnit.getOwner())) {
            return unit.getToolTip();
        }

        return tooltip;

    }

    public static String getHintsString(DC_HeroObj unit) {
        List<OUTLINE_HINT> hints = getHints(unit, getImageDark(unit));
        String hintString = "";
        for (OUTLINE_HINT hint : hints) {
            hintString += StringMaster.getWellFormattedString(hint.toString()) + " ";
        }
        return hintString;
    }

    // public String getTooltipForType(OUTLINE_TYPE type) {
    // // "vaguely visible" "shining brilliantly" "clear in sight"
    // // "wreathed in shadows"
    // }

    public static Image getDisplayImageForUnit(DC_Obj obj) {
        // construct and cache an Outline obj per unit?
        OUTLINE_TYPE type = obj.getOutlineType();

        if (type == null) {
            return null;
        }
        if (type == OUTLINE_TYPE.BLOCKED_OUTLINE) {
            return getImage(OUTLINE_TYPE.THICK_DARKNESS, obj);
        }
        if ((type == OUTLINE_TYPE.THICK_DARKNESS || type == OUTLINE_TYPE.BLINDING_LIGHT)) {
            return getImage(type, obj);
        }
        if (obj instanceof DC_Cell) {
            return null;
        }
        String outlinePath = "ui\\outlines\\" + type.toString();
        OUTLINE_IMAGE outlineImage;
        if (type == OUTLINE_TYPE.VAGUE_OUTLINE) {
            outlineImage = getImageVague((DC_HeroObj) obj);
        } else {
            outlineImage = getImageDark((DC_HeroObj) obj);
        }
        if (outlineImage != OUTLINE_IMAGE.UNKNOWN) {
            outlinePath += "_" + outlineImage.toString();
        }

        if (obj.isTargetHighlighted()) {
            outlinePath += TARGET;
        } else {
            if (obj.isInfoSelected()) {
                outlinePath += INFO;
            }
        }
        Image image = ImageManager.getImage(outlinePath + ".jpg");
        if (ImageManager.isValidImage(image)) {
            return image;
        }

        image = ImageManager.getImage(outlinePath.replace("_" + outlineImage.toString(), "") + ".jpg");
        if (ImageManager.isValidImage(image)) {
            return image;
        }
        image = ImageManager.getImage(outlinePath.replace(INFO, "").replace(TARGET, "") + ".jpg");
        if (ImageManager.isValidImage(image)) {
            return image;
        }
        return null;
        // DIFFERENTIATE BETWEEN RANGE, CONCEALMENT, ILL AND STEALTH
    }

    public static Image getImage(OUTLINE_TYPE type, DC_Obj unit) {
        String outlinePath = "ui\\outlines\\" + type.toString();
        if (unit.isTargetHighlighted()) {
            outlinePath += TARGET;
        } else {
            if (unit.isInfoSelected()) {
                outlinePath += INFO;
            }
        }
        Image image = ImageManager.getImage(outlinePath + ".jpg");
        if (!ImageManager.isValidImage(image)) {
            image = ImageManager.getImage("ui\\outlines\\" + type.toString() + ".jpg");
        }
        return image;
    }

    private static OUTLINE_IMAGE getImageVague(DC_HeroObj unit) {
        // if (unit.isHuge()) {
        //
        // }
        // if (unit.isShort()) {
        //
        // }
        // if (unit.isSmall()) {
        //
        // }
        // if (unit.isTall()) {
        //
        // }
        if (unit.isWall()) {
            return OUTLINE_IMAGE.WALL;
        }
        return OUTLINE_IMAGE.UNKNOWN;
    }

    private static OUTLINE_IMAGE getImageDark(DC_HeroObj unit) {
        // TODO identify!
        if (unit.isWall()) {
            return OUTLINE_IMAGE.WALL;
        }
        if (unit.checkClassification(CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.BEAST;
        }
        if (unit.checkClassification(CLASSIFICATIONS.HUMANOID)) {
            if (unit.getRace() == RACE.HUMAN || unit.isHero()) {
                return OUTLINE_IMAGE.HUMAN;
            } else if (unit.checkClassification(CLASSIFICATIONS.MONSTER)) {
                return OUTLINE_IMAGE.MONSTROUS_HUMANOID;
            } else {
                return OUTLINE_IMAGE.HUMANLIKE;
            }
        }
        if (unit.checkClassification(CLASSIFICATIONS.INSECT)) {
            return OUTLINE_IMAGE.INSECT;
        }

        if (unit.checkClassification(CLASSIFICATIONS.DEMON)) {
            return OUTLINE_IMAGE.HORROR;
        }
        if (unit.checkClassification(CLASSIFICATIONS.UNDEAD)) {
            return OUTLINE_IMAGE.HORROR;
        }

        if (unit.checkClassification(CLASSIFICATIONS.MONSTER)) {
            return OUTLINE_IMAGE.MONSTROUS;
        }

        if (unit.checkClassification(CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.MULTIPLE;
        }
        if (unit.checkClassification(CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.INSECT;
        }

        return OUTLINE_IMAGE.UNKNOWN;
    }

    public static VISIBILITY_LEVEL getVisibilityLevel(DC_Obj target, DC_HeroObj source) {
        return getVisibility(getOutlineType(target, source));
    }

    public static void resetOutlineAndVisibilityLevel(DC_Obj unit) {
        OUTLINE_TYPE type = getType(unit);

        unit.setOutlineType(type);
        unit.setVisibilityLevel(getVisibility(type));
        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return null; //
        // return type;
    }

    private static VISIBILITY_LEVEL getVisibility(OUTLINE_TYPE type) {
        VISIBILITY_LEVEL visibilityLevel = VISIBILITY_LEVEL.CLEAR_SIGHT;
        if (type != null) {
            switch (type) {
                case THICK_DARKNESS:
                case BLINDING_LIGHT:
                    visibilityLevel = VISIBILITY_LEVEL.CONCEALED;
                    break;
                case BLOCKED_OUTLINE:
                    visibilityLevel = VISIBILITY_LEVEL.BLOCKED;
                    break;
                case BRILLIANT_OUTLINE:
                case CLEAR_OUTLINE:
                    visibilityLevel = VISIBILITY_LEVEL.VAGUE_OUTLINE;
                    break;
                case FLAT_OUTLINE:
                case VAGUE_OUTLINE:
                case MASS_OUTLINE:
                case DARK_OUTLINE:
                    visibilityLevel = VISIBILITY_LEVEL.OUTLINE;
            }
        }
        return visibilityLevel;
    }

    public static OUTLINE_TYPE getType(DC_Obj unit) {
        if (unit.getGame().isDebugMode()) {
            if (unit.isMine()) {
                return null;
            }
        }
        DC_HeroObj activeUnit = DC_Game.game.getTurnManager().getActiveUnit(true);
        DC_TurnManager.setVisionInitialized(true);
        if (activeUnit == null) {
            return null;
        }
        if (unit == activeUnit) {
            return null;
        }
        return getType(activeUnit, unit);
    }

    public static OUTLINE_TYPE getType(DC_HeroObj activeUnit, DC_Obj unit) {
        // if (unit.getVisibilityLevel() == VISIBILITY_LEVEL.CLEAR_SIGHT)
        // return null;
        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return null;
        return getOutlineType(unit, activeUnit);
    }

    public static OUTLINE_TYPE getOutlineType(DC_Obj unit, DC_HeroObj activeUnit) {
        if (DebugMaster.isOmnivisionOn()) {
            return null;
        }
        if (unit.getGame().isSimulation()) {
            return null;
        }
        if (unit.isDetectedByPlayer()) {
            if (unit instanceof DC_Cell) {
                return null;
            }
            if (unit instanceof DC_HeroObj) {
                DC_HeroObj heroObj = (DC_HeroObj) unit;
                if (heroObj.isWall() || heroObj.isLandscape()) {

                    return null;
                }
            }
        }
        Ref ref = new Ref(activeUnit);
        ref.setMatch(unit.getId());
        // [quick fix]

        int gamma = ConcealmentRule.getGamma(true, activeUnit, unit);
        if (gamma == Integer.MIN_VALUE) {
            return OUTLINE_TYPE.VAGUE_LIGHT;
        } else if (gamma >= getGammaForBlindingLight()) {
            if (!activeUnit.checkPassive(STANDARD_PASSIVES.EYES_OF_LIGHT)) {
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
        }
        // TODO LIGHT_EMISSION !
        if (gamma <= getGammaForThickDarkness()) {

            return OUTLINE_TYPE.THICK_DARKNESS;
        }
        // LIT_HAZE ?

        // if (unit instanceof DC_Cell)
        // return null;

        // int effectiveVisibility = (int) (gamma / Math.max(1, 2 *
        // Math.sqrt(diff)));
        // first check if there is enough for either... then check which is
        // relatively greater! Or "Dark Vague Outline?" :)
        if (unit instanceof DC_Cell) {
            if (gamma > 50) {

                // [quick fix]
                if (!new ClearShotCondition().check(ref)) {
                    // vision type check - x.ray or so TODO
                    return OUTLINE_TYPE.BLOCKED_OUTLINE;
                }
                return null;
            }
        }
        if (gamma > 50) {// ++ dark vision!
            // flat/blocked?

            // [quick fix]
            if (!new ClearShotCondition().check(ref)) {
                // vision type check - x.ray or so TODO
                return OUTLINE_TYPE.BLOCKED_OUTLINE;
            }
            return null;
        }
        int distance = PositionMaster.getDistance(activeUnit, unit);
        int diff = distance - activeUnit.getIntParam(PARAMS.SIGHT_RANGE);
        // if adjacent, gamma must be
        if (gamma < 40 - diff * 10) {
            return OUTLINE_TYPE.DARK_OUTLINE;
        }
        return OUTLINE_TYPE.VAGUE_OUTLINE;
    }

    private static int getGammaForThickDarkness() {
        return 15;
    }

    private static int getGammaForBlindingLight() {
        return 300;
    }

    public static void resetVisibilityLevels() {
        for (DC_HeroObj unit : DC_Game.game.getUnits()) {
            resetOutlineAndVisibilityLevel(unit);
        }

        for (Obj cell : DC_Game.game.getCells()) {
            resetOutlineAndVisibilityLevel((DC_Obj) cell);
        }
    }

    public static boolean isZeroVisibility(DC_Obj obj) {
        return isZeroVisibility(obj, false);
    }

    public static boolean isZeroVisibility(DC_Obj obj, boolean active) {
        return obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.BLOCKED
                || obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.CONCEALED;
    }

    public enum OUTLINE_HINT {
        SMALL, HUGE, SHORT, TALL, BROAD, INSECTOID, ANIMAL_LIKE,
        // "Something huge" "broadly" "insectoid" "animal-like"
        MONSTROUS, HUMANOID, HUMAN_LIKE, MONSTROUS_HUMANOID, FOUR_LEGGED, SOLID, IMMATERIAL, BESTIAL, ABHORRENT, OUTLINE_HINT() {

        }
    }

    public enum OUTLINE_IMAGE {
        MONSTROUS, HUMANOID, HUMAN, MONSTROUS_HUMANOID, HUMANLIKE, QUADRUPED, STRUCTURE, BEAST, INSECT,

        MULTIPLE,
        // 'certainty factor' - certain vs uncertain?
        // "four legged" "seemingly humanlike" "humanoid in shape"
        // "monstrious in shape"
        HORROR, WALL, UNKNOWN;

        OUTLINE_IMAGE() {
        }

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum OUTLINE_TYPE {
        BLINDING_LIGHT, VAGUE_LIGHT, THICK_DARKNESS, VAGUE_OUTLINE, DARK_OUTLINE, BRILLIANT_OUTLINE, CLEAR_OUTLINE, MASS_OUTLINE, BLOCKED_OUTLINE, FLAT_OUTLINE;

        String outlinePath = "ui\\outlines\\" + toString();
        String path;
        private Image image;

        OUTLINE_TYPE() {

        }

        // hasSelectImage(){
        //
        // }
        public Image getImage() {
            if (image == null) {
                image = ImageManager.getImage(outlinePath + ".jpg");
            }
            if (image == null) {
                return DARK_OUTLINE.getImage();
            }
            return image;

        }

        public Image getImage128() {
            String outlinePath = "ui\\outlines\\raw\\" + toString();
            return ImageManager.getImage(outlinePath + ".jpg");
        }
    }
}
