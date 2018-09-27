package eidolons.game.battlecraft.rules.mechanics;

import eidolons.ability.conditions.req.ItemCondition;
import eidolons.ability.effects.oneshot.rule.DurabilityReductionEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import main.ability.effects.Effects;
import main.ability.effects.container.ConditionalEffect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.elements.conditions.OrConditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.MathMaster;

public class DurabilityRule extends DC_RuleImpl {

    private static DurabilityReductionEffect durabilityReductionEffect;
    private Integer amount;

    public DurabilityRule(GenericGame game) {
        super(game);
        setOn(false);
    }

    private static int physicalDamage(int damage, int blocked, DAMAGE_TYPE damage_type,
                                      DC_HeroSlotItem armor, DC_WeaponObj weapon, BattleFieldObject target) {
        MATERIAL m1 = null ;
        if (armor !=null )
            m1=armor.getMaterial();
        else {
            m1=getNaturalArmorMaterial(target);
        }
        MATERIAL m2 = weapon.getMaterial();
        int self_damage_mod = 100;
        if (armor != null && blocked>0) {
            self_damage_mod = armor.getIntParam(DC_ContentValsManager
             .getArmorSelfDamageParamForDmgType(damage_type));
            reduceDurability(false, armor, weapon, m1, m2, damage, blocked, self_damage_mod);
        }
        if (weapon != null) {
            if (!weapon.isRanged()){ //TODO check ranged atk
            self_damage_mod = weapon.getIntParam(DC_ContentValsManager
             .getArmorSelfDamageParamForDmgType(damage_type));
            reduceDurability(true, weapon, weapon, m2, m1, damage, blocked, self_damage_mod);
            }
        }
        if (durabilityReductionEffect == null) {
            return 0;
        }
       target. getGame().getDungeonMaster().getExplorationMaster().
         getResetter().setResetNotRequired(false);
        return durabilityReductionEffect.getDurabilityLost();
    }

    private static MATERIAL getNaturalArmorMaterial(BattleFieldObject target) {
        return MATERIAL.PETTY;
    }

    private static void reduceDurability(boolean attacker, DC_HeroSlotItem item, DC_WeaponObj weapon, MATERIAL m1,
                                         MATERIAL m2, int damage, int blocked, int self_damage_mod) {

            if (self_damage_mod == 0)
                self_damage_mod = 100;

        int armor_vs_weapon = m2.getHardness() - m1.getHardness();

        int armor_amount =Math.max(blocked, damage/3) * self_damage_mod / 100;
//        armor_amount = MathMaster.addFactor(armor_amount, armor_vs_weapon); TODO outdated?
        if (armor_amount <= 0)
            return;
        durabilityReductionEffect =
         new DurabilityReductionEffect(attacker,
          armor_amount);
        durabilityReductionEffect.setHardness((!attacker? m1 : m2).getHardness());
        durabilityReductionEffect.setHardness2((attacker? m1 : m2).getHardness());
        Ref ref = Ref.getSelfTargetingRefCopy(item);
        ref.setID(KEYS.WEAPON, weapon.getId());

        if (armor_amount > 0)
            durabilityReductionEffect.apply(ref);
    }

    private static int spellDamage(int damage, int blocked, DAMAGE_TYPE damage_type,
                                   DC_HeroSlotItem armor, BattleFieldObject target) {
        int self_damage_mod = armor.getIntParam(DC_ContentValsManager
         .getArmorSelfDamageParamForDmgType(damage_type));
        // special cases may apply for Damage
        int amount = blocked * self_damage_mod / 100;
        DurabilityReductionEffect durabilityReductionEffect = new DurabilityReductionEffect(null,
         amount);
        durabilityReductionEffect.apply(Ref.getSelfTargetingRefCopy(armor));
        return durabilityReductionEffect.getDurabilityLost();

    }

    private static Effects getAttackItemDurabilityReductionEffects(int amount) {
        String source_ref = "{EVENT_SOURCE}";
        String target_ref = "{EVENT_TARGET}";

        return new Effects(new ConditionalEffect(new OrConditions(new ItemCondition(source_ref,
         ItemEnums.ITEM_SLOT.MAIN_HAND.getProp().getName()), new ItemCondition(source_ref,
         ItemEnums.ITEM_SLOT.OFF_HAND.getProp().getName())), new DurabilityReductionEffect(true,
         amount)), new ConditionalEffect(new ItemCondition(target_ref, ItemEnums.ITEM_SLOT.ARMOR
         .getProp().getName()), new DurabilityReductionEffect(false, amount)));
    }


    public static int damageDealt(int blocked, DC_HeroSlotItem obj, DAMAGE_TYPE dmg_type,
                                  DC_WeaponObj activeWeapon, int amount,
                                  BattleFieldObject attacked) {
        return damageDealt(blocked, obj,false, dmg_type, activeWeapon, amount, attacked);
    }



    public static int damageDealt(int blocked, DC_HeroSlotItem armorObj, boolean spell,
                                  DAMAGE_TYPE damage_type, DC_WeaponObj weapon, int damage,
                                  BattleFieldObject target) {
        if (!RuleKeeper.isRuleOn(RULE.DURABILITY))
            return 0;
        if (!checkDamageType(damage_type))
            return 0;
    if (weapon.getOwnerObj()== target)
    return 0;


        if (spell) {
            return spellDamage(damage, blocked, damage_type, armorObj, target);
        } else {
            return physicalDamage(damage, blocked, damage_type, armorObj, weapon,
             target);
        }

    }

    private static boolean checkDamageType(DAMAGE_TYPE damage_type) {
        switch (damage_type) {
            case POISON:
            case PSIONIC:
            case HOLY:
            case SHADOW:
            case DEATH:
                return false;
        }
        return true;
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

    public static boolean isSaveBrokenItem() {
        return false;
    }


    /**
     * upon each physical attack, preCheck weapon/armor of the belligerents and
     * call their damageDealt() method
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
