package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created by JustMe on 4/5/2018.
 */
public class LastSeenTooltipFactory extends TooltipFactory<BattleFieldObject, BaseView>{
    @Override
    protected Tooltip createTooltip(BaseView actor) {
        return new LastSeenTooltip(actor);
    }

    @Override
    protected Supplier<List<Actor>> supplier(BattleFieldObject object, BaseView view) {
        return ()->{
            List<Actor> list=    new ArrayList<>() ;
            String time =
             String.format(Locale.US, "%.0f",
             (object.getGame().getLoop().getTime() -
             object.getLastSeenTime())) ;
            String name= object.getName();
            String info =  "You have seen " +
             name +
             " here " +time +
             "" +
             "seconds ago";
            if (!object.isDetectedByPlayer()){
                OUTLINE_TYPE outline = object.getLastSeenOutline();
                if (outline!=null )
                    name = outline.getName();
            } else {
                if (object instanceof Unit) {
                    info+=", facing " + object.getLastSeenFacing();
                }
            }
            list.add(new ValueContainer("You  recall...", info));

            return list;
        };
    }
}
