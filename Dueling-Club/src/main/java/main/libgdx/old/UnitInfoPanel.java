package main.libgdx.old;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.gui.panels.PagedPanel;

/**
 * Created with IntelliJ IDEA.
 * Date: 22.10.2016
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class UnitInfoPanel extends Group {

    protected OrbPanel orbPanel;
    protected PortraitPanel portraitPanel;
    protected PagedPanel buffPanel;
    protected ItemPanel itemPanel;

    protected String imagePath;

    public UnitInfoPanel(String imagePath) {
        this.imagePath = imagePath;
    }

    public UnitInfoPanel init() {
        orbPanel = new OrbPanel(false, imagePath).init();
        orbPanel.setX(0);
        addActor(orbPanel);

        portraitPanel = new PortraitPanel(imagePath).init();
        portraitPanel.setX(orbPanel.getWidth());
        portraitPanel.setY(0);
        addActor(portraitPanel);

        orbPanel.setY(portraitPanel.getHeight() - orbPanel.getHeight());

        buffPanel = new PagedPanel(imagePath, 5, 1).init();
        buffPanel.setX(orbPanel.getWidth() + portraitPanel.getWidth());
        buffPanel.setY(portraitPanel.getHeight() - buffPanel.getHeight());
        addActor(buffPanel);

        itemPanel = new ItemPanel(imagePath).init();
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
