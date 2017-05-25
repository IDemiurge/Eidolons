package main.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import main.libgdx.DialogScenario;

import java.util.Iterator;
import java.util.List;

public class ChainedStage extends Stage {
    private boolean done;
    private Container<DialogScenario> current;
    private Iterator<DialogScenario> iterator;
    private List<DialogScenario> newList = null;

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
                current.setActor(iterator.next());
                setKeyboardFocus(current.getActor());
            } else if (newList != null) {
                final List<DialogScenario> list = this.newList;
                newList = null;
                iterator = list.iterator();
            } else {
                done = true;
            }
        }
    }

    public boolean isDone() {
        return done;
    }
}
