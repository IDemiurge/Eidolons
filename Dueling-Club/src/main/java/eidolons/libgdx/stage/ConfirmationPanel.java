package eidolons.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 5/23/2018.
 */
public class ConfirmationPanel extends TablePanelX implements Blocking, InputProcessor {
    private static ConfirmationPanel instance;
    private boolean canCancel;
    private Runnable onConfirm;
    private String text;
    private InputProcessor bufferedController;

    Label label;
    TextButtonX ok;
    TextButtonX cancel;

    private ConfirmationPanel() {
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        setSize(600, 300);
        add(label= new Label("", StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 20)))
         .center().colspan(2).minWidth(400).top(). row();
        TablePanel<Actor> btns = new TablePanel<>();
        add( btns)
         .center().colspan(2).fill().minWidth(400) ;
        btns.addNormalSize(cancel = new TextButtonX(STD_BUTTON.CANCEL, () -> {
            cancel();
        })).left();
        btns. addNormalSize(ok = new TextButtonX(STD_BUTTON.OK, () -> {
            ok();
        })).right();
        setVisible(false);

    }

    public static ConfirmationPanel getInstance() {
        if (instance==null )
            instance = new ConfirmationPanel();
        return instance;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha== ShaderMaster.SUPER_DRAW )
            super.draw(batch, 1);
        else
            ShaderMaster.drawWithCustomShader(this, batch, null );
    }

    @Override
    public void close() {
//        Eidolons.getScreen().updateInputController();
        getStageWithClosable().closeClosable(this);
        Gdx.input.setInputProcessor(bufferedController);
    }

    @Override
    public void open() {
        getStageWithClosable().openClosable(this);
        cancel.setVisible(canCancel);
        label.setText(text);
        label.pack();
        bufferedController = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(
         new InputMultiplexer(getStage(),
         this)
        );

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

    private void ok() {
        close();
        onConfirm.run();
    }

    private void cancel() {
        close();
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
}
