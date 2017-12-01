package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.entity.Entity;
import main.libgdx.GdxMaster;
import main.libgdx.gui.menu.selection.SelectionPanel;
import main.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import main.libgdx.stage.UiStage;
import main.libgdx.video.VideoMaster;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.List;

public abstract class ScreenWithVideoLoader extends ScreenWithLoader {
    protected UiStage overlayStage;
    protected VideoMaster video;
    protected boolean looped;
    protected SelectionPanel selectionPanel;


    public ScreenWithVideoLoader() {
        //TODO loader here, but need data!
        super();
        if (isVideoLoader())
        if (isVideoEnabled())
          initVideo();
        looped =true;
        overlayStage= new UiStage();

    }

    protected boolean isVideoEnabled() {
        return OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VIDEO);
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        GuiEventManager.bind(true, GuiEventType.SHOW_SELECTION_PANEL , p->{
            if (selectionPanel!=null )
                selectionPanel.closed(null);
            if (p.get()==null )
            {
                selectionPanelClosed();
                updateInputController();
                return ;
            }
            selectionPanel=
             createSelectionPanel(p);

            boolean displayOnLoader = hideLoader!=isVideoLoader();
            if (displayOnLoader) {
                getOverlayStage().addActor(selectionPanel);
            } else {
                if (getMainStage()!= null )
                getMainStage().addActor(selectionPanel);
            }
            getOverlayStage().setActive(true);
            updateInputController();

            selectionPanel.setPosition(
             GdxMaster.right(selectionPanel),
//               GdxMaster.centerWidth(selectionPanel),
             GdxMaster.centerHeight(selectionPanel));
        });
    }

    protected void selectionPanelClosed() {
    }

    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        return new HeroSelectionPanel(()-> (List<? extends Entity>) p.get());

    }

    protected   Stage getMainStage(){
        return null ;
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
