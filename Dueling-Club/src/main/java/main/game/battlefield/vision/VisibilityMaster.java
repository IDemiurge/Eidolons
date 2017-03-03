package main.game.battlefield.vision;

import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.images.ImageManager;

import java.awt.*;

public class VisibilityMaster {

    private final String TARGET = " t";
    private final String INFO = " s";
    private VisionMaster master;
    private boolean outlinesOn;

    public VisibilityMaster(VisionMaster visionManager) {
        master = visionManager;
        outlinesOn=!visionManager.getGame().isDummyPlus();

    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target) {
        return getVisibilityLevel(source, target, null);
    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target, Boolean status) {
//        int value = getGamma(source, target, status);
        VISIBILITY_LEVEL vLevel = null;
        for (VISIBILITY_LEVEL vLvl : VISIBILITY_LEVEL.values()) {
            vLevel = vLvl;
            // if (value > vLvl.getIlluminationBarrier())
            // break;
        }
        return vLevel;
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


    public void resetOutlineAndVisibilityLevel(DC_Obj unit) {
        OUTLINE_TYPE type = master.getOutlineMaster().getOutlineType(unit);

        unit.setOutlineType(type);
        unit.setVisibilityLevel(getVisibility(type));
        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return null; //
        // return type;
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

        for (Obj cell : DC_Game.game.getCells()) {
            resetOutlineAndVisibilityLevel((DC_Obj) cell);
        }
    }

    public boolean isZeroVisibility(DC_Obj obj) {
        return isZeroVisibility(obj, false);
    }

    public boolean isZeroVisibility(DC_Obj obj, boolean active) {
        return obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.BLOCKED
                || obj.getVisibilityLevel(active) == VISIBILITY_LEVEL.CONCEALED;
    }


    public VISIBILITY_LEVEL getUnitVisibilityLevel(DC_Obj target, Unit source) {
        VISIBILITY_LEVEL visibilityLevel = getVisibility(master.getOutlineMaster().
                getOutlineType(target, source));
        return visibilityLevel;
    }

    public boolean isOutlinesOn() {
        return outlinesOn;
    }

    public void setOutlinesOn(boolean outlinesOn) {
        this.outlinesOn = outlinesOn;
    }
}
