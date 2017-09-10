package main.data;

import main.content.OBJ_TYPE;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 9/10/2017.
 */
public interface GenericItemGenerator {

    ObjType getOrCreateItemType(String typeName, OBJ_TYPE type);

    void init();
}
