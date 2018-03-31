package main.system.graphics;

import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.VisualComponent;
import main.system.images.ImageManager.ALIGNMENT;
import net.miginfocom.swing.MigLayout;

import java.awt.*;

public class MigMaster {

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String CENTER_X = "center_x";
    public static final String CENTER_Y = "center_y";
    public static final String CENTER_X_ALT = "centered_x";
    public static final String CENTER_Y_ALT = "centered_y";
    public static final String MAX_TOP = "max_top";
    public static final String MAX_BOTTOM = "max_bottom";
    public static final String MAX_LEFT = "max_left";
    public static final String MAX_RIGHT = "max_right";

    public static final String MAX_TOP_ALT = "max_y";
    public static final String MAX_BOTTOM_ALT = "min_y";
    public static final String MAX_LEFT_ALT = "min_x";
    public static final String MAX_RIGHT_ALT = "max_x";
    public static final String PROCESS_CHAR = "@";

    public static String getCenteredTextPosition(String title) {
        return "container.x2/2-" + title.length() + "*" + FontMaster.SIZE / 4;
    }

    public static int getCenteredPosition(int container_length, int element_length) {
        return (container_length - element_length) / 2;
    }

    public static int getCenteredTextPosition(String string, Font font, int width) {
        if (font == null) {
            font = FontMaster.getDefaultFont();
        }

        return (width - FontMaster.getStringWidth(font, string)) / 2;

    }

    public static int getAlignmentPosition(ALIGNMENT alignment, int length, int container_length) {
        switch (alignment) {
            case EAST:
            case SOUTH:
                return 0;
            case CENTER:
                return (container_length - length) / 2;
            case NORTH:
            case WEST:
                return container_length - length;
        }
        return 0;
    }

    public static int getFarRightTextPosition(String text, Font font, int width) {
        return (width - FontMaster.getStringWidth(font, text));
    }

    public static int getCenteredTextPositionY(Font font, int height) {

        return FontMaster.getFontHeight(font) / 2 + (height - FontMaster.getFontHeight(font)) / 2;

    }

    public static LayoutManager getLayout(String string) {
        return new MigLayout(string);
    }

    public static int getCenteredWidth(int width) {
        return getCenteredPosition((int) GuiManager.getScreenSize().getWidth(), width);
    }

    public static int getCenteredHeight(int height) {
        return getCenteredPosition((int) GuiManager.getScreenSize().getHeight(), height);
    }

    public static int getOptimalPosition(Boolean left_right_center, int container, int element) {
        if (left_right_center == null) {
            return getCenteredPosition(container, element);
        }
        return left_right_center ? 0 : container - element;
    }

    public static String processConstraints(G_Panel panel, Component comp, String constraints) {
        int height = (int) comp.getPreferredSize().getHeight();
        int width = (int) comp.getPreferredSize().getWidth();

        int container_height = (int) panel.getPreferredSize().getHeight();
        int container_width = (int) panel.getPreferredSize().getWidth();
        if (panel.getVisuals() != null) {
            if (container_height == 0) {
                container_height = panel.getVisuals().getHeight();
            }
            if (container_width == 0) {
                container_width = panel.getVisuals().getWidth();
            }
        }// if null?! container.x2...

        if (comp instanceof VisualComponent) {
            VISUALS visuals = ((VisualComponent) comp).getVisuals();
            if (visuals != null) {
                if (height == 0) {
                    height = visuals.getHeight();
                }
                if (width == 0) {
                    width = visuals.getWidth();
                }
            }
        }
        return process(constraints, height, width, container_height, container_width);
    }


    public static String process(String constraints, int height, int width, int container_height, int container_width) {
        constraints = constraints.replace(PROCESS_CHAR, "");
        constraints = constraints.replace(WIDTH, "" + width);
        constraints = constraints.replace(HEIGHT, "" + height);
        if (constraints.contains("_")) {
            constraints = constraints.replace(CENTER_X, ""
             + getCenteredPosition(container_width, width));
            constraints = constraints.replace(CENTER_Y, ""
             + getCenteredPosition(container_height, height));

            constraints = constraints.replace(CENTER_X_ALT, ""
             + getCenteredPosition(container_width, width));
            constraints = constraints.replace(CENTER_Y_ALT, ""
             + getCenteredPosition(container_height, height));

            constraints = constraints.replace(MAX_TOP, "" + height);
            constraints = constraints.replace(MAX_BOTTOM, "" + (container_height - height));

            constraints = constraints.replace(MAX_LEFT, "" + width);
            constraints = constraints.replace(MAX_RIGHT, "" + (container_width - width));

            constraints = constraints.replace(MAX_TOP_ALT, "" + height);
            constraints = constraints.replace(MAX_BOTTOM_ALT, "" + (container_height - height));

            constraints = constraints.replace(MAX_LEFT_ALT, "" + width);
            constraints = constraints.replace(MAX_RIGHT_ALT, "" + (container_width - width));
        }
        return constraints;
    }

    public static float center(float parentSize, float size) {
        return (parentSize - size) / 2;
    }

    public static float top(float parentHeight, float height) {
        return (parentHeight - height);
    }
}
