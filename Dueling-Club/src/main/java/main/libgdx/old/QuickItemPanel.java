package main.libgdx.old;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class QuickItemPanel extends PagedPanel {
    public QuickItemPanel(String imagePath, int col, int row) {
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
    public QuickItemPanel init() {
        return (QuickItemPanel) super.init();
    }
}
