package eidolons.libgdx.anims.phased;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import eidolons.system.graphics.AttackAnimation;
import eidolons.system.graphics.PhaseAnimation;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.libgdx.GdxMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;

import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 1/5/2017.
 */
public class PhaseAnimator extends Group {
    private static PhaseAnimator instance;
    List<PhaseAnim> anims = new ArrayList<>();
    private boolean on;

    public PhaseAnimator(Stage stage) {
        this.on = false;
//                FAST_DC.getGameLauncher().FAST_MODE || FAST_DC.getGameLauncher().SUPER_FAST_MODE;

        stage.addActor(this);
        setBounds(0, 0, (float) GdxMaster.getWidth(), (float)
         GdxMaster.getHeight());
        setVisible(true);
        instance = this;
        init();
    }

    public static PhaseAnimator getInstance() {
        return instance;
    }

    public static AttackAnimation getAttackAnimation(Ref ref, Unit obj) {
        return (AttackAnimation) obj.getGame().getAnimationManager().getAnimation(
         AttackAnimation.generateKey((DC_ActiveObj) ref.getActive()));
    }

    public static PhaseAnimation getActionAnimation(Ref ref, Unit obj) {
        return obj.getGame().getAnimationManager().getAnimation(
         ((DC_ActiveObj) ref.getActive()).getAnimationKey());
    }

    public static PhaseAnimation getAnimation(Ref ref, BattleFieldObject obj) {
//        PhaseAnimation a = getAttackAnimation(ref, (Unit) obj);
//        if (a != null) {
//            return a;
//        }
//
//        return getActionAnimation(ref, obj);
        return null;
    }

    @Deprecated
    public static void handleDamageAnimAndLog(Ref ref, Unit attacked, boolean magical, DAMAGE_TYPE dmg_type) {
//        LogEntryNode entry = attacked.getGame().getLogManager().newLogEntryNode(true,
//                ENTRY_TYPE.DAMAGE);
//        PhaseAnimation animation = magical ? PhaseAnimator.getActionAnimation(ref, attacked)
//                : PhaseAnimator.getAttackAnimation(ref,
//                attacked);

//        entry.addLinkedAnimations(animation);
//        entry.setAnimPhasesToPlay(PHASE_TYPE.DAMAGE_DEALT);
////active.getAnimator(). TODO
//        if (animation != null) {
//            animation.addPhaseArgs(true, PHASE_TYPE.REDUCTION_NATURAL, dmg_type);
//        }
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
        if (!isOn()) {
            return;
        }
        removeAnims();
        setBounds(0, 0, (float) GdxMaster.getWidth(), (float)
         GdxMaster.getHeight());

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
            Vector2 p = GridMaster
             .getCenteredPos(anim.getAnim().getSourceCoordinates());
            float x = p.x;
            float y = p.y;
//            y = GridMaster.getCellHeight() *
//             GridMaster.getRows();
//            x = 0;
//            y = 0;
//            y = 255+GameScreen.getInstance().getController().getYCamPos();
//            x = 255+  GameScreen.getInstance().getController().getXCamPos();

            anim.setX(x);
            anim.setY(y);
            LogMaster.shout("Added anim : "
             + anim + "at " + x + " - " + y);
//    anim.getAnim().getMouseMap()
        });
//            sprite = new TextureRegion(j2dTex);
        setVisible(true);
    }

    private void removeAnims() {
        getAnims().forEach(anim -> {
            if (!DC_Game.game.getAnimationManager().
             getAnimations().contains(anim.getAnim())) {
                try {
                    getAnims().remove(anim);
                    LogMaster.shout("Removed anim : " + anim);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }

        });
    }

    public List<PhaseAnim> getAnims() {
        return anims;
    }

    public boolean checkAnimClicked(float x, float y, int pointer, int button) {
        for (PhaseAnim a : getAnims()) {
            try {
                if (a.getListener().checkClick(x, y, button)) {
                    return true;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
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

    public void initAttackAnimRawDamage(Attack attack) {
        List<Damage> rawDamage = DamageCalculator.precalculateRawDamageForDisplay(attack);
        attack.setRawDamage(rawDamage);
        attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.PRE_ATTACK, attack, rawDamage), 0);
    }
}
