package eidolons.libgdx.gui.panels.headquarters.weave.skills;

import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import main.content.DC_TYPE;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.data.DataManager;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveSkillTree extends WeaveTree {
    MASTERY mastery;

    public WeaveSkillTree(MASTERY mastery) {
        this.mastery = mastery;
    }

    public void init(){
        DataManager.getTypesSubGroup(DC_TYPE.SKILLS, mastery.name());



    }
}
