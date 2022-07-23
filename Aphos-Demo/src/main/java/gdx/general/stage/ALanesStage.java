package gdx.general.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import content.AphosEvent;
import gdx.general.AScreen;
import gdx.general.anims.ActionAnims;
import gdx.general.anims.AnimDrawer;
import gdx.views.FieldView;
import gdx.visuals.front.FrontField;
import gdx.visuals.front.HeroZone;
import gdx.visuals.front.ViewManager;
import gdx.visuals.lanes.LanesField;
import libgdx.anims.std.ActionAnim;
import logic.entity.Entity;
import main.system.GuiEventManager;

public class ALanesStage extends Stage {

    private final FrontField frontField;
    private final LanesField laneField;
    private final HeroZone heroZone;
    private final AnimDrawer animDrawer;
//    CentreVisuals centre;
    /*
    decor? overlays? vfx?
     */

    public ALanesStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addActor(laneField = new LanesField());
        addActor(heroZone = new HeroZone());
        addActor(frontField = new FrontField());
        addActor(animDrawer = new AnimDrawer());
        ActionAnims.setAnimDrawer(animDrawer);

        GuiEventManager.bind(AphosEvent. ATB_ACTIVE, p->{
            Entity e = (Entity) p.get();
            ViewManager.onViews(view -> view.setActive(false));
            FieldView view = ViewManager.getView(e);
            view.setActive(true);


        } );
//        heroZone.setPosition(840, 200);
//        laneField.setPosition(-880, 0);
//        addActor(centre = new CentreField());
    }

    public LanesField getLaneField() {
        return laneField;
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public Actor hit(float stageX, float stageY, boolean touchable) {
        return super.hit(stageX, stageY, touchable);
    }
}
