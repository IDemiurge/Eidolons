package eidolons.libgdx.screens.load;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.assets.Atlases;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithAssets;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.Textures;
import main.data.filesys.PathFinder;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.Flags;

/**
 * Created by JustMe on 10/9/2018.
 */
public class LoadingScreen extends ScreenWithAssets {
    private final Image background;
    private final Label label;
    private final CustomSpriteBatch batch;
    private final SCREEN_TYPE toLoad;

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

    public LoadingScreen() {
        this("ui/main/logo fullscreen.png", SCREEN_TYPE.MAIN_MENU,
                Flags.isJarlike()
                        ? Flags.isLiteLaunch() ? atlases_lite : atlasesJar
                        : atlases);

    }

    public LoadingScreen(String backgroundPath, SCREEN_TYPE toLoad, AssetEnums.ATLAS... atlasesToLoad) {
        if (TextureCache.atlasesOn) //will it even get here without  ??
            for (AssetEnums.ATLAS atlas : atlasesToLoad) {
                Atlases.preloadAtlas(atlas);
            }
        this.toLoad = toLoad;
        background = new Image(new Texture(GDX.file(
                PathFinder.getImagePath() + backgroundPath)));

        float x = new Float(GdxMaster.getWidth()) / 1920;
        float y = new Float(GdxMaster.getHeight()) / 1080;
        background.setScale(Math.max(x, y));
        //lerp?
        //        background.setPosition(GdxMaster.centerWidth(background),
        //         GdxMaster.centerHeight(background));

        label = new Label(getLabelText(),
                StyleHolder.getSizedLabelStyle(FONT.AVQ, 22));
        label.pack();
        label.setPosition(GdxMaster.centerWidth(label),
                GdxMaster.getHeight() / 20 + 35);

        batch = GdxMaster.getMainBatch();
    }

    protected String getLabelText() {
        return "Greetings. Please await as we load...";
    }

    @Override
    public void assetsLoaded() {

        main.system.auxiliary.log.LogMaster.log(1, toString() + " assetsLoaded!");
        ScreenData data = new ScreenData(toLoad, ""); //TODO data
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new EventCallbackParam(data));
        Textures.init();
    }

    @Override
    public void render(float delta) {
        label.setText(label.getText() + ".");
        label.pack();
        label.setPosition(GdxMaster.centerWidth(label),
                GdxMaster.getHeight() / 20 + 35);

        batch.setColor(1, 1, 1, 1);
        batch.begin();
        background.draw(batch, 1);
        label.draw(batch, 1);
        batch.end();
    }

}
