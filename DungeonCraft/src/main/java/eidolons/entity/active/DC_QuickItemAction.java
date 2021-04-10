package eidolons.entity.active;

import eidolons.entity.handlers.active.item.ItemActiveMaster;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.sound.AudioEnums;

public class DC_QuickItemAction extends DC_ActiveObj {

    private DC_QuickItemObj item;

    public DC_QuickItemAction(ObjType type, Player owner, GenericGame game, Ref ref) {
        super(type, owner, game, ref);
    }

    @Override
    public ACTION_TYPE_GROUPS getActionGroup() {
        return ActionEnums.ACTION_TYPE_GROUPS.ITEM;
    }

    @Override
    public EntityMaster initMaster() {
        return new ItemActiveMaster(this);
    }

    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS,
         SpellEnums.SPELL_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public boolean activate() {
        return super.activate();
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

    public DC_QuickItemObj getItem() {
        return item;
    }

    public void setItem(DC_QuickItemObj dc_QuickItemObj) {
        this.item = dc_QuickItemObj;
    }

    @Override
    public void invokeClicked() {
        getItem().invokeClicked();
    }
}