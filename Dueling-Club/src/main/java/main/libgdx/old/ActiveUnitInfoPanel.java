package main.libgdx.old;

import main.libgdx.UnitInfoPanel;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class ActiveUnitInfoPanel extends UnitInfoPanel {
    protected ActivePanelUnitName unitName;

    protected QuickActionPagedPanel spellPanel;

    protected QuickActionPagedPanel quickPanel;

    private float minHeight = 0;
    private float minWeight = 0;

    public ActiveUnitInfoPanel(String imagePath) {
        super(imagePath);
    }

    @Override
    public ActiveUnitInfoPanel init() {
        int oldH = 0;
        super.init();

        quickPanel = new QuickActionPagedPanel(imagePath, 2, 5).init();
        quickPanel.setY(oldH);
        quickPanel.setX(portraitPanel.getWidth() / 2 - quickPanel.getWidth() / 2);
        addActor(quickPanel);
        oldH += quickPanel.getHeight();

        spellPanel = new QuickActionPagedPanel(imagePath, 2, 5).init();
        spellPanel.setY(oldH);
        spellPanel.setX(portraitPanel.getWidth() / 2 - spellPanel.getWidth() / 2);
        addActor(spellPanel);
        oldH += spellPanel.getHeight();

        unitName = new ActivePanelUnitName(imagePath).init();

        unitName.setX(portraitPanel.getWidth() / 2 - unitName.getWidth() / 2);
        unitName.setY(oldH);
        addActor(unitName);
        oldH += unitName.getHeight();
        setHeight(getHeight() + oldH);

        portraitPanel.setY(oldH);
        portraitPanel.setX(0);

        buffPanel.setX(portraitPanel.getWidth());
        buffPanel.setY(getHeight() - buffPanel.getHeight());

        itemPanel.setX(portraitPanel.getWidth());
        itemPanel.setY(buffPanel.getY() - itemPanel.getHeight());

        orbPanel.setX(portraitPanel.getWidth() + itemPanel.getWidth());
        orbPanel.setY(getHeight() - orbPanel.getHeight());
        minWeight = portraitPanel.getWidth();
        minHeight = portraitPanel.getY();
        //orbPanel.invertOrbOrder();
        return this;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public float getMinWeight() {
        return minWeight;
    }
}
