package gdx.general.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import gdx.visuals.front.FrontField;
import gdx.visuals.front.FrontLine;
import gdx.visuals.lanes.LanesField;

public class ALanesStage extends Stage {

    private final FrontField frontField;
    private final LanesField laneField;
    private final FrontLine frontLine;
//    CentreVisuals centre;
    /*
    decor? overlays? vfx?
     */

    public ALanesStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addActor(laneField = new LanesField());
        addActor(frontField = new FrontField());
        addActor(frontLine = new FrontLine());
//        addActor(centre = new CentreField());
    }

    public LanesField getLaneField() {
        return laneField;
    }

    @Override
    public void draw() {
        super.draw();
    }
}
