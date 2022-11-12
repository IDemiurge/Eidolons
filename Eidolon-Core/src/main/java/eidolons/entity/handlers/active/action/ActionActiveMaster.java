package eidolons.entity.handlers.active.action;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.handlers.active.ActiveChecker;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.ActiveResetter;
import eidolons.entity.handlers.active.Executor;
import main.content.enums.entity.ActionEnums;
import main.content.values.properties.G_PROPS;
import main.entity.handlers.EntityChecker;
import main.entity.handlers.EntityResetter;

/**
 * Created by JustMe on 2/26/2017.
 */
public class ActionActiveMaster extends ActiveMaster {
    public ActionActiveMaster(ActiveObj entity) {
        super(entity);
    }


    @Override
    public UnitAction getEntity() {
        return (UnitAction) super.getEntity();
    }

    @Override
    protected EntityChecker<ActiveObj> createEntityChecker() {
        return new ActiveChecker(getEntity(), this);
    }

    @Override
    protected EntityResetter<ActiveObj> createResetter() {
        return new ActiveResetter(getEntity(), this) {
            @Override
            public void toBase() {
                super.toBase();
                if (getEntity().getParentAction() != null) {
                    String tag = "";
                    if (getEntity().getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                     ActionEnums.ACTION_TAGS.OFF_HAND.toString())) {
                        tag = ActionEnums.ACTION_TAGS.OFF_HAND.toString();
                    } else if (getEntity().getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                     ActionEnums.ACTION_TAGS.MAIN_HAND.toString())) {
                        tag = ActionEnums.ACTION_TAGS.MAIN_HAND.toString();
                    }
                    if (!tag.isEmpty()) {
                        getEntity().addProperty(G_PROPS.ACTION_TAGS, tag);
                    }
                }
            }
        };
    }


    @Override
    protected Executor createHandler() {
        return new ActionExecutor(getEntity(), this);
    }


}
