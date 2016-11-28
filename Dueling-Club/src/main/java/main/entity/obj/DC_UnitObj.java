package main.entity.obj;

import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.*;
import main.content.*;
import main.content.enums.MODE;
import main.content.enums.ModeImpl;
import main.content.enums.STD_MODES;
import main.content.parameters.G_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_MovementManager;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.VisionManager;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.dungeon.building.MapBlock;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.libgdx.Rotatable;
import main.rules.DC_ActionManager;
import main.swing.components.obj.drawing.VisibilityMaster;
import main.system.DC_Constants;
import main.system.DC_Formulas;
import main.system.ai.UnitAI;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;
import main.system.text.ToolTipMaster;
import main.test.debug.DebugMaster;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_UnitObj extends DC_Obj implements BattlefieldObj, Rotatable {

    protected FACING_DIRECTION facing;
    protected VISION_MODE vision_mode;

    protected DequeImpl<Coordinates> visibleCoordinates;
    protected MODE mode;
    protected Map<ACTION_TYPE, List<DC_UnitAction>> actionMap;
    protected boolean godMode;

    protected boolean standardActionsAdded;

    protected Deity deity;
    protected DC_WeaponObj offhandNaturalWeapon;
    protected DC_WeaponObj naturalWeapon;

    private ImageIcon emblem;
    private UnitAI unitAI;
    private DIRECTION direction;
    private DC_ActiveObj preferredInstantAttack;
    private DC_ActiveObj preferredCounterAttack;
    private DC_ActiveObj preferredAttackOfOpportunity;
    private DC_ActiveObj preferredAttackAction;
    private DequeImpl<Coordinates> sightSpectrumCoordinates;

    public DC_UnitObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);
        this.x = x;
        this.y = y;
        if (this.game == null)
            setGame(game);
        addDynamicValues();
    }

    public DC_UnitObj(ObjType type, DC_Game game) {
        this(type, 0, 0, Player.NEUTRAL, game, new Ref(game));

    }

    public String getNameIfKnown() {
        if (owner.isMe())
            return getName();

        if (!VisionManager.checkVisible(this))
            return "Someone or something";
        if (getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.UNKNOWN) {
            // if (isHuge())
            // return "Something huge";
            // if (isSmall())
            // return "Something small";
            return "Something " + VisibilityMaster.getHintsString((DC_HeroObj) this);
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
        if (getOwner() != DC_Player.NEUTRAL)
            prefix = isMine() ? "My " : "Enemy ";
        if (isDead())
            prefix += "(Dead)";
        return prefix + super.toString() + " at " + getCoordinates();
    }

    @Override
    public String getToolTip() {
        if (!game.isSimulation())
            if (checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(this, action);
                } catch (Exception e) {
                    if (!action.isBroken())
                        e.printStackTrace();
                    else
                        action.setBroken(true);
                }
                if (!StringMaster.isEmpty(actionTargetingTooltip))
                    return actionTargetingTooltip;
            }
        if (DebugMaster.isMapDebugOn()) {
            MapBlock block = getGame().getDungeonMaster().getDungeon().getPlan()
                    .getBlockByCoordinate(getCoordinates());
            if (block != null)
                return getCoordinates() + " (" + block.getShortName() + ") " + getName();
            return getCoordinates() + " " + getName();
        }
        if (!VisionManager.checkKnown(this)) {
            return "?";
        }
        return getName();
    }

    @Override
    public boolean isTransparent() {
        if (super.isTransparent())
            return true;
        if (checkPassive(STANDARD_PASSIVES.IMMATERIAL))
            return true;
        return checkPassive(STANDARD_PASSIVES.TRANSPARENT);

    }

    public Boolean isLandscape() {
        return getType().checkProperty(G_PROPS.BF_OBJECT_TAGS, BF_OBJECT_TAGS.LANDSCAPE.toString());

    }

    public boolean isWall() {
        return getType().checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.WALL.toString());

    }

    @Override
    public boolean isObstructing(Obj obj) {
        return isObstructing(obj, null);

    }

    public boolean isObstructing(Obj obj, DC_Obj target) {

        if (target == null)
            return false;
        if (obj == null)
            return false;
        if (isBfObj())
            if (isWall()) {
                // if (WindowRule.checkWindowOpening(this, obj, target))
                // return false;
            }
        if (checkPassive(STANDARD_PASSIVES.IMMATERIAL))
            return false;
        // boolean targetTall = false;
        // boolean targetShort = false;
        // if (target instanceof DC_HeroObj) {
        // targetTall = (((DC_HeroObj) target).isTall());
        // targetShort = (((DC_HeroObj) target).isShort());
        // }
        if (checkPassive(STANDARD_PASSIVES.NON_OBSTRUCTING))
            return false;
        if (obj instanceof DC_UnitObj) {
            int height = getIntParam(PARAMS.HEIGHT);
            if (height > 200)
                height = getIntParam(PARAMS.HEIGHT);
            int source_height = obj.getIntParam(PARAMS.HEIGHT);
            int target_height = target.getIntParam(PARAMS.HEIGHT);

            DC_UnitObj source = (DC_UnitObj) obj;
            if (target_height > height)
                return false;
            if (source.isAgile() && !isHuge())
                return false;
            if (source_height < height)
                // if (!source.isFlying()) //add height TODO
                return true;

            // if (isShort())
            // if (!(source.isShort() && !targetShort))
            // return false;
            //
            // if (source.isAgile() && !isHuge())
            // return false;
            //
            // if (!isTall())
            // if (source.isFlying() || source.isTall() || targetTall)
            // return false;
        }

        return false;

    }

    public boolean isShort() {
        return checkClassification(CLASSIFICATIONS.SHORT) || checkPassive(STANDARD_PASSIVES.SHORT);
    }

    public boolean isTall() {
        return checkClassification(CLASSIFICATIONS.TALL) || checkPassive(STANDARD_PASSIVES.TALL);
    }

    public VISION_MODE getVisionMode() {
        if (vision_mode == null) {
            String name = getProperty(PROPS.VISION_MODE);
            if (StringMaster.isEmpty(name))
                vision_mode = VISION_MODE.NORMAL_VISION;
            else
                vision_mode = new EnumMaster<VISION_MODE>().retrieveEnumConst(VISION_MODE.class,
                        name);
        }
        return vision_mode;
    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        if (isDead())
            return false;
        boolean ignoreInterrupt = false;
        if (quietly == null) {
            ignoreInterrupt = true;
            quietly = false;

        }
        if (!ignoreInterrupt)
            if (!quietly)
                if (checkPassive(STANDARD_PASSIVES.INDESTRUCTIBLE)) {
                    preventDeath();
                    return false;
                }
        ref.setID(KEYS.KILLER, killer.getId());

        Ref REF = Ref.getCopy(killer.getRef());
        REF.setTarget(id);
        REF.setSource(killer.getId());

        if (!quietly) {
            if (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_KILLED, REF)))
                if (!ignoreInterrupt)
                    return false;

            ((DC_UnitObj) killer).applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_KILL, this, REF);
            applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH, ((DC_UnitObj) killer), REF);

            if (!ignoreInterrupt)
                if (ref.checkInterrupted())
                    return false;
        }
        setDead(true);

        getGame().getManager().unitDies(this, (Obj) killer, leaveCorpse, quietly);

        return true;
    }

    private void preventDeath() {
        setParam(PARAMS.C_ENDURANCE, Math.max(1, getIntParam(PARAMS.C_ENDURANCE)));
        setParam(PARAMS.C_TOUGHNESS, Math.max(1, getIntParam(PARAMS.C_TOUGHNESS)));

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

        setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.UNKNOWN);
        addDefaultFacing();
        if (isBfObj()) {
            addBfObjDefaults();
        }
    }

    private void addBfObjDefaults() {
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BF_OBJECT_TAGS.INDESTRUCTIBLE)) {
            type.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.INDESTRUCTIBLE.toString());
        }
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BF_OBJECT_TAGS.PASSABLE)) {
            type.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.PASSABLE.toString());
        }

        type.addProperty(G_PROPS.STD_BOOLS, STD_BOOLS.LEAVES_NO_CORPSE.toString());

        setParam(PARAMS.C_MORALE, 0);
        setParam(PARAMS.C_STAMINA, 0);
        setParam(PARAMS.C_FOCUS, 0);
        setParam(PARAMS.C_ESSENCE, 0);
        // type.addProperty(G_PROPS.STANDARD_PASSIVES,
        // STANDARD_PASSIVES.SNEAK_IMMUNE.toString());

    }

    protected void addDefaultFacing() {
        facing = DC_MovementManager.getDefaultFacingDirection(owner.isMe());
        resetFacing();
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
        if (getSpecialEffects() != null)
            getSpecialEffects().clear();
        super.toBase();

        // activatePassives();
        if (game.isSimulation())
            return;
        // resetActives();
        // resetFacing();

        if (checkClassification(CLASSIFICATIONS.TALL)) {
            addProperty(G_PROPS.STANDARD_PASSIVES, "" + CLASSIFICATIONS.TALL, true);
        }
        if (checkClassification(CLASSIFICATIONS.SHORT)) {
            addProperty(G_PROPS.STANDARD_PASSIVES, "" + CLASSIFICATIONS.TALL, true);
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
    public void addDynamicValues() {
        setParam(G_PARAMS.POS_X, x, true);
        setParam(G_PARAMS.POS_Y, y, true);
        setParam(PARAMS.C_MORALE, getIntParam(PARAMS.SPIRIT) * DC_Formulas.MORALE_PER_SPIRIT, true);

        setParam(PARAMS.C_ENDURANCE, getIntParam(PARAMS.ENDURANCE), true);
        setParam(PARAMS.C_TOUGHNESS, getIntParam(PARAMS.TOUGHNESS), true);
        setParam(PARAMS.C_N_OF_ACTIONS, getIntParam(PARAMS.N_OF_ACTIONS), true);
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS), true);
        setParam(PARAMS.C_ENERGY, "0", true);

        setParam(PARAMS.C_FOCUS, DC_MathManager.getStartingFocus(this), true);
        setParam(PARAMS.C_ESSENCE, getGame().getMathManager().getStartingEssence(this), true);
        setParam(PARAMS.C_STAMINA, getIntParam(PARAMS.STAMINA), true);
        if (isHero()) {

            setParam(PARAMS.IDENTITY_POINTS, getIntParam(PARAMS.STARTING_IDENTITY_POINTS));
        } else if (!isBfObj()) {
            int xp = MathMaster.getFractionValueCentimal(getIntParam(PARAMS.TOTAL_XP),
                    getIntParam(PARAMS.XP_LEVEL_MOD));
            // for training
            setParam(PARAMS.XP, xp);
        }

    }

    @Override
    public void invokeClicked() {
        clicked();
    }

    @Override
    public void newRound() {
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_BEING_STARTED, ref).fire())
            return;
        // setMode(STD_MODES.NORMAL); just don't.

        resetToughness();
        // resetPercentages(); => toBase()
        resetActions();

        resetAttacksAndMovement();

        regen();

        new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_STARTED, ref).fire();
    }

    public boolean isHero() {
        return TYPE_ENUM == OBJ_TYPES.CHARS;
    }

    public void resetPercentages() {
        resetPercentage(PARAMS.TOUGHNESS);
        resetPercentage(PARAMS.MORALE);
        resetPercentage(PARAMS.ENDURANCE);
        resetPercentage(PARAMS.ESSENCE);
        resetPercentage(PARAMS.FOCUS);
        resetPercentage(PARAMS.STAMINA);
        resetPercentage(PARAMS.N_OF_COUNTERS);
        resetPercentage(PARAMS.N_OF_ACTIONS);
        resetPercentage(PARAMS.INITIATIVE);

    }

    public void resetCurrentValues() {
        resetCurrentValue(PARAMS.TOUGHNESS);
        resetCurrentValue(PARAMS.ENDURANCE);
        resetCurrentValue(PARAMS.MORALE);
        resetCurrentValue(PARAMS.ESSENCE);
        resetCurrentValue(PARAMS.FOCUS);
        resetCurrentValue(PARAMS.STAMINA);
        resetCurrentValue(PARAMS.N_OF_COUNTERS);
        resetCurrentValue(PARAMS.N_OF_ACTIONS);
        resetCurrentValue(PARAMS.INITIATIVE);

    }

    public void recalculateInitiative() {

        int initiative = getIntParam(PARAMS.C_N_OF_ACTIONS)
                * getIntParam(PARAMS.INITIATIVE_MODIFIER);

        initiative += getIntParam(PARAMS.C_INITIATIVE_BONUS);
        // game.getTurnManager().getPreviousActive() take turns
        if (game.isDummyMode())
            if (!isBfObj())
                if (!isNeutral())
                    if (!getOwner().isMe())
                        initiative = Math.min(10, getGame().getRules().getTimeRule()
                                .getTimeRemaining() + 1);

        setParam(PARAMS.C_INITIATIVE, initiative, true);

        int base_initiative = getIntParam(PARAMS.N_OF_ACTIONS)
                * getIntParam(PARAMS.INITIATIVE_MODIFIER) + getIntParam(PARAMS.C_INITIATIVE_BONUS);
        setParam(PARAMS.INITIATIVE, base_initiative, true);
        resetPercentage(PARAMS.INITIATIVE);
    }

    protected void resetToughness() {
        Integer amount = getIntParam(PARAMS.TOUGHNESS_RECOVERY) * getIntParam(PARAMS.TOUGHNESS)
                / 100;
        // setParam(PARAMS.C_TOUGHNESS, amount);
        if (amount > 0)
            modifyParameter(PARAMS.C_TOUGHNESS, amount, getIntParam(PARAMS.TOUGHNESS));
    }

    protected void resetActions() {
        if (checkPassive(STANDARD_PASSIVES.AUTOMATA)) {
            return;
        }
        if (checkStatus(STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        int carryOverFactor = DC_Constants.CARRY_OVER_FACTOR;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) < 0)
            carryOverFactor = DC_Constants.CARRY_OVER_FACTOR_NEGATIVE;

        int actions = getIntParam(PARAMS.N_OF_ACTIONS) + getIntParam(PARAMS.C_N_OF_ACTIONS)
                / carryOverFactor;

        setParam(PARAMS.C_N_OF_ACTIONS, actions);

    }

    protected void resetAttacksAndMovement() {
        if (checkStatus(STATUS.IMMOBILE)) {
            setParam(PARAMS.C_N_OF_ACTIONS, 0);
            return;
        }
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS));

    }

    // melee/ranged separate!
    public boolean canAttack(DC_UnitObj attacked) {
        if (!canAttack())
            return false;
        // ConditionMaster.getAdjacent().check(ref);
        int range = getIntParam(PARAMS.RANGE);
        if (range == 1)
            return getGame().getMovementManager().isAdjacent(this, attacked);
        return (range >= getGame().getMovementManager().getDistance(this, attacked));

    }

    @Override
    public boolean canMove() {
        if (isBfObj())
            return false;
        // if (isstructure)
        return canActNow();
    }

    public boolean canCounter(DC_ActiveObj active) {
        return canCounter(active, false);
    }

    public boolean canCounter(DC_ActiveObj active, boolean sneak) {
        if (!canCounter())
            return false;
        if (active.checkPassive(STANDARD_PASSIVES.NO_RETALIATION))
            return false;
        // if (!attacked.checkPassive(STANDARD_PASSIVES.VIGILANCE))
        if (active.getOwnerObj().checkPassive(STANDARD_PASSIVES.NO_RETALIATION))
            return false;
        // may still fail to activate any particular Attack Action!
        return true;
    }

    public DC_ActiveObj getPreferredInstantAttack() {
        String action = getProperty(PROPS.DEFAULT_INSTANT_ATTACK_ACTION);
        if (!action.isEmpty())
            preferredInstantAttack = getAction(action);
        return preferredInstantAttack;
    }

    public void setPreferredInstantAttack(DC_ActiveObj preferredInstantAttack) {
        this.preferredInstantAttack = preferredInstantAttack;
        setProperty(PROPS.DEFAULT_INSTANT_ATTACK_ACTION, preferredInstantAttack.getName());
    }

    public DC_ActiveObj getPreferredCounterAttack() {
        String action = getProperty(PROPS.DEFAULT_COUNTER_ATTACK_ACTION);
        if (!action.isEmpty())
            preferredCounterAttack = getAction(action);
        return preferredCounterAttack;
    }

    public void setPreferredCounterAttack(DC_ActiveObj preferredCounterAttack) {
        this.preferredCounterAttack = preferredCounterAttack;
        setProperty(PROPS.DEFAULT_COUNTER_ATTACK_ACTION, preferredCounterAttack.getName());
    }

    public DC_ActiveObj getPreferredAttackOfOpportunity() {
        String action = getProperty(PROPS.DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION);
        if (!action.isEmpty())
            preferredAttackOfOpportunity = getAction(action);
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
        if (isDisabled())
            return false;
        if (checkModeDisablesCounters())
            return false;
        if (checkStatusDisablesCounters())
            return false;
        // TODO getMinimumAttackCost
        // if ( checkAlertCounter())
        // return false;
        // alternative cost
        // if (getIntParam(PARAMS.C_N_OF_COUNTERS) <= 0) {
        // }
        return true;
    }

    public boolean isDisabled() {
        if (isUnconscious())
            return true;
        return isDead();
    }

    public boolean checkStatusDisablesCounters() {
        if (checkStatus(STATUS.IMMOBILE))
            return true;
        if (checkStatus(STATUS.CHARMED))
            return true;
        if (checkStatus(STATUS.ENSNARED))
            return true;
        if (checkStatus(STATUS.PRONE))
            return true;
        return checkStatus(STATUS.EXHAUSTED);

    }

    public boolean isUnconscious() {
        return checkStatus(STATUS.UNCONSCIOUS);
    }

    protected boolean checkAlertCounter() {
        if (!getMode().equals(STD_MODES.ALERT))
            return false;
        return checkActionCanBeActivated(DC_ActionManager.HIDDEN_ACTIONS.Counter_Attack.toString());

    }

    public boolean turnStarted() {
        if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_TURN_STARTED, ref)))
            return false;
        return canActNow();

    }

    public boolean canAct() {
        if (owner == Player.NEUTRAL)
            return false;
        if (checkStatusPreventsActions())
            return false;
        return !isImmobilized();
    }

    public boolean canActNow() {
        if (owner == Player.NEUTRAL)
            return false;
        if (checkStatusPreventsActions())
            return false;

        // if (checkStatus(STATUS.DISCOMBOBULATED))
        // return false;

        if (checkStatus(STATUS.ON_ALERT))
            return false;
        if (checkStatus(STATUS.WAITING))
            return false;
        // if (checkStatus(STATUS.LATE))
        // return false;
        if (getIntParam(PARAMS.C_N_OF_ACTIONS) <= 0)
            return false;
        return !isImmobilized();

    }

    public boolean checkUncontrollable() {
        // if (checkBuffStatusPreventsActions())
        // return true;
        return getMode().isBehavior();

    }

    public boolean checkStatusPreventsActions() {
        if (checkStatus(STATUS.DEAD))
            return true;
        if (checkStatus(STATUS.EXHAUSTED))
            return true;
        if (checkStatus(STATUS.ASLEEP))
            return true;
        if (checkStatus(STATUS.FROZEN))
            return true;
        return checkStatus(STATUS.UNCONSCIOUS);
    }

    public boolean isIncapacitated() {
        if (checkStatus(STATUS.IMMOBILE))
            return true;
        if (checkStatus(STATUS.CHARMED))
            return true;
        return checkStatusPreventsActions();
    }

    public boolean isImmobilized() {

        if (checkStatus(STATUS.IMMOBILE))
            return true;

        if (checkStatus(STATUS.CHARMED))
            return true;

        return checkModeDisablesActions();

    }

    public boolean checkModeDisablesCounters() {
        if (getBehaviorMode() != null)
            return getBehaviorMode().isDisableCounters();

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
            if (behavior != null)
                this.mode = new ModeImpl(behavior);
        }
        if (mode == null)
            this.mode = (STD_MODES.NORMAL);

        setMode(mode);
        main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG, getName() + " has mode: " + mode);

    }

    public MODE getMode() {
        if (mode == null || mode == STD_MODES.NORMAL)
            initMode();
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
        if (mode == null)
            removeProperty(G_PROPS.MODE, "");
        else
            setProperty(G_PROPS.MODE, StringMaster.getWellFormattedString(mode.toString()));
    }

    public boolean canAttack() {
        return getAttack().canBeActivated(ref, true);
    }

    private DC_UnitAction getAttack() {
        return getAction(DC_ActionManager.ATTACK);
    }

    public boolean removeStatus(STATUS status) {
        return removeProperty(G_PROPS.STATUS, status.name());

    }

    @Override
    public void addPassive(String abilName) {
        if (DataManager.isTypeName(abilName))
            super.addPassive(abilName);
        else {
            addProperty(G_PROPS.STANDARD_PASSIVES, abilName);
        }
    }

    @Override
    public void setRef(Ref ref) {
        ref.setSource(id);
        // if (owner.getHeroObj() != null)
        // ref.setID(KEYS.SUMMONER, owner.getHeroObj().getId());
        super.setRef(ref);
        this.ref.setTarget(null);
    }

    public boolean isDone() {
        if (isDead())
            return true;
        return getIntParam(PARAMS.C_N_OF_ACTIONS) <= 0;
    }

    public void regen() {
        if (isFull())
            return;
        regen(PARAMS.ENDURANCE);
        regen(PARAMS.FOCUS);
        regen(PARAMS.ESSENCE);
        regen(PARAMS.STAMINA);

        regen(PARAMS.ENERGY);

    }

    protected void regen(PARAMETER p) {
        if (isFull(p))
            return;
        Integer regen = getIntParam(ContentManager.getRegenParam(p));
        if (regen != 0)
            modifyParameter(ContentManager.getCurrentParam(p), regen, getIntParam(p));

    }

    public boolean isUnmoved() {
        return getIntParam(PARAMS.C_N_OF_ACTIONS) >= getIntParam(PARAMS.N_OF_ACTIONS);

    }

    public boolean isFull() {
        if (getIntParam(PARAMS.C_ENDURANCE) < getIntParam(PARAMS.ENDURANCE))
            return false;
        if (getIntParam(PARAMS.C_TOUGHNESS) < getIntParam(PARAMS.TOUGHNESS))
            return false;
        if (getIntParam(PARAMS.C_ESSENCE) < getIntParam(PARAMS.ESSENCE))
            return false;
        if (getIntParam(PARAMS.C_FOCUS) < getIntParam(PARAMS.FOCUS))
            return false;
        if (getIntParam(PARAMS.C_STAMINA) < getIntParam(PARAMS.STAMINA))
            return false;
        if (getIntParam(PARAMS.C_ESSENCE) < getIntParam(PARAMS.ESSENCE))
            return false;
        return getIntParam(PARAMS.C_ENERGY) >= getIntParam(PARAMS.ENERGY);
    }

    public FACING_DIRECTION getFacing() {
        if (facing == null)
            resetFacing();
        return facing;
    }

    public void setFacing(FACING_DIRECTION direction) {
        this.facing = direction;
        resetFacing();
    }

    public FACING_DIRECTION getFacingOrNull() {
        return facing;
    }

    protected void resetFacing() {
        if (facing != null) {
            setProperty(PROPS.FACING_DIRECTION, facing.getName());
        } else {
            String name = getProperty(PROPS.FACING_DIRECTION);
            facing = (new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
                    name));
            if (facing == null) {
                if (getDirection() != null)
                    FacingMaster.getFacingFromDirection(getDirection());
                else if (ref.getObj(KEYS.SUMMONER) != null)
                    facing = ((DC_UnitObj) ref.getObj(KEYS.SUMMONER)).getFacing();
                else
                    facing = FacingMaster.getRandomFacing();
            }

        }
    }

    public void initDeity() {
        if (DataManager.isTypeName(getProperty(G_PROPS.DEITY)))
            this.setDeity(DC_ContentManager.getDeity(this));

    }

    protected void initEmblem() {
        this.setEmblem((ImageManager.getIcon(getProperty(G_PROPS.EMBLEM, true))));

    }

    public ImageIcon getEmblem() {
        if (emblem != null)
            return emblem;

        if (getDeity() == null)
            return null;
        return getDeity().getEmblem();
    }

    public void setEmblem(ImageIcon emblem) {
        this.emblem = emblem;
    }

    public Deity getDeity() {
        if (deity == null)
            initDeity();
        return deity;
    }

    public void setDeity(Deity deity) {
        this.deity = deity;
        setProperty(G_PROPS.DEITY, deity.getName(), true);
    }

    public DIRECTION getDirection() {
        if (direction == null)
            direction = new EnumMaster<DIRECTION>().retrieveEnumConst(DIRECTION.class,
                    getProperty(PROPS.DIRECTION));
        // if (direction == null)
        // if (!isOverlaying())
        // direction = facing.getDirection();
        return direction;
        //
        // TODO perhaps for stacked as well...
        // return
    }

    public void setDirection(DIRECTION d) {
        this.direction = d;

    }

    public DequeImpl<Coordinates> getPlainSightSpectrumCoordinates() {
        if (visibleCoordinates == null)
            visibleCoordinates = new DequeImpl<>();
        return visibleCoordinates;
    }

    public void setPlainSightSpectrumCoordinates(DequeImpl<Coordinates> list) {
        this.visibleCoordinates = list;
    }

    public void setSightSpectrumCoordinates(DequeImpl<Coordinates> list, boolean extended) {
        if (extended)
            this.sightSpectrumCoordinates = list;
        else
            setPlainSightSpectrumCoordinates(list);
    }

    public DequeImpl<Coordinates> getSightSpectrumCoordinates(boolean extended) {
        if (extended)
            return getSightSpectrumCoordinates();
        else
            return getPlainSightSpectrumCoordinates();
    }

    public DequeImpl<Coordinates> getSightSpectrumCoordinates() {
        if (sightSpectrumCoordinates == null)
            sightSpectrumCoordinates = new DequeImpl<>();
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
        if (actionMap == null)
            actionMap = new HashMap<>();
        return actionMap;
    }

    public void setActionMap(Map<ACTION_TYPE, List<DC_UnitAction>> actionMap) {
        this.actionMap = actionMap;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean b) {
        this.godMode = b;
    }

    public boolean checkActionCanBeActivated(String actionName) {
        DC_UnitAction action = getAction(actionName);
        if (action == null)
            return false;
        return action.canBeActivated();
    }

    public DC_UnitAction getAction(String action) {
        return getAction(action, false);
    }

    public DC_UnitAction getAction(String action, boolean strict) {
        if (StringMaster.isEmpty(action))
            return null;
        // if (game.isSimulation()) if (getGame().getActionManager().)
        // return new DC_UnitAction(DataManager.getType(action,
        // OBJ_TYPES.ACTIONS),
        // getOriginalOwner(), getGame(), ref);
        // }
        for (ACTION_TYPE type : getActionMap().keySet()) {
            for (DC_UnitAction a : getActionMap().get(type)) {
                if (StringMaster.compare(action, a.getName(), true))
                    return a;
            }
        }
        if (!strict)
            for (ACTION_TYPE type : getActionMap().keySet()) {
                for (DC_UnitAction a : getActionMap().get(type)) {
                    if (StringMaster.compare(action, a.getName(), false))
                        return a;
                }
            }
        // DC_UnitAction unitAction = new
        // ListMaster<DC_UnitAction>().findType(action,
        // new MapMaster<ACTION_TYPE, DC_UnitAction>().joinMap(getActionMap()));
        // return unitAction;
        return null;

    }

    public RACE getRace() {
        return new EnumMaster<RACE>().retrieveEnumConst(RACE.class, getProperty(G_PROPS.RACE));
    }

    public AI_TYPE getAiType() {
        AI_TYPE ai = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class,
                getProperty(PROPS.AI_TYPE));
        if (ai == null)
            return AI_TYPE.NORMAL;
        return ai;
    }

    public DAMAGE_TYPE getDamageType() {
        if (dmg_type == null) {
            String name = getProperty(PROPS.DAMAGE_TYPE);
            if (StringMaster.isEmpty(name))
                dmg_type = DAMAGE_TYPE.PHYSICAL;
            else
                dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, name);
        }
        return dmg_type;
    }

    public boolean hasDoubleStrike() {
        return checkPassive(STANDARD_PASSIVES.DOUBLE_STRIKE);
    }

    public boolean isStandardActionsAdded() {
        return standardActionsAdded;
    }

    public void setStandardActionsAdded(boolean standardActionsAdded) {
        this.standardActionsAdded = standardActionsAdded;
    }

    public boolean isBfObj() {
        // TODO would be cool to make petrified/frozen units appear this way too
        return getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ;
    }

    public boolean isHuge() {
        if (checkProperty(G_PROPS.STANDARD_PASSIVES, "" + STANDARD_PASSIVES.HUGE))
            return true;
        return checkProperty(G_PROPS.CLASSIFICATIONS, "" + CLASSIFICATIONS.HUGE);
    }

    public boolean isSmall() {
        if (checkProperty(G_PROPS.STANDARD_PASSIVES, "" + STANDARD_PASSIVES.SMALL))
            return true;
        return checkProperty(G_PROPS.CLASSIFICATIONS, "" + CLASSIFICATIONS.SMALL);
    }

    public boolean checkImmunity(IMMUNITIES type) {
        return checkProperty(G_PROPS.IMMUNITIES, type.toString());
    }

    public UnitAI getAI() {
        return getUnitAI();
    }

    public UnitAI getUnitAI() {
        if (unitAI == null)
            unitAI = new UnitAI(this);
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
        if (checkClassification(CLASSIFICATIONS.UNDEAD))
            return false;
        if (checkClassification(CLASSIFICATIONS.WRAITH))
            return false;
        if (checkClassification(CLASSIFICATIONS.ELEMENTAL))
            return false;
        if (checkClassification(CLASSIFICATIONS.CONSTRUCT))
            return false;
        if (checkClassification(CLASSIFICATIONS.STRUCTURE))
            return false;
        return !checkClassification(CLASSIFICATIONS.MECHANICAL);
    }

    public boolean isTurnable() {
        return true;
    }

}
