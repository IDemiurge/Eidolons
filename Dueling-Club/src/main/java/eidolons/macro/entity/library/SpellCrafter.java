package eidolons.macro.entity.library;

import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 12/5/2018.
 *
 * how to make them persistent?
 * 1) generate all into an XML , load it if hero has CUSTOM==true
 * 2) keep this data in 'campaign's data - 'custom types'; it will be necessary either way!!!
 * it's kind of like save data really.
 *
 * random custom spells in libraries then?
 *
 * that seems not far away from actually letting ppl craft their own!
 *
 */
public class SpellCrafter {

    public void generate(){
        ObjType type = new ObjType();
//        type.setProperty(G_PROPS.TARGETING_MODE, targeting);
        //abilities

//        type.addProperty(G_PROPS.ACTIVES, abil);
        //formula per ability
        DataManager.addType(type);

    }
    public enum SPELL_BASE{
        FIRE,
        LAVA,
        //OR JUST PER MASTERY?
    }
        public enum SPELL_EFFECT_TEMPLATE{
        DAMAGE,
        DEBUFF,
        BUFF,
        POSITIVE,
        CUSTOM,

    }
        public enum SPELL_TARGETING_TEMPLATE{
        SELF,
        SINGLE,
        DOUBLE, TRIPPLE,
        TRAP,
        SPRAY,
        RAY,
        NOVA,
        WAVE,
        CHAIN,
        BLAST
    }
}
