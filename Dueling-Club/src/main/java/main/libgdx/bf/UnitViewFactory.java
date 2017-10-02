package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.active.DefaultActionHandler;
import main.entity.obj.BattleFieldObject;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import main.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.DialogScenario;
import main.libgdx.bf.mouse.BattleClickListener;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.test.frontend.IntroTestLauncher;

import java.util.List;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.CREATE_RADIAL_MENU;

public class UnitViewFactory {
    public static BaseView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        GridUnitView view = new GridUnitView(options);
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.setToolTip(tooltip);
        view.addListener(createListener(bfObj));
        return view;
    }

    public static BaseView createGraveyardView(BattleFieldObject bfObj) {
        BaseView view = new BaseView(getOrCreateR(bfObj.getImagePath()));
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
                        if (isAlt())
                            try {
                                DefaultActionHandler.leftClickUnit(bfObj);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                }
            }
        };
    }

    public static OverlayView createOverlay(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        OverlayView view = new OverlayView(options, bfObj);
        view.setScale(OverlayView.SCALE, OverlayView.SCALE);

        Map<Coordinates, Map<BattleFieldObject, Coordinates.DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<BattleFieldObject, Coordinates.DIRECTION> map = directionMap.get(bfObj.getCoordinates());

        if (map != null) {
            view.setDirection(map.get(bfObj));
        }

        return view;
    }
}
