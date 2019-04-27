package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.manual.ManualPanel;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.stage.ChainedStage;
import eidolons.libgdx.stage.LoadingStage;
import eidolons.libgdx.stage.UiStage;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.PostProcessingOptions;
import eidolons.system.text.TipMaster;
import main.system.EventCallbackParam;
import main.system.auxiliary.log.Chronos;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 11/28/2017.
 */
public abstract class ScreenWithLoaderAndUI extends ScreenWithLoader {
    protected UiStage overlayStage;
    protected SelectionPanel selectionPanel;
    protected ManualPanel manualPanel;

    public ScreenWithLoaderAndUI() {
        super();
        overlayStage = new UiStage();
    }

    public UiStage getOverlayStage() {
        return overlayStage;
    }

    protected void renderLoaderAndOverlays(float delta) {
        loadingStage.act(delta);
        loadingStage.draw();
        overlayStage.act(delta);
        overlayStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        overlayStage.getRoot().setSize(width, height);
        overlayStage.getViewport().update(width, height);
        loadingStage.getRoot().setSize(width, height);
        loadingStage.getViewport().update(width, height);
    }

}
