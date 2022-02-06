package libgdx.anims.std;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.anims.AnimData;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.fullscreen.ScreenshakeMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.text.FloatingText;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.screens.handlers.ScreenMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.CONTENT_CONSTS.OBJECT_ARMOR_TYPE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_MATERIAL_GROUP;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.secondary.Bools;
import main.system.images.ImageManager;
import main.system.launch.Flags;

import static main.system.GuiEventType.HP_BAR_UPDATE;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private static String spritesPath;
    private static Boolean bloodOff;
    private static boolean displacementOn = true;
    private SPRITE_TYPE spriteType;
    private final String text;
    private String imagePath;
    private Color c;
    private float originalActorX;
    private float originalActorY;
    private DAMAGE_TYPE damageType;

    public HitAnim(ActiveObj active, AnimData data) {
        this(active, data, active.getDamageType());
    }

    public HitAnim(ActiveObj active, AnimData data, DAMAGE_TYPE damageType) {
        this(active, data, false, null, null,
                ImageManager.getDamageTypeImagePath(
                        damageType == null ? "Physical" : damageType.getName(), true));
        setDamageType(damageType);
    }

    public HitAnim(ActiveObj active, AnimData data, boolean blood, Color c,
                   String text, String imagePath) {
        super(active, data);
        if (blood) {
            //              spriteType = getSpriteType((BattleFieldObject) getRef().getTargetObj());
            //              hitType = getHitType(getActive(), spriteType);
            //            String spritePath = StrPathBuilder.build(spriteType.name(), hitType.spritePath) + ".png";
            //            data.addValue(ANIM_VALUES.SPRITES, spritePath);
        }
        this.text = text;
        this.imagePath = imagePath;
        initColor();
        this.c = c;
        duration = 0.85f;
        setLoops(1);

        if (active != null) {
            damageType = active.getDamageType();
        }

        part = VisualEnums.ANIM_PART.IMPACT;
    }

    @Override
    public String toString() {
        return "floating: " + text;
    }

    public static Boolean getBloodOff() {
        if (bloodOff == null)
            bloodOff = OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION.BLOOD_ANIMS_OFF);
        return bloodOff;
    }

    public static void setBloodOff(Boolean bloodOff) {
        HitAnim.bloodOff = bloodOff;
    }

    public static boolean isDisplacementOn() {
        return displacementOn;
    }

    public static void setDisplacementOn(boolean displacementOn) {
        HitAnim.displacementOn = displacementOn;
    }

    private void initImage() {
        this.imagePath = ImageManager.getDamageTypeImagePath(
                getDamageType() == null ? "Physical" : damageType.getName(), true);
    }

    private void initColor() {
        if (c == null) {
            if (getDamageType() != null) {
                c = GdxColorMaster.getDamageTypeColor(getDamageType());
            } else {
                c = Color.RED;
            }
        }
    }

    @Override
    protected void resetSprites() {
        super.resetSprites(); //from data

        try {
            spriteType = getSpriteType((BattleFieldObject) getRef().getTargetObj());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (spriteType == null || getRef().getObj(KEYS.BLOCK) instanceof WeaponItem) {
            spriteType = SPRITE_TYPE.SPARKS; //shield!
        }
        HIT hitType = getHitType(spriteType);
        String spritePath = getSpritePath(spriteType, hitType);
        //         + ".png";
        //        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(spritePath);
        //scale?
        // SmartTextureAtlas atlas =
        //         SmartTextureAtlas.getAtlas(PathFinder.getImagePath() + );
        // if (atlas == null)
        //     return;
        // Array<AtlasRegion> regions = atlas.getRegions();
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(spritePath);
        sprite.setFrameDuration(getDuration() / sprite.getRegions().size);
        float x = RandomWizard.getRandomFloatBetween(-10, 10);
        float y = RandomWizard.getRandomFloatBetween(-10, 10);
        if (spriteType == SPRITE_TYPE.SPARKS) {
            x += -64;
            y += -64;
        }
        sprite.setOffsetX(x);
        sprite.setOffsetY(y);


        sprite.setFlipX(RandomWizard.random());
        sprite.setFlipY(RandomWizard.random());


        if (getRef().getTargetObj() instanceof Unit)
            sprite.setColor(getColorForSprite((Unit) getRef().getTargetObj()));
        boolean blood = spriteType == SPRITE_TYPE.BLOOD;
        if (blood)
            if (getBloodOff())
                return;
        sprites.add(sprite);
    }

    public static String getSpritePath(SPRITE_TYPE spriteType, HIT hitType) {
        return StrPathBuilder.build(PathFinder.getHitSpritesPath(), spriteType.name(), hitType.spritePath)
                + ".txt";
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
        //        if (!OptionsMaster.getAnimOptions().getBooleanValue(
        //         ANIMATION_OPTION.HIT_ANIM_DISPLACEMENT))
        //            return null;
        if (!isDisplacementOn())
            return null;
        if (getRef() == null)
            return null;
        if (getRef().getSourceObj() == null)
            return null;
        if (getRef().getTargetObj() == null)
            return null;
        DIRECTION d = DirectionMaster.getRelativeDirection(getRef().getSourceObj(), getRef().getTargetObj());

        int dx = d.isVertical() ? 5 : 30;
        int dy = !d.isVertical() ? 5 : 30;
        if (Bools.isFalse(d.growX)) {
            dx = -dx;
        }
        if (Bools.isTrue(d.growY)) {
            dy = -dy;
        }
        //ScreenMaster.getDungeonGrid().detachUnitView()
        originalActorX = getActor().getX();
        originalActorY = getActor().getY();
        float x = originalActorX;
        float y = originalActorY;

        boolean overlaying = false;
        if (getRef().getSourceObj() instanceof DC_Obj) {
            if (((DC_Obj) getRef().getSourceObj()).isOverlaying()) {
                overlaying = true;
            }
        }

        SequenceAction sequence =
                ActionMasterGdx.getDisplaceSequence(x, y, dx, dy, getDuration() / 2, overlaying);

        if (isDelayed()) {
            DelayAction delayed = new DelayAction(getDuration() / 3);
            delayed.setAction(sequence);
            return delayed;
        }
        return sequence;
    }

    private boolean isDelayed() {
        return true;
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
        Color color = null;
        if (getRef().getTargetObj() instanceof Unit) {
            color = getColorForSprite((Unit) getRef().getTargetObj());
        }
        if (color != null)
            setColor(color);
        else
            setColor(1, 1, 1, 1); //TODO colored blood! ;)
    }


    @Override
    public Actor getActor() {
        return ScreenMaster.getGrid().getViewMap()
                .get(getRef().getTargetObj());
    }

    public String getTexturePath() {
        return ImageManager.getDamageTypeImagePath(damageType.getName());
    }

    @Override
    public void start() {
        initColor();
        initImage();
        super.start();
        getActions().clear();
        addFadeAnim();

        //        if (textSupplier != null)
        //            floatingText.setText(textSupplier.getVar());

        Damage damage = null;
        if (getActive() != null) {
            damage = getActive().getDamageDealt();

        }
        if (damage == null) {
            damage = DamageFactory.getGenericDamage(damageType, ref.getAmount(), ref);
        }
        FloatingText floatingText = FloatingTextMaster.getInstance().getFloatingText(
                active, VisualEnums.TEXT_CASES.HIT, text == null ?
                        damage.getAmount()
                        : text);
        floatingText.setImageSupplier(() -> imagePath);
        floatingText.setColor(c);
        floatingText.init(destination, 0, 128, getDuration() * 0.3f
        );
        main.system.auxiliary.log.LogMaster.log(1, "dmg ADD_FLOATING_TEXT " + floatingText);
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText);
        FloatingTextMaster.getInstance().initFloatTextForDamage(damage, this);
        add();

        try {
            ScreenshakeMaster.shakeCamera(ref, damage);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }


    @Override
    protected Actor getActionTarget() {
        //        BattleFieldObject BattleFieldObject = (BattleFieldObject) getRef().getSourceObj();
        //        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
        //         MoveEffect.class)))
        //            BattleFieldObject = (BattleFieldObject) getRef().getTargetObj();
        //        BaseView actor = ScreenMaster.getDungeonGrid().getViewMap()
        //         .getVar(BattleFieldObject);
        //        return actor;
        return getActor();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private Color getColorForSprite(Unit targetObj) {
        switch (spriteType) {
            case BLOOD:
                if (targetObj.getChecker().checkClassification(CLASSIFICATIONS.DEMON)) {
                    return GdxColorMaster.CRIMSON;
                }
                if (targetObj.getChecker().checkClassification(CLASSIFICATIONS.INSECT)) {
                    return GdxColorMaster.YELLOW_GREEN;
                }
                if (targetObj.getChecker().checkClassification(CLASSIFICATIONS.REPTILE)) {
                    return GdxColorMaster.COPPER;
                }

                break;

            case SPARKS:
            case STONE:
            case AETHER:
            case BONE:
                break;
            case DUST:
                if (targetObj.getChecker().checkClassification(CLASSIFICATIONS.WRAITH)) {
                    return GdxColorMaster.DARK_STEEL;
                }

                break;
        }
        return new Color(1, 1, 1, 1);
    }

    private SPRITE_TYPE getSpriteType(BattleFieldObject targetObj) {
        Obj block = getRef().getObj(KEYS.BLOCK);
        if (block != null) {
            ITEM_MATERIAL_GROUP group = new EnumMaster<ITEM_MATERIAL_GROUP>().retrieveEnumConst(ITEM_MATERIAL_GROUP.class,
                    block.getProperty(G_PROPS.ITEM_MATERIAL_GROUP));
            if (group == ITEM_MATERIAL_GROUP.METAL || group == ITEM_MATERIAL_GROUP.CRYSTAL)
                return SPRITE_TYPE.SPARKS;
            if (group == ITEM_MATERIAL_GROUP.STONE)
                return SPRITE_TYPE.STONE;
        }
        OBJECT_ARMOR_TYPE type =
                new EnumMaster<OBJECT_ARMOR_TYPE>().retrieveEnumConst(OBJECT_ARMOR_TYPE.class,
                        targetObj.getProperty(PROPS.OBJECT_ARMOR_TYPE));
        if (type == OBJECT_ARMOR_TYPE.METAL) {
            return SPRITE_TYPE.SPARKS;
        }
        if (type == OBJECT_ARMOR_TYPE.STONE) {
            return SPRITE_TYPE.STONE;
        }
        if (type == OBJECT_ARMOR_TYPE.FLESH) {
            return SPRITE_TYPE.BLOOD;
        }
        if (type == OBJECT_ARMOR_TYPE.BONE) {
            return SPRITE_TYPE.BONE;
        }

        if (targetObj instanceof Structure) {
            if (targetObj.isWall())
                return SPRITE_TYPE.STONE;
            return SPRITE_TYPE.DUST;
        } else {
            if (targetObj instanceof Unit) {
                if (!((Unit) targetObj).getChecker().isLiving()) {
                    return SPRITE_TYPE.DUST;
                }
            }
        }
        return SPRITE_TYPE.BLOOD;
    }

    @Override
    public void finished() {
        super.finished();
        if (getActionTarget() == null) {
            return;
        }
        getActionTarget().setX(originalActorX);
        getActionTarget().setY(originalActorY);
        GuiEventManager.trigger(HP_BAR_UPDATE, getActionTarget().getUserObject());
        if (getParentAnim() != null)
            getParentAnim().setHpUpdate(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Flags.isFootageMode())
            return;
        super.draw(batch, parentAlpha);

    }

    private HIT getHitType(SPRITE_TYPE sprite) {
        if (sprite != SPRITE_TYPE.DUST)
            if (sprite != SPRITE_TYPE.STONE)
                if (sprite != SPRITE_TYPE.BONE) {
                    if (damageType == DAMAGE_TYPE.SLASHING)
                        return HIT.SLICE;
                    if (damageType == DAMAGE_TYPE.PIERCING)
                        return RandomWizard.random() ? HIT.SQUIRT : HIT.SPLASH;
                    if (damageType == DAMAGE_TYPE.BLUDGEONING)
                        return HIT.SMASH;
                }
        //        active.getVar
        return HIT.SHOWER;
    }

    public DAMAGE_TYPE getDamageType() {
        return damageType;
    }

    public void setDamageType(DAMAGE_TYPE damageType) {
        this.damageType = damageType;
    }

    //TO ATLASES!
    public enum HIT {
        SLICE("slice"),
        SPLASH("shower"),
        //                "blood splatter 3 3"),
        SMASH("smash 3 3"),
        SQUIRT("shower"),
        //                "squirt"),
        SHOWER("shower"),
        //        TORRENT("smear 3 3")
        BONE_CRACK("bone");

        public String spritePath;

        HIT(String fileNameNoFormat) {
            spritePath = fileNameNoFormat;
        }
    }

    public enum SPRITE_TYPE {
        BLOOD,
        SPARKS,
        DUST,
        BONE,
        AETHER, STONE,
    }


}
