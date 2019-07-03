package eidolons.libgdx.gui.panels.dc.inventory;

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
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.menu.selection.town.shops.ShopPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import eidolons.libgdx.gui.panels.dc.inventory.shop.ShopDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.GoldMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.inv.ItemActor;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.stage.DragManager;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
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

    public InvItemActor(DC_HeroItemObj model, CELL_TYPE cellType, InventoryClickHandler handler) {
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
                case JEWELRY:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_GOLD);
                case WEAPONS:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_STEEL);
                case ARMOR:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_STONE);
                case ITEMS:
                    return new FadeImageContainer(Images.ITEM_BACKGROUND_GOLD);
            }
        }
        return new FadeImageContainer(Images.ITEM_BACKGROUND);
    }

    protected FadeImageContainer createBackgroundOverlay() {
        FadeImageContainer overlay = new FadeImageContainer(

         RandomWizard.random() ?
          Images.ITEM_BACKGROUND_OVERLAY_LIGHT2 :
          Images.ITEM_BACKGROUND_OVERLAY_LIGHT);
        overlay.setAlphaTemplate(Fluctuating.ALPHA_TEMPLATE.ITEM_BACKGROUND_OVERLAY);
        return overlay;
    }

    @Override
    public boolean isOverlayOn() {
        return true;
    }

    @Override
    protected String getImagePath(DC_HeroItemObj model) {
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
                boolean sell = cellType == CELL_TYPE.STASH ||
                 cellType == CELL_TYPE.INVENTORY;
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
                 StringMaster.getWellFormattedString(cellType.toString()) +
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
            goldGroup.setVisible(cellType == CELL_TYPE.CONTAINER || cellType == CELL_TYPE.INVENTORY
             || cellType == CELL_TYPE.STASH);
        } else {
            return;
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
                            ActionMaster.addScaleAction(image, hoverScale, 0.5f);
                        else
                            ActionMaster.addAfter(image, ActionMaster.getScaleAction(hoverScale, 0.5f));
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
                            ActionMaster.addScaleAction(image, defaultScale, 0.5f);
                        else
                            ActionMaster.addAfter(image, ActionMaster.getScaleAction(defaultScale, 0.5f));
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

                Eidolons.onNonGdxThread(() ->
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
                    if (cellType == CELL_TYPE.CONTAINER) {
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

    public void setCellType(InventoryClickHandler.CELL_TYPE cellType) {
        this.cellType = cellType;
    }

    public void setHandler(InventoryClickHandler handler) {
        this.handler = handler;
    }


}
