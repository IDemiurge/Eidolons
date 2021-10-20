package libgdx.gui.panels.headquarters.tabs.tree.skill;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.obj.attach.DC_PassiveObj;
import eidolons.game.module.herocreator.logic.passives.SkillMaster;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.panels.headquarters.datasource.tree.HeroTreeDataSource;
import libgdx.gui.panels.headquarters.datasource.tree.SkillsDataSource;
import libgdx.gui.panels.headquarters.tabs.tree.HeroTree;
import main.content.enums.entity.SkillEnums;
import org.apache.commons.lang3.tuple.Triple;

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
        return new MasteryRankSlot(tier, i);
    }

    @Override
    protected SkillSlot buildEmptyLinkNode(int tier, int i) {
        return new SkillSlot(tier, i);
    }

    @Override
    protected MasteryRankSlot[] createRow(int n) {
        return new MasteryRankSlot[n];
    }

    @Override
    protected SkillSlot[] createLinkRow(int n) {
        return new SkillSlot[n];
    }

    @Override
    protected boolean isDataAnOpenSlot(Object lastData) {
        return SkillMaster.isDataAnOpenSlot(
                (Triple<DC_PassiveObj, SkillEnums.MASTERY, SkillEnums.MASTERY>) lastData);
    }

    @Override
    protected Object getEmptySlotData(int tier, int slot) {
        return SkillMaster.getEmptySkill();
    }



    @Override
    protected boolean isSequentialLinks() {
        return true;
    }

    @Override
    protected boolean isSequentialSlots() {
        return false;
    }

}
