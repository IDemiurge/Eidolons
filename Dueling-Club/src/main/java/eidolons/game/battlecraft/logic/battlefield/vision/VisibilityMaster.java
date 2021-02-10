package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.system.images.ImageManager;

import java.awt.*;

public class VisibilityMaster {

    private final VisionMaster master;

    public VisibilityMaster(VisionMaster visionMaster) {
        master = visionMaster;

    }



    public Image getImage(OUTLINE_TYPE type, DC_Obj unit) {
        return ImageManager.getImage(getImagePath(type, unit));
    }

    public String getImagePath(OUTLINE_TYPE type, DC_Obj unit) {
        String image = (type.getImagePath());
        if (!ImageManager.isImage(image)) {
            image = (  type.getImagePath() );
        }
        return image;
    }


    private VISIBILITY_LEVEL getVisibility(OUTLINE_TYPE type) {
        if (type != null) {
            switch (type) {
                case UNKNOWN:
                    return VISIBILITY_LEVEL.UNSEEN;
                case DEEPER_DARKNESS:
                case BLINDING_LIGHT:
                    return VISIBILITY_LEVEL.CONCEALED;
                case BLOCKED_OUTLINE:
                    return VISIBILITY_LEVEL.BLOCKED;
                case BRILLIANT_OUTLINE:
                case CLEAR_OUTLINE:
                    return VISIBILITY_LEVEL.VAGUE_OUTLINE;
                case FLAT_OUTLINE:
                case VAGUE_OUTLINE:
                case MASS_OUTLINE:
                case DARK_OUTLINE:
                    return VISIBILITY_LEVEL.OUTLINE;
            }
        }
        return VISIBILITY_LEVEL.CLEAR_SIGHT;
    }


    public VISIBILITY_LEVEL getUnitVisibilityLevel(Unit source, DC_Obj target) {
        if (!source.isMine() && target.isMine()) {
            OUTLINE_TYPE outline = master.getOutlineMaster().
             getOutlineType(target, source);
            return getVisibility(outline);
        }
        VISIBILITY_LEVEL visibilityLevel = getVisibility(target.getOutlineType());
        target.isOutsideCombat();
        return visibilityLevel;
    }



}
