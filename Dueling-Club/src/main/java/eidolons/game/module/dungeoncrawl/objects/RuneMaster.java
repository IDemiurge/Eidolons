package eidolons.game.module.dungeoncrawl.objects;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

/**
 * town portal
 * map effects
 * > reveal area + pan camera
 * >
 * >
 * combat effects
 *
 * buffs
 * > per param
 * > perhaps via mass icons?
 *
 * Those mini icons are PERKS godsent
 *
 *
 * Ability associated with it - for QI
 *
 *
 * Active Runes to be used once
 * In the future - some objects will have this AE GLOW - and so we use them, ONCE
 *
 *
 */
public class RuneMaster {

    public void initRune(ObjType type){
        /**
         * or should I use traditional AV?
         */

    }
    public  enum  RUNE_VARIANT{
        //which would I have really? maybe 3

        // I could do it via VFX - like circles ?

        NOR,
        TAR,
        URD,
        LIR,

    }
    public void createRuneEffect(Unit unit, ObjType shrine){ //could work via delayed inv use?
        RUNE_VARIANT rune = getRune(shrine);
        PARAMS paramBoosted = chooseParam(unit, shrine, rune);
        float duration= 20;

        String buffName = rune.toString() + " Rune" + StringMaster.wrapInParenthesis(paramBoosted.getName());
//        RUNE_VARIANT  //sprite and icon

        String amount= getAmount(paramBoosted);
        Effect effect= new ModifyValueEffect(paramBoosted, Effect.MOD.MODIFY_BY_PERCENT, amount);

        new AddBuffEffect(buffName, effect, (int) duration).apply(Ref.getSelfTargetingRefCopy(unit));

    }

    private String getAmount(PARAMS paramBoosted) {
        switch (paramBoosted) {
            case ATTACK_MOD:
                return "25";
            case DEFENSE_MOD:
                return "35";
            case STEALTH:
                return "45";
            case SIGHT_RANGE:
                return "50";
        }
        return "0";
    }

    private PARAMS chooseParam(Unit unit, ObjType shrine, RUNE_VARIANT rune) {
        switch (rune) {
            case NOR:
                return PARAMS.ATTACK_MOD;
            case TAR:
                return PARAMS.DEFENSE_MOD;
            case URD:
                return PARAMS.STEALTH;
            case LIR:
                return PARAMS.SIGHT_RANGE;
        }
        return null;
    }

    private RUNE_VARIANT getRune(ObjType shrine) {

        return RUNE_VARIANT.LIR;
    }

    public void interact(){

    }
    public void tryPickUp(){

    }
        public void runeActivate(){
        RUNE_EFFECT effect = null;
        String argument;

        assert effect != null;
        switch (effect) {

            case TOWN_PORTAL:
                break;
            case SHRINE_BUFF:
                break;
            case VALUE_RESTORE:
                break;
            case SUMMON_ENEMY:
                break;
            case DAMAGE_AREA:

                break;
        }

    }
    public enum RUNE_EFFECT {
        TOWN_PORTAL,
        SHRINE_BUFF,   //OFFENSE, DEFENSE,
        VALUE_RESTORE,
        SUMMON_ENEMY,
        DAMAGE_AREA,

    }
        public enum RUNE_TYPE {
        TOWN_PORTAL,

        ;
        // "Invoke the rune's power now or try to extract it for a later time? (%)
    }

}
