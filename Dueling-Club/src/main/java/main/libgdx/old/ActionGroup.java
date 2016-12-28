package main.libgdx.old;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 29.10.2016
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public class ActionGroup extends Group {
    private ActionPanel panel;
    private ActionPanel panel2;
    private ActionPanel panel3;
    private ActionPanel panel4;
    private ActionPanel panel5;
    private ActionPanel panel6;

    private String imagePath;

    public ActionGroup(String imagePath) {
        this.imagePath = imagePath;
    }

    public ActionGroup init() {
        panel = new ActionPanel(imagePath, 5, 1).init();
        panel2 = new ActionPanel(imagePath, 5, 1).init();
        panel3 = new ActionPanel(imagePath, 5, 1).init();
        panel4 = new ActionPanel(imagePath, 5, 1).init();
        panel5 = new ActionPanel(imagePath, 5, 1).init();
        panel6 = new ActionPanel(imagePath, 5, 1).init();

        final int hOffset = 0;
        final int wOffset = 10;

        panel.setX(0);
        panel.setY(0);

        panel2.setX(0);
        panel2.setY(panel.getHeight() + hOffset);

        panel3.setX(panel.getWidth() + wOffset);
        panel3.setY(0);

        panel4.setX(panel2.getWidth() + wOffset);
        panel4.setY(panel3.getHeight() + hOffset);

        panel5.setX(panel3.getWidth() * 2 + wOffset * 2);
        panel5.setY(0);

        panel6.setX(panel3.getWidth() * 2 + wOffset * 2);
        panel6.setY(panel5.getHeight() + hOffset);

        addActor(panel);
        addActor(panel2);

        addActor(panel3);
        addActor(panel4);

        addActor(panel5);
        addActor(panel6);

        setWidth(panel.getWidth() * 3 + wOffset * 2);
        setHeight(panel.getHeight() * 2 + hOffset);
        return this;
    }
}
