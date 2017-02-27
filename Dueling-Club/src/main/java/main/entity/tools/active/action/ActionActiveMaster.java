package main.entity.tools.active.action;

import main.content.enums.entity.ActionEnums;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.tools.EntityChecker;
import main.entity.tools.EntityResetter;
import main.entity.tools.active.ActiveChecker;
import main.entity.tools.active.ActiveMaster;
import main.entity.tools.active.ActiveResetter;
import main.entity.tools.active.Executor;

/**
 * Created by JustMe on 2/26/2017.
 */
public class ActionActiveMaster extends ActiveMaster {
    public ActionActiveMaster(DC_ActiveObj entity) {
        super(entity);
    }


    @Override
    public DC_UnitAction getEntity() {
        return (DC_UnitAction) super.getEntity();
    }
    @Override
    protected EntityChecker<DC_ActiveObj> createEntityChecker() {
        return new ActiveChecker(getEntity(), this){

        };
    }

    @Override
    protected EntityResetter<DC_ActiveObj> createResetter() {
        return new ActiveResetter(getEntity(), this){
            @Override
            public void toBase() {
                super.toBase();
                if (getEntity(). getParentAction() != null) {
                    String tag = "";
                    if (getEntity().getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                     ActionEnums.ACTION_TAGS.OFF_HAND.toString())) {
                        tag = ActionEnums.ACTION_TAGS.OFF_HAND.toString();
                    } else if (getEntity().getParentAction().checkProperty(G_PROPS.ACTION_TAGS,
                     ActionEnums.ACTION_TAGS.MAIN_HAND.toString())) {
                        tag = ActionEnums.ACTION_TAGS.MAIN_HAND.toString();
                    }
                    if (!tag.isEmpty())
                        getEntity().addProperty(G_PROPS.ACTION_TAGS, tag);
                }
            }
        };
    }



    @Override
    protected Executor createHandler() {
        return new ActionExecutor(getEntity(), this);
    }


}
