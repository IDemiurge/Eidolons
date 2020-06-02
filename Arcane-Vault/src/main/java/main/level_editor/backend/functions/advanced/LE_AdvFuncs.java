package main.level_editor.backend.functions.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.bf.grid.moving.PlatformData;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.threading.WaitMaster;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.*;
import static main.level_editor.gui.dialog.struct.PlatformEditDialog.EDIT_DONE;

public class LE_AdvFuncs extends LE_Handler implements IAdvFuncs {

    private static int ID = 0;
    Map<LevelBlock, PlatformData> platforms = new LinkedHashMap<>();
    private String platformData = "";
    private String platformCopy;

    public LE_AdvFuncs(LE_Manager manager) {
        super(manager);

    }

    public void platform_copy() {

        LevelBlock block = getModelManager().getModel().getBlock();
        if (block == null) {
        block = (LevelBlock) getStructureMaster().getLowestStruct(getSelectionHandler().getSelection().getLastCoordinates());
        }
        PlatformData platform = platforms.get(block);
        platformCopy = platform.getData();
    }

    @Override
    public void platform() {
        //prompt edit dialogue immediately
        //create a block for it? of type PLATFORM, which will be checked
        // link block and platform - ?

        // init them - via overridden Handler? blocks are persistent and well, if we just link by name...

        // we need findByName anyway

        // create block with same name then
        // standard data by default perhaps
        Set<Coordinates> coordinates = getSelectionHandler().getSelection().getCoordinates();

        PlatformData data = new PlatformData(coordinates);
        String name = data.getValue(PlatformData.PLATFORM_VALUE.name);


        if (platformCopy == null) {
            getDialogHandler().editData(data);
            boolean o = (boolean) WaitMaster.waitForInput(EDIT_DONE);
            if (!o)
                return;
        } else {
            data.setData(platformCopy);
            platformCopy=null;
            data.setCells(coordinates);
        }
        if (name.isEmpty()) {
            name = getDialogHandler().textInput("Platform unique name...", "Platform N" + ID++);
        }
        data.setValue(PlatformData.PLATFORM_VALUE.name, name);
        LevelBlock block =
                getStructureHandler().addBlock(LocationBuilder.ROOM_TYPE.PLATFORM,
                        name, coordinates);
        // block.getData().setValue(LevelStructure.BLOCK_VALUE.cell_type,
        //         data.getValue(PlatformData.PLATFORM_VALUE.cell_type));
        GuiEventManager.trigger(GuiEventType.PLATFORM_CREATE, data);
        platforms.put(block, data);
        getStructureHandler().updateTree();

        //so is it void?
    }

    public void platformBlockRemoved(LevelBlock block) {
        platforms.remove(block);
        GuiEventManager.trigger(GuiEventType.PLATFORM_REMOVE, block.getName());
    }

    @Override
    public void afterLoaded() {
        for (String substring : ContainerUtils.openContainer(platformData, StringMaster.VERTICAL_BAR)) {
            if (substring.isEmpty()) {
                continue;
            }
            PlatformData data = new PlatformData(substring);
            String name = data.getValue(PlatformData.PLATFORM_VALUE.name);
            LevelBlock block = getStructureMaster().findBlockByName(name);
            if (block == null) {
                continue;
            }
            platforms.put(block, data);
            ID++;
        }
        for (LevelBlock block : platforms.keySet()) {
            GuiEventManager.trigger(GuiEventType.PLATFORM_CREATE, platforms.get(block));
        }
    }

    public String getPlatformData(Module module) {
        String data = "";
        for (LevelBlock block : platforms.keySet()) {
            if (block.getModule() == module) {
                data += platforms.get(block).getData() + StringMaster.VERTICAL_BAR;
            }
        }
        return data;
    }

    @Override
    public void fill() {
        Set<Coordinates> area = getSelectionHandler().getSelection().getCoordinates();
        if (area.isEmpty()) {
            area = getSelectionHandler().selectArea();
        }
        if (area.isEmpty()) {
            return;
        }
        operation(FILL_START);
        ObjType type = getModel().getPaletteSelection().getObjType();

        for (Coordinates coordinates : area) {
            operation(ADD_OBJ, type, coordinates);
        }
        operation(FILL_END);

    }

    @Override
    public void clear() {
        Set<Coordinates> area = getSelectionHandler().getSelection().getCoordinates();

        if (area.isEmpty()) {
            return;
        }
        operation(CLEAR_START);
        for (Coordinates coordinates : area) {
            getObjHandler().clear(coordinates);
        }
        operation(CLEAR_END);
    }

    @Override
    public void toggleVoid() {

        operation(CLEAR_START);
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            operation(VOID_TOGGLE, c);
        }
        operation(CLEAR_END);
    }

    @Override
    public void setVoid() {

        operation(CLEAR_START);
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            operation(VOID_SET, c);
        }
        operation(CLEAR_END);
    }
    public void mirror() {
        //TODO
        LE_Selection selection = getModel().getSelection();
        Set<Coordinates> coordinates = selection.getCoordinates();
        boolean fromSelection = false;
        if (!ListMaster.isNotEmpty(coordinates)) {
            LevelStruct struct = getModel().getLastSelectedStruct();
            coordinates = struct.getCoordinatesSet();
        }
        //including void,
        int w = CoordinatesMaster.getWidth(coordinates);
        int h = CoordinatesMaster.getHeight(coordinates);
        DIRECTION d = getDialogHandler().chooseEnum(DIRECTION.class);
        if (fromSelection) {
            //            axis = left ? :;
        }

        Coordinates origin = CoordinatesMaster.getUpperLeftCornerCoordinates(coordinates);
        operation(FILL_START);
        Set<Coordinates> toVoid = new LinkedHashSet<>();
        {
            for (Coordinates c : coordinates) {
                Coordinates c1 = c.getOffset(getMirroredCoordinate(c.getOffset(-origin.x, -origin.y), d, w, h));
                boolean VOID = getGame().getCellByCoordinate(c).isVOID();
                if (VOID) {
                    toVoid.add(c1);
                    continue;
                }
                Set<BattleFieldObject> objects = getGame().getObjectsOnCoordinateAll(c);
                //                c = c.getOffset(origin);
                for (BattleFieldObject object : objects) {
                    getObjHandler().copyTo(object, c1);
                }
            }
        }
        operation(MASS_SET_VOID, toVoid);

        operation(FILL_END);
    }

    private Coordinates getMirroredCoordinate(Coordinates c, DIRECTION d, int w, int h) {
        int offsetX = 0;
        int offsetY = 0;
        if (d.growX != null) {
            offsetX = d.growX ? w - c.x : -c.x;
        }
        if (d.growY != null) {
            offsetY = d.growY ? h - c.y : -c.y;
        }
        return Coordinates.get(true, offsetX, offsetY);
    }

    @Override
    public void rotate() {

    }

    @Override
    public void replace() {
        Set<BattleFieldObject> toReplace = new LinkedHashSet<>();
        boolean overlaying = getSelectionHandler().getObject().isOverlaying();
        String name = getSelectionHandler().getObject().getName();
        LevelStruct struct = getModel().getLastSelectedStruct();
        for (Object o : struct.getCoordinatesSet()) {
            for (BattleFieldObject object : getGame().getObjectsOnCoordinate((Coordinates) o, true)) {
                if (object.getName().equalsIgnoreCase(name)) {
                    toReplace.add(object);
                }
            }
        }
        ObjType replacing = overlaying ? getModel().getPaletteSelection().getObjType() : getModel().getPaletteSelection().getObjType();

        if (EUtils.waitConfirm("Replace " +
                toReplace.size() +
                " objects with " + replacing.getName())) {
            replace(toReplace, replacing, overlaying);
        }
    }

    private void replace(Set<BattleFieldObject> toReplace, ObjType replacing, boolean overlaying) {
        operation(FILL_START);
        for (BattleFieldObject object : toReplace) {
            if (overlaying) {
                operation(REMOVE_OVERLAY, object);
                operation(ADD_OVERLAY, replacing, object.getCoordinates(), object.getDirection());
            } else {
                operation(REMOVE_OBJ, object);
                operation(ADD_OBJ, replacing, object.getCoordinates());
            }
        }
        operation(FILL_END);
    }

    @Override
    public void repeat() {
        switch (getOperationHandler().lastOperation.getOperation()) {
            case FILL_END:
                fill();
                break;
            case CLEAR_END:
                clear();
                break;
            case INSERT_END:
                //                fill();
                break;
            case PASTE_END:
                //                fill();
                break;

        }
    }

    public void initPlatforms(String textContent) {
        if (!textContent.isEmpty()) {
            platformData += textContent + StringMaster.VERTICAL_BAR;
        }

    }
}
