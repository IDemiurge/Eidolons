package eidolons.libgdx.screens.load;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.assets.Atlases;
import eidolons.libgdx.bf.generic.FadeImageContainer;
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
 * these are only used for the FIRST TIME when we load a screen!
 */
public abstract class LoadScreen extends ScreenWithAssets {
    protected final FadeImageContainer background;
    protected final Label loadLabel;
    protected final Label infoLabel;
    protected final CustomSpriteBatch batch;
    protected final SCREEN_TYPE toLoad;
    private final AssetEnums.ATLAS[] atlasesToLoad;

    private int ticks;
    private boolean loadStarted;

    public LoadScreen(String backgroundPath, SCREEN_TYPE toLoad, AssetEnums.ATLAS... atlasesToLoad) {

        this.toLoad = toLoad;
        this.atlasesToLoad = atlasesToLoad;
        background = new FadeImageContainer();
        background.setNoAtlas(true);
        background.setImage(PathFinder.getImagePath() + backgroundPath);

        float x = (float) GdxMaster.getWidth() / 1920;
        float y = (float) GdxMaster.getHeight() / 1080;
        background.setScale(Math.max(x, y));
        background.fadeIn();
        //lerp?
        //        background.setPosition(GdxMaster.centerWidth(background),
        //         GdxMaster.centerHeight(background));

        loadLabel = new Label(getLabelText(),
                StyleHolder.getSizedLabelStyle(FONT.AVQ, 22));
        loadLabel.pack();
        loadLabel.setPosition(GdxMaster.centerWidth(loadLabel),
                GdxMaster.getHeight() / 20 + 35);

        infoLabel = new Label("",
                StyleHolder.getSizedLabelStyle(FONT.MAIN, 20));
        infoLabel.setPosition(35,
                GdxMaster.getHeight() - 115);

        batch = GdxMaster.getMainBatch();
    }

    protected String getLabelText() {
        return "Greetings. Please await";
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

        batch.setColor(1, 1, 1, 1);
        batch.begin();
        background.act(delta);
        background.draw(batch, 1);
        loadLabel.draw(batch, 1);
        infoLabel.draw(batch, 1);
        batch.end();

        if (!loadStarted){
            load();
            loadStarted = true;
            return;
        }
        if (!Assets.get().getManager().update()) {
            loadTick();
        } else {
            assetsLoaded();
        }
    }

    private void load() {
        if (TextureCache.atlasesOn) //will it even get here without  ??
            for (AssetEnums.ATLAS atlas : atlasesToLoad) {
                Atlases.preloadAtlas(atlas);
            }
    }

    private void loadTick() {
        main.system.auxiliary.log.LogMaster.devLog("Assets being loaded...");
        if (ticks++ > getMaxDots()*getTickPeriod()) {
            loadLabel.setText(getLabelText());
            ticks = 0;
        } else
        {
            if (ticks%getTickPeriod()==0)
                loadLabel.setText(loadLabel.getText() + ".");
        }

        loadLabel.pack();
        if (isCenteredLabel())
            loadLabel.setPosition(GdxMaster.centerWidth(loadLabel),
                    GdxMaster.getHeight() / 20 + 35);
        if (isDebug())
            infoLabel.setText(Assets.get().getManager().getDiagnostics());
    }

    private boolean isDebug() {
        return Flags.isMe();
    }

    private boolean isCenteredLabel() {
        return false;
    }

    private int getMaxDots() {
        return 5;
    }

    private int getTickPeriod() {
        return 5;
    }
}
