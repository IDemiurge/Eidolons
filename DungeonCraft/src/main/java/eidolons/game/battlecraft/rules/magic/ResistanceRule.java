package eidolons.game.battlecraft.rules.magic;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

public class ResistanceRule {

    public static boolean checkNotResisted(Ref ref) {
        int resistance = getResistance(ref);

        if (resistance >= 100) {
            return false;
        }
        if (resistance <= 0) {
            return true;
        }

        if (RandomWizard.chance(resistance)) {
            ref.getGame().getLogManager().logAlert(
             ref.getTargetObj().getName() + " has resisted "
              + ref.getObj(KEYS.SPELL).getName()
              + StringMaster.wrapInParenthesis(resistance + "%"));
            return false; // TODO special effect case?
        }

        return true;
    }

    public static int getResistanceMod(Ref ref) {
        return 100 - getResistance(ref);
    }

    public static int getResistance(Ref ref) {
        ActiveObj spell = (ActiveObj) ref.getObj(KEYS.SPELL);
        if (spell == null) {
            return 0;
        }
        Obj target = ref.getTargetObj();
        Obj source = ref.getSourceObj();
        DAMAGE_TYPE type = spell.getEnergyType();
        int specResist = 0;
        PARAMETER typeResistance = DC_ContentValsManager.getDamageTypeResistance(type);
        if (typeResistance != null) {
            specResist = target.getIntParam(typeResistance);
        }

        int resistance = specResist;
        // int resistance = target.getIntParam(PARAMS.RESISTANCE);
        // resistance = MathMaster.addFactor(resistance, specResist);
        resistance -= source.getIntParam(PARAMS.RESISTANCE_PENETRATION);

        int mod = spell.getIntParam(PARAMS.RESISTANCE_MOD);
        resistance = MathMaster.applyPercent(resistance, mod);
        return resistance;
    }

}
