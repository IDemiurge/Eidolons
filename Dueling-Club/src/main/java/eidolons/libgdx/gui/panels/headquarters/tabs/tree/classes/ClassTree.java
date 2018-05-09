package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.tree.ClassesDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HeroTree;

/**
 * Created by JustMe on 5/6/2018.
 */
public class ClassTree extends HeroTree<ClassSlot, PerkSlot> {


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
        return 5-tier;
    }

    @Override
    protected int getLinkSlotsPerTier(int tier) {
        switch (tier){
            case 0:
                return 4;
            case 1:
                return 3;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 4-tier;
    }

    @Override
    protected PerkSlot buildEmptyLinkNode(int tier) {
        return new PerkSlot(tier);
    }

    @Override
    protected ClassSlot buildEmptyNode(int tier) {
        return new ClassSlot(tier);
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
    protected HeroTreeDataSource createTreeDataSource(HqHeroDataSource dataSource) {
        return new ClassesDataSource(dataSource.getEntity());
    }
}
