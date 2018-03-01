package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.module.adventure.MacroGame;
import main.libgdx.screens.map.MapScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/28/2018.
 */
public abstract class MapTimedLayer<T extends Actor> extends Group {
    protected DAY_TIME time;
    protected Map<DAY_TIME, List<T>> map = new HashMap<>();
        List<T> displayed = new ArrayList<>();
    protected boolean initialized;

    public MapTimedLayer() {
        for (DAY_TIME time : DAY_TIME.values())
            map.put(time, new ArrayList<>());
        map.put(null, new ArrayList<>());
        setSize(MapScreen.defaultSize,MapScreen.defaultSize);
    }


    protected abstract void init();

    protected DAY_TIME getTime() {
        return MacroGame.game.getTime();
    }

    public void applyTint() {
        for (T sub : displayed) {
            if (isTinted(sub)) {
              tint(sub.getColor());  
            }
        }
    }

    protected boolean isTinted(T sub) {
        return false;
    }

    protected void tint(Color color) {
        switch (time) {

            case DAWN:
                color.g= 0.94f;
                break;
            case MORNING:
                color.r= 0.94f;
                break;
            case NOON:
                color.b= 0.94f;
                break;
            case DUSK:
                color.b= 0.84f;
                break;
            case NIGHTFALL:
                color.r=0.84f;
                color.g=0.84f;
                break;
            case MIDNIGHT:
                color.r=0.84f;
                break;
        }
    }

    public void update() {
        time = getTime();
        if (!initialized)
            init();
        clearLayer();
        spawnLayer();
    }
    protected void spawnLayer() {
        displayed = map.get(time);
        for (T sub : displayed) {
            addActor(sub);
            sub.setVisible(true);
        }
    }

    protected void clearLayer() {

        for (T sub : displayed) {
            sub.setVisible(false);
            sub.remove();
        }
    }

}
