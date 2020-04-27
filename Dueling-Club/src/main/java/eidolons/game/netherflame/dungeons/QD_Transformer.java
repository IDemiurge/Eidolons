package eidolons.game.netherflame.dungeons;


import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.netherflame.dungeons.model.QD_Floor;
import eidolons.game.netherflame.dungeons.model.QD_Model;
import eidolons.game.netherflame.dungeons.model.QD_Module;
import eidolons.game.netherflame.dungeons.model.assembly.ModuleGridMapper;
import main.data.xml.XmlStringBuilder;
import main.system.auxiliary.data.FileManager;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class QD_Transformer {

    private LocationMaster master;
    private QD_Model model;
    private Location location;

    public QD_Transformer(LocationMaster master) {
        this.master = master;
    }

    public String constructXmlFromModel(QD_Model model) {
        this.model = model;
        for (QD_Floor fl : model.getDungeon().getFloors().values()) {
            String contents = constructXmlForFloor(fl);
            FileManager.write(contents, getFloorFilePath(fl));
            location = master.getFloorWrapper();
        }
        return getRootPath();
    }

    private String getFloorFilePath(QD_Floor fl) {
        return getRootPath() + fl.getData().getValue("name");
    }

    private String getRootPath() {
        return null;
    }

    private String getDataContents(QD_Floor floor) {
        return floor.getData().getValue(QD_Enums.FloorProperty.dc_data);
    }
    public String constructXmlForFloor(QD_Floor floor ) {
        XmlStringBuilder builder = new XmlStringBuilder();
        builder.open(FloorLoader.DATA);
        builder.append(getDataContents(floor));
        builder.close(FloorLoader.DATA);
        Set<Module> modules = new LinkedHashSet<>();
        builder.open("Modules");
        for (QD_Module value : floor.getModules().values()) {
            String modulePath = value.getData().getValue(QD_Enums.ModuleProperty.file_path);
            String s = FileManager.readFile(modulePath);
            builder.appendNode(s, "Module");

//            Document doc = XML_Converter.getDoc(s);
//            Module module = master.getStructureBuilder().createModule(doc, location);
//            //we don't need to create it all though, do we?
//            modules.add(module);

        }
        LinkedHashMap<Point, Module> grid = new ModuleGridMapper().getOptimalGrid(modules);
        //TODO could use a wrapper

        //TODO offset per module, and that's it? just to spare some coordinates? THAT"S ADDITIONAL FEATURE!
        //on the face of it, we just mix up existing XML, that's it.

        //TODO shift coordinates for this grid!
        for (Module module : modules) {
                //le_xml saver!..
        }
        builder.close("Modules");
        return builder.wrap("Floor").toString();
    }



}
