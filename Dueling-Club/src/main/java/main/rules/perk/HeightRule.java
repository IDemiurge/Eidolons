package main.rules.perk;

import main.content.PARAMS;
import main.entity.obj.unit.DC_HeroObj;

public class HeightRule {

    public static boolean isTaller(DC_HeroObj source, DC_HeroObj target) {
        int source_height = source.getIntParam(PARAMS.HEIGHT);
        int target_height = target.getIntParam(PARAMS.HEIGHT);

        return false;

    }

}
