package eidolons.system.math.roll;

import main.content.enums.GenericEnums;

public class Dice {
    public final Integer dice;
    public final GenericEnums.DieType type;

    public Dice(Integer dice, GenericEnums.DieType type) {
        this.dice = dice;
        this.type = type;
    }
}
