package main.level_editor.backend.functions.advanced;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import eidolons.game.core.Eidolons;
import eidolons.content.consts.GraphicData;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.utils.TextInputPanel;
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
                    "Vfx",

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
            controlPanel.add(new SmartTextButton(cmd, ButtonStyled.STD_BUTTON.MENU, () ->
                    Eidolons.onNonGdxThread(() ->
                            command(cmd))));
            if (i++>=cmds.length/2){
                controlPanel.row();
                i=0;
            }
        }
    }

    private void command(String cmd) {
        int d = 90;
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
            case   "Rotate45":
                d=45;
            case   "Rotate90":
                GraphicData data = getData();
                int intValue = data. getIntValue(GraphicData.GRAPHIC_VALUE.rotation);
                data.setValue(GraphicData.GRAPHIC_VALUE.rotation, (intValue+d)%360);
                tf.setText(data.getData());
                break;
            case   "Align":
                break;
            case   "Vfx":
                break;
            case   "Blending":
                insert(new GraphicData("").setValue(GraphicData.GRAPHIC_VALUE.blending,
                        LevelEditor.getManager().getEditHandler().chooseEnum(GenericEnums.BLENDING.class)+"").getData());
                break;
        }
    }

    private GraphicData getData() {
        return new GraphicData(tf.getText());
    }
    /*
    keywords with tooltips
    coordinates
    object ID's on click

     */

    public void insert(String insert) {
        int cursorPosition = //tf.getCursorPosition();
        tf.getText().length() ;
        tf.setText(tf.getText().substring(0, cursorPosition) + insert +
                tf.getText().substring(cursorPosition));
    }

    @Override
    public void keyTyped(TextField textField, char c) {
        super.keyTyped(textField, c);
    }
}
