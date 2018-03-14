package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxMaster;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.gui.RollDecorator;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.screens.map.layers.LightLayer;
import main.libgdx.screens.map.obj.PartyActor;
import main.libgdx.screens.map.ui.*;
import main.libgdx.screens.map.ui.time.MapTimePanel;
import main.libgdx.stage.GuiStage;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static main.system.MapEvent.CREATE_PARTY;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapGuiStage extends GuiStage {
    private final String vignettePath = "ui\\macro\\vignette.png";
    private final LightLayer lights;
    PartyActor mainPartyMarker;
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
            vignette.getContent().setWidth(GdxMaster.getWidth());
            vignette.getContent().setHeight(GdxMaster.getHeight());
            vignette.setAlphaStep(0.1f);
            vignette.setFluctuatingAlphaRandomness(0.3f);
            vignette.setFluctuatingFullAlphaDuration(1.5f);
            addActor(vignette);
            vignette.setTouchable(Touchable.disabled);
        }
        addActor(lights = new LightLayer(true));
        init();

        GuiEventManager.bind(MapEvent.DATE_CHANGED, p -> {
            update();
        });
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {

            if (!CoreEngine.isMapEditor())
                blackout.fadeOut(1.25f);
            MacroGame.getGame().setTime((DAY_TIME) p.get());
            new Thread(new Runnable() {
                public void run() {
                    WaitMaster.WAIT(1500);
                    GuiEventManager.trigger(MapEvent.TIME_CHANGED, p.get());
                }
            }, " thread").start();

        });
        resetZIndices();
    }

    public void resetZIndices() {
        if (CoreEngine.isMapEditor())
            return;
        super.resetZIndices();
        lights.setZIndex(0);
        vignette.setZIndex(0);

    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(MapEvent.TIME_UPDATED, p -> {

            if (!CoreEngine.isMapEditor())
                blackout.fadeIn(1.25f);
        });
        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            if (party == null) {
                return;
            }
            if (party.isMine()) {
                setParty(party);
            }
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
        addActor(
         RollDecorator.decorate(partyInfoPanel));

        actionPanel = new MapActionPanel();
        addActor(RollDecorator.decorate(actionPanel, FACING_DIRECTION.SOUTH));
        //background? roll out decorator

//        resources = new MapResourcesPanel();
//        addActor(resources);
//        resources.setPosition(GdxMaster.centerWidth(resources),
//         GdxMaster.top(resources));

        datePanel = new MapDatePanel();
        addActor(RollDecorator.decorate(datePanel, FACING_DIRECTION.WEST));

        timePanel = new MapTimePanel();
        addActor(timePanel); //RollDecorator.decorate(timePanel, FACING_DIRECTION.NORTH));

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty) {
            timePanel.setPosition(GdxMaster.centerWidthScreen(timePanel),
             GdxMaster.topScreen(timePanel) + 13 * (1 + GdxMaster.getFontSizeMod()) / 2);
            actionPanel.layout();
            actionPanel.getParent().setPosition(GdxMaster.centerWidthScreen(actionPanel.getParent())
             , 0);
            partyInfoPanel.getParent().setPosition(0,
             GdxMaster.topScreen(partyInfoPanel.getParent()) - 70
            );
            dirty = false;
        }
        Coordinates c = mainPartyMarker.getParty().getCoordinates();
        Vector3 pos = MapScreen.getInstance().getCamera().position;
if (        MapScreen.getInstance().getController().isWithinCamera(c.x, c.y, 128, 128))
{
    mainPartyMarker.setVisible(false);
    return;
}

        Line2D line = new Float(c.x, c.y,  pos.x,   pos.y );
        Rectangle2D rect = new Rectangle(
         (int) pos.x - GdxMaster.getWidth()/2,
         (int) pos.y + GdxMaster.getHeight()/2,
         (int) GdxMaster.getWidth() ,
         (int)  GdxMaster.getHeight()
        );
        Point2D[] points = GeometryMaster.getIntersectionPoint(line, rect);
        Point2D point = null ;
        double dst = Double.MAX_VALUE;

        for (Point2D sub : points) {
            double dst1 = sub.distance(c.x, c.y+ GdxMaster.getHeight());
            if (dst1<dst){
                dst = dst1;
                point = sub;
            }
        }

          dst = dst-500;
        if (dst > 100) {
            float scale = (float) (10f/ Math.sqrt(Math.sqrt(dst)));
            mainPartyMarker.setScale(scale);
        }
        mainPartyMarker.setPosition((float)point.getX(), (float)point.getY()- GdxMaster.getHeight());
        mainPartyMarker.setZIndex(Integer.MAX_VALUE);

        //set scale depending on how far we are
    }

    public void setMainPartyMarker(PartyActor mainPartyMarker) {
        this.mainPartyMarker = mainPartyMarker;
        mainPartyMarker.setMarker(true);
        addActor(mainPartyMarker);
        mainPartyMarker.clearListeners();
        mainPartyMarker.hover();
        mainPartyMarker.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MapScreen.getInstance().centerCamera();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
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
