package main.entity.type;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public class ObjAtCoordinate {
    ObjType type;
    Coordinates coordinates;

    public ObjAtCoordinate(ObjType type, Coordinates coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public ObjAtCoordinate(String s, OBJ_TYPE TYPE) {
        String coord = s.split("=")[0];
        String typeName = s.split("=")[1];
        type = DataManager.getType(typeName, TYPE);
        coordinates = Coordinates.get(true, coord);
    }


    public ObjAtCoordinate(String typeName, String s, OBJ_TYPE TYPE) {
        type = DataManager.getType(typeName, TYPE);
        coordinates = Coordinates.get(true, s);
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

    public boolean isValid() {
        return type != null;
    }
}
