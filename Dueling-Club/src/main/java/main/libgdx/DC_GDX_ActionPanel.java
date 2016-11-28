package main.libgdx;

/**
 * Created with IntelliJ IDEA.
 * Date: 29.10.2016
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_ActionPanel extends DC_GDX_PagedPanel {
    public DC_GDX_ActionPanel(String imagePath, int col, int row) {
        super(imagePath, col, row);
    }

    @Override
    protected float getCellScale() {
        return 0.85f;
    }

    @Override
    protected boolean isHorizontal() {
        return true;
    }

    @Override
    public DC_GDX_ActionPanel init() {
        return (DC_GDX_ActionPanel) super.init();
    }
}
