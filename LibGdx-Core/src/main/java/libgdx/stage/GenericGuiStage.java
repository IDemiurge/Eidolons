package libgdx.stage;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.core.EUtils;
import eidolons.game.exploration.handlers.ExplorationMaster;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.Fluctuating;
import libgdx.bf.generic.SuperContainer;
import libgdx.gui.LabelX;
import libgdx.gui.dungeon.overlay.LargeText;
import libgdx.gui.dungeon.panels.dc.logpanel.text.OverlayTextPanel;
import libgdx.gui.dungeon.tooltips.CursorDecorator;
import libgdx.gui.dungeon.tooltips.ToolTipManager;
import libgdx.gui.utils.TextInputPanel;
import libgdx.shaders.ShaderDrawer;
import libgdx.gui.dungeon.panels.generic.TipMessageWindow;
import main.content.enums.GenericEnums;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class GenericGuiStage extends StageX implements StageWithClosable {

    protected final LabelX actionTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected final LabelX infoTooltip = new LabelX("", StyleHolder.getDefaultInfoStyle());
    protected OverlayTextPanel textPanel;
    protected TextInputPanel textInputPanel;
    protected ToolTipManager tooltips;
    protected SuperContainer actionTooltipContainer;
    protected SuperContainer infoTooltipContainer;
    protected ConfirmationPanel confirmationPanel;
    protected DragManager dragManager;
    protected Entity draggedEntity;
    protected TipMessageWindow tipMessageWindow;
    protected Closable displayedClosable;
    private FileChooser fileChooser;

    protected LargeText largeText;
    protected CursorDecorator cursorDecorator = CursorDecorator.getInstance();

    protected OverlayPanel overlayPanel;

    public GenericGuiStage(Viewport viewport, Batch batch) {
        super(viewport == null
                        ? new FillViewport(GdxMaster.getWidth(),
                        GdxMaster.getHeight(), new OrthographicCamera())
                        : viewport,
                batch == null
                        ? GdxMaster.createBatchInstance()
                        : batch);

        initTooltipsAndMisc();
        GuiEventManager.bind(GuiEventType.SHOW_LARGE_TEXT, p -> {
            List list = (List) p.get();
            largeText.show((String) list.get(0), (String) list.get(1), (Float) list.get(2));
        });

        GuiEventManager.bind(GuiEventType.SHOW_INFO_TEXT, p -> {
            if (p.get() == null) {
                hideTooltip(infoTooltip, 1f);
            } else {
                String text = p.get().toString();
                VisualEnums.LABEL_STYLE style = null;
                if (text.contains(EUtils.STYLE)) {
                    String[] parts = text.split(EUtils.STYLE);
                    style = new EnumMaster<VisualEnums.LABEL_STYLE>().retrieveEnumConst(VisualEnums.LABEL_STYLE.class, parts[0]);
                    text = parts[1];
                }

                //                textToShow.add() queue!
                infoTooltipContainer.setContents(infoTooltip);
                hideTooltip(actionTooltip, 1f);
                showTooltip(style, false, text, infoTooltip, p.getOrDefault("dur", 2f));
            }
        });

        GuiEventManager.bind(GuiEventType.HIDE_ALL_TEXT, p -> {
            hideTooltip(infoTooltip, 1f);
            hideTooltip(actionTooltip, 1f);
            infoTooltip.setVisible(false);
            actionTooltip.setVisible(false);
        });
        GuiEventManager.bind(GuiEventType.HIDE_ACTION_INFO_TEXT, p -> {
            hideTooltip(actionTooltip, 1f);
        });
        GuiEventManager.bind(GuiEventType.HIDE_INFO_TEXT, p -> {
            hideTooltip(infoTooltip, 1f);
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
            ActiveObj active = (ActiveObj) p.get();
            if (ExplorationMaster.isExplorationOn()) {
                return;
            }

            showTooltip(true, active.getOwnerUnit().getNameIfKnown()
                    + " activates " + active.getName(), actionTooltip, 3f);
            hideTooltip(infoTooltip, 1f);

        });

        GuiEventManager.bind(GuiEventType.CONFIRM, p -> {
            Triple<String, Object, Runnable> triple = (Triple<String, Object, Runnable>) p.get();
            if (triple.getMiddle() instanceof Runnable) {
                confirm(triple.getLeft(), true, triple.getRight(), ((Runnable) triple.getMiddle()));
            } else
                confirm(triple.getLeft(), (Boolean) triple.getMiddle(), triple.getRight(), null);

        });
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
        if (tipMessageWindow != null)
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

    protected void initTooltipsAndMisc() {

        addActor(cursorDecorator);

        tooltips = createToolTipManager();
        if (tooltips != null)
            addActor(tooltips);

        addActor(infoTooltipContainer = new SuperContainer(infoTooltip) {
            @Override
            public int getFluctuatingAlphaPeriod() {
                return 0;
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (parentAlpha == ShaderDrawer.SUPER_DRAW)
                    super.draw(batch, 1);
                else
                    ShaderDrawer.drawWithCustomShader(this, batch, null, false, false);
            }
        });
        infoTooltipContainer.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_MAP);
        Fluctuating.setAlphaFluctuationOn(true);

        addActor(confirmationPanel = ConfirmationPanel.getInstance());
        addActor(largeText = new LargeText());

        largeText.setPosition(GdxMaster.centerWidth(largeText),
                GdxMaster.centerHeight(largeText));
    }

    protected ToolTipManager createToolTipManager() {
        return new ToolTipManager(this);
    }

    protected void showTooltip(String s, LabelX tooltip, float dur) {
        showTooltip(false, s, tooltip, dur);
    }

    protected void showTooltip(boolean action, String s, LabelX tooltip, float dur) {
        showTooltip(null, action, s, tooltip, dur);
    }

    protected void showTooltip( String text) {
        showTooltip(null, false, text, infoTooltip, 0);
    }
    protected void showTooltip(VisualEnums.LABEL_STYLE style, boolean action, String s, LabelX tooltip, float dur) {

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
            ActionMasterGdx.addFadeInAndOutAction(tooltip, dur, true);
        } else {
            ActionMasterGdx.addFadeInAction(tooltip, 0.85f);
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
        ActionMasterGdx.addFadeOutAction(tooltip, dur, true);
        if (container == null)
            return;
        //        tooltip.clearActions();
        container.setFluctuateAlpha(false);

    }

    protected void showText(String s) {
        if (s == null) {
            getTextPanel().close();
            return;
        }
        getTextPanel().setText(s);
        getTextPanel().open();
    }

    public OverlayTextPanel getTextPanel() {
        if (textPanel == null) {
            textPanel = new OverlayTextPanel();
            addActor(textPanel);
            textPanel.setPosition(GdxMaster.centerWidth(textPanel),
                    GdxMaster.centerHeight(textPanel));
        }
        return textPanel;
    }

    public void textInput(Input.TextInputListener textInputListener,
                          String title, String text, String hint) {
        textInput(false, textInputListener, title, text, hint);
    }

    public void textInput(boolean script, Input.TextInputListener textInputListener, String title, String text, String hint) {
        textInputPanel = new TextInputPanel(title, text, hint, textInputListener);
        addActor(textInputPanel);
        textInputPanel.setPosition(GdxMaster.centerWidth(textInputPanel), GdxMaster.centerHeight(textInputPanel));
        textInputPanel.open();
        setKeyboardFocus(textInputPanel);

    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.ENTER) {
        }
        return super.keyDown(keyCode);
    }

    public ToolTipManager getTooltips() {
        return tooltips;
    }


    public Closable getDisplayedClosable() {
        return displayedClosable;
    }

    @Override
    public void setDisplayedClosable(Closable displayedClosable) {
        this.displayedClosable = displayedClosable;
        if (displayedClosable == null)
            setDraggedEntity(null);
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

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }

    public OverlayPanel getOverlayPanel() {
        return overlayPanel;
    }

    public void setOverlayPanel(OverlayPanel overlayPanel) {
        this.overlayPanel = overlayPanel;
    }

    public TextInputPanel getTextInputPanel() {
        return textInputPanel;
    }
}
