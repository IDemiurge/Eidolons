package eidolons.libgdx.gui.panels.headquarters.weave;

import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveModelBuilder;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/4/2018.
 */
public abstract class WeaveTree extends HqElement {

    WeaveDataNode root;


    public WeaveTree init() {
        List<ObjType> data = initData(getUserObject());
        root = build(data);
        return this;
    }

    private WeaveDataNode build(List<ObjType> data) {
        return WeaveModelBuilder.buildTreeModel(getRootArg(), data, isSkill());
    }

    protected abstract Object getRootArg();


    protected abstract boolean isSkill();

    abstract protected List<ObjType> initData(HqHeroDataSource userObject);

    @Override
    protected void update(float delta) {
        //            dataSource
    }

    public WeaveDataNode getRoot() {
        return root;
    }
}
