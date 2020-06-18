package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.attack.DefenseVsAttackRule;
import eidolons.game.battlecraft.rules.combat.attack.ShieldMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref.KEYS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.launch.Flags;
import main.system.math.MathMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.function.Supplier;

public class ArmorMaster {
    // is the damage already reduced by defense/... ?
    // natural resistances/armor should not reduce it in advance! so it's early
    // TODO use with natural missile spells too
    // return penetrated! TODO

    boolean simulation;
    private final DC_Game game;

    public static final  boolean isCodeReady() {
        return false;
    }

    public ArmorMaster(boolean simulation, DC_Game game) {
        this.simulation = simulation;
        this.game = game;
    }

    public static int getArmorValue(DC_Obj obj, DAMAGE_TYPE dmg_type) {
        if (dmg_type == GenericEnums.DAMAGE_TYPE.PHYSICAL) {
            return obj.getIntParam(PARAMS.ARMOR);
        }

        return obj.getIntParam(DC_ContentValsManager.getArmorParamForDmgType(dmg_type));
    }

    public static int getShieldReducedAmountForDealDamageEffect(
            DealDamageEffect effect,
            BattleFieldObject targetObj, int amount, DC_ActiveObj active) {

        if (effect.getGame().getArmorMaster().checkCanShieldBlock(active, targetObj)) {
            int shieldBlock = effect.getGame().getArmorMaster().getShieldDamageBlocked(amount, targetObj,
                    (BattleFieldObject) effect.getRef().getSourceObj(), active, null, effect.getDamageType());
            // event?
            amount -= shieldBlock;
        }


        return amount;
    }

    public static boolean isArmorUnequipAllowed(BattleFieldObject hero) {
        return hero.getGame().isSimulation();
    }

    public int getArmorBlockDamage(Damage damage) {
        return getArmorBlockDamage(false, damage.isSpell(), damage.canCritOrBlock(),
                damage.isAverage(), damage.getAmount(), damage.getTarget(), damage.getSource(),
                damage.isOffhand(),
                damage.getDmgType(), damage.getAction());
    }

    public int getArmorBlockDamage(int damage, BattleFieldObject attacked, BattleFieldObject attacker,
                                   DC_ActiveObj action) {


        if (attacked.getArmor() == null) {
            return getNaturalArmorBlock(damage, attacked, attacker, action);
        }
        boolean offhand = action.isOffhand();
        int blocked = getArmorBlockDamage(false, action instanceof Spell, !action.isZone(), action
                .isZone(), damage, attacked, attacker, offhand, getDamageType(action, attacker
                .getActiveWeapon(offhand)), action);
        return Math.min(damage, blocked);
    }

    private int getNaturalArmorBlock(int damage, BattleFieldObject attacked, BattleFieldObject attacker, DC_ActiveObj action) {
        //        DurabilityRule.physicalDamage()
        return 0;
    }

    // for spells/special actions
    public int getArmorBlockForActionDamage(int amount, DAMAGE_TYPE damage_type,
                                            BattleFieldObject targetObj, DC_ActiveObj action) {
        boolean zone = (action.isZone());
        boolean canCritOrBlock = !zone;
        boolean average = zone;
        if (!zone) {
            if (!action.isMissile() || !damage_type.isNatural()) {
                canCritOrBlock = false; // TODO astral?
            }
        }
        return getArmorBlockDamage(false, action instanceof Spell, canCritOrBlock, average,
                amount, targetObj, action.getOwnerObj(), action.isOffhand(),
                action.getEnergyType(), action);
    }

    private int getArmorBlockDamage(boolean shield, boolean spell, boolean canCritOrBlock,
                                    boolean average, Integer damage, BattleFieldObject attacked, BattleFieldObject attacker,
                                    boolean offhand, DAMAGE_TYPE dmg_type, DC_ActiveObj action) {
        /*
         * if blocks, apply armor's resistance values too
         */
        if (action == null) {
            return 0;
        }
        if (!offhand) {
            offhand = action.isOffhand();
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

        // game.getLogManager().newLogEntryNode(type, args)
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
            action.getRef().setID(KEYS.BLOCK, armorObj.getId());

            String entry = null;
            if (blockedPercentage == 0 || blocked == 0) {
                entry = attacker.getNameIfKnown()
                        + " penetrates "
                        + armorObj.getName()
                        + " with "
                        + name
                        + (action.getName().equals("Attack") ? "" : StringMaster
                        .wrapInParenthesis(action.getName())) + "!";
            } else {
                entry = armorObj.getName() + " takes " + blockedPercentage + "% of "
                        + action.getName() + StringMaster.wrapInParenthesis(name) + " absorbing "
                        + blocked + " " + dmg_type.getName() + " damage";
                DC_SoundMaster.playAttackImpactSound(attacker.getActiveWeapon(offhand), attacker,
                        attacked, damage, blocked);
            }
            game.getLogManager().log(LOG.GAME_INFO, entry, ENTRY_TYPE.DAMAGE);

        }

        return blocked;
    }

    public Integer getShieldBlockValue(int amount, DC_WeaponObj shield, BattleFieldObject attacked,
                                       BattleFieldObject attacker, DC_WeaponObj weapon, DC_ActiveObj action, boolean zone) {
       if (!isCodeReady()){
           return shield.getIntParam(PARAMS.DAMAGE_BONUS);
       }
       //ToDo-Cleanup
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            if (weapon == null) {
                dmg_type = action.getEnergyType();
            } else {
                dmg_type = weapon.getDamageType();
            }
        }
        int blockValue = shield.getIntParam(PARAMS.DAMAGE_BONUS);// getArmorValue(shield, dmg_type);
        // if blocks, full value is applied always

        if (zone) {
            // TODO always apply maximum? or half..
            // blockValue =amount*shield.getIntParam(params.cover)/100;
        }
        // TODO <?> blockValue = blockValue * (100 +
        // attacked.getIntParam(PARAMS.SHIELD_MASTERY)) / 100;
        return blockValue;
    }


    public boolean checkCanShieldBlock(DC_ActiveObj active, BattleFieldObject targetObj) {
        // if (active.isMissile())
        if (RuleKeeper.isRuleTestOn(RuleKeeper.RULE.SHIELD))
            return true;
        if (targetObj.getActiveWeapon(true) != null) {
            return targetObj.getActiveWeapon(true).isShield();
        }
        return false;
    }

    public Integer getShieldBlockChance(DC_WeaponObj shield, BattleFieldObject attacked,
                                        BattleFieldObject attacker, DC_WeaponObj weapon, DC_ActiveObj action, boolean offhand,
                                        boolean spell) {

        Integer chance = getBlockPercentage(true, spell, true, true, shield, offhand, attacker,
                attacked, action);
        chance += attacked.getIntParam(PARAMS.BLOCK_CHANCE);
        chance += -attacker.getIntParam(PARAMS.PARRY_PENETRATION);
        if (!simulation)
            if (action.getGame().getCombatMaster().isChancesOff()) {
                if (chance < 50)
                    return 0;
                return 100;
            }
        return chance;
    }

    public int getShieldDamageBlocked(Integer damage, BattleFieldObject attacked, BattleFieldObject attacker,
                                      DC_ActiveObj action, DC_WeaponObj weapon, DAMAGE_TYPE damage_type) {
        if (!checkCanShieldBlock(action, attacked)) {
            return 0;
        }
        DC_WeaponObj shield = (DC_WeaponObj) attacked.getRef().getObj(KEYS.OFFHAND);

        boolean testOn = !simulation && RuleKeeper.isRuleTestOn(RuleKeeper.RULE.SHIELD);
        if (testOn) {
            if (shield == null) {
                shield = (DC_WeaponObj) attacked.getRef().getObj(KEYS.WEAPON);
            }
        }
        String message;
        boolean spell = action.isSpell();
        boolean zone = action.isZone();
        boolean offhand = action.isOffhand();
        Integer chance =
                testOn ? 100 :
                        getShieldBlockChance(shield, attacked, attacker, weapon, action, offhand,
                                spell);
        if (!zone) {
            if (!simulation)// will be average instead
            {
                if (!RandomWizard.chance(chance)) {
                    message = StringMaster.getMessagePrefix(true, attacked.getOwner().isMe())
                            + attacked.getName() + " fails to use " + shield.getName()
                            + " to block " + action.getName()
                            + StringMaster.wrapInParenthesis(chance + "%");
                   game.getLogManager().log(LOG.GAME_INFO, message, ENTRY_TYPE.DAMAGE);
                    return 0;
                }
            }
        }
        Integer blockValue = getShieldBlockValue(damage, shield, attacked, attacker, weapon,
                action, zone);
        if (blockValue == 0) {
            if (testOn) {
                blockValue = damage;
            }
        }
        blockValue = Math.min(blockValue, damage);
        if (!simulation) {
            action.getRef().setID(KEYS.BLOCK, shield.getId());
            if (blockValue <= 0) {
                game.getLogManager().log(LOG.GAME_INFO,
                        shield.getName() + " is ineffective against " + action.getName() + "!",
                        ENTRY_TYPE.DAMAGE);
                return 0;
            }

            DC_SoundMaster.playBlockedSound(attacker, attacked, shield, weapon, blockValue, damage);
            // shield.reduceDurabilityForDamage(damage, blockValue,
            // durabilityMod);
            if (Flags.isPhaseAnimsOn()) {
                //                PhaseAnimation animation = action.getGame().getAnimationManager().getAnimation(
                //                 Attack.getAnimationKey(action));
                //                animation.addPhase(new AnimPhase(PHASE_TYPE.REDUCTION_SHIELD, chance, blockValue,
                //                 durabilityLost, damage, damage_type, shield));
            }

            FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.COUNTER_ATTACK,
                    "Shield block!", attacked);
            Integer finalBlockValue = blockValue;
            DC_WeaponObj finalShield = shield;
            // TODO gdx Review - why didn't this ever work?
            GuiEventManager.trigger(GuiEventType.SHOW_SPRITE_SUPPLIER,
                    (Supplier<SpriteAnimation>) () -> ShieldMaster.getSprite(finalShield, action, finalBlockValue));

            message = attacked.getName() + " uses " + shield.getName() + " to block" + "" + " "
                    + blockValue + " out of " + damage + " " + damage_type + " damage from " +
                    // StringMaster.wrapInParenthesis
                    (action.getName());

            game.getLogManager().log(LOG.GAME_INFO, message, ENTRY_TYPE.DAMAGE);
        }

        return blockValue;

    }

    // % to block for shield; max % of attack's damage blocked for armor
    private int getBlockPercentage(boolean shield, boolean spell, boolean canCritOrBlock,
                                   boolean average, DC_Obj armorObj, boolean offhand, BattleFieldObject attacker,
                                   BattleFieldObject attacked, DC_ActiveObj action) {
        if (!average)
            average = action.getGame().getCombatMaster().isDiceAverage();
        if (canCritOrBlock)
            canCritOrBlock = !action.getGame().getCombatMaster().isChancesOff();

        int covered = armorObj.getIntParam(shield ? PARAMS.IMPACT_AREA : PARAMS.COVER_PERCENTAGE);
        int uncovered = 100 - covered;
        DC_WeaponObj weapon = attacker.getActiveWeapon(offhand);

        Integer area = action.getIntParam(PARAMS.IMPACT_AREA);
        if (attacker instanceof Unit)
            if (action.isRanged() && !action.isThrow()) {
                if (weapon.isRanged()) {
                    try {
                        DC_QuickItemObj ammo = (DC_QuickItemObj) weapon.getRef().getObj(KEYS.AMMO);
                        if (ammo == null)
                        // ammo = attacker.getAmmo(weapon); TODO
                        {
                            ammo = ((Unit) attacker).getQuickItems().get(0);
                        }
                        weapon = ammo.getWrappedWeapon();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
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

        int attack = DefenseVsAttackRule.getAttackValue(offhand, attacker, attacked, action); // sneak?
        int defense = 0;
        int def_coef = 100;
        int atk_coef = 100;
        if (shield) {
            defense = 10 + 2 * attacked.getIntParam(PARAMS.SHIELD_MASTERY);
        } else {
            defense = DefenseVsAttackRule.getDefenseValue(attacker, attacked, action);
            def_coef += attacked.getIntParam(PARAMS.ARMORER_MASTERY);
            defense += attacked.getIntParam(PARAMS.ARMORER_MASTERY);
        }
        boolean sneak = false;// sneakCondition.preCheck(attacker);
        // boolean watched_attacker=false;// watchCondition.preCheck(attacker)
        // boolean watched_target=false;// watchCondition.preCheck(attacked)
        if (sneak) {
            def_coef = 25;
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

    private DAMAGE_TYPE getDamageType(DC_ActiveObj action, DC_WeaponObj weapon) {
        DAMAGE_TYPE dmg_type = action.getDamageType();
        if (dmg_type == null) {
            dmg_type = weapon.getDamageType();
        }
        return dmg_type;
    }

}
