package libgdx.screens.map.editor;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import libgdx.GdxMaster;
import libgdx.bf.mouse.InputController;
import libgdx.screens.map.MapGuiStage;
import libgdx.screens.map.MapScreen;

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
    protected InputProcessor createInputController() {
        return
         new InputMultiplexer(guiStage,
          super.createInputController());
    }

    @Override
    protected void preLoad() {
        super.preLoad();
        mapStage.addActor(editorParticles = new EditorParticleMaster(mapStage.getParticles()));

    }

    @Override
    protected void afterLoad() {
        super.afterLoad();
        MapPointMaster.getInstance(). added();
    }

    protected InputController initController() {
        return new EditorInputController(getCam());
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
