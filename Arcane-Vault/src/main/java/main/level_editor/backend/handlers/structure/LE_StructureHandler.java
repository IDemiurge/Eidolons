package main.level_editor.backend.handlers.structure;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.init.RngTypeChooser;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import eidolons.libgdx.GdxColorMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.data.DataManager;
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
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            GdxColorMaster.PURPLE,
            GdxColorMaster.CYAN,
            GdxColorMaster.BLUE,
            GdxColorMaster.RED,
            GdxColorMaster.BEIGE,
            GdxColorMaster.GREEN,
    };

    private RoomTemplateMaster roomTemplateMaster;

    public LE_StructureHandler(LE_Manager manager) {
        super(manager);
    }

    private LevelBlock getBlock() {
        return getModel().getBlock();
    }

    public void addZone() {
        //should we have a number of Floor templates?
        List<LevelZone> zones = getModel().getModule().getZones();
        LevelZone zone = new LevelZone(zones.size()); //or default per floor template

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

        LocationBuilder.ROOM_TYPE type = LocationBuilder.ROOM_TYPE.THRONE_ROOM;
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

        LevelZone zone = getModel().getZone();
        if (zone == null) {
            zone = getModel().getModule().getZones().get(0);
        }
        LevelBlock block = new LevelBlock(blockTemplate, zone);
        zone.addBlock(block);
        int x = 0;
        int y = 0;
        List<Coordinates> coords = new ArrayList<>();
        getOperationHandler().operation(Operation.LE_OPERATION.INSERT_START);
        for (String[] column : blockTemplate.getCells()) {
            for (String cell : column) {
                if (TilesMaster.isIgnoredCell(cell)) {
                    continue;
                }
                Coordinates c;
                coords.add(c = Coordinates.get(x, y).getOffset(at));
                processCell(c, cell);
                y++;
            }
            y = 0;
            x++;
        }
        getOperationHandler().operation(Operation.LE_OPERATION.INSERT_END);
        block.setCoordinatesList(coords);
        block.setOrigin(at);
        block.setWidth(blockTemplate.getWidth());
        block.setHeight(blockTemplate.getHeight());
//block.setName(blockTemplate);
        block.setRoomType(blockTemplate.getType());


        updateTree();
    }

    @Override
    public void updateTree() {
        getModel().setTreeModel(LevelEditor.getCurrent());
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

    private void initObject(Coordinates c, GeneratorEnums.ROOM_CELL s) {
        ObjType type = getObjectType(c, s);
        operation(Operation.LE_OPERATION.ADD_OBJ, type, c);
    }

    private ObjType getObjectType(Coordinates c, GeneratorEnums.ROOM_CELL s) {
        switch (s) {
            case WALL:
                return   getModelManager().getDefaultWallType();
        }
        DungeonEnums.DUNGEON_STYLE style = getModel().getZone().getStyle();
       return  RngTypeChooser.getType(s, style, false);
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
        Set<Coordinates> coordinates = getSelectionHandler().getSelection().getCoordinates();
        LevelBlock block = new LevelBlock(zone);

        block.setName("Custom block");
        block.setOrigin(coordinates.iterator().next());
        block.setCoordinatesList(new ArrayList<>(coordinates));
    }

    @Override
    public void removeBlock() {
        LevelBlock block = getModel().getBlock();
        if (block != null) {
            getOperationHandler().operation(Operation.LE_OPERATION.CLEAR_START);
            for (Coordinates coordinates : block.getCoordinatesSet()) {
                for (BattleFieldObject battleFieldObject : getGame().getObjectsAt(coordinates)) {
                    getOperationHandler().operation(Operation.LE_OPERATION.REMOVE_OBJ, battleFieldObject);
                }
                //full clear - scripts, ..?
            }
            getOperationHandler().operation(Operation.LE_OPERATION.CLEAR_END);
            block.getZone().getSubParts().remove(block);
            updateTree();
        }
    }

    @Override
    public void moveBlock() {
//TODO tricky!..
    }


    @Override
    public void mergeBlock() {
        LevelBlock block = getBlock();
        EUtils.infoPopup("Select an adjacent block");
        WaitMaster.waitForInput(LevelEditor.SELECTION_EVENT);
        LevelBlock newBlock = getBlock();
        if (isAdjacent(block, newBlock)) {
            mergeBlocks(block, newBlock);
        }
        GuiEventManager.trigger(GuiEventType.LE_TREE_RESET);
    }

    private void mergeBlocks(LevelBlock block, LevelBlock newBlock) {
    }

    private boolean isAdjacent(LevelBlock block, LevelBlock newBlock) {
        return false;
    }


    @Override
    public void removeCellsFromBlock() {
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            getModel().getBlock().getCoordinatesSet().remove(c);
        }
    }

    @Override
    public void addCellsToBlock() {
        Set<Coordinates> set = getModel().getBlock().getCoordinatesSet();
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
           set.add(c);
        }
    }


    @Override
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
        int i = block.getZone().getSubParts().indexOf(block);
        int max = BLOCK_COLORS.length;
        if (i > max) {
            i = i % max;
        }
        return BLOCK_COLORS[i];
    }

    public LevelZone findZone(int index) {
        for (Module module : getFloor().getModules()) {
            for (LevelZone zone : module.getZones()) {
                if (zone.getIndex() == index) {
                    return zone;
                }
            }

        }
        return null;
    }

    public void blockReset(LevelBlock block) {
        for (Coordinates coordinates : block.getCoordinatesSet()) {
            DungeonLevel.CELL_IMAGE type = block.getCellType();
            getGame().getCellByCoordinate(coordinates).setCellType(type);
//            getGame().getCellByCoordinate(coordinates).setCellVariant(type);
            if (StringMaster.isEmpty(block.getWallType())) {
                return;
            }
            ObjType wallType = DataManager.getType(block.getWallType(), DC_TYPE.BF_OBJ);
            for (BattleFieldObject obj : getGame().getObjectsOnCoordinate(coordinates)) {
                if (obj.isWall()) {
                    getOperationHandler().execute(Operation.LE_OPERATION.REMOVE_OBJ, obj);
                    getOperationHandler().execute(Operation.LE_OPERATION.ADD_OBJ, obj.getCoordinates(), wallType);
                }
            }

        }
    }
}
