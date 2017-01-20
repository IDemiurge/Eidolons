package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import main.content.PARAMS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private DC_WeaponObj weapon;
    AttackAnim atkAnim;

    public HitAnim(Entity active, AnimData params) {
        super(active, params);
        active.getIntParam(PARAMS.DAMAGE_LAST_DEALT);
//        active.getIntParam(PARAMS.BLEEDING_LAST_DEALT); for emitter strength

        weapon =getActive().getActiveWeapon();
        params.addValue(ANIM_VALUES.SPRITES, getHitType(getActive()).spritePath
        + getTargetSuffix(getRef().getTargetObj())+".png"
        );
        duration = 0.75f;
        AlphaAction fade = new AlphaAction();
        fade.setDuration(duration);
        fade.setAlpha(0);
        addAction(fade);
        setLoops(1);
        //shake target!
    }

    private String getTargetSuffix(Obj targetObj) {
//       DC_HeroObj unit = (DC_HeroObj) targetObj;
      //dark, green, ...
       return "";
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    private HIT getHitType(DC_ActiveObj active) {
//        active.get
        return HIT.SPLASH;
    }

    public enum HIT {
        SLICE("blood 4 4"),
        SPLASH("blood splatter 3 3"),

        ;
        HIT(String fileNameNoFormat){
            spritePath = PathFinder.getSpritesPath()+"blood\\"+ fileNameNoFormat;
        }
        String spritePath;
    }

    public enum DEATH {
        SHATTER,
        FADE,
        EXPLODE,
        BURN,
        COLLAPSE,
        ATOMIZE,
        ;
        String spritePath;

    }
}
