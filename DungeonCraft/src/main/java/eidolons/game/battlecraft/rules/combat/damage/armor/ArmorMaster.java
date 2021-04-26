package eidolons.game.battlecraft.rules.combat.damage.armor;

import com.google.inject.internal.util.ImmutableList;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.MultiDamage;
import eidolons.game.battlecraft.rules.mechanics.DurabilityRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.math.roll.DiceMaster;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.List;

import static main.content.enums.GenericEnums.*;
import static main.content.enums.GenericEnums.DieType.d10;
import static main.content.enums.entity.NewRpgEnums.*;

public class ArmorMaster {
    boolean simulation;
    StringBuilder toLog= new StringBuilder();
    DC_Game game;

    public enum ArmorLayer {
        Inner,
        Outer,
        Cloak,
        Helmet
    }

    public List<ArmorLayer> getLayersForHitType(HitType hitType, boolean sneak) {
        switch (hitType) {
            case graze:
                if (sneak)
                    return ImmutableList.of(ArmorLayer.Cloak, ArmorLayer.Outer);
                return ImmutableList.of(ArmorLayer.Outer);
            case hit:
                if (sneak)
                    return ImmutableList.of(ArmorLayer.Cloak, ArmorLayer.Inner, ArmorLayer.Outer);
                return ImmutableList.of(ArmorLayer.Inner, ArmorLayer.Outer);
            case critical_hit:
                if (sneak)
                    return ImmutableList.of(ArmorLayer.Cloak, ArmorLayer.Helmet, ArmorLayer.Inner);
                return ImmutableList.of(ArmorLayer.Helmet, ArmorLayer.Inner);
            case deadeye:
                if (sneak)
                    return new ArrayList<>();
                return ImmutableList.of(ArmorLayer.Helmet);
        }
        return new ArrayList<>();
    }

    public ArmorMaster(boolean simulation, DC_Game game) {
        this.simulation = simulation;
        this.game = game;
    }

    public void damageEffect(Damage damage, boolean sneak, HitType hitType) {
/*
diff from attack?
 */
        if (!simulation)
            log();
    }

    private void log() {
        game.getLogManager().log(LogMaster.LOG.GAME_INFO, toLog.toString());
        toLog = new StringBuilder();
    }

    public Damage processDamage(Damage damage) {
        return processDamage(damage, damage.isSneak(), damage.getHitType(), damage.isAttack());
    }
    public Damage processDamage(Damage damage, boolean sneak, HitType hitType, boolean weaponAttack) {
        List<ArmorLayer> armorLayers = getLayersForHitType(hitType, sneak);
        for (ArmorLayer armorLayer : armorLayers) {
            DC_ArmorObj armor = getArmor(armorLayer, (Unit) damage.getTarget());
            if (armor == null) {
                continue;
            }
            int blocked = getBlockedAmount(armorLayer, damage.getAmount(), damage.getDmgType(),
                    weaponAttack,  damage.getModifiers(), damage.getRef());
            String msg = "";
            if (damage.getAmount() - blocked > 0) {
                msg = getLoggedMsg(armor, blocked, damage);
                damage.setAmount(damage.getAmount() - blocked);
            } else {
                msg = getLoggedMsgNegated(armor, damage);
                damage.setAmount(0);
            }
            damage.setBlocked(blocked);
            if (!simulation)
                toLog.append(msg);
        }
        // already handled by DamageDealer
        // if (damage instanceof MultiDamage) {
        //     processMultiDamage((MultiDamage) damage, sneak, hitType, weaponAttack);
        // }
        return damage;
    }

    private String getLoggedMsgNegated(DC_ArmorObj armorObj, Damage damage) {
        return armorObj.getName() + " negates " + damage;
    }

    private String getLoggedMsg(DC_ArmorObj armorObj, int blocked, Damage damage) {
        return armorObj.getName() + " absorbs " + blocked + " from " + damage;
    }

    private MultiDamage processMultiDamage(MultiDamage damage, boolean sneak, HitType hitType, boolean weaponAttack) {
        List<Damage> list = damage.getAdditionalDamage();
        for (Damage dmg : list) {
            processDamage(dmg, sneak, hitType, weaponAttack);
        }
        return damage;
    }

    private int getBlockedAmount(ArmorLayer armorLayer, Integer amount, DAMAGE_TYPE dmgType, boolean weaponAttack,
                                 DAMAGE_MODIFIER[] modifiers, Ref ref) {
        Unit attacker = (Unit) ref.getSourceObj();
        Unit attacked = (Unit) ref.getTargetObj();
        DC_ArmorObj armor = getArmor(armorLayer, attacked);
        if (armor == null) {
            return 0;
        }
        int absorbedPerc = getAbsorbedPerc(armor, dmgType, modifiers, attacker, attacked, simulation);

        int armorValue = getArmorValue(armor, dmgType);
        int blocked = Math.min(armorValue, MathMaster.getFractionValue(amount, absorbedPerc));

        if (!weaponAttack) {
            int modifier = 100 + attacker.getIntParam(PARAMS.DURABILITY_DAMAGE_MOD)
                    - attacked.getIntParam(PARAMS.DURABILITY_SELF_DAMAGE_MOD);
            absorbed(blocked, armor, dmgType, modifier);
        } else {
            //TODO weapon!
        }

        return blocked;
    }

    private DC_ArmorObj getArmor(ArmorLayer armorLayer, Unit attacked) {
        switch (armorLayer) {
            case Inner:
                return attacked.getInnerArmor();
            case Outer:
                return attacked.getArmor();
            case Cloak:
                return attacked.getGarment();
            case Helmet:
                return attacked.getHeadwear();
        }
        return attacked.getArmor();
    }

    private int getAbsorbedPerc(DC_ArmorObj armor, DAMAGE_TYPE dmgType, DAMAGE_MODIFIER[] modifiers,
                                Unit attacker, Unit attacked, boolean average) {
        int tDice = DiceMaster.getDefaultDieNumber(attacked);
        int sDice = DiceMaster.getDefaultDieNumber(attacker); //TODO add 'case'
        int tValue = average ? DiceMaster.average(d10, tDice) : DiceMaster.roll(d10, attacked, tDice, false);
        tValue += attacked.getIntParam(PARAMS.ARMOR_BLOCK_BONUS);
        int sValue = average ? DiceMaster.average(d10, tDice) : DiceMaster.roll(d10, attacker, sDice, false);
        sValue += attacker.getIntParam(PARAMS.ARMOR_PENETRATION);


        Integer base = armor.getIntParam(PARAMS.COVER_PERCENTAGE);
        return base + tValue - sValue;
    }

    private void absorbed(Integer base, DC_ArmorObj armor, DAMAGE_TYPE dmgType, int modifier) {
        DurabilityRule.spellDamage(base,   dmgType, armor, modifier);
    }


    public static int getArmorValue(DC_Obj obj, DAMAGE_TYPE dmg_type) {
        if (dmg_type == GenericEnums.DAMAGE_TYPE.PHYSICAL) {
            return obj.getIntParam(PARAMS.ARMOR);
        }
        return obj.getIntParam(DC_ContentValsManager.getArmorParamForDmgType(dmg_type));
    }

}
