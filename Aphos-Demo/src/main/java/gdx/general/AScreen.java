package gdx.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gdx.controls.AKeyListener;
import gdx.dto.LaneFieldDto;
import gdx.general.stage.AGuiStage;
import gdx.general.stage.ALanesStage;
import libgdx.GdxMaster;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.stage.camera.CameraMan;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import math.geom.Geom2D;
import org.lwjgl.opengl.GL11;

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
        guiStage = new AGuiStage(viewport, batch);
        background = new ABackground( batch);

//        lanesStage.setDebugAll(true);

        Gdx.input.setInputProcessor(new InputMultiplexer(controller, lanesStage, guiStage,
                new AKeyListener()));

        GuiEventManager.bind(GuiEventType.DTO_LaneField , p->{
            LaneFieldDto dto = (LaneFieldDto) p.get();
            setBackground(dto.getFocusAreaImage());
            lanesStage.getLaneField().setDto(dto);
            cameraMan.getCam().position.x = 0;
            cameraMan.centerCam();
        } );
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
}
