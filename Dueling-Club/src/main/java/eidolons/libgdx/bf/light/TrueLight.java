package eidolons.libgdx.bf.light;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import eidolons.libgdx.GdxColorMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 8/16/2017.
 */
public class TrueLight {
    private World world;
    private RayHandler rayHandler;

    private float ambient = 0.05f;
    private float ambientAlpha = 0.05f;
    private Color ambientColor;

    public TrueLight(World world, RayHandler rayHandler) {
        this.world = world;
        this.rayHandler = rayHandler;

        rayHandler.setBlur(true);
        rayHandler.setBlurNum(15);
        ambientColor = new Color(0.2f, 0.1f, 0.3f, ambientAlpha);
        rayHandler.setAmbientLight(ambientColor);
        rayHandler.setAmbientLight(ambient);
        RayHandler.setGammaCorrection(true);
        ConeLight light = new ConeLight(rayHandler, 3, GdxColorMaster.ENEMY_COLOR, 200, 350, 350, 200, 100);
        light.setActive(true);
//        updateMap();
        bindEvents();
    }

    public void render() {

        world.step(1 / 60, 4, 4);
//        rayHandler.setCombinedMatrix(DungeonScreen.camera);
        rayHandler.updateAndRender();
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.ADD_LIGHT, p -> {
            Vector2 v = (Vector2) p.get();
            new ConeLight(rayHandler, 3, GdxColorMaster.ENEMY_COLOR, 200, v.x, v.y, 200, 100);
            new PointLight(rayHandler, 3, GdxColorMaster.ENEMY_COLOR, 200, v.x, v.y);
        });
    }
}
