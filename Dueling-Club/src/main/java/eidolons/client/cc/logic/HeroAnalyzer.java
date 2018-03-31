package eidolons.client.cc.logic;

import main.content.enums.entity.HeroEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;

public class HeroAnalyzer {

    public static boolean isFemale(Entity hero) {
        return hero.checkProperty(G_PROPS.GENDER, HeroEnums.GENDER.FEMALE + "");
    }

}
