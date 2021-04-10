package libgdx.shaders.post;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by JustMe on 12/3/2018.
 */
public interface Processor {
    void prepareForFBO(SpriteBatch batch, float delta);

    void prepareForBatch(SpriteBatch batch);
}
