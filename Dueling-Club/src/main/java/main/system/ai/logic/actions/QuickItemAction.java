package main.system.ai.logic.actions;

import main.entity.Ref;
import main.entity.obj.DC_QuickItemObj;

public class QuickItemAction extends Action {

    private DC_QuickItemObj item;

    public QuickItemAction(DC_QuickItemObj item) {
        this(item, item.getRef());

    }

    public QuickItemAction(DC_QuickItemObj item, Ref ref) {
        super(item.getActive(), ref);
        this.item = item;

    }

    @Override
    public boolean activate() {
        if (ref.getTargetObj() != null)
            active.setForcePresetTarget(true);

        getActive().setRef(ref);
        boolean result = item.activate();

        return result;
    }

}
