package main.swing.components.panels.page.info.element;

import main.content.VALUE;
import main.content.parameters.MultiParameter;
import main.content.parameters.PARAMETER;
import main.system.graphics.FontMaster;
import main.system.images.ImageManager;

import java.awt.*;

public class ParamElement extends ValueTextComp {

    private static final VISUALS VALUE_BOX_SMALL = VISUALS.VALUE_BOX_SMALL;

    private static final int DEFAULT_SIZE = 16;

    private int X_1 = 8;
    private int X_2_FACTOR = 4;

    private Integer fontSize;

    private String paramValue;

    private Image image;

    public ParamElement(VALUE v, VISUALS V) {
        super(v, V);
        y = getDefaultY();
    }

    public ParamElement(VALUE v) {
        super(v, VALUE_BOX_SMALL);

    }

    @Override
    public void paint(Graphics g) {
        // while (true)? :)
        setDefaultFont(getDefaultFont());
        if (FontMaster.getStringWidth(getDefaultFont(), getText()) >= visuals.getWidth() - X_1
                * (X_2_FACTOR + 1)) {
            fontSize = DEFAULT_SIZE - 1;
            X_1 /= 2;
            X_2_FACTOR += 2;
            setDefaultFont(getDefaultFont());
        }
        super.paint(g);

        if (isMultiParameter()) {
            paintParamIcon(g);
        }

        if (isPaintBlocked()) {
            return;
        }
        paramValue = getValue();
        if (!isMultiParameter()) {
            if (paramValue.contains(".")) {
                if (!entity.getGame().isSimulation()) {
                    paramValue = "" + entity.getIntParam((PARAMETER) value);
                }
            }
        }
        // now here we could use some color or so... getParam(base)
        g.drawString(paramValue + "", getDefaultX2(), getDefaultY());
    }

    private void paintParamIcon(Graphics g) {
        if (value instanceof MultiParameter) {
            MultiParameter multiParameter = (MultiParameter) value;
            if (image == null) {
                image = ImageManager.getValueIcon(multiParameter.getParameters()[0]);
            }
            if (image == null) {
                image = ImageManager.getEmptyEmblem().getImage();
            }

            g.drawImage(image, x, (getVisuals().getHeight() - image.getHeight(null)) / 2, null);
        }
    }

    @Override
    protected boolean isPaintText() {
        return (!isMultiParameter());
    }

    private boolean isMultiParameter() {
        return value instanceof MultiParameter;
    }

    // if (entity != null && param != null)
    // if ((DC_ContentManager.isParamFloatDisplayed(param)))
    // text = entity.getParam(param);
    // else
    @Override
    protected int getDefaultFontSize() {
        if (fontSize != null) {
            return fontSize;
        }
        return DEFAULT_SIZE;
    }

    protected String getValue() {
        // Revamp INFO LEVELS! TODO
        // if (!getEntity().getGame().isSimulation())
        // if (getEntity() instanceof DC_Obj) {
        // DC_Obj obj = (DC_Obj) getEntity();
        // if (obj.getVisibilityLevel() != null)
        // switch (obj.getVisibilityLevel()) {
        // case CLEAR_SIGHT:
        // break;
        // case BLOCKED:
        // case CONCEALED:
        // return "?";
        // case OUTLINE:
        // // some values should be ok...
        // // if (value == )
        // return "?";
        // case VAGUE_OUTLINE:
        // return "?";
        // }
        // }
        return super.getValue();
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getDefaultFont(getDefaultFontSize());
    }

    @Override
    protected int getDefaultY() {
        return 20;
    }

    @Override
    protected int recalculateX() {
        return getDefaultX();
    }

    protected int getDefaultX2() {
        if (isMultiParameter()) {
            return Math.max(image.getWidth(null), (visuals.getImage().getWidth(null) - FontMaster
                    .getStringWidth(getDefaultFont(), paramValue)) / 2);
        }

        return visuals.getImage().getWidth(null) - X_1 * X_2_FACTOR;
    }

    @Override
    protected int getDefaultX() {
        return X_1;
    }

    @Override
    protected String getText() {
        return getPrefix();
    }

    @Override
    protected String getPrefix() {
        return value.getShortName() + ":";
    }

}
