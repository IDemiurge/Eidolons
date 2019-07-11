package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefBackground;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueDataSource;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GuiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Iterator;
import java.util.List;

public class DialogueContainer extends TablePanelX {
    protected boolean done;
    protected Runnable onDoneCallback;
    protected DialogueHandler dialogueHandler;
    protected BriefBackground background;
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

    protected void done() {
        getStage().dialogueDone();
        hide();
    }

    @Override
    public GuiStage getStage() {
        return (GuiStage) super.getStage();
    }

    protected void start() {
        //init key listening
        GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 2);
        next();
    }

    protected void playOut() {
        current.playOut();
    }

    public void next() {
        if (!iterator.hasNext()) {
            GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 2);
            done();
            return;
        }
        clearChildren();
        //fade out and back?
        current = (DialogueView) iterator.next();
        current.setHandler(dialogueHandler);
        current.setContainer(this);
        addActor(background = new BriefBackground(current.getBackgroundPath()));
        addActor(current);
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
        background.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
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

}
