package main.entity.active;

import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
import main.content.CONTENT_CONSTS.SPELL_TAGS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.player.Player;
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
        return ACTION_TYPE_GROUPS.ITEM;
    }

    @Override
    protected void applyPenalties() {
    }

    @Override
    public boolean isRangedTouch() {
        return checkProperty(G_PROPS.SPELL_TAGS,
                SPELL_TAGS.RANGED_TOUCH.toString());
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
