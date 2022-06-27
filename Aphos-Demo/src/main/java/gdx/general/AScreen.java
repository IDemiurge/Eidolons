package gdx.general;

import com.badlogic.gdx.ScreenAdapter;
import gdx.general.stage.AGuiStage;
import gdx.general.stage.ALanesStage;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.mouse.InputController;
import libgdx.stage.camera.CameraMan;

public class AScreen extends ScreenAdapter {

    protected AGuiStage guiStage;
    protected ALanesStage lanesStage;
    protected CameraMan cameraMan;
    public InputController controller;
    protected FadeImageContainer background;

    public AScreen() {
    }

    public void init(){

    }

    public void setBackground(String bg){
        background.setImage(bg);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
