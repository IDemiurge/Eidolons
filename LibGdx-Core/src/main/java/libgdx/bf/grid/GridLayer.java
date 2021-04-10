package libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.game.DC_Game;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.bf.grid.handlers.ColorHandler;
import libgdx.gui.generic.GroupX;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;

public abstract class GridLayer<T extends Actor> extends GroupX {
    protected GridPanel grid;

    public GridLayer(GridPanel grid) {
        this.grid = grid;
    }

    public void draw(Batch batch, float parentAlpha) {
        //TODO gdx Review - decor e.g. needs smarter offset for visibility..
        //tester fix
        if (!CoreEngine.isWeakCpu() && !CoreEngine.isWeakGpu()){
            for (int x = 0; x < grid.getModuleCols(); x++) {
                for (int y = 0; y < grid.getModuleRows(); y++) {
                    draw(x, y, batch, parentAlpha);
                }
            }
            return;
        }
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

    protected void setColor(T actor, int x, int y) {
        // Color c = DC_Game.game.getColorMap().getOutput()[x][y];
        if (actor == null) {
            return;
        }
        if (ColorHandler.isStaticColors()){
            if (actor.getColor().equals(GdxColorMaster.WHITE))
                return;
        }
        Color c = grid. getColorMap().getOutput().get(Coordinates.get(x, y));
        if (c == null) {
            // c = GdxColorMaster.get(GdxColorMaster.NULL_COLOR);
            return;
        }
        actor.setColor(new Color(c.r, c.g, c.b, 1));
        applyLightness(actor, c.a, x, y);
    }

    protected void applyLightness(T actor, float a, int x, int y) {
        //assuming the batch has colorful shader...
        float light = a - 0.5f;
        Color c = actor.getColor();
        actor.setColor(new Color(c.r + light, c.g + light, c.b + light, 1));

    }
}
