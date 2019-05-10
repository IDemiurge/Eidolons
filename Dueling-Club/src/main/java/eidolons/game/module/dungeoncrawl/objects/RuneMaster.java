package eidolons.game.module.dungeoncrawl.objects;

import main.entity.type.ObjType;

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
 */
public class RuneMaster {

    public void initRune(ObjType type){
        /**
         * or should I use traditional AV?
         */

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
