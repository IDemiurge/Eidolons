package libgdx.anims.fullscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.Core;
import eidolons.system.libgdx.datasource.FullscreenAnimDataSource;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.assets.Assets;
import eidolons.content.consts.SpriteData;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.NoHitImage;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/12/2018.
 */
public class FullscreenAnims extends GroupX {
    public static final boolean randomFacingMode = true;
    private static final float DELAY = 0.5f;
    private static final boolean SPRITE_MODE = true; //TODO just make 2 instances
    ObjectMap<VisualEnums.FULLSCREEN_ANIM, ObjectMap<FACING_DIRECTION, Actor>> groupCache = new ObjectMap<>();
    private FullscreenAnimDataSource data;
    private float delayTimer;
    private float showingTimer;

    List<SpriteAnimation> spriteList = new ArrayList<>();

    public FullscreenAnims() {
        if (!SPRITE_MODE)
            init();
        GuiEventManager.bind(GuiEventType.INGAME_EVENT, p -> {
            Event e = (Event) p.get();
            VisualEnums.FULLSCREEN_ANIM type = getType(e);
            if (type == null) {
                return;
            }
            float intensity = getIntensity(e);
            FACING_DIRECTION facing = getFacing(e);
            Obj obj = e.getRef().getTargetObj();
//            if (obj.isDead()) {
//                obj = e.getRef().getSourceObj();
//            }
            {
                if (obj == Core.getMainHero()) {
                    GenericEnums.BLENDING blending = getBlending(type);

                    if (SPRITE_MODE) {
                        data = new FullscreenAnimDataSource(type, intensity,
                                facing, blending);
                        initAnim(data);
                    }
//                    data = new FullscreenAnimDataSource(type, intensity,
//                            facing, blending);
                }
            }
        });

        GuiEventManager.bind(GuiEventType.SHOW_FULLSCREEN_ANIM, p -> {
            FullscreenAnimDataSource dataSource = (FullscreenAnimDataSource) p.get();

            initAnim(dataSource);

        });
    }

    private VisualEnums.FULLSCREEN_ANIM getType(Event e) {
        VisualEnums.FULLSCREEN_ANIM type = null;

        if (e.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS)
            if (e.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {
                return VisualEnums.FULLSCREEN_ANIM.GATES;
            }
        if (e.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE) {
            type =
//                    RandomWizard.chance(33) ?
//                    FULLSCREEN_ANIM.BLOOD_SCREEN :
                    VisualEnums.FULLSCREEN_ANIM.BLOOD;
            if (e.getRef().getDamageType() == GenericEnums.DAMAGE_TYPE.POISON) {
                type = VisualEnums.FULLSCREEN_ANIM.POISON;
            }
        }
        return type;
    }

    private float getIntensity(Event e) {
        if (e.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS) {
            return 1;
        }
        float intensity = RandomWizard.getRandomFloatBetween(0.15f, 0.35f);

        intensity +=
                e.getRef().getAmount() / e.getRef().getTargetObj().getIntParam(PARAMS.ENDURANCE);
        intensity +=
                e.getRef().getAmount() / e.getRef().getTargetObj().getIntParam(PARAMS.TOUGHNESS) / 2;

        return intensity;
    }

    private FACING_DIRECTION getFacing(Event e) {
        FACING_DIRECTION facing;
        if (randomFacingMode) {
            facing = FacingMaster.getRandomFacing();
        } else
            facing = FacingMaster.getRelativeFacing(e.getRef().getSourceObj().getCoordinates(),
                    e.getRef().getTargetObj().getCoordinates());

        return facing;
    }

    private GenericEnums.BLENDING getBlending(VisualEnums.FULLSCREEN_ANIM type) {
        switch (type) {

            case BLOOD:
            case BLOOD_SCREEN:
                return GenericEnums.BLENDING.INVERT_SCREEN;
            case POISON:
                return null;
            case DARKNESS:
                break;
        }
        return GenericEnums.BLENDING.SCREEN;
    }


    private void initAnim(FullscreenAnimDataSource dataSource) {
        if (!isOn()){
            return;
        }
        String path =
//                FileManager.getRandomFilePathVariant(
//                PathFinder.getImagePath()+
                dataSource.getType().getSpritePath();
        if (Assets.get().getManager().isLoaded(path)){
            main.system.auxiliary.log.LogMaster.devLog("No fullscreen anim preloaded for " +path);
            return;
        }
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(path, false, false);

        sprite.setCustomAct(true);
        sprite.setBlending(dataSource.getBlending());
        float intensity = dataSource.getIntensity();
        float alpha = Math.min(1, RandomWizard.getRandomFloatBetween(intensity * 2, intensity * 3));
        int fps = RandomWizard.getRandomIntBetween(11, 14);
        sprite.setFps(fps);
        sprite.setAlpha(alpha);
        //TODO

        sprite.setFlipX(dataSource.flipX);
        sprite.setFlipY(dataSource.flipY);

        sprite.setX(GdxMaster.getWidth() / 2);
        sprite.setY(GdxMaster.getHeight() / 2);

        SpriteData spriteData= dataSource.getSpriteData();
        if (spriteData != null) {
            sprite.setData(spriteData);
        }
        spriteList.add(sprite);

        main.system.auxiliary.log.LogMaster.devLog("Fullscreen anim added: " +path);
        if (dataSource.getType().color != null) {
            sprite.setColor(dataSource.getType().color);
        }

        sprite.setLoops(dataSource.getLoops());

//        if (showingTimer > 0) {
//            data.intensity += intensity;
//            return;
//        }
//        delayTimer = DELAY;
//        showingTimer = getDuration() + delayTimer;
    }

    private boolean isOn() {
        return !CoreEngine.TEST_LAUNCH;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!SPRITE_MODE)
            if (showingTimer <= 0)
                return; //precaution...
        for (SpriteAnimation animation : new ArrayList<>(spriteList)) {
            animation.act(Gdx.graphics.getDeltaTime());
            if (animation.isAnimationFinished()) {
                WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.FULLSCREEN_DONE, animation);
                spriteList.remove(animation);
                continue;
            }
            animation.draw(batch);

        }
        super.draw(batch, parentAlpha);
    }


    @Override
    public void act(float delta) {
        if (SPRITE_MODE)
            return;
        if (showingTimer <= 0)
            return;
        showingTimer -= delta;
        if (delayTimer > 0) {
            delayTimer -= delta;
            return;
        }

        if (!SPRITE_MODE)
            show(data);
        super.act(delta);
    }


    private void init() {
        for (VisualEnums.FULLSCREEN_ANIM type : VisualEnums.FULLSCREEN_ANIM.values()) {
            ObjectMap<FACING_DIRECTION, Actor> map = new ObjectMap<>();
            for (FACING_DIRECTION facing : FACING_DIRECTION.values) {
                //                Actor group = new Actor();

                String path = PathFinder.getImagePath() + getCorePath() + type;
                path = FileManager.getRandomFilePathVariant(path, ".png");
                path = GdxStringUtils.cropImagePath(path);
                //TODO Anim Review
                Image img = new NoHitImage(TextureCache.getOrCreateR(path));
                //                group.addActor(img);
                img.setVisible(false);
                img.getColor().a = 0;
                //                addActor(img);
                map.put(facing, img);
                img.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
            }
            groupCache.put(type, map);
        }
    }

    private String getCorePath() {
        return PathFinder.getSpritesPath() + "fullscreen/";
    }

    private void show(FullscreenAnimDataSource data) {
        Actor group = groupCache.get(data.getType()).get(data.getFrom());
        group.setVisible(true);
        group.getColor().a = 0;
        ActionMasterGdx.addAlphaAction(group, getFadeInDuration(), data.getIntensity());
        ActionMasterGdx.addAfter(group, ActionMasterGdx.getAlphaAction(group, getFadeOutDuration(), 0, false));
        ActionMasterGdx.addRemoveAfter(group);
        addActor(group);
    }

    private float getDuration() {
        return getFadeInDuration() + getFadeOutDuration();
    }

    @Override
    protected float getFadeInDuration() {
        return 0.5f;
    }

    @Override
    protected float getFadeOutDuration() {
        return 1.5f;
    }

}
