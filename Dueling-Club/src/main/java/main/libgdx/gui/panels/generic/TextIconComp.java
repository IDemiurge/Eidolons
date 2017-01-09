package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.libgdx.StyleHolder;
import main.system.images.ImageManager.ALIGNMENT;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class TextIconComp extends Group {
    TextComp textComp;
    int offsetX;
    int offsetY;
    ALIGNMENT alignment;
    private LabelStyle style;
    private Supplier<String> textGetter;
    private Supplier<String> imgGetter;
    private Comp imageComp;


    public TextIconComp(Supplier<String> textGetter, Supplier<String> imgGetter
    ) {
        this(textGetter, imgGetter
         , StyleHolder.getDefaultLabelStyle()
         , 0,
         0,
         ALIGNMENT.EAST);
    }

    public TextIconComp(Supplier<String> textGetter, Supplier<String> imgGetter
     , LabelStyle style
     , int offsetX,
                        int offsetY,
                        ALIGNMENT alignment
    ) {
        this.style = style;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.alignment = alignment;
        this.textGetter = textGetter;
        this.imgGetter = imgGetter;
    }


    public TextIconComp(String text, String imagePath) {
        this(() -> text, () -> imagePath);
    }


    public void initComps() {
        imageComp = new Comp(
         imgGetter.get());
        textComp = new TextComp(textGetter.get(), style);
        //switch (alignment)
        addActor(imageComp);
        addActor(textComp);

    }


}
