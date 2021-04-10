package libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import libgdx.GdxImageMaster;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.creation.HeroCreationWorkspace;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import libgdx.gui.panels.headquarters.weave.WeaveMaster;
import libgdx.TiledNinePatchGenerator;
import libgdx.gui.generic.btn.ButtonStyled;
import org.apache.commons.lang3.tuple.ImmutableTriple;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class HeroTree<N extends HtNode, N2 extends HtNode>
        extends HqElement {

    protected N[][] mainNodeRows;
    protected N2[][] linkNodeRows;

    public HeroTree() {
        this(false);
    }

    public HeroTree(boolean altBackground) {
        if (altBackground) {
            setSize(HeroCreationWorkspace.PREVIEW_WIDTH, HeroCreationWorkspace.SELECTION_HEIGHT);
            setBackground(NinePatchFactory.getLightPanelDrawable());
        } else
            setBackgroundAndSize(GdxImageMaster.
                    getPanelBackground(TiledNinePatchGenerator.NINE_PATCH.SAURON, TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.PATTERN,
                            530, 725));
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
            add(new SmartTextButton("Weave", ButtonStyled.STD_BUTTON.MENU, WeaveMaster::openWeave));
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

    protected float getLinkOffsetY(int tier) {
        return 0;
    }

    protected float getLinkOffsetX(int tier) {
        return (getInnerWidth() - getLinkSlotsPerTier(tier) *
                (getLinkWidth() + getLinksSpacing())) / 2;
    }

    @Override
    public float getMinWidth() {
        return getWidth();
    }

    @Override
    public float getMinHeight() {
        return getHeight();
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
        updateSlots();
        updateLinks();
    }

    private void updateLinks() {
        update(false);
    }

    private void updateSlots() {
        update(true);
    }

    protected void update(boolean slotsOrLinks) {
        HtNode[][] array = slotsOrLinks ? mainNodeRows : linkNodeRows;

        for (int tier = 0; tier < array.length; tier++) {
            HeroTreeDataSource treeDataSource = createTreeDataSource(dataSource);
            int length = array[tier].length;
            Object lastData = null;
            boolean lastSlot = false;
            for (int slot = 0; slot < length; slot++) {
                if (lastSlot) {
                    lastData = null;
                } else if (isSequential(slotsOrLinks)) {
                    if (isDataAnOpenSlot(lastData)) {
                        lastData = null;
                    } else
                        lastData = treeDataSource.getData(tier, slot, slotsOrLinks);
                } else {
                    lastData = treeDataSource.getData(tier, slot, slotsOrLinks);
                }
//                if (isSequential(slotsOrLinks))
//                    if (lastData == null) {
//                     if (!lastSlot)
//                        lastData = getEmptySlotData(tier, slot);
//                    lastSlot = true;
//                }
                if (!slotsOrLinks)
                    if (lastData instanceof ImmutableTriple) {
                        if (((ImmutableTriple) lastData).getLeft() == null) {
                            lastSlot = true;
                        }
                    }

                array[tier][slot].setUserObject(lastData);
                array[tier][slot].update(0);
            }
        }

//        for (int tier = 0; tier < linkNodeRows.length; tier++) {
//            HeroTreeDataSource treeDataSource = createTreeDataSource(dataSource);
//            int length = linkNodeRows[tier].length;
//            Object lastData = null;
//            for (int slot = 0; slot < length; slot++) {
//                if (isSequentialLinks()) {
//                    if (lastData != null) {
//                        if (isDataAnOpenSlot(lastData)) {
//                            linkNodeRows[tier][slot].setUserObject(null);
//                            linkNodeRows[tier][slot].update(delta);
//                            continue;
//                        }
//                    }
//
//                }
//                lastData = treeDataSource.getLinkData(tier, slot);
//                linkNodeRows[tier][slot].setUserObject(lastData);
//                linkNodeRows[tier][slot].update(delta);
//            }
//        }


        // or update all nodes?
        // yes, each node at a time,with anim actions!
    }

    protected abstract boolean isDataAnOpenSlot(Object lastData);

    protected boolean isSequential(boolean slotsOrLinks) {
        return slotsOrLinks ? isSequentialSlots() : isSequentialLinks();
    }

    protected abstract Object getEmptySlotData(int tier, int slot);

    protected abstract boolean isSequentialLinks();

    protected abstract boolean isSequentialSlots();

    protected abstract HeroTreeDataSource createTreeDataSource(HqHeroDataSource dataSource);
}
