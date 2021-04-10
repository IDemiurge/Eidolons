package libgdx.stage;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import libgdx.gui.generic.btn.SmartButton;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.TablePanelX;
import libgdx.shaders.ShaderDrawer;
import main.system.graphics.FontMaster.FONT;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 5/23/2018.
 */
public class ConfirmationPanel extends TablePanelX implements Blocking, InputProcessor, OverlayingUI {
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
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawableNoMinSize());
        setSize(600, 300);
        add(label = new Label("", StyleHolder.getSizedLabelStyle(getFONT(), getFontSize())))
                .center().colspan(2).minWidth(400).top().row();
        TablePanel<Actor> btns = new TablePanel<>();
        add(btns)
                .center().colspan(2).fill().minWidth(400);
        btns.addNormalSize((Actor) (cancel = new SymbolButton(STD_BUTTON.CANCEL, this::cancel))).left();
        btns.addNormalSize((Actor) (ok = new SymbolButton(STD_BUTTON.OK, this::ok))).right();
        ok.setIgnoreConfirmBlock(true);
        cancel.setIgnoreConfirmBlock(true);
        setVisible(false);

    }

    public static ConfirmationPanel getInstance() {
        if (instance == null)
            instance = new ConfirmationPanel();
        return instance;

    }

    public static void clearInstance() {
        instance = new ConfirmationPanel();
        if (instance.getStage() instanceof GuiStage) {
            ((GuiStage) instance.getStage()).resetConfirmPanel(instance);
        }
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
        if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
            super.draw(batch, 1);
        } else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
    }


    @Override
    public void open() {
        setSize(600, 300);
        getStageWithClosable().openClosable(this);
        cancel.getActor().setVisible(canCancel);
        String wrapped = text;
        //        if (!wrapped.contains("\n")) {
        //            wrapped = TextWrapper.wrapWithNewLine(text,
        //             label.getStyle().font.getSpaceWidth()
        //             FontMaster.getStringLengthForWidth(getFONT(), getFontSize(),
        //              (int) (getWidth() / 3 * 2)));
        //        }
        label.setWrap(true);
        label.setText(wrapped);
        label.pack();
        setFixedSize(false);
        pack();
        setSize(Math.max(getWidth(), GdxMaster.getWidth() / 3), Math.max(getHeight(), GdxMaster.getHeight() / 4));
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

    public void ok() {
        result = true;
        close();
        if (onConfirm != null)
            onConfirm.run();
    }

    public void cancel() {
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

    public Runnable getOnConfirm() {
        return onConfirm;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }
}
