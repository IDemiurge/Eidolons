package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import main.libgdx.StyleHolder;
import org.apache.commons.lang3.StringUtils;

public class ValueContainer extends TablePanel {
    private static final int SMALL_NAME_SIZE = 3;
    protected Cell<Image> imageContainer;
    protected Cell<Label> nameContainer;
    protected Cell<Label> valueContainer;
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
        imageContainer = addElement(null);

        if (texture != null) {
            imageContainer.setActor(new Image(texture))
                    .height(texture.getRegionHeight())
                    .width(texture.getRegionWidth())
                    .center();
        } else {
            imageContainer.fill(false).expand(0, 0);
        }

        if (isVertical()) {
            row();
        }

        this.nameContainer = addElement(null);
        if (isVertical()) {
            row();
        }

        if (name != null) {
            nameContainer.setActor(new Label(name, StyleHolder.getDefaultLabelStyle())).grow().center();
        }

        valueContainer = addElement(null);

        if (isVertical()) {
            row();
        }

        if (value != null) {
            valueContainer.setActor(new Label(value, StyleHolder.getDefaultLabelStyle())).grow().center();
        }

        setNameAlignment(Align.center);
        setValueAlignment(Align.center);
    }

    public void setBorder(TextureRegion region) {
        setBorder(region, false);
    }

    public void setBorder(TextureRegion region, boolean wrapEach) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        if (!wrapEach) {
            background(drawable);
        } else {
          /*  if (imageContainer.getActor() != null) {
                imageContainer.setBackground(drawable);
            }
            if (nameContainer.getActor() != null) {
                nameContainer.setBackground(drawable);
            }
            if (valueContainer.getActor() != null) {
                valueContainer.setBackground(drawable);
            }*/
        }
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

    public void setNameAlignment(int align) {
        if (nameContainer.getActor() != null) {
            nameContainer.getActor().setAlignment(align);
        }
    }

    public void setValueAlignment(int align) {
        if (valueContainer.getActor() != null) {
            valueContainer.getActor().setAlignment(align);
        }
    }
}

