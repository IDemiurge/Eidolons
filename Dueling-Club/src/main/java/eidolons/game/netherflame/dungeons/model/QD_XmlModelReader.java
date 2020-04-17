package eidolons.game.netherflame.dungeons.model;

import eidolons.game.netherflame.dungeons.QD_Enums;
import eidolons.game.netherflame.dungeons.model.assembly.TransformData;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class QD_XmlModelReader {
    static Map<String, QD_Floor> floorMap;
    static Map<String, QD_Module> moduleMap;
    private static boolean modules;

    public static void init(){
    floorMap = new LinkedHashMap<>();
    moduleMap = new LinkedHashMap<>();

    recursiveRead(PathFinder.getFloorTemplatesPath());
        modules = true;
    recursiveRead(PathFinder.getModuleTemplatesPath());
}

    public static QD_Floor getOrCreateFloorFromXml(String path) {
        QD_Floor floor = floorMap.get(path);
        if (floor != null) {
            return floor;
        }
        floor = constructFloor(FileManager.getFileName(path),
                FileManager.readFile(path));
        return floor;
    }

    public static QD_Module getOrCreateModuleFromXml(String path) {
        QD_Module module = moduleMap.get(path);
        if (module != null) {
            return module;
        }
        module = constructModule(FileManager.getFileName(path),
                FileManager.readFile(path));
        return module;
    }

    private static void recursiveRead(String path) {
        for (File file : FileManager.getFilesFromDirectory(path, true)) {
            if (file.isDirectory()) {
                recursiveRead(file.getPath());
            } else
                read(file);
        }
    }

    private static void read(File file) {
        String fileName = StringMaster.cropFormat(file.getName());
        if (modules) {
        floorMap.put(file.getPath(), constructFloor(fileName,FileManager.readFile(file)));
        } else {
        moduleMap.put(file.getPath(), constructModule(fileName,FileManager.readFile(file)));
        }
        //determine if it is floor or module?
    }

    private static QD_Module constructModule(String fileName, String contents) {
        DataUnit<QD_Enums.ModuleProperty> data=new QD_FloorLoader().createModuleData(contents);
        QD_Module module= new QD_Module(data);
        TransformData t= createTransformFromFileName(fileName);
        module.setTransformData(t);
        return module;
    }

    private static TransformData createTransformFromFileName(String fileName) {
        return null;
    }

    private static QD_Floor constructFloor(String fileName, String contents) {
        DataUnit<QD_Enums.FloorProperty> data=new QD_FloorLoader().createFloorData(contents);
        QD_Floor floor= new QD_Floor(data, null );
        return floor;
    }

}
