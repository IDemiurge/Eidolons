package libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.unit.Unit;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.menu.selection.SelectionPanel;
import libgdx.gui.menu.selection.difficulty.DifficultySelectionPanel;
import libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import libgdx.gui.menu.selection.manual.ManualPanel;
import libgdx.gui.menu.selection.town.quest.QuestSelectionPanel;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import libgdx.stage.LoadingStage;
import libgdx.video.VideoMaster;
import main.entity.Entity;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static main.system.GuiEventType.*;

public abstract class ScreenWithVideoLoader extends ScreenWithLoaderAndUI {
    private static final Object DIFFICULTY_PANEL_ARG = 1;
    private static final Object QUEST_PANEL_ARG = 2;
    private static Boolean videoEnabled;
    protected VideoMaster loadVideo;
    protected Label underText;

    public ScreenWithVideoLoader() {
        //TODO loader here, but need data!
        super();


        if (isLoadingWithVideo())
            initVideo();
        underText = new Label(LoadingStage.getBottomText(), StyleHolder.getHqLabelStyle(20));
        getOverlayStage().addActor(underText);
        underText.setPosition(GdxMaster.centerWidth(underText), 50);

        GuiEventManager.bind(BRIEFING_START, p -> underText.setVisible(false));
        GuiEventManager.bind(BRIEFING_FINISHED, p -> underText.setVisible(true));
    }

    public static void setVideoEnabled(Boolean videoEnabled) {
        ScreenWithVideoLoader.videoEnabled = videoEnabled;
    }

    public static Boolean isVideoEnabled() {
        if (GdxMaster.getWidth() != 1920) {
            return false;
        }
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
        if (p.get() == null) {
            selectionPanelClosed();
            updateInputController();
            return;
        } else if (!(p.get() instanceof Integer)) {
            //            if (selectionPanel != null)// TODO why was it necessary?
            //                selectionPanel.cancel(false);
        }

        selectionPanel =
                createSelectionPanel(p);
        addSelectionPanel(selectionPanel);
    }

    @Override
    protected void preLoad() {
        super.preLoad();

        GuiEventManager.bind(true, SHOW_LOAD_PANEL, this::selectionPanelEvent);
        GuiEventManager.bind(true, SHOW_SELECTION_PANEL, this::selectionPanelEvent);
        GuiEventManager.bind(true, HC_SHOW, this::showHeroCreationPanel);
        GuiEventManager.bind(true, GuiEventType.SHOW_DIFFICULTY_SELECTION_PANEL, p -> {
            GuiEventManager.trigger(SHOW_SELECTION_PANEL, DIFFICULTY_PANEL_ARG);
        });
        GuiEventManager.bind(true, GuiEventType.SHOW_QUEST_SELECTION, p -> {
            GuiEventManager.triggerWithParams(SHOW_SELECTION_PANEL, QUEST_PANEL_ARG, p.get());
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


    private void showHeroCreationPanel(EventCallbackParam p) {
        if (p.get() == null) {
            overlayStage.setActive(false);
            updateInputController();
        } else {
            Unit unit = (Unit) p.get();
            HqDataMaster.getInstance(unit); //init model
            //update
            overlayStage.setActive(true);
            updateInputController();
        }


    }


    protected void back() {
    }

    private void addSelectionPanel(SelectionPanel selectionPanel) {
        if (selectionPanel.isDone()) {
            return;
        }

        boolean displayOnLoader = !(selectionPanel instanceof HeroSelectionPanel); // loading == isLoadingWithVideo();
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

        GdxMaster.setDefaultCursor();
    }

    protected void selectionPanelClosed() {
        if (selectionPanel != null)
            try {
                selectionPanel.setStage(null);
                selectionPanel.setVisible(false);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        GdxMaster.setLoadingCursor();
    }

    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        if (p.get() == DIFFICULTY_PANEL_ARG) {
            return new DifficultySelectionPanel();
        }
        if (p.get() instanceof Collection) {
            Iterator iterator = ((Collection) p.get()).iterator();
            if (iterator.next() == QUEST_PANEL_ARG) {
                return new QuestSelectionPanel(() -> (List<? extends Entity>) iterator.next());
            }
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
        } else if (loadVideo != null) {
            //            try {
            //                loadVideo.stop();
            //                loadVideo.getPlayer().dispose();
            //                loadVideo = null;
            //            } catch (Exception e) {
            //                main.system.ExceptionMaster.printStackTrace(e);
            //            }
        }
    }

    protected void initVideo() {
        if (isVideoEnabled())
            try {
                loadVideo = new VideoMaster();
            } catch (Exception e) {
                main.system.auxiliary.log.LogMaster.log(1, "VIDEO INIT FAILED!");
                videoEnabled = false;
                main.system.ExceptionMaster.printStackTrace(e);
            }
    }

    private void playVideo() {
        try {
            loadVideo.playTestVideo();
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.log(1, "VIDEO PLAY FAILED!");
            main.system.ExceptionMaster.printStackTrace(e);
            loadVideo = null;
            videoEnabled = false;
        }
    }

    protected void renderLoader(float delta) {
        super.renderLoader(delta);

        if (loadVideo != null) {
            renderVideo(delta);
            if (isVideoAsBackground()) {
                overlayStage.act(delta);
                overlayStage.draw();
            }

        }
    }

    protected boolean isVideoAsBackground() {
        return false;
    }

    @Override
    public void backToLoader() {
        super.backToLoader();
        overlayStage.setActive(true);
        initVideo();
    }

    @Override
    protected InputProcessor createInputController() {
        if (overlayStage.isActive())
            return GdxMaster.getMultiplexer(overlayStage);
        return super.createInputController();
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

    @Override
    public CustomSpriteBatch getBatch() {
        return super.getBatch();
    }

    protected boolean isLoadingWithVideo() {
        return false;
    }

    protected void renderVideo(float delta) {
        if (!loadVideo.isAvailable()) return;
        if (loadVideo.getPlayer() == null)
            playVideo();
        else if (!loadVideo.getPlayer().isPlaying()) {
            if (isLooped())
                playVideo();
        }
        //        Gdx.gl.glViewport(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        if (isClearForVideo())
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (!loadVideo.getPlayer().render())
            if (isLooped())
                playVideo();

    }

    protected boolean isClearForVideo() {
        return false;
    }


    public boolean isLooped() {
        return false;
    }

}
