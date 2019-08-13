package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.Images;
import main.system.auxiliary.log.FileLogManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

public class DialogueView extends TablePanelX implements Scene {
    private DialogueInputProcessor inputProcessor;
    //    private final TablePanelX<T> textArea;
    DialoguePortraitContainer portraitLeft;
    DialoguePortraitContainer portraitRight;  //IDEA zoom into the portrait sometimes!
    // or flash it with a shader to signify some emotion ... use dif borders
    DialogueScroll scroll;
    TablePanelX replyBox; //slots? should we support horizontal layout ?

    DialogueContainer container;
    private long currentTime = 0;
    private boolean done;

    DialogueHandler handler;
    private String backgroundPath;
    private boolean scrollToBottom;
    private int timer = 0;
    private Float timeToRespond;
    private boolean canSkip;

    public static final float HEIGHT = 400;
    public static final float WIDTH = 1800;
    private boolean autoRespond;
    private Float speed = 0.5f;
    private boolean paused;
//    boolean lightweight;
//    boolean upsideDown;
    /*
    cinematic version - no reponses, time-based, click to skip forward
     */

    public DialogueView() {
        super(WIDTH, HEIGHT);
        add(portraitLeft = new DialoguePortraitContainer());
//        add(textArea = new TablePanelX<>());
        TablePanelX<Actor> middle = new TablePanelX<>(WIDTH / 3 * 2, HEIGHT);
        add(middle);
        middle.add(scroll = new DialogueScroll()).row();
        middle.add(replyBox = new TablePanelX(WIDTH / 3, HEIGHT / 3));
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
        String text = data.getMessage();
        if (CoreEngine.isIDE()) {
            if (SpeechScript.TEST_MODE) {
                if (data.getSpeech().getScript() != null) {
                    text += "\n" + data.getSpeech().getScript().getScriptText();
                }
            }
        }
        scroll.append(text, active.getActorName(), active.getActorImage());
        scrollToBottom = true;
//        scroll.scrollPane.setScrollPercentY(1);

        //getHeight());
        //TODO info about fx! "Gain 50 xp"

        if (timeToRespond != null)
            if (!canSkip) {
                return;
            }
        initResponses(data);

    }
    //        updateResponses(dataSource.getSpeech().getChildren())


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
        if (data.getTime() != null) {
            timeToRespond = new Float(data.getTime());
        }
        canSkip = data.canSkip;
    }


    public void playOut() {
        while (tryNext(false)) {
        }
    }

    public boolean space() {
        if (!CoreEngine.isIDE())
            if (!isRepliesEnabled()) {
                paused = !paused;
                return true;
            }
        return tryNext();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
        }
        else
            ShaderDrawer.drawWithCustomShader(this, batch, paused? GrayscaleShader.getGrayscaleShader(): null );
    }

    public boolean tryNext() {
        return tryNext(true);
    }

    private boolean isRepliesEnabled() {
        return replyBox.getTouchable() == Touchable.enabled;
    }


    protected boolean tryNext(boolean allowFinish) {

        Eidolons.onThisOrNonGdxThread(() ->
                respond(SpeechDataSource.DEFAULT_RESPONSE, 0, allowFinish));
        return true;
    }

    private boolean respond(String option, int index) {
        Eidolons.onThisOrNonGdxThread(() -> {
            respond(option, index, true);
        });
        return true;
    }

    private boolean respond(String option, int index, boolean allowFinish) {
        autoRespond = false;
        FileLogManager.streamMain("Dialogue continues");

        if (container != null) {
//            container.respond(option);
            ActorDataSource actor = getSpeakerActor();
            if (!option.equalsIgnoreCase(SpeechDataSource.DEFAULT_RESPONSE))
                if (actor == null) {
                    scroll.append(option, "", "");
                } else {
                    scroll.append(option, actor.getActorName(), actor.getActorImage());


                }
            if (actor == null)
                if (actor.getActor().getLinkedUnit() == null)
                    try {
                        actor.getActor().setupLinkedUnit();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

            SpeechDataSource next =
                    handler.lineSpoken(getUserObject().speech, index);
            if (next != null) {
                scroll.append("", "", Images.SEPARATOR_ALT).center().setX(getWidth() / 2);
                update(next);
                int time = handler.getDialogue().getTimeBetweenScripts() + next.getMessage().length() *
                        handler.getDialogue().getTimeBetweenScriptsLengthMultiplier();
                if (time > 0) {
                    disableReplies();
                    main.system.auxiliary.log.LogMaster.dev("autoRespond = true in " + handler.getDialogue().getTimeBetweenScripts());
                    WaitMaster.doAfterWait(time, () ->
                            {
                                autoRespond = true;
                                main.system.auxiliary.log.LogMaster.dev("autoRespond = true; ");
                            }
                    );
                } else {
                    if (!isRepliesEnabled()) {
                        enableReplies();
                    }
                }


//                if (container.getColor().a == 0)
//                    container.fadeIn(); //TODO refactor
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

    private void enableReplies() {
        replyBox.fadeIn();
        replyBox.setTouchable(Touchable.enabled);
    }

    private void disableReplies() {
        replyBox.fadeOut();
        replyBox.setTouchable(Touchable.disabled);
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void act(float delta) {
        if (autoRespond) {
            if (!paused)
                if (!container.getHandler().isScriptRunning()) {
                    autoRespond = false;
                    tryNext(true);
                }
        } else if (timeToRespond != null)
            if (timeToRespond > 0)
                if (!paused) {
                    timeToRespond -= delta * 1000;
                    main.system.auxiliary.log.LogMaster.log(1, "Time to respond: " + timeToRespond + " " + getUserObject().getMessage());
                    if (timeToRespond <= 0)
                        if (!container.getHandler().isScriptRunning()) {
                            timeToRespond = null;
                            main.system.auxiliary.log.LogMaster.important(" " + "Auto-respond: " + " " + getUserObject().getMessage()
                            );
                            tryNext(true);
                        }
                }

        if (scrollToBottom) {
//            scroll.scrollPane.setScrollY(scroll.scrollPane.getActor().getHeight() + 1100);
            scroll.scrollPane.setScrollPercentY(1);
            if (scroll.scrollPane.getVisualScrollY() >= scroll.scrollPane.getMaxY()) {
                timer++;
                if (timer >= 10) {
                    scrollToBottom = false;
                    timer = 0;
                }
            }
        }
//        setVisible(true);
        setPosition(-150, 0);
        if (speed != null) {
            delta = delta * speed;
        }
        super.act(delta);
        if (isDoubleSpeedFade()) {
            portraitLeft.act(delta);
            portraitRight.act(delta);
        }
        if (done)
            return;
//        if (timeToRespond > 0) {
//            currentTime += (int) (delta * 1000);
//            if (time <= currentTime) {
//                done = true;
//            }
//        }
    }

    private boolean isDoubleSpeedFade() {
        return true;
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
        return HEIGHT;
    }

    @Override
    public float getWidth() {
        return WIDTH;
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

    public void setTime(Float valueOf) {
        main.system.auxiliary.log.LogMaster.important(
                " from " + timeToRespond +
                        " to " + valueOf);
        timeToRespond = valueOf;

        if (timeToRespond == null) {
            disableReplies();
        } else {
            enableReplies();
        }
    }

}
