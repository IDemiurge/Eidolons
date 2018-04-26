package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.graphics.Color;
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
    private final Pool<Ambience> ambiencePool = new Pool<Ambience>() {
        @Override
        protected Ambience newObject() {
            return new Ambience(presetPath);
        }
    };
    private   AmbienceDataSource dataSource;
    Map<Coordinates, Ambience> map = new LinkedHashMap<>();
    private Color color;
    private boolean hideAroundPC=HIDE_SMOKE_AROUND_MAIN_HERO;
    private float minDistance=MIN_DISTANCE_BETWEEN_FOG;
    private float distanceX=xDistanceFog;
    private float distanceY=yDistanceFog;
    String presetPath;
    private float timer;

    public EmitterMap(String presetPath, AmbienceDataSource dataSource) {
        this.presetPath = presetPath;
        this.dataSource = dataSource;
        if (Eidolons.game instanceof DC_Game) {
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
            if (!RandomWizard.chance(dataSource.getShowChance())){
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

    private void remove (Coordinates c) {
        Ambience fog = map.remove(c);
        if (fog == null) {
            return;
        }
        fog.remove();
        fog.setVisible(false);
        ambiencePool.free(fog);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer+=delta;
        if (timer>=getUpdatePeriod()){
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
        ambience.reset();
//        ActorMaster.addFadeInAction(ambience, 0.5f );
    }
        private void hide(Coordinates c) {
        Ambience ambience = map.get(c);
        if (ambience == null) {
            return;
        }
//        ambience.remove();
//        ambience.setVisible(false);
        ambience.clearActions();
//        ActorMaster.addFadeOutAction(ambience, 0.5f, true);
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
        if (!getChildren().contains(ambience, true))
            addActor(ambience);
        //DungeonScreen.getInstance().getAmbienceStage().addActor(fog);
        ambience.setVisible(true);
        ambience.getEffect().start();
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
}
