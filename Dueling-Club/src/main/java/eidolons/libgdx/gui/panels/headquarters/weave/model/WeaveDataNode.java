package eidolons.libgdx.gui.panels.headquarters.weave.model;

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
    private List<WeaveDataNode> children;
    private WeaveDataNode parent;

    public WeaveDataNode(ObjType type, boolean skill) {
        this.type = type;
        this.skill = skill;
    }

    public WeaveDataNode(String imagePath, String description) {

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
}
