package eidolons.netherflame.generic.feat;

import eidolons.content.PROPS;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.enums.entity.NewRpgEnums;
import main.content.values.properties.PROPERTY;

/**
 * Created by Alexander on 2/1/2022
 */
public class FeatFactory {

    public static void createFeats(HeroDataModel model, NewRpgEnums.FeatGroup group) {
        String string = model.getProperty(getFeatProp(group));

    }

    private static PROPERTY getFeatProp(NewRpgEnums.FeatGroup group) {
        switch (group) {
            case Quick_Item:
                return PROPS.QUICK_ITEMS;
            case Token:
                return PROPS.TOKENS;
            case Spells:
                return PROPS.LEARNED_SPELLS;
            case Actions:
                return PROPS.LEARNED_ACTIONS;
            case Passives:
                return PROPS.LEARNED_PASSIVES;
        }
        return null;
    }
}
