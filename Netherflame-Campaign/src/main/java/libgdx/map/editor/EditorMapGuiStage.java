package libgdx.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.Draggable;
import libgdx.GdxMaster;
import libgdx.map.MapGuiStage;
import libgdx.stage.ConfirmationPanel;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorMapGuiStage extends MapGuiStage {
    private EditorPalette palette;
    private EmitterPalette emitterPalette;

    public EditorMapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    protected boolean isVignetteOn() {
        return false;
    }

    @Override
    protected void init() {
        EditorControlPanel controlPanel = new EditorControlPanel();
        DragPane dragPane = new DragPane(controlPanel);
        Draggable listener = new Draggable();
        listener.setKeepWithinParent(false);
        controlPanel.addListener(listener);
//        dragPane.setPosition(0, GdxMaster.top(controlPanel)-225);
//        addActor(dragPane);
        controlPanel.setPosition(0, GdxMaster.getTopY(controlPanel) - 20);
        addActor(controlPanel);

        palette = new EditorPalette();
        palette.addListener(new Draggable());
        dragPane = new DragPane(palette);
//        dragPane.setPosition(0,  125);
//        addActor(dragPane);
        palette.setPosition(0, 0);
        addActor(palette);

        EditorInfoPanel editorInfoPanel = new EditorInfoPanel();
        addActor(editorInfoPanel);
        editorInfoPanel.setPosition(GdxMaster.right(editorInfoPanel), 0);


        addActor(confirmationPanel = ConfirmationPanel.getInstance());
    }

    @Override
    public void act(float delta) {
        dirty=false;
        super.act(delta);
    }

    protected boolean checkBlocked() {
        return false;
    }
    public EditorPalette getPalette() {
        return palette;
    }

    public EmitterPalette getEmitterPalette() {
        return getPalette().getEmitterPalette();
    }

}
