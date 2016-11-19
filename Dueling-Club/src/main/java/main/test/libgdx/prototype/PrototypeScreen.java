package main.test.libgdx.prototype;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import main.data.filesys.PathFinder;

import java.util.ArrayList;


/**
 * Created by PC on 07.11.2016.
 */
public class PrototypeScreen implements Screen {
    World world;
   Stage stage;
    float viewport_width = 20;
    float viewport_height = 15;
    static RayHandler rayHandler;
    GUIStage guiStage;
    Box2DDebugRenderer debugRenderer;
    PrototypeController controller;
    boolean flag = true;
    float distance = 30;
    float distance1 = 30;
    float degree = 45;
    float degree1 = 90;
    long timer = 0;
    long counter = 0;
    ConeLight coneLight;
    ConeLight coneLight1;
    private ArrayList<Texture> regions;
    private Animation anim;
    Sprite sprite;
    SpriteBatch batch;

    @Override
    public void show() {
        PathFinder.init();
        // TEMP
        PathFinder.init();
        regions = new ArrayList<>();
        for (int i = 1;i<18;i++){
            Texture local_Texture = new Texture(PathFinder.getImagePath() + "mini\\sprites\\impact\\electro impact\\e"+i+".jpg");
            local_Texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            regions.add(local_Texture);
        }
        anim = new Animation(regions,regions.size()-1,0.85f);
        sprite = new Sprite(new Texture(PathFinder.getImagePath() + "mini\\sprites\\impact\\electro impact\\e"+1+".jpg"));
        sprite.setBounds(3,3,5,5);
        batch = new SpriteBatch();

        // TEMP END
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0,-10),false);
        rayHandler = new RayHandler(world);
//        rayHandler.setAmbientLight(Color.SKY);
//        rayHandler.setAmbientLight(0.1f);
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(5);
        rayHandler.setGammaCorrection(true);

       coneLight = new ConeLight(rayHandler,75,Color.RED,distance,10,10,degree,degree);
       coneLight1 = new ConeLight(rayHandler,75,Color.RED,distance1,20,25,270,degree1);

        guiStage = new GUIStage();
        stage = new Stage(new FitViewport(viewport_width,viewport_height));
        batch.setProjectionMatrix(stage.getCamera().combined);
        controller = new PrototypeController((OrthographicCamera) stage.getCamera());
        PlayerActor player = new PlayerActor(world);
        GridActor grid = new GridActor();

        stage.addActor(player);
        stage.addActor(grid);
        grid.setZIndex(1);
        player.setZIndex(2);
        stage.setDebugAll(true);
        InputMultiplexer in = new InputMultiplexer();
        in.addProcessor(guiStage);
        in.addProcessor(controller);
        in.addProcessor(stage);





        Gdx.input.setInputProcessor(in);
         timer = System.nanoTime();

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        long now = System.nanoTime();
        long diff = now - timer;
        timer = now;
        counter += diff;
        anim.update(v);
        sprite.setTexture(anim.getTexture());
        if (counter >= 100000000){
            if (flag){
                distance++;
                degree++;
                distance1++;
                counter = 0;
                coneLight.setDistance(distance);
                coneLight.setDirection(degree);
                coneLight.setConeDegree(degree);
                coneLight1.setDistance(distance1);
                if (distance >= 34){
                    flag = false;
                }
            }else {
                distance--;
                degree--;
                distance1--;
                counter = 0;
                coneLight.setDistance(distance);
                coneLight.setDirection(degree);
                coneLight.setConeDegree(degree);
                coneLight1.setDistance(distance1);
                if (distance <= 33){
                    flag = true;
                }
            }
        }

        world.step(1/60f,4,4);
        stage.act(v);
        stage.draw();
        batch.begin();
        sprite.draw(batch);
        batch.end();

        rayHandler.setCombinedMatrix((OrthographicCamera) stage.getCamera());
        rayHandler.updateAndRender();

        guiStage.act(v);
        guiStage.draw();
        debugRenderer.render(world,stage.getCamera().combined);

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

    }

    @Override
    public void dispose() {

    }
}
