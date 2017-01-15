package main.libgdx.anims.particles.lighting;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
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
    float DISTANCEBIGGER;
    float DISTANCESMALLER;
    float DEGREE;
    float lengthFinal;
    float now;
    float past;
    static float alphaBigger;
    static float alphaSmaller;
    static Color biggerLight;
    static Color smallerLight;

    public void setDISTANCE(float DISTANCE) {
        this.DISTANCEBIGGER = DISTANCE;
        this.DISTANCESMALLER = DISTANCEBIGGER/3;
    }


    public FireLightProt(World world, RayHandler rayHandler, float x, float y, int distance, float degree, float changeSpeed) {
        biggerLight = new Color(0xffe9a3AF);
        smallerLight = new Color(0xf7ffa832);
        alphaBigger = biggerLight.a;
        alphaSmaller = smallerLight.a;
        DISTANCEBIGGER = distance;
        DISTANCESMALLER = DISTANCEBIGGER/3;

//        pointLight = new ConeLight(rayHandler, AMOUNT_OF_RAYS, Color.RED, distance,degree, x, y);
        pointLight = new ConeLight(rayHandler, AMOUNT_OF_RAYS, biggerLight, distance, x, y, 90, degree);
//        pointLight.setSoft(true);
//        pointLight.setSoftnessLength(50f);
        pointLight_test = new ConeLight(rayHandler, AMOUNT_OF_RAYS, smallerLight, distance / 3, x, y, 90, degree);

//    pointLight_test.setSoft(true);
//        pointLight_test.setSoftnessLength(50f);


        changesp = changeSpeed;
        Random random = new Random((long) changeSpeed * 2);
        randomTimeToAdd = random.nextFloat() - changeSpeed;
        amountOfPercentFromDistance = distance * PERCENT;
        DEGREE = degree;
        DISTANCEBIGGER = distance;
        past = System.nanoTime();
        timeCounter = 0;

    }

    public void update() {
        now = System.nanoTime();
        float diff = now - past;
        timeCounter += diff;
        past = now;
        pointLight.setColor(biggerLight);
        pointLight_test.setColor(smallerLight);
//        System.out.println("V = " + v + " || timeCounter = " + timeCounter);
//        System.out.println("Timecounter is " + timeCounter);
        if (timeCounter >= changesp + randomTimeToAdd) {
//            System.out.println("CREATED NEW RANDOM ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Random currentRandom = new Random();
            int number = 1 + Math.abs(currentRandom.nextInt() % 10);
            Random random = new Random();
            randomTimeToAdd = Math.abs(random.nextFloat()) % changesp;
            timeCounter = 0;
        }
//        Random random = new Random();
//        int diff = 1 + random.nextInt() % 5;
//        if (pointLight.getDistance() <= lengthFinal) {
//
//            pointLight.setDistance(pointLight.getDistance() + diff);
//            pointLight_test.setDistance(pointLight_test.getDistance() + diff);
//        }
//        if (pointLight.getDistance() > lengthFinal) {
//            pointLight.setDistance(pointLight.getDistance() - diff);
//            pointLight_test.setDistance(pointLight_test.getDistance() - diff);
//        }


        if (pointLight.getDistance() < DISTANCEBIGGER - amountOfPercentFromDistance) {
            Random currentRandom = new Random();
            float number = 1 + currentRandom.nextFloat() % 4;
            float lengthtoAddBig = (DISTANCEBIGGER / 100) * number;
            float lengthtoAddSmall = (DISTANCESMALLER / 100) * number;
            pointLight.setDistance(DISTANCEBIGGER + lengthtoAddBig);
            pointLight_test.setDistance(DISTANCESMALLER + lengthtoAddSmall);
        }
        if (pointLight.getDistance() > DISTANCEBIGGER + amountOfPercentFromDistance) {
            Random currentRandom = new Random();
            float number = 1 + currentRandom.nextFloat() % 4;
            float lengthtoAddBig = (DISTANCEBIGGER / 100) * number;
            float lengthtoAddSmall = (DISTANCESMALLER / 100) * number;
            pointLight.setDistance(DISTANCEBIGGER - lengthtoAddBig);
            pointLight_test.setDistance(DISTANCESMALLER - lengthtoAddSmall);
        }
        if (pointLight.getDistance() >= DISTANCEBIGGER - amountOfPercentFromDistance) {
            if (pointLight.getDistance() <= DISTANCEBIGGER + amountOfPercentFromDistance) {
//                    System.out.println("Distance is in between");
                Random random = new Random();
                int whatAction = Math.abs(random.nextInt());
                if (whatAction % 2 == 0) {
                    Random currentRandom = new Random();
                    float number = 1 + currentRandom.nextFloat() % 4;
                    float lengthtoAddBig = (DISTANCEBIGGER / 100) * number;
                    float lengthtoAddSmall = (DISTANCESMALLER / 100) * number;
                    pointLight.setDistance(DISTANCEBIGGER + lengthtoAddBig);
                    pointLight_test.setDistance(DISTANCESMALLER + lengthtoAddSmall);
                } else {
                    Random currentRandom = new Random();
                    float number = 1 + currentRandom.nextFloat() % 4;
                    float lengthtoAddBig = (DISTANCEBIGGER / 100) * number;
                    float lengthtoAddSmall = (DISTANCESMALLER / 100) * number;
                    pointLight.setDistance(DISTANCEBIGGER - lengthtoAddBig);
                    pointLight_test.setDistance(DISTANCESMALLER - lengthtoAddSmall);
                }
            }
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
//                                System.out.println("GOT HERE");
//                            }
//                        }
//                    }
//                }
//            }

//            System.out.println(" Distance is " + pointLight.getDistance() + " || lengthFinal is " + lengthFinal);
//            System.out.println("Total time for next time to change effect is:" +(changesp+randomTimeToAdd));

    }

    public void attachToBody(Body body) {
        pointLight.attachToBody(body);
        pointLight_test.attachToBody(body);
    }

    public static void setBiggerAlpha(float alpha) {
        biggerLight.a = alpha;
        alphaBigger = alpha;

    }

    public static void setSmallerAlpha(float alpha) {
        smallerLight.a = alpha;
        alphaSmaller = alpha;
    }

    public static float getAlphaBigger() {
        return alphaBigger;
    }

    public static float getAlphaSmaller() {
        return alphaSmaller;
    }

    public void dispose() {
        pointLight.remove();
        pointLight_test.remove();
    }
}
