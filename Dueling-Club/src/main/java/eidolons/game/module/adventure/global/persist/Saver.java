package eidolons.game.module.adventure.global.persist;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 6/9/2018.
 */
public class Saver {

    private static final String HEAD = "SAVE";
    private static final String HERO_NODE = "HERO";
    private static final String WORLD_NODE = "WORLD";

    public static void save(){
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
        String content =getSaveContent();
        FileManager.write(content, path);
    }

    private static String getSaveContent() {
        String content ="";

        String heroData="";
        //write full type data!
        HeroDataModel model = new HeroDataModel(Eidolons.getMainHero());

        heroData=  XML_Writer.getTypeXML_Builder(model,
         model.getType()).toString();

//        for (PARAMETER sub : Eidolons.getMainHero().getParamMap().keySet()) {
//            if (sub.isDynamic() ||
//        for (PROPERTY sub : Eidolons.getMainHero().getPropMap().keySet()) {
        content =heroData;
        content =XML_Converter.wrap(HERO_NODE, content);
        content =XML_Converter.wrap(HEAD, content);
        return content;
    }
    private static String getPath() {
        return PathFinder.getSavesPath();
    }
}
