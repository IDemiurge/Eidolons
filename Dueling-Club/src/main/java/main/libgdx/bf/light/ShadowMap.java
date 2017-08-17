package main.libgdx.bf.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
        float xPos = 0;
        float yPos = 0;
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                shadowCells[x][y] = new Image(shadowTexture);
                addActor(shadowCells[x][y]);
                xPos = grid.getCells()[x][y].getX();
                yPos = grid.getCells()[x][y].getY();
                shadowCells[x][y].setPosition(xPos, yPos);
            }}
        bindEvents();
        update();
        addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
//                event.cancel();
//             event.reset();
             return false;
            }
        });
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null ;
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_GUI, p -> {
            update();
        });

    }

    private void update() {
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                float gamma = 1;
                try {
                    gamma =DC_Game.game.getVisionMaster().
                      getGammaMaster().getGammaForCell(x, y);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                float alpha = 1 - gamma;
                Gdx.app.log("ShadowMap", (x+y)+ " alpha = "+alpha);
                 shadowCells[x][y].setColor(1, 1, 1, alpha);

//                    GridCellContainer cell = grid.getCells()[x][y];
//                    List<GridUnitView> views = cell.getUnitViews();
//                    for (GridUnitView sub : views) {
//                    }
            }


        }
    }

}








