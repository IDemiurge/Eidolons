package eidolons.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.DC_Cell;
import main.game.bf.Coordinates;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureManager;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

public class GridCell extends Group implements Borderable {
    private static boolean spriteCacheOn;
    protected Image backImage;
    protected TextureRegion backTexture;
    protected Image border = null;
    private int gridX;
    private int gridY;
    private TextureRegion borderTexture;
    private Label cordsText;

    public GridCell(TextureRegion backTexture, int gridX, int gridY) {
        this.backTexture = backTexture;
        this.gridX = gridX;
        this.gridY = gridY;
        setTransform(false);
    }

    public static void setSpriteCacheOn(boolean spriteCacheOn) {
        GridCell.spriteCacheOn = spriteCacheOn;
    }

    public GridCell init() {
        backImage = new Image(backTexture);
        backImage.setFillParent(true);
        addActor(backImage);
        setSize(GridConst.CELL_W, GridConst.CELL_H);

        cordsText = new Label(getGridX() + ":" + getGridY(),
         StyleHolder.getAVQLabelStyle());
        cordsText.setPosition(getWidth() / 2 - cordsText.getWidth() / 2, getHeight() / 2 - cordsText.getHeight() / 2);
        cordsText.setVisible(false);
        addActor(cordsText);

        addListener(new BattleClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                DC_Obj dc_cell = Eidolons.gameMaster.getCellByCoordinate(new Coordinates(getGridX(), getGridY()));
                if (button == Input.Buttons.RIGHT && !event.isHandled()) {
                    event.handle();
                    GuiEventManager.trigger(CREATE_RADIAL_MENU, dc_cell);
                }

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
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
                    GuiEventManager.trigger(CALL_BLUE_BORDER_ACTION, GridCell.this);

                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
            }
        });

        return this;
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!DungeonScreen.getInstance().controller.isWithinCamera
         (this)) {
            return;
        }
        if (spriteCacheOn) {
            TextureManager.drawFromSpriteCache(TextureManager.getCellSpriteCacheId(
             getGridX(), getGridY()
            ));
        }
        super.draw(batch, parentAlpha);

    }

    @Override
    public void act(float delta) {
        if (gridX == 0)
            if (gridY == 0)
                gridX = 0;
        if (!SuperActor.isCullingOff())
            if (!DungeonScreen.getInstance().controller.isWithinCamera((this))
             ) {
                return;
            }
        super.act(delta);
        if (DC_Game.game.isDebugMode()) {
            if (!cordsText.isVisible()) {
                cordsText.setVisible(true);
            }
            if (GammaMaster.DEBUG_MODE) {
                DC_Cell cell = DC_Game.game.getCellByCoordinate(new Coordinates(gridX, gridY));
                cordsText.setText(getGridX() + ":" + getGridY() + "\n gamma="
                  + DC_Game.game.getVisionMaster().getGammaMaster().
                  getGammaForCell(getGridX(), getGridY())
                  + "\n illumination="
                  + cell.getIntParam(PARAMS.ILLUMINATION)
                 /*             Additional Debug Info

                  + "\n" + cell.getVisibilityLevel()
                   +cell.getOutlineType()==null ? "" : ("\n" + cell.getOutlineType())
                  + cell.getActivePlayerVisionStatus()==null ? "" :("\n" +
                  cell.getActivePlayerVisionStatus())
                 +"\n gamma="
                 + DC_Game.game.getVisionMaster().getGammaMaster().
                 getGammaForCell(getGridX(), getGridY())+"\n Illumination="
                 + DC_Game.game.getVisionMaster().getIlluminationMaster().
                 getIllumination(getGridX(), getGridY()))+"\n gamma="
                 + DC_Game.game.getVisionMaster().getGammaMaster().
                 getGammaForCell(getGridX(), getGridY())
                 */
                );
                cordsText.setPosition(0, getHeight() / 2 - cordsText.getHeight() / 2);

            } else {
                cordsText = new Label(getGridX() + ":" + getGridY(), StyleHolder.getDefaultLabelStyle());
                cordsText.setPosition(getWidth() / 2 - cordsText.getWidth() / 2, getHeight() / 2 - cordsText.getHeight() / 2);
            }
        } else {
            if (cordsText.isVisible()) {
                cordsText.setVisible(false);
            }
        }
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

    private void updateBorderSize() {
        border.setX(-4);
        border.setY(-4);
        border.setHeight(getWidth() - 8);
        border.setWidth(getHeight() - 8);
    }

    protected int getGridX() {
        return gridX;
    }

    protected int getGridY() {
        return gridY;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        getChildren().forEach(ch -> ch.setUserObject(userObject));
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

}