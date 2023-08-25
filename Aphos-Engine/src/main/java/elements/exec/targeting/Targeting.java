package elements.exec.targeting;

// import com.google.common.collect.ImmutableSet;


import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import framework.data.TypeData;
import framework.entity.Entity;
import framework.entity.EntityData;
import framework.entity.field.FieldEntity;

import java.util.List;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 6/11/2023 Special rules will be for Flank-to-Flank etc * or should these all be aggregated
 * behavior methods? * Targeting should actually be more like... data object, not an executor! * So all we need is: *
 * Template * Conditions * Args (e.g. number of targets?) * * Usage cases * >> Depending on target, action has diff
 * effects (WH's pierce, Soldier's spear..) * >>
 * <p>
 * <p>
 * Let's really examine our targeting reqs: 1) Selective for action is DIFF 2)
 */
public class Targeting {
    protected TypeData data;
    protected Condition condition;

    // boolean all_in_range; //if true, action affects all units that match the Targeting condition
    // //E.G. - All Melee, All Range (1), All Enemy, All <...>
    // boolean friendly_fire;
    // boolean left_right; //for ray only?
    // maybe these can be part of TARGETING DATA? Parsed same way as for entity?

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void select(EntityRef ref) {

        //if there is only Self - will auto-target (if some option is checked)

        //modify conditions based on data

        List<Entity> fieldEntities = combat().getEntities().getFieldEntities();//should we always start with that list? then remove omens/obst/..
        // filter( condition.check())

        //how to easily assemble filters? Should have some MNGR for that

        //here we determine if there is Manual Selection

        fieldEntities.removeIf(e -> !condition.check(ref.setMatch(e)));
        // single = true;
        if (fieldEntities.size() == 1) {
            ref.setTarget(fieldEntities.get(0));
        } else {
            //TODO
            List<FieldEntity> entities = null;
            ref.setGroup(new TargetGroup(entities));
        }
    }


    public void setData(TypeData data) {
        this.data = data;
    }

    public TypeData getData() {
        return data;
    }

    public void setType(TargetingTemplates.TargetingType type) {
        this.type = type;
    }

    public TargetingTemplates.TargetingType getType() {
        return type;
    }
}
















