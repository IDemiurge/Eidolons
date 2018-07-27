package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.game.module.herocreator.logic.party.Party;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;

import java.util.stream.Collectors;

public class UnitData extends DataUnit<PARTY_VALUE> {


    public static final Boolean FORMAT = false;

    public UnitData(Party party) {
        this(PARTY_VALUE.MEMBERS +
         DataUnitFactory.getPairSeparator(FORMAT) +
         ContainerUtils.joinList(party.getMembers().stream().
           map(s -> s.getName()).collect(Collectors.toList())
//         ,
         ) + DataUnitFactory.getSeparator(FORMAT));
    }

    public UnitData(String data) {
        super(data);
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
        LEADER, MEMBERS,
        PLAYER_NAME,

        SPAWN_SIDE,
        COORDINATES,
        FACING,
        LEVEL,
        FACING_TEMPLATE,

    }


}
