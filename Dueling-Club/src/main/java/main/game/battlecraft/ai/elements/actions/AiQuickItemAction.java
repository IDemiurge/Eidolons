package main.game.battlecraft.ai.elements.actions;

import main.entity.Ref;
import main.entity.item.DC_QuickItemObj;

public class AiQuickItemAction extends Action {

    private DC_QuickItemObj item;

    public AiQuickItemAction(DC_QuickItemObj item) {
        this(item, item.getRef());

    }

    public AiQuickItemAction(DC_QuickItemObj item, Ref ref) {
        super(item.getActive(), ref);
        this.item = item;
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