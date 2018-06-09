package eidolons.game.module.adventure.global.persist;

import eidolons.ability.InventoryTransactionManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.TypeBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.List;

/**
 * Created by JustMe on 6/9/2018.
 * <p>
 * save file structure
 */
public class Loader {

    public static void load() {
//        EUtils.event(GuiEventType.SHOW_LOAD_PANEL);
        List<File> saves = FileManager.getFilesFromDirectory(PathFinder.getSavesPath(), false);
        saves.sort(
         SortMaster.getSorterByExpression(false,
         (file) -> (int) ((File) file).lastModified()));
        load(saves.get(0).getName());
    }

    public static void load(String fileName) {
        //create custom types
        // invoke normal methods, but first load some fields
        String xml = FileManager.readFile(PathFinder.getSavesPath() + fileName);
        Document xmlDoc = XML_Converter.getDoc(xml);

        Node node = XML_Converter.findNode(xmlDoc, Saver.HERO_NODE);
        for (Node sub : XML_Converter.getNodeList(node)) {

        }

        Unit hero = Eidolons.getMainHero();

        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {

        }
        node = XML_Converter.findNode(xmlDoc, Saver.ITEMS_NODE);
        for (Node sub : XML_Converter.getNodeList(node)) {
            Node typeNode = sub.getFirstChild();
            int id =StringMaster.getInteger(StringMaster.getLastPart(sub.getNodeName(), "_"));
            String name=  StringMaster.getWellFormattedString(sub.getNodeName().split("_")[0]);
            OBJ_TYPE TYPE= C_OBJ_TYPE.ITEMS;
            ObjType type = DataManager.getType(name, TYPE);

            type = TypeBuilder.buildType(typeNode, type);
            Obj item = hero.getGame().getObjectById(id);
            item.cloneMaps(type);



        }

    }
}

















