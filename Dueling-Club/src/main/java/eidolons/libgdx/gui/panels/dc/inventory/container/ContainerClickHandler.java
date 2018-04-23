package eidolons.libgdx.gui.panels.dc.inventory.container;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_InventoryManager.OPERATIONS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandlerImpl;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerClickHandler extends InventoryClickHandlerImpl {
    private final ContainerObj container;

    public ContainerClickHandler(ContainerObj obj, Unit unit) {
        super(new HqDataMaster(unit), HqDataMaster.getHeroModel(unit));
        this.container = obj;
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        if (cellContents == null) {
            close();
            return false;
        }
        if (sim.isInventoryFull()) {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
             "Inventory is full!", sim);
            return false;
        }
        DC_HeroItemObj item = (DC_HeroItemObj) cellContents;
        pickUp(item);


        return true;
    }

    private void update() {
        dataMaster.applyModifications();
        Pair<InventoryDataSource, ContainerDataSource> param =
         new ImmutablePair<>(new InventoryDataSource(sim.getHero()),
          new ContainerDataSource(container, sim.getHero()));
        GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL, param);

    }

    public void takeAllClicked() {
        for (DC_HeroItemObj item : new ArrayList<>(container.getItems())) {
            if (sim.isInventoryFull()) {
                FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
                 "Inventory is full!", sim);
                return;
            }
            pickUp(item);
        }
        close();
    }

    private void pickUp(DC_HeroItemObj item) {
        container.getItems().remove(item);
        dataMaster.operation(sim, HQ_OPERATION.PICK_UP, item);
        update();
    }

    private void close() {
        GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL,
         null);
        dataMaster.applyModifications();
    }

    @Override
    protected OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return OPERATIONS.PICK_UP;//super.getOperation(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    @Override
    protected String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return super.getArg(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    public ContainerObj getContainer() {
        return container;
    }
}
