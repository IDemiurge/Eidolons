package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.generic.GroupX;
import main.game.bf.Coordinates;

public abstract class GridLayer extends GroupX {
    protected GridPanel grid;

    public GridLayer(GridPanel grid) {
        this.grid = grid;
    }

    public void draw(Batch batch, float parentAlpha) {
        for (int x = grid.drawX1; x < grid.drawX2; x++) {
            for (int y = grid.drawY1; y < grid.drawY2; y++) {
                draw(x, y, batch, parentAlpha);
            }
        }
    }

    public void act(float delta) {
        for (int x = grid.drawX1; x < grid.drawX2; x++) {
            for (int y = grid.drawY1; y < grid.drawY2; y++) {
                act(x, y, delta);
            }
        }
    }

    protected abstract void act(int x, int y, float delta);

    protected abstract void draw(int x, int y, Batch batch, float parentAlpha);

    protected void setColor(Actor actor, int x, int y) {
        // Color c = DC_Game.game.getColorMap().getOutput()[x][y];
        if (actor == null) {
            return;
        }
        Color c = DC_Game.game.getColorMap().getOutput().get(Coordinates.get(x, y));
        if (c == null) {
            // c = GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
            return;
        }
        actor.setColor(new Color(c.r, c.g, c.b, 1));
        applyLightness(actor, c.a, x, y);
    }

    protected void applyLightness(Actor actor, float a, int x, int y) {
        //assuming the batch has colorful shader...
        float light = a - 0.5f;
        Color c = actor.getColor();
        actor.setColor(new Color(c.r + light, c.g + light, c.b + light, 1));

    }
}
