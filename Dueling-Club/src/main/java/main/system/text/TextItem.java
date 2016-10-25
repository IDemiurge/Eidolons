package main.system.text;

import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TextItem {

    Point point;
    Rectangle rectangle;
    List<Object> lines;
    TEXT_TYPE type;
    Color color;
    Font font;
    Font imageLineFont;
    private Boolean alignment; // true == left, false == right, null == center
    private Boolean imageLineAlignment = true; // true == left, false == right,
    // null == center

    // public TextItem(Point p, TEXT_TYPE type, List<Object> lines) {
    //
    // }

    public TextItem(Point p, TEXT_TYPE type, Object... lines) {
        this(new LinkedList<>(Arrays.asList(lines)), type);
        setPoint(p);
    }

    public TextItem(List<Object> lines, TEXT_TYPE type) {
        this.type = type;
        this.lines = lines;
        color = getColorForType(type);
        font = getFontForType(type);
        imageLineFont = getDefaultImageLineFont();
        rectangle = initArea();
    }

    public TextItem(List<?> lines, Point pointX, TEXT_TYPE info) {
        this(pointX, info, lines.toArray());
    }

    public static Font getFontForType(TEXT_TYPE type) {
        switch (type) {
            case ABILITY_TOOLTIP:
                break;
            case ACTION_TOOLTIP:
                break;
            case ANIMATION:
                break;
            case BUFF_TOOLTIP:
                break;
            case DIALOGUE:
                break;
            case INFO:
                break;
            case ITEM_TOOLTIP:
                break;
            case PARAM_REQUIREMENT:
                FontMaster.getFont(FONT.AVQ, 22, Font.PLAIN);
            case REQUIREMENT:
                break;
            case TARGETING:
                break;
            case TUTORIAL:
                break;
            case UNIT_TOOLTIP:
                break;
            default:
                break;

        }
        return getDefaultTextItemFont();
    }

    public static Font getDefaultTextItemFont() {
        return FontMaster.getFont(FONT.AVQ, 18, Font.PLAIN);
    }

    public static Font getDefaultImageLineFont() {
        return FontMaster.getFont(FONT.MAIN, 26, Font.PLAIN);
    }

    private Rectangle initArea() {
        int width = 0;
        int height = 0;
        for (Object line : lines) {
            int w = (line instanceof ImageLine) ? ((ImageLine) line).getWidth() : FontMaster
                    .getStringWidth(font, line.toString());
            if (w > width)
                width = w;
            int h = (line instanceof ImageLine) ? ((ImageLine) line).getHeight() : FontMaster
                    .getFontHeight(font);
            height += h;
        }
        if (point == null)
            return new Rectangle(0, 0, width, height);
        return new Rectangle(point.x, point.y, width, height);
    }

    private Color getColorForType(TEXT_TYPE type) {
        switch (type) {
            case REQUIREMENT:
                return ColorManager.CRIMSON;
        }
        return ColorManager.GOLDEN_WHITE;
    }

    public Boolean getAlignment() {
        return alignment;
    }

    public void setAlignment(Boolean alignment) {
        this.alignment = alignment;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point p) {
        this.point = p;
        rectangle.setLocation(p);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public List<Object> getLines() {
        return lines;
    }

    public TEXT_TYPE getType() {
        return type;
    }

    public void setType(TEXT_TYPE type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        rectangle = initArea();
    }

    public Font getImageLineFont() {
        return imageLineFont;
    }

    public void setImageLineFont(Font imageLineFont) {
        this.imageLineFont = imageLineFont;
    }

    public Boolean getImageLineAlignment() {
        return imageLineAlignment;
    }

    public void setImageLineAlignment(Boolean imageLineAlignment) {
        this.imageLineAlignment = imageLineAlignment;
    }

    public enum TEXT_TYPE {
        // POSITIVE, NEGATIVE, NEUTRAL
        ANIMATION,
        INFO,
        REQUIREMENT,
        TUTORIAL,
        UNIT_TOOLTIP,
        ACTION_TOOLTIP,
        BUFF_TOOLTIP,
        ITEM_TOOLTIP,
        ABILITY_TOOLTIP,
        DIALOGUE,
        TARGETING,
        PARAM_REQUIREMENT
    }

}
