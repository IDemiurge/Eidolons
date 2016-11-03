package main.test.libgdx;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.GdxRuntimeException;


/**
 * Created by PC on 30.10.2016.
 */
public class LessonBox2d implements Screen {
 World world;
   float GRAVITY = -10;
   Box2DDebugRenderer debugRenderer;
   OrthographicCamera camera;
   Body rectangle;
   Body place;
   Body circle;
   Body circle1;
   float x_For_Body = 10;
   float y_For_Body = 7.5f;
   float viewPortWidth = 20;
   float viewPortHeight = 15;
    @Override
    public void show() {
        try {
            debugRenderer = new Box2DDebugRenderer();
            world = new World(new Vector2(0,GRAVITY),true);

            camera = new OrthographicCamera(viewPortWidth,viewPortHeight);
            camera.position.set(new Vector2(viewPortWidth/2,viewPortHeight/2),0);
            createRect();
            createCircle(rectangle.getPosition().x-1,rectangle.getPosition().y-2);
            createCircle1(rectangle.getPosition().x+3.5f,rectangle.getPosition().y-2);
            RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.bodyA = rectangle;
            revoluteJointDef.bodyB = circle;
            revoluteJointDef.localAnchorA.set(new Vector2(-0.5f,-1.8f));
            RevoluteJointDef revoluteJointDef1 = new RevoluteJointDef();
            revoluteJointDef1.bodyA = rectangle;
            revoluteJointDef1.bodyB = circle1;
            revoluteJointDef1.localAnchorA.set(new Vector2(+3.5f,-1.5f));
//            revoluteJointDef.localAnchorB.set(new Vector2(0,1));
            world.createJoint(revoluteJointDef);
            world.createJoint(revoluteJointDef1);
            createEntity();
        }catch ( GdxRuntimeException e){e.printStackTrace();
        }catch ( ExceptionInInitializerError e){e.printStackTrace();
        }


    }
    @Override
    public void render(float v) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        camera.update();
      debugRenderer.render(world,camera.combined);
       world.step(1/60f,4,4);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            rectangle.applyForceToCenter(new Vector2(-100,0),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            rectangle.applyForceToCenter(new Vector2(+100,0),true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            rectangle.applyForceToCenter(new Vector2(0,+1000),true);
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

    }

    @Override
    public void dispose() {

    }

    public void createRect(){
       BodyDef bDef = new BodyDef();
       bDef.type = BodyDef.BodyType.DynamicBody;
       bDef.position.set(x_For_Body,y_For_Body);
       rectangle = world.createBody(bDef);
       FixtureDef fDef = new FixtureDef();
//       fDef.friction шероховатость поверхности
//       fDef.density - масса
//       fDef.restitution - прыгучесть
//         fDef.shape - форма
      PolygonShape shape = new PolygonShape();
        Vector2[] verticles = new Vector2[7];
        verticles[0] = new Vector2(4,-1);
        verticles[1] = new Vector2(4.5f,0);
        verticles[2] = new Vector2(1.5f,0.5f);
        verticles[3] = new Vector2(1.3f,2);
        verticles[4] = new Vector2(-1,2);
        verticles[5] = new Vector2(-1.4f,0);
        verticles[6] = new Vector2(-1,-1);
        shape.set(verticles);
       fDef.shape = shape;
        fDef.friction = 0.0f;
       fDef.density = 2;
        fDef.restitution = 0.3f;
       rectangle.createFixture(fDef);
//
//        CircleShape circleShape = new CircleShape();
//
//        circleShape.setRadius(0.75f);
//        circleShape.setPosition(new Vector2(-1,-2));
//        fDef.shape = circleShape;
//        fDef.density=0.5f;
//        fDef.friction = 0.2f;
//        fDef.restitution = 0.6f;
//        rectangle.createFixture(fDef);
//        circleShape.setPosition(new Vector2(3.5f,-2));
//        fDef.shape = circleShape;
//        rectangle.createFixture(fDef);

    }
    public void createCircle(float x,float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        bodyDef.fixedRotation = false;
        circle = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(0.75f);
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0.6f;
        circle.createFixture(fixtureDef);
    }
    public void createCircle1(float x,float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        bodyDef.fixedRotation = false;
        circle1 = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(0.75f);
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0.6f;
        circle1.createFixture(fixtureDef);
    }
    public void createEntity(){
       BodyDef bDef = new BodyDef();
       bDef.type = BodyDef.BodyType.StaticBody;
       bDef.position.set(0,0);
       place = world.createBody(bDef);
       FixtureDef fDef = new FixtureDef();
//       fDef.friction шероховатость поверхности
//       fDef.density - масса
//       fDef.restitution - прыгучесть
//         fDef.shape - форма
      ChainShape shape = new ChainShape();
       shape.createChain(new Vector2[]{new Vector2(0,15),new Vector2(3,0),new Vector2(17,0),new Vector2(20,15)});
       fDef.shape = shape;
        fDef.friction = 5;
       fDef.density = 2;
       place.createFixture(fDef);
    }
}
