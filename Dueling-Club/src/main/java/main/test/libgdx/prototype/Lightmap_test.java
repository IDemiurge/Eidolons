package main.test.libgdx.prototype;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import main.content.PARAMS;
import main.entity.obj.MicroObj;
import main.system.datatypes.DequeImpl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by PC on 19.11.2016.
 */
public class Lightmap_test {
    static Map<MicroObj, Body> bodyMap;


    public Lightmap_test(DequeImpl<MicroObj> un, World world, RayHandler rayHandler) {
        bodyMap = new Map<MicroObj, Body>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Body get(Object key) {
                return null;
            }

            @Override
            public Body put(MicroObj key, Body value) {
                return null;
            }

            @Override
            public Body remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends MicroObj, ? extends Body> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<MicroObj> keySet() {
                return null;
            }

            @Override
            public Collection<Body> values() {
                return null;
            }

            @Override
            public Set<Entry<MicroObj, Body>> entrySet() {
                return null;
            }
        };
        for (int i = 0;i < un.size();i++){
//            System.out.println("===================");
//            System.out.println(un.get(i).getName());
//            System.out.println(un.get(i).getIntParam(PARAMS.LIGHT_EMISSION));
//            if (un.get(i).getName().equalsIgnoreCase("Torch")){
            if (un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) > 0){
//                System.out.println("Torch Code DETECTED");
//                System.out.println("Coords: " + un.get(i).getX() + " || " + un.get(i).getY() + " || " + un.get(i).getCoordinates());
//                System.out.println("Add Body");
//                System.out.println("Add PointLighther and Attach it to the body");
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.KinematicBody;
                Body body = world.createBody(bdef);
                body.setTransform(un.get(i).getX()*132,un.get(i).getY()*113,0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(132/20,113/20);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
                PointLight pointLight = new PointLight(rayHandler,100, Color.RED,un.get(i).getIntParam(PARAMS.LIGHT_EMISSION)*5,un.get(i).getX(),un.get(i).getY());
                pointLight.attachToBody(body);
                bodyMap.put(un.get(i),body);
//                System.out.println("Created a body for Torch with a point light");


            }else {
//                if (!un.get(i).getName().equalsIgnoreCase("Stone Wall")){
                    if (un.get(i).getType().toString().contains("units")){
//                    System.out.println("Unit detected - need only to create a body");
                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.KinematicBody;
                    Body body = world.createBody(bdef);
                    body.setTransform(un.get(i).getX()*132+132/2,un.get(i).getY()*113+113/2,0);
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(132/2,113/2);
                    FixtureDef fdef = new FixtureDef();
                    fdef.shape = shape;
                    body.createFixture(fdef);
//                    System.out.println("Created a body");
//                    System.out.println("Position on: " + un.get(i).getX() + "||" + un.get(i).getY());
                        bodyMap.put(un.get(i),body);
//                    System.out.println("===================");
                }
            }
        }
    }
    public void move(MicroObj obj,float x, float y ){
        if (bodyMap.containsKey(obj)) {
            bodyMap.get(obj).setTransform(x,y,0);
        }
    }
}
//TODO - вынести в переменные класса размеры спрайта для клетки
