package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.MacroTimeMaster;
import eidolons.libgdx.screens.map.MapScreen;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.CoreEngine;

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
    protected boolean initialized;
    List<T> displayed = new ArrayList<>();

    public MapTimedLayer() {
        for (DAY_TIME time : DAY_TIME.values())
            map.put(time, new ArrayList<>());
        map.put(null, new ArrayList<>());
        setSize(MapScreen.defaultSize, MapScreen.defaultSize);
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, param -> {
                update();
        });
    }


    protected abstract void init();

    protected DAY_TIME getTime() {
        return MacroTimeMaster.getInstance().getDayTime();
    }

    public void applyTint() {
        for (T sub : displayed) {
            if (isTinted(sub)) {
                tint(sub.getColor());
            }
        }
    }

    public void applyDynamicTint() {
        for (T sub : displayed) {
            if (isTinted(sub)) {
                tintDynamic(sub.getColor(), sub);
            }
        }
    }

    protected boolean isTinted(T sub) {
        return false;
    }

    protected void tintDynamic(Color color, T sub) {
        Color c = new Color(color);
        tint(color, time);
        float percentage =
         MacroGame.getGame().getLoop().getTimeMaster().getPercentageIntoNextDaytime();
        color.lerp(tint(c, time.getNext()), percentage);
        applyAlpha(color, sub);
    }

    protected void applyAlpha(Color color, T sub) {

    }

    protected void tint(Color color) {
        tint(color, time);
    }

    protected Color tint(Color color, DAY_TIME time) {
        color.r = 1f;
        color.g = 1f;
        color.b = 1f;
        switch (time) {

            case DAWN:
                color.g = 0.94f;
                break;
            case MORNING:
                color.r = 0.96f;
                break;
            case MIDDAY:
                color.b = 0.94f;
                break;
            case DUSK:
                color.b = 0.86f;
                break;
            case NIGHTFALL:
                color.r = 0.84f;
                color.g = 0.87f;
                break;
            case MIDNIGHT:
                color.r = 0.89f;
                color.g = 0.94f;
                break;
        }
        tintWithWeather(color, MacroGame.getGame().getLoop().getTimeMaster().getWeather());
        return new Color(color);
    }

    private void tintWithWeather(Color color, WEATHER weather) {
        switch (weather) {
            case CLEAR:
                break;
            case OVERCAST:
                color.r = color.r * 0.89f;
                color.g = color.g * 0.90f;
                color.b = color.b * 0.94f;
                break;
            case STORM:
                color.r = color.r * 0.81f;
                color.g = color.g * 0.80f;
                color.b = color.b * 0.90f;
                break;
            case MISTY:
                color.r = color.r * 0.92f;
                color.g = color.g * 0.95f;
                color.b = color.b * 0.97f;
                break;
        }
    }

    protected WEATHER getWeather() {
        return MacroGame.getGame().getWeather();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    public void update() {
        time = getTime();
        if (!initialized)
            init();
        clearLayer();
        spawnLayer();
        if (CoreEngine.isMapEditor())
            return;
        if (isTinted())
            applyTint();
    }

    protected boolean isTinted() {
        return false;
    }

    protected void spawnLayer() {
        displayed = map.get(time);
        for (T sub : displayed) {
            addActor(sub);
            sub.setVisible(true);
        }
    }

    protected void clearLayer() {

        for (T sub : new ArrayList<>(displayed)) {
            sub.setVisible(false);
            sub.remove();
            displayed.remove(sub);
        }
    }

}
