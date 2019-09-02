package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.Images;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

//IDEA zoom into the portrait sometimes! perhaps we can have a Full sprite + scissors? even custom border overlay
// or flash it with a shader to signify some emotion ... use dif borders
public class DialogueView extends TablePanelX implements Scene {
    public static final float HEIGHT = 400;
    public static final float WIDTH = 1800;

    private DialogueInputProcessor inputProcessor;
    DialogueContainer container;
    DialogueHandler handler;

    DialoguePortraitContainer portraitLeft;
    DialoguePortraitContainer portraitRight;
    DialogueScroll scroll;
    TablePanelX replyBox; //slots? should we support horizontal layout ?

    private String backgroundPath;
    private int timer = 0;
    private Float timeToRespond;
    private boolean canSkip;
    private boolean autoRespond;
    private Float speed = 0.5f;
    private boolean paused;
    private boolean timerDisabled;
    private boolean scrollToBottom;


    public DialogueView() {
        super(WIDTH, HEIGHT);
        add(portraitLeft = new DialoguePortraitContainer());
        TablePanelX<Actor> middle = new TablePanelX<>(WIDTH / 3 * 2
                , HEIGHT);
        add(middle);
        middle.add(scroll = new DialogueScroll()).row();
        middle.add(replyBox = new TablePanelX(WIDTH / 3, HEIGHT / 3));
//        replyBox.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        add(portraitRight = new DialoguePortraitContainer());
//        middle.setBackground(NinePatchFactory.getHqDrawable());
//        setBackground(NinePatchFactory.getHqDrawable());
        inputProcessor = new DialogueInputProcessor(this);

        StyleHolder.getDialogueReplyStyle(); //have init it!
    }

    public DialogueInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public DialogueView(SpeechDataSource speechDataSource) {
        this();
        update(speechDataSource);
    }

    public void update(SpeechDataSource data) {
        update(data, false);
    }
    public void update(SpeechDataSource data, boolean appendedMessage) {
        //        prev = portrait.getPrevious();
//        history.add(data);

        if (getUserObject() == null) {
            setTime(1f);
        }
        setUserObject(data);
        data.setHandler(handler);
        ActorDataSource active = data.getSpeakerActor();
        ActorDataSource listener = data.getListenerActor();

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
        scroll.append(text, active.getActorName(), active.getActorImage(), appendedMessage);
        scrollToBottom = true;

        if (timeToRespond != null)
            if (!canSkip) {
                return;
            }

        initResponses(data);

    }


    private void initResponses(SpeechDataSource data) {
        replyBox.clearChildren();
//fade
        int i = 0;
        for (String option : data.getResponses()) {
//process text, color, ..
            SmartButton response;
            final int i_ = i++;
            if (option.equals(SpeechDataSource.DEFAULT_RESPONSE)) {
                response = new SmartButton("Continue", StyleHolder.getDialogueReplyStyle(),
                        () -> respond(option, i_), STD_BUTTON.HIGHLIGHT_ALT);
//                response = new SmartButton(STD_BUTTON.OK, () -> respond(option, i_));
            } else
                response = new SmartButton(option, StyleHolder.getDialogueReplyStyle(),
                        () -> respond(option, i_), STD_BUTTON.HIGHLIGHT_ALT);

            replyBox.add(response).left().row();
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
        if (!CoreEngine.isIDE() || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            if (!isRepliesEnabled()) {
                paused = !paused;
                return true;
            }
        disableTimer();
        return tryNext();
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
        if (container != null) {
//            container.respond(option);
            ActorDataSource actor = getUserObject().getSpeakerActor();
            SpeechDataSource next =
                    handler.lineSpoken(getUserObject().speech, index);

            boolean appendedMessage = false;
            if (next!=null)
            if (next.getSpeakerActor().getActorName().equalsIgnoreCase(
                    actor.getActorName())) {
                appendedMessage = getUserObject().isAppendedMessage();
            }
            if (!option.equalsIgnoreCase(SpeechDataSource.DEFAULT_RESPONSE))
                if (actor == null) {
                    scroll.append(option, "", "", appendedMessage);
                } else {
                    scroll.append(option, actor.getActorName(), actor.getActorImage(), appendedMessage);
                }
            if (actor != null) {
                if (actor.getActor().getLinkedUnit() == null)
                    try {
                        actor.getActor().setupLinkedUnit();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
            }
//            timerDisabled = false; ???

            if (next != null) {
                if (!appendedMessage)
                    scroll.append("", "", Images.SEPARATOR_ALT, false).center().setX(getWidth() / 2);
                update(next, appendedMessage);

                if (!appendedMessage)
                    try { //TODO refactor!
                        handler.checkAutoCamera(getUserObject().getSpeakerActor().getActor().getLinkedUnit());
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                GuiEventManager.trigger(GuiEventType.ACTOR_SPEAKS, getUserObject().getSpeakerActor().getActor().getLinkedUnit());
                if (timeToRespond == null) {
                    int time = handler.getDialogue().getTimeBetweenScripts() + next.getMessage().length() *
                            handler.getDialogue().getTimeBetweenScriptsLengthMultiplier();
                    if (time > 0) {
//                    disableReplies();
                        main.system.auxiliary.log.LogMaster.dev("autoRespond = true in " + handler.getDialogue().getTimeBetweenScripts());
                        setTime(new Float(time));
                    }
                }
                if (timeToRespond != null)
                {
                    if (!isRepliesEnabled()) {
                        enableReplies();
                    }
                }
                return true;
            } else {
                if (allowFinish)
                    container.next();
                return false;
            }
        }
        return false;
    }

    public void setTime(Float valueOf) {
        main.system.auxiliary.log.LogMaster.important(
                " from " + timeToRespond +
                        " to " + valueOf);
        timeToRespond = valueOf;

        if (timeToRespond != null) {
            disableReplies();
            timerDisabled = false;
        } else {
            enableReplies();
        }
    }

    public void disableTimer() {
        timeToRespond=null;
        timerDisabled = true;
    }

    private boolean isDoubleSpeedFade() {
        return true;
    }

    @Override
    public void act(float delta) {
        if (autoRespond) {
            if (!paused)
                if (!timerDisabled)
                    if (!container.getHandler().isScriptRunning()) {
                        autoRespond = false;
                        tryNext(true);
                    }
        } else if (timeToRespond != null)
            if (timeToRespond !=null  )
                if (!timerDisabled)
                    if (!paused) {
                        timeToRespond -= delta * 1000;
//                    main.system.auxiliary.log.LogMaster.log(1, "Time to respond: " + timeToRespond + " " + getUserObject().getMessage());
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
    }

    @Override
    public SpeechDataSource getUserObject() {
        return (SpeechDataSource) super.getUserObject();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
            super.draw(batch, 1);
        } else
            ShaderDrawer.drawWithCustomShader(this, batch, paused ? GrayscaleShader.getGrayscaleShader() : null);
    }
    @Override
    public void layout() {
        super.layout();
//        scroll.setY(-20);
//        replyBox.setY(30);
    }

    private void enableReplies() {
        replyBox.fadeIn();
        replyBox.setTouchable(Touchable.enabled);
        GdxMaster.setDefaultCursor();
    }

    private void disableReplies() {
        replyBox.fadeOut();
        replyBox.setTouchable(Touchable.disabled);
        GdxMaster.setEmptyCursor();
    }

    public boolean isDone() {
        return false;
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

    public void resume() {
        paused=false;
    }
    public void pause() {
        paused=true;
    }
}
