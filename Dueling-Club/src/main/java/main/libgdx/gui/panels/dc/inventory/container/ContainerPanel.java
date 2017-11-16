package main.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends  TablePanel{

    private InventorySlotsPanel inventorySlotsPanel;
    private InventorySlotsPanel containerSlotsPanel;

    public ContainerPanel( ) {

        TextureRegion textureRegion = new TextureRegion(getOrCreateR("UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        inventorySlotsPanel = new InventorySlotsPanel();
        containerSlotsPanel = new InventorySlotsPanel();

        addElement(inventorySlotsPanel)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        row();
        addElement(containerSlotsPanel)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        initListeners();
    }

    @Override
    public void clear() {

    }

    private void initListeners() {
        GuiEventManager.bind(SHOW_LOOT_PANEL, (obj) -> {
            final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
            if (param == null) {
                setVisible(false);
            } else {
                setVisible(true);
                inventorySlotsPanel. setUserObject(param.getKey());
                containerSlotsPanel. setUserObject(param.getValue());
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
