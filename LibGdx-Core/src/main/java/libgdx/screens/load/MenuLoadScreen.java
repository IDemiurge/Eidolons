package libgdx.screens.load;

import libgdx.assets.AssetEnums;
import libgdx.screens.SCREEN_TYPE;
import main.system.launch.Flags;

public class MenuLoadScreen extends LoadScreen {

    public static final AssetEnums.ATLAS[] atlases = {
            AssetEnums.ATLAS.UI_BASE, AssetEnums.ATLAS.UI_DC
    };
    public static final AssetEnums.ATLAS[] atlasesJar = {
            AssetEnums.ATLAS.UI_BASE, AssetEnums.ATLAS.UI_DC,
            AssetEnums.ATLAS.SPRITES_GRID, AssetEnums.ATLAS.UNIT_VIEW,
            AssetEnums.ATLAS.SPRITES_UI, AssetEnums.ATLAS.TEXTURES
    };
    public static final AssetEnums.ATLAS[] atlases_lite = {
            AssetEnums.ATLAS.UI_BASE, AssetEnums.ATLAS.UI_DC,
            AssetEnums.ATLAS.SPRITES_ONEFRAME, AssetEnums.ATLAS.UNIT_VIEW,
            AssetEnums.ATLAS.TEXTURES
    };

    public MenuLoadScreen() {
        super("ui/main/logo fullscreen.png", SCREEN_TYPE.MAIN_MENU,
                Flags.isJarlike()
                        ? Flags.isLiteLaunch() ? atlases_lite : atlasesJar
                        : atlases);

    }
}
