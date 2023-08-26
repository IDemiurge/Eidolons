package elements.exec.effect.attack;

import elements.content.enums.types.CombatTypes;
import elements.exec.EntityRef;
import elements.exec.effect.Effect;
import framework.entity.sub.UnitAction;
import logic.calculation.GradeCalc;

/**
 * Created by Alexander on 8/25/2023 does this include all offensive actions? What about area stuff? a zone spell
 * applies 'attack' to all its targets... with custom stuff
 * <p>
 * SO - this is about ALL negative stuff from a unit to a target > Roll ATK/DEF or SP/RES > Work with Roll Grade that
 * resulted
 * <p>
 * Notes: 1) What if effect depends on previous targets? 2) Total targets - we can gain some info from 'actionResult'
 * class and apply another action that will reference its values
 * <p>
 * So attack is essentially just a wrapper! Or maybe ... a superclass? should we have some general class that an effect
 * returns ?
 */
public abstract class AttackEffect extends Effect {


    public abstract void process(CombatTypes.RollGrade grade, EntityRef ref);

    @Override
    protected void applyThis(EntityRef ref) {
        ref.applyToTargets(target -> {
            UnitAction action = ref.getAction();
            int atk = ref.getSource().getAtkOrSp(action, ref);
            int def = target.getDefOrRes(action, ref);
            int die = action.getInt("die");
            int offset = 0;
            CombatTypes.RollGrade grade=null ;
            if (data.has("grade")){
                grade = data.getEnum("grade", CombatTypes.RollGrade.class);
            } else
                grade = GradeCalc.calculateGrade(atk, def, die, offset);
            // ref.getSource().omenUsed();
            // target.omenUsed();
            process(grade, ref);
        });
        // GradeCalcInfo info;
        // still events must be diff for single-attack vs zone-atk!
    }

    @Override
    public String getArgs() {
        return "grade|";
    }
}
