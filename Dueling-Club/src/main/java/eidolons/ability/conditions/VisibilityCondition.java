package eidolons.ability.conditions;

import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;

public class VisibilityCondition extends ConditionImpl {

    private UNIT_TO_PLAYER_VISION p_vision;
    private UNIT_TO_UNIT_VISION u_vision;
    private KEYS source;
    private KEYS match;

    public VisibilityCondition(UNIT_TO_PLAYER_VISION p_vision) {
        this(null, null, p_vision);
    }

    public VisibilityCondition(UNIT_TO_UNIT_VISION u_vision) {
        this(null, null, u_vision);
    }

    public VisibilityCondition(KEYS source, KEYS match, UNIT_TO_PLAYER_VISION p_vision) {
        this.match = match;
        this.source = source;
        this.p_vision = p_vision;
    }

    public VisibilityCondition(KEYS source, KEYS match, UNIT_TO_UNIT_VISION u_vision) {
        this.match = match;
        this.source = source;
        this.u_vision = u_vision;
    }

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getObj(KEYS.MATCH) instanceof BfObj)) {
            return false;
        }

        DC_Obj match = (DC_Obj) ref.getObj(KEYS.MATCH);

        boolean result = false;
        if (this.match == null && this.source == null) {
            if (p_vision != null) {
                UNIT_TO_PLAYER_VISION playerVision = match.getActivePlayerVisionStatus();
                if (game.getManager().getActiveObj().isMine() !=
                 ref.getSourceObj().isMine()) {
                    if (ref.getSourceObj().isMine()) {
                        playerVision = match.getPlayerVisionStatus(false);
                    } else {
                        //TODO for enemy unit on player's unit...
                    }
                }
                if (playerVision == p_vision) {
                    return true;
                }
            }
            UNIT_TO_UNIT_VISION visionStatus = match.getUnitVisionStatus();
            if (!ref.getSourceObj().isActiveSelected()) {
                visionStatus = match.getGame().getVisionMaster().getSightMaster().
                 getUnitVisibilityStatus(match, (Unit) ref.getSourceObj());
            }
            return visionStatus.isSufficient(u_vision);
        }

        if (p_vision != null) {
            Unit unit = (Unit) ref.getObj(source);
            result = unit.getActivePlayerVisionStatus() == p_vision;
        } else if (u_vision != null) {
            match = (DC_Obj) ref.getObj(this.match);
            // if (((DC_Game) game).getManager().isAI_Turn()) { what's the idea?
            Unit activeObj = (Unit) ref.getObj(source);
            result = ((DC_Game) game).getVisionMaster().getUnitVisibilityStatus(match, activeObj)
             .isSufficient(u_vision);
            // }
        }

        return result;
    }

}