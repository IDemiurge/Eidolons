package libgdx.gui.dungeon.panels.headquarters.weave.actor;

import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveHighlightable;
import eidolons.content.consts.Images;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveLinkActor extends FadeImageContainer implements WeaveHighlightable {
    WeaveNodeActor fromNode;
    WeaveNodeActor toNode;

    public WeaveLinkActor(WeaveNodeActor fromNode, WeaveNodeActor toNode) {
        super(Images.WEAVE_LINK);
        this.fromNode = fromNode;
        this.toNode = toNode;
        getColor().a= getDefaultAlpha();
    }

    private float getDefaultAlpha() {
        return 0.4f;
    }

    @Override
    public void highlight() {
        if (fromNode!=null )
            fromNode.highlight();
        ActionMasterGdx.addAlphaAction(this, 0.5f, 1f);
    }

    @Override
    public void highlightOff() {
        if (fromNode!=null )
            fromNode.highlightOff();
        ActionMasterGdx.addAlphaAction(this, 0.5f, getDefaultAlpha());

    }
}
