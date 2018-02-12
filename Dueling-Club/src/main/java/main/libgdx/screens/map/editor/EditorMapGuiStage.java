package main.libgdx.screens.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.Draggable;
import main.libgdx.GdxMaster;
import main.libgdx.screens.map.MapGuiStage;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorMapGuiStage extends MapGuiStage {
    private EditorControlPanel controlPanel;
    private EditorPalette palette;

    public EditorMapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    protected boolean isVignetteOn() {
        return false;
    }
    @Override
    protected void init() {
        controlPanel = new EditorControlPanel();
        DragPane dragPane = new DragPane(controlPanel);
        Draggable listener = new Draggable();
        listener.setKeepWithinParent(false);
        controlPanel.addListener(listener);
//        dragPane.setPosition(0, GdxMaster.top(controlPanel)-225);
//        addActor(dragPane);
        controlPanel.setPosition(0, GdxMaster.top(controlPanel)-225);
        addActor(controlPanel);

         palette = new EditorPalette();
        palette.addListener(new Draggable());
        dragPane = new DragPane(palette);
//        dragPane.setPosition(0,  125);
//        addActor(dragPane);
        palette.setPosition(0,  125);
        addActor(palette);
    }

    public EditorPalette getPalette() {
        return palette;
    }
}
