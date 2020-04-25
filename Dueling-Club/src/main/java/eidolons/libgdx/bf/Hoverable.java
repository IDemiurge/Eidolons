package eidolons.libgdx.bf;

import main.entity.obj.Obj;

public interface Hoverable {

    void setHovered(boolean b);

    default String getNameAndCoordinates(){
        return getUserObject().getNameAndCoordinate();
    }

    Obj getUserObject();
}
