package eidolons.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.ANIM_MODS.ANIM_MOD;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import main.ability.Ability;
import main.entity.Entity;
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

    public ActionAnim(DC_ActiveObj active, AnimData animData, Supplier<String> imagePath,
                      ANIM_MOD[] anim_mods) {
        super(active, animData);
        mods = anim_mods;
        this.textureSupplier = () -> TextureCache.getOrCreate(imagePath.get());
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
    public DC_ActiveObj getActive() {
        return (DC_ActiveObj) super.getActive();
    }

    public Actor getActor() {
        return DungeonScreen.getInstance().getGridPanel().getUnitMap()
         .get(getActive().getOwnerObj());
    }

    //for triggers!
    public void addAbilityAnims(Ability ability) {

    }


}