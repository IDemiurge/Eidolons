package main.libgdx.anims.phased;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.DC_Game;
import main.libgdx.GameScreen;
import main.system.GuiEventManager;

import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 1/5/2017.
 */
public class PhaseAnimator extends Group {
    private static PhaseAnimator instance;
    List<PhaseAnim> anims = new LinkedList<>();
    private boolean on;

    public PhaseAnimator(Stage stage) {
        this.on = false;
//                FAST_DC.getGameLauncher().FAST_MODE || FAST_DC.getGameLauncher().SUPER_FAST_MODE;

        stage.addActor(this);
        setBounds(0, 0, (float) Gdx.graphics.getWidth(), (float)
                Gdx.graphics.getHeight());
        setVisible(true);
        instance = this;
        init();
    }

    public static PhaseAnimator getInstance() {
        return instance;
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
        if (!isOn()) return;
        removeAnims();
        setBounds(0, 0, (float) Gdx.graphics.getWidth(), (float)
                Gdx.graphics.getHeight());

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
            Vector2 p = GameScreen.getInstance().getGridPanel()
                    .getVectorForCoordinateWithOffset(anim.getAnim().getSourceCoordinates());
            float x = p.x;
            float y = p.y;
//            y = GameScreen.getInstance().getGridPanel().getCellHeight() *
//             GameScreen.getInstance().getGridPanel().getRows();
//            x = 0;
//            y = 0;
//            y = 255+GameScreen.getInstance().getController().getY_cam_pos();
//            x = 255+  GameScreen.getInstance().getController().getX_cam_pos();

            anim.setX(x);
            anim.setY(y);
            main.system.auxiliary.LogMaster.shout("Added anim : "
                    + anim + "at " + x + " - " + y);
//    anim.getAnim().getMouseMap()
        });
//            sprite = new TextureRegion(j2dTex);
        setVisible(true);
    }


    private void removeAnims() {
        getAnims().forEach(anim -> {
            if (!DC_Game.game.getAnimationManager().
                    getAnimations().contains(anim.getAnim()))
                try {
                    getAnims().remove(anim);
                    main.system.auxiliary.LogMaster.shout("Removed anim : " + anim);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        });
    }

    public List<PhaseAnim> getAnims() {
        return anims;
    }

    public boolean checkAnimClicked(float x, float y, int pointer, int button) {
        for (PhaseAnim a : getAnims()) {
            try {
                if (a.getListener().checkClick(x, y, button))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
