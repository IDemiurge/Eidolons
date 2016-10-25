package main.client.cc.logic;

import main.content.CONTENT_CONSTS.GENDER;
import main.content.properties.G_PROPS;
import main.entity.Entity;

public class HeroAnalyzer {

    public static boolean isFemale(Entity hero) {
        return hero.checkProperty(G_PROPS.GENDER, GENDER.FEMALE + "");
    }

}
