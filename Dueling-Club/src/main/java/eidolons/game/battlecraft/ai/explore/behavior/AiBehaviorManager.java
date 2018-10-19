package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.UnitExploreAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 10/15/2018.
 */
public class AiBehaviorManager extends AiHandler{

    Set<UnitExploreAI> aiSet = new LinkedHashSet<>();

    public AiBehaviorManager(AiMaster master) {
        super(master);
    }

    public void act(float delta){
        for (UnitExploreAI ai : aiSet) {
                ai.act(delta);
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

    public List<AI_BEHAVIOR_MODE> getBehaviors() {
        List<AI_BEHAVIOR_MODE> list = new ArrayList<>();
//        if (checkAmbush()) {
//            list.add(AI_BEHAVIOR_MODE.AMBUSH);
//        }
//        if (checkStalk()) {
//            list.add(AI_BEHAVIOR_MODE.STALK);
//        }
//        if (checkAggro()) {
//            list.add(AI_BEHAVIOR_MODE.AGGRO);
//        }
//
//        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
//            list.add(getPassiveBehavior());
//        }

        return list;
    }
    private List<AiBehavior> initBehaviors(UnitAI ai) {
        AiBehavior behavior = new FollowAi(master, ai);

        switch (ai.getGroupAI().getType()) {

        }

        switch (ai.getType()) {

        }

        return null;
    }
}
