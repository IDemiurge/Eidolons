package eidolons.libgdx.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.kotcrab.vis.ui.VisUI;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;

/**
 * Created by JustMe on 2/22/2018.
 */
public class TextInputPanel extends TablePanelX implements Blocking, TextFieldListener, TextField.TextFieldFilter {
    String title, text, hint;
    TextInputListener textInputListener;
    private TextField tf;

    public TextInputPanel(String title, String text, String hint, TextInputListener textInputListener) {
        this.title = title;
        this.text = text;
        this.hint = hint;
        this.textInputListener = textInputListener;
//StyleHolder.getTextButtonStyle()
        add(new LabelX(title)).top().row();
        add(tf = new TextField(text, VisUI.getSkin())).width(800).height(500).row();
        tf.setTextFieldListener(this);
        tf.setTextFieldFilter(this);
        TablePanelX<Actor> table = new TablePanelX<>();
        add(table);
        table.setBackground(NinePatchFactory.getLightPanelFilledSmallDrawable());
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        table.add(new SmartButton(ButtonStyled.STD_BUTTON.OK, () -> ok())).left().bottom();
        table.add(new SmartButton(ButtonStyled.STD_BUTTON.CANCEL, () -> close())).right().bottom();
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }
    @Override
    public void close() {
        fadeOut();
        textInputListener.canceled();
    }
    public void ok(){
        fadeOut();
        textInputListener.input(tf.getText());
    }

    @Override
    public void fadeOut() {
        clearActions();
        ActionMaster.addFadeOutAction(this, 0.25f);
        ActionMaster.addRemoveAfter(this);
    }

    @Override
    public void keyTyped(TextField textField, char c) {
        if (Input.Keys.ENTER==(int)c) {
            main.system.auxiliary.log.LogMaster.log(1,"Input: " +textField.getText());
        }
        main.system.auxiliary.log.LogMaster.log(1,"Input: " +textField.getText());
        return;
    }

    @Override
    public void open() {
        fadeIn();
        tf.setText("Input something...");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public boolean acceptChar(TextField textField, char c) {
        return true;
    }
}
