package eidolons.game.battlecraft.logic.meta.igg.event;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderDrawer;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.threading.WaitMaster;

public class TipMessageWindow extends TablePanelX {

    FadeImageContainer imageContainer;
    LabelX label;
    SmartButton[] btns;
    TipMessageSource source;
    public TipMessageWindow(TipMessageSource source) {
        super(GdxMaster.getWidth() / 3, GdxMaster.getHeight() / 3);
        this.source = source;
        if (source == null) {
            setVisible(false);
            return; //TODO ....
        }
        if (!StringMaster.isEmpty(source.image)) {
            add(imageContainer = new FadeImageContainer(source.image));
        }
        add(label = new LabelX(source.message, StyleHolder.getSizedLabelStyle(FontMaster.FONT.SUPER_KNIGHT, 18)));
        label.setMaxWidth(getWidth());
        label.setWrap(true);
        row();
        TablePanelX btnsTable = new TablePanelX();
        int i = 0;
        for (String button : source.getButtons()) {
            Runnable runnable = source.btnRun[i++];
            btnsTable.add(new SmartButton(button, ButtonStyled.STD_BUTTON.MENU,
                    ()->{
                       runnable.run();
                       fadeOut();
                        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE, button);

                    }).makeActive( ));
        }
        add(btnsTable);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
    }
}
