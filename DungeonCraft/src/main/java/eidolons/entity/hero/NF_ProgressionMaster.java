package eidolons.entity.hero;

import eidolons.content.DC_Calculator;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.entity.Entity;

public class NF_ProgressionMaster {
    /*
    points
    power
    requirements
    mastery ranks

     */

    public static void levelUp(Unit unit){
        int level = unit.getLevel();
        int mstr = DC_Calculator.getMasteryRanks(unit, level);
        unit.modifyParameter(PARAMS.MASTERY_RANKS_UNSPENT, mstr);

        unit.modifyParameter(PARAMS.CLASS_RANKS_UNSPENT, 1);

        int sp_pts = DC_Calculator.getSpellPoints(unit, level);
        unit.modifyParameter(PARAMS.SPELL_POINTS, sp_pts);
        int sk_pts = DC_Calculator.getSkillPoints(unit, level);
        unit.modifyParameter(PARAMS.SKILL_POINTS, sk_pts);


    }


    public static int getMasteryRanksForLevelUp(Entity hero) {
        return hero.getIntParam(PARAMS.HERO_LEVEL)+1;
    }
}
