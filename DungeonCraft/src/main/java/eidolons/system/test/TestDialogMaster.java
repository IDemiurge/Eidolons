package eidolons.system.test;

import eidolons.content.PROPS;
import eidolons.game.core.Eidolons;
import main.content.DC_TYPE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.util.DialogMaster;

import java.util.ArrayList;
import java.util.List;

public class TestDialogMaster {

    public static boolean key(char c){
        switch (c) {
            case 'S':
                return prompt(DC_TYPE.SKILLS);
            case 'P':
            case 'p':
                return prompt(DC_TYPE.SPELLS);
            case 'K':
            case 'k':
                return prompt(DC_TYPE.PERKS);
            case 'L':
            case 'l':
                return prompt(DC_TYPE.CLASSES);
        }
        return false;
    }
        public static boolean prompt(DC_TYPE T){
        List<ObjType> full = new ArrayList<>(DataManager.getTypes(T));
//        full.removeIf(hq)

        //TODO via event HqMaster.filterTestContent(full);
        String val= DialogMaster.inputText("; Separated string...", ContainerUtils.constructEntityNameContainer(full));
        Eidolons.getMainHero().addProperty(true, prop(T), val);
        Eidolons.getMainHero().setInitialized(false);
        Eidolons.getMainHero().reset();
            return true;
        }

    private static PROPERTY prop(DC_TYPE t) {
        switch (t) {
            case SPELLS:
                return PROPS.VERBATIM_SPELLS;
            case SKILLS:
                return PROPS.SKILLS;
            case PERKS:
                return PROPS.PERKS;
            case CLASSES:
                return PROPS.CLASSES;
        }
        return null;
    }
}
