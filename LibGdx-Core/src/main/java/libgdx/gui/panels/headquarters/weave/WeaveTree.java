package libgdx.gui.panels.headquarters.weave;

import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import libgdx.gui.panels.headquarters.weave.model.WeaveModelBuilder;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/4/2018.
 */
public abstract class WeaveTree extends HqElement {

    WeaveDataNode root;
    boolean unbound;

    public WeaveTree() {
        this(false);
    }
    public WeaveTree(boolean unbound) {
        this.unbound = unbound;
    }

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
