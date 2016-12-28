package main.libgdx.old;

/**
 * Created with IntelliJ IDEA.
 * Date: 29.10.2016
 * Time: 23:56
 * To change this template use File | Settings | File Templates.
 */
public class QuickSpellPagedPanel extends QuickActionPagedPanel {
    public QuickSpellPagedPanel(String imagePath, int col, int row) {
        super(imagePath, col, row);
    }

    @Override
    protected String[] getActionButtonImagePaths() {
        return new String[]{imagePath + "\\UI\\components\\new\\log.png", imagePath + "\\UI\\components\\new\\hand.jpg"};
    }
}
