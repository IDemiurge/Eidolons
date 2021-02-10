package libgdx.bf.overlays.bar;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.BattleFieldObject;
import main.system.EventType;
import main.system.GuiEventManager;

public class TimerBar extends ValueBar {
    /*
    - show numbers?
    - must be flashy?
    - team-colored? but HP bar already is!...
    - on top of units, sure.

    - could be useful for TRAPS or some other TIMED events...

     */
    float value;

    public TimerBar(BattleFieldObject dataSource) {
    }


    public void init(){
        GuiEventManager.bind(getSetValueEvent(), p->{
            value = (float) p.get();
        } );
    }

    private EventType getSetValueEvent() {
        return null;
    }

    @Override
    protected String getBarBgPath() {
        return null;
    }

    @Override
    protected void setValues() {

    }

    @Override
    protected void resetLabel() {

    }

    @Override
    protected Color getColor(boolean over) {
        return null;
    }
}
