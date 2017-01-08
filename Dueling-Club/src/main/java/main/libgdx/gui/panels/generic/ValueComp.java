package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.content.VALUE;
import main.entity.obj.DC_Obj;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureManager;
import main.system.images.CustomImage;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 1/6/2017.
 */
public class ValueComp extends Group {

    private VALUE value;
    private Label label;
    private DC_Obj obj;
    private boolean iconDisplayed;
    private boolean nameDisplayed;
    private Image image;

    public ValueComp(VALUE value, DC_Obj obj,
                     boolean nameDisplayed, boolean iconDisplayed) {
        this.iconDisplayed = iconDisplayed;
        this.nameDisplayed = nameDisplayed;
        this.value = value;
        this.obj = obj;
        image = new Image(TextureManager.getOrCreate(((CustomImage)
                ImageManager.getValueIcon(value)).getImgPath()));
        label = new Label("", StyleHolder.getDefaultLabelStyle());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void update() {
        clearChildren();
        if (nameDisplayed)
            addActor(image);

        label.setText(getText());
        addActor(label);

//           image.setVisible(nameDisplayed);
    }

    public String getText() {
        String text = obj.getValue(value);
        if (nameDisplayed)
            text = value.getName() + " :" + text;

        return text;
    }

}
