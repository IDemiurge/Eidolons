package libgdx.bf.grid.cell;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.content.PROPS;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.puzzle.gridobj.LinkedGridObject;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.cinematic.Cinematics;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import libgdx.gui.panels.headquarters.HqMaster;
import libgdx.gui.tooltips.LastSeenTooltipFactory;
import libgdx.gui.tooltips.UnitViewTooltip;
import libgdx.gui.tooltips.UnitViewTooltipFactory;
import libgdx.screens.ScreenMaster;
import libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.Flags;

import java.util.Map;
import java.util.function.Function;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.RADIAL_MENU_CLOSE;


public class UnitViewFactory {
    private static UnitViewFactory instance;

    public static UnitViewFactory getInstance() {
        if (instance == null) {
            instance = new UnitViewFactory();
        }
        return instance;
    }

    public static UnitGridView doCreate(BattleFieldObject battleFieldObject) {
        return getInstance().create(battleFieldObject);
    }

    public static OverlayView doCreateOverlay(BattleFieldObject bfObj, Function<Coordinates, Color> colorFunction) {
        return getInstance().createOverlay(bfObj, colorFunction);
    }

    public UnitGridView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        UnitGridView view = createView(bfObj, options);

        addLastSeenView(bfObj, view, options);

        addOutline(bfObj, view, options);

        addForDC(bfObj, view, options);

        ClickListener listener = createListener(bfObj);
        view.addListener(listener);
        if (view.getInitiativeQueueUnitView() != null) {
            view.getInitiativeQueueUnitView().addListener(listener);
        }
        if (isGridObjRequired(bfObj)) {
            CUSTOM_OBJECT x = new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, bfObj.getProperty(PROPS.CUSTOM_OBJECT));
            LinkedGridObject obj = new LinkedGridObject(view,
                    x,
                    bfObj.getCoordinates());
            if (x.attach || bfObj instanceof Unit) {
                view.addActor(obj);
                obj.setZIndex(0);
            } else
                GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, obj);
        }

        return view;
    }

    protected UnitGridView createView(BattleFieldObject bfObj, UnitViewOptions options) {
        return new UnitGridView(bfObj, options);
    }

    protected void addLastSeenView(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {
        if (bfObj instanceof Unit || bfObj.isLandscape() || bfObj.isWall() || bfObj.isWater())
            if (VisionMaster.isLastSeenOn()) {
                if (!bfObj.isPlayerCharacter())
                    if (!bfObj.isBoss())
                    //                        if (!bfObj.isWall())
                    {
                        LastSeenView lsv = new LastSeenView(options, view);
                        view.setLastSeenView(lsv);
                        new LastSeenTooltipFactory().add(lsv, bfObj);
                    }
            }
    }

    protected void addOutline(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {

        view.setOutlinePathSupplier(() -> {
            if (Flags.isFootageMode()) {
                return null;
            }
            if (Cinematics.ON) {
                return null;
            }
            if (bfObj.isWater()) {
                return null;
            }
            if (!Flags.isOutlinesFixed()) {
                return null;
            }
            if (bfObj.isBoss()) {
                return null;
            }
            if (bfObj.isDetectedByPlayer()) {
                return null;
            }
            OUTLINE_TYPE type = bfObj.getOutlineTypeForPlayer();
            if (type == null)
                return null;

            return (Core.game.getVisionMaster().getVisibilityMaster()
                    .getImagePath(type, bfObj));
        });
    }

    protected void addForDC(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {
        view.createHpBar();
        if (bfObj instanceof Unit) {
            view.getInitiativeQueueUnitView().getHpBar().setTeamColor(options.getTeamColor());
        }
        view.getHpBar().setTeamColor(options.getTeamColor());

        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj, view));
        view.setToolTip(tooltip);
        if (bfObj.checkBool(GenericEnums.STD_BOOLS.INVISIBLE)) {
            view.setInvisible(true);
        }
    }

    protected boolean isGridObjRequired(BattleFieldObject bfObj) {
        return bfObj.checkProperty(PROPS.CUSTOM_OBJECT);
    }

    public static BaseView createGraveyardView(BattleFieldObject bfObj) {
        BaseView view = new BaseView(TextureCache.getRegionUV(bfObj.getImagePath()), bfObj.getImagePath());
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj, view));
        view.addListener(tooltip.getController());
        view.addListener(doCreateListener(bfObj));
        view.setUserObject(bfObj);
        return view;
    }

    public static ClickListener doCreateListener(BattleFieldObject bfObj) {
        return getInstance().createListener(bfObj);
    }

    public ClickListener createListener(BattleFieldObject bfObj) {
        return new BattleClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1)
                    return true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                int btn = event.getButton();
                Core.onNonGdxThread(() ->
                        tryDefaultAction(btn, event));
                super.clicked(event, x, y);
            }

            @Override
            public boolean handle(Event e) {
                if (ScreenMaster.getScreen().getGuiStage().isBlocked()) {
                    return true;
                }

                return super.handle(e);
            }

            private void tryDefaultAction(int button, InputEvent event) {
                try {
                    if (getTapCount() > 1)
                        if (button == 0)
                            if (bfObj.isPlayerCharacter() && !UnitInfoPanelNew.isNewUnitInfoPanelWIP())
                                if (EidolonsGame.isHqEnabled()) {
                                    HqMaster.openHqPanel();
                                    event.stop();
                                    return;
                                } else {
                                    if (UnitInfoPanelNew.isNewUnitInfoPanelWIP())
                                        if (bfObj instanceof Unit) {
                                            GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL,
                                                    bfObj);
                                            return;
                                        }
                                    DefaultActionHandler.leftClickUnit(isShift(), isControl(), bfObj);
                                    event.cancel();
                                    return;

                                }
                    //TODO control options
                    if (button == Buttons.LEFT) {
                        if (!DefaultActionHandler.leftClickInteractiveObj(bfObj)) {
                            DefaultActionHandler.leftClickUnit(isShift(), isControl(), bfObj);
                        }
                        event.cancel();
                    }
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }
            //

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {


                if (event.getButton() == Buttons.RIGHT) {
                    GuiEventManager.trigger(CREATE_RADIAL_MENU, bfObj);
                    event.handle();
                    event.stop();
                } else {


                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
                super.touchUp(event, x, y, pointer, button);
            }

        }

                ;
    }

    public void addOverlayingListener(OverlayView view, BattleFieldObject bfObj) {
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj, view));
        view.addListener(tooltip.getController());
        view.addListener(UnitViewFactory.doCreateListener(bfObj));
    }

    public OverlayView createOverlay(BattleFieldObject bfObj, Function<Coordinates, Color> colorFunction) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        OverlayView view = new OverlayView(options, colorFunction);
        addOverlayingListener(view, bfObj);

        Map<Coordinates, Map<BattleFieldObject, DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<BattleFieldObject, DIRECTION> map = directionMap.get(bfObj.getCoordinates());

        if (map != null) {
            view.setDirection(map.get(bfObj));
        } else {
            view.setDirection((bfObj).getDirection());
        }

        view.setUserObject(bfObj);
        if (isGridObjRequired(bfObj)) {
            CUSTOM_OBJECT x = new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, bfObj.getProperty(PROPS.CUSTOM_OBJECT));
            LinkedGridObject obj = new LinkedGridObject(view,
                    x, bfObj.getCoordinates());
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, obj);
        }
        return view;
    }


}
