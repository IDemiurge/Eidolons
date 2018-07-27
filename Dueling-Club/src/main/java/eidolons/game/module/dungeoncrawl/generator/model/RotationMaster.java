package eidolons.game.module.dungeoncrawl.generator.model;

import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.Arrays;

/**
 * Created by JustMe on 7/27/2018.
 */
public class RotationMaster {

    public static final void checkParentRotationPossible(Room parent){
        //preserve all prev links, return new exit

        parent.getRotations();


    }

    public static  Boolean[] getRotations(FACING_DIRECTION from, FACING_DIRECTION to) {
        if (from == null)
            //for default entrance! full random...
            return new Boolean[]{
             RandomWizard.random(),
             RandomWizard.random(),
             RandomWizard.random(),
             RandomWizard.random()
            };
        if (to == null)
            return new Boolean[0];
        int dif = from.getDirection().getDegrees() - to.getDirection().getDegrees();
        int turns = dif / 90;
        boolean clockwise = true;
        if (turns < 0)
            clockwise = false;
        Boolean[] bools = new Boolean[Math.abs(turns)];
        Arrays.fill(bools, clockwise);
        return bools;
    }
}
