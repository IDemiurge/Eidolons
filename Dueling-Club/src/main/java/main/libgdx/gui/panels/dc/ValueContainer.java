package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.StringBuilder;
import main.libgdx.StyleHolder;
import org.apache.commons.lang3.StringUtils;

public class ValueContainer extends Container<Table> {
    private static final int SMALL_NAME_SIZE = 3;
    protected Container<Image> imageContainer;
    protected Container<Label> nameContainer;
    protected Container valueContainer;
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

        this.imageContainer = new Container<>();
        table.add(this.imageContainer);
        if (isVertical()) {
            table.row();
        }
        if (texture != null) {
            this.imageContainer.setActor(new Image(texture));
        }

        this.nameContainer = new Container<>();
        table.add(this.nameContainer);
        if (isVertical()) {
            table.row();
        }

        if (name != null) {
            this.nameContainer.setActor(new Label(name, StyleHolder.getDefaultLabelStyle()));
        }

        this.valueContainer = new Container();
        this.valueContainer.fill().center();
        table.add(this.valueContainer);

        if (isVertical()) {
            table.row();
        }

        if (value != null) {
            final Label label = new Label(value, StyleHolder.getDefaultLabelStyle());
            this.valueContainer.setActor(label);
            this.valueContainer.width(30);
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
            if (imageContainer.getActor() != null) {
                imageContainer.setBackground(drawable);
            }
            if (nameContainer.getActor() != null) {
                nameContainer.setBackground(drawable);
            }
            if (valueContainer.getActor() != null) {
                valueContainer.setBackground(drawable);
            }
        }
    }

    public float getNameSize() {
        return nameContainer.getWidth();
    }

    public void cropName() {
        if (nameContainer.getActor() != null) {
            StringBuilder text = nameContainer.getActor().getText();

            text.replace("Modifier", "Mod");
            text.replace("Damage", "Dmg");
            text.replace("Capacity", "Cap.");
            text.replace("Protection", "Prot.");
            text.replace("Penalty", "Pen.");
            text.replace("Restoration", "Rest");
            text.replace("Concentration", "Concentr");
            text.replace("Memorization", "Memorize");
            text.replace("Retainment", "Retain");
            text.replace("Close Quarters", "Close");
            text.replace("Long Reach", "Long");
            text.replace("Defense", "Def.");
            text.replace("Sneak", "Snk.");
            text.replace("Cadence", "Cad.");
            text.replace("Watch", "W.");
            text.replace("Attack", "Attk");
            text.replace("Penetration", "Penetr.");
            text.replace("Diagonal", "Diag.");

            if (StringUtils.contains(text, "Chance")) {
                text.replace("Chance", "");
                if (valueContainer.getActor() != null) {
                    if (valueContainer.getActor() instanceof Label) {
                        final Label actor = (Label) valueContainer.getActor();
                        actor.getText().append("%");
                    }
                }
            }
        }
    }

    private void configure() {
        if (imageContainer.getActor() != null) {
            imageContainer.getActor().setFillParent(true);
            imageContainer.width(imageContainer.getActor().getWidth());
            imageContainer.height(imageContainer.getActor().getHeight());
        }
        if (!singleImageMode) {
            if (isVertical()) {
                imageContainer.padBottom(3);
            } else {
                imageContainer.padLeft(3);
            }
        }
        if (nameContainer.getActor() != null) {
            if (isVertical()) {
                nameContainer.padBottom(3);
            } else {
                nameContainer.padLeft(3);
            }
        }
        if (valueContainer.getActor() != null) {
            if (isVertical()) {
                valueContainer.padBottom(3);
            } else {
                valueContainer.padLeft(3);
            }
        }
    }

    public void updateValue(String val) {
        if (!(valueContainer.getActor() instanceof Label)) {
            valueContainer.setActor(new Label(val, StyleHolder.getDefaultLabelStyle()));
        } else {
            ((Label) valueContainer.getActor()).setText(val);
        }
    }

    protected boolean isVertical() {
        return false;
    }
}

