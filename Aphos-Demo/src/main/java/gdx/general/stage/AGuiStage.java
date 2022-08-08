package gdx.general.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import content.AphosEvent;
import gdx.views.FieldView;
import gdx.visuals.front.ViewManager;
import libgdx.gui.dungeon.tooltips.ToolTipManager;
import libgdx.stage.GenericGuiStage;
import logic.entity.Entity;
import main.system.GuiEventManager;

public class AGuiStage extends GenericGuiStage {
    public AGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        GuiEventManager.bind(AphosEvent. ATB_ACTIVE, p->{
            Entity e = (Entity) p.get();
            ViewManager.onViews(view -> view.setActive(false));
            FieldView view = ViewManager.getView(e);
            view.setActive(true);
            showTooltip(e + " ACTIVE! ");
        } );
    }
    /**
     * Phase 1 - anything?
     *
     * Info Text
     *
     */
    @Override
    protected ToolTipManager createToolTipManager() {
        return null;
    }


    @Override
    public void draw() {
        super.draw();
    }
}
