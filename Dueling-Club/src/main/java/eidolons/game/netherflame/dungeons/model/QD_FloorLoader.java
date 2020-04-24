package eidolons.game.netherflame.dungeons.model;

import main.data.xml.XML_Converter;
import main.system.data.DataUnit;
import org.w3c.dom.Document;

import static eidolons.game.netherflame.dungeons.QD_Enums.FloorProperty;
import static eidolons.game.netherflame.dungeons.QD_Enums.ModuleProperty;

public class QD_FloorLoader   {

    public DataUnit<ModuleProperty> createModuleData(String contents) {
        Document doc = XML_Converter.getDoc(contents);
//        XmlNodeMaster.findNodeText()
        for (ModuleProperty value : ModuleProperty.values()) {
            switch (value) {
                case size:
                    break;
                case dimension:
                    break;
                case type:
                    break;
                case location:
                    break;
                case elevation:
                    break;
            }
        }
        return null;
    }

    public DataUnit<FloorProperty> createFloorData(String contents) {
        return null;
    }
}
