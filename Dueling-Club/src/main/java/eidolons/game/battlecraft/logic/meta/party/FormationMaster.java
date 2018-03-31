package eidolons.game.battlecraft.logic.meta.party;

import eidolons.client.cc.logic.party.Party;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.party.warband.Warband;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.entity.Entity;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon.POINTS;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 5/30/2017.
 */
public class FormationMaster extends MetaPartyHandler {

    public static final int MAX_UNITS_PER_FORMATION = 5;

    public FormationMaster(MetaGameMaster master) {
        super(master);
    }

    public void promptFormationChange() {

//        showFormationWindow(this);

    }

    public void formationSet(FORMATION formation, Entity hero) {

    }

    public void scheduleFormations(Warband warband) {
        warband.getFormationsMap().keySet().forEach(formation ->
         {
             int round = getDefaultRoundForFormation(formation);
         }
        );


    }

    private int getDefaultRoundForFormation(FORMATION formation) {
        switch (formation) {
            case CENTER:
                return 5;
            case REAR:
                return 8;
            case SCOUTING:
                return 12;
        }
        return 0;
    }

    public void createFormationParty(Unit leader, Warband warband, Unit... units) {

    }

    public void formationArrives(FORMATION formation, Party party) {

        Coordinates origin = getDefaultPoint(getMaster().getDungeonMaster().
         getDungeon(), formation
        );
        FACING_DIRECTION facing;
//calculateOrganizationValue
    }

    private Coordinates getDefaultPoint(Dungeon dungeon, FORMATION formation) {
        String arg = "";
        switch (formation) {
            case CENTER:
                arg = POINTS.CENTER_SPAWN.toString();
                break;
            case REAR:
                arg = POINTS.REAR_SPAWN.toString();
                break;
            case SCOUTING:
                arg = POINTS.SCOUTS_SPAWN.toString();
                break;
        }
        Coordinates p = dungeon.getPoint(arg);
        if (p == null)
            p = dungeon.getPoint(EnumMaster.getEnumConstIndex(POINTS.class, formation));

        return p;
    }

    public enum FORMATION {
        VANGUARD,
        CENTER,
        REAR,
        SCOUTING,
    }


}
