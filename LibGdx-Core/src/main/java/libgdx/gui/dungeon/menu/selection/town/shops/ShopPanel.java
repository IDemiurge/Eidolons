package libgdx.gui.dungeon.menu.selection.town.shops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.HideDecorator;
import libgdx.gui.LabelX;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.NoHitGroup;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.dungeon.menu.selection.SelectableItemDisplayer;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.dc.inventory.InvItemActor;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryPanel;
import libgdx.gui.dungeon.panels.dc.inventory.InventorySlotsPanel;
import libgdx.gui.dungeon.panels.dc.inventory.container.ContainerDataSource;
import libgdx.gui.dungeon.panels.dc.inventory.container.ContainerPanel;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.dungeon.panels.dc.inventory.shop.ShopClickHandler;
import libgdx.gui.dungeon.panels.dc.inventory.shop.ShopDataSource;
import libgdx.gui.dungeon.panels.headquarters.tabs.inv.StashPanel;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import libgdx.stage.DragManager;
import eidolons.content.consts.Images;
import libgdx.assets.texture.TextureCache;
import libgdx.gui.generic.btn.ButtonStyled;
import eidolons.game.battlecraft.logic.meta.universal.shop.Shop;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster.FONT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 3/14/2018.
 */
public class ShopPanel extends ContainerPanel implements SelectableItemDisplayer {
    public static final int COLUMNS_DEFAULT = 5;
    public static final int ROWS_DEFAULT = 8;
    private final TablePanelX header;
    private final StashPanel stashPanel;
    private final SymbolButton help;
    private final GroupX stashContainer;
    private boolean stashOpen;
    private ValueContainer debtLabel;

    public ShopPanel() {
        super();
        GuiEventManager.bind(GuiEventType.UPDATE_SHOP, p ->
         update((Shop) p.get()));

        containerLabel = new LabelX("", 20);
        addActor(header = new TablePanelX<>(GdxMaster.adjustWidth(200),
         GdxMaster.adjustHeight(100)));
        header.add(containerLabel);
        header.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        stashPanel = new StashPanel();

        addActor(stashContainer = HideDecorator.decorate(true, ButtonStyled.STD_BUTTON.CHEST, DIRECTION.UP_LEFT,
         stashPanel, () -> {
             stashOpen = !stashOpen;
             ShopClickHandler.setStashOpen(stashOpen);
         }, new Vector2(-50, 52)));

        String header = "Shop controls:";
        String on = DragManager.isOff() ? "OFF" : "ON";
        String info = "\n**Drag'n'drop is [" +
         on +
         "]**\n" +
         "[Right click]: unequip, buy or sell\n" +
         "[Double left-click]: default equip/unequip \n" +
         "[Alt-Click]: equip weapon in quick slot \n" +
         "[Ctrl-Click]: sell or unequip \n";
        if (true) {
            help = new SymbolButton(ButtonStyled.STD_BUTTON.HELP, () -> {
                EUtils.onConfirm(
                 info, false, () -> {
                 });
            });
            addActor(help);
            help.setPosition(GDX.centerWidth(help), 50);
        } else {
            ValueContainer controls = new ValueContainer(
             StyleHolder.getSizedLabelStyle(FONT.MAIN, 1400),
             header,
             info);
            GroupX container = HideDecorator.decorate(true, DIRECTION.UP_RIGHT, controls);
            addActor(container);
            container.setPosition(GDX.centerWidth(container), 100);
        }

    }

    public void update() {
        setUserObject(getUserObject());
    }

    protected int getDefaultHeight() {
        return (int) GdxMaster.adjustSizeBySquareRoot(700);
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustSizeBySquareRoot(1080);
    }

    @Override
    public void setItem(SelectableItemData sub) {
        Shop shop = (Shop) sub.getEntity();
        Unit unit = Core.getMainHero();
        update(shop);
        Core.onThisOrNonGdxThread(() ->
         shop.enter(unit));
    }

    private void update(Shop shop) {
        if (shop == null) {
            shop = getUserObject().getShop();
        }
        Unit unit = Core.getMainHero();
        setUserObject(
         new ShopDataSource(shop, unit));
    }


    protected InventorySlotsPanel createContainerSlots() {
        return new ShopSlotsPanel(getContainerRowCount(),
         getContainerColumnCount());
    }

    protected TablePanel createInventory() {
        inventory = new InventoryPanel();
        inventory.setBackground((Drawable) null);
        return inventory;
    }

    protected boolean isButtonRequired() {
        return false;
    }

    @Override
    protected Cell initUpperTable() {
        return addElement(new NoHitGroup());
        //        .colspan(2)

    }

    @Override
    public void layout() {
        super.layout();

        header.setPosition(
         containerSlotsPanel.getX() + (containerSlotsPanel.getWidth() - header.getWidth()) + 11,
         //         GdxMaster.right(header) - NINE_PATCH_PADDING.SAURON.right*3+5,
         containerSlotsPanel.getY() + containerSlotsPanel.getHeight() + 10
         //         getHeight() - header.getHeight() / 2- Math.max(1130-GdxMaster.getHeight(), 45 )
        );


        TextureRegion bg = TextureCache.getOrCreateR("ui/components/hq/inv/inv slots bg.png");
        containerSlotsPanel.setBackground(new TextureRegionDrawable(bg));
        containerSlotsPanel.setSize(bg.getRegionWidth(), bg.getRegionHeight());

        help.setPosition(GDX.centerWidth(help) + 100, 50);
        if (stashContainer != null) {
            stashContainer.setY(header.getY() - stashContainer.getHeight() - 45);
            stashContainer.setX(GdxMaster.centerWidth(stashContainer) - 245);
            //            stashPanel.setY(header.getY() - stashPanel.getHeight() - 150);
            //            stashPanel.setX(GdxMaster.centerWidth(stashPanel) - 300);
            //            stashBtn.setPosition(stashPanel.getX()- stashPanel.getWidth()/2 + stashBtn.getWidth(),
            //             stashPanel.getY() - stashPanel.getHeight()/2 - stashBtn.getHeight());
        }
    }

    @Override
    protected Cell<Table> initLowerTable() {
        Cell<Table> table = super.initLowerTable();
        weightLabel2.setImage(Images.SHOP_PRICES);
        weightLabel2.overrideImageSize(51, 40);
        //.width(getWidth()).fillX().growX().space(100);
        TablePanelX<Actor> rightMost = new TablePanelX<>();

        debtLabel = new ValueContainer("Debt:", "");
        rightMost.add(debtLabel);
        rightMost.row();
        SymbolButton repairButton;
        rightMost.add(repairButton =
         new SymbolButton(ButtonStyled.STD_BUTTON.REPAIR, () -> getUserObject().getHandler().askRepair()));
        //selective repair => hammer cursor!
        table.getActor().add(rightMost);
        return table;
    }

    @Override
    protected void updateUpperTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        ContainerDataSource dataSource = param.getValue();
        containerLabel.setText(dataSource.getHandler().getContainerName());
    }

    @Override
    public ShopDataSource getUserObject() {
        return (ShopDataSource) super.getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject != null)         if (!(userObject instanceof ShopDataSource)) {
            main.system.auxiliary.log.LogMaster.log(1,this+ " Wtf are you doing with tis shop " + userObject);
            return;
        }
        super.setUserObject(userObject);
        stashPanel.setUserObject(userObject);
    }

    @Override
    public void updateAct(float delta) {
        ShopDataSource dataSource = getUserObject();
        update(dataSource.getInvDataSource(), dataSource);
    }

    @Override
    protected void update(InventoryDataSource invData, ContainerDataSource containerData) {
        Pair<InventoryDataSource, ContainerDataSource> param = new ImmutablePair<>(invData, containerData);
        setUserObject(containerData);
        //        inventory.setUserObject(invData); we don't do that here...

        if (containerSlotsPanel.getListeners().size > 0)
            inventory.addListener(containerSlotsPanel.getListeners().first());

        updateUpperTable(param);
        updateLowerTable(param);

        debtLabel.setValueText(getUserObject().getDebtInfo());
        debtLabel.clearListeners();
        debtLabel.addListener(new ValueTooltip(getUserObject().getDebtTooltip()).getController());
    }

    @Override
    protected void updateLowerTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        weightLabel.setImage(param.getKey().isOverburdened() ?
         Images.WEIGHT_BURDENED
         : Images.WEIGHT);
        weightLabel.setValueText(param.getKey().getWeightInfo());
        goldLabel.setValueText(param.getKey().getGoldInfo());

        weightLabel2.setValueText(param.getValue().getPricesInfo());
        weightLabel2.clearListeners();
        weightLabel2.addListener(new ValueTooltip(
         "You are charged " + weightLabel2.getValueText() + " of the price").getController());
        weightLabel2.setColor(param.getValue().getPricesColor());
        goldLabel2.setValueText(param.getValue().getGoldInfo());


    }

    protected int getInvRowCount() {
        return 6;
    }

    protected int getInvColumnCount() {
        return 4;
    }

    protected int getContainerRowCount() {
        return ROWS_DEFAULT;
    }

    protected int getContainerColumnCount() {
        return COLUMNS_DEFAULT;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void subItemClicked(SelectableItemData item, String sub) {

    }

    @Override
    public void setDoneDisabled(boolean doneDisabled) {

    }

    @Override
    public void initStartButton(String doneText, Runnable o) {

    }


    public class ShopSlotsPanel extends InventorySlotsPanel {
        public ShopSlotsPanel(int rows, int cols) {
            super(rows, cols);
        }

        @Override
        protected boolean isScrolled() {
            return Shop.isUnlimitedSize();
        }

        @Override
        protected VisualEnums.CELL_TYPE getCellType() {
            return VisualEnums.CELL_TYPE.CONTAINER;
        }

        @Override
        protected List<InvItemActor> getSlotActors() {
            return ShopPanel.this.getUserObject().getShopSlots();
        }
    }
}
