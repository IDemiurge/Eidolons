package eidolons.game.module.dungeoncrawl.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.data.XLinkedMap;
import main.system.auxiliary.data.MapMaster;

import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 10/15/2018.
 */
public class AiBehaviorManager extends AiHandler{

    private Map<UnitAI, List<AiBehavior>> behaviors = new XLinkedMap<>();

    public AiBehaviorManager(AiMaster master) {
        super(master);
    }

    public void act(float delta){
        for (UnitAI ai : behaviors.keySet()) {
            for (AiBehavior behavior : behaviors.get(ai)) {
              //speed modifier? remove?
                behavior.act(delta);
            }
        }
    }
    public void init(){
        for (Unit unit : master.getGame().getUnits()) {
            initBehaviors(unit.getAI());
        }
        for (GroupAI groupAI : master.getManager().getGroups()) {
            //group behavior?
        }
    }

    private void initBehaviors(UnitAI ai) {
        AiBehavior behavior = new FollowAi(ai, master);
        MapMaster.addToListMap(behaviors, ai , behavior);

        switch (ai.getGroupAI().getType()) {

        }

        switch (ai.getType()) {

        }

    }
}
