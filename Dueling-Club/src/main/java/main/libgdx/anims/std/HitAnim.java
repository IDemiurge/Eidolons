package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import main.ability.effects.oneshot.move.MoveEffect;
import main.content.PARAMS;
import main.data.filesys.PathFinder;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.battlecraft.rules.combat.damage.Damage;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.actions.MoveByActionLimited;
import main.libgdx.anims.text.FloatingText;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.bf.BaseView;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.images.ImageManager;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.OptionsMaster;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private Supplier<String> textSupplier;
    private FloatingText floatingText;
//    AttackAnim atkAnim;
//    private DC_WeaponObj weapon;


    public HitAnim(DC_ActiveObj active, AnimData params) {
        this(active, params, true, null, () -> String.valueOf(
         active.getIntParam(PARAMS.DAMAGE_LAST_DEALT)),
         () -> ImageManager.getDamageTypeImagePath(
          active.getDamageType() == null ? "Physical" : active.getDamageType().getName()));
    }

    public HitAnim(DC_ActiveObj active, AnimData params, boolean blood, Color c,
                   Supplier<String> floatingTextSupplier,
                   Supplier<String> imageSupplier) {
        super(active, params);

        if (blood) {
            params.addValue(ANIM_VALUES.SPRITES, getHitType(getActive()).spritePath
             + getTargetSuffix(getRef().getTargetObj()) + ".png");
        }

        this.textSupplier = floatingTextSupplier;
//        this.imageSupplier = floatingTextSupplier;
        duration = 0.55f;
        AlphaAction fade = new AlphaAction();
        fade.setDuration(duration);
        fade.setAlpha(0);
        addAction(fade);
        setLoops(1);
        if (c == null) {
            if (active.getDamageType() != null) {
                c = GdxColorMaster.getDamageTypeColor(active.getDamageType());
            } else {
                c = Color.RED;
            }
        }
        floatingText = FloatingTextMaster.getInstance().getFloatingText(
         active, TEXT_CASES.HIT, floatingTextSupplier.get());
        floatingText.setImageSupplier(imageSupplier);
        floatingText.setColor(c);


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

        return super.getOriginCoordinates();
    }

    @Override
    protected Action getAction() {

        DIRECTION d = DirectionMaster.getRelativeDirection(getRef().getSourceObj(), getRef().getTargetObj());

        int dx =d.isVertical() ? 5:  30;
        int dy =!d.isVertical() ? 5:  30;
        if (BooleanMaster.isTrue(d.isGrowX())) {
            dx = -dx;
        }
        if (BooleanMaster.isTrue(d.isGrowY())) {
            dy = -dy;
        }

        float  x =getActor().getX();
        float  y =getActor().getY();

        MoveByActionLimited move = (MoveByActionLimited) ActorMaster.getAction(MoveByActionLimited.class);
        move.setAmount(dx, dy);
        move.setDuration(getDuration()/2);
        MoveToAction moveBack = (MoveToAction) ActorMaster.getAction(MoveToAction.class);
        moveBack.setPosition(x, y);
        moveBack.setDuration(getDuration()/2);
        return new SequenceAction(move, moveBack);
    }

    @Override
    public void start() {
        super.start();
        if (textSupplier != null)
            floatingText.setText(textSupplier.get());
        floatingText.init(destination, 0, 128, getDuration() * 0.3f *
         OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.TEXT_DURATION)
        );
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText);
        Damage damage = getActive().getDamageDealt();
        FloatingTextMaster.getInstance().initFloatTextForDamage(damage, this);
        add();
    }

    @Override
    protected Actor getActionTarget() {
        BattleFieldObject BattleFieldObject = (BattleFieldObject) getRef().getSourceObj();
        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
         MoveEffect.class)))
            BattleFieldObject = (BattleFieldObject) getRef().getTargetObj();
        BaseView actor = DungeonScreen.getInstance().getGridPanel().getUnitMap()
         .get(BattleFieldObject);
        return actor;
    }

    private String getTargetSuffix(Obj targetObj) {
//       DC_HeroObj BattleFieldObject = (DC_HeroObj) targetObj;
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
        SPLASH("blood splatter 3 3"),;

        String spritePath;

        HIT(String fileNameNoFormat) {
            spritePath = PathFinder.getSpritesPath() + "blood\\" + fileNameNoFormat;
        }
    }


}
