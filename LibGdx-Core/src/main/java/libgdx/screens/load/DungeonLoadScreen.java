package libgdx.screens.load;

import eidolons.content.consts.VisualEnums;
import libgdx.assets.AssetEnums;
import libgdx.gui.generic.GearCluster;

import static libgdx.assets.AssetEnums.ATLAS.UI_DC;


public class DungeonLoadScreen extends LoadScreen{

    private static final AssetEnums.ATLAS[] atlasesToLoad ={
            UI_DC,
    } ;
    GearCluster gears;

    public DungeonLoadScreen(String backgroundPath ) {
        super(backgroundPath, VisualEnums.SCREEN_TYPE.DUNGEON, atlasesToLoad);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        gears.act(delta);
        gears.draw(batch, 1);
    }
}
