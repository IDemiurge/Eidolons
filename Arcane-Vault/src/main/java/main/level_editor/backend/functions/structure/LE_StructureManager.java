package main.level_editor.backend.functions.structure;

import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.BlockTemplate;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.FileManager;
import main.system.threading.WaitMaster;

import java.util.List;

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

    public void insertBlock(String templatePath, Coordinates at){
        LevelZone zone = getModel().getCurrentZone();
        //check ot her zone
        String stringData = FileManager.readFile(templatePath);
        BlockTemplate blockTemplate = new BlockTemplate(stringData);
        LevelBlock block = new LevelBlock(blockTemplate, zone);
        zone.addBlock(block);

        for (ObjAtCoordinate object : block.getObjects()) {
            //init
            //what layer?
            //transform into default walls!
        }
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
    public void moveZone() {

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
    public void insertBlock() {

    }

    @Override
    public void assignBlock() {

    }

}
