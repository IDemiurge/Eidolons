package main.libgdx.anims.phased;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.game.DC_Game;
import main.libgdx.GameScreen;
import main.system.GuiEventManager;
import main.system.auxiliary.GuiManager;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 1/5/2017.
 */
public class PhaseAnimator extends Group {
    private static PhaseAnimator instance;
    List<PhaseAnim> anims = new LinkedList<>();

    public PhaseAnimator() {
        setBounds(0, 0, (float) GuiManager.getScreenWidth(), (float)
                GuiManager.getScreenHeight());
        setVisible(true);

    }

    public static PhaseAnimator getInstance() {
        if (instance == null)
            instance = new PhaseAnimator();
        return
                instance;
    }

    public void init() {
        GuiEventManager.bind(SHOW_PHASE_ANIM, (event) -> {

        });
        GuiEventManager.bind(UPDATE_PHASE_ANIMS, (event) -> {
            update();
        });
        GuiEventManager.bind(UPDATE_PHASE_ANIM, (param) -> {
            ((PhaseAnim) param.get()).update();
        });
//        DC_Game.game.getAnimationManager().drawAnimations();

//        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//        v2 = getStage().screenToStageCoordinates(v2);
//        setPosition(v2.x,v2.y);
//        setBounds();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)

    {

//        getAnims().forEach(anim -> {
//            main.system.auxiliary.LogMaster.log(1,"anim drawn "+anim+ "at " + anim.getX() + " - " + anim.getY() );
//            anim.draw(batch, parentAlpha);
//        });
        super.draw(batch, parentAlpha);
    }

    public void update() {
//        getAnims().forEach(anim -> {
//            if (!DC_Game.game.getAnimationManager().
//             getAnimations().contains(anim.getAnim()))
//                try {
//                    getAnims().remove(anim);
//                    main.system.auxiliary.LogMaster.log(1, "**********Removed anim : " + anim);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//        });
        DC_Game.game.getAnimationManager().getAnimations().forEach(a -> {
            if (a.getPhaseAnim() == null) {
                getAnims().add(new PhaseAnim(a));
            }

        });
        clear();
        clearChildren();
        getAnims().forEach(anim -> {
            anim.update();
            addActor(anim);
            Point p = GameScreen.getInstance().getGridPanel()
                    .getPointForCoordinateWithOffset(anim.getAnim().getSourceCoordinates());
            float x = p.x;
            float y = p.y;
            y = GameScreen.getInstance().getGridPanel().getCellHeight() *
                    GameScreen.getInstance().getGridPanel().getRows();
            x = 0;
            y = 0;
//            y = 255+GameScreen.getInstance().getController().getY_cam_pos();
//            x = 255+  GameScreen.getInstance().getController().getX_cam_pos();

            anim.setX(x);
            anim.setY(y);
            main.system.auxiliary.LogMaster.log(1, "**********Added anim : "
                    + anim + "at " + x + " - " + y);
//    anim.getAnim().getMouseMap()
        });
//            sprite = new TextureRegion(j2dTex);
        setVisible(true);
    }

    public List<PhaseAnim> getAnims() {
        return anims;
    }
}
