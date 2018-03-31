package eidolons.libgdx.screens.map.editor;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import eidolons.game.module.adventure.MacroManager;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.screens.map.MapScreen;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.screens.map.MapGuiStage;

/**
 * Created by JustMe on 2/9/2018.
 * <p>
 * Apart from main map, what will I edit?
 * <p>
 * 'full map' can be created, then parts of it randomly used for play
 */
public class EditorMapView extends MapScreen {
    private static EditorMapView instance;
    private EditorParticleMaster editorParticles;

//    Palette;
//    InfoPanel;

    private EditorMapView() {
        super();
    }

    public static EditorMapView getInstance() {
        if (instance == null)
            instance = new EditorMapView();
        return instance;
    }

    public static void setInstance(EditorMapView instance) {
        EditorMapView.instance = instance;
    }

    public EditorParticleMaster getEditorParticles() {
        return editorParticles;
    }

    @Override
    protected InputMultiplexer getInputController() {
        return
         new InputMultiplexer(guiStage,
          super.getInputController());
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        mapStage.addActor(editorParticles = new EditorParticleMaster(mapStage.getParticles()));

    }

    @Override
    protected void afterLoad() {
        super.afterLoad();
        MacroManager.getPointMaster().added();
    }

    protected InputController initController() {
        return new EditorInputController(cam);
    }

    @Override
    protected MapGuiStage createGuiStage() {
        return new EditorMapGuiStage(new ScalingViewport
         (Scaling.stretch, GdxMaster.getWidth(),
          GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    public EditorMapGuiStage getGuiStage() {
        return (EditorMapGuiStage) guiStage;
    }
}
