package main.libgdx;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_ActiveUnitInfoPanel extends DC_GDX_UnitInfoPanel {
    protected DC_GDX_ActivePanelUnitName unitName;

    protected DC_GDX_QuickActionPagedPanel spellPanel;

    protected DC_GDX_QuickActionPagedPanel quickPanel;

    private float minHeight = 0;
    private float minWeight = 0;

    public DC_GDX_ActiveUnitInfoPanel(String imagePath) {
        super(imagePath);
    }

    @Override
    public DC_GDX_ActiveUnitInfoPanel init() {
        int oldH = 0;
        super.init();

        quickPanel = new DC_GDX_QuickActionPagedPanel(imagePath, 2, 5).init();
        quickPanel.setY(oldH);
        quickPanel.setX(portraitPanel.getWidth() / 2 - quickPanel.getWidth() / 2);
        addActor(quickPanel);
        oldH += quickPanel.getHeight();

        spellPanel = new DC_GDX_QuickActionPagedPanel(imagePath, 2, 5).init();
        spellPanel.setY(oldH);
        spellPanel.setX(portraitPanel.getWidth() / 2 - spellPanel.getWidth() / 2);
        addActor(spellPanel);
        oldH += spellPanel.getHeight();

        unitName = new DC_GDX_ActivePanelUnitName(imagePath).init();

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
