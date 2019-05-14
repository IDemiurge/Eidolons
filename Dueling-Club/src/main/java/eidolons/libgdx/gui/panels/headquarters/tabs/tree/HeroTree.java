package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationWorkspace;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveMaster;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class HeroTree<N extends HtNode, N2 extends HtNode>
 extends HqElement {

    protected N[][] mainNodeRows;
    protected N2[][] linkNodeRows;

    public HeroTree( ) {
        this(false);
    }
    public HeroTree(boolean altBackground) {
        if (altBackground){
            setSize(HeroCreationWorkspace.PREVIEW_WIDTH, HeroCreationWorkspace.SELECTION_HEIGHT);
            setBackground(NinePatchFactory.getLightPanelDrawable());
        } else
        setBackgroundAndSize(GdxImageMaster.
         getPanelBackground(NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.PATTERN,
          630, 825));
        mainNodeRows = createNodeRows(getMaxTier());
        linkNodeRows = createLinkNodeRows(getMaxTier());
        int tier = isTopToBottom() ? 0 : getMaxTier() - 1;
        for (; isTopToBottom()
         ? tier < getMaxTier()
         : tier >= 0;
             tier += isTopToBottom() ? 1 : -1) {
            TablePanelX rowContainer = new TablePanelX(getInnerWidth(), getRowHeight(tier));
            Stack rowStack = new Stack();
            rowStack.setSize(getWidth(), getRowHeight(tier));

            HorizontalGroup slots = new HorizontalGroup();
            HorizontalGroup links = new HorizontalGroup();
            links.setPosition(getLinkOffsetX(tier), getLinkOffsetY(tier));
            slots.space(getSlotsSpacing());
            links.space(getLinksSpacing());
            rowStack.add(slots);
            rowStack.add(links);

            if (isTopToBottom()) {
                rowContainer.add(slots).center().row();
                rowContainer.add(links).center();
            } else {
                rowContainer.add(links).center().row();
                rowContainer.add(slots).center();
            }


            N[] row = mainNodeRows[tier] = createRow(getMainSlotsPerTier(tier));
            N2[] linkRow = linkNodeRows[tier] = createLinkRow(getLinkSlotsPerTier(tier));
            for (int i = 0; i < getMainSlotsPerTier(tier); i++) {
                slots.addActor(row[i] = buildEmptyNode(tier, i));
            }
            for (int i = 0; i < getLinkSlotsPerTier(tier); i++) {
                links.addActor(linkRow[i] = buildEmptyLinkNode(tier, i));
            }
            add(rowContainer);
            row();
        }
        if (WeaveMaster.isOn())
            add(new SmartButton("Weave", STD_BUTTON.MENU, () -> WeaveMaster.openWeave()));
    }

    @Override
    public float getRowHeight(int rowIndex) {
        return 125;
    }
    // build an empty tree always? probably!
    public void setBackgroundAndSize(Texture texture) {
        setBackground(new TextureRegionDrawable(new TextureRegion(texture)));
        setFixedSize(true);
        setSize(texture.getWidth(), texture.getHeight());
    }

    protected   float getLinkOffsetY(int tier){
        return 0;
    };

    protected float getLinkOffsetX(int tier) {
        return (getInnerWidth() - getLinkSlotsPerTier(tier) *
         (getLinkWidth() + getLinksSpacing())) / 2;
    }

    protected int getInnerWidth() {
        return 590;
    }

    protected abstract int getLinkWidth();

    protected abstract float getLinksSpacing();

    protected abstract float getSlotsSpacing();

    protected boolean isTopToBottom() {
        return true;
    }

    protected abstract N2[][] createLinkNodeRows(int maxTier);

    protected abstract N[][] createNodeRows(int maxTier);

    protected int getMaxTier() {
        return 5;
    }

    protected abstract int getMainSlotsPerTier(int tier);

    protected abstract int getLinkSlotsPerTier(int tier);

    protected abstract N2 buildEmptyLinkNode(int tier, int i);

    protected abstract N buildEmptyNode(int tier, int i);

    protected abstract N[] createRow(int n);

    protected abstract N2[] createLinkRow(int n);

    @Override
    protected void update(float delta) {

        for (int tier = 0; tier < mainNodeRows.length; tier++) {
            HeroTreeDataSource treeDataSource = createTreeDataSource(dataSource);
            int length = mainNodeRows[tier].length;
            for (int slot = 0; slot <  length; slot++) {
                mainNodeRows[tier][slot].setUserObject(treeDataSource.getSlotData(tier, slot));
                mainNodeRows[tier][slot].update(delta);
            }
        }
        for (int tier = 0; tier < linkNodeRows.length; tier++) {
            HeroTreeDataSource treeDataSource = createTreeDataSource(dataSource);
            int length = linkNodeRows[tier].length;
            for (int slot = 0; slot < length; slot++) {
                linkNodeRows[tier][slot].setUserObject(treeDataSource.getLinkData(tier, slot));
                linkNodeRows[tier][slot].update(delta);
            }
        }


        // or update all nodes?
        // yes, each node at a time,with anim actions!
    }

    protected abstract HeroTreeDataSource createTreeDataSource(HqHeroDataSource dataSource);
}
