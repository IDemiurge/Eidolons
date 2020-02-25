package eidolons.entity.obj.unit;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.Deity;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.handlers.bf.unit.UnitCalculator;
import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.handlers.bf.unit.UnitInitializer;
import eidolons.entity.handlers.bf.unit.UnitResetter;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.Rotatable;
import eidolons.system.text.ToolTipMaster;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager;
import main.system.math.MathMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static main.system.GuiEventType.INITIATIVE_CHANGED;
import static main.system.GuiEventType.SHOW_MODE_ICON;

public abstract class DC_UnitModel extends BattleFieldObject implements Rotatable {

    protected VISION_MODE vision_mode;

    protected MODE mode;
    protected Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> actionMap;

    protected Deity deity;
    protected UnitAI unitAI;
    private ImageIcon emblem;
    private DC_ActiveObj preferredInstantAttack;
    private DC_ActiveObj preferredCounterAttack;
    private DC_ActiveObj preferredAttackOfOpportunity;
    private DC_ActiveObj preferredAttackAction;

    public DC_UnitModel(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);
        Coordinates c = Coordinates.get(x, y);
        setCoordinates(c);
        originalCoordinates = c;
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

        if (getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT) {
            //!VisionManager.checkVisible(this)) {
//        if (getActivePlayerVisionStatus() == PLAYER_VISION.UNKNOWN) {
            // if (isHuge())
            // return "Something huge";
            // if (isSmall())
            // return "Something small";
            return "Something " + getGame().getVisionMaster().getHintMaster().getHintsString(this);
        }
//        return StringMaster.getWellFormattedString(getVisibilityLevel().toString()); //"Someone or something";

        return getName();
    }

    @Override
    public String getImagePath() {
        return super.getImagePath();
    }


    @Override
    public String getToolTip() {
        if (!game.isSimulation())
            return super.getToolTip();
        else {
            if (checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(this, action);
                } catch (Exception e) {
                    if (!action.isBroken()) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    } else {
                        action.setBroken(true);
                    }
                }
                if (!StringMaster.isEmpty(actionTargetingTooltip)) {
                    return actionTargetingTooltip;
                }
            }
        }
        //        if (!VisionManager.checkKnown(this)) {
        //            return "?";
        //        }
        return getNameIfKnown();
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
        if (getChecker().isHero()) {
            setParam(PARAMS.IDENTITY_POINTS, getIntParam(PARAMS.STARTING_IDENTITY_POINTS));
        } else if (!getChecker().isBfObj()) {
            int xp = MathMaster.getFractionValueCentimal(getIntParam(PARAMS.TOTAL_XP),
                    getIntParam(PARAMS.XP_LEVEL_MOD));
            // for training
            setParam(PARAMS.XP, xp);
        }
    }


    protected void initMode() {
        getInitializer().initMode();
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
        if (game.getState().getRound() > -1) // ???
            getResetter().regenerateToughness();
        if (!DC_Engine.isAtbMode())
            getResetter().resetActions();

        regen();

        new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_STARTED, ref).fire();
    }


    public void recalculateInitiative() {

        final int before = getIntParam(PARAMS.C_INITIATIVE);
        final int initiative = getCalculator().calculateInitiative(true);

        setParam(PARAMS.C_INITIATIVE, initiative, true);

        int baseInitiative = getCalculator().calculateInitiative(false);
        setParam(PARAMS.INITIATIVE, baseInitiative, true);

        resetPercentage(PARAMS.INITIATIVE);

        final int after = getIntParam(PARAMS.C_INITIATIVE);

        if (before - after != 0) {
            GuiEventManager.trigger(
                    INITIATIVE_CHANGED,
                    new ImmutablePair<>(this, after)
            );
        }
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

    public boolean turnStarted() {
//        if (!game.fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_TURN_READY, ref))) {
//            return false;
//        }
        return canActNow();

    }

    public BEHAVIOR_MODE getBehaviorMode() {
        MODE mode = getMode();
        if (mode == null) {
            return null;
        }
        if (isPlayerCharacter())
            if (ExplorationMaster.isExplorationOn()) {
                return null;
            }
        return mode.getBehaviorMode();
    }


    public MODE getMode() {
        if (mode == null || mode == STD_MODES.NORMAL || ExplorationMaster.isExplorationOn()) {
            initMode();
        } else if (mode.isContinuous())
            initMode(); //this is a quickfix for Guarding+Defend/Alert compatibility...
        return mode;
    }

    public void setMode(MODE mode) {
        if (this.mode == mode) return;
        if (getBuff("Channeling") != null) {
            if (mode != STD_MODES.CHANNELING) {
                return;
            }
        }
        this.mode = mode;

        if (mode == null) {
            removeProperty(G_PROPS.MODE, "");
        } else {
            setProperty(G_PROPS.MODE, StringMaster.getWellFormattedString(mode.toString()));
        }
        if (mode == null || STD_MODES.NORMAL.equals(mode)) {
            GuiEventManager.triggerWithParams(SHOW_MODE_ICON, this, null);
        } else {
            GuiEventManager.triggerWithParams(SHOW_MODE_ICON, this, mode.getImagePath());
            EUtils.showInfoText(
                    StringMaster.getWellFormattedString(mode.getBuffName()) + "...");
        }
    }


    public DC_UnitAction getAttack() {
        return getAction(DC_ActionManager.ATTACK);
    }

    public DC_UnitAction getOffhandAttack() {
        return getAction(DC_ActionManager.OFFHAND_ATTACK);
    }

    public FACING_DIRECTION getFacingOrNull() {
        return facing;
    }

    public void initDeity() {
        if (DataManager.isTypeName(getProperty(G_PROPS.DEITY), DC_TYPE.DEITIES)) {
            this.setDeity(DC_ContentValsManager.getDeity(this));
            setProperty(G_PROPS.DEITY, deity.getName(), true);
        } else
            setDeity(null);

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
        if (deity == null || isSimulation()) {
            initDeity();
        }
        return deity;
    }

    public void setDeity(Deity deity) {
        this.deity = deity;
    }


    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        // getVisibleCoordinates().clear();
    }

    public Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> getActionMap() {
        if (actionMap == null) {
            actionMap = new ConcurrentHashMap<>();
        }
        return actionMap;
    }

    public void setActionMap(Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> actionMap) {
        this.actionMap = actionMap;
    }


    public boolean checkActionCanBeActivated(String actionName) {
        DC_UnitAction action = getAction(actionName);
        if (action == null) {
            return false;
        }
        return action.canBeActivated();
    }

    public DC_ActiveObj getActionOrSpell(String name) {
        return getGame().getActionManager().getAction(name, this, false);
    }

    public DC_UnitAction getAction(String name) {
        ActiveObj action = getGame().getActionManager().getAction(name, this);
        if (action instanceof DC_UnitAction) {
            return (DC_UnitAction) action;
        }
        return getAction(name, true); //TODO was non-strict required??
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
        return getChecker().hasDoubleStrike();
    }

    public boolean isBfObj() {
        return getChecker().isBfObj();
    }

    public boolean isHero() {
        return getChecker().isHero();
    }

    public boolean isLiving() {
        return getChecker().isLiving();
    }

    public boolean checkImmunity(IMMUNITIES type) {
        return getChecker().checkImmunity(type);
    }

    public Unit getEntity() {
        return getChecker().getEntity();
    }

    public boolean canUseItems() {
        return getChecker().canUseItems();
    }

    public boolean canAttack() {
        return getChecker().canAttack();
    }

    public boolean canAttack(DC_UnitModel attacked) {
        return getChecker().canAttack(attacked);
    }

    public boolean canCounter() {
        return getChecker().canCounter();
    }

    public boolean canCounter(DC_ActiveObj active) {
        return getChecker().canCounter(active);
    }

    public boolean canCounter(DC_ActiveObj active, boolean sneak) {
        return getChecker().canCounter(active, sneak);
    }

    public boolean canUseArmor() {
        return getChecker().canUseArmor();
    }

    public boolean canUseWeapons() {
        return getChecker().canUseWeapons();
    }

    public boolean checkDualWielding() {
        return getChecker().checkDualWielding();
    }

    public boolean isImmortalityOn() {
        return getChecker().isImmortalityOn();
    }


    public boolean isDisabled() {
        return getChecker().isDisabled();
    }

    public boolean checkStatusDisablesCounters() {
        return getChecker().checkStatusDisablesCounters();
    }

    public boolean isUnconscious() {
        return getChecker().isUnconscious();
    }

    public boolean canAct() {
        return getChecker().canAct();
    }

    public boolean canActNow() {
        return getChecker().canActNow();
    }

    public boolean checkUncontrollable() {
        return getChecker().checkUncontrollable();
    }

    public boolean checkStatusPreventsActions() {
        return getChecker().checkStatusPreventsActions();
    }

    public boolean isIncapacitated() {
        return getChecker().isIncapacitated();
    }

    public boolean isImmobilized() {
        return getChecker().isImmobilized();
    }

    public boolean checkModeDisablesCounters() {
        return getChecker().checkModeDisablesCounters();
    }

    public boolean checkModeDisablesActions() {
        return getChecker().checkModeDisablesActions();
    }

    @Override
    public boolean isTransparent() {
        return getChecker().isTransparent();


    }


    public DC_ActiveObj getDummyAction() {
        DC_UnitAction action = getAction("Dummy Action");
        if (action == null) {

        }
        return action;
    }

    @Override
    public UnitInitializer getInitializer() {
        return (UnitInitializer) super.getInitializer();
    }

    @Override
    public UnitCalculator getCalculator() {
        return (UnitCalculator) super.getCalculator();
    }

    @Override
    public UnitResetter getResetter() {
        return (UnitResetter) super.getResetter();
    }

    @Override
    public UnitChecker getChecker() {
        return (UnitChecker) super.getChecker();
    }


    public MODE getModeFinal() {
        return mode;
    }
}
