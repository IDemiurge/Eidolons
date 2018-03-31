package eidolons.libgdx.anims.controls;

import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.system.controls.Controller;

import java.util.Stack;

/**
 * Created by JustMe on 2/10/2017.
 */
public class AnimController implements Controller {
    private static AnimController instance;
    Stack<Animation> stack = new Stack<>();

    public AnimController() {
        instance = this;
//        AnimEventMaster.bind();
    }

    public static AnimController getInstance() {
        return instance;
    }

    public void store(CompositeAnim animation) {
        stack.add(animation);
    }

    @Override
    public boolean charTyped(char c) {
        switch (c) {
            case 'r':
                replayLast();
                return true;
            case 'g':
                toggle();
                return true;
            case 'm':
                modify();
                return true;
            case 'a':
                autofind();
                return true;
        }
        return false;
    }

    private void autofind() {
    }

    private void modify() {
    }

    private void toggle() {
    }

    private void replayLast() {
    }

}
