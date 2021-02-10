package eidolons.game.netherflame.main.soul.panel.sub.imbue;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.netherflame.main.soul.eidola.Soul;
import eidolons.game.netherflame.main.soul.eidola.SoulMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;

public class ImbueSoulSlots extends TablePanelX {
    ImbuePanel imbuePanel;
    private Soul[] souls = new Soul[4];
    private final SoulSlot[] slots = new SoulSlot[4];

    public ImbueSoulSlots(ImbuePanel imbuePanel) {
        this.imbuePanel = imbuePanel;
//        addActor(new NoHitImage(Images.COLUMNS));
        defaults().space(99);
        for (int i = 0; i < 4; i++) {
            add(slots[i] = new SoulSlot(i));
        }
        SoulMaster.resetSouls();

    }

    public Soul[] getSouls() {
        return souls;
    }


    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        int i = 0;
        for (SoulSlot slot : slots) {
            slot.setSoul(souls[i++]);
        }
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    public void resetSouls() {
        for (SoulSlot slot : slots) {
            slot.setSoul(null);
        }
        souls = new Soul[4];
//        SoulMaster.resetSouls();
    }
    public void addSoul(Soul soul) {
        for (int i = 0; i < 4; i++) {
            if (souls[i]==null) {
                souls[i] = soul;
                return;
            }
        }
        Soul last = souls[souls.length - 1];
        last.setBeingUsed(false);
        System.arraycopy(souls, 0, souls, 1, souls.length-1 );
        souls[0]= soul;
    }

    private static class SoulSlot extends GroupX {
        FadeImageContainer container;

        public SoulSlot(int i) {
            String bg = Images.CIRCLE_BORDER;
//            GdxImageMaster.flip(bg, true, false, true);
//            bg = GdxImageMaster.getFlippedPath(bg, true, false);
            addActor(container = new FadeImageContainer());
            addActor(new NoHitImage(bg));

        }

        public void setSoul(Soul soul) {
            if (soul == null) {
                container.setImage("");
            } else
            {
                container.setImage(new Image(GdxImageMaster.round(soul.getUnitType().getImagePath(),false, "")));
                container.setScale(0.76f);
//                container.setImage(soul.getUnitType().getImagePath());
            }
        }
    }
}
