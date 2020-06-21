package main.content.enums.entity;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 7/20/2018.
 */
public interface OBJ_TYPE_ENUM {

    default String getName() {
            return StringMaster.format(
        // XML_Formatter.restoreXmlNodeName(
             toString());
    }
    default String name(){
        return getName();
    }
}
