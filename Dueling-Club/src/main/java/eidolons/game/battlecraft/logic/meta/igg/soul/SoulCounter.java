package eidolons.game.battlecraft.logic.meta.igg.soul;

import com.google.gwt.user.client.ui.CustomButton;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import main.system.graphics.FontMaster;

public class SoulCounter extends GroupX {
    SmartButton btn;
    LabelX counter;

    public SoulCounter() {
        addActor(btn = new SmartButton(ButtonStyled.STD_BUTTON.SOULS_BTN, () -> {

        }));
        addActor(counter = new LabelX("",
                StyleHolder.getHqLabelStyle(20)));

        counter.setText("3");
    }

    @Override
    public void act(float delta) {
        setSize(btn.getWidth(), btn.getHeight());
        GdxMaster.center(counter);
        counter.setX(counter.getX()-5);
        super.act(delta);
    }
}
