package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.FLOOR_VALUES;

public class FloorData extends StructureData<FLOOR_VALUES, Location> {

    public FloorData(Location structure) {
        super(structure);
    }

    private static String[] cropped;

    @Override
    public String[] getValuesCropped() {
        if (cropped == null) {
            List<String> list = Arrays.stream(getEnumClazz().getEnumConstants()).map(Enum::toString).
                    collect(Collectors.toList());
            list.remove(FLOOR_VALUES.height.toString());
            list.remove(FLOOR_VALUES.width.toString());
            list.remove(FLOOR_VALUES.id.toString());
            list.remove(FLOOR_VALUES.illumination.toString());
            list.remove(FLOOR_VALUES.soundscape.toString());
            list.remove(FLOOR_VALUES.ambience.toString());
            list.remove(FLOOR_VALUES.vfx_template.toString());
            list.remove(FLOOR_VALUES.module_grid.toString());
            list.remove(FLOOR_VALUES.cell_spans.toString());
            cropped = list.toArray(new String[0]);
        }
        return cropped;
    }

    @Override
    public void apply() {
        //sink thru to DUNGEON?
        super.apply();
        getStructure().setData(this);
        Floor floor = getStructure().getFloor();
        floor.setProperty(PROPS.MAP_BACKGROUND, getValue(FLOOR_VALUES.background), true);
        //why type??
        floor.setName(getValue(FLOOR_VALUES.name));
        getStructure().setWidth(getIntValue(FLOOR_VALUES.width));
        getStructure().setHeight(getIntValue(FLOOR_VALUES.height));

        GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, floor.getMapBackground());
    }

    @Override
    public LevelStruct getLevelStruct() {
        return DC_Game.game.getDungeonMaster().getFloorWrapper();
    }

    @Override
    protected void init() {
        if (getStructure().getData() != null) {
            setData(getStructure().getData().getData());
        }
        //support manual resizing?
        setValue(FLOOR_VALUES.width, getStructure().getWidth());
        setValue(FLOOR_VALUES.height, getStructure().getHeight());
        setValue(FLOOR_VALUES.name, getStructure().getFloor().getName());
        setValue(FLOOR_VALUES.background, getStructure().getFloor().getMapBackground());
    }

    @Override
    public Class<? extends FLOOR_VALUES> getEnumClazz() {
        return FLOOR_VALUES.class;
    }
}
