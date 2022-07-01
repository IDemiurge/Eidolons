package gdx.visuals.front;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import gdx.visuals.lanes.LaneConsts;
import libgdx.GdxMaster;

/*
need this to be a highly flexible pack of drawables. Mirror principle could be used perhaps to some extent - just draw the
same with other half via Flip ; maybe with a shader
 */

//Single diagonal line of Cells
public class FrontCells extends Group {
    public static final int ANGLE = 55;

    TextureRegion cell;
    TextureRegion halfCell;
    int cellN;
    private final boolean flip;
    private float startX;
    private final float startY;
    private final float stepX;
    private final float stepY;

    public FrontCells(TextureRegion cell, TextureRegion halfCell, int cellN, boolean flip) {
        this.cell = cell;
        this.halfCell = halfCell;
        this.cellN = cellN;
        this.flip = flip;

        startX = LaneConsts.FRONTLINE_X;
        startY = LaneConsts.FRONTLINE_Y;
        if (flip)
            startX += GdxMaster.getWidth() / 2;

        stepX = (float) Math.sin(55d / 360);
        stepY = (float) Math.cos(55d / 360);
        //TODO
        //memorize cell positions for movement - via some FrontlineManager?
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = startX;
        float y = startY;
        batch.begin();
        for (int i = 0; i < cellN; i++) {
            TextureRegion region = i % 2 == 0 ? cell : halfCell;
            if (flip) {
                batch.draw(region, x, y);
            } else {
                batch.draw(region, x, y);
            }
            x += stepX * (region).getRegionWidth();
            y += stepY * (region).getRegionHeight();


        }
        batch.end();
    }
}
