package eidolons.libgdx.stage;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageWindow;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.logpanel.text.OverlayTextPanel;
import eidolons.libgdx.gui.tooltips.ToolTipManager;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.utils.TextInputPanel;
import main.entity.Entity;

public class GenericGuiStage extends StageX {

    protected final LabelX actionTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected final LabelX infoTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected OverlayTextPanel textPanel;
    protected ValueContainer locationLabel;
    protected TextInputPanel tf;
    protected ToolTipManager tooltips;
    protected SuperContainer actionTooltipContainer;
    protected SuperContainer infoTooltipContainer;
    protected ConfirmationPanel confirmationPanel;
    protected DragManager dragManager;
    protected Entity draggedEntity;
    protected TipMessageWindow tipMessageWindow;

    public GenericGuiStage(Viewport viewport, Batch batch) {
        super(viewport == null
                        ? new FillViewport(GdxMaster.getWidth(),
                        GdxMaster.getHeight(), new OrthographicCamera())
                        : viewport,
                batch == null
                        ? new CustomSpriteBatch()
                        : batch);
    }

    public void confirm(String text,
                        boolean canCancel,
                        Runnable onConfirm,
                        Runnable onCancel) {
        confirm(text, canCancel, onConfirm, onCancel, false);
    }

    public void confirm(String text,
                        boolean canCancel,
                        Runnable onConfirm,
                        Runnable onCancel,
                        boolean recursion) {
        if (tipMessageWindow.isVisible()) {
//                tipMessageWindow.getOnClose() TODO
            tipMessageWindow.setOnClose(() -> {
                confirm(text, canCancel, onConfirm, onCancel, true);
            });
            return;
        }
        if (!recursion)
            if (confirmationPanel.isVisible()) {
                confirmationPanel.setOnConfirm(
                        () -> {
                            confirmationPanel.getOnConfirm().run();
                            confirm(text, canCancel, onConfirm, onCancel, true);
                        }
                );
                confirmationPanel.setOnCancel(
                        () -> {
                            confirmationPanel.getOnCancel().run();
                            confirm(text, canCancel, onConfirm, onCancel, true);
                        }
                );
            }
        confirmationPanel.setText(text);
        confirmationPanel.setCanCancel(
                canCancel);
        confirmationPanel.setOnConfirm(onConfirm);
        confirmationPanel.setOnCancel(onCancel);
        confirmationPanel.open();

    }

    protected void showTooltip(String s, LabelX tooltip, float dur) {
        showTooltip(false, s, tooltip, dur);
    }

    protected void showTooltip(boolean action, String s, LabelX tooltip, float dur) {
        showTooltip(null, action, s, tooltip, dur);
    }

    protected void showTooltip(GuiStage.LABEL_STYLE style, boolean action, String s, LabelX tooltip, float dur) {

        infoTooltip.setVisible(true);
        actionTooltip.setVisible(true);

        if (style != null) {
            tooltip.setStyle(StyleHolder.getStyle(style));
        } else {
            tooltip.setStyle(StyleHolder.getDefaultInfoStyle());
        }

        tooltip.setText(s);
        tooltip.getColor().a = 0;
        tooltip.clearActions();
        if (dur != 0) {
            ActionMaster.addFadeInAndOutAction(tooltip, dur, true);
        } else {
            ActionMaster.addFadeInAction(tooltip, 0.85f);
        }
        tooltip.layout();
        tooltip.pack();
        SuperContainer container = (SuperContainer) tooltip.getParent();
        if (container != null)
            container.setFluctuateAlpha(false);
        else
            return;
        tooltip.getParent().setPosition(
                GdxMaster.centerWidthScreen(tooltip) - 20
//                ((GdxMaster.getWidth() - fullLogPanel.getWidth() * 0.88f) - tooltip.getWidth()) / 2
                , action ? GDX.size(175, 0.2f) : GDX.size(200, 0.2f));
    }

    protected void hideTooltip(LabelX tooltip, float dur) {
        SuperContainer container = (SuperContainer) tooltip.getParent();
        ActionMaster.addFadeOutAction(tooltip, dur, true);
        if (container == null)
            return;
        //        tooltip.clearActions();
        container.setFluctuateAlpha(false);

    }

    protected void showText(String s) {
        if (s == null) {
            textPanel.close();
            return;
        }
        textPanel.setText(s);
        textPanel.open();
    }

    public void textInput(Input.TextInputListener textInputListener, String title, String text, String hint) {
        TextInputPanel tf = new TextInputPanel(title, text, hint, textInputListener);
        addActor(tf);
        tf.setPosition(GdxMaster.centerWidth(tf), GdxMaster.centerHeight(tf));
        tf.open();
        setKeyboardFocus(tf);

    }

    public ToolTipManager getTooltips() {
        return tooltips;
    }

    public Closable getDisplayedClosable() {
        return null;
    }

    public boolean isBlocked() {
        return false;
    }

    public Entity getDraggedEntity() {
        return null;
    }

    public void setDraggedEntity(Entity dragged) {
    }

    public void outsideClick() {
    }
}
