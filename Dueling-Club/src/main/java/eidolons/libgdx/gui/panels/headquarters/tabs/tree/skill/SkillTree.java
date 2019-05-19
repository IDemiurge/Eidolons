package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.tree.SkillsDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HeroTree;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SkillTree extends HeroTree<MasteryRankSlot, SkillSlot> {

    public SkillTree(boolean altBackground) {
        super(altBackground);
    }

    @Override
    protected HeroTreeDataSource createTreeDataSource(HqHeroDataSource dataSource) {
        return new SkillsDataSource(dataSource.getEntity());
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
        return 14;
    }

    @Override
    protected SkillSlot[][] createLinkNodeRows(int maxTier) {
        return new SkillSlot[maxTier][];
    }

    @Override
    protected MasteryRankSlot[][] createNodeRows(int maxTier) {
        return new MasteryRankSlot[maxTier][];
    }

    @Override
    protected int getMainSlotsPerTier(int tier) {
        return SkillMaster.getSlotsForTier(tier);
    }

    @Override
    protected int getLinkSlotsPerTier(int tier) {
        return SkillMaster.getLinkSlotsPerTier(tier);
    }

    @Override
    protected MasteryRankSlot buildEmptyNode(int tier, int i) {
        MasteryRankSlot node = new MasteryRankSlot(tier, i);
        return node;
    }

    @Override
    protected SkillSlot buildEmptyLinkNode(int tier, int i) {
        SkillSlot node = new SkillSlot(tier, i);
        return node;
    }

    @Override
    protected MasteryRankSlot[] createRow(int n) {
        return new MasteryRankSlot[n];
    }

    @Override
    protected SkillSlot[] createLinkRow(int n) {
        return new SkillSlot[n];
    }

}
