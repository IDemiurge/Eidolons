package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.menu.selection.town.shops.ShopPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import eidolons.libgdx.gui.panels.dc.inventory.shop.ShopDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.GoldMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.inv.ItemActor;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.system.graphics.FontMaster.FONT;

public class InvItemActor extends ItemActor {
    private GroupX goldGroup;
    private LabelX goldLabel;
    private CELL_TYPE cellType;
    private InventoryClickHandler handler;
    private float clickTimer;
    private boolean goldPack;



    public InvItemActor(String customImage) {
        super(null);
        image.setImage(customImage);
        addListener(listener=createListener());
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
            image.setImage(GoldMaster.getImageVariant(model));
        }
        goldGroup = new GroupX(true);
        NoHitImage img = new NoHitImage(TextureCache.getOrCreateR(Images.GOLD_INV_ITEM_OVERLAY));
        goldGroup.addActor(img);


        goldGroup.addActor(goldLabel = new LabelX());

        goldLabel.setPosition(GdxMaster.centerWidth(goldLabel),
         GdxMaster.centerHeight(goldLabel));
        addActor(goldGroup);

        goldGroup.setX(GdxMaster.right(goldGroup) + goldGroup.getWidth() / 3);
        goldGroup.setY(-goldGroup.getHeight() / 3);
        goldGroup.setVisible(false);
    }
    protected boolean isListenerRequired() {
        return true;
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
        if (model == null) {
            return;
        }
        if (!(userObject instanceof ShopDataSource)) {
            return;
        }
        ShopDataSource dataSource = (ShopDataSource) userObject;
        handler= dataSource.getHandler();
        int price =goldPack? model.getIntParam(PARAMS.GOLD_COST) : dataSource.getPrice(model, cellType);
        goldLabel.setText(price + "");
        goldLabel.setStyle(getGoldLabelStyle(price));

        if (goldPack) {

            goldLabel.setColor(GdxColorMaster.YELLOW);
        } else {
            Obj buyer = dataSource.getInvDataSource().getUnit();
            if (cellType == CELL_TYPE.STASH ||
             cellType == CELL_TYPE.INVENTORY)
                buyer = dataSource.getShop();
            boolean canBuy = buyer.checkParam(PARAMS.GOLD, price);
            goldLabel.setColor(canBuy ? Color.GREEN : Color.RED);
            goldLabel.pack();
        }


        goldLabel.setPosition(GdxMaster.centerWidth(goldLabel),
         GdxMaster.centerHeight(goldLabel));
    }

    @Override
    public void act(float delta) {
        clickTimer+=delta;
        super.act(delta);
        if (model == null) {
            return;
        }
        if (!goldPack)
        goldGroup.setVisible(cellType == CELL_TYPE.CONTAINER || cellType == CELL_TYPE.INVENTORY
         || cellType == CELL_TYPE.STASH);
    }

    @Override
    protected String getEmptyImage() {
        if (cellType != null) {
            return cellType.getSlotImagePath();
        }
        return super.getEmptyImage();
    }

    protected ClickListener createListener() {
        return new SmartClickListener(this) {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if (model == null) {
                    return;
                }
                if (handler.getDragged() == null)
                    handler.singleClick(cellType, model);
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
                if (clickTimer < 0.05f)
                    return;
                final int tapCount = this.getTapCount();
                final boolean isRightClicked = event.getButton() == Input.Buttons.RIGHT;
                final boolean isAltPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);

                Eidolons.onNonGdxThread(() ->
                 handler.cellClicked(cellType, tapCount, isRightClicked,
                  isAltPressed, model));
                clickTimer = 0;
                event.stop();
            }
        };
    }

    private Group findPanelToDragTo(Vector2 v) {

        Group panel = GdxMaster.getFirstParentOfClass(
         InvItemActor.this,
         ShopPanel.class);
        v = localToAscendantCoordinates(panel, v);

        if (panel instanceof ShopPanel) {
            ShopPanel shop = ((ShopPanel) panel);

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
