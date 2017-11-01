package main.data;

import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 9/10/2017.
 */
public interface GenericItemGenerator {

    ObjType getOrCreateItemType(String typeName, OBJ_TYPE type);

    ObjType generateItem(boolean weapon, QUALITY_LEVEL quality,
                         MATERIAL material, ObjType type);

    void init();
}
