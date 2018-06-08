package eidolons.game.module.adventure.map.travel;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.libgdx.screens.map.MapObjStage;
import eidolons.libgdx.screens.map.obj.PartyActor;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JustMe on 3/12/2018.
 */
public class MapWanderAi {

    List<PartyActor> list = new ArrayList<>();
    MapObjStage objStage;
    private Map<PartyActor, Float> triggerMap = new ConcurrentHashMap<>();
    private Map<PartyActor, Float> timerMap = new ConcurrentHashMap<>();

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

        if (!isOn())
            return;
        for (PartyActor party : list) {
            if (party.getActionsOfClass(MoveToAction.class).size > 0) {
                continue;
            }
            MapMaster.addToFloatMap(timerMap, party, delta);
            if (triggerMap.get(party) == null) {
                float delay = getDelay(party);
                triggerMap.put(party, RandomWizard.getRandomFloatBetween(delay, delay * 2));
            }

            if (triggerMap.get(party) == null ||
             timerMap.get(party) > triggerMap.get(party)) {
                party.moveTo(party.getX() + distance * modX,
                 party.getY() + distance * modY, distance / (triggerMap.get(party) -
                  RandomWizard.getRandomFloatBetween(getDelay(party) / 3, getDelay(party) / 2)));
                timerMap.remove(party);
                triggerMap.remove(party);
            }
        }
    }

    private boolean isOn() {
        return false;
    }

    private float getDelay(PartyActor party) {
        return 10;
    }
}

