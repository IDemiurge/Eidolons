package eidolons.game.netherflame.dungeons.model.assembly;

import eidolons.game.netherflame.dungeons.QuestDungeonData;
import eidolons.game.netherflame.dungeons.model.*;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.data.DataUnit;

import java.util.*;

import static eidolons.game.netherflame.dungeons.QD_Enums.*;

public class QD_Assembler {

    Map<Integer, QD_Floor> floors;
    Map<Integer, QD_Module> modules;
    QuestDungeonData data;

    private Random random;
    private static Set<QD_Module> modulePool;
    private Set<QD_Module> pool;

    public Set<QD_Module> constructModulePool() {
//QD_XmlModelReader.getOrCreateFloorFromXml()
        return null;
    }

    private Set<QD_Module> filterPool(Set<QD_Module> modulePool) {
        modulePool.removeIf(module-> {
           return  module.getData().getEnum(ModuleProperty.location, QD_LOCATION.class)
                    == data.getLocation();
        });
        return null;
    }
    public QD_Model assemble(QuestDungeonData data, long seed) {
        this.data = data;
        random = new Random(seed);
        int numberOfFloors = getNumberOfFloors(data);
        if (modulePool == null) {
            modulePool = constructModulePool();
            pool = filterPool(modulePool);
        }
        //use filters to construct the pool?
        // by folder, but also by contents of course
        // we need to read module xml into a MODEL of it...
        // we need to construct and TAG this pool that we have - dynamically!
        /*
        steps:
        1. Quest Floors -
        2. Location floors
        3. Quest / Puzzle / Special Modules
        4.
         */
        DataUnit<DungeonProperty> dungeonData = generateDungeonData(data);
        List<DataUnit<FloorProperty>> floorData = new ArrayList<>(numberOfFloors);

        for (int i = 0; i < numberOfFloors; i++) {
            floorData.add(constructFloorData(i, numberOfFloors, dungeonData));
        }
        String preset = dungeonData.getValue(DungeonProperty.preset_floors);
        for(String substring: ContainerUtils.openContainer( preset )){
            Integer index = NumberUtils.getIntParse(VariableManager.getVar(substring));
            String s = VariableManager.removeVarPart(substring);
            QD_Floor  module = QD_XmlModelReader.getOrCreateFloorFromXml(s);
            floors.put(index, module);
        }
        for (int i = 0; i < numberOfFloors; i++) {
            QD_Floor floor = assembleFloor(floorData.get(i));
        }
        QD_Dungeon dungeon = new QD_Dungeon(floors, dungeonData);
        return new QD_Model(dungeon);
    }

    private DataUnit<DungeonProperty> generateDungeonData(QuestDungeonData data) {
        return null;
    }

    private DataUnit<FloorProperty> constructFloorData(int i, int numberOfFloors, DataUnit<DungeonProperty> dungeonData) {
        return null;
    }


    private QD_Floor assembleFloor(DataUnit<FloorProperty> data) {
        int moduleCount = getModuleCount(data, this.data);

        String preset = data.getValue(FloorProperty.preset_modules);
        for(String substring: ContainerUtils.openContainer( preset )){
            Integer index = NumberUtils.getIntParse(VariableManager.getVar(substring));
            String s = VariableManager.removeVarPart(substring);
            QD_Module  module = QD_XmlModelReader.getOrCreateModuleFromXml(s);
            modules.put(index, module);
        }
        for (int i = 0; i < moduleCount; i++) {
          QD_Module  module = pickModule(i, moduleCount, data, 0);
            modules.put(i, module);
        }
        return new QD_Floor(data, modules);

    }


    private QD_Module pickModule(int i, int max, DataUnit<FloorProperty> data, int attempt) {
        QD_Module previous=i<=0? null : modules.get(i-1);
        QD_Module next= modules.get(i+1); //usually null unless req
        QD_Module pick=null ;

        QD_Picker picker = new QD_Picker(random, i, max, attempt, data, previous, next);

        LinkedList<QD_Module> sorted = new LinkedList<>(pool);
        sorted.removeIf(picker::check);
        sorted.sort(picker);


        if (pick == null) {
            if (attempt<5)
                return pickModule(i,  max,  data, attempt++);
            //try again with less restrictions?..
            main.system.auxiliary.log.LogMaster.log(1,"FAILED TO FIND A MODULE FOR "+data );
        }

        Transform transform = getTransform(pick, data, previous, next, i, max );

        QD_Module module= new QD_Module(pick, transform);
        modules.put(i, module);
        return module;
    }

    private Transform getTransform(QD_Module pick, DataUnit<FloorProperty> data, QD_Module previous, QD_Module next, int i, int max) {
        return null;
    }

    private int getNumberOfFloors(QuestDungeonData data) {
        switch (data.getLength()) {
            case blitz:
                return 2;
            case normal:
                return 3;
            case hardy:
                return 4;
            case grueling:
                return 5;
        }
        return 0;
    }

    private int getModuleCount(DataUnit<FloorProperty> data, QuestDungeonData data1) {
        int n = getNumberOfFloors(data1)-1;
        switch (data.getEnum(FloorProperty.length, QD_LENGTH.class)) {

        }
        return 0;
    }
}













