package math.geom;

public class Geom2D {

    public final int width, height;
    public final int centerX, centerY;

    public Geom2D(int width, int height) {
        this.width = width;
        this.height = height;
        centerX = width/2;
        centerY = height/2;
    }

    public int fromCenterX(int offset, boolean side){
        if (side){
            return centerX-offset;
        }
        return centerX+offset;
    }
    public int fromCenterX(int offset, int compWidth){
        if (offset < 0)
            return centerX+offset - compWidth;
        return centerX+offset;
    }
}
