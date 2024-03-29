package libgdx.gui.dungeon.panels.headquarters.weave.model.skills;

import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveTree;
import main.content.DC_TYPE;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveSkillTree extends WeaveTree {
    MASTERY mastery;

    public WeaveSkillTree(MASTERY mastery, boolean unbound) {
        super(unbound);
        this.mastery = mastery;
    }


    @Override
    protected Object getRootArg() {
        return mastery;
    }

    @Override
    protected boolean isSkill() {
        return true;
    }

    @Override
    protected List<ObjType> initData(HqHeroDataSource userObject) {
        return  DataManager.getTypesSubGroup(DC_TYPE.SKILLS, mastery.name());
    }
}
