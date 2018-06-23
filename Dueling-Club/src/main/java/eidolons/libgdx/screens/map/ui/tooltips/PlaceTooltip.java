package eidolons.libgdx.screens.map.ui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.macro.MacroGame;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.screens.map.MapScreen;
import eidolons.libgdx.screens.map.editor.EditorManager;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.screens.map.obj.PlaceActor;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import javax.swing.*;

/**
 * Created by JustMe on 2/23/2018.
 */
public class PlaceTooltip extends Tooltip {
    Place place;
    PlaceActor actor;

    public PlaceTooltip(Place place, PlaceActor actor) {
        this.place = place;
        this.actor = actor;

        GuiEventManager.bind(MapEvent.ROUTES_PANEL_HOVER_OFF, r -> {
            super.onMouseExit(null, 0, 0, 0, null);
            GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
            main.system.auxiliary.log.LogMaster.log(1, "ROUTES_PANEL_HOVER_OFF");
        });
    }

    @Override
    protected void onMouseMoved(InputEvent event, float x, float y) {
//        super.onMouseMoved(event, x, y);
    }


    @Override
    protected void onTouchUp(InputEvent event, float x, float y) {
        SwingUtilities.invokeLater(() -> {
            main.system.auxiliary.log.LogMaster.log(1, "Waiting!!! ");
            WaitMaster.WAIT(500);
            if (!MapScreen.getInstance().getMapStage().getRoutes().isRouteHighlighted()) {
                GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
                super.onMouseExit(event, 0, 0, 0, null);
                main.system.auxiliary.log.LogMaster.log(1, "REMOVED!!! ");
            } else {
                main.system.auxiliary.log.LogMaster.log(1, "Nothing!!! ");
            }
        });
        super.onTouchUp(event, x, y);
    }

    @Override
    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        actor.minimize();
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        actor.hover();
//        super.onMouseEnter(event, x, y, pointer, fromActor);
    }

    @Override
    protected void onDoubleClick(InputEvent event, float x, float y) {
        if (CoreEngine.isMapEditor()) {
            if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
                EditorManager.remove(actor);
            }
        } else {
            MacroGame.getGame().getLoop().tryEnter(place);
        }

    }

    @Override
    protected void onTouchDown(InputEvent event, float x, float y) {
        if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            onDoubleClick(event, x, y);
            return;
        }
        GuiEventManager.trigger(MapEvent.PLACE_HOVER, place);
        setUpdateRequired(true);
        super.onMouseEnter(event, x, y, 1, null);
        super.onTouchDown(event, x, y);
    }

    @Override
    public boolean isTouchable() {
        return true;
    }

    @Override
    public void updateAct(float delta) {
        clearChildren();

        TextureRegion r = TextureCache.getOrCreateR(place.getImagePath());
        ValueContainer container = new ValueContainer(r, place.getName());
        float size = GdxMaster.adjustSize(128);
        if (size < r.getRegionHeight() && size < r.getRegionWidth())
            container.overrideImageSize(size, size);
        add(container);
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
//        if (!displayRoutes)
//            return ;
        if (place.getRoutes().isEmpty()) {
            return;
        }
        row();

        TablePanel<ValueContainer> routesInfo = new TablePanel<>();
        routesInfo.defaults().space(5);
        add(routesInfo);
        routesInfo.addListener(new ClickListener() {


                                   @Override
                                   public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                                       if (toActor == routesInfo)
                                           return;
                                       if (toActor == null) {
                                           if (getWidth() >= x)
                                               return;
                                           if (getWidth() >= y)
                                               return;
                                       }

                                       if (GdxMaster.getAncestors(toActor).contains(routesInfo))
                                           return;
                                       if (!checkActorExitRemoves(toActor))
                                           return;
                                       super.exit(event, x, y, pointer, toActor);
                                       GuiEventManager.trigger(MapEvent.ROUTES_PANEL_HOVER_OFF);
                                   }
                               }

        );

        int i = 0;
        for (Route sub : place.getRoutes()) {
            //reverse pic pos
            TextureRegion tex = TextureCache.getOrCreateR(sub.getImagePath());
            ValueContainer routeInfo = new ValueContainer(tex, sub.getName(),
             sub.getLength() + " leagues");
            routeInfo.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
            routesInfo.add(routeInfo).left().padLeft(5).uniform(true, false);
            routeInfo.setUserObject(sub);
            routeInfo.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                     getTapCount()
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, sub);
                    return super.touchDown(event, x, y, pointer, button);

                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, null);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, sub);
                }
            });
            if (i % 2 == 1)
                routesInfo.row();
            i++;
        }
    }

    @Override
    protected boolean checkActorExitRemoves(Actor toActor) {
        if (toActor == null)
            return true;
        if (toActor instanceof LightLayer)
            return false;
        if (MapScreen.getInstance().getGuiStage().getVignette().getContent().equals(toActor))
            return false;
        if (MapScreen.getInstance().getGuiStage().getBlackout().equals(toActor.getParent())) {
            return false;
        }
        return super.checkActorExitRemoves(toActor);
    }
}
