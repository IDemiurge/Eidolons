package main.ability.effects.oneshot.special;

import main.ability.effects.oneshot.MicroEffect;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;

/* 
 * filter passives, getOrCreate a list of names to remove and generate ModifyPropertyEffect
 * 
 * 
 */
public class PassiveManipulationEffect extends MicroEffect {

    private ABILITY_MANIPULATION type;
    private String abilName;

    public PassiveManipulationEffect() {
        this.type = ABILITY_MANIPULATION.REMOVE_ALL;
    }

    public PassiveManipulationEffect(ABILITY_MANIPULATION type) {
        this.type = type;
    }

    public PassiveManipulationEffect(ABILITY_MANIPULATION type, String name) {
        this.type = type;
        this.abilName = name;
    }

    public PassiveManipulationEffect(ABILITY_MANIPULATION type,
                                     STANDARD_PASSIVES passive) {
        this.type = type;
        this.abilName = passive.getName();
    }

    @Override
    public boolean applyThis() {
        // targeting??? "all"? by tag?
        switch (type) {
            // ++ random passives; matching passives;
            case ADD:
                ref.getTargetObj().addPassive(abilName);
                break;
            case MODIFY_FORMULA:
                break;
            case REMOVE:
                ref.getTargetObj().removePassive(abilName);
                break;

            case REMOVE_ALL:
                ref.getTargetObj().removeAllPassives();
                break;
            case STEAL:
                break;
            default:
                break;

        }

        return false;
    }

}
