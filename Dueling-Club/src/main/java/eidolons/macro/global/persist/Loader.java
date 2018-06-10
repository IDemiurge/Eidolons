package eidolons.macro.global.persist;

import eidolons.ability.InventoryTransactionManager;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.DequeImpl;
import main.system.launch.TypeBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 6/9/2018.
 * <p>
 * save file structure
 */
public class Loader {

    private static String fileName;

    public static void load() {
//        EUtils.event(GuiEventType.SHOW_LOAD_PANEL);
//        List<File> saves = FileManager.getFilesFromDirectory(
//         PathFinder.getSavesPath(), false);
//        saves.sort(
//         SortMaster.getSorterByExpression(false,
//         (file) -> (int) ((File) file).lastModified()));

        String saveName = "klhk";//saves.get(0).getName();
        ScreenData data=new ScreenData(ScreenType.MAP, null );
        data.setParam(new EventCallbackParam(saveName));
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         data);
    }

    static Map<Integer, DC_HeroItemObj> itemMap;
    static Map<String, Unit> loadedHeroMap;

    public static void load(String data) {
        fileName= data;
    }
    public static void loadCharacters() {
        loadedHeroMap = new HashMap<>();
        //create custom types
        // invoke normal methods, but first load some fields
        String xml = FileManager.readFile(
         "C:\\Eidolons\\battlecraft\\Dueling-Club\\target\\resources\\text\\saves\\Test 5-4202.xml"
//         PathFinder.getSavesPath() + fileName
        );
        Document xmlDoc = XML_Converter.getDoc(xml);

        Unit hero =null ;// Eidolons.getMainHero();
        Node node = XML_Converter.findNode(xmlDoc, Saver.HERO_NODE);
        ObjType loadedType = TypeBuilder.buildType(node.getFirstChild(),
         DataManager.getType(
           StringMaster.getWellFormattedString(
          node.getFirstChild().getNodeName()) , DC_TYPE.CHARS));
//        hero.applyType(loadedType);
        //overload type?
        hero = new Unit(loadedType);

        node = XML_Converter.findNode(xmlDoc, Saver.ITEMS_NODE);
        itemMap = new HashMap<>();
        for (Node propNode : XML_Converter.getNodeList(node)) {
            PROPERTY prop = ContentValsManager.getPROP(propNode.getNodeName());
            for (Node sub : XML_Converter.getNodeList(propNode)) {
                Node typeNode = sub.getFirstChild();
                int id = StringMaster.getInteger(StringMaster.getLastPart(sub.getNodeName(), "_"));
                String name = StringMaster.getWellFormattedString(sub.getNodeName().replace("" + id, ""));
                OBJ_TYPE TYPE = DC_ContentValsManager.getTypeForProperty( prop);
                ObjType type = DataManager.getType(name, TYPE);

                type = TypeBuilder.buildType(typeNode, type);
                createLoadedItem(type, id, hero,prop==PROPS.QUICK_ITEMS);


            }
        }
        hero.setLoaded(true);
        hero.reset();
        updateItemIds(hero);
        hero.setLoaded(false);
        loadedHeroMap.put(hero.getName(), hero);
    }

    public static Unit getLoadedHero(String typeName) {
        return loadedHeroMap.get(typeName);
    }
    private static void updateItemIds(Unit hero) {
        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {
            String newValue = "";
            for (String substring : StringMaster.openContainer(hero.getProperty(sub))) {
                if (!StringMaster.isInteger(substring))
                    continue;
                int id = StringMaster.getInteger(substring);
                newValue += itemMap.get(id).getId() + ";";
            }
            hero.setProperty(sub, newValue);
        }
    }

    private static DC_HeroItemObj createLoadedItem(ObjType type, int id, Unit hero, boolean quick) {
        DC_HeroItemObj item= ItemFactory.createItemObj(type, hero, quick);
        item.cloneMaps(type);
        itemMap.put(id, item);
        return  item;
    }

    public static <T extends BattleFieldObject> DequeImpl<? extends DC_HeroItemObj>
    getLoadedItemContainer(T entity, PROPS prop) {
        DequeImpl<DC_HeroItemObj> container = new DequeImpl<>();

        for (String substring : StringMaster.openContainer(entity.getProperty(prop))) {
            container.add(itemMap.get(StringMaster.getInteger(substring)));
        }

        return container;
    }

    public static DC_HeroItemObj getLoadedItem(Unit entity, G_PROPS prop) {
        return
         itemMap.get(StringMaster.getInteger(entity.getProperty(prop)));
    }

}

















