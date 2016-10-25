package main.swing.components.panels.secondary;

import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.swing.generic.components.G_Panel;
import main.system.DC_SoundMaster;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionModePanel extends G_Panel implements MouseListener {
    public static final String AUTO_ON = "Auto Pick On";
    public static final String AUTO_OFF = "Auto Pick Off";
    public static final int ICON_WIDTH = 50;
    public static final int ICON_HEIGHT = 50;
    protected static final int GAP_X = 6;
    protected static final int BORDER_X = 12;
    protected static final int BORDER_Y = 6;
    protected DC_ActiveObj action;
    // protected boolean vertical; // spells/items
    protected List<DC_ActiveObj> subActions;
    protected DC_ActiveObj previousAction;
    protected String tooltip = "";
    protected Map<Rectangle, DC_ActiveObj> mouseMap = new HashMap<Rectangle, DC_ActiveObj>();
    protected int auto = FontMaster.getFontHeight(getTooltipFont()) / 2;
    int wrap = 5;

    public ActionModePanel() {
        addMouseListener(this);
    }

    @Override
    public int getWidth() {
        return Math.max(getSubActions().size() * ICON_WIDTH + (getSubActions().size() - 1 * GAP_X)
                + getOffsetX() * 2, FontMaster.getStringWidth(getTooltipFont(), tooltip));
    }

    @Override
    public int getHeight() {
        return getBackgroundHeight() +
                // toolTipLines.size()*
                FontMaster.getFontHeight(getTooltipFont()) + auto * 2;
    }

    protected int getBackgroundHeight() {
        return (getSubActions().size() / wrap + 1) * ICON_HEIGHT + BORDER_Y * 2;
    }

    protected void initSubActions() {
        subActions = getAction().getSubActions();
        int y = FontMaster.getFontHeight(getTooltipFont());
        int x = getOffsetX();
        mouseMap.clear();
        for (DC_ActiveObj a : subActions) {
            mouseMap.put(new Rectangle(x, y, ICON_WIDTH, ICON_HEIGHT), a);
            x += ICON_WIDTH + GAP_X;
        }
    }

    private int getOffsetX() {
        return BORDER_X;
    }

    @Override
    public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int x = getOffsetX();
        int y = 0;
        if (isDrawBackground()) {
            g.setColor(getBackgroundColor());
            g.fillRect(x, y, getWidth(), getHeight()); // overlap text on dark
            // background
        }

        if (isDrawTooltip()) {
            g.setFont(getTooltipFont());
            g.setColor(getTextColor());

            // center x...
            y += FontMaster.getFontHeight(getTooltipFont()) / 2;
            g.drawString(tooltip, x, y);
            y += FontMaster.getFontHeight(getTooltipFont()) / 2;
        }

        // g.setColor(Color.black);

        x = drawActions(g, x, y);

        // g.drawImage(STD_IMAGES.ATTACKS.getImage(), 4, getHeight() - auto,
        // null);

        if (isDrawAuto()) {
            String string = action.isSwitchOn() ? AUTO_ON : AUTO_OFF;
            g.setColor(getTextColor());
            g.drawString(string, MigMaster.getCenteredTextPosition(string, getTooltipFont(),
                    getWidth()), getHeight() - auto);
            mouseMap.put(new Rectangle(0, getHeight() - auto, auto, getWidth()), null);
        }
        super.paint(g);
    }

    protected boolean isDrawAuto() {
        return true;
    }

    protected boolean isDrawTooltip() {
        return true;
    }

    protected boolean isDrawBackground() {
        return true;
    }

    protected int drawActions(Graphics g, int x, int y) {
        for (DC_ActiveObj subAction : getSubActions()) {
            if (subAction == null)
                continue;
            boolean blocked = checkBlocked(subAction);
            boolean on_off = false;
            on_off = isOn(subAction);
            Image image = ImageManager.getModeImage(getModeFromSubAction(subAction), on_off,
                    blocked);
            if (image == null)
                continue;
            g.drawImage(image, x, y, null);

            if (action.getOwnerObj().getPreferredAttackOfOpportunity() == subAction) {
                Image specImage = ImageManager.getPreferredAoO_Image();
                g.drawImage(specImage, x + image.getWidth(null) - specImage.getWidth(null) / 2, y
                        + image.getHeight(null) - specImage.getHeight(null), null);
            }
            if (action.getOwnerObj().getPreferredCounterAttack() == subAction) {
                Image specImage = ImageManager.getPreferredCounter_Image();
                g.drawImage(specImage, x - specImage.getWidth(null) / 2, y + image.getHeight(null)
                        - specImage.getHeight(null), null);
            }
            if (action.getOwnerObj().getPreferredInstantAttack() == subAction) {
                Image specImage = ImageManager.getPreferredInstant_Image();
                g.drawImage(specImage, x + image.getWidth(null) - specImage.getWidth(null) / 2, y,
                        null);
            }

            x += ICON_WIDTH + GAP_X;
        }
        return x;
    }

    protected boolean isOn(DC_ActiveObj subAction) {
        boolean on_off;
        if (action.getActionMode() == null)
            on_off = false;
        else
            on_off = action.getActionMode().equals(getModeFromSubAction(subAction));
        return on_off;
    }

    private boolean checkBlocked(DC_ActiveObj subAction) {
        return !(subAction.canBeActivated(action.getRef()));
    }

    protected void toggleAuto() {
        action.setAutoSelectionOn(!action.isSwitchOn());
        refresh();
    }

    protected Color getBackgroundColor() {
        return ColorManager.BACKGROUND_MORE_TRANSPARENT;
    }

    protected Color getTextColor() {
        return ColorManager.STANDARD_TEXT;
    }

    protected Font getTooltipFont() {
        return FontMaster.getFont(FONT.AVQ, 16, Font.PLAIN);
    }

    protected boolean toggleAttack(MouseEvent e, DC_ActiveObj subAction) {
        boolean result = false;
        if (e.isAltDown()) {
            if (action.getOwnerObj().getPreferredAttackOfOpportunity() == subAction)
                action.getOwnerObj().setPreferredAttackOfOpportunity(null);
            else
                action.getOwnerObj().setPreferredAttackOfOpportunity(subAction);
            result = true;
        }
        if (e.isControlDown()) {
            if (action.getOwnerObj().getPreferredCounterAttack() == subAction)
                action.getOwnerObj().setPreferredCounterAttack(null);
            else
                action.getOwnerObj().setPreferredCounterAttack(subAction);
            result = true;
        }
        if (e.isShiftDown()) {
            if (action.getOwnerObj().getPreferredInstantAttack() == subAction)
                action.getOwnerObj().setPreferredInstantAttack(null);
            else
                action.getOwnerObj().setPreferredInstantAttack(subAction);
            result = true;
        }

        return result;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        DC_ActiveObj subAction = null;
        for (Rectangle rect : mouseMap.keySet()) {
            if (rect.contains(e.getPoint())) {
                subAction = mouseMap.get(rect);
                break;
            }
        }
        if (subAction == null) {
            // if (e.getPoint().y < auto)
            toggleAuto();
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            SoundMaster.playStandardSound(STD_SOUNDS.ON_OFF);
            subAction.invokeRightClicked();
            tooltip = subAction.getName();

            // info panel or quick info tooltip
        } else {
            if (action.isAttack())
                if (toggleAttack(e, subAction)) {
                    refresh();
                    return;
                }
            if (!action.getGame().isSimulation()) {
                boolean on_off = action.getOwnerObj().toggleActionMode(action,
                        getModeFromSubAction(subAction));
                DC_SoundMaster.playSoundForModeToggle(on_off, action,
                        getModeFromSubAction(subAction));
                // if (action.isAttack()) {
                // tooltip = (subAction).getName();
                // action.getOwnerObj().setPreferredAttackAction(on_off ?
                // subAction
                // : null);
                // } else
                tooltip = getModeFromSubAction(subAction) + (on_off ? " +on+" : " -off-");

                if (on_off)
                    if (e.isAltDown()
                        // || action.getTargeting() instanceof SelectiveTargeting
                            ) {
                        subAction.invokeClicked();
                    }

                DC_Game.game.getBattleField().getBuilder().getUap().getPanelForAction(action)
                        .getCurrentComponent().repaint();
            }
        }
        refresh();

    }

    @Override
    public void refresh() {
        setPanelSize(new Dimension(getWidth(), getHeight()));
        repaint();
    }

    protected String getModeFromSubAction(DC_ActiveObj subAction) {
        if (subAction.isStandardAttack())
            return subAction.getName();
        return subAction.getName().replace(" " + action.getName(), "");
    }

    public List<DC_ActiveObj> getSubActions() {
        if (subActions == null)
            initSubActions();
        return subActions;
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
        if (action != null)
            tooltip = action.getName();

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public DC_ActiveObj getAction() {
        return action;
    }

    public void setAction(DC_ActiveObj activeObj) {
        previousAction = action;
        action = activeObj;
        // if (previousAction != action)
        subActions = null;
        tooltip = activeObj.getName() + " Modes";
        setPanelSize(new Dimension(getWidth(), getHeight()));
    }

    public DC_ActiveObj getPreviousAction() {
        return previousAction;
    }

}
