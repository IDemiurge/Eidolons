package main.libgdx.old;

import main.libgdx.gui.panels.PagedPanel;

/**
 * Created with IntelliJ IDEA.
 * Date: 29.10.2016
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
public class ActionPanel extends PagedPanel {
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
    public ActionPanel init() {
        return (ActionPanel) super.init();
    }
}
