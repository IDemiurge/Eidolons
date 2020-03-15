package main.level_editor.backend.functions.structure;

import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LE_StructureManager extends LE_Handler implements IStructureManager{


    public LE_StructureManager(LE_Manager manager) {
        super(manager);
    }

    private LevelBlock getBlock() {
        return getModel().getBlock();
    }

    public void addZone(){
        //at first, it is EMPTY
        // then assign blocks...


        //should we have a number of Floor templates?
        List<LevelZone> zones = getModel().getModule().getZones();
        if (zones.isEmpty()){

        }
        LevelZone zone = new LevelZone(zones.size()); //or default per floor template
        getModel().setCurrentZone(zone);
        LevelBlock block= new LevelBlock(zone);

        zone.addBlock(block); //de
    }

    @Override
    public void insertBlock() {
        LevelData data = new LevelData("");
        data.setTemplateGroups(new ROOM_TEMPLATE_GROUP[]{ROOM_TEMPLATE_GROUP.TEMPLE});
        RoomTemplateMaster templateMaster = new RoomTemplateMaster(data);
        ROOM_TEMPLATE_GROUP room_template_group
                = LE_Screen.getInstance().getGuiStage().getEnumChooser()
                .choose(templateMaster.getModels().keySet().toArray(new ROOM_TEMPLATE_GROUP[0]),
                        ROOM_TEMPLATE_GROUP.class);

        Set<RoomModel> from = templateMaster.getModels().get(room_template_group);
        from.removeIf(model -> model == null);
        RoomModel template = LE_Screen.getInstance().getGuiStage().getTemplateChooser()
                .choose(from);
        Coordinates c = getSelectionHandler().selectCoordinate();
        insertBlock(template, c);
    }
    public void insertBlock(RoomModel blockTemplate, Coordinates at){
        //confirm if TRANSFORM

        //what about exits? 

        LevelZone zone = getModel().getCurrentZone();
        LevelBlock block = new LevelBlock(  zone);
//        zone.addBlock(block);
        int x=0;
        int y=0;
        List<Coordinates> coords=    new ArrayList<>() ;
        for (String[] column : blockTemplate.getCells()) {
            for (String cell : column) {
                if (TilesMaster.isIgnoredCell(cell)) {
                    continue;
                }
                Coordinates c;
                coords.add(c=Coordinates.get(x, blockTemplate.getHeight()- y +1).getOffset(at));
                processCell(c, cell);
                y++;
            }
            y=0;
            x++;
        }
        block.setCoordinatesList(coords);
        updateTree();
    }
    @Override
    public void updateTree() {
        GuiEventManager.trigger(GuiEventType.LE_TREE_RESET);
    }

    private void processCell(Coordinates c, String cell) {

        switch (GeneratorEnums.ROOM_CELL.getBySymbol(cell)) {
            case WALL:
                initWall(c);
                break;
        }
    }

    private void initWall(Coordinates c) {
       ObjType type=  getModelManager().getDefaultWallType();
       getModelManager().addObj(type, c.x, c.y );
    }

    @Override
    public void removeZone() {

    }

    @Override
    public void addBlock() {

    }

    @Override
    public void removeBlock() {

    }

    @Override
    public void moveBlock() {

    }
 

    @Override
    public void mergeBlock() {
        LevelBlock block = getBlock();
        EUtils.info("Select an adjacent block");
        WaitMaster.waitForInput(LevelEditor.SELECTION_EVENT);
        LevelBlock newBlock = getBlock();
        if (isAdjacent(block, newBlock)){
            mergeBlocks(block , newBlock);
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

    }

    @Override
    public void addCellsToBlock() {

    }


    @Override
    public void transformBlock() {

    }


    @Override
    public void assignBlock() {

    }

}
