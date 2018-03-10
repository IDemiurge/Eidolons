package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import main.content.CONTENT_CONSTS2.MIST_SFX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPools;
import main.libgdx.screens.map.editor.EditorParticleMaster;
import main.libgdx.screens.map.sfx.MapMoveLayers.MAP_POINTS;
import main.libgdx.screens.map.sfx.MapMoveLayers.MOVE_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/17/2018.
 */
public class MapParticles extends MapTimedLayer<EmitterActor> {

    public Map<DAY_TIME, List<EmitterActor>> getEmitterMap() {
        return map;
    }

    @Override
    protected void clearLayer() {
        displayed.forEach(emitterActor -> {
        emitterActor.remove();
//      TODO       emitterActor.getEffect().dispose();
    });
    }

    @Override
    protected void spawnLayer() {
          displayed = new ArrayList<>(map.get(time));
        displayed.addAll(map.get(null)); //all-time
        displayed.forEach(emitterActor -> {
            addActor(emitterActor);
            emitterActor.start(); //when comes in view? if already in view, randomize
            //or just act(delta) on it

            if (!emitterActor.isIgnored()) {
                emitterActor.start();
                emitterActor.act(RandomWizard.getRandomFloatBetween(2, 5));
            } else
                emitterActor.addAction(new Action() {
                    @Override
                    public boolean act(float delta) {
                        if (!emitterActor.isIgnored()) {
                            emitterActor.start();
                            return true;
                        }
                        return false;
                    }
                });
        });
    }

    public void init() {
        map.put(null, new ArrayList<>());
        for (DAY_TIME sub : DAY_TIME.values()) {
            map.put(sub, new ArrayList<>());
        }
        load();
        for (MAP_EMITTER_GROUP sub : MAP_EMITTER_GROUP.values()) {
            boolean displayed = false;
            for (DAY_TIME time : sub.times) {
                if (time == this.time) {
                    displayed = true;
                    break;
                } //same check for weather perhaps
            }
            if (sub.points != null)
                for (MAP_POINTS point : sub.points) {
                    for (int i = 0; i < sub.number; i++) {
                        int mod = (i % 2 == 0) ? 1 : -1;
                        int offsetX = mod * sub.distance * i;
                        mod = (i % 3 == 0) ? 1 : -1;
                        int offsetY = mod * sub.distance * i;
                        EmitterActor emitter =

                         create(sub.sfxPath, point.x + offsetX, point.y + offsetY, time);
                        if (displayed) {
                            this.displayed.add(emitter);
                        }

                    }

                }
        }
        initialized = true;
    }

    public EmitterActor create(MAP_EMITTER_GROUP sfx, int x, int y) {
        return create(sfx.sfxPath, x, y);
    }

    private EmitterActor create(String path, int x, int y, DAY_TIME time) {
        EmitterActor actor = EmitterPools.getEmitterActor(path);// new EmitterActor(sfx.sfxPath);
        actor.setPosition(x, y);
        actor.setSpeed(getSpeed(path));
        MapMaster.addToListMap(map, time, actor);
        return actor;
    }

    private float getSpeed(String path) {
        List<String> paths = StringMaster.getPathSegments(path);
        if (paths.size() > 1) {
            switch (paths.get(1)) {
                case "leaves": return 0.3f;
            }

        }
        switch (paths.get(0)) {
            case "smokes":
                return 0.5f;
        }
        return 1;
    }

    public EmitterActor create(String path, int x, int y) {
        return create(path, x, y, null);
    }

    public void load() {
        load(FileManager.readFile(EditorParticleMaster.getPath() + "all.txt"), null);
        for (DAY_TIME sub : DAY_TIME.values()) {
            load(FileManager.readFile(EditorParticleMaster.getPath() + sub.name() + ".txt"), sub);
        }
    }

    public void load(String data, DAY_TIME time) {
        for (String sub : StringMaster.openContainer(data)) {
            String pos = VariableManager.getVarPart(sub);
            Coordinates c = new Coordinates(true, pos);
            try {
                create((VariableManager.removeVarPart(sub)).trim(), c.x, c.y, time);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                main.system.auxiliary.log.LogMaster.log(1, "failed to l: " + sub);
            }
        }
    }

    public enum MAP_EMITTER_GROUP {
        DARK_MIST(MIST_SFX.DARK_MIST.getPath(), 70, 1),
        CLOUDS(MIST_SFX.CLOUDS.getPath(), 70, 1),
        WHITE_MIST(MIST_SFX.WHITE_MIST.getPath(), 70, 1),
        CYAN_MIST(MIST_SFX.CYAN_MIST.getPath(), 70, 1),;

        String sfxPath;
        DAY_TIME[] times;
        float[] intensity;
        Color[] colors;
        MAP_POINTS[] points;
        int distance;
        int number;
        float speed;
        MOVE_DIRECTION direction;

        MAP_EMITTER_GROUP(String sfxPath, int distance, int number) {
            this.sfxPath = sfxPath;
            this.distance = distance;
            this.number = number;
            times = DAY_TIME.values();
        }

        public void setTimes(DAY_TIME... times) {
            this.times = times;
        }

        public void setIntensity(float... intensity) {
            this.intensity = intensity;
        }

        public void setColors(Color... colors) {
            this.colors = colors;
        }

        public void setPoints(MAP_POINTS... points) {
            this.points = points;
        }
    }

}
