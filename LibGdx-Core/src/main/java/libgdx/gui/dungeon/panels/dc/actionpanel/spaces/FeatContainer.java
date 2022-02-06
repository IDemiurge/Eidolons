package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.consts.Images;
import eidolons.entity.feat.active.Spell;
import libgdx.GdxMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.ValueContainer;
import main.content.enums.GenericEnums;

import java.util.function.Supplier;

/**
 * A border that reacts to mouse properly
 * Charges or infinity symbol
 *
 */
public class FeatContainer extends ValueContainer {

    protected  FadeImageContainer highlight;
    protected boolean highlighted;
    protected boolean hover;
    protected Supplier<Integer> charges;

    public FeatContainer(Supplier<Integer> charges, TextureRegion texture, String value) {
        super(texture, value);
        this.charges = charges;
    }


    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlight(boolean b) {
        highlighted = b;
        if (highlight == null) {
            if (getUserObject() instanceof Spell) {
                addActor(highlight = new FadeImageContainer(Images.TARGET_BORDER)); //circle
            } else {
                addActor(highlight = new FadeImageContainer(Images.TARGET_BORDER));
            }
            highlight.setScale(0.4f);
            highlight.setPosition(-4, -4);
            highlight.setColor(0.15f, 0.75f, 0.35f, 1);
            highlight.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_SPEAKER);
        }
        //back and forth floating? or scale?
        if (b) {
            highlight.fadeIn();
        } else {
            highlight.fadeOut();
        }
    }


    @Override
    public void act(float delta) {
        if (highlight != null) {
            if (highlight.getColor().a > 0 && highlight.getActions().size == 0) {
                highlight.fluctuate(delta);
                setScale(highlight.getColor().a);
                setY(highlight.getContent().getColor().a * -32 + 32);
            } else {
                setScale(1);
                setY(0);
            }
        }
        super.act(delta);
    }

    public boolean isHover() {
        return hover;
    }

    public void setHover(boolean hover) {
        if (hover)
        {
            if (highlight != null) {
                highlighted=false;
                highlight.fadeOut();
            }
        }
        this.hover = hover;

        FeatSpacePanel panel = (FeatSpacePanel) GdxMaster.getFirstParentOfClass(this, FeatSpacePanel.class);
        if (panel != null) {
            panel.setHovered(hover);
        }
    }

}
