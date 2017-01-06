package main.libgdx.bf.mouse;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.DC_Game;
import main.libgdx.GameScreen;

/**
 * Created by PC on 25.10.2016.
 */
public class InputController implements InputProcessor {


    private Stage  bf;
    private Stage  gui;
    float x_cam_pos;
    float y_cam_pos;



    OrthographicCamera camera;
    boolean is_it_Left_Click = false;

    public InputController(OrthographicCamera camera){
        this.camera = camera;
    }

    public InputController(Stage bf, Stage gui, OrthographicCamera cam) {
        this.bf = bf;
        this.gui = gui;
        this.camera = cam;

    }

    


    // сюда передаются все обьекты, что есть в мире, и потом отсюда они управляются
    @Override
    public boolean keyDown(int i) {

        return false;
    }


    @Override
    public boolean keyUp(int i) {


        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        DC_Game.game.getBattleField().getKeyListener().handleKeyTyped(0,c);

        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
/*        bf.addActor(new ParticleActor(PARTICLE_EFFECTS.SMOKE_TEST.getPath(),
         GameScreen.getInstance().getWorld()
         , i, i1));*/
        // Условно у меня на ширину приложения пикселей приходится ширина камеры абстрактрых едениц
            if (i3 ==0){
                x_cam_pos = i;
                y_cam_pos = i1;
                is_it_Left_Click = true;
            }



        System.out.println( i + " || " + i1 + " || " + i2 + " || " + i3);

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        is_it_Left_Click = false;

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
//        System.out.println("i = " + i + " || i1 = " + i1 + " || i2 = "  + i2);
        if (is_it_Left_Click){
            camera.position.x +=(x_cam_pos- i)*camera.zoom;
            camera.position.y -=(y_cam_pos- i1)*camera.zoom;
            x_cam_pos = i;
            y_cam_pos = i1;
            Image background = GameScreen.getInstance().getBackground().backImage;
            background.setBounds(
              camera.position.x-  background.getWidth ()/2, camera.position.y-  background.getHeight ()/2,
             background.getWidth (),
             background.getHeight());
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        if (i == 1){
//            camera.zoom *= 2f;
            camera.zoom += 0.25f;
        }
        if (i == -1){
//            camera.zoom /= 2f;
            if (camera.zoom>=0.25f)
            camera.zoom -= 0.25f;

        }
        System.out.println(camera.zoom);
        return false;
    }
    public float getZoom() {
        return camera.zoom;
    }
    public float getX_cam_pos() {
        return x_cam_pos;
    }

    public float getY_cam_pos() {
        return y_cam_pos;
    }}
