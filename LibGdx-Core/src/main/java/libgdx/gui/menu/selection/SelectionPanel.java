package libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Core;
import eidolons.game.core.Core.SCOPE;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.TablePanelX;
import libgdx.launch.MainLauncher;
import libgdx.shaders.ShaderDrawer;
import libgdx.gui.generic.btn.ButtonStyled;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Comparator;
import java.util.List;

/**
 * Created by JustMe on 11/29/2017.
 */
public abstract class SelectionPanel extends TablePanelX {
    protected ItemListPanel listPanel;
    protected SelectableItemDisplayer infoPanel;
    protected SymbolButton backButton;
    protected SelectionInputListener listener;
    protected Label title;
    protected Object data;

    SpriteAnimation backgroundSprite;
    private boolean done;


    public SelectionPanel() {
        this(null);
    }

    public SelectionPanel(Object data) {
        this.data = data;
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        backgroundSprite = initBackgroundSprite();
        listPanel = createListPanel();
        infoPanel = createInfoPanel();
        title = new Label(getTitle(), StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 20));
        listPanel.setInfoPanel(infoPanel);
        backButton = new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL, () -> cancel(true));

        row();

        TablePanel<Actor> container = new TablePanelX<>(getWidth() - listPanel.getWidth(), getHeight());

        if (isListOnTheRight()) {
            container.addNormalSize(infoPanel.getActor()).left();
            addNormalSize(container).left();
            addNormalSize(listPanel).right();
        } else {
            container.addNormalSize(infoPanel.getActor()).center();
            addNormalSize(listPanel).left();
            addNormalSize(container).right();
        }
        listPanel.addActor(title); //trick for pos
        title.pack();
        title.setPosition(GdxMaster.centerWidth(title),
                getTitlePosY());
        addActor(title);

        row();
        addElement(null).bottom().size(getWidth(), 70);

        if (isDoneSupported()) {
            infoPanel.initStartButton(getDoneText(), this::done);
        }
        if (isBackSupported()) {
            addActor(backButton);
        }


        if (isReadyToBeInitialized())
            init();

        padRight(50);
        listPanel.setZIndex(Integer.MAX_VALUE);
        title.setZIndex(Integer.MAX_VALUE);

    }

    protected SpriteAnimation initBackgroundSprite() {
        if (getBackgroundSpritePath() == null) {
            return null;
        }
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(getBackgroundSpritePath());
        sprite.setFps(20);
        sprite.setOffsetY(GdxMaster.getHeight() / 2);
        sprite.setOffsetX(GdxMaster.getWidth() / 2);
        sprite.setAlpha(getBgAlpha());
        return sprite;
    }

    protected float getBgAlpha() {
        return 1;
    }

    protected String getBackgroundSpritePath() {
        return null;
    }

    protected boolean isListOnTheRight() {
        return false;
    }

    protected float getTitlePosY() {
        return GdxMaster.getHeight() - GDX.size(200);
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
        infoPanel.setDoneDisabled(isDoneDisabled());
        if (backgroundSprite != null)
            backgroundSprite.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (backgroundSprite != null)
            backgroundSprite.draw(batch);

        if (isShadersEnabled()) {
            super.draw(batch, 1);
            return;
        }
        if (getStage() != null) {
            if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
                super.draw(batch, 1);
                return;
            }

            ShaderDrawer.drawWithCustomShader(this, batch, null);
        }
    }

    protected boolean isShadersEnabled() {
        return false;
    }

    protected boolean isDoneDisabled() {
        if (listPanel.getCurrentItem() == null)
            return true;
        return listPanel.isBlocked();

    }

    public void init() {
        List<SelectableItemData> list = createListData();
        Comparator<? super SelectableItemData> sorter = getDataSorter();
        if (sorter != null)
            list.sort(sorter);
        listPanel.setItems(list);
        listener = new SelectionInputListener(this);

        if (list.size() == 1) {
            // Eidolons.onNonGdxThread(() ->
            // {
                listPanel.select(0);
                done();
            // });
        } else if (isAutoDoneEnabled() && (Flags.isMacro()
                || ListMaster.isNotEmpty(MainLauncher.presetNumbers))) {
            listPanel.updateAct(0);
            Core.onNonGdxThread(this::tryDone);
        }
    }

    protected Comparator<? super SelectableItemData> getDataSorter() {
        return null;
    }

    @Override
    public void setStage(Stage stage) {
        if (listener != null)
            if (stage != null) {
                stage.addListener(listener);
            } else {
                if (getStage() != null)
                    getStage().removeListener(listener);
            }
        super.setStage(stage);
    }

    @Override
    public boolean remove() {
        if (getStage() != null) {
            getStage().removeListener(listener);
        }
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


    protected abstract SelectableItemDisplayer createInfoPanel();

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
        if (Core.getScope() == SCOPE.MENU)
            Core.showMainMenu();
    }

    public void tryDone() {
        if (MainLauncher.presetNumbers.isEmpty())
            if (EidolonsGame.SELECT_SCENARIO) {
                return;
            }
        if (isAutoDoneEnabled())
            if (listPanel.getCurrentItem() == null) {
                if (isRandom()) {
                    listPanel.selectRandomItem();
                } else if (!MainLauncher.presetNumbers.isEmpty()) {
                    listPanel.select(MainLauncher.presetNumbers.pop());
                } else
                    return;
            }
        done();
    }

    protected boolean isAutoDoneEnabled() {
        return true;
    }

    public void done() {
        if (listPanel.getCurrentItem() == null || listPanel.isBlocked(listPanel.getCurrentItem())) {
            return;
        }
        setDone(true);
        close();
    }

    protected boolean isRandom() {
        return false;
    }

    public void close() {
        if (listPanel.getCurrentItem() != null)
            closed(listPanel.getCurrentItem().name);
        else
            GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,                    null);
        //            closed(listPanel.getItems().getVar(0).name);

    }

    public void closed(Object selection) {
        if (listPanel.getCurrentItem() != null)
            WaitMaster.receiveInput(getWaitOperation(), selection);
        else
            WaitMaster.interrupt(getWaitOperation());
        fadeOut();
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
        GdxMaster.setDefaultCursor();
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

    public SelectableItemDisplayer getInfoPanel() {
        return infoPanel;
    }

    public ItemListPanel getListPanel() {
        return listPanel;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }
}
