package elements.exec.targeting.area;

import elements.content.enums.FieldConsts;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static elements.content.enums.FieldConsts.*;
import static elements.content.enums.FieldConsts.Cell.*;

/**
 * Created by Alexander on 10/20/2023
 */
public class CellSets {
    public static final  Cell[] flanks = {
            Top_Flank_Enemy,
            Top_Flank_Player,
            Bottom_Flank_Enemy,
            Bottom_Flank_Player,
    };
    public static final  Cell[] mainArea = {
            Front_Enemy_1,
            Front_Enemy_2,
            Front_Enemy_3,
            Back_Enemy_1,
            Back_Enemy_2,
            Back_Enemy_3,
            Front_Player_1,
            Front_Player_2,
            Front_Player_3,
            Back_Player_1,
            Back_Player_2,
            Back_Player_3};
    public static final Set<Cell> mainAreaSet = toSet(mainArea);


    public static final Cell[] allyArea = {
            Front_Player_1,
            Front_Player_2,
            Front_Player_3,
            Back_Player_1,
            Back_Player_2,
            Back_Player_3,
    };
    public static final Set<Cell> allyAreaSet =toSet(allyArea);

    public static final Cell[] allyFront = {
            Front_Player_1,
            Front_Player_2,
            Front_Player_3,
    };
    public static final Set<Cell> allyFrontSet =toSet(allyFront);

    public static final Cell[] allyBack = {
            Back_Player_1,
            Back_Player_2,
            Back_Player_3,
    };
    public static final Set<Cell> allyBackSet =toSet(allyBack);

    public static final Cell[] enemyArea = {
            Front_Enemy_1,
            Front_Enemy_2,
            Front_Enemy_3,
            Back_Enemy_1,
            Back_Enemy_2,
            Back_Enemy_3,
    };
    public static final Set<Cell> enemyAreaSet =toSet(enemyArea);

    public static final Cell[] enemyFront = {
            Front_Enemy_1,
            Front_Enemy_2,
            Front_Enemy_3,
    };
    public static final Set<Cell> enemyFrontSet =toSet(enemyFront);

    public static final Cell[] enemyBack = {
            Back_Enemy_1,
            Back_Enemy_2,
            Back_Enemy_3,
    };
    public static final Set<Cell> enemyBackSet =toSet(enemyBack);

    public static Cell[] front(Boolean ally) {
        return ally ? allyFront : enemyFront;
    }
    public static Cell[] back(Boolean ally) {
        return ally ? allyBack : enemyBack;
    }
    public static Cell[] area(Boolean ally) {
        return ally ? allyArea : enemyArea;
    }


    public static Set<Cell> frontSet(Boolean ally) {
        return ally ? allyFrontSet : enemyFrontSet;
    }
    public static Set<Cell> backSet(Boolean ally) {
        return ally ? allyBackSet : enemyBackSet;
    }
    public static Set<Cell> areaSet(Boolean ally) {
        return ally ? allyAreaSet : enemyAreaSet;
    }








    public static Set<Cell> toSet(Cell... positions) {
        return Arrays.stream(positions).collect(Collectors.toSet());
    }

    public static Cell rear(Boolean ally) {
        return ally ? Rear_Player : Rear_Player;
    }
}
