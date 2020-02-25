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

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof ZCoordinates) {
            ZCoordinates z1 = this;
            ZCoordinates z2 = (ZCoordinates) arg0;
            if (z1.z != z2.z) {
                return false;
            }
        }
        return super.equals(arg0);
    }
}
