package main.libgdx.anims.particles.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import main.data.filesys.PathFinder;
import main.entity.obj.ActiveObj;
import main.game.DC_Game;
import main.libgdx.GameScreen;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.CompositeAnim;
import main.libgdx.anims.particles.Ambience;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPools;
import main.libgdx.anims.particles.EmitterPresetMaster;
import main.libgdx.anims.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import main.libgdx.bf.GridMaster;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.DialogMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/27/2017.
 */
public class EmitterController {
    public static boolean overrideKeys = false;
    static EmitterController instance;
    private static boolean testMode;
    EmitterActor last;
    boolean continuous;
    int multiplier;
    boolean cursorAttached;
    private LinkedList<EmitterActor> sfx;

    public EmitterController() {
        sfx = new LinkedList<>();

        GuiEventManager.bind(GuiEventType.CREATE_EMITTER, p -> {
            create();
            if (p != null) {
                addRandom();
            }
        });
        GuiEventManager.bind(GuiEventType.SFX_PLAY_LAST, p -> replay());
    }

    public static EmitterController getInstance() {
        if (instance == null) {
            instance = new EmitterController();
        }
        return instance;
    }

    public void save() {
        EmitterPresetMaster.save(last);
    }

    public void create() {
        String presetPath =
         new FileChooser(PathFinder.getSfxPath()).launch("", "");
        if (presetPath == null) return;
        ImageChooser ic = new ImageChooser();
        ic.setDefaultFileLocation(PathFinder.getSfxPath() + "images//");
        String imagePath = ic
         .launch("", "");
        add(presetPath, imagePath );
    }

    public void addRandom() {
        String presetPath = FileManager.getRandomFilePath(PathFinder.getSfxPath()
         + "templates\\work\\"
        );
        add(presetPath, null );

//        add(presetPath,imagePath,destination);
    }



    public void replay() {
        if (last == null) return;
        last.reset();
        add(last, null );

    }

    public void add(String presetPath, String imagePath ) {
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
            e.printStackTrace();
            return;
        }

        if (imagePath != null) {
            actor.getEffect().setImagePath(imagePath);
        }
        add(actor, null );
    }

        public void add(EmitterActor actor, Vector2 v) {
        GameScreen.getInstance().getAnimsStage().addActor(actor);
        if (v==null )
            v = GridMaster.getMouseCoordinates();
        actor.setPosition(v.x, v.y);

//        int speed = 500;
//        ActorMaster.getMoveToAction(destination, actor, speed);
//        ActorMaster.addRemoveAfter(actor);
        actor.getEffect().start();
        last = actor;
        sfx.add(last);
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
            random = DialogMaster.confirm("random?");
            SFX_MODIFICATION_PRESET preset = random ?
             new EnumMaster<SFX_MODIFICATION_PRESET>().
              getRandomEnumConst(SFX_MODIFICATION_PRESET.class) :
             new EnumMaster<SFX_MODIFICATION_PRESET>().selectEnum(SFX_MODIFICATION_PRESET.class);
            modify(preset);
        }else {
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

        boolean setOrOffset = random ? true : DialogMaster.confirm("Set or Offset?");
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

    private void modify(SFX_MODIFICATION_PRESET preset) {
        String valName = getValName(preset);
        String val = getValue(preset);
        last.getEffect().set(valName, val);
        if (valName.equals("imagePath")) {
        EmitterActor newActor = EmitterPresetMaster.getInstance().
         getModifiedEmitter(last.path, true, valName + EmitterPresetMaster.value_separator + val);
//last.remove();
        add(newActor, null);
        }
    }

    private String getValue(SFX_MODIFICATION_PRESET preset) {
        switch (preset) {
            case IMAGE_SAME_FOLDER:
                return FileManager.getRandomFile(
                 StringMaster.cropLastPathSegment(
                  EmitterPresetMaster.getInstance().findImagePath(last.path))).getPath();
            case IMAGE_SPRITE:
                return FileManager.getRandomFile(PathFinder.getSpritesPath(), true).getPath();

        }
        return null;
    }

    private String getValName(SFX_MODIFICATION_PRESET preset) {
        switch (preset) {
            case IMAGE_SAME_FOLDER:
            case IMAGE_SPRITE:
            case IMAGE_PARTICLE:
//                return EMITTER_VALUE_GROUP.Image_Path.name();
            return "imagePath";
//            case DOUBLE:
//            case HALF:
//                return EMITTER_VALUE_GROUP.Emission.name();
            case INVERSE:
                return EMITTER_VALUE_GROUP.Angle.name();
            case MIRROR:
                return EMITTER_VALUE_GROUP.Rotation.name();
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
         new FileChooser(PathFinder.getSfxPath()).launch("", "");
//        ImageChooser ic = new ImageChooser();
//        ic.setDefaultFileLocation(PathFinder.getSfxPath()+"images//");
//        String imagePath = ic.

        ActiveObj active = DC_Game.game.getManager().getActiveObj().getRef().getActive();
        CompositeAnim anim = AnimMaster.getInstance().getConstructor().getOrCreate(active);
//        ANIM_PART part = ANIM_PART.IMPACT;
        anim.getMap().keySet().forEach(part -> {
//            anim.getMap().get(part).getData().setValue();
            List<EmitterActor> list = EmitterPools.getEmitters(presetPath);
            anim.getMap().get(part).setEmitterList(list);
        });
        //impact?


    }

    public boolean charTyped(char c) {
        switch (c) {
            case 'd':
                addRandom();
                return true;
            case 'c':
                create();
                return true;
            case 'm':
                modify();
                return true;
            case 's':
                save();
                return true;
            case 'r':
                clear();
                return true;
        }
        return false;
    }

    public enum SFX_MODIFICATION_PRESET {
        IMAGE_SAME_FOLDER,
        IMAGE_SPRITE,
        IMAGE_PARTICLE,

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
