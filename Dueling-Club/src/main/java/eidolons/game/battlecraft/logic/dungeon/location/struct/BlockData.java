package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.BLOCK_VALUE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.game.bf.Coordinates;

public class BlockData extends StructureData<BLOCK_VALUE,  LevelBlock> {

    private LevelBlock block;

    public BlockData(LevelBlock block) {
        super(block);
    }

    @Override
    public BlockData setData(String data) {
        return (BlockData) super.setData(data);
    }

    @Override
    protected void init() {
        if (block == null) {
            this.block = getStructure() ;
            levelStruct = block;
            if (block.getData() != null) {
                setData(block.getData().getData());
            }
        }
        setValue(BLOCK_VALUE.height, block.getHeight());
        setValue(BLOCK_VALUE.width, block.getWidth());
        setValue(BLOCK_VALUE.name, block.toString());
        setValue(BLOCK_VALUE.wall_type, block.getWallType());
        setValue(BLOCK_VALUE.cell_type, block.getCellType());
        setValue(BLOCK_VALUE.room_type, block.getRoomType());
        setValue(BLOCK_VALUE.origin, block.getOrigin().toString());
    }

    @Override
    public Class<? extends BLOCK_VALUE> getEnumClazz() {
        return BLOCK_VALUE.class;
    }

    public void apply() {
        if (block == null) {
            this.block = getStructure() ;
        }
        block.setData(this);
        block.setWidth(getIntValue(BLOCK_VALUE.width));
        block.setHeight(getIntValue(BLOCK_VALUE.height));
        block.setOrigin(Coordinates.get(getValue(BLOCK_VALUE.origin)));
        if (!getValue(BLOCK_VALUE.room_type).isEmpty()) {
            LocationBuilder.ROOM_TYPE type = LocationBuilder.ROOM_TYPE.valueOf(
                    getValue(BLOCK_VALUE.room_type).toUpperCase());
            block.setRoomType(type);
        }
//            block.getOriginalTileMap()
        //replace walls?

    }

    public LevelBlock getBlock() {
        return block;
    }
}