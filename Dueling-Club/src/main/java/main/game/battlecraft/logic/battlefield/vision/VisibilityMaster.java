package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.images.ImageManager;
import main.system.math.PositionMaster;

import java.awt.*;

public class VisibilityMaster {

    private final String TARGET = " t";
    private final String INFO = " s";
    private VisionMaster master;
    private boolean outlinesOn = true;

    public VisibilityMaster(VisionMaster visionMaster) {
        master = visionMaster;
//        outlinesOn = !visionMaster.getGame().isDummyPlus() && !VisionManager.isVisionHacked();

    }


    public Image getDisplayImageForUnit(DC_Obj obj) {

        return null;
    }

    public String getDisplayImagePathForUnit(DC_Obj obj) {
        // construct and cache an Outline obj per unit?
        OUTLINE_TYPE type = obj.getOutlineType();

        if (type == null) {
            return null;
        }
        if (type == VisionEnums.OUTLINE_TYPE.BLOCKED_OUTLINE) {
            return getImagePath(VisionEnums.OUTLINE_TYPE.THICK_DARKNESS, obj);
        }
        if ((type == VisionEnums.OUTLINE_TYPE.THICK_DARKNESS || type == VisionEnums.OUTLINE_TYPE.BLINDING_LIGHT)) {
            return getImagePath(type, obj);
        }
        if (obj instanceof DC_Cell) {
            return null;
        }
        String outlinePath = "ui\\outlines\\" + type.toString();
        OUTLINE_IMAGE outlineImage;
        if (type == VisionEnums.OUTLINE_TYPE.VAGUE_OUTLINE) {
            outlineImage = master.getOutlineMaster().getImageVague((Unit) obj);
        } else {
            outlineImage = master.getOutlineMaster().getImageDark((Unit) obj);
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
        String image = (outlinePath + ".jpg");
        if (ImageManager.isImage(image)) {
            return image;
        }

        image = (outlinePath.replace("_" + outlineImage.toString(), "") + ".jpg");
        if (ImageManager.isImage(image)) {
            return image;
        }
        image = (outlinePath.replace(INFO, "").replace(TARGET, "") + ".jpg");
        if (ImageManager.isImage(image)) {
            return image;
        }
        return null;
        // DIFFERENTIATE BETWEEN RANGE, CONCEALMENT, ILL AND STEALTH
    }

    public Image getImage(OUTLINE_TYPE type, DC_Obj unit) {
        return ImageManager.getImage(getImagePath(type, unit));
    }

    public String getImagePath(OUTLINE_TYPE type, DC_Obj unit) {
        String outlinePath = "ui\\outlines\\" + type.toString();
        if (unit.isTargetHighlighted()) {
            outlinePath += TARGET;
        } else {
            if (unit.isInfoSelected()) {
                outlinePath += INFO;
            }
        }
        String image = (outlinePath + ".jpg");
        if (!ImageManager.isImage(image)) {
            image = ("ui\\outlines\\" + type.toString() + ".jpg");
        }
        return image;
    }


    private VISIBILITY_LEVEL getVisibility(OUTLINE_TYPE type) {
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

    public void resetVisibilityLevels() {
        for (Unit unit : DC_Game.game.getUnits()) {
            resetOutlineAndVisibilityLevel(unit);
        }

        for (Structure structure : DC_Game.game.getStructures()) {
            resetOutlineAndVisibilityLevel(structure);
        }
        for (Obj cell : DC_Game.game.getCells()) {
            resetOutlineAndVisibilityLevel((DC_Obj) cell);
        }
    }

    public boolean isZeroVisibility(DC_Obj obj) {
        return isZeroVisibility(obj, false);
    }

    public boolean isZeroVisibility(DC_Obj obj, boolean active) {
        return obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.BLOCKED
//         || obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.CONCEALED
         || obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.UNSEEN;
    }


    public void resetOutlineAndVisibilityLevel(DC_Obj unit) {
        unit.setVisibilityLevel(getUnitVisibilityLevel(master.getActiveUnit(), unit));
    }

    public VISIBILITY_LEVEL getUnitVisibilityLevel(Unit source, DC_Obj target) {
//        Ref ref= source.getRef().getTargetingRef(target);
//        ref.setMatch(target.getId());
//        if (!master.getSightMaster().getClearShotCondition().preCheck(ref)) {
//            return VISIBILITY_LEVEL.BLOCKED;
//        }
        OUTLINE_TYPE outline = master.getOutlineMaster().
         getOutlineType(target, source);
        if (outline == OUTLINE_TYPE.THICK_DARKNESS) {
            if (checkUnseen(source, target)) {
                return VISIBILITY_LEVEL.UNSEEN;
            }
        }
        if (source.isMainHero())
            target.setOutlineType(outline);
        //check stealth-invisible?
        VISIBILITY_LEVEL visibilityLevel = getVisibility(outline);
        return visibilityLevel;
    }

    private boolean checkUnseen(Unit source, DC_Obj target) {
        if (PositionMaster.getExactDistance(source.getCoordinates(), target.getCoordinates()) >
         ClearShotCondition.getMaxCheckDistance(source, target)) {
            return true;
        }
        return false;
    }


}
