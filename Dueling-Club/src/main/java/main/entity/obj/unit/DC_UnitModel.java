package main.entity.obj.unit;

import main.content.DC_ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.mode.MODE;
import main.content.mode.ModeImpl;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Deity;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.type.ObjType;
import main.game.ai.UnitAI;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.vision.VisionManager;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.battle.player.Player;
import main.game.logic.dungeon.building.MapBlock;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.generic.DC_ActionManager;
import main.libgdx.bf.Rotatable;
import main.system.DC_Constants;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.test.TestMasterContent;
import main.system.text.ToolTipMaster;
import main.test.debug.DebugMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static main.system.GuiEventType.INITIATIVE_CHANGED;

public abstract class DC_UnitModel extends BattleFieldObject implements  Rotatable {

    protected VISION_MODE vision_mode;

    protected DequeImpl<Coordinates> visibleCoordinates;
    protected MODE mode;
    protected Map<ACTION_TYPE, List<DC_UnitAction>> actionMap;

    protected boolean standardActionsAdded;

    private boolean hidden;

    protected Deity deity;
    private ImageIcon emblem;
    private UnitAI unitAI;
    private DC_ActiveObj preferredInstantAttack;
    private DC_ActiveObj preferredCounterAttack;
    private DC_ActiveObj preferredAttackOfOpportunity;
    private DC_ActiveObj preferredAttackAction;
    private DequeImpl<Coordinates> sightSpectrumCoordinates;

    public DC_UnitModel(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);
        this.x = x;
        this.y = y;
        if (this.game == null) {
            setGame(game);
        }
        addDynamicValues();
    }

    public DC_UnitModel(ObjType type, DC_Game game) {
        this(type, 0, 0, Player.NEUTRAL, game, new Ref(game));

    }

    public String getNameIfKnown() {
        if (owner.isMe()) {
            return getName();
        }

        if (!VisionManager.checkVisible(this)) {
            return "Someone or something";
        }
        if (getActivePlayerVisionStatus() == VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN) {
            // if (isHuge())
            // return "Something huge";
            // if (isSmall())
            // return "Something small";
            return "Something " + getGame().getVisionMaster().getHintMaster().getHintsString((Unit) this);
        }
        return getName();
    }

    @Override
    public String getImagePath() {
        if (getGame().getDungeonMaster().getDungeon() != null) {
            return ImageManager.getThemedImagePath(super.getImagePath(), getGame()
                    .getDungeonMaster().getDungeon().getColorTheme());
        }
        return super.getImagePath();
    }

    @Override
    public String toString() {
        String prefix = "";
        if (getOwner() != DC_Player.NEUTRAL) {
            prefix = isMine() ? "My " : "Enemy ";
        }
        if (isDead()) {
            prefix += "(Dead)";
        }
        return prefix + super.toString() + " at " + getCoordinates();
    }

    @Override
    public String getToolTip() {
        if (!game.isSimulation()) {
            if (checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(this, action);
                } catch (Exception e) {
                    if (!action.isBroken()) {
                        e.printStackTrace();
                    } else {
                        action.setBroken(true);
                    }
                }
                if (!StringMaster.isEmpty(actionTargetingTooltip)) {
                    return actionTargetingTooltip;
                }
            }
        }
        if (DebugMaster.isMapDebugOn()) {
            MapBlock block = getGame().getDungeonMaster().getDungeon().getPlan()
                    .getBlockByCoordinate(getCoordinates());
            if (block != null) {
                return getCoordinates() + " (" + block.getShortName() + ") " + getName();
            }
            return getCoordinates() + " " + getName();
        }
//        if (!VisionManager.checkKnown(this)) {
//            return "?";
//        }
        return getName();
    }

    @Override
    public boolean isTransparent() {
        if (super.isTransparent()) {
            return true;
        }
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return true;
        }
        return checkPassive(UnitEnums.STANDARD_PASSIVES.TRANSPARENT);

    }

    public VISION_MODE getVisionMode() {
        if (vision_mode == null) {
            String name = getProperty(PROPS.VISION_MODE);
            if (StringMaster.isEmpty(name)) {
                vision_mode = VisionEnums.VISION_MODE.NORMAL_VISION;
            } else {
                vision_mode = new EnumMaster<VISION_MODE>().retrieveEnumConst(VISION_MODE.class,
                        name);
            }
        }
        return vision_mode;
    }


    public void addDynamicValues() {
        super.addDynamicValues();
        if (isHero()) {
            setParam(PARAMS.IDENTITY_POINTS, getIntParam(PARAMS.STARTING_IDENTITY_POINTS));
        } else if (!isBfObj()) {
            int xp = MathMaster.getFractionValueCentimal(getIntParam(PARAMS.TOTAL_XP),
             getIntParam(PARAMS.XP_LEVEL_MOD));
            // for training
            setParam(PARAMS.XP, xp);
        }
    }

    public boolean isBfObj() {
        return false;
    }
    protected void addDefaultValues() {

        super.addDefaultValues();

        // for (VALUE value : DC_ContentManager
        // .getHeaderValues(getOBJ_TYPE_ENUM())) {
        // if (StringMaster.isEmpty(getValue(value))) {
        // getType()
        // .setValue(value, DC_ContentManager.getDefaultValue(value,
        // getOBJ_TYPE_ENUM()));
        // setValue(value, ContentManager.getValue(value)
        // .getDefaultValue());
        // }
        // }

        setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN);
        addDefaultFacing();
        if (isBfObj()) {
            addBfObjDefaults();
        }
    }

    @Override
    public void init() {
        super.init();
        this.setGame(getGenericGame());

        addDynamicValues();
        // construct();

    }

    @Override
    public void toBase() {
        setMode(null);
        if (getSpecialEffects() != null) {
            getSpecialEffects().clear();
        }
        super.toBase();

        // activatePassives();
        if (game.isSimulation()) {
            return;
        }
        // resetActives();
        // resetFacing();
//        if (isActiveSelected())
        if (isMine()) {
            if (CoreEngine.isAnimationTestMode()) {
                TestMasterContent.addANIM_TEST_Spells(this);
            } else if (CoreEngine.isGraphicTestMode()) {
                TestMasterContent.addGRAPHICS_TEST_Spells(this);
            }
        }

        if (checkClassification(UnitEnums.CLASSIFICATIONS.TALL)) {
            addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.TALL, true);
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.SHORT)) {
            addProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.CLASSIFICATIONS.TALL, true);
        } // INSTEAD, LET'S DISPLAY CLASSIFICATIONS - OR SOME OF THEM!

        if (!isLiving()) {
            // really for all? or should it be special?
            // addProperty(G_PROPS.STANDARD_PASSIVES, "" +
            // STANDARD_PASSIVES.CRITICAL_IMMUNE,
            // true);
            // addProperty(G_PROPS.STANDARD_PASSIVES, "" +
            // STANDARD_PASSIVES.SNEAK_IMMUNE,
            // true);

        }
        setDirty(false);
    }

    protected void resetActives() {
        for (ActiveObj active : getActives()) {
            active.setRef(ref);
            active.toBase();
        }
    }

    @Override
    public void invokeClicked() {
        clicked();
    }

    @Override
    public void newRound() {
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_BEING_STARTED, ref).fire()) {
            return;
        }
        // setMode(STD_MODES.NORMAL); just don't.

        resetToughness();
        // resetPercentages(); => toBase()
        resetActions();

        resetAttacksAndMovement();

        regen();

        new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_STARTED, ref).fire();
    }

    public boolean isHero() {
        return TYPE_ENUM == DC_TYPE.CHARS;
    }

    public void recalculateInitiative() {
        int before =  getIntParam(PARAMS.C_INITIATIVE);
        int initiative = getIntParam(PARAMS.C_N_OF_ACTIONS)
                * getIntParam(PARAMS.INITIATIVE_MODIFIER);

        initiative += getIntParam(PARAMS.C_INITIATIVE_BONUS);
        // game.getTurnManager().getPreviousActive() take turns

        if (game.isDummyMode()) {
            if (!isBfObj()) {
                if (!isNeutral()) {
                    if (!getOwner().isMe()) {
                        initiative = Math.min(10, getGame().getRules().getTimeRule()
                                .getTimeRemaining() + 1);
                    }
                }
            }
        }

        setParam(PARAMS.C_INITIATIVE, initiative, true);

        int base_initiative = getIntParam(PARAMS.N_OF_ACTIONS)
                * getIntParam(PARAMS.INITIATIVE_MODIFIER) + getIntParam(PARAMS.C_INITIATIVE_BONUS);
        setParam(PARAMS.INITIATIVE, base_initiative, true);
        resetPercentage(PARAMS.INITIATIVE);

        int after =  getIntParam(PARAMS.C_INITIATIVE);
        if (before == after) {
            return;
        }
        int diff = before - after;

        if (diff != 0) {
            GuiEventManager.trigger(INITIATIVE_CHANGED,
             new EventCallbackParam(new ImmutablePair<>(this, after)));

        }
    }


    protected void resetActions() {
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.AUTOMATA)) {
            return;
        }
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        int carryOverFactor = DC_Constants.CARRY_OVER_FACTOR;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) < 0) {
            carryOverFactor = DC_Constants.CARRY_OVER_FACTOR_NEGATIVE;
        }

        int actions = getIntParam(PARAMS.N_OF_ACTIONS) + getIntParam(PARAMS.C_N_OF_ACTIONS)
                / carryOverFactor;

        setParam(PARAMS.C_N_OF_ACTIONS, actions);

    }

    protected void resetAttacksAndMovement() {
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS));

    }

    // melee/ranged separate!
    public boolean canAttack(DC_UnitModel attacked) {
        if (!canAttack()) {
            return false;
        }
        // ConditionMaster.getAdjacent().check(ref);
        int range = getIntParam(PARAMS.RANGE);
        if (range == 1) {
            return getGame().getMovementManager().isAdjacent(this, attacked);
        }
        return (range >= getGame().getMovementManager().getDistance(this, attacked));

    }

    @Override
    public boolean canMove() {
        if (isBfObj()) {
            return false;
        }
        // if (isstructure)
        return canActNow();
    }

    public boolean canCounter(DC_ActiveObj active) {
        return canCounter(active, false);
    }

    public boolean canCounter(DC_ActiveObj active, boolean sneak) {
        if (!canCounter()) {
            return false;
        }
        if (active.checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // if (!attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        if (active.getOwnerObj().checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION)) {
            return false;
        }
        // may still fail to activate any particular Attack Action!
        return true;
    }

    public DC_ActiveObj getPreferredInstantAttack() {
        String action = getProperty(PROPS.DEFAULT_INSTANT_ATTACK_ACTION);
        if (!action.isEmpty()) {
            preferredInstantAttack = getAction(action);
        }
        return preferredInstantAttack;
    }

    public void setPreferredInstantAttack(DC_ActiveObj preferredInstantAttack) {
        this.preferredInstantAttack = preferredInstantAttack;
        setProperty(PROPS.DEFAULT_INSTANT_ATTACK_ACTION, preferredInstantAttack.getName());
    }

    public DC_ActiveObj getPreferredCounterAttack() {
        String action = getProperty(PROPS.DEFAULT_COUNTER_ATTACK_ACTION);
        if (!action.isEmpty()) {
            preferredCounterAttack = getAction(action);
        }
        return preferredCounterAttack;
    }

    public void setPreferredCounterAttack(DC_ActiveObj preferredCounterAttack) {
        this.preferredCounterAttack = preferredCounterAttack;
        setProperty(PROPS.DEFAULT_COUNTER_ATTACK_ACTION, preferredCounterAttack.getName());
    }

    public DC_ActiveObj getPreferredAttackOfOpportunity() {
        String action = getProperty(PROPS.DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION);
        if (!action.isEmpty()) {
            preferredAttackOfOpportunity = getAction(action);
        }
        return preferredAttackOfOpportunity;
    }

    public void setPreferredAttackOfOpportunity(DC_ActiveObj preferredAttackOfOpportunity) {
        this.preferredAttackOfOpportunity = preferredAttackOfOpportunity;
        setProperty(PROPS.DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION, preferredAttackOfOpportunity
                .getName());
    }

    public DC_ActiveObj getPreferredAttackAction() {
        return preferredAttackAction;
    }

    public void setPreferredAttackAction(DC_ActiveObj preferredAttackAction) {
        this.preferredAttackAction = preferredAttackAction;
    }

    public boolean canCounter() {
        if (isDisabled()) {
            return false;
        }
        if (checkModeDisablesCounters()) {
            return false;
        }
        if (checkStatusDisablesCounters()) {
            return false;
        }
        // TODO getMinimumAttackCost
        // if ( checkAlertCounter())
        // return false;
        // alternative cost
        // if (getIntParam(PARAMS.C_N_OF_COUNTERS) <= 0) {
        // }
        return true;
    }

    public boolean isDisabled() {
        if (isUnconscious()) {
            return true;
        }
        return isDead();
    }

    public boolean checkStatusDisablesCounters() {
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.ENSNARED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.PRONE)) {
            return true;
        }
        return checkStatus(UnitEnums.STATUS.EXHAUSTED);

    }

    public boolean isUnconscious() {
        return checkStatus(UnitEnums.STATUS.UNCONSCIOUS);
    }

    protected boolean checkAlertCounter() {
        if (!getMode().equals(STD_MODES.ALERT)) {
            return false;
        }
        return checkActionCanBeActivated(DC_ActionManager.HIDDEN_ACTIONS.Counter_Attack.toString());

    }

    public boolean turnStarted() {
        if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_TURN_STARTED, ref))) {
            return false;
        }
        return canActNow();

    }

    public boolean canAct() {
        if (owner == Player.NEUTRAL) {
            return false;
        }
        if (checkStatusPreventsActions()) {
            return false;
        }
        return !isImmobilized();
    }

    public boolean canActNow() {
        if (getGame().isDummyPlus()) {
            if(!isMine())
             return false;
        }
        if (owner == Player.NEUTRAL) {
            return false;
        }
        if (checkStatusPreventsActions()) {
            return false;
        }

        // if (checkStatus(STATUS.DISCOMBOBULATED))
        // return false;

        if (checkStatus(UnitEnums.STATUS.ON_ALERT)) {
            return false;
        }
        if (checkStatus(UnitEnums.STATUS.WAITING)) {
            return false;
        }
        // if (checkStatus(STATUS.LATE))
        // return false;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) <= 0) {
            return false;
        }
        return !isImmobilized();

    }

    public boolean checkUncontrollable() {
        // if (checkBuffStatusPreventsActions())
        // return true;
        return getMode().isBehavior();

    }

    public boolean checkStatusPreventsActions() {
        if (checkStatus(UnitEnums.STATUS.DEAD)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.EXHAUSTED)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.ASLEEP)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.FROZEN)) {
            return true;
        }
        return checkStatus(UnitEnums.STATUS.UNCONSCIOUS);
    }

    public boolean isIncapacitated() {
        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }
        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }
        return checkStatusPreventsActions();
    }

    public boolean isImmobilized() {

        if (checkStatus(UnitEnums.STATUS.IMMOBILE)) {
            return true;
        }

        if (checkStatus(UnitEnums.STATUS.CHARMED)) {
            return true;
        }

        return checkModeDisablesActions();

    }

    public boolean checkModeDisablesCounters() {
        if (getBehaviorMode() != null) {
            return getBehaviorMode().isDisableCounters();
        }

        return getMode().isDisableCounter();
    }

    public boolean checkModeDisablesActions() {
        return getMode().isDisableActions();

    }

    public BEHAVIOR_MODE getBehaviorMode() {
        MODE mode = getMode();
        if (!(mode instanceof ModeImpl) || mode == null) {
            return null;
        }
        ModeImpl modeImpl = (ModeImpl) mode;
        return modeImpl.getBehaviorMode();
    }

    protected void initMode() {
        String name = getProperty(G_PROPS.MODE);
        this.mode = (new EnumMaster<STD_MODES>().retrieveEnumConst(STD_MODES.class, name));
        if (mode == null) {
            BEHAVIOR_MODE behavior = new EnumMaster<BEHAVIOR_MODE>().retrieveEnumConst(
                    BEHAVIOR_MODE.class, name);
            if (behavior != null) {
                this.mode = new ModeImpl(behavior);
            }
        }
        if (mode == null) {
            this.mode = (STD_MODES.NORMAL);
        }

        setMode(mode);
        LogMaster.log(LogMaster.CORE_DEBUG, getName() + " has mode: " + mode);

    }

    public MODE getMode() {
        if (mode == null || mode == STD_MODES.NORMAL) {
            initMode();
        }
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
        if (mode == null) {
            removeProperty(G_PROPS.MODE, "");
        } else {
            setProperty(G_PROPS.MODE, StringMaster.getWellFormattedString(mode.toString()));
        }
    }

    public boolean canAttack() {
        return getAttack().canBeActivated(ref, true);
    }

    private DC_UnitAction getAttack() {
        return getAction(DC_ActionManager.ATTACK);
    }

    @Override
    public void setRef(Ref ref) {
        ref.setSource(id);
        super.setRef(ref);
        this.ref.setTarget(null);
    }



    public FACING_DIRECTION getFacingOrNull() {
        return facing;
    }

    public void initDeity() {
        if (DataManager.isTypeName(getProperty(G_PROPS.DEITY))) {
            this.setDeity(DC_ContentManager.getDeity(this));
        }

    }

    protected void initEmblem() {
        this.setEmblem((ImageManager.getIcon(getProperty(G_PROPS.EMBLEM, true))));

    }

    public ImageIcon getEmblem() {
        if (emblem != null) {
            return emblem;
        }

        if (getDeity() == null) {
            return null;
        }
        return getDeity().getEmblem();
    }

    public void setEmblem(ImageIcon emblem) {
        this.emblem = emblem;
    }

    public Deity getDeity() {
        if (deity == null) {
            initDeity();
        }
        return deity;
    }

    public void setDeity(Deity deity) {
        this.deity = deity;
        setProperty(G_PROPS.DEITY, deity.getName(), true);
    }

    public DequeImpl<Coordinates> getPlainSightSpectrumCoordinates() {
        if (visibleCoordinates == null) {
            visibleCoordinates = new DequeImpl<>();
        }
        return visibleCoordinates;
    }

    public void setPlainSightSpectrumCoordinates(DequeImpl<Coordinates> list) {
        this.visibleCoordinates = list;
    }

    public void setSightSpectrumCoordinates(DequeImpl<Coordinates> list, boolean extended) {
        if (extended) {
            this.sightSpectrumCoordinates = list;
        } else {
            setPlainSightSpectrumCoordinates(list);
        }
    }

    public DequeImpl<Coordinates> getSightSpectrumCoordinates(boolean extended) {
        if (extended) {
            return getSightSpectrumCoordinates();
        } else {
            return getPlainSightSpectrumCoordinates();
        }
    }

    public DequeImpl<Coordinates> getSightSpectrumCoordinates() {
        if (sightSpectrumCoordinates == null) {
            sightSpectrumCoordinates = new DequeImpl<>();
        }
        return sightSpectrumCoordinates;
    }

    public void setSightSpectrumCoordinates(DequeImpl<Coordinates> list) {
        this.sightSpectrumCoordinates = list;
    }

    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        // getVisibleCoordinates().clear();
    }

    public Map<ACTION_TYPE, List<DC_UnitAction>> getActionMap() {
        if (actionMap == null) {
            actionMap = new ConcurrentHashMap<>();
        }
        return actionMap;
    }

    public void setActionMap(Map<ACTION_TYPE, List<DC_UnitAction>> actionMap) {
        this.actionMap = actionMap;
    }


    public boolean checkActionCanBeActivated(String actionName) {
        DC_UnitAction action = getAction(actionName);
        if (action == null) {
            return false;
        }
        return action.canBeActivated();
    }

    public DC_UnitAction getAction(String action) {
        return getAction(action, false);
    }

    public DC_UnitAction getAction(String action, boolean strict) {
        if (StringMaster.isEmpty(action)) {
            return null;
        }
        for (ACTION_TYPE type : getActionMap().keySet()) {
            for (DC_UnitAction a : getActionMap().get(type)) {
                if (StringMaster.compare(action, a.getName(), true)) {
                    return a;
                }
            }
        }
        if (!strict) {
            for (ACTION_TYPE type : getActionMap().keySet()) {
                for (DC_UnitAction a : getActionMap().get(type)) {
                    if (StringMaster.compare(action, a.getName(), false)) {
                        return a;
                    }
                }
            }
        }
        return null;

    }

    public RACE getRace() {
        return new EnumMaster<RACE>().retrieveEnumConst(RACE.class, getProperty(G_PROPS.RACE));
    }

    public AI_TYPE getAiType() {
        AI_TYPE ai = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class,
                getProperty(PROPS.AI_TYPE));
        if (ai == null) {
            return AiEnums.AI_TYPE.NORMAL;
        }
        return ai;
    }

    public DAMAGE_TYPE getDamageType() {
        if (dmg_type == null) {
            String name = getProperty(PROPS.DAMAGE_TYPE);
            if (StringMaster.isEmpty(name)) {
                dmg_type = GenericEnums.DAMAGE_TYPE.PHYSICAL;
            } else {
                dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, name);
            }
        }
        return dmg_type;
    }

    public boolean hasDoubleStrike() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.DOUBLE_STRIKE);
    }

    public void setStandardActionsAdded(boolean standardActionsAdded) {
        this.standardActionsAdded = standardActionsAdded;
    }


    public boolean checkImmunity(IMMUNITIES type) {
        return checkProperty(G_PROPS.IMMUNITIES, type.toString());
    }

    public UnitAI getAI() {
        return getUnitAI();
    }

    public UnitAI getUnitAI() {
        if (unitAI == null) {
            unitAI = new UnitAI(this);
        }
        return unitAI;
    }

    public void setUnitAI(UnitAI unitAI) {
        this.unitAI = unitAI;
    }

    public DC_ActiveObj getDummyAction() {
        DC_UnitAction action = getAction("Dummy Action");
        if (action == null) {

        }
        return action;
    }

    public boolean isLiving() {
        if (checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.ELEMENTAL)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.CONSTRUCT)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.STRUCTURE)) {
            return false;
        }
        return !checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean b) {
        hidden = b;
    }

}
