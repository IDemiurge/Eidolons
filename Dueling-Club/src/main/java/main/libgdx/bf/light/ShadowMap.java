package main.libgdx.bf.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.core.game.DC_Game;
import main.libgdx.bf.GridPanel;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap extends Group {
    public static final String shadowTexturePath =
     "UI\\outlines\\shadows\\shadow.png";
    private GridPanel grid;
    private Image[][] shadowCells;
    private Texture shadowTexture;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        init();
    }

    private void init() {
        shadowCells = new Image[grid.getCols()][grid.getRows()];
        shadowTexture = TextureCache.getOrCreate(shadowTexturePath);
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                shadowCells[x][y] = new Image(shadowTexture);
                addActor(shadowCells[x][y]);
//                TODO shadowCells[x][y].setPosition();
            }
        }
        bindEvents();
        update();
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_GUI, p -> {
            update();
        });

    }

    private void update() {
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                float gamma = 0;
                try {
                    gamma =DC_Game.game.getVisionMaster().
                      getGammaMaster().getGammaForCell(x, y);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Gdx.app.log("ShadowMap", (x+y)+ " gamma = "+gamma);
                shadowCells[x][y].setColor(1, 1, 1, gamma);

//                    GridCellContainer cell = grid.getCells()[x][y];
//                    List<GridUnitView> views = cell.getUnitViews();
//                    for (GridUnitView sub : views) {
//                    }
            }


        }
    }

}








