package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxMaster;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.screens.map.ui.MapActionPanel;
import main.libgdx.screens.map.ui.PartyInfoPanel;
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

    public MapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        if (isVignetteOn()) {
            SuperContainer vignette = new SuperContainer(
             new Image(TextureCache.getOrCreateR(vignettePath)),
             true);
            vignette.setWidth(GdxMaster.getWidth());
            vignette.setHeight(GdxMaster.getHeight());
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
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty){
            actionPanel.setPosition(GdxMaster.centerWidth(actionPanel)
             , 0);
            partyInfoPanel.setPosition(0,
             500
             //GdxMaster.top(partyInfoPanel)
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
}
