package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.DialogScenario;
import main.libgdx.bf.BFDataCreatedEvent;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.AnimationEffectStage;
import main.libgdx.stage.BattleGuiStage;
import main.libgdx.stage.ChainedStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.DIALOG_SHOW;
import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class DungeonScreen extends ScreenWithLoader {
    public static OrthographicCamera camera;
    private static DungeonScreen instance;

    private Stage gridStage;
    private BattleGuiStage guiStage;
    private GridPanel gridPanel;
    private ChainedStage dialogsStage = null;
    private OrthographicCamera cam;
    private InputController controller;

    private TextureRegion backTexture;
    private AnimationEffectStage animationEffectStage;

    private Vector2 velocity;
    private boolean cameraAutoCenteringOn=true;

    public static DungeonScreen getInstance() {
        return instance;
    }

    @Override
    protected void preLoad() {
        instance = this;
        super.preLoad();

        gridStage = new Stage();
        gridStage.setViewport(viewPort);

        guiStage = new BattleGuiStage();
        //guiStage.setViewport(viewPort);

        animationEffectStage = new AnimationEffectStage();
        animationEffectStage.setViewport(viewPort);

        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        GuiEventManager.bind(UPDATE_DUNGEON_BACKGROUND, param -> {
            final String path = (String) param.get();
            backTexture = getOrCreateR(path);
        });

        GuiEventManager.bind(DIALOG_SHOW, obj -> {
              DialogueHandler handler= (DialogueHandler) obj.get();
            final List<DialogScenario> list = handler.getList();
            if (dialogsStage == null) {
                dialogsStage = new ChainedStage(list);
                updateInputController();
            } else {
                dialogsStage.play(list);
            }
            dialogsStage.setDialogueHandler(handler);
        });
    }

    @Override
    protected void afterLoad() {
        Gdx.app.log("DungeonScreen::afterLoad()", "-- Start!");
        cam = camera = (OrthographicCamera) viewPort.getCamera();
        controller = new InputController(cam);

        final BFDataCreatedEvent param = ((BFDataCreatedEvent) data.getParams().get());
        gridPanel = new GridPanel(param.getGridW(), param.getGridH()).init(param.getObjects());
        gridStage.addActor(gridPanel);
        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED, p-> {
            Coordinates coordinatesActiveObj = DC_Game.game.getManager().getActiveObj().getCoordinates();
            Vector2 unitPosition = new Vector2(coordinatesActiveObj.x*GridConst.CELL_W + GridConst.CELL_W/2, (gridPanel.getRows()-coordinatesActiveObj.y)*GridConst.CELL_H - GridConst.CELL_H/2);
//            Vector2 unitPosition = new Vector2(coordinatesActiveObj.x*GridConst.CELL_W, coordinatesActiveObj.y*GridConst.CELL_H);
            velocity = new Vector2(unitPosition.x - cam.position.x, unitPosition.y - cam.position.y).nor().scl(Math.min(cam.position.dst(unitPosition.x, unitPosition.y, 0f), 200f));
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- coordinatesActiveObj:" + coordinatesActiveObj);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- unitPosition:" + unitPosition);
            Gdx.app.log("DungeonScreen::show()--bind.ACTIVE_UNIT_SELECTED", "-- velocity:" + velocity);
        });
        Gdx.app.log("DungeonScreen::afterLoad()", "-- End!");
    }

    @Override
    protected InputMultiplexer getInputController() {
        InputMultiplexer current;
        if (canShowScreen()) {
            current = new InputMultiplexer(guiStage, controller, gridStage);
            if (dialogsStage != null) {
                current.addProcessor(dialogsStage);
            }
            current.addProcessor(new GestureDetector(controller)); // TODO коряво:D пересикается с обычным контролером
        } else {
            current = super.getInputController();
        }

        return current;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            DC_Game.game.setDebugMode(!DC_Game.game.isDebugMode());
        }

        super.render(delta);

        guiStage.act(delta);
        animationEffectStage.act(delta);
        gridStage.act(delta);
        if (isCameraAutoCenteringOn())
            cameraShift();
        //cam.update();
        if (canShowScreen()) {
            if (backTexture != null) {
                guiStage.getBatch().begin();
                guiStage.getBatch().draw(backTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                guiStage.getBatch().end();
            }

            gridStage.draw();

            animationEffectStage.draw();

            guiStage.draw();

            if (dialogsStage != null) {
                dialogsStage.act(delta);
                if (dialogsStage.isDone()) {
                    final ChainedStage dialogsStage = this.dialogsStage;
                    this.dialogsStage = null;
                    dialogsStage.dispose();
                    updateInputController();
                } else {
                    dialogsStage.draw();
                }
            }
        }
    }

    private void cameraShift() {
//        Gdx.app.log("DungeonScreen::cameraShift()", "-- Start! cam:" + cam + " velocity:" + velocity);
        if(cam != null) {
            try {
                cam.position.add(velocity.x*Gdx.graphics.getDeltaTime(), velocity.y*Gdx.graphics.getDeltaTime(), 0f);
                Coordinates coordinatesActiveObj = DC_Game.game.getManager().getActiveObj().getCoordinates();
                Vector2 unitPosition = new Vector2(coordinatesActiveObj.x*GridConst.CELL_W + GridConst.CELL_W/2, (gridPanel.getRows()-coordinatesActiveObj.y)*GridConst.CELL_H - GridConst.CELL_H/2);
//                Vector2 unitPosition2 = new Vector2(coordinatesActiveObj.x*GridConst.CELL_W, coordinatesActiveObj.y*GridConst.CELL_H);
                if(Intersector.overlaps(new Circle(new Vector2(cam.position.x, cam.position.y), 1f), new Circle(unitPosition, 1f))) {
                    cameraStop();
                }
            } catch (Exception exp) {
                if (CoreEngine.isGraphicTestMode())
                Gdx.app.log("DungeonScreen::cameraShift()", "-- exp:" + exp);
            }
            cam.update();
        }
//        Gdx.app.log("DungeonScreen::cameraShift()", "-- End!");
    }

    public void cameraStop() {
        if (velocity!=null )
        velocity.setZero();
    }
    @Override
    public void resize(int width, int height) {
/*        animationEffectStage.getViewport().update(width, height);
        gridStage.getViewport().update(width, height);
        guiStage.getViewport().update(width, height);*/
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public InputController getController() {
        return controller;
    }

    public Stage getGridStage() {
        return gridStage;
    }

    public Stage getAnimsStage() {
        return animationEffectStage;
    }

    public boolean isCameraAutoCenteringOn() {
        return cameraAutoCenteringOn;
    }

    public void setCameraAutoCenteringOn(boolean cameraAutoCenteringOn) {
        this.cameraAutoCenteringOn = cameraAutoCenteringOn;
    }
}
