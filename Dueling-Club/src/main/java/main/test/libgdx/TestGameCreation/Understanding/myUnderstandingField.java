package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.*;


/**
 * Created by PC on 01.11.2016.
 */
public class myUnderstandingField implements Screen {

    static World world;
   static Stage stage;
//   static myLittleTestingGUI gui;
    testplay player;
    Box2DDebugRenderer deb;
    static boolean TimeToCreate = false;
//    OrthographicCamera camera;
    Vector2 p;
    @Override
    public void show() {
        world = new World(new Vector2(0,-10),true);
        p = new Vector2();

        world.setContactListener(new MyContact());
//        gui = new myLittleTestingGUI();
        player = new testplay(world);
        deb = new Box2DDebugRenderer();
//        camera = new OrthographicCamera(20,15);
        stage = new Stage(new FitViewport(15,20));
//        stage = new Stage();
        stage.setDebugAll(true);
        stage.addActor(new Fon(world));
        stage.addActor(new Ground(world));
        stage.addActor(player);
        stage.addActor(new HitBall(world));
        stage.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                p.set(player.getX(),player.getY());
//                player.body.setTransform(x,y,0);
//                System.out.println("q");
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
//                return super.touchDown(event, x, y, pointer, button);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {

                player.body.setTransform(x,y,0);
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                player.body.setTransform(-5,-5,0);
                super.touchUp(event, x, y, pointer, button);
            }
        });

        InputMultiplexer in = new InputMultiplexer();
//        in.addProcessor(gui);
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

//        gui.act(v);
//        gui.draw();


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
