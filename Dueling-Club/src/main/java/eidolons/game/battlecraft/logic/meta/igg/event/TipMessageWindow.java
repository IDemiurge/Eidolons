package eidolons.game.battlecraft.logic.meta.igg.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
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
        setSize(source.getWidth(),source.getHeight());
        if (!StringMaster.isEmpty(source.title)) {

        }
        if (!StringMaster.isEmpty(source.image)) {
            add(imageContainer = new FadeImageContainer(source.image));
            row();
        }
        add(label = new LabelX(source.message, StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAGIC, 18)));
        label.setMaxWidth(getWidth());
        label.setWrap(true);
        row();
        TablePanelX btnsTable = new TablePanelX();
        int i = 0;
        for (String button : source.getButtons()) {
            Runnable runnable = source.btnRun[i++];
            Cell cell = btnsTable.add(new SmartButton(button, ButtonStyled.STD_BUTTON.MENU,
                    () -> {
                        if (source.isNonGdxThread()) {
                            Eidolons.onThisOrNonGdxThread(()->{
                                runnable.run();
                                fadeOut();
                                WaitMaster.receiveInput(source.msgChannel, button);
                            });
                        } else {
                            runnable.run();
                            fadeOut();
                            WaitMaster.receiveInput(source.msgChannel, button);
                        }

                    }){
                @Override
                public boolean isIgnoreConfirmBlock() {
                    return true;
                }
            }.makeActive());

            if (source.getButtons().length == 1) {
                if (isAddToggle()){
                    cell.colspan(5);
                }
            }
        }
        if (source.isOptional())
        if (isAddToggle()){
            VisCheckBox box;
            GDX.loadVisUI();
            btnsTable.add(box= new VisCheckBox("Disable", OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF)));
            box.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                   OptionsMaster.getSystemOptions().setValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF, box.isChecked());
                }
            });
        }
        add(btnsTable);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        main.system.auxiliary.log.LogMaster.log(1,"Tip msg created with text: " +source.getMessage());
    }

    private boolean isAddToggle() {
        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
    }
}
