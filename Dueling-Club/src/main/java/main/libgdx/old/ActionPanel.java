package main.libgdx.old;

import main.libgdx.gui.panels.generic.PagedListPanel;

/**
 * Created with IntelliJ IDEA.
 * Date: 29.10.2016
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
public class ActionPanel extends PagedListPanel {
    public ActionPanel(String imagePath, int col, int row) {
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
    public PagedListPanel init() {
        return super.init();
    }
}
