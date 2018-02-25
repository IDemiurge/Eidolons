package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.content.CONTENT_CONSTS2.MIST_SFX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPools;
import main.libgdx.screens.map.editor.EditorParticleMaster;
import main.libgdx.screens.map.sfx.MapMoveLayers.MAP_POINTS;
import main.libgdx.screens.map.sfx.MapMoveLayers.MOVE_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/17/2018.
 */
public class MapParticles extends Group {

    static {
//        MAP_EMITTER_GROUP.DARK_MIST.setPoints(
//         WISP_GROVE
//        );
//
//        MAP_EMITTER_GROUP.WHITE_MIST.setPoints(
//         GRAY_SWAMP, TAL_MERETH, BLACKWOOD
//        );
//
//        MAP_EMITTER_GROUP.CLOUDS.setPoints(
//         ASHWOOD, SEALED_KINGDOM
//        );
//
//        MAP_EMITTER_GROUP.CYAN_MIST.setPoints(SEALED_KINGDOM_WATERFALLS
//        );
//        MAP_EMITTER_GROUP.DARK_MIST.setPoints(
//         SEALED_KINGDOM, SEALED_KINGDOM_WATERFALLS, SEALED_KINGDOM_PASS,
//         WRAITH_MARSH, TAL_MERETH, WISP_GROVE
//        );
//
//        MAP_EMITTER_GROUP.WHITE_MIST.setPoints(
//         GRAY_SWAMP, BLACKWOOD, ASHWOOD, SEALED_KINGDOM,
//         WRAITH_MARSH, TAL_MERETH, WISP_GROVE
//        );
//
//        MAP_EMITTER_GROUP.CLOUDS.setPoints(
//         GRAY_SWAMP, BLACKWOOD, ASHWOOD, SEALED_KINGDOM,
//         SEALED_KINGDOM_WATERFALLS, SEALED_KINGDOM_PASS
//        );
    }

    DAY_TIME time;
    Map<DAY_TIME, List<EmitterActor>> emitterMap = new HashMap<>();
    List<EmitterActor> displayed = new ArrayList<>();
    private boolean dirty;

    public Map<DAY_TIME, List<EmitterActor>> getEmitterMap() {
        return emitterMap;
    }

    @Override
    public void act(float delta) {
        if (time != getTime()) {
            dirty = true;
            time = getTime();
        }
        if (dirty) {
            update();
        }
        super.act(delta);

    }

    private DAY_TIME getTime() {
        //macroGame.
        return DAY_TIME.NOON;
    }

    public void update() {
        if (emitterMap.isEmpty())
            init();

        displayed.forEach(emitterActor -> {
            emitterActor.remove();
//            emitterActor.getEffect().dispose();
        });

        displayed = new ArrayList<>(emitterMap.get(time));
        displayed.addAll(emitterMap.get(null)); //all-time
        displayed.forEach(emitterActor -> {
            addActor(emitterActor);
            emitterActor.start(); //when comes in view? if already in view, randomize
            //or just act(delta) on it

            if (!emitterActor.isIgnored()){
                emitterActor.start();
                emitterActor.act(RandomWizard.getRandomFloatBetween(2, 5));
            }else
            emitterActor.addAction(new Action() {
                @Override
                public boolean act(float delta) {
                    if (!emitterActor.isIgnored()){
                        emitterActor.start();
                        return true;
                    }
                    return false;
                }
            });
        });
        dirty = false;
    }

    public void init() {
        emitterMap.put(null, new ArrayList<>());
        for (DAY_TIME sub : DAY_TIME.values()) {
            emitterMap.put(sub, new ArrayList<>());
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
            if (sub.points!=null )
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

    }

    public EmitterActor create(MAP_EMITTER_GROUP sfx, int x, int y) {
        return create(sfx.sfxPath, x, y);
    }

    private EmitterActor create(String path, int x, int y, DAY_TIME time) {
        EmitterActor actor = EmitterPools.getEmitterActor(path);// new EmitterActor(sfx.sfxPath);
        actor.setPosition(x, y);
        addActor(actor);
        MapMaster.addToListMap(emitterMap, time, actor);
        return actor;
    }

    public EmitterActor create(String path, int x, int y) {
        return create(path, x, y, null);
    }

    public void load() {
        load(FileManager.readFile(EditorParticleMaster.getPath() + "all.txt"));
        for (DAY_TIME sub : DAY_TIME.values()) {
            load(FileManager.readFile(EditorParticleMaster.getPath() + sub.name() + ".txt"));
        }
    }

    public void load(String data) {
        for (String sub : StringMaster.openContainer(data)) {
            String pos = VariableManager.getVarPart(sub);
            Coordinates c = new Coordinates(true, pos);
            try {
                create((VariableManager.removeVarPart(sub)).trim(), c.x, c.y, new EnumMaster<DAY_TIME>().retrieveEnumConst(DAY_TIME.class, sub));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                main.system.auxiliary.log.LogMaster.log(1,"failed to l: " +sub);
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
