package eidolons.libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderMaster;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.text.TextWrapper;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 5/23/2018.
 */
public class ConfirmationPanel extends TablePanelX implements Blocking, InputProcessor {
    private static ConfirmationPanel instance;
    Label label;
    SmartButton ok;
    SmartButton cancel;
    private boolean canCancel;
    private Runnable onConfirm;
    private Runnable onCancel;
    private String text;
    private boolean result;

    private ConfirmationPanel() {
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        setSize(600, 300);
        add(label = new Label("", StyleHolder.getSizedLabelStyle(getFONT(), getFontSize())))
         .center().colspan(2).minWidth(400).top().row();
        TablePanel<Actor> btns = new TablePanel<>();
        add(btns)
         .center().colspan(2).fill().minWidth(400);
        btns.addNormalSize(cancel = new SmartButton(STD_BUTTON.CANCEL, () -> {
            cancel();
        })).left();
        btns.addNormalSize(ok = new SmartButton(STD_BUTTON.OK, () -> {
            ok();
        })).right();
        ok.setIgnoreConfirmBlock(true);
        cancel.setIgnoreConfirmBlock(true);
        setVisible(false);

    }

    public static ConfirmationPanel getInstance() {
        if (instance == null)
            instance = new ConfirmationPanel();
        return instance;

    }

    public boolean isPausing() {
        return false;
    }

    private Integer getFontSize() {
        return 20;
    }

    private FONT getFONT() {
        return FONT.METAMORPH;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderMaster.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderMaster.drawWithCustomShader(this, batch, null);
    }


    @Override
    public void open() {
        getStageWithClosable().openClosable(this);
        cancel.setVisible(canCancel);
        String wrapped = text;
        if (!wrapped.contains("\n")) {
            wrapped = TextWrapper.wrapWithNewLine(text,
             FontMaster.getStringLengthForWidth(getFONT(), getFontSize(),
              (int) (getWidth() / 3 * 2)));
        }
        label.setText(wrapped);
        label.pack();
        setPosition(GdxMaster.centerWidth(this), GdxMaster.centerHeight(this));
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
                cancel();
                return true;
            case Keys.ENTER:
                ok();
                return true;
        }

        return false;
    }

    @Override
    public void close() {
        //        Eidolons.getScreen().updateInputController();
        getStageWithClosable().closeClosable(this);
        WaitMaster.receiveInput(WAIT_OPERATIONS.CONFIRM, result);
    }

    private void ok() {
        result = true;
        close();
        if (onConfirm != null)
            onConfirm.run();
    }

    private void cancel() {
        result = false;
        close();
        if (onCancel != null)
            onCancel.run();
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }
}
