package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.libgdx.screens.map.MapScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/19/2018.
 */
public class MapMoveLayers extends Group {
    private static final int DEFAULT_AREA_SIZE = 300;

    static {
        MAP_MOVING_LAYER_TYPE.CLOUD_LARGE.times = new DAY_TIME[]{
         DAY_TIME.NOON, DAY_TIME.NIGHTFALL, DAY_TIME.MIDNIGHT, DAY_TIME.MORNING
        };
        MAP_MOVING_LAYER_TYPE.CLOUD_HEAVY.times = new DAY_TIME[]{
         DAY_TIME.NIGHTFALL, DAY_TIME.MIDNIGHT, DAY_TIME.DUSK
        };
    }

    static {
        MAP_MOVING_LAYER_TYPE.HEAVENLY_LIGHT_LARGE.spawnAreas = new MAP_AREA[]{
         MAP_AREA.PALE_MOUNTAINS_WEST, MAP_AREA.PALE_MOUNTAINS_EAST, MAP_AREA.PALE_MOUNTAINS_SOUTH,
         MAP_AREA.PALE_MOUNTAINS_NORTH, MAP_AREA.PALE_MOUNTAINS_SOUTH_SOUTH, MAP_AREA.PALE_MOUNTAINS_CENTER,
        };
        MAP_MOVING_LAYER_TYPE.HEAVENLY_LIGHT_LARGE_GOLDEN.spawnAreas = new MAP_AREA[]{
         MAP_AREA.PALE_MOUNTAINS_WEST, MAP_AREA.PALE_MOUNTAINS_EAST, MAP_AREA.PALE_MOUNTAINS_SOUTH,
         MAP_AREA.PALE_MOUNTAINS_NORTH, MAP_AREA.PALE_MOUNTAINS_SOUTH_SOUTH, MAP_AREA.PALE_MOUNTAINS_CENTER,
        };
        MAP_MOVING_LAYER_TYPE.HEAVENLY_LIGHT_LARGE_GOLDEN_SPECKLED.spawnAreas = new MAP_AREA[]{
         MAP_AREA.PALE_MOUNTAINS_WEST, MAP_AREA.PALE_MOUNTAINS_EAST, MAP_AREA.PALE_MOUNTAINS_SOUTH,
         MAP_AREA.PALE_MOUNTAINS_NORTH, MAP_AREA.PALE_MOUNTAINS_SOUTH_SOUTH, MAP_AREA.PALE_MOUNTAINS_CENTER,
        };
        MAP_MOVING_LAYER_TYPE.HEAVENLY_LIGHT_LARGE_SILVER_SPECKLED.spawnAreas = new MAP_AREA[]{
         MAP_AREA.PALE_MOUNTAINS_WEST, MAP_AREA.PALE_MOUNTAINS_EAST, MAP_AREA.PALE_MOUNTAINS_SOUTH,
         MAP_AREA.PALE_MOUNTAINS_NORTH, MAP_AREA.PALE_MOUNTAINS_SOUTH_SOUTH, MAP_AREA.PALE_MOUNTAINS_CENTER,
        };
    }

    DAY_TIME time;
    List<MapMoveLayer> displayed;
    Map<DAY_TIME, List<MapMoveLayer>> map = new HashMap<>();
    Map<MAP_MOVING_LAYER_TYPE, TextureRegion> textureMap = new HashMap<>();
    Map<MAP_MOVING_LAYER_TYPE, Float> timerMap = new HashMap<>();
    Map<MAP_MOVING_LAYER_TYPE, Float> triggerMap = new HashMap<>();
    Map<MapMoveLayer, Float> durationMap = new HashMap<>();
    private boolean dirty;

    public MapMoveLayers(float x, float y) {
        setSize(x, y);
        for (DAY_TIME time : DAY_TIME.values()) {
            map.put(time, new ArrayList<>());
        }
    }

    public static String getMainPath() {
        return
         StrPathBuilder.build("global", "map", "layers", "moving");
    }

    public void setTime(DAY_TIME time) {
        if (this.time == time)
            return;
        this.time = time;
        dirty = true;
    }

    public void spawn() {
//        clearChildren();
//        displayed = map.get(time);
//        if (displayed.size() > 0) {
//            dirty = false;
//            return;
//        }
        for (MAP_MOVING_LAYER_TYPE sub : MAP_MOVING_LAYER_TYPE.values()) {
            MapMaster.addToFloatMap(timerMap, sub, 0f);
            MapMaster.addToFloatMap(triggerMap, sub, 0f);
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
        TextureRegion texture = textureMap.get(sub);
        if (texture == null) {
            texture = TextureCache.getOrCreateR(sub.getTexturePath());
            textureMap.put(sub, texture);
        }
        MAP_AREA mapArea = null;
        if (sub.spawnAreas == null) {
            //mapArea = MAP_AREA.values()[RandomWizard.getRandomInt(MAP_AREA.values().length)];
        } else mapArea =
         sub.spawnAreas[RandomWizard.getRandomInt(sub.spawnAreas.length)];
        //area , number
        MapMoveLayer container = new MapMoveLayer(new Image(texture), mapArea, sub) {
            @Override
            protected float getAlphaFluctuationMin() {
                return 0f;
            }
        }; //cache!

        container.setDirectionModX(getModX(sub.direction)
         + getModX(sub.direction) * (RandomWizard.getRandomFloatBetween(-sub.randomness, sub.randomness))
        );
        container.setDirectionModY(getModY(sub.direction) + getModY(sub.direction)
         * (RandomWizard.getRandomFloatBetween(-sub.randomness, sub.randomness))
        );
        float scale = 1 + RandomWizard.getRandomFloatBetween(-sub.sizeRange, sub.sizeRange);
        container.setScale(scale);

        if (sub.rotation != 0) {
            container.setRotation(RandomWizard.getRandomInt(360));
        }

        container.setMaxDistance(getMaxDistance(mapArea, sub));
        container.setShakiness(sub.shakiness);
        container.setSpeed(sub.speed);
        if ((sub.alphaStep > 0)) {
            container.setFluctuateAlpha(true);
            container.setFluctuatingAlphaRandomness(0.2f);
            container.setFluctuatingFullAlphaDuration(2.5f);

            container.setFluctuatingAlphaRandomness(sub.alphaRandomness);
            container.setFluctuatingAlphaPauseDuration(sub.pauseDuration);
            container.setFluctuatingFullAlphaDuration(sub.fullAlphaDuration);
            container.setAlphaStep(sub.alphaStep);
        }
        durationMap.put(container, 0f);
        for (DAY_TIME day_time : sub.times) {
            map.get(day_time).add(container);
        }
        spawn(container, mapArea);
//alpha should reduce as it goes away
//        container.setFluctuateAlpha(true);
//        container.setFluctuatingAlpha(0);
    }

    private float getMaxDistance(MAP_AREA mapArea, MAP_MOVING_LAYER_TYPE sub) {
        return 2110;
    }

    @Override
    public void act(float delta) {
        if (MacroGame.getGame() == null)
            return;

        setTime(MacroGame.getGame().getTime());
        if (dirty)
            spawn();
        for (MAP_MOVING_LAYER_TYPE sub : MAP_MOVING_LAYER_TYPE.values()) {
            MapMaster.addToFloatMap(timerMap, sub, delta);
            if (triggerMap.get(sub) == null || timerMap.get(sub) > triggerMap.get(sub)) {
                spawn(sub);
                float willSpawnOn = RandomWizard.getRandomFloatBetween(sub.frequency, 2 * sub.frequency);
                triggerMap.put(sub, willSpawnOn);
                timerMap.put(sub, 0f);
            }
        }
        for (MapMoveLayer sub : displayed) {
            MAP_MOVING_LAYER_TYPE type = sub.getType();
//            if (Math.abs(sub.getSpeed() )> 0) {
//            }
            float x = sub.getContent().getX() + sub.getSpeed() * delta * getModX(type.direction);
            float y = sub.getContent().getY() + sub.getSpeed() * delta * getModY(type.direction);
            sub.getContent().setPosition(x, y);
            float distance = new Vector2(sub.getContent().getX(), sub.getContent().getY()).dst(new Vector2(sub.getOriginalX(), sub.getOriginalY()));
            float maxDistance = sub.getMaxDistance();
            if (x > getWidth() || x < -sub.getWidth()
             && (y > getHeight() + sub.getHeight() || y < 0))
                 remove(sub);
                //center to edge? direction? better check sumis <> maxWidth or 0
            else if (distance > maxDistance) {
                 remove(sub);
            } else if (type.maxDuration != null)
                if (durationMap.get(sub) > type.maxDuration) {
                    remove(sub);
                } else
                    MapMaster.addToFloatMap(durationMap, sub, delta);

            if (type.rotation != 0) {
                sub.setRotation(sub.getRotation() + type.rotation);
            }
            //++ shakiness
            //checkRemove or reset
        }
        super.act(delta);
    }

    private void remove(MapMoveLayer sub) {
        sub.remove();
        durationMap.remove(sub);
        displayed.remove(sub);
    }

    private void spawn(MapMoveLayer container, String mapArea) {
        Coordinates c= MacroGame.getGame().getPointMaster().getCoordinates(mapArea);
        spawn(container, c.x, c.y, DEFAULT_AREA_SIZE, DEFAULT_AREA_SIZE);
    }
    private void spawn(MapMoveLayer container, MAP_AREA mapArea) {
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        if (mapArea == null) {
            x = 0;
            y = 0;
            w = MapScreen.defaultSize;
            h = MapScreen.defaultSize;
        } else {
            x =   mapArea.x   ;
            y =   mapArea.y ;
            w =   mapArea.w ;
            h  =   mapArea.h ;
            container.setSpawnArea(mapArea);
        }
        spawn(container, x, y, w, h);
    }
        private void spawn(MapMoveLayer container, int x, int y, int w, int h) {

            x = RandomWizard.getRandomIntBetween(x,
            x +w); //can overlap!}
            y = RandomWizard.getRandomIntBetween(y,
            y +h);

        addActor(container);
        container.setOriginalX(x);
        container.setOriginalY(y);
        container.getContent().setPosition(x, y);
        container.setFluctuatingAlpha(0);
        //alpha to 0
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
        NEUGARD_SOUTH(1000, 1200, 500, 500),
        NEUGARD_NORTH(600, 800, 500, 500),
        PALE_MOUNTAINS_NORTH(2250, 2050, 300, 300),
        PALE_MOUNTAINS_CENTER(2150, 1665, 300, 300),
        PALE_MOUNTAINS_WEST(1680, 1965, 300, 300),
        PALE_MOUNTAINS_EAST(2550, 1965, 300, 300),
        PALE_MOUNTAINS_SOUTH(2150, 1465, 300, 300),
        PALE_MOUNTAINS_SOUTH_SOUTH(2050, 1165, 300, 300),

        ASHWOOD(MAP_POINTS.ASHWOOD.name(), 500, 500),
        WISP_GROVE(MAP_POINTS.WISP_GROVE.name(), 500, 500),
        WRAITH_MARSH(MAP_POINTS.WRAITH_MARSH.name(), 500, 500),
        BOTTOM_LEFT(0, 0, 300, 300);

        int x, y, w, h;
        String centerPoint;

        MAP_AREA(String centerPoint, int w, int h) {
            this.centerPoint = centerPoint;
            this.w = w;
            this.h = h;
        }

        MAP_AREA(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    public enum MAP_MOVING_LAYER_TYPE {
        CLOUD(MOVE_DIRECTION.WIND, 50, 3, MAP_AREA.WRAITH_MARSH, MAP_AREA.NEUGARD_SOUTH,
         MAP_AREA.NEUGARD_NORTH),
        CLOUD_HEAVY(MOVE_DIRECTION.WIND, 30, 2, MAP_AREA.WISP_GROVE, MAP_AREA.NEUGARD_SOUTH,
         MAP_AREA.NEUGARD_NORTH),
        CLOUD_LARGE(MOVE_DIRECTION.WIND, 50, 3, 0.5f, 1f, 0.3f, MAP_AREA.BOTTOM_LEFT),
        //        SHADOW,
//        WAVE,
//        SMOKE,
//        WATER_REFLECTION,
//        SUNSHINE,
        HEAVENLY_LIGHT_LARGE(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),
        HEAVENLY_LIGHT_LARGE_GOLDEN(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),

        HEAVENLY_LIGHT_LARGE_GOLDEN_SPECKLED(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),
        HEAVENLY_LIGHT_LARGE_SILVER_SPECKLED(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),

        LIGHT_SPREAD_SILVER(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),
        LIGHT_SPREAD_GOLDEN(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),
        LIGHT_SPREAD(MOVE_DIRECTION.SUN, 0, 6, 0, 0, 0.2f, 0,
         0.2f, 1f, 2f, 10f, 0.4f, DAY_TIME.NOON),;
        public float alphaStep = 0.5f;
        public float fullAlphaDuration;
        public float pauseDuration;
        public float alphaRandomness;
        public Float maxDuration;
        DAY_TIME[] times;
        float frequency;
        MAP_AREA[] spawnAreas;
        MOVE_DIRECTION direction;
        int speed;
        float shakiness;
        float randomness;
        float rotation; //degrees per second
        float sizeRange;

        MAP_MOVING_LAYER_TYPE(MOVE_DIRECTION direction, int speed, float frequency, MAP_AREA... areas) {
            this(direction, speed,
             frequency, 0, 0, 0, areas);
        }


        MAP_MOVING_LAYER_TYPE(MOVE_DIRECTION direction,
                              int speed, float frequency, float randomness,
                              float rotation, float sizeRange, float shakiness,
                              float alphaStep, float fullAlphaDuration, float pauseDuration,
                              float maxDuration, float alphaRandomness, DAY_TIME... times) {
            this.frequency = frequency;
            this.times = times;
            this.direction = direction;
            this.maxDuration = maxDuration;
            this.speed = speed;
            this.shakiness = shakiness;
            this.randomness = randomness;
            this.rotation = rotation;
            this.sizeRange = sizeRange;
            this.alphaStep = alphaStep;
            this.fullAlphaDuration = fullAlphaDuration;
            this.pauseDuration = pauseDuration;
            this.alphaRandomness = alphaRandomness;
        }

        MAP_MOVING_LAYER_TYPE(MOVE_DIRECTION direction, int speed,
                              float frequency,
                              float randomness, float rotation, float sizeRange,
                              MAP_AREA... areas) {
            this.direction = direction;
            this.speed = speed;
            this.spawnAreas = areas;
            this.frequency = frequency;
            this.randomness = randomness;
            this.rotation = rotation;
            this.sizeRange = sizeRange;
            times = DAY_TIME.values();
        }

        public String getTexturePath() {
            return StrPathBuilder.build(getMainPath(), StringMaster.getWellFormattedString(name()) + ".png");
        }

//AlphaFluctuation fluctuation;
    }

    public enum MAP_POINTS {
        //HINTERLANDS
        MONASTERY(1754, 1874),
        MONASTERY_CLOISTER(1804, 1894),
        GREENTORCH(1350, 1568),
        ELF_CEMETERY(1564, 1554),
        ELF_BARROWS(1587, 1530),
        NAUROG(1298, 1435),
        WAR_CEMETERY(1228, 1622),
        WRAITH_MARSH(1228, 1622),
        ASHWOOD(1396, 1724),
        SEALED_KINGDOM(1448, 2124),
        SEALED_KINGDOM_WATERFALLS(1543, 2068),
        SEALED_KINGDOM_PASS(1228, 1622),
        WISP_GROVE(1098, 1752),
        TAL_MERETH(1474, 1484),
        STONESHIELD_HALL(1704, 1452),
        RIVEREND(868, 1684),
        WARDEN_SHRINE(976, 1412),
        BLACKWOOD(1234, 1416),
        GRAY_SWAMP(1280, 1892),;

        public int x, y;

        MAP_POINTS(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public enum MOVE_DIRECTION {
        WIND, TIDE, SUN,
    }
}
