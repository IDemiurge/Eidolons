package eidolons.libgdx.gui.panels.headquarters.weave.model.classes;

import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveTree;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class ClassWeave extends Weave<CLASS_GROUP> {
    public ClassWeave(WeaveDataNode root, boolean unbound) {
        super(root, unbound);
    }
    @Override
    protected List<CLASS_GROUP> getData(boolean unbound) {
        if (unbound)
        {
            List<CLASS_GROUP> list=    new ArrayList<>() ;
            for (CLASS_GROUP sub : CLASS_GROUP.values()) {
             if (sub!=CLASS_GROUP.MULTICLASS)
                 list.add(sub);
            }
            list.add((CLASS_GROUP) coreNode.getArg());
            return list;
        }

        return HeroClassMaster.getClassGroups(getUserObject().getEntity());
    }

    @Override
    protected WeaveTree createTree(CLASS_GROUP sub) {
        return new WeaveClassTree(sub, unbound);
    }
}
