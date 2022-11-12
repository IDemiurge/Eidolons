package eidolons.netherflame.generic.match.feats;

import eidolons.entity.unit.netherflame.HeroUnit;
import eidolons.netherflame.content.feat.FeatContent;
import eidolons.netherflame.generic.spaces.model.FeatSpaceModel;
import main.content.enums.entity.SkillEnums;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Alexander on 2/10/2022
 */
public class FeatMatchConstructor {

    boolean type;

    public FeatMatchLogic construct(HeroUnit hero) {
        Collection<SkillEnums.MASTERY> active =     new LinkedList<>( hero.getMasteries().getList() ) ;
        FeatContent.filterMasteryType(active, type);
        //TODO
        Collection<SkillEnums.MASTERY> passive =     new LinkedList<>(active) ;
        //is it possible that some have 0 options and must be removed?

        FeatSpaceModel[] spaces = new FeatSpaceModel[0];
        FeatMatchLogic logic = new FeatMatchLogic(active, passive, spaces);

        return logic;
    }
}
