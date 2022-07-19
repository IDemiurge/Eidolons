package gdx.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import logic.functions.GameController;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CellMouseListener extends ClickListener {

    private Consumer<Boolean> c;
    private Runnable onClick;
    private boolean hover = false;

    public CellMouseListener(Consumer<Boolean> c, Runnable onClick) {
        this.c = c;
        this.onClick = onClick;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (hover)
            return;
        super.enter(event, x, y, pointer, fromActor);
        c.accept(true);
        hover = true;
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (!hover)
            return;
        super.exit(event, x, y, pointer, toActor);
        c.accept(false);
        hover = false;

    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (getTapCount()>1) {
            onClick.run();
        }
    }
}
