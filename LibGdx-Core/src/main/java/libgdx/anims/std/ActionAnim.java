package libgdx.anims.std;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import libgdx.anims.ANIM_MODS.ANIM_MOD;
import libgdx.anims.Anim;
import libgdx.anims.AnimData;
import libgdx.anims.main.AnimMaster;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.screens.handlers.ScreenMaster;
import main.ability.Ability;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;

import java.util.function.Supplier;


/**
 * Created by JustMe on 1/9/2017.
 */
public class ActionAnim extends Anim {
    protected Action action;

    public ActionAnim(Entity active, AnimData params) {
        super(active, params);
    }

    public ActionAnim(ActiveObj active, AnimData animData, Supplier<String> imagePath,
                      ANIM_MOD[] anim_mods) {
        super(active, animData);
        mods = anim_mods;
        //TODO Anim Review
        // this.textureSupplier = () -> TextureCache.getOrCreate(imagePath.get());
    }

    public ActionAnim(Entity active, AnimData params, VisualEnums.ANIM_PART part) {
        super(active, params, part);
    }

    protected Action getAction() {
        return null;
    }

    protected void add() {
        action = getAction();
        if (action == null) {
            return;
        }

        getActionTarget().addAction(action);
        getAction().setTarget(getActionTarget());
        if (getActionTarget() == this) {
            AnimMaster.getInstance().addActor(this);
            LogMaster.log(1, this + " added to stage");
        }
    }

    protected Actor getActionTarget() {
        return this;
    }

    @Override
    protected void dispose() {
        super.dispose();
        remove();
    }

    @Override
    public void finished() {
        if (action != null)
            if (action.getActor() != null)
                if (action.getActor().getActions() != null)
                    action.getActor().getActions().clear();
        super.finished();
    }

    @Override
    public void start() {

        setRotation(initialAngle);
        super.start();
    }

    @Override
    public ActiveObj getActive() {
        return (ActiveObj) super.getActive();
    }

    public Actor getActor() {
        return ScreenMaster.getGrid().getViewMap()
         .get(getActive().getOwnerUnit());
    }

    //for triggers!
    public void addAbilityAnims(Ability ability) {

    }

    @Override
    public void checkAddFloatingText() {
        getFloatingText().forEach(floatingText1 -> {
            if (time >= floatingText1.getDelay()) {
                Vector2 floatTextPos = //localToSctageCoordinates
                 new Vector2(
                  getActor().getX() + getActor().getWidth() / 2,
                  getActor().getY() + getActor().getHeight());
                if (getActor().getParent() instanceof GridCellContainer)
                    floatTextPos = getActor().localToStageCoordinates(floatTextPos);
                floatingText1.setX(floatTextPos.x);
                floatingText1.setY(floatTextPos.y);
                GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText1);
            }
        });
    }

}