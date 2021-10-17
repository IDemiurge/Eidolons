package eidolons.game.eidolon.heromake;

import eidolons.content.DC_ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

public class TextFormatHero {

    private static final String SEPARATOR = ":";
    private static final String BASE_HERO_TYPE = "BASE_HERO_TYPE";

    public void process(String text){
        ObjType baseType = DataManager.getType(BASE_HERO_TYPE, DC_TYPE.CHARS);
        /*
        apply types for race, backgrounds
         */

        for (String line : StringMaster.splitLines(text, false)) {
            if (!line.contains(SEPARATOR))
                continue;
            String[] split = line.split(SEPARATOR);
            String name = split[0];
            String value = split[1];
            apply(baseType, name, value);
        }

    }

    private void apply(ObjType baseType, String name, String value) {
        VALUE val = DC_ContentValsManager.findValue(name);
        if (val==null){
            main.system.auxiliary.log.LogMaster.log(1,"No such value: " +name);
            return;
        }
        baseType.setValue(val, value);
    }

}
