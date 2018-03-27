package main.client.cc.gui.neo.points;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.awt.*;

public class UnlockDialog extends G_Dialog {

    private static final VISUALS PANEL_WIDE = VISUALS.INFO_PANEL_WIDE;
    private static final String XP = "Self-learn";
    private static final String GOLD = "Hire a mentor";
    protected boolean initialized;
    private PARAMETER param;
    private int xpCost;
    private int goldCost;
    private Entity entity;

    public UnlockDialog(Entity entity, PARAMETER param, int xpCost, int goldCost) {
        this.entity = entity;
        this.xpCost = xpCost;
        this.goldCost = goldCost;
        this.param = param;
        initialized = true;
        init();
    }

    protected void unlock(boolean xp) {
        UnlockMaster.unlock(entity, param, !xp, xp ? xpCost : goldCost);
        close();
        // if (xp) {
        // unlockWithXp();
        // } else
        // unlockWithGold();
    }

    @Override
    protected boolean isOnMousePoint() {
        return true;
    }

    protected void unlockWithGold() {
        entity.modifyParameter(PARAMS.GOLD, -goldCost);
        WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, true);
        close();
    }

    protected void unlockWithXp() {
        entity.modifyParameter(PARAMS.XP, -xpCost);
        WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, true);
        close();
    }

    protected void cancel() {
        WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, false);
        close();
    }

    @Override
    public Component createComponent() {
        G_Panel panel = new G_Panel(PANEL_WIDE);

        CustomButton xpButton = new UnlockButton(true);

        xpButton.setToolTipText(XP);
        xpButton.setText(xpCost + " xp");
        CustomButton goldButton = new UnlockButton(false);
        goldButton.setText(goldCost + " gold");
        goldButton.setToolTipText(GOLD);

        CustomButton cancelButton = new CustomButton(VISUALS.CANCEL) {
            @Override
            public void handleClick() {
                cancel();
            }
        };

        TextCompDC label = new TextCompDC(VISUALS.PROP_BOX);
        label.setText("Unlock " + param.getName());
        panel.add(label, "pos 20 50");
        if (xpCost > 0) {
            panel.add(xpButton, "pos 50 100");
        }

        panel.add(cancelButton, "pos 300 50");
        if (goldCost > 0) {
            panel.add(goldButton, "pos 300 100");
        }

        return panel;
    }

    @Override
    public String getTitle() {
        return "Unlock ";// + param.getName()
    }

    @Override
    public Dimension getSize() {
        return PANEL_WIDE.getSize();
    }

    @Override
    protected boolean isReady() {
        return initialized;
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    public class UnlockButton extends CustomButton {
        static final int X = 45;
        static final int Y = 30;
        private boolean xp;

        public UnlockButton(boolean xp) {
            super((xp) ? VISUALS.XP : VISUALS.GOLD);
            this.xp = xp;

        }

        @Override
        public void handleClick() {
            unlock(xp);
        }

        @Override
        public Dimension getSize() {
            panelSize = new Dimension(X * 3 / 2, Y * 2);
            return super.getSize();
        }

        @Override
        protected int getDefaultX() {
            return X;
        }

        @Override
        protected int getDefaultY() {
            return Y;
        }
    }

}
