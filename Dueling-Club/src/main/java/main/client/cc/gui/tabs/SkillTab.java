package main.client.cc.gui.tabs;

import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.images.ImageManager.BORDER;

public class SkillTab extends HeroItemTab {

    public SkillTab(MainViewPanel mvp, DC_HeroObj hero) {
        super("Skills", mvp, hero);
    }

    // protected void initData() {
    // data = new LinkedList<>();
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
        return OBJ_TYPES.SKILLS;
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
