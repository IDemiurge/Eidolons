package main.entity.active;

import main.ability.effects.Effect;
import main.ability.effects.ModeEffect;
import main.content.CONTENT_CONSTS.ACTION_TAGS;
import main.content.CONTENT_CONSTS.ACTION_TYPE;
import main.content.CONTENT_CONSTS.SPELL_TAGS;
import main.content.enums.STD_MODES;
import main.content.properties.G_PROPS;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.conditions.OrConditions;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.ai.AI_Manager;
import main.game.ai.tools.target.EffectMaster;
import main.game.player.Player;
import main.rules.combat.CadenceRule;
import main.system.entity.ConditionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.graphics.Sprite;
import main.system.math.Formula;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.List;

public class DC_UnitAction extends DC_ActiveObj {

    private ACTION_TYPE actionType;
    private ModeEffect modeEffect;

    public DC_UnitAction(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref);

    }

    @Override
    public boolean activate() {
        if (checkContinuousMode()) {
            return true;
        }
        return super.activate();
    }

    private boolean checkContinuousMode() {
        ModeEffect effect = getModeEffect();
        if (effect == null) {
            return false;
        }
        if (effect.getMode().isContinuous()) {
            return false;
        }
        if (checkContinuousModeDeactivate()) {
            deactivate();
            return true;
        }

        removeModeBuffs();
        return false;
    }

    private void removeModeBuffs() {
        for (STD_MODES s : STD_MODES.values()) {
            ownerObj.removeBuff(s.getBuffName());
        }

    }

    public boolean deactivate() {
        try {
            ownerObj.removeBuff(getModeBuffName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getGame().getManager().setActivatingAction(null);
            game.getManager().reset();
            game.getManager().refreshAll();
        }

        return true;
    }

    public boolean checkContinuousModeDeactivate() {
        if (getModeEffect() == null) {
            return false;
        }
        return (ownerObj.getBuff(getModeBuffName()) != null);

    }

    public String getModeBuffName() {
        return getModeEffect().getMode().getBuffName();
    }

    public boolean isContinuousMode() {
        ModeEffect effect = getModeEffect();
        if (effect == null) {
            return false;
        }
        return effect.getMode().isContinuous();
    }

    public ModeEffect getModeEffect() {
        if (modeEffect != null) {
            return modeEffect;
        }
        List<Effect> e = EffectMaster.getEffectsOfClass(this, ModeEffect.class);
        if (!e.isEmpty()) {
            modeEffect = (ModeEffect) e.get(0);
        }
        return modeEffect;
    }

    @Override
    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.RANGED_TOUCH.toString());
    }

    public void playActivateSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ACTIVATE);
    }

    public boolean isChanneling() {
        return checkProperty(G_PROPS.ACTION_TAGS, SPELL_TAGS.CHANNELING.toString());
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void actionComplete() {
        CadenceRule.checkDualAttackCadence(this, (DC_HeroObj) ownerObj);
        super.actionComplete();
    }

    @Override
    public boolean canBeActivated(Ref ref, boolean first) {
        if (isAttack()) {
            for (DC_ActiveObj attack : getSubActions()) {
                if (attack.canBeActivated(ref, true)) {
                    return true;
                }
            }
            // if (getActionMode() == null) {
            // return false;
            // }
        }
        if (isContinuousMode()) {
            if (checkContinuousModeDeactivate()) {
                return true;
            }
        }
        return super.canBeActivated(ref, first);
    }

    @Override
    public String getToolTip() {
        if (checkContinuousModeDeactivate()) {
            return "Deactivate " + getName();
        }
        return super.getToolTip();
    }

    @Override
    public void toBase() {
        super.toBase();

        if (getParentAction() != null) {
            String tag = "";
            if (getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                    ACTION_TAGS.OFF_HAND.toString())) {
                tag = ACTION_TAGS.OFF_HAND.toString();
            } else if (getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                    ACTION_TAGS.MAIN_HAND.toString())) {
                tag = ACTION_TAGS.MAIN_HAND.toString();
            }
            setProperty(G_PROPS.ACTION_TAGS, tag);
        }

        // if (getIntParam(PARAMS.RANGE) == 0)
        // setParam(PARAMS.RANGE, ref.getSourceObj().getIntParam(PARAMS.RANGE));

        // if (isStandardAttack()) { // [OUTDATED]
        // if (getActionMode() == null) {
        // DC_WeaponObj weapon = ownerObj.getActiveWeapon(isOffhand());
        // if (weapon == null)
        // return;
        // String mode = weapon.getProperty(PROPS.DEFAULT_ATTACK_ACTION);
        // if (mode.isEmpty()) {
        // mode =
        // StringMaster.getFirstItem(weapon.getProperty(PROPS.WEAPON_ATTACKS));
        // }
        // ownerObj.toggleActionMode(this, mode);
        // }
        // }
        // TODO COUNTER etc...

        // int range = getIntParam(DC_PARAM.RANGE);
        // setParam(DC_PARAM.RANGE, range);
    }

    public ACTION_TYPE getActionType() {
        if (actionType == null) {
            initActionType();
        }
        if (actionType == null) {
            initActionType();
        }
        return actionType;
    }

    public void setActionType(ACTION_TYPE actionType) {
        this.actionType = actionType;
    }

    private void initActionType() {
        actionType = new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
                getProperty(G_PROPS.ACTION_TYPE));

    }

    @Override
    public Targeting getTargeting() {
        initTargetingMode();
        if (AI_Manager.isRunning()) {
            return super.getTargeting();
        }
        if (isAttack()) {
            Conditions conditions = new OrConditions();
            int maxRange = 0;
            for (DC_ActiveObj attack : getSubActions()) {
                if (attack.isThrow()) {
                    continue;
                }
                if (!attack.canBeActivated()) {
                    continue;
                }
                conditions.add(attack.getTargeting().getFilter().getConditions());
                if (maxRange < attack.getRange()) {
                    maxRange = attack.getRange();
                }
            }
            conditions.setFastFailOnCheck(true);
            conditions = ConditionMaster.getFilteredConditions(conditions, DistanceCondition.class);
            conditions.add(new DistanceCondition("" + maxRange));
            SelectiveTargeting selectiveTargeting = new SelectiveTargeting(
                    SELECTIVE_TARGETING_TEMPLATES.ATTACK, conditions, new Formula("1"));
            return selectiveTargeting;

        }
        return super.getTargeting();
    }

    @Override
    public void playCancelSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
    }

    @Override
    public Sprite getSprite() {
        return null;
    }
}
