package gdx.controls;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import logic.core.Aphos;
import logic.entity.Unit;

public class AUnitClicker extends ClickListener {

    private final Unit unit;

    public AUnitClicker(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (event.getButton() == 0) {
            System.out.println("Attack on " + unit);
          Aphos.game.getController().getCombatLogic().attack(unit);
        } else {
        }

        super.clicked(event, x, y);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (event.getButton() == 0) {
        } else {
            System.out.println("Info on " + unit);
            for (String s : unit.getValueMap().keySet()) {
                System.out.println(s + " : " + unit.getValueMap().get(s));
            }
        }
        return super.touchDown(event, x, y, pointer, button);
    }
}
