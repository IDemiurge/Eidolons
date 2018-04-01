package eidolons.ability.effects.containers.customtarget;

import eidolons.ability.targeting.TemplateAutoTargeting;
import main.ability.effects.Effect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;

public class GlobalEffect extends SpecialTargetingEffect {

    private AUTO_TARGETING_TEMPLATES template = AUTO_TARGETING_TEMPLATES.ALL;

    // public GlobalEffect(Effect effects, Boolean friendlyFire,
    // Boolean notSelf) {
    // this.effects = effects;
    // this.friendlyFire = friendlyFire;
    // this.notSelf = notSelf;
    // }
    public GlobalEffect(Effect effects, AUTO_TARGETING_TEMPLATES template) {
        this.template = template;
    }

    @Override
    public void initTargeting() {
        // filteringConditions = new Conditions();
        // if (friendlyFire){
        // template = AUTO_TARGETING_TEMPLATES.ALL_ENEMIES;
        // }
        targeting = new TemplateAutoTargeting(template);
    }

}
