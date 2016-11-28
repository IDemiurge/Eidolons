package main.libgdx;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_QuickItemPanel extends DC_GDX_PagedPanel {
    public DC_GDX_QuickItemPanel(String imagePath, int col, int row) {
        super(imagePath, col, row);
    }

    @Override
    protected float getCellScale() {
        return 1;
    }

    @Override
    protected boolean isHorizontal() {
        return false;
    }

    @Override
    public DC_GDX_QuickItemPanel init() {
        return (DC_GDX_QuickItemPanel) super.init();
    }
}
