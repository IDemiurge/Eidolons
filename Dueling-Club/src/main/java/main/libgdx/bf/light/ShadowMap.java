package main.libgdx.bf.light;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import main.game.core.game.DC_Game;
import main.libgdx.bf.GridPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap   {

    public static final SHADE_LIGHT[] SHADE_LIGHT_VALUES = SHADE_LIGHT.values();
    private GridPanel grid;
    private static boolean on;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        init();
    }

    public static void setOn(boolean on) {
        ShadowMap.on = on;
    }

    public static boolean isOn() {
        return on;
    }
//TODO act -> fluctuate alpha

    private void init() {
        for (SHADE_LIGHT type : SHADE_LIGHT_VALUES ) {
            type.setCells(new ShadeLightCell[grid.getCols()][grid.getRows()]);
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    type.getCells()[x][y] = new ShadeLightCell(type, x, y  );
                   grid. addActor(type.getCells()[x][y]);
                    type.getCells()[x][y].setPosition(
                     grid.getCells()[x][grid.getRows()- 1- y].getX(),
                     grid.getCells()[x][grid.getRows()- 1- y].getY());

                    type.getCells()[x][y].setColor(1, 1, 1, type.defaultAlpha );
                    type.getCells()[x][y]. addListener(new EventListener() {
                        @Override
                        public boolean handle(Event event) {
                            return true;
                        }
                    });
                }
            }
        }
        bindEvents();
//        update();

    }


    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_LIGHT, p -> {
            update();
        });

    }

    private void update() {
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                for (SHADE_LIGHT type : SHADE_LIGHT_VALUES) {
                    float alpha = 0;
                    if (isOn())
                    try {
                        alpha = DC_Game.game.getVisionMaster().
                         getGammaMaster().getAlphaForShadowMapCell(x, y, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (type== SHADE_LIGHT.GAMMA_LIGHT){
                        alpha /=2;
                    }
                    if (type.getCells()[x][y].getColor().a != alpha){
                        type.getCells()[x][y].setBaseAlpha(alpha);
                        type.getCells()[x][y].setColor(1, 1, 1, alpha );
                    }

                }
            }

        }
    }

    public enum SHADE_LIGHT {
        GAMMA_SHADOW(0.75f, StrPathBuilder.build("UI", "outlines", "shadows", "shadow.png")),
        GAMMA_LIGHT(0 , StrPathBuilder.build("UI", "outlines", "shadows", "light.png")),
        LIGHT_EMITTER(0 , StrPathBuilder.build("UI", "outlines", "shadows", "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build("UI", "outlines", "shadows", "concealment.png")),;
        private String texturePath;
        private ShadeLightCell[][] cells;

        public float defaultAlpha;

        SHADE_LIGHT(float alpha, String texturePath) {
            defaultAlpha = alpha;
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public ShadeLightCell[][] getCells() {
            return cells;
        }

        public void setCells(ShadeLightCell[][] cells) {
            this.cells = cells;
        }
    }

}








