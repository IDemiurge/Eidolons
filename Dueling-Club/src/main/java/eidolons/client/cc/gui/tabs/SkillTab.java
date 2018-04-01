package eidolons.client.cc.gui.tabs;

import eidolons.client.cc.gui.MainViewPanel;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.system.images.ImageManager.BORDER;

public class SkillTab extends HeroItemTab {

    public SkillTab(MainViewPanel mvp, Unit hero) {
        super("Skills", mvp, hero);
    }

    // protected void initData() {
    // data = new ArrayList<>();
    // for (DC_SkillObj skill : hero.getSkills()) {
    // data.add(skill.getType());
    // }
    // }

    @Override
    protected HERO_VIEWS getVIEW() {
        return HERO_VIEWS.SKILLS;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.SKILLS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.SKILLS;
    }

    @Override
    protected PROPERTY getPROP2() {
        return null;
    }
}
