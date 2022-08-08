package gdx.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.content.consts.VisualEnums;
import gdx.controls.AKeyListener;
import gdx.dto.LaneFieldDto;
import gdx.general.stage.AGuiStage;
import gdx.general.stage.ALanesStage;
import libgdx.GdxMaster;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.stage.camera.CameraMan;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.threading.WaitMaster;
import math.geom.Geom2D;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class AScreen extends ScreenAdapter {

    public InputController controller;
    public static Geom2D geometry;
    public static AScreen instance;
    private static float width,height;

    protected AGuiStage guiStage;
    protected ALanesStage lanesStage;
    protected CameraMan cameraMan;
    protected ABackground background;
    private boolean initialized;
    List<Screenshake> shakes = new ArrayList<>();

    public AScreen() {
        instance = this;
    }


    public void init(){
        ScreenViewport viewport = ScreenMaster.getMainViewport();
        CustomSpriteBatch batch = GdxMaster.getMainBatch();
        cameraMan = new CameraMan(viewport.getCamera(), ()-> controller.cameraZoomChanged());

        cameraMan.setWidth(viewport.getScreenWidth());
        cameraMan.setHeight(viewport.getScreenHeight());

        controller = new InputController(cameraMan);

        lanesStage = new ALanesStage(viewport, batch);
        guiStage = new AGuiStage(null, batch);
        GuiEventManager.bind(AphosEvent.DTO_LaneField , p->{
            LaneFieldDto dto = (LaneFieldDto) p.get();
            setBackground(dto.getFocusAreaImage());
            lanesStage.getLaneField().setDto(dto);
            cameraMan.getCam().position.x = 0;
            cameraMan.centerCam();
        } );

        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);
        background = new ABackground( batch);

//        lanesStage.setDebugAll(true);

        Gdx.input.setInputProcessor(new InputMultiplexer(controller, lanesStage, guiStage,
                new AKeyListener(), cameraMan.getDragController().createDragGestureHandler()));

        GuiEventManager.bind(AphosEvent.CAMERA_SHAKE, p -> {
            shakes.add((Screenshake) p.get());
        });
    }

    public void setBackground(String bg){
        background.setBackgroundPath(bg);
    }

    @Override
    public void render(float delta) {
        if (!initialized)
        {
            init();
            Gdx.gl20.glEnable(GL11.GL_POINT_SMOOTH);
            Gdx.gl20.glHint(GL11.GL_POINT_SMOOTH_HINT, GL20.GL_NICEST);
            Gdx.gl20.glEnable(GL11.GL_LINE_SMOOTH);
            Gdx.gl20.glHint(GL11.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);
            Gdx.gl20.glEnable(GL11.GL_POLYGON_SMOOTH);
            Gdx.gl20.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL20.GL_NICEST);
            initialized=true;
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        background.draw(delta);
        guiStage.act(delta);
        lanesStage.act(delta);
        guiStage.draw();
        lanesStage.draw();
        cameraMan.act(delta);
        processShakes(delta);
    }
    public void kill() {
        setBackground("bg/eldritch red.png");
        //fullscreen anims?
        shakes.add(new Screenshake(3f, true, VisualEnums.ScreenShakeTemplate.HARD));
        cameraMan.centerCam();
        cameraMan.zoom(10, 5);
    }

    public InputController getController() {
        return controller;
    }

    public CameraMan getCameraMan() {
        return cameraMan;
    }

    public static void setWidth(float width) {
        AScreen.width = width;
        geometry = new Geom2D((int) AScreen.getWidth(), (int) AScreen.getHeight());
    }

    public static float getWidth() {
        return width;
    }

    public static void setHeight(float height) {
        AScreen.height = height;
        geometry = new Geom2D((int)AScreen.getWidth(), (int)AScreen.getHeight());
    }

    public static float getHeight() {
        return height;
    }


    protected void processShakes(float delta) {
        if (!shakes.isEmpty()) {
            for (Screenshake shake : new ArrayList<>(shakes)) {
                try {
                    if (!shake.update(delta, getCameraMan().getCam(), getCameraMan().getCameraCenter())) {
                        shakes.remove(shake);
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    shakes.remove(shake);
                }
            }
            getCameraMan().getCam().update();
        }
    }

    public AGuiStage getGuiStage() {
        return guiStage;
    }

    public ALanesStage getLanesStage() {
        return lanesStage;
    }
}
