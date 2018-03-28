package main.client.cc.gui.neo.points;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.panels.page.info.element.ParamElement;
import main.swing.components.panels.page.info.element.ValueTextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ColorManager;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HC_PointComp extends G_Panel implements MouseListener {

    public static final VISUALS VALUE_BOX = VISUALS.BUTTON_NEW
     // VISUALS.VALUE_BOX
     ;
    protected static final int DEFAULT_FONT_SIZE = 18;
    protected static final int ARROW_OFFSET_X = -6;
    protected static final int ARROW_OFFSET_Y = 7;
    protected HC_PointElement textComp;
    protected PointSpinnerModel model;
    protected GraphicComponent upArrow;
    protected GraphicComponent downArrow;

    protected int arrowWidth;
    protected boolean editable;
    protected Unit hero;
    protected PARAMETER param;
    protected GraphicComponent lock;
    protected GraphicComponent icon;
    boolean locked;

    public HC_PointComp(boolean editable, final Unit hero, ObjType buffer,
                        final PARAMETER param, PARAMETER pool, VISUALS V) {
        this(editable, hero, buffer, param, pool, V, false);
    }

    public HC_PointComp(boolean editable, final Unit hero, ObjType buffer,
                        final PARAMETER param, PARAMETER pool) {
        this(editable, hero, buffer, param, pool, VALUE_BOX);
    }

    public HC_PointComp(boolean editable, final Unit hero, ObjType buffer,
                        final PARAMETER param, PARAMETER pool, VISUALS V, boolean principle) {
        arrowWidth = STD_COMP_IMAGES.ARROW_4_UP.getImg().getWidth(null);
        this.hero = hero;
        this.param = param;
        textComp = getTextElement(editable, param, V);
        textComp.setEntity((editable) ? buffer : hero);
        textComp.refresh();
        textComp.addMouseListener(this);

        Image valueIcon = getValueIcon(param);
        if (ImageManager.isValidImage(valueIcon)) {
            icon = new GraphicComponent(valueIcon);
            icon.addMouseListener(this);
        }

        this.editable = editable;
        if (editable) {
            model = createModel(hero, buffer, param, pool);
            upArrow = new GraphicComponent(ImageManager.getArrowImagePath(true, true,
             getArrowVersion()));

            upArrow.addMouseListener(this);
            downArrow = new GraphicComponent(ImageManager.getArrowImagePath(true, false,
             getArrowVersion()));
            downArrow.addMouseListener(this);
        }

        addComps();
        panelSize = V.getSize();
    }

    protected PointSpinnerModel createModel(final Unit hero, ObjType buffer,
                                            final PARAMETER param, PARAMETER pool) {
        return new PointSpinnerModel(textComp, hero, buffer, param, pool);
    }

    protected Image getValueIcon(final PARAMETER param) {
        return ImageManager.getValueIcon(param);
    }

    protected HC_PointElement getTextElement(boolean editable, final PARAMETER param, VISUALS V) {
        return new HC_PointElement(V != VALUE_BOX, editable, param, V);
    }

    protected int getArrowVersion() {
        return 5;
    }

    public void setEntity(Entity entity) {
        textComp.setEntity(entity);
        if (model != null) {
            model.setEntity(entity);
        }
    }

    @Override
    public void refresh() {
        if (icon != null) {
            updateIcon();
        }
        textComp.refresh();
        removeAll();
        addComps();
        revalidate();
        if (param.isMastery()) {
            textComp.setToolTipText(getToolTipText());
        }
    }

    protected void updateIcon() {
        if (ContentManager.isBase(param)) {
            icon.setImg(HC_Master.generateValueIcon(ContentManager.getFinalAttrFromBase(param),
             isLocked()));
        } else {
            icon.setImg(HC_Master.generateValueIcon(param, isLocked()));
        }
    }

    protected void addComps() {
        if (icon != null) {
            add(icon, "id icon, pos " + getIconOffsetX() + " " + getIconOffsetY()); // pos
            // 6
            // 6");
            setComponentZOrder(icon, getComponentCount() - 1);
        }
        if (isLocked()) {
            if (lock == null) {
                lock = new GraphicComponent(STD_COMP_IMAGES.LOCK);
                lock.addMouseListener(this);
            }
            add(lock, "id lock, pos c.x2+"
             + StringMaster.wrapInParenthesis("" + (getArrowOffsetX() - arrowWidth)) + " "
             + getArrowOffsetY() * 3 / 2);
            setComponentZOrder(lock, getComponentCount() - 1);
        } else if (editable) {
            add(upArrow, "id ua, pos c.x2+"
             + StringMaster.wrapInParenthesis("" + (getArrowOffsetX() - arrowWidth)) + " "
             + getArrowOffsetY());
            setComponentZOrder(upArrow, getComponentCount() - 1);
            add(downArrow, "id da, pos c.x2+"
             + StringMaster.wrapInParenthesis("" + (getArrowOffsetX() - arrowWidth))
             + " ua.y2");
            setComponentZOrder(downArrow, getComponentCount() - 1);
        }
        add(textComp, "id c, pos 0 0");
        setComponentZOrder(textComp, getComponentCount() - 1);

    }

    protected String getIconOffsetX() {
        return "3";
    }

    protected String getIconOffsetY() {
        return "3";
    }

    protected int getArrowOffsetY() {
        return ARROW_OFFSET_Y;
    }

    protected int getArrowOffsetX() {
        return ARROW_OFFSET_X;
    }

    protected void promptUnlock() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean result = false;
                try {
                    result = UnlockMaster.promptUnlock(param, textComp.getEntity());
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);

                }
                if (!result) {
                    return;
                }
                refresh();
                CharacterCreator.getHeroPanel(hero).getMiddlePanel().getScc().refreshPools();
            }
        }).start();
    }

    protected boolean isLocked() {
        if (textComp.getEntity() == null) {
            return true;
        }
        if (!param.isAttribute()) {
            return textComp.getEntity().getIntParam(param) <= 0;
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) { // TODO in new thread!!!
        if (e.getClickCount() > 1 || e.isAltDown()) {
            if (e.getSource() == icon) {
                if (isTreeNagivationOn()) {
                    if (!param.isAttribute()) {
                        HC_Master.goToSkillTree(param);
                        return;
                    }
                }
            }
        }
        clicked();

        if (e.getSource() == upArrow) {
            upClick(); // sound
            // CharacterCreator.refreshGUI();
        } else if (e.getSource() == downArrow) {
            downClick(); // sound
            // CharacterCreator.refreshGUI();
        } else if (e.getSource() == lock) {
            lockClick(e);

        } else {
            // ???
            infoClick();
        }

    }

    protected boolean isTreeNagivationOn() {
        return true;
    }

    protected void clicked() {
        if (param.isMastery()) {
            HC_Master.setLastClickedMastery(param);
        } else {
            HC_Master.setLastClickedAttribute(ContentManager.getFinalAttrFromBase(param));
        }
    }

    protected void downClick() {
        model.tryDown();
    }

    protected void upClick() {
        model.tryUp();
    }

    protected void lockClick(MouseEvent e) {
        if (!param.isMastery()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
        } else {
            if (e.getClickCount() > 1) {
                UnlockMaster.unlock(hero, param, SwingUtilities.isRightMouseButton(e));
            } else {
                promptUnlock();
            }
        }
    }

    protected void infoClick() {
        if (param.isAttribute()) {
            CharacterCreator.getHeroPanel(hero).getMiddlePanel().getScc().getAttrPanel().setValue(
             param);
        } else {
            CharacterCreator.getHeroPanel(hero).getMiddlePanel().getScc().getMstrPanel().setValue(
             param);
        }

        CharacterCreator.getHeroPanel(hero).getMvp().getCurrentViewComp().refresh();
        CharacterCreator.getHeroPanel(hero).getCurrentTab().refresh();

        DC_SoundMaster.playStandardSound(STD_SOUNDS.ON_OFF);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public String getToolTipText() {
        // String toolTipText = param.getName();
        // if (!param.isMastery())
        // return toolTipText;
        // return toolTipText +
        return "Final score: " + hero.getIntParam(ContentManager.getMasteryScore(param));

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean isEditable() {
        return editable;
    }

    public void reset() {
        if (model != null) {
            model.reset();
        }

    }

    public Unit getHero() {
        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

    public PARAMETER getParam() {
        return param;
    }

    public ValueTextComp getTextComp() {
        return textComp;
    }

    public PointSpinnerModel getModel() {
        return model;
    }

    public class HC_PointElement extends ParamElement {

        protected boolean integerValue;
        protected boolean shortened;
        private Integer fontSize;

        public HC_PointElement(boolean shortened, boolean integerValue, VALUE v, VISUALS V) {
            super(v, V);
            y = getDefaultY();
            this.shortened = shortened;
            this.integerValue = integerValue;
        }

        public HC_PointElement(boolean integerValue, VALUE v, VISUALS V) {
            super(v, V);
            y = getDefaultY();
            this.integerValue = integerValue;
        }

        @Override
        protected String getText() {
            if (shortened) {
                return "";
            }
            if (ContentManager.isBase(param)) {
                return "Base: ";
            }
            return super.getText();
        }

        @Override
        protected Color getColor() {
            return ColorManager.getHC_DefaultColor();
        }

        @Override
        protected int getDefaultY() {
            return super.getDefaultY() * 3 / 2;
        }

        protected int getDefaultX() {
            if (icon == null) {
                return super.getDefaultX();
            }
            return 52;
        }

        @Override
        protected int getDefaultX2() {
            return super.getDefaultX2() - arrowWidth;
        }

        @Override
        protected int getDefaultFontSize() {
            if (fontSize == null) {
                fontSize = DEFAULT_FONT_SIZE;
            }
            return fontSize;
        }
    }

}
