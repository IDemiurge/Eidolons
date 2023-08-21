package combat;

import combat.battlefield.BattleField;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;

import java.util.Set;

/**
 * Created by Alexander on 6/10/2023
 * Replay - set of states? 
 */
public class Battle {
    Set<Unit> units; //what about ID's? Do we need them beyond some in-game reference?
    Set<FieldEntity> obstacles;
    Set<FieldEntity> omens;

    BattleField battleField;
    BattleSetup battleSetup;
    // BattleField battleAi;

}
