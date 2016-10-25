package main.game.battlefield;

import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.content.parameters.G_PARAMS;
import main.entity.obj.Obj;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Death Logic Store the dead: automatically; accessibility (for selecting and
 * checks):
 * <p>
 * Zone Targeting:
 * <p>
 * Destroy corpse if toughness or endurance reach -100%
 *
 * @param unit
 */
public class GraveyardManager {
    private static final String RIP = "Here lie: ";
    private Map<ZCoordinates, Stack<Obj>> graveMap = new ConcurrentHashMap<ZCoordinates, Stack<Obj>>();
    private BattleField battlefield;
    private List<Obj> removed = new LinkedList<>();

    public GraveyardManager(BattleField battlefield) {
        this.battlefield = battlefield;
    }

    public void init() {
        for (Coordinates c : battlefield.getGrid().getCoordinatesList()) {
            graveMap.put(getZCoordinate(c), new Stack<Obj>());
        }
    }

    private ZCoordinates getZCoordinate(Coordinates c) {
        return new ZCoordinates(c.x, c.y, battlefield.getGrid().getZ());
    }

    public Obj removeCorpse(Obj unit) {
        // this does not tell us which corpse has been removed???

        graveMap.get(getZCoordinate(unit.getCoordinates())).remove(unit); // ???
        battlefield.getCell(getZCoordinate(unit.getCoordinates())).setParam(G_PARAMS.N_OF_CORPSES,
                graveMap.get(getZCoordinate(unit.getCoordinates())).size());
        if (battlefield.getObj(getZCoordinate(unit.getCoordinates())) != null)
            battlefield.getObj(getZCoordinate(unit.getCoordinates())).setParam(
                    G_PARAMS.N_OF_CORPSES,
                    graveMap.get(getZCoordinate(unit.getCoordinates())).size());

        removed.add(unit);

        return unit;
    }

    public void unitReturns(Obj unit) {
        removed.remove(unit);
    }

    public void unitDies(Obj unit) {

        if (unit.checkBool(STD_BOOLS.LEAVES_NO_CORPSE))
            return;
        addCorpse(unit);

    }

    public void addCorpse(Obj unit) {
        graveMap.get(getZCoordinate(unit.getCoordinates())).push(unit);
        Obj cell = battlefield.getCell(getZCoordinate(unit.getCoordinates()));
        cell.setParam(G_PARAMS.N_OF_CORPSES, graveMap.get(getZCoordinate(unit.getCoordinates()))
                .size());
        if (battlefield.getObj(getZCoordinate(unit.getCoordinates())) != null)
            battlefield.getObj(getZCoordinate(unit.getCoordinates())).setParam(
                    G_PARAMS.N_OF_CORPSES,
                    graveMap.get(getZCoordinate(unit.getCoordinates())).size());
    }

    public Obj getTopDeadUnit(Coordinates c) {
        Stack<Obj> stack = graveMap.get(c);
        if (stack == null)
            return null;
        if (stack.isEmpty())
            return null;
        return stack.peek();
    }

    public List<Coordinates> getCorpseCells() {
        List<Coordinates> list = new LinkedList<>();
        for (Coordinates l : graveMap.keySet()) {
            if (!graveMap.get(l).isEmpty())
                list.add(l);
        }
        return list;
    }

    public List<Obj> getDeadUnits(Coordinates c) {
        return graveMap.get(c);
    }

    public Obj destroyTopCorpse(Coordinates c) {
        return removeCorpse(getTopDeadUnit(c));
    }

    public boolean checkForCorpses(Obj obj) {
        try {
            return !getDeadUnits(getZCoordinate(obj.getCoordinates())).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getRipString(Obj obj) {
        String result = RIP;
        for (Obj corpse : getDeadUnits(getZCoordinate(obj.getCoordinates()))) {
            result += corpse.getName() + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

}
