package eidolons.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.ability.InventoryTransactionManager;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends TablePanel implements Blocking {

    Image portrait;
    private InventorySlotsPanel inventorySlotsPanel;
    private InventorySlotsPanel containerSlotsPanel;
    private Image container;
    private TextButtonX takeAllButton;
    private LabelX playerLabel;
    private LabelX containerLabel;

    public ContainerPanel() {
        initListeners();
    }

    @Override
    public void clear() {

    }
public enum ITEM_FILTERS{
        ALL,
    WEAPON,
    ARMOR,
    USABLE,
    JEWELRY,
    QUEST
}
    private void initListeners() {
        clear();
        setSize(800, 500);
        addActor(
         new Image(TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON,
         BACKGROUND_NINE_PATCH.PATTERN, 800, 500)));

        pad(NINE_PATCH_PADDING.SAURON);

        inventorySlotsPanel = new InventorySlotsPanel();
        containerSlotsPanel = new InventorySlotsPanel();

        portrait = new Image();
        container = new Image();

        TablePanelX upper = new TablePanelX<>();
        TablePanelX middle= new TablePanelX<>();
        TablePanelX lower = new TablePanelX<>();

        addElement(upper).pad(0, 30, 20, 20).row();
        addElement(middle).pad(0, 30, 20, 20).row();
        addElement(lower).pad(0, 30, 20, 20).row();

        TablePanelX upperLeft = new TablePanelX<>();
        TablePanelX upperRight = new TablePanelX<>();

        upper.add(upperLeft);
        upper.add(upperRight);
        upperLeft.add(portrait).left().left().top();
        upperLeft.add(playerLabel=new LabelX("", 20)).top();

        upperRight.add(container).left().right().top();
        upperRight.add(containerLabel=new LabelX("", 20)).top();

        TablePanelX filters = new TablePanelX<>();
        for (ITEM_FILTERS filter : ITEM_FILTERS.values()) {
            filters.add(new TextButtonX(getButtonStyle(filter),
             () -> applyFilter(filter))).row();
        }

        middle.addElement(inventorySlotsPanel).left();
        middle.addElement(filters).center();
        middle.addElement(containerSlotsPanel).right();


        takeAllButton =  new TextButtonX(
         "Take All", StyleHolder.getHqTextButtonStyle(20), ()-> takeAll()) ;
        add(takeAllButton);

        GuiEventManager.bind(SHOW_LOOT_PANEL, (obj) -> {
            final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
            if (param == null) {
                close();
            } else {
                open();

                ContainerDataSource dataSource =
                 (ContainerDataSource) getUserObject();
                setUserObject(dataSource);
                inventorySlotsPanel.setUserObject(dataSource);
                containerSlotsPanel.setUserObject(param.getValue());
                if (containerSlotsPanel.getListeners().size > 0)
                    inventorySlotsPanel.addListener(containerSlotsPanel.getListeners().first());

                portrait.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(
                 StringMaster.getAppendedImageFile(
                  dataSource.getHandler().getContainerImagePath(), ContainerMaster.OPEN)));

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
         (ContainerDataSource)containerSlotsPanel. getUserObject();
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
}
