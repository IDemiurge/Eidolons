package eidolons.netherflame.content.feat;

import eidolons.entity.feat.Feat;
import eidolons.netherflame.generic.spaces.model.FeatSpaceModel;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.enums.entity.SkillEnums;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static main.content.enums.entity.SkillEnums.*;

/**
 * Created by Alexander on 2/10/2022
 */
public class FeatContent {
    public static FeatSpaceModel fill(MASTERY active, MASTERY passive,
                                      FeatSpaceModel space, HeroDataModel heroModel) {
        // int maxTier = space.getMaxTier();
        // //BG - pick up from ALL?
        // //prefs - ?
        // List<ObjType> sorted = getFiltered(heroModel, maxTier, active, true);
        // space.

        return space;
    }

    private static List<ObjType> getFiltered(HeroDataModel heroModel, int maxTier, MASTERY mastery, boolean active) {
        // List<ObjType> list = new ArrayList<>();
        // boolean combat = isCombatMastery(mastery);
        // int rank = heroModel.getMasteryRank(mastery);
        // if (active) {
        //     if (combat)
        //         getActions(mastery, rank);
        //     else list.addAll(spellContent.getSpells(maxTier, mastery, heroModel));
        //     //TODO UPGRADES?!
        // } else {
        //
        // }
        // return list;
        return null;
    }

    public static void filterMasteryType(Collection<MASTERY> active, boolean combatOrMagic) {
        active.removeIf(mstr -> isCombatMastery(mstr) != combatOrMagic);
    }

    private static boolean isCombatMastery(MASTERY mstr) {
        return false;
    }
}
