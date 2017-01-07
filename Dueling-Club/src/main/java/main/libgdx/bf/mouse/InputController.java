package main.libgdx.bf.mouse;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.DC_Game;
import main.libgdx.GameScreen;
import main.test.libgdx.prototype.FireLightProt;
import main.test.libgdx.prototype.Lightmap;

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

    public InputController(OrthographicCamera camera) {
        this.camera = camera;
    }

    // сюда передаются все обьекты, что есть в мире, и потом отсюда они управляются
    @Override
    public boolean keyDown(int i) {
        if (i == Input.Keys.ALT_LEFT) {
            alt = true;
        }
        if (i == Input.Keys.CONTROL_LEFT) {
            ctrl = true;
        }
        return false;
        // alt = 57, crtl = 129
    }


    @Override
    public boolean keyUp(int i) {
        if (i == Input.Keys.ALT_LEFT) {
            alt = false;
        }
        if (i == Input.Keys.CONTROL_LEFT) {
            ctrl = false;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        DC_Game.game.getBattleField().getKeyListener().handleKeyTyped(0, c);

        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
/*        bf.addActor(new ParticleActor(PARTICLE_EFFECTS.SMOKE_TEST.getPath(),
         GameScreen.getInstance().getWorld()
         , i, i1));*/
        // Условно у меня на ширину приложения пикселей приходится ширина камеры абстрактрых едениц
        if (i3 == 0) {
            x_cam_pos = i;
            y_cam_pos = i1;
            is_it_Left_Click = true;
        }


        System.out.println(i + " || " + i1 + " || " + i2 + " || " + i3);

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        is_it_Left_Click = false;

        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
//        System.out.println("i = " + i + " || i1 = " + i1 + " || i2 = "  + i2);
        if (is_it_Left_Click) {
            camera.position.x += (x_cam_pos - i) * camera.zoom;
            camera.position.y -= (y_cam_pos - i1) * camera.zoom;
            x_cam_pos = i;
            y_cam_pos = i1;
            Image background = GameScreen.getInstance().getBackground().backImage;
            background.setBounds(
                    camera.position.x - background.getWidth() / 2, camera.position.y - background.getHeight() / 2,
                    background.getWidth(),
                    background.getHeight());
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        if (alt && !ctrl) {
            // ambient
            if (i == 1) {
                Lightmap.setAmbint(Lightmap.getAmbint() + 0.02f);
            }
            if (i == -1) {
                Lightmap.setAmbint(Lightmap.getAmbint() - 0.02f);
            }

        }
        if (ctrl) {
            if (alt) {
                if (i == 1) {
                    FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() + 0.05f);
                }
                if (i == -1) {
                    FireLightProt.setBiggerAlpha(FireLightProt.getAlphaBigger() - 0.05f);
                }

            } else {
                if (i == 1) {
                    FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() + 0.05f);
                }
                if (i == -1) {
                    FireLightProt.setSmallerAlpha(FireLightProt.getAlphaSmaller() - 0.05f);
                }
            }
            // local lights
        }
        if (!alt && !ctrl) {
            if (i == 1) {
                camera.zoom += 0.25f;
            }
            if (i == -1) {
                if (camera.zoom >= 0.25f)
                    camera.zoom -= 0.25f;
            }
        }

//        System.out.println(camera.zoom);
        return false;
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
