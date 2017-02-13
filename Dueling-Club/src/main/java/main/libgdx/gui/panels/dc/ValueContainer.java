package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.StyleHolder;

public class ValueContainer extends Container<Table> {
    private static final int SMALL_NAME_SIZE = 3;
    private boolean hideIconOnSmallSize = false;
    private boolean hideNameOnSmallSize = false;
    private boolean showTooltip = false;

    private Container<Image> image;
    private Container<Label> name;
    private Container<Label> value;

    public ValueContainer(TextureRegion texture, String name, String value) {
        fill().left().bottom();
        this.image = new Container<>(new Image(texture));
        this.name = new Container<>(new Label(name, StyleHolder.getDefaultLabelStyle()));
        this.value = new Container<>(new Label(value, StyleHolder.getDefaultLabelStyle()));
        Table table = new Table();
        table.setFillParent(true);
        table.left().bottom();
        table.add(image, this.name, this.value);
        setActor(table);
        configure();
    }

    private void configure() {
        image.getActor().setFillParent(true);
        this.image.width(image.getActor().getWidth());
        this.image.height(image.getActor().getHeight());
        image.padLeft(3);
        name.padLeft(3);
        value.padLeft(3);
    }

    public void updateValue(String val) {
        value.getActor().setText(val);
    }

    public void setHideIconOnSmallSize(boolean hideIconOnSmallSize) {
        this.hideIconOnSmallSize = hideIconOnSmallSize;
        configure();
    }

    public void setHideNameOnSmallSize(boolean hideNameOnSmallSize) {
        this.hideNameOnSmallSize = hideNameOnSmallSize;
        configure();
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        configure();
    }
}

