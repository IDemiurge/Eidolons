package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.content.PARAMS;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.texture.TextureManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

public class GridCell extends Group implements Borderable {
    protected static boolean spriteCacheOn;
    protected Image backImage;
    protected TextureRegion backTexture;
    protected Image border = null;
    protected int gridX;
    protected int gridY;
    protected TextureRegion borderTexture;
    protected Label cordsText;

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
        setSize(GridMaster.CELL_W, GridMaster.CELL_H);

        cordsText = new Label(getGridX() + ":" + getGridY(),
         StyleHolder.getAVQLabelStyle());
        cordsText.setPosition(getWidth() / 2 - cordsText.getWidth() / 2, getHeight() / 2 - cordsText.getHeight() / 2);
        cordsText.setVisible(false);
        addActor(cordsText);

        addListener(new BattleClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getTapCount()>1){
                    if (!isAlt()) {
                        if (!DefaultActionHandler.
                         leftClickCell(false, isControl(), getGridX(), getGridY()))
                            DefaultActionHandler.
                             leftClickCell(true, isControl(), getGridX(), getGridY());
                    }
                }
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
                    GuiEventManager.trigger(TARGET_SELECTION, GridCell.this);

                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                }
                super.touchUp(event, x, y, pointer, button);
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
        if (parentAlpha == ShaderMaster.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderMaster.drawWithCustomShader(this,
             batch,
             !getUserObject().isPlayerHasSeen() ?
              DarkShader.getShader() : null, true);

    }

    @Override
    public DC_Cell getUserObject() {
        return (DC_Cell) super.getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        getChildren().forEach(ch -> ch.setUserObject(userObject));
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
                DC_Cell cell = DC_Game.game.getCellByCoordinate(Coordinates.get(gridX, gridY));
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

    protected void updateBorderSize() {
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
