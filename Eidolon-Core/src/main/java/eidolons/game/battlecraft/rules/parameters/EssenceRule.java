package eidolons.game.battlecraft.rules.parameters;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.DC_SecondsRule;
import eidolons.game.core.game.DC_Game;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * static methods for Essence manipulation
 * <p>
 * Questions: max essence
 * <p>
 * Eidolon hero death - spills essence? If so, the balance's upset. If not, we probably wanna transfer that essence onto
 * new hero - in absolute value! Interesting, then spellcasting won't be so 'gambit'!
 */
public class EssenceRule implements DC_SecondsRule {

    private static final Integer STD_ESS_ABS_VAL = 25;

    public static class Essence {
        BattleFieldObject source;
        int value;

        public Essence(BattleFieldObject source, int value) {
            this.source = source;
            this.value = value;
        }
    }

    /**
     * At the start of each round, units absorb up to N essence from the cells they're standing on.
     *
     * @param source
     */
    public static void cellAbsorb(BattleFieldObject source) {

    }

    /**
     * When a unit dies, first its killer absorbs up to N, then units with special absorbs; then finally the remainder
     * is split between all adjacent cells
     */
    public static void killed(Unit killer, Unit killed) {
        Essence essence = new Essence(killed, killed.getIntParam(PARAMS.C_ESSENCE));
        absorb(killer, essence);

    }

    private static void absorb(Unit unit, Essence essence) {
        absorb(unit, essence, null);
    }

    private static void absorb(Unit unit, Essence essence, Integer value) {
        if (value == null)
            value = getStdAbsorbVal(unit);

        int absorbed = Math.min(value, essence.value);
        unit.modifyParameter(PARAMS.C_ESSENCE, absorbed);
        essence.value -= absorbed;
    }

    public static void split(int amount, BattleFieldObject object) {
        split(new Essence(object, amount));
    }

    public static void split(Essence essence) {
        split(essence, essence.source.getCoordinates());
    }

    public static void split(Essence essence, Coordinates center) {
        //trigger-based abils to steal essence when it leaks
        DC_Game game = essence.source.getGame();
        Ref ref = new Ref(essence.source);
        ref.setAmount(essence.value);
        game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.ESSENCE_LEAKS, ref));
        essence.value = ref.getAmount();
        if (essence.value == 0)
            return;

        List<GridCell> cells = Arrays.stream(essence.source.getCoordinates().getAdjacent()).map(c -> game.getCell(c)).collect(Collectors.toList());

        int remainder = essence.value % cells.size();
        int n = essence.value / cells.size();
        for (GridCell cell : cells) {
            cellAbsorbs(cell, remainder-- > 0 ? n + 1 : n);
        }
        //first split between units, then cells/..

    }

    private static void cellAbsorbs(GridCell cell, int i) {
        cell.addParam(PARAMS.C_ESSENCE, i);
        //TODO make sure it doesn't change due to some bs logic!
    }

    //some abils allow to abs % of essence from kills/.. before it is split
    public static void preabsorb(BattleFieldObject source) {

    }


    @Override
    public void secondsPassed(Unit unit, int seconds) {
        //sometimes units LEAK essence onto adjacent cells over time! - summoned, cursed, etc

        Integer val = unit.getIntParam(PARAMS.ESSENCE_LEAK);
        split(new Essence(unit, val));
    }


    private static Integer getStdAbsorbVal(Unit unit) {
        Integer val = STD_ESS_ABS_VAL;
        val += (val * unit.getIntParam(PARAMS.ESSENCE_ABSORB_MOD) / 100);
        val += unit.getIntParam(PARAMS.ESSENCE_ABSORB_BONUS);
        return val;
    }
}
