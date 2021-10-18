package libgdx.gui.panels.dialogue;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueDataSource;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.DialogueContainerAdapter;
import libgdx.anims.fullscreen.BriefBackground;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.gui.panels.TablePanelX;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.GuiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Iterator;
import java.util.List;

public class DialogueContainer extends TablePanelX implements DialogueContainerAdapter {
    protected boolean done;
    protected Runnable onDoneCallback;
    protected DialogueHandler dialogueHandler;
    protected BriefBackground bgSprite;
    protected DialogueView current;
    protected List<Scene> toPlay;
    protected Iterator<Scene> iterator;

    public DialogueContainer() {
        GuiEventManager.bind(GuiEventType.DIALOGUE_UPDATED, p -> {
            DialogueDataSource data = (DialogueDataSource) p.get();
            // INK ? not rly used?
            if (current.isDone()) {
                next();
            }

            //            background.setAlpha(alpha);

        });
    }

    public void play(DialogueHandler handler) {
        play(handler.getList(), handler);
        if (handler.isTutorial()) {
            playOut();
        }
    }

    public void play(List<Scene> list, DialogueHandler handler) {
        toPlay = list;
        iterator = toPlay.iterator();
        dialogueHandler = handler;
        start();


    }

    public void done() {
        getStage().dialogueDone();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void setTime(Float aFloat) {

    }

    @Override
    public void disableTimer() {

    }

    @Override
    public GuiStage getStage() {
        return (GuiStage) super.getStage();
    }

    protected void start() {
        //init key listening
        //        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 2);
        dialogueHandler.getDialogueManager().setContainer(this);
        next();
    }

    protected void playOut() {
        current.playOut();
    }

    public void next() {
        if (!iterator.hasNext()) {
            //            GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 2);
            done();
            return;
        }
        clearChildren();
        //fade out and back?
        current = (DialogueView) iterator.next();
        current.setHandler(dialogueHandler);
        current.setContainer(this);
        addActor(bgSprite = new BriefBackground(current.getBackgroundPath()));
        addActor(current);
    }

    @Override
    public void fadeIn() {
        super.fadeIn();
    }

    @Override
    public void updateAct(float delta) {
        //        if (briefingData.backgroundSprite != null)
        //            background.setUserObject(briefingData.backgroundSprite);
        ////        background.setAlpha(0.7f);
        //        plainBg.setImage(briefingData.background);
    }

    public void hide() {

    }

    protected boolean isLinear() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (bgSprite != null) {
            //            if (bgSprite.getColor().a==1)
            float bgAlpha = 0.7f;
            bgSprite.setAlpha(bgAlpha * getColor().a);
        }
        bgSprite.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
        if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
            super.draw(batch, 1);
        } else {
            //            ShaderDrawer.drawWithCustomShader(bgSprite, batch, null, false, false);
            //            ShaderDrawer.drawWithCustomShader(this, batch, null, false, false);
        }
    }

    public BriefBackground getBgSprite() {
        return bgSprite;
    }

    public boolean isOpaque() {
        if (getStage().getBlackout().isOpaque()) {
            return true;
        }
        if (bgSprite != null) {
            return bgSprite.getColor().a == 1f;
        }
        return false;
    }

    @Override
    public void fadeOut() {

    }

    @Override
    public float getHeight() {
        //        if (current != null) {
        //            return current.getHeight();
        //        }
        //        return super.getHeight();
        return 600;
    }

    @Override
    public float getWidth() {
        //        if (current != null) {
        //            return current.getWidth();
        //        }
        return 1500;
    }

    public DialogueView getCurrent() {
        return current;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (current != null) {
            if (current.isDone()) {
                done();
            }
        }

        //        if (current != null && ((Scene) current.getActor()).isDone()) {
        //            if (iterator.hasNext()) {
        //                if (dialogueHandler != null) {
        ////                    dialogueHandler.lineSpoken((Scene) current.getActor());
        //                }
        //                current.setActor((Actor) iterator.next());
        //              getStage().setKeyboardFocus(current.getActor());
        //            } else if (newList != null) {
        //                final List<DialogueView> list = this.newList;
        //                newList = null;
        //                iterator = list.iterator();
        //            } else {
        //                done = true;
        //                if (onDoneCallback != null) {
        //                    onDoneCallback.run();
        //                }
        //                if (dialogueHandler != null) {
        //                    dialogueHandler.dialogueDone();
        //                }
        //            }
    }

    public void setOnDoneCallback(Runnable onDoneCallback) {
        this.onDoneCallback = onDoneCallback;
    }

    public GameDialogue getDialogue() {
        return dialogueHandler.getDialogue();
    }

    public DialogueHandler getHandler() {
        return dialogueHandler;
    }

    @Override
    public void fadeBg(Float dur, Float alpha) {
        ActionMasterGdx.addAlphaAction(this, dur, alpha);
    }

    @Override
    public void fade(Float dur, Float alpha, boolean ui_bg_both) {
        ActionMasterGdx.addAlphaAction((Actor)(ui_bg_both ? getCurrent() : getBgSprite()), dur, alpha);
    }
}
