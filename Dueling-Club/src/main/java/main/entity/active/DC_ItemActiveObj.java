package main.entity.active;

import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.item.DC_QuickItemObj;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.graphics.Sprite;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class DC_ItemActiveObj extends DC_ActiveObj {

    private DC_QuickItemObj item;

    public DC_ItemActiveObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref);
    }

    @Override
    public ACTION_TYPE_GROUPS getActionGroup() {
        return ActionEnums.ACTION_TYPE_GROUPS.ITEM;
    }

    @Override
    protected void applyPenalties() {
    }

    @Override
    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS,
                SpellEnums.SPELL_TAGS.RANGED_TOUCH.toString());
    }

    @Override
    public void playCancelSound() {
        SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
    }

    public DC_QuickItemObj getItem() {
        return item;
    }

    public void setItem(DC_QuickItemObj dc_QuickItemObj) {
        this.item = dc_QuickItemObj;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }
}
