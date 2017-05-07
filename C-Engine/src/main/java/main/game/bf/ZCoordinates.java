package main.game.bf;

public class ZCoordinates extends Coordinates {
    public ZCoordinates(int x, int y, int z) {
        super(x, y);
        this.setZ(z);
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
