package main.client.cc.gui.tabs;

import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.obj.DC_FeatObj;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.util.LinkedList;

public class ClassTab extends HeroItemTab {

    public ClassTab(MainViewPanel mvp, DC_HeroObj hero) {
        super("Classes", mvp, hero);
    }

    @Override
    protected HERO_VIEWS getVIEW() {
        return HERO_VIEWS.CLASSES;
    }

    protected void initData() {
        data = new LinkedList<>();
        String property = hero.getProperty(getPROP());
        for (String skill : StringMaster.openContainer(property)) {
            skill = VariableManager.removeVarPart(skill);
            data.add(DataManager.getType(skill, getTYPE()));
        }
    }

    @Override
    public BORDER getBorder(ObjType value) {
        for (DC_FeatObj clazz : hero.getClasses()) {
            if (!clazz.getName().equals(value.getName())) {
                continue;
            }
            Integer rank = clazz.getIntParam(PARAMS.RANK);
            if (rank > 0) {
                switch (rank) {
                    case 1:
                        return BORDER.RANK_II;
                    case 2:
                        return BORDER.RANK_III;
                    case 3:
                        return BORDER.RANK_IV;
                    case 4:
                        return BORDER.RANK_V;
                }
            }
        }
        return null;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.CLASSES;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.CLASSES;
    }

    @Override
    protected PROPERTY getPROP2() {
        return null;
    }

}
