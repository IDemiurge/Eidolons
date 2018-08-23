package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.Bools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/27/2018.
 */
public class RotationMaster {

    public static Boolean[] getRandomPossibleParentRotations(Room parent) {
        List<Boolean[]> list = getParentRotationsPossible(parent);
        return list.get(RandomWizard.getRandomListIndex(list));
    }

    public static Boolean[] getRandomPossibleParentRotations(FACING_DIRECTION entrance,
                                                             EXIT_TEMPLATE template) {

        List<Boolean[]> list = getParentRotationsPossible(entrance, ExitMaster.getExits(template));
        if (ListMaster.isNotEmpty(list))
            return list.get(RandomWizard.getRandomListIndex(list));
        return new Boolean[0];
    }


    public static List<Boolean[]> getParentRotationsPossible(Room parent) {
        return getParentRotationsPossible(parent.getEntrance(), parent.getExits());
    }

    private static List<Boolean[]> getParentRotationsPossible(FACING_DIRECTION entrance,
                                                              FACING_DIRECTION[] exits) {
        //preserve all prev links, return new exit
        if (entrance==null )
            return null;
        entrance = entrance.flip();
        List<Boolean[]> list = new ArrayList<>();
        for (Boolean clockwise : Bools.TRUE_FALSE) {
            for (int i = 1; i < 4; i++) {
                Boolean[] rotations = new Boolean[i];
                Arrays.fill(rotations, clockwise);
                FACING_DIRECTION[] newExits = getRotatedExits(rotations, exits);
                for (FACING_DIRECTION exit : newExits) {
                    if (exit == entrance) {
                        list.add(rotations);
                    }
                }
            }
        }

        return list;
    }

    public static Boolean[] getRotations(FACING_DIRECTION from, FACING_DIRECTION to) {
        if (from == null) {
            if (!isRotateNull())
                return new Boolean[0];
            //for default entrance! full random...
            return new Boolean[]{
             RandomWizard.random(),
             RandomWizard.random(),
             RandomWizard.random(),
             RandomWizard.random()
            };
        }
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

    private static boolean isRotateNull() {
        return true;
    }

    public static FACING_DIRECTION[] getRotatedExits(Boolean[] rotated, FACING_DIRECTION[] exits) {
        return Arrays.stream(exits).map(exit -> rotate(exit, rotated))
         .collect(Collectors.toList()).toArray(new FACING_DIRECTION[exits.length]);
    }

    public static FACING_DIRECTION rotate(FACING_DIRECTION exit, Boolean[] rotated) {
        FACING_DIRECTION newExit = exit;
        for (Boolean clockwise : rotated) {
            newExit = FacingMaster.rotate(newExit, clockwise);
        }
        return newExit;
    }

}
