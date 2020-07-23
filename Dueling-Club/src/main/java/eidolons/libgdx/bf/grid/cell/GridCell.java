package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.BlockableGroup;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.system.controls.GlobalController;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;

import java.util.function.Function;

import static main.system.GuiEventType.*;

public abstract class GridCell extends BlockableGroup implements Borderable, Colored {
    protected ImageContainer cellImgContainer;
    protected TextureRegion backTexture;
    protected FadeImageContainer overlay;
    protected float overlayRotation;
    protected Image border = null;
    protected int gridX;
    protected int gridY;
    protected TextureRegion borderTexture;
    protected Actor cordsText;
    protected Label infoText;

    protected boolean voidAnimHappened;
    protected boolean withinCamera;
    protected FadeImageContainer pillar;
    protected Function<Coordinates, Color> colorFunc;

    public GridCell(TextureRegion backTexture, int gridX, int gridY, Function<Coordinates, Color> colorFunc) {
        this.backTexture = backTexture;
        this.gridX = gridX;
        this.gridY = gridY;
        this.colorFunc = colorFunc;
        setTransform(false);
    }

    public Image getCellImage() {
        return cellImgContainer.getContent();
    }

    public ImageContainer getCellImgContainer() {
        return cellImgContainer;
    }

    public void setVoid(boolean VOID, boolean animated) {
        if (animated) {
            if (VOID) {
                ActionMaster.addFadeOutAction(cellImgContainer, 0.5f, false);
            } else {
                ActionMaster.addFadeInAction(cellImgContainer, 0.5f);
            }
        } else {
            cellImgContainer.setVisible(!VOID);
            cellImgContainer.getColor().a = 0;
        }
    }

    public boolean isRotation(boolean wall) {
        return false;
    }

    public GridCell init() {
        cellImgContainer = new ImageContainer(new Image(backTexture));
        cellImgContainer.getColor().a = getCellImgAlpha();
        if (isRotation(false)) {
            int n = RandomWizard.getRandomIntBetween(0, 4);
            cellImgContainer.setRotation(90 * n);
            cellImgContainer.setOrigin(64, 64);
        }
        addActor(cellImgContainer);
        //        addActor(overlay = new SpriteX());
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
        return 1.0f;
        // return 0.8f;
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
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                DC_Obj cell = getUserObject();// Eidolons.gameMaster.getCellByCoordinate(Coordinates.get(getGridX(), getGridY()));

                if (button == Input.Buttons.RIGHT && !event.isHandled()) {
                    if (Flags.isMe()) {
                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            cell.getGame().getMovementManager().move(Eidolons.getMainHero(), cell.getCoordinates());
                            GuiEventManager.trigger(UNIT_MOVED, Eidolons.getMainHero());
                            return;
                        }
                    }
                    event.handle();
                    if (cell.getCoordinates().dst(Eidolons.getPlayerCoordinates()) > 1) {
                        DefaultActionHandler.moveToMotion(cell.getCoordinates());
                    } else
                        GuiEventManager.trigger(CREATE_RADIAL_MENU, cell);
                }

                super.touchUp(event, x, y, pointer, button);
                if (button == Input.Buttons.LEFT) {
                    event.handle();
                    if (isEmpty())
                        if (isAlt() || isShift() || isControl())
                            if (DefaultActionHandler.
                                    leftClickCell(isShift(), isControl(), getGridX(), getGridY()))
                                return;
                    GuiEventManager.trigger(TARGET_SELECTION, GridCell.this);
                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
            }
        };
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isWithinCamera()) {
            return;
        }
        super.draw(batch, 1);
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

    public boolean isWithinCamera() {
        return true;

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isCoordinatesShown()) {
            infoText.setText(getInfoText());
            cordsText.setVisible(true);
        } else {
            if (cordsText.isVisible()) {
                cordsText.setVisible(false);
            }
        }
    }

    private CharSequence getInfoText() {
        DC_Cell cell = getUserObject();
        return ContainerUtils.build(getGridX(), ":", getGridY())
                +  //TODO into method
                "\n gamma=" + DC_Game.game.getVisionMaster().getGammaMaster().
                getGammaForCell(getGridX(), getGridY())
                + "\n color = "
                + cell.getGame().getColorMap().getOutput().get(cell.getCoordinates());
    }

    protected boolean isCoordinatesShown() {
        return GridOverlaysManager.debug;// DC_Game.game.isDebugMode();
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
            border.setX(-4);
            border.setY(-4);
            border.setHeight(getWidth() - 8);
            border.setWidth(getHeight() - 8);
        }
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
        if (this.overlayRotation == overlayRotation)
            return;
        this.overlayRotation = overlayRotation;
        overlay.setOrigin(64, 64);
        ActionMaster.addRotateByAction(overlay, overlay.getRotation(), overlayRotation);
    }

    public void setOverlayTexture(TextureRegion region) {
        if (region == null) {
            ActionMaster.addFadeOutAction(getOverlay(), 2);
            return;
        }
        ActionMaster.addFadeInAction(getOverlay(), 2);
        this.overlay.setImage(region);
        GdxMaster.center(this.overlay);
    }


    public Label getInfoTextLabel() {
        return infoText;
    }

    public void setVoidAnimHappened(boolean voidAnimHappened) {
        this.voidAnimHappened = voidAnimHappened;
    }

    public boolean getVoidAnimHappened() {
        return voidAnimHappened;
    }

    public FadeImageContainer addPillar(String path) {
        if (pillar == null) {
            pillar = new FadeImageContainer(path) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);
                }

                @Override
                public boolean remove() {
                    return super.remove();
                }

                @Override
                public void setColor(Color color) {
                    super.setColor(color);
                }

                @Override
                public void setVisible(boolean visible) {
                    super.setVisible(visible);
                }
            };
        } else {
            if (pillar.getImagePath().equalsIgnoreCase(path)) {
                return null;
            }
            pillar.setImage(path);
        }
        if (pillar.getParent() == null) {
            addActor(this.pillar);
        }
        pillar.setZIndex(0);
        return pillar;
    }

    public void removePillar() {
        if (pillar != null)
            ActionMaster.addAfter(pillar, new SequenceAction(
                    ActionMaster.getFadeOut(pillar, 1f),
                    new RemoveActorAction()));
    }

    public FadeImageContainer getOverlay() {
        if (overlay == null) {
            addActor(overlay = new FadeImageContainer() {
                public float getFadeDuration() {
                    float mod = 1 / (AnimMaster.speedMod());
                    return 1.0f * mod;
                }

                @Override
                protected boolean isHideWhenFade() {
                    return false;
                }
            });
            overlay.setTouchable(Touchable.disabled);
        }
        return overlay;
    }

    public FadeImageContainer getPillar() {
        return pillar;
    }

    public Function<Coordinates, Color> getColorFunc() {
        return colorFunc;
    }
}
