package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.entity.active.DefaultActionHandler;
import main.entity.obj.DC_Obj;
import main.game.battlecraft.logic.battlefield.vision.GammaMaster;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.libgdx.StyleHolder;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import static main.system.GuiEventType.CALL_BLUE_BORDER_ACTION;
import static main.system.GuiEventType.CREATE_RADIAL_MENU;

public class GridCell extends Group implements Borderable {
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

    public GridCell init() {

        backImage = new Image(backTexture);
        backImage.setFillParent(true);
        addActor(backImage);
        setSize(GridConst.CELL_W, GridConst.CELL_H);

        cordsText = new Label(getGridX() + ":" + getGridY(), StyleHolder.getDefaultLabelStyle());
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

                    if (isAlt() ||isShift() ||isControl()
                        //|| ExplorationMaster.isExplorationOn()
                     )
                        try {
                            if (DefaultActionHandler.
                             leftClickCell(isShift(), isControl(), getGridX(), getGridY()))
                                return;
                             } catch (Exception e) {
                            e.printStackTrace();
                        }
                    GuiEventManager.trigger(CALL_BLUE_BORDER_ACTION, GridCell.this);

                }
            }
        });

        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().getController().isWithinCamera
//             (getX(), getY(), getWidth(), getHeight()))
 (this)) {
                return;
            }
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON)) {
//            if (getGridY()==0 && getGridX()==0)
            TextureManager.drawFromSpriteCache(TextureManager.getCellSpriteCacheId(
             getGridX(), getGridY()
            ));
        }
//         else
        super.draw(batch, parentAlpha);

    }

    @Override
    public void act(float delta) {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().getController().isWithinCamera(
//             getX(), getY(),
//             2*getWidth(), 2*getHeight())
             (this))
             ) {
                return;
            }
        super.act(delta);
        if (DC_Game.game.isDebugMode()) {
            if (!cordsText.isVisible()) {
                cordsText.setVisible(true);
            }
            if (GammaMaster.DEBUG_MODE) {
                cordsText.setText(getGridX() + ":" + getGridY() + ", gamma="
                  + DC_Game.game.getVisionMaster().getGammaMaster().
                  getGammaForCell(getGridX(), getGridY())
//                 +"\n gamma="
//                 + DC_Game.game.getVisionMaster().getGammaMaster().
//                 getGammaForCell(getGridX(), getGridY())+"\n Illumination="
//                 + DC_Game.game.getVisionMaster().getIlluminationMaster().
//                 getIllumination(getGridX(), getGridY()))+"\n gamma="
//                 + DC_Game.game.getVisionMaster().getGammaMaster().
//                 getGammaForCell(getGridX(), getGridY())
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
//        if (DC_Game.game!=null )
//            if (DC_Game.game.isStarted())
//        if (!VisionManager.isVisionHacked()){
//            gamma = DC_Game.game.getVisionMaster().getGammaMaster().getGammaForCell(getGridX(), getGridX());
//        if (gamma !=0)
//            setColor(gamma, gamma, gamma, 1);
//        else
//            setColor(0.1f, 0.1f, 0.1f, 1);
//        }
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
