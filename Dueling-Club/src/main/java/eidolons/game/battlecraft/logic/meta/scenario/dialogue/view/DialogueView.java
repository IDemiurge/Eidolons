package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;

public class DialogueView extends TablePanelX {
    DialoguePortraitContainer portraitLeft;
    DialoguePortraitContainer portraitRight;  //IDEA zoom into the portrait sometimes!
    // or flash it with a shader to signify some emotion ... use dif borders
    DialogueScroll scroll;
    TablePanelX replyBox; //slots? should we support horizontal layout ?

    private int time;
    private long currentTime = 0;
    private boolean done;

//    boolean lightweight;
//    boolean upsideDown;
    /*
    cinematic version - no reponses, time-based, click to skip forward
     */

    public DialogueView() {
        TablePanelX<Actor> textArea = new TablePanelX<>();


        add(portraitLeft);
        add(textArea);
        add(portraitRight);

    }

    public void update(SpeechDataSource data) {
        //        prev = portrait.getPrevious();
        ActorDataSource left = data.getLeft();
        ActorDataSource right = data.getRight();
        portraitLeft.setUserObject(left);
        portraitRight.setUserObject(right);

        ActorDataSource  active =   data.isLeftActive() ? left : right;

        scroll.append(data.getMessage(), active.getActorName(), active.getActorImage());
        //TODO info about fx! "Gain 50 xp"

        initResponses(data);
        //        updateResponses(dataSource.getSpeech().getChildren())


    }

    private void initResponses(SpeechDataSource data) {
        replyBox.clearChildren();

        for (String option : data.getResponses()) {
//process text, color, ..
            SmartButton response = new SmartButton(option, StyleHolder.getDialogueReplyStyle(),
             () -> respond(option), STD_BUTTON.TAB_HIGHLIGHT);

            replyBox.add(response).left().
             row();
        }
    }

    private void respond(String option) {
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (done) return;
        if (time > 0) {
            currentTime += (int) (delta * 1000);

            if (time <= currentTime) {
                done = true;
            }
        }
    }
}
