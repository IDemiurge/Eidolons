package main.libgdx.gui.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.TextButtonX;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
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
        TablePanel buttonPanel= new TablePanel<>();
        buttonPanel
//         .padRight(300)
         .setWidth(getWidth());
        if (isDoneSupported())
        addElement(buttonPanel).bottom().size(getWidth(), 70);
        if (isBackSupported())
            buttonPanel.addNormalSize(backButton).left() ;
        buttonPanel.addNormalSize(startButton).right() ;
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
        if (listPanel.getCurrentItem()==null )
            return true;
        if (listPanel.isBlocked() )
            return true;
        
            return false;
    }
    

    public void init() {
        listPanel.setItems(createListData());
        listener= new SelectionInputListener(this);
    }
    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (getStage()!=null )
            getStage().addListener(listener);
        else
            getStage().removeListener(listener);
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
            if (isRandom()){
                listPanel.selectRandomItem();
                WaitMaster.WAIT(400);
            } else
                return;
        }
        if (listPanel.isBlocked(listPanel.getCurrentItem())){
            return ;
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
            WaitMaster.interrupt(getWaitOperation() );
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
