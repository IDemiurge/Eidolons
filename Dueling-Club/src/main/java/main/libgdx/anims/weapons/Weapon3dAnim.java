package main.libgdx.anims.weapons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.std.ActionAnim;
import main.system.math.FuncMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 9/6/2017.
 */
public class Weapon3dAnim extends ActionAnim {
    SpriteAnimation sprite;
    //additional actions
    //effects/emitters

    //moving anim
    //frame rate / speed
    //sound sync
    //hit-part sync

    public Weapon3dAnim(DC_ActiveObj active) {
        super(active, init3dAnimData(active));
    }

    public static AnimData init3dAnimData(DC_ActiveObj active) {
        //duration, speed,
//        ANIM_VALUES.SCALE;
//        ANIM_VALUES.PARTICLE_EFFECTS;
        return new AnimData();
    }

    @Override
    public int getPixelsPerSecond() {
        return AnimMaster3d.getWeaponActionSpeed(getActive());

    }



    @Override
    protected void resetSprites() {
        if (sprite != null)
        sprite.dispose(); //or cache!
        sprite = get3dSprite();
        sprites.clear();
        sprites.add(sprite);
        int w = new FuncMaster<AtlasRegion>().getGreatest_(  (Arrays.asList(sprite.getRegions().toArray())),
         r -> r.getRegionWidth()).getRegionWidth();
        int h = new FuncMaster<AtlasRegion>().getGreatest_((Arrays.asList(sprite.getRegions().toArray())),
         r -> r.getRegionHeight()).getRegionHeight();
        setSize(w, h);
    }

    protected SpriteAnimation get3dSprite() {
       return  AnimMaster3d.getSpriteForAction(getDuration(), getActive(), ref.getTargetObj());
    }

    @Override
    public void start(Ref ref) {
        initDuration();
        super.start(ref);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sprite != null)
            if (sprite.getSprite()!=null )
        main.system.auxiliary.log.LogMaster.log(1,
         "drawing weapon anim at " +getX() + " "+ getY() + "; w="+sprite.getSprite().getWidth()+ "; h="+sprite.getSprite().getHeight());
    }

}
