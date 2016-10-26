package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import main.data.filesys.PathFinder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by PC on 22.10.2016.
 */
public class BattleFieldScreen implements Screen {
    SpriteBatch batch; // seems to be as Graphics2D class in simple java
    ShapeRenderer shapeRenderer; // renderer of classic shapes
    Texture img; // Image Class
    Texture img1; // Image Class
    TextureRegion region; // sub image of Texture
    Sprite sprite; //simple batch
    public static Sprite sprite1; //simple batch
    Sprite spriteForBack; //simple batch

    float img_X = 200;
    float img_Y = 200;
    float background_X = 0;
    float background_Y = 0;
    float background_Width = 1365;
    float background_Height = 1023;
    float img_Width = 50;
    float img_Height = 50;
    public static float shape_X = 600;
    public static float shape_Y = 150;
    float shape_Width = 200;
    float shape_Height = 200;
    float time_Of_Each_FPS_delay = 0;
    float time_Of_Each_FPS_delay1 = 0;
    float time_for_input_listen = 0;
    float time_for_alpha = 0;
    float alpha = 1;
    float alpha1 = 0.2f;
    float color1 = 0;
    float color2 = 0;
    float color3 = 0;
    float color4 = 0;
    boolean time_to_change_backgroud = true;
    Game myGame;
    Pixmap pixmap;
    Texture back;
    Lightning_Animation lightning_animation;
    Dark_Impact_Animation dark_impact_animation;


    public static OrthographicCamera camera;
    public static float x_for_camera = 0;
    public static float y_for_camera = 0;
    float z_for_camera = 0;
    float camera_width = 1024;
    float camera_height =768;

    public BattleFieldScreen(Game game){
        this.myGame = game;
    }

    @Override
    public void dispose() {
        System.out.println("dispose");

    }

    @Override
    public void show() {
        System.out.println("show");
        batch = new SpriteBatch();
        shapeRenderer =new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        PathFinder.init();
       String s = PathFinder.getImagePath();

        img = new Texture(s+"mini\\item\\mage armor.jpg");
        img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lightning_animation = new Lightning_Animation();
        dark_impact_animation = new Dark_Impact_Animation();
        sprite = new Sprite(img);
        sprite1 = new Sprite(img);

        pixmap = new Pixmap((int)background_Width+1,(int)background_Height+1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(Color.RED));
        pixmap.drawRectangle(0,0,(int)background_Width,(int)background_Height);
        back = new Texture(pixmap);
        spriteForBack = new Sprite(back);


//        sprite.scale(0.1f);
        sprite.setPosition(img_X,img_Y);
        sprite.setBounds(img_X,img_Y,300,300);

        sprite1.setPosition(shape_X,shape_Y);
        spriteForBack.setPosition(0,0);

        camera = new OrthographicCamera(camera_width,camera_height);
        camera.position.set(x_for_camera+camera_width/2,y_for_camera+camera_height/2,z_for_camera);
        MyInputController controller = new MyInputController();
        Gdx.input.setInputProcessor(controller);



    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(color1,color2,color3,color4); // color for cleaning the screen,as for now its black
////        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );// method that deletes previous scene and make all pixels get Clear Color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        time_for_input_listen += Gdx.graphics.getDeltaTime();
        time_Of_Each_FPS_delay += Gdx.graphics.getDeltaTime();
        time_Of_Each_FPS_delay1 += Gdx.graphics.getDeltaTime();
        time_for_alpha += Gdx.graphics.getDeltaTime();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (time_for_alpha >= 0.2){
            Random random = new Random();
            float current = 0.5f+Math.abs(random.nextFloat())%0.5f;
                alpha = current;
                sprite.setAlpha(alpha);
            random = new Random();
            current = 0.2f+Math.abs(random.nextFloat())%0.8f;
            sprite1.setAlpha(current);
            int a = random.nextInt(100)-50;
            int b = random.nextInt(100)-50;

            sprite.setPosition(img_X+a,img_Y+b);
            time_for_alpha = 0;
        }

        if (time_Of_Each_FPS_delay > 0.34f){
            time_Of_Each_FPS_delay = 0;
        }
        if (time_Of_Each_FPS_delay1 > 0.4f){
            time_Of_Each_FPS_delay1 = 0;
        }
        sprite.setTexture(lightning_animation.getTexture(time_Of_Each_FPS_delay));
        sprite1.setTexture(dark_impact_animation.getTexture(time_Of_Each_FPS_delay1));
//        shapeRenderer.begin();
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(20,20,100,100);
//        shapeRenderer.end();

        batch.begin();
        spriteForBack.draw(batch);
        sprite.draw(batch);
        sprite1.draw(batch);
        batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            if (time_for_input_listen >1){
                myGame.setScreen(new AnotherMenu(this));
                time_for_input_listen = 0;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
//            y_for_camera += 5;
            camera.position.y +=5;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
//            y_for_camera -= 5;
            camera.position.y -=5;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
//            x_for_camera -= 5;
            camera.position.x -=5;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//            x_for_camera += 5;
            camera.position.x += 5;
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {
        System.out.println("pause");

    }

    @Override
    public void resume() {
        System.out.println("resume");

    }

    @Override
    public void hide() {
        System.out.println("hide");
//        batch.end();
        dispose();

    }
}
