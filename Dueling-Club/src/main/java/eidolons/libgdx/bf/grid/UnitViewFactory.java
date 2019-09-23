package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.content.PROPS;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.LinkedGridObject;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.boss.sprite.BossView;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.tooltips.LastSeenTooltipFactory;
import eidolons.libgdx.gui.tooltips.UnitViewTooltip;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;

import java.util.Map;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

public class UnitViewFactory {
    public static GridUnitView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        GridUnitView view =
                bfObj.isBoss() ? new BossView(options) :
                        new GridUnitView(bfObj, options);

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
        view.setOutlinePathSupplier(() -> {
            if (CoreEngine.isFootageMode()) {
                return null;
            }
            if (Cinematics.ON) {
                return null;
            }
            if (bfObj.isWater()) {
                return null;
            }
            if (EidolonsGame.BRIDGE) {
                return null;
            }
            if (!CoreEngine.isOutlinesFixed()) {
                return null;
            }
            if (bfObj.isBoss()) {
                return null;
            }
            if (bfObj == ShadowMaster.getShadowUnit()) {
                return null;
            }
            if (bfObj.isDetectedByPlayer()) {
                return null;
            }
            OUTLINE_TYPE type = bfObj.getOutlineTypeForPlayer();
            if (type == null)
                return null;
            String path = Eidolons.game.getVisionMaster().getVisibilityMaster()
                    .getImagePath(type, bfObj);

            return (path);
        });
        view.createHpBar();
        if (bfObj instanceof Unit) {
            view.getInitiativeQueueUnitView().getHpBar().setTeamColor(options.getTeamColor());
        }
        view.getHpBar().setTeamColor(options.getTeamColor());

        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj));
        view.setToolTip(tooltip);
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

        if (bfObj.checkBool(GenericEnums.STD_BOOLS.INVISIBLE)) {
            view.setInvisible(true);
        }
        return view;
    }

    private static boolean isGridObjRequired(BattleFieldObject bfObj) {
        return bfObj.checkProperty(PROPS.CUSTOM_OBJECT);
    }

    public static BaseView createGraveyardView(BattleFieldObject bfObj) {
        BaseView view = new BaseView(getOrCreateR(bfObj.getImagePath()), bfObj.getImagePath());
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj));
        view.addListener(tooltip.getController());
        view.addListener(createListener(bfObj));
        view.setUserObject(bfObj);
        return view;
    }

    public static ClickListener createListener(BattleFieldObject bfObj) {
        return new BattleClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1)
                    return true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                tryDefaultAction(event);

                super.clicked(event, x, y);
            }

            @Override
            public boolean handle(Event e) {
                if (Eidolons.getScreen().getGuiStage().isBlocked()) {
                    return true;
                }

                return super.handle(e);
            }

            private void tryDefaultAction(InputEvent event) {
                try {
                    if (getTapCount() > 1)
                        if (event.getButton() == 0)
                            if (bfObj.isPlayerCharacter() && !UnitInfoPanelNew.isNewUnitInfoPanelWIP())
                                if (EidolonsGame.isHqEnabled()) {
                                    HqMaster.openHqPanel();
                                    event.stop();
                                    return;
                                } else {
                                    if (UnitInfoPanelNew.isNewUnitInfoPanelWIP())
                                        if (bfObj instanceof Unit) {
                                            GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL,
                                                    ((Unit) bfObj));
                                            return;
                                        }
                                    DefaultActionHandler.leftClickUnit(isShift(), isControl(), bfObj);
                                    event.cancel();
                                    return;

                                }
                    //TODO control options

                    if (event.getButton() == Buttons.LEFT) {
                        if (isAlt() || isShift() || isControl()) {
                            DefaultActionHandler.leftClickUnit(isShift(), isControl(), bfObj);
                            event.cancel();
                        } else {
                            if (DefaultActionHandler.leftClickActor(bfObj))
                                return;
                        }
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            //

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {


                if (event.getButton() == Input.Buttons.RIGHT) {
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

    public static OverlayView createOverlay(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        OverlayView view = new OverlayView(options, bfObj);


        Map<Coordinates, Map<BattleFieldObject, DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<BattleFieldObject, DIRECTION> map = directionMap.get(bfObj.getCoordinates());

        if (map != null) {
            view.setDirection(map.get(bfObj));
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
