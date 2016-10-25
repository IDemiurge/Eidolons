package main.libgdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_OrbPanel extends Group {

    DC_GDX_ValueOrb[] orbs = new DC_GDX_ValueOrb[6];
    private boolean leftToRight;

    public DC_GDX_OrbPanel(boolean leftToRight) {
        this.leftToRight = leftToRight;

    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }
}
