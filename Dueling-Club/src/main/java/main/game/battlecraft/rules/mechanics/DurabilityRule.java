package main.game.battlecraft.rules.mechanics;

import main.ability.conditions.req.ItemCondition;
import main.ability.effects.Effects;
import main.ability.effects.container.ConditionalEffect;
import main.ability.effects.oneshot.rule.DurabilityReductionEffect;
import main.content.DC_ContentManager;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.elements.conditions.OrConditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroSlotItem;
import main.entity.item.DC_WeaponObj;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.battlecraft.rules.DC_RuleImpl;
import main.system.math.MathMaster;

public class DurabilityRule extends DC_RuleImpl {

    private Integer amount;

    public DurabilityRule(MicroGame game) {
        super(game);
        setOn(false);
    }

    public static int physicalDamage(int damage, int blocked, DAMAGE_TYPE damage_type,
                                     DC_HeroSlotItem armor, DC_WeaponObj weapon, boolean simulation) {
        int self_damage_mod = armor.getIntParam(DC_ContentManager
                .getArmorSelfDamageParamForDmgType(damage_type));

        // new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, )

        MATERIAL m1 = armor.getMaterial();
        MATERIAL m2 = weapon.getMaterial();
        int armor_vs_weapon = m1.getHardness() - m2.getHardness();

        int armor_amount = blocked * self_damage_mod / 100;
        armor_amount = MathMaster.addFactor(armor_amount, armor_vs_weapon);
        DurabilityReductionEffect durabilityReductionEffect = new DurabilityReductionEffect(false,
                armor_amount);
        durabilityReductionEffect.setSimulation(simulation);
        Ref ref = Ref.getSelfTargetingRefCopy(armor);
        ref.setID(KEYS.WEAPON, weapon.getId());
        durabilityReductionEffect.apply(ref);

        self_damage_mod = weapon.getIntParam(DC_ContentManager
                .getArmorSelfDamageParamForDmgType(damage_type));
        int weapon_amount = blocked * self_damage_mod / 100;
        weapon_amount = MathMaster.addFactor(weapon_amount, armor_vs_weapon);
        durabilityReductionEffect = new DurabilityReductionEffect(true, weapon_amount);
        durabilityReductionEffect.setSimulation(simulation);
        durabilityReductionEffect.apply(Ref.getSelfTargetingRefCopy(weapon));
        return durabilityReductionEffect.getDurabilityLost();
    }

    public static int spellDamage(int damage, int blocked, DAMAGE_TYPE damage_type,
                                  DC_HeroSlotItem armor, boolean simulation) {
        int self_damage_mod = armor.getIntParam(DC_ContentManager
                .getArmorSelfDamageParamForDmgType(damage_type));
        // special cases may apply for Damage
        int amount = blocked * self_damage_mod / 100;
        DurabilityReductionEffect durabilityReductionEffect = new DurabilityReductionEffect(null,
                amount);
        durabilityReductionEffect.setSimulation(simulation);
        durabilityReductionEffect.apply(Ref.getSelfTargetingRefCopy(armor));
        return durabilityReductionEffect.getDurabilityLost();

    }

    public static Effects getAttackItemDurabilityReductionEffects(int amount) {
        String source_ref = "{EVENT_SOURCE}";
        String target_ref = "{EVENT_TARGET}";

        return new Effects(new ConditionalEffect(new OrConditions(new ItemCondition(source_ref,
                ItemEnums.ITEM_SLOT.MAIN_HAND.getProp().getName()), new ItemCondition(source_ref,
                ItemEnums.ITEM_SLOT.OFF_HAND.getProp().getName())), new DurabilityReductionEffect(true,
                amount)), new ConditionalEffect(new ItemCondition(target_ref, ItemEnums.ITEM_SLOT.ARMOR
                .getProp().getName()), new DurabilityReductionEffect(false, amount)));
    }

    @Override
    public void apply(Ref ref) {
        this.amount = ref.getAmount();
        super.apply(ref);
    }

    @Override
    public boolean check(Event event) {
        return super.check(event);
    }

    @Override
    public void initEffects() {
        // TODO conditional effect for armor and weapon separately
        effects = getAttackItemDurabilityReductionEffects(amount);
    }

    @Override
    public void initConditions() {
        // preCheck attack?
    }

    @Override
    public void initEventType() {
        this.event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE;
    }
    /**
     * upon each physical attack, preCheck weapon/armor of the belligerents and
     * call their reduceDurability() method
     *
     * New rules: Custom trigger: write a method to be invoked upon X Event.
     *
     *
     * What are the disadvantages? Isn't "generic" enough Doesn't comply with
     * trigger paradigm Or does it? Originally, in wc3 e.g., this was exactly
     * what triggers were all about! Writing a function to be invoked upon some
     * event...
     *
     * Maybe I should pick it up real time now!
     *
     *
     * It can help with writing many other rules... Some generic and
     * object-oriented elements will be necessary of course.
     *
     * Weight/Morale/Wounds/FOC-STA all follow a certain pattern...
     *
     *
     *
     *
     *
     *
     *
     */

}
