package libgdx.shaders.post;

/**
 * Created by JustMe on 12/3/2018.
 */
public class PostEffectDataSource {
    private final Object[] params;

    public enum POST_EFFECT{
        BLOOM,
        BLUR,
        WATER,
        FREEZE,
        VIGNETTE,

    }
    POST_EFFECT effect;

    public PostEffectDataSource(POST_EFFECT effect, Object... params) {
        this.effect = effect;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public POST_EFFECT getEffect() {
        return effect;
    }
}
