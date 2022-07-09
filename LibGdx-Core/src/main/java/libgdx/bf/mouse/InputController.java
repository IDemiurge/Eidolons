package libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import libgdx.GdxMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.menu.GameMenu;
import libgdx.screens.GameScreen;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.datatypes.DequeImpl;
import main.system.launch.Flags;
import main.system.math.MathMaster;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 25.10.2016.
 */
public abstract class InputController implements InputProcessor {
    protected static final float MARGIN = 300;
    protected static float zoomStep_ = OptionsMaster.getControlOptions().
            getIntValue(CONTROL_OPTION.ZOOM_STEP) / 100f;
    protected float zoomStep;
    protected OrthographicCamera camera;
    protected boolean isLeftPressed = false;
    protected boolean alt = false;
    protected boolean ctrl = false;
    protected char lastTyped;
    protected List<String> charsUp = new ArrayList<>();
    protected float width;
    protected float height;
    protected int mouseButtonPresed;
    protected float xTouchPos;
    protected float yTouchPos;
    protected static float halfWidth;
    protected static float halfHeight;
    protected static boolean unlimitedZoom;
    protected static boolean dragOff;
    private Runnable onInput;
    private Runnable onInputGdx;
    private Runnable onPassInput;

    private final DequeImpl<Runnable> onInputQueue =  (new DequeImpl<>());
    private final DequeImpl<Runnable> onInputGdxQueue =  (new DequeImpl<>());
    private final DequeImpl<Runnable> onPassInputQueue =  (new DequeImpl<>());
    protected float defaultZoom;

    public static boolean cameraMoved;


    public InputController(OrthographicCamera camera) {
        this.camera = camera;

        width = GdxMaster.getWidth() * getZoom();
        height = GdxMaster.getHeight() * getZoom();
        halfWidth = width / 2;
        halfHeight = height / 2;
        try {
            initZoom();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public static float getHalfWidth() {
        return halfWidth;
    }

    public static float getHalfHeight() {
        return halfHeight;
    }

    protected void initZoom() {
        defaultZoom= getDefaultZoom();
        defaultZoom = Math.min(getWidth() * getPreferredMinimumOfScreenToFitOnDisplay()
                / GdxMaster.getWidth(), 1f);
        defaultZoom = Math.min(getHeight() * getPreferredMinimumOfScreenToFitOnDisplay()
                / GdxMaster.getHeight(), defaultZoom);
        camera.zoom = defaultZoom;


        zoomStep = zoomStep_ * defaultZoom;

        width = GdxMaster.getWidth() * camera.zoom;
        height = GdxMaster.getHeight() * camera.zoom;
        halfWidth = width / 2;
        halfHeight = height / 2;


    }

    protected float getDefaultZoom() {
        return 1;
    }

    protected float getPreferredMinimumOfScreenToFitOnDisplay() {
        return 0.66f;
    }

    public static void setZoomStep(float zoomStep) {
        InputController.zoomStep_ = zoomStep;
    }

    public static void setUnlimitedZoom(boolean unlimitedZoom) {
        InputController.unlimitedZoom = unlimitedZoom;
    }

    public static boolean isUnlimitedZoom() {
        return unlimitedZoom;
    }

    public static void setDragOff(boolean dragOff) {
        InputController.dragOff = dragOff;
    }

    public static boolean isDragOff() {
        return dragOff;
    }

    @Override
    public boolean keyDown(int i) {
        keyInput();
        if (isBlocked())
            return true;
        if (i == Keys.ALT_LEFT) {
            alt = true;
        }
        if (i == Keys.CONTROL_LEFT) {
            ctrl = true;
        }

//        if (i == 54) {
//            LightMap.resizeFBOa();
//        }
//        if (i == 52) {
//            LightMap.resizeFBOb();
//        }

        return false;
        // alt = 57, crtl = 129
    }

    protected boolean isBlocked() {
        return OptionsMaster.isMenuOpen() || GameMenu.menuOpen;
    }

    @Override
    public boolean keyUp(int i) {


        if (isBlocked())
            return true;
        switch (i) {
            case Keys.ALT_LEFT:
                alt = false;
                break;
            case Keys.CONTROL_LEFT:
                ctrl = false;
                break;
            default:
//                lastUp = ((char) i);
                String c = Keys.toString(i);//Character.valueOf((char) i);

                if (!charsUp.contains(c)) {
                    charsUp.add(c);
                }
                break;
        }

        return false;
    }

    public void keyInput() {
        ExplorationMaster.setWaiting(false);
        input();

    }

    public boolean inputPass() {
        if (isStackInput()) {
            if (!onPassInputQueue.isEmpty()) {
                onPassInput = onPassInputQueue.removeFirst();
            } else
                return false;
        }
        if (onPassInput != null) {
            SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK);

            main.system.auxiliary.log.LogMaster.devLog("onPassInput.run() ");
            onPassInput.run();
            onPassInput = null;
            return true;
        }
        return false;
    }

    public void input() {
        if (isStackInput()) {
            if (!onInputGdxQueue.isEmpty()) {
                onInputGdx = onInputGdxQueue.removeFirst();
            }
            if (!onInputQueue.isEmpty()) {
                onInput = onInputQueue.removeFirst();
            }
        }
        if (onInputGdx != null) {
            main.system.auxiliary.log.LogMaster.devLog("onInputGdx.run() ");
            onInputGdx.run();
            onInputGdx = null;
        }
        if (onInput != null) {
            main.system.auxiliary.log.LogMaster.devLog("onInput.run() ");
            Core.onNonGdxThread(onInput);
            onInput = null;
        }
    }

    public void mouseInput() {
        input();
    }

    @Override
    public boolean keyTyped(char c) {
        keyInput();
        if (isBlocked())
            return true;
//        if (keyMap.getVar(c))
        String str = String.valueOf(c).toUpperCase();
        if (c == lastTyped) {
            if (!charsUp.contains(str)) {
                return false;
            }
        }
        charsUp.remove(str);
        lastTyped = c;

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseInput();
        if (isBlocked())
            return true;
        if (button == Input.Buttons.LEFT || button == 1) {
            xTouchPos = screenX;
            yTouchPos = screenY;
            isLeftPressed = true;
        }
        mouseButtonPresed = button;
        outsideClick();
        return false;
    }

    protected void outsideClick() {
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        mouseInput();
        if (isBlocked())
            return true;
        isLeftPressed = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

//        mouseInput();
        if (isDragOff())
            return false;
        if (isBlocked())
            return false;
        if (isManualCameraDisabled())
            return false;
        if (mouseButtonPresed == Input.Buttons.LEFT) {
            tryPullCameraX(screenX);
            tryPullCameraY(screenY);
            cameraStop();
        }

        return true;
    }

    private boolean isManualCameraDisabled() {
        if (!Flags.isIDE())
            return Cinematics.ON;
        return false;
    }
    protected abstract GameScreen getScreen();

    protected void cameraStop() {
        getScreen().cameraStop(false);
    }


    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    protected boolean isFreeDrag() {
        return true;
    }
    protected void tryPullCameraY(int screenY) {
        float diffY = (yTouchPos - screenY) * camera.zoom * getDragCoef();
        if (isFreeDrag()){
            camera.position.y =camera.position.y - diffY;
        } else
            camera.position.y = MathMaster.getMinMax(
                camera.position.y - diffY,
                halfHeight - getMargin(),
                getHeight() - halfHeight + getMargin());
        yTouchPos = screenY;

        Gdx.input.setCursorPosition(Gdx.input.getX(), (int) yTouchPos);
        cameraPosChanged();
    }


    protected void tryPullCameraX(int screenX) {
        //TODO custom bounds
        float diffX = (xTouchPos - screenX) * camera.zoom * getDragCoef();
        if (isFreeDrag()){
            camera.position.x =camera.position.x + diffX;
        } else
        camera.position.x = MathMaster.getMinMax(
                camera.position.x + diffX,//-getMargin(),
                halfWidth - getMargin(),
                getWidth() - halfWidth + getMargin());
        xTouchPos = screenX;
        Gdx.input.setCursorPosition((int) xTouchPos,Gdx.input.getY() );
        cameraPosChanged();
    }

    protected float getDragCoef() {
        return 1f;
    }


    protected float getMargin() {
        return MARGIN ;
    }

    protected float getOffsetX() {
        return 0;
    }
    protected float getOffsetY() {
        return 0;
    }

    protected abstract float getWidth();

    protected abstract float getHeight();

    public void setDefaultPos() {
        centerAt(DC_Game.game.getPlayer(true).getHeroObj().getCoordinates());
    }

    protected void centerAt(Coordinates coordinates) {
        float x = coordinates.x * GridMaster.CELL_W * camera.zoom;
        float y = coordinates.x * GridMaster.CELL_W * camera.zoom;
        camera.position.set(x, y, 0);
        cameraPosChanged();
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;

    }

    @Override
    public boolean scrolled(int i) {
        if (isBlocked())
            return true;
        zoom(i);
        return false;
    }

    protected boolean zoom(int i) {
        // if (!isUnlimitedZoom())
        //     if (!checkZoom(i))
        //         return false;
        if (!alt && !ctrl) {
            if (i == 1) {

                camera.zoom += zoomStep;
            }
            if (i == -1) {
                if (camera.zoom >= zoomStep) {
                    camera.zoom -= zoomStep;
                }
            }
        }
        if (camera.zoom < 0)
            camera.zoom = defaultZoom;

        cameraZoomChanged();
        cameraPosChanged();
        return true;
    }

    private void setZoom(float zoom) {
        camera.zoom = zoom;
        cameraZoomChanged();
        cameraPosChanged();
    }
    public void cameraZoomChanged() {
        width = GdxMaster.getWidth() * camera.zoom;
        height = GdxMaster.getHeight() * camera.zoom;
        halfWidth = width / 2;
        halfHeight = height / 2;
        cameraPosChanged();
    }

    public void cameraPosChanged() {
        cameraMoved=true;
//        for (SuperActor sub : cachedPosActors) {
//            sub.cameraMoved();
//        }

//        cachedPosActors.clear(); not needed?
    }

    private boolean checkZoom(int i) {
        float newHeight = height + zoomStep * i * height;

        float y = camera.position.y;
        if (newHeight > y + halfHeight + getMargin())
            return false;
        if (newHeight > getHeight() - y + halfHeight + getMargin())
            return false;

        float newWidth = width + zoomStep * i * width;
        float x = camera.position.x;
        if (newWidth > x + halfWidth + getMargin())
            return false;
        return !(newWidth > getWidth() - x + halfWidth + getMargin());
    }
    public void centerCam() {
        camera.position.x = width/2;
        camera.position.y = height/2;
        cameraPosChanged();
    }

    public void maxZoom() {
        while(zoom(1)){
        }
    }
    public boolean isWithinCamera(Actor actor) {
        return isWithinCamera(actor.getX() + actor.getWidth(), actor.getY() + actor.getHeight(), actor.getWidth(), actor.getHeight());
    }

    public boolean isCursorWithinCameraX(float x) {

        return false;
    }

    public boolean isWithinCamera(float x, float y, float width, float height, boolean adjustForRotation) {
        if (adjustForRotation) {
            return isWithinCamera(x-width, y-height, 3*width, 3*height);
        } else
            return isWithinCamera(x, y, width, height);
    }
    public boolean isWithinCamera(float x, float y, float width, float height) {
        float xPos = Math.abs(camera.position.x - x) - width;
        if (xPos > halfWidth)
            return false;
        float yPos = Math.abs(camera.position.y - y) - height;
        return !(yPos > halfHeight);
    }

    public float getZoom() {
        return camera.zoom;
    }

    public float getXCamPos() {
        return camera.position.x;
    }
    public float getAbsoluteCursorX () {
        return camera.position.x +  Gdx.input.getX() - halfWidth;
    }
    public float getAbsoluteCursorY () {
        return camera.position.y +  Gdx.input.getY() - halfHeight;
    }

    public float getYCamPos() {
        return camera.position.y;
    }

    public void onInput(Runnable runnable) {
        onInput = runnable;
        main.system.auxiliary.log.LogMaster.devLog("onInput set ");
    }

    public void onInputGdx(Runnable runnable) {
        onInputGdx = runnable;
        main.system.auxiliary.log.LogMaster.devLog("onInputGdx set ");
    }

    public Runnable getOnInput() {
        return onInput;
    }

    public Runnable getOnInputGdx() {
        return onInputGdx;
    }

    public void onInputGdx(Boolean gdx_any_pass, Runnable runnable) {
        main.system.auxiliary.log.LogMaster.devLog(gdx_any_pass + " onInput set ");

        if (isStackInput()) {
            if (gdx_any_pass == null) {
                onPassInputQueue.add(runnable);
            } else if (gdx_any_pass) {
                onInputGdxQueue.add(runnable);
            } else
                onInputQueue.add(runnable);
        } else
        if (gdx_any_pass == null) {
            onPassInput = runnable;
        } else if (gdx_any_pass) {
            onInputGdx = runnable;
        } else
            onInput = runnable;
    }

    public Runnable getOnInput(Boolean gdx_any_pass) {
        if (isStackInput()) {
            //TODO
        }
        if (gdx_any_pass == null) {
            return onPassInput;
        }
        if (gdx_any_pass) {
            return onInputGdx;
        }
        return onInput;
    }

    public boolean space() {
        return inputPass();
    }

    public boolean enter() {
        return inputPass();
    }

    public boolean escape() {
        return inputPass();
    }

    public boolean isStackInput() {
        return true;
    }


    public void resetZoom() {
        setZoom(defaultZoom);
    }

    public int getGridDrawY2(float height) {
        return (int) Math.max(0, ((height/2+camera.position.y - halfHeight) / 128-2));
    }
    public int getGridDrawY1(float height) {
        return (int)  (( height /2+camera.position.y + halfHeight) / 128 +2 );
    }
    public int getGridDrawX1(float width) {
        return (int) Math.max(0, ((camera.position.x - halfWidth) / 128 -1));
    }
    public int getGridDrawX2(float width) {
        return (int) ((camera.position.x+halfWidth)/128+1);
    }
}
