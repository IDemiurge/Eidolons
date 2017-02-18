package main.ability.conditions.req;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.MicroCondition;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.system.math.DC_MathManager;

public class SkillPointCondition extends MicroCondition {

    private int pointsRequired;
    private boolean rank;

    public SkillPointCondition(boolean rank) {
        this.rank = rank;
    }

    public SkillPointCondition() {

    }

    @Override
    public boolean check() {
        Entity match = ref.getMatchObj();
        if (match == null) {
            match = ref.getType("match");
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
