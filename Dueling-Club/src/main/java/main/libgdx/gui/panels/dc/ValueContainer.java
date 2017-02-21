package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
        init(texture, name, value);
    }

    public ValueContainer(TextureRegion texture, String value) {
        init(texture, null, value);
    }

    public ValueContainer(String name, String value) {
        init(null, name, value);
    }

    private void init(TextureRegion texture, String name, String value) {
        fill().left().bottom();
        Table table = new Table();
        table.setFillParent(true);
        table.left().bottom();
        if (texture != null) {
            this.image = new Container<>(new Image(texture));
            table.add(this.image);
            if (isVertical()) {
                table.row();
            }
        }
        if (name != null) {
            this.name = new Container<>(new Label(name, StyleHolder.getDefaultLabelStyle()));
            table.add(this.name);
            if (isVertical()) {
                table.row();
            }
        }
        this.value = new Container<>(new Label(value, StyleHolder.getDefaultLabelStyle()));
        table.add(this.value);
        if (isVertical()) {
            table.row();
        }
        setActor(table);
        configure();
    }

    public void setBorder(TextureRegion region) {
        setBorder(region, false);
    }

    public void setBorder(TextureRegion region, boolean wrapEach) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        if (!wrapEach) {
            background(drawable);
        } else {
            if (image != null) {
                image.setBackground(drawable);
            }
            if (name != null) {
                name.setBackground(drawable);
            }
            if (value != null) {
                value.setBackground(drawable);
            }
        }
    }

    private void configure() {
        if (image != null) {
            image.getActor().setFillParent(true);
            image.width(image.getActor().getWidth());
            image.height(image.getActor().getHeight());
            if (isVertical()) {
                image.padBottom(3);
            } else {
                image.padLeft(3);
            }
        }
        if (name != null) {
            if (isVertical()) {
                name.padBottom(3);
            } else {
                name.padLeft(3);
            }
        }
        if (value != null) {
            if (isVertical()) {
                value.padBottom(3);
            } else {
                value.padLeft(3);
            }
        }
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

    protected boolean isVertical() {
        return false;
    }
}

