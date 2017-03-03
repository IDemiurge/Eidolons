package main.test.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.data.filesys.PathFinder;

import java.util.Random;


/**
 * Created by PC on 27.10.2016.
 */
public class TestActorForStage extends Actor {
    static float x_cam_pos;
    static float y_cam_pos;
    Sprite sprite;
    Sprite spriteDirection;
    Texture texture;
    Texture textureDirection;
    float counterup;
    float counterdown;
    boolean timeTOChanceDirection = false;
    boolean first = true;
    float time_for_frame_alfa = 0;
    float alpha1 = 0.2f;
    boolean frame_alfa_flag = true;
    Sprite portraitFrame;
    boolean selected = false;
    boolean unit;
    boolean test;
    int ID;
    InputListener listener;


    public TestActorForStage(Sprite face, float width, float height, float X, float Y, boolean Unit_Graphic_Items_Needed) {
        unit = Unit_Graphic_Items_Needed;
        counterup = 0;
        counterdown = 0;
        Random random = new Random();
        ID = random.nextInt();
        setName("" + ID);
        setBounds(X, Y, width, height);
        String path = PathFinder.getImagePath();
        sprite = new Sprite(face);
        sprite.setBounds(getX(), getY(), getWidth(), getHeight());
        textureDirection = new Texture(path + "UI\\DIRECTION POINTER.png");
        spriteDirection = new Sprite(textureDirection);
        spriteDirection.setBounds(getX() + 45, getY() + 5, 10, 10);
        portraitFrame = new Sprite(new Texture(path + "UI\\Borders\\neo\\active enemy select.png"));
        portraitFrame.setBounds(getX() - 2, getY() + 2, getWidth() + 2, getHeight() + 2);
        listener = new InputListener();
        addListener(new InputListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                System.out.println("drag");
//                getStage().touchDragged((int)x,(int)y,pointer);
//                System.out.println( "last position " + x_cam_pos +"______" + y_cam_pos);
//                System.out.println(event + " || " + x + " || " + y );
//                float currXdiff = -1*(x_cam_pos-x);
//                float currYdiff = y_cam_pos-y;
//                x_cam_pos = x;
//                y_cam_pos = y;
//                System.out.println("diff X: " +(x_cam_pos -x) + " -_-_-_- diff Y: " +(y_cam_pos -y) );
//                System.out.println();
//                getStage().getCamera().position.set(new Vector2(getStage().getCamera().position.x-5,getStage().getCamera().position.y),0);
//                getStage().getCamera().position.set(new Vector2(getStage().getCamera().position.x-x_cam_pos+x,getStage().getCamera().position.y-y_cam_pos+y),0);
//                System.out.println(" Cam X: " + getStage().getCamera().position.x + " [][][] Cam Y: " + getStage().getCamera().position.y);
//                getStage().getCamera().position.x = getStage().getCamera().position.x + x_cam_pos - x;
//                BattleFieldScreen.camera.position.x += (x_cam_pos - x);
//                BattleFieldScreen.camera.position.y -= (y_cam_pos - y);
//                BattleFieldScreen.camera.position.x += (x_cam_pos - x);
//                BattleFieldScreen.camera.position.y -= (y_cam_pos - y);
//                if (x_cam_pos> x){
//                    getStage().getCamera().position.x += 10;
//                }
//                if (x_cam_pos< x){
//                    getStage().getCamera().position.x -= 10;
//                }
//                if (y_cam_pos> x){
//                    getStage().getCamera().position.x += 10;
//                }
//                if (x_cam_pos< x){
//                    getStage().getCamera().position.x -= 10;
//                }
//
//                getStage().getCamera().position.y += (y_cam_pos - y);

//                System.out.println("Cam position X: " +  BattleFieldScreen.camera.position.x  + " || Cam position Y: " + BattleFieldScreen.camera.position.y);

                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touch UP");
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                System.out.println("Key down detected on " + getName());
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Touch down detected on " + getName());
                System.out.println(event + " || " + x + " || " + y);

                selected = true;
                for (int i = 0; i < BattleFieldScreen.actorsArray.size(); i++) {
                    if (!BattleFieldScreen.actorsArray.get(i).getName().equals(getName())) {
                        BattleFieldScreen.actorsArray.get(i).setSelected(false);
                    }
                }
                x_cam_pos = x;
                y_cam_pos = y;

                return super.touchDown(event, x, y, pointer, button);
//                return true;
            }

//            @Override
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                System.out.println("Mouse Entered " + getName());
//                super.enter(event, x, y, pointer, fromActor);
//            }
//
//            @Override
//            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                System.out.println("Mouse left" + getName());
//                super.exit(event, x, y, pointer, toActor);
//            }

        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
        if (selected) {
            if (unit) {
                portraitFrame.draw(batch);
            }

            getStage().setKeyboardFocus(this);
        }
        if (unit) {
            spriteDirection.draw(batch);
        }

    }

    @Override
    public void act(float delta) {
        getStage().getCamera().update();
        time_for_frame_alfa += delta;
        if (time_for_frame_alfa >= 0.05) {
            if (frame_alfa_flag) {
                alpha1 += 0.02;
                if (alpha1 >= 1) {
                    frame_alfa_flag = false;
                }
            }
            if (!frame_alfa_flag) {
                alpha1 -= 0.02;
                if (alpha1 <= 0.2f) {
                    frame_alfa_flag = true;
                }
            }
            portraitFrame.setAlpha(alpha1);
            time_for_frame_alfa = 0;
//            testSprite.setCenter(150,150);
        }
//    boolean test  = BattleFieldScreen.controller.keyDown(Input.Keys.UP);
//        System.out.println(test);
        if (selected) {
            if (unit) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

                    setPosition(getX() - 1, getY());
                }
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    setPosition(getX() + 1, getY());
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    setPosition(getX(), getY() + 1);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    setPosition(getX(), getY() - 1);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    timeTOChanceDirection = true;
                }
            }
        }
        if (timeTOChanceDirection) {
            if (first) {
                setOrigin(getWidth() / 2, getHeight() / 2);
                setRotation(getRotation() + 1);
                counterup++;
                if (counterup == 95) {
                    first = false;
                    counterup = 0;
                }
            }
            if (!first) {
                setRotation(getRotation() - 1);
                counterdown++;
                if (counterdown == 5) {
                    first = true;
                    timeTOChanceDirection = false;
                    counterdown = 0;
                }
            }
        }

        sprite.setBounds(getX(), getY(), getWidth(), getHeight());
        spriteDirection.setBounds(getX() + 45, getY() + 5, 10, 10);
        spriteDirection.setRotation(getRotation());
        spriteDirection.setOrigin(5, 45);
        portraitFrame.setBounds(getX(), getY(), getWidth(), getHeight());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
