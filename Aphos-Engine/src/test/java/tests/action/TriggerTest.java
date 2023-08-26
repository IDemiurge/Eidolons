package tests.action;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.condition.ConditionBuilder;
import elements.exec.effect.generic.AddTriggerFx;
import elements.exec.targeting.TargetingTemplates;
import logic.execution.event.combat.CombatEventType;
import tests.basic.BattleInitTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/24/2023
 */
public class TriggerTest extends BattleInitTest {

    @Override
    public void test() {
        super.test();
        /*
        Clumsy: misses result in grazing random allies left/right
        Attack with preset grade
        Random targeting
        Miss event
         */
        String grazeLeftRight="grazeLeftRight";
        Map map=new HashMap();
        Condition sourceAttack = ConditionBuilder.build(
                TargetingTemplates.ConditionTemplate.SELF_CHECK, map);

        EntityRef ref=new EntityRef();
        //so this would be applied by passive - with default retain condition of ... source is alive

        new AddTriggerFx(CombatEventType.Unit_Attack_Misses, sourceAttack, grazeLeftRight).apply(ref);



    }


    //how do we specify trigger's target?
    //Suppose we make a passive that makes a unit damage adjacent enemies when they move
    //source of event will be target
    //triggered effect must have targeting then
    //so that 'data' has to build into whole EXEC with custom targeting!..
    //it would be nice to have a way to construct these fx+targeting in similar way ... to Builder
    //


    // new AddTriggerFx(CombatEventType.Unit_Being_Moved, builtCondition, data).apply()

    //regen? some passive that works reliably
    //what level of rarity warrants Trigger over code-rule?
    //gain rage when being Hit
    //Living Armor - remove broken armor or restore to full armor
    //
    //modify 100% syntax
}
