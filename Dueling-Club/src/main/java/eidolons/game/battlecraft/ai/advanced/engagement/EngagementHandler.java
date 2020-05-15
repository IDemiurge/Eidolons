package eidolons.game.battlecraft.ai.advanced.engagement;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.explore.ExplorationHandler;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.CONTENT_CONSTS2;
import main.content.enums.rules.VisionEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/*
Features:
- Support non-encounter groups (the only difference should be reinforcements at this point)
Encounter is just an ad hoc to spawn the right units and tie them together etc

Rules:
- Very specific cases when you can attack a group member and get away

Preferences:
- Avoid double encounter engagements, make them less likely
-

Disengagement
- Confirm and add negative effect

Old Aggro logic:
groups join together
2 rounds of Engaged status (per unit)

New ideas

(pre)Aggro Events - per unit, with message and visuals etc

periods
or events that trigger the check?

movement
suppose we go with Timer Mode


Who will this handler talk to?

When do we decide about reinforcements?


 */
public class EngagementHandler extends ExplorationHandler {
    EngageMsgs msgs = new EngageMsgs();
    EngageEvents events = new EngageEvents();
    public EngagementHandler(ExplorationMaster master) {
        super(master);
    }

    @Override
    protected float getTimerPeriod() {
        return 1f;
    }

    public void timerEvent() {
        checkAggroEvents();
    }

    public void checkAggroEvents(){
        for (Unit unit : getGame().getUnits()) {

        }
    }

    public void engagementChanged(UnitAI ai, VisionEnums.ENGAGEMENT_LEVEL level){
        switch (level) {
            case ALARMED:
                //call out
            case ENGAGED:
                //check if group engages, otherwise
        }
        ai.setEngagementLevel(level);

        String comment= msgs.getComment(ai.getGroupAI(), ai.getUnit(), level);
        GuiEventManager.triggerWithParams(GuiEventType.SHOW_COMMENT_PORTRAIT, ai.getUnit(),  comment);
//        GuiEventManager.trigger(GuiEventType.GRID_OBJ_ANIM, new AnimData());

        String message=msgs.getMessage(ai.getUnit(), level);
        getGame().getLogManager().log(message);
        EUtils.showInfoText(message);

        //ai orders
        Order orders= new Order(CONTENT_CONSTS2.ORDER_TYPE.MOVE, "");
        ai.setCurrentOrder(orders);
    }


    public VisionEnums.ENGAGEMENT_LEVEL getLevelForGroup(GroupAI groupAI){

        VisionEnums.ENGAGEMENT_LEVEL level= VisionEnums.ENGAGEMENT_LEVEL.UNSUSPECTING;

        Unit leader = groupAI.getLeader();
        leader.getAI().getEngagementDuration();
        for (Unit member : groupAI.getMembers()) {

        }
        switch (level) {
            case ENGAGED:

        }
        return level;
    }

}
