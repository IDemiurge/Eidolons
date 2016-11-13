package main.test.libgdx;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by PC on 25.10.2016.
 */
public class MyInputController implements InputProcessor {

    float x_cam_pos;
    float y_cam_pos;
    OrthographicCamera camera;
    boolean is_it_Left_Click = false;

    public MyInputController (OrthographicCamera camera){
        this.camera = camera;
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
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
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
            camera.position.x +=(x_cam_pos- i);
            camera.position.y -=(y_cam_pos- i1);
            x_cam_pos = i;
            y_cam_pos = i1;
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
            camera.zoom *= 2f;
        }
        if (i == -1){
            camera.zoom /= 2f;
        }
        System.out.println(camera.zoom);
        return false;
    }


}
