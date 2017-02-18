package main.swing.components.panels.page.info.element;

import main.content.DC_ContentManager;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.DC_Obj;
import main.system.graphics.FontMaster;
import main.system.auxiliary.StringMaster;

import java.awt.*;

public class PropertyElement extends ValueTextComp {
    public static final VISUALS HEADER_COMPONENT = VISUALS.PROP_BOX;
    public static final VISUALS ALT_HEADER_COMPONENT = VISUALS.INFO_COMP_HEADER;
    public static final int DEFAULT_SIZE = 18;
    private boolean showName;

    public PropertyElement(PROPERTY prop) {
        super(prop, HEADER_COMPONENT);
    }

    public PropertyElement(String header) {
        super(null, ALT_HEADER_COMPONENT);
        this.text = header;
        this.permanent = true;
    }

    // @Override
    // protected int getDefaultY() {
    // return HEADER_COMPONENT.getHeight()*2/3;
    // }

    @Override
    protected int getDefaultX() {
        return 0; // centered
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getDefaultFont(getSize(value));
    }

    @Override
    protected String getText() {
        if (permanent) {
            return text;
        }
        String propertyValue = getEntity().getProperty((PROPERTY) value);
        if (isShowName()) {
            if (value != null) {
                return value.getShortName() + ": "
                        + StringMaster.getFormattedContainerString(propertyValue);
            }
        }
        return StringMaster.getFormattedContainerString(propertyValue);
    }

    @Override
    protected String getPrefix() {
        return "";
    }

    @Override
    protected Color getColor() {
        Color c = getCustomColor(value);
        if (c == null) {
            c = super.getColor();
        }
        return c;
    }

    @Override
    protected String getValue() {
        if (!getEntity().getGame().isSimulation()) {
            if (getEntity() instanceof DC_Obj) {
                DC_Obj obj = (DC_Obj) getEntity();
                if (obj.getVisibilityLevel() != null) {
                    switch (obj.getVisibilityLevel()) {
                        case CLEAR_SIGHT:
                            break;
                        case BLOCKED:
                        case CONCEALED:
                            return "?";
                        case OUTLINE:
                            // some values should be ok...
                            // if (value == )
                            return "?";
                        case VAGUE_OUTLINE:
                            return "?";
                        default:
                            break;

                    }
                }
            }
        }
        return super.getValue();
    }

    private int getSize(VALUE value) {
        if (value == G_PROPS.NAME) {

        }
        return DEFAULT_SIZE;
    }

    private Color getCustomColor(VALUE value) {
        if (value == G_PROPS.ASPECT) {

        }
        if (value == G_PROPS.DEITY) {

        }
        if (value == G_PROPS.NAME) {
            // enemy? difficulty? (IMPOSSIBLE!)
        }
        return null;
    }

    public boolean isShowName() {
        return DC_ContentManager.isShowValueName(value);
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }
}
