package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import main.entity.DataModel;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;

import java.util.List;
import java.util.stream.Collectors;

public class UnitsData extends DataUnit<PARTY_VALUE> {
    public static final Boolean FORMAT = false;

    public UnitsData(String data) {
        super(data);
    }

    public UnitsData() {

    }

    public UnitsData(List<Coordinates> coordinatesList, List<ObjType> types) {
        setValue(PARTY_VALUE.UNITS,
                ContainerUtils.joinList(types.stream().
                        map(DataModel::getName).collect(Collectors.toList())));
        setValue(PARTY_VALUE.COORDINATES, ContainerUtils.joinList(coordinatesList) );
    }

    @Override
    public Boolean getFormat() {
        return FORMAT;
    }

    public enum PARTY_VALUE {
        LEADER, UNITS,
        PLAYER_NAME,

        SPAWN_SIDE,
        COORDINATES,
        FACING,
        LEVEL,
        FACING_TEMPLATE,

    }


}
