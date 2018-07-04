package eidolons.macro.global.persist;

import eidolons.ability.InventoryTransactionManager;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import main.content.VALUE;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 6/9/2018.
 *
 * id sync will be broken by:
 * New version of the game
 * any init() before - even if in original game it happened after
 *
 * solution
 * specify item_slot/container
 * use the save-id's while loading, then re-assign
 *
 * id*=10000
 * => custom map
 *
 * 0) create hero
 * 1) apply hero type (including id's)
 * 2) init item maps
 * 3) reset()
 * when loaded hero inits an item ...
 * we will look up custom map for items, where save-id's are mapped to real items with new ids
 * update container props
 *
 *
 * ensure that things are not cycled
 *
 *
 *
 */
public class Saver {

    public static final String HEAD = "SAVE";
    public static final String HERO_NODE = "HERO";
    public static final String ITEMS_NODE = "ITEMS";
    public static final String WORLD_NODE = "WORLD";

    public static String save() {
        //slot
        //file
        //ui?

        // custom types
        // map info
        // dynamic params - coordinates, ...

        //loading is more under question actually!

        String saveName =getSaveName();
        String path = getPath() + saveName;
        String content = getSaveContent();
        FileManager.write(content, path);
        return path;
    }

    private static String getSaveName() {
        return Eidolons.getMainHero().getType().getName()+
         ".xml";
    }

    private static String getSaveContent() {
        String content = "";

        //write full type data!
        Unit hero = Eidolons.getMainHero();

        String heroData = XML_Writer.getTypeXML_Builder(hero,
         null, hero.getType(), hero.getOriginalType(), true, getExceptionValues()).toString();

        String fullItemsData = "";
        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {
            String itemsData = "";
            for (String substring : StringMaster.openContainer(hero.getProperty(sub))) {
                if (!StringMaster.isInteger(substring))
                    continue;
                Integer id = StringMaster.getInteger(substring);
                DC_HeroItemObj item = (DC_HeroItemObj) hero.getGame()
                 .getObjectById((id));
                String itemData = XML_Writer.getTypeXML_Builder(item,
                 null, item.getType(), item.getOriginalType(), false).toString();

                itemData = XML_Converter.wrap(item.getName() + "_" + id, itemData);
                itemsData += itemData + "\n";
            }
            itemsData = XML_Converter.wrap(sub.name(), itemsData);
            fullItemsData += itemsData + "\n";
        }
        fullItemsData = XML_Converter.wrap(ITEMS_NODE, fullItemsData);

        content = heroData + "\n" + fullItemsData;
        content = XML_Converter.wrap(HERO_NODE, content);
        content = XML_Converter.wrap(HEAD, content);
        return content;
    }

    private static VALUE[] getExceptionValues() {
        return new VALUE[]{
         PARAMS.ATTACK,
         PARAMS.DEFENSE,
        };
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

    public static void autosave() {
        save();
    }
}
