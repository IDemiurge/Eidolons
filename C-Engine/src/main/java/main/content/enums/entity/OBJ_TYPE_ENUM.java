package main.content.enums.entity;

import main.data.xml.XML_Formatter;

/**
 * Created by JustMe on 7/20/2018.
 */
public interface OBJ_TYPE_ENUM {

    default String getName() {
            return //StringMaster.getWellFormattedString
             (XML_Formatter.restoreXmlNodeName(
             toString()));
    }
    default String name(){
        return getName();
    }
}
