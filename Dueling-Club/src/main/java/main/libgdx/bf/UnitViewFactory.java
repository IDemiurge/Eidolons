package main.libgdx.bf;

import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;

import java.util.Map;

public class UnitViewFactory {
    public static BaseView create(BattleFieldObject bfObj) {
        UnitViewOptions options = new UnitViewOptions(bfObj);
        BaseView baseView = new GridUnitView(options);
        return baseView;
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
