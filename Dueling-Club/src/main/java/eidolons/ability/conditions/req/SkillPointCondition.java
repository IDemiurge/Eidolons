package eidolons.ability.conditions.req;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.content.ContentManager;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.MicroCondition;
import main.entity.Entity;
import main.entity.Ref;
import eidolons.system.math.DC_MathManager;

public class SkillPointCondition extends MicroCondition {

    private int pointsRequired;
    private boolean rank;

    public SkillPointCondition(boolean rank) {
        this.rank = rank;
    }

    public SkillPointCondition() {

    }

    @Override
    public boolean check(Ref ref) {
        Entity match = ref.getMatchObj();
        if (match == null) {
            match = ref.getType("match");
        }
        if (match == null) {
            return false;
        }
        PARAMETER masteryParam = ContentManager.getPARAM(match.getProperty(G_PROPS.MASTERY));
        if (masteryParam == null) {
            masteryParam = ContentManager.getMastery(match.getProperty(G_PROPS.MASTERY));
        }
        Integer amount = match.getIntParam(PARAMS.SKILL_DIFFICULTY);
        if (rank) {
            amount = amount * match.getIntParam(PARAMS.RANK_SD_MOD) / 100;
        }
        pointsRequired = amount
         - DC_MathManager
         .getFreeMasteryPoints((Unit) ref.getSourceObj(), masteryParam);

        return pointsRequired <= 0;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

}
