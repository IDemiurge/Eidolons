package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by PC on 22.10.2016.
 */
public class AnotherMenu implements Screen {
    SpriteBatch batch; // seems to be as Graphics2D class in simple java
    ShapeRenderer shapeRenderer;
    Texture img; // Image Class
    Sprite sprite;
    float img_X;
    float img_Y;
    float img_Width;
    float img_Height;
    float shape_X;
    float shape_Y;
    float shape_Width;
    float shape_Height;
    float time_Of_Each_FPS_delay;
    float timer_for_input_listen;
    float color1;
    float color2;
    float color3;
    float color4;
    boolean time_to_change_backgroud;
    Dark_Impact_Animation dark_impact_animation;
    Menu menu;
    Game game;
    public AnotherMenu(Menu menu){
        this.menu = menu;
    }
    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        batch = new SpriteBatch();
//        shapeRenderer =new ShapeRenderer();
//        shapeRenderer.setAutoShapeType(true);

//        path = new PathFinder();
//        String localPath = path.
        img = new Texture("D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\item\\mage armor.jpg");
        dark_impact_animation = new Dark_Impact_Animation();
        sprite = new Sprite(img);
//        sprite.scale(0.05f);


        img_X =200;
        img_Y = 200;
        img_Width = 50;
        img_Height = 50;
        time_Of_Each_FPS_delay = 0;
        timer_for_input_listen = 0;
        shape_X = 600;
        shape_Y = 150;
        shape_Width = 100;
        shape_Height = 85;
        sprite.setPosition(img_X,img_Y);

        color1 = 0;
        color2 = 0;
        color3 = 0;
        color4 = 0;
        time_to_change_backgroud = true;

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(color1,color2,color3,color4); // color for cleaning the screen,as for now its black
////        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );// method that deletes previous scene and make all pixels get Clear Color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        time_Of_Each_FPS_delay += Gdx.graphics.getDeltaTime();
        timer_for_input_listen += Gdx.graphics.getDeltaTime();
        batch.begin();
        if (time_Of_Each_FPS_delay >=0.4f){
            time_Of_Each_FPS_delay = 0;
        }
        System.out.println(time_Of_Each_FPS_delay);



        sprite.setTexture(dark_impact_animation.getTexture(time_Of_Each_FPS_delay));

        sprite.draw(batch);
        System.out.println("drawed");

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            if (timer_for_input_listen > 1){
                menu.myGame.setScreen(new Menu(menu.myGame));
                timer_for_input_listen = 0;
            }

        }

    }


    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
//        batch.end();

    }
}
