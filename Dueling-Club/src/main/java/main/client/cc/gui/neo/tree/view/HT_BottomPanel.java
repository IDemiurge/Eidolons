package main.client.cc.gui.neo.tree.view;

import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.neo.points.BuyButton;
import main.client.cc.gui.neo.tree.HC_Tree;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.math.DC_MathManager;

import java.awt.*;

public abstract class HT_BottomPanel extends G_Panel {
    protected static final String MASTERY_COMP_ID = "masteryComp";
    protected HC_Tree tree;
    protected ObjType selectedType;
    protected TextCompDC freePoints;
    protected TextCompDC masteryScore;
    protected PoolComp masteryPoints;
    protected CustomButton buyButtonGold;
    protected CustomButton buyButtonXp;
    protected PoolComp costGold;
    protected PoolComp costXp;
    protected DC_HeroObj hero;
    protected ObjType bufferType;
    protected Object arg;
    // TextComp pointCost;
    // TextComp rank;
    public HT_BottomPanel(final Object arg, final DC_HeroObj hero, final HC_Tree tree) {
        this.arg = arg;
        this.hero = hero;
        bufferType = initBufferType();
        main.system.auxiliary.LogMaster.log(1, " bottom buffer set: " + bufferType);

        this.tree = tree;
        buyButtonGold = new BuyButtonSkillTree(true, true);
        buyButtonXp = new BuyButtonSkillTree(true, false);

        masteryPoints = new PoolComp(hero, PARAMS.MASTERY_POINTS, "Mastery Points", false);

        String cost = DC_MathManager.getBuyCost(false, true, hero) + " gold";
        costGold = new PoolComp(cost);
        cost = DC_MathManager.getBuyCost(false, false, hero) + " xp";
        costXp = new PoolComp(cost);
        // rank = new TextComp(null) {
        // protected String getText() {
        // return "Rank: " + DC_MathManager.getMasteryRank(hero,
        // mastery).getName();
        // };
        // };
        int x = 460;
        int y = 160;
        panelSize = new Dimension(x, y);

        addSpecial();
        add(masteryPoints, "id masteryPoints, pos " + MASTERY_COMP_ID + ".x2 points.y2");
        add(buyButtonGold, "id buyButtonGold, pos masteryPoints.x2 points.y2");
        add(buyButtonXp, "id buyButtonXp, pos masteryPoints.x2 buyButtonGold.y2");
        add(costGold, "id costGold, pos buyButtonXp.x2 points.y2");
        add(costXp, "id costXp, pos buyButtonXp.x2 costGold.y2");
        add(new GraphicComponent(STD_COMP_IMAGES.XP), "pos costXp.x2 costXp.y");
        add(new GraphicComponent(STD_COMP_IMAGES.GOLD), "pos costGold.x2 costGold.y");
    }

    private ObjType initBufferType() {
        return hero.getType();
        // return
        // CharacterCreator.getHeroPanel().getMiddlePanel().getScc().getBufferType();
    }

    protected abstract void addSpecial();

    public ObjType getBuffer() {
        return initBufferType();
    }

    @Override
    public void refresh() {

        bufferType = initBufferType();
        main.system.auxiliary.LogMaster.log(1, " bottom buffer reset: " + bufferType);

        masteryPoints.refresh();
        buyButtonGold.refresh();
        buyButtonXp.refresh();
        String cost = DC_MathManager.getBuyCost(false, true, hero) + " gold";
        costGold.setText(cost);
        cost = DC_MathManager.getBuyCost(false, false, hero) + " xp";
        costGold.setText(cost);

    }

    public ObjType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(ObjType selectedType) {
        this.selectedType = selectedType;
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public ObjType getBufferType() {
        return bufferType;
    }

    public void setBufferType(ObjType bufferType) {
        this.bufferType = bufferType;
    }

    protected final class BuyButtonSkillTree extends BuyButton {
        protected BuyButtonSkillTree(boolean attr, boolean gold) {
            super(attr, gold);
        }

        protected void buyPoints(boolean attr, boolean gold) {
            super.buyPoints(!attr, gold);
            refresh();
            masteryPoints.refresh();
        }
    }

}
