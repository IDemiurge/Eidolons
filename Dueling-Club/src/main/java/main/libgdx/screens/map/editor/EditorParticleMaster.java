package main.libgdx.screens.map.editor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.screens.map.sfx.MapParticles;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

/**
 * Created by JustMe on 2/20/2018.
 */
public class EditorParticleMaster extends Group {
    MapParticles particles;
    Map<DAY_TIME, Group> layers = new HashMap<>();
    Map<DAY_TIME, List<EmitterActor>> map = new HashMap<>();
    DAY_TIME time;
    private boolean dirty;
    private Stack<EmitterActor> stack = new Stack();

    public EditorParticleMaster(MapParticles particles) {
        this.particles = particles;

        for (DAY_TIME sub : DAY_TIME.values()) {
            layers.put(sub, new Group());
            map.put(sub, new ArrayList<>());
        }
        map.put(null, new ArrayList<>());
        layers.put(null, new Group()); //always
        debugAll();
    }

    public static String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroImgPath(), "emitters", "main") + StringMaster.getPathSeparator();
    }

    public void setTime(DAY_TIME time) {
        this.time = time;
        dirty = true;
    }

    @Override
    public void act(float delta) {
        if (MacroGame.getGame().getTime() != this.time) {
            setTime(MacroGame.getGame().getTime());
        }
        if (dirty) {
            clearChildren();
            addActor(layers.get(time));
            dirty = false;
        }
        //speed for emitters
        super.act(delta);
    }

    public void removeClosest(int x, int y) {
        float minDistance = Float.MAX_VALUE;
        Actor actor = null;
        DequeImpl<Actor> list = new DequeImpl<>(particles.getEmitterMap().get(time));
        list.addAll(map.get(time));
        for (Actor sub : list) {
            if (sub instanceof EmitterActor) {
                float distance = new Vector2(x, y).dst(new Vector2(sub.getX(), sub.getY()));
                if (distance < minDistance) {
                    minDistance = distance;
                    actor = sub;
                }
            }
            //can we not attach click listeners to emtiterActors?!
        }
        removeEmitter((EmitterActor) actor, time);
    }

    public void removeLast() {
        if (stack.isEmpty())
            return;
        EmitterActor last = stack.pop();
        removeEmitter(last, time);
    }

    private void removeEmitter(EmitterActor actor, DAY_TIME time) {
        map.get(time).remove(actor);
        particles.getEmitterMap().get(time).remove(actor);
        actor.remove();
        GuiEventManager.trigger(MapEvent.EMITTER_REMOVED, actor);
    }

    public void clicked(int x, int y) {
        String path = EditorMapView.getInstance().getGuiStage().getEmitterPalette().getSelectedEmitterPath();
        EmitterActor last = particles.create(path, x, y);
        last.start();
        stack.push(last);
        final DAY_TIME time = this.time;
        last.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    removeEmitter(last, time);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        MapMaster.addToListMap(map, time, last);
        //layer
        last.setPosition(x, y); //centering? emitters probably self-center...
        layers.get(time).addActor(last);
        GuiEventManager.trigger(MapEvent.EMITTER_CREATED, last);
    }

    public void saveAll() {
        for (DAY_TIME sub : DAY_TIME.values()) {
            save(sub);
        }
        save(null);
    }

    public void save(DAY_TIME time) {
        List<EmitterActor> emitterActors = new ArrayList<>(map.get(time));
        try {
            emitterActors.addAll(particles.getEmitterMap().get(time));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        String contents = "";
        for (EmitterActor sub : emitterActors) {
            String s = sub.getPath() + StringMaster.wrapInParenthesis(
             new Coordinates(true, (int) sub.getX(), (int) sub.getY()).toString()) + ";\n";
            contents += s;
        }
        String s;
        if (time == null)
            s = "all";
        else s = time.toString();
        String path = getPath() + s + ".txt";
        FileManager.write(contents, path);
    }
}
