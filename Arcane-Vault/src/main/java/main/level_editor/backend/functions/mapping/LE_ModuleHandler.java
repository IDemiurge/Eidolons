package main.level_editor.backend.functions.mapping;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.libgdx.gui.utils.FileChooserX;
import main.data.filesys.PathFinder;
import main.data.xml.XmlNodeMaster;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;
import main.system.auxiliary.data.FileManager;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LE_ModuleHandler extends LE_Handler implements IModuleHandler {
    //let's assume maximum of 4 modules, so we have a simple 2x2 grid, ok? damn, I did want to have 5 in some..
    Map<Point, Module> moduleGrid = new LinkedHashMap<>();
    Map<Point, Coordinates> pointCoordMap = new LinkedHashMap<>();

    public static final int DEFAULT_MAX_WIDTH = 100;
    public static final int DEFAULT_MAX_HEIGHT = 100;

    public LE_ModuleHandler(LE_Manager manager) {
        super(manager);
//        GuiEventManager.bind(GuiEventType.LE_REMAP_MODULES, p -> remapAll());
    }


    public Set<Module> getModules() {
        return getGame().getMetaMaster().getModuleMaster().getModules();
    }

    private Module addLogicalModule(int w, int h, String name) {
        Point p = choosePointAt();
        Coordinates at = getMappedCoordForPoint(p);

        Module module = new Module(at, w, h, name);
        moduleGrid.put(p, module);
        getModules().add(module);
        return module;
    }

    public void addModule(Module module) {

    }

    public void addModule(ModuleData data) {

//        zones = data.getValue("zones");
        //general structure - what zones have what blocks
        int w = data.getIntValue(LevelStructure.MODULE_VALUE.width);
        int h = data.getIntValue(LevelStructure.MODULE_VALUE.height);
        String name = data.getValue(LevelStructure.MODULE_VALUE.name);
        Module module = addLogicalModule(w, h, name);
        //TODO add ZONES AND BLOCKS from this!

        getGame().getMetaMaster().getDungeonMaster().getLayerManager().
                initLayers(data.getValue(LevelStructure.MODULE_VALUE.layer_data));

        if (data.getBooleanValue(LevelStructure.MODULE_VALUE.replace_default)){
//            initDefaultModule();
//            getMapHandler().setOffset(offset);
        }

    }

    @Override
    public void addModule() {
        /*
        some meta-data
        layers
         */
        boolean tileMapVariant = EUtils.confirm("Tilemap template?");
        String template = null;
        boolean empty = false;
        if (tileMapVariant) {
            template = FileChooserX.chooseFile(PathFinder.getModuleTemplatesPath(),
                    "xml", LE_Screen.getInstance().getGuiStage());
        } else {
            empty = EUtils.confirm("Empty module?");

            if (!empty) {
                template = FileChooserX.chooseFile(PathFinder.getModulesPath(),
                        "xml", LE_Screen.getInstance().getGuiStage());
            } else {
                template = getDefaultModulePath();
            }
        }
        String name = FileManager.getFileName(template);
        String contents = FileManager.readFile(template);
        int w = 0;
        int h = 0;
        ModuleData data = new ModuleData(null);
        data.setValue(LevelStructure.MODULE_VALUE.name, name);
        if (tileMapVariant) {
            String gridData = XmlNodeMaster.findNodeText(contents, "tilemap");
            TileMap tileMap = TileMapper.createTileMap(gridData);
            //translate based on preferences of the floor?
            w = tileMap.getWidth();
            h = tileMap.getHeight();

            data.setValue(LevelStructure.MODULE_VALUE.tile_map, gridData);


        } else {
            if (empty) {

            } else {

            }
            String layerData = XmlNodeMaster.findNodeText(contents, "layers");
            String structureData = XmlNodeMaster.findNodeText(contents, LocationBuilder.ZONES_NODE);
            data.setValue(LevelStructure.MODULE_VALUE.layer_data, layerData);
//            data.setValue(LE_Structure.MODULE_VALUES.structureData, structureData);
        }
        data.setValue(LevelStructure.MODULE_VALUE.height, h);
        data.setValue(LevelStructure.MODULE_VALUE.width, w);

        addModule(data);
    }

    private String getDefaultModulePath() {
        return PathFinder.getModulesPath() + "default.xml";
    }


    private Coordinates getMappedCoordForPoint(Point p) {
        /*
        either already mapped or same logic as Point - by width/height
         */
        Coordinates c = pointCoordMap.get(p);
        return c;
    }


    private Point choosePointAt() {
        //auto
        Point p = new Point(-1, 0);
        for (Point p1 : moduleGrid.keySet()) {
            Module module = moduleGrid.get(p1);
            if (module == null) {
                return p1;
            }
            if (p1.x >= p.x) {
                p.x = p1.x;
            }
            if (p1.y >= p.y) {
                p.y = p1.y;
            }
        }
        p.x++;
        return p;
    }

    @Override
    public void removeModule() {

    }

    @Override
    public void editModule() {
//        GuiEventManager.trigger(GuiEventType.LE_EDIT_ENTITY, getModel().getModule());
    }

    @Override
    public void moveModule() {

    }

    @Override
    public void swapModules() {

    }

    public void offsetModule() {

    }

    @Override
    public void remap() {

    }

    @Override
    public void resetBorders() {

    }

    @Override
    public void cloneModule() {
        addModule(getSelected());
    }

    private Module getSelected() {
        return getModel().getModule();
    }
}
