package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.StringBuilder;
import main.libgdx.StyleHolder;

public class ValueContainer extends Container<Table> {
    private static final int SMALL_NAME_SIZE = 3;
    protected Container<Image> image;
    protected Container<Label> name;
    protected Container value;
    private boolean singleImageMode = false;

    protected ValueContainer() {

    }

    public ValueContainer(TextureRegion texture, String name, String value) {
        init(texture, name, value);
    }

    public ValueContainer(TextureRegion texture) {
        singleImageMode = true;
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
            final Label label = new Label(value, StyleHolder.getDefaultLabelStyle());
            this.value.setActor(label);
            this.value.width(30);
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
            if (image.getActor() != null) {
                image.setBackground(drawable);
            }
            if (name.getActor() != null) {
                name.setBackground(drawable);
            }
            if (value.getActor() != null) {
                value.setBackground(drawable);
            }
        }
    }

    public float getNameSize() {
        return name.getWidth();
    }

    public void cropName() {
        if (name.getActor() != null) {
            StringBuilder text = name.getActor().getText();

            text.replace("Modifier", "Mod");
            text.replace("Damage", "Dmg");
            text.replace("Capacity", "Cap");
            text.replace("Protection", "Prot");
            text.replace("Restoration", "Rest");
            text.replace("Close Quarters", "C.Q.");
            text.replace("Long Reach", "L.R.");
            text.replace("Defense", "Def");
            text.replace("Sneak", "Sn.");
            text.replace("Cadence", "Ca.");
            text.replace("Watch", "W.");

        }
    }

    private void configure() {
        if (image.getActor() != null) {
            image.getActor().setFillParent(true);
            image.width(image.getActor().getWidth());
            image.height(image.getActor().getHeight());
        }
        if (!singleImageMode) {
            if (isVertical()) {
                image.padBottom(3);
            } else {
                image.padLeft(3);
            }
        }
        if (name.getActor() != null) {
            if (isVertical()) {
                name.padBottom(3);
            } else {
                name.padLeft(3);
            }
        }
        if (value.getActor() != null) {
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

    protected boolean isVertical() {
        return false;
    }
}

