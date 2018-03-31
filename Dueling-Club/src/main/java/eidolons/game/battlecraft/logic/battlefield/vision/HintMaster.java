package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_HINT;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class HintMaster {

    Map<BattleFieldObject, Map<OUTLINE_IMAGE, String>> cache = new HashMap<>();
    private VisionMaster master;

    public HintMaster(VisionMaster visionMaster) {
        master = visionMaster;
    }

    private List<OUTLINE_HINT> getHints(BattleFieldObject unit, OUTLINE_IMAGE image) {


        List<OUTLINE_HINT> list = new ArrayList<>();
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
        // + unit.getParams(PARAMS.ILLUMINATION);
        // if (unit.getIntParam(PARAMS.CONCEALMENT) != 0)
        // text += StringMaster.getWellFormattedString(", CONCEALMENT - ")
        // + unit.getParams(PARAMS.CONCEALMENT);
        // hintText += " \nRelative visibility for "
        // + unit.getGame().getManager().getActiveObj().getName() + " :" +
        // unit.getGamma()
        // + StringMaster.wrapInParenthesis(text);

        return list;

    }

    public String getTooltip(DC_Obj target) {
        if (target.getOutlineType() == null) {
            return null;
        }
        if (target.getOutlineType() == VisionEnums.OUTLINE_TYPE.DEEPER_DARKNESS) {
            return "Impenetrable darkness...";
        }
        if (target.getOutlineType() == VisionEnums.OUTLINE_TYPE.BLINDING_LIGHT) {
            return "Blinding light!";
        }
        if (target instanceof Unit) {
            return getTooltipForUnit((Unit) target);
        }
        return null;
    }

    public String getTooltipForUnit(Unit unit) {
        String hintString = getHintsString(unit);
        String tooltip = StringMaster.getWellFormattedString(unit.getOutlineType().toString()) + " of something "
         + hintString;
        Unit activeUnit = DC_Game.game.getManager().getActiveObj();
        if (unit.getOwner().equals(activeUnit.getOwner())) {
            return unit.getToolTip();
        }

        return tooltip;

    }

    public String getHintsString(BattleFieldObject unit) {
        OUTLINE_IMAGE img = master.getOutlineMaster().getImageDark(unit);
        Map<OUTLINE_IMAGE, String> map = cache.get(unit);
        if (map == null) {
            map = new HashMap<>();
            cache.put(unit, map);
        }

        String hintString = map.get(img);
        if (hintString != null)
            return hintString;
        hintString = "";
        List<OUTLINE_HINT> hints =
         getHints(unit, img);
        for (OUTLINE_HINT hint : hints) {
            hintString += StringMaster.getWellFormattedString(hint.toString()) + " ";
        }
        map.put(img, hintString);
        return hintString;
    }

    // public String getTooltipForType(OUTLINE_TYPE type) {
    // // "vaguely visible" "shining brilliantly" "clear in sight"
    // // "wreathed in shadows"
    // }
}