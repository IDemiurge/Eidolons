package main.test.libgdx;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by PC on 25.10.2016.
 */
public class MyInputController implements InputProcessor {

    float x_cam_pos;
    float y_cam_pos;
    public MyInputController (){

//        x_cam_pos = BattleFieldScreen.camera.position.x;
//        x_cam_pos = BattleFieldScreen.camera.position.y;
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
        System.out.println(c);
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        float lightningSpriteX = BattleFieldScreen.sprite1.getX();
        float lightningSpriteY = BattleFieldScreen.sprite1.getY();
        float lightningSpriteWidth = BattleFieldScreen.sprite1.getWidth();
        float lightningSpriteHeight = BattleFieldScreen.sprite1.getHeight();

//        System.out.println( i + " || " + i1 + " || " + i2 + " || " + i3);
        x_cam_pos = i;
        y_cam_pos = i1;
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {

//        cam.position.x += (x_cam_pos- i);
//        cam.position.y -= (y_cam_pos- i1) ;
        BattleFieldScreen.camera.position.x +=(x_cam_pos- i)*2;
        BattleFieldScreen.camera.position.y -=(y_cam_pos- i1)*2;
        System.out.println((x_cam_pos- i) + "||"+ (y_cam_pos- i1));
        x_cam_pos = i;
        y_cam_pos = i1;

        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
