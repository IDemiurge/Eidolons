package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;
import main.system.GuiEventManager;

import java.util.Map;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;

public class UnitViewFactory {
    public static BaseView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        GridUnitView view = new GridUnitView(options);
        final UnitViewTooltip tooltip = new UnitViewTooltip();
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        view.setToolTip(tooltip);
        view.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return event.getButton() == Input.Buttons.RIGHT;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getButton() == Input.Buttons.RIGHT) {
                    GuiEventManager.trigger(CREATE_RADIAL_MENU, bfObj);
                    event.handle();
                    event.stop();
                }
            }
        });
        return view;
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
