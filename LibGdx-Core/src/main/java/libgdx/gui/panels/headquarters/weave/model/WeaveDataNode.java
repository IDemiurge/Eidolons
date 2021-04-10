package libgdx.gui.panels.headquarters.weave.model;

import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 6/25/2018.
 *
 * controls:
 * learn on double click
 *
 */
public class WeaveDataNode {
    private ObjType type;
    private boolean skill;

    private Object arg;
    private   String description;
    private   String imagePath;

    private List<WeaveDataNode> children;
    private WeaveDataNode parent;

    public WeaveDataNode(ObjType type, boolean skill) {
        this.type = type;
        this.skill = skill;
    }

    public WeaveDataNode(String imagePath, String description, Object arg) {
        this.imagePath = imagePath;
        this.description = description;
        this.arg = arg;
    }

    public ObjType getType() {
        return type;
    }

    public boolean isSkill() {
        return skill;
    }

    public void setChildren(List<WeaveDataNode> children) {
        this.children = children;
    }

    public List<WeaveDataNode> getChildren() {
        return children;
    }

    public void setParent(WeaveDataNode parent) {
        this.parent = parent;
    }

    public WeaveDataNode getParent() {
        return parent;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public String getDescription() {
        if (description == null) {
            return getType().getDescription();
        }
        return description;
    }

    public String getImagePath() {
        if (imagePath == null) {
            return getType().getImagePath();
        }
        return imagePath;
    }
}
