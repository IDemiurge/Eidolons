package main.entity.obj;

import main.ability.DC_CostsFactory;
import main.ability.conditions.StatusCheckCondition;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effect.SPELL_MANIPULATION;
import main.client.cc.logic.HeroAnalyzer;
import main.client.cc.logic.spells.DivinationMaster;
import main.client.cc.logic.spells.SpellUpgradeMaster;
import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.*;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.elements.conditions.Condition;
import main.elements.conditions.NotCondition;
import main.elements.conditions.Requirement;
import main.elements.conditions.StringComparison;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.player.Player;
import main.rules.mechanics.ChannelingRule;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.graphics.Sprite;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedList;

public class DC_SpellObj extends DC_ActiveObj {

    private static final SPELL_TYPE DEFAULT_SPELL_TYPE = SPELL_TYPE.SORCERY;
    private SPELL_TYPE spellType;
    private SPELL_POOL spellPool;
    private SPELL_GROUP spellGroup;
    private ObjType rawType;

    public DC_SpellObj(ObjType type, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);

        // DC_CostsFactory.copyCosts();
    }

    @Override
    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS, SPELL_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public void playCancelSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.SPELL_CANCELLED);
    }

    public void playActivateSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.SPELL_ACTIVATE);
    }

    @Override
    protected void applyPenalties() {

        super.applyPenalties();
    }

    @Override
    protected void addCustomMods() {
        if (ownerObj.getCustomParamMap() == null) {
            return;
        }
        super.addCustomMods();

        for (PARAMETER param : DC_ContentManager.getCostParams()) {
            addCustomMod(
                    main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_GROUP,
                    getSpellGroup().toString(), param, false);
            addCustomMod(
                    main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_POOL,
                    getSpellPool().toString(), param, false);
            addCustomMod(
                    main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_TYPE,
                    getSpellType().toString(), param, false);

            addCustomMod(main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_GROUP,
                    getSpellGroup().toString(), param, true);
            addCustomMod(main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_POOL,
                    getSpellPool().toString(), param, true);
            addCustomMod(main.content.CONTENT_CONSTS.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_TYPE,
                    getSpellType().toString(), param, true);
        }
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
        if (spellPool == null) {
            spellGroup = new EnumMaster<SPELL_GROUP>().retrieveEnumConst(SPELL_GROUP.class,
                    getProperty(G_PROPS.SPELL_GROUP));
        }
        return spellGroup;
    }

    @Override
    public void toBase() {
        // if (getGame().isSimulation())
        // if (rawType != null)
        // type = new ObjType(rawType);
        Chronos.mark(getName() + " reset");
        super.toBase();
        String prev_req = getParam(PARAMS.FOC_REQ);
        if (StringMaster.isEmpty(prev_req)) {
            prev_req = "0";
        }
        setParam(PARAMS.FOC_REQ, StringMaster.wrapInParenthesis(prev_req)
                + ((!focReqMod.equals("0")) ? focReqMod : ""));

        // setRequirement(PARAMS.C_FOCUS, getParam(PARAMS.FOC_REQ));

        for (PARAMETER costModParam : getCostMods().keySet()) {
            String prev_value = getParam(costModParam);
            if (StringMaster.isEmpty(prev_value)) {
                prev_value = "0";
            }
            setParam(costModParam, StringMaster.wrapInParenthesis(prev_value)
                    + getCostMods().get(costModParam));
            setCost(costModParam, getParam(costModParam));
        }
        // if (SpellUpgradeMaster.isUpgraded(this))
        // if (getGame().isSimulation()) {
        // if (rawType == null) {
        // rawType = new ObjType(type);
        // type = new ObjType(rawType);
        // }
        // }
        if (SpellUpgradeMaster.applyUpgrades(this)) {
            setCustomIcon(SpellUpgradeMaster.generateSpellIcon(this));
        } else {
            setCustomIcon(null);
        }
        // Chronos.logTimeElapsedForMark(getName() + " reset");
        setDirty(false);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        super.setHighlighted(highlighted);
        getGame().getBattleField().refreshSpellbook();
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

    public void addPreparedCheck() {
        Condition retainPreparedCondition = new StringComparison("{SOURCE_"
                + PROPS.PREPARED_SPELLS.name() + "}", getName(), false);
        attachStatusBuff("Prepared", SPELL_MANIPULATION.PREPARE, retainPreparedCondition);

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
        if (channeling) {
            return channelingResolveCosts.canBePaid(ref);
        }

        return super.canBeActivated(ref, first);
    }

    @Override
    public void initCosts() {
        if (game.isDebugMode() && getGame().getTestMaster().isActionFree(getName())) {
            costs = new Costs(new LinkedList<Cost>());
        } else {
            costs = DC_CostsFactory.getCostsForSpell(this, isSpell());
            costs.getRequirements().add(
                    new Requirement(new NotCondition(new StatusCheckCondition(STATUS.SILENCED)),
                            InfoMaster.SILENCE));
        }
        costs.setActive(this);
        setCanActivate(costs.canBePaid(ref));

    }

    @Override
    public boolean activate() {
        ownerObj.getRef().setID(Ref.KEYS.SPELL, id);
        SoundMaster.playEffectSound(SOUNDS.PRECAST, this);
        if (isChanneling()) {
            return activateChanneling();
        } else {
            return super.activate();
        }
    }

    public boolean activateChanneling() {
        animate(ownerObj);
        initCosts();
        initChannelingCosts();
        game.getLogManager().log(">> " + ownerObj.getName() + " has begun Channeling " + getName());
        boolean result = (checkExtraAttacksDoNotInterrupt(ENTRY_TYPE.ACTION));
        if (result) {
            this.channeling = true;
            ChannelingRule.playChannelingSound(this, HeroAnalyzer.isFemale(ownerObj));
            result = ChannelingRule.activateChanneing(this);
        }
        if (result) {
            if (getOwner().isMe()) {
                communicate(ref);
            }
        }
        channelingActivateCosts.pay(ref);
        actionComplete();
        return result;
    }

    @Override
    protected void communicate(Ref ref) {
        super.communicate(ref);
    }

    @Override
    public boolean activate(Ref ref) {
        if (getGame().isOnline()) {
            if (getOwnerObj().isActiveSelected()) {
                if (!channeling) {
                    if (isChanneling()) {
                        return activateChanneling();
                    }
                }
            }
        }

        ownerObj.getRef().setID(KEYS.SPELL, id);
        if (!isQuietMode()) {
            if (!new Event(STANDARD_EVENT_TYPE.SPELL_ACTIVATED, ref).fire()) {
                return false;
            }
        }
        return super.activate(ref);
    }

    @Override
    protected void addCooldown() {
        // if (channeling)
        // channeling = false;
        // else
        super.addCooldown();
    }

    @Override
    public void payCosts() {
        // TODO
        if (channeling) {
            // ChannelingRule.getChannelingCosts(this).pay(ref); // new
            // Channeling rules!
            addCooldown();
            channelingResolveCosts.pay(ref);
            channeling = false;
            return;
        }
        super.payCosts();
    }

    @Override
    public void actionComplete() {

        super.actionComplete();

        if (getSpellPool() == SPELL_POOL.DIVINED) {
            if (DivinationMaster.rollRemove(this)) {
                if (getBuff(DivinationMaster.BUFF_FAVORED) != null) {
                    removeBuff(DivinationMaster.BUFF_FAVORED);
                } else {
                    remove();
                }
            }
        }

    }

    private void remove() {
        ((DC_HeroObj) ownerObj).getSpells().remove(this);
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

    @Override
    public boolean resolve() {

        if (!isQuietMode()) {
            if (!new Event(STANDARD_EVENT_TYPE.SPELL_BEING_RESOLVED, ref).fire()) {
                return false;
            }
        }
        if (isInterrupted()) {
            return true; // TODO group effects blocked?!
        }
        boolean result = false;

        applySpellpowerMod();
        SoundMaster.playEffectSound(SOUNDS.RESOLVE, this);
        result = super.resolve();
        if (result) {
            applyImpactSpecialEffect();
        }

        if (!isQuietMode()) {
            new Event(STANDARD_EVENT_TYPE.SPELL_RESOLVED, ref).fire();
        }

        if (!result) {
            if (channeling) {
                SoundMaster.playEffectSound(SOUNDS.FAIL, this);
            } else
            // try fail sound?
            {
                SoundMaster.playStandardSound(STD_SOUNDS.SPELL_RESISTED);
            }
        }

        return result;
    }

    private void applyImpactSpecialEffect() {
        SPECIAL_EFFECTS_CASE case_type = SPECIAL_EFFECTS_CASE.SPELL_IMPACT;
        // TODO will this ref have any {event} or {amount}?
        // perhaps this sort of thing should be stored in a special way, not
        // just in effect's ref!

        // TODO spell itself should also have special effects available and
        // separate from unit's!
        if (ref.getTargetObj() instanceof DC_UnitObj) {
            ownerObj.applySpecialEffects(case_type, (DC_UnitObj) ref.getTargetObj(), ref);
        }
        if (ref.getGroup() != null) {
            for (Obj unit : ref.getGroup().getObjects()) {
                if (unit != ref.getTargetObj()) {
                    if (unit instanceof DC_UnitObj) {
                        ownerObj.applySpecialEffects(case_type, (DC_UnitObj) unit, ref);
                    }
                }
            }
        }
    }

    private void applySpellpowerMod() {
        ownerObj.modifyParameter(PARAMS.SPELLPOWER, getIntParam(PARAMS.SPELLPOWER_BONUS));

        Integer perc = getIntParam(PARAMS.SPELLPOWER_MOD);
        if (perc != 100) {
            ownerObj.multiplyParamByPercent(PARAMS.SPELLPOWER, MathMaster.getFullPercent(perc),
                    false);
        }

    }

    public boolean isInstant() {
        return checkProperty(G_PROPS.SPELL_TAGS, SPELL_TAGS.INSTANT.toString());
    }

    public boolean isChanneling() {
        return checkProperty(G_PROPS.SPELL_TAGS, SPELL_TAGS.CHANNELING.toString());
        // fix
        // return getIntParam(PARAMS.CHANNELING) > 0;
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

        return checkSingleProp(G_PROPS.SPELL_TYPE, CONTENT_CONSTS.SPELL_TYPE.ENCHANTMENT.name());
    }

    public boolean isSummoning() {

        return checkSingleProp(G_PROPS.SPELL_TYPE, CONTENT_CONSTS.SPELL_TYPE.SUMMONING.name());
    }

    // public String getToolTip() {
    // String toolTip = super.getToolTip();
    // toolTip += " " + costs;
    // return toolTip;
    // }
    public String getToolTip() {
        return getStatusString() + getName();
    }

    private String getStatusString() {
        return (getCanActivate()) ? "Activate " : "" + costs.getReasonsString() + " to activate ";
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    public enum CHANNELING_SOUND {
        ARCANE, ELDRITCH, DARK, CHAOS, HOLY, EVIL, SUMMON, BUFF, DAMAGE, MISC

    }

}
