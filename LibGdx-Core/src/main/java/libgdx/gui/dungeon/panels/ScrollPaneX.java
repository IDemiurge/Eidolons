package libgdx.gui.dungeon.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import libgdx.StyleHolder;

/**
 * Created by JustMe on 11/21/2018.
 */
public class ScrollPaneX extends ScrollPane {
    public  ScrollPaneX(Actor widget) {
        super(widget, StyleHolder.getScrollStyle());
        // widget.
                addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                getStage().setScrollFocus(ScrollPaneX.this);
            }
        });
    }

    @Override
    public void act(float delta) {
        setForceScroll(false, false);
        super.act(delta);
    }
}
