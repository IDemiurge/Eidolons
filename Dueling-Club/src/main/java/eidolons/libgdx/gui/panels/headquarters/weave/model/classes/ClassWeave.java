package eidolons.libgdx.gui.panels.headquarters.weave.model.classes;

import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.system.auxiliary.ClassMaster;

import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class ClassWeave extends Weave<CLASS_GROUP> {
    public ClassWeave(WeaveDataNode coreNode) {
        super(coreNode);
    }

    @Override
    protected List<CLASS_GROUP> getData() {
        return HeroClassMaster.getClassGroups(getUserObject().getEntity());
    }

    @Override
    protected WeaveTree createTree(CLASS_GROUP sub) {
        return new WeaveClassTree(sub);
    }
}
