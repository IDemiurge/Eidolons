package eidolons.game.module.adventure.map.travel.old;

import eidolons.libgdx.screens.map.layers.AlphaMap;
import eidolons.libgdx.screens.map.layers.AlphaMap.ALPHA_MAP;
import eidolons.libgdx.screens.map.obj.PartyActor;
import eidolons.macro.map.travel.TravelAction;
import main.game.bf.Coordinates;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 3/12/2018.
 */
public class FreeTravelMaster {
    private static FreeTravelMaster instance;

    //path finding? or just bumping...

    Map<ALPHA_MAP, AlphaMap> map = new HashMap<>();

    private FreeTravelMaster() {
        instance = this;
        init();
    }

    public static FreeTravelMaster getInstance() {
        if (instance == null)
            instance = new FreeTravelMaster();
        return instance;
    }

    public void init() {
        for (ALPHA_MAP sub : ALPHA_MAP.values()) {
            map.put(sub, new AlphaMap(sub));

        }
    }

    public TravelAction travelTo(PartyActor actor, int x, int y, float speed) {
        float dur = actor.getParty().getCoordinates().dst(Coordinates.get(true, x, y))
         / speed;
        TravelAction action = new TravelAction(this);
        action.setPosition(x, y);
        action.setDuration(dur);
        actor.addAction(action);
        action.setTarget(actor);
        return action;


    }

    public void check(PartyActor actor) {

        boolean stop = false;


    }

    public void act(float delta) {

    }

    public boolean check(ALPHA_MAP mapType, float x, float y) {
        return map.get(mapType).isThere((int) x, (int) y);
    }
}
