package eidolons.entity.obj.unit;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.handlers.bf.unit.UnitCalculator;
import eidolons.entity.handlers.bf.unit.UnitChecker;
import eidolons.entity.handlers.bf.unit.UnitInitializer;
import eidolons.entity.handlers.bf.unit.UnitResetter;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.text.ToolTipMaster;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static main.system.GuiEventType.SHOW_MODE_ICON;

public abstract class DC_UnitModel extends BattleFieldObject {

    protected VISION_MODE vision_mode;
    protected MODE mode;
    protected Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> actionMap;

    protected UnitAI unitAI;
    protected ImageIcon emblem;
    protected Boolean unconscious;
    private FACING_DIRECTION tempFacing;
    private Coordinates tempCoordinates;

    public DC_UnitModel(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, owner, game, ref);
        if (this.game == null) {
            setGame(game);
        }
        if (!game.isSimulation()) {
            Coordinates c = Coordinates.get(x, y);
            setCoordinates(c);
        originalCoordinates = c;
        addDynamicValues();
        }
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
                        ExceptionMaster.printStackTrace(e);
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
        if (isMainHero()) {
            if (game.getVisionMaster().isVisionTest()) {
                return VISION_MODE.X_RAY_VISION;
            }
        }
        if (vision_mode == null) {
            String name = getProperty(PROPS.VISION_MODE);
            if (StringMaster.isEmpty(name)) {
                vision_mode = VISION_MODE.NORMAL_VISION;
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
        resetDynamicParam(PARAMS.C_TOUGHNESS);
        regen();

        new Event(STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_STARTED, ref).fire();
    }


    public DC_ActiveObj getStdAttack() {
        return getAttackOfType(ActionEnums.ATTACK_TYPE.STANDARD_ATTACK);
    }
    public DC_ActiveObj getAttackOfType(ActionEnums.ATTACK_TYPE type) {
        return getAttack();
        //TODO NF Rules revamp
        // for (DC_UnitAction subAction : getAttack().getSubActions()) {
        //     if (subAction.getChecker().checkAttackType(type)) {
        //         return subAction;
        //     }
        // }
        // DC_Logger.logicError("No action of type " ,
        //         type , " found for " , getName());
        // return getAttack().getSubActions().get(0);
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
        if (getEntity().isUnconscious()) {
            this.mode = STD_MODES.UNCONSCIOUS;
            return;
        }
        if (getBuff("Channeling") != null) {
            if (mode != STD_MODES.CHANNELING) {
                return;
            }
        }
        this.mode = mode;

        if (mode == null) {
            removeProperty(G_PROPS.MODE, "");
        } else {
            setProperty(G_PROPS.MODE, StringMaster.format(mode.toString()));
        }
        if (mode == null || STD_MODES.NORMAL.equals(mode)) {
            GuiEventManager.triggerWithParams(SHOW_MODE_ICON, this, null);
        } else {
            GuiEventManager.triggerWithParams(SHOW_MODE_ICON, this, mode.getImagePath());
            if (isMainHero())
                EUtils.showInfoText(
                        StringMaster.format(mode.getBuffName()) + "...");
        }
    }


    public DC_UnitAction getAttack() {
        return getAction(ActionEnums.ATTACK);
    }

    public DC_UnitAction getOffhandAttack() {
        return getAction(ActionEnums.OFFHAND_ATTACK);
    }

    public FACING_DIRECTION getFacingOrNull() {
        return facing;
    }

    protected void initEmblem() {
        this.setEmblem((ImageManager.getIcon(getProperty(G_PROPS.EMBLEM, true))));
    }

    public ImageIcon getEmblem() {
        return emblem;
    }

    public void setEmblem(ImageIcon emblem) {
        this.emblem = emblem;
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
                dmg_type = DAMAGE_TYPE.PHYSICAL;
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
        if (unconscious != null) {
            return unconscious;
        }
        return unconscious = getChecker().isUnconscious();
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

    public void setTempFacing(FACING_DIRECTION tempFacing) {
        this.tempFacing = tempFacing;
    }

    public void initTempFacing() {
        setTempFacing(super.getFacing());
    }

    public void removeTempFacing() {
        this.tempFacing = null;
    }


    public void setTempCoordinates(Coordinates tempCoordinates) {
        this.tempCoordinates = tempCoordinates;
    }

    public void initTempCoordinates() {
        setTempCoordinates(super.getCoordinates());
    }

    public void removeTempCoordinates() {
        this.tempCoordinates = null;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        super.setCoordinates(coordinates);
    }

    @Override
    public Coordinates getCoordinates() {
        if (tempCoordinates != null) {
            return tempCoordinates;
        }
        return super.getCoordinates();
    }

    @Override
    public FACING_DIRECTION getFacing() {
        if (tempFacing != null) {
            return tempFacing;
        }
        return super.getFacing();
    }

}
