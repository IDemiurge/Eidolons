package main.game.bf.directions;

/**
 * Created by Giskard on 6/9/2018.
 */
public enum UNIT_DIRECTION {
    AHEAD(0),
    AHEAD_LEFT(45),
    AHEAD_RIGHT(-45),
    LEFT(90),
    RIGHT(-90),
    BACKWARDS(180),
    BACKWARDS_LEFT(135),
    BACKWARDS_RIGHT(-135),;

    private int degrees;

    UNIT_DIRECTION(int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }
}
