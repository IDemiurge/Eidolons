package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import main.libgdx.DialogScenario;

import java.util.Iterator;
import java.util.List;

public class ChainedStage extends Stage {
    private boolean done;
    private Container<DialogScenario> current;
    private Iterator<DialogScenario> iterator;
    private List<DialogScenario> newList = null;
    private Runnable onDoneCallback;
    private DialogueHandler dialogueHandler;

    public ChainedStage(List<DialogScenario> list) {
        current = new Container<>();
        iterator = list.iterator();
        if (iterator.hasNext()) {
            current.setActor(iterator.next());
        }
        setKeyboardFocus(current.getActor());
        addActor(current);
    }

    public void play(List<DialogScenario> list) {
        newList = list;
    }

    @Override
    public boolean keyDown(int keyCode) {
        return super.keyDown(keyCode);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (current.getActor() != null && current.getActor().isDone()) {
            if (iterator.hasNext()) {
                if (dialogueHandler != null) {
                dialogueHandler.lineSpoken(current.getActor());
                }
                current.setActor(iterator.next());
                setKeyboardFocus(current.getActor());
            } else if (newList != null) {
                final List<DialogScenario> list = this.newList;
                newList = null;
                iterator = list.iterator();
            } else {
                done = true;
                if (onDoneCallback != null) {
                    onDoneCallback.run();
                }
                if (dialogueHandler!=null ){
                    dialogueHandler.dialogueDone();
                }
            }
        }
    }

    public void setOnDoneCallback(Runnable onDoneCallback) {
        this.onDoneCallback = onDoneCallback;
    }

    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }

    public boolean isDone() {
        return done;
    }

    public void setDialogueHandler(DialogueHandler dialogueHandler) {
        this.dialogueHandler = dialogueHandler;
    }
}
