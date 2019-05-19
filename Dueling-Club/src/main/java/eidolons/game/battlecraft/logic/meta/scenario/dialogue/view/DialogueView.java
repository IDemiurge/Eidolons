package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;

public class DialogueView extends TablePanelX implements Scene {
    //    private final TablePanelX<T> textArea;
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
        super(1500, 500);
        add(portraitLeft = new DialoguePortraitContainer());
//        add(textArea = new TablePanelX<>());
        TablePanelX<Actor> middle = new TablePanelX<>(1000, 500);
        add(middle);
        middle.add(scroll = new DialogueScroll()).row();
        middle.add(replyBox = new TablePanelX());
        add(portraitRight = new DialoguePortraitContainer());
        middle.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }

    @Override
    public float getPrefHeight() {
        return super.getPrefHeight();
    }

    @Override
    public float getHeight() {
        return 500;
    }

    @Override
    public float getWidth() {
        return 1000;
    }

    @Override
    public float getMinWidth() {
        return getWidth();
    }

    @Override
    public float getMinHeight() {
        return getHeight();
    }

    public DialogueView(SpeechDataSource speechDataSource) {
        this();
        update(speechDataSource);
    }

    public void update(SpeechDataSource data) {
        //        prev = portrait.getPrevious();
//        history.add(data);
        setUserObject(data);
        ActorDataSource left = data.getLeft();
        ActorDataSource right = data.getRight();
        portraitLeft.setUserObject(left);
        portraitRight.setUserObject(right);

        ActorDataSource active = data.isLeftActive() ? left : right;

        scroll.append(data.getMessage(), active.getActorName(), active.getActorImage());
        //TODO info about fx! "Gain 50 xp"

        initResponses(data);
        //        updateResponses(dataSource.getSpeech().getChildren())


    }

    private void initResponses(SpeechDataSource data) {
        replyBox.clearChildren();
//fade
        for (String option : data.getResponses()) {
//process text, color, ..
            SmartButton response;
            if (option.equals(SpeechDataSource.DEFAULT_RESPONSE)) {
                response = new SmartButton(option, STD_BUTTON.OK, () -> respond(option));
            } else
                response = new SmartButton(option, StyleHolder.getDialogueReplyStyle(),
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
        setVisible(false);
        setPosition(0, 0);
        super.act(delta);
        if (done) return;
        if (time > 0) {
            currentTime += (int) (delta * 1000);

            if (time <= currentTime) {
                done = true;
            }
        }
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }
}
