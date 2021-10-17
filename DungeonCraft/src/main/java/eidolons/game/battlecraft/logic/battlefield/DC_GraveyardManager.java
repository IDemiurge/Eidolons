package eidolons.game.battlecraft.logic.battlefield;

import eidolons.game.core.game.DC_Game;
import main.content.enums.GenericEnums;
import main.content.values.parameters.G_PARAMS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.GraveyardManager;
import main.system.GuiEventManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static main.system.GuiEventType.UPDATE_GRAVEYARD;

/**
 * Created by JustMe on 2/21/2017.
 */
public class DC_GraveyardManager implements GraveyardManager {

    public static final String RIP = "Here lie: ";
    protected Map< Coordinates, Stack<Obj>> graveMap = new ConcurrentHashMap<>();
    protected List<Obj> removed = new ArrayList<>();
    private DC_Game game;
    private Map<Obj, Integer> indexMap = new HashMap<>();


    public DC_GraveyardManager(DC_Game game) {
        this.game = game;
    }

    @Override
    public void init() {
        for (Obj c : game.getCells()) {
            graveMap.put( (c.getCoordinates()), new Stack<>());
        }
    }


    @Override
    public Obj removeCorpse(Obj unit) {
        // this does not tell us which corpse has been removed???

        graveMap.get( (unit.getCoordinates())).remove(unit); // ???
        game.getCell( (unit.getCoordinates())).setParam(G_PARAMS.N_OF_CORPSES,
         graveMap.get( (unit.getCoordinates())).size());

        if (game.getObjectByCoordinate( (unit.getCoordinates())) != null) {
            game.getObjectByCoordinate( (unit.getCoordinates())).setParam(
             G_PARAMS.N_OF_CORPSES,
             graveMap.get( (unit.getCoordinates())).size());
        }

        removed.add(unit);

        GuiEventManager.trigger(UPDATE_GRAVEYARD, unit.getCoordinates());

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
        graveMap.get( (unit.getCoordinates())).push(unit);
        Obj cell = game.getCell( (unit.getCoordinates()));
        cell.setParam(G_PARAMS.N_OF_CORPSES, graveMap.get( (unit.getCoordinates()))
         .size());
        if (game.getObjectByCoordinate( (unit.getCoordinates())) != null) {
            game.getObjectByCoordinate( (unit.getCoordinates())).setParam(
             G_PARAMS.N_OF_CORPSES,
             graveMap.get( (unit.getCoordinates())).size());
        }

        GuiEventManager.trigger(UPDATE_GRAVEYARD, unit.getCoordinates());
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
    public Set<Coordinates> getCorpseCells() {
        Set<Coordinates> set = new LinkedHashSet<>();
        for (Coordinates l : graveMap.keySet()) {
            if (!graveMap.get(l).isEmpty()) {
                set.add(l);
            }
        }
        return set;
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
            return !getDeadUnits( (obj.getCoordinates())).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getRipString(Obj obj) {
        StringBuilder resultBuilder = new StringBuilder(RIP);
        for (Obj corpse : getDeadUnits( (obj.getCoordinates()))) {
            resultBuilder.append(corpse.getName()).append(", ");
        }
        String result = resultBuilder.toString();
        result = result.substring(0, result.length() - 2);
        return result;
    }
}

