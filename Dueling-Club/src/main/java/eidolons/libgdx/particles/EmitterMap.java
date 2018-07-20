package eidolons.libgdx.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.DungeonScreen;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap extends Group {

    private static final int MIN_DISTANCE_FROM_LIGHT = 1;
    private static final boolean HIDE_SMOKE_AROUND_MAIN_HERO = true;
    private static float MIN_FOG_DISTANCE_FROM_ALLY = 3;
    private static float MIN_DISTANCE_BETWEEN_FOG = 2;
    private static int xDistanceFog = 3;
    private static int yDistanceFog = 2;

    private static Boolean on;
    Map<Coordinates, Ambience> map = new LinkedHashMap<>();
    String presetPath;
    private final Pool<Ambience> ambiencePool = new Pool<Ambience>() {
        @Override
        protected Ambience newObject() {
            return new Ambience(presetPath);
        }
    };
    private int showChance;
    private Color color;
    private boolean hideAroundPC = HIDE_SMOKE_AROUND_MAIN_HERO;
    private float minDistance = MIN_DISTANCE_BETWEEN_FOG;
    private float distanceX = xDistanceFog;
    private float distanceY = yDistanceFog;
    private float timer;

    public EmitterMap(String presetPath, int showChance, Color colorHue) {
        this.presetPath = presetPath;
        this.showChance = showChance;
        if (colorHue != null)
            color = colorHue;
        else if (Eidolons.game instanceof DC_Game) {
            COLOR_THEME colorTheme = Eidolons.game.getDungeon().getColorTheme();
            if (colorTheme != null)
                color = GdxColorMaster.getColorForTheme(colorTheme);
        }
    }

    public static Boolean getOn() {
        return on;
    }

    public static void setOn(Boolean on) {
        EmitterMap.on = on;
    }


//    public boolean contains(ParticleInterface actor) {
//        return emitters.contains(actor);
//    }

    public void init() {
        for (int x = 0; x + getDistanceX() <=
         DungeonScreen.getInstance().getGridPanel().getRows(); x += getDistanceX())
            for (int y = 0; y + getDistanceY() <=
             DungeonScreen.getInstance().getGridPanel().getCols(); y += getDistanceY()) {

                add(new Coordinates(x, y));

            }

    }

    public void update() {
        if (!isAmbienceOn()) {
            return;
        }
        if (DC_Game.game.getPlayer(true).getHeroObj() == null) {
            return;
        }
        if (map.isEmpty())
            init();
        for (Coordinates c1 : map.keySet()) {
            if (!RandomWizard.chance(showChance)) {
                hide(c1);
                continue;
            }
            Coordinates mainHeroCoordinates =
             Eidolons.getMainHero().getCoordinates();
            if (isHideAroundPC() &&
             PositionMaster.getDistance(c1, mainHeroCoordinates)
              < getMinDistance()) {
                hide(c1);
            } else
                show(c1);
        }
    }

    private void remove(Coordinates c) {
        Ambience ambience = map.remove(c);
        if (ambience == null) {
            return;
        }
        ambience.remove();
        ambience.setVisible(false);
        ambiencePool.free(ambience);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer += delta;
        if (timer >= getUpdatePeriod()) {
            timer = 0;
            update();
        }
    }

    private float getUpdatePeriod() {
        return 10;
    }

    private void show(Coordinates c) {
        Ambience ambience = map.get(c);
        if (ambience == null) {
            return;
        }
        addActor(ambience);
        if (ambience.getEffect().isComplete()) {
            ambience.reset();
        }
    }

    private void hide(Coordinates c) {
        Ambience ambience = map.get(c);
        if (ambience == null) {
            return;
        }
        ambience.clearActions();
        ambience.hide();
    }

    private void add(Coordinates c) {
        if (map.containsKey(c)) {
            return;
        }

        Vector2 v = GridMaster.
         getCenteredPos(c);
        Ambience ambience = ambiencePool.obtain();
        if (color != null)
            ambience.setColor(color);
        ambience.setTarget(c);
        map.put(c, ambience);
        ambience.setPosition(v.x, v.y);
        ambience.added();

        if (color!=null )
        ambience.getEffect().getEmitters().forEach(emitter -> {
            try {
                tint(emitter, color, 0.85f);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        if (!getChildren().contains(ambience, true))
            addActor(ambience);
        //DungeonScreen.getInstance().getAmbienceStage().addActor(fog);
        ambience.setVisible(true);
        ambience.getEffect().start();
    }

    private void tint(ParticleEmitter emitter, Color color, float v) {
        float[] colors1 = emitter.getTint().getColors();
        float[] colors = new float[colors1.length];
        for (int j = 0; j < colors.length; j += 3) {
            colors[j] =
             MathUtils.lerp(colors1[j],
              color.r, v);
            colors[j + 1] =
             MathUtils.lerp(colors1[j + 1],
              color.g, v);
            colors[j + 2] =
             MathUtils.lerp(colors1[j + 2],
              color.b, v);
        }

        emitter.getTint().setColors(colors);
    }


    public boolean isAmbienceOn() {
        return ParticleManager.isAmbienceOn();
    }

    public boolean isHideAroundPC() {
        return hideAroundPC;
    }

    public float getDistanceX() {
        return distanceX;
    }

    public float getDistanceY() {
        return distanceY;
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setShowChance(int showChance) {
        this.showChance = showChance;
    }
}
