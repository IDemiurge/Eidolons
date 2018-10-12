package eidolons.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends TablePanel implements Blocking {

    private InventorySlotsPanel inventorySlotsPanel;
    private InventorySlotsPanel containerSlotsPanel;
    private FadeImageContainer portrait;
    private FadeImageContainer container;
    private SmartButton takeAllButton;
    private LabelX heroLabel;
    private LabelX containerLabel;
    private ValueContainer weightText;

    public ContainerPanel() {
        initListeners();
    }

    @Override
    public void clear() {

    }

    private void initListeners() {
        clear();

        setBackground(new TextureRegionDrawable(new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON,
          BACKGROUND_NINE_PATCH.PATTERN, (int) GdxMaster.adjustSizeBySquareRoot(900),
          (int) GdxMaster.adjustSizeBySquareRoot(600)))));

        pad(NINE_PATCH_PADDING.SAURON);

        inventorySlotsPanel = new InventorySlotsPanel(4, 6);
        containerSlotsPanel = new InventorySlotsPanel(4, 6);

        TablePanelX upper = new TablePanelX<>();
        TablePanelX middle = new TablePanelX<>();
        TablePanelX lower = new TablePanelX<>();

        addElement(upper).pad(0, 30, 20, 20).row();
        addElement(middle).pad(0, 30, 20, 20).row();
        addElement(lower).pad(0, 30, 20, 20).row();

        TablePanelX upperLeft = new TablePanelX<>();
        TablePanelX upperRight = new TablePanelX<>();

        upper.add(upperLeft);
        upper.addEmpty((int) GdxMaster.adjustWidth(100), 100);
        upper.add(upperRight);

        upperLeft.add(portrait = new FadeImageContainer()).left().left().top();
        upperLeft.add(heroLabel = new LabelX("", 20)).top().right().colspan(2);

        upperRight.add(containerLabel = new LabelX("", 20)).top().left().colspan(2);
        upperRight.add(container = new FadeImageContainer()).right().top();

        TablePanelX filters = new TablePanelX<>();
        for (ITEM_FILTERS filter : ITEM_FILTERS.values()) {
            filters.add(new SmartButton(getButtonStyle(filter),
             () -> applyFilter(filter))).row();
        }

        middle.addElement(inventorySlotsPanel).left();
        middle.addElement(filters).center().width(GdxMaster.adjustWidth(100));
        middle.addElement(containerSlotsPanel).right();


//        lower.add(heroInfo);
//        ValueContainer controls = new ValueContainer(
//         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1400),
//         header,
//         "\n**Drag'n'drop is [ON]**\n" +
//          "[Right click]: unequip or drop onto the ground\n" +
//          "[Double left-click]: default equip \n" +
//          "[Alt-Click]: equip weapon in quick slot \n");
//        controls.setBackground(NinePatchFactory.getLightPanelDrawable());

        lower.add(weightText = new ValueContainer(
         StyleHolder.getSizedLabelStyle(FONT.MAIN, 1800),
         "Weight: ", "")).left().fillX().growX();

        takeAllButton = new SmartButton(
         "Take All", StyleHolder.getHqTextButtonStyle(20), () -> takeAll());
        lower.add(takeAllButton).colspan(2). right().fillX().growX();

        GuiEventManager.bind(SHOW_LOOT_PANEL, (obj) -> {
            final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
            if (param == null) {
                close();
            } else {
                open();

                ContainerDataSource dataSource = param.getValue();
                setUserObject(dataSource);
                inventorySlotsPanel.setUserObject(param.getKey());

                if (containerSlotsPanel.getListeners().size > 0)
                    inventorySlotsPanel.addListener(containerSlotsPanel.getListeners().first());

                container.setImage(StringMaster.getAppendedImageFile(
                 dataSource.getHandler().getContainerImagePath(), ContainerMaster.OPEN));
                containerLabel.setText(dataSource.getHandler().getContainerName());

                portrait.setImage(param.getKey().getUnit().getImagePath());
                heroLabel.setText(param.getKey().getUnit().getName());
                weightText.setValueText(param.getKey().getWeightInfo());
            }
        });

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }
        });
    }

    private void applyFilter(ITEM_FILTERS filter) {

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

    private STD_BUTTON getButtonStyle(ITEM_FILTERS filter) {
        return new EnumMaster<STD_BUTTON>().retrieveEnumConst(STD_BUTTON.class, "ITEM_" + filter.name());
    }

    private void takeAll() {
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


    }

    @Override
    public void updateAct(float delta) {
        clear();
        super.updateAct(delta);
    }

    @Override
    public void afterUpdateAct(float delta) {
        clear();

    }

    public enum ITEM_FILTERS {
        ALL,
        WEAPONS,
        ARMOR,
        USABLE,
        JEWELRY,
        QUEST
    }
}
