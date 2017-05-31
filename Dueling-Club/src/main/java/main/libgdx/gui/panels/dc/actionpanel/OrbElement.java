package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class OrbElement extends ValueContainer {
    private static final String EMPTY_PATH = "/UI/components/new/orb 64.png";
    private TextureRegion orbRegion;
    private TextureRegion backTexture;
    private int orbFullness = 62;
    private Vector2 backOffset = new Vector2();
    private boolean logged;

    public OrbElement(TextureRegion texture, String value) {
        super(getOrCreateR(EMPTY_PATH), null, null);
        orbRegion = texture;
        calculateOrbFullness(value);
        imageContainer.align(Align.bottomLeft);
    }

    @Override
    public void setBackground(String background) {
        backTexture = getOrCreateR(background);
    }

    private void calculateOrbFullness(String value) {
        final String[] split = value.split("/");
        if (split.length == 2) {
            final int cur = Integer.valueOf(split[0]);
            final int max = Integer.valueOf(split[1]);
            orbFullness = Math.min(Math.round(cur / (max / 62f)), 62);
        } else {
            orbFullness = 62;
        }
    }

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, name, value);
    }

    public void setBackOffset(Vector2 offset) {
        backOffset = offset;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.flush();
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(getX(), getY(), 62, orbFullness);
        getStage().calculateScissors(clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        batch.draw(orbRegion, getX(), getY());
        batch.flush();
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
            if (!logged) {
                e.printStackTrace(); //TODO spams into console!
                logged = true;
            }
        }

        //Vector2 v2 = new Vector2(-24, -4);
        //Vector2 v2 = new Vector2(-32, -5);
        Vector2 v2 = localToStageCoordinates(new Vector2(backOffset));

        batch.draw(backTexture, v2.x, v2.y);
    }
}
