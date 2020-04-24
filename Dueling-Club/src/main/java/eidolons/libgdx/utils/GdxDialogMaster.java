package eidolons.libgdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import eidolons.libgdx.screens.ScreenMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 2/22/2018.
 */
public class GdxDialogMaster {
    private static final WAIT_OPERATIONS OPERATION = WAIT_OPERATIONS.TEXT_INPUT;

    public static String inputText(String hint, String text) {
        return inputText("Input text", hint, text);
    }

    public static String inputText(String title, String hint, String text) {
//            else
            Gdx.app.postRunnable(() -> textInput(new TextInputListener() {
                                                     @Override
                                                     public void input(String text) {
                                                         WaitMaster.receiveInput(OPERATION, text);
                                                     }

                                                     @Override
                                                     public void canceled() {
                                                         WaitMaster.interrupt(OPERATION);
                                                     }
                                                 }
             , title, text, hint
            ));

        String input = (String) WaitMaster.waitForInput(OPERATION);
        return input;
    }

    private static void textInput(TextInputListener textInputListener, String title,
                                  String text, String hint) {
            ScreenMaster.getScreen().getGuiStage().textInput(textInputListener, title,
             text, hint);
    }
}
