package logic.calculation.damage;

import elements.content.enums.types.CombatTypes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/20/2023
 * is there anything more to this?
 *
 * PAIRS vs MAP - what if some action will have multiple atks of same dmg type?
 */
public class Damage {
    CombatTypes.DamageType type;
    Integer amount;

    public Damage(CombatTypes.DamageType type, Integer amount) {
        this.type = type;
        this.amount = amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
    public CombatTypes.DamageType getType() {
        return type;
    }
    // Map<CombatTypes.DamageType, Integer> damageMap= new LinkedHashMap<>();
    //
    // public Damage() {
    // }
    //
    // public Damage(Map<CombatTypes.DamageType, Integer> damageMap) {
    //     this.damageMap = damageMap;
    // }
    // //modifiers - periodic? self-inflicted? etc etc
    // //sources and REF SYSTEM?
    // //
    //
    // public Map<CombatTypes.DamageType, Integer> getDamageMap() {
    //     return damageMap;
    // }
}
