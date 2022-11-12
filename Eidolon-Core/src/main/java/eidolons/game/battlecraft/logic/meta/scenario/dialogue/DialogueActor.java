package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.content.PROPS;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueActor extends LightweightEntity {
    private Unit linkedUnit;

    public DialogueActor(ObjType type) {
        super(type);
//        name, portrait, description
//        toBase(); copy maps?
    }

    @Override
    public Game getGame() {
        return Core.getGame();
    }

    public Unit getLinkedUnit() {
        return linkedUnit;
    }

    public void setupLinkedUnit() {
        if (getName().equalsIgnoreCase("you")) {
            setLinkedUnit(Core.getMainHero());
            return;
        }
        Coordinates c = Core.getPlayerCoordinates();
        List<Coordinates> area = CoordinatesMaster.getCoordinatesWithin(
                Math.max(0, c.getX() - 10),
                Math.min(((DC_Game) getGame()).getDungeon().getCellsX(), c.getX() + 10),
                Math.max(0, c.getY() - 10),
                Math.min(((DC_Game) getGame()).getDungeon().getCellsY(), c.getY() + 10)
        );
        List<String> names = new ArrayList<>(ContainerUtils.openContainer(getProperty(PROPS.ACTOR_UNIT_NAMES)));
        names.add(0, getName());
        for (Unit unit : ((DC_Game) getGame()).getUnitsForCoordinates(area.toArray(new Coordinates[0]))) {
            for (String substring : names) {
                if (unit.getName().equalsIgnoreCase(substring)) {
//                    if (!unit.isActorLinked()) {
                        setLinkedUnit(unit);
                        break;

                }

            }
        }

    }

    public void setLinkedUnit(Unit linkedUnit) {
        if (linkedUnit != null) {
            linkedUnit.setActorLinked(true);
        } else {
//            this.linkedUnit.setActorLinked(fireParamEvent());
        }
        this.linkedUnit = linkedUnit;
    }
}
