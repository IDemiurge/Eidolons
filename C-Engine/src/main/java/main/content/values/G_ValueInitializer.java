package main.content.values;

import main.content.VALUE;

import static main.content.values.properties.G_PROPS.*;
public class G_ValueInitializer {

    public static void init(){
        VERSION.setDevOnly(true);

        NAME.setInputReq(VALUE.INPUT_REQ.STRING);
        // MAIN_HAND_ITEM.setDynamic(true);
        // OFF_HAND_ITEM.setDynamic(true);
        // ARMOR_ITEM.setDynamic(true);
        UNIQUE_ID.setDynamic(true);
        STATUS.setDynamic(true);
        // STATUS.setDynamic(true);
        SPELL_POOL.setDynamic(true);

        VARIABLES.setContainer(true);

        TARGETING_MODE.setDefaultValue("SINGLE");
    }
}
