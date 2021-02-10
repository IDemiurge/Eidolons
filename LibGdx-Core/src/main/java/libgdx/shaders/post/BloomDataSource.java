package libgdx.shaders.post;

import com.bitfire.postprocessing.effects.Bloom.Settings;

/**
 * Created by JustMe on 12/3/2018.
 */
public class BloomDataSource extends PostEffectDataSource {
    public BloomDataSource(Settings settings) {
        super(POST_EFFECT.BLOOM, settings);
    }
}
