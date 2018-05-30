package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.shaders.ShaderMaster;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
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
    protected TextButtonX backButton;
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
        title = new Label(getTitle(), StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 20));
        listPanel.setInfoPanel(infoPanel);
        backButton = new TextButtonX(STD_BUTTON.CANCEL, () -> cancel());
        startButton = new TextButtonX(getDoneText(),STD_BUTTON.GAME_MENU , () -> tryDone());

        listPanel.addActor(title); //trick for pos
        title.pack();
        title.setPosition(GdxMaster.centerWidth(title),
         GdxMaster.getHeight()- GDX.size(200));
        addActor(title);
        row();
        addNormalSize(listPanel).left();
        addNormalSize(infoPanel).right();

        row();
        addElement(null ).bottom().size(getWidth(), 70);

        if (isDoneSupported()) {
            addActor(startButton);
            startButton.setPosition(GdxMaster.centerWidth(startButton)-40,
             (40+ GdxMaster.getHeight() / 25));
        }
        if (isBackSupported()) {
            addActor(backButton);
        }


        if (isReadyToBeInitialized())
            init();

        padRight(50);
        title.setZIndex(Integer.MAX_VALUE);

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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getStage()!=null )
        {
            if (parentAlpha== ShaderMaster.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }

            ShaderMaster.drawWithCustomShader(this, batch, null);
        }
    }

    protected boolean isDoneDisabled() {
        if (listPanel.getCurrentItem() == null)
            return true;
        return listPanel.isBlocked();

    }

    public void init() {
        listPanel.setItems(createListData());
        listener = new SelectionInputListener(this);

        startButton.setY(GdxMaster.getHeight()/2- infoPanel.getHeight()/2+80);
        if ( CoreEngine.isMacro()
         || ListMaster.isNotEmpty(MainLauncher.presetNumbers)) {
            listPanel.updateAct(0);
            tryDone();
        }
    }

    @Override
    public void setStage(Stage stage) {
        if (stage != null) {
            stage.addListener(listener);
        } else {
            if (getStage()!=null )
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

    public void cancel(boolean manual) {
        listPanel.deselect();
        closed(null);
        if (manual)
            close();
//        back();
    }

    public void cancel() {
        cancel(true);
    }
    private void back() {
        if (Eidolons.getScope() == SCOPE.MENU)
            Eidolons.showMainMenu();
    }

    public void tryDone() {
        if (listPanel.getCurrentItem() == null) {
            if (!MainLauncher.presetNumbers.isEmpty()) {
                listPanel.select(MainLauncher.presetNumbers.pop());
            } else if (isRandom()) {
                listPanel.selectRandomItem();
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
//        else
//            closed(listPanel.getItems().get(0).name);

    }

    public void closed(Object selection) {
        if (listPanel.getCurrentItem() != null)
            WaitMaster.receiveInput(getWaitOperation(), selection);
        else
            WaitMaster.interrupt(getWaitOperation());
        fadeOut();
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
