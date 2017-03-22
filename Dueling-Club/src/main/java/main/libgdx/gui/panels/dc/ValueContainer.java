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

    protected Container<Image> image;
    protected Container<Label> name;
    protected Container value;

    protected ValueContainer() {

    }

    public ValueContainer(TextureRegion texture, String name, String value) {
        init(texture, name, value);
    }

    public ValueContainer(TextureRegion texture) {
        init(texture, null, null);
    }

    public ValueContainer(TextureRegion texture, String value) {
        init(texture, null, value);
    }

    public ValueContainer(String name, String value) {
        init(null, name, value);
    }

    protected void init(TextureRegion texture, String name, String value) {
        fill().left().bottom();
        Table table = new Table();
        table.setFillParent(true);
        table.left().bottom();

        this.image = new Container<>();
        table.add(this.image);
        if (isVertical()) {
            table.row();
        }
        if (texture != null) {
            this.image.setActor(new Image(texture));
        }

        this.name = new Container<>();
        table.add(this.name);
        if (isVertical()) {
            table.row();
        }

        if (name != null) {
            this.name.setActor(new Label(name, StyleHolder.getDefaultLabelStyle()));
        }

        this.value = new Container();
        this.value.fill().center();
        table.add(this.value);

        if (isVertical()) {
            table.row();
        }

        if (value != null) {
            this.value.setActor(new Label(value, StyleHolder.getDefaultLabelStyle()));
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
            if (image.getActor() != null) {
                image.getActor().setFillParent(true);
                image.width(image.getActor().getWidth());
                image.height(image.getActor().getHeight());
            }
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
        if (!(value.getActor() instanceof Label)) {
            value.setActor(new Label(val, StyleHolder.getDefaultLabelStyle()));
        } else {
            ((Label) value.getActor()).setText(val);
        }
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

