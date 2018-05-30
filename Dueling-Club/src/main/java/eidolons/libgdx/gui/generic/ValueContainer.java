package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import org.apache.commons.lang3.StringUtils;

public class ValueContainer extends TablePanel {
    protected Cell<ImageContainer> imageContainer;
    protected Cell<Label> nameContainer;
    protected Cell<Label> valueContainer;
    private LabelStyle style = StyleHolder.getDefaultLabelStyle();
    private float imageScaleX;
    private float imageScaleY;
    private Label valueLabel;
    private Label nameLabel;

    protected ValueContainer() {

    }

    public ValueContainer(LabelStyle style, TextureRegion texture, String name, String value) {
        this.style = style;
        init(texture, name, value);
    }


    public ValueContainer(TextureRegion texture, String name, String value) {
        init(texture, name, value);
    }

    public ValueContainer(TextureRegion texture) {
        init(texture, null, null);
    }

    public ValueContainer(Image image) {
        imageContainer = addElement(new ImageContainer(image)).size(image.getImageWidth(), image.getImageHeight()).center();
    }

    public ValueContainer(TextureRegion texture, String value) {
        init(texture, null, value);
    }

    public ValueContainer(String name ) {
        init(null, name, null );
    }
    public ValueContainer(String name, String value) {
        init(null, name, value);
    }
    public ValueContainer(LabelStyle style, String name, String value) {
        init(null, name, value);
        setStyle(style);
    }

    public ValueContainer(Label actor) {
        this(actor, null);
    }

    public ValueContainer(Label nameLabel, Label valueLabel) {
        this.nameLabel = nameLabel;
        this.valueLabel = valueLabel;
        init(null, null, null);
    }

    public Cell<Label> getNameContainer() {
        return nameContainer;
    }

    public Cell<Label> getValueContainer() {
        return valueContainer;
    }

    public Label getValueLabel() {
        return valueLabel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    @Override
    public ValueContainer pad(float pad) {
        super.pad(pad);
        return this;
    }

    @Override
    public ValueContainer initDefaultBackground() {
        return (ValueContainer) super.initDefaultBackground();
    }

    protected void init(TextureRegion texture, String name, String value) {
        imageContainer = addElement(null);
        setName(name);
        if (texture != null) {
            imageContainer.setActor(new ImageContainer(new Image(texture)))
             .height(texture.getRegionHeight())
             .width(texture.getRegionWidth())
             .center()
            ;
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
            setName(name);
            nameLabel = new Label(name, style);
        }
        nameContainer.setActor(
         nameLabel
        ).grow().center().padRight(12);

        valueContainer = addElement(null);

        if (isVertical()) {
            row();
        }

        if (value!=null ) {
            valueLabel = new Label(value, style);
        }
        valueContainer.setActor(
         valueLabel).grow().center();
        setNameAlignment(Align.center);
        setValueAlignment(Align.center);

        initSize();
    }

    protected void initSize() {
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
                        final Label actor = valueContainer.getActor();
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
            valueContainer.getActor().setText(val);
        }
    }

    public void setNameText(CharSequence newText) {
        nameLabel.setText(newText);
    }
    public void setValueText(CharSequence newText) {
        valueLabel.setText(newText);
    }
    public void setImage(String newPic) {
        imageContainer.getActor().setImage(newPic);
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

    public void setValueOffset(int offset) {
        valueContainer.padLeft(offset);
    }

    public void wrapNames() {
        if (nameContainer.getActor() != null) {
            nameContainer.getActor().setWrap(true);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }


    public float getImageScaleX() {
        return imageScaleX;
    }

    public float getImageScaleY() {
        return imageScaleY;
    }

    public void overrideImageSize(float w, float h) {
        w = Math.max(0, w);
        h = Math.max(0, h);
//        imageContainer.maxSize(w, h);
        if (imageContainer.getActor() != null) {
            if (isScaledOnHover()) {
                imageContainer.setActorX(
                 imageContainer.getActor().getWidth()/2 - w/2);
                imageContainer.setActorY(
                 imageContainer.getActor().getHeight()/2  - h/2);
                imageScaleX = w / imageContainer.getActor().getWidth();
                imageScaleY = h / imageContainer.getActor().getHeight();
                imageContainer.getActor().setScale(getImageScaleX(),
                 getImageScaleY());
                setFixedSize(true);
                setSize(w, h);
            } else
            {
                imageContainer.size(w, h);
//                defaults().size(w, h);
                setFixedSize(true);
                setSize(w, h);
//                imageContainer.getActor().    setSize(w, h);
                imageContainer.setActorX(
                 imageContainer.getActor().getWidth()/2 - w/2);
                imageContainer.setActorY(
                 imageContainer.getActor().getHeight()/2  - h/2);
            }
        }
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }


    @Override
    public float getHeight() {
        return super.getHeight();
    }

    protected boolean isScaledOnHover() {
        return false;
    }

    public void setImageAlign(int imageAlign) {
        if (imageContainer.getActor() != null) {
            imageContainer.getActor().getContent().setAlign(imageAlign);
        }
    }

    public Cell<ImageContainer> getImageContainer() {
        return imageContainer;
    }


    public void setStyle(LabelStyle labelStyle) {
        if (getNameLabel() != null)
            getNameLabel().setStyle(labelStyle);
        if (getValueLabel() != null)
            getValueLabel().setStyle(labelStyle);
    }

    public String getValueText() {
        if (getValueLabel()==null )
            return null ;
        return getValueLabel().getText().toString();
    }
}

