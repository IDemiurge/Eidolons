package main.level_editor.backend.functions.advanced;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.libgdx.bf.grid.moving.PlatformData;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static main.level_editor.gui.dialog.struct.PlatformEditDialog.EDIT_DONE;

public class IPlatformHandlerImpl extends LE_Handler implements IPlatformHandler {
    private static int ID = 0;
    Map<LevelBlock, PlatformData> platforms = new LinkedHashMap<>();
    private String platformData = "";
    private String platformCopy;
    public IPlatformHandlerImpl(LE_Manager manager) {
        super(manager);
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
    public void initPlatforms(String textContent) {
        if (!textContent.isEmpty()) {
            platformData += textContent + StringMaster.VERTICAL_BAR;
        }

    }
    public void platform_copy() {

        LevelBlock block = getModelManager().getModel().getBlock();
        if (block == null) {
            block = (LevelBlock) getStructureMaster().getLowestStruct(getSelectionHandler().getSelection().getLastCoordinates());
        }
        PlatformData platform = platforms.get(block);
        platformCopy = platform.getData();
    }
    public void platformBlockRemoved(LevelBlock block) {
        platforms.remove(block);
        GuiEventManager.trigger(GuiEventType.PLATFORM_REMOVE, block.getName());
    }



    @Override
    public void platform() {
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
    @Override
    public void obstacle() {

    }

    @Override
    public void move() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void destination() {

    }

}
