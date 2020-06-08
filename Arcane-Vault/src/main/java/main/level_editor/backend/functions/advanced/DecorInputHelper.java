package main.level_editor.backend.functions.advanced;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.utils.TextInputPanel;
import main.content.enums.GenericEnums;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Manager;

public class DecorInputHelper extends TextInputPanel {
    private final LE_Manager manager;
    public static final String[] cmds =
            {
                    "FlipX",
                    "FlipY",
                    "Rotate90",
                    "Rotate45",
                    "Align",
                    "Blending",

            };

    public DecorInputHelper(
            LE_Manager manager,
            String title, String text, String hint,
            Input.TextInputListener textInputListener) {
        super(title, text, hint, textInputListener);
        this.manager = manager;
        TablePanelX<Actor> controlPanel = new TablePanelX<>();
        controlPanel.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        row();
        add(controlPanel);
            int i=0;
        for (String cmd : cmds) {
            controlPanel.add(new SmartButton(cmd, ButtonStyled.STD_BUTTON.MENU, () ->
                    Eidolons.onNonGdxThread(() ->
                            command(cmd))));
            if (i++>=cmds.length/2){
                controlPanel.row();
            }
        }
    }

    private void command(String cmd) {
        switch (cmd) {
            case "FlipX":
                //remove if contains
                insert(new GraphicData("").setValue(GraphicData.GRAPHIC_VALUE.flipX,
                        "true").getData());
                break;
            case   "FlipY":
                insert(new GraphicData("").setValue(GraphicData.GRAPHIC_VALUE.flipY,
                         "true").getData());
                break;
            case   "Rotate90":
                break;
            case   "Rotate45":
                break;
            case   "Align":
                break;
            case   "Blending":
                insert(new GraphicData("").setValue(GraphicData.GRAPHIC_VALUE.blending,
                        LevelEditor.getManager().getEditHandler().chooseEnum(GenericEnums.BLENDING.class)+"").getData());
                break;
        }
    }
    /*
    keywords with tooltips
    coordinates
    object ID's on click

     */

    public void insert(String insert) {
        int cursorPosition = tf.getCursorPosition();
        tf.setText(tf.getText().substring(0, cursorPosition) + insert +
                tf.getText().substring(cursorPosition));
    }

    @Override
    public void keyTyped(TextField textField, char c) {
        super.keyTyped(textField, c);
    }
}
