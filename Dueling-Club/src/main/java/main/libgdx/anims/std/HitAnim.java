package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import main.content.PARAMS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.text.FloatingText;
import main.system.images.ImageManager;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private Supplier<String> textSupplier;
    private FloatingText floatingText;
//    AttackAnim atkAnim;
//    private DC_WeaponObj weapon;

    public HitAnim(Entity active, AnimData params) {
        this(active, params, true, Color.RED, () -> String.valueOf(
         active.getIntParam(PARAMS.DAMAGE_LAST_DEALT)),
         () -> ImageManager.getValueIconPath(PARAMS.TOUGHNESS));
    }

    public HitAnim(Entity active, AnimData params, boolean blood, Color c,
                   Supplier<String> floatingTextSupplier,
                   Supplier<String> imageSupplier) {
        super(active, params);

        if (blood) {
            params.addValue(ANIM_VALUES.SPRITES, getHitType(getActive()).spritePath
                    + getTargetSuffix(getRef().getTargetObj()) + ".png");
        }

        this.textSupplier = floatingTextSupplier;
//        this.imageSupplier = floatingTextSupplier;
        duration = 0.75f;
        AlphaAction fade = new AlphaAction();
        fade.setDuration(duration);
        fade.setAlpha(0);
        addAction(fade);
        setLoops(1);
        floatingText =
         new FloatingText(
          floatingTextSupplier, imageSupplier
          , c);
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
    public void start() {
        super.start();
        floatingText.init(GameScreen.getInstance().getAnimsStage()
         , destination, 64, 128, 1.5f);
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
        SPLASH("blood splatter 3 3"),;

        String spritePath;

        HIT(String fileNameNoFormat) {
            spritePath = PathFinder.getSpritesPath() + "blood\\" + fileNameNoFormat;
        }
    }


}
