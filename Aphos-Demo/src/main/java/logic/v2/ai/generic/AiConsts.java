package logic.v2.ai.generic;

import logic.v2.ai.generic.action.AiActionTemplate;

/**
 * Created by Alexander on 1/21/2023
 */
public class AiConsts {
    public static final String p_units = "units";
    public static final String INDECISION = "Indecision";

    public enum AiEventType {
        atb_time,
        command,
        death,
        damage,
        

    }

    public enum AiMode {
        Impulse, Plan, Command;
    }
}
