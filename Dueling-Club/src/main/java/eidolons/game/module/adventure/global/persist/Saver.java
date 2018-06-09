package eidolons.game.module.adventure.global.persist;

import eidolons.ability.InventoryTransactionManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 6/9/2018.
 */
public class Saver {

    private static final String HEAD = "SAVE";
    private static final String HERO_NODE = "HERO";
    private static final String ITEMS_NODE = "HERO";
    private static final String WORLD_NODE = "WORLD";

    public static void save() {
        //slot
        //file
        //ui?

        // custom types
        // map info
        // dynamic params - coordinates, ...

        //loading is more under question actually!

        String saveName = "Test " +
         TimeMaster.getFormattedTime(true, true) +
         ".xml";
        String path = getPath() + saveName;
        String content = getSaveContent();
        FileManager.write(content, path);
    }

    private static String getSaveContent() {
        String content = "";

        //write full type data!
        Unit hero = Eidolons.getMainHero();

        String heroData = XML_Writer.getTypeXML_Builder(hero,
         hero.getType(), true).toString();

        String itemsData = "";
        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {
            for (String substring : StringMaster.openContainer(hero.getProperty(sub))) {
                if (!StringMaster.isInteger(substring))
                    continue;
                Integer id = StringMaster.getInteger(substring);
                Obj item = hero.getGame()
                 .getObjectById((id));
                String itemData = XML_Writer.getTypeXML_Builder(item,
                 item.getType(), false).toString();

                itemData = XML_Converter.wrap(item.getName()+"_" + id, itemData);
                itemsData += itemData + "\n";
            }
        }
        itemsData = XML_Converter.wrap(ITEMS_NODE, itemsData);

        content = heroData + "\n" + itemsData;
        content = XML_Converter.wrap(HERO_NODE, content);
        content = XML_Converter.wrap(HEAD, content);
        return content;
    }

    private static String getPath() {
        return PathFinder.getSavesPath();
    }

    public static void prepareType(ObjType type) {
        for (PROPERTY prop : InventoryTransactionManager.INV_PROPS) {
            String propValue = type.getProperty(prop);
            List<String> items = StringMaster.openContainer(propValue);
            for (String item : items) {
                if (StringMaster.isInteger(item)) {
                    try {
                        propValue = StringMaster.replaceFirst(propValue, item, type.getGame()
                         .getObjectById(StringMaster.getInteger(item)).getType().getName());
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }
            type.setProperty(prop, propValue);
        }

    }
}
