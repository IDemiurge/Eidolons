package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UnitsData extends DataUnit<PARTY_VALUE> {


    public static final Boolean FORMAT = false;

    public UnitsData(Party party) {
        this(PARTY_VALUE.UNITS +
         DataUnitFactory.getPairSeparator(FORMAT) +
         ContainerUtils.joinList(party.getMembers().stream().
           map(s -> s.getName()).collect(Collectors.toList())
//         ,
         ) + DataUnitFactory.getSeparator(FORMAT));
    }

    public UnitsData(String data) {
        super(data);
    }

    public UnitsData() {

    }

    public UnitsData(List<Coordinates> coordinatesList, List<ObjType> types) {
        setValue(PARTY_VALUE.UNITS,
                ContainerUtils.joinList(types.stream().
                        map(s -> s.getName()).collect(Collectors.toList())));
        setValue(PARTY_VALUE.COORDINATES, ContainerUtils.joinList(coordinatesList) );
    }

    @Override
    public Boolean getFormat() {
        return FORMAT;
    }


    /*
            coordinates?
            spawn type?
            player owner
            leader?
            facing?

            use always to spawn?
             would be great! First form consistent data fully, then process it!

            can come from a Mission file... a preset

            facing in () ?

            spawner processing:
            1) check spawn at (then ignore coords? or use offset) or init coords
            2) create for player at coords
            3) init party/group and leader
            4) init facing

             pipelines:
             Preset/Hardcore from LAUNCH
             Creeps for a Mission
             Party from Arcade
             Custom spawn()

             'spawn()' for an HQ party is in fact more like 'place()' ...
            > don't respawn if exists?


     */
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
