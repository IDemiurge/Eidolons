package main.game.module.adventure.travel;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import main.libgdx.screens.map.MapObjStage;
import main.libgdx.screens.map.obj.PartyActor;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 3/12/2018.
 */
public class MapWanderAi {

    private Map<PartyActor, Float> triggerMap = new HashMap<>();
    private Map<PartyActor, Float> timerMap = new HashMap<>();
    List<PartyActor> list = new ArrayList<>();
    MapObjStage objStage;

    public MapWanderAi(MapObjStage objStage) {
        this.objStage = objStage;
    }


    public void update() {
        list = new ArrayList<>(
         objStage.getParties());
        list.removeIf(p -> p.getParty().isMine());

    }
        public void act(float delta) {
//        triggerMap.put(party, orderAt);
        float distance = 120;
        float modX = RandomWizard.getRandomFloatBetween(-1, 1);
        float modY = RandomWizard.getRandomFloatBetween(-1, 1);

//        MapMaster.addToFloatMap(triggerMap, party, delta);
        for (PartyActor party : list) {
            if (party.getActionsOfClass(MoveToAction.class).size < 1) {
                float delay = getDelay(party);
                RandomWizard.getRandomFloatBetween(delay, delay * 2);
            }
        }

        for (PartyActor sub : triggerMap.keySet()) {
            MapMaster.addToFloatMap(timerMap, sub, delta);
            if (triggerMap.get(sub) == null ||
             timerMap.get(sub) > triggerMap.get(sub)) {
                sub.moveTo(sub.getX()+distance*modX,
                 sub.getY()+distance*modY);
                timerMap.remove(sub);
            }
        }
    }

    private float getDelay(PartyActor party) {
        return 10;
    }
}

