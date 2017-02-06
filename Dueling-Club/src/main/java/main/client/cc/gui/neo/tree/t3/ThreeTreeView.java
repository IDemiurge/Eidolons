package main.client.cc.gui.neo.tree.t3;

import main.client.cc.gui.neo.tree.view.HT_View;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.swing.SwingMaster;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.GuiManager;
import main.system.graphics.MigMaster;

import java.awt.*;

public class ThreeTreeView extends G_Panel {
    protected DC_HeroObj hero;
    protected T3InfoPanel infoPanel;
    protected T3InfoPanel infoPanel2;
    protected T3UpperPanel upperPanel;
    // T3HeroPanel heroPanel;

    protected HT_View leftTree;
    protected HT_View centerTree;
    protected HT_View rightTree;
    protected boolean infoSwitch;
    protected Dimension treeSize;

	/*
     * let's say they're just all same for now!
	 */

    public ThreeTreeView(DC_HeroObj hero, Boolean skill_class_spell) {
        setBackground(Color.black);
        setOpaque(true);
        this.hero = hero; // masteries.getOrCreate(0)
        initInfoPanels();
        initTrees(hero, skill_class_spell);
        setPanelSize(getDimension());
        initUpperPanel(hero);
        initDefaultSelection();

        addComps();

    }

    protected Dimension getDimension() {
        return new Dimension(GuiManager.getScreenWidthInt(), GuiManager.getScreenHeightInt());
    }

    protected void initTrees(DC_HeroObj hero, Boolean skill_class_spell) {
        treeSize = getTreeSize();
        if (skill_class_spell != null) {
            leftTree = (!skill_class_spell) ? new T3ClassTreePanel(true, null, hero)
                    : new T3SkillTreePanel(true, null, hero);
        }
        if (skill_class_spell != null) {
            centerTree = (!skill_class_spell) ? new T3ClassTreePanel(true, null, hero) // TODO
                    // T3HeroTreePanel
                    : new T3SkillTreePanel(null, null, hero);
        }
        if (skill_class_spell != null) {
            rightTree = (!skill_class_spell) ? new T3ClassTreePanel(true, null, hero)
                    : new T3SkillTreePanel(false, null, hero);
        }

        leftTree.refresh();
        centerTree.refresh();
        rightTree.refresh();
        leftTree.setPanelSize(treeSize);
        centerTree.setPanelSize(treeSize);
        rightTree.setPanelSize(treeSize);
    }

    protected void initDefaultSelection() {
        leftTree.tabSelected(0);
        centerTree.tabSelected(1);
        rightTree.tabSelected(2);
    }

    protected void initUpperPanel(DC_HeroObj hero) {
        upperPanel = new T3UpperPanel(hero, this);
        upperPanel.setPanelSize(new Dimension(treeSize.width, panelSize.height - treeSize.height));
        upperPanel.init();
    }

    protected Dimension getTreeSize() {
        return new Dimension(VISUALS.TREE_VIEW.getSize().width,
                VISUALS.TREE_VIEW.getSize().height + 42);
    }

    protected void initInfoPanels() {
        infoPanel = new T3InfoPanel(false);
        infoPanel2 = new T3InfoPanel(true);
        Dimension infoSize = new Dimension((GuiManager.getScreenWidthInt() - VISUALS.TREE_VIEW
                .getSize().width) / 2, VISUALS.INFO_PANEL.getSize().height);
        infoPanel.setPanelSize(infoSize);
        infoPanel2.setPanelSize(infoSize);
        infoPanel.init();
        infoPanel2.init();
    }

    protected void addComps() {
        add(infoPanel, getInfoPanelPos());
        add(infoPanel2, getInfoPanel2Pos());
        add(upperPanel, "id up, pos leftTree.x2 0");
        add(leftTree, getLeftTreePos());
        add(centerTree, getCenterTreePos());
        add(rightTree, getRightTreePos());
    }

    protected String getInfoPanel2Pos() {
        return "id ip2, pos centerTree.x2 @max_bottom";
    }

    protected String getInfoPanelPos() {
        return "id ip, pos 0 @max_bottom";
    }

    protected String getRightTreePos() {
        return "id rightTree, pos " + (panelSize.width - treeSize.getWidth()) + " 0";
    }

    protected String getLeftTreePos() {
        return "id leftTree, pos 0 0";
    }

    protected String getCenterTreePos() {
        return "id centerTree, pos " + MigMaster.getCenteredWidth((int) treeSize.getWidth()) + " "
                + "up.y2";
    }

    @Override
    protected boolean isAutoZOrder() {
        return true;
    }

    @Override
    public void refresh() {
        refreshComponents();
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero; // panel per hero perhaps...
        // upperPanel.setHero(hero);
        // leftTree.setHero(hero);
        // centerTree.setHero(hero);
        // rightTree.setHero(hero);

    }

    public void selected(Boolean left_right_none_preferred, Entity value) {
        if (left_right_none_preferred == null) {
            left_right_none_preferred = infoSwitch;
            infoSwitch = !infoSwitch;
        }

        if (left_right_none_preferred) {
            infoPanel.select(value);
        } else {
            infoPanel2.select(value);
        }
    }

    public void selected(final Entity value) {
        SwingMaster.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                selected(null, value);
            }
        });

    }

    public T3InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public T3InfoPanel getInfoPanel2() {
        return infoPanel2;
    }

    public T3UpperPanel getUpperPanel() {
        return upperPanel;
    }

    public HT_View getLeftTree() {
        return leftTree;
    }

    public HT_View getCenterTree() {
        return centerTree;
    }

    public HT_View getRightTree() {
        return rightTree;
    }

    public boolean isInfoSwitch() {
        return infoSwitch;
    }

    // public void init() {
    // infoArray = new String[] {
    //
    // };
    // cInfoArray = new String[] {
    //
    // };
    // }

}
