package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.entity.Entity;
import main.libgdx.GdxMaster;
import main.libgdx.gui.menu.selection.SelectionPanel;
import main.libgdx.gui.menu.selection.difficulty.DifficultySelectionPanel;
import main.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import main.libgdx.gui.menu.selection.manual.ManualPanel;
import main.libgdx.stage.UiStage;
import main.libgdx.video.VideoMaster;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.List;

import static main.system.GuiEventType.SHOW_SELECTION_PANEL;

public abstract class ScreenWithVideoLoader extends ScreenWithLoader {
    private static final Object DIFFICULTY_PANEL_ARG = 1;
    protected UiStage overlayStage;
    protected VideoMaster video;
    protected boolean looped;
    protected SelectionPanel selectionPanel;
    protected ManualPanel manualPanel;


    public ScreenWithVideoLoader() {
        //TODO loader here, but need data!
        super();
        if (isVideoLoader())
            if (isVideoEnabled())
                initVideo();
        looped = true;
        overlayStage = new UiStage();

    }

    protected boolean isVideoEnabled() {
        return OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VIDEO);
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        GuiEventManager.bind(true, SHOW_SELECTION_PANEL, p -> {
            if (p.get() != DIFFICULTY_PANEL_ARG) {
                if (selectionPanel != null)
                    selectionPanel.closed(null);
                if (p.get() == null) {
                    selectionPanelClosed();
                    updateInputController();
                    return;
                }
            }
            selectionPanel =
             createSelectionPanel(p);
            addSelectionPanel(selectionPanel);

        });
        GuiEventManager.bind(true, GuiEventType.SHOW_DIFFICULTY_SELECTION_PANEL, p -> {
            GuiEventManager.trigger(SHOW_SELECTION_PANEL, DIFFICULTY_PANEL_ARG);
        });
        GuiEventManager.bind(true, GuiEventType.SHOW_MANUAL_PANEL, p -> {
            if (manualPanel != null)
                manualPanel.closed(null);
            else
                try {
                    manualPanel = new ManualPanel();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return;
                }
            if (p.get() == null) {
                selectionPanelClosed();
                updateInputController();
                return;
            }
            addSelectionPanel(manualPanel);
        });
    }

    private void addSelectionPanel(SelectionPanel selectionPanel) {
        boolean displayOnLoader = hideLoader != isVideoLoader();
        if (displayOnLoader) {
            getOverlayStage().addActor(selectionPanel);
        } else {
            if (getMainStage() != null)
                getMainStage().addActor(selectionPanel);
        }
        getOverlayStage().setActive(true);
//        selectionPanel.setStage(getOverlayStage());
        updateInputController();
        selectionPanel.setVisible(true);
        selectionPanel.setPosition(
         GdxMaster.right(selectionPanel),
         GdxMaster.centerHeight(selectionPanel));
    }

    protected void selectionPanelClosed() {
        selectionPanel.setStage(null);
    }

    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        if (p.get()==DIFFICULTY_PANEL_ARG ){
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
        if (!isVideoLoader()) {
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
        video = new VideoMaster();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    protected void renderLoader(float delta) {

        if (video != null) {
            renderVideo(delta);
            overlayStage.act(delta);
            overlayStage.draw();
            return;
        }
        super.renderLoader(delta);

    }

    @Override
    protected InputMultiplexer getInputController() {
        if (overlayStage.isActive())
            return new InputMultiplexer(overlayStage);
        return super.getInputController();
    }

    @Override
    protected void renderMain(float delta) {
    }

    public UiStage getOverlayStage() {
        return overlayStage;
    }

    protected boolean isVideoLoader() {
        return true;
    }

    protected void renderVideo(float delta) {
        if (!video.isAvailable()) return;
        if (video.getPlayer() == null)
            playVideo();
        else if (!video.getPlayer().isPlaying())
            playVideo();
        Gdx.gl.glViewport(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        try {
            if (!video.getPlayer().render()) {
                if (isLooped())
                    playVideo();
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private void playVideo() {
        video.playTestVideo();
    }

    public boolean isLooped() {
        return looped;
    }

    public void setLooped(boolean looped) {
        this.looped = looped;
    }
}
