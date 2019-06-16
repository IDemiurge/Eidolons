package eidolons.entity.item;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.system.DC_Formulas;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.ContentValsManager;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.WEAPON_CLASS;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.util.List;

public class DC_WeaponObj extends DC_HeroSlotItem {

    private boolean mainHand;
    private boolean natural;
    private List<DC_UnitAction> attackActions;
    private DC_QuickItemObj ammo;
    private DC_QuickItemObj lastAmmo;

    public DC_WeaponObj(ObjType type, Player owner, GenericGame game, Ref ref) {
        this(type, owner, game, ref, false);
    }

    public DC_WeaponObj(ObjType type, Player owner, GenericGame game, Ref ref, boolean main_hand) {
        super(type, owner, game, ref, DC_ContentValsManager.getWeaponModifyingParams());
        this.setMainHand(main_hand);
    }

    public DC_WeaponObj(ObjType type, Unit heroObj) {
        this(type, heroObj.getOwner(), heroObj.getGame(), heroObj.getRef(), true);
    }

    @Override
    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, DC_UnitModel target, Ref REF) {
        if (REF.getActive() instanceof DC_QuickItemAction) {
            DC_Obj weapon = (DC_Obj) REF.getActive().getRef().getObj(KEYS.ITEM);
            if (weapon != null) {
                weapon.applySpecialEffects(case_type, target, REF);
            }
        } else if (isRanged()) {
            if (ref.getObj(KEYS.AMMO) instanceof DC_Obj) {
                DC_Obj ammo = (DC_Obj) ref.getObj(KEYS.AMMO);
                ammo.applySpecialEffects(case_type, target, REF);
            }
        }
        super.applySpecialEffects(case_type, target, REF);
    }

    @Override
    public void applyMods() {
        super.applyMods();
        int mod = getIntParam(PARAMS.DEFENSE_MOD);
        getHero().multiplyParamByPercent(PARAMS.DEFENSE_MOD, mod, false);
        mod = getIntParam(PARAMS.ATTACK_MOD);

        if (isMainHand()) {
            getHero().multiplyParamByPercent(PARAMS.ATTACK_MOD, mod, false);
            getHero().modifyParameter(PARAMS.ATTACK_AP_PENALTY,
                    getIntParam(PARAMS.ATTACK_AP_PENALTY), false);
            getHero().modifyParameter(PARAMS.ATTACK_STA_PENALTY,
                    getIntParam(PARAMS.ATTACK_STA_PENALTY), false);
        } else {
            getHero().multiplyParamByPercent(PARAMS.OFFHAND_ATTACK_MOD, mod, false);
            getHero().modifyParameter(PARAMS.OFFHAND_ATTACK_AP_PENALTY,
                    getIntParam(PARAMS.ATTACK_AP_PENALTY), false);
            getHero().modifyParameter(PARAMS.OFFHAND_ATTACK_STA_PENALTY,
                    getIntParam(PARAMS.ATTACK_STA_PENALTY), false);
        }
    }

    public void modified(ModifyValueEffect modifyValueEffect) {
        super.modified(modifyValueEffect);
        switch (modifyValueEffect.getMod_type()) {
            case MODIFY_BY_CONST:
                break;
            case MODIFY_BY_PERCENT:
                int mod = 2;
                break;
            case SET:
                break;
            default:
                break;

        }
    }

    @Override
    public void apply() {
        toBase();
        super.apply();
        // applyMods();
        if (!mainHand) {
            if (isWeapon()) {
                checkApplyOffhandPenalties();
            }
        } else {
            if (hero.getOffhandWeapon() == null) {
                checkApplySingleHandBonus();
            }
        }
        Ref ref = getHero().getRef();
        if (isRanged()) {
            getHero().setRangedWeapon(this);
            ref.setID(KEYS.RANGED, getId());
        }
        if (mainHand) {
            ref.setID(KEYS.WEAPON, getId());
        } else {
            ref.setID(KEYS.OFFHAND, getId());
        }
    }

    private void checkApplySingleHandBonus() {
        if (isRanged()) {
            return;
        }
        if (isTwoHanded()) {
            return;
        }

        int bonus = DC_Formulas.SINGLE_HAND_ATTACK_BONUS;
        MathMaster.addFactor(bonus, getIntParam(PARAMS.SINGLE_HAND_ATTACK_BONUS_MOD));
        MathMaster.addFactor(bonus, hero.getIntParam(PARAMS.SINGLE_HAND_ATTACK_BONUS_MOD));
        hero.modifyParamByPercent(PARAMS.ATTACK_MOD, bonus);
        bonus = DC_Formulas.SINGLE_HAND_DEFENSE_BONUS;
        bonus = MathMaster.addFactor(bonus, getIntParam(PARAMS.SINGLE_HAND_DEFENSE_BONUS_MOD));
        Integer factor = hero.getIntParam(PARAMS.SINGLE_HAND_DEFENSE_BONUS_MOD);
        if (factor != 0) {
            bonus = MathMaster.addFactor(bonus, factor);
        }
        hero.modifyParamByPercent(PARAMS.DEFENSE_MOD, bonus);
        bonus = DC_Formulas.SINGLE_HAND_DAMAGE_BONUS;
        bonus = MathMaster.addFactor(bonus, getIntParam(PARAMS.SINGLE_HAND_DAMAGE_BONUS_MOD));
        bonus = MathMaster.addFactor(bonus, hero.getIntParam(PARAMS.SINGLE_HAND_DAMAGE_BONUS_MOD));
        hero.modifyParamByPercent(PARAMS.DAMAGE_MOD, bonus);

    }

    public boolean isTwoHanded() {
        // if (mainHand)
        // if (hero.getOffhandWeapon() == null) {
        // return true;
        // }
        return checkProperty(G_PROPS.WEAPON_CLASS, StringMaster
                .getWellFormattedString(ItemEnums.WEAPON_CLASS.DOUBLE.name()))
                || checkProperty(G_PROPS.WEAPON_CLASS, StringMaster
                .getWellFormattedString(ItemEnums.WEAPON_CLASS.TWO_HANDED.name()));
    }

    public void applyUnarmedMasteryBonus() {
        PARAMETER mastery = getMastery();
        if (mastery != null) {
            getHero().modifyParameter((mainHand) ? PARAMS.ATTACK : PARAMS.OFF_HAND_ATTACK,
                    2 * DC_Formulas.getAttackFromWeaponMastery(getHero().getIntParam(mastery)));
        }
    }

    public void applyMasteryBonus() {

        getHero().modifyParameter((mainHand) ? PARAMS.ATTACK_MOD : PARAMS.OFFHAND_ATTACK_MOD,
                DC_Formulas.getAttackFromWeaponMastery(getHero().getIntParam(getMastery())) / 10);

        getHero().modifyParameter((mainHand) ? PARAMS.ATTACK : PARAMS.OFF_HAND_ATTACK,
                DC_Formulas.getAttackFromWeaponMastery(getHero().getIntParam(getMastery())));
        if (isTwoHanded() || getWeaponSize() == ItemEnums.WEAPON_SIZE.HUGE) {
            if (!isRanged()) {
                getHero().modifyParameter(
                        PARAMS.DAMAGE_MOD,
                        DC_Formulas.getDamageFromTwohandedMastery(getHero().getIntParam(
                                PARAMS.TWO_HANDED_MASTERY)));
                // modifyParameter(PARAMS.STR_DMG_MODIFIER,
            }
        }
        if (isDouble()) {
            getHero().modifyParameter(
                    PARAMS.ATTACK_MOD,
                    DC_Formulas.getAttackFromWeaponMastery(getHero().getIntParam(
                            PARAMS.DUAL_WIELDING_MASTERY) / 2));

        }

        if (isShield()) {
            modifyParameter(PARAMS.BLOCK_CHANCE, getHero().getIntParam(PARAMS.SHIELD_MASTERY));
            getHero().modifyParameter(PARAMS.BLOCK_CHANCE, getIntParam(PARAMS.BLOCK_CHANCE));
        }
    }

    private boolean isDouble() {
        return checkProperty(G_PROPS.WEAPON_CLASS, StringMaster
                .getWellFormattedString(ItemEnums.WEAPON_CLASS.DOUBLE.name()));
    }

    private PARAMETER getMastery() {
        return ContentValsManager.getPARAM(getProperty(G_PROPS.MASTERY));
    }

    private void checkApplyOffhandPenalties() {
        // off hand already uses different parameter which is lower by default;
        // dual mastery

        // TODO
        if (hero.getMainWeapon() != null) {
            if (!hero.getMainWeapon().isRanged()) {
                int penalty = Math.min(0, DC_Formulas.getMainHandDualAttackMod()
                        + getHero().getIntParam(PARAMS.DUAL_WIELDING_MASTERY));
                getHero().modifyParameter(PARAMS.ATTACK_MOD, penalty);
            } else {
                hero.setParam(PARAMS.OFFHAND_ATTACK_MOD, hero.getIntParam(PARAMS.ATTACK_MOD));
            }
        }

    }

    public int getDamageModifiers() {
        int result = 0;
        result += getDamageModifier(PARAMS.SP_DMG_MODIFIER, PARAMS.SPELLPOWER);
        result += getDamageModifier(PARAMS.AGI_DMG_MODIFIER, PARAMS.AGILITY);
        result += getDamageModifier(PARAMS.STR_DMG_MODIFIER, PARAMS.STRENGTH);
        result += getDamageModifier(PARAMS.INT_DMG_MODIFIER, PARAMS.INTELLIGENCE);
        return result;
    }

    private int getDamageModifier(PARAMS dmgModifier, PARAMS value) {
        int amount = getIntParam(dmgModifier) * getHero().getIntParam(value) / 100;
        return (amount);
    }

    @Override
    public void setRef(Ref ref) {
        if (!equipped) { // TODO preCheck ref contains *this* ?
            // HC unequip bug?
            equipped(ref);
        }
    }

    public void equippedInReserve(Ref ref) {
        //TODO igg demo fix
    }

    @Override
    public void equipped(Ref ref) {
        this.equipped = true;
        setAttackActions(null);
        if (ref == null) {
            return;
        }
        Integer ammo = ref.getId(KEYS.AMMO);
        if (ammo != null) {
            if (ammo != 0) {
                ref.setID(KEYS.AMMO, ammo);
            }
        }
        if (isRanged()) {
            ref.setID(KEYS.RANGED, getId());
        }
        if (mainHand) {
            ref.setID(KEYS.WEAPON, getId());
        } else {
            ref.setID(KEYS.OFFHAND, getId());

        }
        super.setRef(ref);
        if (ref.getSourceObj() instanceof Unit) {
            setOwnerObj((Unit) ref.getSourceObj());
            if (isRanged() && getHero() != null) {
                getHero().setRangedWeapon(this);
            }
        }
    }

    @Override
    public void newRound() {
        // toBase();

    }

    public boolean isMainHand() {
        return mainHand;
    }

    public void setMainHand(boolean mainHand) {
        this.mainHand = mainHand;
    }

    public boolean isOffhand() {
        return !mainHand;
    }

    public boolean isWeapon() {
        if (checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.NATURAL.toString())) {
            return false;
        }
        if (checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.MAGICAL.toString())) {
            return false;
        }
        return !checkProperty(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.SHIELD.toString());
    }

    @Override
    public int reduceDurabilityForDamage(int damage, int armor, int mod, boolean sim) {
        if (checkBool(GenericEnums.STD_BOOLS.INDESTRUCTIBLE)) {
            return 0;
        }
        return super.reduceDurabilityForDamage(damage, armor, mod, sim);
    }

    @Override
    protected PARAMETER getDurabilityParam() {
        return PARAMS.DAMAGE_BONUS;
    }

    public boolean isNatural() {
        return checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.NATURAL.toString(), true);
    }

    public boolean isShield() {
        return checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.SHIELD.toString());
    }

    public boolean isAmmo() {
        return checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO.toString());

    }

    public boolean isMagical() {
        return checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.MAGICAL.toString());
    }

    public boolean isRanged() {
        return checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.RANGED.toString(), true);
    }

    public boolean isMelee() {
        if (isRanged()) {
            return false;
        }
        if (isAmmo()) {
            return false;
        }
        return !isMagical();
    }

    public WEAPON_TYPE getWeaponType() {
        return new EnumMaster<WEAPON_TYPE>().retrieveEnumConst(WEAPON_TYPE.class,
                getProperty(G_PROPS.WEAPON_TYPE));
    }

    public WEAPON_GROUP getWeaponGroup() {
        return new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class,
                getProperty(G_PROPS.WEAPON_GROUP));
    }

    public WEAPON_SIZE getWeaponSize() {
        return new EnumMaster<WEAPON_SIZE>().retrieveEnumConst(WEAPON_SIZE.class,
                getProperty(G_PROPS.WEAPON_SIZE));
    }

    public WEAPON_CLASS getWeaponClass() {
        return new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class,
                getProperty(G_PROPS.WEAPON_CLASS));
    }

    @Override
    protected void applyPenaltyReductions() {
        int penalty_reduction = -getHero().getIntParam(PARAMS.STRENGTH);

        modifyParameter(PARAMS.ATTACK_MOD, -penalty_reduction, 100, true);
        modifyParameter(PARAMS.DEFENSE_MOD, -penalty_reduction, 100, true);

        modifyParameter(PARAMS.ATTACK_AP_PENALTY, penalty_reduction, 0, true);
        modifyParameter(PARAMS.ATTACK_STA_PENALTY, penalty_reduction, 0, true);

        penalty_reduction = -getHero().getIntParam(PARAMS.WILLPOWER);
        modifyParameter(PARAMS.SPELL_FOC_PENALTY, penalty_reduction, 0, true);
        modifyParameter(PARAMS.SPELL_AP_PENALTY, penalty_reduction, 0, true);

    }

    public List<DC_UnitAction> getOrCreateAttackActions() {
        if (attackActions == null)
            setAttackActions(getGame().getActionManager().getOrCreateWeaponActions(this));
        return attackActions;
    }

    public List<DC_UnitAction> getAttackActions() {
        return attackActions;
    }

    public void setAttackActions(List<DC_UnitAction> attackActions) {
        this.attackActions = attackActions;
    }

    public DC_QuickItemObj getAmmo() {
        return ammo;
    }

    public void setAmmo(DC_QuickItemObj ammo) {
        this.ammo = ammo;
        if (ammo == null) {
            getHero().getRef().removeValue(KEYS.AMMO);
            if (getHero().getRef().getActive() != null)
                getHero().getRef().getActive().getRef().removeValue(KEYS.AMMO);
            ref.removeValue(KEYS.AMMO);
        } else {
            ref.setID(KEYS.AMMO, ammo.getId());
            AnimMaster3d.preloadAtlas(ammo.getWrappedWeapon());
        }
        if (ammo != null)
            lastAmmo = ammo;
    }

    public DC_QuickItemObj getLastAmmo() {
        return lastAmmo;
    }


    @Override
    protected void applyDurability() {
        resetPercentages();
        Integer durability = getIntParam(PARAMS.DURABILITY_PERCENTAGE);
        if (isNatural() && durability <= 50 * MathMaster.MULTIPLIER)
            durability = 50 * MathMaster.MULTIPLIER;
        else if (durability <= 0) {
            broken();
            return;
        }

        multiplyParamByPercent(getDurabilityParam(), durability, false);
    }

    public String getSpriteImagePath() {
        if (!checkProperty(PROPS.SPRITE_PATH)) {
            setProperty(PROPS.SPRITE_PATH,
                    StrPathBuilder.build("main",
                            "item",
                            "weapon",
                            "sprites", getBaseType() + ".png"));
        }
        return getProperty(PROPS.SPRITE_PATH);
    }

    private String getBaseType() {
        if (!getType().isGenerated())
            return getName();
        return getProperty(G_PROPS.BASE_TYPE);
    }

    public int calculateDamageMin(Ref ref) {
        Unit source = (Unit) ref.getSourceObj();
        int damage = getIntParam(PARAMS.DAMAGE_BONUS) + getIntParam(PARAMS.DICE);
        damage += source.getIntParam(PARAMS.STRENGTH) * getIntParam(PARAMS.STR_DMG_MODIFIER) / 100;
        damage += source.getIntParam(PARAMS.AGILITY) * getIntParam(PARAMS.AGI_DMG_MODIFIER) / 100;
        damage += source.getIntParam(PARAMS.SPELLPOWER) * getIntParam(PARAMS.SP_DMG_MODIFIER) / 100;
        damage += source.getIntParam(PARAMS.INTELLIGENCE) * getIntParam(PARAMS.INT_DMG_MODIFIER) / 100;

        damage = damage * getIntParam(PARAMS.DAMAGE_MOD) / 100;
        return damage;
    }

    public int calculateDiceMax() {
        return getIntParam(PARAMS.DICE) * getIntParam(PARAMS.DIE_SIZE) * getIntParam(PARAMS.DAMAGE_MOD) / 100;
    }
}
