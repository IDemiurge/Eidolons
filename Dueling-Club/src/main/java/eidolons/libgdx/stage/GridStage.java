package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.screens.AtlasGenSpriteBatch;

public class GridStage extends StageX {

    public GridStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    @Override
    public void draw() {
        if (GdxMaster.WRITE_ATLAS_IMAGES) {
            if (getBatch() instanceof AtlasGenSpriteBatch) {
                ((AtlasGenSpriteBatch) getBatch()).setAtlas(AtlasGenSpriteBatch.ATLAS_GROUP.grid);
            }
        }
        super.draw();
    }
}
