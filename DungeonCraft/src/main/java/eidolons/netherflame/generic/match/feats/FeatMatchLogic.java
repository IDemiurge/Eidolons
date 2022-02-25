package eidolons.netherflame.generic.match.feats;

import eidolons.netherflame.generic.match.MatchLogic;
import eidolons.netherflame.generic.spaces.model.FeatSpaceModel;

import java.util.Collection;

import static main.content.enums.entity.SkillEnums.*;

/**
 * Created by Alexander on 2/9/2022
 */
public class FeatMatchLogic extends MatchLogic<MASTERY, FeatSpaceModel, MASTERY> {
    public FeatMatchLogic(Collection<MASTERY> top, Collection<MASTERY> bottom, FeatSpaceModel... center) {
        super(top, bottom, center);
    }

    @Override
    protected void update() {

    }

    @Override
    protected void fail() {

    }
}
