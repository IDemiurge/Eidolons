package eidolons.libgdx.screens.load;

import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.screens.SCREEN_TYPE;

import static eidolons.libgdx.assets.AssetEnums.ATLAS.UI_DC;


public class DungeonLoadScreen extends LoadScreen{

    private static final AssetEnums.ATLAS[] atlasesToLoad ={
            UI_DC,
    } ;
    GearCluster gears;

    public DungeonLoadScreen(String backgroundPath ) {
        super(backgroundPath, SCREEN_TYPE.DUNGEON, atlasesToLoad);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        gears.act(delta);
        gears.draw(batch, 1);
    }
}
