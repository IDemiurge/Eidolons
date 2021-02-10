package libgdx.gui.generic.btn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import libgdx.GdxColorMaster;
import libgdx.StyleHolder;
import libgdx.controls.GlobalController;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;
import main.system.sound.AudioEnums.STD_SOUNDS;

/**
 * Created by JustMe on 12/1/2017.
 */
public class SmartTextButton extends TextButton implements SmartButton {

    private static final FONT DEFAULT_FONT = FONT.MAGIC;
    private ButtonStyled.STD_BUTTON style;
    private Runnable runnable;
    private boolean fixedSize;
    private boolean ignoreConfirmBlock;
    private Runnable disabledRunnable;
    private boolean flipY;
    private boolean flipX;
    private boolean noClickCheck;

    public SmartTextButton(String text, TextButtonStyle style) {
        this(text, style, null, ButtonStyled.STD_BUTTON.MENU);
    }

    public SmartTextButton(String text, ButtonStyled.STD_BUTTON button, Runnable runnable) {
        this(text, button, runnable,
                StringMaster.isEmpty(text) ? null : DEFAULT_FONT, 20, GdxColorMaster.PALE_GOLD);
    }

    public SmartTextButton(ButtonStyled.STD_BUTTON button, Runnable runnable) {
        this("", button, runnable);
    }

    public SmartTextButton(String text, ButtonStyled.STD_BUTTON button, Runnable runnable,
                           FONT font, int size, Color color_) {
        this(text, StyleHolder.getTextButtonStyle(button,
                font, color_, size), runnable, button);
        this.style = button;
    }

    public SmartTextButton(String text, TextButtonStyle style, Runnable runnable, ButtonStyled.STD_BUTTON btnStyle) {
        super(text, StyleHolder.getTextButtonStyle(style, btnStyle));
        this.runnable = runnable;
        addListener(this);
        //        getStyle(). TODO this won't affect  *** !
        this.style = btnStyle;
    }

    public SmartTextButton(ButtonStyled.STD_BUTTON button) {
        this("", button, null);
    }

    public SmartTextButton(String text, Runnable runnable) {
        this(text, StyleHolder.getHqTextButtonStyle(16), runnable, ButtonStyled.STD_BUTTON.MENU);
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

    @Override
    public Runnable getDisabledRunnable() {
        return disabledRunnable;
    }


    @Override
    public boolean isNoClickCheck() {
        return noClickCheck;
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public BUTTON_SOUND_MAP getSoundMap() {
        if (style != null) {
            BUTTON_SOUND_MAP map = new EnumMaster<BUTTON_SOUND_MAP>().retrieveEnumConst(BUTTON_SOUND_MAP.class, style.name());
            if (map != null) {
                return map;
            }
            switch (style) {
                case STAT:
                    return AudioEnums.BUTTON_SOUND_MAP.STAT;
                case TAB_HIGHLIGHT:
                    return AudioEnums.BUTTON_SOUND_MAP.TAB;
                case MENU:
                    return AudioEnums.BUTTON_SOUND_MAP.MENU;
            }
        }
        return null;
    }

    @Override
    public STD_SOUNDS getSound() {
        return null;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    @Override
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isIgnoreConfirmBlock() {
        return ignoreConfirmBlock;
    }

    @Override
    public void setIgnoreConfirmBlock(boolean ignoreConfirmBlock) {
        this.ignoreConfirmBlock = ignoreConfirmBlock;
    }

    @Override
    public void setDisabledRunnable(Runnable disabledRunnable) {
        this.disabledRunnable = disabledRunnable;
    }

    public SmartTextButton makeActive() {
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
    public boolean isFlipX() {
        return flipX;
    }
@Override
    public boolean isFlipY() {
        return flipY;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(new FlipDrawable(background, () -> flipX, () -> flipY));

    }

    public void setNoClickCheck(boolean noClickCheck) {
        this.noClickCheck = noClickCheck;
    }

}
