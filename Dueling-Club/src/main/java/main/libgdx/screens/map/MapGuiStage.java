package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxMaster;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.screens.map.ui.*;
import main.libgdx.stage.GuiStage;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapGuiStage extends GuiStage {
    private final String vignettePath="ui\\macro\\vignette.png";
    private PartyInfoPanel partyInfoPanel;
    private MapActionPanel actionPanel;
    private boolean dirty;
    private MapResourcesPanel resources;
    private MapTimePanel timePanel;
    private MapDatePanel datePanel;
    private SuperContainer vignette;

    public MapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        if (isVignetteOn()) {
             vignette = new SuperContainer(
             new Image(TextureCache.getOrCreateR(vignettePath)),
             true){
                @Override
                protected float getAlphaFluctuationMin() {
                    return 0.3f;
                }
                @Override
                protected float getAlphaFluctuationMax() {
                    return 1;
                }
            };
            vignette.setWidth(GdxMaster.getWidth());
            vignette.setHeight(GdxMaster.getHeight());
            vignette.setAlphaStep(0.1f);
            vignette.setFluctuatingAlphaRandomness(0.3f);
            vignette.setFluctuatingFullAlphaDuration(1.5f);
            addActor(vignette);
        }
        init();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    protected void init() {
        super.init();
        partyInfoPanel= new PartyInfoPanel();
        addActor(partyInfoPanel);

        actionPanel= new MapActionPanel();
        addActor(actionPanel);
        //background? roll out decorator

        resources = new MapResourcesPanel();
        addActor(resources);
        resources.setPosition(GdxMaster.centerWidth(resources),
         GdxMaster.top(resources));

        datePanel = new MapDatePanel();
        addActor(datePanel);

        timePanel = new MapTimePanel();
        addActor(timePanel);
        timePanel.setPosition(GdxMaster.centerWidth(timePanel),
         GdxMaster.top(timePanel));

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty){
            actionPanel.setPosition(GdxMaster.centerWidth(actionPanel)
             , 0);
            partyInfoPanel.setPosition(0,
             GdxMaster.top(partyInfoPanel)-50
            );
            dirty = false;
        }
    }

    protected boolean isVignetteOn() {
        return true;
    }

    @Override
    public boolean keyDown(int keyCode) {
        return true;
    }
    @Override
    protected GameMenu createGameMenu() {
        return new MapMenu();//TODO loop
    }

    @Override
    protected boolean handleKeyTyped(char character) {
        return true; //TODO
    }

    public void setParty(MacroParty party) {
        partyInfoPanel.setUserObject(party);
        actionPanel.setUserObject(party);
        dirty = true;
    }


    public SuperContainer getVignette() {
        return vignette;
    }
}
