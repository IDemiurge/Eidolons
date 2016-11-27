package main.test.libgdx.prototype;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.*;
import main.content.PARAMS;
import main.entity.obj.MicroObj;
import main.system.datatypes.DequeImpl;

import java.util.Map;

/**
 * Created by PC on 19.11.2016.
 */
public class Lightmap_test {
    static Map<MicroObj, Body> bodyMap;


    public Lightmap_test(DequeImpl<MicroObj> un, World world, RayHandler rayHandler) {
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
        for (Map.Entry<MicroObj,Body> entry : bodyMap.entrySet() ){
            MicroObj current = entry.getKey();
            if (current.equals(obj)){
                entry.getValue().setTransform(x,y,0);

            }
        }
    }
}
//TODO - вынести в переменные класса размеры спрайта для клетки
