package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.BlockableGroup;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.system.controls.GlobalController;
import main.game.bf.Coordinates;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

public class GridCell extends BlockableGroup implements Borderable {
    protected static boolean spriteCacheOn;
    protected Image backImage;
    protected Image overlayTexture;
    protected TextureRegion backTexture;
    //    protected TextureRegion overlay;
    protected float overlayRotation;
    protected Image border = null;
    protected int gridX;
    protected int gridY;
    protected TextureRegion borderTexture;
    protected Actor cordsText;
    protected Label infoText;

    FadeImageContainer overlay;
    private boolean voidAnimHappened;

    // some creatures can walk there?

    /**
     * so we do create cells, but hide them... fade in
     * <p>
     * check if void - via prop
     */

    public GridCell(TextureRegion backTexture, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.gridX = gridX;
        this.gridY = gridY;
        setTransform(false);
    }


    public static void setSpriteCacheOn(boolean spriteCacheOn) {
        GridCell.spriteCacheOn = spriteCacheOn;
    }

    public Image getBackImage() {
        return backImage;
    }

    public TextureRegion getBackTexture() {
        return backTexture;
    }

    public void setVoid(boolean VOID, boolean animated) {
        if (animated) {
            if (VOID) {
                ActionMaster.addFadeOutAction(backImage, 0.5f, false);
            } else {
                ActionMaster.addFadeInAction(backImage, 0.5f);
            }
        } else {
            backImage.setVisible(!VOID);
        }
    }

    public GridCell init() {
        backImage = new Image(backTexture);
        backImage.getColor().a=getCellImgAlpha();

        backImage.setFillParent(true);
        addActor(backImage);
        //        addActor(overlay = new SpriteX());
        addActor(overlayTexture = new Image());
        setSize(GridMaster.CELL_W, GridMaster.CELL_H);

        cordsText = new Label(getGridX() + ":" + getGridY(),
                StyleHolder.getDebugLabelStyleLarge());
        TablePanelX<Actor> cordsTextTable = new TablePanelX<>(50, 30);
        cordsTextTable.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        cordsTextTable.add(cordsText).center();
        addActor(cordsTextTable);
        cordsTextTable.setTouchable(Touchable.disabled);
        cordsText.setTouchable(Touchable.disabled);
        cordsTextTable.setPosition(getWidth() / 2 - cordsText.getWidth() / 2,
                getHeight() / 2 - cordsText.getHeight() / 2);
        cordsText = cordsTextTable;
        cordsText.setVisible(false);
        addListener(
                createListener());

        infoText = new Label("", StyleHolder.getDebugLabelStyle());
        //        addActor(infoText); NOW VIA OVERLAYS

        return this;
    }

    public float getCellImgAlpha() {
        return      0.8f   ;
        //TODO derive alpha from somewhere
    }

    @Override
    public void clearListeners() {
        super.clearListeners();
    }

    @Override
    public boolean removeListener(EventListener listener) {
        return super.removeListener(listener);
    }

    protected EventListener createListener() {

        return new BattleClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                GlobalController.cellClicked(event, x, y);
                if (getTapCount() > 1) {
                    if (!isAlt()) {
                        if (!DefaultActionHandler.
                                leftClickCell(false, isControl(), getGridX(), getGridY()))
                            DefaultActionHandler.
                                    leftClickCell(true, isControl(), getGridX(), getGridY());
                    }
                }
            }

            @Override
            public boolean handle(Event e) {
                return super.handle(e);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                DC_Obj dc_cell = Eidolons.gameMaster.getCellByCoordinate(Coordinates.get(getGridX(), getGridY()));
                if (button == Input.Buttons.RIGHT && !event.isHandled()) {
                    event.handle();
                    GuiEventManager.trigger(CREATE_RADIAL_MENU, dc_cell);
                }

                super.touchUp(event, x, y, pointer, button);
                if (button == Input.Buttons.LEFT) {
                    event.handle();
                    if (isEmpty())
                        if (isAlt() || isShift() || isControl()
                            //|| ExplorationMaster.isExplorationOn()
                        )
                            try {
                                if (DefaultActionHandler.
                                        leftClickCell(isShift(), isControl(), getGridX(), getGridY()))
                                    return;
                            } catch (Exception e) {
                                ExceptionMaster.printStackTrace(e);
                            }
                    GuiEventManager.trigger(TARGET_SELECTION, GridCell.this);

                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
                //                super.touchUp(event, x, y, pointer, button);
            }
        };
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (EidolonsGame.BOSS_FIGHT) {
            super.draw(batch, 1);
            return;
        }
        if (!isWithinCamera()) {
            return;
        }

        if (!isShadersSupported() || parentAlpha == ShaderDrawer.SUPER_DRAW
        ) {
            super.draw(batch, 1);
        } else {
            ShaderDrawer.drawWithCustomShader(this,
                    batch,
                    !getUserObject().isPlayerHasSeen() ?
                            DarkShader.getDarkShader()
                            : null, true);
        }

    }

    protected boolean isShadersSupported() {
        return true;
    }

    protected boolean isWithinCamera() {
        return DungeonScreen.getInstance().controller.isWithinCamera
                (this);
    }

    @Override
    public DC_Cell getUserObject() {
        return (DC_Cell) super.getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        boolean propagate = false;
        if (getUserObject() == null) {
            propagate = true;
        }
        super.setUserObject(userObject);
        if (propagate)
            getChildren().forEach(ch -> ch.setUserObject(userObject));
    }

    @Override
    public void act(float delta) {
        //        if (!DungeonScreen.getInstance().controller.isWithinCamera((this))
        //         ) {
        //            return;
        //        }
        super.act(delta);
        if (isCoordinatesShown()) {
            //            DC_Cell cell = getUserObject();
            //            cordsText.setText(
            //                    ContainerUtils.build(getGridX(), ":", getGridY())
            //                            + (GammaMaster.DEBUG_MODE ? //TODO into method
            //                            "\n gamma=" + DC_Game.game.getVisionMaster().getGammaMaster().
            //                                    getGammaForCell(getGridX(), getGridY())
            //                                    + "\n illumination="
            //                                    + cell.getIntParam(PARAMS.ILLUMINATION)
            //                            : "")
            //            );
            cordsText.setVisible(true);
        } else {
            if (cordsText.isVisible()) {
                cordsText.setVisible(false);
            }
        }
    }

    protected boolean isCoordinatesShown() {
        return DC_Game.game.getVisionMaster().isVisionDebugMode();
    }

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        if (border != null) {
            removeActor(border);
        }

        if (texture == null) {
            border = null;
            borderTexture = null;
        } else {
            addActor(border = new Image(texture));
            borderTexture = texture;
            updateBorderSize();
        }
    }

    protected void updateBorderSize() {
        border.setX(-4);
        border.setY(-4);
        border.setHeight(getWidth() - 8);
        border.setWidth(getHeight() - 8);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    @Override
    public boolean isTeamColorBorder() {
        return false;
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {

    }

    @Override
    public Color getTeamColor() {
        return null;
    }

    @Override
    public void setTeamColor(Color teamColor) {

    }

    public void setOverlayRotation(float overlayRotation) {
        this.overlayRotation = overlayRotation;
        overlayTexture.setOrigin(64, 64);
        ActionMaster.addRotateByAction(overlayTexture, overlayTexture.getRotation(), overlayRotation);
    }

    public void setOverlayTexture(TextureRegion overlay) {
        if (overlay == null) {
            ActionMaster.addFadeOutAction(overlayTexture, 2);
            //            setDebug(false, true);
            return;
        }
        ActionMaster.addFadeInAction(overlayTexture, 2);
        this.overlayTexture.setDrawable(new TextureRegionDrawable(overlay));
        this.overlayTexture.setWidth(overlay.getRegionWidth());
        this.overlayTexture.setHeight(overlay.getRegionHeight());
        GdxMaster.center(this.overlayTexture);
        //        debug();
        //        this.overlay = overlay;
    }


    public Label getInfoText() {
        return infoText;
    }

    public float getOverlayRotation() {
        return overlayRotation;
    }

    public void setVoidAnimHappened(boolean voidAnimHappened) {
        this.voidAnimHappened = voidAnimHappened;
    }

    public boolean getVoidAnimHappened() {
        return voidAnimHappened;
    }
}


//        if (overlay != null) {
//            Vector2 v =  localToStageCoordinates
//                    (new Vector2(getGridX()*128, getGridY()*128));
//            float x= v.x;
//            float y= v.y;
//            batch.draw(overlay, x, y, x+64, y+64, 128, 128, 1, 1, overlayRotation);
//              v = localToStageCoordinates(new Vector2(0, 0));
//              x= v.x;
//              y= v.y;
//            batch.draw(overlay, 0, 0, x+64, y+64, 128, 128, 1, 1, overlayRotation);
//        }
