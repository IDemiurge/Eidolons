package eidolons.netherflame.generic.match.feats;

import eidolons.entity.feat.spaces.FeatSpaceData;
import eidolons.netherflame.content.feat.FeatContent;
import eidolons.netherflame.generic.match.Match;
import eidolons.netherflame.generic.match.MatchLogic;
import eidolons.netherflame.generic.spaces.model.FeatSpaceModel;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.enums.entity.SkillEnums;

import static main.content.enums.entity.SkillEnums.*;

/**
 * Created by Alexander on 2/10/2022
 */
public class FeatMatch implements Match<MASTERY, FeatSpaceModel, MASTERY> {
    /**
     * construct the FeatSpace for 2 masteries
     */
    @Override
    public void process(MatchLogic.MatchResult<MASTERY, FeatSpaceModel, MASTERY> result) {
        MASTERY active = result.top;
        MASTERY passive = result.bottom;
        FeatSpaceModel space = result.center;
        HeroDataModel heroModel = null;

        FeatSpaceModel model = FeatContent.fill(active, passive, space, heroModel);
        // space.setData(data);

    }
}
