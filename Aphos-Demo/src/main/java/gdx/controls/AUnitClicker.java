package gdx.controls;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.combat.CombatController;

public class AUnitClicker extends ClickListener {

    private final Unit unit;

    public AUnitClicker(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (event.getButton()==1) {
            System.out.println("Attack on " + unit);
           GameController.getInstance().getCombatController().attack(unit);
        } else {
            // radial? info? other atk?
        }

        super.clicked(event, x, y);
    }
}
