package main.client.cc.gui.neo.tree;

import main.client.cc.CharacterCreator;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.CoreEngine;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HT_Node extends ListItem<ObjType> {
    List<HT_Node> children;
    ObjType parent;
    private String req;
    private boolean viewMode;
    private boolean grouped;
    private List<String> reqs;
    private boolean reqOverlaysDrawn;

    public HT_Node(ObjType type, int size, ObjType parent) {
        super(type, false, false, size);
        // generateComponent();
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getObj().getName() + " node";
    }

    @Override
    protected boolean isHighlighted() {
        return isSelected();
    }

    @Override
    public boolean isSelected() {
        // return HC_Master.getSelectedTreeNode() == this;
        return selected;
    }

    @Override
    public void initDefaultBorders() {
        Image image = getIcon().getImage();
        image = ImageManager.applyImage(ImageManager.getNewBufferedImage(64, 64), image, 0, 0);
        if (isAcquired()) {
            // if (getType().getIntParam(PARAMS.CIRCLE) > 4)
            // image = ImageManager.applyBorder(image, BORDER.GOLDEN_PLUS_64);
            // if (getType().getIntParam(PARAMS.CIRCLE) > 2)
            // image = ImageManager.applyBorder(image, BORDER.SILVER_64);
            // else
            if (getType().getIntParam(PARAMS.CIRCLE) % 2 == 0) {
                image = ImageManager.applyBorder(image, BORDER.STEEL_64);
            } else {
                image = ImageManager.applyBorder(image, BORDER.PLATINUM_64);
            }

        } else if (!CoreEngine.isArcaneVault()) {
            if (!isAvailable()) {
                if (!viewMode) {

                    // draw vertically
                    int y = 0;

                    reqOverlaysDrawn = true;
                    ObjType parent = DataManager.getParent(getType());
                    if (parent != null) {
                        reqOverlaysDrawn = CharacterCreator.getHero().checkProperty(
                                getContainerProperty(), parent.getName());
                        // so what's the issue?
                    }
                    if (reqOverlaysDrawn) {
                        image = ImageManager.applyBorder(image, BORDER.CONCEALED);
                    } else {
                        image = ImageManager.applyBorder(image, BORDER.HIDDEN);
                    }
                    if (isBaseBlocked()) {
                        y = drawVertically(image, STD_IMAGES.REQ_BLOCKED.getImage(), y);
                    } else {
                        if (isCostBlocked()) {
                            y = drawVertically(image, STD_IMAGES.REQ_XP.getImage(), y);
                            // image = ImageManager.applyBorder(image,
                            // BORDER.SPEC_LOCK);
                        }
                        if (isBlocked()) {
                            y = drawVertically(image, STD_IMAGES.REQ_MASTERY.getImage(), y);
                            // image = ImageManager.applyBorder(image,
                            // BORDER.SPEC_DEAD2);
                        }
                    }
                }
            }
        }
        // if (isHighlighted()) {
        // image = ImageManager.applyBorder(image, BORDER.HIGHLIGHTED);
        // }
        setIcon(new ImageIcon(image));
    }

    public boolean isBlocked() {
        if (!ListMaster.isNotEmpty(getReqs())) {
            return false;
        }
        if (isBaseBlocked()) {
            return false;
        }
        return ListMaster.contains(getReqs(), InfoMaster.PROP_REASON_STRING, false);
    }

    public boolean isBaseBlocked() {
        return ListMaster.contains(getReqs(), InfoMaster.BASE, false);
        // return req.contains(InfoMaster.BASE); //
        // if (isSkill()) {
        // return req.contains("Skills required: ");
        // } else {
        // return req.contains("Classes Required: ");
        // }
    }

    public boolean isSkill() {
        return getType().getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS;
    }

    private int drawVertically(Image image, Image img, int y) {
        if (!reqOverlaysDrawn) {
            return y;
        }
        if (image == null) {
            return y;
        }
        if (img == null) {
            return y;
        }
        int x = image.getWidth(null) - img.getWidth(null);
        image.getGraphics().drawImage(img, x, y, null);
        // try {
        // } catch (UnsupportedOperationException e) {
        // } catch (Exception e) {
        // main.system.ExceptionMaster.printStackTrace(e);
        // }

        y += img.getHeight(null) + 2;
        return y;
    }

    public boolean isAcquired() {
        return CharacterCreator.getHero().checkProperty(getContainerProperty(),
                getValue().getName());
    }

    protected PROPS getContainerProperty() {
        return PROPS.SKILLS;
    }

    public boolean isCostBlocked() {
        if (getReq() == null) {
            return false;
        }
        if (!ListMaster.isNotEmpty(getReqs())) {
            return false;
        }
        return ListMaster.contains(getReqs(), InfoMaster.PARAM_REASON_STRING, false);
        // return req.contains(InfoMaster.PARAM_REASON_STRING);
    }

    public boolean isAvailable() { // tooltip TODO
        return getReq() == null;
    }

    public void refresh() {
        setReqs(null);
        setReq(null);
        super.refresh();

    }

    public String getReq() {
        if (req == null) {
            setReq(DC_Game.game.getRequirementsManager().getRequirements(getType(), 0).checkReason(
                    getRef(), getObj()));
        }
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    private Ref getRef() {
        Ref ref = new Ref(CharacterCreator.getHero());
        if (CharacterCreator.getHero().getFeat(isSkill(), getType()) != null) {
            ref.setMatch(CharacterCreator.getHero().getFeat(isSkill(), getType()).getId());
        } else {
            ref.setMatch(getType().getId());
        }
        return ref;
    }

    public List<String> getReqs() {
        if (reqs == null) {
            Ref ref = getRef();
            DC_Game.game.getRequirementsManager().getRequirements(getType(), 0).check(ref, true);
            setReqs(DC_Game.game.getRequirementsManager().getRequirements(getType(), 0)
                    .getReasons());
        }
        return reqs;
    }

    public void setReqs(List<String> reqs) {
        this.reqs = reqs;
    }

    public ObjType getType() {
        return (ObjType) super.getObj();
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;

    }

    public boolean isGrouped() {
        return grouped;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }

    public ObjType getParentType() {
        return parent;
    }

    public void setParent(ObjType parent) {
        this.parent = parent;
    }
}
