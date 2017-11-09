package main.entity.active;

import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.content.enums.entity.SpellEnums.SPELL_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.elements.costs.Costs;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.active.spell.SpellActiveMaster;
import main.entity.type.ObjType;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.battlecraft.rules.magic.ChannelingRule;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.EnumMaster;
import main.system.graphics.Sprite;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class DC_SpellObj extends DC_ActiveObj {

    private static final SPELL_TYPE DEFAULT_SPELL_TYPE = SpellEnums.SPELL_TYPE.SORCERY;
    protected Costs channelingActivateCosts;
    protected Costs channelingResolveCosts;
    private SPELL_TYPE spellType;
    private SPELL_POOL spellPool;
    private SPELL_GROUP spellGroup;
    private ObjType rawType;
    public DC_SpellObj(ObjType type, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);

        // DC_CostsFactory.copyCosts();
    }

    @Override
    public EntityMaster initMaster() {
        return new SpellActiveMaster(this);
    }

    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public void playCancelSound() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_CANCELLED);
    }

    @Override
    public boolean isEffectSoundPlayed() {
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {

    }

    @Override
    public boolean isForcePresetTarget() {
        return false;
    }

    @Override
    public void setForcePresetTarget(boolean b) {

    }

    public void playActivateSound() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_ACTIVATE);
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
        ownerObj.getSpells().remove(this);
        ownerObj.removeProperty(getSpellProp(), getName());

    }

    private PROPERTY getSpellProp() {
        switch (getSpellPool()) {
            case DIVINED:
                return PROPS.DIVINED_SPELLS;
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
        if (RuleMaster.isRuleOn(RULE.CHANNELING))
            return false;
        if (RuleMaster.isRuleTestOn(RULE.CHANNELING))
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

        return getSpellType() == SpellEnums.SPELL_TYPE.SORCERY;
    }

    public boolean isEnchantment() {

        return checkSingleProp(G_PROPS.SPELL_TYPE, SpellEnums.SPELL_TYPE.ENCHANTMENT.name());
    }

    public boolean isSummoning() {

        return checkSingleProp(G_PROPS.SPELL_TYPE, SpellEnums.SPELL_TYPE.SUMMONING.name());
    }


    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public void setCancelled(Boolean c) {

    }

    @Override
    public Boolean isCancelled() {
        return null;
    }


    public enum CHANNELING_SOUND {
        ARCANE, ELDRITCH, DARK, CHAOS, HOLY, EVIL, SUMMON, BUFF, DAMAGE, MISC

    }

}
