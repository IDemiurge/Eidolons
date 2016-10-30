package main.test.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by PC on 25.10.2016.
 */
public class MyInputController implements InputProcessor {

    float x_cam_pos;
    float y_cam_pos;
    float x_for_sprite_to_move;
    float y_for_sprite_to_move;
    boolean flag = false;
    public MyInputController (){

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
//        float ratio = TestLauncher.wid/BattleFieldScreen.camera.viewportWidth;
//        float ratio = 800/1024;
//        float mouseX = i+BattleFieldScreen.camera.position.x- TestLauncher.wid/2;
////        float mouseX = i+BattleFieldScreen.camera.position.x- TestLauncher.wid/2;
//        float mouseY = (i1 + BattleFieldScreen.camera.position.y*-1 - TestLauncher.hei/2)*-1;
//        float spriteX = BattleFieldScreen.sprite1.getX();
//        float spriteY = BattleFieldScreen.sprite1.getY();
//        float spriteWidth = BattleFieldScreen.sprite1.getWidth();
//        float spriteHeight = BattleFieldScreen.sprite1.getHeight();
//        System.out.println("mous X is on Sprite: " + (mouseX >= spriteX && mouseX <= (spriteX + spriteWidth))+ " || mouse Y in on Sprite: " + (mouseY >= spriteY && mouseY <= (spriteY + spriteHeight)));
//        System.out.println("Mouse X:" + mouseX + " || i: " + i + "|| Sprite X:" + spriteX + " || Sprite Width:" + spriteWidth );
//        System.out.println("Mouse Y:" + mouseY + " || i1: " + i1 + " || Sprite Y:" + spriteY + " || Sprite Height:" + spriteHeight );
//        System.out.println("Camera X:" +BattleFieldScreen.camera.position.x + " || Camera Y:" + BattleFieldScreen.camera.position.y + " || ratio: " + ratio );
//        if (mouseX >= spriteX && mouseX <= (spriteX + spriteWidth)){
//            if (mouseY >= spriteY && mouseY <= (spriteY + spriteHeight)){
//                x_for_sprite_to_move = i;
//                y_for_sprite_to_move = i1;
//                flag = true;
//            }
//        }
//        else {
            x_cam_pos = i;
            y_cam_pos = i1;
//        }

//        System.out.println( i + " || " + i1 + " || " + i2 + " || " + i3);

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (flag){
            flag = !flag;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
//        System.out.println("i = " + i + " || i1 = " + i1 + " || i2 = "  + i2);
//        if (flag){
//            float spriteX = BattleFieldScreen.sprite1.getX();
//            float spriteY = BattleFieldScreen.sprite1.getY();
//            spriteX -= (x_for_sprite_to_move -i);
//            spriteY += (y_for_sprite_to_move - i1);
//            BattleFieldScreen.sprite1.setPosition(spriteX,spriteY);
//            x_for_sprite_to_move = i;
//            y_for_sprite_to_move = i1;
//        }
//        else {
//            BattleFieldScreen.camera.position.x +=(x_cam_pos- i);
//            BattleFieldScreen.camera.position.y -=(y_cam_pos- i1);
//            x_cam_pos = i;
//            y_cam_pos = i1;
//        }
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
