package eidolons.netherflame.generic.spaces.model;

import eidolons.netherflame.generic.feat.FeatFactory;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.enums.entity.NewRpgEnums;

import java.util.Set;

/**
 * Created by Alexander on 1/31/2022
 *
 * Split stuff into groups - QI, Spells, Actions, Passives, what else?
 */
public class HeroFeatsModel {

    HeroDataModel model;

    public Set<FeatModel> getFeats(NewRpgEnums.FeatGroup group){
        if (!model.isFeatsGenerated())
            FeatFactory.createFeats(model, group);
        switch (group) {
            case Quick_Item:
                break;
            case Token:
                break;
            case Spells:
                //AVAILABLE spells... just one pool - "Learned"
                break;
            case Actions:
                break;
            case Passives:
                break;
        }

        return null;
    }

}
