package libgdx.anims.std;

import eidolons.entity.item.QuickItem;
import libgdx.anims.Anim;
import libgdx.anims.AnimData;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.system.sound.AudioEnums;

/**
 * Created by JustMe on 8/30/2017.
 */
public class QuickItemAnim extends Anim {
    private final QuickItem item;

    public QuickItemAnim(QuickItem item) {
        super(item.getActive(), new AnimData());
        this.item = item;
    }

    public QuickItemAnim(Event e) {
        super(e.getRef().getEntity(KEYS.ACTIVE), new AnimData());
        this.item = (QuickItem) e.getRef().getObj(KEYS.ITEM);
    }

    @Override
    public void playSound() {
        if (item.isPotion()) {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.POTION);
        }
        if (item.isConcoction()) {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.POTION);
        }
        if (item.isCoating()) {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.POTION);
        }
        if (item.isAmmo()) {

        }
        super.playSound();
    }
}
