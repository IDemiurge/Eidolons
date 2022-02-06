package libgdx.gui.dungeon.panels.headquarters.weave.model.classes;

import eidolons.netherflame.eidolon.heromake.passives.HeroClassMaster;
import libgdx.gui.dungeon.panels.headquarters.weave.Weave;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveTree;
import libgdx.gui.dungeon.panels.headquarters.weave.model.WeaveDataNode;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.ClassEnums.CLASS_GROUP;

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
            for (CLASS_GROUP sub : ClassEnums.CLASS_GROUP.values()) {
             if (sub!= ClassEnums.CLASS_GROUP.MULTICLASS)
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
