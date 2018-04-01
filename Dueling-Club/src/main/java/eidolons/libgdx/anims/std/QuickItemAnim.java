package eidolons.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * Created by JustMe on 8/30/2017.
 */
public class QuickItemAnim extends Anim {
    private DC_QuickItemObj item;

    public QuickItemAnim(DC_QuickItemObj item) {
        super(item.getActive(), new AnimData());
        this.item = item;
    }

    public QuickItemAnim(Event e) {
        super(e.getRef().getEntity(KEYS.ACTIVE), new AnimData());
        this.item = (DC_QuickItemObj) e.getRef().getObj(KEYS.ITEM);
    }

    @Override
    protected Texture getTexture() {
        return super.getTexture();
    }

    @Override
    public void playSound() {
        if (item.isPotion()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.POTION);
        }
        if (item.isConcoction()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.POTION);
        }
        if (item.isCoating()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.POTION);
        }
        if (item.isAmmo()) {

        }
        super.playSound();
    }
}
