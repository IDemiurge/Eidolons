package main.level_editor.backend.functions.advanced;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import eidolons.game.core.Eidolons;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.utils.TextInputPanel;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.ContainerUtils;

public class ScriptInputHelper extends TextInputPanel {
    private final LE_Manager manager;
    public static final String[] cmds =
            {
                    "Keyword",
                    "Last",
                    "Area",
                    "Id",
                    "Struct",
            };
    private String lastInserted;

    public ScriptInputHelper(
            LE_Manager manager,
            String title, String text, String hint,
            Input.TextInputListener textInputListener) {
        super(title, text, hint, textInputListener);
        this.manager = manager;
        TablePanelX<Actor> controlPanel = new TablePanelX<>();
        controlPanel.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        row();
        add(controlPanel);
        for (String cmd : cmds) {
            controlPanel.add(new SmartTextButton(cmd, ButtonStyled.STD_BUTTON.MENU, () ->
                    Eidolons.onNonGdxThread(() ->
                            command(cmd))));
        }
    }

    private void command(String cmd) {
        switch (cmd) {
            case "Keyword":
                //TODO         //scrollpane with keywords?.. or show them on demand instead?
                break;
            case "Last":
                insert(lastInserted);
                break;
            case "Id":
                insert(ContainerUtils.constructStringContainer(true,
                        manager.getSelectionHandler().getSelection().getIds(), ","));
                break;
            case "Struct":
                insert(manager.getStructureMaster().getLowestStruct
                        (manager.getSelectionHandler().getSelection().getLastCoordinates()).getName());
                break;
            case "Area":
                insert(ContainerUtils.constructStringContainer(true,
                        manager.getSelectionHandler().getSelection().getCoordinates(), ","));
                break;
        }
    }
    /*
    keywords with tooltips
    coordinates
    object ID's on click

     */

    public void insert(String insert) {
        lastInserted=insert;
        int cursorPosition = tf.getCursorPosition();
        tf.setText(tf.getText().substring(0, cursorPosition) + insert +
                tf.getText().substring(cursorPosition));
    }

    @Override
    public void keyTyped(TextField textField, char c) {
        super.keyTyped(textField, c);
    }
}
