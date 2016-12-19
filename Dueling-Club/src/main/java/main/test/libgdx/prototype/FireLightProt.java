package main.test.libgdx.prototype;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

/**
 * Created by PC on 29.11.2016.
 */
public class FireLightProt {
    float timeCounter;
    long timePassed;
    float changesp;
    float randomTimeToAdd;
    ConeLight pointLight;
    ConeLight pointLight_test;
    float PERCENT = 0.1f;
    int AMOUNT_OF_RAYS = 100;
    float amountOfPercentFromDistance;
    float DISTANCE;
    float DEGREE;
    float lengthFinal;

    public FireLightProt(World world, RayHandler rayHandler, int x, int y, int distance, float degree, float changeSpeed) {
//        pointLight = new ConeLight(rayHandler, AMOUNT_OF_RAYS, Color.RED, distance,degree, x, y);
        pointLight = new ConeLight(rayHandler, AMOUNT_OF_RAYS, new Color(0xc32a2aff), distance, x, y, 90, degree);
        pointLight.setSoft(false);
//        pointLight.setSoftnessLength(-50f);
        pointLight_test = new ConeLight(rayHandler, AMOUNT_OF_RAYS, new Color(0xff4b31ff), distance / 3, x, y, 90, degree);
        pointLight_test.setSoft(false);


        changesp = changeSpeed;
        Random random = new Random((long) changeSpeed * 2);
        randomTimeToAdd = random.nextFloat() - changeSpeed;
        amountOfPercentFromDistance = distance * PERCENT;
        DEGREE = degree;
        DISTANCE = distance;
        timeCounter = 0;

    }

    public void update(float v) {
        timeCounter += v;
//        System.out.println("Timecounter is " + timeCounter);
        if (timeCounter >= changesp + randomTimeToAdd) {
//            System.out.println("CREATED NEW RANDOM ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Random currentRandom = new Random();
            int number = 1 + Math.abs(currentRandom.nextInt() % 10);
//            System.out.println(" Number is " + number);
            float lengthtoAdd = (DISTANCE / 100) * number;
            lengthFinal = lengthtoAdd + DISTANCE;
            Random random = new Random();
            randomTimeToAdd = Math.abs(random.nextFloat()) % changesp;
            timeCounter = 0;
        }
        Random random = new Random();
        int diff = 1 + random.nextInt() % 5;
        if (pointLight.getDistance() <= lengthFinal) {

            pointLight.setDistance(pointLight.getDistance() + diff);
            pointLight_test.setDistance(pointLight_test.getDistance() + diff);
        }
        if (pointLight.getDistance() > lengthFinal) {
            pointLight.setDistance(pointLight.getDistance() - diff);
            pointLight_test.setDistance(pointLight_test.getDistance() - diff);
        }


//            if (pointLight.getDistance() <= DISTANCE - amountOfPercentFromDistance) {
//                Random currentRandom = new Random(4);
//                float number = 1 + currentRandom.nextFloat();
//                float lengthtoAdd = (DISTANCE / 100) * number;
//                pointLight.setDistance(DISTANCE + lengthtoAdd);
//            }
//            else{
//                if (pointLight.getDistance() >= DISTANCE + amountOfPercentFromDistance) {
//                    Random currentRandom = new Random(4);
//                    float number = 1 + currentRandom.nextFloat();
//                    float lengthtoAdd = (DISTANCE / 100) * number;
//                    pointLight.setDistance(DISTANCE - lengthtoAdd);
//                } else{
//                    if (pointLight.getDistance() > DISTANCE - amountOfPercentFromDistance){
//                        if (pointLight.getDistance() < DISTANCE + amountOfPercentFromDistance){
//                            Random random = new Random();
//                            float whatAction = Math.abs(random.nextFloat());
//                            if (whatAction%2 == 0){
//                                Random currentRandom = new Random(4);
//                                float number = 1 + currentRandom.nextFloat();
//                                float lengthtoAdd = (DISTANCE / 100) * number;
//                                pointLight.setDistance(DISTANCE + lengthtoAdd);
//                            }else {
//                                Random currentRandom = new Random(4);
//                                float number = 1 + currentRandom.nextFloat();
//                                float lengthtoAdd = (DISTANCE / 100) * number;
//                                pointLight.setDistance(DISTANCE - lengthtoAdd);
//                            }
//                        }
//                    }
//                }
//            }

//            System.out.println(" Distance is " + pointLight.getDistance() + " || lengthFinal is " + lengthFinal);
//            System.out.println("Total time for next time to change effect is:" +(changesp+randomTimeToAdd));

    }
}
