package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import main.system.auxiliary.log.FileLogManager;

public class DialogueView extends TablePanelX implements Scene {
    private DialogueInputProcessor inputProcessor;
    //    private final TablePanelX<T> textArea;
    DialoguePortraitContainer portraitLeft;
    DialoguePortraitContainer portraitRight;  //IDEA zoom into the portrait sometimes!
    // or flash it with a shader to signify some emotion ... use dif borders
    DialogueScroll scroll;
    TablePanelX replyBox; //slots? should we support horizontal layout ?

    DialogueContainer container;
    private int time;
    private long currentTime = 0;
    private boolean done;

    DialogueHandler handler;
    private String backgroundPath;
    private boolean scrollToBottom;
    private int timer=0;
//    boolean lightweight;
//    boolean upsideDown;
    /*
    cinematic version - no reponses, time-based, click to skip forward
     */

    public DialogueView() {
        super(1800, 550);
        add(portraitLeft = new DialoguePortraitContainer());
//        add(textArea = new TablePanelX<>());
        TablePanelX<Actor> middle = new TablePanelX<>(1200, 500);
        add(middle);
        middle.add(scroll = new DialogueScroll()).row();
        middle.add(replyBox = new TablePanelX(600, 150));
        replyBox.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        add(portraitRight = new DialoguePortraitContainer());
//        middle.setBackground(NinePatchFactory.getHqDrawable());
//        setBackground(NinePatchFactory.getHqDrawable());
        inputProcessor = new DialogueInputProcessor(this);
    }

    public DialogueInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public DialogueView(SpeechDataSource speechDataSource) {
        this();
        update(speechDataSource);
    }

    public void update(SpeechDataSource data) {
        //        prev = portrait.getPrevious();
//        history.add(data);
        setUserObject(data);

        ActorDataSource active = getSpeakerActor();
        ActorDataSource listener = getListenerActor();

        backgroundPath = data.getBackground();

        portraitLeft.setUserObject(active);
        portraitRight.setUserObject(listener);
        scroll.append(data.getMessage(), active.getActorName(), active.getActorImage());
        scrollToBottom=true;
//        scroll.scrollPane.setScrollPercentY(1);

        //getHeight());
        //TODO info about fx! "Gain 50 xp"

        initResponses(data);

        //        updateResponses(dataSource.getSpeech().getChildren())


    }

    private ActorDataSource getListenerActor() {
        SpeechDataSource data = getUserObject();
        ActorDataSource left = data.getLeft();
        ActorDataSource right = data.getRight();

        ActorDataSource listener = !data.isLeftActive() ? left : right;
        if (listener == null) {
            listener = handler.getSpeakerLast();
        }
        return listener;
    }

    private ActorDataSource getSpeakerActor() {
        SpeechDataSource data = getUserObject();

        ActorDataSource left = data.getLeft();
        ActorDataSource right = data.getRight();

        ActorDataSource listener = !data.isLeftActive() ? left : right;
        ActorDataSource active = data.isLeftActive() ? left : right;
        if (active == null) {
            if (handler.isMe(listener)) {
                active = handler.getListenerLast();
            } else {
                active = handler.getMyActor();
            }
        }
        return active;
    }

    @Override
    public SpeechDataSource getUserObject() {
        return (SpeechDataSource) super.getUserObject();
    }

    private void initResponses(SpeechDataSource data) {
        replyBox.clearChildren();
//fade
        int i = 0;
        for (String option : data.getResponses()) {
//process text, color, ..
            SmartButton response;
//            data.getLeft()

            final int i_ = i++;
            if (option.equals(SpeechDataSource.DEFAULT_RESPONSE)) {
                response = new SmartButton("Continue", StyleHolder.getDialogueReplyStyle(),
                        () -> respond(option, i_), STD_BUTTON.TAB_HIGHLIGHT);
//                response = new SmartButton(STD_BUTTON.OK, () -> respond(option, i_));
            } else
                response = new SmartButton(option, StyleHolder.getDialogueReplyStyle(),
                        () -> respond(option, i_), STD_BUTTON.TAB_HIGHLIGHT);

            replyBox.add(response).left().
                    row();
        }
    }


    public void playOut() {
        while (tryNext(false)) {
        }
    }

    protected boolean tryNext() {
        return tryNext(true);
    }

    protected boolean tryNext(boolean allowFinish) {
        //check index?
        try {
            return respond(SpeechDataSource.DEFAULT_RESPONSE, 0, allowFinish);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    private boolean respond(String option, int index) {
        return respond(option, index, true);
    }

    private boolean respond(String option, int index, boolean allowFinish) {

        FileLogManager.streamMain("Dialogue continues");

        if (container != null) {
//            container.respond(option);
            ActorDataSource actor = getSpeakerActor();
            if (!option.equalsIgnoreCase(SpeechDataSource.DEFAULT_RESPONSE))
                if (actor == null) {
                    scroll.append(option, "", "");
                } else
                    scroll.append(option, actor.getActorName(), actor.getActorImage());


            SpeechDataSource next =
                    handler.lineSpoken(getUserObject().speech, index);
             if (next != null) {
                scroll.append("", "", Images.SEPARATOR_ALT).center().setX(getWidth()/2);
                update(next);
                return true;
            } else {
                if (allowFinish)
                    container.next();
                return false;
            }
//            if (actor.isMe()){
//                getUserObject().
//            GuiEventManager.trigger(GuiEventType. )
//            }
        }

        return false;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void act(float delta) {
        if (scrollToBottom) {
//            scroll.scrollPane.setScrollY(scroll.scrollPane.getActor().getHeight() + 1100);
            scroll.scrollPane.setScrollPercentY(1);
        if ( scroll .scrollPane .getVisualScrollY() >=  scroll .scrollPane .getMaxY()){
            timer++;
            if (timer>=10){
                scrollToBottom=false;
                timer = 0;
            }
        }
        }
//        setVisible(true);
        setPosition(250, 50);
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
    public void layout() {
        super.layout();
        replyBox.setY(30);
    }

    public void setContainer(DialogueContainer container) {
        this.container = container;
    }

    @Override
    public float getPrefHeight() {
        return super.getPrefHeight();
    }

    public void setHandler(DialogueHandler handler) {
        this.handler = handler;
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

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }
}
