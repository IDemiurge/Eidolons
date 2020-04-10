package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Floor;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.FLOOR_VALUES;

public class FloorData extends StructureData<FLOOR_VALUES, LE_Floor> {

    public FloorData(LE_Floor structure) {
        super(structure);
    }

    @Override
    public void apply() {
        //sink thru to DUNGEON?
        super.apply();
        getStructure().setData(this);
        Dungeon dungeon = getStructure().getDungeon();
        dungeon.setProperty(PROPS.MAP_BACKGROUND, getValue(FLOOR_VALUES.background), true);
        //why type??
        dungeon.setName(getValue(FLOOR_VALUES.name));

        GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, dungeon.getMapBackground());
    }

    @Override
    public LevelStruct getLevelStruct() {
        return DC_Game.game.getDungeonMaster().getDungeonLevel();
    }

    @Override
    protected void init() {
        if (getStructure().getData() != null) {
            setData(getStructure().getData().getData());
        }
        setValue(FLOOR_VALUES.name, getStructure().getDungeon().getName());
        setValue(FLOOR_VALUES.background, getStructure().getDungeon().getMapBackground());
    }

    @Override
    public Class<? extends FLOOR_VALUES> getEnumClazz() {
        return FLOOR_VALUES.class;
    }
}
