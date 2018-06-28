package eidolons.libgdx.gui.panels.headquarters.weave.model.skills;

import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import main.content.enums.entity.SkillEnums.MASTERY;

import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class SkillWeave extends Weave<MASTERY> {

    public SkillWeave(WeaveDataNode coreNode) {
        super(coreNode);
    }

    @Override
    protected List<MASTERY> getData() {
        return SkillMaster.getUnlockedMasteries_(getUserObject().getEntity());
    }

    @Override
    protected WeaveTree createTree(MASTERY sub) {
        return new WeaveSkillTree(sub);
    }

}
