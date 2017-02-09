package main.entity.type;

import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.SPELL_GROUP;
import main.content.CONTENT_CONSTS.SPELL_POOL;
import main.content.CONTENT_CONSTS.SPELL_TYPE;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.system.auxiliary.EnumMaster;

public class SpellType extends ActiveObjType {

    private static final SPELL_TYPE DEFAULT_SPELL_TYPE = SPELL_TYPE.SORCERY;
    private SPELL_GROUP spellGroup;
    private SPELL_TYPE spellType;
    private SPELL_POOL spellPool;

    public SPELL_GROUP getSpellGroup() {
        if (spellPool == null) {
            spellGroup = new EnumMaster<SPELL_GROUP>().retrieveEnumConst(
                    SPELL_GROUP.class, getProperty(G_PROPS.SPELL_GROUP));
        }
        return spellGroup;
    }

    public SPELL_TYPE getSpellType() {
        if (spellType == null) {
            spellType = new EnumMaster<SPELL_TYPE>().retrieveEnumConst(
                    SPELL_TYPE.class, getProperty(G_PROPS.SPELL_TYPE));
        }
        if (spellType == null) {
            spellType = DEFAULT_SPELL_TYPE;
        }
        return spellType;
    }

    public SPELL_POOL getSpellPool() {
        if (spellPool == null) {
            spellPool = new EnumMaster<SPELL_POOL>().retrieveEnumConst(
                    SPELL_POOL.class, getProperty(G_PROPS.SPELL_POOL));
        }
        return spellPool;

    }

    public int getCircle() {
        return getIntParam("CIRCLE");
    }

    public int getEssenceCost() {
        return getIntParam("ESS_COST");
    }

    public boolean isSorcery() {

        return getSpellType() == SPELL_TYPE.SORCERY;
    }

    public boolean isEnchantment() {

        return checkSingleProp(G_PROPS.SPELL_TYPE,
                CONTENT_CONSTS.SPELL_TYPE.ENCHANTMENT.name());
    }

    public boolean isSummoning() {

        return checkSingleProp(G_PROPS.SPELL_TYPE,
                CONTENT_CONSTS.SPELL_TYPE.SUMMONING.name());
    }

    @Override
    public boolean activate(boolean transmit) {
        return false;
    }

    @Override
    public boolean activate(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean resolve() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean activate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInterrupted() {
        // TODO Auto-generated method stub
        return false;
    }

}
