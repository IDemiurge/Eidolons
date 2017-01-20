package main.libgdx.old;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */
public class OrbPanel extends Group {

    ValueOrb[] orbs = new ValueOrb[6];
    private boolean rightToLeft;
    private String imagePath;

    public OrbPanel(boolean rightToLeft, String imagePath) {
        this.rightToLeft = rightToLeft;
        this.imagePath = imagePath;
    }

    public OrbPanel init() {
        int i = 0;
        orbs[i++] = new ValueOrb(Color.BLUE, imagePath).init();
        orbs[i++] = new ValueOrb(Color.BROWN, imagePath).init();
        orbs[i++] = new ValueOrb(Color.CYAN, imagePath).init();

        orbs[i++] = new ValueOrb(Color.GOLD, imagePath).init();
        orbs[i++] = new ValueOrb(Color.FOREST, imagePath).init();
        orbs[i++] = new ValueOrb(Color.MAGENTA, imagePath).init();

        for (i = 0; i < orbs.length / 2; i++) {
            ValueOrb orb = orbs[i];
            orb.setX(i * orb.getWidth());
            orb.setY(0);
            addActor(orb);

            orb = orbs[i + 3];
            orb.setX(i * orb.getWidth());
            orb.setY(orb.getHeight());
            addActor(orb);
        }

        this.setWidth(orbs[0].getWidth() * 3);
        this.setHeight(orbs[0].getHeight() * 2);
        return this;
    }

    public void invertOrbOrder() {
        swapOrbsPos(orbs[0], orbs[2]);
        swapOrbsPos(orbs[3], orbs[5]);
    }

    private void swapOrbsPos(ValueOrb first, ValueOrb last) {
        float fX = first.getX();
        float fY = first.getY();

        float lX = last.getX();
        float lY = last.getX();

        first.setX(lX);
        first.setY(lY);

        last.setX(fX);
        last.setY(fY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
