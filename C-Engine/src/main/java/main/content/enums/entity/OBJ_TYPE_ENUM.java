package main.content.enums.entity;

import main.data.xml.XML_Formatter;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 7/20/2018.
 */
public interface OBJ_TYPE_ENUM {

    default String getName() {
            return XML_Formatter.restoreXmlNodeName(StringMaster.
             getWellFormattedString(toString()));
    }
    default String name(){
        return getName();
    }
}
