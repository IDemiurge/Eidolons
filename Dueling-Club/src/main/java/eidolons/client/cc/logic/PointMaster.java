package eidolons.client.cc.logic;

import eidolons.content.DC_ContentManager;
import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;

public class PointMaster {

    public static final int ATTRIBUTE_MINIMUM = 4;
    public static final int MASTERY_MINIMUM = 0;
    public static final int MASTERY_MAXIMUM = 50;
    public static final int ATTRIBUTE_MAXIMUM = 100;

    public static int getPointCost(int value, Entity hero, PARAMETER param) {
        int MOD_FACTOR = 10;
        value--;
        if (param.isAttribute()) {
            Integer defParam = hero
             .getIntParam(DC_ContentManager
              .getDefaultAttr(ContentManager
               .getFinalAttrFromBase(param)));
            defParam += 5;
            MOD_FACTOR = defParam * 2;

        }

        int mod = value / MOD_FACTOR;

        return 1 + mod;
    }

    public static int getCost(int initial, int points, Entity hero, PARAMETER param) {
        int cost = 0;
        for (int i = 0; i < points; i++) {
            cost += getPointCost(initial + i, hero, param);
        }
        return cost;
    }
}
