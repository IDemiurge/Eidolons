package main.test.libgdx.prototype;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 19.11.2016.
 */
public class Lightmap {
    private Map<MicroObj, Body> bodyMap;
    private World world;
    private RayHandler rayHandler;
    private float cellWidth;
    private float cellHeight;
    private Map<MicroObj, PointLight> lightMap;
    private Map<Integer, FireLightProt> fireLightProtMap;
    private static float SECOND = 1000000000;
    Box2DDebugRenderer debugRenderer;

    private void init(DequeImpl<DC_HeroObj> un, World world, RayHandler rayHandler, float cellWidth, float cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.world = world;
        this.rayHandler = rayHandler;
        this.rayHandler.setBlur(true);
        this.rayHandler.setBlurNum(15);
        this.rayHandler.setAmbientLight(Color.GRAY);
        this.rayHandler.setAmbientLight(LightingManager.ambient_light);
        this.rayHandler.setBlurNum(15);
        RayHandler.setGammaCorrection(true);
        debugRenderer = new Box2DDebugRenderer();
        lightMap = new HashMap<>();
        fireLightProtMap = new HashMap<>();
        bodyMap = new HashMap<>();
        for (int i = 0; i < un.size(); i++) {

//            PointLight pointLight = new PointLight(rayHandler, 100, Color.RED, un.getOrCreate(i).getIntParam(PARAMS.LIGHT_EMISSION) * 5, un.getOrCreate(i).getX(), un.getOrCreate(i).getY());
//            PointLight pointLight = new PointLight(rayHandler, 100, new Color(0xf7ffa8C8), un.getOrCreate(i).getIntParam(PARAMS.LIGHT_EMISSION) * 5, un.getOrCreate(i).getX(), un.getOrCreate(i).getY());
//            pointLight.setSoft(true);
//            pointLight.setSoftnessLength(50);
//            pointLight.attachToBody(body);
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.KinematicBody;
            Body body = world.createBody(bdef);

            if (un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
                body.setTransform(un.get(i).getX() * cellWidth + cellWidth / 2, un.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
                // TEMP
                FireLightProt fireLightProt = new FireLightProt(world, rayHandler,
                        un.get(i).getX() * cellWidth + cellWidth / 2, un.get(i).getY() *
                        cellHeight + cellHeight / 2, un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) * 30, 360, SECOND);
//                FireLightProt fireLightProt = new FireLightProt();
                fireLightProt.attachToBody(body);
                //TEMP END
                fireLightProtMap.put(i, fireLightProt);
            } else {
                body.setTransform(un.get(i).getX() * cellWidth + cellWidth / 2, un.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
            }


            bodyMap.put(un.get(i), body);
//            lightMap.put(un.getOrCreate(i), pointLight);


        }
//        for (int i = 0; i < un.size(); i++) {
//            if (un.getOrCreate(i).getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
//                BodyDef bdef = new BodyDef();
//                bdef.type = BodyDef.BodyType.KinematicBody;
//                Body body = world.createBody(bdef);
//                body.setTransform(un.getOrCreate(i).getX() * cellWidth, un.getOrCreate(i).getY() * cellHeight, 0);
//                PolygonShape shape = new PolygonShape();
//                shape.setAsBox(cellWidth / 20, cellHeight / 20);
//                FixtureDef fdef = new FixtureDef();
//                fdef.shape = shape;
//                body.createFixture(fdef);
//                PointLight pointLight = new PointLight(rayHandler, 100, Color.RED, un.getOrCreate(i).getIntParam(PARAMS.LIGHT_EMISSION) * 5, un.getOrCreate(i).getX(), un.getOrCreate(i).getY());
//                pointLight.attachToBody(body);
//                bodyMap.put(un.getOrCreate(i), body);
//
//            } else {
//                if (un.getOrCreate(i).getType().toString().contains("units")) {
//
//                    BodyDef bdef = new BodyDef();
//                    bdef.type = BodyDef.BodyType.KinematicBody;
//                    Body body = world.createBody(bdef);
//                    body.setTransform(un.getOrCreate(i).getX() * cellWidth + cellWidth / 2, un.getOrCreate(i).getY() * cellHeight + cellHeight / 2, 0);
//                    PolygonShape shape = new PolygonShape();
//                    shape.setAsBox(cellWidth / 2, cellHeight / 2);
//                    FixtureDef fdef = new FixtureDef();
//                    fdef.shape = shape;
//                    body.createFixture(fdef);
//                    bodyMap.put(un.getOrCreate(i), body);
//
//                }
//            }
//        }
    }

    public Lightmap(DequeImpl<DC_HeroObj> un, float cellWidth, float cellHeight) {
        World world = new World(new Vector2(0, 0), true);
        init(un, world, new RayHandler(world), cellWidth, cellHeight);
    }

    public void updatePos(MicroObj obj) {
        if (bodyMap.containsKey(obj)) {
            Coordinates c = obj.getCoordinates();
            bodyMap.get(obj).setTransform(c.getX() * cellWidth, c.getY() * cellHeight, 0);
        }
    }

    public void updateLight() {
        for (Map.Entry<Integer, FireLightProt> entry : fireLightProtMap.entrySet()) {
            entry.getValue().update();

        }
//        for (int q =0;q<fireLightProtMap.entrySet().size();q++){
//                fireLightProtMap.getOrCreate(q).update(Gdx.graphics.getDeltaTime());
//        }
        world.step(1 / 60, 4, 4);
        rayHandler.setCombinedMatrix(GameScreen.camera);
        rayHandler.updateAndRender();

//        debugRenderer.render(world,GameScreen.camera.combined);
    }

    public void updateObject(DC_HeroObj heroObj) {
//        int lightEmmi = heroObj.getIntParam(PARAMS.LIGHT_EMISSION);
//        if (lightMap.containsKey(heroObj)) {
//            lightMap.getOrCreate(heroObj).setDistance(lightEmmi * 15);
//        }
        // TODO: 12.12.2016 pointlighter around the mouse - 35 ligth emission and arround active Unite - (his emission +20) (DC_Game.game.getManager.getActiveUnit()
        //      light_emission + 20)
    }
}