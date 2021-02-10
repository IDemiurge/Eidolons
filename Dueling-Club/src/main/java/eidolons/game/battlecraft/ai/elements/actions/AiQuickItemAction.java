package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.item.DC_QuickItemObj;
import main.entity.Ref;

public class AiQuickItemAction extends Action {

    public AiQuickItemAction(DC_QuickItemObj item) {
        this(item, item.getRef());

    }

    public AiQuickItemAction(DC_QuickItemObj item, Ref ref) {
        super(item.getActive(), ref);
    }

//    @Override
//    public boolean activate() {
//        if (ref.getTargetObj() != null) {
//            active.setForcePresetTarget(true);
//        }
//
//        getActive().setRef(ref);
//        boolean result = item.activate();
//
//        return result;
//    }

}
