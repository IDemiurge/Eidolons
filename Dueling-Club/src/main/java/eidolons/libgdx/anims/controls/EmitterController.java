package eidolons.libgdx.anims.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.ambi.Ambience;
import eidolons.libgdx.particles.spell.SpellVfx;
import eidolons.libgdx.particles.spell.SpellVfxPool;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import eidolons.libgdx.particles.util.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.controls.Controller;
import main.data.filesys.PathFinder;
import main.entity.obj.ActiveObj;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.DialogMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/27/2017.
 */
public class EmitterController implements Controller {
    public static boolean overrideKeys = false;
    static EmitterController instance;
    private static boolean testMode;
    EmitterActor last;
    boolean continuous;
    int multiplier;
    boolean cursorAttached;
    private LinkedList<EmitterActor> sfx;
    private VFX_MODIFICATION_PRESET lastPreset;
    private String randomSfxPath = "modified/work";

    public EmitterController() {
        sfx = new LinkedList<>();

        GuiEventManager.bind(GuiEventType.CREATE_EMITTER, p -> {
            create();
            if (p != null) {
                addRandom();
            }
        });
        GuiEventManager.bind(GuiEventType.VFX_PLAY_LAST, p -> replay());
    }

    public static EmitterController getInstance() {
        if (instance == null) {
            instance = new EmitterController();
        }
        return instance;
    }

    public void save() {
        EmitterPresetMaster.save(last, "target");
    }

    private void removeLast() {
        last.remove();
        if (!sfx.isEmpty()) {
            last = sfx.pollLast();
        }
        if (!sfx.isEmpty()) {
            last = sfx.peekLast();
        }
    }

    private void saveAs() {
        EmitterPresetMaster.save(last, "target", DialogMaster.inputText("name?"));
    }

    public void create() {
        String presetPath =
         new FileChooser(PathFinder.getVfxPath()).launch("", "");
        if (presetPath == null) {
            return;
        }
        ImageChooser ic = new ImageChooser();
        ic.setDefaultFileLocation(PathFinder.getVfxPath() + "images//");
        String imagePath = ic
         .launch("", "");
        add(presetPath, imagePath);
    }

    public void addRandom() {
        String presetPath =
         FileManager.getRandomFilePath(PathFinder.getVfxPath()
          + randomSfxPath
         );
        add(presetPath, null);

//        add(presetPath,imagePath,destination);
    }


    public void replay() {
        if (last == null) {
            return;
        }
        last.reset();
        add(last, null);

    }

    public void add(String presetPath, String imagePath) {
        EmitterActor actor = null;
        try {
            actor =
             new EmitterActor(presetPath, true) {
                 @Override
                 public void draw(Batch batch, float parentAlpha) {
                     act(Gdx.graphics.getDeltaTime());
//                    Vector2 v = GridMaster.getMouseCoordinates();
//                     setPosition(v.x, v.y);
                     super.draw(batch, parentAlpha);
                 }

             };
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return;
        }

        if (imagePath != null) {
            actor.getEffect().setImagePath(imagePath);
        }
        add(actor, null);
    }

    public void add(EmitterActor actor, Vector2 v) {
        DungeonScreen.getInstance().getGridStage().addActor(actor);
        if (v == null) {
            v = GridMaster.getMouseCoordinates();
        }
        actor.setPosition(v.x, v.y);

//        int speed = 500;
//        ActorMaster.getMoveToAction(destination, actor, speed);
//        ActorMaster.addRemoveAfter(actor);
        actor.getEffect().start();
        last = actor;
        sfx.add(last);
        last.getEffect().getEmitters().forEach(e -> e.setContinuous(true));
    }

    public void clear() {
        sfx.forEach(fx -> {
            fx.remove();
            fx.getEffect().dispose();
        });
        sfx.clear();
    }


    public void modify() {
        boolean random = false;
        boolean presetmod = DialogMaster.confirm("preset?");
        if (presetmod) {
//            random = DialogMaster.confirm("random?");
//            lastPreset = random ?
//             new EnumMaster<VFX_MODIFICATION_PRESET>().
//              getRandomEnumConst(VFX_MODIFICATION_PRESET.class) :
            lastPreset = new EnumMaster<VFX_MODIFICATION_PRESET>().selectEnum(VFX_MODIFICATION_PRESET.class);
            modify(lastPreset);
        } else {
            random = DialogMaster.confirm("random?");
            modify(random);
        }
        last.reset();
    }

    public void modify(boolean random) {
        String choice = random ? new EnumMaster<EMITTER_VALUE_GROUP>().
         getRandomEnumConst(EMITTER_VALUE_GROUP.class).toString() :
         ListChooser.chooseEnum(EMITTER_VALUE_GROUP.class);
//        val=new EnumMaster<EMITTER_VALUE_GROUP>().retrieveEnumConst(EMITTER_VALUE_GROUP.class, choice);

        boolean setOrOffset = random || DialogMaster.confirm("Set or Offset?");
        String value = random ? getRandomValue(choice) : DialogMaster.inputText("");
        if (setOrOffset) {
            last.getEffect().set(choice, value);
        } else {
            last.getEffect().offset(choice, value);
        }

    }

    private String getRandomValue(String choice) {
        return "0";
    }

    private void lastModification() {
        modify(lastPreset);
    }

    private void modify(VFX_MODIFICATION_PRESET preset) {
        EMITTER_VALUE_GROUP group = getValGroup(preset);

        String val = getValue(preset);
        String modVals = (val == null) ? "" : group + EmitterPresetMaster.value_separator + val;
        EmitterActor newActor =
         EmitterPresetMaster.getInstance().
          getModifiedEmitter(last.path, true,
           modVals);
        if (val == null) {
            newActor.getEffect().toggle(group.getFieldName());
        }
        add(newActor, new Vector2(last.getX() +
//         newActor.getEffect().getBoundingBox().getWidth()
         100
         , last.getY()));

    }

    private String getValue(VFX_MODIFICATION_PRESET preset) {
        switch (preset) {
            case IMAGE_SAME_FOLDER:
                return FileManager.getRandomFile(
                 PathUtils.cropLastPathSegment(
                  EmitterPresetMaster.getInstance().findImagePath(last.path))).getPath();
            case IMAGE_SPRITE:
                return
                 PathFinder.getSpritesPathFull() +
                  new ImageChooser(PathFinder.getSpritesPathFull()).launch("", "");

            case IMAGE:
                return PathFinder.getVfxPath() + "images/" +
                 new ImageChooser(PathFinder.getVfxPath() + "images/").launch("", "");
        }
        return null;
    }

    private EMITTER_VALUE_GROUP getValGroup(VFX_MODIFICATION_PRESET preset) {
        switch (preset) {
            case IMAGE_SAME_FOLDER:
            case IMAGE_SPRITE:
            case IMAGE_PARTICLE:
            case IMAGE:
//                return EMITTER_VALUE_GROUP.Image_Path.name();
                return EMITTER_VALUE_GROUP.Image_Path;
//            case DOUBLE:
//            case HALF:
//                return EMITTER_VALUE_GROUP.Emission.name();
            case LOOPS:
                break;
            case LAGGING_PARTICLES:
                return EMITTER_VALUE_GROUP.Percentage_Of_Lagging_Particles;
            case COLOR_SET_PURPLE:

                break;
            case COLOR_SET_CYAN:
                break;
            case COLOR_SET_BROWN:
                break;
            case COLOR_SET_CRIMSON:
                break;
            case COLOR_SET_GOLDEN:
                break;
            case COLOR_OFFSET_RANDOM:
                break;
            case COLOR_INVERT_RED:
                break;
            case COLOR_INVERT_GREEN:
                break;
            case COLOR_INVERT_BLUE:
                break;
            case MERGE_RANDOM:
                break;
            case MERGE:
                break;
            case DOUBLE:
                break;
            case HALF:
                break;
            case INVERSE:
                return EMITTER_VALUE_GROUP.Angle;
            case MIRROR:
                return EMITTER_VALUE_GROUP.Rotation;
            case TOGGLE_ALPHA:
                return EMITTER_VALUE_GROUP.PremultipliedAlpha;
        }
        return null;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        if (testMode) {
            Ambience.setModifyParticles(true);
        }
        EmitterController.testMode = testMode;
    }

    public void setForActive() {
        String presetPath =
         new FileChooser(PathFinder.getVfxPath()).launch("", "");
//        ImageChooser ic = new ImageChooser();
//        ic.setDefaultFileLocation(PathFinder.getVfxPath()+"images//");
//        String imagePath = ic.

        ActiveObj active = DC_Game.game.getManager().getActiveObj().getRef().getActive();
        CompositeAnim anim = AnimConstructor.getOrCreate(active);
//        ANIM_PART part = ANIM_PART.IMPACT;
        anim.getMap().keySet().forEach(part -> {
//            anim.getMap().getVar(part).getData().setValue();
            List<SpellVfx> list = SpellVfxPool.getEmitters(presetPath);
            anim.getMap().get(part).setEmitterList(list);
        });
        //impact?


    }

    public boolean charTyped(char c) {
        switch (c) {
            case 'f':
                pickFunction();
                return true;
            case 'd':
                addRandom();
                return true;
            case 'c':
                create();
                return true;
            case 'M':
                lastModification();
                return true;
            case 'm':
                modify();
                return true;
            case 'l':
                lastModification();
                return true;
            case 's':
                save();
                return true;
            case 'S':
                saveAs();
                return true;
            case 'r':
                removeLast();
                return true;
            case 'R':
                clear();
                return true;
        }
        return false;
    }

    private void pickFunction() {
        EMITTER_CONTROLLER_FUNCTIONS func = new EnumMaster<EMITTER_CONTROLLER_FUNCTIONS>().selectEnum(EMITTER_CONTROLLER_FUNCTIONS.class);
        if (func == null) {
            return;
        }
        executeFunction(func);
    }

    private void executeFunction(EMITTER_CONTROLLER_FUNCTIONS func) {
        switch (func) {

            case ADD:
                create();
                break;
            case ADD_RANDOM:
                addRandom();
                break;
            case MODIFY:
                modify();
                break;
            case MODIFY_LAST:
                lastModification();
                break;
            case REMOVE:
                removeLast();
                break;
            case REMOVE_ALL:
                clear();
                break;
            case SAVE:
                save();
                break;
            case SAVE_AS:
                saveAs();
                break;
            case SAVE_ALL:
                break;
            case SET_DEFAULT_ADD_PATH:
                String result = new FileChooser(true, PathFinder.getVfxPath()).launch("", "");
                if (result != null) {
                    if (FileManager.isDirectory(PathFinder.getVfxPath() + result)) {
                        randomSfxPath = result;
                    }
                }
                break;
            case PICK_VFX:
                break;
        }
    }

    public enum EMITTER_CONTROLLER_FUNCTIONS {

        SET_DEFAULT_ADD_PATH,
        PICK_VFX,
        ADD,
        ADD_RANDOM,
        MODIFY,
        MODIFY_LAST,
        REMOVE,
        REMOVE_ALL,
        SAVE,
        SAVE_AS,
        SAVE_ALL,


    }


    public enum VFX_MODIFICATION_PRESET {
        IMAGE_SAME_FOLDER,
        IMAGE_SPRITE,
        IMAGE_PARTICLE,
        IMAGE,

        TOGGLE_ALPHA,
        LOOPS,
        LAGGING_PARTICLES,
        COLOR_SET_PURPLE,
        COLOR_SET_CYAN,
        COLOR_SET_BROWN,
        COLOR_SET_CRIMSON,
        COLOR_SET_GOLDEN,


        COLOR_OFFSET_RANDOM,

        COLOR_INVERT_RED,
        COLOR_INVERT_GREEN,
        COLOR_INVERT_BLUE,


        MERGE_RANDOM,
        MERGE,
        DOUBLE,
        HALF,
        INVERSE,
        MIRROR,

    }
}
