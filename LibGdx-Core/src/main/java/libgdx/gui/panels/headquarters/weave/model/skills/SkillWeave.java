package libgdx.gui.panels.headquarters.weave.model.skills;

import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import libgdx.gui.panels.headquarters.weave.Weave;
import libgdx.gui.panels.headquarters.weave.WeaveTree;
import libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class SkillWeave extends Weave<MASTERY> {


    public SkillWeave(WeaveDataNode root, boolean unbound) {
        super(root, unbound);
    }

    @Override
    protected List<MASTERY> getData(boolean unbound) {

        if (unbound) {
            SKILL_GROUP group = (SKILL_GROUP) getCoreNode().getArg();
            return
             new ArrayList<>(Arrays.asList(SkillMaster.getMasteriesFromSkillGroup(group)));
        }


        return SkillMaster.getUnlockedMasteries_(getUserObject().getEntity());

    }


    @Override
    protected WeaveTree createTree(MASTERY sub) {
        return new WeaveSkillTree(sub, unbound);
    }

}
