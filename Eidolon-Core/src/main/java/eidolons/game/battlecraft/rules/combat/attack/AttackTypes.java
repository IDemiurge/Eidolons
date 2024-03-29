package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.entity.feat.active.ActiveObj;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Entity;
import main.system.auxiliary.ContainerUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static main.content.enums.GenericEnums.DAMAGE_TYPE.*;

public class AttackTypes {

    //TODO NF Rules
    public static boolean canActivateAny(ActiveObj active) {
        return true;
    }

    public static List<Entity> getAttackTypes(ActiveObj activeObj) {
        return null;
    }

    public enum AttackType {
        standard(100, 35, 10),
        quick(75, 25, 5),
        power(125, 50, 20),
        special(0, 0, 0),
        //unarmed? dual?
        ;
        int dmgMod;
        int atbMod;
        int atkMod; //usually a thing, eh?
        int toughnessCost; // in absolute value? Or % max?
        int strDmgMod, agiDmgMod; //TODO not just from Weapon, for sure!

        AttackType(int dmgMod, int atbMod, int toughnessCost) {
            this.dmgMod = dmgMod;
            this.atbMod = atbMod;
            this.toughnessCost = toughnessCost;
        }
    }

    public enum AttacksSet {
        dagger("Slash;Stab;Jab;Back Stab", SLASHING, PIERCING, PIERCING),
        sword("Slash;Stab;Jab;Back Stab", SLASHING, PIERCING, PIERCING),
        ;

        DAMAGE_TYPE[] standard_quick_power;
        String names; //generate objTypes then? The game's logic now is that we need a concrete ActiveObj

        AttacksSet(String names, DAMAGE_TYPE... standard_quick_power) {
            this.names = names;
            this.standard_quick_power = standard_quick_power;
        }
    }

    public static void generateAtkTypes() {
        //generate special too , then edit it in AV?
        Map<String, Pair<AttackType, DAMAGE_TYPE>> atks = new HashMap<>();
        AttackType[] types= new AttackType[]{
                AttackType.quick, AttackType.standard, AttackType.power
        };
        for (AttacksSet value : AttacksSet.values()) {
            int i=0;
            for (String substring : ContainerUtils.openContainer(value.names)) {
                atks.put(substring, new ImmutablePair<>(types[i], value.standard_quick_power[i]));
                i++;
            }
        }
    }
}
