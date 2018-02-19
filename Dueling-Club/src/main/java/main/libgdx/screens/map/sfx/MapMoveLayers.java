package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/19/2018.
 */
public class MapMoveLayers extends Group {
    DAY_TIME time;
    Map<ImageContainer, MAP_MOVING_LAYER_TYPE> displayed;
    Map<DAY_TIME, Map<ImageContainer, MAP_MOVING_LAYER_TYPE>> map = new HashMap<>();
     Map<MAP_MOVING_LAYER_TYPE, TextureRegion>  textureMap = new HashMap<>();

    private boolean dirty;

    public MapMoveLayers(float x, float y) {
        setSize(x, y);
        for (DAY_TIME time : DAY_TIME.values()) {
            map.put(time, new HashMap<>());
        }
    }

    public static String getMainPath() {
        return
         StrPathBuilder.build("global", "map", "layers", "moving");
    }

    public void setTime(DAY_TIME time) {
        this.time = time;
        dirty = true;
    }

    public void spawn() {
        clearChildren();
        displayed = map.get(time);
    if (displayed.size()>0){
        dirty = false;
    return;
        }
        for (MAP_MOVING_LAYER_TYPE sub : MAP_MOVING_LAYER_TYPE.values()) {
            for (DAY_TIME time : sub.times) {
                if (time == this.time) {
                    spawn(sub);
                }
            }
        }
        displayed = map.get(time);
        dirty = false;
        //with 0 alpha!
    }

    private void spawn(MAP_MOVING_LAYER_TYPE sub) {
        TextureRegion texture = textureMap. get(sub);
        if (texture == null) {
            texture = TextureCache.getOrCreateR(sub.getTexturePath());
            textureMap .put(sub, texture);
        }
        for (MAP_AREA mapArea : sub.spawnAreas) {
            //area , number
            int n = 5;
            for (int i = 0; i < n; i++) {
                int x = RandomWizard.getRandomIntBetween(mapArea.x,
                 mapArea.x + mapArea.w); //can overlap!
                int y = RandomWizard.getRandomIntBetween(mapArea.y,
                 mapArea.y + mapArea.h); //cache!
                ImageContainer container = new ImageContainer(new Image(texture));
                map.get(time).put(container, sub);
                addActor(container);
                container.setPosition(x, y);
//        container.setFluctuateAlpha(true);
//        container.setFluctuatingAlpha(0);
            }
        }
    }

    @Override
    public void act(float delta) {
        if (dirty)
            spawn();
        for (ImageContainer sub : displayed.keySet()) {
            MAP_MOVING_LAYER_TYPE type = displayed.get(sub);
            float x = sub.getContent(). getX() + type.speed * delta * getModX(type.direction);
            float y = sub.getContent().getY() + type.speed * delta * getModY(type.direction);
            sub.setPosition(x, y);
            //++ shakiness
            //checkRemove or reset
        }
        super.act(delta);
    }

    private float getModX(MOVE_DIRECTION direction) {
        switch (direction) {
            case WIND:
                return 1;
            case TIDE:
                return 1;
            case SUN:
                return -1;
        }
        return 0;
    }

    private float getModY(MOVE_DIRECTION direction) {
        switch (direction) {
            case WIND:
                return 0.6f;
            case TIDE:
                return -0.2f;
            case SUN:
                return 0.1f;
        }
        return 0;
    }

    public enum MAP_AREA {
        PALE_MOUNTAINS_SOUTH(1000, 1200, 500, 500);

        int x, y, w, h;

        MAP_AREA(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    public enum MAP_MOVING_LAYER_TYPE {
        CLOUD(MOVE_DIRECTION.WIND, 50,  MAP_AREA.PALE_MOUNTAINS_SOUTH),
//        SHADOW,
//        WAVE,
//        SMOKE,
//        WATER_REFLECTION,
//        SUNSHINE,
        ;
        DAY_TIME[] times;
        int frequency;
        MAP_AREA[] spawnAreas;
        MOVE_DIRECTION direction;
        int speed;
        float shakiness;

        MAP_MOVING_LAYER_TYPE(MOVE_DIRECTION direction, int speed, MAP_AREA... areas) {
            this.direction = direction;
            this.speed = speed;
            this.spawnAreas = areas;
            times = DAY_TIME.values();
        }

        public String getTexturePath() {
            return StrPathBuilder.build(getMainPath(), name() + ".png");
        }

//AlphaFluctuation fluctuation;
    }

    public enum MOVE_DIRECTION {
        WIND, TIDE, SUN,
    }
}
