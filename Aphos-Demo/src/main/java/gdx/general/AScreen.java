package gdx.general;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gdx.dto.LaneFieldDto;
import gdx.general.stage.AGuiStage;
import gdx.general.stage.ALanesStage;
import libgdx.GdxMaster;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.stage.camera.CameraMan;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class AScreen extends ScreenAdapter {

    protected AGuiStage guiStage;
    protected ALanesStage lanesStage;
    protected CameraMan cameraMan;
    public InputController controller;
    protected ABackground background;

    public AScreen() {
//        GuiEventManager.setManager();
        init();
    }

    public void init(){
        ScreenViewport viewport = ScreenMaster.getMainViewport();
        CustomSpriteBatch batch = GdxMaster.getMainBatch();
        cameraMan = new CameraMan(viewport.getCamera(), ()-> controller.cameraZoomChanged());

        controller = new InputController(cameraMan);

        lanesStage = new ALanesStage(viewport, batch);
        guiStage = new AGuiStage(viewport, batch);
        background = new ABackground( batch);

        GuiEventManager.bind(GuiEventType.DTO_LaneField , p->{
            lanesStage.getLaneField().setDto((LaneFieldDto) p.get());
        } );
    }

    public void setBackground(String bg){
        background.setBackgroundPath(bg);
    }

    @Override
    public void render(float delta) {
        background.draw(delta);
        guiStage.act(delta);
        lanesStage.act(delta);
        guiStage.draw();
        lanesStage.draw();
    }
}
