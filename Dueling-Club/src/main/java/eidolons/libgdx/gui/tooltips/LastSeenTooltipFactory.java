package eidolons.libgdx.gui.tooltips;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.bf.grid.BaseView;
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
    protected Supplier<List<ValueContainer>> supplier(BattleFieldObject data) {
        return ()->{
            List<ValueContainer> list=    new ArrayList<>() ;
            String time =
             String.format(Locale.US, "%.1f",
             (data.getGame().getLoop().getTime() -
             data.getLastSeenTime())) ;
            String info =  "Has been seen here " +
             time +" seconds ago";
            String name=data.getName();
            if (!data.isDetectedByPlayer()){
                OUTLINE_TYPE outline = data.getLastSeenOutline();
                if (outline!=null )
                    name = outline.getName();
            } else {
                if (data instanceof Unit) {
                    info+=", facing " + data.getLastSeenFacing();
                }
            }
            list.add(new ValueContainer(name, info));

            return list;
        };
    }
}
