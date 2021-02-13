package libgdx.map.town.navigation.data;

import eidolons.macro.entity.MacroObj;

import java.util.Set;

/**
 * Created by JustMe on 11/21/2018.
 */
public abstract class NavigableWrapper<T extends MacroObj> implements Navigable {
    T obj;
    private Set<Navigable> children;
    private Navigable parent;

    public NavigableWrapper(T obj) {
        this.obj = obj;
    }

    @Override
    public String getName() {
        return obj.getName();
    }

    @Override
    public String getTooltip() {
        return obj.getToolTip();
    }

    @Override
    public String getDescription() {
        return obj.getDescription();
    }

    @Override
    public String getIconPath() {
        return obj.getMapIcon();
    }

    @Override
    public String getPreviewImagePath() {
        return obj.getImagePath();
    }

    @Override
    public Navigable getParent() {
        return parent;
    }

    @Override
    public Set<Navigable> getChildren() {
        return children;
    }

    @Override
    public void setChildren(Set<Navigable> children) {
        this.children = children;
    }
}
