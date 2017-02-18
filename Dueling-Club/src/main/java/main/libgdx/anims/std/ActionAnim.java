package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.ability.Ability;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.libgdx.GameScreen;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimMaster;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.log.LogMaster;

import java.util.function.Supplier;


/**
 * Created by JustMe on 1/9/2017.
 */
public class ActionAnim extends Anim {
    public ActionAnim(Entity active, AnimData params) {
        super(active, params);
    }

    public ActionAnim(DC_ActiveObj active, AnimData animData, Supplier<String> imagePath,
                      ANIM_MOD[] anim_mods) {
        super(active, animData);
        mods = anim_mods;
        this.textureSupplier = () -> TextureManager.getOrCreate(imagePath.get());
    }

    protected Action getAction() {
        return null;
    }

    protected void add() {
        addAction(getAction());
        getAction().setTarget(this);
        AnimMaster.getInstance().addActor(this);
        LogMaster.log(1, this + " added to stage");
    }
    @Override
    protected void dispose() {
        super.dispose();
        remove();
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
        return GameScreen.getInstance().getGridPanel().getUnitMap()
         .get(getActive().getOwnerObj());
    }

//for triggers!
    public void addAbilityAnims(Ability ability) {

    }


}