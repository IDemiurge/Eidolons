package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxMaster;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.screens.map.sfx.LightLayer;
import main.libgdx.screens.map.ui.*;
import main.libgdx.screens.map.ui.time.MapTimePanel;
import main.libgdx.stage.GuiStage;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapGuiStage extends GuiStage {
    private final String vignettePath = "ui\\macro\\vignette.png";
    private final LightLayer lights;
    private PartyInfoPanel partyInfoPanel;
    private MapActionPanel actionPanel;
    private boolean dirty;
    private MapResourcesPanel resources;
    private MapTimePanel timePanel;
    private MapDatePanel datePanel;
    private SuperContainer vignette;
    private MapKeyHandler keyHandler = new MapKeyHandler();

    public MapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        if (isVignetteOn()) {
            vignette = new SuperContainer(
             new Image(TextureCache.getOrCreateR(vignettePath)),
             true) {
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
        addActor(lights = new LightLayer(true));
        init();

        GuiEventManager.bind(MapEvent.DATE_CHANGED, p -> {
            update();
        });
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(MapEvent.TIME_UPDATED, p -> {
            blackout.fadeIn(1.25f);
        });
            GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            blackout.fadeOut(1.25f);
            MacroGame.getGame().setTime((DAY_TIME) p.get());
            new Thread(new Runnable() {
                public void run() {
                    WaitMaster.WAIT(1500);
                    GuiEventManager.trigger(MapEvent.TIME_CHANGED, p.get());
                }
            }, " thread").start();

        });
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    protected void init() {
        super.init();
        partyInfoPanel = new PartyInfoPanel();
        addActor(partyInfoPanel);

        actionPanel = new MapActionPanel();
        addActor(actionPanel);
        //background? roll out decorator

//        resources = new MapResourcesPanel();
//        addActor(resources);
//        resources.setPosition(GdxMaster.centerWidth(resources),
//         GdxMaster.top(resources));

        datePanel = new MapDatePanel();
        addActor(datePanel);

        timePanel = new MapTimePanel();
        addActor(timePanel);
        timePanel.setPosition(GdxMaster.centerWidth(timePanel),
         GdxMaster.top(timePanel) + 12);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty) {
            actionPanel.layout();
            actionPanel.setPosition(GdxMaster.centerWidth(actionPanel)
             , 0);
            partyInfoPanel.setPosition(0,
             GdxMaster.top(partyInfoPanel) - 70
            );
            dirty = false;
        }
    }

    protected boolean isVignetteOn() {
        return true;
    }

    @Override
    public boolean keyDown(int keyCode) {
        keyHandler.keyDown(keyCode);
        return true;
    }

    @Override
    protected GameMenu createGameMenu() {
        return new MapMenu();//TODO loop
    }

    @Override
    protected boolean handleKeyTyped(char character) {
        keyHandler.handleKeyTyped(character);
        return true; //TODO
    }

    public void setParty(MacroParty party) {
        partyInfoPanel.setUserObject(party);
        actionPanel.setUserObject(party);
        dirty = true;
    }

    public void update() {
//            update(getRoot().getChildren());
    }

    private void update(SnapshotArray<Actor> children) {
        for (Actor sub : children) {
            if (sub instanceof TablePanel) {
                ((TablePanel) sub).setUpdateRequired(true);
            }
            if (sub instanceof Group)
                update(((Group) sub).getChildren());
        }
    }

    public SuperContainer getVignette() {
        return vignette;
    }

    public LightLayer getLights() {
        return lights;
    }
}
