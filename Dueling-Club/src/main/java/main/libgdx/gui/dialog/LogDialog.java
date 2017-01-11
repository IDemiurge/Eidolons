package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureManager;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogDialog extends Group {
    public final static String path = "UI\\components\\2017\\dialog\\log\\";
    public final static String bgPath = path +
            "background.png";

    private static final String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum faucibus, augue sit amet porttitor rutrum, nulla eros finibus mauris, nec sagittis mauris nulla et urna. Sed ac orci nec urna ornare aliquam a sit amet neque. Nulla condimentum iaculis dolor, et porttitor dui sollicitudin vel. Fusce convallis fringilla dolor eu mollis. Nam porta augue nec ullamcorper ultricies. Morbi bibendum libero efficitur metus accumsan viverra at ut metus. Duis congue pulvinar ligula, sed maximus tellus lacinia eu.";

    public LogDialog() {
        setSize(300, 500);
        Image bg = new Image(TextureManager.getOrCreate(bgPath));
        bg.setFillParent(true);
        addActor(bg);

        Table tb = new Table();
        tb.setFillParent(true);
        //tb.setDebug(true);
        tb.align(Align.left);

        for (int i = 0; i < 32; i++) {
            if (i != 0 && i != 31) tb.row();
            tb.add(getLabel()).fill().width(getWidth());
        }

        //require to calc valid height
        tb.setLayoutEnabled(true);
        tb.pack();


        ScrollPane sp = new ScrollPane(tb);
        sp.setWidth(getWidth());
        sp.setHeight(getHeight());
        //sp.setDebug(true);
        sp.setForceScroll(false, true);
        sp.setScrollBarPositions(true, true);


        WidgetGroup widgetGroup = new WidgetGroup();
        widgetGroup.setFillParent(true);
        widgetGroup.setDebug(true);
        widgetGroup.addActor(sp);

        Table table = new Table();
        table.setFillParent(true);
        //table.add(sp).fill().expand();
        //table.setDebug(true);

        addActor(widgetGroup);
    }

    private static Label getLabel() {
//        Label l = new Label("loremIpsum", StyleHolder.getDefaultLabelStyle());
        Label l = new Label(loremIpsum, StyleHolder.getDefaultLabelStyle());
        l.setWrap(true);
        l.setAlignment(Align.left);
        return l;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
