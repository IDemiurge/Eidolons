package main.libgdx.anims.std;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import javafx.util.Pair;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.DC_WeaponObj;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.FacingMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.bf.GridConst;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.FileManager;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/14/2017.
 */
public class AttackAnim extends ActionAnim {

    protected ATK_ANIMS[] anims;
    protected DC_WeaponObj weapon;
    protected SequenceAction sequence;
    protected String imgPath;

    public AttackAnim(Entity active, ATK_ANIMS... anims) {
        super(active, getWeaponAnimData(active));
        this.anims = anims;


        this.anims = new ATK_ANIMS[]{
                ATK_ANIMS.HEAVY_SWING
        };
        weapon = getActive().getActiveWeapon();

    }

    protected static AnimData getWeaponAnimData(Entity active) {
        return new AnimData();
    }

    protected String findWeaponSprite(DC_WeaponObj weapon) {
        if (weapon == null) return "";
        String path = PathFinder.getSpritesPath() + "weapons\\"
                + (weapon.isNatural() ? "natural\\" : "")
                + (weapon.isRanged() ? "ranged\\" : "")
                + (weapon.isAmmo() ? "ammo\\" : "");
        String file = FileManager.findFirstFile(path, weapon.getName(), false);
        if (file == null)
            file = FileManager.findFirstFile(path, weapon.getProperty(G_PROPS.BASE_TYPE), false);
        if (file == null)
            file = FileManager.findFirstFile(path, weapon.getWeaponGroup().toString(), false);

        if (file == null)
            file = FileManager.findFirstFile(path, weapon.getProperty(G_PROPS.BASE_TYPE), true);
        if (file == null)
            file = FileManager.findFirstFile(path, weapon.getName(), true);
        if (file == null)
            file = FileManager.findFirstFile(path, weapon.getWeaponGroup().toString(), true);
        if (file == null) return path + FileManager.getRandomFileName(path);
        return path + file;
    }

    public String getWeaponSpritePath() {

        if (imgPath == null)
//            imgPath = FileManager.getRandomFile(PathFinder.getSpritesPath() + "weapons\\"
//             + (weapon.isNatural() ? "natural\\" : "")
//             + (weapon.isRanged() ? "ranged\\" : "")
//            ).getPath();
            imgPath = findWeaponSprite(getActive().getActiveWeapon());
        return imgPath;
//        return PathFinder.getSpritesPath() + "weapons\\" + "scimitar.png";
    }

    @Override
    protected Texture getTexture() {
        return TextureManager.getOrCreate(getWeaponSpritePath());
    }

    @Override
    public void draw(Batch batch, float alpha) {
        debug();
        act(Gdx.graphics.getDeltaTime());
        Texture texture = getTexture();
        batch.draw(texture, this.getX(), getY(), this.getOriginX(), this.getOriginY(), this.getWidth(),
                this.getHeight(), this.getScaleX(), this.getScaleY(), initialAngle + this.getRotation(), 0, 0,
                texture.getWidth(), texture.getHeight(), flipX, flipY);

        batch.draw(texture, 543, 456, this.getOriginX(), this.getOriginY(), this.getWidth(),
                this.getHeight(), this.getScaleX(), this.getScaleY(), initialAngle + this.getRotation(), 0, 0,
                texture.getWidth(), texture.getHeight(), flipX, flipY);

    }

    protected boolean isDrawTexture() {
        return true;
    }

    //entity params?

    @Override
    public void start() {
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
            if (!getFacing().isVertical()) offsetY -= getActor().getH();
            else offsetX -= getActor().getW();
        }

        defaultPosition.x = getX() + offsetX;
        defaultPosition.y = getY() + offsetY;
        setPosition(defaultPosition.x, defaultPosition.y);

        if (!getFacing().isVertical()) flipY = !flipY;
        else flipX = !flipX;
    }

    protected int getInitialAngle() {
        return
                FacingMaster.getFacing(active.getRef().getSourceObj()).getDirection().getDegrees();
    }

    protected FACING_DIRECTION getFacing() {
        return getActive().getOwnerObj().getFacing();
    }


    @Override
    protected Action getAction() {
//        if (sequence != null)  //reset sometimes?
//            return sequence;
        sequence = new SequenceAction();
        int i = 0;
        float totalDuration = 0;
        for (ATK_ANIMS anim : anims) {
            for (float angle : anim.targetAngles) {
                List<Pair<MoveByAction, RotateByAction>> swings = new LinkedList<>();
                float duration = anim.durations[i];
                totalDuration += duration;
                float x = anim.offsetsX[i];
                float y = anim.offsetsY[i];
                MoveByAction mainMove = getMoveAction(x, y, duration);
                RotateByAction mainRotate = getRotateAction(angle, duration);

                swings.add(new Pair<>(mainMove, mainRotate));
                swings.forEach(swing -> {
                    sequence.addAction(new ParallelAction(swing.getKey(), swing.getValue()));
                });
                i++;
            }

        }

        this.duration = totalDuration;
        return sequence;
    }

/*
delayAction
scale
size - elongate
 */
    //TODO back?

    protected MoveByAction getMoveAction(float x, float y, float duration) {
        MoveByAction mainMove = new MoveByAction();
        mainMove.setDuration(duration);
        int distanceX = active.getRef().getSourceObj().getX() -
                active.getRef().getTargetObj().getX();
        x -= distanceX * GridConst.CELL_W;
        int distanceY = active.getRef().getSourceObj().getY() -
                active.getRef().getTargetObj().getY();
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
        if (facing.isVertical())
            add = above;
        else add = left;

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

                if (getActive().isOffhand())
                    if (left)
                        break;
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
        SHOT;

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
