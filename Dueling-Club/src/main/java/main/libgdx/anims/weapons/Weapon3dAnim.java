package main.libgdx.anims.weapons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.anims.AnimMaster3d.PROJECTION;
import main.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.std.ActionAnim;
import main.system.math.FuncMaster;
import main.system.math.PositionMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 9/6/2017.
 */
public class Weapon3dAnim extends ActionAnim {
    protected SpriteAnimation sprite;
   protected Map<PROJECTION, SpriteAnimation> projectionsMap=new HashMap<>();

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
//        if (sprite != null)
//        sprite.dispose(); //or cache per projection!
        sprite = get3dSprite();
        if (sprite==null )
            return ;
        getSprites().clear();
        getSprites().add(sprite);
        int w = new FuncMaster<AtlasRegion>().getGreatest_(  (Arrays.asList(sprite.getRegions().toArray())),
         r -> r.getRegionWidth()).getRegionWidth();
        int h = new FuncMaster<AtlasRegion>().getGreatest_((Arrays.asList(sprite.getRegions().toArray())),
         r -> r.getRegionHeight()).getRegionHeight();
        setSize(w, h);
    }

    protected SpriteAnimation get3dSprite() {
        PROJECTION   projection= getProjection();

        sprite = projectionsMap.get(projection );
        if (sprite  != null) {
            return sprite;
        }
        sprite =  AnimMaster3d.getSpriteForAction(getDuration(),
        getActive(),  ref.getTargetObj(),  getCase(),  projection );
        projectionsMap.put(projection, sprite);
        return sprite;
    }

    @Override
    public Vector2 getOffsetOrigin() {
    switch(getProjection()){
        case FROM:
            return new Vector2(0, 32);
        case TO:
            return new Vector2(0, -32);
        case HOR:
            return new Vector2(32, 0);
    }
        return super.getOffsetOrigin();
    }

    protected WEAPON_ANIM_CASE getCase() {
//        return WEAPON_ANIM_CASE. BLOCKED;

        if (getActive().isFailedLast())
                return WEAPON_ANIM_CASE.MISS;
//        return WEAPON_ANIM_CASE.PARRY; counter?


        return WEAPON_ANIM_CASE.NORMAL;
    }

    protected PROJECTION getProjection() {
        Boolean b =
         PositionMaster.isAboveOr(getActive().getOwnerObj(),   ref.getTargetObj());
        if (getActive().getOwnerObj().getCoordinates().equals(  ref.getTargetObj().getCoordinates()))
            b = getActive().getOwnerObj().isMine();
        PROJECTION   projection= PROJECTION.HOR;
        if (b!=null )
            projection = b ? PROJECTION.FROM : PROJECTION.TO;
        return projection;
    }



    @Override
    public void start(Ref ref) {
        initDuration();
        super.start(ref);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        if (sprite != null)
//            if (sprite.getSprite()!=null )
//        main.system.auxiliary.log.LogMaster.log(1,
//         "drawing weapon anim at " +getX() + " "+ getY() + "; w="+sprite.getSprite().getWidth()+ "; h="+sprite.getSprite().getHeight());
    }

}
