package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.controls.GlobalController;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * Created by JustMe on 12/1/2017.
 */
public class SmartButton extends TextButton implements EventListener {

    private STD_BUTTON style;
    private Runnable runnable;
    private boolean fixedSize;
    private boolean ignoreConfirmBlock;
    private Runnable disabledRunnable;
    private boolean flipY;
    private boolean flipX;
    private boolean noClickCheck;

    public SmartButton(String text, TextButtonStyle style) {
        this(text, style, null, STD_BUTTON.MENU);
    }

    public SmartButton(STD_BUTTON button, Runnable runnable) {
        this("", button, runnable);
    }

    public SmartButton(String text, STD_BUTTON button, Runnable runnable) {
        this(text, button, runnable,
                FONT.MAGIC, 20, GdxColorMaster.PALE_GOLD);
    }

    public SmartButton(String text, STD_BUTTON button, Runnable runnable,
                       FONT font, int size, Color color_) {
        this(text, StyleHolder.getTextButtonStyle(button,
                font, color_, size), runnable, button);
        this.style = button;
    }

    public SmartButton(String text, TextButtonStyle style, Runnable runnable, STD_BUTTON btnStyle) {
        super(text, StyleHolder.getTextButtonStyle(style, btnStyle));
        this.runnable = runnable;
        addListener(this);
//        getStyle(). TODO this won't affect  *** !
        this.style = btnStyle;
    }

    public SmartButton(STD_BUTTON button) {
        this("", button, null);
    }

    public SmartButton(String text, Runnable runnable) {
        this(text, StyleHolder.getHqTextButtonStyle(16), runnable, STD_BUTTON.MENU);
    }


    @Override
    public float getPrefWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getPrefHeight();
    }

    //    addListener(new SmartClickListener(this) {
    //        @Override
    //        public void clicked(InputEvent event, float x, float y) {
    //            super.clicked(event, x, y);
    //            runnable.run();
    //        }
    //    }
    //        );
    @Override
    public boolean handle(Event event) {
        // igg demo hack
        try {
            return handleEvent(event);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return true;
    }

    public boolean handleEvent(Event e) {
        if (!isIgnoreConfirmBlock())
            if (ConfirmationPanel.getInstance().isVisible())
                return true;


        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        STD_SOUNDS sound = null;
        if (event.getType() == Type.touchUp) {
            if (!isCheckClickArea() || event.getPointer() == -1 //programmatic
                    || GdxMaster.isWithin(event.getTarget(), new Vector2(event.getStageX(), event.getStageY()), true)) {
                if (!isDisabled()) {
                    if (getSoundMap() != null)
                        sound = getSoundMap().up;

                    if (runnable != null)
                        runnable.run();
                } else {
                    if (disabledRunnable != null)
                        disabledRunnable.run();
                }
            }
        } else {
            if (getSoundMap() != null)
                switch (event.getType()) {
                    case touchDown:
                        if (isDisabled()) {
                            sound = getSoundMap().disabled;
                        } else {
                            sound = getSoundMap().down;
                        }
                        break;
                    case enter:
                        sound = getSoundMap().hover;
                        break;
                }
        }
        if (sound != null) {

            DC_SoundMaster.playStandardSound(sound);
        }
        return true;
    }

    protected boolean isCheckClickArea() {
        if (noClickCheck) {
            return false;
        }
        return !flipX && !flipY;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    protected BUTTON_SOUND_MAP getSoundMap() {
        if (style != null) {
            BUTTON_SOUND_MAP map = new EnumMaster<BUTTON_SOUND_MAP>().retrieveEnumConst(BUTTON_SOUND_MAP.class, style.name());
            if (map != null) {
                return map;
            }
            switch (style) {
                case STAT:
                    return BUTTON_SOUND_MAP.STAT;
                case TAB_HIGHLIGHT:
                    return BUTTON_SOUND_MAP.TAB;
                case MENU:
                    return BUTTON_SOUND_MAP.MENU;
            }
        }
        return null;
    }

    protected STD_SOUNDS getSound() {
        return null;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isIgnoreConfirmBlock() {
        return ignoreConfirmBlock;
    }

    public void setIgnoreConfirmBlock(boolean ignoreConfirmBlock) {
        this.ignoreConfirmBlock = ignoreConfirmBlock;
    }

    public void setDisabledRunnable(Runnable disabledRunnable) {
        this.disabledRunnable = disabledRunnable;
    }

    public SmartButton makeActive() {
        GlobalController.setActiveButton(this);
        return this;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(new FlipDrawable(background, ()-> flipX, ()-> flipY));

    }

    public void setNoClickCheck(boolean noClickCheck) {
        this.noClickCheck = noClickCheck;
    }

}
