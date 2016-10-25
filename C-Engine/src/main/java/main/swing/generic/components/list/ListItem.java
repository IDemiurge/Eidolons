package main.swing.generic.components.list;

import main.content.ContentManager;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.generic.misc.BORDER_CHECKER;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.math.Property;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ListItem<E> extends JLabel {

    public static final String EMPTY_LIST_ITEM = "UI\\EMPTY_LIST_ITEM.jpg";
    protected E value;
    protected ActionListener listener;
    protected int obj_size;
    protected boolean selected;
    protected boolean alt;
    protected boolean highlighted;
    protected boolean disabled;
    protected String emptyIcon;
    protected BORDER border;
    protected BORDER_CHECKER borderChecker;
    protected boolean isSelected;
    boolean visible;
    private boolean obj;

    public ListItem(E item, boolean isSelected, boolean cellHasFocus, int obj_size) {
        this(false, item, isSelected, cellHasFocus, obj_size);
    }

    public ListItem(boolean obj, E item, boolean isSelected, boolean cellHasFocus, int obj_size) {
        this.setObj(obj);
        this.isSelected = isSelected;
        this.obj_size = obj_size;
        if (obj_size == 0)
            obj_size = GuiManager.getSmallObjSize();
        this.setSelected(isSelected);
        // if (item == null) {
        // if (getEmptyIcon() != null)
        // setIcon(ImageManager.getIcon(getEmptyIcon()));
        // return;
        // }
        this.setObj(item);

        refresh();

        // Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        // setBorder(emptyBorder);
    }

    @Override
    public String toString() {
        if (getValue() != null)
            return getValue().toString();
        return "Empty List Item";
    }

    public void refresh() {
        if (getValue() instanceof String) {
            if (isSelected()) {
                if (isHighlightSelected())
                    try {
                        setIcon(new ImageIcon(ImageManager.applyBorder(ImageManager
                                .getImage((String) getValue()), BORDER.HIGHLIGHTED)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else
                setIcon(ImageManager.getIcon((String) getValue()));
            return;
        }
        if ((isObj() && getObj() == null) || getValue() == null) {
            if (getEmptyIcon() != null)
                setIcon(ImageManager.getIcon(getEmptyIcon()));
            else
                setIcon(ImageManager.getEmptyIcon(obj_size));
            return;
        }

        setIcon(getCompIcon(getObj(), isSelected()));
        String tooltip = getToolTip();
        setToolTipText(tooltip);

        try {
            initBorder();
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (!ImageManager.isValidIcon(getIcon()) || isTextShownAlways())
            if (getValue() instanceof Entity)
                setText(getObj().getName());
            else
                setText(getValue().toString());
    }

    private boolean isTextShownAlways() {
        if (getValue() instanceof Entity)
            return ContentManager.getInstance().isTextAlwaysShownInListItems(
                    getObj().getOBJ_TYPE_ENUM());
        return false;
    }

    protected boolean isHighlightSelected() {
        return true;
    }

    public Image getImage() {
        return getIcon().getImage();
    }

    protected String getToolTip() {
        return getObj().getProperty(G_PROPS.NAME, false);
    }

    protected ImageIcon getCompIcon(Entity entity, boolean isSelected) {
        if (entity == null)
            return null;
        Image img = (entity.getCustomIcon() != null ? entity.getCustomIcon().getImage()
                : ImageManager.getImage(entity.getProperty(G_PROPS.IMAGE, true)));
        boolean noImg = false;
        if (img == null)
            if (entity.getRef() != null) {
                img = ImageManager.getImage(new Property(entity.getProperty(G_PROPS.IMAGE, true))
                        .getStr(entity.getRef()));
            }
        if (img == null) {
            noImg = true;
            img = (getEmptyIcon() != null) ? ImageManager.getImage(getEmptyIcon()) : ImageManager
                    .getEmptyIcon(getObjSize()).getImage();
        }
        if (getObjSize() != 0)
            if (img.getHeight(null) != getObjSize() || img.getWidth(null) != getObjSize()) {
                img = ImageManager.getSizedVersion(img, new Dimension(getObjSize(), getObjSize()));
            } else if (img.getWidth(null) > ImageManager.getMaxTypeIconSize()
                    || img.getHeight(null) > ImageManager.getMaxTypeIconSize())
                img = ImageManager.getSizedVersion(img, new Dimension(ImageManager
                        .getMaxTypeIconSize(), ImageManager.getMaxTypeIconSize()));

        ImageIcon icon = new ImageIcon(img);

        return icon;
    }

    protected boolean isHighlighted() {
        if (getValue() instanceof Obj) {
            Obj obj = (Obj) getValue();
            try {
                return obj.getGame().getManager().getSelectingSet().contains(getValue());
            } catch (Exception e) {

            }
        }
        return false;
    }

    protected void initBorder() {
        if (borderChecker != null) {
            try {
                initBorderConcurrently(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        // else
        initDefaultBorders();
    }

    @Override
    public ImageIcon getIcon() {
        if (super.getIcon() instanceof ImageIcon)
            return (ImageIcon) super.getIcon();
        if (super.getIcon() == null)
            return null;
        return new ImageIcon(((ImageIcon) super.getIcon()).getImage());
    }

    public void initDefaultBorders() {
        if (border == null)
            border = getSpecialBorder();
        if (border != null)
            setIcon(new ImageIcon(ImageManager.applyBorder(getIcon().getImage(), border)));
        if (isSelected)
            if (isHighlightSelected())
                if (!ImageManager.isValidImage(getImage()))
                    setFont(getFont().deriveFont(15).deriveFont(Font.ITALIC).deriveFont(Font.BOLD)
                            .deriveFont(Font.HANGING_BASELINE));
                else
                    setIcon(new ImageIcon(ImageManager.applyBorder(getIcon().getImage(),
                            BORDER.HIGHLIGHTED)));
    }

    public BORDER getSpecialBorder() {

        return border;
    }

    private void initBorderConcurrently(final ListItem<E> listItem) throws Exception {
        border = borderChecker.getBorder(getObj());
        if (border == null)
            return;
        listItem.setIcon(new ImageIcon(ImageManager.applyBorder(listItem.getIcon().getImage(),
                border)));
        // listItem.initDefaultBorders(); breaks icon, doesn't it
        // new SwingWorker<BORDER, BORDER>() {
        // BORDER border;
        //
        // protected BORDER doInBackground() throws Exception {
        // if (borderChecker != null) {
        // border = borderChecker.getBorder(getObj());
        // return border;
        // }
        // return null;
        // }
        //
        // protected void done() {
        // if (border == null)
        // return;
        // icon = new ImageIcon(ImageManager.applyBorder(icon.getImage(),
        // border));
        // listItem.initDefaultBorders();
        // listItem.setIcon(icon);
        //
        // };
        // }.doInBackground();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public int getObjSize() {
        return obj_size;
    }

    public void setObj_size(int obj_size) {
        this.obj_size = obj_size;
    }

    public Entity getObj() {
        if (value instanceof String) {
            return DataManager.getType((String) value);
        }
        if (value instanceof ObjType) {
            return (ObjType) value;
        }
        if (value instanceof Entity) {
            return ((Entity) getValue());
        }
        return null;
    }

    public E getValue() {

        return value;
    }

    public void setObj(E obj) {

        this.value = obj;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(String emptyIcon) {
        this.emptyIcon = emptyIcon;
    }

    public void setBorder(BORDER border) {
        this.border = border;
    }

    public void setBorderChecker(BORDER_CHECKER borderChecker) {
        this.borderChecker = borderChecker;

    }

    public boolean isObj() {
        return obj;
    }

    public void setObj(boolean obj) {
        this.obj = obj;
    }

    // public boolean isVisible() {
    // return visible;
    // }

    // public void setVisible(boolean visible) {
    // this.visible = visible;
    // }

}
