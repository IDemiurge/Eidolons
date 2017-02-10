package main.game.battlefield;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.attack.Attack;
import main.rules.mechanics.DurabilityRule;
import main.system.DC_SoundMaster;
import main.system.auxiliary.LogMaster.LOG;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.PhaseAnimation;
import main.system.math.MathMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class ArmorMaster {
    // is the damage already reduced by defense/... ?
    // natural resistances/armor should not reduce it in advance! so it's early
    // TODO use with natural missile spells too
    // return penetrated! TODO

    boolean simulation;

    public ArmorMaster(boolean simulation) {
        this.simulation = simulation;
    }

    public static int getArmorValue(DC_Obj obj, DAMAGE_TYPE dmg_type) {
        if (dmg_type == DAMAGE_TYPE.PHYSICAL) {
            return obj.getIntParam(PARAMS.ARMOR);
        }

        return obj.getIntParam(DC_ContentManager.getArmorParamForDmgType(dmg_type));
    }

    public static boolean isArmorUnequipAllowed(DC_HeroObj hero) {
        if (hero.getGame().isSimulation()) {
            return true;
        }
        return false;
    }

    public int getArmorBlockDamage(Damage damage) {
        return getArmorBlockDamage(damage.shield, damage.spell, damage.canCritOrBlock,
                damage.average, damage.damage, damage.attacked, damage.attacker, damage.offhand,
                damage.dmg_type, damage.action);
    }

    public int getArmorBlockDamage(boolean shield, boolean spell, boolean canCritOrBlock,
                                   boolean average, Integer damage, DC_HeroObj attacked, DC_HeroObj attacker,
                                   boolean offhand, DAMAGE_TYPE dmg_type, DC_ActiveObj action) {
        /*
		 * if blocks, apply armor's resistance values too
		 */
        if (!offhand) {
            offhand = action.isOffhand();
        }
        if (action == null) {
            return 0;
        }
        DC_ArmorObj armorObj = attacked.getArmor();
        if (armorObj == null) {
            return 0;
        }
        if (dmg_type == null) {
            dmg_type = action.getDamageType();
        }
        if (simulation) {
            average = true;
            canCritOrBlock = true;
        }
        int blockedPercentage = getBlockPercentage(shield, spell, canCritOrBlock, average,
                armorObj, offhand, attacker, attacked, action);

        // action.getGame().getLogManager().newLogEntryNode(type, args)
        // TODO ??? damage =
        // getReducedDamageForArmorResistances(blockedPercentage, armorObj,
        // damage, dmg_type);
        int maxBlockValue = damage * blockedPercentage / 100;
        int blocked = Math.min(getArmorValue(armorObj, dmg_type), maxBlockValue);
        // blocked = MathMaster.applyMod(amount, mod);
        // blocked -= penetration;
        String name = "[No Weapon]";
        if (spell) {
            name = action.getName();
        } else if (attacker.getActiveWeapon(offhand) != null) {
            name = attacker.getActiveWeapon(offhand).getName();
        }
        if (!simulation) {
            String entry = armorObj.getName() + " takes " + blockedPercentage + "% of "
                    + action.getName() + StringMaster.wrapInParenthesis(name) + " absorbing "
                    + blocked + " " + dmg_type.getName() + " damage";
            if (blockedPercentage == 0 || blocked == 0) {
                entry = attacker.getNameIfKnown()
                        + " penetrates "
                        + armorObj.getName()
                        + " with "
                        + name
                        + (action.getName().equals("Attack") ? "" : StringMaster
                        .wrapInParenthesis(action.getName())) + "!";
            } else {
                DC_SoundMaster.playAttackImpactSound(attacker.getActiveWeapon(offhand), attacker,
                        attacked, damage, blocked);
                int durabilityLost = reduceDurability(blocked, armorObj, spell, dmg_type, attacker
                        .getActiveWeapon(offhand), damage);
                if (!shield) {
                    PhaseAnimation animation = action.getGame().getAnimationManager().getAnimation(
                            Attack.getAnimationKey(action));
                    animation.addPhase(new AnimPhase(PHASE_TYPE.REDUCTION_ARMOR, blockedPercentage,
                            blocked, durabilityLost, damage, dmg_type, armorObj));
                }

            }
            if (!simulation) {
                action.getGame().getLogManager().log(LOG.GAME_INFO, entry, ENTRY_TYPE.DAMAGE);
            }

        }

        return blocked;
    }

    public int getArmorBlockDamage(int damage, DC_HeroObj attacked, DC_HeroObj attacker,
                                   DC_ActiveObj action) {
        // TODO Auto-generated method stub
        boolean offhand = action.isOffhand();
        return getArmorBlockDamage(false, action instanceof DC_SpellObj, !action.isZone(), action
                .isZone(), damage, attacked, attacker, offhand, getDamageType(action, attacker
                .getActiveWeapon(offhand)), action);
    }

    // private Integer getReducedDamageForArmorResistances(int
    // blockedPercentage,
    // DC_ArmorObj armorObj, Integer damage, DAMAGE_TYPE dmg_type) {
    // // TODO special?
    // return damage;
    // }

    private DAMAGE_TYPE getDamageType(DC_ActiveObj action, DC_WeaponObj weapon) {
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            dmg_type = weapon.getDamageType();
        }
        return dmg_type;
    }

    // for spells/spec effect actions TODO rename
    public int getArmorDamageEffectBlocked(int amount, DAMAGE_TYPE damage_type,
                                           DC_HeroObj targetObj, DC_ActiveObj action) {
        boolean zone = (action.isZone());
        boolean canCritOrBlock = !zone;
        boolean average = zone;
        if (!zone) {
            if (!action.isMissile() || !damage_type.isNatural()) {
                canCritOrBlock = false; // TODO astral?
            }
        }
        return getArmorBlockDamage(false, action instanceof DC_SpellObj, canCritOrBlock, average,
                amount, targetObj, action.getOwnerObj(), action.isOffhand(),
                action.getEnergyType(), action);
    }

    private int reduceDurability(int blocked, DC_HeroSlotItem armorObj, boolean spell,
                                 DAMAGE_TYPE damage_type, DC_WeaponObj weapon, int damage) {

        if (!spell) {
            return DurabilityRule.physicalDamage(damage, blocked, damage_type, armorObj, weapon,
                    simulation);
        } else {
            return DurabilityRule.spellDamage(damage, blocked, damage_type, armorObj, simulation);
        }

    }

    public Integer getShieldBlockValue(int amount, DC_WeaponObj shield, DC_HeroObj attacked,
                                       DC_HeroObj attacker, DC_WeaponObj weapon, DC_ActiveObj action, boolean zone) {
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            if (weapon == null) {
                dmg_type = action.getEnergyType();
            } else {
                dmg_type = weapon.getDamageType();
            }
        }
        int blockValue = getArmorValue(shield, dmg_type);
        // if blocks, full value is applied always

        if (zone) {
            // TODO always apply maximum? or half..
            // blockValue =amount*shield.getIntParam(params.cover)/100;
        }
        // TODO <?> blockValue = blockValue * (100 +
        // attacked.getIntParam(PARAMS.SHIELD_MASTERY)) / 100;
        return blockValue;
    }

    public boolean checkCanShieldBlock(DC_ActiveObj active, DC_HeroObj targetObj) {
        // if (active.isMissile())
        if (targetObj.getActiveWeapon(true) != null) {
            if (targetObj.getActiveWeapon(true).isShield()) {
                return true;
            }
        }
        return false;
    }

    public Integer getShieldBlockChance(DC_WeaponObj shield, DC_HeroObj attacked,
                                        DC_HeroObj attacker, DC_WeaponObj weapon, DC_ActiveObj action, boolean offhand,
                                        boolean spell) {

        Integer chance = getBlockPercentage(true, spell, true, true, shield, offhand, attacker,
                attacked, action);
        chance += attacked.getIntParam(PARAMS.BLOCK_CHANCE);
        chance += -attacker.getIntParam(PARAMS.PARRY_PENETRATION);
        if (!simulation) {

        }
        return chance;
    }

    public int getShieldDamageBlocked(Integer damage, DC_HeroObj attacked, DC_HeroObj attacker,
                                      DC_ActiveObj action, DC_WeaponObj weapon, DAMAGE_TYPE damage_type) {
        DC_WeaponObj shield = (DC_WeaponObj) attacked.getRef().getObj(KEYS.OFFHAND);
        String message;
        boolean spell = action.isSpell();
        boolean zone = action.isZone();
        boolean offhand = action.isOffhand();
        Integer chance = getShieldBlockChance(shield, attacked, attacker, weapon, action, offhand,
                spell);
        if (!zone) {
            if (!simulation)// will be average instead
            {
                if (!RandomWizard.chance(chance)) {
                    message = StringMaster.getMessagePrefix(true, attacked.getOwner().isMe())
                            + attacked.getName() + " fails to use " + shield.getName()
                            + " to block " + action.getName()
                            + StringMaster.wrapInParenthesis(chance + "%");
                    action.getGame().getLogManager().log(LOG.GAME_INFO, message, ENTRY_TYPE.DAMAGE);
                    return 0;
                }
            }
        }
        Integer blockValue = getShieldBlockValue(damage, shield, attacked, attacker, weapon,
                action, zone);
        int durabilityLost = reduceDurability(blockValue, shield, spell, damage_type, attacker
                .getActiveWeapon(offhand), damage);
        // shield.getIntParam(PARAMS.DAMAGE_BONUS); TODO so strength may
        // increase it? ...
        // RandomWizard.getRandomIntBetween(attacked.getIntParam(PARAMS.OFF_HAND_MIN_DAMAGE),
        // attacked
        // .getIntParam(PARAMS.OFF_HAND_MAX_DAMAGE));
        // blockValue = blockValue * (100 +
        // attacked.getIntParam(PARAMS.SHIELD_MASTERY)) / 100;
        if (!simulation) {
            if (blockValue <= 0) {
                action.getGame().getLogManager().log(LOG.GAME_INFO,
                        shield.getName() + " is ineffective against " + action.getName() + "!",
                        ENTRY_TYPE.DAMAGE);
                return 0;
            }
        }
        blockValue = Math.min(blockValue, damage);
        if (!simulation) {
            DC_SoundMaster.playBlockedSound(attacker, attacked, shield, weapon, blockValue, damage);
            // shield.reduceDurabilityForDamage(damage, blockValue,
            // durabilityMod);

            // if (blockValue > damage) {
            // message = attacked.getName() + " uses " + shield.getName() +
            // " to block ";
            // log(StringMaster.getMessagePrefix(false,
            // attacked.getOwner().isMe())
            // + message);
            // action.getGame().getLogManager().log(LOG.GAME_INFO, message,
            // ENTRY_TYPE.DAMAGE);
            // return damage;
            // }
            PhaseAnimation animation = action.getGame().getAnimationManager().getAnimation(
                    Attack.getAnimationKey(action));
            animation.addPhase(new AnimPhase(PHASE_TYPE.REDUCTION_SHIELD, chance, blockValue,
                    durabilityLost, damage, damage_type, shield));

            message = attacked.getName() + " uses " + shield.getName() + " to block" + "" + " "
                    + blockValue + " out of " + damage + " " + damage_type + " damage from " +
                    // StringMaster.wrapInParenthesis
                    (action.getName());

            action.getGame().getLogManager().log(LOG.GAME_INFO, message, ENTRY_TYPE.DAMAGE);
        }

        return blockValue;

    }

    // % to block for shield; max % of attack's damage blocked for armor
    private int getBlockPercentage(boolean shield, boolean spell, boolean canCritOrBlock,
                                   boolean average, DC_Obj armorObj, boolean offhand, DC_HeroObj attacker,
                                   DC_HeroObj attacked, DC_ActiveObj action) {
        int covered = armorObj.getIntParam(PARAMS.COVER_PERCENTAGE);
        int uncovered = 100 - covered;
        DC_WeaponObj weapon = attacker.getActiveWeapon(offhand);

        Integer area = action.getIntParam(PARAMS.IMPACT_AREA);
        if (action.isRanged() && !action.isThrow()) {
            if (weapon.isRanged()) {
                try {
                    DC_QuickItemObj ammo = (DC_QuickItemObj) weapon.getRef().getObj(KEYS.AMMO);
                    if (ammo == null)
                        // ammo = attacker.getAmmo(weapon); TODO
                    {
                        ammo = attacker.getQuickItems().get(0);
                    }
                    weapon = ammo.getWrappedWeapon();
                } catch (Exception e) {
                    e.printStackTrace();
                    area = 15;
                }
            }
        }
        if (!spell) {
            if (area == 0) {
                area = weapon.getIntParam(PARAMS.IMPACT_AREA);
            }
        }
        int maximum = covered;
        int minimum = area - uncovered;
        if (minimum < 0) {
            minimum = 0;
        }

        if (!canCritOrBlock) {
            if (simulation || average) {
                return MathMaster.getAverage(minimum, maximum);
            }
            return RandomWizard.getRandomIntBetween(minimum, maximum);

        }

        int attack = DC_AttackMaster.getAttackValue(offhand, attacker, attacked, action); // sneak?
        int defense = DC_AttackMaster.getDefenseValue(attacker, attacked, action);
        boolean sneak = false;// sneakCondition.check(attacker);
        // boolean watched_attacker=false;// watchCondition.check(attacker)
        // boolean watched_target=false;// watchCondition.check(attacked)

        int def_coef = 100;
        int atk_coef = 100;
        if (sneak) {
            def_coef = 25;
        }
        if (shield) {
            defense = 10 + 2 * attacked.getIntParam(PARAMS.SHIELD_MASTERY);
        } else {
            def_coef += attacked.getIntParam(PARAMS.ARMORER_MASTERY);
            defense += attacked.getIntParam(PARAMS.ARMORER_MASTERY);
        }
        // TODO action must already have those params from attacker!
        if (!spell) {
            atk_coef = atk_coef * action.getIntParam(PARAMS.ARMOR_MOD) / 100;
            atk_coef += action.getIntParam(PARAMS.ARMOR_PENETRATION);
        }
        // else accuracy/resist penetr

        int blockPercBonus = (def_coef * defense - atk_coef * attack) / 100;

        if (shield) {
            return Math.max(0, MathMaster.addFactor((maximum + minimum) / 2, blockPercBonus));
        }

        // roll accuracy for minimum? or roll between minimum and maximum?

        // TODO OR MAKE REFLEX ROLL TO DOUBLE BLOCK CHANCE?
        if (simulation || average) {
            return (minimum + maximum) / 2;
        }
        boolean crit_or_block = RandomWizard.chance(Math.abs(blockPercBonus));
        if (crit_or_block) {
            if (blockPercBonus > 0) {
                return maximum;
            }
            return minimum;
        }
        int percentageBlocked = RandomWizard.getRandomIntBetween(minimum, maximum);

        return percentageBlocked;
    }

}
