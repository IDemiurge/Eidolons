package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import main.system.sound.AudioEnums;

/**
 * Created by JustMe on 4/19/2018.
 */
public class SymbolButton extends Button implements SmartButton{
    private  Runnable runnable;
    private  Runnable disabledRunnable;
    private boolean ignoreConfirmBlock;
    private boolean flipY;
    private boolean flipX;
    private boolean noClickCheck;
    public SymbolButton(STD_BUTTON button, Runnable runnable) {
        this(button);
        this.runnable = runnable;
    }

    public SymbolButton(STD_BUTTON button ) {
        super(StyleHolder.getButtonStyle(button, null));
        addListener(this);
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public AudioEnums.BUTTON_SOUND_MAP getSoundMap() {
        return null;
    }

    @Override
    public Runnable getDisabledRunnable() {
        return disabledRunnable;
    }
    @Override
    public void setBackground(Drawable background) {
        super.setBackground(new FlipDrawable(background, () -> flipX, () -> flipY));

    }
    @Override
    public boolean isCheckClickArea() {
        return true;
    }

    @Override
    public void setDisabledRunnable(Runnable disabledRunnable) {
        this.disabledRunnable = disabledRunnable;
    }

    @Override
    public boolean isIgnoreConfirmBlock() {
        return ignoreConfirmBlock;
    }

    @Override
    public void setIgnoreConfirmBlock(boolean ignoreConfirmBlock) {
        this.ignoreConfirmBlock = ignoreConfirmBlock;
    }

    public boolean isFlipY() {
        return flipY;
    }

    @Override
    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean isNoClickCheck() {
        return noClickCheck;
    }

    @Override
    public void setNoClickCheck(boolean noClickCheck) {
        this.noClickCheck = noClickCheck;
    }
    // public boolean handle(Event e) {
}
