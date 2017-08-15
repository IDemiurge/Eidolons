package main.entity.active;

import main.ability.effects.Effect;
import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TAGS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.item.DC_WeaponObj;
import main.entity.tools.EntityMaster;
import main.entity.tools.active.action.ActionActiveMaster;
import main.entity.tools.active.action.ActionExecutor;
import main.entity.type.ObjType;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.graphics.Sprite;
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
    public EntityMaster initMaster() {
        return new ActionActiveMaster(this);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public DC_WeaponObj getActiveWeapon() {
            if (isUnarmed())
                return getOwnerObj().getNaturalWeapon(isOffhand());
        return super.getActiveWeapon();
    }


    public String getModeBuffName() {
        if (getModeEffect()==null )
            return null ;
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
        List<Effect> e = EffectFinder.getEffectsOfClass(this, ModeEffect.class);
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
         || checkGroup("Creature")|| checkGroup("Unarmed");
    }
    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.RANGED_TOUCH.toString());
    }

    public void playActivateSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ACTIVATE);
    }

    @Override
    public void playCancelSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
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


    @Override
    public Sprite getSprite() {
        return null;
    }


}
