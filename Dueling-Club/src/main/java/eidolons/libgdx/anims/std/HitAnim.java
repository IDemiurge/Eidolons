package eidolons.libgdx.anims.std;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.utils.Array;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimationConstructor.ANIM_PART;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.SmartTextureAtlas;
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
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.images.ImageManager;

import static main.system.GuiEventType.HP_BAR_UPDATE;

/**
 * Created by JustMe on 1/16/2017.
 */
public class HitAnim extends ActionAnim {
    private static String spritesPath;
    private static Boolean bloodOff;
    private SPRITE_TYPE spriteType;
    private HIT hitType;
    private String text;
    private String imagePath;
    private Color c;
    private FloatingText floatingText;
    private float originalActorX;
    private float originalActorY;
    private boolean blood;
    private static boolean displacementOn=true;

    public HitAnim(DC_ActiveObj active, AnimData params, Color c) {
        this(active, params, true, c, null);
    }

    public HitAnim(DC_ActiveObj active, AnimData params) {
        this(active, params, null);
    }
    //    AttackAnim atkAnim;
//    private DC_WeaponObj weapon;


    public HitAnim(DC_ActiveObj active, AnimData params, boolean blood, Color c, String text) {
        this(active, params, blood, c, text,
         ImageManager.getDamageTypeImagePath(
          active.getDamageType() == null ? "Physical" : active.getDamageType().getName(), true));
    }


    public HitAnim(DC_ActiveObj active, AnimData params, boolean blood, Color c,
                   String text, String imagePath) {
        super(active, params);
        if (blood) {
//              spriteType = getSpriteType((BattleFieldObject) getRef().getTargetObj());
//              hitType = getHitType(getActive(), spriteType);
//            String spritePath = StrPathBuilder.build(spriteType.name(), hitType.spritePath) + ".png";
//            params.addValue(ANIM_VALUES.SPRITES, spritePath);
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
        duration = 0.85f;
        setLoops(1);


        part = ANIM_PART.IMPACT;
    }

    public static Boolean getBloodOff() {
        if (bloodOff == null)
            bloodOff =  OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION.BLOOD_ANIMS_OFF);
        return bloodOff;
    }

    public static void setBloodOff(Boolean bloodOff) {
        HitAnim.bloodOff = bloodOff;
    }

    public static String getHitSpritesPath() {
        if (spritesPath == null)
            spritesPath = StrPathBuilder.build("main",
             "sprites",
             "hit");
        return spritesPath;
    }

    public static void setDisplacementOn(boolean displacementOn) {
        HitAnim.displacementOn = displacementOn;
    }

    public static boolean isDisplacementOn() {
        return displacementOn;
    }

    @Override
    protected void resetSprites() {
        sprites.clear();
        spriteType = getSpriteType((BattleFieldObject) getRef().getTargetObj());
        hitType = getHitType(getActive(), spriteType);
        String spritePath = StrPathBuilder.build(getHitSpritesPath(), spriteType.name(), hitType.spritePath)
         + ".txt";
//         + ".png";
//        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(spritePath);
        //scale?
        SmartTextureAtlas atlas =
         SmartTextureAtlas.getAtlas(PathFinder.getImagePath() + spritePath);
        if (atlas == null)
            return;
        Array<AtlasRegion> regions = atlas.getRegions();
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(regions,
         getDuration() / regions.size, 1);
        if (getRef().getTargetObj() instanceof Unit)
            sprite.setColor(getColorForSprite((Unit) getRef().getTargetObj()));
        blood = spriteType == SPRITE_TYPE.BLOOD;
        if (blood)
            if (getBloodOff())
                return;
        sprites.add(sprite);
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
            return null ;
        if (getRef() == null)
            return null;
        if (getRef().getSourceObj() == null)
            return null;
        if (getRef().getTargetObj() == null)
            return null;
        DIRECTION d = DirectionMaster.getRelativeDirection(getRef().getSourceObj(), getRef().getTargetObj());

        int dx = d.isVertical() ? 5 : 30;
        int dy = !d.isVertical() ? 5 : 30;
        if (BooleanMaster.isFalse(d.growX)) {
            dx = -dx;
        }
        if (BooleanMaster.isTrue(d.growY)) {
            dy = -dy;
        }
//DungeonScreen.getInstance().getGridPanel().detachUnitView()
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
        SequenceAction sequence = new SequenceAction(move, moveBack);
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
        return DungeonScreen.getInstance().getGridPanel().getViewMap()
         .get(getRef().getTargetObj());
    }

    @Override
    public void start() {
        super.start();
        getActions().clear();
        addFadeAnim();
//        if (textSupplier != null)
//            floatingText.setText(textSupplier.get());


        floatingText = FloatingTextMaster.getInstance().getFloatingText(
         active, TEXT_CASES.HIT, text == null ?
          getActive().getDamageDealt() == null ?
           "0"
           : getActive().getDamageDealt().getAmount()
          : text);
        floatingText.setImageSupplier(() -> imagePath);
        floatingText.setColor(c);
        floatingText.init(destination, 0, 128, getDuration() * 0.3f
        );

        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, floatingText);
        if (getActive().getDamageDealt() != null)
            FloatingTextMaster.getInstance().initFloatTextForDamage(getActive().getDamageDealt(), this);
        add();
//        main.system.auxiliary.log.LogMaster.log(1, "HIT ANIM STARTED WITH REF: " + getRef());
    }

    @Override
    protected Actor getActionTarget() {
//        BattleFieldObject BattleFieldObject = (BattleFieldObject) getRef().getSourceObj();
//        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
//         MoveEffect.class)))
//            BattleFieldObject = (BattleFieldObject) getRef().getTargetObj();
//        BaseView actor = DungeonScreen.getInstance().getGridPanel().getViewMap()
//         .get(BattleFieldObject);
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

        }
        return null;
    }

    private SPRITE_TYPE getSpriteType(BattleFieldObject targetObj) {
        Obj block = getActive().getRef().getObj(KEYS.BLOCK);
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
        GuiEventManager.trigger(HP_BAR_UPDATE, getActionTarget());
        getParentAnim().setHpUpdate(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    private HIT getHitType(DC_ActiveObj active, SPRITE_TYPE sprite) {
        if (sprite != SPRITE_TYPE.DUST)
            if (sprite != SPRITE_TYPE.STONE)
                if (sprite != SPRITE_TYPE.BONE) {
                    DAMAGE_TYPE damageType = active.getDamageType();
                    if (damageType == DAMAGE_TYPE.SLASHING)
                        return HIT.SLICE;
                    if (damageType == DAMAGE_TYPE.PIERCING)
                        return HIT.SQUIRT;
                    if (damageType == DAMAGE_TYPE.BLUDGEONING)
                        return HIT.SMASH;
                }
//        active.get
        return HIT.SHOWER;
    }

    //TO ATLASES!
    public enum HIT {
        SLICE("slice"),
        SPLASH("blood splatter 3 3"),
        SMASH("smash 3 3"),
        SQUIRT("squirt"),
        SHOWER("shower"),
//        TORRENT("smear 3 3")
        ;

        String spritePath;

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
