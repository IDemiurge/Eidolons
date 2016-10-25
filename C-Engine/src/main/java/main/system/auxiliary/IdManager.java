package main.system.auxiliary;

import main.entity.Entity;
import main.entity.type.ObjType;

public interface IdManager {

    Integer getNewId();

    Integer getNewTypeId();

    void setSpecialTypeId(ObjType type, int id);

    void setSpecialId(Entity e, int id);

}
