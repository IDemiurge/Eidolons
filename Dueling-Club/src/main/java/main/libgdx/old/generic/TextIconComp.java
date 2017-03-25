package main.libgdx.old.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.StyleHolder;
import main.system.images.ImageManager.ALIGNMENT;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class TextIconComp extends Table {
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
        addActor(imageComp);
        addActor(textComp);
        com.badlogic.gdx.scenes.scene2d.ui.Container textTable =
                new com.badlogic.gdx.scenes.scene2d.ui.Container<TextComp>();
//        center()
        switch (alignment) {

            case NORTH:
                textTable.top();


                break;
            case SOUTH:
                break;
            case EAST:
                break;
            case WEST:
                break;
            case CENTER:
                break;
            case NORTH_WEST:
                break;
            case SOUTH_EAST:
                break;
            case NORTH_EAST:
                break;
            case SOUTH_WEST:
                break;
        }
    }


}
