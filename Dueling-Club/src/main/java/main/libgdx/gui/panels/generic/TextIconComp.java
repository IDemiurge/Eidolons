package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.libgdx.StyleHolder;

/**
 * Created by JustMe on 1/9/2017.
 */
public class TextIconComp extends Container {
    TextComp textComp;
    public TextIconComp(String text, String imagePath) {
        this(text, imagePath, StyleHolder.getDefaultLabelStyle());
    }
    public TextIconComp(String text, String imagePath, LabelStyle style) {
        super(imagePath);
        textComp= new TextComp(text, style);
    }

    @Override
    public void initComps() {
        setComps(textComp);
    }
}
