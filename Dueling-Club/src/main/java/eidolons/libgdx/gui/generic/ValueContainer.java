package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.StringBuilder;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.HqTooltipPanel;
import org.apache.commons.lang3.StringUtils;

public class ValueContainer extends TablePanelX implements AbstractValueContainer {
    protected Cell<ImageContainer> imageContainer;
    protected Cell<LabelX> nameContainer;
    protected Cell<LabelX> valueContainer;
    private LabelStyle style = StyleHolder.getDefaultLabelStyle();
    private float imageScaleX;
    private float imageScaleY;
    private LabelX valueLabel;
    private LabelX nameLabel;

    protected ValueContainer() {

    }

    public ValueContainer(LabelStyle style, TextureRegion texture, String name, String value) {
        this.style = style;
        init(texture, name, value);
    }

    public void invokeClicked() {

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

    public ValueContainer(String name) {
        init(null, name, null);
    }

    public ValueContainer(String name, String value) {
        init(null, name, value);
    }

    public ValueContainer(LabelStyle style, String name, String value) {
        init(null, name, value);
        setStyle(style);
    }

    public ValueContainer(LabelX actor) {
        this(actor, null);
    }

    public ValueContainer(LabelX nameLabel, LabelX valueLabel) {
        this.nameLabel = nameLabel;
        this.valueLabel = valueLabel;
        init(null, null, null);
    }

    public Cell<LabelX> getNameContainer() {
        if (nameContainer == null) {
            initNameContainer("");
        }
        return nameContainer;
    }

    public Cell<LabelX> getValueContainer() {
        if (valueContainer == null) {
            initValueContainer("");
        }
        return valueContainer;
    }

    public LabelX getValueLabel() {
        return valueLabel;
    }

    public LabelX getNameLabel() {
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
        setName(name);

        if (texture != null) {
            initImageContainer(texture);
        } else {
            // imageContainer.fill(false).expand(0, 0);
        }

        if (name != null) {
            initNameContainer(name);
        }

        if (value != null) {
            initValueContainer(value);
        }

        initSize();
    }

    private void initImageContainer(TextureRegion texture) {
        imageContainer = addElement(null);
        imageContainer.setActor(new ImageContainer(new Image(texture)))
                .height(texture.getRegionHeight())
                .width(texture.getRegionWidth())
                .center();
        if (isVertical()) {
            row();
        }
    }

    private void initNameContainer(String name) {
        this.nameContainer = addElement(null);
        if (isVertical()) {
            row();
        }
        nameLabel = new LabelX(name, style);
        nameContainer.setActor(nameLabel).padRight(12);
    }
    private void initValueContainer(String value) {
        valueContainer = addElement(null);

        if (isVertical()) {
            row();
        }
        valueLabel = new LabelX(value, style) {
            @Override
            public float getMaxWidth() {
                if (this.wrapped) {
                    return super.getMaxWidth();
                }
                return super.getPrefWidth();
            }

            @Override
            public float getMinWidth() {
                return super.getPrefWidth();
            }

            @Override
            public float getPrefWidth() {
                return Math.min(HqTooltipPanel.INNER_WIDTH - 50, super.getPrefWidth());
            }
        };
        valueContainer.setActor(
                valueLabel).growX().fillX();
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
                        final LabelX actor = valueContainer.getActor();
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
        valueLabel.pack();
    }

    public void setImage(String newPic) {
        imageContainer.getActor().setImage(newPic);
    }

    protected boolean isVertical() {
        return false;
    }

    public void setNameAlignment(int align) {
        if (getNameContainer().getActor() != null) {
            nameContainer.getActor().setAlignment(align);
            nameContainer.align(align);
        }
    }

    public void setValueAlignment(int align) {
        if (getValueContainer().getActor() != null) {
            valueContainer.getActor().setAlignment(align);
            valueContainer.align(align);
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

    @Override
    protected boolean isVisibleEffectively() {
        return true; //TODO performance fix
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
                        imageContainer.getActor().getWidth() / 2 - w / 2);
                imageContainer.setActorY(
                        imageContainer.getActor().getHeight() / 2 - h / 2);
                imageScaleX = w / imageContainer.getActor().getWidth();
                imageScaleY = h / imageContainer.getActor().getHeight();

                imageContainer.getActor().setScale(getImageScaleX(),
                        getImageScaleY());
                setFixedSize(true);
                setSize(w, h);
            } else {
                imageContainer.size(w, h);
                //                defaults().size(w, h);
                setFixedSize(true);
                setSize(w, h);
                //                imageContainer.getActor().    setSize(w, h);
                imageContainer.setActorX(
                        imageContainer.getActor().getWidth() / 2 - w / 2);
                imageContainer.setActorY(
                        imageContainer.getActor().getHeight() / 2 - h / 2);
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
            imageContainer.align(imageAlign);
        }
    }

    public Cell<ImageContainer> getImageContainer() {
        // if (imageContainer == null) {
        //     initImageContainer(null );
        // }
        return imageContainer;
    }

    public void setNameStyle(LabelStyle labelStyle) {
        getNameLabel().setStyle(labelStyle);
    }

    public void setValueStyle(LabelStyle labelStyle) {
        getValueLabel().setStyle(labelStyle);
    }

    public ValueContainer setStyle(LabelStyle labelStyle) {
        if (getNameLabel() != null)
            getNameLabel().setStyle(labelStyle);
        if (getValueLabel() != null)
            getValueLabel().setStyle(labelStyle);
        return this;
    }

    public String getValueText() {
        if (getValueLabel() == null)
            return null;
        return getValueLabel().getText().toString();
    }

    public void setValueText(CharSequence newText) {
        valueLabel.setText(newText);
        try {
            valueLabel.pack();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    public void layout() {
        super.layout();
    }

    public void wrapText(float maxWidth) {
        if (getNameLabel() != null) {
            getNameLabel().setMaxWidth(maxWidth);
            getNameLabel().setWrap(true);
        }
        if (getValueLabel() != null) {
            getValueLabel().setMaxWidth(maxWidth);
            getValueLabel().setWrap(true);
        }
    }
}

