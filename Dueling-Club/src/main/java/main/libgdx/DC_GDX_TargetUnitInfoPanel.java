package main.libgdx;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_TargetUnitInfoPanel extends DC_GDX_UnitInfoPanel {

    private DC_GDX_ValueIconGroup valueIconGroup;

    public DC_GDX_TargetUnitInfoPanel(String imagePath) {
        super(imagePath);
    }



    @Override
    public DC_GDX_TargetUnitInfoPanel init() {
        super.init();

        valueIconGroup = new DC_GDX_ValueIconGroup(imagePath).init();
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
