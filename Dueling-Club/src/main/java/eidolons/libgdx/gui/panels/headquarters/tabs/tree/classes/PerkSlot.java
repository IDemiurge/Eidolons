package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.nodes.HtNode;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.Images;

/**
 * Created by JustMe on 5/6/2018.
 */
public class PerkSlot extends HtNode {

    public PerkSlot( int tier ) {
        super(  tier, Images.EMPTY_RANK_SLOT);
    }

    @Override
    protected Tooltip getTooltip() {
        return null;
    }

    @Override
    protected void click() {

    }

    @Override
    protected void doubleClick() {

    }
}
