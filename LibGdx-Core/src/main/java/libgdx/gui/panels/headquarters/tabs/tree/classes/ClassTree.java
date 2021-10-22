package libgdx.gui.panels.headquarters.tabs.tree.classes;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.netherflame.eidolon.heromake.passives.HeroClassMaster;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.panels.headquarters.datasource.tree.ClassesDataSource;
import libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import libgdx.gui.panels.headquarters.tabs.tree.HeroTree;

/**
 * Created by JustMe on 5/6/2018.
 */
public class ClassTree extends HeroTree<ClassSlot, PerkSlot> {

    public ClassTree(boolean altBackground) {
        super(altBackground);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
    @Override
    protected int getLinkWidth() {
        return 64;
    }

    @Override
    protected float getLinksSpacing() {
        return 5;
    }

    @Override
    protected float getSlotsSpacing() {
        return 18;
    }

    @Override
    protected PerkSlot[][] createLinkNodeRows(int maxTier) {
        return new PerkSlot[maxTier][];
    }

    @Override
    protected ClassSlot[][] createNodeRows(int maxTier) {
        return new ClassSlot[maxTier][];
    }

    @Override
    protected int getMainSlotsPerTier(int tier) {
        return 5 - tier;
    }

    @Override
    protected int getLinkSlotsPerTier(int tier) {
        switch (tier) {
            case 0:
            case 2:
                return 4;
            case 1:
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 4 - tier;
    }

    @Override
    protected PerkSlot buildEmptyLinkNode(int tier, int i) {
        return new PerkSlot(tier, i);
    }

    @Override
    protected ClassSlot buildEmptyNode(int tier, int i) {
        return new ClassSlot(tier, i);
    }

    @Override
    protected ClassSlot[] createRow(int n) {
        return new ClassSlot[n];
    }

    @Override
    protected PerkSlot[] createLinkRow(int n) {
        return new PerkSlot[n];
    }

    @Override
    protected Object getEmptySlotData(int tier, int slot) {
        return HeroClassMaster.getOpenSlot();
    }

    @Override
    protected boolean isDataAnOpenSlot(Object lastData) {
        if (lastData == null) {
            return false;
        }
        return HeroClassMaster.isDataAnOpenSlot( lastData);
    }
    @Override
    protected boolean isSequentialLinks() {
        return true;
    }

    @Override
    protected boolean isSequentialSlots() {
        return true;
    }

    @Override
    protected HeroTreeDataSource createTreeDataSource(HqHeroDataSource dataSource) {
        return new ClassesDataSource(dataSource.getEntity());
    }
}
