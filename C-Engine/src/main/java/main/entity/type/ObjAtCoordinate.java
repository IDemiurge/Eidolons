package main.entity.type;

import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public class ObjAtCoordinate {
    ObjType type;
    Coordinates coordinates;

    public ObjAtCoordinate(ObjType type, Coordinates coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return coordinates.toString() + "=" + type.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjAtCoordinate) {
            ObjAtCoordinate obj2 = (ObjAtCoordinate) obj;
            return getCoordinates().equals(obj2.getCoordinates())
             && getType().equals(obj2.getType());
        }
        if (obj instanceof Obj) {
            Obj obj2 = (Obj) obj;
            return getCoordinates().equals(obj2.getCoordinates())
             && getType().equals(obj2.getType());
        }
        return super.equals(obj);
    }

    public ObjType getType() {
        return type;
    }

    public void setType(ObjType type) {
        this.type = type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
