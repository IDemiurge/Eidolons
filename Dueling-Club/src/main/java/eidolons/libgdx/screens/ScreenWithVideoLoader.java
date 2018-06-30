package eidolons.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.difficulty.DifficultySelectionPanel;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import eidolons.libgdx.gui.menu.selection.manual.ManualPanel;
import eidolons.libgdx.stage.LoadingStage;
import eidolons.libgdx.video.VideoMaster;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Entity;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.List;

import static main.system.GuiEventType.SHOW_LOAD_PANEL;
import static main.system.GuiEventType.SHOW_SELECTION_PANEL;

public abstract class ScreenWithVideoLoader extends ScreenWithLoader {
    private static final Object DIFFICULTY_PANEL_ARG = 1;
    private static Boolean videoEnabled;
    protected VideoMaster video;
    protected boolean looped;
    private Label underText;

    public ScreenWithVideoLoader() {
        //TODO loader here, but need data!
        super();
        if (isLoadingWithVideo())
            if (isVideoEnabled())
                initVideo();
        looped = true;
        underText = new Label(LoadingStage. getBottonText(), StyleHolder.getHqLabelStyle(20));
        getOverlayStage().addActor(underText);
        underText.setPosition(GdxMaster.centerWidth(underText), 0);

    }

    public static void setVideoEnabled(Boolean videoEnabled) {
        ScreenWithVideoLoader.videoEnabled = videoEnabled;
    }

    public static Boolean isVideoEnabled() {
        if (videoEnabled == null)
            videoEnabled = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VIDEO);
        return videoEnabled;
    }

    @Override
    protected boolean isTooltipsOn() {
        if (selectionPanel != null)
            if (selectionPanel.isVisible())
                return false;
        if (manualPanel != null)
            if (manualPanel.isVisible())
                return false;
        return super.isTooltipsOn();
    }

    private void selectionPanelEvent(EventCallbackParam p) {
        if (p.get() != DIFFICULTY_PANEL_ARG) {
            if (selectionPanel != null)
                selectionPanel.cancel(false);
            if (p.get() == null) {
                selectionPanelClosed();
                updateInputController();
                return;
            }
        }
        selectionPanel =
         createSelectionPanel(p);
        addSelectionPanel(selectionPanel);
    }
    @Override
    protected void preLoad() {
        super.preLoad();

        GuiEventManager.bind(true, SHOW_LOAD_PANEL, p -> {
            selectionPanelEvent(p);
        });
        GuiEventManager.bind(true, SHOW_SELECTION_PANEL, p -> {
            selectionPanelEvent(p);
        });
        GuiEventManager.bind(true, GuiEventType.SHOW_DIFFICULTY_SELECTION_PANEL, p -> {
            GuiEventManager.trigger(SHOW_SELECTION_PANEL, DIFFICULTY_PANEL_ARG);
        });
        GuiEventManager.bind(true, GuiEventType.SHOW_MANUAL_PANEL, p -> {
            if (manualPanel != null) {
                manualPanel.closed(null);
            } else {
                manualPanel = new ManualPanel() {
                    @Override
                    public void close() {
                        super.close();
                        ScreenWithVideoLoader.this.back();
                    }
                };
            }

            if (p.get() == null) {
                manualPanel.setStage(null);
                updateInputController();
                return;
            }
            addSelectionPanel(manualPanel);
        });
    }


    protected void back() {
    }

    private void addSelectionPanel(SelectionPanel selectionPanel) {
        boolean displayOnLoader = loading == isLoadingWithVideo();
        Stage stage = displayOnLoader ? getOverlayStage() : getMainStage();
        if (stage != null) {
            stage.addActor(selectionPanel);
            selectionPanel.setStage(stage);
        }
        getOverlayStage().setActive(true);

        updateInputController();
        selectionPanel.setVisible(true);
        selectionPanel.setPosition(
         GdxMaster.right(selectionPanel),
         GdxMaster.centerHeight(selectionPanel));

        selectionPanel.fadeIn();
    }

    protected void selectionPanelClosed() {
        try {
            selectionPanel.setStage(null);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        if (p.get() == DIFFICULTY_PANEL_ARG) {
            return new DifficultySelectionPanel();
        }
        return new HeroSelectionPanel(() -> (List<? extends Entity>) p.get());

    }

    protected Stage getMainStage() {
        return null;
    }

    @Override
    protected void hideLoader() {
        super.hideLoader();
        if (!isLoadingWithVideo()) {
            initVideo();
        } else if (video != null) {
            try {
                video.stop();
                video.getPlayer().dispose();
                video = null;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    protected void initVideo() {
        try {
            video = new VideoMaster();
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.log(1, "VIDEO INIT FAILED!");
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            videoEnabled = false;
//            OptionsMaster.getGraphicsOptions().setValue(GRAPHIC_OPTION.VIDEO, false);
        }
    }

    private void playVideo() {
        try {
            video.playTestVideo();
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.log(1, "VIDEO PLAY FAILED!");
            main.system.ExceptionMaster.printStackTrace(e);
            video = null;
            videoEnabled=false;
        }
    }
    @Override
    public void render(float delta) {
        if (CoreEngine.isJar() && !CoreEngine.isCrashSafeMode()) {
            super.render(delta);
        } else
            try {
                super.render(delta);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }


    }

    protected void renderLoader(float delta) {

        super.renderLoader(delta);

        if (video != null) {
            renderVideo(delta);
            overlayStage.act(delta);
            overlayStage.draw();

        }
    }

    @Override
    public void backToLoader() {
        super.backToLoader();
        overlayStage.setActive(true);
        initVideo();
    }

    @Override
    protected InputProcessor getInputController() {
        if (overlayStage.isActive())
            return new InputMultiplexer(overlayStage);
        return super.getInputController();
    }

    @Override
    protected void renderMain(float delta) {
//        if (!isVideoEnabled()) {
            loadingStage.act(delta);
            loadingStage.draw();
            overlayStage.act(delta);
            overlayStage.draw();
//        }
    }

    protected boolean isLoadingWithVideo() {
        return true;
    }

    protected void renderVideo(float delta) {
        if (!video.isAvailable()) return;
        if (video.getPlayer() == null)
            playVideo();
        else if (!video.getPlayer().isPlaying())
            playVideo();
        Gdx.gl.glViewport(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        if (isClearForVideo())
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (!video.getPlayer().render())
            if (isLooped())
                playVideo();


    }

    protected boolean isClearForVideo() {
        return false;
    }


    public boolean isLooped() {
        return looped;
    }

    public void setLooped(boolean looped) {
        this.looped = looped;
    }
}
