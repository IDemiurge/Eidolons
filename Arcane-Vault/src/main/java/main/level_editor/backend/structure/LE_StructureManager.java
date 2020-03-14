package main.level_editor.backend.structure;

import eidolons.game.module.dungeoncrawl.dungeon.BlockTemplate;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.data.FileManager;

import java.util.List;

public class LE_StructureManager extends LE_Handler implements IStructureManager{


    public LE_StructureManager(LE_Manager manager) {
        super(manager);
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
    public void mergeCurBlock() {

    }

    @Override
    public void removeSelectedCellsFromCurBlock() {

    }

    @Override
    public void addSelectedCellsToCurBlock() {

    }

    @Override
    public void transformBlock() {

    }

    @Override
    public void insertBlock() {

    }

    @Override
    public void assignBlockToZone() {

    }

    @Override
    public void addModule() {

    }

    @Override
    public void transformModule() {

    }
}
