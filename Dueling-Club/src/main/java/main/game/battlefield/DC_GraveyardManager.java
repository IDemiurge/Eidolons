package main.game.battlefield;

import main.content.enums.GenericEnums;
import main.content.values.parameters.G_PARAMS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.game.core.game.DC_Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JustMe on 2/21/2017.
 */
public class DC_GraveyardManager implements GraveyardManager {

    public static final String RIP = "Here lie: ";
    protected Map<ZCoordinates, Stack<Obj>> graveMap = new ConcurrentHashMap<>();
    protected List<Obj> removed = new LinkedList<>();
    private DC_Game game;
    private Map<Obj, Integer> indexMap = new HashMap<>();


    public DC_GraveyardManager(DC_Game game) {
        this.game = game;
    }

    @Override
    public void init() {
        for (Obj c : game.getCells()) {
            graveMap.put(getZCoordinate(c.getCoordinates()), new Stack<>());
        }
    }

    @Override
    public void updateGraveIndices() {

        for (Obj c : game.getUnits()) {

        }

    }
    @Override
    public ZCoordinates getZCoordinate(Coordinates c) {
        return new ZCoordinates(c.x, c.y, game.getDungeon().getZ());
    }

    @Override
    public Obj removeCorpse(Obj unit) {
        // this does not tell us which corpse has been removed???

        graveMap.get(getZCoordinate(unit.getCoordinates())).remove(unit); // ???
        game.getCellByCoordinate(getZCoordinate(unit.getCoordinates())).setParam(G_PARAMS.N_OF_CORPSES,
         graveMap.get(getZCoordinate(unit.getCoordinates())).size());

        if (game.getObjectByCoordinate(getZCoordinate(unit.getCoordinates())) != null) {
            game.getObjectByCoordinate(getZCoordinate(unit.getCoordinates())).setParam(
             G_PARAMS.N_OF_CORPSES,
             graveMap.get(getZCoordinate(unit.getCoordinates())).size());
        }

        removed.add(unit);

        return unit;
    }


    @Override
    public void unitDies(Obj unit) {

        if (unit.checkBool(GenericEnums.STD_BOOLS.LEAVES_NO_CORPSE)) {
            return;
        }
        addCorpse(unit);

    }

    @Override
    public void addCorpse(Obj unit) {
        graveMap.get(getZCoordinate(unit.getCoordinates())).push(unit);
        Obj cell = game.getCellByCoordinate(getZCoordinate(unit.getCoordinates()));
        cell.setParam(G_PARAMS.N_OF_CORPSES, graveMap.get(getZCoordinate(unit.getCoordinates()))
         .size());
        if (game.getObjectByCoordinate(getZCoordinate(unit.getCoordinates())) != null) {
            game.getObjectByCoordinate(getZCoordinate(unit.getCoordinates())).setParam(
             G_PARAMS.N_OF_CORPSES,
             graveMap.get(getZCoordinate(unit.getCoordinates())).size());
        }
    }



    @Override
    public int getGraveIndex(BfObj obj) {
        return indexMap.get(obj);
    }

    @Override
    public Obj getTopDeadUnit(Coordinates c) {
        Stack<Obj> stack = graveMap.get(c);
        if (stack == null) {
            return null;
        }
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    @Override
    public List<Coordinates> getCorpseCells() {
        List<Coordinates> list = new LinkedList<>();
        for (Coordinates l : graveMap.keySet()) {
            if (!graveMap.get(l).isEmpty()) {
                list.add(l);
            }
        }
        return list;
    }

    @Override
    public List<Obj> getDeadUnits(Coordinates c) {
        return graveMap.get(c);
    }

    @Override
    public Obj destroyTopCorpse(Coordinates c) {
        return removeCorpse(getTopDeadUnit(c));
    }

    @Override
    public boolean checkForCorpses(Obj obj) {
        try {
            return !getDeadUnits(getZCoordinate(obj.getCoordinates())).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getRipString(Obj obj) {
        String result = RIP;
        for (Obj corpse : getDeadUnits(getZCoordinate(obj.getCoordinates()))) {
            result += corpse.getName() + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }
}

