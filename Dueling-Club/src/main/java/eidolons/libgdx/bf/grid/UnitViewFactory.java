package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.DialogScenario;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import eidolons.libgdx.gui.tooltips.LastSeenTooltipFactory;
import eidolons.libgdx.gui.tooltips.UnitViewTooltip;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import eidolons.test.frontend.IntroTestLauncher;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Map;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

public class UnitViewFactory {
    public static GridUnitView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        GridUnitView view = new GridUnitView(options);

        if (VisionMaster.isLastSeenOn()) {
            LastSeenView lsv = new LastSeenView(options, view);
            view.setLastSeenView(lsv);
            new LastSeenTooltipFactory().add(lsv, bfObj);
        }
        view.setOutlinePathSupplier(() -> {
            OUTLINE_TYPE type = null;
//            if (bfObj instanceof Unit) {
//                if (!VisionManager.checkVisible(bfObj)) {
//                    type = OUTLINE_TYPE.DARK_OUTLINE;
//                }
//            }
//            if (type == null)
            type = bfObj.getOutlineTypeForPlayer();

            if (type == null)
                return null;
            String path = Eidolons.game.getVisionMaster().getVisibilityMaster()
             .getImagePath(type, bfObj);

            return (path);
        });

        view.createHpBar((new ResourceSourceImpl(bfObj)));
        if (bfObj instanceof Unit) {
            view.getInitiativeQueueUnitView().getHpBar().setTeamColor(options.getTeamColor());
        }
        view.getHpBar().setTeamColor(options.getTeamColor());

        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.setToolTip(tooltip);
        ClickListener listener = createListener(bfObj);
        view.addListener(listener);
        view.getInitiativeQueueUnitView().addListener(listener);
        return view;
    }

    public static BaseView createGraveyardView(BattleFieldObject bfObj) {
        BaseView view = new BaseView(getOrCreateR(bfObj.getImagePath()), bfObj.getImagePath());
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.addListener(tooltip.getController());
        view.addListener(createListener(bfObj));
        return view;
    }

    public static ClickListener createListener(BattleFieldObject bfObj) {
        return new BattleClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;//event.getButton() == Input.Buttons.RIGHT;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (IntroTestLauncher.running) {
                    ScenarioMetaMaster m = new ScenarioMetaMaster("Pride and Treachery");
                    GameDialogue dialogue = null;//new LinearDialogue();
                    dialogue = bfObj.getGame().getMetaMaster().getDialogueFactory().getDialogue("Interrogation");
                    List<DialogScenario> list = SceneFactory.getScenes(dialogue);
                    GuiEventManager.trigger(GuiEventType.DIALOG_SHOW, list);
                }
                if (event.getButton() == Input.Buttons.RIGHT)

                {
                    GuiEventManager.trigger(CREATE_RADIAL_MENU, bfObj);
                    event.handle();
                    event.stop();
                } else {
                    if (event.getButton() == Buttons.LEFT)
                        if (isAlt() || isShift() || isControl())
                            try {
                                DefaultActionHandler.leftClickUnit(isShift(), isControl(), bfObj);
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }

                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
            }
        };
    }

    public static OverlayView createOverlay(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        OverlayView view = new OverlayView(options, bfObj);

//        view.setScale(OverlayView.SCALE, OverlayView.SCALE);

        Map<Coordinates, Map<BattleFieldObject, Coordinates.DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<BattleFieldObject, Coordinates.DIRECTION> map = directionMap.get(bfObj.getCoordinates());

        if (map != null) {
            view.setDirection(map.get(bfObj));
        }

        return view;
    }
}
