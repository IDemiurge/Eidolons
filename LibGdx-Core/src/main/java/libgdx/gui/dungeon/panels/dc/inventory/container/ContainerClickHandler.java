package libgdx.gui.dungeon.panels.dc.inventory.container;

import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.handlers.DC_InventoryManager.OPERATIONS;
import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.entity.item.vendor.GoldMaster;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryClickHandlerImpl;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.dungeon.panels.dc.inventory.shop.ShopClickHandler;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_REMAINS;
import main.content.enums.entity.HeroEnums;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;

import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerClickHandler extends InventoryClickHandlerImpl {
    protected final Obj container;
    protected String containerImagePath;
    protected Collection<HeroItem> items;
    protected CharSequence containerName;

    public ContainerClickHandler(
            String containerImagePath,
            Collection<HeroItem> items, Unit unit, Obj container) {
        this(HqDataMaster.getInstance(unit),
                containerImagePath, items, container);
    }

    public ContainerClickHandler(HqDataMaster dataMaster,
                                 String containerImagePath,
                                 Collection<HeroItem> items, Obj container) {
        super(dataMaster, dataMaster.getHeroModel());
        this.containerImagePath = containerImagePath;
        this.containerName = container.getName();
        if (container instanceof GridCell) {
            this.containerName = "Dropped Items";
            try {
                this.containerImagePath = DataManager.getType(BF_OBJ_SUB_TYPES_REMAINS.REMAINS.getName(),
                        DC_TYPE.BF_OBJ).getImagePath();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        this.items = items;
        this.container = container;
    }


    @Override
    public boolean cellClicked(VisualEnums.CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents, boolean ctrlClick) {
        if (cellContents == null) {
            return false;
        }
        HeroItem item = (HeroItem) cellContents;
        if (!GoldMaster.isGoldPack(item) && hero.isInventoryFull()) {
            FloatingTextMaster.getInstance().createFloatingText(VisualEnums.TEXT_CASES.DEFAULT,
                    "Inventory is full!", hero);
            EUtils.showInfoText("Inventory is full!");
            return false;
        }
        pickUp(item);


        return true;
    }

    public Obj getContainer() {
        return container;
    }

    protected void update() {
        dataMaster.applyModifications();
        Pair<InventoryDataSource, ContainerDataSource> param =
                new ImmutablePair<>(new InventoryDataSource(hero.getHero()),
                        new ContainerDataSource(container, hero.getHero()));
        GuiEventManager.trigger(getGuiEvent(), param);

    }

    protected EventType getGuiEvent() {
        return SHOW_LOOT_PANEL;
    }

    public void takeAllClicked() {
        for (HeroItem item : new ArrayList<>(items)) {
            if (item == null)
                continue;
            if (hero.isInventoryFull()) {
                FloatingTextMaster.getInstance().createFloatingText(VisualEnums.TEXT_CASES.DEFAULT,
                        "Inventory is full!", hero);
                EUtils.showInfoText("Inventory is full!");
                return;
            }
            items.remove(item);
            if (container instanceof GridCell) {
                item.getGame().getDroppedItemManager().pickedUp(item);
            }
            dataMaster.operation(hero, HeroEnums.HERO_OPERATION.PICK_UP, item);
        }
        takeGold();

        close();
    }

    public void takeGold() {
        if (GoldMaster.isGoldPacksOn()) {

            for (HeroItem item : items) {
                if (item == null) {
                    continue;
                }
                if (GoldMaster.isGoldPack(item)) {
                    dataMaster.operation(HeroEnums.HERO_OPERATION.PICK_UP, item);
                    return;
                }
            }
        } else {
            Integer gold = container.getIntParam(PARAMS.GOLD);
            if (gold > 0) {
                dataMaster.operation(HeroEnums.HERO_OPERATION.ADD_PARAMETER, PARAMS.GOLD, gold);
                container.setParam(PARAMS.GOLD, 0);
                update();
            }
        }
    }

    protected void pickUp(HeroItem item) {
        items.remove(item);
        if (container instanceof GridCell) {
            item.getGame().getDroppedItemManager().pickedUp(item);
        }
        dataMaster.operation(hero, HeroEnums.HERO_OPERATION.PICK_UP, item);
        Ref ref = hero.getHero().getRef().getCopy();
        ref.setObj(KEYS.ITEM, item);
        ref.setObj(KEYS.TARGET, item);
        hero.getHero().getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_ACQUIRED, ref));
        if (ListMaster.isNotEmpty(items)) {
            update();
        } else {
            close();
        }
    }

    protected void close() {
        GuiEventManager.trigger(getGuiEvent(),
                null);
        dataMaster.applyModifications();
    }

    @Override
    protected OPERATIONS getInvOperation(VisualEnums.CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, boolean ctrlClick, Entity cellContents) {
        if (this instanceof ShopClickHandler)//this is the worst quick fix ever!
            return super.getInvOperation(cell_type, clickCount, rightClick, altClick, ctrlClick, cellContents);
        return OPERATIONS.PICK_UP;
    }

    @Override
    protected String getArg(VisualEnums.CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return super.getArg(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    public String getContainerImagePath() {
        return containerImagePath;
    }

    public CharSequence getContainerName() {
        return containerName;
    }

}
