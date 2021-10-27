package libgdx.gui.dungeon.panels.headquarters.weave.model.classes;

import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveTree;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveClassTree extends WeaveTree{
    private final CLASS_GROUP group;

    public WeaveClassTree(CLASS_GROUP sub, boolean unbound) {
      super(unbound);
        group= sub;
    }


    @Override
    protected Object getRootArg() {
        return group;
    }
    @Override
    protected boolean isSkill() {
        return false;
    }
    @Override
    protected List<ObjType> initData(HqHeroDataSource userObject) {
        return  DataManager.getTypesSubGroup(DC_TYPE.CLASSES, group.name());
    }
}
