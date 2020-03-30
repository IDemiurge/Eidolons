package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.BLOCK_VALUE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Block;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;

public class BlockData extends LevelStructure.StructureData<BLOCK_VALUE, LE_Block> {

private   LevelBlock block;

public BlockData(LE_Block block) {
    super(block);
    this.block = block.getBlock() ;
}

    @Override
    public BlockData setData(String data) {
        return (BlockData) super.setData(data);
    }

    @Override
    protected void init() {
        setValue(BLOCK_VALUE.zone, this.block.getZone().getIndex());
        setValue(BLOCK_VALUE.main_wall_type,  block.getWallType());
        setValue(BLOCK_VALUE.cell_type,  block.getCellType());
        setValue(BLOCK_VALUE.height,  block.getHeight());
        setValue(BLOCK_VALUE.width,  block.getWidth());
        setValue(BLOCK_VALUE.name,  block.toString());
        setValue(BLOCK_VALUE.room_type,  block.getRoomType());
    }

    @Override
    public Class<? extends BLOCK_VALUE> getEnumClazz() {
        return BLOCK_VALUE.class;
    }

    public void apply() {
    LevelZone zone = DC_Game.game.getMetaMaster().getDungeonMaster().
        getDungeonLevel().getZoneById(getIntValue(BLOCK_VALUE.zone));
    block.setZone(zone);
    block.setWidth(getIntValue(BLOCK_VALUE.width));
    block.setHeight(getIntValue(BLOCK_VALUE.height));
    try {
        DungeonLevel.CELL_IMAGE type = DungeonLevel.CELL_IMAGE.valueOf(getValue(BLOCK_VALUE.cell_type));
        block.setCellType(type);
    } catch (Exception e) {
        main.system.ExceptionMaster.printStackTrace(e);
    }

    LocationBuilder.ROOM_TYPE type =  LocationBuilder.ROOM_TYPE.valueOf(
            getValue(BLOCK_VALUE.room_type).toUpperCase());

    block.setRoomType(type);
    block.setWallType(getValue(BLOCK_VALUE.main_wall_type));
//            block.getOriginalTileMap()
    //replace walls?

}

    public LevelBlock getBlock() {
        return block;
    }
}
