package main.level_editor.backend.handlers.structure;

import com.badlogic.gdx.graphics.Color;
import com.google.inject.internal.util.ImmutableSet;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.generator.LevelData;
import eidolons.game.module.generator.model.RoomModel;
import eidolons.game.module.generator.model.RoomTemplateMaster;
import eidolons.game.module.generator.tilemap.TilesMaster;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.utils.GdxDialogMaster;
import eidolons.system.text.NameMaster;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapBuilder;
import main.system.threading.WaitMaster;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

public class LE_StructureHandler extends LE_Handler implements IStructureHandler {


    private static final ROOM_TEMPLATE_GROUP[] TEMPLATE_GROUPS =
            new ROOM_TEMPLATE_GROUP[]{
                    ROOM_TEMPLATE_GROUP.TEMPLE,
                    ROOM_TEMPLATE_GROUP.CASTLE,
                    ROOM_TEMPLATE_GROUP.CAVE,
                    ROOM_TEMPLATE_GROUP.TOWER,
                    ROOM_TEMPLATE_GROUP.CRYPT,
                    ROOM_TEMPLATE_GROUP.DUNGEON,
                    ROOM_TEMPLATE_GROUP.CEMETERY,
            };
    private static final Color[] BLOCK_COLORS = {
            GdxColorMaster.LILAC,
            GdxColorMaster.CYAN,
            GdxColorMaster.BLUE,
            GdxColorMaster.RED,
            GdxColorMaster.BEIGE,
            GdxColorMaster.GREEN,
    };

    Map<String, BlockData> blockTemplates = new LinkedHashMap<>();
    Map<String, ZoneData> zoneTemplates = new LinkedHashMap<>();
    private RoomTemplateMaster roomTemplateMaster;

    public LE_StructureHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void afterLoaded() {
        String content = FileManager.readFile(getTemplatesPath() + "blocks.xml");
        blockTemplates =
                new MapBuilder<>(":", StringMaster.VERTICAL_BAR, s -> s, s -> new BlockData(s)).build(content);

        content = FileManager.readFile(getTemplatesPath() + "zones.xml");

        zoneTemplates =
                new MapBuilder<>(":", StringMaster.VERTICAL_BAR, s -> s, s -> new ZoneData(s)).build(content);
    }

    @Override
    public void saved() {
        StringBuilder builder = new StringBuilder();
        for (String s : blockTemplates.keySet()) {
            BlockData data = blockTemplates.get(s);
            builder.append(s).append(":").append(data.toString()).append(StringMaster.VERTICAL_BAR);
        }
        FileManager.write(builder.toString(), getTemplatesPath() + "blocks.xml");
        builder = new StringBuilder();

        for (String s : zoneTemplates.keySet()) {
            ZoneData data = zoneTemplates.get(s);
            builder.append(s).append(":").append(data.toString()).append(StringMaster.VERTICAL_BAR);
        }
        FileManager.write(builder.toString(), getTemplatesPath() + "zones.xml");
    }

    private String getTemplatesPath() {
        return PathFinder.getLevelEditorPath() + "templates/struct/";
    }

    private LevelBlock getBlock() {
        return getModel().getBlock();
    }

    @Override
    public void exportStruct() {
        LevelStruct struct = getModel().getLastSelectedStruct();
        String name = struct.getName();
        name = GdxDialogMaster.inputText("Export with name...", name);
        if (struct instanceof Module) {
            name = NameMaster.getUniqueVersionedName(new ArrayList<>(blockTemplates.keySet()), name);
            String structName = struct.getName();
            struct.setName(name);
            getDataHandler().saveModule((Module) struct);
            struct.setName(structName);
        }
        if (struct instanceof LevelBlock) {
            name = NameMaster.getUniqueVersionedName(new ArrayList<>(blockTemplates.keySet()), name);
            blockTemplates.put(name, ((LevelBlock) struct).getData());
        }
        if (struct instanceof LevelZone) {
            name = NameMaster.getUniqueVersionedName(new ArrayList<>(zoneTemplates.keySet()), name);
            zoneTemplates.put(name, (ZoneData) struct.getData());
        }
        saved();
    }

    public void addZone() {
        //should we have a number of Floor templates?
        List<LevelZone> zones = getModel().getModule().getZones();
        LevelZone zone = new LevelZone(zones.size()); //or default per floor template
        zone.setModule(getModel().getModule());
        zone.setData(new ZoneData(zone));
        //TODO templates
        zones.add(zone);
        getModel().setZone(zone);
        updateTree();
    }

    @Override
    public void insertBlock() {
        RoomTemplateMaster templateMaster = getRoomTemplateManager();
        ROOM_TEMPLATE_GROUP room_template_group
                = (ROOM_TEMPLATE_GROUP) LE_Screen.getInstance().getGuiStage().getEnumChooser()
                .choose(templateMaster.getModels().keySet().toArray(new ROOM_TEMPLATE_GROUP[0]));

        ROOM_TYPE type = ROOM_TYPE.THRONE_ROOM;
        //TODO choose

        Set<RoomModel> from = templateMaster.getModels().get(room_template_group);
        from.removeIf(model -> model == null);
        from.removeIf(model -> model.getType() != type);
        RoomModel template = LE_Screen.getInstance().getGuiStage().getTemplateChooser()
                .choose(from);
        Coordinates c = getSelectionHandler().selectCoordinate();
        insertBlock(template, c);
    }

    public RoomTemplateMaster getRoomTemplateManager() {
        if (roomTemplateMaster == null) {
            LevelData data = new LevelData("");
            data.setTemplateGroups(TEMPLATE_GROUPS);
            roomTemplateMaster = new RoomTemplateMaster(data);
        }
        return roomTemplateMaster;
    }

    public void insertBlock(RoomModel blockTemplate, Coordinates at) {
        //check overlapping with other blocks
        // delete block - with all that's in it?
        // edit block - room type?

        //confirm if TRANSFORM
        //what about exits?

        if (blockTemplate == null) {
            return;
        }

        LevelZone zone = getModel().getZone();
        if (zone == null) {
            zone = getModel().getModule().getZones().get(0);
        }
        LevelBlock block = new LevelBlock(zone);
        block.setModel(blockTemplate);
        block.setOrigin(at);
        int x = 0;
        int y = 0;
        Set<Coordinates> coords = block.getCoordinatesSet();
        getOperationHandler().operation(Operation.LE_OPERATION.INSERT_START);
        if (!addBlock(block)) {
            getOperationHandler().operation(Operation.LE_OPERATION.INSERT_END);
            return;
        }
        for (String[] column : blockTemplate.getCells()) {
            for (String cell : column) {
                if (TilesMaster.isIgnoredCell(cell)) {
                    continue;
                }
                Coordinates c = Coordinates.get(x, y).getOffset(at);
                processCell(c, cell);
                y++;
            }
            y = 0;
            x++;
        }
        getOperationHandler().operation(Operation.LE_OPERATION.INSERT_END);
        initBlockCoords(block, coords);
        initNewBlock(block, true);
        block.setRoomType(blockTemplate.getType());

        LevelStruct level = getGame().getDungeonMaster().getFloorWrapper();
        reset(level);
        updateTree();
    }

    private void fixOverlap(Set<Coordinates> coordinates) {
        Set<Coordinates> overlap = new LinkedHashSet<>();
        for (Coordinates c : coordinates) {
            LevelStruct lowestStruct = getStructureMaster().getLowestStruct(c);
            if (lowestStruct instanceof LevelBlock) {
                overlap.add(c);
            }
        }
        if (overlap.size() > 0) {
            if (EUtils.waitConfirm("Remove " +
                    overlap.size() +
                    " overlapping coordinates?")) {
                coordinates.removeAll(overlap);
                return;
            }
            //remove from others
            for (Coordinates c : coordinates) {
                LevelStruct lowestStruct = getStructureMaster().getLowestStruct(c);
                lowestStruct.getCoordinatesSet().remove(c);
            }
        }
    }

    private boolean checkOverlap(RoomModel blockTemplate, Coordinates at) {
        for (int i = 0; i < blockTemplate.getWidth(); i++) {
            for (int j = 0; j < blockTemplate.getHeight(); j++) {
                if (checkOverlap(Coordinates.get(at.x + i, at.y + j), blockTemplate.getCells()[i][j]))
                    return true;
            }
        }
        return false;
    }

    private boolean checkOverlap(Coordinates coordinates, String s) {
        GeneratorEnums.ROOM_CELL cell = GeneratorEnums.ROOM_CELL.getBySymbol(s);
        if (cell != GeneratorEnums.ROOM_CELL.FLOOR) {
            return !getGame().getObjectsOnCoordinateNoOverlaying(coordinates).isEmpty();
        }
        return false;
    }

    public void updateTree() {
        getGame().getDungeonMaster().getStructMaster().modelChanged();
        getModel().setTreeModel(LevelEditor.getCurrent().getWrapper());
    }

    private void processCell(Coordinates c, String cell) {
        GeneratorEnums.ROOM_CELL s = GeneratorEnums.ROOM_CELL.getBySymbol(cell);
        switch (s) {
            case WALL:
            case ART_OBJ:
            case SPECIAL_ART_OBJ:
            case CONTAINER:
            case SPECIAL_CONTAINER:
            case DOOR:
                initObject(c, s);
                break;
            case VOID:
                operation(Operation.LE_OPERATION.VOID_TOGGLE, c);
                break;
        }
    }

    private void initObject(Coordinates c, GeneratorEnums.ROOM_CELL cell) {
        ObjType type = getObjectType(c, cell);
        operation(Operation.LE_OPERATION.ADD_OBJ, type, c, new Boolean(false));
    }

    private ObjType getObjectType(Coordinates c, GeneratorEnums.ROOM_CELL cell) {
        return getPlaceholderType(cell);
    }

    private ObjType getPlaceholderType(GeneratorEnums.ROOM_CELL cell) {
        return DataManager.getType(cell.name() + " Placeholder", DC_TYPE.BF_OBJ);
    }


    @Override
    public void removeZone() {
        if (getModel().getZone() != null) {
            getModel().getModule().getZones().remove(getModel().getZone());
            updateTree();
        }
    }


    @Override
    public void addBlock() {
        LevelZone zone = getModel().getZone();
        Set<Coordinates> coordinates = getSelectionHandler().getCoordinatesAll();
        fixOverlap(coordinates);
        LevelBlock block = createBlock(zone, coordinates);
        initNewBlock(block, false);
        if (addBlock(block))
            updateTree();

        reset(block);
    }

    private void initNewBlock(LevelBlock block, boolean insert) {
        if (!blockTemplates.isEmpty() && EUtils.waitConfirm("Use template?")) {
            Object[] array = blockTemplates.keySet().toArray();
            Object c = LE_Screen.getInstance().getGuiStage().getEnumChooser().choose(array);
            BlockData template = blockTemplates.get(c);
            template.removeValue(LevelStructure.BLOCK_VALUE.width);
            template.removeValue(LevelStructure.BLOCK_VALUE.height);
            block.setData(new BlockData(block).setData(template.getData()));
            block.setName(template.getValue("name"));

            block.getData().apply();
        } else {
            if (insert)
                return;
            ROOM_TYPE type = LE_Screen.getInstance().getGuiStage().getEnumChooser()
                    .chooseEnum(ROOM_TYPE.class);
            if (type == null) {
                type = ROOM_TYPE.COMMON_ROOM;
            }
            block.setRoomType(type);
        }
    }

    private void reset(LevelBlock block) {
        reset(block.getData().getLevelStruct());
    }

    private boolean addBlock(LevelBlock block) {
        if (block.getModel() != null) { //TODO not just for model!
            boolean overlap = checkOverlap(block.getModel(), block.getOrigin());
            if (overlap) {
                if (!EUtils.waitConfirm("Allow overlap?")) {
                    return false;
                }
            }
        }
        LevelZone zone = block.getZone();
        Set<Coordinates> coordinates = block.getCoordinatesSet();

        if (!zone.getModule().getCoordinatesSet().containsAll(coordinates)) {
            if (EUtils.waitConfirm("Wrong module! Add to nearest zone?")) {
                zone = getZoneForBlock(block);
                block.setZone(zone);
            } else {
                return false;
            }
        }
        getOperationHandler().operation(Operation.LE_OPERATION.ADD_BLOCK, block);
        return true;
    }

    private LevelZone getZoneForBlock(LevelBlock block) {
        Module module = getModule(block.getOrigin());
        for (LevelZone zone : module.getZones()) {
            for (LevelBlock subPart : zone.getSubParts()) {
                if (isAdjacent(block, subPart)) {
                    return zone;
                }
            }
        }
        return module.getZones().get(0);
    }

    @Override
    public void removeBlock() {
        LevelBlock block = getModel().getBlock();
        if (block != null) {
            clearBlock(block);
            removeBlock(block);
        }
    }

    private void clearBlock(LevelBlock block) {
        getOperationHandler().operation(Operation.LE_OPERATION.CLEAR_START);
        for (Coordinates coordinates : block.getCoordinatesSet()) {
            for (BattleFieldObject battleFieldObject : getGame().getObjectsNoOverlaying(coordinates)) {
                getOperationHandler().operation(Operation.LE_OPERATION.REMOVE_OBJ, battleFieldObject);
            }
            //full clear - scripts, ..?
        }
        getOperationHandler().operation(Operation.LE_OPERATION.CLEAR_END);
    }

    private void removeBlock(LevelBlock block) {
        getOperationHandler().operation(Operation.LE_OPERATION.REMOVE_BLOCK, block);
        updateTree();
        if (block.getRoomType() == ROOM_TYPE.PLATFORM) {
            getPlatformHandler().platformBlockRemoved(block);
        }
    }

    @Override
    public void mergeBlock() {
        LevelBlock block = getBlock();
        EUtils.showInfoText("Select an adjacent block");
        WaitMaster.waitForInput(LevelEditor.SELECTION_EVENT);
        // TODO use LAST
        LevelBlock newBlock = getBlock();
        if (newBlock != block)
            if (isAdjacent(block, newBlock)) {
                mergeBlocks(block, newBlock);
            }
    }

    private void mergeBlocks(LevelBlock block, LevelBlock consumed) {
        Set<Coordinates> coordinatesSet = block.getCoordinatesSet();
        coordinatesSet.addAll(consumed.getCoordinatesSet());
        //        consumed.setOrigin(block.getOrigin());
        removeBlock(consumed);
        LevelStruct zone = block.getZone();
        getModel().setBlock(block);
        reset(zone);
        updateTree();
    }

    private boolean isAdjacent(LevelBlock block, LevelBlock newBlock) {
        for (Coordinates coordinates : block.getCoordinatesSet()) {
            for (Coordinates coordinates1 : newBlock.getCoordinatesSet()) {
                if (coordinates.isAdjacent(coordinates1)) {
                    return true;
                }

            }

        }
        return false;
    }

    public void moveBlock() {
        //TODO tricky!..
    }

    @Override
    public void removeCells() {
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            getModel().getBlock().getCoordinatesSet().remove(c);
        }
        for (Integer integer : getSelectionHandler().getSelection().getIds()) {
            getModel().getBlock().getCoordinatesSet().remove(getIdManager().getObjectById(integer).getCoordinates());
        }
    }

    @Override
    public void addCells() {
        Set<Coordinates> set = getModel().getBlock().getCoordinatesSet();
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            set.add(c);
        }
        for (Integer integer : getSelectionHandler().getSelection().getIds()) {
            set.add(getIdManager().getObjectById(integer).getCoordinates());
        }
    }

    public void transformBlock() {
        //getOperationHandler().operation(Operation.LE_OPERATION.REMOVE_OBJ, transform);
    }


    @Override
    public void assignBlock() {
        LevelBlock block = getModel().getBlock();
        block.getZone().getSubParts().remove(block);
        getModel().getZone().addBlock(block);
        updateTree();
    }

    public Color getColorForBlock(LevelBlock block) {
        int i = 0;
        for (LevelBlock subPart : block.getZone().getSubParts()) {
            if (block == subPart) {
                break;
            }
            i++;
        }
        int max = BLOCK_COLORS.length - 1;
        if (i >= max) {
            i = i % max;
        }
        return BLOCK_COLORS[i];
    }

    public LevelZone findZone(int index) {
        for (Module module : getFloorWrapper().getModules()) {
            for (LevelZone zone : module.getZones()) {
                if (zone.getIndex() == index) {
                    return zone;
                }
            }

        }
        return null;
    }

    public void reset(LevelStruct<LevelStruct, LevelStruct> layer) {
        resetWalls(layer);
        resetCells(layer);
        for (LevelStruct subPart : layer.getSubParts()) {
            reset(subPart);
        }
    }

    public void initWall(Coordinates c) {
        resetWalls(ImmutableSet.of(c));
    }

    public void resetWalls(LevelStruct<LevelStruct, LevelStruct> subPart) {
        resetWalls(subPart.getCoordinatesSet());
    }

    private void resetWalls(Set<Coordinates> coordinatesSet) {
        getGame().getBattleFieldManager().resetWalls(coordinatesSet);
    }


    private void resetCells(LevelStruct<LevelStruct, LevelStruct> layer) {
        DungeonEnums.CELL_IMAGE type = layer.getCellType();
        CONTENT_CONSTS.COLOR_THEME theme = layer.getColorTheme();
        if (type != null) {
            Set<DC_Cell> set = new LinkedHashSet<>();
            for (Coordinates coordinates : layer.getCoordinatesSet()) {
                DC_Cell cell = getGame().getCellByCoordinate(coordinates);
                if (cell != null) //TODO without buffer!
                    if (cell.getCellType() != type) {
                        set.add(cell);
                        cell.setCellType(type);
                        cell.resetCell(false);
                        cell.setColorTheme(theme);
                    }
            }
            GuiEventManager.trigger(GuiEventType.CELL_RESET, set);
            log(1, type + " cell type from " + layer.getName() +
                    "; for cell: " + set.size());
        }

    }


    private LevelBlock createBlock(LevelZone zone, Set<Coordinates> coordinates) {
        LevelBlock block = new LevelBlock(zone);
        return initBlockCoords(block, coordinates);

    }

    private LevelBlock initBlockCoords(LevelBlock block, Set<Coordinates> coordinates) {
        block.setCoordinates(coordinates);
        block.setOrigin(CoordinatesMaster.getUpperLeftCornerCoordinates(coordinates));
        int w = CoordinatesMaster.getWidth(block.getCoordinatesSet());
        int h = CoordinatesMaster.getHeight(block.getCoordinatesSet());
        block.setWidth(w);
        block.setHeight(h);
        block.setData(new BlockData((block)));
        return block;
    }

    public LevelBlock addBlock(ROOM_TYPE type, String name, Set<Coordinates> coordinates) {
        LevelZone z = getModel().getZone();
        LevelBlock block = createBlock(z, coordinates);
        block.setRoomType(type);
        block.setName(name);
        if (addBlock(block)) {
            return block;
        }
        return null;
    }
}
