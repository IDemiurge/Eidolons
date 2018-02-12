package main.libgdx.screens.map.editor;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import main.libgdx.GdxMaster;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.screens.map.MapGuiStage;
import main.libgdx.screens.map.MapScreen;

/**
 * Created by JustMe on 2/9/2018.
 *
 * Apart from main map, what will I edit?
 *
 * 'full map' can be created, then parts of it randomly used for play
 *
 *
 */
public class EditorMapView extends  MapScreen{
    private static EditorMapView instance;

//    Palette;
//    InfoPanel;

    private EditorMapView() {
        super();
    }

    public static EditorMapView getInstance() {
        if (instance==null )
            instance = new EditorMapView();
        return instance;
    }

    @Override
    protected InputMultiplexer getInputController() {
        return super.getInputController();
    }

    protected InputController initController() {
        return new EditorInputController(cam);
    }
    @Override
    protected MapGuiStage createGuiStage() {
        return new EditorMapGuiStage( new ScalingViewport
         (Scaling.stretch, GdxMaster.getWidth(),
         GdxMaster.getHeight(), new OrthographicCamera()), getBatch());
    }

    public static void setInstance(EditorMapView instance) {
        EditorMapView.instance = instance;
    }

    public EditorMapGuiStage getGuiStage() {
        return (EditorMapGuiStage) guiStage;
    }
}
