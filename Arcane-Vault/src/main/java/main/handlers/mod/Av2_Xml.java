package main.handlers.mod;

import main.content.DC_TYPE;
import main.data.xml.XML_Reader;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.v2_0.AV2;

public class Av2_Xml extends XML_Reader {

    private static Av2_Xml instance= new Av2_Xml();

    public static Av2_Xml getInstance() {
        return instance;
    }

    public static void addTab(){
        Class<?> ENUM_CLASS = DC_TYPE.class;
                String toAdd = ListChooser.chooseEnum(ENUM_CLASS,
                        ListChooser.SELECTION_MODE.MULTIPLE);

                for (String sub : ContainerUtils.open(toAdd)) {
                    AV2.getMainBuilder().getTabBuilder().addTab(
                            ENUM_CLASS, sub);
                }
    }

}
