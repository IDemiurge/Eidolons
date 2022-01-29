package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.entity.item.QuickItem;
import main.entity.Ref;

public class AiQuickItemAction extends Action {

    public AiQuickItemAction(QuickItem item) {
        this(item, item.getRef());

    }

    public AiQuickItemAction(QuickItem item, Ref ref) {
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
