package main.test.libgdx.TestGameCreation.Understanding;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * Created by PC on 01.11.2016.
 */
public class myUnderstandingField implements Screen {

    static World world;
   static Stage stage;
    static float View_Width = 20;
    static float View_Height = 15;

   static myLittleTestingGUI gui;

    testplay player;
    Box2DDebugRenderer deb;
    static boolean TimeToCreate = false;
    RayHandler rayHandler;

//    OrthographicCamera camera;
    Vector2 p;
    @Override
    public void show() {
        world = new World(new Vector2(0,-10),true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(Color.SKY);
        rayHandler.setAmbientLight(0.1f);
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(5);
        rayHandler.setGammaCorrection(true);
        PointLight pointLight = new PointLight(rayHandler,150,Color.BLUE,10,10,10);
        ConeLight coneLight = new ConeLight(rayHandler,75,Color.RED,20,10,10,270,45);
        p = new Vector2();
        gui = new myLittleTestingGUI();
        world.setContactListener(new MyContact());
        gui = new myLittleTestingGUI();
        player = new testplay(world);
        pointLight.attachToBody(player.body);
        deb = new Box2DDebugRenderer();
        stage = new Stage(new FitViewport(View_Width,View_Height));
        stage.setDebugAll(true);
        stage.addActor(new Fon(world));
        stage.addActor(new Ground(world));
        stage.addActor(player);
        stage.addActor(new HitBall(world));
        stage.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                p.set(player.getX(),player.getY());
                if (player.getActions().size == 0) {
                    player.addAction(Actions.moveTo(x - player.getWidth() / 2, y - player.getHeight() / 2, p.dst(x, y) / 2));
                }
                else
                {
                    while (player.getActions().size >=1){
                        player.removeAction(player.getActions().first());
                    }
                    player.addAction(Actions.moveTo(x - player.getWidth() / 2, y - player.getHeight() / 2, p.dst(x, y) / 3));

                }
                System.out.println(player.getActions().size);
                return true;
            }

        });

        InputMultiplexer in = new InputMultiplexer();
        in.addProcessor(gui);
        in.addProcessor(stage);

        Gdx.input.setInputProcessor(in);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));


        if (GameScore.statement == Statements.PLAY){
            world.step(1/60f,4,4);
            stage.act(v);
        }
        stage.draw();
        rayHandler.setCombinedMatrix((OrthographicCamera) stage.getCamera());
        rayHandler.updateAndRender();
        gui.act(v);
        gui.draw();


        if (TimeToCreate){
            stage.addActor(new HitBall(world));
            TimeToCreate = false;
        }
        deb.render(world,stage.getCamera().combined);



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
