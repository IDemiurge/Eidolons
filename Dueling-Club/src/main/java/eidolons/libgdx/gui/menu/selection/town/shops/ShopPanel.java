package eidolons.libgdx.gui.menu.selection.town.shops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.NoHitGroup;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryPanel;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.shop.ShopDataSource;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.entity.town.Shop;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 3/14/2018.
 */
public class ShopPanel extends ContainerPanel implements SelectableItemDisplayer {
    private final TablePanelX header;

    public ShopPanel() {
        super();
        GuiEventManager.bind(GuiEventType.UPDATE_SHOP, p ->
         update((Shop) p.get()));

        containerLabel = new LabelX("", 20);
        addActor(header = new TablePanelX<>(GdxMaster.adjustWidth(200),
         GdxMaster.adjustHeight(100)));
        header.add(containerLabel);
        header.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

       }

    protected int getDefaultHeight() {
        return (int) GdxMaster.adjustSizeBySquareRoot(700);
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustSizeBySquareRoot(1080);
    }

    @Override
    public void setItem(SelectableItemData sub) {
        update((Shop) sub.getEntity());

    }

    private void update(Shop shop) {
        Unit unit = Eidolons.getMainHero();
        setUserObject(
         new ShopDataSource(shop, unit));
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
        header.setPosition( GdxMaster.right(header)- NINE_PATCH_PADDING.SAURON.right,
         getHeight()-header.getHeight()/2);
        TextureRegion bg = TextureCache.getOrCreateR("ui/components/hq/inv/inv slots bg.png");
        containerSlotsPanel.setBackground(new TextureRegionDrawable(bg));
        containerSlotsPanel.setSize(bg.getRegionWidth(), bg.getRegionHeight());

    }

    @Override
    protected Cell initLowerTable() {
        return super.initLowerTable();//.width(getWidth()).fillX().growX().space(100);
    }

    @Override
    protected void updateUpperTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        ContainerDataSource dataSource = param.getValue();
        containerLabel.setText(dataSource.getHandler().getContainerName());
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    public void updateAct(float delta) {
        ShopDataSource dataSource = (ShopDataSource) getUserObject();
        update(dataSource.getInvDataSource(), dataSource);
    }

    protected int getInvRowCount() {
        return 6;
    }

    protected int getInvColumnCount() {
        return 4;
    }

    protected int getContainerRowCount() {
        return 8;
    }

    protected int getContainerColumnCount() {
        return 5;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void subItemClicked(SelectableItemData item, String sub) {

    }
}
