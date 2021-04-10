package libgdx.gui.panels.headquarters.weave;

import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/3/2018.
 *
 * Explore the entirety of game skill/class content
 *
 * radial-based?
 *
 * like a Web perhaps
 *
 * Skill Weave
 *
 * yes, with a style of the Weave itself...
 *
 * Animation
 *
 * Aesthetics
 * diff node styles
 *
 *
 * Lighting! and shading
 *
 * animated background
 *
 * building the Space
 *
 * separate stage? or how to add camera?
 *
 *
 *
 */
public abstract class Weave<T> extends HqElement{

    protected WeaveDataNode coreNode;
    protected List<WeaveTree> trees;
    protected boolean unbound;

    public Weave(WeaveDataNode coreNode, boolean unbound) {
        this.coreNode = coreNode;
        this.unbound = unbound;
    }

    abstract protected List<T> getData(boolean unbound);

    public void init(){
        trees = new ArrayList<>();
        List<T> data = getData(unbound);
        for (T sub : data) {
            trees.add(createTree(sub).init());
        }
        WeaveAmbience ambience;
        if (isAmbienceOn())
            addActor(ambience =new WeaveAmbience(this));
    }

    private boolean isAmbienceOn() {
        return false;
    }

    protected abstract WeaveTree createTree(T sub);

    @Override
    protected void update(float delta) {

    }

    public WeaveDataNode getCoreNode() {
        return coreNode;
    }

    public List<WeaveTree> getTrees() {
        return trees;
    }
}
