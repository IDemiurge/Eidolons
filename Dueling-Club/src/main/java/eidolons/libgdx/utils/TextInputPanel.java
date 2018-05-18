package eidolons.libgdx.utils;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;

/**
 * Created by JustMe on 2/22/2018.
 */
public class TextInputPanel extends TablePanel implements Blocking, TextFieldListener {
    String title, text, hint;
    TextInputListener textInputListener;
    private TextField tf;

    public TextInputPanel(String title, String text, String hint, TextInputListener textInputListener) {
        this.title = title;
        this.text = text;
        this.hint = hint;
        this.textInputListener = textInputListener;

//        tf= new TextField(text, style);
//        tf.setTextFieldListener(this);
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }
    @Override
    public void close() {

    }

    @Override
    public void keyTyped(TextField textField, char c) {
//        Input.Keys.ENTER
    }

    @Override
    public void open() {

    }

}
