package eidolons.entity.active;

import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.content.PARAMS;
import eidolons.entity.handlers.active.action.ActionActiveMaster;
import eidolons.entity.handlers.active.action.ActionExecutor;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.DC_RuleMaster;
import eidolons.game.core.master.EffectMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TAGS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.sound.AudioEnums;

import java.util.List;

public class UnitAction extends ActiveObj {

    private ACTION_TYPE actionType;
    private ModeEffect modeEffect;

    public UnitAction(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref);
    }

    public UnitAction(ObjType type, Unit unit) {
        super(type, unit.getOwner(), unit.getGame(), unit.getRef().getCopy());
    }


    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public EntityMaster initMaster() {
        return new ActionActiveMaster(this);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void toBase() {
        super.toBase();
        if (DC_RuleMaster.isFocusReqsOff()) {
            setParam(PARAMS.FOC_REQ, 0);
//            setParam(PARAMS.FOC_COST, 0);
        }
    }

    @Override
    public WeaponItem getActiveWeapon() {
        if (isUnarmed())
            return getOwnerUnit().getNaturalWeapon(isOffhand());
        return super.getActiveWeapon();
    }


    public String getModeBuffName() {
        if (getModeEffect() == null)
            return null;
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
    public String getToolTip() {
        if (getHandler().checkActionDeactivatesContinuousMode()) {
            return "Deactivate " + getName();
        }
        return super.getToolTip();
    }

    public boolean isChanneling() {
        return checkProperty(G_PROPS.ACTION_TAGS, SpellEnums.SPELL_TAGS.CHANNELING.toString());
    }

    public boolean isUnarmed() {
        return checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.UNARMED.toString())
                || checkGroup("Creature") || checkGroup("Unarmed");
    }

    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.RANGED_TOUCH.toString());
    }

    public void playActivateSound() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK_ACTIVATE);
    }

    @Override
    public void playCancelSound() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.ACTION_CANCELLED);
    }

    @Override
    public boolean isEffectSoundPlayed() {
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {

    }

    @Override
    public ActionActiveMaster getMaster() {
        return (ActionActiveMaster) super.getMaster();
    }

    public boolean checkContinuousModeDeactivate() {
        return getHandler().checkActionDeactivatesContinuousMode();
    }

    @Override
    public ActionExecutor getHandler() {
        return (ActionExecutor) super.getHandler();
    }

    public void deactivate() {
        getHandler().deactivate();
    }
    // TODO DEPRECATED METHODS!


}
