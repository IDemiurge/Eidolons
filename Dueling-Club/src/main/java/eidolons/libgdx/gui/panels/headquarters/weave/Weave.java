package eidolons.libgdx.gui.panels.headquarters.weave;

import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;

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

    WeaveDataNode coreNode;
    List<WeaveTree> trees;

    public Weave(WeaveDataNode coreNode) {
        this.coreNode = coreNode;
    }

    abstract protected List<T> getData();

    public void init(){
        trees = new ArrayList<>();
        List<T> data = getData();
        for (T sub : data) {
            trees.add(createTree(sub).init());
        }
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
