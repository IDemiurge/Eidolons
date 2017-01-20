package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.texture.TextureManager;
import main.system.datatypes.Boxer;
import main.system.images.CustomImage;
import main.system.images.ImageManager;
import main.system.images.ImageManager.ALIGNMENT;

/**
 * Created by JustMe on 1/6/2017.
 */
public class ValueComp extends Container {

    private ALIGNMENT textAlignment;
    private LabelStyle style;
    private VALUE value;
    private DC_Obj obj;
    private boolean iconDisplayed;
    private boolean nameDisplayed;
    private TextComp label;

    public ValueComp(VALUE value, DC_Obj obj,
                     boolean nameDisplayed, boolean iconDisplayed,
                     ALIGNMENT textAlignment, LabelStyle style, String bgImage) {
        super(bgImage, getLayout(textAlignment));
        this.iconDisplayed = iconDisplayed;
        this.nameDisplayed = nameDisplayed;
        this.value = value;
        this.obj = obj;
        this.style = style;
        this.textAlignment = textAlignment;

    }

    public ValueComp(DC_Obj unit, VALUE value) {
        this(value, unit, false, false, ALIGNMENT.EAST, StyleHolder.getDefaultLabelStyle(), null);
    }

    private static LAYOUT getLayout(ALIGNMENT textAlignment) {
        return (textAlignment == ALIGNMENT.WEST || textAlignment == ALIGNMENT.EAST) ? LAYOUT.HORIZONTAL : LAYOUT.VERTICAL;
    }


    @Override
    public void initComps() {
        Image valueImage = null;
        if (iconDisplayed)
            valueImage = new Image(TextureManager.getOrCreate(new Boxer<>(
                    () -> ((CustomImage)
                            ImageManager.getValueIcon(value)).getImgPath())));

        label = new TextComp(getText(), style);
//        if (layout)
        setComps(valueImage, label);
    }


    public String getText() {
        String text = obj.getValue(value);
        if (value.isDynamic()) {
            text = ContentManager.getCurrentOutOfTotal((PARAMETER) value, obj);
        }
        if (nameDisplayed)
            text = value.getName() + " :" + text;

        return text;
    }

}
