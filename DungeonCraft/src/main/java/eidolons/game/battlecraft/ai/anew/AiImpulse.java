package eidolons.game.battlecraft.ai.anew;

import eidolons.game.battlecraft.ai.UnitAI;
import main.content.enums.system.AiEnums;

public class AiImpulse {
    UnitAI ai;
    AiEnums.IMPULSE_TYPE type;
    Object arg;

    public AiImpulse(UnitAI ai, AiEnums.IMPULSE_TYPE type, Object arg) {
        this.ai = ai;
        this.type = type;
        this.arg = arg;
    }
}
