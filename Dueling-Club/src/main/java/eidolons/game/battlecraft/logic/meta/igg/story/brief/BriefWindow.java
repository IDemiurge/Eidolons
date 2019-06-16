package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.video.VideoMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * or should these be sprite-sequences?
 *
 */
public class BriefWindow extends TablePanelX {
    private final FadeImageContainer img;
    String[] images;
    private int i=0;

    public BriefWindow(int w, int h, String... images) {
        super(w,h);
        this.images = images;
        addActor(img = new FadeImageContainer());
        GuiEventManager.bind(GuiEventType.BRIEFING_NEXT, p -> next());
        next();
    }
    public void next(){
        if (i >= images.length) {
            return;
        }
        setUserObject(images[i++]);
}

    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        img.setImage(getUserObject().toString());
        setWidth(img.getWidth());
        setHeight(img.getHeight());
    }
}
