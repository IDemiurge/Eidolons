package logic.v2.ai.generic.action;

/**
 * Created by Alexander on 1/21/2023
 */
public class AiActionTemplate {
    // Targeting targeting; //closest, rational, random, specific
    // action provider - by concrete or general type
    IAiActionProvider provider;
    IAiTargeting targeting;

    public AiActionTemplate(IAiActionProvider provider, IAiTargeting targeting) {
        this.provider = provider;
        this.targeting = targeting;
    }

    public IAiActionProvider getProvider() {
        return provider;
    }

    public IAiTargeting getTargeting() {
        return targeting;
    }
}
