package eidolons.game.battlecraft.logic.meta.party.request;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.party.request.HeroRequestMaster.REQUEST_TYPE;

/**
 * Created by JustMe on 7/23/2017.
 */
public class HeroRequest {
    Unit requester;
    Unit responder;
    REQUEST_TYPE type;
    String arg;

    public HeroRequest(Unit requester, Unit responder, REQUEST_TYPE type, String arg) {
        this.requester = requester;
        this.responder = responder;
        this.type = type;
        this.arg = arg;
    }

    public Unit getRequester() {
        return requester;
    }

    public Unit getResponder() {
        return responder;
    }

    public REQUEST_TYPE getType() {
        return type;
    }

    public String getArg() {
        return arg;
    }
}
