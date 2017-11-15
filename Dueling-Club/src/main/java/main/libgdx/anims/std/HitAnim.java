package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.text.FloatingText;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.images.ImageManager;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private String text;
    private String imagePath;
    private Color c;
    private FloatingText floatingText;
    private float originalActorX;
    private float originalActorY;
//    AttackAnim atkAnim;
//    private DC_WeaponObj weapon;


    public HitAnim(DC_ActiveObj active, AnimData params, Color c) {
        this(active, params, true, c, null);
    }


    public HitAnim(DC_ActiveObj active, AnimData params) {
        this(active, params, null);
    }

    public HitAnim(DC_ActiveObj active, AnimData params, boolean blood, Color c, String text) {
        this(active, params, blood, c, text,
         ImageManager.getDamageTypeImagePath(active.getDamageType() == null ? "Physical" : active.getDamageType().getName()));
    }


    public HitAnim(DC_ActiveObj active, AnimData params, boolean blood, Color c,
                   String text, String imagePath) {
        super(active, params);
        if (blood) {
            params.addValue(ANIM_VALUES.SPRITES, getHitType(getActive()).spritePath
             + getTargetSuffix(getRef().getTargetObj()) + ".png");
        }
        this.text = text;
        this.imagePath = imagePath;
        if (c == null) {
            if (active.getDamageType() != null) {
                c = GdxColorMaster.getDamageTypeColor(active.getDamageType());
            } else {
                c = Color.RED;
            }
        }
        this.c = c;
        duration = 0.55f;
        setLoops(1);


        part = ANIM_PART.IMPACT;
    }

    @Override
    public Coordinates getOriginCoordinates() {
        if (forcedDestination != null) {
            return forcedDestination;
        }
        if (getRef().getTargetObj() != null) {
            return getRef().getTargetObj().getCoordinates();
        }
        if (getRef().getActive() != null) {
            if (getRef().getActive().getRef().getTargetObj() != null)
                return getRef().getActive().getRef().getTargetObj().getCoordinates();
        }
//
//        return super.getOriginCoordinates();
        return getDestinationCoordinates();
    }

    @Override
    protected Action getAction() {
        if (getRef() == null)
            return null;
        if (getRef().getSourceObj() == null)
            return null;
        if (getRef().getTargetObj() == null)
            return null;
        DIRECTION d = DirectionMaster.getRelativeDirection(getRef().getSourceObj(), getRef().getTargetObj());

        int dx = d.isVertical() ? 5 : 30;
        int dy = !d.isVertical() ? 5 : 30;
        if (BooleanMaster.isFalse(d.isGrowX())) {
            dx = -dx;
        }
        if (BooleanMaster.isTrue(d.isGrowY())) {
            dy = -dy;
        }

        originalActorX = getActor().getX();
        originalActorY = getActor().getY();
        float x = originalActorX;
        float y = originalActorY;

        MoveByAction move = (MoveByAction) ActorMaster.getAction(MoveByAction.class);
        move.setAmount(dx, dy);
        move.setDuration(getDuration() / 2);
        MoveToAction moveBack = (MoveToAction) ActorMaster.getAction(MoveToAction.class);
        if (getRef().getSourceObj() instanceof DC_Obj) {
            if (((DC_Obj) getRef().getSourceObj()).isOverlaying()) {
                moveBack.setPosition(x, y);
            }
        }
        moveBack.setPosition(x, y);
        moveBack.setDuration(getDuration() / 2);
        return new SequenceAction(move, moveBack);
    }

    public void addFadeAnim() {
//        ActorMaster.addfa
        resetColor();
        AlphaAction fade = new AlphaAction();
        fade.setDuration(getDuration());
        fade.setAlpha(0);
        fade.setTarget(this);
        addAction(fade);
    }

    private void resetColor() {
        setColor(1, 1, 1, 1); //TODO colored blood! ;)
    }

    @Override
    public Actor getActor() {
        return DungeonScreen.getInstance().getGridPanel().getUnitMap()
         .get(getRef().getTargetObj());
    }


    @Override
    public void start() {
        super.start();
        addFadeAnim();
//        if (textSupplier != null)
//            floatingText.setText(textSupplier.get());


        floatingText = FloatingTextMaster.getInstance().getFloatingText(
         active, TEXT_CASES.HIT, text == null ? getActive().getDamageDealt().getAmount() : text);
        floatingText.setImageSupplier(() -> imagePath);
        floatingText.setColor(c);
        floatingText.init(destination, 0, 128, getDuration() * 0.3f *
         OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.TEXT_DURATION)
        );

        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText);
        if (getActive().getDamageDealt() != null)
            FloatingTextMaster.getInstance().initFloatTextForDamage(getActive().getDamageDealt(), this);
        add();
        main.system.auxiliary.log.LogMaster.log(1, "HIT ANIM STARTED WITH REF: " + getRef());
    }

    @Override
    protected Actor getActionTarget() {
//        BattleFieldObject BattleFieldObject = (BattleFieldObject) getRef().getSourceObj();
//        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
//         MoveEffect.class)))
//            BattleFieldObject = (BattleFieldObject) getRef().getTargetObj();
//        BaseView actor = DungeonScreen.getInstance().getGridPanel().getUnitMap()
//         .get(BattleFieldObject);
//        return actor;
        return getActor();
    }

    private String getTargetSuffix(Obj targetObj) {
//       DC_HeroObj BattleFieldObject = (DC_HeroObj) targetObj;
        //dark, green, ...
        return "";
    }

    @Override
    public void finished() {
        super.finished();
        if (getActionTarget() == null) {
            return;
        }
        getActionTarget().setX(originalActorX);
        getActionTarget().setX(originalActorY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    private HIT getHitType(DC_ActiveObj active) {
        DAMAGE_TYPE damageType = active.getDamageType();
        if (damageType == DAMAGE_TYPE.SLASHING)
            return HIT.SLICE;
//        active.get
        return HIT.SPLASH;
    }

    public enum HIT {
        SLICE("slice 8 1"),
        SPLASH("blood splatter 3 3"),;

        String spritePath;

        HIT(String fileNameNoFormat) {
            spritePath = StrPathBuilder.build("main",
             "sprites",
             "hit",
             "blood", fileNameNoFormat)  ;
        }
    }


}
