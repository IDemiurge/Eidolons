package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.gui.layout.LayoutParser;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogDialog extends Dialog{
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
     "background.png";

    private static final String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum faucibus, augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    public LogDialog() {
        super(bgPath, LayoutParser.LAYOUT.VERTICAL);
//    wrapped text container
        Label l = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        l.setDebug(true);
        Label l2 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l3 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l4 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l5 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l6 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l7 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l8 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l9 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l0 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l11 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l12 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l13 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l14 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l15 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        Label l16 = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        setComps(l, l2, l3, l4, l5, l6, l7, l8, l9, l0, l11, l12, l13, l14, l15, l16);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
