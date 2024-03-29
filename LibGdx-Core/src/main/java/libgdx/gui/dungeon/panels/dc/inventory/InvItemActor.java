package libgdx.gui.dungeon.panels.dc.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.content.PARAMS;
import eidolons.content.consts.Images;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.HeroSlotItem;
import eidolons.game.core.Core;
import eidolons.entity.item.vendor.GoldMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.UiMaster;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.NoHitImage;
import libgdx.gui.dungeon.menu.selection.town.shops.ShopPanel;
import libgdx.gui.dungeon.panels.dc.inventory.container.ContainerPanel;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryTableDataSource;
import libgdx.gui.dungeon.panels.dc.inventory.shop.ShopDataSource;
import libgdx.gui.dungeon.panels.headquarters.tabs.inv.ItemActor;
import libgdx.gui.dungeon.tooltips.SmartClickListener;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import libgdx.stage.DragManager;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

public class InvItemActor extends ItemActor {
    private GroupX goldGroup;
    private LabelX goldLabel;
    private CELL_TYPE cellType;
    private InventoryClickHandler handler;
    private float clickTimer;
    private boolean goldPack;
    private float defaultScale = 1;
    private float hoverScale = 1;
    private float preferredSize = 64;

    public InvItemActor(String customImage, float preferredSize) {
        super(null);
        this.preferredSize = preferredSize;
        image.setImage(customImage);
        addListener(listener = createListener());
        initSize();
    }

    @Override
    public String toString() {
        return model + "'s Actor";
    }

    public InvItemActor(HeroItem model, CELL_TYPE cellType, InventoryClickHandler handler) {
        super(model);
        this.cellType = cellType;
        this.handler = handler;
        if (model == null) {
            image.setImageImmediately(getEmptyImage());
            return;
        }

        goldPack = GoldMaster.isGoldPack(model);
        if (goldPack) {
            image.setImageImmediately(GoldMaster.getImageVariant(model));
        }
        goldGroup = new GroupX(true);
        NoHitImage img = new NoHitImage(TextureCache.getOrCreateR(Images.GOLD_INV_ITEM_OVERLAY));
        goldGroup.addActor(img);


        goldGroup.addActor(goldLabel = new LabelX());

        goldLabel.setPosition(GdxMaster.centerWidth(goldLabel),
         GdxMaster.centerHeight(goldLabel));
        addActor(goldGroup);

        initSize();
        goldGroup.setX(GdxMaster.right(goldGroup) + goldGroup.getWidth() / 3);
        goldGroup.setY(-goldGroup.getHeight() / 3);
        if (!goldPack)
            goldGroup.setVisible(false);

    }

    protected boolean isListenerRequired() {
        return true;
    }

    protected FadeImageContainer createBackground() {
        if (model != null) {
            switch (model.getOBJ_TYPE_ENUM()) {
                case  JEWELRY:
                case  ITEMS:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_GOLD);
                case  WEAPONS:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_STEEL);
                case ARMOR:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_STONE);
            }
        }
        return new FadeImageContainer(Images.ITEM_BACKGROUND);
    }

    protected FadeImageContainer createBackgroundOverlay(HeroItem model) {
        String path = getUnderlayPathForItem(model);
        if (path == null) {
            return null;
        }
        FadeImageContainer overlay = new FadeImageContainer(
                path
         );
        overlay.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.ITEM_BACKGROUND_OVERLAY);
        return overlay;
    }

    private String getUnderlayPathForItem(HeroItem model) {
        if (model instanceof HeroSlotItem) {
        ItemEnums.QUALITY_LEVEL qualityLevel =  ((HeroSlotItem) model).getQuality();
            switch (qualityLevel) {

                case ANCIENT:
                case OLD:
                case DAMAGED:
                case INFERIOR:
                return Images.ITEM_BACKGROUND_OVERLAY_CRACKS;
                case NORMAL:
//                    return Images.ITEM_BACKGROUND_OVERLAY_NORMAL;
                return null ;
                case SUPERIOR:
                case SUPERB:
                    return Images.ITEM_BACKGROUND_OVERLAY_LIGHT;
                case MASTERPIECE:
                    return Images.ITEM_BACKGROUND_OVERLAY_BRILLIANT;
            }
        }


        return null ;
    }

    @Override
    public boolean isOverlayOn() {
        return true;
    }

    @Override
    protected String getImagePath(HeroItem model) {
        return UiMaster.getSprite(super.getImagePath(model));
    }

    @Override
    public boolean isAutoSize() {
        return true;
    }

    private LabelStyle getGoldLabelStyle(Integer c) {
        int size = 13;
        if (c >= 1000) {
            size = 11;
        } else if (c >= 100) {
            size = 12;
        }
        return StyleHolder.getSizedColoredLabelStyle(0.1f, FONT.METAMORPH, size);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

        if (userObject instanceof InventoryTableDataSource) {
            handler = ((InventoryTableDataSource) userObject).getClickHandler();
            initListeners();
        }
        if (model == null) {
            return;
        }
        int price = 0;
        if (goldPack) {
            goldLabel.setColor(GdxColorMaster.YELLOW);
            price = model.getIntParam(GoldMaster.GOLD_VALUE);
        } else {
            if ((userObject instanceof InventoryTableDataSource)) {
                InventoryTableDataSource dataSource = (InventoryTableDataSource) userObject;
                price = dataSource.getPrice(model, cellType);
            }
            if ((userObject instanceof ShopDataSource)) {
                ShopDataSource shopDataSource = (ShopDataSource) userObject;

                Obj buyer = shopDataSource.getInvDataSource().getUnit();
                boolean sell = cellType == VisualEnums.CELL_TYPE.STASH ||
                 cellType == VisualEnums.CELL_TYPE.INVENTORY;
                if (sell)
                    buyer = shopDataSource.getShop();
                boolean canBuy = buyer.checkParam(PARAMS.GOLD, price);

                goldLabel.setColor(canBuy ? Color.GREEN : Color.RED);
                if (!canBuy) {
                    boolean canBuyInCredit =
                     sell ? shopDataSource.getShop().canSellTo(
                      model, shopDataSource.getInvDataSource().getUnit(), true) :
                      shopDataSource.getShop().canBuy(
                       model, shopDataSource.getInvDataSource().getUnit(), true);
                    goldLabel.setColor(canBuyInCredit ? Color.PURPLE : Color.RED);
                }
            } else {
                goldLabel.setColor(Color.YELLOW);
            }
        }

        goldLabel.setText(price + "");
        goldLabel.setStyle(getGoldLabelStyle(price));
        goldLabel.pack();

        goldLabel.setPosition(GdxMaster.centerWidth(goldLabel),
         GdxMaster.centerHeight(goldLabel));
    }

    private void initListeners() {
        clearListeners();
        addListener(createListener());
        if (model == null) {
            if (cellType != null)
                addListener(new ValueTooltip(
                 StringMaster.format(cellType.toString()) +
                  " slot").getController());
        } else {
            String vals = InventoryFactory.getTooltipsVals(model);
            addListener(new ValueTooltip(model.getName() + "\n" +
             vals).getController());
        }
    }

    @Override
    public void act(float delta) {
        clickTimer += delta;
        super.act(delta);
        if (model == null) {
            return;
        }
        if (!goldPack) {
            goldGroup.setVisible(cellType == VisualEnums.CELL_TYPE.CONTAINER || cellType == VisualEnums.CELL_TYPE.INVENTORY
             || cellType == VisualEnums.CELL_TYPE.STASH);
        } else {
        }
    }

    @Override
    protected String getEmptyImage() {
        if (cellType != null) {
            return cellType.getSlotImagePath();
        }
        return super.getEmptyImage();
    }

    protected void initSize() {
        image.getContent().pack();
        defaultScale = preferredSize / image.getWidth();
        hoverScale = defaultScale * getScaleFactor();
        image.setScale(defaultScale, defaultScale);
        image.getContent().pack();
        setSize(image.getWidth() * defaultScale, image.getHeight() * defaultScale);
        //        if (scaleListener == null && isScalingSupported())
        //            addListener(scaleListener = createScalingListener());
    }

    private float getScaleFactor() {
        return 1.1f;
    }

    private boolean isScalingSupported() {
        return true;
    }

    protected ClickListener createListener() {
        return new SmartClickListener(this) {
            @Override
            protected void entered() {
                if (!isScalingSupported()) {
                    return;
                }
                if (image.getScaleX() != hoverScale)
                    if (image.getActions().size < 3)
                    //                        if (defaultScale != 1)
                    {
                        if (image.getActions().size == 0)
                            ActionMasterGdx.addScaleAction(image, hoverScale, 0.5f);
                        else
                            ActionMasterGdx.addAfter(image, ActionMasterGdx.getScaleAction(hoverScale, 0.5f));
                    }
                super.entered();
            }

            @Override
            protected void exited() {
                if (!isScalingSupported()) {
                    return;
                }
                if (image.getActions().size < 3)
                    if (image.getScaleX() != defaultScale) {
                        if (image.getActions().size == 0)
                            ActionMasterGdx.addScaleAction(image, defaultScale, 0.5f);
                        else
                            ActionMasterGdx.addAfter(image, ActionMasterGdx.getScaleAction(defaultScale, 0.5f));
                    }
                super.exited();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (DragManager.isOff())
                    return;
                //              TODO trying to fix   listener-removal
                //  super.touchDragged(event, x, y, pointer);
                if (model == null) {
                    return;
                }
                if (handler.getDragged() == null) {
                    //                    if (x+y> GDX.size(100))
                    handler.singleClick(cellType, model);
                    initListeners();
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    clicked(event, x, y);
                    return false;
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (handler.getDragged() != null) {
                    Vector2 v = localToStageCoordinates(new Vector2(x, y));

                    Group panel = findPanelToDragTo(new Vector2(x, y));
                    if (panel == null) {
                        super.touchUp(event, x, y, pointer, button);
                        return;
                    }

                    v = panel.stageToLocalCoordinates(v);
                    Actor cell = panel.hit(v.x, v.y, true);

                    if (cell != null)
                        if (!(cell instanceof InvItemActor))
                            cell = GdxMaster.getFirstParentOfClass(cell, InvItemActor.class);


                    if (cell instanceof InvItemActor) {
                        ((InvItemActor) cell).getListener().clicked(event, x, y);
                        return;
                    }
                    super.touchUp(event, x, y, pointer, button);
                    handler.setDragged(null);
                } else {
                    super.touchUp(event, x, y, pointer, button);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickTimer < 0.15f)
                    return;
                clickTimer = 0;
                final int tapCount = this.getTapCount();
                final boolean isRightClicked = event.getButton() == Input.Buttons.RIGHT;
                final boolean isAltPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
                final boolean isCtrlPressed = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ||
                 Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);

                Core.onNonGdxThread(() ->
                 handler.cellClicked(cellType, tapCount, isRightClicked,
                  isAltPressed, model, isCtrlPressed));

                //                event.stop();
            }
        };
    }

    private Group findPanelToDragTo(Vector2 v) {

        Group panel = GdxMaster.getFirstParentOfClass(
         InvItemActor.this,
         ShopPanel.class);

        if (panel instanceof ShopPanel) {
            ShopPanel shop = ((ShopPanel) panel);
            Actor hit = shop.getParent().hit(v.x, v.y, true);
            if (hit != null)
                if (shop == GdxMaster.getFirstParentOfClass(
                 hit, ShopPanel.class)) {
                    v = localToAscendantCoordinates(panel, v);

                    Actor actor = shop.hit(v.x, v.y, true);
                    if (actor != null) {
                        Group container = GdxMaster.getFirstParentOfClass(actor, InventorySlotsPanel.class);
                        if (container != null) {
                            return container;
                        }
                    }
                    if (cellType == VisualEnums.CELL_TYPE.CONTAINER) {
                        return shop.getInventory();
                    } else {
                        return shop.getContainerSlotsPanel();
                    }
                } else {
                    panel = null;
                }
        }
        if (panel == null) {
            panel = GdxMaster.getFirstParentOfClass(
             InvItemActor.this,
             InventoryPanel.class);
        }
        if (panel == null) {
            panel = GdxMaster.getFirstParentOfClass(
             InvItemActor.this,
             ContainerPanel.class);
        }
        return panel;
    }

    public void setCellType(CELL_TYPE cellType) {
        this.cellType = cellType;
    }

    public void setHandler(InventoryClickHandler handler) {
        this.handler = handler;
    }


}
