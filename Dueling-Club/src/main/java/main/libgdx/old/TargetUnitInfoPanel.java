package main.libgdx.old;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class TargetUnitInfoPanel extends UnitInfoPanel {

    private ValueIconGroup valueIconGroup;

    public TargetUnitInfoPanel(String imagePath) {
        super(imagePath);
    }



    @Override
    public TargetUnitInfoPanel init() {
        super.init();

        valueIconGroup = new ValueIconGroup(imagePath).init();
        valueIconGroup.setX(getWidth() - valueIconGroup.getWidth());
        valueIconGroup.setY(0);

        addActor(valueIconGroup);


        setHeight(getHeight() + valueIconGroup.getHeight());
        portraitPanel.setY(getHeight() - portraitPanel.getHeight());
        orbPanel.setY(getHeight() - orbPanel.getHeight());
        buffPanel.setY(getHeight() - buffPanel.getHeight());
        itemPanel.setY(buffPanel.getY() - itemPanel.getHeight());
        return this;
    }
}
