package main.level_editor.backend.handlers.structure;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.tilemap.TileMap;
import eidolons.game.module.generator.tilemap.TileMapper;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.system.content.PlaceholderGenerator;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.screen.LE_Screen;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.awt.*;
import java.util.List;
import java.util.*;

import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.BORDER_TYPE;
import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.MODULE_VALUE;
import static main.system.auxiliary.log.LogMaster.log;

public class LE_ModuleHandler extends LE_Handler implements IModuleHandler {
    //let's assume maximum of 4 modules, so we have a simple 2x2 grid, ok? damn, I did want to have 5 in some..
    Map<Point, Module> moduleGrid = new LinkedHashMap<>();
    private Set<BattleFieldObject> borderObjects = new HashSet<>();

    public LE_ModuleHandler(LE_Manager manager) {
        super(manager);
    }

    public Map<Point, Module> getModuleGrid() {
        return moduleGrid;
    }

    public void setGrid(LinkedHashMap<Point, Module> grid) {
        if (moduleGrid != null) {
            if (moduleGrid.equals(grid)) {
                return;
            }
        }
        moduleGrid = grid;
        for (Point point : grid.keySet()) {
            Module module = grid.get(point);
            Coordinates c = getMappedCoordForPoint(point, module);

            log(LOG_CHANNEL.BUILDING, module.getName() + " Module placed at " + c);
            module.setOrigin(c);
        }
        resetBorders();
        if (isLoaded())
            resetBufferVoid();
    }


    private Coordinates getMappedCoordForPoint(Point p, Module module) {
        int offsetX = 0;
        int offsetY = 0;
        for (int i = 1; i <= p.x; i++) {
            Module relativeTo = moduleGrid.get(new Point(p.x - i, p.y));
            int w = relativeTo.getEffectiveWidth(true);
            offsetX += w;
        }
        for (int i = 1; i <= p.y; i++) {
            Module relativeTo = moduleGrid.get(new Point(p.x, p.y - i));
            int h = relativeTo.getEffectiveHeight(true);
            offsetY += h;
        }

//        offsetX += module.getData().getIntValue(MODULE_VALUE.width_buffer);
//        offsetY += module.getData().getIntValue(MODULE_VALUE.height_buffer);

        return Coordinates.get(offsetX, offsetY);
    }

    public void resetBufferVoid() {
        LinkedHashSet<Coordinates> set = new LinkedHashSet<>();
        LinkedHashSet<Coordinates> full = new LinkedHashSet<>(getGame().getCoordinates());
        for (Module module : getModules()) {
            set.addAll(module.initCoordinateSet(true));
            set.removeAll(module.getVoidCells());
        }
        full.removeAll(set);
        log(LOG_CHANNEL.BUILDING, " Buffer void being reset " + full.size());
        getOperationHandler().execute(Operation.LE_OPERATION.MASS_SET_VOID, full);
    }

    @Override
    public void resetBorders() {
        //TODO can optimize not to remove/add unnecessarily!
        for (BattleFieldObject borderObject : borderObjects) {
            getObjHandler().removeIgnoreWrap(borderObject);
        }
        borderObjects.clear();
        for (Module module : getModules()) {
            List<Coordinates> borderCoords = getBorderCoordinates(module, false);

            ObjType objType = null;
            BORDER_TYPE type = new EnumMaster<BORDER_TYPE>().retrieveEnumConst(BORDER_TYPE.class,
                    module.getData().getValue(MODULE_VALUE.border_type));
            if (type == null) {
                objType = DataManager.getType(module.getData().getValue(MODULE_VALUE.border_type)
                        , DC_TYPE.BF_OBJ);
                if (objType == null) {
                    type = BORDER_TYPE.wall;
                }
            }
            if (objType == null) {
                String objTypeName = null;
                switch (type) {
                    case wall_alt:
                        objTypeName = PlaceholderGenerator.getPlaceholderName(GeneratorEnums.ROOM_CELL.ALT_WALL) + " Indestructible";
                        break;
                    case wall:
                        objTypeName = PlaceholderGenerator.getPlaceholderName(GeneratorEnums.ROOM_CELL.WALL) + " Indestructible";
                        break;
                    case chism:
                        break;
                    case irregular:
                        break;
                    case wall_and_chism:
                        break;
                }
                objType = DataManager.getType(objTypeName
                        , DC_TYPE.BF_OBJ);
            }

            log(LOG_CHANNEL.BUILDING, module.getName() + " borders being reset " + borderCoords.size());
            for (Coordinates borderCoord : borderCoords) {
                BattleFieldObject obj = getObjHandler().addObjIgnoreWrap(objType, borderCoord.x, borderCoord.y);
                obj.setModuleBorder(true);
                borderObjects.add(obj);
                //TODO set border flag to remove when resetting.. or put into a map
            }
        }
    }

    @Override
    public void afterLoaded() {
//        for (Module module : getModules()) {
//            List<Coordinates> borderCoords = getBorderCoordinates(module, false);
//            getStructureHandler().resetWalls(getDungeonLevel(), borderCoords);
//        }
        getStructureHandler().reset(getDungeonLevel());
        resetBufferVoid();
    }

    private List<Coordinates> getBufferCoordinates(Module module) {
        return getBorderCoordinates(module, true);
    }

    private List<Coordinates> getBorderCoordinates(Module module, boolean buffer) {
        Coordinates origin = module.getOrigin();
        int border = module.getData().getIntValue(MODULE_VALUE.border_width);
        int borderW = border;
        int borderH = border;

        int h = module.getHeight() + borderH * 2;
        int w = module.getWidth() + borderW * 2;

        int bufferW = module.getWidthBuffer() - 1;
        int bufferH = module.getHeightBuffer() - 1;
        if (buffer) {
            borderW += bufferW;
            borderH += bufferH;
        }
        Coordinates corner = origin.getOffset(w, h);
        corner = corner.getOffset(bufferW, bufferH);
        origin = origin.getOffset(bufferW, bufferH);

        List<Coordinates> full = CoordinatesMaster.getCoordinatesBetweenInclusive(origin, corner);
        List<Coordinates> inner = buffer ?
                CoordinatesMaster.getCoordinatesBetweenInclusive(
                        origin.getOffset(borderW - border, borderH - border),
                        corner.getOffset(-borderW + border, -borderH + border))
                :
                CoordinatesMaster.getCoordinatesBetweenInclusive(
                        origin.getOffset(borderW, borderH), corner.getOffset(-borderW, -borderH));
        full.removeIf(c -> inner.contains(c));
        return full;
    }

    public Set<Module> getModules() {
        return getGame().getMetaMaster().getModuleMaster().getModules();
    }

    private void placeModule(Module module) {
        getMapHandler().initModuleSize(module);
        Point p = choosePointAt();
        Coordinates at = getMappedCoordForPoint(p, module);
        moduleGrid.put(p, module);
        module.setOrigin(at);
    }


    public void addModule(ModuleData data) {
        Module module = new Module(data);
        placeModule(module);

//        getGame().getMetaMaster().getDungeonMaster().getLayerManager().
//                initLayers(data.getValue(LevelStructure.MODULE_VALUE.layer_data));

//        initModuleStructure(dataString, module);

        getModules().add(module);
    }


    @Override
    public void addModule() {
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
        data.setValue(MODULE_VALUE.name, name);
        if (tileMapVariant) {
            String gridData = XmlNodeMaster.findNodeText(contents, "tilemap");
            TileMap tileMap = TileMapper.createTileMap(gridData);
            //translate based on preferences of the floor?
            w = tileMap.getWidth();
            h = tileMap.getHeight();

            data.setValue(MODULE_VALUE.tile_map, gridData);
        } else {
            if (empty) {

            } else {

            }
            String layerData = XmlNodeMaster.findNodeText(contents, "layers");
            String structureData = XmlNodeMaster.findNodeText(contents, LocationBuilder.ZONES_NODE);
            data.setValue(MODULE_VALUE.layer_data, layerData);
//            data.setValue(LE_Structure.MODULE_VALUES.structureData, structureData);
        }
        data.setValue(MODULE_VALUE.height, h);
        data.setValue(MODULE_VALUE.width, w);
        addModule(data);
    }

    private String getDefaultModulePath() {
        return PathFinder.getModulesPath() + "default.xml";
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
//clear with cells or void or cull
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
//'pack' for editing convenience
    }

    @Override
    public void cloneModule() {
//        addModule(getSelected());
    }

    private Module getSelected() {
        return getModel().getModule();
    }

}
