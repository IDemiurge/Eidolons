package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import com.badlogic.gdx.utils.Align;
import eidolons.entity.feat.spaces.FeatSpace;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;

import java.util.*;

public class FeatSpacePanel extends GroupX {
    private static final String SPELL_SPACE_ICON = "ui/new/spaces/spell_spaces.png";
    private static final String COMBAT_SPACE_ICON = "ui/new/spaces/combat_spaces.png";
    private static final int INACTIVE_OFFSET = -400;
    protected final int imageSize;
    protected boolean hovered;
    public static boolean hoveredAny; // do we need to ... update anything?
    protected boolean firstUpdateDone;

    // public static final int SLOTS = FeatSpaceDcHandler.MAX_SLOTS;

    protected Map<FeatSpace, FeatSpaceRow> rows = new LinkedHashMap<>();
    // List<FeatSpace> orderedList;
    protected boolean extended;
    protected boolean spellSpaces;
    protected FeatSpaceRow current;
    protected FadeImageContainer icon;
    protected SymbolButton extendButton;
    protected ValueContainer switches;

    public FeatSpacePanel(int imageSize, boolean spellSpaces) {
        this.imageSize = imageSize;
        this.spellSpaces = spellSpaces;
        addActor(icon = new FadeImageContainer(spellSpaces ? SPELL_SPACE_ICON : COMBAT_SPACE_ICON));
        addActor(switches = new ValueContainer(""));
        addActor(extendButton = new SymbolButton(ButtonStyled.STD_BUTTON.BUTTON_ZARK,
                ()-> setExtended(!extended)));
        extendButton.setOrigin(Align.center);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        FeatsDataSource source = (FeatsDataSource) userObject;
        switches.setValueText(source.getSwitchesLeft(spellSpaces));
        List<FeatSpaceDataSource> ds = source.getFeatSpacesDS();
        if (!firstUpdateDone) {
            init(ds);
        } else {
            for (FeatSpaceDataSource d : ds) {
                FeatSpaceRow row = rows.get(d.space);
                row.setUserObject(d);
                if (d.isActive()){
                    setCurrent(row);
                }

            }
        }
    }

    private void init(List<FeatSpaceDataSource> ds) {
        int i =0;
        for (FeatSpaceDataSource d : ds) {
            FeatSpace space = d.space;
            FeatSpaceRow row = new FeatSpaceRow(space.getName());
            row.setUserObject(d);
            rows.put(space, row);
            addActor(row);
            if (d.isActive()){
                current = row;
            } else {
                row.setY(INACTIVE_OFFSET + imageSize*i);
                row.setVisible(false);
            }
        }

    }

    public void setCurrent(FeatSpaceRow current) {
        FeatSpaceRow prev = this.current;
        this.current = current;
        // ActionMasterGdx.addMoveToAction();
        // ActionMasterGdx.addFadeInAction();
        setExtended(false);

        /*
        what kind of animation could this have? fade others and roll it down to fit onto main slots?
        And the space it leaves? preserve the order of Spaces - could be important
         how would we select a space to go to, just click any point on it? NO!
         do we support the 'only up to 1 shift' ? Stupid for QI!
        */
    }

    public void setExtended(boolean extended) {
        if (extended == this.extended)
            return;
        this.extended = extended;
        ActionMasterGdx.addRotateByAction(extendButton, 180);
        if (extended) {
        /*        roll up,
         */

            // ActionMasterGdx.addMoveToAction();
        } else {

        }
    }


    // @Override
    // public void act(float delta) {
    //     beforeReset -= delta;
    //     TODO Gdx Review
    //     if (hoveredAny) {
    //         super.setUpdateRequired(false);
    //     } else if (beforeReset <= 0) {
    //         beforeReset = getResetPeriod();
    //         super.setUpdateRequired(true);
    //     } else {
    //         super.setUpdateRequired(false);
    //     }
    //     if (!firstUpdateDone) {
    //         super.setUpdateRequired(true);
    //     }
    //     super.act(delta);
    //     firstUpdateDone = true;
    // }


    // @Override
    // public void clear() {
    //     for (Actor child : GdxMaster.getAllChildren(this)) {
    //         child.clear();
    //     }
    //     super.clear();
    // }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return hovered;
    }
}
