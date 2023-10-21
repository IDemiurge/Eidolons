package tests.field;

import elements.content.enums.FieldConsts.Cell;
import elements.exec.EntityRef;
import elements.exec.effect.Effect;
import elements.exec.targeting.Targeting;
import elements.exec.targeting.area.CellSets;
import elements.exec.targeting.area.MeleeTargeter;
import framework.entity.field.FieldEntity;
import framework.entity.sub.UnitAction;
import framework.field.Transformer;
import org.apache.commons.lang3.tuple.Pair;
import system.ListMaster;
import system.log.SysLog;
import tests.basic.BattleInitTest;

import java.util.*;

import static combat.sub.BattleManager.combat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 10/19/2023 Substitute diff targeting for unit's attack? targeting should at first return us
 * maybe a list of targets?!
 */
public class MeleeTargetingTest extends BattleInitTest {
    //TODO extract generic for future targeting tests
    private static Map<Cell, List<Cell>> checkPosMap; //separate for close/long
    public static final Cell[] testPos = CellSets.allyArea;

    private static final List<Cell> targets1 = Arrays.asList(new Cell[]{
            //TODO
    });

    static {
        // checkPosMap = new HashMap<>();
        // List<List<Cell>> list = List.of(targets1);
        //         // , targets2, targets3,
        //         // back_targets1, back_targets2, back_targets3);
        // Iterator<List<Cell>> iterator = list.iterator();
        // for (Cell pos : testPos) {
        //     checkPosMap.put(pos, iterator.next());
        // }
    }

    // public void testTargets(boolean full, boolean ) {
    // }
    @Override
    public void test() {
        super.test();

        //cells test
        for (Cell pos : testPos) {
            Set<Cell> cellSet = MeleeTargeter.getCellSet(pos, true, false, false);
            SysLog.printOut(pos,"can target:",ListMaster.represent(cellSet)+"\n");
        }

        //fill entire non-ally board with dummy enemies
        //for each position? check against a static map of position to targets?
        UnitAction action = ally.getActionSet().getStandard();
        for (Cell pos : testPos) {
            ally.setPos(combat().getField().getPos(pos));
            for (Pair<Targeting, Effect> targetedEffect : action.getExecutable().getTargetedEffects()) {
                Targeting targeting = targetedEffect.getLeft();
                EntityRef ref = new EntityRef(ally);
                List<FieldEntity> list = combat().getEntities().targetFilter(ref, targeting);
                List<Cell> positions = Transformer.toCells(list);

                assertTrue(checkPosMap.get(pos).containsAll(positions));

            }
        }
        // action.getExecutable().
    }
}
