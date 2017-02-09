package main.client.cc.gui.views;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.images.ImageManager.BORDER;

public class SkillPickView extends HeroItemView {

    public SkillPickView(DC_HeroObj hero) {
        super(hero, true, true);
        // init();
    }

    @Override
    public void activate() {

    }

    protected PARAMETER getSortingParam() {
        return PARAMS.XP_COST;
    }

    @Override
    public PARAMS getPoolParam() {
        return PARAMS.XP;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.SKILLS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.SKILLS;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        BORDER b = WorkspaceMaster.getBorderForType(value);
        if (b != null) {
            return b;
        }

        if (StringMaster.checkContainer(hero.getProperty(getPROP()),
                value.getName(), true)) {
            return BORDER.SILVER_64;
        }
        String r = hero.getGame().getRequirementsManager().check(hero, value);
        if (r == null) {
            return null;
        }

        return BORDER.HIDDEN;
    }

}
