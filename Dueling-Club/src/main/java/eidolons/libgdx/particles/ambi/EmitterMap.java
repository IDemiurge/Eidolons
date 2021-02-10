package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap extends GroupX {

    private static final boolean HIDE_SMOKE_AROUND_MAIN_HERO = true;
    private static final float MIN_DISTANCE_BETWEEN_FOG = 2;

    private static Boolean on;
    private static Integer globalShowChanceCoef;
    Map<Coordinates, EmitterActor> map = new LinkedHashMap<>();
    String presetPath;
    private int showChance;
    private Color color;
    private boolean hidden;
    private int activeCount;

    Collection<Coordinates> coordinates;

    public EmitterMap(String presetPath, Collection<Coordinates> coordinates, int showChance, Color colorHue) {
        this.coordinates = coordinates;
        this.presetPath = presetPath;
        this.presetPath = presetPath;
        this.showChance = showChance;
        if (colorHue != null)
            color = colorHue;
        else if (Eidolons.game instanceof DC_Game) {
            CONTENT_CONSTS.COLOR_THEME colorTheme = Eidolons.game.getDungeon().getColorTheme();
            if (colorTheme != null)
                color = GdxColorMaster.getColorForTheme(colorTheme);
        }
    }

    public void init() {
        //TODO reinit on timer?
        for (Coordinates c : map.keySet()) {
            hide(c);
        }
        for (Coordinates coordinate : coordinates) {
            if (RandomWizard.chance(showChance * getGlobalShowChanceCoef() / 100)) {
                tryAdd(coordinate);
            }
        }
        checkRemove();
    }

    public void update(Coordinates mainHeroCoordinates) {
        if (hidden)
            return;
        //update for surrounding area
        for (Coordinates c1 : map.keySet()) {
            if (isHideAroundPC() &&
                    PositionMaster.getExactDistanceNoCache(c1, mainHeroCoordinates)
                            < getMinDistance()) {
                hide(c1);
            }
        }
    }

    public void setShowChance(int showChance) {
        this.showChance = showChance;
        init();
    }

    public void setHue(Color colorHue) {
        this.color = colorHue;
    }

    public void setBaseAlpha(float baseAlpha) {
        // this.baseAlpha = baseAlpha;
        map.values().forEach(ambience -> ambience.offsetAlpha(baseAlpha));
    }

    @Override
    public void act(float delta) {
        // super.act(delta);
        // all emitters act on draw()
    }

    public void checkRemove() {
        // super.act(delta);
        // for (Ambience ambience : map.values()) {
        //     ambience.setVisible(true);
        // }
        SnapshotArray<Actor> children = getChildren();
        Actor[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            EmitterActor actor = (EmitterActor) actors[i];
            if (!actor.isVisible())
            if (actor.isComplete() ) {
                removeActor(actor);
            }
        }
        children.end();
    }

    private void show(Coordinates c) {
        EmitterActor ambience = map.get(c);
        if (ambience == null) {
            tryAdd(c);
        } else
            addActor(ambience);
        ambience.setVisible(true);
        if (ambience.getEffect().isComplete()) {
            ambience.reset();
        }
    }

    private void hide(Coordinates c) {
        EmitterActor ambience = map.get(c);
        if (ambience == null) {
            return;
        }
        EmitterPools.freeActor(ambience);
        // if (ambience.isIgnored()) {
        //     return;
        // }
        ambience.clearActions();
        ambience.hide();
        activeCount--;
        activeCount = Math.max(0, activeCount);
    }


    private void tryAdd(Coordinates c) {
        if (map.containsKey(c)) {
            return;
        }

        Vector2 v = GridMaster.
                getCenteredPos(c);
        EmitterActor ambience = EmitterPools.getEmitterActor(presetPath);
        if (color != null)
            ambience.setColor(color);
        ambience.setTarget(c);
        map.put(c, ambience);
        int maxOffset = getMaxOffset();
        int offsetX = RandomWizard.getRandomIntBetween(-maxOffset, maxOffset);
        int offsetY = RandomWizard.getRandomIntBetween(-maxOffset, maxOffset);
        v.add(offsetX, offsetY);
        ambience.setPosition(v.x, v.y);
        // ambience.added();

        if (RandomWizard.random())
            if (color != null)
                ambience.getEffect().getEmitters().forEach(emitter -> {
                    try {
                        tint(emitter, color, 0.85f);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

                });
        if (!getChildren().contains(ambience, true)) {
            addActor(ambience);
        }
        // ambience.setVisible(false);
    }


    public void hide() {
        hidden = true;
        map.values().forEach(EmitterActor::hide);
    }

    public void show() {
        hidden = false;
        map.values().forEach(ambience -> {
            addActor(ambience);
            if (ambience.getEffect().isComplete()) {
                ambience.reset();
            }
        });
        activeCount++;
        activeCount = Math.min(map.keySet().size(), activeCount);
    }

    public static Boolean getOn() {
        return on;
    }

    public static void setOn(Boolean on) {
        EmitterMap.on = on;
    }

    private int getMaxOffset() {
        return 42;
    }

    public int getActiveCount() {
        return activeCount;
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

    public boolean isHideAroundPC() {
        return HIDE_SMOKE_AROUND_MAIN_HERO;
    }


    public float getMinDistance() {
        return MIN_DISTANCE_BETWEEN_FOG;
    }

    public static Integer getGlobalShowChanceCoef() {
        if (CoreEngine.TEST_LAUNCH) {
            return 100;
        }
        if (globalShowChanceCoef == null) {
            globalShowChanceCoef = OptionsMaster.getGraphicsOptions().getIntValue(
                    GRAPHIC_OPTION.AMBIENCE_DENSITY);
        }
        return globalShowChanceCoef * DungeonScreen.getInstance().getParticleManager().getEmitterCountControlCoef() / 100;
    }

    public static void setGlobalShowChanceCoef(Integer globalShowChanceCoef) {
        EmitterMap.globalShowChanceCoef = globalShowChanceCoef;
    }


    public String getVfxPath() {
        return presetPath;
    }

    public Collection<Coordinates> getCoordinates() {
        return coordinates;
    }
}
