package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by PC on 01.11.2016.
 */
public class MainField implements Screen {
    World world;
    Stage stage;
    Player player;
    private Box2DDebugRenderer renderer;
    @Override
    public void show() {
        world = new World(new Vector2(0,-10),true);
        stage = new Stage(new FitViewport(15,20));
        stage.setDebugAll(true);
        renderer = new Box2DDebugRenderer();
        player = new Player(world);
        stage.addActor(player);
        stage.addActor(new Ball(world));
        stage.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("qq");
                player.setPosition(x,y);
//                return super.touchDown(event, x, y, pointer, button);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                player.setPosition(x,y);
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                player.setPosition(-2,-2);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        renderer.render(world,stage.getCamera().combined);
        world.step(1/60f,4,4);
        stage.draw();
        System.out.println(player.getX() + " | " + player.getY() + " || " +player.body.getPosition());
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
