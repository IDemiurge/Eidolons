package main.libgdx.bf.mouse;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import main.libgdx.anims.particles.lighting.FireLightProt;
import main.libgdx.anims.particles.lighting.LightMap;
import main.libgdx.anims.particles.lighting.LightingManager;

import java.util.LinkedList;
import java.util.List;

import static com.badlogic.gdx.Input.Buttons.LEFT;
import static com.badlogic.gdx.Input.Keys.ALT_LEFT;
import static com.badlogic.gdx.Input.Keys.CONTROL_LEFT;

/**
 * Created by PC on 25.10.2016.
 */
public class InputController implements InputProcessor {


    private float xCamPos;
    private float yCamPos;
    private OrthographicCamera camera;
    private boolean isLeftClick = false;
    private boolean alt = false;
    private boolean ctrl = false;
    private char lastTyped;
    private char lastUp;
    private List<String> charsUp = new LinkedList<>();

    public InputController(OrthographicCamera camera) {
        this.camera = camera;
    }

    // сюда передаются все обьекты, что есть в мире, и потом отсюда они управляются
    @Override
    public boolean keyDown(int i) {
        if (i == ALT_LEFT) {
            alt = true;
        }
        if (i == CONTROL_LEFT) {
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
        switch (i) {
            case ALT_LEFT:
                alt = false;
                break;
            case CONTROL_LEFT:
                ctrl = false;
                break;
            default:
                lastUp = ((char) i);
                String c = Keys.toString(i);//Character.valueOf((char) i);

                if (!charsUp.contains(c)) {
                    charsUp.add(c);
                }
                break;
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

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
/*        bf.addActor(new ParticleInterface(PARTICLE_EFFECTS.SMOKE_TEST.getPath(),
         GameScreen.getInstance().getWorld()
         , screenX, screenY));*/
        // Условно у меня на ширину приложения пикселей приходится ширина камеры абстрактрых едениц

        int x = screenX;
        int y = screenY;
        if (button == LEFT) {
            xCamPos = screenX;
            yCamPos = screenY;
            isLeftClick = true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        isLeftClick = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

//        System.out.println("screenX = " + screenX + " || screenY = " + screenY + " || pointer = "  + pointer);
        if (isLeftClick) {
            camera.position.x += (xCamPos - screenX) * camera.zoom;
            camera.position.y -= (yCamPos - screenY) * camera.zoom;
            xCamPos = screenX;
            yCamPos = screenY;
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


    public float getZoom() {
        return camera.zoom;
    }

    public float getXCamPos() {
        return xCamPos;
    }

    public float getYCamPos() {
        return yCamPos;
    }
}
