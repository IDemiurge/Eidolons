package eidolons.libgdx.gui.panels.headquarters.datasource.tree;

import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SkillsDataSource extends HeroTreeDataSource {

    public SkillsDataSource(Unit hero) {
        super(hero);
    }

    @Override
    public List getSlotData(int tier) {
        return getMasteryRanks(tier);
    }

    @Override
    public List getLinkData(int tier) {
        return getSkillSlots(tier);
    }

    public List<MASTERY> getMasteryRanks(int tier) {
        List<MASTERY> list = new ArrayList<>();

        for (String sub : StringMaster.openContainer(
         hero.getProperty(SkillMaster.getMasteryRankProp(tier)))) {
            list.add(new EnumMaster<MASTERY>().retrieveEnumConst(MASTERY.class,
             sub));

        }
        return list;
    }

    public List<Triple<DC_FeatObj, MASTERY, MASTERY>> getSkillSlots(int tier) {
        List<Triple<DC_FeatObj, MASTERY, MASTERY>> list = new ArrayList<>();
        List<MASTERY> ranks = getMasteryRanks(tier);
        int i = 0;
        while (true) {
            if (ranks.size() <= i) break;

            DC_FeatObj skill = null;
            List<DC_FeatObj> skills = SkillMaster.getSkillsOfTier(hero, tier);
            if (skills.size() > i)
                skill = skills.get(i);
            MASTERY mastery1 = ranks.get(i++);
            if (ranks.size() <= i) break;
            MASTERY mastery2 = ranks.get(i);
            list.add(new ImmutableTriple<>(skill, mastery1, mastery2));
        }
        return list;
    }




















}
