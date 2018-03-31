package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.dc.ButtonStyled;
import eidolons.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.TextButtonX;
import eidolons.libgdx.launch.MainLauncher;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

/**
 * Created by JustMe on 11/29/2017.
 */
public abstract class SelectionPanel extends TablePanel {
    protected ItemListPanel listPanel;
    protected ItemInfoPanel infoPanel;
    protected ButtonStyled backButton;
    protected TextButtonX startButton;
    protected SelectionInputListener listener;
    Label title;

    public SelectionPanel() {
//        setBackground(TextureCache.getOrCreateTextureRegionDrawable
//         (getBackgroundPath()));
//        debug();
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        listPanel = createListPanel();
        infoPanel = createInfoPanel();
        title = new Label(getTitle(), StyleHolder.getSizedLabelStyle(FONT.AVQ, 24));
        listPanel.setInfoPanel(infoPanel);
        backButton = new ButtonStyled(STD_BUTTON.CANCEL, () -> cancel());
        startButton = new TextButtonX(getDoneText(),
         STD_BUTTON.GAME_MENU, () -> tryDone());

        addElement(title).center();
        row();
        addNormalSize(listPanel).left();
        addNormalSize(infoPanel).right();

        row();
        TablePanel buttonPanel = new TablePanel<>();
        buttonPanel
//         .padRight(300)
         .setWidth(getWidth());
        if (isDoneSupported())
            addElement(buttonPanel).bottom().size(getWidth(), 70);
        if (isBackSupported())
            buttonPanel.addNormalSize(backButton).left();
        buttonPanel.addNormalSize(startButton).right();
        if (isReadyToBeInitialized())
            init();

        padRight(50);

    }

    protected boolean isDoneSupported() {
        return true;
    }

    protected String getTitle() {
        return "Make your Choice";
    }

    protected String getDoneText() {
        return "Done";
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        startButton.setDisabled(isDoneDisabled());
    }

    protected boolean isDoneDisabled() {
        if (listPanel.getCurrentItem() == null)
            return true;
        return listPanel.isBlocked();

    }

    public void init() {
        listPanel.setItems(createListData());
        listener = new SelectionInputListener(this);

        if (CoreEngine.isFastMode() || CoreEngine.isMacro()) {
            listPanel.updateAct(0);
            tryDone();
        }
    }

    @Override
    public void setStage(Stage stage) {
        if (stage != null) {
            stage.addListener(listener);
        } else {
            getStage().removeListener(listener);
        }
        super.setStage(stage);
    }

    @Override
    public boolean remove() {
        getStage().removeListener(listener);
        return super.remove();
    }

    protected boolean isBackSupported() {
        return true;
    }

    protected String getBackgroundPath() {
        return VISUALS.MAIN.getImgPath();
    }

    protected boolean isReadyToBeInitialized() {
        return true;
    }


    protected abstract ItemInfoPanel createInfoPanel();

    protected abstract List<SelectableItemData> createListData();

    protected abstract ItemListPanel createListPanel();

    protected void cancel() {
        closed(null);
    }

    public void tryDone() {
        if (listPanel.getCurrentItem() == null) {
            if (!MainLauncher.presetNumbers.isEmpty()) {
                listPanel.select(MainLauncher.presetNumbers.pop());
            } else if (isRandom()) {
                listPanel.selectRandomItem();
                WaitMaster.WAIT(400);
            } else
                return;
        }
        if (listPanel.isBlocked(listPanel.getCurrentItem())) {
            return;
        }
        done();
    }

    public void done() {
        close();
    }

    protected boolean isRandom() {
        return true;
    }

    public void close() {
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
         null);
        if (listPanel.getCurrentItem() != null)
            closed(listPanel.getCurrentItem().name);
        else
            closed(listPanel.getItems().get(0).name);

    }

    public void closed(Object selection) {
        if (listPanel.getCurrentItem() != null)
            WaitMaster.receiveInput(getWaitOperation(), selection);
        else
            WaitMaster.interrupt(getWaitOperation());
        setVisible(false);
    }

    public WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATIONS.SELECTION;
    }

    public void next() {
        listPanel.next();
    }

    public void previous() {
        listPanel.previous();
    }
}
