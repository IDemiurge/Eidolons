package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.data.filesys.PathFinder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by PC on 22.10.2016.
 */
public class Menu implements Screen {
    SpriteBatch batch; // seems to be as Graphics2D class in simple java
    ShapeRenderer shapeRenderer; // renderer of classic shapes
    Texture img; // Image Class
    Texture img1; // Image Class
    TextureRegion region; // sub image of Texture
    Sprite sprite; //simple batch
    Sprite sprite1; //simple batch

//    String pic1 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e1.jpg";
//    String pic2  = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e2.jpg";
//    String pic3 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e3.jpg";
//    String pic4 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e4.jpg";
//    String pic5 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e5.jpg";
//    String pic6 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e6.jpg";
//    String pic7 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e7.jpg";
//    String pic8 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e8.jpg";
//    String pic9 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e9.jpg";
//    String pic10 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e10.jpg";
//    String pic11 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e11.jpg";
//    String pic12 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e12.jpg";
//    String pic13 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e13.jpg";
//    String pic14 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e14.jpg";
//    String pic15 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e15.jpg";
//    String pic16 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e16.jpg";
//    String pic17 = "D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\electro impact\\e17.jpg";
    float img_X;
    float img_Y;
    float img_Width;
    float img_Height;
    float shape_X;
    float shape_Y;
    float shape_Width;
    float shape_Height;
    float time_Of_Each_FPS_delay;
    float time_Of_Each_FPS_delay1;
    float time_for_input_listen;
    float time_for_alpha;
    float alpha;
    float alpha1;
    float color1;
    float color2;
    float color3;
    float color4;
    boolean time_to_change_backgroud;
    Game myGame;
    Lightning_Animation lightning_animation;
    Dark_Impact_Animation dark_impact_animation;

    public Menu(Game game){
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
//        shapeRenderer =new ShapeRenderer();
//        shapeRenderer.setAutoShapeType(true);
        PathFinder.init();
       String s = PathFinder.getImagePath();

        img = new Texture(s+"mini\\item\\mage armor.jpg");
        img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lightning_animation = new Lightning_Animation();
        dark_impact_animation = new Dark_Impact_Animation();
        sprite = new Sprite(img);
        alpha = 1;
        alpha1 = 0.2f;
        sprite1 = new Sprite(img);

        sprite.scale(0.1f);



        img_X =200;
        img_Y = 200;
        img_Width = 50;
        img_Height = 50;
        time_Of_Each_FPS_delay = 0;
        time_Of_Each_FPS_delay1 = 0;
        time_for_input_listen = 0;
        time_for_alpha = 0;
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
        sprite1.setPosition(shape_X,shape_Y);


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
        System.out.println(time_Of_Each_FPS_delay+ " || " + time_Of_Each_FPS_delay1);
//        lightning_animation.setTime_Counter(lightning_animation.getTime_Counter()+Gdx.graphics.getDeltaTime());
//        System.out.println(time_Of_Each_FPS_delay);
//        sprite = new Sprite(lightning_animation.getTexture(time_Of_Each_FPS_delay));
        sprite.setTexture(lightning_animation.getTexture(time_Of_Each_FPS_delay));
        sprite1.setTexture(dark_impact_animation.getTexture(time_Of_Each_FPS_delay1));
        batch.begin();
        sprite.draw(batch);
        sprite1.draw(batch);
        batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            if (time_for_input_listen >1){
                myGame.setScreen(new AnotherMenu(this));
                time_for_input_listen = 0;
            }

        }
//        batch.end();
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
