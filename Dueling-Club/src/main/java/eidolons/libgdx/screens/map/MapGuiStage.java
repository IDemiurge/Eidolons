package eidolons.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.screens.map.obj.PartyActor;
import eidolons.libgdx.screens.map.ui.*;
import eidolons.libgdx.screens.map.ui.time.MapTimePanel;
import eidolons.libgdx.shaders.VignetteShader;
import eidolons.libgdx.stage.GuiStage;
import eidolons.macro.entity.party.MacroParty;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import static main.system.MapEvent.CREATE_PARTY;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapGuiStage extends GuiStage {
    protected final String vignettePath = "ui/macro/vignette.png";
    protected final LightLayer lights;
    PartyActor mainPartyMarker;
    protected PartyInfoPanel partyInfoPanel;
    protected MapActionPanel actionPanel;
    protected boolean dirty=true;
    protected MapResourcesPanel resources;
    protected MapTimePanel timePanel;
    protected MapDatePanel datePanel;
    protected SuperContainer vignette;
    protected MapKeyHandler keyHandler = new MapKeyHandler();


    public MapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        if (isVignetteOn()) {
            addActor(  vignette =
                    VignetteShader.createVignetteActor());
        }
        addActor(lights = new LightLayer(true));
        init();

        GuiEventManager.bind(MapEvent.DATE_CHANGED, p -> {
            update();
        });
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {

//            if (!CoreEngine.isMapEditor())
//                blackout.fadeOut(1.25f);
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
        if (Flags.isMapEditor())
            return;
        hqPanel.setZIndex(Integer.MAX_VALUE);
        super.resetZIndices();
        lights.setZIndex(0);
        vignette.setZIndex(0);

    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(MapEvent.TIME_UPDATED, p -> {

//            if (!CoreEngine.isMapEditor())
//                blackout.fadeIn(1.25f);
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
        addActor(RollDecorator.decorate(actionPanel, main.game.bf.directions.FACING_DIRECTION.SOUTH));
        //background? roll out decorator

//        resources = new MapResourcesPanel();
//        addActor(resources);
//        resources.setPosition(GdxMaster.centerWidth(resources),
//         GdxMaster.top(resources));

        datePanel = new MapDatePanel();
        addActor(RollDecorator.decorate(datePanel, main.game.bf.directions.FACING_DIRECTION.WEST));

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
//        Coordinates c = mainPartyMarker.getParty().getCoordinates();
//        Vector3 pos = MapScreen.getInstance().getCamera().position;
//if (        MapScreen.getInstance().getController().isWithinCamera(c.x, c.y, 128, 128))
//{
//    mainPartyMarker.setVisible(false);
//    return;
//}
//
//        Line2D line = new Float(c.x, c.y,  pos.x,   pos.y );
//        Rectangle2D rect = new Rectangle(
//         (int) pos.x - GdxMaster.getWidth()/2,
//         (int) pos.y + GdxMaster.getHeight()/2,
//         (int) GdxMaster.getWidth() ,
//         (int)  GdxMaster.getHeight()
//        );
//        Point2D[] points = GeometryMaster.getIntersectionPoint(line, rect);
//        Point2D point = null ;
//        double dst = Double.MAX_VALUE;
//
//        for (Point2D sub : points) {
//            double dst1 = sub.distance(c.x, c.y+ GdxMaster.getHeight());
//            if (dst1<dst){
//                dst = dst1;
//                point = sub;
//            }
//        }
//
//          dst = dst-500;
//        if (dst > 100) {
//            float scale = (float) (10f/ Math.sqrt(Math.sqrt(dst)));
//            mainPartyMarker.setScale(scale);
//        }
//        mainPartyMarker.setPosition((float)point.getX(), (float)point.getY()- GdxMaster.getHeight());
//        mainPartyMarker.setZIndex(Integer.MAX_VALUE);

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
        keyHandler. handleKeyTyped(character);
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

    protected void update(SnapshotArray<Actor> children) {
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
