package eidolons.libgdx.anims.std;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.GridConst;
import eidolons.system.test.TestMasterContent;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.math.PositionMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/14/2017.
 */
public class AttackAnim extends ActionAnim {

    static {
        ATK_ANIMS.THROW.startSpeed = 500;
    }

    protected ATK_ANIMS[] anims;
    protected DC_WeaponObj weapon;
    protected SequenceAction sequence;
    protected String imgPath;

    public AttackAnim(Entity active) {
        this(active, ATK_ANIMS.HEAVY_SWING);
    }

    public AttackAnim(Entity active, ATK_ANIMS... anims) {
        super(active, getWeaponAnimData(active, anims));
        this.anims = anims;


        weapon = getActive().getActiveWeapon();
//        debug();
    }

    protected static AnimData getWeaponAnimData(Entity active, ATK_ANIMS... anims) {
        AnimData data = new AnimData();
        float base_speed = anims[0].startSpeed;
        if (base_speed != 0) {
            data.setValue(ANIM_VALUES.MISSILE_SPEED, String.valueOf(base_speed));
        } else {
            data.setValue(ANIM_VALUES.MISSILE_SPEED, "200");
        }
        return data;
    }

    @Override
    protected void initSpeed() {
        super.initSpeed();
    }

    protected String findWeaponSprite(DC_WeaponObj weapon) {
        if (weapon == null) {
            return "";
        }
        String path = PathFinder.getImagePath();
        StrPathBuilder builder = new StrPathBuilder(PathFinder.getSpritesPath(), "weapons");

        builder.append
         (weapon.isNatural() ? "natural"
          : null);
        builder.append(weapon.isRanged() ? "ranged" : null);
        builder.append(weapon.isAmmo() ? "ammo" : null);
        builder.append(TestMasterContent.isArtifact(weapon) ? "artifact" : null);
        path += builder.toString();

        String file = findWeaponSprite(path, weapon);
        if (file == null) {
            if (TestMasterContent.isArtifact(weapon))
                file = findWeaponSprite
                 (StringMaster.cropLastPathSegment(path), weapon);
            if (file == null) {
                file = findWeaponSprite(path, weapon, false);
                if (file == null)
                    file = findWeaponSprite
                     (StringMaster.cropLastPathSegment(path), weapon, false);
            }
        }

        return path + file;
    }

    private String findWeaponSprite(String path, DC_WeaponObj weapon) {
        return findWeaponSprite(path, weapon, true);
    }

    private String findWeaponSprite(String path, DC_WeaponObj weapon, boolean strict) {
        String file = FileManager.findFirstFile(path, weapon.getName(), false);

        if (file == null) {
            file = FileManager.findFirstFile(path, weapon.getProperty(G_PROPS.BASE_TYPE), false);
        }
        if (file == null) {
            file = FileManager.findFirstFile(path, weapon.getWeaponGroup().toString(), false);
        }

        if (file == null) {
            file = FileManager.findFirstFile(path, weapon.getProperty(G_PROPS.BASE_TYPE), true);
        }
        if (strict)
            return file;
        if (file == null) {
            file = FileManager.findFirstFile(path, weapon.getName(), true);
        }
        if (file == null) {
            file = FileManager.findFirstFile(path, weapon.getWeaponGroup().toString(), true);
        }
        if (file == null) {
            return path + FileManager.getRandomFileName(path);
        }
        return path + file;
    }

    public String getTexturePath() {
        if (imgPath == null)
//            imgPath = FileManager.getRandomFile(PathFinder.getSpritesPath() + "weapons"+StringMaster.getPathSeparator()
//             + (weapon.isNatural() ? "natural"+StringMaster.getPathSeparator() : "")
//             + (weapon.isRanged() ? "ranged"+StringMaster.getPathSeparator() : "")
//            ).getPath();
        {
            imgPath = findWeaponSprite(getActive().getActiveWeapon());
        }
        return imgPath;
//        return PathFinder.getSpritesPath() + "weapons"+StringMaster.getPathSeparator() + "scimitar.png";
    }

    @Override
    public void draw(Batch batch, float alpha) {
        act(Gdx.graphics.getDeltaTime());
        super.draw(batch, alpha);

    }

    //entity params?

    protected boolean isDrawTexture() {
        return false;
    }

    @Override
    public void start() {
        sprites.clear();
        sprites.add(SpriteAnimationFactory.getSpriteAnimation(getTexturePath(), true));
        super.start();
        add();
    }

    @Override
    public void initPosition() {
        super.initPosition();
        initialAngle =
         getInitialAngle();
        initFlip();
        initOffhand();
//            destination.x = destination.x+offsetX;
    }

    protected void initOffhand() {
        int offsetX = 0;
        int offsetY = 0;
        if (getActive().isOffhand()) {
            if (!getFacing().isVertical()) {
                offsetY -= getActor().getHeight();
            } else {
                offsetX -= getActor().getWidth();
            }
        }

        defaultPosition.x = getX() + offsetX;
        defaultPosition.y = getY() + offsetY;
        setPosition(defaultPosition.x, defaultPosition.y);
// TODO for down??
        if (!getFacing().isVertical()) {
            flipY = !flipY;
        } else {
            flipX = !flipX; //for west?
        }
    }

    protected int getInitialAngle() {
        return
         FacingMaster.getFacing(active.getRef().getSourceObj()).getDirection().getDegrees();
    }

    protected FACING_DIRECTION getFacing() {
        return getActive().getOwnerObj().getFacing();
    }

/*
delayAction
scale
size - elongate
 */
    //TODO back?

    @Override
    protected Action getAction() {
//        if (sequence != null)  //reset sometimes?
//            return sequence;
        sequence = new SequenceAction();
        int i = 0;
        float totalDuration = 0;
        for (ATK_ANIMS anim : anims) {
            for (float angle : anim.targetAngles) {
                List<Pair<MoveByAction, RotateByAction>> swings = new ArrayList<>();
                float duration =
                 this.duration;
                if (duration <= 0) {
                    duration = 1;
                }
                //anim.durations[i];
                totalDuration += duration;
                float x = anim.offsetsX[i];
                float y = anim.offsetsY[i];
                MoveByAction mainMove = null;
                try {
                    mainMove = getMoveAction(x, y, duration);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                if (mainMove == null) {
                    return null;
                }
                RotateByAction mainRotate = getRotateAction(angle, duration);

                swings.add(new ImmutablePair<>(mainMove, mainRotate));
                swings.forEach(swing -> {
                    sequence.addAction(new ParallelAction(swing.getKey(), swing.getValue()));
                });
                i++;
            }

        }

        setDuration(totalDuration);
        return sequence;
    }

    protected MoveByAction getMoveAction(float x, float y, float duration) {
        MoveByAction mainMove = new MoveByAction();
        mainMove.setDuration(duration);
        int distanceX =
         getOriginCoordinates().
//         active.getRef().getSourceObj().
 getX() -
//         getActive().getAnimator().getAnimRef().getTargetObj()
          getDestinationCoordinates()
           .getX();
        x -= distanceX * GridConst.CELL_W;
        int distanceY =
         getOriginCoordinates().
//         active.getRef().getSourceObj().
 getY() -
//         getActive().getAnimator().getAnimRef().getTargetObj()
          getDestinationCoordinates()
           .getY();
        y += distanceY * GridConst.CELL_H;

        mainMove.setAmount(x, y);
        return mainMove;
    }

    protected RotateByAction getRotateAction(float angle, float duration) {
        RotateByAction mainRotate = new RotateByAction();
//                angle += targetAngleOffset;
        FACING_DIRECTION facing = getFacing();
        boolean offhand = getActive().isOffhand(); //add default offset
        Boolean left = PositionMaster.isToTheLeftOr(getOriginCoordinates(), getDestinationCoordinates());
        Boolean above = PositionMaster.isAboveOr(getOriginCoordinates(), getDestinationCoordinates());
        Boolean add = null;
        if (facing.isVertical()) {
            add = above;
        } else {
            add = left;
        }

        FACING_SINGLE relativeFacing = FacingMaster.getSingleFacing(
         facing,
         getOriginCoordinates(), getDestinationCoordinates());
//increase angle?
        int addAngle = 0;
        switch (relativeFacing) {

            case IN_FRONT:
                break;
            case BEHIND:
                break;
            case TO_THE_SIDE:

                if (getActive().isOffhand()) {
                    if (left) {
                        break;
                    }
                }
            case NONE:
                break; //TODO on the same cell?
        }
        angle += addAngle;


        //pixel collision for impact!

        mainRotate.setDuration(duration);
        mainRotate.setAmount(angle);
        return mainRotate;
    }

    @Override
    protected void initDuration() {

    }

    public enum ATK_ANIMS {
        THRUST_LANCE,

        STAB,
        JAB,
        POKE,
        SLASH,
        POLE_SMASH,
        HEAVY_SWING(),
        SHOT, THROW();

        //        ATK_ANIMS(float overswing, float overswing, float overswing, float overswing) {
//
//        }
        float baseX = 0;// -1 to 1 from 0 to 100% of width/height of source unit view
        float baseY = 0.5f;
        float baseOffsetX; // in pixels
        float baseOffsetY;
        float targetX = 0.5f;
        float targetY = 0.5f;
        float targetOffsetX = 25;
        float targetOffsetY = 25;
        float baseAngle = 0; // 0 - horizontal; 90 - vertical
        float[] targetAngles = {
         -90
        };

        float[] durations = {
         0.5f
        };
        float[] offsetsY = {
         0f
        };
        float[] offsetsX = {
         0f
        };

        // will assume each one in turn during animation
        float overswing; // go over the target's face-mark
        float backswing; // return after impact
        float preswing; // draw back before atk

        float zoomOut; // уменьшить дальний конец при атаке
        float zoomIn;// увеличить дальний конец до атаки
        float startSpeed;
        float acceleration; // +speed per second

        float rotationPointOffset; // for imgPath?

        Vector2[] targetPoints;
        Vector2[] basePoint;

        ATK_ANIMS(float... params) {

        }


    }

//    bloodTemplate; from real bleeding amount?
//    sparks;
}

//    static {
//        new DataUnit
//        ""
//        Arrays.stream(ATK_ANIMS.class.getFields()).forEach(field -> {
//            Class c = field.getType();
//            if (c == Float.class) {
//
//            }
//            field.set(name, value);
//        });
//    }
