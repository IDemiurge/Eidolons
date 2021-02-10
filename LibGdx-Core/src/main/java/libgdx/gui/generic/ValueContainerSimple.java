package libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import libgdx.StyleHolder;

public class ValueContainerSimple extends GroupX implements AbstractValueContainer{
    private LabelStyle style = StyleHolder.getDefaultLabelStyle();
    private Label valueLabel;
    private Label nameLabel;
 
    public ValueContainerSimple(String name, String value) {
        init(null, name, value);
    }

    @Override
    public Label getValueLabel() {
        return valueLabel;
    }

    @Override
    public Label getNameLabel() {
        return nameLabel;
    }

    protected void init(TextureRegion texture, String name, String value) {


        if (name != null) {
            setName(name);
           addActor(  nameLabel = new Label(name, style)) ;
        }
        if (value != null) {
            addActor(valueLabel = new Label(value, style))  ;
        }
    }

    @Override
    public void setNameText(CharSequence newText) {
        nameLabel.setText(newText);
        valueLabel.pack();
    }

    protected boolean isVertical() {
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        nameLabel.setY(0);
        valueLabel.setY(0);
        nameLabel.setX(0);
        valueLabel.setX(getWidth()-valueLabel.getWidth());

    }
    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

    @Override
    public void setNameStyle(LabelStyle labelStyle) {
        getNameLabel().setStyle(labelStyle);
    }

    @Override
    public void setValueStyle(LabelStyle labelStyle) {
        getValueLabel().setStyle(labelStyle);
    }

    @Override
    public ValueContainer setStyle(LabelStyle labelStyle) {
        if (getNameLabel() != null)
            getNameLabel().setStyle(labelStyle);
        if (getValueLabel() != null)
            getValueLabel().setStyle(labelStyle);
        return null;
    }

    @Override
    public String getValueText() {
        if (getValueLabel() == null)
            return null;
        return getValueLabel().getText().toString();
    }

    @Override
    public void setValueText(CharSequence newText) {
        valueLabel.setText(newText);
        try {
            valueLabel.pack();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

}

