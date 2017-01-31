package main.libgdx.anims.particles.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import main.data.filesys.PathFinder;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.particles.Ambience;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPresetMaster;
import main.libgdx.anims.particles.EmitterPresetMaster.EMITTER_VALUE_GROUP;
import main.libgdx.bf.GridMaster;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.DialogMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 1/27/2017.
 */
public class EmitterController    {
    static EmitterActor last;
    static  EmitterController instance;
    boolean continuous;
    int multiplier;
    boolean cursorAttached;
    private static boolean testMode;



    public static EmitterController getInstance() {
        if (instance==null )instance=new EmitterController();
        return instance;
    }

    public  EmitterController (){
        GuiEventManager.bind(GuiEventType.CREATE_EMITTER, p->{
                create();
            if (p!=null ){
                addRandom();
            }
        });
        GuiEventManager.bind(GuiEventType.SFX_PLAY_LAST, p->EmitterController.replay());
    }

    public static void save() {
        EmitterPresetMaster.save(last);
    }

    public static void create() {
        String presetPath =
        new FileChooser(PathFinder.getSfxPath()).launch("","");
        ImageChooser ic = new ImageChooser();
        ic.setDefaultFileLocation(PathFinder.getSfxPath()+"images//");
        String imagePath = ic
         .launch("", "");
        Coordinates destination = DC_Game.game.getUnits().get(1).getCoordinates();
add(presetPath,imagePath,destination);
    }

    public static void addRandom() {
//        String presetPath = FileManager.getRandomFilePath(PathFinder.getSfxPath())
//        add(presetPath,imagePath,destination);
    }

    public static void replay(Coordinates destination) {
        add(last, destination);
    }

    public static void replay() {
        Coordinates destination = DC_Game.game.getUnits().get(1).getCoordinates();
        last.reset();
        add(last, destination);

    }

    public static void add(String presetPath, String imagePath, Coordinates destination) {
        EmitterActor actor = new EmitterActor(presetPath, true){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                    act(Gdx.graphics.getDeltaTime());
                super.draw(batch, parentAlpha);
            }
        };
        if (imagePath!=null )
        actor.getEffect().setImagePath(imagePath);
        add(actor, destination);
    }

    public static void add(EmitterActor actor, Coordinates destination) {
        GameScreen.getInstance().getAnimsStage().addActor(actor);
       Vector2 v= GridMaster.getMouseCoordinates();
        actor.setPosition(v.x,v.y);

        int speed = 500;
        ActorMaster.getMoveToAction(destination, actor, speed);
        ActorMaster.addRemoveAfter(actor);
        actor.getEffect().start();
        last = actor;


    }

    public static void modify() {
        String choice = ListChooser.chooseEnum(EMITTER_VALUE_GROUP.class);
        boolean setOrOffset = DialogMaster.confirm("Set or Offset?");
        String value = DialogMaster.inputText("");
        if (setOrOffset)
        last.getEffect(). set(choice, value);
        else last.getEffect().offset(choice, value);

    }

    public static void setTestMode(boolean testMode) {
        if (testMode){
            Ambience.setModifyParticles(true);
        }
        EmitterController.testMode = testMode;
    }

    public static boolean isTestMode() {
        return testMode;
    }
}
