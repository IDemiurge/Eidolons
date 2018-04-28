package eidolons.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Closable;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends TablePanel implements Closable {

    Image portrait;
    private Cell<Actor> takeAllButton;
    private InventorySlotsPanel inventorySlotsPanel;
    private InventorySlotsPanel containerSlotsPanel;

    public ContainerPanel() {
        initListeners();
    }

    @Override
    public void clear() {

    }

    private void initListeners() {
        clear();
        TextureRegion textureRegion = new TextureRegion(getOrCreateR(
         "UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        inventorySlotsPanel = new InventorySlotsPanel();
        containerSlotsPanel = new InventorySlotsPanel();

        portrait = new Image();
        portrait.setSize(GridMaster.CELL_W, GridMaster.CELL_H);
        addElement(portrait).top().height(GridMaster.CELL_H).width(GridMaster.CELL_W);
        row();

        addElement(inventorySlotsPanel)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        row();


        addElement(containerSlotsPanel)
         .height(340)
         .pad(20, 30, 0, 20)
         .top().expand(1, 0);
//        initListeners();

        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 30, 20, 20);


        takeAllButton = lower.addElement(new TextButton("Take All",
         StyleHolder.getDefaultTextButtonStyle()))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10).size(50, 50);

        GuiEventManager.bind(SHOW_LOOT_PANEL, (obj) -> {
            final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
            if (param == null) {
                close();
            } else {
                open();
                inventorySlotsPanel.setUserObject(param.getKey());
                containerSlotsPanel.setUserObject(param.getValue());
                if (containerSlotsPanel.getListeners().size > 0)
                    inventorySlotsPanel.addListener(containerSlotsPanel.getListeners().first());

                TextButton button = (TextButton) takeAllButton.getActor();
                button.getListeners().clear();
                final ContainerDataSource source = param.getValue();
                button.addListener(new ClickListener() {
                                       @Override
                                       public void clicked(InputEvent event, float x, float y) {
                                           source.getHandler().takeAllClicked();
                                       }
                                   }
                );
                portrait.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(
                 StringMaster.getAppendedImageFile(
                  source.getHandler().getContainerImagePath(), ContainerMaster.OPEN)));

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

    public void close() {
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
        setVisible(false);
    }

    public void open() {
        GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
        if (getStage() instanceof StageWithClosable) {
            ((StageWithClosable) getStage()).closeDisplayed();
            ((StageWithClosable) getStage()).setDisplayedClosable(this);
        }
        setVisible(true);
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
