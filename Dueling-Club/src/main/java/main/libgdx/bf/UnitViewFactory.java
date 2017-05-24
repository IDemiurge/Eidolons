package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import main.game.battlecraft.logic.meta.scenario.dialogue.LinearDialogue;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.DialogScenario;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewFactory {
    public static BaseView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        GridUnitView view = new GridUnitView(options);
        final UnitViewTooltip tooltip = new UnitViewTooltip();
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.setToolTip(tooltip);
        view.addListener(createListener(bfObj));
        return view;
    }

    public static BaseView createBaseView(BattleFieldObject bfObj) {
        BaseView view = new BaseView(getOrCreateR(bfObj.getImagePath()));
        final UnitViewTooltip tooltip = new UnitViewTooltip();
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.addListener(tooltip.getController());
        view.addListener(createListener(bfObj));
        return view;
    }

    private static InputListener createListener(BattleFieldObject bfObj) {
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return event.getButton() == Input.Buttons.RIGHT;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                if (event.getButton() == Input.Buttons.RIGHT) {
//                    GuiEventManager.trigger(CREATE_RADIAL_MENU, bfObj);
//                    event.handle();
//                    event.stop();
//                }
                ScenarioMetaMaster m = new ScenarioMetaMaster("Pride and Treachery");
                LinearDialogue dialogue = null ;//new LinearDialogue();
                DialogueFactory.constructScenarioLinearDialogues
                 (DialogueLineFormatter. getLinearDialoguesFilePath(), m );
                dialogue = DialogueFactory.getLinearDialogue("Interrogation");
                List<DialogScenario> list = SceneFactory.getScenes(dialogue);
                GuiEventManager.trigger(GuiEventType.DIALOG_SHOW, list);
            }
        };
    }


    public static OverlayView createOverlay(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        OverlayView view = new OverlayView(options);
        view.setScale(OverlayView.SCALE, OverlayView.SCALE);

        Map<Coordinates, Map<Unit, Coordinates.DIRECTION>> directionMap = DC_Game.game.getDirectionMap();
        Map<Unit, Coordinates.DIRECTION> heroObjDIRECTIONMap = directionMap.get(bfObj.getCoordinates());

        if (heroObjDIRECTIONMap != null) {
            view.setDirection(heroObjDIRECTIONMap.get(bfObj));
        }

        return view;
    }
}
