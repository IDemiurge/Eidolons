package main.test.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import main.data.filesys.PathFinder;
import main.libgdx.bf.mouse.InputController;

import java.util.ArrayList;

/**
 * Created by PC on 22.10.2016.
 */
public class BattleFieldScreen implements Screen {
    SpriteBatch batch; // seems to be as Graphics2D class in simple java
    ShapeRenderer shapeRenderer; // renderer of classic shapes
    Texture img; // Image Class
    TextureRegion region; // sub image of Texture
    Sprite sprite; //simple sprite
    public static Sprite sprite1; //simple sprite
      Sprite spriteForBack; //simple sprite
    public static Sprite portraitFrame; //simple sprite
    Sprite testSprite; //simple sprite
    Texture testTexture;


    protected static InputController controller;

    float img_X = 200;
    float img_Y = 200;

    float background_Width = 1365;
    float background_Height = 1023;

    public static float shape_X = 600;
    public static float shape_Y = 150;

    float time_Of_Each_FPS_delay = 0;
    float time_Of_Each_FPS_delay1 = 0;
    float time_for_input_listen = 0;
    float time_for_alpha = 0;
    float y_cam_pos;
    float x_cam_pos;
    float time_for_frame_alfa = 0;
    boolean frame_alfa_flag = true;
    float alpha1 = 0.2f;
    float alpha = 0.2f;

    Stage stage;
    Game myGame;
    Pixmap pixmap;
    Texture back;
    Texture face;
    Texture face2;
    Texture backGround;


    Texture cam;
    Sprite camspr;
    TestActorForStage camact;


    Lightning_Animation lightning_animation;
    Dark_Impact_Animation dark_impact_animation;

    public static OrthographicCamera camera;
    public static float x_for_camera = 0;
    public static float y_for_camera = 0;
    float z_for_camera = 0;
    float camera_width = 800;
    float camera_height = 600;
    TestActorForStage actor;
    TestActorForStage actor2;
    TestActorForStage actorBackground;
    public static ArrayList<TestActorForStage> actorsArray;


    Texture paralax;
    Texture paralax1;
    Table table;

    public BattleFieldScreen(Game game){
        this.myGame = game;
    }

    @Override
    public void dispose() {
        System.out.println("dispose");

    }

    @Override
    public void show() {
        System.out.println("update");
        actorsArray = new ArrayList<>();
        batch = new SpriteBatch();
//        shapeRenderer =new ShapeRenderer();
//        shapeRenderer.setAutoShapeType(true);

        PathFinder.init();
       String s = PathFinder.getImagePath();

        img = new Texture(s+"mini\\item\\mage armor.jpg");
        img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

//        lightning_animation = new Lightning_Animation();
//        dark_impact_animation = new Dark_Impact_Animation();
//        sprite = new Sprite(img);
//        sprite1 = new Sprite(img);

        testTexture = new Texture(s + "UI\\DIRECTION POINTER.png");

        pixmap = new Pixmap((int)background_Width+1,(int)background_Height+1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(Color.RED));
        pixmap.drawRectangle(0,0,(int)background_Width,(int)background_Height);
        back = new Texture(pixmap);
        spriteForBack = new Sprite(back);
        portraitFrame = new Sprite(new Texture(s + "UI\\Borders\\neo\\active enemy select.png"));
        portraitFrame.setBounds(20,400,100,100);

        testSprite = new Sprite(testTexture);
        testSprite.setBounds(20+45,400+10,10,10);
        testSprite.flip(true,false);

//        sprite.setPosition(img_X,img_Y);
//        sprite.setBounds(img_X,img_Y,300,300);
//
//        sprite1.setPosition(shape_X,shape_Y);
//        sprite1.setBounds(shape_X,shape_Y,200,300);
        spriteForBack.setPosition(0,0);

        camera = new OrthographicCamera(camera_width,camera_height);
        camera.position.set(x_for_camera+camera_width/2,y_for_camera+camera_height/2,z_for_camera);
        paralax = new Texture(s + "big\\New2\\Underworld.jpg");
        paralax1 = new Texture(s + "big\\New2\\Underworld.jpg");
        Image image = new Image(paralax);
        Image image1 = new Image(paralax1);
       image.setBounds(0,0,1000,1000);
       image1.setBounds(1000,0,1000,1000);
        table = new Table();
        table.addActor(image);
        table.addActor(image1);



        face = new Texture(s + "mini\\unit\\Kingsguard.jpg");
        face2 = new Texture(s + "mini\\unit\\new3\\knight3.jpg ");
        backGround = new Texture(s + "big\\New2\\188110.jpg");
        face.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        face2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backGround.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Sprite forFace1 = new Sprite(face);
        Sprite forFace2 = new Sprite(face2);
        Sprite forbackGround = new Sprite(backGround);

        actor = new TestActorForStage(forFace1,100,100,400,200,true);
        actor2 = new TestActorForStage(forFace2,100,100,400,350,true);
        actorBackground = new TestActorForStage(forbackGround,1024,768,0,0,false);
        camspr = new Sprite(testTexture);


        stage = new Stage(new ExtendViewport(camera_width,camera_height,camera));
        stage.getCamera().viewportWidth = 800;
        stage.getCamera().viewportHeight = 600;
//        stage.getCamera().position.add(0,0,0);
        camact = new TestActorForStage(camspr,10,10,stage.getCamera().viewportWidth/2,stage.getCamera().viewportHeight/2,false);
        stage.addActor(table);
        actorsArray.add(actor);
        actorsArray.add(actor2);
        actorsArray.add(actorBackground);
        actorsArray.add(camact);
        stage.addActor(camact);
        stage.addActor(actorBackground);
        stage.addActor(actor);
        stage.addActor(actor2);


        controller = new InputController(camera);
        Gdx.input.setInputProcessor(controller);
//        stage.setKeyboardFocus(actor);
//        Gdx.input.setInputProcessor(stage);
//        stage.addListener(new InputListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                if (actorBackground.isSelected()){
//                    x_cam_pos = x;
//                    y_cam_pos = y;
//                }
//                  return true;
//            }
//
//            @Override
//            public void touchDragged(InputEvent event, float x, float y, int pointer) {
//                System.out.println(event + "| X: " + x + " | Y:" + y );
//                float posX = camact.getX() - (x-x_cam_pos)/2;
//
//                float posY = camact.getY() - (y-y_cam_pos)/2;
//
//                camact.setPosition(posX,posY);
//                x_cam_pos = x;
//                y_cam_pos = y;
//                super.touchDragged(event, x, y, pointer);
//            }
//        });
    }

    @Override
    public void render(float v) {
//        Gdx.gl.glClearColor(color1,color2,color3,color4); // color for cleaning the screen,as for now its black
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );// method that deletes previous scene and make all pixels getOrCreate Clear Color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
//        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.setProjectionMatrix(camera.combined);

        time_for_input_listen += Gdx.graphics.getDeltaTime();
        time_Of_Each_FPS_delay += Gdx.graphics.getDeltaTime();
        time_Of_Each_FPS_delay1 += Gdx.graphics.getDeltaTime();
        time_for_alpha += Gdx.graphics.getDeltaTime();
        time_for_frame_alfa += Gdx.graphics.getDeltaTime();
        camera.update();


//        if (time_for_frame_alfa >=0.05){
//            if (frame_alfa_flag){
//                alpha1 += 0.02;
//                if (alpha1 >=1){
//                    frame_alfa_flag = false;
//                }
//            }
//            if (!frame_alfa_flag){
//                alpha1-=0.02;
//                if (alpha1 <=0.2f){
//                    frame_alfa_flag = true;
//                }
//            }
//            portraitFrame.setAlpha(alpha1);
//            time_for_frame_alfa = 0;
//            testSprite.setRotation(testSprite.getRotation()+5);
//            testSprite.setOrigin(5,40);
//            portraitFrame.setRotation(portraitFrame.getRotation()+5);
//            portraitFrame.setOrigin(50,50);
////            testSprite.setCenter(150,150);
//
//        }

//        if (time_for_alpha >= 0.2){
//            Random random = new Random();
//            float current = 0.5f+Math.abs(random.nextFloat())%0.5f;
//                alpha = current;
//                sprite.setAlpha(alpha);
//            random = new Random();
//            current = 0.2f+Math.abs(random.nextFloat())%0.8f;
//            sprite1.setAlpha(current);
//            int a = random.nextInt(100)-50;
//            int b = random.nextInt(100)-50;
//
//            sprite.setPosition(img_X+a,img_Y+b);
//            time_for_alpha = 0;
//        }
//
//        if (time_Of_Each_FPS_delay > 0.34f){
//            time_Of_Each_FPS_delay = 0;
//        }
//        if (time_Of_Each_FPS_delay1 > 0.4f){
//            time_Of_Each_FPS_delay1 = 0;
//        }
//        sprite.setTexture(lightning_animation.getTexture(time_Of_Each_FPS_delay));
//        sprite1.setTexture(dark_impact_animation.getTexture(time_Of_Each_FPS_delay1));

//        shapeRenderer.begin();
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(20,20,100,100);
//        shapeRenderer.end();
        for (Actor a : table.getChildren()){
            if (a.getX() < stage.getCamera().position.x - 1500){
                a.setX(stage.getCamera().position.x + 500);
            }
        }
//        for (Actor a : table.getChildren()){
//            if (a.getX() < stage.getCamera().position.x - stage.getCamera().viewportWidth/2 - stage.getCamera().position.x){
//                a.setX(stage.getCamera().position.x + stage.getCamera().viewportWidth/2);
//            }
//        }
//        stage.getCamera().position.set(new Vector2(camact.getX(),camact.getY()),0);
        stage.act(v);
        stage.draw();

//        batch.begin();
//        batch.enableBlending();
//        spriteForBack.draw(batch);
////        testSprite.draw(batch);
////        portraitFrame.draw(batch);
////        sprite.draw(batch);
////        sprite1.draw(batch);
//        batch.end();
//        if (Gdx.input.isKeyPressed(Input.Keys.A)){
//            if (time_for_input_listen >1){
//                myGame.setScreen(new AnotherMenu(this));
//                time_for_input_listen = 0;
//            }
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
//            camera.position.y +=5;
//
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
//            camera.position.y -=5;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
//            camera.position.x -=5;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//            camera.position.x += 5;
//        }
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
        dispose();
    }
}
