package libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.content.consts.VisualEnums;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.TiledNinePatchGenerator;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.AdjustingVerticalGroup;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.TablePanelX;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.stage.Blocking;
import libgdx.stage.StageWithClosable;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import eidolons.system.audio.DC_SoundMaster;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends TablePanel implements Blocking {

    protected TablePanel inventory;
    protected InventorySlotsPanel containerSlotsPanel;
    protected FadeImageContainer portrait;
    protected FadeImageContainer container;
    protected SmartTextButton mainButton;
    protected LabelX heroLabel;
    protected LabelX containerLabel;
    protected ValueContainer weightLabel;
    protected ValueContainer goldLabel;
    protected ValueContainer weightLabel2;
    protected ValueContainer goldLabel2;
    protected TablePanelX lowerLeft;
    protected TablePanelX lowerRight;

    public ContainerPanel() {
        init();
    }

    protected void init() {
        setBackground(new TextureRegionDrawable(new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.SAURON,
          TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.PATTERN, getDefaultWidth(), getDefaultHeight())
        )));

        pad(TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON);

        inventory = createInventory();
        containerSlotsPanel = createContainerSlots();

        initUpperTable().row();
        initMiddleTable().row();
        initLowerTable().row();

        if (getGuiEvent() != null)
            GuiEventManager.bind(getGuiEvent(), (obj) -> {

                final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
                if (param == null) {
                    close();
                } else {
                    open();
                    update(param.getKey(), param.getValue());
                }
            });

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                //                event.stop();
                return true;
            }
        });
    }

    @Override
    public void open() {
        getStageWithClosable().openClosable(this);
        try {
            DC_Game.game.getLoop().setPaused(true, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    protected InventorySlotsPanel createContainerSlots() {
        return new InventorySlotsPanel(getContainerRowCount(),
         getContainerColumnCount()) {
            @Override
            protected CELL_TYPE getCellType() {
                return VisualEnums.CELL_TYPE.CONTAINER;
            }
        };
    }

    public TablePanel getInventory() {
        return inventory;
    }

    public InventorySlotsPanel getContainerSlotsPanel() {
        return containerSlotsPanel;
    }

    protected TablePanel createInventory() {
        return new InventorySlotsPanel(getInvRowCount(), getInvColumnCount());
    }

    @Override
    public void clear() {

    }

    protected int getInvRowCount() {
        return 4;
    }

    protected int getInvColumnCount() {
        return 6;
    }

    protected int getContainerRowCount() {
        return 4;
    }

    protected int getContainerColumnCount() {
        return 6;
    }

    protected int getDefaultHeight() {
        return (int) GdxMaster.adjustSizeBySquareRoot(600);
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustSizeBySquareRoot(900);
    }


    protected Cell<Table> initLowerTable() {
        TablePanelX lower = new TablePanelX<>();
       lowerLeft = new TablePanelX<>();
       lowerRight = new TablePanelX<>();

        lowerLeft.add(weightLabel = new ValueContainer(
         TextureCache.getOrCreateR(Images.WEIGHT), "")).left().fillX().growX()
         .row();
        weightLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));
        lowerLeft.add(goldLabel = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_GOLD), "")).left().fillX().growX();
        goldLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));


        lowerRight.add(weightLabel2 = new ValueContainer(
         TextureCache.getOrCreateR(Images.WEIGHT), "")).left().fillX().growX()
         .row();
        weightLabel2.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));
        lowerRight.add(goldLabel2 = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_GOLD), "")).left().fillX().growX();
        goldLabel2.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));

        goldLabel2.addListener(new SmartClickListener(goldLabel2) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                takeGold();
            }
        });

        lower.add(lowerLeft).left();
        if (!isButtonRequired())
            lower.add(new AdjustingVerticalGroup(400, 0.5f)).growX();
        lower.add(lowerRight).right();
        if (isButtonRequired()) {
            lower.row();
            mainButton = new SmartTextButton(
             getButtonText(), StyleHolder.getHqTextButtonStyle(20), this::mainButton, ButtonStyled.STD_BUTTON.MENU);
            lower.add(mainButton).colspan(2).right().fillX().growX();
        }
        return addElement(lower).pad(0, 30, 20, 20);
    }

    protected Cell initMiddleTable() {
        TablePanelX middle = new TablePanelX<>();

        TablePanelX filters = new TablePanelX<>();
        for (VisualEnums.ITEM_FILTERS filter : VisualEnums.ITEM_FILTERS.values()) {
            filters.add(new SymbolButton(getButtonStyle(filter),
             () -> {
                 EUtils.showInfoText("Filters are under construction...");
                 //                 try {
                 //                     applyFilter(filter);
                 //                 } catch (Exception e) {
                 //                     main.system.ExceptionMaster.printStackTrace(e);
                 //                 }
             })).row();
            filters.addListener(new ValueTooltip("Under construction...").getController());
        }

        middle.addElement(inventory).left();
        middle.addElement(filters).center().width(GdxMaster.adjustWidth(100));
        middle.addElement(containerSlotsPanel).right();

        return addElement(middle).pad(0, 30, 20, 20);
    }

    protected Cell initUpperTable() {
        TablePanelX upper = new TablePanelX<>();
        TablePanelX upperLeft = new TablePanelX<>();
        TablePanelX upperRight = new TablePanelX<>();

        upper.add(upperLeft);
        upper.addEmpty((int) GdxMaster.adjustWidth(100), 100);
        upper.add(upperRight);

        upperLeft.add(portrait = new FadeImageContainer()).left().left().top();
        upperLeft.add(heroLabel = new LabelX("", 20)).top().right().colspan(2);

        upperRight.add(containerLabel = new LabelX("", 20)).top().left().colspan(2);
        upperRight.add(container = new FadeImageContainer()).right().top();

        return addElement(upper).pad(0, 30, 20, 20);
    }

    protected boolean isButtonRequired() {
        return true;
    }

    protected void mainButton() {
        takeAll();
    }

    protected void update(InventoryDataSource invData, ContainerDataSource containerData) {
        Pair<InventoryDataSource, ContainerDataSource> param = new ImmutablePair<>(invData, containerData);
        setUserObject(containerData);
        inventory.setUserObject(invData);

        if (containerSlotsPanel.getListeners().size > 0)
            inventory.addListener(containerSlotsPanel.getListeners().first());

        updateUpperTable(param);
        updateLowerTable(param);
    }

    protected void updateUpperTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        ContainerDataSource dataSource = param.getValue();
        container.setImage(StringMaster.getAppendedImageFile(
         dataSource.getHandler().getContainerImagePath(), ContainerMaster.OPEN));
        containerLabel.setText(dataSource.getHandler().getContainerName());
        portrait.setImage(param.getKey().getUnit().getImagePath());
        heroLabel.setText(param.getKey().getUnit().getName());
    }

    protected void updateLowerTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        weightLabel.setImage(param.getKey().isOverburdened() ?
         Images.WEIGHT_BURDENED
         : Images.WEIGHT);
        weightLabel.setValueText(param.getKey().getWeightInfo());
        goldLabel.setValueText(param.getKey().getGoldInfo());
        weightLabel2.setValueText(param.getValue().getWeightInfo());
        goldLabel2.setValueText(param.getValue().getGoldInfo());
    }

    protected String getButtonText() {
        return "Take All";
    }

    protected EventType getGuiEvent() {
        return SHOW_LOOT_PANEL;
    }

    protected void applyFilter(VisualEnums.ITEM_FILTERS filter) {

        InventoryDataSource dataSource =
         (InventoryDataSource) getUserObject();
        ContainerDataSource dataSource2 = (ContainerDataSource)
         containerSlotsPanel.getUserObject();
        final Pair<InventoryDataSource, ContainerDataSource> param =
         new ImmutablePair<>(
          dataSource, dataSource2);

        dataSource.setFilter(filter);
        dataSource2.setFilter(filter);

        setUserObject(dataSource);
    }

    protected ButtonStyled.STD_BUTTON getButtonStyle(VisualEnums.ITEM_FILTERS filter) {
        return new EnumMaster<ButtonStyled.STD_BUTTON>().retrieveEnumConst(ButtonStyled.STD_BUTTON.class, "ITEM_" + filter.name());
    }

    protected void takeGold() {
        ContainerDataSource dataSource =
         (ContainerDataSource) containerSlotsPanel.getUserObject();
        dataSource.getHandler().takeGold();
    }

    protected void takeAll() {
        ContainerDataSource dataSource =
         (ContainerDataSource) containerSlotsPanel.getUserObject();
        dataSource.getHandler().takeAllClicked();
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) super.getStage();
    }

    public void close() {
        getStageWithClosable().closeClosable(this);
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__TAB);

    }


}
