package eidolons.game.battlecraft.logic.meta.igg.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import eidolons.entity.active.DummyAction;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.content.enums.GenericEnums;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.threading.WaitMaster;

public class TipMessageWindow extends TablePanelX {

    private boolean large;
    FadeImageContainer imageContainer;
    LabelX label;
    SmartButton[] btns;
    TipMessageSource source;
    private Runnable onClose;

    public TipMessageWindow(TipMessageSource source) {
        super();
        this.source = source;
        if (source == null) {
            setVisible(false);
            return; //TODO ....
        }
//        setSize(source.getWidth(), source.getHeight());
        if (!StringMaster.isEmpty(source.title)) {

        }
        large = false;
        boolean medium = false;
        boolean over = false;
        if (!StringMaster.isEmpty(source.image)) {
            over = source.image.contains("blotch");
            add(imageContainer = new FadeImageContainer(source.image));
            if (imageContainer.getWidth() > 250) {
                medium = true;
            }
            if (imageContainer.getWidth() > 500) {
                large = true;
            }
            if (isVertical())
                row();
        }
        if (large) {
            label = new LabelX(source.message, StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 21));
        } else {
//            label = new LabelX(source.message, StyleHolder.getDefaultHiero());
            label = new LabelX(source.message, StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 20));
        }
        if (over) {
            addActor(label);
        } else {
            add(label).pad(15);
        }
        if (large || medium) {
            label.setMaxWidth(getPrefWidth());
            if (!large) {
                label.setMaxWidth(imageContainer.getWidth());
            }
            label.setWrap(true);
            label.pack();
            layout();
            setSize(getPrefWidth() + 100, getPrefHeight() + 100);
        } else {
            setSize(GdxMaster.getWidth() / 3, GdxMaster.getHeight() / 3);
            label.setMaxWidth(getWidth());
            label.setWrap(true);
        }

        defaults().space(30);
        row();
        TablePanelX btnsTable = new TablePanelX();
        int i = 0;
        for (String button : source.getButtons()) {
            Runnable runnable = source.btnRun[i++];
            Cell cell = btnsTable.add(new SmartButton(button, ButtonStyled.STD_BUTTON.MENU,
                    () -> {
                        if (source.isNonGdxThread()) {
                            Eidolons.onThisOrNonGdxThread(() -> {
                                runnable.run();
                                close();
                                WaitMaster.receiveInput(source.msgChannel, button);
                            });
                        } else {
                            runnable.run();
                            close();
                            WaitMaster.receiveInput(source.msgChannel, button);
                        }

                    }) {
                @Override
                public boolean isIgnoreConfirmBlock() {
                    return true;
                }
            }.makeActive());

            if (source.getButtons().length == 1) {
                if (isAddToggle()) {
                    cell.colspan(5);
                }
            }
        }
        if (source.isOptional())
            if (isAddToggle()) {
                VisCheckBox box;
                GDX.loadVisUI();
                btnsTable.add(box = new VisCheckBox("Disable", OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF)));
                box.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        OptionsMaster.getSystemOptions().setValue(SystemOptions.SYSTEM_OPTION.MESSAGES_OFF, box.isChecked());
                    }
                });
            }
        add(btnsTable);

        if (large) {
            setBackground(NinePatchFactory.getHqDrawable());
        } else
            setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        if (over) {
            imageContainer.remove();
            addActor(imageContainer);
            GdxMaster.center(imageContainer);
            imageContainer.setY(imageContainer.getY() + getHeight()/2);

            ImageContainer c=null ;
            addActor(c = new ImageContainer("ui/INK BLOTCH.png"));
//            c.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_SPEAKER);
            c.setColor(1,1,1,0.6f);
            c.setOrigin(c.getWidth()/2, c.getHeight()/2);
            c.setRotation(90);
            GdxMaster.center(c);
            label.remove();
            addActor(label);
            label.setWidth(label.getPrefWidth()+200);
            label.setHeight(getHeight()-100);
            label.setZigZagLines(true);
            label.setWrap(true);


            GdxMaster.center(label);
            label.setX(label.getX() + label.getWidth()/10);

            btnsTable.remove();
            addActor(btnsTable);
            GdxMaster.center(btnsTable);
            btnsTable.setY(  btnsTable.getHeight()/2);
//            btnsTable.setY(0);

        }

        main.system.auxiliary.log.LogMaster.log(1, "Tip msg created with text: " + source.getMessage());
    }

    private boolean isVertical() {
        return true;
    }

    private void close() {
        fadeOut();

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            if (onClose != null) {
                onClose.run();
                onClose = null;
            }
        }
    }

    @Override
    public boolean remove() {
        if (onClose != null) {
            onClose.run();
            onClose = null;
        }
        return super.remove();
    }

    public Runnable getOnClose() {
        return onClose;
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
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
