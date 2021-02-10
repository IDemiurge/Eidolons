package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.graphics.Color;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.ambi.EmitterMap;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/30/2018.
 *
 * so what variations do I need?
 *
 * just different hues for some types of mist...
 * and stars!
 *
 */
public class WeaveAmbience extends GroupX {
    List<EmitterMap> maps=    new ArrayList<>() ;
    public   WeaveAmbience(Weave weave) {

        Color hue=getHueForWeave(weave);
        int showChance = 30;
//         maps.add(new EmitterMap(GenericEnums.VFX.STARS.getPath(), showChance, hue));
//         maps.add(new EmitterMap(GenericEnums.VFX.MIST_WHITE.getPath(), showChance, hue));
//         maps.add(new EmitterMap(GenericEnums.VFX.MIST_WHITE2.getPath(), showChance, hue));
//         maps.add(new EmitterMap(GenericEnums.VFX.MIST_WHITE3.getPath(), showChance, hue));
//         maps.add(new EmitterMap(GenericEnums.VFX.MIST_WIND.getPath(), showChance, hue));
// //         for (String sub: getAdditional)
//         for (EmitterMap sub : maps) {
//             addActor(sub);
//             sub.update();
//         }
    }

    private Color getHueForWeave(Weave weave) {
        Object arg = weave.getCoreNode().getArg();
        if (arg instanceof CLASS_GROUP) {
            return GdxColorMaster.getColorForClassGroup(((CLASS_GROUP) arg));
        }
        if (arg instanceof SKILL_GROUP) {
            return GdxColorMaster.getColorForSkillGroup(((SKILL_GROUP) arg));
        }
        return null;
    }
}














