package main.libgdx;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_UnitInfoPanel extends Group {

    protected DC_GDX_OrbPanel orbPanel;
    protected DC_GDX_PortraitPanel portraitPanel;
    protected DC_GDX_PagedPanel buffPanel;
    protected DC_GDX_ItemPanel itemPanel;

    protected String imagePath;

    public DC_GDX_UnitInfoPanel(String imagePath) {
        this.imagePath = imagePath;
    }

    public DC_GDX_UnitInfoPanel init() {
        orbPanel = new DC_GDX_OrbPanel(false, imagePath).init();
        orbPanel.setX(0);
        addActor(orbPanel);

        portraitPanel = new DC_GDX_PortraitPanel(imagePath).init();
        portraitPanel.setX(orbPanel.getWidth());
        addActor(portraitPanel);

        orbPanel.setY(portraitPanel.getHeight() - orbPanel.getHeight());

        buffPanel = new DC_GDX_PagedPanel(imagePath, 5, 1).init();
        buffPanel.setX(orbPanel.getWidth() + portraitPanel.getWidth());
        buffPanel.setY(portraitPanel.getHeight() - buffPanel.getHeight());
        addActor(buffPanel);

        itemPanel = new DC_GDX_ItemPanel(imagePath).init();
        itemPanel.setX(buffPanel.getX());
        itemPanel.setY(buffPanel.getY() - itemPanel.getHeight());
        addActor(itemPanel);

        setHeight(portraitPanel.getHeight());//set by bigger component
        setWidth(orbPanel.getWidth() + portraitPanel.getWidth() + buffPanel.getWidth());
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }


}
