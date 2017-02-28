package main.libgdx.bf.mouse;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.core.game.DC_Game;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.lighting.FireLightProt;
import main.libgdx.anims.particles.lighting.LightMap;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.gui.dialog.Dialog;
import main.libgdx.gui.dialog.DialogDisplay;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.MigMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by PC on 25.10.2016.
 */
public class InputController implements InputProcessor {


    float x_cam_pos;
    float y_cam_pos;
    OrthographicCamera camera;
    boolean is_it_Left_Click = false;
    boolean alt = false;
    boolean ctrl = false;
    private Stage bf;
    private Stage gui;
    private char lastTyped;
    private char lastUp;
    private List<String> charsUp=    new LinkedList<>() ;

    public InputController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public InputController(Stage bf, Stage gui, OrthographicCamera cam) {
        this.bf = bf;
        this.gui = gui;
        this.camera = cam;

    }


    // сюда передаются все обьекты, что есть в мире, и потом отсюда они управляются
    @Override
    public boolean keyDown(int i) {
        if (i == 57) {
            alt = true;
        }
        if (i == 129) {
            ctrl = true;
        }

        if (i == 54) {
            LightMap.resizeFBOa();
        }
        if (i == 52) {
            LightMap.resizeFBOb();
        }

        return false;
        // alt = 57, crtl = 129
    }


    @Override
    public boolean keyUp(int i) {
        if (i == 57) {
            alt = false;
        }
else        if (i == 129) {
            ctrl = false;
        }
else
        {
            lastUp = ((char)i);
            String c = Keys.toString(i);//Character.valueOf((char) i);
            if (!charsUp.contains(c)) {
                charsUp.add(c);
            }
        }

        return false;
    }

    @Override
    public boolean keyTyped(char c) {
//        if (keyMap.get(c))
        String str = String.valueOf(c).toUpperCase();
        if (c == lastTyped) {
            if (!charsUp.contains(str)) {
                return false;
            }
        }
        charsUp.remove(str);
        lastTyped = c;

        DC_Game.game.getBattleField().getKeyListener().handleKeyTyped(0, c);

        return true;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
/*        bf.addActor(new ParticleInterface(PARTICLE_EFFECTS.SMOKE_TEST.getPath(),
         GameScreen.getInstance().getWorld()
         , i, i1));*/
        // Условно у меня на ширину приложения пикселей приходится ширина камеры абстрактрых едениц

        int x = i;
        int y = i1;
        if (i3 == 0) {
            x_cam_pos = i;
            y_cam_pos = i1;
            is_it_Left_Click = true;
        }
        Dialog dialog = GameScreen.getInstance().getDialogDisplay().getDialog();
        if (dialog != null) {
            int w = MigMaster.getCenteredPosition(
                    (int) GameScreen.getInstance().getBackground().getWidth()
                    , (int) dialog.getWidth());
            boolean
                    outside = x < w || x > w + (int) dialog.getWidth();
            if (!outside) {
                int h = MigMaster.getCenteredPosition(
                        (int) GameScreen.getInstance().getBackground().getHeight()
                        , (int) dialog.getHeight());
                outside = y < h || y > h + (int) dialog.getHeight();
            }
            if (outside) {
                GuiEventManager.trigger(GuiEventType.DIALOG_CLOSED, null);
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        is_it_Left_Click = false;

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {

        if (DialogDisplay.isDisplaying()) {
            return false;
        }
//        System.out.println("i = " + i + " || i1 = " + i1 + " || i2 = "  + i2);
        if (is_it_Left_Click) {
            camera.position.x += (x_cam_pos - i) * camera.zoom;
            camera.position.y -= (y_cam_pos - i1) * camera.zoom;
            x_cam_pos = i;
            y_cam_pos = i1;
            Image background = GameScreen.getInstance().getBackground().getImage();
            background.setBounds(
                    camera.position.x - background.getWidth() / 2,
                    camera.position.y - background.getHeight() / 2,
                    background.getWidth(),
                    background.getHeight());
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {

        if (LightingManager.isMouse_light()) {
            LightMap.mouseMouseMove((camera.position.x - (camera.viewportWidth / 2) * camera.zoom + (i) * camera.zoom), (camera.position.y + (camera.viewportHeight / 2) * camera.zoom - i1 * camera.zoom), camera.zoom);
            float x_light = (camera.position.x - (camera.viewportWidth / 2) * camera.zoom + (i) * camera.zoom);
            float y_light = (camera.position.y + (camera.viewportHeight / 2) * camera.zoom - i1 * camera.zoom);
            boolean by_X;
            boolean by_Y;
            by_X = !(x_light < camera.position.x - (camera.viewportWidth / 2) * camera.zoom + LightingManager.mouse_light_distance_to_turn_off || x_light > camera.position.x + (camera.viewportWidth / 2) * camera.zoom - LightingManager.mouse_light_distance_to_turn_off);
            by_Y = !(y_light < camera.position.y - (camera.viewportHeight / 2) * camera.zoom + LightingManager.mouse_light_distance_to_turn_off || y_light > camera.position.y + (camera.viewportHeight / 2) * camera.zoom - LightingManager.mouse_light_distance_to_turn_off);
            if (by_X & by_Y) {
                LightMap.mouseLightDistance(LightingManager.mouse_light_distance);
            } else {
                LightMap.mouseLightDistance(0);
            }
        }
        return false;

    }

    @Override
    public boolean scrolled(int i) {

//        if (DialogDisplay.isDisplaying() )
//            return false;
//
        if (alt && !ctrl) {
            if (i == 1) {
                FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() + 0.05f);
            }
            if (i == -1) {
                FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() - 0.05f);
            }

        }
        if (!alt && ctrl) {
            if (i == 1) {
                FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() + 0.05f);
            }
            if (i == -1) {
                FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() - 0.05f);
            }

        }
        if (alt && ctrl) {
            if (i == 1) {
                LightMap.setAmbient(LightMap.getAmbint() + 0.02f);
            }
            if (i == -1) {
                LightMap.setAmbient(LightMap.getAmbint() - 0.02f);
            }

        }
        zoom(i);

//        System.out.println(camera.zoom);
        return false;
    }

    private void zoom(int i) {
        if (!alt && !ctrl) {
            if (i == 1) {
                camera.zoom += 0.25f;
            }
            if (i == -1) {
                if (camera.zoom >= 0.25f) {
                    camera.zoom -= 0.25f;
                }
            }
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getZoom() {
        return camera.zoom;
    }

    public float getX_cam_pos() {
        return x_cam_pos;
    }

    public float getY_cam_pos() {
        return y_cam_pos;
    }
}
