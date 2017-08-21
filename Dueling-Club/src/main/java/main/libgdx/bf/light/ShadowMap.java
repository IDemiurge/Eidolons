package main.libgdx.bf.light;

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
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 8/16/2017.
 */
public class ShadowMap extends Group {

    private GridPanel grid;

    public ShadowMap(GridPanel grid) {
        this.grid = grid;
        init();
    }

    private void init() {
        for (SHADE_LIGHT type : SHADE_LIGHT.values()) {
            Texture texture = TextureCache.getOrCreate(type.getTexturePath());
            type.setCells(new Image[grid.getCols()][grid.getRows()]);
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    type.getCells()[x][y] = new Image(texture);
                    addActor(type.getCells()[x][y]);
                    type.getCells()[x][y].setPosition(
                     grid.getCells()[x][y].getX(),
                     grid.getCells()[x][y].getY());
                }
            }
        }
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
        return null;
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_GUI, p -> {
            update();
        });

    }

    private void update() {
        for (int x = 0; x < grid.getCols(); x++) {
            for (int y = 0; y < grid.getRows(); y++) {
                for (SHADE_LIGHT type : SHADE_LIGHT.values()) {
                    float alpha = 0;
                    try {
                        alpha = DC_Game.game.getVisionMaster().
                         getGammaMaster().getAlphaForShadowMapCell(x, y, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    type.getCells()[x][y].setColor(1, 1, 1, alpha);

                }
            }

        }
    }

    public enum SHADE_LIGHT {
        GAMMA_SHADOW(StrPathBuilder.build("UI", "outlines", "shadows", "shadow.png")),
        GAMMA_LIGHT(StrPathBuilder.build("UI", "outlines", "shadows", "light.png")),
        LIGHT_EMITTER(StrPathBuilder.build("UI", "outlines", "shadows", "light emitter.png")),
        CONCEALMENT(StrPathBuilder.build("UI", "outlines", "shadows", "concealment.png")),;
        private String texturePath;
        private Image[][] cells;

        SHADE_LIGHT(String texturePath) {
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public Image[][] getCells() {
            return cells;
        }

        public void setCells(Image[][] cells) {
            this.cells = cells;
        }
    }

}








