package eidolons.entity.active;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.handlers.active.spell.SpellActiveMaster;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.content.enums.entity.SpellEnums.SPELL_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.elements.costs.Costs;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.sound.AudioEnums;

public class Spell extends DC_ActiveObj {

    private static final SPELL_TYPE DEFAULT_SPELL_TYPE = SPELL_TYPE.SORCERY;
    protected Costs channelingActivateCosts;
    protected Costs channelingResolveCosts;
    private SPELL_TYPE spellType;
    private SPELL_POOL spellPool;
    private SPELL_GROUP spellGroup;
    private ObjType rawType;
    private boolean channelingNow;

    public Spell(ObjType type, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);

        // DC_CostsFactory.copyCosts();
    }

//    public enum CHANNELING_SOUND {
//        ARCANE, ELDRITCH, DARK, CHAOS, HOLY, EVIL, SUMMON, BUFF, DAMAGE, MISC
//    }

    @Override
    public EntityMaster initMaster() {
        return new SpellActiveMaster(this);
    }

    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public void playCancelSound() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.SPELL_CANCELLED);
    }

    @Override
    public boolean isEffectSoundPlayed() {
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {

    }

    @Override
    public GenericEnums.DAMAGE_TYPE getDamageType() {
        if (super.getDamageType() != GenericEnums.DAMAGE_TYPE.PHYSICAL)
            return super.getDamageType();
        switch (getAspect()) {
            case NEUTRAL:
            case LIFE:
                break;
            case ARCANUM:
                if (RandomWizard.random())
                    return GenericEnums.DAMAGE_TYPE.MAGICAL;
                return GenericEnums.DAMAGE_TYPE.ARCANE;
            case DARKNESS:
                if (getSpellGroup() == SPELL_GROUP.PSYCHIC) {
                    return GenericEnums.DAMAGE_TYPE.PSIONIC;
                }
                return GenericEnums.DAMAGE_TYPE.SHADOW;
            case CHAOS:
                if (getSpellGroup() == SPELL_GROUP.DESTRUCTION) {
                    if (RandomWizard.random())
                        return GenericEnums.DAMAGE_TYPE.FIRE;
                    return GenericEnums.DAMAGE_TYPE.SONIC;
                }
                return GenericEnums.DAMAGE_TYPE.CHAOS;
            case LIGHT:
                if (getSpellGroup() == SPELL_GROUP.CELESTIAL) {
                    return GenericEnums.DAMAGE_TYPE.LIGHT;
                }
                return GenericEnums.DAMAGE_TYPE.HOLY;
            case DEATH:
                if (getSpellGroup() == SPELL_GROUP.AFFLICTION) {
                    if (RandomWizard.random())
                        return GenericEnums.DAMAGE_TYPE.ACID;
                    return GenericEnums.DAMAGE_TYPE.POISON;
                }
                return GenericEnums.DAMAGE_TYPE.DEATH;
        }
        switch (getSpellGroup()) {
            case FIRE:
                return GenericEnums.DAMAGE_TYPE.FIRE;
            case AIR:
                if (RandomWizard.chance(33))
                    return GenericEnums.DAMAGE_TYPE.LIGHTNING;
                return GenericEnums.DAMAGE_TYPE.SONIC;
            case WATER:
                if (RandomWizard.chance(33))
                    return GenericEnums.DAMAGE_TYPE.ACID;
                return GenericEnums.DAMAGE_TYPE.COLD;
            case EARTH:
                return GenericEnums.DAMAGE_TYPE.BLUDGEONING;

            case SYLVAN:
            case SAVAGE:
                return GenericEnums.DAMAGE_TYPE.LIGHT;
        }
        return super.getDamageType();
    }

    @Override
    public boolean isForcePresetTarget() {
        return false;
    }

    @Override
    public void setForcePresetTarget(boolean b) {

    }

    public void playActivateSound() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.SPELL_ACTIVATE);
    }


    public SPELL_TYPE getSpellType() {
        if (spellType == null) {
            spellType = new EnumMaster<SPELL_TYPE>().retrieveEnumConst(SPELL_TYPE.class,
                    getProperty(G_PROPS.SPELL_TYPE));
        }
        if (spellType == null) {
            spellType = DEFAULT_SPELL_TYPE;
        }
        return spellType;
    }

    public SPELL_POOL getSpellPool() {
        if (spellPool == null) {
            spellPool = new EnumMaster<SPELL_POOL>().retrieveEnumConst(SPELL_POOL.class,
                    getProperty(G_PROPS.SPELL_POOL));
        }
        return spellPool;

    }

    public boolean isMemorized() {
        return getSpellPool() == SPELL_POOL.MEMORIZED;
    }

    public boolean isVerbatim() {
        return getSpellPool() == SPELL_POOL.VERBATIM;
    }

    public boolean isDivined() {
        return getSpellPool() == SPELL_POOL.DIVINED;
    }

    public SPELL_GROUP getSpellGroup() {
        if (spellGroup == null) {
            spellGroup = new EnumMaster<SPELL_GROUP>().retrieveEnumConst(SPELL_GROUP.class,
                    getProperty(G_PROPS.SPELL_GROUP));
        }
        if (spellGroup == null)
            return SPELL_GROUP.VOID;
        return spellGroup;
    }


    public boolean isBlocked() {
        // if (ownerObj.checkProperty(PROPS.STATUS, STATUS.SILENCED.toString()))
        // return true;
        // return checkProperty(PROPS.STATUS, BLOCKED); TODO
        return false;
    }

    public boolean isPrepared() {
        // if (owner.isMe())
        return true;
        // return checkProperty(PROPS.STATUS, PREPARED);
    }


    @Override
    public void invokeClicked() {
        if (getGame().getManager().isSelecting()) {
            getGame().getManager().objClicked(this);
            return;
        }
        if (!isPrepared() || isBlocked()) {
            return;
        }

        super.invokeClicked();
    }

    @Override
    public boolean canBeActivated(Ref ref, boolean first) {
        if (getChecker().isChanneling()) {
            return channelingResolveCosts.canBePaid(ref);
        }

        return super.canBeActivated(ref, first);
    }


    public void remove() {
        getOwnerUnit().getSpells().remove(this);
        getOwnerUnit().removeProperty(getSpellProp(), getName());

    }

    private PROPERTY getSpellProp() {
        switch (getSpellPool()) {
            case MEMORIZED:
                return PROPS.MEMORIZED_SPELLS;
            case VERBATIM:
                return PROPS.VERBATIM_SPELLS;

        }
        return null;
    }


    public boolean isInstant() {
        return checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.INSTANT.toString());
    }

    public boolean isChanneling() {
        if (ChannelingRule.isTestMode())
            return true;
        if (RuleKeeper.isRuleOn(RuleEnums.RULE.CHANNELING))
            return false;
        if (RuleKeeper.isRuleTestOn(RuleEnums.RULE.CHANNELING))
            return true;
        return checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.CHANNELING.toString());
        // fix
        // return getIntParam(PARAMS.CHANNELING) > 0;
    }

    public Costs getChannelingActivateCosts() {
        return channelingActivateCosts;
    }

    public void setChannelingActivateCosts(Costs channelingActivateCosts) {
        this.channelingActivateCosts = channelingActivateCosts;
    }

    public Costs getChannelingResolveCosts() {
        return channelingResolveCosts;
    }

    public void setChannelingResolveCosts(Costs channelingResolveCosts) {
        this.channelingResolveCosts = channelingResolveCosts;
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID(Ref.KEYS.SPELL, id);
        super.setRef(ref);
    }

    @Override
    public void clicked() {
        super.clicked();

    }

    public int getCircle() {
        return getIntParam(PARAMS.CIRCLE);
    }

    public int getEssenceCost() {
        return getIntParam(PARAMS.ESS_COST);
    }

    public boolean isSorcery() {

        return getSpellType() == SPELL_TYPE.SORCERY;
    }

    public boolean isEnchantment() {

        return checkSingleProp(G_PROPS.SPELL_TYPE, SPELL_TYPE.ENCHANTMENT.name());
    }

    public boolean isSummoning() {

        return checkSingleProp(G_PROPS.SPELL_TYPE, SPELL_TYPE.SUMMONING.name());
    }

    @Override
    public void setCancelled(Boolean c) {

    }

    @Override
    public Boolean isCancelled() {
        return null;
    }

    public void setSpellPool(SPELL_POOL spellPool) {
        this.spellPool = spellPool;
    }

    public void setChannelingNow(boolean channelingNow) {
        this.channelingNow = channelingNow;
    }

    public boolean isChannelingNow() {
        return channelingNow;
    }



}
