package eidolons.game.battlecraft.ai.advanced.engagement;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.explore.ExplorationHandler;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.rules.VisionEnums.ENGAGEMENT_LEVEL;

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

    actual combat begins when either a hostile actin is performed or dst<=4
    otherwise it's still possible to evade
    it matters whether when we detect a unit we know they have detected us!..
    what about SNEAKING ?
=> making those rolls
=> getting events
=> enable enemy sneaking!
    Hearing and Perception?

    NEXT:
    status propagation in AiGroup

 */

public class EngagementHandler extends ExplorationHandler {
    EngageEvents events;

    public EngagementHandler(ExplorationMaster master) {
        super(master);
        events = new EngageEvents(master);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        events.act(delta);
    }

    public void detected(Unit source, BattleFieldObject object) {
        if (object instanceof Structure)
            return;
        events.detected(source, object);
        //precombat event
    }

    public void lostSight(Unit source, BattleFieldObject object) {
        events.lostSight(source, object);
        //punishing retreat
    }

    //an outline came into view
    public void alert(Unit source, BattleFieldObject object) {

        ENGAGEMENT_LEVEL level = getLevel(source);
        switch (level) {
            case UNSUSPECTING:
                events.newAlert(source, object);
                break;
        }
    }

    private ENGAGEMENT_LEVEL getLevel(Unit source) {
        if (source.isMine()) {
            // return playerEngagement;
            return source.getAI().getEngagementLevel();
        }
        return source.getAI().getEngagementLevel();
    }

    public EngageEvents getEvents() {
        return events;
    }
}
